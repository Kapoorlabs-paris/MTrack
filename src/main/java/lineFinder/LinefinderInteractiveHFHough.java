package lineFinder;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import graphconstructs.Logger;
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
	protected Logger logger = Logger.DEFAULT_LOGGER;
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
	
	
	
	public LinefinderInteractiveHFHough (final Interactive_MTDoubleChannel parent,
			final RandomAccessibleInterval<FloatType> source, 
			final RandomAccessibleInterval<FloatType> Preprocessedsource, 
			
			final int MaxLabel,
			final int framenumber){
		this.parent = parent;
		this.source = source;
		this.Preprocessedsource = Preprocessedsource;
		
		this.Maxlabel = MaxLabel;
		
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
		
		for (int label = 1; label < Maxlabel - 1; ++label){
		
			Pair<RandomAccessibleInterval<FloatType>, FinalInterval> pair =  Boundingboxes.CurrentLabelImagepair(parent.intimg, Preprocessedsource, label);
			RandomAccessibleInterval<FloatType> ActualRoiimg = Boundingboxes.CurrentLabelImage(parent.intimg, source, label);
			RandomAccessibleInterval<FloatType> roiimg = pair.getA();
			
			
			MserTree<UnsignedByteType> tree = parent.newHoughtree.get(label);
			LinefinderInteractiveHFMSER newlineMser = new LinefinderInteractiveHFMSER(ActualRoiimg, roiimg,
					tree, parent.thirdDimension);
			newlineMser.process();
			outputcurr = newlineMser.getResult();
			
			
		
				int i = 0; 
				if (outputcurr.size() > 0){
				CommonOutputHF currentOutput = new CommonOutputHF(outputcurr.get(i).framenumber, label,
						outputcurr.get(i).Roi, outputcurr.get(i).Actualroi,parent.intimg,
						outputcurr.get(i).interval,outputcurr.get(i).Allrois);
				output.add(currentOutput);
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
@Override
public void setLogger(Logger logger) {
	this.logger = logger;
	
}

	

}
