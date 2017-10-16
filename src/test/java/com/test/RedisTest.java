package com.test;

import com.util.JedisUtil;
import org.junit.Test;
import redis.clients.jedis.Jedis;

public class RedisTest {

	@Test
	public void connectTest() {
		JedisUtil.set("sdf","");
	}
}
