package com.collection;

/**
 * Created by lh on 2017/12/13.
 */
@SuppressWarnings("unused")
public class Solution {

	/**
	 * Given an array of integers, return indices of the two numbers such that they add up to a specific target.
	 * You may assume that each input would have exactly one solution, and you may not use the same element twice.
	 *
	 * @param arr    数组
	 * @param target 和
	 * @return
	 */
	@SuppressWarnings("unused")
	public int[] twoSum(int[] arr, int target) {
		for (int i = 0; i < arr.length; i++) {
			for (int j = i + 1; j < arr.length; j++) {
				if (arr[j] == target - arr[i]) {
					return new int[]{i, j};
				}
			}
		}
		return null;
	}

	private static int INT_MAX = 2147483647;

	public int reverse(int x) {
		int res = 0;
		// x=-932113122;
		while (x != 0) {
			if (Math.abs(res) > INT_MAX / 10) return 0;
			res = res * 10 + x % 10;
			x /= 10;
		}
		return res;
	}
}
