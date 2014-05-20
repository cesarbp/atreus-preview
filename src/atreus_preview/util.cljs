(ns atreus-preview.util
  (:require [atreus-preview.constants :as constants]
            [clojure.string :as str]))

(defn label-type
  [label]
  (or (and (contains? constants/modifiers  label) :modifier)
      (and (contains? constants/key->code  label) :big-label)
      (and (contains? constants/expansions label) :short-label)))

(defn abbreviate
  [label]
  (or (constants/abbreviations label) label))

(defmulti show-key
  (fn [key]
    (cond
     (string? key)     :label
     (integer? key)    :keycode
     (sequential? key) :combo)))

(defmethod show-key
  :label
  [label]
  (let [label (str/upper-case label)]
    (case (label-type label)
      (:modifier :big-label) (abbreviate label)
      :short-label label
      (throw (js/Error. (str "Invalid key label: " label))))))

(defmethod show-key
  :keycode
  [kc]
  (let [label (constants/code->key kc)]
    (if label
      (abbreviate label)
      (throw (js/Error. (str "Invalid key code: " kc
                             ". Maybe you meant to pass a string?"))))))

(defmethod show-key
  :combo
  [combo]
  (let [modifier (str/upper-case (str (first combo)))
        label    (str/upper-case (str (second combo)))
        t        (label-type label)]
    ;(assert (= t :big-label))
    (cond
     (= "SHIFT" modifier)
     (let [kc (constants/key->code label)
           actual-label (constants/code->key (constants/shift kc))]
       (abbreviate actual-label))
     ;; TODO
     :else (str modifier label))))

(defn valid-edn?
  "Check for '3d' sequence"
  [edn]
  (sequential? (get-in edn [0 0])))
