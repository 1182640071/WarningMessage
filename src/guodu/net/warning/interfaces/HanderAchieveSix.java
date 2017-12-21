package guodu.net.warning.interfaces;

import guodu.net.warning.db.LoadConfigDb;
import guodu.net.warning.entity.Gd_information_configure;
import guodu.net.warning.function.CreateMessageSentinelImp;
import guodu.net.warning.util.ConfigContainer;
import guodu.net.warning.util.ConfigDb;
import guodu.net.warning.util.JudgeLimt;
import guodu.net.warning.util.Loger;

import java.util.List;

public class HanderAchieveSix implements Hander{
	/**
	 * hander�ӿھ���ʵ��
	 * */
	public void exe(Gd_information_configure A) {
		JudgeLimt JL = JudgeLimt.getInstance();
		CreateMessage cm = new CreateMessageSentinelImp();
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
		String a = "192.168.168.101:2101,192.168.168.102:2102!|mymaster6380,gu0dU51Der";
		System.out.println(a.split("!\\|")[0]);
	}
}
