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



(defn make-player
  "Takes a player name, and outputs player map"
  [n]
  {:rack []
   :name n
   :score 0})


(def state
  (atom {:board board
         :bag bag
         :players [(make-player "Allan")
                   (make-player "Adi")
                   (make-player "Rich")]
         :turn 0}))


(defn- draw-tiles
  "draws at most n tiles from bag
  returns tiles drawn and remaining tiles"
  [bag n]
  {:pre [(coll? bag) (pos-int? n)]}
  [(take n bag) (drop n bag)])


(defn- replenish-rack [bag {:keys [rack] :as player}]
  (let [rack-size (count rack)
        [tiles-drawn remaining-bag] (draw-tiles (shuffle bag) (- 7 rack-size))]
    {:bag remaining-bag
     :player (update player :rack concat tiles-drawn)}))


(defn init-game [{:keys [bag players] :as state}]
  (let [player-cnt (count players)]
    (loop [bag bag
           players players
           state state]
      (if (pos? (count players))
        (let [data (replenish-rack bag (first players))
              new-bag (:bag data)
              new-player (:player data)
              new-state (-> state
                          (update :players conj new-player)
                          (update :players subvec 1)
                          (assoc :bag new-bag))]
          (recur new-bag (rest players) new-state))
        state))))


(defn- play-tiles
  "Places tiles, does not perform validation"
  [board {:keys [tiles] :as player} tile->position]
  #_(let [new-tiles ]
    {:board new-board
     :player new-player}))


(defn player-turn [{:keys [bag players turn board] :as state}]
  )


(comment
  (init-game @state)
  (replenish-rack [:A :B :C :F :Q :blank :R :A :A :B] (make-player "Adi"))
  (replenish-rack [:A :B :A :A :B] (make-player "Adi"))
  (draw-tiles [:A :B :E] 7)
  (= (make-player "Allan")
     {:rack [], :name "Allan", :score 0})
  )


(defn main []
  ;; conditionally start the app based on whether the #main-app-area
  ;; node is on the page
  (if-let [node (.getElementById js/document "main-app-area")]
    (reagent/render node [:div "This is working"])))

(main)

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


(defcard-rg rack
  [view/rack (take 7 (shuffle bag))])
