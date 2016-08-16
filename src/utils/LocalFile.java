package tum.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import tum.aws.S3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class LocalFile {

	// calcMD5sum
	public static String calcMD5sum(String filePath) {
		MessageDigest md;
		String ret = "";

		try {
			StringBuilder sb = new StringBuilder();
			md = MessageDigest.getInstance("MD5");

			DigestInputStream inStream = new DigestInputStream(new BufferedInputStream(new FileInputStream(filePath)), md);

			while (inStream.read() != -1) {}

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

	// getTypeList
	public static List<String> getTypeList(String localPath) {
		List<String> fileList = new ArrayList<>();
		List<String> typeList = new ArrayList<>();

		String preFix = "layers_txt/";
		AmazonS3 s3client = S3.getS3Client();
		ObjectListing list = s3client.listObjects(Param.bucketName, preFix);

		do {
			for (S3ObjectSummary s : list.getObjectSummaries()) {

				fileList.add(s.getKey());
			}
			list = s3client.listNextBatchOfObjects(list);
		} while (list.getMarker() != null);

		String tempFilePath = localPath + "\\tempfiles";
		File dir = new File(tempFilePath);
		dir.mkdirs();

		String temp = "";
		int i = 0;

		for (String s : fileList) {
			i++;
			temp = tempFilePath + "\\tempFile" + String.valueOf(i);

			try {
				S3.getFileFromS3(s, temp);
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

	// makeTypeList
	private static List<String> makeTypeList(File file) {
		List<String> list = new ArrayList<>();

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

			while (true) {
				String line = reader.readLine();
				if (line == null) break;

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

			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}
}
