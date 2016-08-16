package tum.aws;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import tum.utils.LocalFile;
import tum.utils.Param;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class S3 {
	static int count = 0;

	// putToS3
	public static String putToS3(AmazonS3 s3Client, String s3FilePath, String filePath) {
		do {
			try {
				File file = new File(filePath);

				s3Client.putObject(new PutObjectRequest(Param.bucketName, s3FilePath + file.getName(), file));
				count++;

				System.out.println(count + "\tS3 Put:" + s3FilePath + file.getName());

				return "success";
			} catch (Exception e) {
				e.printStackTrace();
			}
		} while (true);
	}

	// putToS3 with Metadata
	public static String putToS3(AmazonS3 s3Client, String s3FilePath, String filePath, String contentType) {
		do {
			try {
				File file = new File(filePath);

				ObjectMetadata metadata = new ObjectMetadata();
				metadata.setContentType(contentType);

				s3Client.putObject(new PutObjectRequest(Param.bucketName, s3FilePath + file.getName(), file)
					.withMetadata(metadata));
				count++;

				System.out.println(count + "\tS3 Put:" + s3FilePath + file.getName());

				return "success";
			} catch (Exception e) {
				e.printStackTrace();
			}
		} while (true);
	}

	// putToS3 by mokuroku
	public static void putToS3(AmazonS3 s3Client, String fileName, File putFile) {
		try {
			s3Client.putObject(new PutObjectRequest(Param.bucketName, fileName, putFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// checkFileInS3
	public static String getFileFromS3(String key, String localPath) {
		try {
			do {
				BufferedInputStream bis = null;
				BufferedOutputStream bos = null;

				try {
					AmazonS3 s3 = getS3Client();

					S3Object object = s3.getObject(new GetObjectRequest(Param.bucketName, key));
					bis = new BufferedInputStream(object.getObjectContent());

					/*if(localPath.contains("/")){
						tum.utils.Util.log("(getFileFromS3:86) localPath: " + localPath);
					}*/

					bos = new BufferedOutputStream(new FileOutputStream(localPath));

					byte[] buffer = new byte[1024];

					while (true) {
						int data = bis.read(buffer);
						if (data < 0) {
							break;
						}

						bos.write(buffer, 0, data);
					}

					bis.close();
					bos.close();

					if (LocalFile.calcMD5sum(localPath).equals(object.getObjectMetadata().getETag())) {
						object.close();
						return "success";
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("retry Download\t" + localPath);
					System.out.println("S3 prefix is " + key);
				} finally {
					if (bis != null) bis.close();
					if (bos != null) bos.close();
				}
			} while (true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "fail";
	}

	// listObjReq
	public static ListObjectsRequest listObjReq(String preFix){
		return new ListObjectsRequest()
			.withBucketName(Param.bucketName).withPrefix(preFix)
			.withMaxKeys(5000); // default 1000
	}

	// getS3Client
	public static AmazonS3 getS3Client(){
		return new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider(), Param.conf);
	}

	/*
    // getListObjects
	public static ObjectListing getListObjects(AmazonS3 s3, ListObjectsRequest listObjectsRequest){
		ObjectListing objectListing = null;

		try{
			//AmazonS3 s3 = getS3Client();
			objectListing = s3.listObjects(listObjectsRequest);
		} catch(Exception e) {
			e.printStackTrace();
		}

		return objectListing;
	}

	// getListObjects by LocalFile.getTypeList
	public static ObjectListing getListObjects(String preFix){
		ObjectListing objectListing = null;

		try{
			//AmazonS3 s3 = getS3Client();
			objectListing = s3.listObjects(Param.bucketName, preFix);
		} catch(Exception e) {
			e.printStackTrace();
		}

		return objectListing;
	}

	// getNextfObjects
	public static ObjectListing getNextfObjects(ObjectListing list){
		ObjectListing objectListing = null;

		try{
			AmazonS3 s3 = getS3Client();
			objectListing = s3.listNextBatchOfObjects(list);
		} catch(Exception e) {
			e.printStackTrace();
		}

		return objectListing;
	}
	*/
}
