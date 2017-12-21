package guodu.net.warning.thread;

import java.text.SimpleDateFormat;
import java.util.Date;
import guodu.net.warning.db.LoadConfigDb;
import guodu.net.warning.util.ConfigContainer;
import guodu.net.warning.util.ConfigDb;
import guodu.net.warning.util.Loger;

/**
 *加载线程
 *用于加载数据库中的各种配置表
 *分别包括gd_configure  gd_information_configure  gd_mobile_configure三张配置表 
 * */
public class LoadConfigThread extends Thread{
	final String sql[] = ConfigContainer.getSqlMonitor().split(";");
    public LoadConfigThread(){
    	
    }
    public void run(){
    	Loger.Info_log.info("[INFO]配置表加载线程启动");
    	while(true){
    		try {
				sleep(Long.parseLong(ConfigContainer.getTimeRun()));
				ConfigDb.load();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Loger.Info_log.info("[ERROR]配置表加载异常");
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
				Loger.Info_log.info("[ERROR]配置表state,next_time监控、更新异常");
			}
    	}
    }
}
