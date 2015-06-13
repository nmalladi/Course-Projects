package com.recommendation;

import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

import com.connection.ClientInterface;
import com.connection.DiasporaClientImpl;
import com.connection.DiasporaServerImpl;


public class ProtocolImpl implements RecommendationAlgoProtocol{
	
		
	BlockingQueue<String> requestQueue = null;
	BlockingQueue<String> responseQueue = null;
	
	private static RecommendationAlgoProtocol protocol = null;

	private ProtocolImpl(){
		

	}
	
	public static RecommendationAlgoProtocol getInstace(){
		
		if(protocol == null){
			protocol = new ProtocolImpl();
		}
		
		return protocol;
		
	}
	
	
	public void setRequestQueue(BlockingQueue<String> val){
		
		requestQueue = val;

	}
	
	public void setResponseQueue(BlockingQueue<String> val){
		
		
		responseQueue = val;
		
	}
	

	public void sendRequest(Map<String,String> messages){
		
		Sender sender = new Sender(messages);
		Thread t1 = new Thread(sender);
		t1.start();
	}
	
	public void sendResponse(String url, String message){
		
		SenderRP sender = new SenderRP(url, message);
		Thread t1 = new Thread(sender);
		t1.start();
		
	}

	public void processRequest(String message) {
		// TODO Auto-generated method stub
		
	}
	

}
