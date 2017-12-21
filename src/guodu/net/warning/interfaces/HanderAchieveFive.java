package guodu.net.warning.interfaces;

import java.util.List;
import guodu.net.warning.db.LoadConfigDb;
import guodu.net.warning.entity.Gd_information_configure;
import guodu.net.warning.function.CreateMessageRedisImp;
import guodu.net.warning.util.ConfigContainer;
import guodu.net.warning.util.ConfigDb;
import guodu.net.warning.util.JudgeLimt;
import guodu.net.warning.util.Loger;

/**
 * 类型5，具体实现redis队列大小监控
 * 
 * 获取redis连接存入map中
 * db任务表中设置的ip,端口为key，对应的JedisPool为value
 * 每一个JedisPool只实例化一次
 * 获取JedisPool时如果存在此JedisPool，则直接从map中取出用于实例话jedis
 * */
public class HanderAchieveFive implements Hander{
	/**
	 * hander接口具体实现
	 * */
	public void exe(Gd_information_configure A) {
		JudgeLimt JL = JudgeLimt.getInstance();
		CreateMessage cm = new CreateMessageRedisImp();
		List<String> messages = cm.getMessage(A);
		for(String message : messages){
			Boolean flag = JL.getCount(message , ConfigDb.getMapConfig().get(A.getClass_id()));//判断信息是否需要发送
			if(flag){
				String sql  = ConfigContainer.getGd_mobile_configure() + " where id = '"+ A.getClass_id() + "'";
				List<Object[]> desmobile = LoadConfigDb.loadConfig(sql, "defult"); //获取信息对应的手机号
		        for(Object[] o : desmobile){
					int ecode = LoadConfigDb.Save(o[0].toString().trim(),  message ,A , "defult");
					if(ecode == 0){
						Loger.Info_log.info("[INFO]提交手机号：" + o[0].toString().trim() + "。 提交消息" + message);
					}else{
						Loger.Info_log.info("[ERROR]提交手机号：" + o[0].toString().trim() + "。 提交消息" + message +" 失败！");
					}
				}	
			}
		}		
		try {
			LoadConfigDb.updateForm(A);                                          //报警信息发送完毕，更新下一次执行时间
		} catch (Exception e) {
			e.printStackTrace();
			Loger.Info_log.info("[ERROR]next_time更新失败，类型：" + A.getId() );
		}
	}
	
	public static void main(String[] args){
		Gd_information_configure a = new Gd_information_configure();
		a.setSql("192.168.1.140,6379,qq");
		new HanderAchieveFive().exe(a);
	}
	
}
