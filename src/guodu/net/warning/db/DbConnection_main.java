package guodu.net.warning.db;

import guodu.net.warning.util.Loger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * DbConnetion ʵ������ת��
 * */
public class DbConnection_main {
//    private static DbConnetion instance = null; // Ψһʵ��
    private static Map<String , Connection> map = new HashMap<String , Connection>();
/**
 * ����arg����ʵ������ͬDbConnetion�����
 * @param arg ��ʾ���ţ�����c3p0�����ļ��еĸ����ݿ�config.name
 * @return DbConnetion�����
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
	 * ����Ч�����Ӵ�map���Ƴ�
	 * @param string ���ݿ��ʶ��
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
			Loger.Info_log.info("[ERROR]��Ч�����Ƴ�ʧ��");
		}
	}
}
