package com.es.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author lh
 */
@SuppressWarnings("unused")
public class SearchResult {
	private Long total;
	private Float useTime;
	private String distance;
	private List<Map<String, Object>> data = new ArrayList<>();

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public Float getUseTime() {
		return useTime;
	}

	public void setUseTime(Float useTime) {
		this.useTime = useTime;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public List<Map<String, Object>> getData() {
		return data;
	}

	public void setData(List<Map<String, Object>> data) {
		this.data = data;
	}

	public SearchResult() {
	}

	public SearchResult(Long total, Float useTime, String distance, List<Map<String, Object>> data) {
		this.total = total;
		this.useTime = useTime;
		this.distance = distance;
		this.data = data;
	}
}
