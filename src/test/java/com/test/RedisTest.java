package com.test;

import com.util.JedisUtil;
import org.junit.Test;
import redis.clients.jedis.Jedis;

public class RedisTest {

	@Test
	public void connectTest() {
		Jedis jedis = JedisUtil.getJedis();
		jedis.select(0);
		System.out.println(jedis.dbSize());
		String s = "0";
		switch (s) {
			case "q":
				System.out.println("sss");
				break;
			case "2":
				System.out.println("123");
				break;
			default:
				break;
		}

	}
}
