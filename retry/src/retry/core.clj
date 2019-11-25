(ns retry.core)

(defn retry
  "Retry f three times (if it throws an Exception), or until it succeeds"
  ([times f & args]
   (if-let [result (try
                     (apply f args)
                     (catch Exception e
                       (when (= 1 times)
                         (throw (ex-info "retry exceeded number of retries" {:retries 3} e)))))]
     result
     (recur (dec times) f args))))