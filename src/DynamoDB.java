package tum;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.lang.time.StopWatch;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class DynamoDB {

	static final long kinesisLimit = 1000000;
	static final String bucketName = "input your bucket name";

	static PrintWriter coco;

	static AmazonDynamoDB Dynamo;
	static AmazonKinesis KinesisClient;
	static AmazonS3 S3Client;

	public static void dynamoMain(String filePath, AmazonDynamoDB dynamo,
			AmazonKinesis kinesis, AmazonS3 s3) {

		Dynamo = dynamo;
		KinesisClient = kinesis;
		S3Client = s3;

		File file = new File(filePath);

		String temp = filePath.substring(0, filePath.length()
				- file.getName().length());

		int index = temp.indexOf("xyz");
		temp = temp.substring(index).trim();

		// temp = {t}/{z}/{x}/
		// file.getName() = {y}

		String t, z, x, y, ext, coordinate, destination;

		String spl = "\\\\";
		int s = 0;

		String[] saved = temp.split(spl);

		x = saved[saved.length - 1];
		z = saved[saved.length - 2];

		s = x.length() + z.length();

		t = temp.substring(4, temp.length() - s - 3);
		t = t.replaceAll("\\\\", "/");

		int point = file.getName().lastIndexOf(".");
		y = file.getName().substring(0, point);
		ext = file.getName().substring(point + 1);
		coordinate = z + "/" + x + "/" + y;
		destination = "xyz" + "/" + t + "/" + z + "/" + x + "/";

		String md5sum = calcMD5sum(filePath);

		Map<String, AttributeValue> item = makeNewItem(destination + y, md5sum,
				t, ext, coordinate);

		if (putToDynamoDB(item, Dynamo) == true) {

				S3.putToS3(destination, filePath, S3Client);
		}
	}

	public static String calcMD5sum(String filePath) {

		MessageDigest md;
		String ret = "";
		try {

			StringBuilder sb = new StringBuilder();
			md = MessageDigest.getInstance("MD5");

			DigestInputStream inStream = new DigestInputStream(
					new BufferedInputStream(new FileInputStream(filePath)), md);

			while (inStream.read() != -1) {
			}

			byte[] digest = md.digest();
			inStream.close();

			for (int i = 0; i < digest.length; i++) {

				sb.append(String.format("%02x", Byte.valueOf(digest[i])));

			}
			ret = new String(sb);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	private static Map<String, AttributeValue> makeNewItem(String tileInfo,
			String md5sum, String type, String ext, String coordinate) {
		Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();

		item.put("tileInfo", new AttributeValue(tileInfo));
		item.put("md5sum", new AttributeValue(md5sum));
		item.put("type", new AttributeValue(type));
		item.put("ext", new AttributeValue(ext));
		item.put("coordinate", new AttributeValue(coordinate));

		return item;
	}

	public static boolean putToDynamoDB(Map<String, AttributeValue> item,
			AmazonDynamoDB dynamo2) {

		AmazonDynamoDB Dynamo = dynamo2;

		try {

			Region Reg = Region.getRegion(Regions.AP_NORTHEAST_1);
			Dynamo.setRegion(Reg);

			String tableName = "input your table name";// for test
			PutItemRequest putItemRequest;

			putItemRequest = new PutItemRequest(tableName, item)
					.addExpectedEntry("tileInfo",
							new ExpectedAttributeValue().withExists(false))
					.addExpectedEntry("md5sum",
							new ExpectedAttributeValue().withExists(false));
			PutItemResult putItemResult = Dynamo.putItem(putItemRequest);

			return true;

		} catch (ConditionalCheckFailedException a) {
			return false;
		}

	}

	public static String makeMokuroku(ClientConfiguration configure,
			String type, String path) {
		int count = 0;
		System.out.println("makeMokuroku\t" + type);

		do {

			PrintWriter pw = null;

			try {

				String mokurokuPath = path + "/mokuroku.csv";
				File file = new File(mokurokuPath);
				pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));

				String preFix = type;

				AmazonS3 s3;
				AWSCredentialsProvider credentialsProvider = new ClasspathPropertiesFileCredentialsProvider();

				s3 = new AmazonS3Client(credentialsProvider, configure);

				ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
						.withBucketName(bucketName).withPrefix(preFix)
						.withMaxKeys(5000);// default 1000

				ObjectListing objectListing;

				String temp;
				String regex = "mokuroku.csv.gz";
				Pattern p;
				Matcher m;

				do {
					objectListing = s3.listObjects(listObjectsRequest);

					for (S3ObjectSummary objectSummary : objectListing
							.getObjectSummaries()) {

						temp = objectSummary.getKey().substring(type.length())
								.trim()
								+ ","
								+ objectSummary.getSize()
								+ ","
								+ objectSummary.getLastModified().getTime()
								+ "," + objectSummary.getETag();

						p = Pattern.compile(regex);
						m = p.matcher(temp);
						if (!m.find()) {
							pw.println(temp);
						}
					}
					listObjectsRequest.setMarker(objectListing.getNextMarker());
				} while (objectListing.isTruncated());

				return mokurokuPath;

			} catch (Exception e) {

				e.printStackTrace();
				System.out.println("re attempt");
				System.out.println();


			} finally {
				if (pw != null) {
					pw.close();
				}
			}
		} while (++count <= 10);

		return "fail";

	}

	public static String getFile(ClientConfiguration configure, String key,
			String localPath) {

		try {
			int count = 0;

			do {

				BufferedInputStream bis = null;
				BufferedOutputStream bos = null;

				try {

					AmazonS3 s3;
					AWSCredentialsProvider credentialsProvider = new ClasspathPropertiesFileCredentialsProvider();
					s3 = new AmazonS3Client(credentialsProvider, configure);

					S3Object object = s3.getObject(new GetObjectRequest(
							bucketName, key));
					bis = new BufferedInputStream(object.getObjectContent());
					bos = new BufferedOutputStream(new FileOutputStream(
							localPath));

					byte[] buffer = new byte[1024];

					while (true) {
						int data = bis.read(buffer);
						if (data < 0) {
							break;
						}
						bos.write(buffer, 0, data);
					}

					return "success";
				} catch (Exception e) {
					e.printStackTrace();
				} finally {

					if (bis != null) {
						bis.close();
					}

					if (bos != null) {
						bos.close();
					}
				}

			} while (++count <= 3);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "fail";
	}

	public static List<String> getTypeList(ClientConfiguration configure,
			String path) {
		List<String> fileList = new ArrayList<>();
		List<String> typeList = new ArrayList<>();

		AmazonS3 s3;
		AWSCredentialsProvider credentialsProvider = new ClasspathPropertiesFileCredentialsProvider();
		s3 = new AmazonS3Client(credentialsProvider, configure);

		String preFix = "layers_txt/";

		ObjectListing list = s3.listObjects(bucketName, preFix);
		do {
			for (S3ObjectSummary s : list.getObjectSummaries()) {

				fileList.add(s.getKey());
			}
			list = s3.listNextBatchOfObjects(list);
		} while (list.getMarker() != null);

		String tempFilePath = path + "\\tempfiles";
		File dir = new File(tempFilePath);
		dir.mkdirs();

		String temp = "";
		int i = 0;

		for (String s : fileList) {
			i++;
			temp = tempFilePath + "\\tempFile" + String.valueOf(i);

			try {

				getFile(configure, s, temp);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		File tempDir = new File(tempFilePath);

		for (File f : tempDir.listFiles()) {
			List<String> tempList = makeTypeList(f);
			typeList.addAll(tempList);
		}

		return typeList;

	}

	public static List<String> makeTypeList(File file) {

		List<String> list = new ArrayList<>();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file)));

			while (true) {

				String line = reader.readLine();
				if (line == null) {
					break;
				}

				String[] temp = line.split(":", 0);

				if (temp[0].trim().equalsIgnoreCase("\"url\"")) {
					int index = 0;
					String s = temp[2].trim();
					s = s.substring(0, s.length() - 2);

					index = s.indexOf("xyz");
					if (index != -1) {

						s = s.substring(index).trim();

						index = s.indexOf("{z}");

						s = s.substring(0, index);
						list.add(s);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;

	}

	public static String compressMokuroku(String type, String mokurokuPath) {

		byte[] buf = new byte[1024];
		String from = mokurokuPath;
		String to = mokurokuPath + ".gz";

		try {

			BufferedInputStream in = new BufferedInputStream(
					new FileInputStream(from));

			GZIPOutputStream out = new GZIPOutputStream(
					new FileOutputStream(to));

			int size;
			while ((size = in.read(buf, 0, buf.length)) != -1) {
				out.write(buf, 0, size);
			}
			out.flush();
			out.close();
			in.close();

			return to;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return to;
	}

	public static void mokuroku(String path, ClientConfiguration configure) {

		File dir = new File(path);
		dir.mkdirs();

		AWSCredentialsProvider credentialsProvider = new ClasspathPropertiesFileCredentialsProvider();
		AmazonS3 s3;
		s3 = new AmazonS3Client(credentialsProvider, configure);

		List<String> typeList = getTypeList(configure, path);

		String mokurokuPath;
		String mokurokuGzPath;

		for (String type : typeList) {

			mokurokuPath = makeMokuroku(configure, type, path);

			mokurokuGzPath = compressMokuroku(type, mokurokuPath);
			File putFile = new File(mokurokuGzPath);

			System.out.println("put:" + type + putFile.getName());

			s3.putObject(new PutObjectRequest(bucketName, type
					+ putFile.getName(), putFile));

			putFile.delete();
		}
	}

	public static void cocotile(String path, ClientConfiguration configure) {

		try {

			AWSCredentialsProvider credentialsProvider = new ClasspathPropertiesFileCredentialsProvider();
			AmazonS3 s3;

			s3 = new AmazonS3Client(credentialsProvider, configure);

			File dir = new File(path);
			dir.mkdirs();

			List<String> typeList = getTypeList(configure, path);

			String typeExtListPath = path + "\\typeList.txt";
			File file = new File(typeExtListPath);
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(
					file)));

			typeList = distinct(typeList);
			Collections.sort(typeList);

			for (String t : typeList) {
				pw.println(t);
			}

			pw.close();

			String str = "";
			Scanner scan1 = new Scanner(file);

			while (scan1.hasNext()) {

				str = scan1.next();

				String localPath = path + "\\" + str;

				dir = new File(localPath);
				dir.mkdirs();

				getFile(configure, str + "mokuroku.csv.gz", localPath
						+ "mokuroku.csv.gz");
				readGzipFile(configure, localPath + "mokuroku.csv.gz", path, s3);// and
																					// make
																					// cocotile

			}// end of make cocotile process

			System.out.println("uploadCocotile");
			uploadCocotile(s3, path);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static List<String> distinct(List<String> slist) {
		// sort
		return new ArrayList<String>(new LinkedHashSet<String>(slist));
	}

	public static void readGzipFile(ClientConfiguration configure,
			String filePath, String localPath, AmazonS3 s3) {

		try {
			File mokuroku = new File(filePath);

			Scanner scan = new Scanner(new InputStreamReader(
					new GZIPInputStream(new BufferedInputStream(
							new FileInputStream(mokuroku)))));

			String regex = "mokuroku";
			Pattern p;
			Matcher m;

			String str = "", type = "";

			String coordinate = "";
			int index = 0;

			while (scan.hasNext()) {
				str = scan.next();

				p = Pattern.compile(regex);
				m = p.matcher(str);

				if (!m.find()) {

					String[] temp1 = str.split(",", 0);
					index = temp1[0].indexOf(".");

					coordinate = temp1[0];
					coordinate = coordinate.substring(0,
							coordinate.lastIndexOf("."));

					String[] temp2 = coordinate.split("/", 0);

					// z temp2[0]
					// x temp2[1]
					// y temp2[2]

					type = filePath;
					index = type.indexOf("xyz");
					type = type.substring(index + 4).trim();
					type = type.substring(0, type.length()
							- mokuroku.getName().length() - 1);

					makeCocotile(temp2[0], temp2[1], temp2[2], localPath, type);
				}
			}// end of make cocotile process

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void cocotileSearch(String filePath) {

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

	public static String uploadCocotile(AmazonS3 s3, String localPath) {

		System.gc();

		try {
			String localCocotilePath = localPath;

			FileOutputStream fos = new FileOutputStream(localPath + "\\"
					+ "cocotemp");
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			coco = new PrintWriter(osw);
			cocotileSearch(localCocotilePath + "\\cocotile");
			coco.close();

			String line;

			int MAX_INFLIGHT = 1000;
			// ExecutorService executor = Executors.newCachedThreadPool();
			ExecutorService executor = Executors.newFixedThreadPool(2000);

			List<Future<String>> inFlight = new ArrayList<Future<String>>();

			Scanner scanner = new Scanner(new File(localCocotilePath
					+ "\\cocotemp"));

			while (scanner.hasNextLine()) {
				line = scanner.nextLine();

				// flow control
				while (inFlight.size() > MAX_INFLIGHT) {
					List<Future<String>> dones = new ArrayList<Future<String>>();

					for (Future<String> f : inFlight) {
						if (f.isDone()) {
							f.get();
							dones.add(f);
						}
					}

					inFlight.removeAll(dones);

					if (inFlight.size() > MAX_INFLIGHT) {
						Thread.sleep(1 * 1000);
					}
				}

				// start new task

				cocotileUploadTask task = new cocotileUploadTask(line, s3);

				Future<String> future = executor.submit(task);
				inFlight.add(future);
			}

			// wait for completion
			for (Future<String> f : inFlight) {
				f.get();
			}

			System.out.println("completed");

			return "completed";
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	static class cocotileUploadTask implements Callable<String> {

		String line;
		AmazonS3 s3;
		int i;
		static AtomicInteger v = new AtomicInteger();

		public cocotileUploadTask(String line, AmazonS3 s3) {
			this.line = line;
			this.s3 = s3;
		}

		@Override
		public String call() {
			int count = 0;
			do {

				try {
					StopWatch stopWatch = new StopWatch();
					stopWatch.start();

					String preFix = line;
					int index;

					// type = filePath;
					index = preFix.indexOf("cocotile");
					preFix = preFix.substring(index).trim();
					preFix = preFix.replaceAll("\\\\", "/");

					String[] temp = preFix.split("/", 0);
					/*
					 * temp[0] cocotile temp[1] z temp[2] x temp[3] y.csv preFix
					 * S3 path without filename line local file Path
					 */
					preFix = temp[0] + "/" + temp[1] + "/" + temp[2] + "/";

					S3.putToS3(preFix, line, s3);

					stopWatch.stop();

					System.out.println(v.incrementAndGet() + "\t" + line + "\t"
							+ stopWatch.getTime());

					return "success";
				} catch (Exception e) {
					e.printStackTrace();

				}

				// Thread.sleep(10 * 1000);
			} while (++count <= 3);
			return "fail";
		}
	}

	public static void makeCocotile(String z, String x, String y,
			String localPath, String type) throws Exception {

		File cocotileDir = new File(localPath + "\\cocotile\\" + z + "\\" + x
				+ "\\");
		cocotileDir.mkdirs();

		File cocotile = new File(localPath + "\\cocotile\\" + z + "\\" + x
				+ "\\" + y + ".csv");

		if (!cocotile.exists()) {// first time
			FileOutputStream fos = new FileOutputStream(cocotile);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			PrintWriter pw = new PrintWriter(osw);
			pw.print(type);

			pw.close();
			osw.close();
			fos.close();

		} else {// else
			FileOutputStream fos = new FileOutputStream(cocotile, true);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			PrintWriter pw = new PrintWriter(osw);
			pw.print("," + type);

			pw.close();
			osw.close();
			fos.close();
		}

	}

}// end of class
