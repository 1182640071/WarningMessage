package guodu.net.warning.thread;

import java.text.SimpleDateFormat;
import java.util.Date;
import guodu.net.warning.db.LoadConfigDb;
import guodu.net.warning.util.ConfigContainer;
import guodu.net.warning.util.ConfigDb;
import guodu.net.warning.util.Loger;

/**
 *�����߳�
 *���ڼ������ݿ��еĸ������ñ�
 *�ֱ����gd_configure  gd_information_configure  gd_mobile_configure�������ñ� 
 * */
public class LoadConfigThread extends Thread{
	final String sql[] = ConfigContainer.getSqlMonitor().split(";");
    public LoadConfigThread(){
    	
    }
    public void run(){
    	Loger.Info_log.info("[INFO]���ñ�����߳�����");
    	while(true){
    		try {
				sleep(Long.parseLong(ConfigContainer.getTimeRun()));
				ConfigDb.load();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Loger.Info_log.info("[ERROR]���ñ�����쳣");
			}      		
    		try {
    			String date = null;
    			String sqlex = null;
				for(int i = 0 ; i < sql.length ; i ++){
					date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date().getTime());
					sqlex = "to_date('"+date+"' , 'yyyymmddhh24miss')";
					LoadConfigDb.updateOper(sql[i].replace("sysdate", sqlex));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Loger.Info_log.info("[ERROR]���ñ�state,next_time��ء������쳣");
			}
    	}
    }
}
