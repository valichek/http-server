(defproject burningswell/http-kit-component "0.1.3"
  :description "A HTTP Kit server component"
  :url "https://github.com/burningswell/http-kit-component"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[com.stuartsierra/component "0.2.2"]
                 [http-kit "2.1.19"]
                 [org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.logging "0.3.0"]]
  :aliases {"lint" ["do" ["whitespace-linter"] ["eastwood"]]
            "ci" ["do" ["difftest"] ["lint"]]}
  :deploy-repositories [["releases" :clojars]]
  :profiles {:dev {:plugins [[jonase/eastwood "0.1.4"]
                             [lein-difftest "2.0.0"]
                             [listora/whitespace-linter "0.1.0"]]}})
