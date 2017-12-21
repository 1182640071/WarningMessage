package guodu.net.warning.interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import guodu.net.warning.db.LoadConfigDb;
import guodu.net.warning.entity.Gd_information_configure;
import guodu.net.warning.entity.Gd_warning_class;
import guodu.net.warning.function.CreateMessagesImp;
import guodu.net.warning.function.GetMessage;
import guodu.net.warning.util.ConfigContainer;
import guodu.net.warning.util.ConfigDb;
import guodu.net.warning.util.Loger;

public class HanderAchieveThree implements Hander{
	private static Map< String , String > mapContent = new HashMap< String , String>();
	private static Map< String , Map<String , String > > mapCompar = new HashMap< String , Map<String , String > >();
	private static Map< String , Map<String , String > > mapComparR = new HashMap< String , Map<String , String > >();

	public void exe(Gd_information_configure A) {
		CreateMessage cm = new CreateMessagesImp();
		List<String> messages = cm.getMessage(A); //填补信息模版
		StringBuffer content =  new StringBuffer();;
		String key = null;
		Map<String , String> contentMap = new HashMap<String, String>();
		for(String message : messages){	
			//当map中不存在A.getClass_id()：首次执行的任务
			if(!mapComparR.containsKey(A.getClass_id())){
				if(null == exsitContent(message , "《" , "》") || "".equals(exsitContent(message , "《" , "》"))){
					System.out.println("＊＊＊＊＊＊＊＊《》");
					if(null == exsitContent(message , "<" , ">") || "".equals(exsitContent(message , "<" , ">"))){
						mapContent.put(A.getClass_id(), message);
					}else{
						mapContent.put(exsitContent(message , "<" , ">"), message);
					}
				}else{
					mapContent.put(exsitContent(message , "《" , "》"), message);
				}
				
				mapCompar.put(A.getClass_id(), mapContent);
			}else{
			//当map中存在A.getClass_id()：非首次执行的任务，开始比较	
				if(null == exsitContent(message , "《" , "》") || "".equals(exsitContent(message , "《" , "》"))){
					if(mapComparR.get(A.getClass_id()).containsKey(exsitContent(message , "<" , ">") )){
						delMap(message ,mapComparR.get(A.getClass_id()).get(exsitContent(message , "<" , ">")) , A);
					}else{
						delMap(message ,mapComparR.get(A.getClass_id()).get(A.getClass_id()) , A);
					}
				}else{
					contentMap.put(exsitContent(message , "《" , "》"), message);
					if(!mapComparR.get(A.getClass_id()).containsKey(exsitContent(message , "《" , "》"))){
						content.append(" 新增加" + message + "的纪录 ");
						//mapContent.put(exsitContent(message , "《" , "》"), message);	
						mapCompar.get(A.getClass_id()).put(exsitContent(message , "《" , "》"), message);
					}else{
						if(mapComparR.get(A.getClass_id()).containsKey(exsitContent(message , "<" , ">") )){
							delMap(message ,mapComparR.get(A.getClass_id()).get(exsitContent(message , "<" , ">")) , A);
						}else{
							delMap(message ,mapComparR.get(A.getClass_id()).get(A.getClass_id()) , A);
						}
					}						
				}				
			}
		}
		List<String> listString = new ArrayList<String>();
		if(mapComparR.containsKey(A.getClass_id())){
			Set<String> keys = mapComparR.get(A.getClass_id()).keySet();
			Iterator<String> iter = keys.iterator();
			while(iter.hasNext()){
				key = iter.next();
				if(!contentMap.containsKey(key)){
					content.append( mapComparR.get(A.getClass_id()).get(key) + "的纪录的纪录丢失 ");
					listString.add(key);
				}
			}
			for(String keyy : listString){
				mapCompar.get(A.getClass_id()).remove(keyy);
			}
		}
		mapComparR = mapCompar;
		if(!"".equals(content.toString()) && null != content && !"".equals(content)){
			String sql  = ConfigContainer.getGd_mobile_configure() + " where id = '"+ A.getClass_id() + "'";
			List<Object[]> desmobile = LoadConfigDb.loadConfig(sql, "defult"); //获取信息对应的手机号
			for(Object[] o : desmobile){
				int ecode = -1;
				ecode = LoadConfigDb.Save(o[0].toString().trim(),  content.toString() ,A , "defult");
				if(ecode == 0){
					Loger.Info_log.info("[INFO]提交手机号：" + o[0].toString().trim() + "。 提交消息" + content.toString() );
				}else{
					Loger.Info_log.info("[ERROR]提交手机号：" + o[0].toString().trim() + "。 提交消息" + content.toString()  +" 失败！");
				}
			}
		}
		
		try {
			//报警信息发送完毕，更新下一次执行时间
			LoadConfigDb.updateForm(A);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Loger.Info_log.info("[ERROR]next_time更新失败，类型：" + A.getId() );
		}
	}
		
		/**
		 * 判断信息数据是否达到阀值，审查是否符合发送值
		 * @param newMes 新信息
		 * @param oldMes 旧信息
		 * @param Gd_information_configure 
		 * */
		private void delMap(String newMes , String oldMes , Gd_information_configure A){
			int rsNew = getCountAll(newMes , ConfigDb.getMapConfig().get(A.getClass_id()));
			int oldNew = getCountAll(oldMes , ConfigDb.getMapConfig().get(A.getClass_id()));
			String compareSgin = ConfigDb.getMapConfig().get(A.getClass_id()).getLimt_count().split(",")[1].trim(); //比较符号
			int limtCount = Integer.parseInt(ConfigDb.getMapConfig().get(A.getClass_id()).getLimt_count().split(",")[2].trim()); //阀值
			if(sendJudge(rsNew , compareSgin , limtCount) && sendJudge(oldNew , compareSgin , limtCount) && rsNew > oldNew ){
				String sql  = ConfigContainer.getGd_mobile_configure() + " where id = '"+ A.getClass_id() + "'";
				List<Object[]> desmobile = LoadConfigDb.loadConfig(sql, "defult"); //获取信息对应的手机号
		        for(Object[] o : desmobile){
					int ecode = LoadConfigDb.Save(o[0].toString().trim(),  newMes ,A , "defult");
					if(ecode == 0){
						Loger.Info_log.info("[INFO]提交手机号：" + o[0].toString().trim() + "。 提交消息" + newMes);
					}else{
						Loger.Info_log.info("[ERROR]提交手机号：" + o[0].toString().trim() + "。 提交消息" + newMes +" 失败！");
					}
				}
			}
		}
		

	
	   /**
	    * 将信息内容模版中的数据取出来根运算符进行判断
	    * 将取出的数值放入boject[]中
	    * 将取出的值按照运算符计算总数
	    * @param content
	    * @param Gd_warning_class
	    * @param int
	    * */
	   public int getCountAll(String content , Gd_warning_class G){
		   int rs = 0;
    	   try {
			   String operSgin = G.getLimt_count().split(",")[0].trim(); //运算符号
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
			   rs = judgeLimt(list.toArray() , operSgin);
		} catch (Exception e) {
           Loger.Info_log.info("[ERROR]JudgeLimt.getCountAll模版分析出错");
		}
	   return rs;
	   }

	   
	   /**
	    * 将取出的值按照运算符计算总数存储rs中
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
               Loger.Info_log.info("[ERROR]HanderAchieveThree.judgeLimt模版分析出错");
	    	   break;
	       }
		   }
	       return rs;
	   }
	   
	   /**
	    * 最终判断阀值
	    * @param int String int
	    * @return Boolean
	    * */
	   public Boolean sendJudge(int rs , String compareSgin , int limtCount){
		   Boolean flag = false;
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
               Loger.Info_log.info("[ERROR]HanderAchieveThree.sendJudge模版分析出错");
	    	   break;
	       }
	       }
	       return flag;
	   }
	   
	   
	   /**
	    * 判断模版中关键词是否存在
	    * 
	    * */
	   private String exsitContent(String content , String flagA , String flagB){
		   String a = StringUtils.substringBetween(content , flagA , flagB);
		   return a;
	   }
	   
}
