package com.recommendation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.LinkedList;


class Node{
	

	private String guid;
	private String hostName;
	private String firstName;
	


	public Node(String guid, String hostName, String firstName){
		this.guid = guid;
		this.hostName = hostName;
		this.firstName = firstName;
	}
	
	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
}

 public class FriendGraph {
	
		
		private Map<String,ArrayList<Node>> adjList = null;
		private ArrayList<String> candidates = null;
		private Queue<String> visited = null;
		
		public FriendGraph(){
			
			adjList = new HashMap<String, ArrayList<Node>>();
			candidates = new ArrayList<String>();
			visited = new PriorityQueue<String>();
			
		}
		
		
		public void addNode(String usr_guid){
			
			
			adjList.put(usr_guid, new ArrayList<Node>());
			
		}
		
		public void addEdge(String usr_guid, String friend_guid, String friend_hostname,
								String firstName){
			
			Node obj = new Node(friend_guid, friend_hostname, firstName);
			adjList.get(usr_guid).add(obj);
			
		}
		
		public void displayGraph(){
			
			Entry<String, ArrayList<Node>> temp = null;
			Iterator<Entry<String, ArrayList<Node>>> it = adjList.entrySet().iterator();
			while(it.hasNext()){
				
				temp = (Entry<String, ArrayList<Node>>)it.next();
				System.out.println("Edge of Node: "+temp.getKey());
				
				for(Node i: temp.getValue()){
					
					System.out.println(i.getGuid());
					
				}				
			}
			
		}
		
		
		
		/*public ArrayList<String> getNodes(String start, int radius){
			
			Map<Integer, ArrayList<String>> map = new HashMap<Integer, ArrayList<String>>();
			ArrayList<String> temp = new ArrayList<String>();
			for(Node i: adjList.get(start)){
				temp.add(i.getGuid());
			}
			map.put(0, temp);
						
			for( int i =0; i< radius; i++){
				ArrayList<String> temp1 = new ArrayList<String>();
					for(String s: map.get(i)){
						
						candidates.add(s);
						temp1.add(s);
					}
					map.put(i+1, temp1);
				
			}
					
			return candidates;
		}*/
		
		public List<String> traverse_bfs(String start, int radius){
			
			int cnt = 0;
			
			List<String> result = new ArrayList<String>();
			Queue<String> toBeChecked = new LinkedList<String>();
			List<String>[] children = (ArrayList<String>[])new ArrayList[radius+2];
			
			String nodeName;
			ArrayList<Node> edges = null;
			
			toBeChecked.add(start);
			
			children[cnt] = new ArrayList<String> ();
			children[cnt].add(start);
			
			while(!toBeChecked.isEmpty() && cnt <= radius){
				
				nodeName = toBeChecked.remove();
				edges = adjList.get(nodeName);
				
				if(edges == null){
					if(!nodeName.equals(start)){
						result.add(nodeName);
					}
					if(children[cnt].contains(nodeName)){
						
						children[cnt].remove(0);
					}
					
					continue;
				}
				
				visited.add(nodeName);
				if(!nodeName.equals(start))
					result.add(nodeName);
				
				
				for(Node i: edges){
					if(!visited.contains(i.getGuid()) && !toBeChecked.contains(i.getGuid())){
						toBeChecked.add(i.getGuid());
						
						
						if(children[cnt].contains(nodeName)){
							if(children[cnt+1] == null){
								children[cnt+1] = new ArrayList<String>();
							}
							children[cnt+1].add(i.getGuid());
						}					
					}
				}
				
				if(children[cnt].contains(nodeName)){
					
					children[cnt].remove(0);
					if(children[cnt].size() == 0)
						++cnt;
				}

			}		
			return result;
		}
		
		
		public static void main(String args[]){
			
			FriendGraph fg = new FriendGraph();
			
			fg.addNode("a");
			fg.addEdge("a", "b", "local", "first");
			
			fg.addNode("b");
			fg.addEdge("b", "c", "local", "first");
			fg.addEdge("b", "a", "local", "first");
			
			
			fg.addNode("c");
			fg.addEdge("c", "b", "local", "first");
			
			fg.addNode("d");
			fg.addEdge("d", "e", "local", "first");
			
			fg.addNode("e");
			fg.addEdge("e", "d", "local", "first");
			
			fg.displayGraph();
			
			
			List<String> res = fg.traverse_bfs("a", 2);
			System.out.println("Bfs Result:");
			for(String i: res)
				System.out.println(i);
			
			
		}

	}


		
		
		


