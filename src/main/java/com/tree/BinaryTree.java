package com.tree;

/**
 * @author lh
 */
@SuppressWarnings("unused")
public class BinaryTree {

	//根节点数据
	private int data;
	//左子树
	private BinaryTree left;
	//右子树
	private BinaryTree right;

	//实例化二叉树类
	public BinaryTree(int data) {
		this.data = data;
		left = null;
		right = null;
	}


	/**
	 * 向二叉树中插入子节点
	 *
	 * @param root
	 * @param data
	 */
	public void insert(BinaryTree root, int data) {
		//二叉树的左节点都比根节点小
		if (data > root.data) {
			if (root.right == null) {
				root.right = new BinaryTree(data);
			} else {
				this.insert(root.right, data);
			}
		} else {
			//二叉树的右节点都比根节点大
			if (root.left == null) {
				root.left = new BinaryTree(data);
			} else {
				this.insert(root.left, data);
			}
		}
	}

}
