package com.connection;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class DiasporaServerImpl implements ServerInterface {
	
	private ServerSocket server;
	private Socket clientSocket;
	private int serverPort;
	private Thread runningThread;
	private boolean close = false;
	
	private BlockingQueue<String> requestQueue = null;
	private BlockingQueue<String> resultQueue = null;
	
	public DiasporaServerImpl(int port, BlockingQueue<String> requestQueue, 
			BlockingQueue<String> resultQueue){
		serverPort = port;
		this.requestQueue = requestQueue;
		this.resultQueue = resultQueue;
		
	}


/*	public void processRequest(){
		
		BufferedInputStream in;
		
		try {
			
			  in = new BufferedInputStream(clientSocket.getInputStream());

		      InputStreamReader inReader = new InputStreamReader(in, "US-ASCII");
		      
		      StringBuffer sb = new StringBuffer();

		      int c;
		      while ( (c = inReader.read()) != 13)
		    	  sb.append( (char) c);

		      inReader.close();
		      inReader.close();
		      clientSocket.close();
		      
			  System.out.println("Result Received" + sb);
			  
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}*/
	
	private synchronized void processRequest(){
		
		BufferedInputStream in;
		
		try {
			
			  in = new BufferedInputStream(clientSocket.getInputStream());

		      InputStreamReader inReader = new InputStreamReader(in, "US-ASCII");
		      
		      StringBuffer sb = new StringBuffer();

		      int c;
		      while ( (c = inReader.read()) != 13)
		    	  sb.append( (char) c);

		      inReader.close();
		      inReader.close();
		      clientSocket.close();
		      
			  //System.out.println("Result Received" + sb);
		      
		      String resposneStr = sb.toString();
		      
		      if(resposneStr.contains("<result>")){
		    	  
		    	  resultQueue.put(resposneStr);
		      }else{
		    	  requestQueue.put(sb.toString());
		    	  
		      }
		      
		      
			  
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized void startServer(){
		
		try {
			
			server = new ServerSocket(serverPort);
			while(!isStopped()){		
				clientSocket =  server.accept();
				processRequest();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Receive failed"+e.getMessage());
		}
		
		
	}
	
	
	private synchronized boolean isStopped(){
		
		return this.close;
	}
	
	public synchronized void stopServer(){
		close = true;		
		try{
			server.close();

		}catch(IOException e){
			System.out.println("Server stop failed"+e.getMessage());
		}
	}

}
