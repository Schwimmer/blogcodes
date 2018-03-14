#!/usr/bin/env python2
# -*- coding: utf-8 -*-
"""
Created on Sun Mar 11 10:27:17 2018

@author: david
"""

def quick_sort(array, l, r):  
    if l < r:  
        q = partition(array, l, r)  
        quick_sort(array, l, q - 1)  
        quick_sort(array, q + 1, r)  
  
# 把小的往前挪，最后i+1跟标志位交换位置
def partition(array, l, r):  
    x = array[r]  
    i = l - 1  
    for j in range(l, r):  
        if array[j] <= x:  
            i += 1  
            array[i], array[j] = array[j], array[i]  
    array[i + 1], array[r] = array[r], array[i+1]  
    return i + 1  


arr = [1,5,2,1,6,32,1,7,3]
quick_sort(arr,0,8)
