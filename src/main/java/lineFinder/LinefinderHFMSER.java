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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import houghandWatershed.HoughTransformandMser;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.EllipseRoi;
import ij.gui.Overlay;
import labeledObjects.CommonOutput;
import labeledObjects.CommonOutputHF;
import labeledObjects.LabelledImg;
import mserMethods.GetDelta;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.BenchmarkAlgorithm;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.algorithm.componenttree.mser.Mser;
import net.imglib2.algorithm.componenttree.mser.MserTree;
import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Util;
import net.imglib2.view.Views;
import peakFitter.SortListbyproperty;

public class LinefinderHFMSER implements LinefinderHF{

	private static final String BASE_ERROR_MSG = "[Line-Finder]";
	protected String errorMessage;
	private final RandomAccessibleInterval<FloatType> source;
	private final RandomAccessibleInterval<FloatType> Preprocessedsource;
	private final int framenumber;
	private final int minlength;
	private ArrayList<CommonOutputHF> output;
	public  double delta = 10;
	public final long minSize = 1;
	public final long maxSize = Long.MAX_VALUE;
	public  double maxVar = 0.5;
	public double minDiversity = 0;
	public int maxlines = 100;
	public final int maxdelta = 20;
	private Overlay ov;
	public boolean darktoBright = false;
	private int Roiindex;
	private final int ndims;
	private EllipseRoi ellipseroi;
	private ArrayList<EllipseRoi> Allrois;
	public LinefinderHFMSER (final RandomAccessibleInterval<FloatType> source, 
			final RandomAccessibleInterval<FloatType> Preprocessedsource, final int minlength, final int framenumber){
		
		this.source = source;
		this.Preprocessedsource = Preprocessedsource;
		this.minlength = minlength;
		this.framenumber = framenumber;
		ndims = source.numDimensions();
	}
	public void setDelta(double delta) {
		this.delta = delta;
	}
	
	public void setDarktoBright(boolean darktoBright) {
		this.darktoBright = darktoBright;
	}
	

	
	public void setMaxlines(int maxlines) {
		this.maxlines = maxlines;
	}
	
	public void setMinDiversity(double minDiversity) {
		this.minDiversity = minDiversity;
	}
	
	public void setMaxVar(double maxVar) {
		this.maxVar = maxVar;
	}
	 public long getMinSize() {
		return minSize;
	}
	 
	public double getDelta() {
		return delta;
	}
	
	public long getMaxSize() {
		return maxSize;
	} 
	 
	 public int getMaxdelta() {
		return maxdelta;
	}
	 
	 public double getMaxVar() {
		return maxVar;
	}
	 
	 public int getMaxlines() {
		return maxlines;
	}
	 
	 public double getMinDiversity() {
		return minDiversity;
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
		
		Allrois = new ArrayList<EllipseRoi>();
        final FloatType type = source.randomAccess().get().createVariable();
		

		ov = new Overlay();
		ArrayList<double[]> ellipselist = new ArrayList<double[]>();
		ArrayList<double[]> meanandcovlist = new ArrayList<double[]>();
		final Img<UnsignedByteType> newimg;

		try
		{
		ImageJFunctions.wrap(Preprocessedsource, "curr");
		
		final ImagePlus currentimp = IJ.getImage();
		IJ.run("8-bit");
		
		newimg = ImagePlusAdapter.wrapByte(currentimp);

		}
		catch ( final Exception e )
		{
			e.printStackTrace();
			return false;
		}
		
		
		System.out.println("Determining the best delta parameter for the image:");
		double bestdelta = GetDelta.Bestdeltaparam(newimg, delta, minSize, maxSize, maxVar, minDiversity, minlength,
				maxlines, maxdelta, darktoBright);
		
		MserTree<UnsignedByteType> newtree = MserTree.buildMserTree(newimg, bestdelta, minSize, maxSize, maxVar,
				minDiversity, darktoBright);
		
		ArrayList<double[]> redmeanandcovlist = new ArrayList<double[]>();
		ArrayList<double[]> meanandcovchildlist = new ArrayList<double[]>();
		
		final HashSet<Mser<UnsignedByteType>> rootset = newtree.roots();
		
		
		final Iterator<Mser<UnsignedByteType>> rootsetiterator = rootset.iterator();
		
		
		
		
		while (rootsetiterator.hasNext()) {

			Mser<UnsignedByteType> rootmser = rootsetiterator.next();

			if (rootmser.size() > 0) {

				final double[] meanandcov = { rootmser.mean()[0], rootmser.mean()[1], rootmser.cov()[0],
						rootmser.cov()[1], rootmser.cov()[2] };
				meanandcovlist.add(meanandcov);

			}
		}
		
		// We do this so the ROI remains attached the the same label and is not changed if the program is run again
	   
	       final Iterator<Mser<UnsignedByteType>> treeiterator = newtree.iterator();
	       
	       while (treeiterator.hasNext()) {

				Mser<UnsignedByteType> mser = treeiterator.next();

				if (mser.getChildren().size() > 0) {

					for (int index = 0; index < mser.getChildren().size(); ++index) {

						final double[] meanandcovchild = { mser.getChildren().get(index).mean()[0],
								mser.getChildren().get(index).mean()[1], mser.getChildren().get(index).cov()[0],
								mser.getChildren().get(index).cov()[1], mser.getChildren().get(index).cov()[2] };

						meanandcovchildlist.add(meanandcovchild);
						ellipselist.add(meanandcovchild);
						
					}

				}

			}
	       redmeanandcovlist = meanandcovlist;
			for (int childindex = 0; childindex < meanandcovchildlist.size(); ++childindex) {

				final double[] meanchild = new double[] { meanandcovchildlist.get(childindex)[0],
						meanandcovchildlist.get(childindex)[1] };

				for (int index = 0; index < meanandcovlist.size(); ++index) {

					final double[] mean = new double[] { meanandcovlist.get(index)[0], meanandcovlist.get(index)[1] };
					final double[] covar = new double[] { meanandcovlist.get(index)[2], meanandcovlist.get(index)[3],
							meanandcovlist.get(index)[4] };
					final EllipseRoi ellipse = createEllipse(mean, covar, 3);

					if (ellipse.contains((int) meanchild[0], (int) meanchild[1]))
						redmeanandcovlist.remove(index);

				}

			}

			for (int index = 0; index < redmeanandcovlist.size(); ++index) {

				final double[] meanandcov = new double[] { redmeanandcovlist.get(index)[0], redmeanandcovlist.get(index)[1],
						redmeanandcovlist.get(index)[2], redmeanandcovlist.get(index)[3], redmeanandcovlist.get(index)[4] };
				ellipselist.add(meanandcov);

			}
		    SortListbyproperty.sortpointList(ellipselist);
		int count = 0;
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
	    		if (perimeter > 2 * Math.PI * minlength && smalleigenvalue < 30){
	    			
	    			Roiindex = count;
	    			count++;
				ellipseroi.setStrokeColor(Color.green);
				
				ov.add(ellipseroi);

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
				
				Allrois.add(ellipseroi);
				
				CommonOutputHF currentOutput = new CommonOutputHF(framenumber, Roiindex, Roiimg, ActualRoiimg, interval, Allrois);
				
				
				output.add(currentOutput);
				
				}
				
			}

		

		
		
		return true;
	}

	@Override
	public ArrayList<CommonOutputHF> getResult() {

		return output;
	}
	
	public EllipseRoi getRoi(){
	
		return ellipseroi;
		
	}
	
	 public Overlay getOverlay() {
			
			return ov;
		}
	/**
	 * Returns the slope and the intercept of the line passing through the major axis of the ellipse
	 * 
	 * 
	 *@param mean
	 *            (x,y) components of mean vector
	 * @param cov
	 *            (xx, xy, yy) components of covariance matrix
	 * @return slope and intercept of the line along the major axis
	 */
	public  double[] LargestEigenvector( final double[] mean, final double[] cov){
		
		// For inifinite slope lines support is provided
		final double a = cov[0];
		final double b = cov[1];
		final double c = cov[2];
		final double d = Math.sqrt(a * a + 4 * b * b - 2 * a * c + c * c);
		final double[] eigenvector1 = {2 * b, c - a + d};
		double[] LargerVec = new double[eigenvector1.length + 1];

		LargerVec =  eigenvector1;
		
        final double slope = LargerVec[1] / (LargerVec[0] );
        final double intercept = mean[1] - mean[0] * slope;
       
        if (Math.abs(slope) != Double.POSITIVE_INFINITY){
        double[] pair = {slope, intercept, Double.MAX_VALUE};
        return pair;
      
        }
        
        else {
        	
        	double[] prependicular = {Double.MAX_VALUE, Double.MAX_VALUE, mean[0]};
        	return prependicular;
        	}
        	 
       
		
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
	public  double SmallerEigenvalue( final double[] mean, final double[] cov){
		
		// For inifinite slope lines support is provided
		final double a = cov[0];
		final double b = cov[1];
		final double c = cov[2];
		final double d = Math.sqrt(a * a + 4 * b * b - 2 * a * c + c * c);

		
        final double smalleigenvalue = (a + c - d) / 2;
       
        
        	
        	return smalleigenvalue;
        	
        	 
       
		
	}
	
	public double Bestdeltaparam(final Img<UnsignedByteType> newimg,final double delta, final long minSize, 
			final long maxSize, final double maxVar, final double minDiversity, final int minlength, final int maxlines, final int maxdelta,  final boolean darktoBright){
	
		
		
		
			int stepdelta = 10;
			
			
			double MaxBestdelta = delta;
			ArrayList<Double> Bestdelta = new ArrayList<Double>();
			int Maxellipsecount = Integer.MIN_VALUE;
			
			
			for (int i = 0; i < maxdelta ; ++i){
			
				
				
			double bestdelta = delta +  i* stepdelta;	
				
			int ellipsecount = 0;
			
			
			ArrayList<double[]> ellipselist = new ArrayList<double[]>();
			

		MserTree<UnsignedByteType> newtree = MserTree.buildMserTree(newimg, bestdelta, minSize, maxSize, maxVar,
				minDiversity, darktoBright);
		final HashSet<Mser<UnsignedByteType>> rootset = newtree.roots();
		final Iterator<Mser<UnsignedByteType>> rootsetiterator = rootset.iterator();
		
		while (rootsetiterator.hasNext()) {

			Mser<UnsignedByteType> rootmser = rootsetiterator.next();

			if (rootmser.size() > 0) {

				final double[] meanandcov = { rootmser.mean()[0], rootmser.mean()[1], rootmser.cov()[0],
						rootmser.cov()[1], rootmser.cov()[2] };
				ellipselist.add(meanandcov);

			}
		}
		
		if (ellipselist.size() > 0){
		
		for (int index = 0; index < ellipselist.size(); ++index) {
			
			
			final double[] mean = { ellipselist.get(index)[0], ellipselist.get(index)[1] };
			final double[] covar = { ellipselist.get(index)[2], ellipselist.get(index)[3],
					ellipselist.get(index)[4] };
			final EllipseRoi ellipseroi = createEllipse(mean, covar, 3);
			
    		final double perimeter = ellipseroi.getLength();
    		
    		if (perimeter > Math.PI * minlength ){
    			
    			ellipsecount++;
    		}
		}
		}
		if (ellipsecount > Maxellipsecount && rootset.size() <= maxlines){
			
			Maxellipsecount = ellipsecount;
			MaxBestdelta = bestdelta;
		//	System.out.println(rootset.size() + " " + MaxBestdelta);
		}
		

		Bestdelta.add(MaxBestdelta);
		
		}
			
			Set<Double> mySet = new HashSet<Double>(Bestdelta);
			double maxcollection = 0;
			double frequdelta = MaxBestdelta;
			
			for(Double s: mySet){

				 System.out.println( "Best delta:" + s + " " + "Stable over iterations: " + Collections.frequency(Bestdelta,s));

				 
				 if (Collections.frequency(Bestdelta,s) > maxcollection){
				                      maxcollection = Collections.frequency(Bestdelta,s);
				                      frequdelta = s;
				 }
				 
				}
		
			return frequdelta;
		
		
		}
	/**
	 * 2D correlated Gaussian
	 * 
	 * @param mean
	 *            (x,y) components of mean vector
	 * @param cov
	 *            (xx, xy, yy) components of covariance matrix
	 * @return ImageJ roi
	 */
	public EllipseRoi createEllipse(final double[] mean, final double[] cov, final double nsigmas) {
		final double a = cov[0];
		final double b = cov[1];
		final double c = cov[2];
		final double d = Math.sqrt(a * a + 4 * b * b - 2 * a * c + c * c);
		final double scale1 = Math.sqrt(0.5 * (a + c + d)) * nsigmas;
		final double scale2 = Math.sqrt(0.5 * (a + c - d)) * nsigmas;
		final double theta = 0.5 * Math.atan2((2 * b), (a - c));
		final double x = mean[0];
		final double y = mean[1];
		final double dx = scale1 * Math.cos(theta);
		final double dy = scale1 * Math.sin(theta);
		final EllipseRoi ellipse = new EllipseRoi(x - dx, y - dy, x + dx, y + dy, scale2 / scale1);
		return ellipse;
	}
	
	
	@Override
	public String getErrorMessage() {

		return errorMessage;
	}



	
}

