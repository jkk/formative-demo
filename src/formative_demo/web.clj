(ns formative-demo.web
  (:require [compojure.core :refer [defroutes GET POST ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [ring.middleware.stacktrace :as trace]
            [ring.middleware.gzip :refer [wrap-gzip]]
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
  {:fields [{:name :h1 :type :heading :text "Section 1"}
            {:name :full-name}
            {:name "user[email]" :type :email}
            {:name :spam :type :checkbox :label "Yes, please spam me."}
            {:name :password :type :password}
            {:name :password-confirm :type :password}
            {:name :h2 :type :heading :text "Section 2"}
            {:name :note :type :html
             :html [:div.alert.alert-info "Please make note of this note."]}
            {:name :date :type :date-select}
            {:name :time :type :time-select}
            {:name :flavors :type :checkboxes
             :options ["Chocolate" "Vanilla" "Strawberry" "Mint"]}
            {:name :h3 :type :heading :text "Section 3"}
            {:name :state :type :us-state
             :placeholder "Select a state"}
            {:name :explanation :type :textarea :label "Explain yourself"}
            {:name :picture :type :file :title "Choose a file"}]
   :validations [[:required [:full-name "user[email]" :password :state]]
                 [:min-length 4 :password]
                 [:equal [:password :password-confirm]]
                 [:min-length 2 :flavors "Please select two or more flavors"]]
   :validator validate-upload
   :enctype "multipart/form-data"})

(def renderer-form
  {:method "get"
   ;:renderer :inline
   :submit-label nil
   :fields [{:name :renderer
             :type :select
             :options ["bootstrap-horizontal"
                       "bootstrap-stacked"
                       "table"]
             :onchange "this.form.submit()"}]})

(defn layout [& body]
  (page/html5
    [:head
     [:title "Formative Demo"]
     (page/include-css "//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/2.2.2/css/bootstrap.min.css")
     (page/include-css "//google-code-prettify.googlecode.com/svn/trunk/src/prettify.css")
     [:style
      "body { margin: 2em; }"
      "h1 { margin-bottom: 20px; }"
      ".form-horizontal .field-group { margin-bottom: 10px; }"
      ".well form, .well .field-group { margin-bottom: 0; }"
      ".nav-tabs { margin-top: 20px; margin-bottom: 20px; }"
      ".heading-row h3 { margin-bottom: 5px; }"
      ".form-table { width: 100%; }"
      ".form-table th { text-align: left; }"
      ".form-table h3 { border-bottom: 1px solid #ddd; }"
      ".form-table .label-cell { vertical-align: top; text-align: right; padding-right: 10px; padding-top: 10px; }"
      ".form-table td { vertical-align: top; padding-top: 5px; }"
      ".form-table .checkbox-row label { display: inline; margin-left: 5px; }"
      ".form-table .checkbox-row .input-shell { margin-bottom: 10px; }"
      ".form-table .submit-row th, .form-table .submit-row td { padding: 30px 0; }"
      ".form-table .problem th, .form-table .problem td { color: #b94a48; background: #fee; }"]]
    [:body {:onload "prettyPrint()"}
     body]
    (page/include-js "//google-code-prettify.googlecode.com/svn/trunk/src/prettify.js")
    (page/include-js "//google-code-prettify.googlecode.com/svn/trunk/src/lang-clj.js")
    (page/include-js "/js/main.js")))

(defn demo-header [active-tab]
  [:div.header
   [:h1 "Formative Demo"]
   [:p "This is a demo of the "
    [:a {:href "https://github.com/jkk/formative"}
     "Formative"]
    " library. Submit the form to see how the data gets validated and processed."]
   [:p [:a {:href "https://github.com/jkk/formative-demo"}
     "Full demo source code"]]
   [:ul.nav.nav-tabs
    [:li {:class (when (= :clj active-tab) "active")}
     [:a {:href "/"} "Clojure"]]
    [:li {:class (when (= :cljs active-tab) "active")}
     [:a {:href "/cljs"} "ClojureScript"]]]])

(defn show-demo-form [params & {:keys [problems]}]
  (let [renderer (if (:renderer params)
                   (keyword (:renderer params))
                   :bootstrap-horizontal)
        now (java.util.Date.)
        defaults {:spam true
                  :date now
                  :time now}]
    (layout
      [:div.pull-right.well.well-small
       (f/render-form (assoc renderer-form :values params))]
      (demo-header :clj)
      [:div.pull-left {:style "width: 50%"}
       (f/render-form (assoc demo-form
                             :renderer renderer
                             :values (merge defaults params)
                             :problems problems))]
      [:div.pull-right {:style "width: 47%"}
       [:pre.prettyprint.lang-clj {:style "word-wrap: normal; white-space: pre; overflow: hidden; font-size: 12px; border: none; background: #f8f8f8; padding: 10px"}
        "(def demo-form
  {:fields [{:name :h1 :type :heading :text \"Section 1\"}
            {:name :full-name}
            {:name \"user[email]\" :type :email}
            {:name :spam :type :checkbox :label \"Yes, please spam me.\"}
            {:name :password :type :password}
            {:name :password-confirm :type :password}
            {:name :h2 :type :heading :text \"Section 2\"}
            {:name :note :type :html
             :html [:div.alert.alert-info \"Please make note of this note.\"]}
            {:name :date :type :date-select}
            {:name :time :type :time-select}
            {:name :flavors :type :checkboxes
             :options [\"Chocolate\" \"Vanilla\" \"Strawberry\" \"Mint\"]}
            {:name :h3 :type :heading :text \"Section 3\"}
            {:name :state :type :us-state
             :placeholder \"Select a state\"}
            {:name :explanation :type :textarea :label \"Explain yourself\"}
            {:name :picture :type :file :title \"Choose a file\"}]
   :validations [[:required [:full-name \"user[email]\" :password :state]]
                 [:min-length 4 :password]
                 [:equal [:password :password-confirm]]
                 [:min-length 2 :flavors \"Please select two or more flavors\"]]
   :enctype \"multipart/form-data\"})

(defn show-demo-form [params & {:keys [problems]}]
  (let [now (java.util.Date.)
        defaults {:spam true
                  :date now
                  :time now}]
    (layout
      [:h1 \"Formative Demo\"]
      (f/render-form (assoc demo-form
                            :values (merge defaults params)
                            :problems problems)))))

(defn submit-demo-form [params]
  (fp/with-fallback #(show-demo-form params :problems %)
    (let [values (fp/parse-params demo-form params)]
      (layout
        [:h1 \"Thank you!\"]
        [:pre (prn-str values)]))))"]])))

(defn submit-demo-form [params]
  (fp/with-fallback #(show-demo-form params :problems %)
    (let [values (fp/parse-params demo-form params)]
      (layout
        [:h1 "Thank you!"]
        [:pre.prettyprint.lang-clj (with-out-str (pprint values))]
        [:p [:a {:href "/"} "Back to the form"]]))))

(defn show-cljs-demo [params]
  (layout
    (demo-header :cljs)
    [:div#cljs-container.pull-left {:style "width: 50%"}]
    [:div.pull-right {:style "width: 47%"}
     [:pre.prettyprint.lang-clj {:style "word-wrap: normal; white-space: pre; overflow: hidden; font-size: 12px; border: none; background: #f8f8f8; padding: 10px"}
        "(def demo-form
  {:fields [{:name :h1 :type :heading :text \"Section 1\"}
            {:name :full-name}
            {:name \"user[email]\" :type :email}
            {:name :spam :type :checkbox :label \"Yes, please spam me.\"}
            {:name :password :type :password}
            {:name :password-confirm :type :password}
            {:name :h2 :type :heading :text \"Section 2\"}
            {:name :note :type :html
             :html [:div.alert.alert-info \"Please make note of this note.\"]}
            {:name :date :type :date-select}
            {:name :time :type :time-select}
            {:name :flavors :type :checkboxes
             :options [\"Chocolate\" \"Vanilla\" \"Strawberry\" \"Mint\"]}
            {:name :h3 :type :heading :text \"Section 3\"}
            {:name :state :type :us-state
             :placeholder \"Select a state\"}
            {:name :explanation :type :textarea :label \"Explain yourself\"}]
   :validations [[:required [:full-name \"user[email]\" :password :state]]
                 [:min-length 4 :password]
                 [:equal [:password :password-confirm]]
                 [:min-length 2 :flavors \"Please select two or more flavors\"]]})

(defn render-demo-form []
  (let [now (js/Date.)
        defaults {:spam true
                  :date now
                  :time now}]
    (f/render-form (assoc demo-form :values defaults))))

(defn main []
  (when-let [container (sel1 \"#cljs-container\")]
    (d/append! container (node (render-demo-form)))
    (fd/handle-submit
      demo-form container
      (fn [params]
        (js/alert (pr-str params))))))"]]))

(defroutes routes
  (GET "/" [& params] (show-demo-form params))
  (POST "/" [& params] (submit-demo-form params))
  (GET "/cljs" [& params] (show-cljs-demo params))
  (route/resources "/" {:root "public"})
  (ANY "*" [] "Not found!"))

(def app
  (-> #'routes trace/wrap-stacktrace site wrap-gzip))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty #'app
                     {:port port :join? false})))

;; For interactive development:
;; (.stop server)
;; (def server (-main))
