(ns atreus-preview.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(enable-console-print!)

(def app-state (atom {:text "Hello world!"}))

(om/root
  (fn [app owner]
    (dom/text #js{:fill "red" :x 370 :y 150 :font-size 55} (:text app)))
  app-state
  {:target (. js/document (getElementById "app"))})
