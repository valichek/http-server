(ns burningswell.component.http-kit
  "HTTP Kit server component."
  (:require [clojure.tools.logging :as log]
            [com.stuartsierra.component :as component]
            [org.httpkit.server :as httpkit]))

(defn start-server
  "Start the HTTP Kit server component."
  [{:keys [handler-fn] :as server}]
  (if (:stop-fn server)
    server
    (let [handler (handler-fn server)
          stop-fn (httpkit/run-server handler server)]
      (assert handler "No Ring handler given.")
      (log/infof "HTTP Kit server started on %s:%s."
                 (:ip server) (:port server))
      (assoc server :stop-fn stop-fn))))

(defn stop-server
  "Stop the HTTP Kit server component."
  [server]
  (when-let [stop-fn (:stop-fn server)]
    (stop-fn)
    (log/infof "HTTP Kit server stopped on %s:%s."
               (:ip server) (:port server)))
  (dissoc server :stop-fn))

(defrecord HTTPKitServer [handler-fn stop-fn]
  component/Lifecycle
  (start [server]
    (start-server server))
  (stop [server]
    (stop-server server)))

(defn http-kit-server
  "Make a new HTTP Kit server component.

  Required config:

  :handler-fn - A function of one argument, that gets called with the
  server component and it's dependencies, returning a Ring handler

  Optional HTTP Kit config:

  :ip                 - Which IP to bind on
  :max-body           - The max. HTTP body size
  :max-line           - The max. initial HTTP line length
  :port               - Which port to listen on
  :queue-size         - max job queued before reject to project self
  :thread             - http worker thread count
  :worker-name-prefix - The prfix used for worker threads"
  [{:keys [handler-fn ip max-body max-line port queue-size
           thread worker-name-prefix] :as config}]
  (map->HTTPKitServer
   (merge
    {:ip "0.0.0.0"
     :max-body 8388608
     :max-line 4096
     :max-ws 4194304
     :port 8090
     :queue-size 20480
     :thread 4
     :worker-name-prefix "worker-"}
    config)))
