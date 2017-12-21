package guodu.net.warning.function;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.JedisSentinelPool;
import guodu.net.warning.entity.Gd_information_configure;
import guodu.net.warning.interfaces.CreateMessageEx;
import guodu.net.warning.util.ConfigDb;

public class CreateMessageSentinelImp extends CreateMessageEx{
	/**
	 * 接收配置信息，匹配消息模版后返回
	 * @param Gd_information_configure 配置表信息
	 * @return 信息
	 * */
	@Override
    public List<String> getMessage(Gd_information_configure A){
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
			js.close();
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
}
