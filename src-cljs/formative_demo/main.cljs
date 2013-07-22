(ns formative-demo.main
  (:require [formative.core :as f]
            [formative.dom :as fd]
            [dommy.core :as d])
  (:require-macros [dommy.macros :refer [sel1 node]]))

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
            {:name :explanation :type :textarea :label "Explain yourself"}]
   :validations [[:required [:full-name "user[email]" :password :state]]
                 [:min-length 4 :password]
                 [:equal [:password :password-confirm]]
                 [:min-length 2 :flavors "Please select two or more flavors"]]})

(defn render-demo-form []
  (let [now (js/Date.)
        defaults {:spam true
                  :date now
                  :time now}]
    (f/render-form (assoc demo-form :values defaults))))

(defn main []
  (when-let [container (sel1 "#cljs-container")]
    (d/append! container (node (render-demo-form)))
    (fd/handle-submit
      demo-form container
      (fn [params]
        (js/alert (pr-str params))))))

(main)