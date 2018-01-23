(ns scrabble.constants
  (:require
    [devcards.core]
    [reagent.core :as reagent])
  (:require-macros
    [devcards.core :as dc :refer [defcard defcard-rg deftest]]))

(def special-squares
  {:TW {:color "salmon" ;; "tomato"
        :positions [[0 0] [0 7] [0 14]
                    [7 0] [7 14]
                    [14 0] [14 7] [14 14]]}
   :DW {:color "pink"
        :positions [[1 1] [2 2] [3 3] [4 4] [7 7]
                    [10 10] [11 11] [12 12] [13 13]
                    [13 1] [12 2] [11 3] [10 4]
                    [1 13] [2 12] [3 11] [4 10]]}
   :TL {:color "cornflowerblue"
        :positions [[5 1] [1 5] [5 5]
                    [9 1] [1 9] [9 5] [5 9] [9 9]
                    [13 5] [13 9] [9 13] [5 13]]}
   :DL {:color "lightblue"
        :positions [[0 3] [0 11] [2 6] [2 8] [3 7] [8 6] [6 6]
                    [3 0] [11 0] [6 2] [8 2] [7 3] [6 8] [8 8]
                    [14 3] [12 6] [12 8] [11 7] [14 11]
                    [3 14] [6 12] [8 12] [7 11] [11 14]]}})


(def square->color
  (let [f (fn [o [_ {:keys [color positions]}]]
            (reduce (fn [o p]
                      (assoc o p color)) o positions))]
    (reduce f {} special-squares)))



(def value->tiles
  {0 [:blank]
   1 [:A :E :I :L :N :O :R :S :T :U]
   2 [:D :G]
   3 [:B :C :M :P]
   4 [:F :H :V :W :Y]
   5 [:K]
   8 [:J :X]
   10 [:Q :Z]})

(def tile->value
  (let [f (fn [m [v ts]]
            (reduce (fn [m t]
                      (assoc m t v)) m ts))]
    (reduce f {} value->tiles)))


(def tile-frequencies
  {:E 12
   :A 9
   :I 9
   :O 8
   :N 6
   :R 6
   :T 6
   :L 4
   :S 4
   :U 4
   :D 4
   :G 3
   :B 2
   :C 2
   :M 2
   :P 2
   :F 2
   :H 2
   :V 2
   :W 2
   :Y 2
   :K 1
   :J 1
   :X 1
   :Q 1
   :Z 1})


(defcard doc-tile-values
  "
0 Points - Blank tile.

1 Point - A, E, I, L, N, O, R, S, T and U.

2 Points - D and G.

3 Points - B, C, M and P.

4 Points - F, H, V, W and Y.

5 Points - K.

8 Points - J and X.

10 Points - Q and Z.
  ")

(defcard value->tiles
  value->tiles)


(defcard tile->values
  tile->value)

(defcard tile-frequencies
  tile-frequencies)
