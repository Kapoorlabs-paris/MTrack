
package preProcessing;

import java.util.Random;

import drawandOverlay.AddGaussian;
import fftMethods.FFTConvolution;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.gauss3.Gauss3;
import net.imglib2.algorithm.region.hypersphere.HyperSphere;
import net.imglib2.algorithm.region.hypersphere.HyperSphereCursor;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.complex.ComplexFloatType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class Kernels {

	
	public static enum ProcessingType {
		Horizontaledge, Verticaledge, Gradientmag, NaiveEdge, Meanfilter, SupressThresh, CannyEdge
	}
	      // Any preprocessing

			public static RandomAccessibleInterval<FloatType> Preprocess(final RandomAccessibleInterval<FloatType> inputimg,
					final ProcessingType edge){
				
				
				RandomAccessibleInterval<FloatType> imgout = new ArrayImgFactory<FloatType>().create(inputimg,
						new FloatType());
				
				switch(edge){
				
				case Horizontaledge:
					imgout = inputimg;
					HorizontalEdge(imgout);
					break;
				case Meanfilter:
					imgout = Meanfilterandsupress(inputimg, 1.0);
					break;
				case NaiveEdge:
					imgout = NaiveEdge(inputimg, new double[]{1,1});
					break;
				case Gradientmag:
					imgout = GradientmagnitudeImage(inputimg);
					break;
				case Verticaledge:
					imgout = inputimg;
					VerticalEdge(imgout);
					break;
				case SupressThresh:
					imgout = Supressthresh(inputimg);
					break;
				case CannyEdge:
					imgout = CannyEdge(inputimg);
					break;
				default:
					imgout = Supressthresh(inputimg);
					break;
					
				
				
				
				}
				
				
				return imgout;
			}
	
	
	
	public static void ButterflyKernel(final RandomAccessibleInterval<FloatType> inputimage) {

		final float[] butterflyKernel = new float[] { 0, -2, 0, 1, 2, 1, 0, -2, 0 };

		final Img<FloatType> Butterfly = ArrayImgs.floats(butterflyKernel, new long[] { 3, 3 });

		// apply convolution to convolve input data with kernels

		new FFTConvolution<FloatType>(inputimage, Butterfly, new ArrayImgFactory<ComplexFloatType>()).convolve();
	}

	public static void GeneralButterflyKernel(final RandomAccessibleInterval<FloatType> inputimage, double linewidth,
			double dtheta) {
		final double size = Math.sqrt((inputimage.dimension(0) * inputimage.dimension(0)
				+ inputimage.dimension(1) * inputimage.dimension(1)));
		final double gradientmaggth = linewidth / Math.sin(Math.toRadians(dtheta));
		final double mask = -(2 * gradientmaggth + size) / 2;
		final float[] butterflyKernel = new float[] { 0, (float) mask, 0, (float) gradientmaggth, (float) size,
				(float) gradientmaggth, 0, (float) mask, 0 };

		final Img<FloatType> Butterfly = ArrayImgs.floats(butterflyKernel, new long[] { 3, 3 });

		// apply convolution to convolve input data with kernels

		new FFTConvolution<FloatType>(inputimage, Butterfly, new ArrayImgFactory<ComplexFloatType>()).convolve();
	}

	public static void BigButterflyKernel(final RandomAccessibleInterval<FloatType> inputimage) {

		final float[] butterflyKernel = new float[] { -10, -15, -22, -22, -22, -22, -22, -15, -10, -1, -6, -13, -22,
				-22, -22, -13, -6, -1, 3, 6, 4, -3, -22, -3, 4, 6, 3, 3, 11, 19, 28, 42, 28, 19, 11, 3, 3, 11, 27, 42,
				42, 42, 27, 11, 3, 3, 11, 19, 28, 42, 28, 19, 11, 3, 3, 6, 4, -3, -22, -3, 4, 6, 3, -1, -6, -13, -22,
				-22, -22, -13, -6, -1, -10, -15, -22, -22, -22, -22, -22, -15, -10

		};

		final Img<FloatType> Butterfly = ArrayImgs.floats(butterflyKernel, new long[] { 9, 9 });

		// apply convolution to convolve input data with kernels

		new FFTConvolution<FloatType>(inputimage, Butterfly, new ArrayImgFactory<ComplexFloatType>()).convolve();

	}

	public static void HorizontalEdge(final RandomAccessibleInterval<FloatType> inputimage) {
		final float[] HorizontalEdgeFilterKernel = new float[] { -1, 0, 1, -1, 0, 1, -1, 0, 1 };


		final Img<FloatType> HorizontalEdgeFilter = ArrayImgs.floats(HorizontalEdgeFilterKernel, new long[] { 3, 3 });
		// apply convolution to convolve input data with kernels

		new FFTConvolution<FloatType>(inputimage, HorizontalEdgeFilter, new ArrayImgFactory<ComplexFloatType>())
				.convolve();

	}

	public static void VerticalEdge(final RandomAccessibleInterval<FloatType> inputimage) {

		final float[] VerticalEdgeFilterKernel = new float[] { -1, -1, -1, 0, 0, 0, 1, 1, 1 };

		final Img<FloatType> VerticalEdgeFilter = ArrayImgs.floats(VerticalEdgeFilterKernel, new long[] { 3, 3 });
		// apply convolution to convolve input data with kernels

		new FFTConvolution<FloatType>(inputimage, VerticalEdgeFilter, new ArrayImgFactory<ComplexFloatType>())
				.convolve();

	}

	public static void Edgedetector(final RandomAccessibleInterval<FloatType> inputimage) {
		final float[] HorizontalEdgeFilterKernel = new float[] { -1, 0, 1, -1, 0, 1, -1, 0, 1 };

		final float[] VerticalEdgeFilterKernel = new float[] { -1, -1, -1, 0, 0, 0, 1, 1, 1 };

		final Img<FloatType> HorizontalEdgeFilter = ArrayImgs.floats(HorizontalEdgeFilterKernel, new long[] { 3, 3 });
		final Img<FloatType> VerticalEdgeFilter = ArrayImgs.floats(VerticalEdgeFilterKernel, new long[] { 3, 3 });
		// apply convolution to convolve input data with kernels

		new FFTConvolution<FloatType>(inputimage, HorizontalEdgeFilter, new ArrayImgFactory<ComplexFloatType>())
				.convolve();
		new FFTConvolution<FloatType>(inputimage, VerticalEdgeFilter, new ArrayImgFactory<ComplexFloatType>())
				.convolve();

	}

	// Do mean filtering on the inputimage
	public static void MeanFilter(RandomAccessibleInterval<FloatType> inputimage,
			RandomAccessibleInterval<FloatType> outimage, double sigma) {
		// Mean filtering for a given sigma
		Cursor<FloatType> cursorInput = Views.iterable(inputimage).cursor();
		Cursor<FloatType> cursorOutput = Views.iterable(outimage).cursor();
		FloatType mean = Views.iterable(inputimage).firstElement().createVariable();
		while (cursorInput.hasNext()) {
			cursorInput.fwd();
			cursorOutput.fwd();
			HyperSphere<FloatType> hyperSphere = new HyperSphere<FloatType>(Views.extendMirrorSingle(inputimage),
					cursorInput, (long) sigma);
			HyperSphereCursor<FloatType> cursorsphere = hyperSphere.cursor();
			cursorsphere.fwd();
			mean.set(cursorsphere.get());
			int n = 1;
			while (cursorsphere.hasNext()) {
				cursorsphere.fwd();
				n++;
				mean.add(cursorsphere.get());
			}
			mean.div(new FloatType(n));
			cursorOutput.get().set(mean);
		}
		
		
	}

	
	
	// Do mean filtering on the inputimage
	public static void MeanFilterBit(RandomAccessibleInterval<BitType> inputimage,
			RandomAccessibleInterval<BitType> outimage, double sigma) {
		// Mean filtering for a given sigma
		Cursor<BitType> cursorInput = Views.iterable(inputimage).cursor();
		Cursor<BitType> cursorOutput = Views.iterable(outimage).cursor();
		BitType mean = Views.iterable(inputimage).firstElement().createVariable();

		while (cursorInput.hasNext()) {
			cursorInput.fwd();
			cursorOutput.fwd();
			HyperSphere<BitType> hyperSphere = new HyperSphere<BitType>(Views.extendMirrorSingle(inputimage),
					cursorInput, (long) sigma);
			HyperSphereCursor<BitType> cursorsphere = hyperSphere.cursor();
			cursorsphere.fwd();
			mean.set(cursorsphere.get());
			boolean isbright = false;
			while (cursorsphere.hasNext()) {
				cursorsphere.fwd();
				if (cursorsphere.get().get()){
					isbright = true;
				break;	
				}
			}
			
			if (isbright)
			
			cursorOutput.get().set(isbright);
		}
		
		
	}
	
	// Naive Edge detector, first get the gradient of the image, then do local
	// supression

	public static RandomAccessibleInterval<FloatType> NaiveEdge(RandomAccessibleInterval<FloatType> inputimg,
			 double[] sigma) {
		RandomAccessibleInterval<FloatType> premaximgout = new ArrayImgFactory<FloatType>().create(inputimg,
				new FloatType());
		RandomAccessibleInterval<FloatType> maximgout = new ArrayImgFactory<FloatType>().create(inputimg,
				new FloatType());
		// Compute gradient of the image
		premaximgout = GradientmagnitudeImage(inputimg);

		
		// Compute global threshold for the premaximgout
		final Float Lowthreshold = GlobalThresholding.AutomaticThresholding(premaximgout);
		Cursor<FloatType> precursor = Views.iterable(premaximgout).localizingCursor();
		RandomAccess<FloatType> outputran = maximgout.randomAccess();
		while(precursor.hasNext()){
			precursor.fwd();
			outputran.setPosition(precursor);
			if (precursor.get().get()<=Lowthreshold)
				outputran.get().setZero();
			else
				outputran.get().set(precursor.get());;
		}
		return maximgout;

	}


public static void addBackground(final IterableInterval<FloatType> iterable, final double value) {
	for (final FloatType t : iterable)
		t.setReal(t.get() + value);
}

	public static RandomAccessibleInterval<FloatType> CannyEdge(RandomAccessibleInterval<FloatType> inputimg) {
		int n = inputimg.numDimensions();
		RandomAccessibleInterval<FloatType> cannyimage = new ArrayImgFactory<FloatType>().create(inputimg,
				new FloatType());
		RandomAccessibleInterval<FloatType> gradientimage = new ArrayImgFactory<FloatType>().create(inputimg,
				new FloatType());
		RandomAccessibleInterval<FloatType> Threshcannyimg = new ArrayImgFactory<FloatType>().create(inputimg,
				new FloatType());
		
	    // We will create local neighbourhood on this image
		gradientimage = GradientmagnitudeImage(inputimg);
	    
		// This is the intended output image so set up a cursor on it
		Cursor<FloatType> cursor = Views.iterable(cannyimage).localizingCursor();
		
		// Extend the input image for gradient computation
		RandomAccessible<FloatType> view = Views.extendMirrorSingle(inputimg);
		RandomAccess<FloatType> randomAccess = view.randomAccess();

		RandomAccessible<FloatType> gradientview = Views.extendMirrorSingle(gradientimage);

		final double[] direction = new double[n];
		final double[] left = new double[n];
		final double[] right = new double[n];

		// iterate over all pixels
		while (cursor.hasNext()) {
			// Initialize a point
			cursor.fwd();
			// compute gradient and its direction in each dimension and move
			// along the direction
			double gradient = 0;
			for (int d = 0; d < inputimg.numDimensions(); ++d) {
				randomAccess.setPosition(cursor);
				// move one pixel back in dimension d
				randomAccess.bck(d);
				
				// get the value
				double Back = randomAccess.get().getRealDouble();

				// move twice forward in dimension d, i.e.
				// one pixel above the location of the cursor
				randomAccess.fwd(d);
				randomAccess.fwd(d);

				// get the value
				double Fwd = randomAccess.get().getRealDouble();

				gradient += ((Fwd - Back) * (Fwd - Back)) / 4;

				direction[d] = (Fwd - Back) / 2;

			}
			// Normalize the gradient direction
			
			for (int d = 0; d < inputimg.numDimensions(); ++d) {
				if (gradient != 0)
					direction[d] = direction[d] / gradient;
				else
					direction[d] = Double.MAX_VALUE;
			}

			
			cursor.get().setReal(Math.sqrt(gradient));
		
            // A 5*5*5.. neighbourhood for span = 2, a 3*3*3.. neighbourhood for span = 1.
			final int span = 1;
           // Create a hypersphere at the current point in the gradient image
			final HyperSphere<FloatType> localsphere = new HyperSphere<FloatType>(gradientview, cursor, span);
            // To get only the points which are along the gradient direction create left and right in d dimensions
			Cursor<FloatType> localcursor = localsphere.localizingCursor();
			for (int d = 0; d < n; ++d) {
				left[d] = cursor.getDoublePosition(d) - direction[d];
				right[d] = cursor.getDoublePosition(d) + direction[d];
			}
			boolean isMaximum = true;
            final double tolerance = 20;
        	final RandomAccess<FloatType> outbound = Threshcannyimg.randomAccess();
			while (localcursor.hasNext()) {
				localcursor.fwd();
			
				for (int d = 0; d < n; ++d)
					// Before computing maxima check if it is along the gradient direction
					if(localcursor.getDoublePosition(d)-left[d]==0 || localcursor.getDoublePosition(d)-right[d]==0){
				    if (cursor.get().compareTo(localcursor.get()) < 0 ) {
					isMaximum = false;
				    	
					break;
				}
			}
				
				    if (cursor.get().compareTo(localcursor.get()) >= 0 ) {
				    	for (int d = 0; d < n; ++d)
							// If it is a maxima but not near the gradient direction, reject it
							if(Math.abs(localcursor.getDoublePosition(d)-left[d])>tolerance ||
									Math.abs(localcursor.getDoublePosition(d)-right[d])>tolerance){
				    	
					isMaximum = false;
								
					break;
				}
			}
				
			
			
			if (isMaximum) {
				
				
				outbound.setPosition(cursor);
				outbound.get().set(cursor.get());
				
			}
			}
		}
		

		//Supress values below the low threshold
		final Float Lowthreshold = GlobalThresholding.AutomaticThresholding(Threshcannyimg);
		Cursor<FloatType> cannycursor = Views.iterable(Threshcannyimg).localizingCursor();
		 Float threshold = Lowthreshold;
		
		
		while(cannycursor.hasNext()){
			cannycursor.fwd();
			if (cannycursor.get().get()<= threshold)
				cannycursor.get().setZero();
			else
				cannycursor.get().set(cannycursor.get());
		}
		
		
		return Threshcannyimg;
	}
	public static RandomAccessibleInterval<FloatType> CannyEdgenosupress(RandomAccessibleInterval<FloatType> inputimg) {
		int n = inputimg.numDimensions();
		RandomAccessibleInterval<FloatType> cannyimage = new ArrayImgFactory<FloatType>().create(inputimg,
				new FloatType());
		RandomAccessibleInterval<FloatType> gradientimage = new ArrayImgFactory<FloatType>().create(inputimg,
				new FloatType());
		RandomAccessibleInterval<FloatType> Threshcannyimg = new ArrayImgFactory<FloatType>().create(inputimg,
				new FloatType());
		
	    // We will create local neighbourhood on this image
		gradientimage = GradientmagnitudeImage(inputimg);
	    
		// This is the intended output image so set up a cursor on it
		Cursor<FloatType> cursor = Views.iterable(cannyimage).localizingCursor();
		
		// Extend the input image for gradient computation
		RandomAccessible<FloatType> view = Views.extendMirrorSingle(inputimg);
		RandomAccess<FloatType> randomAccess = view.randomAccess();

		RandomAccessible<FloatType> gradientview = Views.extendMirrorSingle(gradientimage);

		final double[] direction = new double[n];
		final double[] left = new double[n];
		final double[] right = new double[n];

		// iterate over all pixels
		while (cursor.hasNext()) {
			// Initialize a point
			cursor.fwd();
			// compute gradient and its direction in each dimension and move
			// along the direction
			double gradient = 0;
			for (int d = 0; d < inputimg.numDimensions(); ++d) {
				randomAccess.setPosition(cursor);
				// move one pixel back in dimension d
				randomAccess.bck(d);
				
				// get the value
				double Back = randomAccess.get().getRealDouble();

				// move twice forward in dimension d, i.e.
				// one pixel above the location of the cursor
				randomAccess.fwd(d);
				randomAccess.fwd(d);

				// get the value
				double Fwd = randomAccess.get().getRealDouble();

				gradient += ((Fwd - Back) * (Fwd - Back)) / 4;

				direction[d] = (Fwd - Back) / 2;

			}
			// Normalize the gradient direction
			
			for (int d = 0; d < inputimg.numDimensions(); ++d) {
				if (gradient != 0)
					direction[d] = direction[d] / gradient;
				else
					direction[d] = Double.MAX_VALUE;
			}

			
			cursor.get().setReal(Math.sqrt(gradient));
		
            // A 5*5*5.. neighbourhood for span = 2, a 3*3*3.. neighbourhood for span = 1.
			final int span = 1;
           // Create a hypersphere at the current point in the gradient image
			final HyperSphere<FloatType> localsphere = new HyperSphere<FloatType>(gradientview, cursor, span);
            // To get only the points which are along the gradient direction create left and right in d dimensions
			Cursor<FloatType> localcursor = localsphere.localizingCursor();
			for (int d = 0; d < n; ++d) {
				left[d] = cursor.getDoublePosition(d) - direction[d];
				right[d] = cursor.getDoublePosition(d) + direction[d];
			}
			boolean isMaximum = true;
        	final RandomAccess<FloatType> outbound = Threshcannyimg.randomAccess();
			while (localcursor.hasNext()) {
				localcursor.fwd();
			
				    if (cursor.get().compareTo(localcursor.get()) < 0 ) {
					isMaximum = false;
				    	
					break;
				
			}
			
				
			
			
			if (isMaximum) {
				
				
				outbound.setPosition(cursor);
				outbound.get().set(cursor.get());
				
			}
			}
		}
		

	
		
	
		
		
		return Threshcannyimg;
	}
	
	public static RandomAccessibleInterval<FloatType> CannyEdgeandMean(RandomAccessibleInterval<FloatType> inputimg,
			final double sigma) {
		int n = inputimg.numDimensions();
		RandomAccessibleInterval<FloatType> cannyimage = new ArrayImgFactory<FloatType>().create(inputimg,
				new FloatType());
		RandomAccessibleInterval<FloatType> gradientimage = new ArrayImgFactory<FloatType>().create(inputimg,
				new FloatType());
		RandomAccessibleInterval<FloatType> Threshcannyimg = new ArrayImgFactory<FloatType>().create(inputimg,
				new FloatType());
		
	    // We will create local neighbourhood on this image
		gradientimage = GradientmagnitudeImage(inputimg);
	    
		// This is the intended output image so set up a cursor on it
		Cursor<FloatType> cursor = Views.iterable(cannyimage).localizingCursor();
		
		// Extend the input image for gradient computation
		RandomAccessible<FloatType> view = Views.extendMirrorSingle(inputimg);
		RandomAccess<FloatType> randomAccess = view.randomAccess();

		RandomAccessible<FloatType> gradientview = Views.extendMirrorSingle(gradientimage);

		final double[] direction = new double[n];
		final double[] left = new double[n];
		final double[] right = new double[n];

		// iterate over all pixels
		while (cursor.hasNext()) {
			// Initialize a point
			cursor.fwd();
			// compute gradient and its direction in each dimension and move
			// along the direction
			double gradient = 0;
			
			for (int d = 0; d < inputimg.numDimensions(); ++d) {
				randomAccess.setPosition(cursor);
				// move one pixel back in dimension d
				randomAccess.bck(d);
				
				// get the value
				double Back = randomAccess.get().getRealDouble();

				// move twice forward in dimension d, i.e.
				// one pixel above the location of the cursor
				randomAccess.fwd(d);
				randomAccess.fwd(d);

				// get the value
				double Fwd = randomAccess.get().getRealDouble();

				gradient += ((Fwd - Back) * (Fwd - Back)) / 4;

				direction[d] = (Fwd - Back) / 2;

			}
			// Normalize the gradient direction
			
			for (int d = 0; d < inputimg.numDimensions(); ++d) {
				if (gradient != 0)
					direction[d] = direction[d] / gradient;
				else
					direction[d] = Double.MAX_VALUE;
			}
			
			
			cursor.get().setReal(Math.sqrt(gradient));
		
            // A 5*5*5.. neighbourhood for span = 2, a 3*3*3.. neighbourhood for span = 1.
			final int span = 1;
           // Create a hypersphere at the current point in the gradient image
			final HyperSphere<FloatType> localsphere = new HyperSphere<FloatType>(gradientview, cursor, span);
            // To get only the points which are along the gradient direction create left and right in d dimensions
			Cursor<FloatType> localcursor = localsphere.localizingCursor();
			for (int d = 0; d < n; ++d) {
				left[d] = cursor.getDoublePosition(d) - direction[d];
				right[d] = cursor.getDoublePosition(d) + direction[d];
			}
			boolean isMaximum = true;
        	final RandomAccess<FloatType> outbound = Threshcannyimg.randomAccess();
			while (localcursor.hasNext()) {
				localcursor.fwd();
			
					
				    if (cursor.get().compareTo(localcursor.get()) < 0 ) {
					isMaximum = false;
				    	
					break;
			}
				
			
			if (isMaximum) {
				
				
				outbound.setPosition(cursor);
				outbound.get().set(cursor.get());
				
			}
			}
		}
		

		//Supress values below the low threshold
		final Float Lowthreshold = GlobalThresholding.AutomaticThresholding(Threshcannyimg);
		Cursor<FloatType> cannycursor = Views.iterable(Threshcannyimg).localizingCursor();
		 Float threshold =  Lowthreshold;
		
		
		while(cannycursor.hasNext()){
			cannycursor.fwd();
			if (cannycursor.get().get()<= threshold)
				cannycursor.get().setZero();
			else
				cannycursor.get().set(cannycursor.get());
		}
		
		
		
		RandomAccessibleInterval<FloatType> meanimg = new ArrayImgFactory<FloatType>().create(inputimg,
				new FloatType());
		MeanFilter(Threshcannyimg, meanimg, sigma);
		
		
		
		return meanimg;
	}
	
	public static RandomAccessibleInterval<FloatType> CannyEdgeandMeanBit(RandomAccessibleInterval<BitType> inputimg,
			final double sigma) {
		int n = inputimg.numDimensions();
		RandomAccessibleInterval<BitType> cannyimage = new ArrayImgFactory<BitType>().create(inputimg,
				new BitType());
		RandomAccessibleInterval<BitType> gradientimage = new ArrayImgFactory<BitType>().create(inputimg,
				new BitType());
		RandomAccessibleInterval<FloatType> Threshcannyimg = new ArrayImgFactory<FloatType>().create(inputimg,
				new FloatType());
		
	    // We will create local neighbourhood on this image
		gradientimage = GradientmagnitudeImageBit(inputimg);
	    
		// This is the intended output image so set up a cursor on it
		Cursor<BitType> cursor = Views.iterable(cannyimage).localizingCursor();
		
		// Extend the input image for gradient computation
		RandomAccessible<BitType> view = Views.extendMirrorSingle(inputimg);
		RandomAccess<BitType> randomAccess = view.randomAccess();

		RandomAccessible<BitType> gradientview = Views.extendMirrorSingle(gradientimage);

		final double[] direction = new double[n];
		final double[] left = new double[n];
		final double[] right = new double[n];

		// iterate over all pixels
		while (cursor.hasNext()) {
			// Initialize a point
			cursor.fwd();
			// compute gradient and its direction in each dimension and move
			// along the direction
			double gradient = 0;
			
			for (int d = 0; d < inputimg.numDimensions(); ++d) {
				randomAccess.setPosition(cursor);
				// move one pixel back in dimension d
				randomAccess.bck(d);
				
				// get the value
				double Back = randomAccess.get().getRealDouble();

				// move twice forward in dimension d, i.e.
				// one pixel above the location of the cursor
				randomAccess.fwd(d);
				randomAccess.fwd(d);

				// get the value
				double Fwd = randomAccess.get().getRealDouble();

				gradient += ((Fwd - Back) * (Fwd - Back)) / 4;

				direction[d] = (Fwd - Back) / 2;

			}
			// Normalize the gradient direction
			
			for (int d = 0; d < inputimg.numDimensions(); ++d) {
				if (gradient != 0)
					direction[d] = direction[d] / gradient;
				else
					direction[d] = Double.MAX_VALUE;
			}
			
			
			cursor.get().setReal(Math.sqrt(gradient));
		
            // A 5*5*5.. neighbourhood for span = 2, a 3*3*3.. neighbourhood for span = 1.
			final int span = 1;
           // Create a hypersphere at the current point in the gradient image
			final HyperSphere<BitType> localsphere = new HyperSphere<BitType>(gradientview, cursor, span);
            // To get only the points which are along the gradient direction create left and right in d dimensions
			Cursor<BitType> localcursor = localsphere.localizingCursor();
			for (int d = 0; d < n; ++d) {
				left[d] = cursor.getDoublePosition(d) - direction[d];
				right[d] = cursor.getDoublePosition(d) + direction[d];
			}
			boolean isMaximum = true;
        	final RandomAccess<FloatType> outbound = Threshcannyimg.randomAccess();
			while (localcursor.hasNext()) {
				localcursor.fwd();
			
					
				    if (cursor.get().compareTo(localcursor.get()) < 0 ) {
					isMaximum = false;
				    	
					break;
			}
				
			
			if (isMaximum) {
				
				
				outbound.setPosition(cursor);
				FloatType setfloat = new FloatType(cursor.get().getRealFloat());
				outbound.get().set(setfloat);
				
			}
			}
		}
		

		RandomAccessibleInterval<FloatType> meanimg = new ArrayImgFactory<FloatType>().create(inputimg,
				new FloatType());
		MeanFilter(Threshcannyimg, meanimg,  sigma);
		
		return Threshcannyimg;
	}
	
	
	public static RandomAccessibleInterval<FloatType> Meanfilterandsupress(RandomAccessibleInterval<FloatType> inputimg, double sigma){
		// Mean filtering for a given sigma
		
		RandomAccessibleInterval<FloatType> outimg = new ArrayImgFactory<FloatType>().create(inputimg,
				new FloatType());
				Cursor<FloatType> cursorInput = Views.iterable(inputimg).cursor();
				Cursor<FloatType> cursorOutput = Views.iterable(outimg).cursor();
				FloatType mean = Views.iterable(inputimg).firstElement().createVariable();
				while (cursorInput.hasNext()) {
					cursorInput.fwd();
					cursorOutput.fwd();
					HyperSphere<FloatType> hyperSphere = new HyperSphere<FloatType>(Views.extendMirrorSingle(inputimg),
							cursorInput, (long) sigma);
					HyperSphereCursor<FloatType> cursorsphere = hyperSphere.cursor();
					cursorsphere.fwd();
					mean.set(cursorsphere.get());
					int n = 1;
					while (cursorsphere.hasNext()) {
						cursorsphere.fwd();
						n++;
						mean.add(cursorsphere.get());
					}
					mean.div(new FloatType(n));
					cursorOutput.get().set(mean);
				}
				final Float Lowthreshold = GlobalThresholding.AutomaticThresholding(inputimg);
				Cursor<FloatType> inputcursor = Views.iterable(inputimg).localizingCursor();
				RandomAccess<FloatType> outputran = outimg.randomAccess();
				while(inputcursor.hasNext()){
					inputcursor.fwd();
					outputran.setPosition(inputcursor);
					if (inputcursor.get().get()<=  0.5 * Lowthreshold)
						outputran.get().setZero();
					else
						outputran.get().set(inputcursor.get());
				}
			return outimg;
		
	}
	
	
	public static RandomAccessibleInterval<FloatType> Supressthresh(RandomAccessibleInterval<FloatType> inputimg){
		RandomAccessibleInterval<FloatType> Threshimg = new ArrayImgFactory<FloatType>().create(inputimg,
				new FloatType());
		//Supress values below the low threshold
		int n = inputimg.numDimensions();
		double[] position = new double[n];
				final Float Lowthreshold = GlobalThresholding.AutomaticThresholdingSec(inputimg);
				 Float threshold = Lowthreshold;
				Cursor<FloatType> inputcursor = Views.iterable(inputimg).localizingCursor();
				RandomAccess<FloatType> outputran = Threshimg.randomAccess();
				while(inputcursor.hasNext()){
					inputcursor.fwd();
					inputcursor.localize(position);
					outputran.setPosition(inputcursor);
					if (inputcursor.get().get()<= threshold)
						outputran.get().setZero();
					else
						outputran.get().set(inputcursor.get());
				}
			return Threshimg;	
				
				
	}
	
	
	public static RandomAccessibleInterval<FloatType> SupressLowthresh(RandomAccessibleInterval<FloatType> inputimg){
		RandomAccessibleInterval<FloatType> Threshimg = new ArrayImgFactory<FloatType>().create(inputimg,
				new FloatType());
		//Supress values below the low threshold
		int n = inputimg.numDimensions();
		double[] position = new double[n];
				final Float Lowthreshold = GlobalThresholding.AutomaticThresholdingSec(inputimg);
				 Float threshold = Lowthreshold;
				Cursor<FloatType> inputcursor = Views.iterable(inputimg).localizingCursor();
				RandomAccess<FloatType> outputran = Threshimg.randomAccess();
				while(inputcursor.hasNext()){
					inputcursor.fwd();
					inputcursor.localize(position);
					outputran.setPosition(inputcursor);
					if (inputcursor.get().get()<= 0.2 * threshold)
						outputran.get().setZero();
					else
						outputran.get().set(inputcursor.get());
				}
			return Threshimg;	
				
				
	}
	
	public static RandomAccessibleInterval<FloatType> SupressHeavythresh(RandomAccessibleInterval<FloatType> inputimg){
		RandomAccessibleInterval<FloatType> Threshimg = new ArrayImgFactory<FloatType>().create(inputimg,
				new FloatType());
		//Supress values below the low threshold
		int n = inputimg.numDimensions();
		double[] position = new double[n];
				final Float Lowthreshold = GlobalThresholding.AutomaticThresholdingSec(inputimg);
				 Float threshold = Lowthreshold;
				 Float Highthreshold = new Float(2.0 * threshold);
				Cursor<FloatType> inputcursor = Views.iterable(inputimg).localizingCursor();
				RandomAccess<FloatType> outputran = Threshimg.randomAccess();
				while(inputcursor.hasNext()){
					inputcursor.fwd();
					inputcursor.localize(position);
					outputran.setPosition(inputcursor);
					if (inputcursor.get().get()<= Highthreshold)
						outputran.get().setZero();
					else
						outputran.get().set(inputcursor.get());
				}
			return Threshimg;	
				
				
	}
	
	public static RandomAccessibleInterval<FloatType> GradientmagnitudeImage(
			RandomAccessibleInterval<FloatType> inputimg) {

		RandomAccessibleInterval<FloatType> gradientimg = new ArrayImgFactory<FloatType>().create(inputimg,
				new FloatType());
		Cursor<FloatType> cursor = Views.iterable(gradientimg).localizingCursor();
		RandomAccessible<FloatType> view = Views.extendBorder(inputimg);
		RandomAccess<FloatType> randomAccess = view.randomAccess();

		// iterate over all pixels
		while (cursor.hasNext()) {
			// move the cursor to the next pixel
			cursor.fwd();

			// compute gradient and its direction in each dimension
			double gradient = 0;

			for (int d = 0; d < inputimg.numDimensions(); ++d) {
				// set the randomaccess to the location of the cursor
				randomAccess.setPosition(cursor);

				// move one pixel back in dimension d
				randomAccess.bck(d);

				// get the value
				double Back = randomAccess.get().getRealDouble();

				// move twice forward in dimension d, i.e.
				// one pixel above the location of the cursor
				randomAccess.fwd(d);
				randomAccess.fwd(d);

				// get the value
				double Fwd = randomAccess.get().getRealDouble();

				gradient += ((Fwd - Back) * (Fwd - Back)) / 4;

			}

			cursor.get().setReal(Math.sqrt(gradient));

		}

		return gradientimg;
	}

	public static RandomAccessibleInterval<BitType> GradientmagnitudeImageBit(
			RandomAccessibleInterval<BitType> inputimg) {

		RandomAccessibleInterval<BitType> gradientimg = new ArrayImgFactory<BitType>().create(inputimg,
				new BitType());
		Cursor<BitType> cursor = Views.iterable(gradientimg).localizingCursor();
		RandomAccessible<BitType> view = Views.extendBorder(inputimg);
		RandomAccess<BitType> randomAccess = view.randomAccess();

		// iterate over all pixels
		while (cursor.hasNext()) {
			// move the cursor to the next pixel
			cursor.fwd();

			// compute gradient and its direction in each dimension
			double gradient = 0;

			for (int d = 0; d < inputimg.numDimensions(); ++d) {
				// set the randomaccess to the location of the cursor
				randomAccess.setPosition(cursor);

				// move one pixel back in dimension d
				randomAccess.bck(d);

				// get the value
				double Back = randomAccess.get().getRealDouble();

				// move twice forward in dimension d, i.e.
				// one pixel above the location of the cursor
				randomAccess.fwd(d);
				randomAccess.fwd(d);

				// get the value
				double Fwd = randomAccess.get().getRealDouble();

				gradient += ((Fwd - Back) * (Fwd - Back)) / 4;

			}

			cursor.get().setReal(Math.sqrt(gradient));

		}

		return gradientimg;
	}
}
