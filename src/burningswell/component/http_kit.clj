(ns burningswell.component.http-kit
  "HTTP Kit server component."
  (:require [com.stuartsierra.component :as component]
            [org.httpkit.server :as httpkit]))

(defn start-server
  "Start the HTTP Kit server component."
  [{:keys [handler-fn] :as server}]
  (if (:stop-fn server)
    server
    (let [handler (handler-fn server)]
      (assert handler "No Ring handler given.")
      (assoc server :stop-fn (httpkit/run-server handler server)))))

(defn stop-server
  "Stop the HTTP Kit server component."
  [server]
  (when-let [stop-fn (:stop-fn server)]
    (stop-fn))
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
  (map->HTTPKitServer config))
