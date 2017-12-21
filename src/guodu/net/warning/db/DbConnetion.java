package guodu.net.warning.db;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import guodu.net.warning.interfaces.DbManagerConnection;

public class DbConnetion implements DbManagerConnection{
	String workpath = System.getProperty("user.dir") + File.separator + "config" +File.separator + "c3p0-config.xml";
	public static ComboPooledDataSource ds = null;
	
	/**
	 * ��������˽���Է�ֹ�������󴴽�����ʵ��
	 */
	public DbConnetion(String arg) {
		System.setProperty("com.mchange.v2.c3p0.cfg.xml", workpath);
		ds = new ComboPooledDataSource(arg);
	}
	
	/**
	 * ��������˽���Է�ֹ�������󴴽�����ʵ��
	 */
	public DbConnetion() {
		System.setProperty("com.mchange.v2.c3p0.cfg.xml", workpath);
		ds = new ComboPooledDataSource();
	}
	
	
	/**
	 * �����Ӷ��󷵻ظ�������ָ�������ӳ�
	 * 
	 * @param name
	 *            �������ļ��ж�������ӳ�����
	 * @param con
	 *            ���Ӷ���
	 * @throws SQLException 
	 */
	public void freeConnection(Connection con) throws SQLException {
		con.close();
	}

	/**
	 * ���һ�����õ�(���е�)����.���û�п�������,������������С����������� ����,�򴴽�������������
	 * 
	 * @param name
	 *            �������ļ��ж�������ӳ�����
	 * @return Connection �������ӻ�null
	 * @throws SQLException 
	 */
	public Connection getConnection() throws SQLException {
		return ds.getConnection();
	}

	
	public void closekind( PreparedStatement stmt, Connection conn) {

		if (stmt != null)
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		if (conn != null)
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
    /**
     * �ر�����
     * @param Statement
     * @param Connection
     * @param ResultSet
     * */
	public void closekind( Statement st, Connection conn, ResultSet rs) {
		if (rs != null)
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		if (st != null)
			try {
				st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		if (conn != null)
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}

    /**
     * �ر�����
     * @param Statement
     * @param Connection
     * */
	public void closekind( Statement st, Connection conn) {
		if (st != null)
			try {
				st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		if (conn != null)
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
	
	
	
	 /**
     * �ر�����
     * @param Statement
     * @param ResultSet
     * */
	public void closekind( Statement st, ResultSet rs) {
		if (rs != null)
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		if (st != null)
			try {
				st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
	
	/**
    * �ر�����
    * @param Statement
    * @param ResultSet
    * */
	public void closekind( Statement st) {
		if (st != null)
			try {
				st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
	
	public static void main(String[] args) throws SQLException {
//		DBManagerConnection_Main ConnMgr = DBManagerConnection_Main.getInstance("45");
//		DBManagerConnection_Main ConnMgr = null;// new DBManagerConnection_Main("45");
//		Connection Connection1 = ConnMgr.getConnection();
//		if (Connection1 == null) {
//			System.out.println("�޷��õ����ݿ����Ӷ���");
//		} else {
//			System.out.println("�ѵõ����ݿ����Ӷ���");
//		}
//
//		ConnMgr.freeConnection(Connection1);
//
//		Connection1 = ConnMgr.getConnection();
//
//		if (Connection1 == null) {
//			System.out.println("�޷��õ����ݿ����Ӷ���");
//		} else {
//			System.out.println("�ѵõ����ݿ����Ӷ���");
//		}
//
//		ConnMgr.freeConnection(Connection1);
	}
}
