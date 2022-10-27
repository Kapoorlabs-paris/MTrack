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
package lineFinder;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import ij.ImagePlus;
import ij.gui.EllipseRoi;
import ij.gui.Overlay;
import interactiveMT.Interactive_MTDoubleChannel;
import labeledObjects.CommonOutputHF;
import mserMethods.GetDelta;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.componenttree.mser.Mser;
import net.imglib2.algorithm.componenttree.mser.MserTree;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Intervals;
import net.imglib2.util.Pair;
import net.imglib2.util.Util;
import net.imglib2.view.Views;
import peakFitter.SortListbyproperty;
import preProcessing.Kernels;
import util.Boundingboxes;

public class LinefinderInteractiveHFHough implements LinefinderHF {
	
	
	private static final String BASE_ERROR_MSG = "[Line-Finder]";
	protected String errorMessage;
	private final RandomAccessibleInterval<FloatType> source;
	private final RandomAccessibleInterval<FloatType> Preprocessedsource;
	final Interactive_MTDoubleChannel parent;
	private final int framenumber;
	private ArrayList<CommonOutputHF> output;
	private ArrayList<CommonOutputHF> outputcurr;
	private final int ndims;
	private  int Maxlabel;
	private int Roiindex;
	private ArrayList<EllipseRoi> Allrois;
	private EllipseRoi ellipseroi;
	private final ArrayList<Pair<Integer, double[]>> IDALL;
	
	
	public LinefinderInteractiveHFHough (final Interactive_MTDoubleChannel parent,
			final RandomAccessibleInterval<FloatType> source, 
			final RandomAccessibleInterval<FloatType> Preprocessedsource, 
			
			final int MaxLabel,
			final int framenumber, 	 final ArrayList<Pair<Integer, double[]>> IDALL){
		this.parent = parent;
		this.source = source;
		this.Preprocessedsource = Preprocessedsource;
		
		this.Maxlabel = MaxLabel;
		this.IDALL = IDALL;
		this.framenumber = framenumber;
		ndims = source.numDimensions();
		
		
	}

	@Override
	public boolean checkInput() {
		if (source.numDimensions() > 2) {
			errorMessage = BASE_ERROR_MSG + " Can only operate on 2D, make slices of your stack . Got "
					+ source.numDimensions() + "D.";
			return false;
		}
		return true;
	}

	
	
	
	@Override
	public boolean process() {
		outputcurr = new ArrayList<CommonOutputHF>();
		Allrois = new ArrayList<EllipseRoi>();
		output = new ArrayList<CommonOutputHF>();
		int count = 0;
		for (int label = 1; label < Maxlabel - 1; ++label){
		
			Pair<RandomAccessibleInterval<FloatType>, FinalInterval> pair =  Boundingboxes.CurrentLabelImagepair(parent.intimg, Preprocessedsource, label);
			RandomAccessibleInterval<FloatType> ActualRoiimg = Boundingboxes.CurrentLabelImage(parent.intimg, source, label);
			RandomAccessibleInterval<FloatType> roiimg = pair.getA();
			
			
			MserTree<UnsignedByteType> tree = parent.newHoughtree.get(label);
			LinefinderInteractiveHFMSER newlineMser = new LinefinderInteractiveHFMSER(ActualRoiimg, roiimg,
					tree, parent.thirdDimension, IDALL);
			newlineMser.process();
			outputcurr = newlineMser.getResult();
			
			
		
				if (outputcurr.size() > 0){
					
					for (int i = 0; i < outputcurr.size(); ++i ){
						
						count++;
						
				CommonOutputHF currentOutput = new CommonOutputHF(outputcurr.get(i).framenumber, count,
						outputcurr.get(i).Roi, outputcurr.get(i).Actualroi,
						outputcurr.get(i).interval,outputcurr.get(i).Allrois);
				output.add(currentOutput);
			
					}
				}
			

			
		
		
		
		}
		
		
			
		return true;
	}

	
	
	
	public ArrayList<CommonOutputHF> getResult() {

		return output;
	}

	@Override
	public String getErrorMessage() {

		return errorMessage;
	}

	/**
	 * Returns the smallest eigenvalue of the ellipse
	 * 
	 * 
	 *@param mean
	 *            (x,y) components of mean vector
	 * @param cov
	 *            (xx, xy, yy) components of covariance matrix
	 * @return slope and intercept of the line along the major axis
	 */
	public static double SmallerEigenvalue( final double[] mean, final double[] cov){
		
		// For inifinite slope lines support is provided
		final double a = cov[0];
		final double b = cov[1];
		final double c = cov[2];
		final double d = Math.sqrt(a * a + 4 * b * b - 2 * a * c + c * c);

		
        final double smalleigenvalue = (a + c - d) / 2;
       
        
        	
        	return smalleigenvalue;
        	
        	 
       
		
	}


	

}
