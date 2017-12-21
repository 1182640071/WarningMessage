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
	 * 报警信息处理线程
	 * @param grp 线程池 
	 * @param thredName 线程名
	 * @param A 配置信息
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
			//反射方式，根据class文件名实例化class对象
			c = Class.forName(ConfigDb.getMapConfig().get(A.getClass_id()).getClass_file());
			met = c.getMethod("exe" , Gd_information_configure.class);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Loger.Info_log.info("[ERROR]class实例化失败，请检查数据库中class文件设置");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    try {
			met.invoke(c.newInstance() , A);  //执行class文件中的exe(Gd_information_configure a)方法
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			Loger.Info_log.info("[ERROR]class运行失败，请检查数据库中class文件设置");
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
