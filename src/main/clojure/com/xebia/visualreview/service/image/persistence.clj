;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Copyright 2015 Xebia B.V.
;
; Licensed under the Apache License, Version 2.0 (the "License")
; you may not use this file except in compliance with the License.
; You may obtain a copy of the License at
;
;  http://www.apache.org/licenses/LICENSE-2.0
;
; Unless required by applicable law or agreed to in writing, software
; distributed under the License is distributed on an "AS IS" BASIS,
; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
; See the License for the specific language governing permissions and
; limitations under the License.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns com.xebia.visualreview.service.image.persistence
  (:require [com.xebia.visualreview.service.persistence.util :as putil]
            [slingshot.slingshot :as ex]))


(defn insert-image!
  "Adds a new image to the database. Returns the new image's ID."
  [conn directory]
  (putil/insert-single! conn :image { :directory directory }))

(defn get-image-path
  "Gets the path of an image with the given image ID.
  The path will contain the directory structure and file name of the image
  relative to the screenshot directory. Example: '2015/1/15/22/1.png'."
  [conn image-id]
  (let [image (putil/query-single conn
                                  ["SELECT id, directory FROM image WHERE id = ?" image-id])
        directory (:directory image)
        id (:id image)]
    (if (nil? image)
      nil
      (str directory "/" id ".png"))
    ))

(defn get-unused-image-ids [conn]
  "Returns a vector of image id's that are not referenced in any diff or screenshot"
  (putil/query conn ["SELECT id FROM image WHERE id NOT IN (SELECT image_id FROM screenshot) AND id NOT IN (SELECT image_id FROM diff)"]
               :row-fn :id
               :result-set-fn vec))

(defn delete-image!
  "Removes the image with the given image-id from the database"
  [conn image-id]
  (putil/delete! conn :image ["id = ? " image-id]))