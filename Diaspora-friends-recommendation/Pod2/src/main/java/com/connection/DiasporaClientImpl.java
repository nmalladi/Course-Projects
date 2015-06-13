package com.connection;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

public class DiasporaClientImpl implements ClientInterface{
	
	private Socket connection = null;
	private String className = "DiasporaClientImpl";

	public void connect(final String hostName, final int port) {
		
		try {
			InetAddress address = InetAddress.getByName(hostName);
			connection = new Socket(address, port);
					
		} catch (UnknownHostException e) {
			
			//e.printStackTrace();
			System.out.println(className+" Connection Failed:"+e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println(className+" Connection Failed:"+e.getMessage());
		} catch (Exception e){
			System.out.println(className+" Connection Failed:"+e.getMessage());
		}
	
	}

	public void sendRequest(RequestType type, String message) {
		
		if(connection != null){
			
			try{
				BufferedOutputStream outStream = new BufferedOutputStream(connection.
				          								getOutputStream());
				OutputStreamWriter outWriter = new OutputStreamWriter(outStream, "US-ASCII");
				
				//String content = "Sending request" + (char)13;
				
				outWriter.write(message);
				outWriter.flush();
			}catch (IOException e) {
				//e.printStackTrace();
				System.out.println(className+" Connection Failed:"+e.getMessage());
			} catch (Exception e){
				System.out.println(className+" Connection Failed:"+e.getMessage());
			}

		}
		
	}
	
	public void processRequest(){
		
		try {
			BufferedInputStream bis = new BufferedInputStream(connection.
		          getInputStream());

		      InputStreamReader isr = new InputStreamReader(bis, "US-ASCII");
		      
		      StringBuffer sb = new StringBuffer();

		      int c;
		      while ( (c = isr.read()) != 13)
		    	  sb.append( (char) c);

				connection.close();
				 System.out.println(sb);
				 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	

}
