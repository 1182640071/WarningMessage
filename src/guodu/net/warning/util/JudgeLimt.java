package guodu.net.warning.util;


import guodu.net.warning.entity.Gd_warning_class;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;

public class JudgeLimt {
	private Boolean flag = true;
	static private JudgeLimt instance; // Ψһʵ��
	
	public synchronized static JudgeLimt getInstance() {
		if (instance == null) {
			instance = new JudgeLimt();
		}
		return instance;
	}
	/**
	    * ����Ϣ����ģ���е�����ȡ����������������ж�
	    * ��ȡ������ֵ����boject[]��
	    * ��ȡ����ֵ�������������
	    * �ж��Ƿ���Ϸ���Ҫ��
	    * @param content
	    * @param Gd_warning_class
	    * */
	   public Boolean getCount(String content , Gd_warning_class G){
		   
       	   try {
			   String operSgin = G.getLimt_count().split(",")[0].trim(); //�������
			   String compareSgin = G.getLimt_count().split(",")[1].trim(); //�ȽϷ���
			   int limtCount = Integer.parseInt(G.getLimt_count().split(",")[2].trim()); //��ֵ      	   
			   String a = StringUtils.substringBetween(content,"{","}");
			   List<String> list = new ArrayList<String>();
			   while(true){
				   a = StringUtils.substringBetween(content,"{","}");
				   content = StringUtils.substringAfter(content, "}"); 
			   	   if(null == a || "".equals(a)){
				      break;
			       }else{
			     	      list.add(a);
			       }
			   }
			   int rs = judgeLimt(list.toArray() , operSgin);
			   if(flag = true){
				   sendJudge(rs , compareSgin , limtCount);
			   }
		} catch (Exception e) {
              flag = false;
              Loger.Info_log.info("[ERROR]JudgeLimt.getCountģ���������");
		}
   	   return flag;
	   }
	   
	   /**
	    * ����MessageEx����ȡ����ֵ������������������洢rs��
	    * @param Object[] o �����
	    * @param operSgin �����
	    * @return int ��
	    * */
	   public int judgeLimt(Object[] os , String operSgin){
		   char[] oper = operSgin.toCharArray(); //�����
		   int rs = 0;
		   switch(oper[0]){
	       case '+':{	 
	    	    for(Object o : os){
	    	    	rs = rs + Integer.parseInt(o.toString());
	    	    }
		        break;
	       }
	       case '-':{
	    	    int rsu = Integer.parseInt(os[0].toString());
	    	    for(int i = 1 ; i < os.length ; i++){
	    		    rsu = rsu - Integer.parseInt(os[i].toString());
	    	    }
	    	    rs = rsu;
		        break;
	       }
	       default:{
               flag = false;
               Loger.Info_log.info("[ERROR]JudgeLimt.judgeLimtģ���������");
	    	   break;
	       }
		   }
	       return rs;
	   }
	   
	   /**
	    * �����жϷ�ֵ�������Ƿ���
	    * @param MessageEx
	    * @return Boolean
	    * */
	   public void sendJudge(int rs , String compareSgin , int limtCount){
		   char[] limt_oper = compareSgin.toCharArray(); //��ֵ�жϷ�
		   switch(limt_oper[0]){
	       case '>':{	 
	            flag = rs > limtCount;
		        break;
	       }
	       case '<':{
	            flag = rs < limtCount;
		        break;
	       }      
	       default:{
	    	   flag = false;
               Loger.Info_log.info("[ERROR]JudgeLimt.sendJudgeģ���������");
	    	   break;
	       }
	       }
	   }
}
