package guodu.net.warning.function;

import java.util.ArrayList;
import java.util.List;

import guodu.net.warning.db.LoadConfigDb;
import guodu.net.warning.entity.Gd_information_configure;
import guodu.net.warning.interfaces.CreateMessageEx;
import guodu.net.warning.util.ConfigDb;
import guodu.net.warning.util.Loger;

public class CreateMessagesImp extends CreateMessageEx{

	 /**
		 * ������ģʽHanderAchieveFour
		 * ����������Ϣ��ƥ����Ϣģ��󷵻�
		 * @param Gd_information_configure ���ñ���Ϣ
		 * @return ��Ϣ
		 * */
		@Override
	    public List<String> getMessage(Gd_information_configure A){
	    	List<Object[]> list = null;
	    	List<String> message = new ArrayList<String>();
	    	String sql[] = A.getSql().split("!\\|");
	    		try {
					list = LoadConfigDb.loadConfig(sql[0] ,  A.getDb());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Loger.Info_log.info("[ERROR]sql���ִ��ʧ�ܣ�class_id��" + A.getId());
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

}
