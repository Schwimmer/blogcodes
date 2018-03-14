#!/usr/bin/env python2
# -*- coding: utf-8 -*-
"""
Created on Thu Feb 22 16:13:07 2018

@author: david
"""

from sklearn.datasets import load_iris

iris = load_iris()
#
#from sklearn.preprocessing import StandardScaler
# 
##标准化，返回值为标准化后的数据
#sdfs = StandardScaler().fit_transform(iris.data)
#
#from sklearn.preprocessing import MinMaxScaler
#
#iris2 = iris.data[0,:]
#
##区间缩放，返回值为缩放到[0, 1]区间的数据
#a = MinMaxScaler().fit_transform(iris2)
#sdfs = StandardScaler().fit_transform(iris2)


"""
kafang
"""
from sklearn.feature_selection import SelectKBest
from sklearn.feature_selection import chi2

#选择K个最好的特征，返回选择特征后的数据
SelectKBest(chi2, k=2).fit_transform(iris.data, iris.target)
