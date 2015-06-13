package com.recommendation;

import java.util.ArrayList;
import java.util.Map;

import com.connection.ClientInterface;
import com.connection.DiasporaClientImpl;

public class SenderRP implements Runnable {
	
	ClientInterface client = null;
	String url;
	String message;
	
	public SenderRP(String url, String message){
		
		this.url = url;
		this.message = message;
		client = new DiasporaClientImpl();
		
	}
	
	private ArrayList<String> splitIp(String IP){
		
		ArrayList<String> result = new ArrayList<String>();
		
		String tokens[] = IP.split("://");
		tokens = tokens[1].split(":");
		
		result.add(tokens[0]);
		
		tokens = tokens[1].split("/");
		result.add(tokens[0]);
		
		return result;
	}
	
	public void run(){
		
		ArrayList<String> params = splitIp(url);
		
		String ip = params.get(0);
		int port = Integer.parseInt(params.get(1));

		
		client.connect(ip, port);
		client.sendRequest(ClientInterface.RequestType.FRIEND, message);
		
		
	}

}
