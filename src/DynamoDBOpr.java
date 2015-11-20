package tum;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;

public class DynamoDBOpr {

	static AmazonDynamoDB Dynamo;
	static long kinesisLimit = 40960;

	public static void DynamoMain(String filePath){

		File file = new File(filePath);

		if(file.length() > kinesisLimit){
			Jets3tOpr.PutToS3(file);
		}

		String temp = filePath.substring(0, filePath.length() - file.getName().length() );

		int index = temp.indexOf("xyz");
	    temp = temp.substring(index).trim();

	    //temp = {t}/{z}/{x}/
	    //file.getName() = {y}

	    String t,z,x,ext,coodinate;

	    String spl ="\\\\";
	    int s=0;

	    String[] saved =temp.split(spl);

	    	x = saved[saved.length-1];
	    	z = saved[saved.length-2];

	    	s = x.length()+z.length();

	    	t=temp.substring(4, temp.length()-s-3);

	    	int point = file.getName().lastIndexOf(".");
	    	ext=file.getName().substring(point + 1);
	    	coodinate = z+"/"+x+"/"+ file.getName().substring(0,file.getName().length() - ext.length()-1);

	    	String md5sum = "fornow";//change later

	    	Map<String, AttributeValue> item = newItem(temp,md5sum,t,ext,z+"/"+x+"/"+file.getName().substring(ext.length()+1));

	    	DynamoPut(item);

	    	KinesisOpr.KinesisMain(filePath);

	}

	private static Map<String, AttributeValue> newItem(String fullpath, String md5sum, String type, String ext, String coordinate) {
        Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();

        item.put("fullpath", new AttributeValue(fullpath));
        item.put("md5sum", new AttributeValue(md5sum));
        item.put("type", new AttributeValue(type));
        item.put("ext", new AttributeValue(ext));
        item.put("coordinate", new AttributeValue(coordinate));

        return item;
    }

	public static void DynamoPut(Map<String, AttributeValue> item){

		//tuning is now in progress

		Dynamo = new AmazonDynamoDBClient();

		Region Reg = Region.getRegion(Regions.AP_NORTHEAST_1);
		Dynamo.setRegion(Reg);

		String tableName = "gsi-tabel";//for test
		PutItemRequest putItemRequest;
		putItemRequest =
				new PutItemRequest(tableName, item).addExpectedEntry("filename", new ExpectedAttributeValue().withExists(false));

		PutItemResult putItemResult =Dynamo.putItem(putItemRequest);



	}

}//end of class
