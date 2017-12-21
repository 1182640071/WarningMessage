package guodu.net.warning.start;

import java.text.SimpleDateFormat;
import java.util.Date;

import guodu.net.warning.db.LoadConfigDb;
import guodu.net.warning.thread.LoadConfigThread;
import guodu.net.warning.thread.ThreadAchieveOne;
import guodu.net.warning.thread.ThreadMonitor;
import guodu.net.warning.util.ConfigContainer;
import guodu.net.warning.util.ConfigDb;
import guodu.net.warning.util.Loger;
/**
 * 2016年1月22日 wml 
 * 
 * HanderAchieveOne.java 直接报量类，查出数据直接匹配模版然后发送
 *   例：【天津银行】[s1]日共发送短信[s2]条，成功[s3]条，当月已累计发送[s4]条，成功[s5]条。
 * 
 * HanderAchieveTwo.java 阀值判断类，查出数据匹配模版，根据{}中的数据判断阀值 +,<,20 "+":{}中的数值相加，"<":相加后的值于"20"比较，符合条件再发送
 *   例：近一小时，建行联通成功率为{s1}，总数[s2]，成功[s3].     +,<,0.7
 * 
 * HanderAchieveThree.java 判断2个时间段查询内容是否有丢失或者新增并可以判断关键数判断阀值 +,<,20  "《》"为key如果丢失或者新增会报警，"<>"为key如果新增或丢失不报警 "{}"阀值
 *   例：s1,《s2服务端s3》s4 或者 s1,<s2服务端s3>{s4}
 *   
 * HanderAchieveFour.java 同第二类型，加入多行返回值逐行判断功能   
 *   例：[s1]表，移动积压{s2}，联通积压{s3}，电信积压{s4}超过阈值  +,>,700000
 * 
 * HanderAchieveFive.java redis队列解压查询 查出数据匹配模版判断后发送  
 *   例：建行上层redis积压{s1}条。   +,>,2000  sql字段配置192.168.1.140,6379,list:sms:ccbsms:realtime:mt:1,list:sms:ccbsms:realtime:mt:2
 * */
public class Start {
    public static void main(String[] args){    	
		try {
			//加载配置文件
			ConfigContainer.load();
			//加载数据库配置表
			ConfigDb.load();  	    
			//线程组定义
			ThreadGroup AchieveOne = new ThreadGroup("AchieveOne");
            //启动配置文件加载线程，每个一段时间进行一次信息加载
 	        new LoadConfigThread().start();
 	        //启动线程池监控线程
 	        new ThreadMonitor().start();
			while(true){
				String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date().getTime());
				int run_time  =Integer.parseInt(new SimpleDateFormat("HHmm").format(new Date().getTime()));
				for(int i = 0 ; i < ConfigDb.getListConfig().size() ; i++){ 
					if(null == ConfigDb.getMapStuts() || !ConfigDb.getMapStuts().containsKey(ConfigDb.getListConfig().get(i).getId())){
						ConfigDb.getMapStuts().put(ConfigDb.getListConfig().get(i).getId(), true);
					}
					if(Long.parseLong(time) >= Long.parseLong(ConfigDb.getListConfig().get(i).getNext_time()) && ConfigDb.getMapStuts().get(ConfigDb.getListConfig().get(i).getId())){
					    if(ThreadMonitor.getMonitor().size() < ConfigContainer.getThreadCount()){
						    //报警信息处理线程
					    	if("".equals(ConfigDb.getListConfig().get(i).getStart_time()) || null == ConfigDb.getListConfig().get(i).getStart_time() ||"".equals(ConfigDb.getListConfig().get(i).getEnd_time()) || null == ConfigDb.getListConfig().get(i).getEnd_time())
					    	{
					    		ConfigDb.getMapStuts().put(ConfigDb.getListConfig().get(i).getId(), false);//state改为已启动状态
								new ThreadAchieveOne(AchieveOne , "one" + time + i , ConfigDb.getListConfig().get(i));
								Loger.Info_log.info("[info]执行：" + ConfigDb.getListConfig().get(i).getId());
					    	}else{
					    		int start = Integer.parseInt(ConfigDb.getListConfig().get(i).getStart_time());
					    		int end = Integer.parseInt(ConfigDb.getListConfig().get(i).getEnd_time());
					    		if(run_time >= start && run_time <= end){
						    		ConfigDb.getMapStuts().put(ConfigDb.getListConfig().get(i).getId(), false);//state改为已启动状态
					    			new ThreadAchieveOne(AchieveOne , "one" + time + i , ConfigDb.getListConfig().get(i));
					    			//new ThreadAchieveOne(AchieveOne , "one"  , ConfigDb.getListConfig().get(0));
									Loger.Info_log.info("[info]执行：" + ConfigDb.getListConfig().get(i).getId());
					    		}else{
					    			Loger.Info_log.info("[info]超出规定时间，未执行：" + ConfigDb.getListConfig().get(i).getId());
					    			try {
					    				//报警信息发送完毕，更新下一次执行时间
					    				LoadConfigDb.updateForm(ConfigDb.getListConfig().get(i));
					    			} catch (Exception e) {
					    				e.printStackTrace();
					    				Loger.Info_log.info("[ERROR]next_time更新失败，类型：" + ConfigDb.getListConfig().get(i).getId() );
					    			}
					    		}
					    	}
					    }else{
					    	for(String desmobile : ConfigContainer.getDesmobile()){
						    	LoadConfigDb.Save(desmobile.trim(),  "［监控报警］监控程序超过最大线程数跳过线程id：" + ConfigDb.getListConfig().get(i).getId() , ConfigDb.getListConfig().get(i) , "defult");
					    	}					    	
					    	try {
			    				//报警信息发送完毕，更新下一次执行时间
			    				LoadConfigDb.updateForm(ConfigDb.getListConfig().get(i));
			    			} catch (Exception e) {
			    				e.printStackTrace();
			    				Loger.Info_log.info("[ERROR]next_time更新失败，类型：" + ConfigDb.getListConfig().get(i).getId() );
			    			}
					    }
					}
				}
		    	Thread.sleep(Long.parseLong(ConfigContainer.getSleepTime()));
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			Loger.Info_log.info("[ERROR]程序异常,终止程序。。。" , e);
		}
    }
}
