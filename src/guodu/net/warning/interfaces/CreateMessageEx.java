package guodu.net.warning.interfaces;

public abstract class CreateMessageEx implements CreateMessage{
	
    /**
     * 填补模版
     * 
     * @param Object[] o
     * 					拼接信息
     * @param String model
     * 					短信模版
     * @return string	
     * 					短信
     * */
    public static String addInfo(Object[] o , String model){
    	String target = null;
    	for(int i = 0 ; i < o.length ; i++){
    		target = "s" + (i+1);
    		model = model.replace(target, o[i].toString());
    	}
    	return model;
    }
    
    /**
     * 合并2个数组
     * 
     * @param Object[] a
     * 				数组
     * @param Object[] b
     * 				数组
     * @return Object[]
     * 				结果数组
     * */
     public static Object[] concat(Object[] a, Object[] b) {  
    	 Object[] c= new Object[a.length+b.length];  
    	 //第一个是要复制的数组，第二个是从要复制的数组的第几个开始，第三个是复制到那，四个是复制到的数组第几个开始，最后一个是复制长度
    	 System.arraycopy(a, 0, c, 0, a.length);  
    	 System.arraycopy(b, 0, c, a.length, b.length);  
    	 return c;  
    	}  

}
