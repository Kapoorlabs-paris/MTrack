package lineFinder;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import graphconstructs.Logger;
import ij.ImagePlus;
import ij.gui.EllipseRoi;
import ij.gui.Overlay;
import interactiveMT.Interactive_MTDoubleChannel;
import labeledObjects.CommonOutputHF;
import mserMethods.GetDelta;
import mserMethods.MserHF;
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
	private final  ArrayList<MserTree<UnsignedByteType>> Treelist;
	private final int framenumber;
	private ArrayList<CommonOutputHF> output;
	private ArrayList<CommonOutputHF> outputcurr;
	public ArrayList<MserTree<UnsignedByteType>> AllnewTree;
	private final int ndims;
	private  int Maxlabel;
	private int Roiindex;
	
	private EllipseRoi ellipseroi;
	
	
	
	public LinefinderInteractiveHFHough (final Interactive_MTDoubleChannel parent,
			final RandomAccessibleInterval<FloatType> source, 
			final RandomAccessibleInterval<FloatType> Preprocessedsource, 
			final ArrayList<MserTree<UnsignedByteType>> Treelist,
			final int MaxLabel,
			final int framenumber){
		this.parent = parent;
		this.source = source;
		this.Preprocessedsource = Preprocessedsource;
		this.Treelist = Treelist;
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
		AllnewTree = new ArrayList<MserTree<UnsignedByteType>>(); 

		output = new ArrayList<CommonOutputHF>();
		
        final FloatType type = source.randomAccess().get().createVariable();
		

		ArrayList<double[]> ellipselist = new ArrayList<double[]>();
		ArrayList<double[]> meanandcovlist = new ArrayList<double[]>();
		int count = 0;
		for (int indexx = 0; indexx < Treelist.size(); ++indexx){
		
			MserTree<UnsignedByteType> newtree = Treelist.get(indexx);
			
			
		final HashSet<Mser<UnsignedByteType>> rootset = newtree.roots();
		
		
		final Iterator<Mser<UnsignedByteType>> rootsetiterator = rootset.iterator();
		
		
		
		
		while (rootsetiterator.hasNext()) {

			Mser<UnsignedByteType> rootmser = rootsetiterator.next();

			if (rootmser.size() > 0) {

				final double[] meanandcov = { rootmser.mean()[0], rootmser.mean()[1], rootmser.cov()[0],
						rootmser.cov()[1], rootmser.cov()[2] };
				meanandcovlist.add(meanandcov);
				ellipselist.add(meanandcov);

			}
		}
		
		// We do this so the ROI remains attached the the same label and is not changed if the program is run again
	       SortListbyproperty.sortpointList(ellipselist);
		
			for (int index = 0; index < ellipselist.size(); ++index) {
				
				
				final ImgFactory<FloatType> factory = Util.getArrayOrCellImgFactory(Preprocessedsource, type);
				RandomAccessibleInterval<FloatType>  Roiimg = factory.create(Preprocessedsource, type);
				RandomAccessibleInterval<FloatType>  ActualRoiimg = factory.create(source, type);
				
				final double[] mean = { ellipselist.get(index)[0], ellipselist.get(index)[1] };
				final double[] covar = { ellipselist.get(index)[2], ellipselist.get(index)[3],
						ellipselist.get(index)[4] };
				ellipseroi = GetDelta.createEllipse(mean, covar, 3);
				
	    		final double perimeter = ellipseroi.getLength();
	    		final double smalleigenvalue = SmallerEigenvalue(mean, covar);
	    	//	if (perimeter > 2 * Math.PI * minlength && smalleigenvalue < 30){
	    			
	    			Roiindex = count;
	    			count++;
				ellipseroi.setStrokeColor(Color.green);
				

				Cursor<FloatType> sourcecursor = Views.iterable(Preprocessedsource).localizingCursor();
				RandomAccess<FloatType> ranac = Roiimg.randomAccess();
				while (sourcecursor.hasNext()) {

					sourcecursor.fwd();

					final int x = sourcecursor.getIntPosition(0);
					final int y = sourcecursor.getIntPosition(1);
					ranac.setPosition(sourcecursor);
					if (ellipseroi.contains(x, y)) {
						
						ranac.get().set(sourcecursor.get());

					}
					

				}
				
				
				
				FinalInterval interval = util.Boundingboxes.CurrentroiInterval(Roiimg, ellipseroi);
				
				
				Cursor<FloatType> Actualsourcecursor = Views.iterable(source).localizingCursor();
				RandomAccess<FloatType> Actualranac = ActualRoiimg.randomAccess();
				while (Actualsourcecursor.hasNext()) {

					Actualsourcecursor.fwd();

					final int x = Actualsourcecursor.getIntPosition(0);
					final int y = Actualsourcecursor.getIntPosition(1);
					Actualranac.setPosition(Actualsourcecursor);
					if (ellipseroi.contains(x, y)) {
						
						Actualranac.get().set(Actualsourcecursor.get());

					}
					

				}
				
				
				CommonOutputHF currentOutput = new CommonOutputHF(framenumber, Roiindex, Roiimg, ActualRoiimg, interval);
				
				
				outputcurr.add(currentOutput);
				output.addAll(outputcurr);
				
			}
			
		}
			
		return true;
	}

	public ArrayList<MserTree<UnsignedByteType>> getTreelist() {

		return AllnewTree;
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
