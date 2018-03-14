#!/usr/bin/env python2
# -*- coding: utf-8 -*-
"""
Created on Sat Mar 10 14:57:30 2018

@author: david
"""
import numpy as np

class NaiveBayes(object):
    plambda = 1.0
    
    def __init__(self):
        pass
    
    
    def trainNB(self, train_path, model_type='Multinomial'):
        train_data = np.loadtxt(train_path, delimiter=',', skiprows=1)
        y_train = train_data[:,0]
        x_train = train_data[:,1:]
        feature_num = x_train.shape[1]
        doc_num = x_train.shape[0]
        
        if model_type == 'Multinomial':
            