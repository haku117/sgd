package LR;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

class ThreadDemo extends Thread {
	private Thread t;
	private String threadName;
	
	private String[] shared;
	private int totalT;

	ThreadDemo(String name, String[] sh, int totalT) {
		threadName = name;
		shared = sh;
		System.out.println("Creating " + threadName);
		this.totalT = totalT;
	}

	public void run() {
		System.out.println("Running " + threadName);
		try {
			for (int i = 4; i > 0; i--) {
				System.out.println("Thread: " + this.getId() + ", " 
						+ this.shared[(int) ((Integer.parseInt(this.threadName)+i*this.totalT) % shared.length)]);
				// Let the thread sleep for a while.
				Thread.sleep(50);
			}
		} catch (InterruptedException e) {
			System.out.println("Thread " + threadName + " interrupted.");
		}
		System.out.println("Thread " + threadName + " exiting.");
	}

	public void start() {
		System.out.println("Starting " + threadName);
		if (t == null) {
			t = new Thread(this, threadName);
			t.start();
		}
	}
}


class ThreadLogR extends Thread {
	private Thread t;
	private String threadName;
	
	private LogisticRegression logr;
	private int totalT;
	

	ThreadLogR(String name, LogisticRegression logr, int totalT) throws IOException {
		threadName = name;
		this.logr = logr;
		System.out.println("Creating " + threadName);
		this.totalT = totalT;
		
	}

	public void run() {
		System.out.println("Running " + threadName);
		try {
			
			logr.trainThread(Integer.parseInt(threadName));
			
		} catch (Exception e) {
			System.out.println("Thread " + threadName + " interrupted.");
			e.printStackTrace();
		}
		System.out.println("Thread " + threadName + " exiting.");
	}

	public void start() {
		System.out.println("Starting " + threadName);
		if (t == null) {
			t = new Thread(this, threadName);
			t.start();
		}
	}
}




public class Multithread {

	public static void main(String args[]) {
		
		String[] sh = new String[100];
		for (int i = 0; i < sh.length; i++)
			sh[i] = String.valueOf(i+100);
		
		int n = 5;
		for (int i = 0; i < n; i++) { 
			ThreadDemo T1 = new ThreadDemo(""+i, sh, n);
			T1.start();
		}
	}
}