package com.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by lh on 2018/1/11.
 *
 * @author lh
 */
public class WeightRandom {
	public static void main(String[] args) {

		WeightRandom wr = new WeightRandom();

		wr.initWeight(new String[]{"1", "2", "3", "4"}, new Integer[]{150, 100, 200, 600});

		Random r = new Random();
		for (int i = 0; i < 10; i++) {
			Integer rv = r.nextInt(wr.getMaxRandomValue());
			System.out.println(rv);
			System.out.println(wr.getElementByRandomValue(rv).getKey() + " " + rv);
		}
	}

	private List<WeightElement> weightElements;

	/**
	 * 初始化权重范围
	 *
	 * @param keys
	 * @param weights
	 */
	private void initWeight(String[] keys, Integer[] weights) {
		if (keys == null || weights == null || keys.length != weights.length) {
			return;
		}

		weightElements = new ArrayList<>();

		for (int i = 0; i < keys.length; i++) {
			weightElements.add(new WeightElement(keys[i], weights[i]));
		}

		rangeWeightElements();

		printRvs();
	}

	/**
	 * 权重范围赋值
	 */
	private void rangeWeightElements() {
		if (weightElements.size() == 0) {
			return;
		}

		//第一个元素权重范围
		WeightElement ele0 = weightElements.get(0);
		ele0.setThresholdLow(0);
		ele0.setThresholdHigh(ele0.getWeight());

		for (int i = 1; i < weightElements.size(); i++) {
			//当前元素
			WeightElement curElement = weightElements.get(i);
			//前一个元素
			WeightElement preElement = weightElements.get(i - 1);

			//当前元素权重范围下限
			curElement.setThresholdLow(preElement.getThresholdHigh());
			//当前元素权重范围上限
			curElement.setThresholdHigh(curElement.getThresholdLow() + curElement.getWeight());
		}
	}

	/**
	 * 通过权重范围获取元素
	 *
	 * @param rv
	 * @return
	 */
	private WeightElement getElementByRandomValue(Integer rv) {
		//因为元素权重范围有序递增，所以这里可以改为二分查找

		for (WeightElement e : weightElements) {
			if (rv >= e.getThresholdLow() && rv < e.getThresholdHigh()) {
				return e;
			}
		}

		return null;
	}

	/**
	 * 获取权重范围上限
	 *
	 * @return
	 */
	private Integer getMaxRandomValue() {
		if (weightElements == null || weightElements.size() == 0) {
			return null;
		}

		return weightElements.get(weightElements.size() - 1).getThresholdHigh();
	}

	/**
	 * 打印元素-权重-权重范围
	 */
	private void printRvs() {
		for (WeightElement e : weightElements) {
			System.out.println(e.toString());
		}
	}

	static class WeightElement {
		/**
		 * 元素标记
		 */
		private String key;
		/**
		 * 元素权重
		 */
		private Integer weight;
		/**
		 * 权重对应随机数范围低线
		 */
		private Integer thresholdLow;
		/**
		 * 权重对应随机数范围高线
		 */
		private Integer thresholdHigh;

		public WeightElement() {
		}

		public WeightElement(Integer weight) {
			this.key = weight.toString();
			this.weight = weight;
		}

		private WeightElement(String key, Integer weight) {
			this.key = key;
			this.weight = weight;
		}

		private String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		private Integer getWeight() {
			return weight;
		}

		public void setWeight(Integer weight) {
			this.weight = weight;
		}

		private Integer getThresholdLow() {
			return thresholdLow;
		}

		private void setThresholdLow(Integer thresholdLow) {
			this.thresholdLow = thresholdLow;
		}

		private Integer getThresholdHigh() {
			return thresholdHigh;
		}

		private void setThresholdHigh(Integer thresholdHigh) {
			this.thresholdHigh = thresholdHigh;
		}

		@Override
		public String toString() {
			return "key:" + this.key + " weight:" + this.weight + " low:" + this.thresholdLow + " high:" + this.thresholdHigh;
		}
	}
}
