package tum.main;

import java.io.File;
import java.util.Map;

import tum.aws.DynamoDB;
import tum.aws.S3;
import tum.utils.LocalFile;
import tum.utils.Util;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.s3.AmazonS3;

public class Tile {

	// uploadTile
	public static void uploadTile(AmazonDynamoDB dynamoClient, AmazonS3 s3Client, String filePath) {
		File file = new File(filePath);
		String temp = filePath.substring(0, filePath.length() - file.getName().length());

		int index = temp.indexOf("xyz");
		temp = temp.substring(index).trim();

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

		String md5sum = LocalFile.calcMD5sum(filePath);

		Map<String, AttributeValue> item = DynamoDB.makeNewItem(destination + y + "." + ext, md5sum, t, ext, coordinate);

		if (DynamoDB.putToDynamoDB(dynamoClient, item) == true) {
			if (!ext.equals("") && ext.equals("geojson")){
				//S3.putToS3(destination, filePath, "Content-Type", "text/plain");
				S3.putToS3(s3Client, destination, filePath, "text/plain");
			} else {
				S3.putToS3(s3Client, destination, filePath);
			}
		} else {
			try{
				Util.log("skipped file : " + filePath);
			} catch(Exception e) {}
		}
	}
}
