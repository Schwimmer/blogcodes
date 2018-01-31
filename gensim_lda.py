#!/usr/bin/env python2
# -*- coding: utf-8 -*-
"""
Created on Wed Jan 31 15:08:32 2018

@author: david
"""

from gensim import corpora, models, similarities
import jieba

sentences = ["我喜欢吃土豆","土豆是个百搭的东西","我不喜欢今天雾霾的北京"]

words = []
for doc in sentences:
    words.append(list(jieba.cut(doc)))
#print words

dic = corpora.Dictionary(words)
#print dic
#print dic.token2id

#for word,index in dic.token2id.iteritems():
#    print word + ', index: ' + str(index)

corpus = [dic.doc2bow(text) for text in words]
#print corpus

tfidf = models.TfidfModel(corpus)
vec = [(0,1),(4,1)]
#print tfidf[vec]
corpus_tfidf = tfidf[corpus]
#for doc in corpus_tfidf:
#    print doc

index = similarities.SparseMatrixSimilarity(tfidf[corpus], num_features=14)
sim = index[tfidf[vec]]
#print list(enumerate(sim))

lsi = models.LsiModel(corpus_tfidf, id2word=dic, num_topics=2)
lsiout = lsi.print_topics(2)
#print lsiout[0]
#print lsiout[1]
    
corpus_lsi = lsi[corpus_tfidf]
#for doc in corpus_lsi:
#    print doc

lda = models.LdaModel(corpus_tfidf, id2word=dic, num_topics=2)
ldaout = lda.print_topics(2)
#print ldaout[0]
#print ldaout[1]
corpus_lda = lda[corpus_tfidf]
#for doc in corpus_lda:
#    print doc

query = "雾霾"
query_bow = dic.doc2bow(list(jieba.cut(query)))
#print query_bow
query_lsi = lsi[query_bow]
#print query_lsi

index = similarities.MatrixSimilarity(lsi[corpus])
sims = index[query_lsi]
print list(enumerate(sims))
sort_sims = sorted(enumerate(sims), key=lambda item: -item[1])
print sort_sims