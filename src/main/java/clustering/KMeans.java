package clustering;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Copyright 2016 Filipe Falcão Batista dos Santos
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
public class KMeans {

	// Number of Clusters. This metric should be related to the number of points
	private int NUM_CLUSTERS = 10;
	// Number of Points
	// Min and Max X and Y
	private static double MIN_COORDINATE_Y = 0;
	private static double MAX_COORDINATE_Y = 10;
	private static double MIN_COORDINATE_X = 0;
	private static double MAX_COORDINATE_X = 10;

	private List<Point> points;
	private List<Cluster> clusters;
	private List<Data> data;

	private int iteration;

	private String path;
	private String txt;

	public KMeans(boolean training) {
		this.points = new ArrayList<Point>();
		this.clusters = new ArrayList<Cluster>();
		this.data = new ArrayList<Data>();

		txt = "";
		if (training) {
			path = "C:\\Users\\Caio\\Desktop\\Ezio Auditore\\UFAL_MultiLayer_AnomalyDetector\\K-mean Clustering\\K-mean Clustering\\trainData.csv";
		} else {
			path = "C:\\Users\\Caio\\Desktop\\Ezio Auditore\\UFAL_MultiLayer_AnomalyDetector\\K-mean Clustering\\K-mean Clustering\\evalData.csv";
		}
		readData();
		// convertTime("");
	}

	/**
	 * Initialize the process.
	 */
	public void init() {
		// Create Points
		if (data.size() > 1) {
			MIN_COORDINATE_Y = Double.valueOf(data.get(0).getValue());
		}

		MIN_COORDINATE_X = 0;
		MAX_COORDINATE_X = data.size();
		txt += "Input:\n";

		for (int i = 0; i < data.size(); i++) {
			Point p = new Point((double) i + 10, Double.valueOf(data.get(i).getValue()));
			// Point p = new Point(Double.valueOf(data.get(i).getValue()),
			// Double.valueOf(data.get(i).getValue()));
			if (Integer.valueOf(data.get(i).getValue()) < MIN_COORDINATE_Y) {
				MIN_COORDINATE_Y = Double.valueOf(data.get(i).getValue());
				// System.out.println("MIN:" + MIN_COORDINATE_Y);
			}

			if (Integer.valueOf(data.get(i).getValue()) > MAX_COORDINATE_Y) {
				MAX_COORDINATE_Y = Double.valueOf(data.get(i).getValue());
				// System.out.println("MAX:" + MAX_COORDINATE_Y);
			}
			// System.out.println(i + 10 + " " +
		
			txt += (i+10) + "," + Double.valueOf(data.get(i).getValue()) + "\n";
						
			// Double.valueOf(data.get(i).getValue();
			points.add(p);

		}

		// System.out.println(data.size());

		// Create Clusters
		// Set Random Centroids
		for (int i = 0; i < NUM_CLUSTERS; i++) {
			Cluster cluster = new Cluster(i);
			Point centroid = Point.createRandomPoint(MIN_COORDINATE_X, MAX_COORDINATE_X, MIN_COORDINATE_Y,
					MAX_COORDINATE_Y);
			cluster.setCentroid(centroid);
			clusters.add(cluster);
		}

		// Print Initial state
		// System.out.println("Initial state:");
		plotClusters();
	}

	/**
	 * Print all the clusters.
	 */
	private void plotClusters() {
		for (int i = 0; i < NUM_CLUSTERS; i++) {
			Cluster c = clusters.get(i);
				txt += c.plotCluster();
			// System.out.println("");
		}
	}

	/**
	 * Calculate the K Means with iterating method.
	 */
	public void calculate() {
		boolean finish = false;
		iteration = 0;

		// Add in new data, one at a time, recalculating centroids with each new
		// one.
		while (!finish) {
			// Clear cluster state
			clearClusters();

			List<Point> lastCentroids = getCentroids();

			// Assign points to the closer cluster
			assignCluster();

			// Calculate new centroids.
			calculateCentroids();

			iteration++;

			List<Point> currentCentroids = getCentroids();

			// Calculates total distance between new and old Centroids
			double distance = 0;

			for (int i = 0; i < lastCentroids.size(); i++) {
				distance += Point.distance(lastCentroids.get(i), currentCentroids.get(i));
			}

			// System.out.println("");
			System.out.println("Iteration: " + iteration);
			txt += "Iteration: " + iteration + "\n";
			System.out.println("\tCentroid distances: " + distance);
			plotClusters();

			// removeEmptyClusters();

			if (distance == 0) {
				finish = true;
			}
		}

	}

	/**
	 * Clear all the clusters.
	 */
	private void clearClusters() {
		for (Cluster cluster : clusters) {
			cluster.clear();
		}
	}

	/**
	 * Get centroids from all clusters.
	 *
	 * @return Centroids
	 */
	private List<Point> getCentroids() {
		List<Point> centroids = new ArrayList<Point>(NUM_CLUSTERS);

		for (Cluster cluster : clusters) {
			Point aux = cluster.getCentroid();
			Point point = new Point(aux.getX(), aux.getY());
			centroids.add(point);
		}

		return centroids;
	}

	/**
	 * Assign a point to the closest cluster.
	 */
	private void assignCluster() {
		double max = Double.MAX_VALUE;
		double min;
		int cluster = 0;
		double distance;

		for (Point point : points) {
			min = max;

			for (int i = 0; i < NUM_CLUSTERS; i++) {
				Cluster c = clusters.get(i);
				distance = Point.distance(point, c.getCentroid());

				if (distance < min) {
					min = distance;
					cluster = i;
				}
			}

			point.setCluster(cluster);
			clusters.get(cluster).addPoint(point);
		}
	}

	/**
	 * Calculate the centroids.
	 */
	private void calculateCentroids() {
		double sumX = 0;
		double sumY = 0;

		for (Cluster cluster : clusters) {
			List<Point> list = cluster.getPoints();
			int n_points = list.size();

			for (Point point : list) {
				sumX += point.getX();
				sumY += point.getY();
			}

			Point centroid = cluster.getCentroid();

			if (n_points > 0) {
				double newX = sumX / n_points;
				double newY = sumY / n_points;
				// centroid.setX(newX);
				centroid.setY(newY);
			}
		}
	}

	private void removeEmptyClusters() {
		if (iteration > 1) {

			ArrayList<Cluster> aux = new ArrayList<Cluster>();

			for (Cluster c : clusters) {

				if (c.getPoints().size() < 1) {
					points.remove(c.getCentroid());
					// clusters.remove(c);
					aux.add(c);
					NUM_CLUSTERS--;
				}

			}

			for (Cluster c : aux) {
				// System.out.println("Clusters: " + c.getId() + " removed.");
				clusters.remove(c);
			}

		}

	}

	private void readData() {

		BufferedReader br = null;

		try {

			String sCurrentLine;

			br = new BufferedReader(new FileReader(path));

			while ((sCurrentLine = br.readLine()) != null) {
				if (sCurrentLine.contains("Activated Virtual Pages")) {
					String[] line = sCurrentLine.split(",");

					SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
					Date dt = null;

					dt = f.parse(line[3]);

					Data d = new Data(line[0], line[1], line[2], dt, line[4], line[5]);
					data.add(d);
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
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

	public void saveData() {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter("C:\\Users\\Caio\\Desktop\\Ezio Auditore\\UFAL_MultiLayer_AnomalyDetector\\K-mean Clustering\\K-mean Clustering\\respData.txt"));
			writer.write(txt);

		} catch (IOException e) {
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {
			}
		}
	}
}