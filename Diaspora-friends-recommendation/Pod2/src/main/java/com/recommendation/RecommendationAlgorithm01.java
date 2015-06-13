package com.recommendation;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;

import com.db.DBConnector;
import com.db.DBFactory;
import com.db.DiasporaQueries;

public class RecommendationAlgorithm01 {
	
	
	private static final String CLASS_NAME = "RecommendationAlgorithm01";
	private static final int FRIEND_GUID = 0;
	private static final int FRIEND_NAME = 1;
	private static final int FRIEND_URL = 2;
	private static final int FRIEND_LOCATION = 3;
	private static final int FRIEND_DISPHANDLE = 4;
	private static final int CONTACT_GUID = 0;
	
	private final int USER_GUID = 0;
	private final int USER_NAME = 1;
	private final int USER_URL = 2;
	private final int USER_LOCATION = 3;
	private final int USER_DISPHANDLE = 4;
	
	
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
	private Map<String, ArrayList<String>> testData;
	private Map<String, ArrayList<String>> testDataTags;
	

	public RecommendationAlgorithm01(BlockingQueue<String> queue){
		
		scores = new HashMap<String, Float>();
		contactsStore = new HashMap<String, ArrayList<String>>();
		testData = new HashMap<String, ArrayList<String>>();
		testDataTags = new HashMap<String, ArrayList<String>>();
		
		response = queue;
		protocol = ProtocolImpl.getInstace();
		
	}
	
	public void buildTestData(){
		
		
		ArrayList<String> t1 = new ArrayList<String>();
		t1.add("usr1");t1.add("nik1");t1.add("http://localhost:30005/");t1.add("syracuse");t1.add("nik1@localhost:30005");
		testData.put(new String("usr1"), t1);
		
		ArrayList<String> tags1 = new ArrayList<String>();
		tags1.add("cricket");tags1.add("football");tags1.add("dota");
		testDataTags.put(new String("usr1"), tags1);
		
		
		ArrayList<String> t5 = new ArrayList<String>();
		t5.add("usr5");t5.add("nik5");t5.add("http://localhost:30006/");t5.add("syracuse");t5.add("nik5@localhost:30006");
		testData.put(new String("usr5"), t5);
		
		ArrayList<String> tags5 = new ArrayList<String>();
		tags5.add("cricket");tags5.add("football");tags5.add("dota");
		testDataTags.put(new String("usr5"), tags5);
		
		ArrayList<String> t6 = new ArrayList<String>();
		t6.add("usr6");t6.add("nik6");t6.add("http://localhost:30006/");t6.add("syracuse");t6.add("nik6@localhost:30006");
		testData.put(new String("usr6"), t6);
		
		ArrayList<String> tags6 = new ArrayList<String>();
		tags6.add("cricket");tags6.add("football");tags6.add("dota");
		testDataTags.put(new String("usr6"), tags6);
		

	}
	
/*	public void calculateScores(Node person) throws SQLException{
		
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
		
	}*/
	
	public void calculateScores(final ArrayList<String> friendInfo){
		
		float score = 0;

		ArrayList<String> tags = testDataTags.get(friendInfo.get(FRIEND_GUID));
		String location = friendInfo.get(FRIEND_LOCATION);
		
		tags.retainAll(userTags);
		score += tags.size() * 0.7;
		
		if(userLocation.equalsIgnoreCase(location)){
			score += tags.size() * 0.3;
		}
		
		scores.put(friendInfo.get(FRIEND_DISPHANDLE), score);
		
	}
	
	private ArrayList<String> getExploreNodes(String guid){
		
		ArrayList<String> result = null;
		
		result = (ArrayList<String>)fg.traverse_bfs(guid, bfs_level);
		
		return result;
		
	}
	
	public void buildGraph(){
		
		fg = new FriendGraph();
		fg.addNode("usr5");
		fg.addEdge("usr5", "usr1", testData.get("usr1").get(USER_URL), testData.get("usr1").get(USER_NAME));
		fg.addEdge("usr5", "usr6", testData.get("usr6").get(USER_URL), testData.get("usr6").get(USER_NAME));
		
		
		fg.addNode("usr6");
		fg.addEdge("usr6", "usr5", testData.get("usr5").get(USER_URL), testData.get("usr5").get(USER_NAME));
		

	}
	
	public void login(){
		
		//To-DO: HostName will be retrieved from login
		userHostName = "http://localhost:30006/";
		userGUID = "usr5";
		userLocation = "syracuse";
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
	
	
	public void sendRequestToRemoteFriends(Map<String, ArrayList<String>> remoteFriends){
		
		Map<String, String> remoteRequest = new HashMap<String, String>();

        userLocation = testData.get(userGUID).get(USER_LOCATION);
		
		StringBuffer sb = new StringBuffer("<tags>");
		for(int j = 0; j < userTags.size(); j++){
			sb.append(userTags.get(j));
			if(j < userTags.size()-1)
				sb.append(",");
		}
		sb.append("</tags>");
		
		sb.append("<loc>"+userLocation+"</loc>");
		sb.append("<suser>"+userGUID+"</suser>");
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
	
public void parseResponse(String resultInfo){
		
		
		int pos1 = resultInfo.indexOf('>');
		int pos2 = resultInfo.indexOf('<', pos1);
		
		String s1 = resultInfo.substring(pos1+1, pos2);	
		
		String tokens[] = s1.split(",");
		
		
		for(String i: tokens){
			
			if(!i.equals("")){
				
				String temp[] = i.split(":");
				scores.put(temp[0]+":"+temp[1], Float.parseFloat(temp[2]));
			}
			
		}
		
	}
	
	public void displayResults(){
		
		ValueComparator vc = new ValueComparator(scores);
		TreeMap<String,Float> sortedScores = new TreeMap<String,Float>();
		sortedScores.putAll(scores);
		
		
		
		System.out.println("Results:"+sortedScores.descendingMap());
		
	}
	
	public void readResponse(){
		
		while(true){
			
			try{
				// Add the result to scores
				String s1 = response.take();
				if(s1.equalsIgnoreCase("stop")){
					return;
				}
				
				//System.out.println(s1);
				parseResponse(s1);
				displayResults();
				
			}catch(InterruptedException e){
				
				System.out.println("Exception while waiting for results:"+ e.getMessage());
			}
		
		}

	}
	
	public void findFriends() {
		
		
		ArrayList<String> friendInfo = null;
		Map<String, ArrayList<String>> remoteFriends = new HashMap<String, ArrayList<String>>();
			

		// find candidate friends for recommendation algorithm
		System.out.println("Exploring Nodes");
		ArrayList<String> friends = getExploreNodes(userGUID);
		
		ArrayList<String> candidates = new ArrayList<String>();
		for(String i: friends){
			
			candidates.addAll(getExploreNodes(i));
		}
		userTags = testDataTags.get(userGUID);
		
		for(String i: candidates){
			
			if(i.equalsIgnoreCase(userGUID))
				continue;
			
			friendInfo = testData.get(i);
			
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
		

	}

}

//Code snippet from stackoverflow
class ValueComparator implements Comparator<String> {

 Map<String, Float> base;
 public ValueComparator(Map<String, Float> base) {
     this.base = base;
 }

 // Note: this comparator imposes orderings that are inconsistent with equals.    
 public int compare(String a, String b) {
     if (base.get(a) >= base.get(b)) {
         return -1;
     } else {
         return 1;
     } // returning 0 would merge keys
 }
}
