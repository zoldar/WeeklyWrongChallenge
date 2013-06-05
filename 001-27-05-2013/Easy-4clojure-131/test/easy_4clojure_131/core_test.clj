(ns easy-4clojure-131.core-test
  (:require [clojure.test :refer :all]
            [easy-4clojure-131.core :refer :all])
  (:use [easy-4clojure-131.submissions.locks :only [solution]]))

(deftest test-1
  (testing "First test"
    (is (= false (solution #{1}
                           #{2}
                           #{3}
                           #{4})))))

(deftest test-2
  (testing "Second test"
    (is (= true (solution #{1})))))

(deftest test-3
  (testing "Third test"
    (is (= false (solution #{1 -3 51 9}
                   #{0}
                   #{9 2 81 33})))))

(deftest test-4
  (testing "Fourth test"
    (is (= true (solution #{-1 1 99}
                          #{-2 2 88}
                          #{-3 3 7777})))))

(deftest test-5
  (testing "Fifth test"
    (is (= true (solution #{1 3 5}
                          #{9 11 4}
                          #{-3 12 3}
                          #{-3 4 -2 10})))))

(deftest test-6
  (testing "Sixth test"
    (is (= false (solution #{-1 -2 -3 -4 -5 -6}
                           #{1 2 3 4 5 6 7 8 9})))))

(deftest test-7
  (testing "Seventh test"
    (is (= true (solution #{1 3 5 7}
                          #{2 4 6 8})))))

(deftest test-8
  (testing "Eighth test"
    (is (= true (solution #{-1 3 -5 7 -9 11 -13 15}
                          #{1 -3 5 -7 9 -11 13 -15}
                          #{1 -1 2 -2 4 -4 8 -8})))))








