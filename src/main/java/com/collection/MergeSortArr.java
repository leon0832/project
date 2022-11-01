package com.collection;

import java.util.Arrays;

/**
 * 合并有序数组
 */
public class MergeSortArr {

    public static void main(String[] args) {
        int[] b = {1, 2, 3, 7};
        int[] a = {2, 3, 4, 6, 9};

        int aLen = a.length;
        int bLen = b.length;
        int[] result = new int[aLen + bLen];

        merge(a, b, aLen, bLen, result);

        System.out.println(Arrays.toString(a));
        System.out.println(Arrays.toString(b));
        System.out.println(Arrays.toString(result));
    }

    private static void merge(int[] a, int[] b, int aLen, int bLen, int[] result) {

        int i = 0;
        int j = 0;
        int t = 0;
        while (i < aLen && j < bLen) {
            if (a[i] <= b[j]) {
                result[t++] = a[i++];
            } else {
                result[t++] = b[j++];
            }
        }
        while (i < aLen) {//将a数组剩余元素填充进temp中
            result[t++] = a[i++];
        }
        while (j < bLen) {//将b数组剩余元素填充进temp中
            result[t++] = b[j++];
        }
    }
}
