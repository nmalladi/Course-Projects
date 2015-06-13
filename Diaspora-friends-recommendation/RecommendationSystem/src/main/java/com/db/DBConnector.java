package com.db;

import java.sql.Connection;
import java.sql.SQLException;

public interface DBConnector {
	
	public static enum type{
		MYSQL("mysql");
		
		private String val;
		type(String val){
			this.val = val;
		}
		
		public String getTypeName(){
			return val;
		}
	}
	
	public Connection connect() throws SQLException;
	public void close() throws SQLException;

}
