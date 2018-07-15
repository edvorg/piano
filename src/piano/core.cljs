(ns piano.core
  (:require [reagent.core :as reagent :refer [atom]]
            [cljsjs.hammer]
            [piano.freqs :refer [compute-freq]]
            [piano.octaves :refer [blacks whites]]
            [piano.synth :refer [play stop]]
            [oops.core :refer [oget oset! ocall oapply ocall! oapply!
                               oget+ oset!+ ocall+ oapply+ ocall!+ oapply!+]]))

(enable-console-print!)

(println "This text is printed from src/piano/core.cljs. Go ahead and edit it and see reloading in action.")

(def app-state (atom {:keys-limit 12
                      :skip       24}))

(defn note-info-str [note octave]
  (str (name note) octave))

(defn note-info-view [note octave freq]
  [:div.note-info
   [:div (note-info-str note octave)]
   [:div freq]])

(defn white-key-view [width i note octave freq]
  (let [pressed (atom nil)]
    (fn [width i note octave freq]
      [:div.white.key {:style           {:width  (str width "%")
                                         :height "50%"
                                         :left   (str (* width i) "%")}
                       :on-touch-start  (fn [_]
                                          (play note octave)
                                          (reset! pressed true))
                       :on-touch-move   (fn [_]
                                          (play note octave)
                                          (reset! pressed true))
                       :on-touch-cancel (fn [_]
                                          (stop note octave)
                                          (reset! pressed nil))
                       :on-touch-end    (fn [_]
                                          (stop note octave)
                                          (reset! pressed nil))
                       :class           (cond-> ""
                                          @pressed (str "pressed"))}
       [note-info-view note octave freq]])))

(defn black-key-view [width i note octave freq offset]
  (let [pressed (atom nil)]
    (fn [width i note octave freq]
      [:div.black.key {:style           {:width  (str width "%")
                                         :height "30%"
                                         :left   (str (- (* width (- i 0.5)) offset) "%")}
                       :on-touch-start  (fn [_]
                                          (play note octave)
                                          (reset! pressed true))
                       :on-touch-move   (fn [_]
                                          (play note octave)
                                          (reset! pressed true))
                       :on-touch-cancel (fn [_]
                                          (stop note octave)
                                          (reset! pressed nil))
                       :on-touch-end    (fn [_]
                                          (stop note octave)
                                          (reset! pressed nil))
                       :class           (cond-> ""
                                          @pressed (str "pressed"))}
       [note-info-view note octave freq]])))

(defn piano-view []
  (let []
    (fn []
      (let [keys-limit  (:keys-limit @app-state)
            skip        (:skip @app-state)
            white-width (/ 100.0 keys-limit)
            black-width (/ 100.0 keys-limit)]
        [:div
         (for [[i {:keys [:note :octave :freq]}] (map-indexed vector (whites keys-limit skip))]
           ^{:key i}
           [white-key-view white-width i note octave freq])
         (for [{:keys [:i :note :octave :freq]} (blacks keys-limit skip)]
           ^{:key i}
           [black-key-view black-width i note octave freq (* skip black-width)])]))))

(reagent/render-component [piano-view]
                          (. js/document (getElementById "app")))

(aset js/window "ontouchstart" (fn [e]
                                 (when (< 1 (aget e "touches" "length"))
                                   (.preventDefault e))))

(defn on-js-reload [])
