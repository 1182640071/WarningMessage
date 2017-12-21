package guodu.net.warning.thread;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import guodu.net.warning.entity.Gd_information_configure;
import guodu.net.warning.interfaces.Hander;
import guodu.net.warning.util.ConfigDb;
import guodu.net.warning.util.Loger;

public class ThreadAchieveOne extends Thread {

	
	private Gd_information_configure A;
	/**
	 * ������Ϣ�����߳�
	 * @param grp �̳߳� 
	 * @param thredName �߳���
	 * @param A ������Ϣ
	 * */
	public ThreadAchieveOne(ThreadGroup grp , String thredName , Gd_information_configure A) {
		super(grp, thredName);	
		this.A = A;
		start();
		}	

	public void run(){
		String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date().getTime());
		ThreadMonitor.getMonitor().put(this.getId() , time);
		Class<?> c = null;
		Method met = null;
		try {
			//���䷽ʽ������class�ļ���ʵ����class����
			c = Class.forName(ConfigDb.getMapConfig().get(A.getClass_id()).getClass_file());
			met = c.getMethod("exe" , Gd_information_configure.class);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Loger.Info_log.info("[ERROR]classʵ����ʧ�ܣ��������ݿ���class�ļ�����");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    try {
			met.invoke(c.newInstance() , A);  //ִ��class�ļ��е�exe(Gd_information_configure a)����
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			Loger.Info_log.info("[ERROR]class����ʧ�ܣ��������ݿ���class�ļ�����");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    ThreadMonitor.getMonitor().remove(this.getId());
	}
}
