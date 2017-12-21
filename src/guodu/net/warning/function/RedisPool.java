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
 * ��ȡredis���ӣ����ݴ���Ĳ������Ӳ�ͬ
 * */
public class RedisPool {
	private static JedisPoolConfig logic_config = new JedisPoolConfig();
	private Map<String , JedisSentinelPool> map = new HashMap<String , JedisSentinelPool>();     //�洢ÿһ��redis���ӳ�
	private static RedisPool instance;

	/**
	 * ����ģʽʵ����RedisPool��
	 * */
	static synchronized public  RedisPool getInstance(){
		if(null == instance){
			instance = new RedisPool();
		}
		return instance;
	}
	
	/**
	 * �޲ι���
	 * ��ʼ��MaxActive��MaxIdle��MaxWait��TestOnBorrow
	 * */
     private RedisPool(){
 		logic_config.setMaxIdle(5);
 		logic_config.setTestOnBorrow(true);
     }
	
	/**
	 * ��ȡlogic_jedispool������redis
	 * @param ip
	 * @param port
	 * @return logic_jedispool
	 * */
	private JedisPool getPool(String ip , int port){
		JedisPool logic_jedispool = new JedisPool(logic_config, ip, port , 100000 );
		return logic_jedispool;		
	}
	
	/**
	 * ��ȡJedisSentinelPool������redis
	 * 
	 * @param username
	 * 				�û���
	 * 
	 * @param password
	 * 				����
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
	 * ��ȡ������Ҳ����ڵ�JedisPool
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
	 * ��ȡ������Ҳ����ڵ�JedisPool
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
//	 * �ж�����JedisPool�Ƿ��Ѵ���
//	 * ������ڣ�ֱ��ȡ
//	 * ��������ڣ�����ʵ����
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
	 * �ж�����JedisSentinelPool�Ƿ��Ѵ���
	 * ������ڣ�ֱ��ȡ
	 * ��������ڣ�����ʵ����
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
	 * ��ȡkey���еĳ���
	 * ��ͨ��getType��ȡkey��������
	 * Ȼ�����getLength������ѯ����
	 * @param Jedis 
	 * @param key
	 * @return long
	 * */
	public Set<String> getKeys(JedisSentinelPool jp , String key){
		Jedis jedis = jp.getResource();          //ʵ����jedis��ͨ��jedis����redis
		Set<String> set = jedis.keys(key);
		jp.returnResource(jedis);
		return set;
	}
	
	
	/**
	 * ��ȡSentinel����Ϣ
	 * 
	 * @param JedisSentinelPool 
	 * 						Sentinel����
	 * @return String
	 * 						����info��Ϣ
	 * */
	public String getInfo(JedisSentinelPool jp){
		Jedis jedis = jp.getResource();          //ʵ����jedis��ͨ��jedis����redis
		String rs = jedis.info();
		jp.returnResource(jedis);
		return rs;
	}
	
	
	/**
	 * ��ȡkey���еĳ���
	 * ��ͨ��getType��ȡkey��������
	 * Ȼ�����getLength������ѯ����
	 * @param Jedis 
	 * @param key
	 * @return long
	 * */
	public long getRedisLength(JedisSentinelPool jp , String key){
		Jedis jedis = jp.getResource();          //ʵ����jedis��ͨ��jedis����redis
		String type = jedis.type(key);
		long l = getLength(type , key , jedis);
		jp.returnResource(jedis);
		return l;
	}
	
	
	/**
	 * ���ݶ������Ͳ�ѯ���г���
	 * @param key
	 * @return long
	 * */
	private long getLength(String type , String key , Jedis jedis){
		long rs = 0;
		try {
			if("String".equals(type)){        //string����
				rs = 1;
			}else if("hash".equals(type)){    //hash����
				rs = jedis.hlen(key);
			}else{
				rs = jedis.llen(key);		  //Ĭ��Ϊlist			
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Loger.Info_log.info("[ERROR]redis���г����ж�ʧ�ܣ�key��" + key);
		}		
		return rs;
	}
}
