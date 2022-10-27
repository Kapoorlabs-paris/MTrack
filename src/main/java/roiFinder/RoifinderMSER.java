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
package roiFinder;



	

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

public class RoifinderMSER  implements Roifinder{

	private static final String BASE_ERROR_MSG = "[Line-Finder]";
	protected String errorMessage;
	private final RandomAccessibleInterval<FloatType> source;
	private final RandomAccessibleInterval<FloatType> Preprocessedsource;
	private final int framenumber;
	private ArrayList<CommonOutput> output;
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
	private ArrayList<EllipseRoi> ellipseroi;
	public RoifinderMSER (final RandomAccessibleInterval<FloatType> source, 
			final RandomAccessibleInterval<FloatType> Preprocessedsource, final int framenumber ){
		
		this.source = source;
		this.Preprocessedsource = Preprocessedsource;
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

		output = new ArrayList<CommonOutput>();
		
        final FloatType type = Preprocessedsource.randomAccess().get().createVariable();
		

		ov = new Overlay();
		ArrayList<double[]> ellipselist = new ArrayList<double[]>();
		ArrayList<double[]> meanandcovlist = new ArrayList<double[]>();
		final Img<UnsignedByteType> newimg;

		try
		{
		ImageJFunctions.wrap(Preprocessedsource, "curr");
		ImageJFunctions.show(Preprocessedsource).setTitle("Preprocessed extended image");
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
		double bestdelta = GetDelta.Bestdeltaparam(newimg, delta, minSize, maxSize, maxVar, minDiversity, minSize,
				maxlines, maxdelta, darktoBright);
		
		MserTree<UnsignedByteType> newtree = MserTree.buildMserTree(newimg, bestdelta, minSize, maxSize, maxVar,
				minDiversity, darktoBright);
		
		
		
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
		int count = 0;
			for (int index = 0; index < ellipselist.size(); ++index) {
				
				
				final ImgFactory<FloatType> factory = Util.getArrayOrCellImgFactory(Preprocessedsource, type);
				RandomAccessibleInterval<FloatType>  Roiimg = factory.create(Preprocessedsource, type);
				RandomAccessibleInterval<FloatType>  ActualRoiimg = factory.create(source, type);
				
				final double[] mean = { ellipselist.get(index)[0], ellipselist.get(index)[1] };
				final double[] covar = { ellipselist.get(index)[2], ellipselist.get(index)[3],
						ellipselist.get(index)[4] };
				
				EllipseRoi or = GetDelta.createEllipse(mean, covar, 3);
				ellipseroi.add(or);
	    		final double perimeter = or.getLength();
	    		final double smalleigenvalue = SmallerEigenvalue(mean, covar);
	    		if (perimeter > 2 * Math.PI * minSize ){
	    			
	    			Roiindex = count;
	    			count++;
				
				ov.add(or);

				Cursor<FloatType> sourcecursor = Views.iterable(Preprocessedsource).localizingCursor();
				RandomAccess<FloatType> ranac = Roiimg.randomAccess();
				while (sourcecursor.hasNext()) {

					sourcecursor.fwd();

					final int x = sourcecursor.getIntPosition(0);
					final int y = sourcecursor.getIntPosition(1);
					ranac.setPosition(sourcecursor);
					if (or.contains(x, y)) {
						
						ranac.get().set(sourcecursor.get());

					}
					

				}
				
				
				
				FinalInterval interval = util.Boundingboxes.CurrentroiInterval(Roiimg, or);
				
				
				Cursor<FloatType> Actualsourcecursor = Views.iterable(source).localizingCursor();
				RandomAccess<FloatType> Actualranac = ActualRoiimg.randomAccess();
				while (Actualsourcecursor.hasNext()) {

					Actualsourcecursor.fwd();

					final int x = Actualsourcecursor.getIntPosition(0);
					final int y = Actualsourcecursor.getIntPosition(1);
					Actualranac.setPosition(Actualsourcecursor);
					if (or.contains(x, y)) {
						
						Actualranac.get().set(Actualsourcecursor.get());

					}
					

				}
				double[] slopeandintercept = new double[ndims];
				double[] slopeandinterceptCI = new double[2*ndims];
				
				
				
				
				// Obtain the slope and intercept of the line by obtaining the major axis of the ellipse (super fast and accurate)
				
					
				
				slopeandintercept = LargestEigenvector(mean, covar);
				if (slopeandintercept!= null){
				for (int d = 0; d < ndims; ++d){
					slopeandinterceptCI[d] = slopeandintercept[d];
					slopeandinterceptCI[d + ndims ] = 0;
				}
				
				CommonOutput currentOutput = new CommonOutput(framenumber, Roiindex, slopeandinterceptCI, Roiimg, ActualRoiimg, interval);
				
				
				output.add(currentOutput);
				}
				}
				
			}

		

		
		
		return true;
	}

	@Override
	public ArrayList<CommonOutput> getResult() {

		return output;
	}
	
	public ArrayList<EllipseRoi> getRois(){
	
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
        double[] pair = {slope, intercept};
        return pair;
      
        }
        else
        	return null;
       
        	 
       
		
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
