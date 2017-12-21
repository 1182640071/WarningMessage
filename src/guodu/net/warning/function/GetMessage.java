package guodu.net.warning.function;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import redis.clients.jedis.JedisSentinelPool;
import guodu.net.warning.db.LoadConfigDb;
import guodu.net.warning.entity.Gd_information_configure;
import guodu.net.warning.util.ConfigDb;
import guodu.net.warning.util.Loger;

public class GetMessage {
	/**
	 * 接收配置信息，匹配消息模版后返回
	 * @param Gd_information_configure 配置表信息
	 * @return 信息
	 * */
    public static String getMessage(Gd_information_configure A){
    	Object[] list = null;
    	Object[] result = null;
    	String sql[] = A.getSql().split("!\\|");
    	for(int i = 0 ; i < sql.length ; i ++){
    		try {
				list = LoadConfigDb.loadConfig(sql[i] ,  A.getDb()).get(0);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Loger.Info_log.info("[ERROR]sql语句执行失败，class_id：" + A.getId());
				return ConfigDb.getMapConfig().get(A.getClass_id()).getModel();
			}
    		if(list == null || list.length == 0){
    			continue;
    		}else if(result == null || result.length == 0){
    			result = list;
    		}else{
        		result = concat(result , list);   			
    		}
    	}
    	String rs = addInfo(result , ConfigDb.getMapConfig().get(A.getClass_id()).getModel());
    	return rs;
    }
    
    
    /**
	 * 第四种模式HanderAchieveFour
	 * 接收配置信息，匹配消息模版后返回
	 * @param Gd_information_configure 配置表信息
	 * @return 信息
	 * */
    public static List<String> getMessages(Gd_information_configure A){
    	List<Object[]> list = null;
    	List<String> message = new ArrayList<String>();
    	String sql[] = A.getSql().split("!\\|");
    		try {
				list = LoadConfigDb.loadConfig(sql[0] ,  A.getDb());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Loger.Info_log.info("[ERROR]sql语句执行失败，class_id：" + A.getId());
				return null;
			}
    		if(list == null || list.size() == 0){
    			return null;
    		}
    	for(Object[] result : list){
        	String rs = addInfo(result , ConfigDb.getMapConfig().get(A.getClass_id()).getModel());
        	message.add(rs);
    	}	
    	return message;
    }
    
    
	/**
	 * 接收配置信息，匹配消息模版后返回
	 * @param Gd_information_configure 配置表信息
	 * @return 信息
	 * */
    public static List<String> getMessageRedis(Gd_information_configure A){		
		String[] parm = A.getSql().split(",");                                   //sql字段配置信息
		String redisInfo = parm[2].trim() + ',' + parm[3].trim();                //用户名,密码组合成
		Set<String> sentinel = new HashSet<String>();
		sentinel.add(parm[0].trim() + ":" + parm[1].trim());
		RedisPool rp = RedisPool.getInstance();
//1		JedisPool js =  rp.getJedis(redisInfo);                                  //实例话JedisPool
		JedisSentinelPool js =  rp.getJedisSentinelPool(redisInfo , sentinel);                                  
		List<Object> list = new ArrayList<Object>();
		List<String> listRs = new ArrayList<String>();
		long rs = 0 ;
		String rsult = "";
		for(int i = 4 ; i < parm.length ; i ++){
			if(parm[i].contains("*")){
				Set<String> set = rp.getKeys(js, parm[i]);
				for(String s : set){
					list.add(s);
					list.add(rp.getRedisLength(js , s));
					rsult = addInfo(list.toArray() , ConfigDb.getMapConfig().get(A.getClass_id()).getModel());
					list.clear();
					listRs.add(rsult);
				}
			}else{
				rs = rp.getRedisLength(js , parm[i]);
				list.add(parm[i]);
				list.add(rs);
				rsult = addInfo(list.toArray() , ConfigDb.getMapConfig().get(A.getClass_id()).getModel());
				list.clear();
				listRs.add(rsult);
			}
		}
    	return listRs;
    }
    
    
    /**
	 * 接收配置信息，匹配消息模版后返回
	 * @param Gd_information_configure 配置表信息
	 * @return 信息
	 * */
    public static List<String> getMessageSentinel1(Gd_information_configure A){
    	List<String> listR = new ArrayList<String>();
    	String[] ipPort = A.getSql().split("!\\|");
    	Set<String> sentinel = new HashSet<String>();
    	for(String ipP : ipPort[0].split(",")){
    		sentinel.add(ipP);
    	}
		RedisPool rp = RedisPool.getInstance();
		for(String SentinelInfo : ipPort[1].split("\\|\\|")){
			List<String> list = new ArrayList<String>();
			int index = 0;
			StringBuffer sb = new StringBuffer();
			JedisSentinelPool js =  rp.getJedisSentinelPool(SentinelInfo , sentinel);
			list.add(String.valueOf(js.getCurrentHostMaster()));
			String rs = rp.getInfo(js);
			for(String information : rs.split("\n")){
				if(information.startsWith("slave") && information.indexOf("state=online,") != -1){
					index++;
					sb.append(information.split(",state=online")[0]);
				}
			}
			list.add(String.valueOf(index));
			list.add(sb.toString());
			String rsu = addInfo(list.toArray() , ConfigDb.getMapConfig().get(A.getClass_id()).getModel());
			list.clear();
			listR.add(rsu);
		}                            
    	return listR;
    }
    
    
    /**
     * 填补模版
     * 
     * */
    public static String addInfo(Object[] o , String model){
    	String target = null;
    	for(int i = 0 ; i < o.length ; i++){
    		target = "s" + (i+1);
    		model = model.replace(target, o[i].toString());
    	}
    	return model;
    }
    
    /**
     * 合并2个数组
     * */
     public static Object[] concat(Object[] a, Object[] b) {  
    	 Object[] c= new Object[a.length+b.length];  
    	 //第一个是要复制的数组，第二个是从要复制的数组的第几个开始，第三个是复制到那，四个是复制到的数组第几个开始，最后一个是复制长度
    	 System.arraycopy(a, 0, c, 0, a.length);  
    	 System.arraycopy(b, 0, c, a.length, b.length);  
    	 return c;  
    	}  
    
}
