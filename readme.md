redis

madir /home/rdtar
上传redis包到此文件夹

解压
tar zxvf redis

进入目录
cd redis

安装
make

创建本地目录
mkdir /usr/local/redis

拷贝文件
cp redis-cli redis-server /usr/local/redis

cp redis-conf /usr/local/redis

cd /usr/local/redis

启动redis(前段启动)
./redis-server

启动redis（后端启动）
修改配置redis.conf
daemonize yes

./redis-server redis.conf 指定配置

.redis-cli 进入终端

1、key 命名规则：除了 \n 和 空格 以外

exists key --key是否存在
del key --删除key
type key --key类型
keys pattern --匹配key
rename oldkey newkey --修改key名
dbsize --数据库key数量
expire key seconds --为key指定过期时间
ttl key --返回key剩余过期时间
select db-index --选择数据库
move key db-index --将key移动到指定数据库
flushdb --删除当前数据库所有key
flusall --删除所有数据库key

2、String 类型操作

String是redis最基本的数据类型
redis的String可以包含任何数据，包括jpg图片或者序列化对象
单个value值最大上限是1G字节
如果只用String类型，redis就可以被看作加上持久化特性的memcache

//String类型操作
set key value --设置key对应值为string类型的value
mset key1 value2 key2 value2 .... --一次设置多个key值
mset key1 key2..... --一次获取多个key值
incr key --key值加加操作，返回新的值
decr key --key值减减操作
incrby key integer --同incr加指定的值
decrby key integer --同decr减指定的值
append key value --给指定key的字符后追加值
substr key start end --返回截取过的key的字符串

3、list 类型操作

list类型为双向链表，通过push,pop操作从链表的头部或者尾部添加删除元素，这使得list既可以用作栈，也可以用作队列

上进上出：栈
上进下出：队列

lpush key string --在key对应list的头部添加字符串元素
rpop key --从list的尾部删除元素，并返回删除元素
llen key --返回key对应list的长度，key不存在时返回0，若key对应类型不是list返回错误
lrange key start end --返回指定区间的元素，下标从0开始
rpush key string --同上，从尾部添加元素
lpop key --从list头部删除元素，并返回删除元素
ltrim key start end --截取list,保留指定区间元素

4、set集合类型

redis的set是string类型的无序集合
set元素最大可以包含(2的32次方-1)个元素
添加、删除、取交集、并集、差集

sadd key member --添加一个string元素到key对应的set集合中，成功返回1;若元素已存在集合中返回0，key不存在返回错误
srem key member1 member2 .. --移除指定元素，成功返回1
smove p1 p2 member --从p1集合中移动元素member到p2集合
scard key --返回set集合元素个数
sismember key member --判断member是否在集合中
sinter key1 key2 ... --返回给定key的交集
sunion key1 key2 ... --返回给定key的并集
sdiff key1 key2 ... --返回给定key的差集
smembers key --返回key对应set的所有元素，结果是无序的

5、Sort Set排序集合类型

和set一样sort set也是string类型元素的集合
不同的是每一个元素都会关联一个权
通过权值可以有序的获取集合中的元素

该sort set类型适合场合：
获得热么帖子(回复量)信息：select * from message order by backnum desc limit 5;（耗费mysql数据库资源）

zadd key score member --添加元素到集合，元素在集合中存在则更新对应score
zrem key member --删除指定元素 1成功，不存在返回0
zincrby key incr menber --安装incr幅度增加对应menber的score值，返回score值
zrank key member --返回集合中指定元素的排名(下标)，score 升序
zrevrank key member --score 降序
zrange key start end --返回集合中指定区间的元素，有序结果 score升序
zrevrange key start end --同上 score将序
zcard key --返回集合中元素个数
zscore key element --返回给定元素对应的score
zremrangebyrank key min max --删除集合中排名在指定区间的元素

快照持久化：

快照持久化备份频率
save 900 1 --900秒如果一个key被修改，则发起快照保存
save 300 10
save 60 10000

--数据修改的频率高，备份频率高，反之

精细持久化(AOF) append only file
./redis-cli bgsave
./redis-cli -h 127.0.0.1 -p 6379 bgsave

开启AOF持久化(会清空redis内部的数据)

aof追加持久化备份频率
appendfsync always --每次收到写命令就立即强制写入磁盘，最慢的，但保证最完整的持久化，不推荐
appendfsync everysec --每秒钟强制写入磁盘一次，在性能和持久化方面坐到折中，推荐
appendfsync no --完全依赖os,性能最好,持久化没保证

为aof备份文件做优化处理
./redis-cli bgrewriteaof
./redis-cli -h 127.0.0.1 -p 6379 bgrewriteaof

redis 远程连接

bind 127.0.0.1 -----> bind 0.0.0.0

CONFIG get requirepass

CONFIG set requirepass "password"

AUTH password

http://www.redis.net.cn/tutorial/3501.html

http://www.redis.cn/documentation.html

http://blog.csdn.net/zbw18297786698/article/details/52904316


#可视化工具
https://redisdesktop.com/download