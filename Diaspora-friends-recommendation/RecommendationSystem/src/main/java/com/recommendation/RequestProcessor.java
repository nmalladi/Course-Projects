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
		podUrl = "http://localhost:30005/";
		
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
				protocol.sendResponse("http://localhost:30006/", message);
				
				
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
		
		ArrayList<String> tags1 = new ArrayList<String>();
		tags1.add("cricket");tags1.add("football");tags1.add("dota");
		testDataTags.put(new String("usr1"), tags1);
		
		
		ArrayList<String> t2 = new ArrayList<String>();
		t2.add("usr2");t2.add("nik2");t2.add("http://localhost:30005/");t2.add("syracuse");t2.add("nik2@localhost:30005");
		testData.put(new String("usr2"), t2);
		
		ArrayList<String> tags2 = new ArrayList<String>();
		tags2.add("espn");tags2.add("arts");tags2.add("boxing");
		testDataTags.put(new String("usr2"), tags2);
		
		ArrayList<String> t3 = new ArrayList<String>();
		t3.add("usr3");t3.add("nik3");t3.add("http://localhost:30005/");t3.add("syracuse");t3.add("nik3@localhost:30005");
		testData.put(new String("usr3"), t3);
		
		ArrayList<String> tags3 = new ArrayList<String>();
		tags3.add("cricket");tags3.add("skydiving");tags3.add("dota");
		testDataTags.put(new String("usr3"), tags3);
		
		ArrayList<String> t4 = new ArrayList<String>();
		t4.add("usr4");t4.add("nik4");t4.add("http://localhost:30005/");t4.add("syracuse");t4.add("nik4@localhost:30005");
		testData.put(new String("usr4"), t4);
		
		ArrayList<String> tags4 = new ArrayList<String>();
		tags4.add("cricket");tags4.add("football");tags4.add("dota");
		testDataTags.put(new String("usr4"), tags4);
		
		ArrayList<String> t5 = new ArrayList<String>();
		t5.add("usr5");t5.add("nik5");t5.add("http://localhost:30006/");t5.add("syracuse");t5.add("nik5@localhost:30006");
		testData.put(new String("usr5"), t5);
		
		ArrayList<String> tags5 = new ArrayList<String>();
		tags5.add("cricket");tags5.add("football");tags5.add("dota");
		testDataTags.put(new String("usr5"), tags5);
		

	}
	
	private void exploreNodes(String userGUID){
		
		ArrayList<String> res = null;
		
		res = (ArrayList<String>)fg.traverse_bfs(userGUID, 1);
		
		result.addAll(res);
		
	}
	
	
	
	public void buildGraph(){
		

		fg = new FriendGraph();
		fg.addNode("usr1");
		fg.addEdge("usr1", "usr2", testData.get("usr2").get(USER_URL), testData.get("usr2").get(USER_NAME));
		fg.addEdge("usr1", "usr3", testData.get("usr3").get(USER_URL), testData.get("usr3").get(USER_NAME));
		fg.addEdge("usr1", "usr5", testData.get("usr5").get(USER_URL), testData.get("usr5").get(USER_NAME));
		
		
		fg.addNode("usr2");
		fg.addEdge("usr2", "usr1", testData.get("usr1").get(USER_URL), testData.get("usr1").get(USER_NAME));
		
		fg.addNode("usr3");
		fg.addEdge("usr3", "usr1", testData.get("usr1").get(USER_URL), testData.get("usr1").get(USER_NAME));
		fg.addEdge("usr3", "usr4", testData.get("usr4").get(USER_URL), testData.get("usr4").get(USER_NAME));
		
		
		fg.addNode("usr4");
		fg.addEdge("usr4", "usr3", testData.get("usr3").get(USER_URL), testData.get("usr3").get(USER_NAME));
		
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
