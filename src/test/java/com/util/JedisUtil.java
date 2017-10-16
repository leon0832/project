package com.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;
import java.util.Map;
import java.util.Set;

public  class JedisUtil {

	//Redis服务器IP
	private static String HOST = "47.95.222.253";

	//Redis的端口号
	private static int PORT = 6379;

	//最大激活连接数 默认值8；
	//如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
	private static int MAX_ACTIVE = 8;

	////连接池最大空闲连接数 默认值8
	private static int MAX_IDLE = 8;

	//等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
	private static int MAX_WAIT = 10000;

	//使用连接时，测试连接是否可用
	private static boolean TEST_ON_BORROW = true;

	//返回连接时，测试连接是否可用
	private static boolean TEST_ON_RETURN = true;

	//redis连接密码
	private static String AUTH = "iceberglh";

	//数据库索引
	private static int INDEX = 15;

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
//		jedisPool = new JedisPool(config, HOST, PORT, 10000);
		// redis如果设置了密码：
		jedisPool = new JedisPool(config, HOST, PORT, 10000, AUTH,INDEX);
	}

	public  JedisPool getPool() {
		return jedisPool;
	}

	/**
	 * 从jedis连接池中获取获取jedis对象
	 */
	public  static Jedis getJedis() {
		return jedisPool.getResource();
	}

	/**
	 * 回收jedis
	 */
	public  static void returnJedis(Jedis jedis) {
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


	/**
	 * 通过key获取储存在redis中的value
	 * 并释放连接
	 *
	 * @param key
	 * @return 成功返回value 失败返回null
	 */
	public static String get(String key) {
		Jedis jedis = null;
		String value = null;
		try {
			jedis = getJedis();
			value = jedis.get(key);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return value;
	}


	/**
	 * 向redis存入key和value,并释放连接资源
	 * 如果key已经存在 则覆盖
	 *
	 * @param key
	 * @param value
	 * @return 成功 返回OK 失败返回 0
	 */
	public static String set(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			return jedis.set(key, value);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
			return "0";
		} finally {
			returnJedis(jedis);
		}
	}

	/**
	 * 删除指定的key,也可以传入一个包含key的数组
	 *
	 * @param keys 一个key 也可以使 string 数组
	 * @return 返回删除成功的个数
	 */
	public static Long del(String... keys) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			return jedis.del(keys);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
			return 0L;
		} finally {
			returnJedis(jedis);
		}
	}

	/**
	 * 通过key向指定的value值追加值
	 *
	 * @param key
	 * @param str
	 * @return 成功返回 添加后value的长度 失败 返回 添加的 value 的长度 异常返回0L
	 */
	public static Long append(String key, String str) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			return jedis.append(key, str);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
			return 0L;
		} finally {
			returnJedis(jedis);
		}
	}

	/**
	 * 判断key是否存在
	 *
	 * @param key
	 * @return true OR false
	 */
	public static Boolean exists(String key) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			return jedis.exists(key);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
			return false;
		} finally {
			returnJedis(jedis);
		}
	}

	/**
	 * 设置key value,如果key已经存在则返回0,nx==> not exist
	 *
	 * @param key
	 * @param value
	 * @return 成功返回1 如果存在 和 发生异常 返回 0
	 */
	public static Long setnx(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			return jedis.setnx(key, value);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
			return 0L;
		} finally {
			returnJedis(jedis);
		}
	}

	/**
	 * 设置key value并制定这个键值的有效期
	 *
	 * @param key
	 * @param value
	 * @param seconds 单位:秒
	 * @return 成功返回OK 失败和异常返回null
	 */
	public static String setex(String key, String value, int seconds) {
		Jedis jedis = null;
		String res = null;
		try {
			jedis = getJedis();
			res = jedis.setex(key, seconds, value);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * 通过key 和offset 从指定的位置开始将原先value替换
	 * 下标从0开始,offset表示从offset下标开始替换
	 * 如果替换的字符串长度过小则会这样
	 * example:
	 * value : bigsea@zto.cn
	 * str : abc
	 * 从下标7开始替换 则结果为
	 * RES : bigsea@.abc.cn
	 *
	 * @param key
	 * @param str
	 * @param offset 下标位置
	 * @return 返回替换后 value 的长度
	 */
	public static Long setrange(String key, String str, int offset) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			return jedis.setrange(key, offset, str);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
			return 0L;
		} finally {
			returnJedis(jedis);
		}
	}

	/**
	 * 通过批量的key获取批量的value
	 *
	 * @param keys string数组 也可以是一个key
	 * @return 成功返回value的集合, 失败返回null的集合 ,异常返回空
	 */
	public static List<String> mget(String... keys) {
		Jedis jedis = null;
		List<String> values = null;
		try {
			jedis = getJedis();
			values = jedis.mget(keys);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return values;
	}

	/**
	 * 批量的设置key:value,可以一个
	 * example:
	 * obj.mset(new String[]{"key2","value1","key2","value2"})
	 *
	 * @param keysvalues
	 * @return 成功返回OK 失败 异常 返回 null
	 */
	public static String mset(String... keysvalues) {
		Jedis jedis = null;
		String res = null;
		try {
			jedis = getJedis();
			res = jedis.mset(keysvalues);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * 批量的设置key:value,可以一个,如果key已经存在则会失败,操作会回滚
	 * example:
	 * obj.msetnx(new String[]{"key2","value1","key2","value2"})
	 *
	 * @param keysvalues
	 * @return 成功返回1 失败返回0
	 */
	public static Long msetnx(String... keysvalues) {
		Jedis jedis = null;
		Long res = 0L;
		try {
			jedis = getJedis();
			res = jedis.msetnx(keysvalues);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * 设置key的值,并返回一个旧值
	 *
	 * @param key
	 * @param value
	 * @return 旧值 如果key不存在 则返回null
	 */
	public static String getset(String key, String value) {
		Jedis jedis = null;
		String res = null;
		try {
			jedis = getJedis();
			res = jedis.getSet(key, value);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * 通过下标 和key 获取指定下标位置的 value
	 *
	 * @param key
	 * @param startOffset 开始位置 从0 开始 负数表示从右边开始截取
	 * @param endOffset
	 * @return 如果没有返回null
	 */
	public static String getrange(String key, int startOffset, int endOffset) {
		Jedis jedis = null;
		String res = null;
		try {
			jedis = getJedis();
			res = jedis.getrange(key, startOffset, endOffset);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * 通过key 对value进行加值+1操作,当value不是int类型时会返回错误,当key不存在是则value为1
	 *
	 * @param key
	 * @return 加值后的结果
	 */
	public static Long incr(String key) {
		Jedis jedis = null;
		Long res = null;
		try {
			jedis = getJedis();
			res = jedis.incr(key);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * 通过key给指定的value加值,如果key不存在,则这是value为该值
	 *
	 * @param key
	 * @param integer
	 * @return
	 */
	public static Long incrBy(String key, Long integer) {
		Jedis jedis = null;
		Long res = null;
		try {
			jedis = getJedis();
			res = jedis.incrBy(key, integer);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * 对key的值做减减操作,如果key不存在,则设置key为-1
	 *
	 * @param key
	 * @return
	 */
	public static Long decr(String key) {
		Jedis jedis = null;
		Long res = null;
		try {
			jedis = getJedis();
			res = jedis.decr(key);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * 减去指定的值
	 *
	 * @param key
	 * @param integer
	 * @return
	 */
	public static Long decrBy(String key, Long integer) {
		Jedis jedis = null;
		Long res = null;
		try {
			jedis = getJedis();
			res = jedis.decrBy(key, integer);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * 通过key获取value值的长度
	 *
	 * @param key
	 * @return 失败返回null
	 */
	public static Long serlen(String key) {
		Jedis jedis = null;
		Long res = null;
		try {
			jedis = getJedis();
			res = jedis.strlen(key);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * 通过key给field设置指定的值,如果key不存在,则先创建
	 *
	 * @param key
	 * @param field 字段
	 * @param value
	 * @return 如果存在返回0 异常返回null
	 */
	public static Long hset(String key, String field, String value) {
		Jedis jedis = null;
		Long res = null;
		try {
			jedis = getJedis();
			res = jedis.hset(key, field, value);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * 通过key给field设置指定的值,如果key不存在则先创建,如果field已经存在,返回0
	 *
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	public static Long hsetnx(String key, String field, String value) {
		Jedis jedis = null;
		Long res = null;
		try {
			jedis = getJedis();
			res = jedis.hsetnx(key, field, value);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * 通过key同时设置 hash的多个field
	 *
	 * @param key
	 * @param hash
	 * @return 返回OK 异常返回null
	 */
	public static String hmset(String key, Map<String, String> hash) {
		Jedis jedis = null;
		String res = null;
		try {
			jedis = getJedis();
			res = jedis.hmset(key, hash);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * 通过key 和 field 获取指定的 value
	 *
	 * @param key
	 * @param field
	 * @return 没有返回null
	 */
	public static String hget(String key, String field) {
		Jedis jedis = null;
		String res = null;
		try {
			jedis = getJedis();
			res = jedis.hget(key, field);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * 通过key 和 fields 获取指定的value 如果没有对应的value则返回null
	 *
	 * @param key
	 * @param fields 可以使 一个String 也可以是 String数组
	 * @return
	 */
	public static List<String> hmget(String key, String... fields) {
		Jedis jedis = null;
		List<String> res = null;
		try {
			jedis = getJedis();
			res = jedis.hmget(key, fields);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * 通过key给指定的field的value加上给定的值
	 *
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	public static Long hincrby(String key, String field, Long value) {
		Jedis jedis = null;
		Long res = null;
		try {
			jedis = getJedis();
			res = jedis.hincrBy(key, field, value);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * 通过key和field判断是否有指定的value存在
	 *
	 * @param key
	 * @param field
	 * @return
	 */
	public static Boolean hexists(String key, String field) {
		Jedis jedis = null;
		Boolean res = false;
		try {
			jedis = getJedis();
			res = jedis.hexists(key, field);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * 通过key返回field的数量
	 *
	 * @param key
	 * @return
	 */
	public static Long hlen(String key) {
		Jedis jedis = null;
		Long res = null;
		try {
			jedis = getJedis();
			res = jedis.hlen(key);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;

	}

	/**
	 * 通过key 删除指定的 field
	 *
	 * @param key
	 * @param fields 可以是 一个 field 也可以是 一个数组
	 * @return
	 */
	public static Long hdel(String key, String... fields) {
		Jedis jedis = null;
		Long res = null;
		try {
			jedis = getJedis();
			res = jedis.hdel(key, fields);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * 通过key返回所有的field
	 *
	 * @param key
	 * @return
	 */
	public static Set<String> hkeys(String key) {
		Jedis jedis = null;
		Set<String> res = null;
		try {
			jedis = getJedis();
			res = jedis.hkeys(key);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * 通过key返回所有和key有关的value
	 *
	 * @param key
	 * @return
	 */
	public static List<String> hvals(String key) {
		Jedis jedis = null;
		List<String> res = null;
		try {
			jedis = getJedis();
			res = jedis.hvals(key);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * 通过key获取所有的field和value
	 *
	 * @param key
	 * @return
	 */
	public static Map<String, String> hgetall(String key) {
		Jedis jedis = null;
		Map<String, String> res = null;
		try {
			jedis = getJedis();
			res = jedis.hgetAll(key);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * <p>
	 * 通过key向list头部添加字符串
	 * </p>
	 *
	 * @param key
	 * @param strs 可以使一个string 也可以使string数组
	 * @return 返回list的value个数
	 */
	public static Long lpush(String key, String... strs) {
		Jedis jedis = null;
		Long res = null;
		try {
			jedis = getJedis();
			res = jedis.lpush(key, strs);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * <p>
	 * 通过key向list尾部添加字符串
	 * </p>
	 *
	 * @param key
	 * @param strs 可以使一个string 也可以使string数组
	 * @return 返回list的value个数
	 */
	public static Long rpush(String key, String... strs) {
		Jedis jedis = null;
		Long res = null;
		try {
			jedis = getJedis();
			res = jedis.rpush(key, strs);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * <p>
	 * 通过key设置list指定下标位置的value
	 * </p>
	 * <p>
	 * 如果下标超过list里面value的个数则报错
	 * </p>
	 *
	 * @param key
	 * @param index 从0开始
	 * @param value
	 * @return 成功返回OK
	 */
	public static String lset(String key, Long index, String value) {
		Jedis jedis = null;
		String res = null;
		try {
			jedis = getJedis();
			res = jedis.lset(key, index, value);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * <p>
	 * 通过key从对应的list中删除指定的count个 和 value相同的元素
	 * </p>
	 *
	 * @param key
	 * @param count 当count为0时删除全部
	 * @param value
	 * @return 返回被删除的个数
	 */
	public static Long lrem(String key, long count, String value) {
		Jedis jedis = null;
		Long res = null;
		try {
			jedis = getJedis();
			res = jedis.lrem(key, count, value);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * <p>
	 * 通过key保留list中从strat下标开始到end下标结束的value值
	 * </p>
	 *
	 * @param key
	 * @param start
	 * @param end
	 * @return 成功返回OK
	 */
	public static String ltrim(String key, long start, long end) {
		Jedis jedis = null;
		String res = null;
		try {
			jedis = getJedis();
			res = jedis.ltrim(key, start, end);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * <p>
	 * 通过key从list的头部删除一个value,并返回该value
	 * </p>
	 *
	 * @param key
	 * @return
	 */
	public static String lpop(String key) {
		Jedis jedis = null;
		String res = null;
		try {
			jedis = getJedis();
			res = jedis.lpop(key);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * <p>
	 * 通过key从list尾部删除一个value,并返回该元素
	 * </p>
	 *
	 * @param key
	 * @return
	 */
	public static String rpop(String key) {
		Jedis jedis = null;
		String res = null;
		try {
			jedis = getJedis();
			res = jedis.rpop(key);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * <p>
	 * 通过key从一个list的尾部删除一个value并添加到另一个list的头部,并返回该value
	 * </p>
	 * <p>
	 * 如果第一个list为空或者不存在则返回null
	 * </p>
	 *
	 * @param srckey
	 * @param dstkey
	 * @return
	 */
	public static String rpoplpush(String srckey, String dstkey) {
		Jedis jedis = null;
		String res = null;
		try {
			jedis = getJedis();
			res = jedis.rpoplpush(srckey, dstkey);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * <p>
	 * 通过key获取list中指定下标位置的value
	 * </p>
	 *
	 * @param key
	 * @param index
	 * @return 如果没有返回null
	 */
	public static String lindex(String key, long index) {
		Jedis jedis = null;
		String res = null;
		try {
			jedis = getJedis();
			res = jedis.lindex(key, index);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * <p>
	 * 通过key返回list的长度
	 * </p>
	 *
	 * @param key
	 * @return
	 */
	public static Long llen(String key) {
		Jedis jedis = null;
		Long res = null;
		try {
			jedis = getJedis();
			res = jedis.llen(key);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * <p>
	 * 通过key获取list指定下标位置的value
	 * </p>
	 * <p>
	 * 如果start 为 0 end 为 -1 则返回全部的list中的value
	 * </p>
	 *
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public static List<String> lrange(String key, long start, long end) {
		Jedis jedis = null;
		List<String> res = null;
		try {
			jedis = getJedis();
			res = jedis.lrange(key, start, end);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * <p>
	 * 通过key向指定的set中添加value
	 * </p>
	 *
	 * @param key
	 * @param members 可以是一个String 也可以是一个String数组
	 * @return 添加成功的个数
	 */
	public static Long sadd(String key, String... members) {
		Jedis jedis = null;
		Long res = null;
		try {
			jedis = getJedis();
			res = jedis.sadd(key, members);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * <p>
	 * 通过key删除set中对应的value值
	 * </p>
	 *
	 * @param key
	 * @param members 可以是一个String 也可以是一个String数组
	 * @return 删除的个数
	 */
	public static Long srem(String key, String... members) {
		Jedis jedis = null;
		Long res = null;
		try {
			jedis = getJedis();
			res = jedis.srem(key, members);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * <p>
	 * 通过key随机删除一个set中的value并返回该值
	 * </p>
	 *
	 * @param key
	 * @return
	 */
	public static String spop(String key) {
		Jedis jedis = null;
		String res = null;
		try {
			jedis = getJedis();
			res = jedis.spop(key);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * <p>
	 * 通过key获取set中的差集
	 * </p>
	 * <p>
	 * 以第一个set为标准
	 * </p>
	 *
	 * @param keys 可以使一个string 则返回set中所有的value 也可以是string数组
	 * @return
	 */
	public static Set<String> sdiff(String... keys) {
		Jedis jedis = null;
		Set<String> res = null;
		try {
			jedis = getJedis();
			res = jedis.sdiff(keys);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * <p>
	 * 通过key获取set中的差集并存入到另一个key中
	 * </p>
	 * <p>
	 * 以第一个set为标准
	 * </p>
	 *
	 * @param dstkey 差集存入的key
	 * @param keys   可以使一个string 则返回set中所有的value 也可以是string数组
	 * @return
	 */
	public static Long sdiffstore(String dstkey, String... keys) {
		Jedis jedis = null;
		Long res = null;
		try {
			jedis = getJedis();
			res = jedis.sdiffstore(dstkey, keys);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * <p>
	 * 通过key获取指定set中的交集
	 * </p>
	 *
	 * @param keys 可以使一个string 也可以是一个string数组
	 * @return
	 */
	public static Set<String> sinter(String... keys) {
		Jedis jedis = null;
		Set<String> res = null;
		try {
			jedis = getJedis();
			res = jedis.sinter(keys);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * <p>
	 * 通过key获取指定set中的交集 并将结果存入新的set中
	 * </p>
	 *
	 * @param dstkey
	 * @param keys   可以使一个string 也可以是一个string数组
	 * @return
	 */
	public static Long sinterstore(String dstkey, String... keys) {
		Jedis jedis = null;
		Long res = null;
		try {
			jedis = getJedis();
			res = jedis.sinterstore(dstkey, keys);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * <p>
	 * 通过key返回所有set的并集
	 * </p>
	 *
	 * @param keys 可以使一个string 也可以是一个string数组
	 * @return
	 */
	public static Set<String> sunion(String... keys) {
		Jedis jedis = null;
		Set<String> res = null;
		try {
			jedis = getJedis();
			res = jedis.sunion(keys);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * <p>
	 * 通过key返回所有set的并集,并存入到新的set中
	 * </p>
	 *
	 * @param dstkey
	 * @param keys   可以使一个string 也可以是一个string数组
	 * @return
	 */
	public static Long sunionstore(String dstkey, String... keys) {
		Jedis jedis = null;
		Long res = null;
		try {
			jedis = getJedis();
			res = jedis.sunionstore(dstkey, keys);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * <p>
	 * 通过key将set中的value移除并添加到第二个set中
	 * </p>
	 *
	 * @param srckey 需要移除的
	 * @param dstkey 添加的
	 * @param member set中的value
	 * @return
	 */
	public static Long smove(String srckey, String dstkey, String member) {
		Jedis jedis = null;
		Long res = null;
		try {
			jedis = getJedis();
			res = jedis.smove(srckey, dstkey, member);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * <p>
	 * 通过key获取set中value的个数
	 * </p>
	 *
	 * @param key
	 * @return
	 */
	public static Long scard(String key) {
		Jedis jedis = null;
		Long res = null;
		try {
			jedis = getJedis();
			res = jedis.scard(key);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * <p>
	 * 通过key判断value是否是set中的元素
	 * </p>
	 *
	 * @param key
	 * @param member
	 * @return
	 */
	public static Boolean sismember(String key, String member) {
		Jedis jedis = null;
		Boolean res = null;
		try {
			jedis = getJedis();
			res = jedis.sismember(key, member);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * <p>
	 * 通过key获取set中随机的value,不删除元素
	 * </p>
	 *
	 * @param key
	 * @return
	 */
	public static String srandmember(String key) {
		Jedis jedis = null;
		String res = null;
		try {
			jedis = getJedis();
			res = jedis.srandmember(key);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * <p>
	 * 通过key获取set中所有的value
	 * </p>
	 *
	 * @param key
	 * @return
	 */
	public static Set<String> smembers(String key) {
		Jedis jedis = null;
		Set<String> res = null;
		try {
			jedis = getJedis();
			res = jedis.smembers(key);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * 通过key向zset中添加value,score,其中score就是用来排序的
	 * 如果该value已经存在则根据score更新元素
	 *
	 * @param key
	 * @param scoreMembers
	 * @return
	 */
	public static Long zadd(String key, Map<String, Double> scoreMembers) {
		Jedis jedis = null;
		Long res = null;
		try {
			jedis = getJedis();
			res = jedis.zadd(key, scoreMembers);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * 通过key向zset中添加value,score,其中score就是用来排序的
	 * 如果该value已经存在则根据score更新元素
	 *
	 * @param key
	 * @param score
	 * @param member
	 * @return
	 */
	public static Long zadd(String key, double score, String member) {
		Jedis jedis = null;
		Long res = null;
		try {
			jedis = getJedis();
			res = jedis.zadd(key, score, member);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * 通过key删除在zset中指定的value
	 *
	 * @param key
	 * @param members 可以使一个string 也可以是一个string数组
	 * @return
	 */
	public static Long zrem(String key, String... members) {
		Jedis jedis = null;
		Long res = null;
		try {
			jedis = getJedis();
			res = jedis.zrem(key, members);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * 通过key增加该zset中value的score的值
	 *
	 * @param key
	 * @param score
	 * @param member
	 * @return
	 */
	public static Double zincrby(String key, double score, String member) {
		Jedis jedis = null;
		Double res = null;
		try {
			jedis = getJedis();
			res = jedis.zincrby(key, score, member);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * 通过key返回zset中value的排名
	 * 下标从小到大排序
	 *
	 * @param key
	 * @param member
	 * @return
	 */
	public static Long zrank(String key, String member) {
		Jedis jedis = null;
		Long res = null;
		try {
			jedis = getJedis();
			res = jedis.zrank(key, member);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * 通过key返回zset中value的排名
	 * 下标从大到小排序
	 *
	 * @param key
	 * @param member
	 * @return
	 */
	public static Long zrevrank(String key, String member) {
		Jedis jedis = null;
		Long res = null;
		try {
			jedis = getJedis();
			res = jedis.zrevrank(key, member);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * 通过key将获取score从start到end中zset的value
	 * socre从大到小排序
	 * 当start为0 end为-1时返回全部
	 *
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public static Set<String> zrevrange(String key, long start, long end) {
		Jedis jedis = null;
		Set<String> res = null;
		try {
			jedis = getJedis();
			res = jedis.zrevrange(key, start, end);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * 通过key返回指定score内zset中的value
	 *
	 * @param key
	 * @param max
	 * @param min
	 * @return
	 */
	public static Set<String> zrangebyscore(String key, String max, String min) {
		Jedis jedis = null;
		Set<String> res = null;
		try {
			jedis = getJedis();
			res = jedis.zrevrangeByScore(key, max, min);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * 通过key返回指定score内zset中的value
	 *
	 * @param key
	 * @param max
	 * @param min
	 * @return
	 */
	public static Set<String> zrangeByScore(String key, double max, double min) {
		Jedis jedis = null;
		Set<String> res = null;
		try {
			jedis = getJedis();
			res = jedis.zrevrangeByScore(key, max, min);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * 返回指定区间内zset中value的数量
	 *
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	public static Long zcount(String key, String min, String max) {
		Jedis jedis = null;
		Long res = null;
		try {
			jedis = getJedis();
			res = jedis.zcount(key, min, max);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * 通过key返回zset中的value个数
	 *
	 * @param key
	 * @return
	 */
	public static Long zcard(String key) {
		Jedis jedis = null;
		Long res = null;
		try {
			jedis = getJedis();
			res = jedis.zcard(key);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * 通过key获取zset中value的score值
	 *
	 * @param key
	 * @param member
	 * @return
	 */
	public static Double zscore(String key, String member) {
		Jedis jedis = null;
		Double res = null;
		try {
			jedis = getJedis();
			res = jedis.zscore(key, member);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * 通过key删除给定区间内的元素
	 *
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public static Long zremrangeByRank(String key, long start, long end) {
		Jedis jedis = null;
		Long res = null;
		try {
			jedis = getJedis();
			res = jedis.zremrangeByRank(key, start, end);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * 通过key删除指定score内的元素
	 *
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public static Long zremrangeByScore(String key, double start, double end) {
		Jedis jedis = null;
		Long res = null;
		try {
			jedis = getJedis();
			res = jedis.zremrangeByScore(key, start, end);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * 返回满足pattern表达式的所有key
	 * keys(*)
	 * 返回所有的key
	 *
	 * @param pattern
	 * @return
	 */
	public static Set<String> keys(String pattern) {
		Jedis jedis = null;
		Set<String> res = null;
		try {
			jedis = getJedis();
			res = jedis.keys(pattern);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}

	/**
	 * 通过key判断值得类型
	 *
	 * @param key
	 * @return
	 */
	public static String type(String key) {
		Jedis jedis = null;
		String res = null;
		try {
			jedis = getJedis();
			res = jedis.type(key);
		} catch (Exception e) {
			returnJedis(jedis);
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
		return res;
	}


}
