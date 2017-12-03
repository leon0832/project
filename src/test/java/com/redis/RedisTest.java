package com.redis;

import org.junit.Test;

public class RedisTest {

	@Test
	public void connectTest() {
		JedisUtil.set("sdf","sdf");
	}
}
