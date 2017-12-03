package com.es.search;

import com.es.model.Member;
import com.es.model.SearchResult;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.GeoDistanceRangeQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author lh
 */
@SuppressWarnings("unused")
public class NearbyService {

	private Settings settings = Settings.settingsBuilder().build();

	private TransportClient client;

	//es index x相当于数据库名
	private String indexName = "es_nearby";

	//相当于表名
	private String indexType = "member";

	//坐标
	private Double myLat = 39.971138;
	private Double myLon = 116.450262;

	//定义性别
	private String[] genders = new String[]{"男", "女"};

	//ES使用前，必须要安装ES服务
	public NearbyService() {

		try {
			client = TransportClient.builder()
					.settings(settings)
					.build()
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("39.106.116.178"), 9300));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 创建索引
	 */
	public void createIndex() {
		XContentBuilder mapping = createMapping();

		//建立数据库，数据表，数据结构
		client.admin().indices().prepareCreate(indexName).execute().actionGet();

		PutMappingRequest putMappingRequest = Requests.putMappingRequest(indexName).type(indexType).source(mapping);

		PutMappingResponse putMappingResponse = client.admin().indices().putMapping(putMappingRequest).actionGet();

		if (!putMappingResponse.isAcknowledged()) {
			System.out.println("无法创建mapping");
		} else {
			System.out.println("创建mapping成功");
		}
	}

	/**
	 * 随机生成十万条数据
	 *
	 * @param lon   经度
	 * @param lat   纬度
	 * @param count 随机产生多少条数据
	 * @return
	 */
	public Integer addDataToIndex(Double lon, Double lat, int count) {

		List<String> memberList = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			Double max = 0.00001;
			Double min = 0.000001;
			Random random = new Random();
			//随机生成一组坐标
			Double s = random.nextDouble() % (max - min + 1) + max;
			//格式化保留六位小数
			DecimalFormat decimalFormat = new DecimalFormat("######0.000000");
			String sLon = decimalFormat.format(s + lon);
			String sLat = decimalFormat.format(s + lat);
			Double dLon = Double.valueOf(sLon);
			Double dLat = Double.valueOf(sLat);

			Integer sex = random.nextInt(2);

			Member member = new Member(i, "用户" + i, genders[sex], dLat, dLon);

			memberList.add(objToJson(member));
		}

		//创建索引
		List<IndexRequest> requests = new ArrayList<>();
		for (String str : memberList) {
			IndexRequest request = client.prepareIndex(indexName, indexType)
					.setSource(str).request();
			requests.add(request);
		}

		//批量创建索引
		BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
		for (IndexRequest request : requests) {
			bulkRequestBuilder.add(request);
		}

		BulkResponse bulkResponse = bulkRequestBuilder.execute().actionGet();

		if (bulkResponse.hasFailures()) {
			System.out.println("创建索引出错");
		}
		return bulkRequestBuilder.numberOfActions();
	}

	/**
	 * 将对象转为json字符串
	 *
	 * @param member
	 * @return
	 */
	private String objToJson(Member member) {
		String jsonStr = null;

		try {
			XContentBuilder jsonBuild = XContentFactory.jsonBuilder();
			jsonBuild.startObject()
					.field("id", member.getId())
					.field("name", member.getName())
					.field("gender", member.getGender())
					.startObject("location")
					.field("lat", member.getLat())
					.field("lon", member.getLon())
					.endObject()
					.endObject();
			jsonStr = jsonBuild.string();
			System.out.println(jsonStr);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonStr;
	}

	/**
	 * 第一步创建mapping,设计数据表结构
	 *
	 * @return
	 */
	private XContentBuilder createMapping() {
		XContentBuilder mapping = null;
		try {
			mapping = XContentFactory.jsonBuilder()
					.startObject().startObject(indexType).startObject("properties")

					.startObject("id").field("type", "integer").endObject()
					.startObject("name").field("type", "string").endObject()
					.startObject("gender").field("type", "string").endObject()
					.startObject("location").field("type", "geo_point").endObject()

					.endObject().endObject().endObject();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mapping;
	}

	/**
	 * 查找附近的人
	 *
	 * @param lat
	 * @param lon
	 * @param radius
	 * @param size
	 * @return
	 */
	public SearchResult searchNearbyMember(Double lat, Double lon, int radius, int size, String gender) {

		SearchResult searchResult = new SearchResult();

		SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indexName).setTypes(indexType);

		//设置分页
		searchRequestBuilder.setFrom(0).setSize(size);

		//构建坐标查询规则
		QueryBuilder queryBuilder = new GeoDistanceRangeQueryBuilder("location")
				.point(lat, lon)
				.from("0m").to(radius + "m")
				.optimizeBbox("memory")
				.geoDistance(GeoDistance.PLANE);
		searchRequestBuilder.setPostFilter(queryBuilder);

		//创建排序规则
		GeoDistanceSortBuilder geoDistanceSortBuilder = SortBuilders.geoDistanceSort("location");
		geoDistanceSortBuilder.unit(DistanceUnit.METERS);
		geoDistanceSortBuilder.order(SortOrder.ASC);
		geoDistanceSortBuilder.point(lat, lon);
		searchRequestBuilder.addSort(geoDistanceSortBuilder);

		//
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		if (gender != null && !"".equals(gender.trim())) {
			boolQueryBuilder.must(QueryBuilders.matchQuery("gender", gender));
		}
		searchRequestBuilder.setQuery(boolQueryBuilder);

		//开始搜索
		SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
		//一般的搜索引擎中，高亮
		SearchHits searchHits = searchResponse.getHits();
		SearchHit[] hits = searchHits.getHits();
		for (SearchHit hit :
				hits) {
			BigDecimal geoDis = new BigDecimal((Double) hit.getSortValues()[0]);
			//获取高亮中的记录
			Map<String, Object> hitMap = hit.getSource();
			//向结果中填值
			hitMap.put("geo", geoDis.setScale(0, BigDecimal.ROUND_HALF_DOWN));
			searchResult.getData().add(hitMap);
		}
		//耗时
		Float useTime = searchResponse.getTookInMillis() / 1000f;
		searchResult.setUseTime(useTime);

		//单位
		searchResult.setDistance("m");

		searchResult.setTotal(searchHits.getTotalHits());

		return searchResult;
	}

}
