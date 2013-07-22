(defproject formative-demo "0.0.2-SNAPSHOT"
  :description "Demo app for the Formative lib"
  :url "http://formative-demo.herokuapp.com"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [compojure "1.1.5"]
                 [ring/ring-core "1.2.0"]
                 [ring/ring-jetty-adapter "1.2.0"]
                 [ring/ring-devel "1.2.0"]
                 [amalloy/ring-gzip-middleware "0.1.2"]
                 [environ "0.3.0"]
                 [formative "0.7.0"]
                 [hiccup "1.0.2"]
                 [prismatic/dommy "0.1.1"]]
  :min-lein-version "2.0.0"
  :plugins [[environ/environ.lein "0.3.0"]
            [lein-cljsbuild "0.3.2"]]
  :hooks [environ.leiningen.hooks]
  :profiles {:production {:env {:production true}}}
  :cljsbuild {:builds [{:source-paths ["src-cljs"]
                        :compiler {:output-to "resources/public/js/main.js"
                                   :optimizations :advanced
                                   :pretty-print false}}]})