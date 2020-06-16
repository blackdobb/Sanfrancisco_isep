package isep;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;



public class ReadFile {
    
	public static HashMap<Long, ArrayList<String>> nodesImport() throws IOException{
		HashMap<Long, ArrayList<String>> nodes = new HashMap<Long, ArrayList<String>>();
		
		File file = new File("C:\\Users\\shiha\\Desktop\\sanfrancisco\\stops.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
	    String line;
	    while ((line = br.readLine()) != null) {
	          String[] splitLine = line.split(",");
	          //stop_id,stop_code,platform_code,stop_name,stop_desc,stop_lat,stop_lon,zone_id,stop_url,location_type,parent_station,stop_timezone,position,direction,wheelchair_boarding
	          long id = Long.valueOf(splitLine[0]);
	          String name = splitLine[3];
	          String latitude = splitLine[5];
	          String longitude = splitLine[6];
	          //name,lon,lat
	          ArrayList<String> details = new ArrayList<String>();
	          details.add(name);
	          details.add(longitude);
	          details.add(latitude);
	          nodes.put(id, details);
	    }
	    br.close();
	    return nodes;
	}
	
	public static HashMap<ArrayList<Long>, Double> edgesImport(Map<Long, ArrayList<String>> nodes) throws IOException{

		
		HashMap<ArrayList<Long>, Double> edges = new HashMap<ArrayList<Long>, Double>();
		File file = new File("C:\\Users\\shiha\\Desktop\\sanfrancisco\\path.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
	    String line;
	    while ((line = br.readLine()) != null) {
	        String[] splitLine = line.split(",");
	        long source = Long.valueOf(splitLine[0]);
	        long target = Long.valueOf(splitLine[1]);
	        double lon1 = Double.valueOf(nodes.get(source).get(1));
			double lat1 = Double.valueOf(nodes.get(source).get(2));
				
			double lon2 = Double.valueOf(nodes.get(target).get(1));
			double lat2 = Double.valueOf(nodes.get(target).get(2));
				
			double weight = weightCalc(lon1, lat1, lon2, lat2);
			
			ArrayList<Long> src_tgt = new ArrayList<Long>();
			src_tgt.add(source);
			src_tgt.add(target);
			
			ArrayList<Long> tgt_src = new ArrayList<Long>();
			tgt_src.add(target);
			tgt_src.add(source);
			
			edges.put(src_tgt, weight);
			edges.put(tgt_src, weight);
	        
	    }
	    br.close();
		return edges;
	}
	
	public static double weightCalc(double lon1, double lat1, double lon2, double lat2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1609.344; //convert to m
        return (dist);
      }

      /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
      /*::  This function converts decimal degrees to radians             :*/
      /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
      public static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
      }

      /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
      /*::  This function converts radians to decimal degrees             :*/
      /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
      public static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
      }
	
	public static void main(String[] args) throws IOException {
		HashMap<Long, ArrayList<String>> nodes = nodesImport();
		for (long l : nodes.keySet()) {
			System.out.println("The Value is: " + nodes.get(l));
		}		
	}
}
