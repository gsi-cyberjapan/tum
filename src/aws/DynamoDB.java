package tum.aws;

import java.util.HashMap;
import java.util.Map;

import tum.utils.Param;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;

public class DynamoDB {

	// makeNewItem
	public static Map<String, AttributeValue> makeNewItem(String tileInfo, String md5sum,
			String type, String ext, String coordinate) {
		Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();

		item.put("tileInfo", new AttributeValue(tileInfo));
		item.put("md5sum", new AttributeValue(md5sum));
		item.put("type", new AttributeValue(type));
		item.put("ext", new AttributeValue(ext));
		item.put("coordinate", new AttributeValue(coordinate));

		return item;
	}

	// putToDynamoDB
	public static boolean putToDynamoDB(AmazonDynamoDB dynamoClient, Map<String, AttributeValue> item) {
		try {
			//AmazonDynamoDB dynamo = getDynamoClient();

			Region Reg = Region.getRegion(Regions.AP_NORTHEAST_1);
			dynamoClient.setRegion(Reg);

			String tableName = Param.tableName;
			PutItemRequest putItemRequest;

			putItemRequest = new PutItemRequest(tableName, item)
					.addExpectedEntry("tileInfo", new ExpectedAttributeValue().withExists(false))
					.addExpectedEntry("md5sum", new ExpectedAttributeValue().withExists(false));

			/*@inui PutItemResult putItemResult =*/ dynamoClient.putItem(putItemRequest);

			return true;
		} catch (ConditionalCheckFailedException a) {
			return false;
		}
	}

	// getDynamoClient
	public static AmazonDynamoDB getDynamoClient(){
		return new AmazonDynamoDBClient(new ClasspathPropertiesFileCredentialsProvider(), Param.conf);
	}
}
