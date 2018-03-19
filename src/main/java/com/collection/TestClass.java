package com.collection;


/**
 * 取最长相似字符串"qwertyuiop"和"gfhrtyuiaagd"的最长相似为"rtyui"
 * <p>
 * 1、获取两个字符串中最大相同子串。
 * 2、枚举出长度较短的字符串的所有子串。将子串放在字符串数组中。
 * 3、通过循环判断长度较长的字符串中是否包含字符串数组中的元素，并返回包含且最长的子字符串。
 */
public class TestClass {
	private static void stringSub(String str1, String str2) {
		if (str1.length() > str2.length()) {
			deal(str2, str1);
		} else {
			deal(str1, str2);
		}
	}

	private static void deal(String str1, String str2) {
		String[] arr = new String[1000000];
		String max = "";
		boolean flag = false;
		int k = 0;
		for (int i = 0; i < str1.length(); i++) {
			for (int j = i + 1; j < str1.length(); j++) {
				arr[k++] = str1.substring(i, j);
			}
		}

		for (int i = 0; i < k; i++) {
			if (str2.contains(arr[i])) {
				flag = true;
				if (max.length() < arr[i].length()) {
					max = arr[i];
				}
			}
		}
		System.out.println(flag ? max : "无");
	}

	public static void main(String[] args) {
		stringSub("qwertyuiop", "gfhrtyuiaagd");
	}

}
