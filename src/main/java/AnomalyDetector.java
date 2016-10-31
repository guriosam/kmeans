import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import clustering.Cluster;
import clustering.Data;
import clustering.Point;

public class AnomalyDetector {

	private List<Cluster> trainClusters;
	private String trainReadPath;
	private String trainSavePath;
	private List<Cluster> evalClusters;
	private String evalReadPath;
	private String evalSavePath;

	// private List<Double> distancesCentroids;
	private HashMap<String, Double> distanceCentroids;
	private HashMap<Double, Double> evaluateDistances;

	public AnomalyDetector() {
		trainClusters = new ArrayList<Cluster>();
		evalClusters = new ArrayList<Cluster>();
		distanceCentroids = new HashMap<String, Double>();
		evaluateDistances = new HashMap<Double, Double>();

		trainReadPath = "C:\\Users\\Caio\\Desktop\\Ezio Auditore\\UFAL_MultiLayer_AnomalyDetector\\K-mean Clustering\\K-mean Clustering\\trainOutput.txt";
		// trainSavePath = "C:\\Users\\Caio\\Desktop\\Ezio
		// Auditore\\UFAL_MultiLayer_AnomalyDetector\\K-mean Clustering\\K-mean
		// Clustering\\respTrainData.txt";
		evalReadPath = "C:\\Users\\Caio\\Desktop\\Ezio Auditore\\UFAL_MultiLayer_AnomalyDetector\\K-mean Clustering\\K-mean Clustering\\evalOutput.txt";

	}

	public void calculateDistanceRate() {

		ArrayList<Double> distances = new ArrayList<Double>();
		double MAX_DISTANCE = 0;
		double MIN_DISTANCE = 0;

		for (int i = 0; i < trainClusters.size(); i++) {
			for (int j = 0; j < evalClusters.size(); j++) {
				double distance = distanceCentroids.get("T" + i + ":E" + j);
				distances.add(distance);
				if (distance > MAX_DISTANCE) {
					MAX_DISTANCE = distance;
				} else if (distance < MIN_DISTANCE) {
					MIN_DISTANCE = distance;
				}
			}
		}

		if (MAX_DISTANCE == 0) {
			MAX_DISTANCE = 1;
		}

		for (Double d : distances) {
			evaluateDistances.put(d, d / MAX_DISTANCE);
		}

		for (int i = 0; i < trainClusters.size(); i++) {
			for (int j = 0; j < evalClusters.size(); j++) {
				double distance = distanceCentroids.get("T" + i + ":E" + j);
				double eval = evaluateDistances.get(distance);
				
				if(eval > 0.3){
					System.out.println("Evaluation between: T" + i + ":E" + j + " = " + eval);
				}
			}
		}
	}

	public void getDistanceBetweenClusters() {

		System.out.println(trainClusters.size());
		System.out.println(evalClusters.size());

		for (Cluster tc1 : trainClusters) {
			Point tc = tc1.getCentroid();
			for (Cluster ec1 : evalClusters) {
				Point ec = ec1.getCentroid();
				// Centering the centroids, so we can get the minimum distance
				// between them (ortogonal distance)
				ec.setX(tc.getX());

				// Getting the distance between them
				// System.out.println(ec.distance(tc, ec));
				System.out.println("Centroid Cluster T" + tc1.getId() + ": " + tc1.getCentroid());
				System.out.println("Centroid Cluster E" + ec1.getId() + ": " + ec1.getCentroid());
				System.out.println("Distance Between Clusters T" + tc1.getId() + " and E" + ec1.getId() + " is "
						+ ec.distance(tc, ec));
				distanceCentroids.put("T" + tc1.getId() + ":E" + ec1.getId(), ec.distance(tc, ec));
			}
		}
	}

	public void readTrainClusters() {
		BufferedReader br = null;

		try {

			String sCurrentLine;

			br = new BufferedReader(new FileReader(trainReadPath));

			int i = 0;

			Cluster c = null;

			while ((sCurrentLine = br.readLine()) != null) {
				if (sCurrentLine.contains("Cluster")) {
					if (c != null) {
						trainClusters.add(c);
					}
					c = new Cluster(i);
					// System.out.println("Cluster " + i + " created.");
				} else {
					if (sCurrentLine.contains("centroid")) {
						String[] centroidLine = sCurrentLine.split(",");
						double x = Double.valueOf(centroidLine[1]);
						double y = Double.valueOf(centroidLine[2]);
						c.setCentroid(new Point(x, y));
						c.getCentroid().setCluster(i);
						// System.out.println("Centroid: " + c.getCentroid());
						i++;
					}
				}
			}

			if (c != null) {
				trainClusters.add(c);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

	public void readEvalClusters() {
		BufferedReader br = null;

		try {

			String sCurrentLine;

			br = new BufferedReader(new FileReader(evalReadPath));

			int i = 0;

			Cluster c = null;

			while ((sCurrentLine = br.readLine()) != null) {
				if (sCurrentLine.contains("Cluster")) {
					if (c != null) {
						evalClusters.add(c);
					}
					c = new Cluster(i);
					// System.out.println("Cluster " + i + " created.");
				} else {
					if (sCurrentLine.contains("centroid")) {
						String[] centroidLine = sCurrentLine.split(",");
						double x = Double.valueOf(centroidLine[1]);
						double y = Double.valueOf(centroidLine[2]);
						c.setCentroid(new Point(x, y));
						// System.out.println("Centroid: " + c.getCentroid());
						i++;
					}
				}
			}

			if (c != null) {
				evalClusters.add(c);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

}
