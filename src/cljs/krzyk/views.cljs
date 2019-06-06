(ns krzyk.views
  (:require
   [re-frame.core :refer [subscribe dispatch]]
   [krzyk.subs :as subs]
   [krzyk.events :as events]))

(defn spectrogram []
  [:section#outer-bar])

(defn mic-button []
  [:div#microphone
   [:i {:class "fa fa-microphone"
        :aria-hidden "true"
        :on-click #(dispatch [::events/analyze])}]])

(defn play-pause-button []
  (let [listening? (subscribe [::subs/listening?])]
    [:div#play-controls
     (if @listening?
         [:div#pause-button
          [:i {:class "fa fa-pause"
               :aria-hidden "true"
               :on-click #(dispatch [::events/pause])}]]
         [:div#play-button
          [:i {:class "fa fa-play"
               :aria-hidden "true"
               :on-click #(dispatch [::events/resume])}]])]))

(defn display []
  (let [on? (subscribe [::subs/on?])]
    [:div#display
     (if @on?
         [spectrogram]
         [mic-button])]))

(defn footer []
  (let [on? (subscribe [::subs/on?])]
    [:div#footer
     (when @on?
       [play-pause-button])]))

(defn main-panel []
  [:div
   [:section#body-content
    [:h1#masthead "krzyk"]
    [:div#underline]
    [:section#main
     [display]]
    [footer]]])