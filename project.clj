(defproject burningswell/http-kit-component "0.1.0-SNAPSHOT"
  :description "A HTTP Kit server component"
  :url "https://github.com/burningswell/http-kit-component"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[com.stuartsierra/component "0.2.2"]
                 [http-kit "2.1.16"]
                 [org.clojure/clojure "1.6.0"]]
  :profiles {:dev {:plugins [[jonase/eastwood "0.1.4"]
                             [lein-difftest "2.0.0"]
                             [listora/whitespace-linter "0.1.0"]]}}
  :aliases {"lint" ["do" ["whitespace-linter"] ["eastwood"]]
            "ci" ["do" ["difftest"] ["lint"]]})
