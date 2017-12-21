package guodu.net.warning.interfaces;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public interface DbManagerConnection {


	/**
	 * 将连接对象返回给由名字指定的连接池
	 * 
	 * @param name
	 *            在属性文件中定义的连接池名字
	 * @param con
	 *            连接对象
	 * @throws SQLException 
	 */
	public void freeConnection(Connection con) throws SQLException;

	/**
	 * 获得一个可用的(空闲的)连接.如果没有可用连接,且已有连接数小于最大连接数 限制,则创建并返回新连接
	 * 
	 * @param name
	 *            在属性文件中定义的连接池名字
	 * @return Connection 可用连接或null
	 * @throws SQLException 
	 */
	public Connection getConnection() throws SQLException;

	
	public void closekind( PreparedStatement stmt, Connection conn);
    /**
     * 关闭连接
     * @param Statement
     * @param Connection
     * @param ResultSet
     * */
	public void closekind( Statement st, Connection conn, ResultSet rs);

    /**
     * 关闭连接
     * @param Statement
     * @param Connection
     * */
	public void closekind( Statement st, Connection conn);
	
}
