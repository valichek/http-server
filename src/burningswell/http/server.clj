(ns burningswell.http.server
  "The Burning Swell HTTP server"
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

(s/defrecord Server
    [bind-address :- s/Str
     bind-port :- s/Int
     handler-fn :- s/Any
     max-body :- s/Int
     max-line :- s/Int
     max-ws :- s/Int
     queue-size :- s/Int
     stop-fn :- s/Any
     thread :- s/Int
     worker-name-prefix :- s/Str]
  {s/Any s/Any})

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
          _ (assert (ifn? handler) "No Ring handler given.")
          config (http-kit-config server)
          stop-fn (httpkit/run-server handler config)]
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
  (assoc server :stop-fn nil))

(extend-protocol component/Lifecycle
  Server
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
  :worker-name-prefix - The prefix used for worker threads"
  [config]
  (assert (ifn? (:handler-fn config)) "Not a function: :handler-fn")
  (map->Server (merge *defaults* config)))
