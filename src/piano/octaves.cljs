(ns piano.octaves
  (:require [piano.freqs :refer [compute-freq]]
            [clojure.string :as s]))

(def octave [{:note :C}
             {:note :C#}
             {:note :D}
             {:note :D#}
             {:note :E}
             {:note :F}
             {:note :F#}
             {:note :G}
             {:note :G#}
             {:note :A}
             {:note :A#}
             {:note :B}])

(def octaves (->> (range)
                  (mapcat (fn [i]
                            (->> octave
                                 (map #(assoc % :octave i)))))
                  (map-indexed vector)
                  (map (fn [[i {:keys [:note :octave] :as key}]]
                         (assoc key
                                :i i
                                :black (s/includes? (name note) "#")
                                :freq (compute-freq note octave))))))

(defn whites [n skip]
  (->> octaves
       (filter (comp not :black))
       (drop skip)
       (take n)))

(defn blacks [n skip]
  (->> (filter :black octaves)
       (map-indexed vector)
       (map (fn [[d key]]
              (update key :i - d)))
       (filter (fn [{:keys [:i]}]
                 (< skip i)))
       (take-while (fn [{:keys [:i]}]
                     (< i (+ n skip))))))
