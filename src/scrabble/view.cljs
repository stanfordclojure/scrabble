(ns scrabble.view)


(def cell-size 40)

(defn board [{:keys [rows cols]} state]
  (let [cells #(* % cell-size)
        width  (cells cols)
        height (cells rows)]
    [:svg {:width width
           :height height
           :view-box [0 0 width height]}
     (for [{:keys [x y tile] :as cell} state]
       (if tile
         ^{:key [x y]}
         [:image {:x (cells x)
                  :y (cells y)
                  :width cell-size :height cell-size
                  :href (str "/img/" (name tile) ".png")}]
         ^{:key [x y]}
         [:rect (-> cell
                  (merge {:width cell-size
                          :height cell-size})
                  (update :x cells)
                  (update :y cells))]))]))


(defn tile [k]
  [:img {:src (str "/img/" (name k) ".png")
         :style {:width cell-size :height cell-size}}])

(defn rack [tiles]
  [:div (for [t tiles]
    ^{:key (gensym t)}
    [tile t])])
