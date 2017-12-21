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
 * 线程监控程序，当线程执行时间大于2分钟时，中断线程
 * */
public class ThreadMonitor extends Thread{	
    private static Map<Long , String> monitor = new HashMap<Long , String>();//纪录线程池中的运行线程
    private static ThreadGroup rootThreadGroup = null;  

    public void run(){
    	Loger.Info_log.info("[INFO]线程池监控线程启动");
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
     * 遍历所有线程，判断当线程为线程池中的时判断运行时间
     * 根据运行时间做终止或跳过逻辑
     * @param Thread 线程
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
    	    	Loger.Info_log.info("[ERROR]线程运行时间超时，终止线程id：" + thread.getId());
    			thread.interrupt();
    			monitor.remove(thread.getId());
    		}
    	}
    }
    /**
     * 判断运行时间，超过规定时间返回true
     * @param runTime 运行起始时间
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
			long diff = d2.getTime() - d1.getTime();//这样得到的差值是微秒级别
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
