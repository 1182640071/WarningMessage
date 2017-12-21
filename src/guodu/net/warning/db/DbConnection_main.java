package guodu.net.warning.db;

import guodu.net.warning.util.Loger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * DbConnetion 实例话中转类
 * */
public class DbConnection_main {
//    private static DbConnetion instance = null; // 唯一实例
    private static Map<String , Connection> map = new HashMap<String , Connection>();
/**
 * 根据arg的来实例话不同DbConnetion类对象
 * @param arg 标示符号，代表c3p0配置文件中的各数据库config.name
 * @return DbConnetion类对象
 * @throws SQLException 
 * */
	public synchronized static Connection getInstance(String arg) throws SQLException {
		Connection  instance1  = null;
		if(map.containsKey(arg)){
			instance1 = map.get(arg);	
			return instance1;
		}else{
			if( "defult".equals(arg)){
				DbConnetion  instance  = new DbConnetion() ;
				Connection con =  instance.getConnection();
				map.put("defult", con);
				return con;
			}else{
				DbConnetion  instance  = new DbConnetion(arg);
				Connection con =  instance.getConnection();
				map.put(arg, con);
				return con;
			}
		}
	}
	
	/**
	 * 将无效的链接从map中移除
	 * @param string 数据库标识符
	 * */
	public static void removeMap(String arg){
		try {
			if( "defult".equals(arg)){
				map.remove("defult");
			}else{
				map.remove(arg);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Loger.Info_log.info("[ERROR]无效链接移除失败");
		}
	}
}
