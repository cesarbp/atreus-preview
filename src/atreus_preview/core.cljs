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

(def magic-numbers
  [[{:x 66  :y 48}
    {:x 136 :y 48}
    {:x 202 :y 45}
    {:x 268 :y 74}
    {:x 330 :y 110}
    {:x 489 :y 107}
    {:x 558 :y 71}
    {:x 618 :y 45}
    {:x 687 :y 48}
    {:x 755 :y 47}]

   [{:x 52  :y 119}
    {:x 123 :y 123}
    {:x 188 :y 116}
    {:x 252 :y 145}
    {:x 316 :y 177}
    {:x 499 :y 172}
    {:x 561 :y 136}
    {:x 626 :y 111}
    {:x 693 :y 118}
    {:x 761 :y 115}]

   [{:x 39  :y 190}
    {:x 107 :y 191}
    {:x 179 :y 181}
    {:x 245 :y 208}
    {:x 306 :y 241}
    {:x 364 :y 278}
    {:x 510 :y 237}
    {:x 572 :y 204}
    {:x 634 :y 173}
    {:x 709 :y 180}
    {:x 777 :y 181}]

   [{:x 24  :y 253}
    {:x 94  :y 254}
    {:x 168 :y 247}
    {:x 230 :y 274}
    {:x 295 :y 308}
    {:x 448 :y 280}
    {:x 520 :y 300}
    {:x 586 :y 270}
    {:x 649 :y 237}
    {:x 721 :y 246}
    {:x 789 :y 247}]])


(def app-state
  (atom
   {:layer 1
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
                (for [[key kidx] (map vector row (range))]
                  (try
                    (let [label (util/show-key key)]
                      (dom/text #js{:x (get-in magic-numbers [ridx kidx :x])
                                    :y (get-in magic-numbers [ridx kidx :y])}
                                label))
                    (catch js/Error e
                      (dom/text #js{:x (get-in magic-numbers [ridx kidx :x])
                                    :y (get-in magic-numbers [ridx kidx :y])
                                    :fill "red"}
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
                  (dom/textarea #js{:rows 20
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
