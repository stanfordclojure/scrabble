(ns scrabble.core
  (:require
    [devcards.core]
    [reagent.core :as reagent]
    [scrabble.constants :as const]
    [scrabble.view :as view])
  (:require-macros
    [devcards.core :as dc :refer [defcard defcard-rg deftest]]))

(enable-console-print!)


(def board
  (->>
    (for [x (range 15)]
      (for [y (range 15)]
        {:fill (const/square->color [x y] "lightyellow")
         :stroke "black"
         :x x
         :y y}))
    (mapv vec)))


(defn- unroll-frequencies [freqs]
  (mapcat (fn [[x n]] (repeat n x)) freqs))

(def bag (unroll-frequencies const/tile-frequencies))

(defcard-rg board
  [view/board {:rows 15 :cols 15}
   (apply concat (-> board
                   (assoc-in [2 4 :tile] :C)
                   (assoc-in [2 5 :tile] :L)
                   (assoc-in [2 6 :tile] :O)
                   (assoc-in [2 7 :tile] :J)
                   (assoc-in [2 8 :tile] :U)
                   (assoc-in [2 9 :tile] :R)
                   (assoc-in [2 10 :tile] :E)
                   (assoc-in [3 7 :tile] :O)
                   (assoc-in [4 7 :tile] :Y)
                   (assoc-in [5 7 :tile] :O)
                   (assoc-in [6 7 :tile] :U)
                   (assoc-in [7 7 :tile] :S)))])

(defcard-rg tile
  [view/tile :A])


(defcard-rg tiles
  [view/rack (take 7 (shuffle bag))])



(defn main []
  ;; conditionally start the app based on whether the #main-app-area
  ;; node is on the page
  (if-let [node (.getElementById js/document "main-app-area")]
    (reagent/render node [:div "This is working"])))

(main)

