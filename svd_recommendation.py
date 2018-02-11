#!/usr/bin/env python2
# -*- coding: utf-8 -*-
"""
Created on Tue Feb  6 16:10:01 2018

@author: david
"""

#!/usr/bin/python2.7
# coding: UTF-8
import numpy as np  
from scipy.sparse.linalg import svds  
from scipy import sparse  
import matplotlib.pyplot as plt

def vector_to_diagonal(vector):  
    """
    将向量放在对角矩阵的对角线上
    :param vector:
    :return:
    """
    if (isinstance(vector, np.ndarray) and vector.ndim == 1) or \
            isinstance(vector, list):
        length = len(vector)
        diag_matrix = np.zeros((length, length))
        np.fill_diagonal(diag_matrix, vector)
        return diag_matrix
    return None

RATE_MATRIX = np.array(  
    [[5, 5, 3, 0, 5, 5],
     [5, 0, 4, 0, 4, 4],
     [0, 3, 0, 5, 4, 5],
     [5, 4, 3, 3, 5, 5]]
).T

print RATE_MATRIX

RATE_MATRIX = RATE_MATRIX.astype('float')  
#U, S, VT = svds(sparse.csr_matrix(RATE_MATRIX),  k=2, maxiter=200) # 2个隐主题  
U, S, VT = np.linalg.svd(RATE_MATRIX)
print U
print S
print VT

S = vector_to_diagonal(S)

newuser = np.array([5,5,0,0,0,5])

S = np.matrix(S).I

print '用户的主题分布：'  
print U[:,:2]  
print '奇异值：'  
print S[:2,:2]
print '物品的主题分布：'  
print VT  
print '重建评分矩阵，并过滤掉已经评分的物品：'  
#print np.dot(np.dot(U, S), VT) * (RATE_MATRIX < 1e-6)  
print np.dot(np.dot(newuser, U[:,:2]),S[:2,:2])