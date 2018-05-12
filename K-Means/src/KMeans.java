import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class KMeans {
	
	public static int K = 5;
	public static int MAXIMUM_RUNS = 1000;
	
	public static ArrayList<ArrayList<Double>> users;
	
	public static ArrayList<ArrayList<Integer>> users_history;
	public static ArrayList<ArrayList<ArrayList<Double>>> centroids_history;
	
	public static PrintWriter historyWriter;
	public static PrintWriter centroidWriter;
	public static PrintWriter infoWriter;
	public static PrintWriter topMoviesWriter;
	

	public static void main(String[] args) {
		
		// Take input
		
//		ArrayList<String> list = new ArrayList<String>();
//
//		ArrayList<ArrayList<String>> list2 = new ArrayList<ArrayList<String>>();
		
		
		ArrayList<String> inputLines = new ArrayList<String>();
		String currentLine;
		String[] inputLineColumns;
		
		BufferedReader inputReader = null;
		try {
			System.out.println("Opening Input File");
			inputReader = new BufferedReader(new FileReader(new File("u.data-raheel.csv")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			System.out.println("Reading from File");
			while ((currentLine = inputReader.readLine()) != null) {
				inputLines.add(currentLine);
			}
			System.out.println("Closing Input File");
			inputReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String time = "" + System.currentTimeMillis();
		try {
			System.out.println("Opening Output Files");
			historyWriter = new PrintWriter(new BufferedWriter(new FileWriter("history-"+time+".csv", false)));
			centroidWriter = new PrintWriter(new BufferedWriter(new FileWriter("centroids-"+time+".csv", false)));
			infoWriter = new PrintWriter(new BufferedWriter(new FileWriter("info-"+time+".csv", false)));
			topMoviesWriter = new PrintWriter(new BufferedWriter(new FileWriter("top-movies-"+time+".csv", false)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		users = new ArrayList<ArrayList<Double>>(
				inputLines.size()
			);
		//inputLines.get(0).split(",").length
		
		// System.out.println("inputLines.size(): " + inputLines.size());
		
		for (int i=0; i<inputLines.size(); i++) {
			inputLineColumns = inputLines.get(i).split(",");
			
			ArrayList<Double> movieRatings = new ArrayList<Double>( inputLineColumns.length - 1 );
			// System.out.println("movieRatings.size(): "+movieRatings.size());
			
			// System.out.println("(inputLineColumns.length - 1): " + Integer.toString(inputLineColumns.length - 1) );
			for (int j=1; j<inputLineColumns.length; j++) {
				movieRatings.add(Double.parseDouble(inputLineColumns[j]) );
			}
			
			users.add(movieRatings);
		}
		
		int totalUsers = users.size();
		int totalMovies = users.get(0).size();
		System.out.println("totalUsers: " + totalUsers);
		System.out.println("totalMovies: " + totalMovies);
		
		
		cluster_with_history();
		save_results();
		
		
		System.out.println("Closing Output File");
		historyWriter.close();
		centroidWriter.close();
		infoWriter.close();
		topMoviesWriter.close();
	}
	
	
	
	
	
	
	public static void choose_init_centroids() {
		centroids_history = new ArrayList<ArrayList<ArrayList<Double>>>();
		HashMap<Integer,Integer> map = new HashMap<Integer,Integer>();
		while(map.size() != K) {
			map.put( (int)(Math.random()*users.size()) , 1);
		}
		
		ArrayList<ArrayList<Double>> groups = new ArrayList<ArrayList<Double>>();
	    
		
	    for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
	    	ArrayList<Double> centroidMovies = new ArrayList<Double>();
			ArrayList<Double> userMovies = users.get(entry.getKey());
			
			for (int j=0; j<userMovies.size() ;j++) {
				centroidMovies.add(userMovies.get(j));
			}
			
			groups.add(centroidMovies);
	    }
		
	    centroids_history.add(groups);	
	}
	
	public static void min_dist() {
		
	}
	
	public static Double euclidean_dist(ArrayList<Double> point, ArrayList<Double> centroid) {
		Double ans = 0.0, a, b;
		for (int i=0; i<point.size(); i++) {
			a = point.get(i);
			b = centroid.get(i);
			ans += ( (a-b) * (a-b) );
		}
		return Math.sqrt(ans);
	}
	
	public static ArrayList<ArrayList<Double>> choose_centroids() {
		ArrayList<ArrayList<Double>> new_centroids = new ArrayList<ArrayList<Double>>();
		
		HashMap<Integer,ArrayList<Double>> centroidMap = new HashMap<Integer,ArrayList<Double>>();
		HashMap<Integer,Integer> countMap = new HashMap<Integer,Integer>();
		
		ArrayList<Integer> groups = users_history.get(users_history.size() - 1);
		
		for (int i=0; i<groups.size(); i++) {
			int userGroup = groups.get(i);
			ArrayList<Double> movieRatings = users.get(i);
			
			if (!centroidMap.containsKey(userGroup)) {
				ArrayList<Double> newCentroid = new ArrayList<Double>();
				for (int j=0; j<movieRatings.size(); j++) {
					newCentroid.add(movieRatings.get(j));
				}
				centroidMap.put(userGroup, newCentroid);
				countMap.put(userGroup, 1);
			} else {
				ArrayList<Double> centroid = centroidMap.get(userGroup);
				for (int j=0; j<movieRatings.size(); j++) {
					centroid.set(j,
							centroid.get(j) + movieRatings.get(j)
						);
				}
				centroidMap.put(userGroup, centroid);
				countMap.put(userGroup, countMap.get(userGroup) + 1);
			}
		}
		
		for (int i=0; i<centroidMap.size(); i++) {
			int countInGroup = countMap.get(i);
			ArrayList<Double> centroid = centroidMap.get(i);
			for (int j=0; j<centroid.size(); j++) {
				centroid.set(j,
						centroid.get(j) / countInGroup
					);
			}
			new_centroids.add(centroid);
		}
		
		return new_centroids;
	}
	
	public static int closest_group(ArrayList<Double> point, ArrayList<ArrayList<Double>> centroids) {
		int selected_group = 0;
		Double selected_dist = euclidean_dist(point, centroids.get(0));
		for (int i=1; i<K; i++) {
			Double temp_dist = euclidean_dist(point, centroids.get(i));
			if (temp_dist < selected_dist) {
                selected_group = i;
                selected_dist = temp_dist;
			}
		}
		return selected_group;
	}
	
	public static ArrayList<Integer> assign_groups(ArrayList<ArrayList<Double>> centroids) {
		ArrayList<Integer> groups = new ArrayList<Integer>();
		
		for (int i=0; i<users.size(); i++) {
			groups.add(closest_group(users.get(i), centroids));
		}
		return groups;
	}
	
	public static void points_to_point_groups() {
		
	}
	
	public static void cluster_with_history() {
		users_history = new ArrayList<ArrayList<Integer>>();
		centroids_history = new ArrayList<ArrayList<ArrayList<Double>>>();
		
		choose_init_centroids();
		int iterationCount = 0;
		boolean solutionFound = false;
		while(solutionFound == false && iterationCount < MAXIMUM_RUNS) {
			iterationCount++;
			System.out.println("Running ITERATION: " + iterationCount);
			ArrayList<ArrayList<Double>> current_centroids = centroids_history.get(centroids_history.size() - 1);
			
			users_history.add(assign_groups(current_centroids));
			
			ArrayList<ArrayList<Double>> new_centroids = choose_centroids();
			
			
			centroids_history.add(new_centroids);
			
			boolean noError = true;
			for (int i=0; i<current_centroids.size(); i++) {
				if (current_centroids.get(i) != new_centroids.get(i)) {
					noError = false;
					break;
				}
			}
			if (noError) {
				break;
			}
		}
	}
	
	public static void save_results() {
		for (int i=0; i<centroids_history.size(); i++) {
			centroidWriter.println("centroids["+i+"]");
			ArrayList<ArrayList<Double>> tempGroups = centroids_history.get(i);
			
			for (int j=0; j<tempGroups.size(); j++) {
				centroidWriter.print("\t\t"+"group["+j+"]");
				ArrayList<Double> def = tempGroups.get(j);
				
				for (int k=0; k<def.size(); k++) {
					centroidWriter.print("\t\t"+def.get(k));
				}
				centroidWriter.println();
			}
			centroidWriter.println();
		}
		
		for (int i=0; i<users_history.size(); i++) {
			historyWriter.println();
			historyWriter.println("groups["+i+"]");
			ArrayList<Integer> tempUsers = users_history.get(i);
			for (int j=0; j<tempUsers.size(); j++) {
				historyWriter.println("\t\t"+j+":  "+tempUsers.get(j));
				
			}
			historyWriter.println();
			historyWriter.println();
			historyWriter.println();
		}
		
		infoWriter.println("K: "+K);
		infoWriter.println("MAXIMUM_RUNS: "+MAXIMUM_RUNS);
		HashMap<Integer,Integer> countMap = new HashMap<Integer,Integer>();
		ArrayList<Integer> users  = users_history.get(users_history.size()-1);
		for (int i=0; i<users.size(); i++) {
			int userGroup = users.get(i);
			if (!countMap.containsKey(userGroup)) {
				countMap.put(userGroup, 1);
			} else {
				countMap.put(userGroup, (countMap.get(userGroup) + 1 ));
			}
		}
		for (int i=0; i<countMap.size(); i++) {
			infoWriter.println("group[" + i + "]: " + countMap.get(i));
		}
		
		ArrayList<ArrayList<Double>> groups = centroids_history.get( centroids_history.size() - 1 );
		for (int i=0; i<groups.size(); i++) {
			ArrayList<Double> movieRatings = groups.get(i);
			MovieData[] sortedMovies = new MovieData[movieRatings.size()];
			for (int j=0; j<movieRatings.size(); j++) {
				MovieData movieData = new MovieData(j, movieRatings.get(j));
				sortedMovies[j] = movieData;
			}
			Arrays.sort(sortedMovies);
			topMoviesWriter.print("group[" + i + "]");
			for (int j=0; j<20; j++) {
				topMoviesWriter.print("\t\t"+sortedMovies[j]);
			}
			topMoviesWriter.println();
		}
		topMoviesWriter.println();
		//topMoviesWriter
	}

	
	
	public static class MovieData implements Comparable<MovieData> {
		int movie;
		Double rating;
		
		MovieData (int movie, Double rating) {
			this.movie = movie;
			this.rating = rating;
		}
		
	    @Override
	    public int compareTo(MovieData o) {
	    	Double compareRating = o.rating;
	    	return (compareRating - this.rating) > 0 ? 1 : (compareRating - this.rating) == 0 ? 0 : -1;

	    }
	    
	    @Override
	    public String toString() {
	    	return "{movie:"+movie+", rating:"+rating+"}";
	    }
	}
}


