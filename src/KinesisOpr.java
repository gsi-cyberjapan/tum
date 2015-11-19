package tum;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.amazonaws.services.kinesis.model.PutRecordResult;

public class KinesisOpr {
	
	static AmazonKinesis kinesis;
	static int count =0;
	
	public static void KinesisMain(String filePath){
		count++;
	//	System.out.println("Num:"+count+"\tput: "+filePath);
		try {
			
			KinesisPut(filePath);
			
		} catch (FileNotFoundException e) {
			
		}
 		
	}
	
	public static void KinesisPut(String filePath) throws FileNotFoundException{
	

		BasicAWSCredentials cred = CredentialSettings.set();
		kinesis = new AmazonKinesisClient(cred);
		
		Region reg = Region.getRegion(Regions.AP_NORTHEAST_1);
	    kinesis.setRegion(reg);
	    
 	   	File f=new File(filePath);
		int length=(int)f.length();
		byte[] buf=new byte[length];
		FileInputStream fs;
		ByteBuffer bb=ByteBuffer.wrap(buf);
		
			PutRecordRequest putRecordRequest = new PutRecordRequest();
	        putRecordRequest.setStreamName("test"); 
	        /*
	         use "test" stream for now.
	         you should change stream name when you use this code.
	         */
	        
	        putRecordRequest.setData(bb);

  	       //filePath    full path
  	       //f.getName() file name
  	        
  	        String key = filePath.substring(0, filePath.length() - f.getName().length() );//key = directory
  	        int index = key.indexOf("xyz");
  	        key = key.substring(index);//key = xyz/{t}/{z}/{x}/
  	        
  	        //System.out.println(key+f.getName());
 	       	putRecordRequest.setPartitionKey(key);
 	        PutRecordResult putRecordResult = kinesis.putRecord(putRecordRequest);
 	        
 	        /*
 	         
 	        in lambda 
 	        
 	       	KinesisEvent event = null;
 	        
 	       	KinesisEventRecord  rec =  event.getRecords().get(1);
 	       
 	       	rec.getKinesis().getData();
 	       	rec.getKinesis().getPartitionKey();
 	       	
 	       */
 	        
	}		
}//end of class
