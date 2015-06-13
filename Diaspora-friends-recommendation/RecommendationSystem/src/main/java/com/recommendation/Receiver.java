package com.recommendation;

import java.util.concurrent.BlockingQueue;

import com.connection.DiasporaServerImpl;
import com.connection.ServerInterface;

public class Receiver implements Runnable {
	
	ServerInterface server = null;
	private Thread runningThread;
	
	
	public Receiver(int port, BlockingQueue<String> requestQueue, 
			BlockingQueue<String> resultQueue){
		
		server = new DiasporaServerImpl(port, requestQueue, resultQueue);
	
	}
	
	
	public void run(){
		
		synchronized (this) {
			this.runningThread = Thread.currentThread();
		}
		
		server.startServer();
		
		
	}
	
	public void stop(){
		server.stopServer();
	}
	
	
}
