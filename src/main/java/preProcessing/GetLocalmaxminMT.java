package preProcessing;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;


import drawandOverlay.AddGaussian;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Line;
import ij.gui.Overlay;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.PointSampleList;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPoint;
import net.imglib2.RealPointSampleList;
import net.imglib2.RealRandomAccess;
import net.imglib2.algorithm.dog.DogDetection;
import net.imglib2.algorithm.dog.DogDetection.ExtremaType;
import net.imglib2.algorithm.localextrema.RefinedPeak;
import net.imglib2.algorithm.neighborhood.Neighborhood;
import net.imglib2.algorithm.neighborhood.RectangleShape;
import net.imglib2.algorithm.region.hypersphere.HyperSphere;
import net.imglib2.algorithm.region.hypersphere.HyperSphereCursor;
import net.imglib2.algorithm.stats.Normalize;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.interpolation.randomaccess.NLinearInterpolatorFactory;
import net.imglib2.Cursor;
import net.imglib2.Point;
import net.imglib2.PointSampleList;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.type.Type;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Intervals;
import net.imglib2.util.Pair;
import net.imglib2.util.RealSum;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;

public class GetLocalmaxminMT {

	public static enum IntensityType {
		Gaussian
	}

	protected IntensityType intensityType;

	// Thresholding a FloatType to set values below the threshold to 0 intensity
	public static void ThresholdingMT(RandomAccessibleInterval<FloatType> img, RandomAccessibleInterval<FloatType> imgout,
			Float ThresholdValue, final IntensityType setintensity, double[] sigma) {

		final double[] backpos = new double[imgout.numDimensions()];
		final Cursor<FloatType> bound = Views.iterable(img).localizingCursor();

		final RandomAccess<FloatType> outbound = imgout.randomAccess();

		while (bound.hasNext()) {


			bound.fwd();

			outbound.setPosition(bound);

			if (bound.get().get() > (ThresholdValue)) {

				bound.localize(backpos);
				switch (setintensity) {

				

				case Gaussian:
					AddGaussian.addGaussian(imgout, backpos, sigma);
					break;
				
				default:
					AddGaussian.addGaussian(imgout, backpos, sigma);
					break;

				}

			}

			else {

				outbound.get().setZero();

			}
		}
	}

	// Thresholding a FlotType to convert to BitType
	

	// Finds and displays Local Maxima by constructing a 3*3*3.. local
	// neighbourhood
	public static RandomAccessibleInterval<FloatType> FindandDisplayLocalMaxima(RandomAccessibleInterval<FloatType> img,
			final IntensityType setintensity, double[] sigma) {

		// Create a new image for the output
		RandomAccessibleInterval<FloatType> output = new ArrayImgFactory<FloatType>().create(img, new FloatType());

		// define an interval that is span number of pixel smaller on each side
		// in each dimension
		int span = 1;

		Interval interval = Intervals.expand(img, -span);

		// create a view on the source with this interval
		img = Views.interval(img, interval);

		// create a Cursor that iterates over the source and checks in a
		// 8-neighborhood
		// if it is a maxima
		final Cursor<FloatType> center = Views.iterable(img).cursor();

		// instantiate a RectangleShape to access rectangular local
		// neighborhoods

		final RectangleShape shape = new RectangleShape(span, true);

		// iterate over the set of neighborhoods in the image
		for (final Neighborhood<FloatType> localNeighborhood : shape.neighborhoods(img)) {
			final FloatType centerValue = center.next();

			// keep this boolean true as long as no other value in the local
			// neighborhood
			// is smaller
			boolean isMaximum = true;

			// check if all pixels in the local neighborhood that are smaller
			for (final FloatType value : localNeighborhood) {
				// test if the center is smaller than the current pixel value
				if (centerValue.compareTo(value) < 0) {
					isMaximum = false;
					break;
				}
			}
			int n = img.numDimensions();
			double[] position = new double[n];
			if (isMaximum) {
				final RandomAccess<FloatType> outbound = output.randomAccess();
				outbound.setPosition(center);

				center.localize(position);
				switch (setintensity) {


				case Gaussian:
					AddGaussian.addGaussian(output, position, sigma);
					break;
				

				}

			}
		}

		return output;
	}

	
	
	
	// Finds and displays Local Minima by constructing a 3*3*3.. local
	// neighbourhood
	public static RandomAccessibleInterval<FloatType> FindandDisplayLocalMinima(RandomAccessibleInterval<FloatType> img,
			 final IntensityType setintensity, double[] sigma) {

		// Create a new image for the output
		Img<FloatType> output = new ArrayImgFactory<FloatType>().create(img, new FloatType());

		// define an interval that is span number of pixel smaller on each side
		// in each dimension
		int span = 1;

		Interval interval = Intervals.expand(img, -span);

		// create a view on the source with this interval
		img = Views.interval(img, interval);

		// create a Cursor that iterates over the source and checks in a
		// 8-neighborhood
		// if it is a maxima
		final Cursor<FloatType> center = Views.iterable(img).cursor();

		// instantiate a RectangleShape to access rectangular local
		// neighborhoods

		final RectangleShape shape = new RectangleShape(span, true);

		// iterate over the set of neighborhoods in the image
		for (final Neighborhood<FloatType> localNeighborhood : shape.neighborhoods(img)) {
			final FloatType centerValue = center.next();

			// keep this boolean true as long as no other value in the local
			// neighborhood
			// is smaller
			boolean isMinimum = true;

			// check if all pixels in the local neighborhood that are smaller
			for (final FloatType value : localNeighborhood) {
				// test if the center is smaller than the current pixel value
				if (centerValue.compareTo(value) >= 0) {
					isMinimum = false;
					break;
				}
			}
			double[] position = new double[img.numDimensions()];
			if (isMinimum) {

				final RandomAccess<FloatType> outbound = output.randomAccess();
				outbound.setPosition(center);
				center.localize(position);
				switch (setintensity) {

				

				case Gaussian:
					AddGaussian.addGaussian(output, position, sigma);
					break;
				default:
					AddGaussian.addGaussian(output, position, sigma);
					break;

				}

			}
		}

		return output;
	}

	// Write Local minima to an ArrayList<RealPoint>
	public static ArrayList<RealPoint> FindLocalMinima(RandomAccessibleInterval<FloatType> img) {

		int n = img.numDimensions();

		ArrayList<RealPoint> Minlist = new ArrayList<RealPoint>(n);

		int span = 1;

		Interval interval = Intervals.expand(img, -span);

		img = Views.interval(img, interval);

		final Cursor<FloatType> center = Views.iterable(img).cursor();

		final RectangleShape shape = new RectangleShape(span, true);

		for (final Neighborhood<FloatType> localNeighborhood : shape.neighborhoods(img)) {

			final FloatType centerValue = center.next();

			boolean isMinimum = true;

			for (final FloatType value : localNeighborhood) {
				if (centerValue.compareTo(value) >= 0) {
					isMinimum = false;
					break;
				}
			}

			if (isMinimum) {
				RealPoint Minpoints = new RealPoint(center);
				Minlist.add(Minpoints);
			}
		}

		return Minlist;
	}

	// Write Local maxima to an ArrayList<RealPoint>
	public static ArrayList<RealPoint> FindLocalMaxima(RandomAccessibleInterval<FloatType> img) {

		int n = img.numDimensions();

		ArrayList<RealPoint> Maxlist = new ArrayList<RealPoint>(n);

		int span = 1;

		Interval interval = Intervals.expand(img, -span);

		img = Views.interval(img, interval);

		final Cursor<FloatType> center = Views.iterable(img).cursor();

		final RectangleShape shape = new RectangleShape(span, true);

		for (final Neighborhood<FloatType> localNeighborhood : shape.neighborhoods(img)) {
			final FloatType centerValue = center.next();

			boolean isMaximum = true;

			for (final FloatType value : localNeighborhood) {

				if (centerValue.compareTo(value) < 0) {
					isMaximum = false;
					break;
				}
			}

			if (isMaximum) {
				RealPoint Minpoints = new RealPoint(center);
				Maxlist.add(Minpoints);
			}
		}

		return Maxlist;
	}

	
	// Detect minima in Scale space write it as an ArrayList<RefinedPeak<Point>>
		public static ArrayList<RefinedPeak<Point>> HoughspaceMaxima(RandomAccessibleInterval<FloatType> houghimage,
				FinalInterval interval, double[] sizes, double thetaPerPixel, double rhoPerPixel) {
			final Float houghval = GlobalThresholding.AutomaticThresholding(houghimage);
			ArrayList<RefinedPeak<Point>> SubpixelMinlist = new ArrayList<RefinedPeak<Point>>(houghimage.numDimensions());

			// Get local Minima in scale space to get Max rho-theta points
			double minPeakValue =   0.5 * houghval; 
			double smallsigma = 1;
			double bigsigma = 1.1;
			SubpixelMinlist = GetLocalmaxminMT.ScalespaceMinima(houghimage, interval, thetaPerPixel, rhoPerPixel,
					minPeakValue, smallsigma, bigsigma);
			
			return SubpixelMinlist;
		}
	
	
	// Detect minima in Scale space write it as an ArrayList<RefinedPeak<Point>>
	public static ArrayList<RefinedPeak<Point>> ScalespaceMinima(RandomAccessibleInterval<FloatType> inputimg,
			FinalInterval interval, double thetaPerPixel, double rhoPerPixel, double minPeakValue, double smallsigma,
			double bigsigma) {

		ArrayList<RefinedPeak<Point>> SubpixelMinlist = new ArrayList<RefinedPeak<Point>>(inputimg.numDimensions());
		// Create a Dog Detection object in Hough space
		DogDetection<FloatType> newdog = new DogDetection<FloatType>(Views.extendMirrorSingle(inputimg), interval,
				new double[] { thetaPerPixel, rhoPerPixel }, smallsigma, bigsigma, DogDetection.ExtremaType.MINIMA,
				minPeakValue, true);

		// Detect minima in Scale space
		SubpixelMinlist = newdog.getSubpixelPeaks();

		return SubpixelMinlist;
	}

	// Detect minima in Scale space write it as an ArrayList<<Point>>
	public static ArrayList<Point> ScalespaceMinimaSimple(RandomAccessibleInterval<FloatType> inputimg,
			FinalInterval interval, double thetaPerPixel, double rhoPerPixel, double minPeakValue, double smallsigma,
			double bigsigma) {

		ArrayList<Point> SubpixelMinlist = new ArrayList<Point>(inputimg.numDimensions());
		// Create a Dog Detection object in Hough spacex
		DogDetection<FloatType> newdog = new DogDetection<FloatType>(Views.extendMirrorSingle(inputimg), interval,
				new double[] { thetaPerPixel, rhoPerPixel }, smallsigma, bigsigma, DogDetection.ExtremaType.MINIMA,
				minPeakValue, true);

		// Detect minima in Scale space
		SubpixelMinlist = newdog.getPeaks();

		return SubpixelMinlist;
	}

	// Detect minima in Scale space write it as an ArrayList<<Point>>
	public static ArrayList<Point> ScalespaceMaximaSimple(RandomAccessibleInterval<FloatType> inputimg,
			FinalInterval interval, double thetaPerPixel, double rhoPerPixel, double minPeakValue, double smallsigma,
			double bigsigma) {

		ArrayList<Point> SubpixelMaxlist = new ArrayList<Point>(inputimg.numDimensions());
		// Create a Dog Detection object in Hough space
		DogDetection<FloatType> newdog = new DogDetection<FloatType>(Views.extendMirrorSingle(inputimg), interval,
				new double[] { thetaPerPixel, rhoPerPixel }, smallsigma, bigsigma, DogDetection.ExtremaType.MAXIMA,
				minPeakValue, true);

		// Detect minima in Scale space
		SubpixelMaxlist = newdog.getPeaks();

		return SubpixelMaxlist;
	}

	public static Pair<FloatType, FloatType> computeMinMaxIntensity(final RandomAccessibleInterval<FloatType> inputimg) {
		// create a cursor for the image (the order does not matter)
		final Cursor<FloatType> cursor = Views.iterable(inputimg).cursor();

		// initialize min and max with the first image value
		FloatType type = cursor.next();
		FloatType min = type.copy();
		FloatType max = type.copy();

		// loop over the rest of the data and determine min and max value
		while (cursor.hasNext()) {
			// we need this type more than once
			type = cursor.next();

			if (type.compareTo(min) < 0) {
				min.set(type);

			}

			if (type.compareTo(max) > 0) {
				max.set(type);

			}
		}
		Pair<FloatType, FloatType> pair = new ValuePair<FloatType, FloatType>(min, max);
		return pair;
	}
	public static Pair<FloatType, FloatType> computesecondMinMaxIntensity(final RandomAccessibleInterval<FloatType> inputimg) {
		// create a cursor for the image (the order does not matter)
		final Cursor<FloatType> cursor = Views.iterable(inputimg).cursor();

		// initialize min and max with the first image value
		FloatType type = cursor.next();
		FloatType min = type.copy();
		FloatType secondmin = type.copy();
		FloatType max = type.copy();

		// loop over the rest of the data and determine min and max value
		while (cursor.hasNext()) {
			// we need this type more than once
			type = cursor.next();

			if (type.compareTo(min) < 0) {
				min.set(type);

			}
			if (type.compareTo(secondmin) < 0 && secondmin.compareTo(min)>0) {
				secondmin.set(type);

			}
			
			if (type.compareTo(max) > 0) {
				max.set(type);

			}
		}
		Pair<FloatType, FloatType> pair = new ValuePair<FloatType, FloatType>(secondmin, max);
		return pair;
	}
	
	public static double computeMaxIntensity(final RandomAccessibleInterval<FloatType> inputimg) {
		final int ndims = inputimg.numDimensions();
		// create a cursor for the image (the order does not matter)
		final Cursor<FloatType> cursor = Views.iterable(inputimg).cursor();

		// initialize min and max with the first image value
		double maxVal =  Double.MIN_VALUE;
		// loop over the rest of the data and determine min and max value
		while (cursor.hasNext()) {
			// we need this type more than once
			cursor.fwd();

			

			if (cursor.get().get() > maxVal) {
				maxVal = cursor.get().get();

			}
		}
		
		return maxVal;
	}
	
	public static double computeMinIntensity(final RandomAccessibleInterval<FloatType> inputimg) {
		final int ndims = inputimg.numDimensions();
		// create a cursor for the image (the order does not matter)
		final Cursor<FloatType> cursor = Views.iterable(inputimg).cursor();

		// initialize min and max with the first image value
		double minVal =  Double.MAX_VALUE;
		// loop over the rest of the data and determine min and max value
		while (cursor.hasNext()) {
			// we need this type more than once
			cursor.fwd();

			

			if (cursor.get().get() < minVal) {
				minVal = cursor.get().get();

			}
		}
		
		return minVal;
	}

	public < T extends Comparable< T > & Type< T > > void computeMinMax(
	        final Iterable< T > input, final T min, final T max )
	    {
	        // create a cursor for the image (the order does not matter)
	        final Iterator< T > iterator = input.iterator();
	 
	        // initialize min and max with the first image value
	        T type = iterator.next();
	 
	        min.set( type );
	        max.set( type );
	 
	        // loop over the rest of the data and determine min and max value
	        while ( iterator.hasNext() )
	        {
	            // we need this type more than once
	            type = iterator.next();
	 
	            if ( type.compareTo( min ) < 0 )
	                min.set( type );
	 
	            if ( type.compareTo( max ) > 0 )
	                max.set( type );
	        }
	    }
	
	public static double computeMaxIntensityinlabel(final RandomAccessibleInterval<FloatType> inputimg, 
			final RandomAccessibleInterval<IntType> intimg, final int label ) {
		// create a cursor for the image (the order does not matter)
		final Cursor<FloatType> cursor = Views.iterable(inputimg).cursor();

		final RandomAccess<IntType> intranac = intimg.randomAccess();
		// initialize min and max with the first image value
		FloatType type = cursor.next();
		FloatType max = type.copy();

		// loop over the rest of the data and determine min and max value
		while (cursor.hasNext()) {
			// we need this type more than once
			type = cursor.next();

			 intranac.setPosition(cursor);
			int i = intranac.get().get();
			

			if (i == label){ 
			if (type.compareTo(max) > 0) {
				max.set(type);

			}
			}
		}
		final double maxintensity = max.getRealDouble();
		return maxintensity;
	}
	
	public static double computeMaxIntensityalongline(final RandomAccessibleInterval<FloatType> inputimg, 
			final RandomAccessibleInterval<IntType> intimg, final int label, final double slope, final double intercept ) {
		// create a cursor for the image (the order does not matter)
		final Cursor<FloatType> cursor = Views.iterable(inputimg).cursor();

		final RandomAccess<IntType> intranac = intimg.randomAccess();
		// initialize min and max with the first image value
		FloatType type = cursor.next();
		FloatType max = type.copy();

		// loop over the rest of the data and determine min and max value
		while (cursor.hasNext()) {
			// we need this type more than once
			type = cursor.next();

			 intranac.setPosition(cursor);
			int i = intranac.get().get();
			

			long pointonline = (long) (cursor.getDoublePosition(1) - slope * cursor.getDoublePosition(1) - intercept);
			if (i == label &&  pointonline == 0){ 
			if (type.compareTo(max) > 0) {
				max.set(type);

			}
			}
		}
		final double maxintensity = max.getRealDouble();
		return maxintensity;
	}
	public static long[] computeMaxinLabel(
			final RandomAccessibleInterval<FloatType> inputimg,
			final RandomAccessibleInterval<IntType> intimg,
			final int label,
			boolean ignorebright) {

		final Cursor<IntType> intcursor = Views.iterable(intimg).localizingCursor();
		final RandomAccess<FloatType> ranac = inputimg.randomAccess();
		
       Pair<FloatType, FloatType> pair = GetLocalmaxminMT.computeMinMaxIntensity(inputimg);
		
		// Neglect bright beads
		
		final Float threshold = (pair.getB().get() - pair.getA().get())/4;
		
		// initialize min and max with the first image value
		double max = Double.MIN_VALUE;
		long[] pos = new long[inputimg.numDimensions()];
		while(intcursor.hasNext()){
			intcursor.fwd();
			
			final int i = intcursor.get().get();
			
			if (i == label){
				ranac.setPosition(intcursor);
				if (ranac.get().get() > max){
					max = ranac.get().getRealDouble();
						
						ranac.localize(pos);
					
				}
				
			}
			
			
		}
		
      if (ignorebright == true){
			
			if (max<= threshold)
		
				return pos;
		}
      if (ignorebright == false)
    	  return pos;
		
      else
		return null;
	}

	
	
	
	// Find maxima only if the pixel intensity is higher than a certain
	// threshold value
	public static RandomAccessibleInterval<FloatType> FindConditionalLocalMaxima(
			RandomAccessibleInterval<FloatType> img, 
			final IntensityType setintensity, double[] sigma, Float val) {

		RandomAccessibleInterval<FloatType> output = new ArrayImgFactory<FloatType>().create(img, new FloatType());
		// Construct a 5*5*5... local neighbourhood
		int span = 2;

		Interval interval = Intervals.expand(img, -span);

		img = Views.interval(img, interval);

		final Cursor<FloatType> center = Views.iterable(img).cursor();

		final RectangleShape shape = new RectangleShape(span, true);

		for (final Neighborhood<FloatType> localNeighborhood : shape.neighborhoods(img)) {
			final FloatType centerValue = center.next();

			boolean isMaximum = true;

			for (final FloatType value : localNeighborhood) {
				if (centerValue.compareTo(value) < 0 && centerValue.get() < val) {
					isMaximum = false;
                    break;
				}
			}

			int n = img.numDimensions();
			double[] position = new double[n];
			if (isMaximum) {
				final RandomAccess<FloatType> outbound = output.randomAccess();
				outbound.setPosition(center);

				center.localize(position);
				switch (setintensity) {

			

				case Gaussian:
					AddGaussian.addGaussian(output, position, sigma);
					break;

				
				default:
					AddGaussian.addGaussian(output, position, sigma);
					break;

				}

			}

		}

		return output;
	}

	// Find maxima only if the pixel intensity is higher than a certain
	// threshold value
	public static RandomAccessibleInterval<FloatType> FindDirectionalLocalMaxima(
			RandomAccessibleInterval<FloatType> img, ImgFactory<FloatType> imageFactory,
			final IntensityType setintensity, double[] sigma, Float val) {

		RandomAccessibleInterval<FloatType> output = imageFactory.create(img, new FloatType());
		// Construct a 5*5*5... local neighbourhood
		int span = 2;

		Interval interval = Intervals.expand(img, -span);

		img = Views.interval(img, interval);

		final Cursor<FloatType> center = Views.iterable(img).cursor();

		final RectangleShape shape = new RectangleShape(span, true);

		for (final Neighborhood<FloatType> localNeighborhood : shape.neighborhoods(img)) {
			final FloatType centerValue = center.next();

			boolean isMaximum = true;

			Cursor<FloatType> localcursor = localNeighborhood.localizingCursor();

			while (localcursor.hasNext()) {
				localcursor.fwd();
				if (centerValue.compareTo(localcursor.get()) < 0){
					isMaximum = false;
				    break;
				}
			}


			int n = img.numDimensions();
			double[] position = new double[n];
			if (isMaximum) {
				final RandomAccess<FloatType> outbound = output.randomAccess();
				outbound.setPosition(center);

				center.localize(position);
				switch (setintensity) {

				

				case Gaussian:
					AddGaussian.addGaussian(output, position, sigma);
					break;

			

				default:
					AddGaussian.addGaussian(output, position, sigma);
					break;

				}

			}

		}

		return output;
	}

	public static ArrayList<RefinedPeak<Point>> Removesimilar(ArrayList<RefinedPeak<Point>> SubpixelMinlist,
			double thetatolerance, double rhotolerance) {
		/********
		 * The part below removes the close values in theta and rho coordinate
		 * (keeps only one of the multiple values)
		 ********/

		int j = 0;

		for (int i = 0; i < SubpixelMinlist.size(); ++i) {

			j = i + 1;
			while (j < SubpixelMinlist.size()) {

				if (Math.abs(SubpixelMinlist.get(i).getDoublePosition(0)
						- SubpixelMinlist.get(j).getDoublePosition(0)) <= thetatolerance
						&& Math.abs(SubpixelMinlist.get(i).getDoublePosition(1)
								- SubpixelMinlist.get(j).getDoublePosition(1)) <= rhotolerance) {

					SubpixelMinlist.remove(j);

				}

				else {
					++j;
				}

			}

		}

		return SubpixelMinlist;
	}
	public static  void ThresholdingMTBit( final RandomAccessibleInterval<FloatType> currentPreprocessedimg,
			final RandomAccessibleInterval< BitType > out,
			final Float threshold )
	{
		final Cursor< FloatType > cIn = Views.iterable( currentPreprocessedimg ).localizingCursor();
		final RandomAccess< BitType > rOut = out.randomAccess();

		final double[] position = new double[out.numDimensions()];
		while ( cIn.hasNext() )
		{
			cIn.fwd();
			rOut.setPosition( cIn );

			rOut.localize(position);
			if ( cIn.get().get() >= ( threshold ) ){
				
				rOut.get().setOne();
				
			}
			else{
				rOut.get().setZero();
				
			}
		}
		
	}
	public static void ThresholdingMTBit(RandomAccessibleInterval<FloatType> img,
			RandomAccessibleInterval<BitType> imgout, FloatType thresholdHough) {
		Normalize.normalize(Views.iterable(img), new FloatType(0), new FloatType(255));
		Normalize.normalize(Views.iterable(img), new FloatType(0), new FloatType(255));
		final double[] backpos = new double[imgout.numDimensions()];
		
		final Cursor<FloatType> bound = Views.iterable(img).localizingCursor();
		
		final RandomAccess<BitType> outbound =  imgout.randomAccess();

		while (bound.hasNext()) {

			bound.fwd();

			outbound.setPosition(bound);
			{
				
             if (bound.get().compareTo( thresholdHough ) < 0){

				bound.localize(backpos);

				outbound.get().setReal(0);

			}

			else  {

				outbound.get().setReal(255);

			}
        
			

		}
		
		}
	}
	

	
}
