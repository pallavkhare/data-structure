package com.practice.ds_project;

import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * @author ranjit soni
 * BinarySearchTree is data structure used to stored element in the form of tree, where 
 * all element smaller then root will be stored in left sub tree and 
 * all element greater then root will be stored in right sub tree.
 * operation : insert element, remove element and contains elements
 */

public class BinarySearchTree<T extends Comparable<T>> {

	// size variable represent the size of binary tree, default size is 0
	private int size = 0;
	
	// root node variable represent root of binary tree
	private Node root;
	
	// map is used to keep track of parent of each node in binary tree
	private Map<Node, Node> map = null;
	
	// binaryTreeElementlist is used to keep element of binary tree after traversal
	private List<T> binaryTreeElementlist = null;
	
	public BinarySearchTree() {
		super();
		map = new HashMap<Node, Node>();
		binaryTreeElementlist = new ArrayList<T>();
	}
	
	public int getSize() {
		return this.size;
	}

	public Node getRoot() {
		return root;
	}

	public boolean isEmpty() {
		return this.size == 0;
	}
	
	/*
	 * This method called by public method and used to check binary tree is empty or not
	 * throw exception in case of empty binary tree
	 */
	private void checkEmptyOrNot() {
		if(isEmpty()) {
			throw new NoSuchElementException();
		}
	}

	/*
	 * This method is used to insert element in binary tree
	 */
	public void insert(T data) {
		if(root == null) {
			root = new Node(data);
			map.put(root, null);
		}
		else {
			addNodeInTree(root, data);
		}
		++this.size;
	}

	public boolean contains(T data) {
		if(this.size == 0) {
			throw new NoSuchElementException();
		}
		return !Objects.isNull(findObjectInTree(root, data));
	}
	
	/**
	 * @param obj
	 */
	public void remove(T obj) {
		checkEmptyOrNot();
		Node targetNode = findObjectInTree(root, obj);
		if(targetNode == null) {
			throw new NoSuchElementException();
		}

		Node successorNode = null;
		/*
		 * case 1
		 * No left and No right tree i.e. tree might have one node or element to be removed exist is leaf node that's why previous and nextPointer is null
		 */
		if(targetNode.prevPointer == null && targetNode.nextPointer == null) { 
			Node parentNodeOfSuccesssor = map.get(targetNode);
			if(parentNodeOfSuccesssor == null) {
				// Single element binary tree
				targetNode.data = null;
				targetNode = null;
			}
			else {
				// Element to be removed is leaf node
				successorNode = targetNode;
			}
		}
		else if(targetNode.prevPointer != null && targetNode.nextPointer == null) { 
			/*
			 * case 2
			 * left tree exist but no right tree i.e. tree has only previous pointer but nextPointer is null
			 * scan scanRightSideTree of prevPointer and find right most element and replace it with currentNode 
			 */
			successorNode = scanRightSideTree(targetNode.prevPointer);
		}
		else if(targetNode.prevPointer == null && targetNode.nextPointer != null) {
			/* 
			 * case 3
			 * No left tree but right tree exist i.e. tree has only nextPointer but previousPointer is null
			 * scan scanLeftSideTree of nextPointer and find left most element and replace it with currentNode 
			 */
			successorNode = scanLeftSideTree(targetNode.nextPointer);
		}
		else { 
			/*
			 * case 4
			 * Both left tree and right tree exist i.e. both nextPointer and previousPointer are not null
			 * scan and find left or right most element and replace it with currentNode, as of find right most node of left tree
			 */
			successorNode = scanLeftSideTree(targetNode.prevPointer);
		}
		
		try {
			replaceTargetNodeWithSuccessorNode(targetNode, successorNode);
		} catch (UnexpectedException e) {
			e.printStackTrace();
		}
	}
	
	private Node scanLeftSideTree(Node node) {
		if(node.nextPointer == null) {
			return node;
		}
		else {
			return scanLeftSideTree(node.nextPointer);
		}
	}
	
	private Node scanRightSideTree(Node node) {
		if(node.prevPointer == null) {
			return node;
		}
		else {
			return scanRightSideTree(node.prevPointer);
		}
	}
	
	/*
	 * This method use to swap element to be removed with its successor node in tree.
	 */
	private void replaceTargetNodeWithSuccessorNode(Node targetNode, Node successorNode) throws UnexpectedException {
		if(targetNode != null && successorNode != null) {
			Node parentNodeOfSuccesssor = map.get(successorNode);
			if(successorNode.prevPointer == null && successorNode.nextPointer == null) { // successorNode is leaf node
				targetNode.data = successorNode.data;
				successorNode.data = null;
				if(parentNodeOfSuccesssor.prevPointer.data == null) {
					map.remove(parentNodeOfSuccesssor.prevPointer);
					parentNodeOfSuccesssor.prevPointer = null;
				}
				else {
					map.remove(parentNodeOfSuccesssor.nextPointer);
					parentNodeOfSuccesssor.nextPointer = null;
				}
			}
			else if(successorNode.prevPointer != null && successorNode.nextPointer == null) { // successorNode has left tree
				parentNodeOfSuccesssor.nextPointer = successorNode.prevPointer;
				map.put(successorNode.prevPointer, parentNodeOfSuccesssor);
			}
			else if(successorNode.prevPointer == null && successorNode.nextPointer != null) { // successorNode has right tree
				parentNodeOfSuccesssor.prevPointer = successorNode.nextPointer;
				map.put(successorNode.nextPointer, parentNodeOfSuccesssor);
			}
			else {
				throw new UnexpectedException("Unexpected case");
			}
		}
		--this.size;
	}
	
	/*
	 * This function used to find required object in binary tree
	 */
	private Node findObjectInTree(Node currentNode, T data) {
		if(data.compareTo(currentNode.data) == 0){
			return currentNode;
		}
		else if(data.compareTo(currentNode.data) < 0){
			//left tree
			if(currentNode.prevPointer == null){
				throw new NoSuchElementException();
			}
			else {
				return findObjectInTree(currentNode.prevPointer, data);
			}
		}
		else if(data.compareTo(currentNode.data) > 0){
			//right tree
			if(currentNode.nextPointer == null){
				throw new NoSuchElementException();
			}
			else {
				return findObjectInTree(currentNode.nextPointer, data);
			}
		}
		else {
			throw new NoSuchElementException();
		}
	}

	/*
	 * This method is called internally by insert function to add node in tree.
	 * Recursion is being used to find the correct position to add node in binary tree
	 */
	private void addNodeInTree(Node currentNode, T data){
		if(data.compareTo(currentNode.data) <= 0) {
			//left tree
			if(currentNode.prevPointer == null) {
				currentNode.prevPointer = new Node(data);
				map.put(currentNode.prevPointer, currentNode);
			}
			else {
				addNodeInTree(currentNode.prevPointer, data);
			}
		}
		else{
			//right tree
			if(currentNode.nextPointer == null) {
				currentNode.nextPointer = new Node(data);
				map.put(currentNode.nextPointer, currentNode);
			}
			else {
				addNodeInTree(currentNode.nextPointer, data);
			}
		}
	}
	
	/*
	 * This method is called to traverse tree in preOrder
	 * return elements in order - Root Left Right
	 */
	public List<T> preOrderTraversal(){
		this.binaryTreeElementlist.clear();
		performPreOrderTraverse(this.root);
		return this.binaryTreeElementlist;
	}
	
	/*
	 * This method is called to traverse tree in inOrder
	 * return elements in order - Left Root Right
	 */
	public List<T> inOrderTraversal(){
		this.binaryTreeElementlist.clear();
		performInOrderTraverse(this.root);
		return this.binaryTreeElementlist;
	}
	
	/*
	 * This method is called to traverse tree in postOrder
	 * return elements in order - Left Right Root
	 */
	public List<T> postOrderTraversal(){
		this.binaryTreeElementlist.clear();
		performPostOrderTraverse(this.root);
		return this.binaryTreeElementlist;
	}

	/*
	 * This method is used to traverse the binary tree in PreOrder
	 * i.e. Root -> Left Tree -> Right Tree
	 */
	private void performPreOrderTraverse(Node currentNode) {
		if(currentNode != null) {
			this.binaryTreeElementlist.add(currentNode.data);
			performPreOrderTraverse(currentNode.prevPointer);
			performPreOrderTraverse(currentNode.nextPointer);
		}
	}

	/*
	 * This method is used to traverse the binary tree in InOrder
	 * i.e. Left Tree -> Root -> Right Tree
	 */
	private void performInOrderTraverse(Node currentNode) {
		if(currentNode != null) {
			performInOrderTraverse(currentNode.prevPointer);
			this.binaryTreeElementlist.add(currentNode.data);
			performInOrderTraverse(currentNode.nextPointer);
		}
	}

	/*
	 * This method is used to traverse the binary tree in PostOrder
	 * i.e. Left Tree -> Right Tree -> Root
	 */
	private void performPostOrderTraverse(Node currentNode) {
		if(currentNode != null) {
			performPostOrderTraverse(currentNode.prevPointer);
			performPostOrderTraverse(currentNode.nextPointer);
			this.binaryTreeElementlist.add(currentNode.data);
		}
	}

	/**
	 * Node is inner class of BinarySearchTree
	 * Node class contains following variable
	 * variable : data, type : T, represent the element to be stored in binary tree
	 * variable : prevPointer, type : Node, represent the pointer to next node in left tree of binary tree
	 * variable : nextPointer, type : Node, represent the pointer to next node in right tree of binary tree
	 */
	class Node {
		T data;
		private Node prevPointer;
		private Node nextPointer;

		public Node(T data) {
			super();
			this.data = data;
		}

		public T getData() {
			return data;
		}

		public void setData(T data) {
			this.data = data;
		}

		public Node getPrevPointer() {
			return prevPointer;
		}

		public void setPrevPointer(Node prevPointer) {
			this.prevPointer = prevPointer;
		}

		public Node getNextPointer() {
			return nextPointer;
		}

		public void setNextPointer(Node nextPointer) {
			this.nextPointer = nextPointer;
		}
	}
}
