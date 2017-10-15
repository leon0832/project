package com.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisUtil {

	//Redis服务器IP
	private static String HOST = "127.0.0.1";

	//Redis的端口号
	private static int PORT = 6379;

	//可用连接实例的最大数目，默认值为8；
	//如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
	private static int MAX_ACTIVE = 8;

	//控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
	private static int MAX_IDLE = 8;

	//等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
	private static int MAX_WAIT = 10000;

	//在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
	private static boolean TEST_ON_BORROW = true;

	private static boolean TEST_ON_RETURN = true;

	private static JedisPool jedisPool = null;

	/**
	 * 缓存生存时间
	 */
	private final static int expire = 60000;

	/**
	 * 初始化Redis连接池
	 */
	static {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(MAX_ACTIVE);
		config.setMaxIdle(MAX_IDLE);
		config.setMaxWaitMillis(MAX_WAIT);
		config.setTestOnBorrow(TEST_ON_BORROW);
		config.setTestOnReturn(TEST_ON_RETURN);
		jedisPool = new JedisPool(config, HOST, PORT, 10000);
		// redis如果设置了密码：
//			jedisPool = new JedisPool(config, HOST,PORT, 10000,"");
	}

	/**
	 * 从jedis连接池中获取获取jedis对象
	 */
	public static Jedis getJedis() {
		return jedisPool.getResource();
	}

	/**
	 * 回收jedis
	 */
	public static void returnJedis(Jedis jedis) {
		if (jedis != null) {
			jedis.close();
		}
	}

	/**
	 * 设置过期时间
	 */
	public static void expire(String key, int seconds) {
		if (seconds <= 0) {
			return;
		}
		Jedis jedis = getJedis();
		jedis.expire(key, seconds);
		returnJedis(jedis);
	}

	/**
	 * 设置默认过期时间
	 */
	public static void expire(String key) {
		expire(key, expire);
	}

	public static void set(String key, String value) {
		if (isBlank(key)) return;
		Jedis jedis = getJedis();
		jedis.set(key, value);
		returnJedis(jedis);
	}

	public static void set(String key, Object value) {
		if (isBlank(key)) return;
		Jedis jedis = getJedis();
		jedis.set(key.getBytes(), SerializeUtil.serialize(value));
		returnJedis(jedis);
	}

	public static void set(String key, int value) {
		if (isBlank(key)) return;
		set(key, String.valueOf(value));
	}

	public static void set(String key, long value) {
		if (isBlank(key)) return;
		set(key, String.valueOf(value));
	}

	public static void set(String key, float value) {
		if (isBlank(key)) return;
		set(key, String.valueOf(value));
	}

	public static void set(String key, double value) {
		if (isBlank(key)) return;
		set(key, String.valueOf(value));
	}

	public static Float getFloat(String key) {
		if (isBlank(key)) return null;
		return Float.valueOf(getStr(key));
	}

	public static Double getDouble(String key) {
		if (isBlank(key)) return null;
		return Double.valueOf(getStr(key));
	}

	public static Long getLong(String key) {
		if (isBlank(key)) return null;
		return Long.valueOf(getStr(key));
	}

	public static Integer getInt(String key) {
		if (isBlank(key)) return null;
		return Integer.valueOf(getStr(key));
	}

	public static String getStr(String key) {
		if (isBlank(key)) return null;
		Jedis jedis = getJedis();
		String value = jedis.get(key);
		returnJedis(jedis);
		return value;
	}

	public static Object getObj(String key) {
		if (isBlank(key)) return null;
		Jedis jedis = getJedis();
		byte[] bits = jedis.get(key.getBytes());
		Object obj = SerializeUtil.unserialize(bits);
		returnJedis(jedis);
		return obj;
	}

	public static boolean isBlank(String str) {
		return str == null || "".equals(str.trim());
	}

}
