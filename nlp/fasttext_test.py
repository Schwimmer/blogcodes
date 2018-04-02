#!/usr/bin/env python2
# -*- coding: utf-8 -*-
"""
Created on Mon Dec 25 14:17:14 2017

@author: david
"""

import fasttext

classifier = fasttext.supervised("news_fasttext_train.txt","model",label_prefix="__label__")

#测试模型
result = classifier.test("news_fasttext_test.txt")
print result.precision
print result.recall