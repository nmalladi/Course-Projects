package com.connection;

public interface ClientInterface {
	
	public static enum RequestType{
		FRIEND("friends");
		
		private final String value;
		RequestType(String value){
			this.value = value;
		}
		
		public String getRequestType(){
			return value;
		}
		
	}
	
	
	public void connect(final String hostname, final int port);
	
	public void sendRequest(RequestType type, String message);
	
}
