(defproject atreus-preview "0.1.0-SNAPSHOT"
  :description "An svg preview for atreus keybindings"
  :url "https://github.com/cesarbp/atreus-preview"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2202"]
                 [org.clojure/core.async "0.1.303.0-886421-alpha"]
                 [om "0.6.2"]]

  :plugins [[lein-cljsbuild "1.0.3"]]

  :profiles {:dev {:dependencies [[org.clojars.cbp/pudge "0.1.0"]]}}

  :source-paths ["src"]

  :cljsbuild {
    :builds [{:id "atreus-preview"
              :source-paths ["src"]
              :compiler {
                :output-to "resources/atreus_preview.js"
                :output-dir "resources/out"
                :optimizations :none
                :source-map true}}]})
