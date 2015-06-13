package com.recommendation;

import java.util.Map;

public interface RecommendationAlgoProtocol {
	
	public void processRequest(String message);
	public void sendRequest(Map<String,String> message);
	public void sendResponse(String url, String message);
}
