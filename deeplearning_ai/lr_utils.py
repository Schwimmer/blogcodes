#!/usr/bin/env python2
# -*- coding: utf-8 -*-
"""
Created on Mon Feb 12 10:57:06 2018

@author: david
"""

import numpy as np
import h5py
     
     
def load_dataset():
    train_dataset = h5py.File('datasets/train_catvnoncat.h5', "r")
    train_set_x_orig = np.array(train_dataset["train_set_x"][:]) # your train set features
    train_set_y_orig = np.array(train_dataset["train_set_y"][:]) # your train set labels
 
    test_dataset = h5py.File('datasets/test_catvnoncat.h5', "r")
    test_set_x_orig = np.array(test_dataset["test_set_x"][:]) # your test set features
    test_set_y_orig = np.array(test_dataset["test_set_y"][:]) # your test set labels
 
    classes = np.array(test_dataset["list_classes"][:]) # the list of classes
     
    train_set_y_orig = train_set_y_orig.reshape((1, train_set_y_orig.shape[0]))
    test_set_y_orig = test_set_y_orig.reshape((1, test_set_y_orig.shape[0]))
     
    return train_set_x_orig, train_set_y_orig, test_set_x_orig, test_set_y_orig, classes

def sigmoid(z):
    s = 1 / ( 1 + np.exp(-z))
    return s 

def initialize_with_zeros(dim):
    w = np.zeros((dim, 1))
    b = 0

    assert(w.shape == (dim ,1))
    assert(isinstance(b, float) or isinstance(b, int))
    return w,b

def forward_propagation(w, b, X, Y):
    m = X.shape[1]
    A = sigmoid(np.dot(w.T, X) + b)
    cost = - ( np.dot(Y, np.log(A.T)) + np.dot(np.log(1-A),(1-Y).T) )/m
    dw = np.dot(X, (A-Y).T) / m
    db = np.sum(A-Y) / m

    assert(dw.shape == w.shape)
    # assert(db.type == float)
    cost = np.squeeze(cost)
    assert(cost.shape == () )
    
    grads = {"dw": dw,
            "db": db}

    return grads, cost

def optimize(w, b, X, Y, num_iterations, learning_rate, print_cost = False):
    costs = []

    for i in range(num_iterations):
        grads, cost = forward_propagation(w, b, X, Y)
        dw = grads["dw"]
        db = grads["db"]
        w = w - learning_rate * dw
        b = b - learning_rate * db

        if i % 10 == 0:
            costs.append(cost)
            print(cost)

    params = {
        "w":w,
        "b":b
    }

    return params, costs

def predict(w, b, X):
    m = X.shape[1]
    Y_prediction = np.zeros((1,m))

    A = sigmoid(np.dot(w.T, X) + b)
    for i in range(A.shape[1]):
        if A[0][i] <= 0.5: 
            A[0][i] = 0
        else:
            A[0][i] = 1

    Y_prediction = A
    
    return Y_prediction

def model(X_train, Y_train, X_test, Y_test, num_iterations = 2000, learning_rate=0.5,print_cost=False):
    # initialize the parameters with zeros
    w, b = initialize_with_zeros(X_train.shape[0])

    # calculate gradient descent
    parameters,costs = optimize(w, b, X_train, Y_train, num_iterations,learning_rate,print_cost)

    # get w and b 
    w = parameters["w"]
    b = parameters["b"]

    # predict
    Y_prediction_test = predict(w, b, X_test)
    Y_prediction_train = predict(w, b, X_train)

    d = {"costs": costs,
     "Y_prediction_test": Y_prediction_test,
     "Y_prediction_train" : Y_prediction_train,
     "w" : w,
     "b" : b,
     "learning_rate" : learning_rate,
     "num_iterations": num_iterations}

     return d



d = model(train_set_x, train_set_y, test_set_x, test_set_y, num_iterations=2000,learning_rate=0.005,print_cost=True)











