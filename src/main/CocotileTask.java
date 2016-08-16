package tum.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import tum.aws.S3;
import tum.utils.Param;

import com.amazonaws.services.s3.AmazonS3;

public class CocotileTask {
	static PrintWriter coco;

	// exec
	public static String exec(String localPath){
		System.out.println("prepare for upload");
		System.out.println("this process can be time consuming...");

		System.gc();

		try {
			String localCocotilePath = localPath;

			FileOutputStream fos = new FileOutputStream(localPath + "\\" + "cocotemp");
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			coco = new PrintWriter(osw);
			cocotileSearch(localCocotilePath + "\\cocotile");
			coco.close();

			System.out.println("upload cocotile");
			String line;

			int threadNum = Param.cocotileThreadNum;
			ExecutorService executor = Executors.newFixedThreadPool(threadNum + 100);
			List<Callable<String>> tasks = new ArrayList<Callable<String>>();

			int count = 0;
			AmazonS3 s3Client = S3.getS3Client();
			Scanner scanner = new Scanner(new File(localCocotilePath + "\\cocotemp"));

			while (scanner.hasNextLine()) {
				line = scanner.nextLine();
				tasks.add(new cocotileUpload(s3Client, line));

				if (tasks.size() > threadNum) {
					executor.invokeAll(tasks);
					tasks.clear();

					count++;
					if (count > 10) {
						count = 0;
						System.gc();
					}
				}
			}

			scanner.close();
			executor.invokeAll(tasks);
			tasks.clear();

			System.out.println("completed");

			return "completed";
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	// cocotileSearch
	private static void cocotileSearch(String filePath) {
		File file = new File(filePath);

		if (!file.isDirectory()) {
			file = file.getParentFile();
		}

		for (File fc : file.listFiles()) {
			if (fc.isDirectory()) {
				cocotileSearch(fc.getPath());
			} else {
				fc.getPath();
				coco.println(fc.getPath());
			}
		}
	}

	// cocotileUpload
	static class cocotileUpload implements Callable<String> {
		AmazonS3 s3Client;
		String line;
		int i;
		static AtomicInteger v = new AtomicInteger();

		public cocotileUpload(AmazonS3 s3Client, String line) {
			this.s3Client = s3Client;
			this.line = line;
		}

		@Override
		public String call() {
			do {
				try {
					String preFix = line;
					int index;

					// type = filePath;
					index = preFix.indexOf("cocotile");
					preFix = preFix.substring(index).trim();
					preFix = preFix.replaceAll("\\\\", "/");

					String[] temp = preFix.split("/", 0);

					/* temp[0] cocotile temp[1] z temp[2] x temp[3] y.csv preFix
					 * S3 path without filename line local file Path */

					preFix = "xyz/" + temp[0] + "/" + temp[1] + "/" + temp[2] + "/";
					S3.putToS3(s3Client, preFix, line);

					System.out.println(v.incrementAndGet() + "\t" + line);

					return "success";
				} catch (Exception e) {
					e.printStackTrace();
				}
			} while (true);
		}
	}
}
