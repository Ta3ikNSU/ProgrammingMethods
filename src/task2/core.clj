(ns task2.core)
; ----------------------------------------------------------------------------
(defn trapezoid [f a b]
  (* (* (+ (f a) (f b)) (- b a)) 0.5))

; 3x^6+38x^5+27x^4-14x^3-19x^2+39x-5
(defn polynomial [x]
  (+ (* 3 (Math/pow x 6))
     (* 38 (Math/pow x 5))
     (* 27 (Math/pow x 4))
     (* -14 (Math/pow x 3))
     (* -19 (Math/pow x 2))
     (* 39 x)
     -5))

; #2.1
; a,b - границы левая и правая соответственно
; h - длина шага
(defn integral
  ([f a b h]
   (if (< a b)
     (+
       (trapezoid f (- b h) b)
       (integral f a (- b h) h))
     0))

  ([f b h]
   (integral f 0 b h))
  )
(def m-integral
  (memoize
    (fn [f a b h]
      (if (< a b)
        (+
          (trapezoid f (- b h) b)
          (m-integral f a (- b h) h))
        0)))
  )

; Решение для шага с плавающей точкой
(def m-integral-array-internal
  (memoize
    (fn [f h index]
      (if (< 0 index)
        (+
          (trapezoid f (* (dec index) h) (* index h))
          (m-integral-array-internal f h (- index 1)))
        0)))
  )

(defn m-integral-array-external [f b h]
  (m-integral-array-internal f h (/ b h))
  )

; #2.2 Обещаем вычислить потом, когда у нас попросят данные

; Решение которое обсуждали на сдаче 2.1 с работой на числах с плавающей точкой на массиве значений
(defn lazy-integral-iterate [f h]
  (let [seq
        (map first
             (iterate
               (fn [[step_sum index]]
                 [
                  (+ step_sum (trapezoid f (* (dec index) h) (* index h)))
                  (inc index)
                  ]
                 )
               [0 1]))]
    (fn [b]
      (let [i (int (/ b h))] (+
                               (nth seq i)
                               (trapezoid f (* h i) b)))
      )
    )
  )
(defn -main [& args]
  (time (m-integral polynomial 0 50 0.5))
  (time (m-integral polynomial 0 50 0.5))
  (time (m-integral polynomial 0 60 0.5))
  (time (m-integral-array-external polynomial 50 0.5))
  (time (m-integral-array-external polynomial 50 0.5))
  (time (m-integral-array-external polynomial 60 0.5))
  (time ((lazy-integral-iterate polynomial 0.5) 50))
  (time ((lazy-integral-iterate polynomial 0.5) 50))
  (time ((lazy-integral-iterate polynomial 0.5) 60))
  )
