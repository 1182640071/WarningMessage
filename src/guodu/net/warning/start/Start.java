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
 * 2016��1��22�� wml 
 * 
 * HanderAchieveOne.java ֱ�ӱ����࣬�������ֱ��ƥ��ģ��Ȼ����
 *   ������������С�[s1]�չ����Ͷ���[s2]�����ɹ�[s3]�����������ۼƷ���[s4]�����ɹ�[s5]����
 * 
 * HanderAchieveTwo.java ��ֵ�ж��࣬�������ƥ��ģ�棬����{}�е������жϷ�ֵ +,<,20 "+":{}�е���ֵ��ӣ�"<":��Ӻ��ֵ��"20"�Ƚϣ����������ٷ���
 *   ������һСʱ��������ͨ�ɹ���Ϊ{s1}������[s2]���ɹ�[s3].     +,<,0.7
 * 
 * HanderAchieveThree.java �ж�2��ʱ��β�ѯ�����Ƿ��ж�ʧ���������������жϹؼ����жϷ�ֵ +,<,20  "����"Ϊkey�����ʧ���������ᱨ����"<>"Ϊkey���������ʧ������ "{}"��ֵ
 *   ����s1,��s2�����s3��s4 ���� s1,<s2�����s3>{s4}
 *   
 * HanderAchieveFour.java ͬ�ڶ����ͣ�������з���ֵ�����жϹ���   
 *   ����[s1]���ƶ���ѹ{s2}����ͨ��ѹ{s3}�����Ż�ѹ{s4}������ֵ  +,>,700000
 * 
 * HanderAchieveFive.java redis���н�ѹ��ѯ �������ƥ��ģ���жϺ���  
 *   ���������ϲ�redis��ѹ{s1}����   +,>,2000  sql�ֶ�����192.168.1.140,6379,list:sms:ccbsms:realtime:mt:1,list:sms:ccbsms:realtime:mt:2
 * */
public class Start {
    public static void main(String[] args){    	
		try {
			//���������ļ�
			ConfigContainer.load();
			//�������ݿ����ñ�
			ConfigDb.load();  	    
			//�߳��鶨��
			ThreadGroup AchieveOne = new ThreadGroup("AchieveOne");
            //���������ļ������̣߳�ÿ��һ��ʱ�����һ����Ϣ����
 	        new LoadConfigThread().start();
 	        //�����̳߳ؼ���߳�
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
						    //������Ϣ�����߳�
					    	if("".equals(ConfigDb.getListConfig().get(i).getStart_time()) || null == ConfigDb.getListConfig().get(i).getStart_time() ||"".equals(ConfigDb.getListConfig().get(i).getEnd_time()) || null == ConfigDb.getListConfig().get(i).getEnd_time())
					    	{
					    		ConfigDb.getMapStuts().put(ConfigDb.getListConfig().get(i).getId(), false);//state��Ϊ������״̬
								new ThreadAchieveOne(AchieveOne , "one" + time + i , ConfigDb.getListConfig().get(i));
								Loger.Info_log.info("[info]ִ�У�" + ConfigDb.getListConfig().get(i).getId());
					    	}else{
					    		int start = Integer.parseInt(ConfigDb.getListConfig().get(i).getStart_time());
					    		int end = Integer.parseInt(ConfigDb.getListConfig().get(i).getEnd_time());
					    		if(run_time >= start && run_time <= end){
						    		ConfigDb.getMapStuts().put(ConfigDb.getListConfig().get(i).getId(), false);//state��Ϊ������״̬
					    			new ThreadAchieveOne(AchieveOne , "one" + time + i , ConfigDb.getListConfig().get(i));
					    			//new ThreadAchieveOne(AchieveOne , "one"  , ConfigDb.getListConfig().get(0));
									Loger.Info_log.info("[info]ִ�У�" + ConfigDb.getListConfig().get(i).getId());
					    		}else{
					    			Loger.Info_log.info("[info]�����涨ʱ�䣬δִ�У�" + ConfigDb.getListConfig().get(i).getId());
					    			try {
					    				//������Ϣ������ϣ�������һ��ִ��ʱ��
					    				LoadConfigDb.updateForm(ConfigDb.getListConfig().get(i));
					    			} catch (Exception e) {
					    				e.printStackTrace();
					    				Loger.Info_log.info("[ERROR]next_time����ʧ�ܣ����ͣ�" + ConfigDb.getListConfig().get(i).getId() );
					    			}
					    		}
					    	}
					    }else{
					    	for(String desmobile : ConfigContainer.getDesmobile()){
						    	LoadConfigDb.Save(desmobile.trim(),  "�ۼ�ر����ݼ�س��򳬹�����߳��������߳�id��" + ConfigDb.getListConfig().get(i).getId() , ConfigDb.getListConfig().get(i) , "defult");
					    	}					    	
					    	try {
			    				//������Ϣ������ϣ�������һ��ִ��ʱ��
			    				LoadConfigDb.updateForm(ConfigDb.getListConfig().get(i));
			    			} catch (Exception e) {
			    				e.printStackTrace();
			    				Loger.Info_log.info("[ERROR]next_time����ʧ�ܣ����ͣ�" + ConfigDb.getListConfig().get(i).getId() );
			    			}
					    }
					}
				}
		    	Thread.sleep(Long.parseLong(ConfigContainer.getSleepTime()));
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			Loger.Info_log.info("[ERROR]�����쳣,��ֹ���򡣡���" , e);
		}
    }
}
