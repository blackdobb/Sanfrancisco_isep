package isep;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import isep.DiGraph;

public class SanFrancisco {

	private static Long connectCellsBothWays(Long v1, Long v2, Long currentEdgeId, DiGraph<Long, Long> SanFrancisco) {
		SanFrancisco.addEdge(currentEdgeId++, v1, v2);
		SanFrancisco.addEdge(currentEdgeId++, v1, v2);
		return currentEdgeId;
	}
	
	private static Long connectCellsBothWays(Long v1, Long v2, Long currentEdgeId, float weight, WeightedDiGraph<Long, Long> SanFrancisco) {
		SanFrancisco.addEdge(currentEdgeId++, weight, v1, v2);
		SanFrancisco.addEdge(currentEdgeId++, weight, v1, v2);
		return currentEdgeId;
	}
	
	public static DiGraph<Long, Long> readDiGraph() throws IOException {
		DiGraph<Long, Long> SanFrancisco = new AdjacencyDiGraph<Long, Long>();
		//TODO
		Map <Long, ArrayList<String>> nodes = ReadFile.nodesImport();
		for(Long v : nodes.keySet()) {
			SanFrancisco.addVertex(v);
			SanFrancisco.nameVertex(nodes.get(v).get(0), v);
			//System.out.println("name"+v+nodes.get(v).get(0));
		}
		
		Map<ArrayList<Long>, Double> edges = ReadFile.edgesImport(nodes);
		Long currentId = 0L;
		for(ArrayList<Long> valList : edges.keySet()) {
			currentId = connectCellsBothWays(valList.get(0), valList.get(1), currentId, SanFrancisco);
		}
		return SanFrancisco;
	}
	

	public static WeightedDiGraph<Long, Long> readWeightedDiGraph() throws IOException {
		WeightedDiGraph<Long, Long> SanFrancisco = new AdjacencyWeightedDiGraph<Long, Long>();
		//TODO
		Map <Long, ArrayList<String>> nodes = ReadFile.nodesImport();
		for(Long v : nodes.keySet()) {
			SanFrancisco.addVertex(v);
			SanFrancisco.nameVertex(nodes.get(v).get(0), v);
			//System.out.println("name"+v+nodes.get(v).get(0));
		}
		
		Map<ArrayList<Long>, Double> edges = ReadFile.edgesImport(nodes);
		Long currentId = 0L;
		for(ArrayList<Long> valList : edges.keySet()) {
			currentId = connectCellsBothWays(valList.get(0), valList.get(1), currentId, edges.get(valList).floatValue(), SanFrancisco);
		}
		return SanFrancisco;
	}
	public static WeightedDiGraph<Integer, Integer> readWeightedDiGraph(InputStream is) throws IOException {
		WeightedDiGraph<Integer, Integer> SanFrancisco = new AdjacencyWeightedDiGraph<Integer, Integer>();
		//TODO
		return SanFrancisco;
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println("San Francisco Bay Area");
		
		System.out.println("Unweighted Graph:");
		DiGraph<Long, Long> parismaze = readDiGraph();
		System.out.println(parismaze.diameterToString());

		System.out.println("Weighted Graph:");
		WeightedDiGraph<Long, Long> parismaze1 = readWeightedDiGraph();
		System.out.println(parismaze1.diameterToString());
		System.out.println(parismaze1.clustersToString(parismaze1.Graph_Clustering(10, 9)));
		
	}

}
