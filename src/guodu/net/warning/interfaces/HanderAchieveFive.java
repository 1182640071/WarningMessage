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
 * ����5������ʵ��redis���д�С���
 * 
 * ��ȡredis���Ӵ���map��
 * db����������õ�ip,�˿�Ϊkey����Ӧ��JedisPoolΪvalue
 * ÿһ��JedisPoolֻʵ����һ��
 * ��ȡJedisPoolʱ������ڴ�JedisPool����ֱ�Ӵ�map��ȡ������ʵ����jedis
 * */
public class HanderAchieveFive implements Hander{
	/**
	 * hander�ӿھ���ʵ��
	 * */
	public void exe(Gd_information_configure A) {
		JudgeLimt JL = JudgeLimt.getInstance();
		CreateMessage cm = new CreateMessageRedisImp();
		List<String> messages = cm.getMessage(A);
		for(String message : messages){
			Boolean flag = JL.getCount(message , ConfigDb.getMapConfig().get(A.getClass_id()));//�ж���Ϣ�Ƿ���Ҫ����
			if(flag){
				String sql  = ConfigContainer.getGd_mobile_configure() + " where id = '"+ A.getClass_id() + "'";
				List<Object[]> desmobile = LoadConfigDb.loadConfig(sql, "defult"); //��ȡ��Ϣ��Ӧ���ֻ���
		        for(Object[] o : desmobile){
					int ecode = LoadConfigDb.Save(o[0].toString().trim(),  message ,A , "defult");
					if(ecode == 0){
						Loger.Info_log.info("[INFO]�ύ�ֻ��ţ�" + o[0].toString().trim() + "�� �ύ��Ϣ" + message);
					}else{
						Loger.Info_log.info("[ERROR]�ύ�ֻ��ţ�" + o[0].toString().trim() + "�� �ύ��Ϣ" + message +" ʧ�ܣ�");
					}
				}	
			}
		}		
		try {
			LoadConfigDb.updateForm(A);                                          //������Ϣ������ϣ�������һ��ִ��ʱ��
		} catch (Exception e) {
			e.printStackTrace();
			Loger.Info_log.info("[ERROR]next_time����ʧ�ܣ����ͣ�" + A.getId() );
		}
	}
	
	public static void main(String[] args){
		Gd_information_configure a = new Gd_information_configure();
		a.setSql("192.168.1.140,6379,qq");
		new HanderAchieveFive().exe(a);
	}
	
}
