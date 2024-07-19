# dross

[![Clojars Project](https://img.shields.io/clojars/v/com.github.kimbsy/dross.svg)](https://clojars.org/com.github.kimbsy/dross)

A Leiningen plugin for checking a project for potentially unused
dependencies. Helpful for sprucing up a legacy system.

The command scans the `src` directory to find unused dependencies
declared in `project.clj`.  It ignores dependencies specified in
profiles or plugins and considers exclusions.

## Usage

Add dross to your `project.clj` or `~/.lein/profiles.clj`:

```Clojure
:plugins [[com.github.kimbsy/dross "1.0.0"]]
```

Usage:
  lein dross [options]

Options:
  :q (or :quiet, -q, --quiet) Enable quiet mode, only outputting names
  of unused dependencies.

Examples:
  lein dross
  lein dross :q
  lein dross --quiet

You can provide a `.dcignore.edn` file in the project root containing
a vector of dependencies (qualified or unqualified symbols) that
should be ignored in the scan.
