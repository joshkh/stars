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
                    :opacity (rand 1)
                    :r (rand 1.2))))

(defn field []
  (-> (create-svg-element :svg)
      (dommy/add-class! "starfield")
      (dommy/set-attr! :width (.-innerWidth js/window)
                       :height (.-innerHeight js/window))))

(defn animateTransform []
  (dommy/set-attr! (create-svg-element :animateTransform)
                   :attributeType "xml"
                   :attributeName "transform"
                   :type "rotate"
                   :values "0 0 0; 360 0 0"
                   :dur "500"
                   :additive "sum"
                   :repeatCount "indefinite"))


        ;  <filter id="fractal" filterUnits="objectBoundingBox"
        ;    x="0%" y="0%" width="100%" height="100%">
        ;    <feTurbulence id="fe-turb-fractal" type="fractalNoise" baseFrequency="0.0205" numOctaves="7"/>
        ;  </filter>


(defn filters []
  (-> (create-svg-element :defs)
      (-> (dommy/append! (-> (create-svg-element :filter)
                             (dommy/set-attr! :id "heavycloud"
                                              :filterUnits "objectBoundingBox"
                                              :x "0%"
                                              :y "0%"
                                              :width "100%"
                                              :height "100%")
                             (dommy/append! (-> (create-svg-element :feTurbulence)
                                                (dommy/set-attr! :id "fe-turb-fractal"
                                                                 :type "fractalNoise"
                                                                 :baseFrequency "0.0205"
                                                                 :numOctaves "7")))
                             )))

      ))

(defn star-plane []
  (let [svg (dommy/set-attr! (create-svg-element :g)
                             :transform "translate(400,400)")]
    (dommy/append! (reduce dommy/append! svg (take 3 (repeatedly star))) (animateTransform))))

(defn ground []
  (-> (create-svg-element :rect)
      (dommy/set-attr! :class "ground"
                       :x 0
                       :filter "url(#fe-turb-fractal)"
                       :y (- (.-innerHeight js/window) 300)
                       :height 400
                       :width (.-innerWidth js/window))
      (dommy/append! (-> (ground)))))

(defn mountains []
  (->
   (create-svg-element :g)
   (dommy/set-attr! :transform "scale(1,-1)")
   (dommy/append! (-> (create-svg-element :g)
                     (dommy/set-attr! :id "mountain-range" :transform (str "translate(0, -"
                                                                          ;  (.-innerHeight js/window)
                                                                           (- (.-innerHeight js/window) 300)
                                                                           ")"))))))


(defn buildall []
  (-> (dommy/append! (field) (star-plane))
      ; (dommy/append! (ground))
      (dommy/append! (mountains))
      (dommy/append! (filters))))

; (.log js/console "animate" )


(dommy/replace-contents! (dommy/sel1 :#app) (buildall))
