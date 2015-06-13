package com.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class Executive {
	
	public static void main(String args[]){
		
		DBConnector sqlConnector = null;
		Connection connection = null;
		
		try {
			sqlConnector = DBFactory.getInstance(DBConnector.type.MYSQL);
			connection = sqlConnector.connect();
			
			//DiasporaQueries.getLocation(connection, 1);
			
			ArrayList<String> result = DiasporaQueries.getHashTagsFollowing(connection,
										2, DiasporaQueries.TAG_COMMENTS);
			for(String s: result){
				System.out.println(s);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				connection.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
		}

	}

}
