#!/usr/bin/env python2
# -*- coding: utf-8 -*-
"""
Created on Thu Mar 15 15:46:10 2018

@author: david
"""

class TreeNode:
    def __init__(self, val, left, right):
        self.val = val
        self.left = left
        self.right = right
        
class BinarySearchTree:
    def insert(self, root, val):
        if root == None:
            root = TreeNode(val, None, None)
        else:
            if val < root.val:
                root.left = self.insert(root.left, val)
            if val > root.val:
                root.right = self.insert(root.right, val)
        return root
    
    def preOrder(self, root):
        if root:
            print root.val
            self.preOrder(root.left)
            self.preOrder(root.right)
    
Tree = BinarySearchTree()
root = None
for i in [1,2,3]:
    root = Tree.insert(root, i)
Tree.preOrder(root)