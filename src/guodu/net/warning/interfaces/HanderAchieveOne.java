package guodu.net.warning.interfaces;

import java.util.List;

import guodu.net.warning.db.LoadConfigDb;
import guodu.net.warning.entity.Gd_information_configure;
import guodu.net.warning.function.CreateMessageImp;
import guodu.net.warning.util.ConfigContainer;
import guodu.net.warning.util.Loger;


public class HanderAchieveOne implements Hander{
	/**
	 * �����޲ι��죬newInstance��������
	 * */
	public HanderAchieveOne(){	
		
	}
	/**
	 * �õ� �� ���� ������Ϣ
	 * @param Gd_information_configure ���ñ���Ϣ
	 * */
	public void exe(Gd_information_configure A) {
		String message = null;
		List<Object[]> desmobile = null;
		CreateMessage cm = new CreateMessageImp();
		try {
			List<String> rs = cm.getMessage(A);
			message = rs.get(0);
			String sql  = ConfigContainer.getGd_mobile_configure() + " where id = '"+ A.getClass_id() + "'";
			desmobile = LoadConfigDb.loadConfig(sql, "defult");
            for(Object[] o : desmobile){
				int ecode = LoadConfigDb.Save(o[0].toString().trim(),  message ,A , "defult");
				if(ecode == 0){
					Loger.Info_log.info("[INFO]�ύ�ֻ��ţ�" + o[0].toString().trim() + "�� �ύ��Ϣ" + message);
				}else{
					Loger.Info_log.info("[ERROR]�ύ�ֻ��ţ�" + o[0].toString().trim() + "�� �ύ��Ϣ" + message +" ʧ�ܣ�");
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Loger.Info_log.info("[ERROR]΢�ŷ���ʧ��,ʧ�����ͣ�" + A.getId() );
		}	
		try {
			//������Ϣ������ϣ�������һ��ִ��ʱ��
			LoadConfigDb.updateForm(A);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Loger.Info_log.info("[ERROR]next_time����ʧ�ܣ����ͣ�" + A.getId() );
		}
	}
}