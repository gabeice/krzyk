(ns krzyk.events
  (:require
   [re-frame.core :refer [reg-event-db reg-event-fx reg-fx dispatch]]
   [cljsjs.d3]))

(defn- connect-mic [stream]
  (let [ctx (js/AudioContext.)
        analyzer (.createAnalyser ctx)
        mic-src (.createMediaStreamSource ctx stream)]
    (.connect mic-src analyzer)
    analyzer))

(defn- render-frame [analyzer frequency-data]
  (js/requestAnimationFrame #(render-frame analyzer frequency-data))
  (.getByteFrequencyData analyzer frequency-data))

(reg-fx
  :render-frequency-data
  (fn [frequency-data]
    (aset (.getElementById js/document "outer-bar") "innerHTML" "")
    (-> js/d3
        (.select "#outer-bar")
        (.selectAll "div")
        (.data frequency-data)
        (.enter)
        (.append "div")
        (.style "height" #(str (* % 2) "px")))))

(reg-fx
  :render-frame
  (fn [[analyzer frequency-data]]
    (render-frame analyzer frequency-data)))

(reg-event-fx
  ::show-frequency
  (fn [cofx _]
    (assoc cofx :render-frequency-data (get-in cofx [:db :frequency-data])
                :dispatch-later        [{:ms 100 :dispatch [::show-frequency]}])))

(reg-event-fx
  ::analyze-mic-input
  (fn [cofx [_ stream]]
    (let [analyzer (connect-mic stream)
          frequency-data (js/Uint8Array. (.-frequencyBinCount analyzer))]
      (-> cofx
          (assoc-in [:db :frequency-data] frequency-data)
          (assoc :render-frame [analyzer frequency-data]
                 :dispatch     [::show-frequency])))))

(reg-fx
  :turn-on-mic
  (fn [_]
    (.then
      (.getUserMedia (.-mediaDevices js/navigator) (js-obj "audio" true "video" false))
      #(dispatch [::analyze-mic-input %]))))

(reg-event-fx
  ::analyze
  (fn [cofx [_]]
    (-> cofx
        (assoc-in [:db :listening?] true)
        (assoc :turn-on-mic nil))))

(reg-event-db
  ::initialize-db
  (fn [_ _] {}))

(reg-fx :event #())