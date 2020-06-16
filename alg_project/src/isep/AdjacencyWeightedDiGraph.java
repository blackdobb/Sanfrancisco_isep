package isep;

import java.util.Set;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.PriorityQueue;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AdjacencyWeightedDiGraph<Vertex, Edge> 
	implements WeightedDiGraph<Vertex, Edge> {
	
	protected Set<Vertex> vertices= new HashSet<Vertex>();
	protected Set<Edge> edges= new HashSet<Edge>();
	protected Map<Edge, Float> edgeToWeight = new HashMap<Edge, Float>();
	protected Map<Vertex, List<Edge> > vertexToEdges= new HashMap<Vertex, List<Edge> >();
	private Map<Edge, Vertex> edgeToSrc= new HashMap<Edge, Vertex>();
	private Map<Edge, Vertex> edgeToDest= new HashMap<Edge, Vertex>();
	private Map<String, Vertex> nameToVertex= new HashMap<String, Vertex>();
	private Map<Vertex, String> vertexToName= new HashMap<Vertex, String>();
	
	List<List<Edge>> global_AllSP = null;
	List<Edge> global_diameter = null;
	Map<Edge,Float> global_betweenness = new HashMap<Edge, Float>();
	
	private class Node implements Comparable<Node>{
		public final Vertex v;
		public Vertex pathFrom;
		public Edge e;
		public float dis;
		
		public Node(Vertex vertex, Vertex from, Edge edge, float distance) {
			v = vertex;
			e = edge;
			pathFrom = from;
			dis = distance;
		}
		
		public int compareTo(Node other) {
			if((float)this.dis < (float)other.dis) return 1;
			return -1;
		}
		
	}
	
	public AdjacencyWeightedDiGraph(){
	}
	
	public void addVertex(Vertex v){
		if(!vertices.contains(v)){
			vertices.add(v);
			vertexToEdges.put(v, new ArrayList<Edge>());
		}
	}
	
	public void removeVertex(Vertex v) {
		vertices.remove(v);
	}
	
	public List<Vertex> getVertices(){
		return new ArrayList<Vertex>(vertices);
	}
	
	public void addEdge(Edge e, float weight, Vertex src, Vertex dest){
		//System.out.println("Add edge " + e + "\n");
		addVertex(src);
		addVertex(dest);
		edges.add(e);
		edgeToWeight.put(e, weight);
		edgeToSrc.put(e, src);
		edgeToDest.put(e, dest);
		vertexToEdges.get(src).add(e);
	}
	
	public void removeEdge(Edge e, Vertex src, Vertex dest){
		//System.out.println("Remove edge " + e + "\n");
		edges.remove(e);
		edgeToWeight.remove(e);
		edgeToSrc.remove(e);
		edgeToDest.remove(e);
		vertexToEdges.get(src).remove(e);
	}
	
	public List<Edge> getEdges(){
		return new ArrayList<Edge>(edges);
	}
	
	public List<Vertex> getAdjacentVertices(Vertex src){
		List<Vertex> res= new ArrayList<Vertex>();
		for(Edge e : vertexToEdges.get(src)){
			res.add(edgeToDest.get(e));
		}
		return res;
	}
	
	public void nameVertex(String name, Vertex v) {
		vertexToName.put(v, name);
	}
	
	public String getNameByVertex(Vertex v){
		return vertexToName.get(v);
	}
	
	// should use java.util.Optional
	public Vertex getVertexByName(String name) {
		for(Map.Entry<Vertex, String> e: vertexToName.entrySet()){
			if(e.getValue().equals(name)){
				return e.getKey();
			}
		}
		return null;
	}
	
	public List<String> getNames(){
		return new ArrayList<String>(vertexToName.values());
	}
	
	public boolean areConnected(Vertex src, Vertex dest){
		// TODO !
		Map<Vertex, Boolean> visit = new HashMap<Vertex, Boolean>();
		List<Vertex> temp = getVertices();
		for(Vertex v : temp){
			visit.put(v, null);
		}
		
		Queue<Vertex> queue = new LinkedList<Vertex>();
		queue.add(src);
		visit.put(src, true);
		
		Vertex cur_vertex = src;
		while(!queue.isEmpty()) {
			cur_vertex = queue.poll();
			//System.out.println("poll " + cur_vertex + "\n");
			if(cur_vertex.equals(dest)) return true;
			for(Vertex v : getAdjacentVertices(cur_vertex)) {
				if(visit.get(v) == null) {
					//System.out.println("visit "+ v +"\n");
					visit.put(v, true);
					queue.add(v);
				}
			}
		}
		return false;
	}
	
	public boolean areConnected(String src, String dest){
		return areConnected(getVertexByName(src), getVertexByName(dest));
	}
/*
	public List<Vertex> shortestPath(Vertex src, Vertex dest){
		// Dijkstra with priority queue
		Map<Vertex, Edge> visit = new HashMap<Vertex, Edge>();
		
		Queue<Node> pq = new PriorityQueue<Node>();
		Node cur = new Node(src, src, null, 0);
		
		pq.add(cur);
		
		while(!pq.isEmpty()) {
			cur = pq.poll();
			//System.out.println("poll " + cur_vertex + "\n");
			
			if(visit.get(cur.v) == null) {
				visit.put(cur.v, cur.e);
				if(cur.v.equals(dest)) break;
				for(Edge e : vertexToEdges.get(cur.v)) { //update distance
					pq.add(new Node(edgeToDest.get(e), cur.v, cur.dis+edgeToWeight.get(e)));
				}
			}
			
		}
		
		if(!cur.v.equals(dest)) return null;
		
		List<Vertex> res = new ArrayList<Vertex>();
		Vertex v = cur.v;
		while(!v.equals(src)) {
			//System.out.println("track "+ cur_vertex +"\n");
			res.add(0, v);
			v = visit.get(v);
		}
		res.add(0, v);
		return res;
	}
*/
	public List<List<Edge>> shortestPathFrom(Vertex src){
		// Dijkstra with priority queue
		Map<Vertex, Float> distance = new HashMap<Vertex, Float>();
		Map<Vertex, Edge> adjacent = new HashMap<Vertex, Edge>();
		Map<Vertex, Boolean> visit = new HashMap<Vertex, Boolean>();
		for(Vertex v : getVertices()){
			visit.put(v, false);
		}
		
		
		Queue<Node> pq = new PriorityQueue<Node>();
		Node cur = new Node(src, src, null, 0);
		//the Last para should be 0. But the WeightType doesn't allow 0;
		pq.add(cur);
		
		while(!pq.isEmpty()) {

			cur = pq.poll();
			//System.out.println("visit.cur is "+visit.get(cur.v));
			//System.out.println("cur is "+cur.v);
			
			if(visit.get(cur.v) == true) continue;
			
			visit.put(cur.v, true);
			adjacent.put(cur.v, cur.e);
			distance.put(cur.v, cur.dis);
			//System.out.println("poll " + cur_vertex + "\n");
			
			for(Edge e : vertexToEdges.get(cur.v)) { //update distance
				
				//Has been visited, next edge
				if(visit.get(edgeToDest.get(e)) == true) continue;
				pq.add(new Node(edgeToDest.get(e), cur.v, e, cur.dis + edgeToWeight.get(e)));
			}
		}
			
		
		List<List<Edge>> res = new ArrayList<List<Edge>>();
		
		for(Vertex v :  getVertices()) {
			
			if(v.equals(src)) continue;
			
			LinkedList<Edge> path = new LinkedList<Edge>();
			while(v != null && !v.equals(src) && adjacent.get(v) != null) {
				//System.out.println("track "+ cur_vertex +"\n");
				path.add(0, adjacent.get(v));
				v = edgeToSrc.get(adjacent.get(v));
			}
			res.add(path);
		}

		return res;
	}
	
	
	public List<List<Edge>> collectAllSP() {
		
		if(global_AllSP != null) return global_AllSP;
		
		List<List<Edge>> AllSP = new ArrayList<List<Edge>>();
		for(Vertex src : getVertices()) {
			List<List<Edge>> path = shortestPathFrom(src);
			AllSP.addAll(path);
		}
		return global_AllSP = AllSP;
	}
	
	
	public List<Edge> diameterWeighted(){
		
		if(global_diameter != null) return global_diameter;
		
		List<Edge> diameter = new LinkedList<Edge>();
		float length = 0;
		
		for(List<Edge> path : collectAllSP()) {
			
			float curlength = 0;
			
			for(Edge e : path) {
				if(e==null) continue;
				curlength += edgeToWeight.get(e);
			}
			
			if(curlength > length) {
				length = curlength;
				diameter = path;
			}
		}
		
		return global_diameter = diameter;
	}
	
	
	public String diameterToString() {
		StringBuilder longestPathStr = new StringBuilder();
		StringBuilder lengthSubPath = new StringBuilder();
		longestPathStr.append("Shortest path: ");
		lengthSubPath.append("Lengths of sub-paths: ");

		List<Edge> diameter = diameterWeighted();
		longestPathStr.append(vertexToName.get( edgeToSrc.get(diameter.get(0))));
		float length = 0f;
		for(Edge e : diameter) {
			longestPathStr.append("=>" + vertexToName.get(edgeToDest.get(e)));
			lengthSubPath.append(vertexToName.get(edgeToSrc.get(e)) + "-" + vertexToName.get(edgeToDest.get(e)) 
				+":"+edgeToWeight.get(e)+"m;");
			length += edgeToWeight.get(e);
		}
		
		StringBuilder res = new StringBuilder();
		res.append(longestPathStr + "\n");
		res.append(lengthSubPath + "\n");
		res.append("Total lengthof the path: "+length+"m.\n");
		return res.toString();
	}
	
	
	public void calBetweenness() {
		global_betweenness = new HashMap<Edge, Float>();
		for(Edge e : getEdges()) {
			global_betweenness.put(e, 0f);
		}
		for(List<Edge> SP : collectAllSP()) {
			float sumWeight = 0;
			for(Edge e : SP) {
				sumWeight += edgeToWeight.get(e);
			}
			for(Edge e : SP) {
				global_betweenness.put(e, global_betweenness.get(e) + sumWeight);
			}
		}
	}
	
	public List<List<Vertex>> findClusters(Map<Vertex,Boolean> visit) {
		List<List<Vertex>> clusters = new LinkedList<List<Vertex>>();
		//System.out.println("To find clusters");
		//BFS test connection
		
		
		//find a new cluster
		Vertex cur_vertex = null;
		for(Vertex v : getVertices()) {
			if(visit.get(v) == false) {
				//System.out.println("Still have "+v);
				cur_vertex = v;
				break;
			}
		}
		
		//no more clusters
		if(cur_vertex == null) {
			return clusters;
		}
		
		clusters.add(0, new LinkedList<Vertex>());
		Queue<Vertex> queue = new LinkedList<Vertex>();
		
		queue.add(cur_vertex);
		visit.put(cur_vertex, true);
		
		
		while(!queue.isEmpty()) {
			//System.out.println("Not Empty");
			cur_vertex = queue.poll();
			
			
			clusters.get(0).add(cur_vertex);
			//System.out.println("poll " + cur_vertex + "\n");

			for(Vertex v : getAdjacentVertices(cur_vertex)) {
				if(visit.get(v) == false) {
					//System.out.println("visit "+ v +"\n");
					queue.add(v);
					visit.put(v, true);
				}
			}
		}
		
		for(Vertex v : getVertices()) {
			if(visit.get(v) == false) {
				clusters.addAll(findClusters(visit));
			}
		}
		//System.out.println("a Cluster!");
		return clusters;
	}
	
	public List<List<Vertex>> Graph_Clustering(int remove_num, int cluster_num) {
		//int cluster_num = 9;
		List<List<Vertex>> clusters = new ArrayList<List<Vertex>>();
		
		calBetweenness();
		
		//sort Betweenness
		List<Map.Entry<Edge,Float>> highestBetweenness = new ArrayList<Map.Entry<Edge,Float>>(global_betweenness.entrySet());
		//System.out.println("Betweenness size is " + highestBetweenness.size());
		Collections.sort(highestBetweenness,new Comparator<Map.Entry<Edge,Float>>() {
            //increase
            public int compare(Entry<Edge,Float> o1,
                    Entry<Edge,Float> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
            
        });
		
	/*	
		//remove edges with highest betweenness
		System.out.println("Remove edges");
		for(int i = 0; i < remove_num; i++) {
			//delete the last(highest) element in array instead of the first, optimize.
			Edge e = highestBetweenness.get(highestBetweenness.size()-1).getKey();
			removeEdge(e, edgeToSrc.get(e), edgeToDest.get(e));
			highestBetweenness.remove(highestBetweenness.size()-1);
		}
	*/	
		
		//find clusters
		Map<Vertex,Boolean> visit = new HashMap<Vertex,Boolean>();
		for(Vertex v : getVertices()) {
			visit.put(v, false);
			if(getAdjacentVertices(v).size() == 0) removeVertex(v);
		}
		
		//System.out.println("Find clusters");
		clusters = findClusters(visit);

		//One clusters still, remove more edges.
		while(clusters.size() <= cluster_num) {
			//System.out.println("Remove edges");
			for(int i = 0; i < remove_num; i++) {
				//delete the last(highest) element in array instead of the first, optimize.
				Edge e = highestBetweenness.get(highestBetweenness.size()-1).getKey();
				System.out.println("Remove edge" + e +" " + highestBetweenness.get(highestBetweenness.size()-1).getValue());
				removeEdge(e, edgeToSrc.get(e), edgeToDest.get(e));
				highestBetweenness.remove(highestBetweenness.size()-1);
			}
			//System.out.println("Find clusters");
			for(Vertex v : getVertices()) {
				visit.put(v, false);
				//if(getAdjacentVertices(v).size() == 0) removeVertex(v);
			}
			clusters = findClusters(visit);
		}
		//System.out.println("We have clusters");
		return clusters;
	}
	
	public List<String> VerticesToStrings(List<Vertex> path){
		List<String> strList = new LinkedList<String>();
		for(Vertex v : path) {
			strList.add(getNameByVertex(v));
		}
		return strList;
	}
	
	public String clustersToString(List<List<Vertex>> clusters) {
		StringBuilder clusterStr = new StringBuilder();
		clusterStr.append("There are " + clusters.size()+ " clusters.\n");
		for(List<Vertex> curCluster : clusters) {
			clusterStr.append("{");
			for(String v : VerticesToStrings(curCluster)) {
				clusterStr.append(v + ",");
			}
			clusterStr.append("}\n");
		}
		return clusterStr.toString();
	}
}
