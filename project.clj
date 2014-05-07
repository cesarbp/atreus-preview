(defproject atreus-preview "0.1.0-SNAPSHOT"
  :description "An svg preview for atreus keybindings"
  :url "https://github.com/cesarbp/atreus-preview"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2173"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [om "0.6.2"]]

  :plugins [[lein-cljsbuild "1.0.2"]]

  :source-paths ["src"]

  :cljsbuild {
    :builds [{:id "atreus-preview"
              :source-paths ["src"]
              :compiler {
                :output-to "atreus_preview.js"
                :output-dir "out"
                :optimizations :none
                :source-map true}}]})
