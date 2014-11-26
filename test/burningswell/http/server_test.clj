(ns burningswell.http.server-test
  (:require [burningswell.http.server :refer :all]
            [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [org.httpkit.client :as http]))

(defn handler-fn [server]
  (fn [request]
    {:status 200
     :headers {"Content-Type" "text/plain"}
     :body "Hello world"}))

(deftest test-http-kit-server
  (let [server (http-kit-server {:handler-fn handler-fn})]
    (let [started (component/start server)]
      (is (fn? (:stop-fn started)))
      (let [response @(http/get "http://localhost:8090/")]
        (is (= (:status response) 200))
        (is (= (:body response) "Hello world")))
      (is (= (component/start started) started))
      (let [stopped (component/stop started)]
        (is (nil? (:stop-fn stopped)))
        (is (= (component/stop stopped) stopped))))))
