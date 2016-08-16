package tum.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tum.aws.DynamoDB;
import tum.aws.S3;
import tum.utils.Param;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.s3.AmazonS3;

public class TileTask {
	static int count = 0;
	static List<Callable<String>> tasks = new ArrayList<Callable<String>>();
	static ExecutorService executor = Executors.newFixedThreadPool(Param.tileThreadNum);

	//static int flug = 0;
	//static int testcount = 0;
	static AmazonDynamoDB dynamoClient;
	static AmazonS3 s3Client;

	// exec
	public static void exec(String localPath) {

		dynamoClient = DynamoDB.getDynamoClient();
		s3Client = S3.getS3Client();

		tileSearch(localPath);

		try {
			executor.invokeAll(tasks);
			tasks.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// tileSearch
	private static void tileSearch(String localPath) {
		File file = new File(localPath);

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
					addTaskToList(fc.getPath());
				}
			}
		}
	}

	// addTaskToList
	private static void addTaskToList(String filePath) {
		tasks.add(new parallelTasks(filePath));

		if (tasks.size() > Param.tileThreadNum) {
			try {
				executor.invokeAll(tasks);
				count++;

				if (count > 10) {
					count = 0;
					System.gc();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			tasks.clear();
		}
	}

	// parallelTasks
	static class parallelTasks implements Callable<String> {
		int taskNumber;
		String filePath;

		public parallelTasks(String filePath) {
			this.filePath = filePath;
		}

		@Override
		public String call() throws Exception {
			Tile.uploadTile(dynamoClient, s3Client, filePath);
			return filePath;
		}
	}
}
