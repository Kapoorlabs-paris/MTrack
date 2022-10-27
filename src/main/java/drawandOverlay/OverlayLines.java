/*-
 * #%L
 * Microtubule tracker.
 * %%
 * Copyright (C) 2017 - 2022 MTrack developers.
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
package drawandOverlay;

import java.util.ArrayList;

import houghandWatershed.TransformCordinates;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.EllipseRoi;
import labeledObjects.CommonOutput;
import labeledObjects.LabelledImg;
import labeledObjects.Lineobjects;
import labeledObjects.Simpleobject;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.localextrema.RefinedPeak;
import net.imglib2.algorithm.stats.Histogram;
import net.imglib2.algorithm.stats.IntBinMapper;
import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;
import preProcessing.GetLocalmaxminMT;
import preProcessing.GetLocalmaxminMT.IntensityType;
import util.Boundingboxes;

public class OverlayLines {
	


	

	public static ArrayList<RefinedPeak<Point>> ReducedList(RandomAccessibleInterval<FloatType> inputimg,
			ArrayList<RefinedPeak<Point>> SubpixelMinlist, double[] sizes, double[] min, double[] max) {

		RandomAccessibleInterval<FloatType> imgout = new ArrayImgFactory<FloatType>().create(inputimg, new FloatType());
		double[] points = new double[imgout.numDimensions()];
		int maxcount = 0;
		int maxindex = 0;
		
		ArrayList<RefinedPeak<Point>> MainMinlist = new ArrayList<RefinedPeak<Point>>(inputimg.numDimensions());
		for (int index = 0; index < SubpixelMinlist.size(); ++index) {
			points = TransformCordinates.transformfwd(new double[] { SubpixelMinlist.get(index).getDoublePosition(0),
					SubpixelMinlist.get(index).getDoublePosition(1) }, sizes, min, max);

			double slope = -1.0 / Math.tan(Math.toRadians(points[0]));
			double intercept = points[1] / Math.sin(Math.toRadians(points[0]));
			
			PushCurves.Drawexactline(imgout, slope, intercept, IntensityType.Gaussian);

			RandomAccess<FloatType> inran = inputimg.randomAccess();
			Cursor<FloatType> outcursor = Views.iterable(imgout).localizingCursor();
			

			int count = 0;
			while (outcursor.hasNext()) {
				outcursor.fwd();

				if (outcursor.get().get() > 0) {
					inran.setPosition(outcursor);

					if (inran.get().get() > 0)
						count++;

					
					if (count > maxcount) {
						maxcount = count;
						maxindex = index;

					}

				}

			}
		}
		
		
		if (maxcount > 0) {
			MainMinlist.add(SubpixelMinlist.get(maxindex));
			
		}
		
		
		
		
		return MainMinlist;
	}
	
	public static RefinedPeak<Point> ReducedListsingle(RandomAccessibleInterval<FloatType> inputimg,
			ArrayList<RefinedPeak<Point>> SubpixelMinlist, double[] sizes, double[] min, double[] max) {

		RandomAccessibleInterval<FloatType> imgout = new ArrayImgFactory<FloatType>().create(inputimg, new FloatType());
		double[] points = new double[imgout.numDimensions()];
		int maxcount = 0;
		int maxindex = 0;
		
		for (int index = 0; index < SubpixelMinlist.size(); ++index) {
			points = TransformCordinates.transformfwd(new double[] { SubpixelMinlist.get(index).getDoublePosition(0),
					SubpixelMinlist.get(index).getDoublePosition(1) }, sizes, min, max);

			double slope = -1.0 / Math.tan(Math.toRadians(points[0]));
			double intercept = points[1] / Math.sin(Math.toRadians(points[0]));
			
			PushCurves.Drawexactline(imgout, slope, intercept, IntensityType.Gaussian);

			RandomAccess<FloatType> inran = inputimg.randomAccess();
			Cursor<FloatType> outcursor = Views.iterable(imgout).localizingCursor();
			

			int count = 0;
			while (outcursor.hasNext()) {
				outcursor.fwd();

				if (outcursor.get().get() > 0) {
					inran.setPosition(outcursor);

					if (inran.get().get() > 0)
						count++;

					
					if (count > maxcount) {
						maxcount = count;
						maxindex = index;

					}

				}

			}
		}
		
		
		if (maxcount > 0)
		
		
		
		
		return SubpixelMinlist.get(maxindex);
		
		else
			
			return null;
	}
	
	


	public static ArrayList<double[]> GetRhoTheta(ArrayList<RefinedPeak<Point>> MainMinlist, double[] sizes, double[] min,
			double[] max) {

		ArrayList<double[]> points = new ArrayList<double[]>(); //[sizes.length];
		for (int index = 0; index < MainMinlist.size(); ++index) {

		final double[]	point = TransformCordinates.transformfwd(new double[] { MainMinlist.get(index).getDoublePosition(0),
					MainMinlist.get(index).getDoublePosition(1) }, sizes, min, max);
		
		points.add(point);
		}
		return points;
	}

	public static double[] GetRhoThetasingle(RefinedPeak<Point> MainMinlistsingle, double[] sizes, double[] min,
			double[] max) {


		final double[]	point = TransformCordinates.transformfwd(new double[] { MainMinlistsingle.getDoublePosition(0),
					MainMinlistsingle.getDoublePosition(1) }, sizes, min, max);
		
		return point;
	}
	
	
	public static void Getlines(RandomAccessibleInterval<FloatType> imgout, ArrayList<CommonOutput> newlinelist){
		
		for (int index = 0; index < newlinelist.size(); ++index){
			
			final double slope = newlinelist.get(index).lineparam[0];
			final double intercept = newlinelist.get(index).lineparam[1];
			final double ifprep = newlinelist.get(index).lineparam[2];
			RandomAccessibleInterval<FloatType> sourceimg = newlinelist.get(index).Actualroi;
			FinalInterval interval = newlinelist.get(index).interval;
			sourceimg = Views.interval(sourceimg, interval);
			
			if (slope!= Double.MAX_VALUE && intercept!=Double.MAX_VALUE && ifprep==Double.MAX_VALUE){
				
				
				PushCurves.DrawRoiimageline(imgout, sourceimg, slope, intercept);
				}
			
			else if (ifprep!= Double.MAX_VALUE){
				
				PushCurves.DrawRoiimagelineprep(imgout, sourceimg, ifprep);
			}
			
		}
	}
	
	
	
	
/*	public static void GetAlllines(
			RandomAccessibleInterval<FloatType> imgout,
			
			RandomAccessibleInterval<IntType> intimg, 
			ArrayList<Lineobjects> linelist,
			ArrayList<Simpleobject> lineobject) {

		for (int index = 0; index < linelist.size(); ++index) {

			final int label = linelist.get(index).Label;
			
			double[] rhothetalist = linelist.get(index).singleslopeandintercept;
			
		
			
			final double slope = rhothetalist[0];
			final double intercept = rhothetalist[1];
			final double ifprep = rhothetalist[2];
			
			

			if (slope!= Double.MAX_VALUE && intercept!=Double.MAX_VALUE && ifprep==Double.MAX_VALUE){
			final Simpleobject simpleobj = new Simpleobject(label, slope, intercept, ifprep);
			lineobject.add(simpleobj);
			
			
			PushCurves.DrawTruncatedline(imgout, intimg, slope, intercept, label);
			
			}
			else if (ifprep!= Double.MAX_VALUE){
				final Simpleobject simpleobj = new Simpleobject(label, Double.MAX_VALUE, Double.MAX_VALUE, ifprep);
				lineobject.add(simpleobj);
				
				PushCurves.DrawTruncatedprepline(imgout, intimg, ifprep, label);
			}
			

		}
		
	}
	
		*/
	

}
