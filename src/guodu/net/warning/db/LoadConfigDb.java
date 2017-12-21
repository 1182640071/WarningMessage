package guodu.net.warning.db;

import guodu.net.warning.entity.Gd_information_configure;
import guodu.net.warning.util.ConfigDb;
import guodu.net.warning.util.Loger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LoadConfigDb {
	
	private static int number = 1; //���к�
	
	 /**
	  * ��ȡ1��9999����λ����
	  * */
    private synchronized static int getNumber(){
   	if(number > 9999){
   		number = 1;
   	}
   	return number++;
    }
    
    /**
     * ��ȡΨһ��ʾ
     * */
    private synchronized static String getCode(){
   	 String code = String.valueOf(getNumber());
   	 while(code.length() < 4){
       	 StringBuffer sb = new StringBuffer();
   		 sb = sb.append("0").append(code);
   		 code = sb.toString();
   	 }
   	 code = "WX" + code + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date().getTime());
   	 return code;
     }
	
    
    /**
     * ��ƥ��õ�ģ�棬�������߼��ж�ȷ�����͵���Ϣ���������
     * @param mobile �ֻ���
     * @param con    ����
     * @param A      Gd_information_configure
     * @param db     ���ݿ��ʾ
     * @return 0��1 0��ʾ�ɹ�����1��ʾʧ��
     * */
	public synchronized static int Save(String mobile , String con ,Gd_information_configure A, String db){
		 String content = con.replace("[", "").replace("]", "").replace("{", "").replace("}", "");
		 String sql="insert into gd_warning_wx (id , content , desmobile ,warning_type) values ('" + getCode() + "' , '" + content + "' , '" + mobile  + "' , '"+ A.getWarning_type() +"')";
		 Connection conn = null;
		 Statement st = null;
		 try {
			 conn = DbConnection_main.getInstance(db);
			 if (null == conn) {
				 Loger.Info_log.info("[ERROR]���ݿ�����ʧ��");
				 return -1;
			 }
			 try {
				st = conn.createStatement();
			} catch (Exception e) {
				e.printStackTrace();
				Loger.Info_log.info("[DEBUG]Save������Ч���ݿ����ӣ�" + db + "���ؽ����ӡ�����");
				DbConnection_main.removeMap(db);
				conn = DbConnection_main.getInstance(db);
				st = conn.createStatement();
			}
			 Loger.Info_log.info("[DEBUG]ִ��sql���" + sql);
			 st.executeUpdate(sql);	
			 Loger.Info_log.info("[INFO]������������������ݣ�" + content + " ;�ֻ��ţ�" + mobile);
			} catch (SQLException e) {
					e.printStackTrace();
					return -1;
			}finally{
				new DbConnetion().closekind(st );
			}
		 return 0;
	 }			
	 
    
    
	/**
	 * �������ñ�
	 * @param form ���ñ�
	 * */
    public synchronized static List<Object[]> loadConfig(String sqlex ,String db){
    	Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		List<Object[]> result = new ArrayList<Object[]>();
		List<Object> list = new ArrayList<Object>();
		try {
			conn = DbConnection_main.getInstance(db);
		    if (null == conn) {
				   Loger.Info_log.info("[ERROR]���ݿ�����ʧ��");
				   return null;
			}
		    Loger.Info_log.info("��DEBUG]��ѯ����" + sqlex + " ,connection�Ƿ�Ͽ�״̬��" + conn.isClosed());
		    try {
				st = conn.createStatement();
			} catch (Exception e) {
				e.printStackTrace();
				Loger.Info_log.info("[DEBUG]loadconfig������Ч���ݿ����ӣ�" + db + "���ؽ����ӡ�����");
				DbConnection_main.removeMap(db);
				conn = DbConnection_main.getInstance(db);
				st = conn.createStatement();
			}
		    sqlex = replaceSql(sqlex);
		    rs = st.executeQuery(sqlex);
		    rsmd = rs.getMetaData();
		    while(rs.next()){
		    	for(int i = 1 ; i <= rsmd.getColumnCount(); i++){
		    		Object a = new Object();
		    		a = rs.getObject(i);
		    		if(a == null|| "".equals(a)){
		    			list.add((Object)"");
		    		}else{
				    	list.add(a);	
		    		}
		    	}
			    if(list == null || list.size() == 0){
			    	result = null;
			    }else{
			    	result.add(list.toArray());	
			    	list.clear();
			    }
		    }
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			new DbConnetion().closekind( st , rs);
		}
    	return result;
    }
    
    /**
     * ���������д������磺ccb_log_mt_���� ת��Ϊccb_log_mt_0123
     * @param sql ԭ��sql���
     * @return sqlex �����ջ������¹ؼ����滻��sql���
     * */
    public static String replaceSql(String sql){
    	String YYYY = new SimpleDateFormat("yyyy").format(new Date().getTime());
    	String YYYYMM = new SimpleDateFormat("yyyyMM").format(new Date().getTime());
    	String YYYYMMDD = new SimpleDateFormat("yyyyMMdd").format(new Date().getTime());
    	String YYYYMMDD1 = new SimpleDateFormat("yyyyMMdd").format(new Date().getTime() - 1 * 24 * 60 * 60 * 1000);
    	String MMDD = new SimpleDateFormat("MMdd").format(new Date().getTime());
    	String MMDD1 = new SimpleDateFormat("MMdd").format(new Date().getTime() - 1 * 24 * 60 * 60 * 1000);
    	String MM = new SimpleDateFormat("MM").format(new Date().getTime());
       	sql = sql.replace("[����]", MMDD).replace("[����]",YYYYMM).replace("[����1]", MMDD1).replace("[��]", YYYY);
       	sql = sql.replace("[������]", YYYYMMDD).replace("[������1]", YYYYMMDD1).replace("[��]", MM);
    	return sql;
    }
    
    
    /**
     * ÿ��ִ������Ϣ��ѯ�����ı�����Ϣ���´�ִ��ʱ��
     * ����type��ֵ�����±�
     * */
    public static void updateForm(Gd_information_configure A){
        String sql = "update gd_warning_information set run_time = next_time , next_time = next_time + " + getTime(A.getInterval()) + " where id = '" + A.getId() + "'"; 
        Boolean flag = updateOper(sql);
        if(!flag){
        	Loger.Info_log.info("[ERROR]update���ִ�д���type:" + A.getId());
        }
        flag = updateTime(A.getId() , getTime(A.getInterval() , A.getNext_time()));
        if(!flag){
        	Loger.Info_log.info("[ERROR]next_time�ڴ�ֵ�޸Ĵ���type:" + A.getId());
        }
        ConfigDb.getMapStuts().put(A.getId(), true);//state��Ϊδ����״̬
    }
        
    /**
     * ���ݲ�������sql����Ӧ��ʱ�����ַ���
     * @param intervalʱ�����ַ���
     * @return interval����sql����ʱ�����ַ���
     * */
    private static String getTime(String interval){
    	String val = interval.trim().split(" ")[0];
    	String[] time = interval.trim().split(" ")[1].split(":");   	
    	 switch(time.length){
    	   case 0 :{	 
		        break;
	       }
	       case 1 :{	 
	    	    val = val + " + " + time[0] + "/24 ";
		        break;
	       }
	       case 2 :{
	    	    val = val + " + " + time[0] + "/24 + " + time[1] + "/24/60 ";
		        break;
	       }case 3 :{
	    	    val = val + " + " + time[0] + "/24 + " + time[1] + "/24/60 + " + time[2] + "/24/60/60 ";
		        break;
	       }
	       default:{
	    	    val = val + " + " + time[0] + "/24 + " + time[1] + "/24/60 + " + time[2] + "/24/60/60 ";
		        break;
	       }
		   }
    	return val;
    }
    
    /**
     * ���ݲ��������ڴ������Ҫ���µ�next_time
     * @param intervalʱ����
     * @param next_time�ڴ��е�ʱ��
     * @param next_time���º��ʱ��
     * */
    private static String getTime(String interval , String next_time){ 
    	int val = Integer.parseInt(interval.trim().split(" ")[0]);
    	String[] time = interval.trim().split(" ")[1].split(":");
    	SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); 
        Date date = null;   
        try {   
            date = format.parse(next_time);   
        } catch (Exception ex) {   
            ex.printStackTrace();   
        }   
        if (date == null)   
            return "";   
        Calendar cal = Calendar.getInstance();   
        cal.setTime(date);   
        cal.add(Calendar.DATE, val); //���� 
        
   	    switch(time.length){
	      case 0 :{	 
	          break;
        }
          case 1 :{	 
        	  cal.add(Calendar.HOUR, Integer.parseInt(time[0]));// 24Сʱ�� 
	          break;
        }
          case 2 :{
        	  cal.add(Calendar.HOUR, Integer.parseInt(time[0]));// 24Сʱ�� 
        	  cal.add(Calendar.MINUTE, Integer.parseInt(time[1]));// ���� 
	          break;
        }
          case 3 :{
        	  cal.add(Calendar.HOUR, Integer.parseInt(time[0]));// 24Сʱ�� 
        	  cal.add(Calendar.MINUTE, Integer.parseInt(time[1]));// ���� 
        	  cal.add(Calendar.SECOND, Integer.parseInt(time[2]));// �� 
	          break;
        }
        default:{
        	 cal.add(Calendar.HOUR, Integer.parseInt(time[0]));// 24Сʱ�� 
        	 cal.add(Calendar.MINUTE, Integer.parseInt(time[1]));// ���� 
        	 cal.add(Calendar.SECOND, Integer.parseInt(time[2]));// ��
	         break;
        }
	    }
        date = cal.getTime();   
        cal = null;   
        return format.format(date);   
    }
    
    
    
    
    
    /**
     * ���²���
     * @param sql
     * @return Boolean �����ɹ�����true ʧ�ܷ���false
     * */
    public synchronized static Boolean updateOper(String sql){
    	Connection conn = null;
		Statement st = null;
		try {
			conn = DbConnection_main.getInstance("defult");
		    if (null == conn) {
				   Loger.Info_log.info("[ERROR]���ݿ�����ʧ��");
				   return false;
			}
		    Loger.Info_log.info("��DEBUG�ݸ��³���" + sql + " ,connection�Ƿ�Ͽ�״̬��" + conn.isClosed());
		    try {
				st = conn.createStatement();
			} catch (Exception e) {
				e.printStackTrace();
				Loger.Info_log.info("[DEBUG]loadconfig������Ч���ݿ����ӣ�defult���ؽ����ӡ�����");
				DbConnection_main.removeMap("defult");
				conn = DbConnection_main.getInstance("defult");
				st = conn.createStatement();
			}
		    st.executeUpdate(sql);
		} catch (SQLException e) {
			Loger.Info_log.info("[ERROR]update���ִ�д���" + sql);
			return false;
		}finally{
			new DbConnetion().closekind( st );
		}
		return true;
    }
    
    
    /**
     * �����ڴ�����д�id��Ӧ��next_time
     * ���³ɹ�ʱ����true��ʧ��ʱ����false
     * @param id
     * @param next_time
     * @return Boolean
     * */
    private static Boolean updateTime(String id , String next_time){
    	Boolean flag = false;
    	for( Gd_information_configure cd : ConfigDb.getListConfig()){
    		if(cd.getId() == id || cd.getId().equals(id)){
    			cd.setNext_time(next_time);
    			flag = true;
    		}
    	}
		return flag;
    }
    
    
    public static void main(String[] args){
//    	String s = "[s1],�߷���ʱ���ķ��͵ط�[s2],����ˮ���{1342134},�׶������ط�{adsfasdf}";
//    	String a = StringUtils.substringBetween(s,"{","}");
//    	List<String> list = new ArrayList<String>();
//    	while(true){
//    		a = StringUtils.substringBetween(s,"{","}");
//         	s = StringUtils.substringAfter(s, "}"); 
//        	if(null == a || "".equals(a)){
//    		   System.out.println("�Ѿ�����");
//    		   break;
//    	    }else{
//          	   list.add(a);
//    	    }
//    	}
//    	System.out.println(list.toArray()[0] + "," + list.toArray()[1] + ",");
//    	
//    	java.text.SimpleDateFormat formatter = new SimpleDateFormat( "yyyyMMddHHmmss ");
//    	long s= Long.parseLong("20160327170537"); //26081110105250
//    	long a = new Date().getTime();
//		String time = new SimpleDateFormat("yyyyMMddHHmmss").format(s + 24 * 60 * 60 * 1000);
        System.out.println(getTime("0 00:00:51","20160327170537")); //20160326170416 //20160326170435 //20160327170537
    	//System.out.println(getTime("1 01:01:01"));
    	
    }
}
