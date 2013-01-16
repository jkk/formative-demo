(defproject formative-demo "0.0.1-SNAPSHOT"
  :description "Demo app for the Formative lib"
  :url "http://formative-demo.herokuapp.com"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [compojure "1.1.5"]
                 [ring/ring-core "1.1.7"]
                 [ring/ring-jetty-adapter "1.1.7"]
                 [ring/ring-devel "1.1.7"]
                 [environ "0.3.0"]
                 [formative "0.3.2"]
                 [hiccup "1.0.2"]]
  :min-lein-version "2.0.0"
  :plugins [[environ/environ.lein "0.3.0"]]
  :hooks [environ.leiningen.hooks]
  :profiles {:production {:env {:production true}}})