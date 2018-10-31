package LR;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

import LR.MF.Instance;

/**
 * Performs simple logistic regression.
 * 
 * @author
 * @author
 */
public class LogisticRegression {
	
	// for data and training
	List<Instance> data;
	private double rate = 0.1;
	private float[] weights;
	private double[] likely; // cache likelihood
	private double lik = 0.0; // total likelihood
	
	private int randomSeed = 0;
	
	// for thread
	private int totalT;
	private ReentrantLock lock;
	
	// adjustable parameters
	private int ITERATIONS = 20;
	private int miniBsize;
	private int paramID = 0;
	
	// for test output
	private long startTime;
	private BufferedWriter[] bw;
	private String alg;

	public LogisticRegression(int totalT, int miniBatchSize, String alg, String dataName) throws IOException {
		
		this.weights = null;
		this.data = null;

		this.totalT = totalT;
		this.miniBsize = miniBatchSize;
		this.alg = alg;
		
		this.lock = new ReentrantLock();
	
		this.bw = new BufferedWriter[totalT+1];
		MF.mkdir("results/" + dataName);
		for (int i = 0; i < totalT; i++) {
			this.bw[i] = new BufferedWriter(new FileWriter("results/" + dataName
					+ "/" + alg + miniBsize + "_pt" + i + ".txt"));
		}
		this.bw[totalT] = new BufferedWriter(new FileWriter("results/" + dataName + alg +
				+ miniBsize + ".txt"));
	}
	
	public void trainThread(int thread) throws IOException, InterruptedException{
		if (alg == "Fsp")
			this.trainThreadFSP(thread);
		else
			this.trainThreadAsync(thread);
	}
	
	private void normalizeData(){
		float[] min = new float[this.weights.length];
		float[] max = new float[this.weights.length];

		for (int i = 0; i < min.length; i++){
			min[i] = 1000000;
			max[i] = -1000000;
		}
		
		// get min max
		for (Instance ins : data){
			for (int i = 0; i < min.length; i++){
				if (ins.x[i] < min[i])
					min[i] = ins.x[i];
				if (ins.x[i] > max[i])
					max[i] = ins.x[i];
			}
		}
		for (Instance ins : data) {
			for (int i = 0; i < min.length; i++){
				ins.x[i] = (ins.x[i]-min[i]) / (max[i] - min[i]);
			}
		}
	}

	private void normalizeLabel(){
		float min = 1000000, max = -1000000;
		
		// get min max
		for (Instance ins : data) {
			if (ins.label < min)
				min = ins.label;
			if (ins.label > max)
				max = ins.label;
		}

		for (Instance ins : data) {
			ins.label = (ins.label-min) / (max - min);
		}
	}

	/*
	public void train(int xx) {

		int mini = (int) Math.pow(2, xx);
		int miniBatch = data.size() / mini;
		System.out.println("Training data " + data.size() + "\tminiBatch: " + miniBatch);
		int totalUpdates = 0;
//		this.ITERATIONS = 200/(xx+1);
		this.ITERATIONS = 30;

		BufferedWriter bw = null;

		try {
			bw = new BufferedWriter(new FileWriter("results/AffairMini" +  mini + ".txt"));
			
			long endTime = System.currentTimeMillis();
			this.trainInitalize();
			long startTime = System.currentTimeMillis();
			System.out.println("initial:\t" + (startTime - endTime) + "\t" + lik + "\t"
					+ Arrays.toString(weights));

//			bw.write("initial:\t" + (startTime - endTime) + "\t0\t" + lik + "\t"
//					+ Arrays.toString(weights)+"\n");
			
			float[] deltaWt = new float[this.weights.length];
			// float[] deltaF = new float[data.size()];
			
			int accumu = 0;
			for (int n = 0; n < ITERATIONS; n++) {
				
				for (int i = 0; i < data.size(); i++) {
					if (n % 2 == 1 && i > data.size() *3 /4 ) {
						break;
					}
					float[] x = data.get(i).x;
					double predicted = classify(x);
					float label = data.get(i).label;
					for (int j = 0; j < weights.length; j++) {
						deltaWt[j] += (label - predicted) * x[j];
					}
					accumu++;
					// not necessary for learning
					if ((accumu + 1) % 50 == 0)
						Thread.sleep(0, 1);
					
					//// partial update likelyhood
//					double like = computeLik(predicted, label);
//					this.lik = this.lik + like - this.likely[i];
//					this.likely[i] = like;

					if ((i + 1) % miniBatch == 0 || i == data.size() - 1) {
						//this.updateWt(deltaWt, accumu, n);
						totalUpdates++;
						
						double newlik = 0.0;
//						if (i == data.size() - 1)
							newlik = this.computeLikelyhood();
						endTime = System.currentTimeMillis();

						bw.write("iter:\t" + n + "\t" + (endTime - startTime) + "\t" + newlik + "\t" + lik + "\t" + Arrays.toString(weights)
								+ "\n");
					}
				}
				if ((n+1) % 10 == 0) {
					endTime = System.currentTimeMillis();
					System.out.println("iter:\t" + n + "\t" + (endTime - startTime) + "\t" + lik);
				}
			}

			endTime = System.currentTimeMillis();
			System.out.println("updates:\t" + totalUpdates + "\t" + (endTime - startTime) + "\t" + lik + "\t"
					+ Arrays.toString(weights));
			bw.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

	}*/

	public void trainInitalize(){
		// initialize likelihood cache and weight 
		Random rr = new Random(this.randomSeed);
		for (int i = 0; i < weights.length; i++) {
			weights[i] = 0;//rr.nextFloat();
		}
		
		// initialize
		this.initialLikihoodCache();

		long t0 = System.currentTimeMillis();
		System.out.println("initial:\t" + (startTime - t0) + "\t" + lik + "\t"
				+ Arrays.toString(weights));

		this.startTime = System.currentTimeMillis();
	}
	
	private void initialLikihoodCache() {
		// initialize all the variables
		this.likely = new double[data.size()];
		
		for (int i = 0; i < data.size(); i++) {
			double predicted = MF.classify(this.weights, data.get(i).x);
			float label = data.get(i).label;
			likely[i] = label * Math.log(predicted) + (1 - label) * Math.log(1 - predicted);
			lik += likely[i];
		}	
	}
	
	private void paritalUpdateLikelihood(double predicted, float label, int i){
		double like = MF.computeLik(predicted, label);
		this.lik = this.lik + like - this.likely[i];
		this.likely[i] = like;
	}

	// compuate the grad for data point i and accumulate to grads
	private void accumuGrads(float[] weights, float[] grads, int i){

		// accumulate gradients
		float[] x = data.get(i).x;
		float label = data.get(i).label;
		double predicted = MF.classify(weights, x);
		for (int j = 0; j < weights.length; j++) {
			grads[j] += (label - predicted) * x[j];
		}
		
		paritalUpdateLikelihood(predicted, label, i);
	}
	
	public void trainThreadFSP(int thread) throws IOException, InterruptedException {

		// number of weight updates
		int totalUpdates = 0;
		// data processed this rounds
		int accumu = 0;

		// local deltaWt
		float[] grads = new float[this.weights.length];
		String updateDetails = "";
		int currparamID = this.paramID;
		int paraRange = 0;
		
		for (int iter = 0; iter < ITERATIONS; iter++) {
			
			for (int i = thread; i < data.size(); i += this.totalT) {
				
				// accumulate gradients
				accumuGrads(this.weights, grads, i);
				accumu++;
				
				// Monitor parameterID
				if (currparamID == this.paramID)
					paraRange++;
				else {
//					System.out.println("worker" + thread + " update paramID from " + currparamID 
//							+ " to " + this.paramID + " at " + paraRange);
					updateDetails += currparamID + "," + paraRange + ";";
					paraRange = 1;
					currparamID = this.paramID;
				}
				
				// sleep x nanosecond
				MF.compSleep(accumu, thread);
				
				/// update rule
				if (accumu == this.miniBsize) {
					updateDetails += currparamID + "," + paraRange + ";";
					currparamID = this.updateGlbWt(this.weights, grads, accumu, iter, thread, updateDetails);
					totalUpdates++;
					updateDetails = "";
					paraRange = 0;
					accumu = 0;
				}
			}
			if ((iter+1) % 2 == 0) {
				long endTime = System.currentTimeMillis();
				System.out.println("iter:\t" + iter + "\t" + (endTime - startTime) + "\t" + lik);
			}
		}

		long endTime = System.currentTimeMillis();
		System.out.println("updates:\t" + totalUpdates + "\t" + (endTime - startTime) + "\t" 
				+ MF.computeLikelyhood(this.data, this.weights) + "\t" + Arrays.toString(weights));
	}
	
	public void trainThreadAsync(int thread) throws IOException, InterruptedException {

		// number of weight updates
		int totalUpdates = 0;
		// data processed this rounds
		int accumu = 0;

		// local deltaWt
		float[] grads = new float[this.weights.length];
		float[] localWts = this.weights;
		int localParaID = this.paramID;
		
		for (int iter = 0; iter < ITERATIONS; iter++) {
			
			for (int i = thread; i < data.size(); i += this.totalT) {
				
				// accumulate gradients
				accumuGrads(localWts, grads, i);
				accumu++;
				
				// sleep to 1 nanosecond
				MF.compSleep(accumu, thread);
				
				/// update rule
				if (accumu == this.miniBsize) {
					localParaID = this.updateGlbWt(localWts, grads, accumu, iter, 
							thread, localParaID + "," + accumu);
					totalUpdates++;
					accumu = 0;
				}
			}
			if ((iter+1) % 2 == 0) {
				long endTime = System.currentTimeMillis();
				System.out.println("iter:\t" + iter + "\t" + (endTime - startTime) + "\t" + lik);
			}
		}

		long endTime = System.currentTimeMillis();
		System.out.println("updates:\t" + totalUpdates + "\t" + (endTime - startTime) + "\t" 
				+ MF.computeLikelyhood(this.data, this.weights) + "\t" + Arrays.toString(weights));
	}
	
	/*
	private void updateWt(float[] grads, int accumu, int iter, int thread) throws IOException {
		lock.lock();
		for (int i = 0; i < weights.length; i++) {
			this.weights[i] += rate * grads[i] / accumu;
			grads[i] = 0;
		}
		
		double newlik = 0.0;
		// when to compute new likelihood
//			if (i == data.size() - 1)
			newlik = this.computeLikelyhood();
		long t1 = System.currentTimeMillis();

		this.bw.write(""+(t1 - startTime) + "\t" + newlik + 
				"\titer: " + iter + "\t" + accumu + "\t" + Arrays.toString(weights)+"\n");
		this.bw.flush();
		lock.unlock();
	}*/
	
	private int updateGlbWt(float[] localWts, float[] grads, int accumu, int iter, int thread, String updateDetails) 
			throws IOException, InterruptedException {

		double l1 = MF.l1grads(grads);
		double l2 = MF.l2grads(grads);
		lock.lock();
		for (int i = 0; i < weights.length; i++) {
			this.weights[i] += rate * grads[i] / accumu;
			grads[i] = 0;
		}
		paramID++;
		lock.unlock();

		// add communication cost
//		Thread.sleep(1, 0);
		
		/// update local weights
		localWts = this.weights;
		
		/// update the global likelihood
//		double newlik = MF.computeLikelyhood(this.data, this.weights);
		long t1 = System.currentTimeMillis();

		this.bw[thread].write(""+(t1 - startTime)*1 + "\tXXXX\t" + this.lik + "\tworker: " + thread 
				+ "\t" + l1 + "\t" + l2 + "\t[" + updateDetails + "]\t" + Arrays.toString(weights)+"\n");
		this.bw[thread].flush();
		
		lock.lock();
		this.bw[this.totalT].write(""+(t1 - startTime)*1 + "\tXXXX\t" + this.lik + "\tworker: " + thread 
				+ "\t" + l1 + "\t" + l2 + "\t[" + updateDetails + "]\t" + Arrays.toString(weights)+"\n");
		this.bw[this.totalT].flush();
		lock.unlock();
		
		return this.paramID;
	}


	public List<Instance> readDataSet(String file) throws FileNotFoundException {
		List<Instance> dataset = new ArrayList<Instance>();
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(file));
			String line = scanner.nextLine();
			while (scanner.hasNextLine()) {
				line = scanner.nextLine();
				if (line.startsWith("#")) {
					continue;
				}
				String[] columns;
				if (file.contains("csv"))
					columns = line.split(",");
				else
					columns = line.split("\\s+");

				// skip first column and last column is the label
				int i = 1;
				float[] data = new float[columns.length - 2];
				for (i = 1; i < columns.length - 1; i++) {
					data[i - 1] = Float.parseFloat(columns[i]);
				}
				float label = Float.parseFloat(columns[i]);
				Instance instance = new Instance(label, data);
				dataset.add(instance);
			}
		} finally {
			if (scanner != null)
				scanner.close();
			this.data = dataset;
			if (this.weights == null)
				weights = new float[data.get(0).x.length];

			this.normalizeData();
			this.normalizeLabel();
		}
		return dataset;
	}


	private List<Instance> readPrimDataSet(String file) throws FileNotFoundException {
		List<Instance> dataset = new ArrayList<Instance>();
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(file));
			String line = scanner.nextLine();
			
			
			ArrayList<HashMap<String, Integer>> maps = new ArrayList<HashMap<String, Integer>>();
			for (int mm = 0; mm < 7; mm++) {
				HashMap<String, Integer> map = new HashMap<String, Integer>();
				maps.add(map);
			}
			
			while (scanner.hasNextLine()) {
				line = scanner.nextLine();
				if (line.startsWith("#")) {
					continue;
				}
				String[] columns;
				if (file.contains("csv"))
					columns = line.split(",");
				else
					columns = line.split("\\s+");

				// skip first column and last column is the label
				int i = 1;
				float[] data = new float[columns.length - 2];
				
				for (i = 1; i < columns.length - 1; i++) {
					if (i == 2 || i == 4 || i == 5 || i == 6) {
						if (maps.get(i).containsKey(columns[i])) {
							data[i - 1] = maps.get(i).get(columns[i]);
						}
						else {
							int size = maps.get(i).size();
							maps.get(i).put(columns[i], size+1);
							data[i - 1] = size+1;
						}
					}
					else {
						if(columns[i].length() < 1) {
							data[i - 1] = 0;
						}
						else
							data[i - 1] = Float.parseFloat(columns[i]);
					}
				}
				float label = Float.parseFloat(columns[i]);
				Instance instance = new Instance(label, data);
				dataset.add(instance);
			}
			System.out.println("Finish reading data");
		} finally {
			if (scanner != null)
				scanner.close();
			this.data = dataset;
			if (this.weights == null)
				weights = new float[data.get(0).x.length];

			this.normalizeData();
			this.normalizeLabel();
		}
		return dataset;	
	}
	
	private void close() throws IOException {
		// TODO Auto-generated method stub
//		for (int i = 0; i < this.totalT; i++)
//		System.out.println("output length: " + this.writeFile.length());
//		bw.write(this.writeFile);
//			if (bw != null)
//				this.bw.close();

//		long endTime = System.currentTimeMillis();
//		System.out.println("Finish training in " + (endTime - startTime));
	}
	
	
	public static void main(String... args) throws IOException {
		// List<Instance> instances = readDataSet("random_graph.txt", 20000);

		int totalT = 2;
		int miniBsize = 500;
		String alg = "Async";
		String dataset = "PE"+totalT;
		
		if (args.length > 0)
			alg = args[0];
		if (args.length > 1)
			miniBsize = Integer.parseInt(args[1]);
		if (args.length > 2)
			totalT = Integer.parseInt(args[2]);
		if (args.length > 3)
			dataset = args[3];
		
		
		LogisticRegression logistic = new LogisticRegression(totalT, miniBsize, alg, dataset);
		if (dataset == "Af")
			logistic.readDataSet("dataset/affairs.csv");
		else
			logistic.readPrimDataSet("dataset/KK_Premium_BASE_Kaggle.csv");
		
		logistic.trainInitalize();
		
		for (int i = 0; i < totalT; i++) { 
			ThreadLogR T1 = new ThreadLogR(""+i, logistic, totalT);
			T1.start();
		}

		logistic.close();
	}

}