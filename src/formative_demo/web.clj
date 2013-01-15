(ns formative-demo.web
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :refer [site]]
            [ring.middleware.stacktrace :as trace]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]))

(defroutes routes
  (GET "/" []
       {:status 200
        :headers {"Content-Type" "text/plain"}
        :body (pr-str ["Hello" :from 'Heroku])})
  (ANY "*" [] "Not found!"))

(def app
  (-> #'routes trace/wrap-stacktrace site))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty #'app
                     {:port port :join? false})))

;; For interactive development:
;; (.stop server)
;; (def server (-main))
