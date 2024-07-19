# dross

[![Clojars Project](https://img.shields.io/clojars/v/com.github.kimbsy/dross.svg)](https://clojars.org/com.github.kimbsy/dross)

A Leiningen plugin for checking a project for potentially unused
dependencies. Helpful for sprucing up a legacy system.

The command scans the `src` directory to find unused dependencies
declared in `project.clj`.  It ignores dependencies specified in
profiles or plugins and considers exclusions.

## Usage

Usage:
  lein depclean [options]

Options:
  :q (or :quiet, -q, --quiet) Enable quiet mode, only outputting names
  of unused dependencies.

Examples:
  lein depclean
  lein depclean :q
  lein depclean --quiet

You can provide a `.dcignore.edn` file in the project root containing
a vector of dependencies (qualified or unqualified symbols) that
should be ignored in the scan.
