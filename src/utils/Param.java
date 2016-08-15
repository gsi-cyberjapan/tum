package tum.utils;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;

public class Param {
	public static String bucketName;
	public static String tableName;
	public static int tileThreadNum;
	public static int cocotileThreadNum;
	public static ClientConfiguration conf;

	// setParams
	public static void setParams(){
		try{
			File file = new File("Params.xml");

			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(file);

			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();

			getParams(doc, xpath);
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	// getParams
	private static void getParams(Document doc, XPath xpath)
	{
		int maxConnections = 100;

		try {
			// S3
			bucketName = xpath.evaluate("/Params/S3/Bucket/text()", doc);
			//String maxConn = xpath.evaluate("/Params/S3/MaxConn/text()", doc);
			//int maxConnections = Integer.parseInt(maxConn);

			// DynamoDB
			tableName = xpath.evaluate("/Params/DynamoDB/TableName/text()", doc); // DynamoDB Table

			// Tile Thread Number
			String tileThreads = xpath.evaluate("/Params/Tile/Threads/text()", doc);
			tileThreadNum = Integer.parseInt(tileThreads);

			// Cocotile Thread Number
			String cocotileThreads = xpath.evaluate("/Params/Cocotile/Threads/text()", doc);
			cocotileThreadNum = Integer.parseInt(cocotileThreads);

			// Proxy
			String host = xpath.evaluate("/Params/Proxy/Host/text()", doc);
			String port = xpath.evaluate("/Params/Proxy/Port/text()", doc);

			conf = new ClientConfiguration();
			conf.setMaxConnections(maxConnections);
			if (!host.equals("") && !port.equals("")) {
				conf.setProtocol(Protocol.HTTPS);
				conf.setProxyHost(host);
				conf.setProxyPort(Integer.parseInt(port));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
