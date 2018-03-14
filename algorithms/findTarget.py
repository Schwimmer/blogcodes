#!/usr/bin/env python2
# -*- coding: utf-8 -*-
"""
Created on Tue Mar 13 16:26:32 2018

@author: david
"""

def findMax(array):
    start = 0
    end = len(array) -1
    middle = 0
    while True:
        middle = (start + end) /2
        if array[middle] > array[start]:
            start = middle
        elif array[middle] <= array[end]:
            end = middle
        else:
            return start
        
def binarySearch(array, l, r, target):
    while l <= r:
        mid = (l + r) /2
        if target == array[mid]:
            return mid
        elif target < array[mid]:
            r = mid - 1
        elif target > array[mid]:
            l = mid + 1
    return -1

def partialSortSearch(array, target):
    maxPos = findMax(array)
    if target == array[maxPos]:
        return maxPos
    elif target >= array[0]:
        return binarySearch(array, 0, maxPos - 1, target)
    else:
        return binarySearch(array, maxPos+1, len(array)-1, target)

            
        
#arr = [2,2,2,2,1,1,1,1,1]
#print arr[findMax(arr)]
arr = [5,6,7,8,1,2,3,4]
print partialSortSearch(arr, 4)