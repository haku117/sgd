package LR;

import java.io.File;
import java.util.List;

public class MF {
	
	
	public static double classify(float[] x, float[] weights) {
		float logit = 0;
		for (int i = 0; i < weights.length; i++) {
			logit += weights[i] * x[i];
		}
		return sigmoid(logit);
	}
	
	public static double sigmoid(float z) {
		return 1.0 / (1.0 + Math.exp(-z));
	}
	
	public static double computeLik(double predicted, float label) {
		return (label * Math.log(predicted) + (1 - label) * Math.log(1 - predicted));		
	}

	public static double computeLikelyhood(List<Instance> data, float[] weights) {
		double like = 0.0;
		for (int i = 0; i < data.size(); i++) {
			double predicted = MF.classify(weights, data.get(i).x);
			float label = data.get(i).label;
			like += label * Math.log(predicted) + (1 - label) * Math.log(1 - predicted);
		}
		return like;
	}
	
	public static void compSleep(int accumu, int thread) throws InterruptedException{

//		if ((accumu + 1) % (10) == 0)
//			Thread.sleep(0, 1);
	}	
	
	public static double l1grads(float[] grads){
		double l1 = 0;
		for (int i = 0; i < grads.length; i++){
			l1 += Math.abs(grads[i]);
		}
		return l1;
	}

	public static double l2grads(float[] grads){
		double l2 = 0;
		for (int i = 0; i < grads.length; i++){
			l2 += grads[i] * grads[i];
		}
		return Math.sqrt(l2);
	}

	/*
//	private void updateWtStale(float[] deltaWt, int accumu, float[] deltaWtStale) {
//		for (int i = 0; i < weights.length; i++) {
//			this.weights[i] += rate * deltaWtStale[i] / accumu;
//			deltaWt[i] = 0;
//		}
//	}*/
	
	public static void mkdir(String fileName){
		File theDir = new File(fileName);

		// if the directory does not exist, create it
		if (!theDir.exists()) {
//		    System.out.println("creating directory: " + theDir.getName());
		    boolean result = false;

		    try{
		        theDir.mkdir();
		        result = true;
		    } 
		    catch(SecurityException se){
		        //handle it
		    }        
		    if(result) {    
		        System.out.println("DIR created");  
		    }
		}
	}
	
	public static class Instance {
		public float label;
		public float[] x;

		public Instance(float label, float[] x) {
			this.label = label;
			this.x = x;
		}
	}

}
