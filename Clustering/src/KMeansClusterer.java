import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;


/**
 * KMeansClusterer.java - a JUnit-testable interface for the Model AI Assignments k-Means Clustering exercises.
 * @author Todd W. Neller
 */
public class KMeansClusterer {
	private int dim; // the number of dimensions in the data
	private int k, kMin, kMax; // the allowable range of the of clusters
	private int iter; // the number of k-Means Clustering iterations per k
	private double[][] data; // the data vectors for clustering
	private double[][] centroids; // the cluster centroids
	private int[] clusters; // assigned clusters for each data point
	private Random random = new Random();


	/**
	 * Read the specified data input format from the standard input stream and return a double[][] 
	 * with each row being a data point and each column being a dimension of the data.
	 * @return a double[][] with each row being a data point and each column being a dimension of the data
	 */
	public double[][] readData() {
		int numPoints = 0;
		Scanner in = new Scanner(System.in);
		try {
			dim = Integer.parseInt(in.nextLine().split(" ")[1]);
			numPoints = Integer.parseInt(in.nextLine().split(" ")[1]);
		}
		catch (Exception e) {
			System.err.println("Invalid data file format. Exiting.");
			e.printStackTrace();
			System.exit(1);
		}
		double[][] data = new double[numPoints][dim];
		for (int i = 0; i < numPoints; i++) {
			String line = in.nextLine();
			Scanner lineIn = new Scanner(line);
			for (int j = 0; j < dim; j++)
				data[i][j] = lineIn.nextDouble();
			lineIn.close();
		}
		in.close();		
		return data;
	}

	/**
	 * Set the given data as the clustering data as a double[][] with each row being a data point and each column being a dimension of the data.
	 * @param data the given clustering dat
	 */
	public void setData(double[][] data) {
		this.data = data;		
		this.dim = data[0].length;
	}

	/**
	 * Return the clustering data as a double[][] with each row being a data point and each column being a dimension of the data.
	 * @return the clustering data
	 */
	public double[][] getData() {
		return data;
	}

	/**
	 * Return the number of dimensions of the clustering data.
	 * @return the number of dimensions of the clustering data
	 */
	public int getDim() {
		return dim;
	}

	/**
	 * Set the minimum and maximum allowable number of clusters k.  If a single given k is to be used, then kMin == kMax.  If kMin &lt; kMax, then all k from kMin to kMax inclusive will be
	 * compared using the gap statistic.  The minimum WCSS run of the k with the maximum gap will be the result.
	 * @param kMin minimum number of clusters
	 * @param kMax maximum number of clusters
	 */
	public void setKRange(int kMin, int kMax) {
		this.kMin = kMin; 
		this.kMax = kMax;
		this.k = kMin;
	}
	
	/**
	 * Return the number of clusters k.  After calling kMeansCluster() with a range from kMin to kMax, this value will be the k yielding the maximum gap statistic.
	 * @return the number of clusters k.
	 */
	public int getK() {
		return k;
	}
	
	/**
	 * Set the number of iterations to perform k-Means Clustering and choose the minimum WCSS result.
	 * @param iter the number of iterations to perform k-Means Clustering
	 */
	public void setIter(int iter) {
		this.iter = iter;	
	}

	/**
	 * Return the array of centroids indexed by cluster number and centroid dimension.
	 * @return the array of centroids indexed by cluster number and centroid dimension.
	 */
	public double[][] getCentroids() {
		return centroids;
	}

	/**
	 * Return a parallel array of cluster assignments such that data[i] belongs to the cluster clusters[i] 
	 * with centroid centroids[clusters[i]].
	 * @return a parallel array of cluster assignments
	 */
	public int[] getClusters() {
		return clusters;
	}

	/**
	 * Return the Euclidean distance between the two given point vectors.
	 * @param p1 point vector 1
	 * @param p2 point vector 2
	 * @return the Euclidean distance between the two given point vectors
	 */
	private double getDistance(double[] p1, double[] p2) {
		double sumOfSquareDiffs = 0;
		for (int i = 0; i < p1.length; i++) {
			double diff = p1[i] - p2[i];
			sumOfSquareDiffs += diff * diff;
		}
		return Math.sqrt(sumOfSquareDiffs);
	}
	
	/**
	 * Return the minimum Within-Clusters Sum-of-Squares measure for the chosen k number of clusters.
	 * @return the minimum Within-Clusters Sum-of-Squares measure
	 */
	public double getWCSS() {
		
		double globalSum = Double.MAX_VALUE;
		
		// For each centroid
		for (int i = 0; i < k; i++) {
			double sum = 0;
			// Calculate the sum of square difference
			for (int j = 0; j < data.length; j ++) {
				// If the data point is in the i-th cluster
				if (clusters[j] == i) {
					sum += Math.pow(getDistance(data[j], centroids[clusters[j]]), 2);
				}
			}
			globalSum = Math.min(globalSum, sum);
		}
		return globalSum;
	}

	/**
	 * Assign each data point to the nearest centroid and return whether or not any cluster assignments changed.
	 * @return whether or not any cluster assignments changed
	 */
	public boolean assignNewClusters() {
		
		// Loop through all data points, for each data points, loop through all centroids.
		boolean change = false;
		for (int p = 0; p < data.length; p++) {
			double minDistance = getDistance(data[p], centroids[clusters[p]]);
			for (int c = 0; c < k; c++) {
				double distance = getDistance(data[p], centroids[c]);
				if (distance < minDistance) {
					change = true;
					clusters[p] = c;
					minDistance = distance;
				}
			}
		}
		return change;
	}
	
	/**
	 * Compute new centroids at the mean point of each cluster of points.
	 */
	public void computeNewCentroids() {
		// for each cluster, compute the mean points.
		// Assign new mean point (centroids) for each cluster
		for (int c = 0; c < k; c ++) {
			double[] sum = new double[dim];
			int cnt = 0;
			for (int p = 0; p < data.length; p ++) {
				if (clusters[p] == c) {
					// for each value in dimension of that point
					for (int d = 0; d < data[p].length; d++) {
						sum[d] += data[p][d];
					}
					cnt ++;
				}
			}
			// divide each dimension by the number of point
			for (int d = 0; d < sum.length; d++) {
				sum[d] /= cnt;
			}
			// assign new centroid
			centroids[c] = sum.clone();
		}
	}

	
	/**
	 * Perform k-means clustering with Forgy initialization and return the 0-based cluster assignments for corresponding data points.
	 * If iter &gt; 1, choose the clustering that minimizes the WCSS measure.
	 * If kMin &lt; kMax, select the k maximizing the gap statistic using 100 uniform samples uniformly across given data ranges.
	 */
	public void kMeansCluster() {
		// TODO - implement
	}
	
	/**
	 * Export cluster data in the given data output format.
	 */
	public void writeClusterData() {
		System.out.printf("%% %d dimensions\n", dim);
		System.out.printf("%% %d points\n", data.length);
		System.out.printf("%% %d clusters/centroids\n", k);
		System.out.printf("%% %f within-cluster sum of squares\n", getWCSS());
		for (int i = 0; i < k; i++) {
			System.out.print(i + " ");
			for (int j = 0; j < dim; j++)
				System.out.print(centroids[i][j] + (j < dim - 1 ? " " : "\n"));	
		}
		for (int i = 0; i < data.length; i++) {
			System.out.print(clusters[i] + " ");
			for (int j = 0; j < dim; j++)
				System.out.print(data[i][j] + (j < dim - 1 ? " " : "\n"));
		}		
	}

	/**
	 * Read UNIX-style command line parameters to as to specify the type of k-Means Clustering algorithm applied to the formatted data on the standard input.
	 * "[-k] int" specifies both the minimum and maximum number of clusters. "-kmin int" specifies the minimum number of clusters. "-kmax int" specifies the maximum number of clusters. 
	 * "-iter int" specifies the number of times k-Means Clustering is performed in iteration to find a lower local minimum.
	 * @param args command-line parameters specifying the type of k-Means Clustering
	 */
	public static void main(String[] args) {
		int kMin = 2, kMax = 2, iter = 1;
		ArrayList<String> attributes = new ArrayList<String>();
		ArrayList<Integer> values = new ArrayList<Integer>();
		int i = 0;
		System.out.println("Hello");
		while (i < args.length) {
			if (args[i].equals("-k") || args[i].equals("-kmin") || args[i].equals("-kmax") || args[i].equals("-iter")) {
				attributes.add(args[i++].substring(1));
			}
			else
				attributes.add("k");
			if (i == args.length) {
				System.err.println("No integer value for" +  attributes.get(attributes.size() - 1) + ".");
				System.exit(1);
			}
			try {
				values.add(Integer.parseInt(args[i]));
				i++;
			}
			catch (Exception e) {
				System.err.printf("Error parsing \"%s\" as an integer.", args[i]);
				System.exit(2);
			}
		}
		for (i = 0; i < attributes.size(); i++) {
			String attribute = attributes.get(i);
			if (attribute.equals("k"))
				kMin = kMax = values.get(i);
			else if (attribute.equals("kmin"))
				kMin = values.get(i);
			else if (attribute.equals("kmax"))
				kMax = values.get(i);
			else if (attribute.equals("iter"))
				iter = values.get(i);
		}
		
		KMeansClusterer km = new KMeansClusterer();
		km.setKRange(kMin, kMax);
		km.setIter(iter);
		km.setData(km.readData());
		km.kMeansCluster();
		km.writeClusterData();
	}
}
