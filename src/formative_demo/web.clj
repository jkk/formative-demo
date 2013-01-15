(ns formative-demo.web
  (:require [compojure.core :refer [defroutes GET POST ANY]]
            [compojure.handler :refer [site]]
            [ring.middleware.stacktrace :as trace]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]
            [formative.core :as f]
            [formative.parse :as fp]
            [hiccup.page :as page]
            [clojure.pprint :refer [pprint]]))

(defn validate-upload [values]
  (let [filename (not-empty (get-in values [:picture "filename"]))]
    (when (and filename (not (re-find #"\.jpg$" filename)))
      {:keys [:picture] :msg "JPG files only"})))

(def demo-form
  {:enctype "multipart/form-data"
   :fields [{:name :h1 :type :heading :text "Section 1"}
            {:name :full-name}
            {:name :email :type :email}
            {:name :spam :type :checkbox :label "Yes, please spam me."}
            {:name :password :type :password}
            {:name :password-confirm :type :password}
            {:name :h2 :type :heading :text "Section 2"}
            {:name :note :type :html
             :html [:div.alert.alert-info "Please make note of this note."]}
            {:name :date :type :date-select}
            {:name :flavors :type :checkboxes
             :options ["Chocolate" "Vanilla" "Strawberry" "Mint"]}
            {:name :h3 :type :heading :text "Section 3"}
            {:name :state :type :us-state
             :placeholder "Select a state"}
            {:name :explanation :type :textarea :label "Explain yourself"}
            {:name :picture :type :file :title "Choose a file"}]
   :validations [[:required [:full-name :email :password :state]]
                 [:min-length 8 :password]
                 [:equal [:password :password-confirm]]
                 [:min-length 2 :flavors "Please select two or more flavors"]]
   :validator validate-upload})

(defn layout [& body]
  (page/html5
    [:head
     [:title "Formative Demo"]
     (page/include-css "//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/2.2.2/css/bootstrap.min.css")
     (page/include-css "//google-code-prettify.googlecode.com/svn/trunk/src/prettify.css")
     [:style "body { margin: 2em; }"]]
    [:body {:onload "prettyPrint()"}
     body]
    (page/include-js "//ajax.googleapis.com/ajax/libs/jquery/1.9.0/jquery.min.js")
    (page/include-js "//google-code-prettify.googlecode.com/svn/trunk/src/prettify.js")
    (page/include-js "//google-code-prettify.googlecode.com/svn/trunk/src/lang-clj.js")
    (page/include-js "//raw.github.com/grevory/bootstrap-file-input/master/bootstrap.file-input.js")))

(defn show-demo-form [params & {:keys [problems]}]
  (let [defaults {:spam true
                  :date (java.util.Date.)}]
    (layout
      [:h1 "Formative Demo"]
      (f/render-form (assoc demo-form
                            :values (merge defaults params)
                            :problems problems)))))

(defn submit-demo-form [params]
  (fp/with-fallback (partial show-demo-form params :problems)
    (let [values (fp/parse-params demo-form params)]
      (layout
        [:h1 "Thank you!"]
        [:pre.prettyprint.lang-clj (with-out-str (pprint values))]))))

(defroutes routes
  (GET "/" [& params] (show-demo-form params))
  (POST "/" [& params] (submit-demo-form params))
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
