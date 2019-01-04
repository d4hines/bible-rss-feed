(defproject bible-rss "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/data.xml "0.0.8"]
                 [org.clojure/data.csv "0.1.4"]
                 [clj-rss "0.2.3"]]
  :main ^:skip-aot bible-rss.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
