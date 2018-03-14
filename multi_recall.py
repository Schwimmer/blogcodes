#!/usr/bin/env python2
# -*- coding: utf-8 -*-
"""
Created on Thu Mar  8 10:06:23 2018

@author: david
"""

M = [
    [14371, 6500, 9, 0, 0, 2, 316],
    [5700, 22205, 454, 20, 0, 11, 23],
    [0, 445, 3115, 71, 0, 11, 0],
    [0, 0, 160, 112, 0, 0, 0],
    [0, 888, 39, 2, 0, 0, 0],
    [0, 486, 1196, 30, 0, 74, 0],
    [1139, 35, 0, 0, 0, 0, 865]
]

n = len(M)
for i in range(n):
    rowsum, colsum = sum(M[i]), sum(M[r][i] for r in range(n))
    try:
        print 'precision: %s' % (M[i][i]/float(colsum)), 'recall: %s' % (M[i][i]/float(rowsum))
    except ZeroDivisionError:
        print 'precision: %s' % 0, 'recall: %s' %0