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
	
	private static int number = 1; //序列号
	
	 /**
	  * 获取1至9999的四位数据
	  * */
    private synchronized static int getNumber(){
   	if(number > 9999){
   		number = 1;
   	}
   	return number++;
    }
    
    /**
     * 获取唯一标示
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
     * 将匹配好的模版，并经过逻辑判断确定发送的信息插入待发表
     * @param mobile 手机号
     * @param con    内容
     * @param A      Gd_information_configure
     * @param db     数据库标示
     * @return 0或－1 0标示成功，－1标示失败
     * */
	public synchronized static int Save(String mobile , String con ,Gd_information_configure A, String db){
		 String content = con.replace("[", "").replace("]", "").replace("{", "").replace("}", "");
		 String sql="insert into gd_warning_wx (id , content , desmobile ,warning_type) values ('" + getCode() + "' , '" + content + "' , '" + mobile  + "' , '"+ A.getWarning_type() +"')";
		 Connection conn = null;
		 Statement st = null;
		 try {
			 conn = DbConnection_main.getInstance(db);
			 if (null == conn) {
				 Loger.Info_log.info("[ERROR]数据库连接失败");
				 return -1;
			 }
			 try {
				st = conn.createStatement();
			} catch (Exception e) {
				e.printStackTrace();
				Loger.Info_log.info("[DEBUG]Save产生无效数据库链接：" + db + "，重建链接。。。");
				DbConnection_main.removeMap(db);
				conn = DbConnection_main.getInstance(db);
				st = conn.createStatement();
			}
			 Loger.Info_log.info("[DEBUG]执行sql语句" + sql);
			 st.executeUpdate(sql);	
			 Loger.Info_log.info("[INFO]插入数据入待发表，内容：" + content + " ;手机号：" + mobile);
			} catch (SQLException e) {
					e.printStackTrace();
					return -1;
			}finally{
				new DbConnetion().closekind(st );
			}
		 return 0;
	 }			
	 
    
    
	/**
	 * 加载配置表
	 * @param form 配置表
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
				   Loger.Info_log.info("[ERROR]数据库连接失败");
				   return null;
			}
		    Loger.Info_log.info("［DEBUG]查询程序：" + sqlex + " ,connection是否断开状态：" + conn.isClosed());
		    try {
				st = conn.createStatement();
			} catch (Exception e) {
				e.printStackTrace();
				Loger.Info_log.info("[DEBUG]loadconfig产生无效数据库链接：" + db + "，重建链接。。。");
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
     * 丢表名进行处理，例如：ccb_log_mt_月日 转换为ccb_log_mt_0123
     * @param sql 原版sql语句
     * @return sqlex 将月日或者年月关键期替换的sql语句
     * */
    public static String replaceSql(String sql){
    	String YYYY = new SimpleDateFormat("yyyy").format(new Date().getTime());
    	String YYYYMM = new SimpleDateFormat("yyyyMM").format(new Date().getTime());
    	String YYYYMMDD = new SimpleDateFormat("yyyyMMdd").format(new Date().getTime());
    	String YYYYMMDD1 = new SimpleDateFormat("yyyyMMdd").format(new Date().getTime() - 1 * 24 * 60 * 60 * 1000);
    	String MMDD = new SimpleDateFormat("MMdd").format(new Date().getTime());
    	String MMDD1 = new SimpleDateFormat("MMdd").format(new Date().getTime() - 1 * 24 * 60 * 60 * 1000);
    	String MM = new SimpleDateFormat("MM").format(new Date().getTime());
       	sql = sql.replace("[月日]", MMDD).replace("[年月]",YYYYMM).replace("[月日1]", MMDD1).replace("[年]", YYYY);
       	sql = sql.replace("[年月日]", YYYYMMDD).replace("[年月日1]", YYYYMMDD1).replace("[月]", MM);
    	return sql;
    }
    
    
    /**
     * 每次执行完信息查询，更改报警信息的下次执行时间
     * 根据type的值来更新表
     * */
    public static void updateForm(Gd_information_configure A){
        String sql = "update gd_warning_information set run_time = next_time , next_time = next_time + " + getTime(A.getInterval()) + " where id = '" + A.getId() + "'"; 
        Boolean flag = updateOper(sql);
        if(!flag){
        	Loger.Info_log.info("[ERROR]update语句执行错误，type:" + A.getId());
        }
        flag = updateTime(A.getId() , getTime(A.getInterval() , A.getNext_time()));
        if(!flag){
        	Loger.Info_log.info("[ERROR]next_time内存值修改错误，type:" + A.getId());
        }
        ConfigDb.getMapStuts().put(A.getId(), true);//state改为未启动状态
    }
        
    /**
     * 根据参数返回sql语句对应的时间间隔字符串
     * @param interval时间间隔字符串
     * @return interval符合sql语句的时间间隔字符串
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
     * 根据参数返回内存队列需要更新的next_time
     * @param interval时间间隔
     * @param next_time内存中的时间
     * @param next_time更新后的时间
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
        cal.add(Calendar.DATE, val); //加天 
        
   	    switch(time.length){
	      case 0 :{	 
	          break;
        }
          case 1 :{	 
        	  cal.add(Calendar.HOUR, Integer.parseInt(time[0]));// 24小时制 
	          break;
        }
          case 2 :{
        	  cal.add(Calendar.HOUR, Integer.parseInt(time[0]));// 24小时制 
        	  cal.add(Calendar.MINUTE, Integer.parseInt(time[1]));// 分钟 
	          break;
        }
          case 3 :{
        	  cal.add(Calendar.HOUR, Integer.parseInt(time[0]));// 24小时制 
        	  cal.add(Calendar.MINUTE, Integer.parseInt(time[1]));// 分钟 
        	  cal.add(Calendar.SECOND, Integer.parseInt(time[2]));// 秒 
	          break;
        }
        default:{
        	 cal.add(Calendar.HOUR, Integer.parseInt(time[0]));// 24小时制 
        	 cal.add(Calendar.MINUTE, Integer.parseInt(time[1]));// 分钟 
        	 cal.add(Calendar.SECOND, Integer.parseInt(time[2]));// 秒
	         break;
        }
	    }
        date = cal.getTime();   
        cal = null;   
        return format.format(date);   
    }
    
    
    
    
    
    /**
     * 更新操作
     * @param sql
     * @return Boolean 操作成功返回true 失败返回false
     * */
    public synchronized static Boolean updateOper(String sql){
    	Connection conn = null;
		Statement st = null;
		try {
			conn = DbConnection_main.getInstance("defult");
		    if (null == conn) {
				   Loger.Info_log.info("[ERROR]数据库连接失败");
				   return false;
			}
		    Loger.Info_log.info("［DEBUG］更新程序：" + sql + " ,connection是否断开状态：" + conn.isClosed());
		    try {
				st = conn.createStatement();
			} catch (Exception e) {
				e.printStackTrace();
				Loger.Info_log.info("[DEBUG]loadconfig产生无效数据库链接：defult，重建链接。。。");
				DbConnection_main.removeMap("defult");
				conn = DbConnection_main.getInstance("defult");
				st = conn.createStatement();
			}
		    st.executeUpdate(sql);
		} catch (SQLException e) {
			Loger.Info_log.info("[ERROR]update语句执行错误" + sql);
			return false;
		}finally{
			new DbConnetion().closekind( st );
		}
		return true;
    }
    
    
    /**
     * 更新内存队列中此id对应的next_time
     * 更新成功时返回true，失败时返回false
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
//    	String s = "[s1],瓦房大时代的发送地方[s2],萨芬水电费{1342134},首都发生地方{adsfasdf}";
//    	String a = StringUtils.substringBetween(s,"{","}");
//    	List<String> list = new ArrayList<String>();
//    	while(true){
//    		a = StringUtils.substringBetween(s,"{","}");
//         	s = StringUtils.substringAfter(s, "}"); 
//        	if(null == a || "".equals(a)){
//    		   System.out.println("已经空了");
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
