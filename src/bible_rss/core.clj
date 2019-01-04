(ns bible-rss.core
  (:gen-class)
  (:require [clojure.data.csv :as csv]
            [clojure.xml :refer [emit]]
            [clj-rss.core :as rss]))

(defn csv-data->maps [csv-data]
  (map zipmap
       (->> (first csv-data) ;; First row is the header
            (map keyword) ;; Drop if you want string keys instead
            repeat)
       (rest csv-data)))

(defn format-num [n] (str (apply str (repeat (- 3 (count (str n))) "0")) n))

(defn transform-to-channel
  [[channel-title books]]
  (let [items (reduce (fn [prev {:keys [BookTitle Chapters BookNumber S3Title BookDescription]}]
                        (into prev (map (fn [ch]
                                          {:title (str BookTitle ", Ch. " ch)
                                           :description BookDescription
                                           ;; https://s3.amazonaws.com/d4hines-bible-rss/WOP-12-2Kings-003.mp3
                                           :link (str
                                                  "https://s3.amazonaws.com/d4hines-bible-rss/WOP-"
                                                  BookNumber "-" S3Title "-" (format-num ch) ".mp3")
                                           :category ["Bible Audio"]})
                                        (range 1 (inc Chapters))))) [] books)]
    {:title (clojure.string/replace channel-title #"\s" "-")
     :content
     (apply rss/channel-xml {:title channel-title
                             :link "https://www.hines.house/bible-rss"
                             :description channel-title}
            ;; Turns out you have to reverse the order for (my)
            ;; Podcast player to get play them in the correct order.
            items)}))

;; Keys: '(:Channel :BookNumber :S3Title :BookTitle :Chapters)
(def feeds (->> "./BibleRSSData.csv"
                slurp
                csv/read-csv
                csv-data->maps
              ;; Parse the string chapters as Ints.
                (map #(assoc % :Chapters (Integer. (:Chapters %))))
                (group-by :Channel)
                (map transform-to-channel)))

(comment (doseq [{:keys [title content]} feeds]
           (spit (str "./" title ".xml") content)))

(defn -main
  ""
  [& args])
