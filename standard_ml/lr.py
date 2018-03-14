#!/usr/bin/env python2
# -*- coding: utf-8 -*-
"""
Created on Fri Mar  9 15:05:47 2018

@author: david
"""

import numpy as np

def sigmoid(x):
    s = 1/(1 + np.exp(-x))

def initialize_with_zeros(dim):
    w = np.zeros((dim, 1))
    
    return w

def forward_propagation(w, X, Y):
    m = X.shape[1]
    A = sigmoid(np.dot(w.T, X) + b)
    cost =  - ( np.dot(Y, np.log(A.T)) + np.dot(np.log(1-A), (1-Y).T) ) / m
    dw = np.dot( X, (A-Y).T ) / m
    db = np.sum(A-Y) / m
    cost = np.squeeze(cost)
    return dw, db, cost

def optimize(w, b, X, Y, num_iterations, learning_rate):
    costs = []

    for i in range(num_iterations):
        grads, cost = forward_propagation(w, b, X, Y)
        dw = grads["dw"]
        db = grads["db"]
        w = w - learning_rate * dw
        b = b - learning_rate * db

        if i % 100 == 0:
            costs.append(cost)
            print(cost)

    params = {
        "w":w,
        "b":b
    }

    return params