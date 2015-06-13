package com.recommendation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.connection.ClientInterface;
import com.connection.DiasporaClientImpl;

public class Sender implements Runnable {
	
	Map<String, String> requests = null;
	ClientInterface client = null;
	
	public Sender(Map<String, String> inVal){
		
		requests = inVal;
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
		
		Iterator it = requests.entrySet().iterator();
		
		while(it.hasNext()){
			
			Entry<String, String> val = (Entry<String, String>) it.next();
			
			ArrayList<String> params = splitIp(val.getKey());
			
			String ip = params.get(0);
			int port = Integer.parseInt(params.get(1));

			
			client.connect(ip, port);
			client.sendRequest(ClientInterface.RequestType.FRIEND, val.getValue());

		}
		
	}

}
