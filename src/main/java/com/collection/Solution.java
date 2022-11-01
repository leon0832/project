package com.collection;

import java.util.Arrays;

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
    public static int[] twoSum(int[] arr, int target) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j] == target - arr[i]) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }


    /**
     * 回文数
     *
     * @param x
     * @return
     */
    public static int reverse(int x) {
        int res = 0;
        // x=-932113122;
        while (x != 0) {
            if (Math.abs(res) > Integer.MAX_VALUE / 10) return 0;
            res = res * 10 + x % 10;
            x /= 10;
        }
        return res;
    }

    public static void main(String[] args) {
        System.out.println(reverse(-932113122));

        int[] arr = {1, 2, 4, 6, 9, 1, 24, 2};
        System.out.println(Arrays.toString(twoSum(arr, 15)));

        System.out.println(Arrays.toString(twoSumTemp(arr, 15)));
    }

    public static int[] twoSumTemp(int[] arr, int target) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j] == target - arr[i]) {
                    return new int[]{j, i};
                }
            }
        }
        return null;
    }
}
