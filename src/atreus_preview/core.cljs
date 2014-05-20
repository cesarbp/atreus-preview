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
  [[{:x 66  :y 48 :side :left}
    {:x 136 :y 48 :side :left}
    {:x 202 :y 45 :side :left}
    {:x 268 :y 74 :side :left}
    {:x 330 :y 110 :side :left}
    {:x 489 :y 107 :side :right}
    {:x 558 :y 71 :side :right}
    {:x 618 :y 45 :side :right}
    {:x 687 :y 48 :side :right}
    {:x 755 :y 47 :side :right}]

   [{:x 52  :y 119 :side :left}
    {:x 123 :y 123 :side :left}
    {:x 188 :y 116 :side :left}
    {:x 252 :y 145 :side :left}
    {:x 316 :y 177 :side :left}
    {:x 499 :y 172 :side :right}
    {:x 561 :y 136 :side :right}
    {:x 626 :y 111 :side :right}
    {:x 693 :y 118 :side :right}
    {:x 761 :y 115 :side :right}]

   [{:x 39  :y 190 :side :left}
    {:x 107 :y 191 :side :left}
    {:x 179 :y 181 :side :left}
    {:x 245 :y 208 :side :left}
    {:x 306 :y 241 :side :left}
    {:x 364 :y 278 :side :left}
    {:x 510 :y 237 :side :right}
    {:x 572 :y 204 :side :right}
    {:x 634 :y 173 :side :right}
    {:x 709 :y 180 :side :right}
    {:x 777 :y 181 :side :right}]

   [{:x 24  :y 253 :side :left}
    {:x 94  :y 254 :side :left}
    {:x 168 :y 247 :side :left}
    {:x 230 :y 274 :side :left}
    {:x 295 :y 308 :side :left}
    {:x 448 :y 280 :side :right}
    {:x 520 :y 300 :side :right}
    {:x 586 :y 270 :side :right}
    {:x 649 :y 237 :side :right}
    {:x 721 :y 246 :side :right}
    {:x 789 :y 247 :side :right}]])

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
                      :let [x (get-in magic-numbers [ridx kidx :x])
                            y (get-in magic-numbers [ridx kidx :y])
                            side (get-in magic-numbers [ridx kidx :side])]]
                  (try
                    (let [label (util/show-key key)]
                      (dom/text #js{:x x
                                    :y y
                                    :transform (if (= :left side)
                                                 (str "rotate(10 " x " " y ")")
                                                 (str "rotate(-10 " x " " y ")"))
                                    :style #js{:text-anchor "middle"}}
                                label))
                    (catch js/Error e
                      (dom/text #js{:x x
                                    :y y
                                    :fill "red"
                                    :transform (if (= :left side)
                                                 (str "rotate(10 " x " " y ")")
                                                 (str "rotate(-10 " x " " y ")"))
                                    :style #js{:text-anchor "middle"}}
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
