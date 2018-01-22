(ns scrabble.core
  (:require
    [devcards.core]
    [reagent.core :as reagent])
  (:require-macros
    [devcards.core :as dc :refer [defcard defcard-rg deftest]]))

(enable-console-print!)

(defcard-rg first-card
  [:h3 "hello"])

(defn main []
  ;; conditionally start the app based on whether the #main-app-area
  ;; node is on the page
  (if-let [node (.getElementById js/document "main-app-area")]
    (reagent/render node [:div "This is working"])))

(main)

