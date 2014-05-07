(ns atreus-preview.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(enable-console-print!)

(def app-state
  (atom
   {:keys
    [{:label "Q"
      :x 66
      :y 48}
     {:label "W"
      :x 136
      :y 48}
     {:label "E"
      :x 202
      :y 45}
     {:label "R"
      :x 268
      :y 74}
     {:label "T"
      :x 330
      :y 110}]}))

(def padding
  "Padding of the body"
  8)

(defn key-view
  [key-label owner]
  (reify
    om/IRender
    (render [this]
      (dom/text #js{:x (- (:x key-label) 8)
                    :y (- (:y key-label) 8)
                    :fill "red"
                    :font-size 55}
                (:label key-label)))))

(defn keys-view
  [app owner]
  (reify
    om/IRender
    (render [this]
      (apply dom/g nil
             (om/build-all key-view
                           (:keys app))))))

(om/root
  keys-view
  app-state
  {:target (. js/document (getElementById "app"))})
