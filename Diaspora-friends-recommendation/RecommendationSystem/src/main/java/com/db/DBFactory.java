package com.db;

public class DBFactory {
	
	private static DBConnectorImpl connImpl = null;
	
	public static DBConnector getInstance(DBConnector.type val){
		
		if(connImpl == null)
			connImpl = new DBConnectorImpl();
		
		
		return connImpl;
	}

}
