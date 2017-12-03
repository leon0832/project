package com.es.model;

/**
 * 成员 实体对象
 */
@SuppressWarnings("unused")
public class Member {

	private Integer id;
	private String name;
	//纬度
	private Double lat;
	//经度
	private Double lon;
	//性别
	private String gender;

	public Member(Integer id, String name, String gender, Double lat, Double lon) {
		this.id = id;
		this.name = name;
		this.lat = lat;
		this.lon = lon;
		this.gender = gender;
	}

	public Member() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLon() {
		return lon;
	}

	public void setLon(Double lon) {
		this.lon = lon;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
}
