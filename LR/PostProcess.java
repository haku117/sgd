package LR;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import LR.MF.Instance;

public class PostProcess {


	public static void compareGrads(List<Instance> data, String fileName) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("results/" + fileName + ".txt"));
		BufferedWriter bw = new BufferedWriter(new FileWriter("results/" + fileName + "_lik.txt"));
		
		while (br.ready()) {
			String line = br.readLine();
			String param = line.split("\t")[7];
			String[] tokens = param.substring(1, param.length()-1).split(",");
			float[] weights = new float[tokens.length];
			
			for (int i = 0; i < tokens.length; i++){
				weights[i] = Float.parseFloat(tokens[i]);
			}
			
			double lik = MF.computeLikelyhood(data, weights);
			
//			int index = line.indexOf("\t");
			line = line.replaceFirst("XXXX", String.valueOf(lik));
			bw.write(line);
			bw.newLine();
		}
		
		br.close();
		bw.close();
	}
	
	private void computeGrad(float[] param, BufferedWriter bw2) throws IOException{
		
		for (int i =0; i< 10; i++) {
			for (int j = i*1000; j < (i+1)*1000; j++) {
				
			}
		}
		
	}
	
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		int totalT = 2;
		int miniBsize = 500;
		String alg = "Async";
		String dataset = "Af";

		LogisticRegression logistic = new LogisticRegression(0, 0, "", "Af");
		logistic.readDataSet("dataset/affairs.csv");
		
		String fileName = dataset + alg + miniBsize;
		PostProcess.compareGrads(logistic.data, fileName);
		
		System.out.println("Finish post process file " + fileName);
	}

}
