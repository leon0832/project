package com.es.search;

import com.es.model.SearchResult;
import org.junit.Test;

import java.util.Map;

public class SearchTest {

	//坐标
	private Double myLat = 39.971138;
	private Double myLon = 116.450262;

	@Test
	public void initData() {
		NearbyService nearbyService = new NearbyService();
		//建库建表
		nearbyService.createIndex();
		//随机产生十万条数据
		nearbyService.addDataToIndex(myLon, myLat, 100000);
	}

	@Test
	public void search() {
		NearbyService nearbyService = new NearbyService();
		SearchResult searchResult = nearbyService.searchNearbyMember(myLat, myLon, 50, 50, "女");

		for (Map<String, Object> map : searchResult.getData()) {
			String name = map.get("name").toString();
			String location = map.get("location").toString();
			Object geo = map.get("geo");
			System.out.println(name + "的坐标：" + location + ",距离" + geo + "米,性别：" + map.get("gender"));
		}
		System.out.println("总数：" + searchResult.getTotal());
		System.out.println("耗时：" + searchResult.getUseTime());
	}
}
