(ns scrabble.assets
  (:import [org.apache.commons.codec.binary Base64]))


(defmacro slurp-b64 [path]
  (let [f (java.io.File. path)
        ary (byte-array (.length f))
        is (java.io.FileInputStream. f)]
    (.read is ary)
    (.close is)
    (String. (Base64/encodeBase64 ary))))

