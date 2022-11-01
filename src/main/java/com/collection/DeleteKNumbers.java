package com.collection;

/**
 * 贪心算法，删除k位数后最大或最小
 */
public class DeleteKNumbers {


	private static void deleteFunc(String str, int k) {

		boolean flag;
		for (int j = k; j > 0; j--) {
			flag = false;
			for (int i = 0; i < str.length() - 1; i++) {
				//删除后最大<    删除后最小>
				if (str.charAt(i) < str.charAt(i + 1)) {
					str = str.replaceFirst(String.valueOf(str.charAt(i)), "");
					flag = true;
					break;
				}
			}
			if (!flag) {
				str = str.substring(0, str.length() - j);
				break;
			}
		}
		System.out.println(str);
	}

	public static void main(String[] args) {
		deleteFunc("2319274", 3);
	}
}
