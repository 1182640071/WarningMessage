package guodu.net.warning.function;

import guodu.net.warning.util.Loger;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
/**
 * 获取redis连接，根据传入的参数连接不同
 * */
public class RedisPool {
	private static JedisPoolConfig logic_config = new JedisPoolConfig();
	private Map<String , JedisSentinelPool> map = new HashMap<String , JedisSentinelPool>();     //存储每一个redis连接池
	private static RedisPool instance;

	/**
	 * 单例模式实例话RedisPool类
	 * */
	static synchronized public  RedisPool getInstance(){
		if(null == instance){
			instance = new RedisPool();
		}
		return instance;
	}
	
	/**
	 * 无参构造
	 * 初始化MaxActive，MaxIdle，MaxWait，TestOnBorrow
	 * */
     private RedisPool(){
 		logic_config.setMaxIdle(5);
 		logic_config.setTestOnBorrow(true);
     }
	
	/**
	 * 获取logic_jedispool，连接redis
	 * @param ip
	 * @param port
	 * @return logic_jedispool
	 * */
	private JedisPool getPool(String ip , int port){
		JedisPool logic_jedispool = new JedisPool(logic_config, ip, port , 100000 );
		return logic_jedispool;		
	}
	
	/**
	 * 获取JedisSentinelPool，连接redis
	 * 
	 * @param username
	 * 				用户名
	 * 
	 * @param password
	 * 				密码
	 * 
	 * @param sentinels
	 * 				Set<String> keys
	 * 
	 * @return JedisSentinelPool
	 * */
	private JedisSentinelPool getPool(Set<String> sentinels , String username , String password){
		JedisSentinelPool logic_jedispool = new JedisSentinelPool(username,sentinels,password);
		return logic_jedispool;		
	}
	
	
	/**
	 * 获取所需的且不存在的JedisPool
	 * @param redisInfo(ip,port)
	 * @param JedisPool
	 * */
	private JedisPool getOnlyJedis(String redisInfo){
		String ip = redisInfo.split(",")[0];
		int port = Integer.parseInt(redisInfo.split(",")[1]);
		JedisPool jp = getPool(ip, port);
		return jp;
	}
	
	/**
	 * 获取所需的且不存在的JedisPool
	 * @param redisInfo(ip,port)
	 * @param JedisPool
	 * */
	private JedisSentinelPool getOnlyJedisSentinelPools(String redisInfo , Set<String> sentinels){
		String username = redisInfo.split(",")[0];
		String password = redisInfo.split(",")[1];
		JedisSentinelPool redisSentinelJedisPool = getPool(sentinels , username , password);
		return redisSentinelJedisPool;
	}
	
//	/**
//	 * 判断所需JedisPool是否已存在
//	 * 如果存在，直接取
//	 * 如果不存在，进行实例话
//	 * @param redisInfo(ip,port)
//	 * @param JedisPool
//	 * */
//	public JedisPool getJedis(String redisInfo){
//		JedisPool jp = null;
//		if(map.containsKey(redisInfo)){
//			jp = map.get(redisInfo);
//		}else{
//			jp = getOnlyJedis(redisInfo);
//			map.put(redisInfo, jp);
//		}
//		return jp;		
//	}
	
	/**
	 * 判断所需JedisSentinelPool是否已存在
	 * 如果存在，直接取
	 * 如果不存在，进行实例话
	 * @param redisInfo(ip,port)
	 * @param JedisPool
	 * */
	public synchronized JedisSentinelPool getJedisSentinelPool(String redisInfo , Set<String> sentinels){
//		JedisSentinelPool jp = null;
//		if(map.containsKey(key)){
//			jp = map.get(key);
//		}else{
//			jp = getOnlyJedisSentinelPools(redisInfo , sentinels);
//			map.put(key, jp);
//		}
		JedisSentinelPool jp = getOnlyJedisSentinelPools(redisInfo , sentinels);
		return jp;		
	}
	
	/**
	 * 获取key队列的长度
	 * 先通过getType获取key队列类型
	 * 然后调用getLength方法查询长度
	 * @param Jedis 
	 * @param key
	 * @return long
	 * */
	public Set<String> getKeys(JedisSentinelPool jp , String key){
		Jedis jedis = jp.getResource();          //实例话jedis，通过jedis操作redis
		Set<String> set = jedis.keys(key);
		jp.returnResource(jedis);
		return set;
	}
	
	
	/**
	 * 获取Sentinel的信息
	 * 
	 * @param JedisSentinelPool 
	 * 						Sentinel链接
	 * @return String
	 * 						返回info信息
	 * */
	public String getInfo(JedisSentinelPool jp){
		Jedis jedis = jp.getResource();          //实例话jedis，通过jedis操作redis
		String rs = jedis.info();
		jp.returnResource(jedis);
		return rs;
	}
	
	
	/**
	 * 获取key队列的长度
	 * 先通过getType获取key队列类型
	 * 然后调用getLength方法查询长度
	 * @param Jedis 
	 * @param key
	 * @return long
	 * */
	public long getRedisLength(JedisSentinelPool jp , String key){
		Jedis jedis = jp.getResource();          //实例话jedis，通过jedis操作redis
		String type = jedis.type(key);
		long l = getLength(type , key , jedis);
		jp.returnResource(jedis);
		return l;
	}
	
	
	/**
	 * 根据队列类型查询队列长度
	 * @param key
	 * @return long
	 * */
	private long getLength(String type , String key , Jedis jedis){
		long rs = 0;
		try {
			if("String".equals(type)){        //string类型
				rs = 1;
			}else if("hash".equals(type)){    //hash类型
				rs = jedis.hlen(key);
			}else{
				rs = jedis.llen(key);		  //默认为list			
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Loger.Info_log.info("[ERROR]redis队列长度判断失败，key：" + key);
		}		
		return rs;
	}
}
