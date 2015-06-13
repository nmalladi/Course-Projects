package com.recommendation;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;

import com.db.DBConnector;
import com.db.DBFactory;
import com.db.DiasporaQueries;

public class RecommendationAlgorithm {
	
	private static final String CLASS_NAME = "RecommendationAlgorithm";
	private static final int FRIEND_GUID = 2;
	private static final int FRIEND_URL = 3;
	private static final int FRIEND_NAME = 0;
	private static final int FRIEND_LOCATION = 1;
	private static final int FRIEND_DISPHANDLE = 4;
	private static final int CONTACT_GUID = 0;
	
	DBConnector sqlConnector = null;
	Connection connection = null;
	RecommendationAlgoProtocol protocol = null;
	
	
	private FriendGraph fg;
	private ArrayList<String> result;
	private Map<String, Float> scores;
	private Map<String, ArrayList<String>> contactsStore;
	
	private int bfs_level;
	private String userHostName;
	private String userGUID;
	private String userLocation;	
	private ArrayList<String> userTags;
	
	private BlockingQueue<String> response = null;
	

	public RecommendationAlgorithm(BlockingQueue<String> queue){
		
		scores = new HashMap<String, Float>();
		contactsStore = new HashMap<String, ArrayList<String>>();
		
		response = queue;
		protocol = ProtocolImpl.getInstace();
		
		try {
			sqlConnector = DBFactory.getInstance(DBConnector.type.MYSQL);
			connection = sqlConnector.connect();
			
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	public void calculateScores(Node person) throws SQLException{
		
		float score = 0;
		int usrId = DiasporaQueries.getUsrId(connection, person.getGuid());
		
		ArrayList<String> tags = DiasporaQueries.getHashTagsFollowing(connection, usrId, 
									DiasporaQueries.TAG_FOLLOWING);
		String location = DiasporaQueries.getLocation(connection, usrId);
		
		
		 tags.retainAll(userTags);
		 score += tags.size() * 0.7;
		 
		 if(userLocation.equalsIgnoreCase(location)){
			 score += tags.size() * 0.3;
		 }
		 
		 scores.put(person.getFirstName(), score);
		
	}
	
	public void calculateScores(final ArrayList<String> friendInfo) throws SQLException{
		
		float score = 0;
		int usrId = DiasporaQueries.getUsrId(connection, friendInfo.get(FRIEND_GUID));
		
		ArrayList<String> tags = DiasporaQueries.getHashTagsFollowing(connection, usrId, DiasporaQueries.TAG_FOLLOWING);
		String location = friendInfo.get(FRIEND_LOCATION);
		
		tags.retainAll(userTags);
		score += tags.size() * 0.7;
		
		if(userLocation.equalsIgnoreCase(location)){
			score += tags.size() * 0.3;
		}
		
		scores.put(friendInfo.get(FRIEND_DISPHANDLE), score);
		
	}
	
	private ArrayList<String> testData(int usrId){
		
		
		if(usrId == 2){
			ArrayList<String> vals = new ArrayList<String>();
			vals.add("ecef4aa0c5ca013234f1080027e490ad");
			vals.add("localhost:80003");
			vals.add("sesnik");
			
			return vals;
		}
		
		return null;
	}
	
	private ArrayList<String> getExploreNodes(){
		
		ArrayList<String> result = null;
		
		result = (ArrayList<String>)fg.traverse_bfs(userGUID, bfs_level);
		
		return result;
		
	}
	
	public void buildGraph() throws SQLException{
		
		ArrayList<Integer> users = null;
		ArrayList<ArrayList<String>> friends = null;
		
		String guid;
		
		users = DiasporaQueries.getUsers(connection);
		fg = new FriendGraph();
		
		for(int i: users){
			
			guid = DiasporaQueries.getGUID(connection, i);
			friends = DiasporaQueries.getFriendsInfo(connection, i);
			
			if(guid != null){
				fg.addNode(guid);
				
			}else{
				System.out.println("Invalid: GUID is NUll");
				return;
			}
			
			for(ArrayList<String> s: friends){
				
				
				fg.addEdge(guid, s.get(FRIEND_GUID), s.get(FRIEND_URL), s.get(FRIEND_NAME));
				
				ArrayList<String> details = new ArrayList<String>();
				details.add(s.get(FRIEND_GUID));
				details.add(s.get(FRIEND_NAME));
				details.add(s.get(FRIEND_LOCATION));
				details.add(s.get(FRIEND_URL));
				details.add(s.get(FRIEND_DISPHANDLE));
				
				contactsStore.put(s.get(FRIEND_GUID), details);	
			}
		
		}
		
	}
	
	public void login(){
		
		//To-DO: HostName will be retrieved from login
		userHostName = "localhost:80001";
		userGUID = "1708a9a0c2d3013264d8080027e490ad";
		bfs_level = 1;
		
	}
	
/*	private void buildGraph(String guid) throws SQLException{

		fg = new FriendGraph();
		
		
		int usrId = DiasporaQueries.getUsrId(connection, guid);
		
		
		ArrayList<Integer> friends = DiasporaQueries.getContacts(connection, usrId);
		
		while(friends.size() !=0){
			for(int i: friends){
				
				ArrayList<String> vals = testData(i);
				//fg.addEdge(guid, "enterfriendguid", "localhost:80003", "sesnik");

			}
		}
		
	}*/
	
	
	public void sendRequestToRemoteFriends(Map<String, ArrayList<String>> remoteFriends) throws SQLException{
		
		Map<String, String> remoteRequest = new HashMap<String, String>();
		
		int usrId = DiasporaQueries.getUsrId(connection, userGUID);
		// Aggregate tags of the user
		userTags = DiasporaQueries.getHashTagsFollowing(connection, usrId, 
				DiasporaQueries.TAG_FOLLOWING);
		userLocation = DiasporaQueries.getLocation(connection, usrId);
		
		StringBuffer sb = new StringBuffer("<tags>");
		for(int j = 0; j < userTags.size(); j++){
			sb.append(userTags.get(j));
			if(j < userTags.size()-1)
				sb.append(",");
		}
		sb.append("</tags>");
		
		sb.append("<loc>"+userLocation+"</loc>");
		sb.append((char)13);
		
		String commonStr = sb.toString();
			
		
		Iterator it = remoteFriends.entrySet().iterator();	
		while(it.hasNext()){
			
			StringBuffer sb1 = new StringBuffer("<users>");
			
			Entry<String, ArrayList<String>> val = (Entry<String, ArrayList<String>>)it.next();
			String remoteUrl = val.getKey();
			ArrayList<String> remoteUsr = val.getValue();
			
			for(int j=0; j< remoteUsr.size(); j++){
				
				sb1.append(remoteUsr.get(j));
				if(j < remoteUsr.size()-1){
					sb1.append(",");
				}
			}
			
			sb1.append("</users>");
			sb1.append(commonStr);
			remoteRequest.put(remoteUrl, sb1.toString());

		}
		
		protocol.sendRequest(remoteRequest);

	}
	
	
	public void readResponse(){
		
		while(true){
			
			try{
				// Add the result to scores
				String s1 = response.take();
				System.out.println(s1);
			}catch(InterruptedException e){
				
				System.out.println("Exception while waiting for results:"+ e.getMessage());
			}
		
		}

	}
	
	public void findFriends() throws SQLException{
		
		
		ArrayList<String> friendInfo = null;
		Map<String, ArrayList<String>> remoteFriends = new HashMap<String, ArrayList<String>>();
			

		// find candidate friends for recommendation algorithm
		System.out.println("Exploring Nodes");
		ArrayList<String> candidates = getExploreNodes();
		
		for(String i: candidates){
			
			friendInfo = contactsStore.get(i);
			
			if(friendInfo.get(FRIEND_URL).equalsIgnoreCase(userHostName)){
				
				calculateScores(friendInfo);
				
			}else{
				
				if(remoteFriends.containsKey(friendInfo.get(FRIEND_URL))){
					
					remoteFriends.get(friendInfo.get(FRIEND_URL)).add(friendInfo.get(CONTACT_GUID));
					
				}else{
					remoteFriends.put(friendInfo.get(FRIEND_URL), new ArrayList<String>());
					remoteFriends.get(friendInfo.get(FRIEND_URL)).add(friendInfo.get(CONTACT_GUID));
				}
			}
			
		}

		sendRequestToRemoteFriends(remoteFriends);
		
		readResponse();
		
			try {
				connection.close();
			} catch (SQLException e) {
				
				System.out.println(CLASS_NAME+":Cannot retrieve instance of DB\n");
			}
		

	}
	
	
}
