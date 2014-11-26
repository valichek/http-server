(defproject burningswell/http-kit-component "0.1.5"
  :description "A HTTP Kit server component"
  :url "https://github.com/burningswell/http-kit-component"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[com.stuartsierra/component "0.2.2"]
                 [http-kit "2.1.19"]
                 [org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [prismatic/schema "0.3.3"]]
  :aliases {"ci" ["do" ["difftest"] ["lint"]]
            "lint" ["do" ["whitespace-linter"] ["eastwood"]]
            "test-ancient" ["test"]}
  :deploy-repositories [["releases" :clojars]]
  :eastwood {:exclude-linters [:constant-test :redefd-vars :unused-ret-vals]}
  :profiles {:dev {:plugins [[jonase/eastwood "0.2.0"]
                             [lein-difftest "2.0.0"]
                             [listora/whitespace-linter "0.1.0"]]}})
