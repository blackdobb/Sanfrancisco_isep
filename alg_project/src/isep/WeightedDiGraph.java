package isep;

import java.util.List;
import java.util.Map;

public interface WeightedDiGraph<Vertex, Edge> {
	//use addVertex();
	public void addVertex(Vertex v);
	public void removeVertex(Vertex v);
	public List<Vertex> getVertices();
	//use addEdge()
	public void addEdge(Edge e, float weight, Vertex src, Vertex dest);
	public void removeEdge(Edge e, Vertex src, Vertex dest);
	public List<Edge> getEdges();
	public List<Vertex> getAdjacentVertices(Vertex v);
	
	//use nameVertex()
	public void nameVertex(String name, Vertex v);
	public Vertex getVertexByName(String name);
	public List<String> getNames();
//	public List<String> VerticesToStrings(List<Vertex> path);
	public boolean areConnected(Vertex src, Vertex dest);
	public boolean areConnected(String src, String dest);
//	public List<Vertex> shortestPath(Vertex src, Vertex dest);
	public List<String> VerticesToStrings(List<Vertex> path);
	
	public List<List<Edge>> shortestPathFrom(Vertex src);
	public List<List<Edge>> collectAllSP();
	public List<Edge> diameterWeighted();
	public String diameterToString();

	public void calBetweenness();
	public List<List<Vertex>> findClusters(Map<Vertex,Boolean> visit);
	public List<List<Vertex>> Graph_Clustering(int remove_num, int cluster_num);
	public String clustersToString(List<List<Vertex>> clusters);
}