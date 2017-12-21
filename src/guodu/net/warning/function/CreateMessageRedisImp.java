package guodu.net.warning.function;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.JedisSentinelPool;

import guodu.net.warning.entity.Gd_information_configure;
import guodu.net.warning.interfaces.CreateMessageEx;
import guodu.net.warning.util.ConfigDb;

public class CreateMessageRedisImp extends CreateMessageEx{

	/**
	 * ����������Ϣ��ƥ����Ϣģ��󷵻�
	 * @param Gd_information_configure ���ñ���Ϣ
	 * @return ��Ϣ
	 * */
	@Override
    public List<String> getMessage(Gd_information_configure A){		
		String[] parm = A.getSql().split(",");                                   //sql�ֶ�������Ϣ
		String redisInfo = parm[2].trim() + ',' + parm[3].trim();                //�û���,������ϳ�
		Set<String> sentinel = new HashSet<String>();
		sentinel.add(parm[0].trim() + ":" + parm[1].trim());
		RedisPool rp = RedisPool.getInstance();
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
		js.close();
    	return listRs;
    }

}
