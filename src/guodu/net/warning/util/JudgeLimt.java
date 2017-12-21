package guodu.net.warning.util;


import guodu.net.warning.entity.Gd_warning_class;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;

public class JudgeLimt {
	private Boolean flag = true;
	static private JudgeLimt instance; // 唯一实例
	
	public synchronized static JudgeLimt getInstance() {
		if (instance == null) {
			instance = new JudgeLimt();
		}
		return instance;
	}
	/**
	    * 将信息内容模版中的数据取出来根运算符进行判断
	    * 将取出的数值放入boject[]中
	    * 将取出的值按照运算符计算
	    * 判断是否符合发送要求
	    * @param content
	    * @param Gd_warning_class
	    * */
	   public Boolean getCount(String content , Gd_warning_class G){
		   
       	   try {
			   String operSgin = G.getLimt_count().split(",")[0].trim(); //运算符号
			   String compareSgin = G.getLimt_count().split(",")[1].trim(); //比较符号
			   int limtCount = Integer.parseInt(G.getLimt_count().split(",")[2].trim()); //阀值      	   
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
              Loger.Info_log.info("[ERROR]JudgeLimt.getCount模版分析出错");
		}
   	   return flag;
	   }
	   
	   /**
	    * 更新MessageEx，将取出的值按照运算符计算总数存储rs中
	    * @param Object[] o 结果集
	    * @param operSgin 运算符
	    * @return int 和
	    * */
	   public int judgeLimt(Object[] os , String operSgin){
		   char[] oper = operSgin.toCharArray(); //运算符
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
               Loger.Info_log.info("[ERROR]JudgeLimt.judgeLimt模版分析出错");
	    	   break;
	       }
		   }
	       return rs;
	   }
	   
	   /**
	    * 最终判断阀值，决定是否发送
	    * @param MessageEx
	    * @return Boolean
	    * */
	   public void sendJudge(int rs , String compareSgin , int limtCount){
		   char[] limt_oper = compareSgin.toCharArray(); //阀值判断符
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
               Loger.Info_log.info("[ERROR]JudgeLimt.sendJudge模版分析出错");
	    	   break;
	       }
	       }
	   }
}
