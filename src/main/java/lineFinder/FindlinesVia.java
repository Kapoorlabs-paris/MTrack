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
package lineFinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JProgressBar;


import LineModels.UseLineModel.UserChoiceModel;
import graphconstructs.Trackproperties;
import interactiveMT.Interactive_MTDoubleChannel.WhichendDouble;
import labeledObjects.Indexedlength;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import peakFitter.ParallelSubpixelVelocityPCLineStart;
import peakFitter.ParallelSubpixelVelocityUserSeed;
import peakFitter.SubpixelLengthPCLine;
import peakFitter.SubpixelVelocityPCLine;
import peakFitter.SubpixelVelocityUserSeed;

public  class FindlinesVia {

	public static  enum LinefindingMethod {

		MSER, Hough, MSERwHough;

	}
	
	public static int Accountedframes ;
	
      protected LinefindingMethod MSER, Hough, MSERwHough;

	public static Pair<ArrayList<Indexedlength>,ArrayList<Indexedlength>> LinefindingMethod(final RandomAccessibleInterval<FloatType> source,
			final RandomAccessibleInterval<FloatType> Preprocessedsource, 
			final int framenumber, final double[] psf, final Linefinder linefinder, final UserChoiceModel model, 
			final boolean DoMask, final double Intensityratio, final double Inispacing, JProgressBar jpb ) {

		
			
		

			
			SubpixelLengthPCLine MTline = new SubpixelLengthPCLine(source, linefinder, psf, model, framenumber, DoMask, jpb);
			MTline.setIntensityratio(Intensityratio);
			
			MTline.setInispacing(Inispacing);
			
			MTline.checkInput();
			MTline.process();
			Pair<ArrayList<Indexedlength>,ArrayList<Indexedlength>>	PrevFrameparam = MTline.getResult();
			
		
		return PrevFrameparam;

	}
	
	

	public static Pair<Pair<ArrayList<Trackproperties>, ArrayList<Trackproperties>>, Pair<ArrayList<Indexedlength>, ArrayList<Indexedlength>>> 
	LinefindingMethodHF(final RandomAccessibleInterval<FloatType> source,
			final RandomAccessibleInterval<FloatType> Preprocessedsource,Pair<ArrayList<Indexedlength>,ArrayList<Indexedlength>> PrevFrameparam,
			 final int framenumber, final double[] psf,  final LinefinderHF linefinder, final UserChoiceModel model,
			final boolean DoMask, final double intensityratio, final double Inispacing, final HashMap<Integer, WhichendDouble> Trackstart, final JProgressBar jpb,
			final int thirdDimsize, final double maxdist, final int startframe, final int numgaussians) {

	
		
	
			int nThreads = Runtime.getRuntime().availableProcessors();
			// set up executor service
			final ExecutorService taskExecutorStart = Executors.newFixedThreadPool(nThreads);
			final ExecutorService taskExecutorEnd = Executors.newFixedThreadPool(nThreads);
			 ArrayList<Indexedlength> NewFrameparamStart = new ArrayList<Indexedlength>();
			 ArrayList<Trackproperties> startStateVectors = new ArrayList<Trackproperties>();
			
			 
			 ArrayList<Indexedlength> NewFrameparamEnd = new ArrayList<Indexedlength>();
			 ArrayList<Trackproperties> endStateVectors = new ArrayList<Trackproperties>();
			 
			 
			 List<Callable<Object>> tasksStart = new ArrayList<Callable<Object>>();
			 List<Callable<Object>> tasksEnd = new ArrayList<Callable<Object>>();
			 
			 if(PrevFrameparam.getA().size() > 0)
			for(int index = 0; index < PrevFrameparam.getA().size(); ++index) {
			final ParallelSubpixelVelocityPCLineStart ParallelgrowthtrackerStart = new ParallelSubpixelVelocityPCLineStart(source, linefinder,
					PrevFrameparam.getA(), index, psf, framenumber, model, DoMask, Trackstart,jpb, thirdDimsize, startframe, numgaussians);
			ParallelgrowthtrackerStart.setIntensityratio(intensityratio);
			ParallelgrowthtrackerStart.setInispacing(Inispacing);
			ParallelgrowthtrackerStart.setMaxdist(maxdist);
			ParallelgrowthtrackerStart.checkInput();
			tasksStart.add(Executors.callable(ParallelgrowthtrackerStart));
			if(tasksStart.size() > 0)
			try {
				taskExecutorStart.invokeAll(tasksStart);
			} catch (InterruptedException e1) {

			}
			
		   Accountedframes  = ParallelgrowthtrackerStart.getAccountedframes() ;
		   
		   NewFrameparamStart.addAll( ParallelgrowthtrackerStart.getResult());
		   startStateVectors.addAll(ParallelgrowthtrackerStart.getstartStateVectors());
			tasksStart.clear();
			
			}
			 taskExecutorStart.shutdown();
			 if(PrevFrameparam.getB().size() > 0)
			for(int index = 0; index < PrevFrameparam.getB().size(); ++index) {
				final ParallelSubpixelVelocityPCLineStart ParallelgrowthtrackerEnd = new ParallelSubpixelVelocityPCLineStart(source, linefinder,
						PrevFrameparam.getB(), index, psf, framenumber, model, DoMask, Trackstart,jpb, thirdDimsize, startframe, numgaussians);
				ParallelgrowthtrackerEnd.setIntensityratio(intensityratio);
				ParallelgrowthtrackerEnd.setInispacing(Inispacing);
				ParallelgrowthtrackerEnd.setMaxdist(maxdist);
				ParallelgrowthtrackerEnd.checkInput();
				
				tasksEnd.add(Executors.callable(ParallelgrowthtrackerEnd));
				if(tasksEnd.size() > 0)
				try {
					taskExecutorEnd.invokeAll(tasksEnd);
				} catch (InterruptedException e1) {

				}
			   Accountedframes  = ParallelgrowthtrackerEnd.getAccountedframes() ;
				
			   NewFrameparamEnd.addAll(ParallelgrowthtrackerEnd.getResult());
			   endStateVectors.addAll(ParallelgrowthtrackerEnd.getstartStateVectors());
			   tasksEnd.clear();
		
				}
			   taskExecutorEnd.shutdown();
			
			Pair<ArrayList<Indexedlength>,ArrayList<Indexedlength>> NewFrameparam = new ValuePair<ArrayList<Indexedlength>,ArrayList<Indexedlength>>(NewFrameparamStart,NewFrameparamEnd );
		
			Pair<ArrayList<Trackproperties>, ArrayList<Trackproperties>> Statevectors = new ValuePair<ArrayList<Trackproperties>, ArrayList<Trackproperties>>(startStateVectors, endStateVectors); 
			Pair<Pair<ArrayList<Trackproperties>, ArrayList<Trackproperties>>,Pair<ArrayList<Indexedlength>,ArrayList<Indexedlength>>> 	returnVector = 
					new ValuePair<Pair<ArrayList<Trackproperties>, ArrayList<Trackproperties>>,Pair<ArrayList<Indexedlength>,ArrayList<Indexedlength>>>(Statevectors, NewFrameparam);
			
			
			
			
		
			
		
		
		
		return returnVector;

	}
	
	public static Pair<ArrayList<Trackproperties>, ArrayList<Indexedlength>> 
	LinefindingMethodHFUser(final RandomAccessibleInterval<FloatType> source,
			final RandomAccessibleInterval<FloatType> Preprocessedsource,ArrayList<Indexedlength> PrevFrameparam,
			 final int framenumber, final double[] psf,  final LinefinderHF linefinder, final UserChoiceModel model,
			final boolean DoMask, final double intensityratio, final double Inispacing, final JProgressBar jpb,
			final int thirdDimsize, final double maxdist, final int startframe) {


		int nThreads = Runtime.getRuntime().availableProcessors();
		// set up executor service
		final ExecutorService taskExecutorStart = Executors.newFixedThreadPool(nThreads);

		
		ArrayList<Indexedlength> NewFrameparamStart = new ArrayList<Indexedlength>();
		 ArrayList<Trackproperties> startStateVectors = new ArrayList<Trackproperties>();
		
		 
		 
		 
		 List<Callable<Object>> tasksStart = new ArrayList<Callable<Object>>();
		 
		 if(PrevFrameparam.size() > 0)
		for(int index = 0; index < PrevFrameparam.size(); ++index) {
                     
			final ParallelSubpixelVelocityUserSeed ParallelgrowthtrackerStart = 
					new ParallelSubpixelVelocityUserSeed(source, linefinder, PrevFrameparam, index, psf, framenumber, model, DoMask, jpb, thirdDimsize, startframe);
			
			ParallelgrowthtrackerStart.setIntensityratio(intensityratio);
			ParallelgrowthtrackerStart.setInispacing(Inispacing);
			ParallelgrowthtrackerStart.setMaxdist(maxdist);
			ParallelgrowthtrackerStart.checkInput();
			tasksStart.add(Executors.callable(ParallelgrowthtrackerStart));
			if(tasksStart.size() > 0)
			try {
				taskExecutorStart.invokeAll(tasksStart);
			} catch (InterruptedException e1) {

			}
			
		   Accountedframes  = ParallelgrowthtrackerStart.getAccountedframes() ;
		   
		   NewFrameparamStart.addAll( ParallelgrowthtrackerStart.getResult());
		   startStateVectors.addAll(ParallelgrowthtrackerStart.getstartStateVectors());
		   tasksStart.clear();
		  
		}
		 taskExecutorStart.shutdown();

		
			
	
			Pair<ArrayList<Trackproperties>,ArrayList<Indexedlength>>	returnVector = 
					new ValuePair<ArrayList<Trackproperties>,ArrayList<Indexedlength>>(startStateVectors, NewFrameparamStart);
			
			
			
			
		
			
		
		
		
		return returnVector;

	}
	
	
	
	public static int getAccountedframes(){
		
		return Accountedframes;
	}
	
	
}
