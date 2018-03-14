#!/usr/bin/env python2
# -*- coding: utf-8 -*-
"""
Created on Sun Mar 11 08:46:16 2018

@author: david
"""

def select_coin(coin_value, total_value):
    min_coin_num = [0]
    for i in range(1, total_value +1):
        min_coin_num.append(float('inf'))
        for value in coin_value:
            if value <= i and min_coin_num[i-value] + 1 < min_coin_num[i]:
                min_coin_num[i] = min_coin_num[i-value] + 1
    return min_coin_num

result = select_coin([1,3,5], 11)
