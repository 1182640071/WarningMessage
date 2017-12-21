package guodu.net.warning.util;

public class RecordThreadSituation {
    private static int count = 0; //用于纪录正在启动中的线程数量

    public static void startThread(String flag){
    	if(flag == "1"){
    		count++;
    	}else if(flag == "0"){
    		count--;
    	}
    } 
    
    
	public static int getCount() {
		return count;
	}

	public static void setCount(int count) {
		RecordThreadSituation.count = count;
	}
    
    
}
