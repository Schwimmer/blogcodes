#!/usr/bin/env python2
# -*- coding: utf-8 -*-
"""
Created on Fri Mar 23 13:52:54 2018

@author: david
"""

import pandas as pd
import matplotlib.pyplot as plt

def cla(n,lim):
    return'[%.f,%.f)'%(lim*(n//lim),lim*(n//lim)+lim) # map function

df = pd.read_csv('/home/david/iaudience-plan-statistics.csv', sep=',')
df['precent'] = df['precent'].astype('float64')
grouped = df['precent'].groupby(df['planid']).sum()

c = pd.DataFrame(grouped)
#print(c.head())
#aa = c[c['precent']>1 and c['precent']<20]
addone = pd.Series([cla(s*1000,1000) for s in c.precent ])
c['addone'] = addone
groups3 = c.groupby(['addone']).count()
groups3['precent'].plot('bar')
plt.show()

