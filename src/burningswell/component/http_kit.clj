(ns burningswell.component.http-kit
  "HTTP Kit server component."
  (:require [clojure.set :refer [rename-keys]]
            [clojure.tools.logging :as log]
            [com.stuartsierra.component :as component]
            [org.httpkit.server :as httpkit]
            [schema.core :as s]))

(def ^:dynamic *defaults*
  "The default server config."
  {:bind-address "0.0.0.0"
   :bind-port 8090
   :max-body 8388608
   :max-line 4096
   :max-ws 4194304
   :queue-size 20480
   :thread 4
   :worker-name-prefix "worker-"})

(def Server
  "The server schema."
  {:handler-fn s/Any
   (s/optional-key :bind-address) s/Str
   (s/optional-key :bind-port) s/Int
   (s/optional-key :max-body) s/Int
   (s/optional-key :max-line) s/Int
   (s/optional-key :max-ws) s/Int
   (s/optional-key :queue-size) s/Int
   (s/optional-key :thread) s/Int
   (s/optional-key :worker-name-prefix) s/Str
   s/Keyword s/Any})

(s/defn ^:always-validate http-kit-config
  "Return the HTTPKit config."
  [server :- Server]
  (->> {:bind-address :ip
        :bind-port :port}
       (rename-keys server)))

(s/defn ^:always-validate start-server :- Server
  "Start the HTTP Kit server."
  [server :- Server]
  (if (:stop-fn server)
    server
    (let [handler ((:handler-fn server) server)
          stop-fn (httpkit/run-server handler (http-kit-config server))]
      (assert handler "No Ring handler given.")
      (log/infof "HTTP Kit server started on %s:%s."
                 (:bind-address server)
                 (:bind-port server))
      (assoc server :stop-fn stop-fn))))

(s/defn ^:always-validate stop-server :- Server
  "Stop the HTTP Kit server."
  [server :- Server]
  (when-let [stop-fn (:stop-fn server)]
    (stop-fn)
    (log/infof "HTTP Kit server stopped on %s:%s."
               (:bind-address server) (:bind-port server)))
  (dissoc server :stop-fn))

(defrecord HTTPKitServer [handler-fn stop-fn]
  component/Lifecycle
  (start [server]
    (start-server server))
  (stop [server]
    (stop-server server)))

(s/defn ^:always-validate http-kit-server :- Server
  "Make a new HTTP Kit server component.

  Required:

  :handler-fn - A function of one argument, that gets called with the
  server component and it's dependencies, returning a Ring handler

  Optional:

  :bind-address       - Which IP to bind on
  :bind-port          - Which port to listen on
  :max-body           - The max. HTTP body size
  :max-line           - The max. initial HTTP line length
  :max-ws             - The max. websocket message size
  :queue-size         - max job queued before reject to project self
  :thread             - http worker thread count
  :worker-name-prefix - The prfix used for worker threads"
  [config :- Server]
  (map->HTTPKitServer (merge *defaults* config)))
