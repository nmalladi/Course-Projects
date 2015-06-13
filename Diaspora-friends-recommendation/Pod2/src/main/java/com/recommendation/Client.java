package com.recommendation;

import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;

public class Client implements Runnable{
	
	private RecommendationAlgorithm algo = null;
	private RecommendationAlgorithm01 algo1 = null;
	
	private BlockingQueue<String> queue= null;
	private int mode = 1;
	
	
	public Client(BlockingQueue<String> queue, int mode){
		this.queue = queue;
		this.mode = mode;
	}
	
	public void run(){
		
		if(mode == 0){
			algo = new RecommendationAlgorithm(queue);
			try {
				
				System.out.println("Starting Friend Recommendation Algorithm");
				algo.login();
				algo.buildGraph();
				algo.findFriends();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				System.out.println("Exception in Client: Algo"+e.getMessage());
			}
			
		}else{
			algo1 = new RecommendationAlgorithm01(queue);
			System.out.println("Starting Friend Recommendation Algorithm");
			algo1.login();
			algo1.buildTestData();
			algo1.buildGraph();
			algo1.findFriends();
		}

	}

}
