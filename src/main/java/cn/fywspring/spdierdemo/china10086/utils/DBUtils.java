package cn.fywspring.spdierdemo.china10086.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DBUtils {
	private static DataSource dataSource = null;
	
	static {
		dataSource = new ComboPooledDataSource("mysql");
	}
	
	/**
	 * 从数据库连接池获取一个数据库连接
	 * @return 数据库链接对象
	 */
	public static Connection getConnection() {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	public static void closeConn(Connection conn) {
		try {
			if (conn != null && conn.isClosed()) {
				conn.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
