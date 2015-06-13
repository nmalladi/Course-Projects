package com.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DiasporaQueries {

	
	public static final int TAG_FOLLOWING = 1;
	public static final int TAG_COMMENTS = 2;
	public static final int TAG_POSTS = 3;

	
	public static String getLocation(Connection connection, int usrId) throws SQLException{
		
		PreparedStatement query = null;
		ResultSet result = null;
		String queryRes = null;
		try{
			if(connection != null){
				query = connection.prepareStatement("select location from profiles where person_id = ?");
				query.setInt(1, usrId);
				result = query.executeQuery();
				

				if(result != null){
					while(result.next()){
						queryRes = result.getString(1);
					}
				}
			}
		}catch(SQLException e){
			System.out.println("Exception in getLocation:" + e.getMessage());
		}finally{
			result.close();
			query.close();
			
		}
		
		return queryRes;

	}
	
	public static ArrayList<String> getHashTagsFollowing(Connection connection, 
			int usrId, int type) throws SQLException{
		
		PreparedStatement query = null;
		ResultSet result = null;
		ArrayList<String> queryRes = null;
		try{
			if(connection != null){
				
				switch(type){
					
					case TAG_FOLLOWING:
						query = connection.prepareStatement("select name from tags " +
								"where id in ( select tag_id from tag_followings "+
								"where user_id = ?)");
						break;
						
					case TAG_COMMENTS:
						query = connection.prepareStatement("select text from comments " +
																"where author_id = ?");
						break;
						
					case TAG_POSTS:
						query = connection.prepareStatement("select text from posts " +
																"where author_id = ?");
						break;
						
					default:
					
				}

				query.setInt(1, usrId);
				result = query.executeQuery();
				
				queryRes = new ArrayList<String>();
				if(result != null){
					while(result.next()){
						String res = result.getString(1);
						queryRes.add(res);
					}
				}
			}
		}catch(SQLException e){
			System.out.println("Exception in getHashTagsFollowing:" + e.getMessage());
		}finally{
			result.close();
			query.close();			
		}
		
		return queryRes;
	}
	
public static ArrayList<Integer> getContacts(Connection connection, int usrId) throws SQLException{
		
		PreparedStatement query = null;
		ResultSet result = null;
		ArrayList<Integer> queryRes = null;
		try{
			if(connection != null){
				query = connection.prepareStatement("select person_id from contacts where user_id = ?");
				query.setInt(1, usrId);
				result = query.executeQuery();
				
				queryRes = new ArrayList<Integer>();
				if(result != null){
					while(result.next()){
						int id = result.getInt(1);
						queryRes.add(id);
					}
				}
			}
		}catch(SQLException e){
			System.out.println("Exception in getContacts:" + e.getMessage());
		}finally{
			result.close();
			query.close();
			
		}
		
		return queryRes;

	}

public static ArrayList<Integer> getUsers(Connection connection) throws SQLException{
	
	PreparedStatement query = null;
	ResultSet result = null;
	ArrayList<Integer> queryRes = null;
	try{
		if(connection != null){
			
			queryRes = new ArrayList<Integer>();
			
			query = connection.prepareStatement("select id from users");
			result = query.executeQuery();
			
			if(result != null){
				while(result.next()){
					 queryRes.add(result.getInt(1));
				}
			}
		}
	}catch(SQLException e){
		System.out.println("Exception in getUsers:" + e.getMessage());
	}finally{
		result.close();
		query.close();
		
	}

	return queryRes;

}

public static ArrayList<ArrayList<String>> getFriendsInfo(Connection connection, int usrId) throws SQLException{
	
	PreparedStatement query = null;
	ResultSet result = null;
	ArrayList<ArrayList<String>> queryRes = null;
	int cnt = 0;
	try{
		if(connection != null){
			query = connection.prepareStatement("select p.first_name, p.location, pe.guid, pe.url," + 
								"pe.diaspora_handle from profiles p, people pe " + 
								"where p.person_id = pe.owner_id and " + 
								"p.person_id in (select person_id from contacts " + 
								"where user_id = ?)");
			query.setInt(1, usrId);
			result = query.executeQuery();
			
			queryRes = new ArrayList<ArrayList<String>>();
			
			if(result != null){
				while(result.next()){
					 queryRes.add(new ArrayList<String>());
					 queryRes.get(cnt).add(result.getString(1));
					 queryRes.get(cnt).add(result.getString(2));
					 queryRes.get(cnt).add(result.getString(3));
					 queryRes.get(cnt).add(result.getString(4));
					 queryRes.get(cnt).add(result.getString(5));

					 cnt++; 
				}
			}
		}
	}catch(SQLException e){
		System.out.println("Exception in getFriendsInfo:" + e.getMessage());
	}finally{
		result.close();
		query.close();
		
	}
	
	return queryRes;

}

public static int getUsrId(Connection connection, String guid) throws SQLException{
	
	PreparedStatement query = null;
	ResultSet result = null;
	int queryRes = 9999;
	try{
		if(connection != null){
			query = connection.prepareStatement("select owner_id from people where guid = ?");
			query.setString(1, guid);
			result = query.executeQuery();
			
			if(result != null){
				while(result.next()){
					 queryRes = result.getInt(1);
				}
			}
		}
	}catch(SQLException e){
		System.out.println("Exception in getUsrId:" + e.getMessage());
	}finally{
		result.close();
		query.close();
		
	}
	
	return queryRes;

}

public static String getGUID(Connection connection, int usrId) throws SQLException{
	
	PreparedStatement query = null;
	ResultSet result = null;
	String queryRes = null;
	try{
		if(connection != null){
			query = connection.prepareStatement("select guid from people where owner_id = ?");
			query.setInt(1, usrId);
			result = query.executeQuery();
			
			if(result != null){
				while(result.next()){
					 queryRes = result.getString(1);
				}
			}
		}
	}catch(SQLException e){
		System.out.println("Exception in getGUID:" + e.getMessage());
	}finally{
		result.close();
		query.close();
		
	}
	
	return queryRes;

}
	

}
