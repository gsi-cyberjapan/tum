package tum;

import java.io.File;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class S3 {

	public static String putToS3(String path, String filepath, AmazonS3 s3) {
		int count = 0;

		do {
			try {

				String bucketName = "input your bucket name";
				File file = new File(filepath);

				s3.putObject(new PutObjectRequest(bucketName, path
						+ file.getName(), file));
				System.out.println("\tS3 Put:" + path + file.getName());

				return "success";
			} catch (Exception e) {
				e.printStackTrace();
			}
		} while (++count <= 1000);

		return "fail";
	}
}
