package tum;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

public class main {

	public static void main(String[] args) {

		File file = new File("Params.xml");
		AWSCredentialsProvider credentialsProvider = new ClasspathPropertiesFileCredentialsProvider();

		AmazonDynamoDB Dynamo;
		AmazonKinesis kinesis;
		AmazonS3 s3;

		ClientConfiguration conf = null;
		if (file.exists()) {
			try {
				conf = init(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		long st, ed;

		String mode = args[0];// tum mokuroku cocotile
		String path = args[1]; // input Path to xyzDir

		st = printDate("system start\t");

		String regex1 = "tum";
		Pattern p1 = Pattern.compile(regex1, Pattern.CASE_INSENSITIVE);
		String regex2 = "mokuroku";
		Pattern p2 = Pattern.compile(regex2, Pattern.CASE_INSENSITIVE);
		String regex3 = "cocotile";
		Pattern p3 = Pattern.compile(regex3, Pattern.CASE_INSENSITIVE);

		if (check(p1, mode)) {

			Dynamo = new AmazonDynamoDBClient(credentialsProvider, conf);
			kinesis = new AmazonKinesisClient(credentialsProvider, conf);
			s3 = new AmazonS3Client(credentialsProvider, conf);

			TaskManager.tileSearchMain(path, Dynamo, kinesis, s3);
		}

		if (check(p2, mode)) {

			DynamoDB.mokuroku(path, conf);

		}

		if (check(p3, mode)) {

			DynamoDB.cocotile(path, conf);

		}

		ed = printDate("system end\t");

		Calendar c = Calendar.getInstance();

		long sa = ed - st - c.getTimeZone().getRawOffset();
		c.setTimeInMillis(sa);

		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

		String[] s = sdf.format(c.getTime()).split(":");
		int hour = Integer.parseInt(s[0]);
		int minute = Integer.parseInt(s[1]);
		int second = Integer.parseInt(s[2]);

		System.out.println("total time\t" + hour + ":" + minute + ":" + second);
		
	}

	private static Boolean check(Pattern p, String target) {

		Matcher m = p.matcher(target);

		if (m.find()) {
			return true;
		} else {
			return false;
		}
	}

	static long printDate(String t) {
		Calendar now = Calendar.getInstance();

		int year = now.get(now.YEAR);
		int month = now.get(now.MONTH) + 1;
		int date = now.get(now.DATE);
		int hour = now.get(now.HOUR_OF_DAY);
		int minute = now.get(now.MINUTE);
		int second = now.get(now.SECOND);

		String user = System.getProperty("user.name");
		System.out.println("user:" + user);

		System.out.print(t + year + "/" + month + "/" + date + "\t");
		System.out.printf("%02d:%02d:%02d%n", hour, minute, second);

		long sa = now.getTimeInMillis();

		return sa;

	}

	private static ClientConfiguration init(File file) throws Exception {

		int maxConnections = 100;

		DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document doc = builder.parse(file);

		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();

		String host = xpath.evaluate("/Params/Proxy/Host/text()", doc);
		String port = xpath.evaluate("/Params/Proxy/Port/text()", doc);

		ClientConfiguration conf = new ClientConfiguration();
		conf.setMaxConnections(maxConnections);
		if (!host.equals("") && !port.equals("")) {
			conf.setProtocol(Protocol.HTTPS);
			conf.setProxyHost(host);
			conf.setProxyPort(Integer.parseInt(port));

			return conf;
		}
		return conf;
	}
}// end of class