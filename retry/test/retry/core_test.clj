(ns retry.core-test
  (:require [clojure.test :refer :all]
            [retry.core :refer :all])
  (:import (clojure.lang ExceptionInfo)))

(deftest retry-test
  (testing "should call given fn and return its return value once its succeeds"
    (let [retried-times (atom 0)
          f (fn []
              (swap! retried-times inc)
              true)]
      (is (retry f))
      (is (= 1 @retried-times))))
  (testing "should retry given fn if it throws an exception until it succeeds"
    (let [retried-times (atom 0)
          return-values (list #(throw (IllegalStateException. "bad state"))
                              (constantly true))
          f (fn []
              (swap! retried-times inc)
              ((nth return-values (dec @retried-times))))]
      (is (retry f))
      (is (= 2 @retried-times))))
  (testing "should retry given fn if it throws an exception a maximum of three times and re-throw an exception info"
    (let [retried-times (atom 0)
          return-values (repeatedly #(throw (IllegalStateException. "bad state")))
          f (fn []
              (swap! retried-times inc)
              ((nth return-values (dec @retried-times))))]
      (let [result (try
                     (retry f)
                     (catch Exception e
                       e))]
        (is (instance? ExceptionInfo result))
        (is (= "retry exceeded number of retries" (ex-message result)))
        (is (instance? IllegalStateException (ex-cause result)))
        (is (= 3 @retried-times))))))
