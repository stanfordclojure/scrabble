(ns scrabble.view
  (:require [scrabble.constants :refer [cell-size]]
            [scrabble.assets :refer [b64-tiles]]))


(defn board [{:keys [rows cols]} state]
  (let [cells #(* % cell-size)
        width  (cells cols)
        height (cells rows)]
    [:svg {:width width
           :height height
           :view-box [0 0 width height]}
     (for [{:keys [x y tile] :as c} state]
       (if tile
         ^{:key [x y]}
         [:image {:x (cells x)
                  :y (cells y)
                  :width cell-size :height cell-size
                  :href #_(str "/img/" (name tile) ".png")
                  (str "data:image/png;base64," (get b64-tiles tile))}]
         ^{:key [x y]}
         [:rect (-> c
                  (merge {:width cell-size
                          :height cell-size})
                  (update :x cells)
                  (update :y cells))]))]))


(defn tile [k]
  [:img {:src #_(str "/img/" (name k) ".png")
         (str "data:image/png;base64," (get b64-tiles k))
         :style {:width cell-size :height cell-size}}])

(defn rack [tiles]
  [:div (for [t tiles]
    ^{:key (gensym t)}
    [tile t])])
