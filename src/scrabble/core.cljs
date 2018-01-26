(ns scrabble.core
  (:require
    [devcards.core]
    [reagent.core :as reagent]
    [scrabble.constants :as const]
    [scrabble.view :as view]
    [clojure.string :as string]
    [goog.net.XhrIo :as gxhr])
  (:require-macros
    [devcards.core :as dc :refer [defcard defcard-rg deftest]]))

(declare coll->trie)

(defn spy [& values]
  (apply println values)
  (last values))

(def dictionary "https://raw.githubusercontent.com/jonbcard/scrabble-bot/master/src/dictionary.txt")

(enable-console-print!)

(defn- get-data [url f]
  (gxhr/send url (fn [e]
                   (let [xhr (-> e .-target)]
                     (f (-> xhr (.getResponseText)))))))


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
         :turn 0
         :dictionary {}}))

(add-watch state :game-state
           (fn [a k old-state new-state]
             (println new-state)))


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


(defn- load-dictionary [state dictionary-string]
  (let [dictionary (->> dictionary-string
                     .toLowerCase
                     (string/split-lines)
                     (coll->trie)
                     spy)]
    (assoc state :dictionary dictionary)))


(get-data dictionary #(swap! state load-dictionary %))


;; STATE TRANSITION ;;;;
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


(defn- multiset-diff [s1 s2]
  (mapcat
    (fn [[x n]] (if (neg? n)
                  (throw (js/Error. "not enough elements"))
                  (repeat n x)))
    (apply merge-with - (map frequencies [s1 s2]))))


(defn- play-tiles
  "Places tiles, does not perform validation"
  [board {:keys [tiles] :as player} position->tile]
  (let [new-tiles (multiset-diff tiles (map val position->tile))
        f (fn [board [position tile]]
            (assoc-in board (concat position [:tile]) tile))
        new-board (reduce f board position->tile)]
    {:board new-board
     :player (assoc player :tiles new-tiles)}))


;; STATE TRANSITION ;;;;
(defn player-turn [{:keys [bag players turn board] :as state} position->tile]
  ;; game is started
  ;; get correct player
  ;; call play-tiles
  ;; redraw tiles
  ;; update bag
  ;; update turn
  ;; update board
  ;; TODO update score
  (let [player (nth players (mod turn (count players)))
        {:keys [board player]} (play-tiles board player position->tile)
        {:keys [bag player]} (replenish-rack bag player)]
    {:bag bag
     :player player
     :turn (inc turn)
     :board board}))


(defn- coll->trie [coll]
  (reduce #(assoc-in %1 %2 {:end true}) {} coll))

(defn- is-prefix? [trie word]
  (boolean (get-in trie word)))

(defn- is-word? [trie word]
  (true? (get-in trie (concat word [:end]))))

(comment
  (def tiles [:A :P :B :L :E :P])
  (def position->tile {[4 5] :A  [5 5] :P  [6 5] :P  [7 5] :L  [8 5] :E})
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

