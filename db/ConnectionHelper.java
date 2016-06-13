/**                                               
 * <p>Title: DROOLS RULE ENGINE SYSTEM</p>          
 * <p>Description: 主要用于动态生成编译和加载JavaBean的服务类 </p>                           
 * <p>Copyright: Copyright (c) 2012 - 2015</p>    
 * <p>Company: Excel Technology International (Bei Jing) Limited</p>      
 * @author Johnson Ma
 * @Date 2013/03/15                                
 * @version 1.0                                   
 */
package com.ruleEngine.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.hibernate.connection.ConnectionProvider;
import org.hibernate.engine.SessionFactoryImplementor;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
/**
 * 此类用于从hebernate中获取连接，还有封装了一些公共方法
 * 此类是由spring组织初始化和单例控制
 * @author Johnson Ma
 *
 */
public class ConnectionHelper  extends HibernateDaoSupport{

	static ConnectionProvider cp;
	/**
	 * 获取ConnectionProvider的单例
	 */
	public  void getInstance(){
		cp = ((SessionFactoryImplementor)getSessionFactory()).getConnectionProvider();
	}
	/**
	 * 从ConnectionProvider中获得连接
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnection() throws SQLException{
		return  cp.getConnection();
	}
	/**
	 * 用ConnectionProvider来回收连接
	 * @param con
	 * @throws SQLException
	 */
	public static void closeConnection(Connection con) throws SQLException{
		cp.closeConnection(con);
	}
	

	/**
	 * 关闭资源的通用方法
	 * @param rs
	 * @param ps
	 * @param st
	 * @param conn
	 */
	public static void close(ResultSet rs,PreparedStatement ps,Statement st,Connection conn){
		try{
			if(rs != null){
				rs.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			if(ps != null){
				ps.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			if(st != null){
				st.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			closeConnection(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
