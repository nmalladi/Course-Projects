package com.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectorImpl implements DBConnector {
	
	private final String className = "DBConnectorImpl";
	private final String mysqlDriver = "com.mysql.jdbc.Driver";
	private Connection connObj = null;
	
	public Connection connect() throws SQLException {
		
		try {
			
			Class.forName(mysqlDriver);
		
			connObj = DriverManager.getConnection("jdbc:mysql://localhost/diaspora_development?"
			              								+ "user=root&password=root");
		} catch (ClassNotFoundException e) {

			System.out.println(className+" Connection Failed:"+e.getMessage());
		}
	
		return connObj;
	}
	
	public void close() throws SQLException{

			connObj.close();

	}

}
