package com.collection;

/**
 * @author lh
 */
@SuppressWarnings("unused")
public class CollectionUtil {
	/*
	 * 快速排序：选一个值pointKey(一般是第一个),小于pointKey放在左边，大于pointKey放在右边，分成两个序列，递归直到low >= high
	 * 步骤：
	 * 1，设定两个指针low,high。初值是0和数组长度-1，指定关键字pointKey
	 * 2，从high所指的位置向前找，找到一个比pointKey小的，和关键字交换值(arr[low] = arr[high])
	 * 3，从low所指的位置向后找，找到一个比pointKey大的，和关键字交换值
	 * 4，直到low>=high，把关键字赋值arr[low]=pointKey
	 * 5，递归分出的两个序列[lowStart,low-1]  [low+1,highEnd]
	 * */
	private static void quickSort(int[] arr, int low, int high) {
		//为后面的递归使用
		int lowStart = low;
		int highEnd = high;
		if (low < high) {
			int pointKey = arr[low];
			while (low < high) {
				//如果都是大于pointKey，high指针往前移
				while (low < high && arr[high] > pointKey) {
					high--;
				}
				//这里low++是先把arr[low]赋值为arr[high]，再low+=1，因为这个值是比pointKey小的，下一次不用比较了
				if (low < high) {
					arr[low++] = arr[high];
				}
				//如果都是小于pointKey，low指针往后移
				while (low < high && arr[low] < pointKey) {
					low++;
				}
				//这里的high--和上面同理
				if (low < high) {
					arr[high--] = arr[low];
				}
			}
			arr[low] = pointKey;
			quickSort(arr, lowStart, low - 1);
			quickSort(arr, low + 1, highEnd);
		}
	}

	/**
	 * 冒泡排序
	 *
	 * @param arr 数组
	 */
	private static void popSort(int[] arr) {
		for (int i = 1; i < arr.length; i++) {
			for (int j = 0; j < arr.length - i; j++) {
				if (arr[j] > arr[j + 1]) {
					int temp = arr[j];
					arr[j] = arr[j + 1];
					arr[j + 1] = temp;
				}
			}
		}
	}


	/**
	 * 生成随即数组
	 *
	 * @param size 数组大小
	 * @return result
	 */
	private static int[] getRandomArray(int size) {
		int[] result = new int[size];
		for (int i = 0; i < size; i++) {
			result[i] = i;
		}
		for (int i = 0; i < size; i++) {
			int random = (int) (size * Math.random());
			int temp = result[i];
			result[i] = result[random];
			result[random] = temp;
		}
		return result;
	}

	public static void main(String[] args) {

		int[] quickArr = getRandomArray(10000);
		int[] popArr = getRandomArray(10000);

		long quickStart = System.currentTimeMillis();
		quickSort(quickArr, 0, quickArr.length - 1);
		long quickEnd = System.currentTimeMillis();

		long popStart = System.currentTimeMillis();
		popSort(popArr);
		long popEnd = System.currentTimeMillis();


		//耗时比较  数组越大差异越大
		System.out.println("快排耗时：" + String.valueOf(quickEnd - quickStart));
		System.out.println("冒泡耗时：" + String.valueOf(popEnd - popStart));
	}
}
