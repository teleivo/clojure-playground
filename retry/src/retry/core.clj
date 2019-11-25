(ns retry.core)

(defn retry
  "Retry f three times (if it throws an Exception), or until it succeeds"
  ([f]
   (retry f 3))
  ([f times]
   (try
     (f)
     (catch Exception e
       (if (= 1 times)
         (throw (ex-info "retry exceeded number of retries" {:retries 3} e))
         (retry f (dec times)))))))
