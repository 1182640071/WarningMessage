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
		List<String> messages = cm.getMessage(A); //���Ϣģ��
		StringBuffer content =  new StringBuffer();;
		String key = null;
		Map<String , String> contentMap = new HashMap<String, String>();
		for(String message : messages){	
			//��map�в�����A.getClass_id()���״�ִ�е�����
			if(!mapComparR.containsKey(A.getClass_id())){
				if(null == exsitContent(message , "��" , "��") || "".equals(exsitContent(message , "��" , "��"))){
					System.out.println("��������������������");
					if(null == exsitContent(message , "<" , ">") || "".equals(exsitContent(message , "<" , ">"))){
						mapContent.put(A.getClass_id(), message);
					}else{
						mapContent.put(exsitContent(message , "<" , ">"), message);
					}
				}else{
					mapContent.put(exsitContent(message , "��" , "��"), message);
				}
				
				mapCompar.put(A.getClass_id(), mapContent);
			}else{
			//��map�д���A.getClass_id()�����״�ִ�е����񣬿�ʼ�Ƚ�	
				if(null == exsitContent(message , "��" , "��") || "".equals(exsitContent(message , "��" , "��"))){
					if(mapComparR.get(A.getClass_id()).containsKey(exsitContent(message , "<" , ">") )){
						delMap(message ,mapComparR.get(A.getClass_id()).get(exsitContent(message , "<" , ">")) , A);
					}else{
						delMap(message ,mapComparR.get(A.getClass_id()).get(A.getClass_id()) , A);
					}
				}else{
					contentMap.put(exsitContent(message , "��" , "��"), message);
					if(!mapComparR.get(A.getClass_id()).containsKey(exsitContent(message , "��" , "��"))){
						content.append(" ������" + message + "�ļ�¼ ");
						//mapContent.put(exsitContent(message , "��" , "��"), message);	
						mapCompar.get(A.getClass_id()).put(exsitContent(message , "��" , "��"), message);
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
					content.append( mapComparR.get(A.getClass_id()).get(key) + "�ļ�¼�ļ�¼��ʧ ");
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
			List<Object[]> desmobile = LoadConfigDb.loadConfig(sql, "defult"); //��ȡ��Ϣ��Ӧ���ֻ���
			for(Object[] o : desmobile){
				int ecode = -1;
				ecode = LoadConfigDb.Save(o[0].toString().trim(),  content.toString() ,A , "defult");
				if(ecode == 0){
					Loger.Info_log.info("[INFO]�ύ�ֻ��ţ�" + o[0].toString().trim() + "�� �ύ��Ϣ" + content.toString() );
				}else{
					Loger.Info_log.info("[ERROR]�ύ�ֻ��ţ�" + o[0].toString().trim() + "�� �ύ��Ϣ" + content.toString()  +" ʧ�ܣ�");
				}
			}
		}
		
		try {
			//������Ϣ������ϣ�������һ��ִ��ʱ��
			LoadConfigDb.updateForm(A);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Loger.Info_log.info("[ERROR]next_time����ʧ�ܣ����ͣ�" + A.getId() );
		}
	}
		
		/**
		 * �ж���Ϣ�����Ƿ�ﵽ��ֵ������Ƿ���Ϸ���ֵ
		 * @param newMes ����Ϣ
		 * @param oldMes ����Ϣ
		 * @param Gd_information_configure 
		 * */
		private void delMap(String newMes , String oldMes , Gd_information_configure A){
			int rsNew = getCountAll(newMes , ConfigDb.getMapConfig().get(A.getClass_id()));
			int oldNew = getCountAll(oldMes , ConfigDb.getMapConfig().get(A.getClass_id()));
			String compareSgin = ConfigDb.getMapConfig().get(A.getClass_id()).getLimt_count().split(",")[1].trim(); //�ȽϷ���
			int limtCount = Integer.parseInt(ConfigDb.getMapConfig().get(A.getClass_id()).getLimt_count().split(",")[2].trim()); //��ֵ
			if(sendJudge(rsNew , compareSgin , limtCount) && sendJudge(oldNew , compareSgin , limtCount) && rsNew > oldNew ){
				String sql  = ConfigContainer.getGd_mobile_configure() + " where id = '"+ A.getClass_id() + "'";
				List<Object[]> desmobile = LoadConfigDb.loadConfig(sql, "defult"); //��ȡ��Ϣ��Ӧ���ֻ���
		        for(Object[] o : desmobile){
					int ecode = LoadConfigDb.Save(o[0].toString().trim(),  newMes ,A , "defult");
					if(ecode == 0){
						Loger.Info_log.info("[INFO]�ύ�ֻ��ţ�" + o[0].toString().trim() + "�� �ύ��Ϣ" + newMes);
					}else{
						Loger.Info_log.info("[ERROR]�ύ�ֻ��ţ�" + o[0].toString().trim() + "�� �ύ��Ϣ" + newMes +" ʧ�ܣ�");
					}
				}
			}
		}
		

	
	   /**
	    * ����Ϣ����ģ���е�����ȡ����������������ж�
	    * ��ȡ������ֵ����boject[]��
	    * ��ȡ����ֵ�����������������
	    * @param content
	    * @param Gd_warning_class
	    * @param int
	    * */
	   public int getCountAll(String content , Gd_warning_class G){
		   int rs = 0;
    	   try {
			   String operSgin = G.getLimt_count().split(",")[0].trim(); //�������
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
           Loger.Info_log.info("[ERROR]JudgeLimt.getCountAllģ���������");
		}
	   return rs;
	   }

	   
	   /**
	    * ��ȡ����ֵ������������������洢rs��
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
               Loger.Info_log.info("[ERROR]HanderAchieveThree.judgeLimtģ���������");
	    	   break;
	       }
		   }
	       return rs;
	   }
	   
	   /**
	    * �����жϷ�ֵ
	    * @param int String int
	    * @return Boolean
	    * */
	   public Boolean sendJudge(int rs , String compareSgin , int limtCount){
		   Boolean flag = false;
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
               Loger.Info_log.info("[ERROR]HanderAchieveThree.sendJudgeģ���������");
	    	   break;
	       }
	       }
	       return flag;
	   }
	   
	   
	   /**
	    * �ж�ģ���йؼ����Ƿ����
	    * 
	    * */
	   private String exsitContent(String content , String flagA , String flagB){
		   String a = StringUtils.substringBetween(content , flagA , flagB);
		   return a;
	   }
	   
}
