package parallelization;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import interactiveMT.BatchMode;
import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTSingleChannel;
import interactiveMT.SingleBatchMode;

public class ProcessFiles  {

	public static void process(File[] directory, ExecutorService taskexecutor) {
		 List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
		 
		for (int fileindex = 0; fileindex < directory.length; ++fileindex) {
			
			BatchMode parent = new BatchMode(directory, new Interactive_MTDoubleChannel(), directory[0]);
			 tasks.add(Executors.callable(new Split(directory[fileindex], parent, fileindex)));
			
			
			
			
		}
		try {
			taskexecutor.invokeAll(tasks);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		taskexecutor.shutdown();
		
		
		
		try {
			taskexecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		}
	
	
	public static void processSingle(File[] directory, ExecutorService taskexecutor) {
		 List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
		for (int fileindex = 0; fileindex < directory.length; ++fileindex) {
		
		
			SingleBatchMode parent = new SingleBatchMode(directory, new Interactive_MTSingleChannel(), directory[0]);
			 tasks.add(Executors.callable(new SplitSingleChannel(directory[fileindex], parent, fileindex)));
		
		
		}
		
		
		try {
			taskexecutor.invokeAll(tasks);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		taskexecutor.shutdown();
		
		
		try {
			taskexecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		}
	

	

}
