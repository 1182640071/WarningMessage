package guodu.net.warning.interfaces;

import java.util.List;

import guodu.net.warning.db.LoadConfigDb;
import guodu.net.warning.entity.Gd_information_configure;
import guodu.net.warning.function.CreateMessagesImp;
import guodu.net.warning.function.GetMessage;
import guodu.net.warning.util.ConfigContainer;
import guodu.net.warning.util.ConfigDb;
import guodu.net.warning.util.JudgeLimt;
import guodu.net.warning.util.Loger;

public class HanderAchieveFour implements Hander{
	/**
	 * �����޲ι��죬newInstance��������
	 * */
	public HanderAchieveFour(){	
		
	}
	@Override
	public void exe(Gd_information_configure A) {
		JudgeLimt JL = JudgeLimt.getInstance();
		CreateMessage cm = new CreateMessagesImp();
		List<String> messages = cm.getMessage(A); //���Ϣģ��
		for(String mes : messages)
		{
			Boolean flag = JL.getCount(mes , ConfigDb.getMapConfig().get(A.getClass_id()));//�ж���Ϣ�Ƿ���Ҫ����
			if(flag){
				String sql  = ConfigContainer.getGd_mobile_configure() + " where id = '"+ A.getClass_id() + "'";
				List<Object[]> desmobile = LoadConfigDb.loadConfig(sql, "defult"); //��ȡ��Ϣ��Ӧ���ֻ���
		        for(Object[] o : desmobile){
					int ecode = LoadConfigDb.Save(o[0].toString().trim(),  mes ,A , "defult");
					if(ecode == 0){
						Loger.Info_log.info("[INFO]�ύ�ֻ��ţ�" + o[0].toString().trim() + "�� �ύ��Ϣ" + mes);
					}else{
						Loger.Info_log.info("[ERROR]�ύ�ֻ��ţ�" + o[0].toString().trim() + "�� �ύ��Ϣ" + mes +" ʧ�ܣ�");
					}
				}	
			}	
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
