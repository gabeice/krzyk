(ns krzyk.subs
  (:require
   [re-frame.core :refer [reg-sub]]))

(reg-sub
 ::on?
 (fn [db]
   (:on? db)))

(reg-sub
  ::listening?
  (fn [db]
    (:listening? db)))
