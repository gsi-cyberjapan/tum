package tum;//Tile Upload Manager

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TaskManager {
	
	static long start = System.nanoTime(),st = System.nanoTime();
	static int timeSum=0;
	
	static int count=0,sum=0;
	static final int threadNumber = 100;
	static ExecutorService executor =Executors.newCachedThreadPool();
    static List<Callable<String>> tasks = new ArrayList<Callable<String>>();
	
	public static void TileSearch(String filePath) {
		
		List<String> pl = new ArrayList<String>();
		File file = new File(filePath);
		
		
		if (!file.isDirectory()){ 
			file = file.getParentFile();
			}
		
		for (File fc : file.listFiles()) {
			
			if(fc.isDirectory()){ 
				TileSearch(fc.getPath());
			}
			
			else{
				   fc.getPath();
				   String csv = "csv";
				
				   if(!fc.getPath().matches(".*"+csv+".*")){ //without CSV file
					   count++;
					   TaskList(fc.getPath());				   
				  }				
				}
			}
		try {
			executor.invokeAll(tasks);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		tasks.clear();
	}
	
	public static void TaskList(String filePath) {
		try {
		long ed,temp1;
		tasks.add(new ParallelTasks(filePath));
			
		if(count==threadNumber){
			
				ed =  System.nanoTime();
				executor.invokeAll(tasks);
				tasks.clear();
				temp1= ed - st;
				temp1 = TimeUnit.SECONDS.convert(temp1, TimeUnit.NANOSECONDS);
				st =  System.nanoTime();
				sum+=count;
				//System.out.println("invoke task:"+sum+"\ttime:"+temp1);
				count = 0;
			}
		
		} catch (InterruptedException e) {
				e.printStackTrace();
		}

	}
		
		
	
    static class ParallelTasks implements Callable<String>{
        
  	  int taskNumber;
  	  String str;
  	  public ParallelTasks(String str){
  		  this.str = str;
  	  }

  	  @Override
  	  public String call() throws Exception{

		DynamoDBOpr.DynamoMain(str);
		return str;
  	  }
    }
    
}//end of class
