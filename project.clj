(defproject com.github.kimbsy/dross "1.0.3"
  :description "A Leiningen plugin for identifying potentially unused dependencies."
  :url "https://github.com/Kimbsy/dross"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.3"]]
  :eval-in-leiningen true
  :deploy-repositories [["clojars" {:url           "https://clojars.org/repo/"
                                    :username      :env/clojars_user
                                    :password      :env/clojars_pass
                                    :sign-releases false}]])
