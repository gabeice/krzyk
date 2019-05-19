(ns krzyk.events
  (:require
   [re-frame.core :refer [reg-event-db reg-event-fx reg-fx]]
   [cljsjs.d3]))

(defn connect-mic [stream]
  (let [ctx (js/AudioContext.)
        analyzer (.createAnalyser ctx)
        mic-src (.createMediaStreamSource ctx stream)]
    (.connect mic-src analyzer)
    analyzer))

(defn render-frame [analyzer frequency-data]
  (js/requestAnimationFrame #(render-frame analyzer frequency-data))
  (.getByteFrequencyData analyzer frequency-data))

(defn show-frequency [frequency-data]
  (aset (.getElementById js/document "outer-bar") "innerHTML" "")
  (-> js/d3
      (.select "#outer-bar")
      (.selectAll "div")
      (.data frequency-data)
      (.enter)
      (.append "div")
      (.style "height" #(str (* % 2) "px"))))

(defn analyze-mic-input [stream]
  (let [analyzer (connect-mic stream)
        frequency-data (js/Uint8Array. (.-frequencyBinCount analyzer))
        render-frame #(render-frame analyzer frequency-data)]
    (render-frame)
    (js/setInterval #(show-frequency frequency-data) 100)))

(reg-fx
  :turn-on-mic
  (fn [_]
    (.then
      (.getUserMedia (.-mediaDevices js/navigator) (js-obj "audio" true "video" false))
      analyze-mic-input)))

(reg-event-fx
  ::analyze
  (fn [cofx [_]]
    (-> cofx
        (assoc-in [:db :listening?] true)
        (assoc :turn-on-mic true))))

(reg-event-db
  ::initialize-db
  (fn [_ _] {}))