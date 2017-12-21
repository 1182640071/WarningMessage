package guodu.net.warning.interfaces;

public abstract class CreateMessageEx implements CreateMessage{
	
    /**
     * �ģ��
     * 
     * @param Object[] o
     * 					ƴ����Ϣ
     * @param String model
     * 					����ģ��
     * @return string	
     * 					����
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
     * �ϲ�2������
     * 
     * @param Object[] a
     * 				����
     * @param Object[] b
     * 				����
     * @return Object[]
     * 				�������
     * */
     public static Object[] concat(Object[] a, Object[] b) {  
    	 Object[] c= new Object[a.length+b.length];  
    	 //��һ����Ҫ���Ƶ����飬�ڶ����Ǵ�Ҫ���Ƶ�����ĵڼ�����ʼ���������Ǹ��Ƶ��ǣ��ĸ��Ǹ��Ƶ�������ڼ�����ʼ�����һ���Ǹ��Ƴ���
    	 System.arraycopy(a, 0, c, 0, a.length);  
    	 System.arraycopy(b, 0, c, a.length, b.length);  
    	 return c;  
    	}  

}
