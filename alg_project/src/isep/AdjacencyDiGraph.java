package isep;

import java.util.Set;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.PriorityQueue;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

public class AdjacencyDiGraph<Vertex, Edge> implements DiGraph<Vertex, Edge> {
	protected Set<Vertex> vertices= new HashSet<Vertex>();
	protected Set<Edge> edges= new HashSet<Edge>();
	protected Map<Vertex, List<Edge> > vertexToEdges= new HashMap<Vertex, List<Edge> >();
	private Map<Edge, Vertex> edgeToSrc= new HashMap<Edge, Vertex>();
	private Map<Edge, Vertex> edgeToDest= new HashMap<Edge, Vertex>();
	private Map<Vertex, String> vertexToName= new HashMap<Vertex, String>();
	
	List<List<Vertex>> global_AllSP = null;
	List<Vertex> global_diameter = null;
	
	public AdjacencyDiGraph(){
	}
	
	public void addVertex(Vertex v){
		if(!vertices.contains(v)){
			vertices.add(v);
			vertexToEdges.put(v, new ArrayList<Edge>());
		}
	}
	
	public List<Vertex> getVertices(){
		return new ArrayList<Vertex>(vertices);
	}
	
	public void addEdge(Edge e, Vertex src, Vertex dest){
		//System.out.println("Add edge " + e + "\n");
		addVertex(src);
		addVertex(dest);
		edges.add(e);
		edgeToSrc.put(e, src);
		edgeToDest.put(e, dest);
		vertexToEdges.get(src).add(e);
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
	
	public List<String> VerticesToStrings(List<Vertex> path){
		List<String> strList = new LinkedList<String>();
		for(Vertex v : path) {
			strList.add(getNameByVertex(v));
		}
		return strList;
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
	
	public List<Vertex> shortestPath(Vertex src, Vertex dest){
		// TODO !
		Map<Vertex, Vertex> visit = new HashMap<Vertex, Vertex>();
		List<Vertex> temp = getVertices();
		for(Vertex v : temp){
			visit.put(v, null);
		}
		
		Queue<Vertex> queue = new LinkedList<Vertex>();
		queue.add(src);
		visit.put(src, src);
		
		Vertex cur_vertex = src;
		while(!queue.isEmpty()) {
			cur_vertex = queue.poll();
			//System.out.println("poll " + cur_vertex + "\n");
			if(cur_vertex.equals(dest)) break;
			for(Vertex v : getAdjacentVertices(cur_vertex)) {
				if(visit.get(v) == null) {
					//System.out.println("visit "+ v +"\n");
					visit.put(v, cur_vertex);
					queue.add(v);
				}
			}
		}
		
		if(!cur_vertex.equals(dest)) return null;
		
		List<Vertex> res = new ArrayList<Vertex>();
		while(!cur_vertex.equals(src)) {
			//System.out.println("track "+ cur_vertex +"\n");
			res.add(0, cur_vertex);
			cur_vertex = visit.get(cur_vertex);
		}
		res.add(0, cur_vertex);
		return res;
	}
	
	public List<List<Vertex>> shortestPathFrom(Vertex src){
		Map<Vertex, Vertex> visit = new HashMap<Vertex, Vertex>();
		List<Vertex> allVerteices = getVertices();
		for(Vertex v : allVerteices){
			visit.put(v, null);
		}
		
		Queue<Vertex> queue = new LinkedList<Vertex>();
		queue.add(src);
		visit.put(src, src);
		
		Vertex cur_vertex = src;
		while(!queue.isEmpty()) {
			cur_vertex = queue.poll();
			//System.out.println("poll " + cur_vertex + "\n");

			for(Vertex v : getAdjacentVertices(cur_vertex)) {
				if(visit.get(v) == null) {
					//System.out.println("visit "+ v +"\n");
					visit.put(v, cur_vertex);
					queue.add(v);
				}
			}
		}
		
		//List<LinkedList<Vertex>> res = new ArrayList<LinkedList<Vertex>>();
		List<List<Vertex>> res = new ArrayList<List<Vertex>>();
		
		for(Vertex v : allVerteices) {
			
			if(v.equals(src)) continue;
			
			List<Vertex> onePath = new LinkedList<Vertex>();
			//System.out.println("v : "+v+" src: "+ src +"\n");
			while(v != null && !v.equals(src)) {
				onePath.add(0, v);
				v = visit.get(v);
				//System.out.println("v: "+ v +"\n");
			}
			onePath.add(0, v);
			res.add(onePath);
		}

		return res;
	}
	
	public List<List<Vertex>> collectAllSP() {
		
		if(global_AllSP != null) return global_AllSP;
		
		List<List<Vertex>> AllSP = new ArrayList<List<Vertex>>();
		for(Vertex src : getVertices()) {
			List<List<Vertex>> path = shortestPathFrom(src);
			AllSP.addAll(path);
		}
		
		return global_AllSP = AllSP;
	}
	
	public List<Vertex> diameterUnweighted() {
		
		if(global_diameter != null) return global_diameter;
		
		List<Vertex> diameter = new LinkedList<Vertex>();
		int length = 0;
		
		for(List<Vertex> path : collectAllSP()) {
			if(path.size() > length) {
				length = path.size();
				diameter = path;
			}
		}
		
		return global_diameter = diameter;
		
	}
	
	public String diameterToString () {
		
		StringBuilder ShortestPathStr = new StringBuilder();
		StringBuilder lengthSubPath = new StringBuilder();
		ShortestPathStr.append("Shortest path: ");
		lengthSubPath.append("Lengths of sub-paths: ");

		long length = 0;
		for(String v : VerticesToStrings(diameterUnweighted())) {
			ShortestPathStr.append(v +"=>");
			lengthSubPath.append(v+":1 step;"+ v + vertexToName.get(v)+"-");
			length++;
		}
		
		StringBuilder res = new StringBuilder();
		res.append(ShortestPathStr + "\n");
		res.append(lengthSubPath + "\n");
		res.append("Total lengthof the path: "+length+"steps.\n");
		return res.toString();
	}
}