package guodu.net.warning.util;

public class RecordThreadSituation {
    private static int count = 0; //���ڼ�¼���������е��߳�����

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
