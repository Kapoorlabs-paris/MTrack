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

import java.util.ArrayList;


import drawandOverlay.HoughPushCurves;
import drawandOverlay.OverlayLines;
import houghandWatershed.WatershedDistimg;
import labeledObjects.CommonOutput;
import labeledObjects.CommonOutputHF;
import net.imglib2.FinalInterval;
import net.imglib2.Point;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.BenchmarkAlgorithm;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.algorithm.localextrema.RefinedPeak;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import preProcessing.GetLocalmaxminMT;
import preProcessing.GlobalThresholding;
import preProcessing.Kernels;
import util.Boundingboxes;

public class LinefinderHFHough implements LinefinderHF {
	
	
	private static final String BASE_ERROR_MSG = "[Line-Finder]";
	protected String errorMessage;
	private final RandomAccessibleInterval<FloatType> source;
	private final RandomAccessibleInterval<FloatType> Preprocessedsource;
	private RandomAccessibleInterval<IntType> intimg;
	private final int framenumber;
	private final int minlength;
	private ArrayList<CommonOutputHF> output;
	private final int ndims;
	private  int Maxlabel;
	private int Roiindex;
	
	public int mintheta = 0;

	// Usually is 180 but to allow for detection of vertical
	// lines,allowing a few more degrees

	public int maxtheta = 240;
	public double thetaPerPixel = 1;
	public double rhoPerPixel = 1;
	
	
	public LinefinderHFHough (final RandomAccessibleInterval<FloatType> source, 
			final RandomAccessibleInterval<FloatType> Preprocessedsource, final int minlength, final int framenumber){
		
		this.source = source;
		this.Preprocessedsource = Preprocessedsource;
		this.minlength = minlength;
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
		output = new ArrayList<CommonOutputHF>();
		// Create the Bit image for distance transform and seed image for watershedding
		final Float ThresholdValue = GlobalThresholding.AutomaticThresholding(Preprocessedsource);
		RandomAccessibleInterval<BitType> bitimg = new ArrayImgFactory<BitType>().create(Preprocessedsource, new BitType());
		FloatType T = new FloatType(Math.round(ThresholdValue));
		GetLocalmaxminMT.ThresholdingMTBit(Preprocessedsource, bitimg, T);
		
		WatershedDistimg<FloatType> WaterafterDisttransform = new WatershedDistimg<FloatType>(Preprocessedsource, bitimg);
		WaterafterDisttransform.checkInput();
		WaterafterDisttransform.process();
	    intimg = WaterafterDisttransform.getResult();
		Maxlabel = WaterafterDisttransform.GetMaxlabelsseeded(intimg);
		int count = 0;
		for (int label = 0; label <= Maxlabel ; label++) {

			
           // Do not offset here
			Pair<RandomAccessibleInterval<FloatType>, FinalInterval> pair =  Boundingboxes.CurrentLabelImagepair(intimg, Preprocessedsource, count);
			RandomAccessibleInterval<FloatType> ActualRoiimg = Boundingboxes.CurrentLabelImage(intimg, source, count);
			RandomAccessibleInterval<FloatType> roiimg = pair.getA();
			
			FinalInterval Realinterval = pair.getB();
			
			
				
			 Roiindex = count;
			CommonOutputHF currentOutput = new CommonOutputHF(framenumber, Roiindex , roiimg, ActualRoiimg, intimg, Realinterval);
			
			
			output.add(currentOutput);
			count++;
			
			}
		
		
		return true;
	}

	@Override
	public ArrayList<CommonOutputHF> getResult() {

		return output;
	}

	@Override
	public String getErrorMessage() {

		return errorMessage;
	}



	

}
