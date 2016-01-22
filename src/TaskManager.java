package tum;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.s3.AmazonS3;

public class TaskManager {

	static int count = 0;
	static final int threadNumber = 10;
	static ExecutorService executor = Executors.newFixedThreadPool(threadNumber);
	static List<Callable<String>> tasks = new ArrayList<Callable<String>>();
	static ClientConfiguration conf;
	static AmazonDynamoDB Dynamo;
	static AmazonKinesis Kinesis;
	static AmazonS3 S3;


	public static void tileSearchMain(String filePath, AmazonDynamoDB dynamo, AmazonKinesis kinesis, AmazonS3 s3) {

		Dynamo = dynamo;
		Kinesis = kinesis;
		S3 = s3;

		tileSearch(filePath);
		try {
			executor.invokeAll(tasks);
			tasks.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void tileSearch(String filePath) {

		File file = new File(filePath);

		if (!file.isDirectory()) {
			file = file.getParentFile();
		}

		for (File fc : file.listFiles()) {

			if (fc.isDirectory()) {
				tileSearch(fc.getPath());

			} else {
				fc.getPath();
				String csv = "csv";

				if (!fc.getPath().matches(".*" + csv + ".*")) { // without CSV
																// file
					addTaskToList(fc.getPath());

				}
			}
		}

	}

	public static void addTaskToList(String filePath) {

		tasks.add(new parallelTasks(filePath));

		if (tasks.size() > threadNumber) {

			try {
				executor.invokeAll(tasks);
				count++;
				if(count>10){
				count = 0;
				System.gc();
				}
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			tasks.clear();
		}

	}

	static class parallelTasks implements Callable<String> {

		int taskNumber;
		String str;

		public parallelTasks(String str) {
			this.str = str;
		}

		@Override
		public String call() throws Exception {

			DynamoDB.dynamoMain(str, Dynamo, Kinesis, S3);
			return str;

		}
	}

}// end of class
