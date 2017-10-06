package lineFinder;

import java.util.ArrayList;


import drawandOverlay.HoughPushCurves;
import drawandOverlay.OverlayLines;
import houghandWatershed.WatershedDistimg;
import labeledObjects.CommonOutput;
import net.imglib2.FinalInterval;
import net.imglib2.Point;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.BenchmarkAlgorithm;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.algorithm.localextrema.RefinedPeak;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.RealSum;
import net.imglib2.view.Views;

import preProcessing.GetLocalmaxminMT;
import preProcessing.GlobalThresholding;
import preProcessing.Kernels;
import util.Boundingboxes;

public class LinefinderHough implements Linefinder {
	
	
	private static final String BASE_ERROR_MSG = "[Line-Finder]";
	protected String errorMessage;
	private final RandomAccessibleInterval<FloatType> source;
	private final RandomAccessibleInterval<FloatType> Preprocessedsource;
	private RandomAccessibleInterval<IntType> intimg;
	private final int framenumber;
	private final int minlength;
	private ArrayList<CommonOutput> output;
	private final int ndims;
	private  int Maxlabel;
	private int Roiindex;
	
	public int mintheta = 0;

	// Usually is 180 but to allow for detection of vertical
	// lines,allowing a few more degrees

	public int maxtheta = 240;
	public double thetaPerPixel = 1;
	public double rhoPerPixel = 1;
	
	
	public LinefinderHough (final RandomAccessibleInterval<FloatType> source, 
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
		output = new ArrayList<CommonOutput>();
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
		final double[] sizes = new double[ndims];

		// Automatic threshold determination for doing the Hough transform
		

		for (int label = 1; label < Maxlabel - 1; label++) {
			
           
			Pair<RandomAccessibleInterval<FloatType>, FinalInterval> pair =  Boundingboxes.CurrentLabeloffsetImagepair(intimg, Preprocessedsource, label);
			RandomAccessibleInterval<FloatType> ActualRoiimg = Boundingboxes.CurrentLabelImage(intimg, source, label);
			RandomAccessibleInterval<FloatType> roiimg = pair.getA();
			
			FinalInterval Realinterval = pair.getB();
			
			Float val = GlobalThresholding.AutomaticThresholding(roiimg);
				System.out.println("Doing Hough Transform in Label Number:" + label);
			double size = Math
					.sqrt((roiimg.dimension(0) * roiimg.dimension(0) + roiimg.dimension(1) * roiimg.dimension(1)));
			int minRho = (int) -Math.round(size);
			int maxRho = -minRho;
			
			double[] min = { mintheta, minRho };
			double[] max = { maxtheta, maxRho };
			int pixelsTheta = (int) Math.round((maxtheta - mintheta) / thetaPerPixel);
			int pixelsRho = (int) Math.round((maxRho - minRho) / rhoPerPixel);

			double ratio = (max[0] - min[0]) / (max[1] - min[1]);
			FinalInterval interval = new FinalInterval(new long[] { pixelsTheta, (long) (pixelsRho * ratio) });
			final RandomAccessibleInterval<FloatType> houghimage = new ArrayImgFactory<FloatType>().create(interval,
					new FloatType());

			HoughPushCurves.Houghspace(roiimg, houghimage, min, max, val);

			for (int d = 0; d < houghimage.numDimensions(); ++d)
				sizes[d] = houghimage.dimension(d);

			// Define Arraylist to get the slope and the intercept of the Hough
			// detected lines
			
			
			ArrayList<RefinedPeak<Point>> SubpixelMinlist = new ArrayList<RefinedPeak<Point>>(roiimg.numDimensions());

			
			// Get the list of all the detections
			
			final double avg = computeAverage( Views.iterable(houghimage) );
			System.out.println(avg);
			if(avg > 0){
			SubpixelMinlist = GetLocalmaxminMT.HoughspaceMaxima(houghimage, interval, sizes, thetaPerPixel, rhoPerPixel);

			// Reduce the number of detections by picking One line per Label,
			// using the best detection for each label
			RefinedPeak<Point> ReducedMinlistsingle =  OverlayLines.ReducedListsingle(roiimg, SubpixelMinlist, sizes, min, max);
			
			double slopeandinterceptCI[] = new double[2*ndims];
			if (ReducedMinlistsingle!= null){
			double[] points  = OverlayLines.GetRhoThetasingle(ReducedMinlistsingle, sizes, min, max);
 
			
			
			RefinedPeak<Point> peak  = OverlayLines.ReducedListsingle(roiimg, SubpixelMinlist, sizes, min, max);


			points = OverlayLines.GetRhoThetasingle(peak, sizes, min, max);
			
				
			double slope = -1.0 / (Math.tan(Math.toRadians(points[0])));
			double intercept = points[1] / Math.sin(Math.toRadians(points[0]));
			
			slopeandinterceptCI[0] = slope;
			slopeandinterceptCI[1] = intercept +  (Realinterval.realMin(1) - slope * Realinterval.realMin(0));
			
			for (int d = 0; d < ndims; ++d)
				slopeandinterceptCI[d + ndims] = 0;
			}
			/**
			 * This object has rho, theta, min dimensions, max dimensions of the
			 * label
			 * 
			 */
			 Roiindex = label;
			CommonOutput currentOutput = new CommonOutput(framenumber, Roiindex - 1, slopeandinterceptCI, roiimg, ActualRoiimg,intimg, Realinterval);
			
			
			output.add(currentOutput);
			
			
			}
		
		}
		return true;
	}
	  public < T extends RealType< T > > double computeAverage( final Iterable< T > input )
	    {
	        // Count all values using the RealSum class.
	        // It prevents numerical instabilities when adding up millions of pixels
	        final RealSum realSum = new RealSum();
	        long count = 0;
	 
	        for ( final T type : input )
	        {
	            realSum.add( type.getRealDouble() );
	            ++count;
	        }
	 
	        return realSum.getSum() / count;
	    }
	@Override
	public ArrayList<CommonOutput> getResult() {

		return output;
	}

	@Override
		public String getErrorMessage() {

			return errorMessage;
		}
	

	
	

}
