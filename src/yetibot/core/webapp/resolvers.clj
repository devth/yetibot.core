(ns yetibot.core.webapp.resolvers
  (:require
    [cuerdas.core :refer [kebab snake]]
    [yetibot.core.adapters.adapter :as adapter]
    [yetibot.core.db.history :as history]
    [yetibot.core.handler :refer [handle-unparsed-expr]]
    [taoensso.timbre :refer [error debug info color-str]]))

(defn eval-resolver
  [context {:keys [expr] :as args} value]
  (debug "eval-resolver" args)
  (let [result (handle-unparsed-expr expr)]
    (if (coll? result)
      result
      [result])))

(defn adapters-resolver
  [context {:keys [] :as args} value]
  (->>
    @adapter/adapters
    vals
    (map #(hash-map :uuid (name (adapter/uuid %))
                    :platform (adapter/platform-name %)))))

(defn history-resolver
  [context {:keys [offset limit chat_source_room chat_source_adapter] :as args} value]
  (info "history resolver with args" args)
  (history/query {:query/identifiers identity
                  :limit/clause limit
                  :offset/clause offset
                  :order/clause "created_at DESC"}))

  ;; (take-last 20 (history/query {:identifiers identity})))
