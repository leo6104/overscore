;;; Compute the Hausdorff distance between two images
(ns overscore.recognition.classification.hausdorff
  (:use overscore.recognition.segmentation.segment
        overscore.recognition.classification.training)
  (:import java.awt.image.BufferedImage))

(defrecord point [x y])

(defn point-distance
  "Compute the (squared) distance between two points"
  [^point a ^point b]
  (let [dx (- (:x a) (:x b))
        dy (- (:y a) (:y b))]
    (+ (* dx dx) (* dy dy))))

(defn black-points
  "Return the list of black points in an image (described by its
  width, height and pixel vector"
  [^long w ^long h ^clojure.lang.PersistentVector v]
  (loop [x 0
         y 0
         ^clojure.lang.PersistentVector v v
         ^clojure.lang.PersistentVector$TransientVector res (transient [])]
    (if (and (not (empty? v)) (< y h))
      (if (< x w)
        (recur (inc x) (inc y) (rest v)
               (if (== (first v) 1)
                 (conj! res (->point x y))
                 res))
        (recur 0 (inc y) v res))
      (persistent! res))))

(defn directed-hausdorff-distance
  "Compute the directed Hausdorff distance between two images, given
  their size (width, height) and a function that returns a pixel value
  given the coordinates in the image"
  [aw ah av bw bh bv]
  (let [b-points (black-points bw bh bv)
        a-points (black-points aw ah av)
        distances
        (map #(let [distances-with
                    (map point-distance
                         (repeat %)
                         b-points)]
                (if (empty? distances-with)
                  0
                  (reduce min distances-with)))
             a-points)]
    (if (empty? distances)
      100000 ; big value
      (reduce max distances))))

(defn hausdorff-distance
  "Compute the undirected Hausdorff distance between two image. The
  template image is an element from the training set, and the other
  image is represented by a (L2) segment of an image"
  [^BufferedImage image segment template]
  (let [tw (:width template)
        th (:height template)
        tv (:data template)
        sw (segment-width segment)
        sh (segment-height segment)
        sv (to-vector image segment)]
    (max
     (directed-hausdorff-distance tw th tv sw sh sv)
     (directed-hausdorff-distance sw sh sv tw th tv))))
