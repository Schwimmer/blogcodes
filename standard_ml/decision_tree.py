#!/usr/bin/env python2
# -*- coding: utf-8 -*-
"""
Created on Sun Mar 11 14:20:29 2018

@author: david
"""

from math import log

def calEnt(dataSet):
    numEntries = len(dataSet)
    labelCounts = {}
    
    for feaVec in dataSet:
        currentLabel = dataSet[-1]
        if currentLabel not in labelCounts.keys():
            labelCounts[currentLabel] = 0
        labelCounts[currentLabel] += 1
    ent = 0.0
    for key in labelCounts:
        prob = float(labelCounts[key]) / numEntries
        ent -= prob * log(prob, 2)
    return ent