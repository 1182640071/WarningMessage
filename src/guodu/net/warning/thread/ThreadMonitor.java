package guodu.net.warning.thread;

import guodu.net.warning.util.ConfigContainer;
import guodu.net.warning.util.Loger;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * �̼߳�س��򣬵��߳�ִ��ʱ�����2����ʱ���ж��߳�
 * */
public class ThreadMonitor extends Thread{	
    private static Map<Long , String> monitor = new HashMap<Long , String>();//��¼�̳߳��е������߳�
    private static ThreadGroup rootThreadGroup = null;  

    public void run(){
    	Loger.Info_log.info("[INFO]�̳߳ؼ���߳�����");
    	while(true){
    		Thread[] threads = getAllThreads( );  
            for ( Thread thread : threads ){
                judgeThread(thread);
            }
        	try {
    			sleep(1000);
    		} catch (InterruptedException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    }
    
    /**
     * ���������̣߳��жϵ��߳�Ϊ�̳߳��е�ʱ�ж�����ʱ��
     * ��������ʱ������ֹ�������߼�
     * @param Thread �߳�
     * */
    public static void judgeThread(Thread thread){
    	Boolean flag = false;
    	if(monitor.get(thread.getId()) == null || "".equals(monitor.get(thread.getId()))){
    		return;
    	}else{
    		flag = judgeTime(monitor.get(thread.getId()));
    		if(flag == false){
    			return;
    		}else{
    	    	Loger.Info_log.info("[ERROR]�߳�����ʱ�䳬ʱ����ֹ�߳�id��" + thread.getId());
    			thread.interrupt();
    			monitor.remove(thread.getId());
    		}
    	}
    }
    /**
     * �ж�����ʱ�䣬�����涨ʱ�䷵��true
     * @param runTime ������ʼʱ��
     * @return Boolean
     * */
    public static Boolean judgeTime(String runTime){
		String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date().getTime());
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");	 
		Date d1 = null;
		Date d2 = null;
		try {
			d1 = df.parse(runTime);
			d2 = df.parse(time);
			long diff = d2.getTime() - d1.getTime();//�����õ��Ĳ�ֵ��΢�뼶��
			long days = diff / (1000 * 60 * 60 * 24);		 
			long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);
			long minutes = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60);
			if(minutes > ConfigContainer.getLimt_time()){
				return true;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
    
    public static Thread getThread( final String name )  
    {  
        if ( name == null )  
            throw new NullPointerException( "Null name" );  
        final Thread[] threads = getAllThreads( );  
        for ( Thread thread : threads )  
            if ( thread.getName( ).equals( name ) )  
                return thread;  
        return null;  
    }  
    
    public static Thread[] getAllThreads( )  
    {  
        final ThreadGroup root = getRootThreadGroup( );  
        final ThreadMXBean thbean =  
            ManagementFactory.getThreadMXBean( );  
        int nAlloc = thbean.getThreadCount( );  
        int n = 0;  
        Thread[] threads = null;  
        do  
        {  
            nAlloc *= 2;  
            threads = new Thread[ nAlloc ];  
            n = root.enumerate( threads, true );  
        } while ( n == nAlloc );  
        return java.util.Arrays.copyOf( threads, n );  
    }  
    
    public static ThreadGroup getRootThreadGroup( )  
    {  
        if ( rootThreadGroup != null )  
            return rootThreadGroup;  
  
        ThreadGroup tg = Thread.currentThread( ).getThreadGroup( );  
        ThreadGroup ptg;  
        while ( (ptg = tg.getParent( )) != null )  
            tg = ptg;  
        rootThreadGroup = tg;  
        return tg;  
    }  
    
	public static Map<Long, String> getMonitor() {
		return monitor;
	}
	
	public static void setMonitor(Map<Long, String> monitor) {
		ThreadMonitor.monitor = monitor;
	}

    
}
