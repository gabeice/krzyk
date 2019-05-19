(ns krzyk.subs
  (:require
   [re-frame.core :refer [reg-sub]]))

(reg-sub
 ::listening?
 (fn [db]
   (:listening? db)))
