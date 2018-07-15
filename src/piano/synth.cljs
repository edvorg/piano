(ns piano.synth
  (:require [piano.freqs :refer [compute-freq]]))

(def context (js/AudioContext.))

(def playing (atom {}))

(defn play [note octave]
  (swap! playing update [note octave]
         (fn [prev]
           (or prev
               (let [note-node (.createOscillator context)
                     gain-node (.createGain context)]
                 (aset note-node "frequency" "value" (compute-freq note octave))
                 (aset gain-node "gain" "value" 1)
                 (.connect note-node gain-node)
                 (.connect gain-node (.-destination context))
                 (.start note-node)
                 {:note-node note-node
                  :gain-node gain-node})))))

(defn stop [note octave]
  (swap! playing update [note octave]
         (fn [{:keys [:note-node :gain-node]}]
           (js/console.log note-node)
           (js/console.log gain-node)
           (when note-node
             (.stop note-node)
             (.disconnect note-node))
           (when gain-node
             (.disconnect gain-node))
           nil)))
