package com.recommendation;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import com.db.DBConnector;
import com.db.DiasporaQueries;

public class RequestProcessor implements Runnable{
	
	private BlockingQueue<String> queue = null;
	RecommendationAlgoProtocol protocol = null;
	private FriendGraph fg;
	//private String userGUID;
	private ArrayList<String> result;
	
	
	private ArrayList<String> userTags;
	private String userLocation;
	private String sourceUserID;
	private Map<String, Float> scores;
	private ArrayList<String> currentUsers;
	private String podUrl;
	
	
	private Map<String, ArrayList<String>> testData;
	private Map<String, ArrayList<String>> testDataTags;
	
	
	DBConnector sqlConnector = null;
	Connection connection = null;
	
	private final int USER_GUID = 0;
	private final int USER_NAME = 1;
	private final int USER_URL = 2;
	private final int USER_LOCATION = 3;
	private final int USER_DISPHANDLE = 4;
	
	
	public RequestProcessor(BlockingQueue<String> queue){
		this.queue = queue;
		fg = new FriendGraph();
		result = new ArrayList<String>();
		testData = new HashMap<String, ArrayList<String>>();
		testDataTags = new HashMap<String, ArrayList<String>>();
		scores = new HashMap<String, Float>();
		
		protocol = ProtocolImpl.getInstace();
		
		podUrl = "http://localhost:30006/";
		
		buildTestData();
	}
	
	
	
	public void run(){
		
		
		buildGraph();
		
		try {
			
			while(true){
				String message = queue.take();
				if(message.equalsIgnoreCase("stop")){
					return;
				}
				
				message = findFriendsRemote(message);
				protocol.sendResponse("http://localhost:30005/", message);
				
				
			}
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void buildTestData(){
		
		
		ArrayList<String> t1 = new ArrayList<String>();
		t1.add("usr1");t1.add("nik1");t1.add("http://localhost:30005/");t1.add("syracuse");t1.add("nik1@localhost:30005");
		testData.put(new String("usr1"), t1);
		
		
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
	
	private void exploreNodes(String userGUID){
		
		ArrayList<String> res = null;
		
		res = (ArrayList<String>)fg.traverse_bfs(userGUID, 1);
		
		result.addAll(res);
		
	}
	
	
	
	public void buildGraph(){
		

		fg = new FriendGraph();
		fg.addNode("usr5");
		fg.addEdge("usr5", "usr1", testData.get("usr1").get(USER_URL), testData.get("usr1").get(USER_NAME));
		fg.addEdge("usr5", "usr6", testData.get("usr6").get(USER_URL), testData.get("usr6").get(USER_NAME));
		
		
		fg.addNode("usr6");
		fg.addEdge("usr6", "usr5", testData.get("usr5").get(USER_URL), testData.get("usr5").get(USER_NAME));
		
	}
	
	
	public void calculateTestScores(String guid){
		
		float score = 0;
		
		ArrayList<String> tags = testDataTags.get(guid);
		String location = testData.get(guid).get(USER_LOCATION);
		
		tags.retainAll(userTags);
		score += tags.size() * 0.7;
		
		if(userLocation.equalsIgnoreCase(location)){
			 score += tags.size() * 0.3;
		 }
		
		scores.put(guid, score);

	}
	
	private void parseMessage(String message){
		

		currentUsers = new ArrayList<String>();
		userTags = new ArrayList<String>();
		
		int pos1 = message.indexOf('>');
		int pos2 = message.indexOf('<', pos1);
		
		String s1 = message.substring(pos1+1, pos2);
		String[] vals = s1.split(",");
		for(String s: vals){
			currentUsers.add(s);
		}
		
		pos1 = message.indexOf("<tags>");
		pos2 = message.indexOf("</tags>");
		
		s1 = message.substring(pos1+6,pos2);
		String[] vals1 = s1.split(",");
		for(String s: vals1){
			userTags.add(s);
		}
		
		pos1 = message.indexOf("<loc>");
		pos2 = message.indexOf("</loc>");
		
		s1 = message.substring(pos1+5,pos2);
		userLocation = s1;
		
		pos1 = message.indexOf("<suser>");
		pos2 = message.indexOf("</suser>");
		s1 = message.substring(pos1+7, pos2);
		sourceUserID = s1;
		
	}
	
	
	public String findFriendsRemote(String message) {
			
			parseMessage(message);
					
			for(String i: currentUsers){
				
				exploreNodes(i);
					
			}
			
			for(String i: result){
				if(testData.get(i).get(USER_URL).equalsIgnoreCase(podUrl)){
					calculateTestScores(i);
				}
			}
			
			StringBuffer sb = new StringBuffer("<result>");
			
			if(scores != null){
				Iterator it = scores.entrySet().iterator();
				while(it.hasNext()){
					
					Map.Entry pair = (Map.Entry)it.next();
					sb.append(testData.get(pair.getKey()).get(USER_DISPHANDLE)+":"+pair.getValue());
					sb.append(",");
					
				}
			}
			
			sb.append("</result>");
			sb.append((char)13);
			
			System.out.println("Result:"+sb.toString());
			return sb.toString();
			
			
		}

}
