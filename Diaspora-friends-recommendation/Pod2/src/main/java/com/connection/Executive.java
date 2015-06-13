package com.connection;

import java.sql.SQLException;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.recommendation.Client;
import com.recommendation.Receiver;
import com.recommendation.RecommendationAlgorithm;
import com.recommendation.RequestProcessor;



public class Executive {
	
	public static void main(String args[]){
		
		
		int mode = 1;
		
		
		BlockingQueue<String> resultQueue = new LinkedBlockingQueue<String>();
		BlockingQueue<String> requestQueue = new LinkedBlockingQueue<String>();
		
		Receiver receiver = new Receiver(30006, requestQueue, resultQueue);
		Thread serverThread = new Thread(receiver);
		serverThread.start();
		
		RequestProcessor requestProcessor = new RequestProcessor(requestQueue);
		Thread requestProcThread = new Thread(requestProcessor);
		requestProcThread.start();

		
		System.out.println("Please enter the running mode");
		System.out.println("Enter 0 for Production Mode");
		System.out.println("Enter 1 for Simulation Mode");
		Scanner scanner = new Scanner(System.in);
		mode = scanner.nextInt();
		
		
		while(true){
			System.out.println("Enter 'start' to begin algo");
			System.out.println("Enter 'stop' to terminate");
			System.out.println("Enter 'restart' to run again");
			
			String s1 = scanner.next();
			
			if(s1.equalsIgnoreCase("start")){
				
				Client client = new Client(resultQueue, mode);
				Thread clientThread = new Thread(client);
				clientThread.start();
			}else if(s1.equalsIgnoreCase("stop")){
				
				try {
					resultQueue.put("stop");
					requestQueue.put("stop");
					
					/*//To-DO: Better way to terminate the server
					DiasporaClientImpl dispClient = new DiasporaClientImpl();
					dispClient.connect("localhost", 30005);
					dispClient.sendRequest(ClientInterface.RequestType.FRIEND, "</terminate>");*/
					
					//serverThread.stop();
					
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				scanner.close();
				return;
				
			}else if(s1.equalsIgnoreCase("restart")){
				
				try {
					resultQueue.put("stop");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Client client = new Client(resultQueue, mode);
				Thread clientThread = new Thread(client);
				clientThread.start();
				
			}
			
			
			
		}
		
		
		/*while(true){
			
			Client client = new Client(resultQueue, mode);
			Thread clientThread = new Thread(client);
			clientThread.start();
			
			System.out.println("Enter 'stop' to terminate or 'restart' to run again");
			s1 = scanner.next();
			
			if(s1.equalsIgnoreCase("restart")){
				
				try {
					resultQueue.put("stop");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				continue;
				
			}else if(s1.equalsIgnoreCase("stop")){
				
				try {
					resultQueue.put("stop");
					requestQueue.put("stop");
					
					//To-DO: Better way to terminate the server
					DiasporaClientImpl dispClient = new DiasporaClientImpl();
					dispClient.connect("localhost", 30005);
					dispClient.sendRequest(ClientInterface.RequestType.FRIEND, "</terminate>");
					
					//serverThread.stop();
					
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				scanner.close();
				return;
				
			}

		}*/
	
	}

}
