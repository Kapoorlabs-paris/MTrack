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
package peakFitter;

import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JProgressBar;

import LineModels.UseLineModel.UserChoiceModel;
import fiji.util.DistanceComparator;
import ij.gui.EllipseRoi;
import labeledObjects.CommonOutput;
import labeledObjects.CommonOutputHF;
import labeledObjects.Indexedlength;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Point;
import net.imglib2.PointSampleList;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPoint;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;
import preProcessing.GetLocalmaxminMT;
import util.Boundingboxes;

public class FitterUtils {

	public static double Distance(final double[] minCorner, final double[] maxCorner) {

		double distance = 0;

		for (int d = 0; d < minCorner.length; ++d) {

			distance += Math.pow((minCorner[d] - maxCorner[d]), 2);

		}
		return Math.sqrt(distance);
	}

	
	static int cutoff = 5;
	public static double[] MakerepeatedLineguess(ArrayList<CommonOutputHF> imgs, Indexedlength iniparam, UserChoiceModel model,  double Intensityratio, double Inispacing, int label, int ndims, 
			int startframe, int currentframe) {

		double[] minVal = new double[ndims];
		double[] maxVal = new double[ndims];

		int labelindex = FitterUtils.getlabelindex(imgs, label);
		
		if (labelindex != -1) {

			RandomAccessibleInterval<FloatType> currentimg = imgs.get(labelindex).Roi;
			FinalInterval interval = imgs.get(labelindex).interval;

			currentimg = Views.interval(currentimg, interval);

			final double maxintensityline = GetLocalmaxminMT.computeMaxIntensity(currentimg);
			final double minintensityline = 0;
			final double axisslope = (iniparam.currentpos[1] - iniparam.fixedpos[1]) / (iniparam.currentpos[0] - iniparam.fixedpos[0]);
			final double axisintercept = iniparam.currentpos[1] - axisslope * iniparam.currentpos[0] ;
			
			
			Pair<double[], double[]> minmaxpair = FitterUtils.MakeinitialEndpointguess(imgs, maxintensityline,
					Intensityratio, ndims, labelindex, axisslope, axisintercept, iniparam.slope, iniparam.intercept,
					iniparam.Curvature, iniparam.Inflection, startframe, currentframe);
			
              if ((int)Math.abs(minmaxpair.getA()[0] - iniparam.currentpos[0]) >= 100 || 
            		  (int)Math.abs(minmaxpair.getA()[1] - iniparam.currentpos[1]) >=100){
				
            	  minmaxpair = FitterUtils.MakeinitialEndpointguessSame(imgs, maxintensityline,
      					Intensityratio, ndims, labelindex, axisslope, axisintercept, iniparam.slope, iniparam.intercept,
      					iniparam.Curvature, iniparam.Inflection, startframe, currentframe);
				
			}
			
			
			for (int d = 0; d < ndims; ++d) {

				minVal[d] = minmaxpair.getA()[d];
				maxVal[d] = minmaxpair.getB()[d];

			}
			
			
			

			if (model == UserChoiceModel.Line) {

				final double[] MinandMax = new double[2 * ndims + 3];

				for (int d = 0; d < ndims; ++d) {

					MinandMax[d] = minVal[d];
					MinandMax[d + ndims] = maxVal[d];
				}

				MinandMax[2 * ndims] = Inispacing;
				MinandMax[2 * ndims + 1] = maxintensityline;
				MinandMax[2 * ndims + 2] = minintensityline;
				for (int d = 0; d < ndims; ++d) {

					if (MinandMax[d] == Double.MAX_VALUE || MinandMax[d + ndims] == -Double.MIN_VALUE)
						return null;
					if (MinandMax[d] >= currentimg.dimension(d) || MinandMax[d + ndims] >= currentimg.dimension(d))
						return null;
					if (MinandMax[d] <= 0 || MinandMax[d + ndims] <= 0)
						return null;

				}
				return MinandMax;
			}

			if (model == UserChoiceModel.Splineordersec) {

				final double[] MinandMax = new double[2 * ndims + 4];

				for (int d = 0; d < ndims; ++d) {

					MinandMax[d] = minVal[d];
					MinandMax[d + ndims] = maxVal[d];
				}

				MinandMax[2 * ndims + 2] = maxintensityline;
				MinandMax[2 * ndims + 3] = minintensityline;
				MinandMax[2 * ndims + 1] = iniparam.Curvature;
				MinandMax[2 * ndims] = Inispacing;

				for (int d = 0; d < ndims; ++d) {

					if (MinandMax[d] == Double.MAX_VALUE || MinandMax[d + ndims] == -Double.MIN_VALUE)
						return null;
					if (MinandMax[d] >= currentimg.dimension(d) || MinandMax[d + ndims] >= currentimg.dimension(d))
						return null;
					if (MinandMax[d] <= 0 || MinandMax[d + ndims] <= 0)
						return null;

				}
				return MinandMax;
			}
			if (model == UserChoiceModel.Splineorderthird) {

				final double[] MinandMax = new double[2 * ndims + 5];

				for (int d = 0; d < ndims; ++d) {

					MinandMax[d] = minVal[d];
					MinandMax[d + ndims] = maxVal[d];
				}

				MinandMax[2 * ndims + 2] = iniparam.Inflection;
				MinandMax[2 * ndims + 3] = maxintensityline;
				MinandMax[2 * ndims + 4] = minintensityline;
				MinandMax[2 * ndims + 1] = iniparam.Curvature;
				MinandMax[2 * ndims] = Inispacing;

				for (int d = 0; d < ndims; ++d) {

					if (MinandMax[d] == Double.MAX_VALUE || MinandMax[d + ndims] == -Double.MIN_VALUE)
						return null;
					
					if (MinandMax[d] <= 0 || MinandMax[d + ndims] <= 0)
						return null;

				}
				return MinandMax;
			}

			else
				return null;
		}

		else
			return null;
	}
	
	public static double[] MakeimprovedLineguess(ArrayList<CommonOutput> imgs, double slope, double intercept, double zeroslope, double zerointercept, double Curvature, double Inflection, double Intensityratio, double Inispacing,
			double[] psf, int label, int startframe, int currentframe)  {
		
		
		int ndims = psf.length;
		long[] newposition = new long[ndims];
		double[] minVal = { Double.MAX_VALUE, Double.MAX_VALUE };
		double[] maxVal = { -Double.MIN_VALUE, -Double.MIN_VALUE };

		RandomAccessibleInterval<FloatType> currentimg = imgs.get(label).Actualroi;

		FinalInterval interval = imgs.get(label).interval;
		
		currentimg = Views.interval(currentimg, interval);

		final Cursor<FloatType> inputcursor = Views.iterable(currentimg).localizingCursor();

		final double maxintensityline = GetLocalmaxminMT.computeMaxIntensity(currentimg);

           

           
          
        	   
        	   while(inputcursor.hasNext()){
       			
       			inputcursor.fwd();
       			inputcursor.localize(newposition);
       			double distline = 0;
       		
       			if (currentframe <= startframe + 1 || Math.abs(slope)==Double.NaN )
       				distline = Math.abs(inputcursor.getDoublePosition(1) - zeroslope * inputcursor.getDoublePosition(0) - zerointercept) / Math.sqrt(1 + zeroslope * zeroslope);
       			
       			else if (currentframe > startframe + 1  && Math.abs(slope)!=Double.NaN)
       				distline =	Math.abs(inputcursor.getDoublePosition(1) - slope * inputcursor.getDoublePosition(0) - intercept) / Math.sqrt(1 + slope * slope);
       			

       			
                if (distline < cutoff && Math.abs(distline)!=Double.NaN){			
				if (inputcursor.getDoublePosition(0) <= minVal[0]
						&& inputcursor.get().get() / maxintensityline > Intensityratio) {
					minVal[0] = inputcursor.getDoublePosition(0);
					minVal[1] = inputcursor.getDoublePosition(1);
				}

				if (inputcursor.getDoublePosition(0) >= maxVal[0]
						&& inputcursor.get().get() / maxintensityline > Intensityratio) {
					maxVal[0] = inputcursor.getDoublePosition(0);
					maxVal[1] = inputcursor.getDoublePosition(1);

				}

                }
                
                else{
                	
                	if (inputcursor.getDoublePosition(0) <= minVal[0]
    						&& inputcursor.get().get() / maxintensityline > Intensityratio) {
    					minVal[0] = inputcursor.getDoublePosition(0);
    					minVal[1] = inputcursor.getDoublePosition(1);
    				}

    				if (inputcursor.getDoublePosition(0) >= maxVal[0]
    						&& inputcursor.get().get() / maxintensityline > Intensityratio) {
    					maxVal[0] = inputcursor.getDoublePosition(0);
    					maxVal[1] = inputcursor.getDoublePosition(1);

    				}
                	
                }
       			
       			
       			
        	   }
		final double[] MinandMax = new double[2 * ndims + 3];

		
			for (int d = 0; d < ndims; ++d) {

				MinandMax[d] = minVal[d];
				MinandMax[d + ndims] = maxVal[d];
			}

		

		// This parameter is guess estimate for spacing between the Gaussians
		MinandMax[2 * ndims] =   Inispacing;
		MinandMax[2 * ndims + 1] = maxintensityline; 
		// This parameter guess estimates the background noise level
		MinandMax[2 * ndims + 2] = 0.0; 
		
		
		System.out.println("Label: " + label + " " + "Detection: " + " StartX: " + MinandMax[0] + " StartY: "
				+ MinandMax[1] + " EndX: " + MinandMax[2] + " EndY: " + MinandMax[3]);

		
		
			for (int d = 0; d < ndims; ++d) {

				if (MinandMax[d] == Double.MAX_VALUE || MinandMax[d + ndims] == -Double.MIN_VALUE)
					return null;
				
				if (MinandMax[d] <= 0 || MinandMax[d + ndims] <= 0)
					return null;

			}
		

		return MinandMax;
           }
       
	
	public static double[] MakeimprovedLineguessSame(ArrayList<CommonOutput> imgs, double slope, double intercept, double zeroslope, double zerointercept, double Curvature, double Inflection, double Intensityratio, double Inispacing,
			double[] psf, int label, int startframe, int currentframe)  {
		
		
		int ndims = psf.length;
		long[] newposition = new long[ndims];
		double[] minVal = { Double.MAX_VALUE, Double.MAX_VALUE };
		double[] maxVal = { -Double.MIN_VALUE, -Double.MIN_VALUE };

		RandomAccessibleInterval<FloatType> currentimg = imgs.get(label).Actualroi;

		FinalInterval interval = imgs.get(label).interval;
		
		currentimg = Views.interval(currentimg, interval);

		final Cursor<FloatType> inputcursor = Views.iterable(currentimg).localizingCursor();

		final double maxintensityline = GetLocalmaxminMT.computeMaxIntensity(currentimg);

           

           
          
        	   
        	   while(inputcursor.hasNext()){
       			
       			inputcursor.fwd();
       			inputcursor.localize(newposition);
       			double distline = 0;
       		
       			if (currentframe <= startframe + 1 || Math.abs(slope)==Double.NaN )
       				distline = Math.abs(inputcursor.getDoublePosition(1) - zeroslope * inputcursor.getDoublePosition(0) - zerointercept) / Math.sqrt(1 + zeroslope * zeroslope);
       			
       			else if (currentframe > startframe + 1  && Math.abs(slope)!=Double.NaN)
       				distline =	Math.abs(inputcursor.getDoublePosition(1) - slope * inputcursor.getDoublePosition(0) - intercept) / Math.sqrt(1 + slope * slope);
       			

       			
                	
                	if (inputcursor.getDoublePosition(0) <= minVal[0]
    						&& inputcursor.get().get() / maxintensityline > Intensityratio) {
    					minVal[0] = inputcursor.getDoublePosition(0);
    					minVal[1] = inputcursor.getDoublePosition(1);
    				}

    				if (inputcursor.getDoublePosition(0) >= maxVal[0]
    						&& inputcursor.get().get() / maxintensityline > Intensityratio) {
    					maxVal[0] = inputcursor.getDoublePosition(0);
    					maxVal[1] = inputcursor.getDoublePosition(1);

    				}
                	
                
       			
       			
       			
        	   }
		final double[] MinandMax = new double[2 * ndims + 3];

		
			for (int d = 0; d < ndims; ++d) {

				MinandMax[d] = minVal[d];
				MinandMax[d + ndims] = maxVal[d];
			}

		

		// This parameter is guess estimate for spacing between the Gaussians
		MinandMax[2 * ndims] =   Inispacing;
		MinandMax[2 * ndims + 1] = maxintensityline; 
		// This parameter guess estimates the background noise level
		MinandMax[2 * ndims + 2] = 0.0; 
		
		
		System.out.println("Label: " + label + " " + "Detection: " + " StartX: " + MinandMax[0] + " StartY: "
				+ MinandMax[1] + " EndX: " + MinandMax[2] + " EndY: " + MinandMax[3]);

		
		
			for (int d = 0; d < ndims; ++d) {

				if (MinandMax[d] == Double.MAX_VALUE || MinandMax[d + ndims] == -Double.MIN_VALUE)
					return null;
				
				if (MinandMax[d] <= 0 || MinandMax[d + ndims] <= 0)
					return null;

			}
		

		return MinandMax;
           }
	
	public static ArrayList<Integer> Getlabel(final ArrayList<CommonOutputHF> imgs, final Point fixedpoint,
			final double originalslope, final double originalintercept) {

		int finallabel = Integer.MIN_VALUE;
		ArrayList<Integer> alllabels = new ArrayList<Integer>();


		int index = 0;
			if (imgs.get(index).intimg!= null){
				
				RandomAccess<IntType> intranac = imgs.get(index).intimg.randomAccess();

				intranac.setPosition(fixedpoint);
				finallabel = intranac.get().get();
				alllabels.add(finallabel);
			
			}
			
			
			else{
				
			for (int indexx = 0; indexx < imgs.size(); ++indexx){	
			RandomAccessibleInterval<FloatType> currentimg = imgs.get(indexx).Actualroi;
			FinalInterval interval = imgs.get(indexx).interval;
			currentimg = Views.interval(currentimg, interval);

		
			

		

			if (fixedpoint.getIntPosition(0) >= interval.min(0) && fixedpoint.getIntPosition(0) <= interval.max(0)
					&& fixedpoint.getIntPosition(1) >= interval.min(1)
					&& fixedpoint.getIntPosition(1) <= interval.max(1)) {
				
				
				for (int i = 0; i < imgs.get(indexx).Allrois.size(); ++i) {

					EllipseRoi roi = imgs.get(indexx).Allrois.get(i);

					java.awt.Point[] Allpoints = roi.getContainedPoints();
					
					for (int pointindex = 0; pointindex < Allpoints.length; ++pointindex){
						
						if (fixedpoint.getIntPosition(0) == Allpoints[pointindex].x && fixedpoint.getIntPosition(1) == Allpoints[pointindex].y ){
							
							finallabel = imgs.get(indexx).roilabel;

							alllabels.add(finallabel);
							
						}
						
					}
					

				}
			}
			}

		}
		
		

		return alllabels;
		
	}

	public static int getlabelindex(final ArrayList<CommonOutputHF> imgs, int label) {

		
		
		
		int labelindex = - 1;
		for (int index = 0; index < imgs.size(); ++index) {

			if (imgs.get(index).roilabel == label) {

				labelindex = index;

			}
		
		}

		return labelindex;

	}

	public static int getlabelindexSeed(final ArrayList<CommonOutput> imgs, int label) {

		int labelindex = - 1;
		for (int index = 0; index < imgs.size(); ++index) {

			
			
			if (imgs.get(index).roilabel == label) {

				labelindex = index;

			}
		}

		return labelindex;

	}

	public static PointSampleList<FloatType> gatherfullDataSeed(final ArrayList<CommonOutput> imgs, final int label,
			final int ndims) {
		final PointSampleList<FloatType> datalist = new PointSampleList<FloatType>(ndims);

		int labelindex = getlabelindexSeed(imgs, label);

		RandomAccessibleInterval<FloatType> currentimg = imgs.get(labelindex).Actualroi;

		FinalInterval interval = imgs.get(labelindex).interval;

		currentimg = Views.interval(currentimg, interval);

		Cursor<FloatType> localcursor = Views.iterable(currentimg).localizingCursor();

		while (localcursor.hasNext()) {

			localcursor.fwd();

			if (localcursor.get().get() > 0) {
				Point newpoint = new Point(localcursor);
				datalist.add(newpoint, localcursor.get().copy());
			}
		}

		return datalist;
	}

	public static PointSampleList<FloatType> gatherfullData(final ArrayList<CommonOutputHF> imgs, final int label,
			final int ndims) {
		final PointSampleList<FloatType> datalist = new PointSampleList<FloatType>(ndims);

		int labelindex = getlabelindex(imgs, label);

		RandomAccessibleInterval<FloatType> currentimg = imgs.get(labelindex).Actualroi;

		FinalInterval interval = imgs.get(labelindex).interval;

		currentimg = Views.interval(currentimg, interval);

		Cursor<FloatType> localcursor = Views.iterable(currentimg).localizingCursor();

		while (localcursor.hasNext()) {

			localcursor.fwd();

			if (localcursor.get().get() > 0) {
				Point newpoint = new Point(localcursor);
				datalist.add(newpoint, localcursor.get().copy());
			}
		}

		return datalist;
	}

	public static final Pair<double[], double[]> MakeinitialEndpointguess(ArrayList<CommonOutputHF> imgs,
			double maxintensityline, double Intensityratio, int ndims, int label, double slope, double intercept, double zeroslope, double zerointercept,
			double Curvature, double Inflection, final int startframe, final int currentframe) {
		long[] newposition = new long[ndims];
		double[] minVal = { Double.MAX_VALUE, Double.MAX_VALUE };
		double[] maxVal = { -Double.MIN_VALUE, -Double.MIN_VALUE };

		RandomAccessibleInterval<FloatType> currentimg = imgs.get(label).Roi;

		
		FinalInterval interval = imgs.get(label).interval;

		currentimg = Views.interval(currentimg, interval);

		final Cursor<FloatType> outcursor = Views.iterable(currentimg).localizingCursor();

		while (outcursor.hasNext()) {

			outcursor.fwd();

			outcursor.localize(newposition);
			double distline = 0;
			
   			if (currentframe <= startframe + 1 || Math.abs(slope) == Double.NaN)
   				distline = Math.abs(outcursor.getDoublePosition(1) - zeroslope * outcursor.getDoublePosition(0) - zerointercept) / Math.sqrt(1 + zeroslope * zeroslope);
   			
   			else if (currentframe > startframe + 1 && Math.abs(slope)!=Double.NaN)
   				distline =	Math.abs(outcursor.getDoublePosition(1) - slope * outcursor.getDoublePosition(0) - intercept) / Math.sqrt(1 + slope * slope);
   					
   			
   			
                if (distline < cutoff && Math.abs(distline)!=Double.NaN){			
				if (outcursor.getDoublePosition(0) <= minVal[0]
						&& outcursor.get().get() / maxintensityline > Intensityratio) {
					minVal[0] = outcursor.getDoublePosition(0);
					minVal[1] = outcursor.getDoublePosition(1);
				}

				if (outcursor.getDoublePosition(0) >= maxVal[0]
						&& outcursor.get().get() / maxintensityline > Intensityratio) {
					maxVal[0] = outcursor.getDoublePosition(0);
					maxVal[1] = outcursor.getDoublePosition(1);

				}

                }
                
                else{
                	
                	if (outcursor.getDoublePosition(0) <= minVal[0]
    						&& outcursor.get().get() / maxintensityline > Intensityratio) {
    					minVal[0] = outcursor.getDoublePosition(0);
    					minVal[1] = outcursor.getDoublePosition(1);
    				}

    				if (outcursor.getDoublePosition(0) >= maxVal[0]
    						&& outcursor.get().get() / maxintensityline > Intensityratio) {
    					maxVal[0] = outcursor.getDoublePosition(0);
    					maxVal[1] = outcursor.getDoublePosition(1);

    				}
                	
                }
                
			
		
		
		}
		Pair<double[], double[]> minmaxpair = new ValuePair<double[], double[]>(minVal, maxVal);

		
		
		return minmaxpair;

	}
	
	
	
	public static final Pair<double[], double[]> MakeinitialEndpointguessSame(ArrayList<CommonOutputHF> imgs,
			double maxintensityline, double Intensityratio, int ndims, int label, double slope, double intercept, double zeroslope, double zerointercept,
			double Curvature, double Inflection, final int startframe, final int currentframe) {
		long[] newposition = new long[ndims];
		double[] minVal = { Double.MAX_VALUE, Double.MAX_VALUE };
		double[] maxVal = { -Double.MIN_VALUE, -Double.MIN_VALUE };

		RandomAccessibleInterval<FloatType> currentimg = imgs.get(label).Roi;

		
		FinalInterval interval = imgs.get(label).interval;

		currentimg = Views.interval(currentimg, interval);

		final Cursor<FloatType> outcursor = Views.iterable(currentimg).localizingCursor();

		while (outcursor.hasNext()) {

			outcursor.fwd();

			outcursor.localize(newposition);
			double distline = 0;
			
   			if (currentframe <= startframe + 1 || Math.abs(slope) == Double.NaN)
   				distline = Math.abs(outcursor.getDoublePosition(1) - zeroslope * outcursor.getDoublePosition(0) - zerointercept) / Math.sqrt(1 + zeroslope * zeroslope);
   			
   			else if (currentframe > startframe + 1 && Math.abs(slope)!=Double.NaN)
   				distline =	Math.abs(outcursor.getDoublePosition(1) - slope * outcursor.getDoublePosition(0) - intercept) / Math.sqrt(1 + slope * slope);
   					
   			
   			
               
                	
                	if (outcursor.getDoublePosition(0) <= minVal[0]
    						&& outcursor.get().get() / maxintensityline > Intensityratio) {
    					minVal[0] = outcursor.getDoublePosition(0);
    					minVal[1] = outcursor.getDoublePosition(1);
    				}

    				if (outcursor.getDoublePosition(0) >= maxVal[0]
    						&& outcursor.get().get() / maxintensityline > Intensityratio) {
    					maxVal[0] = outcursor.getDoublePosition(0);
    					maxVal[1] = outcursor.getDoublePosition(1);

    				}
                	
                }
                
			
		
		
		
		Pair<double[], double[]> minmaxpair = new ValuePair<double[], double[]>(minVal, maxVal);

		
		
		return minmaxpair;

	}

	

	public static final Pair<double[], double[]> MakeinitialEndpointguessUser(ArrayList<CommonOutputHF> imgs,
			double maxintensityline, double Intensityratio, int ndims, int label, double slope, double intercept,
			double Curvature, double Inflection, int startframe, int framenumber) {
		long[] newposition = new long[ndims];
		double[] minVal = { Double.MAX_VALUE, Double.MAX_VALUE };
		double[] maxVal = { -Double.MIN_VALUE, -Double.MIN_VALUE };

		RandomAccessibleInterval<FloatType> currentimg = imgs.get(label).Roi;

		FinalInterval interval = imgs.get(label).interval;

		currentimg = Views.interval(currentimg, interval);

		final Cursor<FloatType> outcursor = Views.iterable(currentimg).localizingCursor();

		while (outcursor.hasNext()) {

			outcursor.fwd();

			outcursor.localize(newposition);

			double distline = Math.abs(outcursor.getDoublePosition(1) - slope * outcursor.getDoublePosition(0) - intercept) / Math.sqrt(1 + slope * slope);
			
			
			
			if (Math.abs(slope)!=Double.NaN ){
            if (distline < cutoff){	
			
					if (outcursor.getDoublePosition(0) <= minVal[0]
							&& outcursor.get().get() / maxintensityline > Intensityratio) {
						minVal[0] = outcursor.getDoublePosition(0);
						minVal[1] = outcursor.getDoublePosition(1);
					}

					if (outcursor.getDoublePosition(0) >= maxVal[0]
							&& outcursor.get().get() / maxintensityline > Intensityratio) {
						maxVal[0] = outcursor.getDoublePosition(0);
						maxVal[1] = outcursor.getDoublePosition(1);

					}
					
            }
            
            if (minVal[0] == Double.MAX_VALUE)
			{
				if (outcursor.getDoublePosition(0) <= minVal[0]
						&& outcursor.get().get() / maxintensityline > Intensityratio) {
					minVal[0] = outcursor.getDoublePosition(0);
					minVal[1] = outcursor.getDoublePosition(1);
				}

				if (outcursor.getDoublePosition(0) >= maxVal[0]
						&& outcursor.get().get() / maxintensityline > Intensityratio) {
					maxVal[0] = outcursor.getDoublePosition(0);
					maxVal[1] = outcursor.getDoublePosition(1);

				}
				
			}

            
            
            
            }
			else {
				
				 
						if (outcursor.getDoublePosition(0) <= minVal[0]
								&& outcursor.get().get() / maxintensityline > Intensityratio) {
							minVal[0] = outcursor.getDoublePosition(0);
							minVal[1] = outcursor.getDoublePosition(1);
						}

						if (outcursor.getDoublePosition(0) >= maxVal[0]
								&& outcursor.get().get() / maxintensityline > Intensityratio) {
							maxVal[0] = outcursor.getDoublePosition(0);
							maxVal[1] = outcursor.getDoublePosition(1);

						}
						
						System.out.println("here");
					}
				
			
			
			
				
		}
		
		
		
		System.out.println(minVal[0]);
		
		Pair<double[], double[]> minmaxpair = new ValuePair<double[], double[]>(minVal, maxVal);

		return minmaxpair;

	}



	public static final Pair<double[], double[]> MakeinitialEndpointguessUserSame(ArrayList<CommonOutputHF> imgs,
			double maxintensityline, double Intensityratio, int ndims, int label, double slope, double intercept,
			double Curvature, double Inflection, int startframe, int framenumber) {
		long[] newposition = new long[ndims];
		double[] minVal = { Double.MAX_VALUE, Double.MAX_VALUE };
		double[] maxVal = { -Double.MIN_VALUE, -Double.MIN_VALUE };

		RandomAccessibleInterval<FloatType> currentimg = imgs.get(label).Roi;

		FinalInterval interval = imgs.get(label).interval;

		currentimg = Views.interval(currentimg, interval);

		final Cursor<FloatType> outcursor = Views.iterable(currentimg).localizingCursor();

		while (outcursor.hasNext()) {

			outcursor.fwd();

			outcursor.localize(newposition);

			double distline = Math.abs(outcursor.getDoublePosition(1) - slope * outcursor.getDoublePosition(0) - intercept) / Math.sqrt(1 + slope * slope);
			
			
				if (outcursor.getDoublePosition(0) <= minVal[0]
						&& outcursor.get().get() / maxintensityline > Intensityratio) {
					minVal[0] = outcursor.getDoublePosition(0);
					minVal[1] = outcursor.getDoublePosition(1);
				}

				if (outcursor.getDoublePosition(0) >= maxVal[0]
						&& outcursor.get().get() / maxintensityline > Intensityratio) {
					maxVal[0] = outcursor.getDoublePosition(0);
					maxVal[1] = outcursor.getDoublePosition(1);

				}
				
			

				
		}
		
		
		
		
		
		Pair<double[], double[]> minmaxpair = new ValuePair<double[], double[]>(minVal, maxVal);

		return minmaxpair;

	}
	
	public static void SetProgressBar(JProgressBar jpb, double percent) {

		jpb.setValue((int) Math.round(percent));
		jpb.setOpaque(true);
		jpb.setStringPainted(true);
		jpb.setString("Finding MT ends");

	}
	
	
	public static void SetProgressBar(JProgressBar jpb, double percent, String message) {

		jpb.setValue((int) Math.round(percent));
		jpb.setOpaque(true);
		jpb.setStringPainted(true);
		jpb.setString(message);

	}

	public static void SetProgressBar(JProgressBar jpb) {
		jpb.setValue(0);
		jpb.setIndeterminate(true);
		jpb.setOpaque(true);
		jpb.setStringPainted(true);
		jpb.setString("Pre-processing Image");

	}

	public static void SetProgressBarTime(JProgressBar jpb, double percent, int framenumber, int thirdDimsize) {

		jpb.setValue((int) percent);
		jpb.setOpaque(true);
		jpb.setStringPainted(true);
		jpb.setString("Time point = " + framenumber + "/" + thirdDimsize);

	}

	public static void SetProgressBarTime(JProgressBar jpb, double percent, int framenumber, int thirdDimsize,
			String message) {

		jpb.setValue((int) percent);
		jpb.setOpaque(true);
		jpb.setStringPainted(true);
		jpb.setString(message + "= " + framenumber + "/" + thirdDimsize);

	}

}
