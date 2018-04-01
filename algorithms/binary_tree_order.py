#!/usr/bin/env python2
# -*- coding: utf-8 -*-
"""
Created on Thu Mar 22 21:41:21 2018

@author: david
"""

class TreeNode:
    def __init__(self, data):
        self.data = data
        self.left_child = None
        self.right_child = None
    
class BinarySearchTree:
    def insert(self, root, data):
        if not root:
            root = TreeNode(data)
        else:
            if data <= root.data:
                root.left_child = self.insert(root.left_child, data)
            if data > root.data:
                root.right_child = self.insert(root.right_child, data)
            
        return root
    
    def preOrderRecur(self, root):
        if root:
            print(root.data)
            self.preOrderRecur(root.left_child)
            self.preOrderRecur(root.right_child)
            
    def preOrder(self, root):
        stackNode = []
        if not root:
            return
        stackNode.append(root)
        while(stackNode):
            node = stackNode.pop()
            print(node.data)
            if node.right_child:
                stackNode.append(node.right_child)
            if node.left_child:
                stackNode.append(node.left_child)
            
    def midOrder(self, root):
        stackNode = []
        if not root:
            return
        stackNode.append(root)
        while(stackNode):
            node = stackNode.pop()
            print(node.data)
            if node.right_child:
                stackNode.append(node.right_child)
            if node.left_child:
                stackNode.append(node.left_child)
                
    def midOrderRecur(self, root):
        if root:
            self.midOrderRecur(root.left_child)
            print(root.data)
            self.midOrderRecur(root.right_child)
            
    def postOrderRecur(self, root):
        if root:
            self.postOrderRecur(root.left_child)
            self.postOrderRecur(root.right_child)
            print(root.data)
       

def main(argv = None):
    bt = BinarySearchTree()
    root = None
    for i in range(1,4,1):
        root = bt.insert(root,i)
    bt.preOrder(root)
    
if __name__ == "__main__":
    main()
