package com.tree;


/**
 * @author lh
 */
@SuppressWarnings("unused")
public class BinaryTree {

	//根节点数据
	public int data;
	//左子树
	public BinaryTree left;
	//右子树
	public BinaryTree right;

	//实例化二叉树类
	public BinaryTree(int data) {
		this.data = data;
	}

	public static void preSort(BinaryTree root) {
		if (root != null) {
			System.out.println(root.data);
			preSort(root.left);
			preSort(root.right);
		}
	}

	private static Integer getTreeDepth(BinaryTree root) {
		if (root.left == null && root.right == null) {
			return 1;
		}
		Integer left = 0, right = 0;
		if (root.left != null) {
			left = getTreeDepth(root.left);
		}
		if (root.right != null) {
			right = getTreeDepth(root.right);
		}
		return left > right ? ++left : ++right;
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
