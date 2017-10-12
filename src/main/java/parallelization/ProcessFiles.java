/*-
 * #%L
 * Microtubule tracker.
 * %%
 * Copyright (C) 2017 MTrack developers.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
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
		
		
		
		
		
		

		}
	

	

}
