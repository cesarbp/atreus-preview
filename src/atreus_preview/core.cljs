(ns atreus-preview.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [clojure.browser.repl]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [put! chan <!]]
            [clojure.string :as str]
            [atreus-preview.util :as util]
            [atreus-preview.constants :as constants]))

(enable-console-print!)

(def app-state
  (atom
   {:layer 0
    :json constants/json-example
    :edn constants/layout-example
    :errors []}))

(def padding
  "Padding of the body"
  8)

(defn layer-view
  [layer owner]
  (reify
    om/IRender
    (render [this]
      (apply dom/g #js{:id "labels"}
             (mapcat
              (fn [[row ridx]]
                (for [[key kidx] (map vector row (range))
                      :let [x (get-in constants/magic-numbers [ridx kidx :x])
                            y (get-in constants/magic-numbers [ridx kidx :y])
                            side (get-in constants/magic-numbers [ridx kidx :side])]]
                  (try
                    (let [label (util/show-key key)]
                      (dom/text #js{:x x
                                    :y y
                                    :transform (if (= :left side)
                                                 (str "rotate(10 " x " " y ")")
                                                 (str "rotate(-10 " x " " y ")"))
                                    :style #js{:text-anchor "middle"
                                               :font-size (condp >= (count label)
                                                            5 "medium"
                                                            6 "small"
                                                            8 "x-small"
                                                            "xx-small")}}
                                label))
                    (catch js/Error e
                      (dom/text #js{:x x
                                    :y y
                                    :fill "red"
                                    :transform (if (= :left side)
                                                 (str "rotate(10 " x " " y ")")
                                                 (str "rotate(-10 " x " " y ")"))
                                    :style #js{:text-anchor "middle"
                                               :font-size (condp >= (count (str key))
                                                            5 "medium"
                                                            6 "small"
                                                            8 "x-small"
                                                            "xx-small")}}
                                (str key))))))
              (map vector layer (range)))))))

(defn labels-view
  [app owner]
  (reify
    om/IRender
    (render [this]
      (let [layout (:edn app)
            layer (nth layout (:layer app))]
        (om/build layer-view layer)))))

(om/root
  labels-view
  app-state
  {:target (. js/document (getElementById "key-labels"))})


; ==============================================================================

(defn errors-view
  [errors owner]
  (reify
    om/IRender
    (render [this]
      (let [s (str/join ". " (map first errors))]
        (dom/span #js{:style
                      #js{:color "red"}}
                  s)))))

(defn controls-view
  [app owner]
  (reify
    om/IInitState
    (init-state [_]
      {:change-layer (chan)
       :change-json (chan)})
    om/IWillMount
    (will-mount [_]
      (let [change-layer (om/get-state owner :change-layer)]
        (go (loop []
              (let [new-layer-idx (<! change-layer)]
                (assert (integer? new-layer-idx))
                (assert (< -1 new-layer-idx (count (:edn @app))))
                (om/transact! app [:layer]
                              (constantly new-layer-idx)))
              (recur))))
      (let [change-json (om/get-state owner :change-json)]
        (go (loop []
              (let [new-json-str (<! change-json)
                    json (try (js/JSON.parse new-json-str)
                              (catch js/Error e nil))
                    edn (if json (js->clj json))
                    valid-edn? (if edn (util/valid-edn? edn))]
                (cond
                 (not json)
                 (om/transact! app []
                  (fn [app]
                    (-> app
                        (assoc :errors [["Invalid JSON - couldn't parse" "error"]])
                        (assoc :json new-json-str))))
                 (not valid-edn?)
                 (om/transact! app []
                  (fn [app]
                    (-> app
                        (assoc :errors [["Badly formatted layout, need a 3D array" "error"]])
                        (assoc :json new-json-str))))
                 :else
                 (om/transact! app []
                               (fn [app]
                                 (-> app
                                     (assoc :edn edn)
                                     (assoc :json new-json-str)
                                     (assoc :errors []))))))
              (recur)))))
    om/IRenderState
    (render-state [this {:keys [change-layer change-json]}]
      (let [layer-count (count (:edn app))
            layer (:layer app)]
        (dom/div nil
         (apply dom/select #js{:value layer
                               :onChange (fn [e]
                                           (put! change-layer
                                                 (js/parseInt
                                                  (-> e .-target .-value))))}
                (for [i (range layer-count)]
                  (dom/option #js{:value i}
                              (str "Layer " i))))
         (dom/div nil
                  (dom/div nil
                           (dom/a #js{:href "#"}
                                  "JSON")
                           (dom/span nil
                                     " | ")
                           (dom/a #js{:href "#"}
                                  "EDN")
                           (dom/span nil
                                     " | ")
                           (om/build errors-view (:errors app)))
                  (dom/textarea #js{:rows 30
                                    :cols 80
                                    :ref "modify-json"
                                    :value (:json app)
                                    :onChange (fn [e]
                                                (put! change-json
                                                      (-> e .-target .-value)))})))))))

(om/root
 controls-view
 app-state
 {:target (. js/document (getElementById "controls"))})
