package br.ufrn.uedashboard.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnector {
	
	private static Connection connection;

	public static Connection getConnection() {
		String url = "<INSERT HERE YOUR DB URL>";
		String dbName = "<INSERT HERE YOUR DB NAME>";
		String driver = "<INSERT HERE YOUR DB DRIVER>"; 
		String userName = "<INSERT HERE YOUR DB USERNAME>"; 
		String password = "<INSERT HERE YOUR DB PASSWORD>"; 
		
		try { 
			Class.forName(driver).newInstance();
			if (connection == null)
				connection = DriverManager.getConnection(url+dbName,userName,password); 
		} catch (Exception e) { 
			e.printStackTrace(); 
		} 
		return connection;
	}
	
	public static void closeConnection() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
