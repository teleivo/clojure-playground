(ns retry.core)

(defn retry
  "Retry f three times (if it throws an Exception), or until it succeeds"
  [f & args]
  (first (drop-while #(instance? Exception %)
                     (repeatedly 3 (fn []
                                     (try
                                       (apply f args)
                                       (catch Exception e e)))))))