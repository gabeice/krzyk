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

(defn display []
  (let [listening? (subscribe [::subs/listening?])]
    [:div#display
     (if @listening?
         [spectrogram]
         [mic-button])]))

(defn main-panel []
  [:div
   [:section#body-content
    [:h1#masthead "krzyk"]
    [:div#underline]
    [:section#main
     [display]]
    [:div#footer]]])