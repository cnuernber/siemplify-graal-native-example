(ns siemplify.main
  (:require [clojure.data.json :as json]
            [tech.v3.datatype.ffi :as dt-ffi]
            [tech.v3.datatype.ffi.graalvm-runtime])
  (:gen-class))


(defn do-the-thing
  [json-data]
  (let [parsed (json/read-str json-data)]
    (println parsed)
    (json/write-str {:a 1 :b 2})))


(def return-buffer (atom nil))


(defn clear-return-buffer
  []
  (reset! return-buffer nil))


(defn do-the-thing-wrapper
  [ptr len]
  (let [str-data (dt-ffi/c->string ptr)
        retval (do-the-thing str-data)
        c-retval (dt-ffi/string->c retval)]
    (reset! return-buffer c-retval)
    c-retval))


(defn -main
  [& args]
  (println "hello from graal native"))


(comment
  (do
    (require '[tech.v3.datatype.ffi.graalvm :as graalvm])
    (with-bindings {#'*compile-path* "classes"}
      (graalvm/expose-clojure-functions
       ;;name conflict - initialize is too general
       {#'do-the-thing-wrapper {:rettype :pointer
                                :argtypes [['input :pointer]
                                           ['len :int64]]}
        }
       'siemplify/libexample nil))
    )
  )
