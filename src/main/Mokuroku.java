package tum.main;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

import tum.aws.S3;
import tum.utils.LocalFile;
import tum.utils.Util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class Mokuroku {

	// execute
	public static void exec(String localPath){
		File dir = new File(localPath);
		dir.mkdirs();

		List<String> typeList = LocalFile.getTypeList(localPath);
		typeList = Util.distinct(typeList);
		Collections.sort(typeList);

		String mokurokuPath;
		String mokurokuGzPath;

		AmazonS3 s3Client = S3.getS3Client();

		for (String type : typeList) {
			mokurokuPath = makeMokuroku(type, localPath);
			mokurokuGzPath = compressMokuroku(type, mokurokuPath);
			File putFile = new File(mokurokuGzPath);

			System.out.println("put:" + type + putFile.getName());

			//@inui s3.putObject(new PutObjectRequest(Param.bucketName, type + putFile.getName(), putFile));
			S3.putToS3(s3Client, type + putFile.getName(), putFile);

			putFile.delete();
		}
	}

	// makeMokuroku
	private static String makeMokuroku(String type, String localPath){
		System.out.println("makeMokuroku\t" + type);

		do {
			PrintWriter pw = null;

			try {
				String mokurokuPath = localPath + "/mokuroku.csv";
				File file = new File(mokurokuPath);
				pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));

				String temp;
				String regex = "mokuroku.csv.gz";
				String regex2 = "style";

				Pattern p;
				Matcher m;

				Pattern p2;
				Matcher m2;

				AmazonS3 s3client = S3.getS3Client();
				ListObjectsRequest listObjectsRequest = S3.listObjReq(type);
				ObjectListing objectListing;

				do {
					objectListing = s3client.listObjects(listObjectsRequest);

					for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
						temp = objectSummary.getKey().substring(type.length()).trim()
								+ ","
								//@inui + objectSummary.getLastModified().getTime()
								+ Util.mills2sec(objectSummary.getLastModified().getTime())
								+ ","
								+ objectSummary.getSize()
								+ ","
								+ objectSummary.getETag()
								+ "\n";

						p = Pattern.compile(regex);
						m = p.matcher(temp);

						p2 = Pattern.compile(regex2);
						m2 = p2.matcher(temp);

						int index = temp.indexOf(".");

						if (!m.find() && !m2.find() && index != -1) {
							//@inui pw.println(temp);
							pw.print(temp);
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
				if (pw != null) pw.close();
			}
		} while (true);
	}

	// compressMokuroku
	private static String compressMokuroku(String type, String mokurokuPath) {
		byte[] buf = new byte[1024];
		String from = mokurokuPath;
		String to = mokurokuPath + ".gz";

		try {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(from));
			GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(to));

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
}
