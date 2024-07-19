(ns leiningen.dross
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.java.shell :as sh]
            [clojure.string :as str]
            [leiningen.core.project :as project]))

(defn test?
  [d]
  (= "test" (:scope (apply hash-map d))))

(defn extract-exclusions
  "Takes a dep vector, if it has :exclusions we should return the seq of
  exclusions"
  [d]
  (map first (get (apply hash-map d) :exclusions [])))

(defn get-all-exclusions
  "find all deps which have been specifically referenced in an
  `:exclusions` field on a dep"
  [deps]
  (->> deps
       (mapcat extract-exclusions)
       set))

(defn get-unused-deps
  "Grep the `src` dir for the name of the dep (looking to find it in
  `:require` statements)

  This is nowhere near a perfect solution, but avoids static analysis"
  [dep-strs]
  (filter #(str/blank? (:out (sh/sh "grep" "-riIn" % "src")))
          dep-strs))

(defn ^:help/doc dross
  "Checks the project for potentially unused dependencies.

The command scans the `src` directory to find unused dependencies
declared in `project.clj`.  It ignores dependencies specified in
profiles or plugins and considers exclusions.

Additionally, you can provide a `.drossignore.edn` file in the project
root containing a vector of dependencies (qualified or unqualified
symbols) that should be ignored in the scan.

Usage:
  lein dross [options]

Options:
  :q (or :quiet, -q, --quiet) Enable quiet mode, only outputting names
  of unused dependencies.

Examples:
  lein dross
  lein dross :q
  lein dross --quiet"
  [project & args]
  (let [quiet? (some #{":q" ":quiet" "-q" "--quiet"} args)]

    (when-not quiet?
      (println "Checking `project.clj` for potentially unused dependencies...")
      (newline))

    (let [project-clj-path (str (:root project) "/project.clj")
          raw-project-deps
          (try
            ;; need to use raw project to filter deps from
            ;; ~/.lein/profiles.clj or plugins
            (:dependencies (project/read-raw project-clj-path))
            (catch Exception e
              (println "Error reading" project-clj-path ": " (.getMessage e)
                       (println "Make sure the file exists and is properly formatted.")
                       (System/exit 1))))

          ignore-path (str (:root project) "/.drossignore.edn")
          ignored-symbols (if (.exists (io/file ignore-path))
                            (edn/read-string (slurp ignore-path))
                            [])
          qualified-ignored? (set (filter (comp some? namespace) ignored-symbols))
          unqualified-ignored? (set (map name (remove (comp some? namespace) ignored-symbols)))

          ;; top-level deps that have been excluded as a transitive
          ;; dep in another declaration should be ignored as we don't
          ;; expect these to be referenced in our code
          exclusion? (get-all-exclusions raw-project-deps)

          unused (->> raw-project-deps
                      (remove test?)
                      (map first)
                      (remove exclusion?)
                      (remove qualified-ignored?)
                      (map name)
                      (remove unqualified-ignored?)
                      get-unused-deps)]

      (if (seq unused)
        (if quiet?
          (doseq [d unused]
            (println d))
          (do (let [n (count unused)]
                (println "There"
                         (if (= n 1)
                           "is 1 dependency"
                           (str "are " n " dependencies"))
                         "which have not been required by any namespace in `src`:"))
              (newline)
              (doseq [d unused]
                (println "-" d))
              (newline)
              (println "Please check to ensure they are still needed.")))
        (println "No potentially unused dependencies found.")))))
