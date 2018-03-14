#!/usr/bin/env python2
# -*- coding: utf-8 -*-
"""
Created on Sat Mar 10 15:08:11 2018

@author: david
"""

import numpy as np

data = np.loadtxt('sample.csv', delimiter=',', skiprows=1)
y_train = data[:,0]
x_train = data[:,1:]
feature_num = x_train.shape[1]
doc_num = x_train.shape[0]

# 
pos_rows = (y_train == 1)
neg_rows = (y_train == 0)
pos_x_train = x_train[pos_rows,:]

#c下每个特征的总和
fea_sum = np.sum(pos_x_train, axis=0) + 1
 
#c下特征为1的文件总和
fe_sum = np.count_nonzero(pos_x_train,axis=0) + 1  
                         
#c下文件总和
pos_num_doc = pos_x_train.shape[0] + 2

#c下词汇总和
pos_word_doc = np.sum(pos_x_train) + feature_num


pos_label_p = sum(y_train) / y_train.shape[0]
neg_label_p = 1-pos_label_p

#for i in range(doc_num):
#    if y_train[i] == 1:
#        