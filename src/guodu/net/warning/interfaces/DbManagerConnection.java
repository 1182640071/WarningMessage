package guodu.net.warning.interfaces;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public interface DbManagerConnection {


	/**
	 * �����Ӷ��󷵻ظ�������ָ�������ӳ�
	 * 
	 * @param name
	 *            �������ļ��ж�������ӳ�����
	 * @param con
	 *            ���Ӷ���
	 * @throws SQLException 
	 */
	public void freeConnection(Connection con) throws SQLException;

	/**
	 * ���һ�����õ�(���е�)����.���û�п�������,������������С����������� ����,�򴴽�������������
	 * 
	 * @param name
	 *            �������ļ��ж�������ӳ�����
	 * @return Connection �������ӻ�null
	 * @throws SQLException 
	 */
	public Connection getConnection() throws SQLException;

	
	public void closekind( PreparedStatement stmt, Connection conn);
    /**
     * �ر�����
     * @param Statement
     * @param Connection
     * @param ResultSet
     * */
	public void closekind( Statement st, Connection conn, ResultSet rs);

    /**
     * �ر�����
     * @param Statement
     * @param Connection
     * */
	public void closekind( Statement st, Connection conn);
	
}
