package com.tree;

/**
 * Created by lh on 2017/12/10.
 */
public class BinaryTreeOrder {

	/**
	 * 先根遍历
	 *
	 * @param root
	 */
	private static void preOrder(BinaryTree root) {
		if (root != null) {
			System.out.print(root.data + "--->");
			preOrder(root.left);
			preOrder(root.right);
		}
	}

	/**
	 * 中根遍历
	 *
	 * @param root
	 */
	private static void inOrder(BinaryTree root) {

		if (root != null) {
			inOrder(root.left);
			System.out.print(root.data + "--->");
			inOrder(root.right);
		}
	}

	/**
	 * 后根遍历
	 *
	 * @param root
	 */
	private static void postOrder(BinaryTree root) {

		if (root != null) {
			postOrder(root.left);
			postOrder(root.right);
			System.out.print(root.data + "--->");
		}
	}

	private static int getTreeDepth(BinaryTree root) {

		if (root.left == null && root.right == null) {
			return 1;
		}

		int left = 0, right = 0;
		if (root.left != null) {
			left = getTreeDepth(root.left);
		}
		if (root.right != null) {
			right = getTreeDepth(root.right);
		}
		return left > right ? ++left : ++right;
	}

	public static void main(String[] args) {
		int[] array = {12, 76, 35, 22, 16, 48, 90, 46, 9, 40};
		BinaryTree root = new BinaryTree(array[0]);   //创建二叉树
		for (int i = 1; i < array.length; i++) {
			root.insert(root, array[i]);       //向二叉树中插入数据
		}
		System.out.println("先根遍历：");
		preOrder(root);
		System.out.println();
		System.out.println("中根遍历：");
		inOrder(root);
		System.out.println();
		System.out.println("后根遍历：");
		postOrder(root);
		System.out.println("深度："+getTreeDepth(root));
	}
}
