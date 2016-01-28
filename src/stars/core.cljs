(ns stars.core
  (:require [dommy.core :as dommy]))

(enable-console-print!)

(println "Edits to this text should show up in your developer console.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))


(defn on-js-reload [])

(defn create-svg-element [tag & [attrs]]
  (dommy/create-element "http://www.w3.org/2000/svg" tag))

(defn star []
  (->
   (create-svg-element :circle)
   (dommy/set-attr! :class "star"
                    :cx (- (rand-int 3000) 1500)
                    :cy (- (rand-int 3000) 1500)
                    :r (rand 1))))

(defn field []
  (-> (create-svg-element :svg)
      (dommy/add-class! "starfield")))

(defn animateTransform []
  (dommy/set-attr! (create-svg-element :animateTransform)
                   :attributeType "xml"
                   :attributeName "transform"
                   :type "rotate"
                   :values "0 0 0; 360 0 0"
                   :dur "1200"
                   :additive "sum"
                   :repeatCount "indefinite"))


(defn star-plane []
  (let [svg (dommy/set-attr! (create-svg-element :g)
                             :transform "translate(1000,1000)")]
    (dommy/append! (reduce dommy/append! svg (take 5000 (repeatedly star))) (animateTransform))))


(defn buildall []
  (-> (dommy/append! (field) (star-plane))))

(.log js/console "animate" )


(dommy/replace-contents! (dommy/sel1 :#app) (buildall))
