package peakFitter;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class GaussianMaskFitMSER {
	public static enum EndfitMSER {
		StartfitMSER, EndfitMSER
	}

	protected EndfitMSER EndfitMSER;

	public static double[] sumofgaussianMaskFit(final RandomAccessibleInterval<FloatType> signalInterval,
			final double[] location, final double[] sigma, final int numgaussians,
			final int iterations, final double[] dxvector, final double slope, final double intercept,
			final double maxintensityline,  final boolean halfgaussian, final EndfitMSER startorend, int label, double noiselevel)
			throws Exception {
		final int n = signalInterval.numDimensions();

		// pre-compute sigma^2
		final double[] sq_sigma = new double[n];
		for (int d = 0; d < n; ++d)
			sq_sigma[d] = sigma[d] * sigma[d];

		// make the interval we fit on iterable
		final IterableInterval<FloatType> signalIterable = Views.iterable(signalInterval);

		// create the mask image
		final Img<FloatType> gaussianMask = new ArrayImgFactory<FloatType>().create(signalInterval,
				signalIterable.firstElement());

		// set the mask image to the same location as the interval we fit on and
		// make it iterable
		final long[] translation = new long[n];
		for (int d = 0; d < n; ++d)
			translation[d] = signalInterval.min(d);

		final RandomAccessibleInterval<FloatType> translatedMask = Views.translate(gaussianMask, translation);
		final IterableInterval<FloatType> translatedIterableMask = Views.iterable(translatedMask);
		// remove background in the input
		final double bg = removeBackground(signalIterable);

		double N = 0;
		int i = 0;
		do {

		
			switch (startorend) {

			case StartfitMSER:
				beststartfitsumofGaussian(translatedIterableMask,  location, numgaussians, sq_sigma, dxvector, slope, intercept, maxintensityline, noiselevel,
						halfgaussian);
				break;

			case EndfitMSER:
				bestendfitsumofGaussian(translatedIterableMask, location, numgaussians, sq_sigma, dxvector, slope, intercept, maxintensityline, noiselevel,
						halfgaussian);
				break;

			}

			

			// ImageJFunctions.show(gaussianMask);
			// compute the sums
			final Cursor<FloatType> cMask = gaussianMask.cursor();
			final Cursor<FloatType> cImg = signalIterable.localizingCursor();
			double sumLocSN[] = new double[n]; // int_{all_px} d * S[ d ] * N[ d
												// ]
			double sumSN = 0; // int_{all_px} S[ d ] * N[ d ]
			double sumSS = 0; // int_{all_px} S[ d ] * S[ d ]

			while (cMask.hasNext()) {
				cMask.fwd();
				cImg.fwd();
				
				
				final double signal = cImg.get().getRealDouble();
				final double mask = cMask.get().getRealDouble();
				final double weight = maxintensityline;

				final double signalmask = signal * mask * weight;

				sumSN += signalmask;
				sumSS += signal * signal * weight;
				
				for (int d = 0; d < n; ++d) {
					final double l = cImg.getDoublePosition(d);
					sumLocSN[d] += l * signalmask;
				}

				}
			for (int d = 0; d < n; ++d)
				location[d] = sumLocSN[d] / sumSN;
				
			N = sumSN / sumSS;

			++i;

		} while (i < iterations);
		restoreBackground(signalIterable, bg);

		// ImageJFunctions.show(gaussianMask);
	
		
		switch (startorend) {

		case StartfitMSER:
			for (int d = 0; d < n; ++d)
			location[d] += -(numgaussians - 1)*dxvector[d];
			break;
		case EndfitMSER:
			for (int d = 0; d < n; ++d)
			location[d] +=  (numgaussians - 1)*dxvector[d];
			break;
		}
	
		
		
		return location;

	}

	

	public static double removeBackground(final IterableInterval<FloatType> iterable) {
		double i = 0;

		for (final FloatType t : iterable)
			i += t.getRealDouble();

		i /= (double) iterable.size();

		for (final FloatType t : iterable)
			t.setReal(t.get() - i);

		return i;
	}

	public static void restoreBackground(final IterableInterval<FloatType> iterable, final double value) {
		for (final FloatType t : iterable)
			t.setReal(t.get() + value);
	}

	final public static void beststartfitsumofGaussian(final IterableInterval<FloatType> image, final double[] location,
			final int numgaussians,
			final double[] sq_sigma, final double[] dxvector, final double slope, final double intercept, final double maxintensityline,
			final double noiselevel,
			boolean halfgaussian) {
		final int ndims = image.numDimensions();
		final Cursor<FloatType> cursor = image.localizingCursor();

		double sumofgaussians = 0;
		
		
		
		while (cursor.hasNext()) {
			cursor.fwd();

			
			double value = 1;
			for (int d = 0; d < ndims; ++d) {
				sumofgaussians = 0;
				for (int n = 1; n <= numgaussians; ++n){	
			
					
			final double x = cursor.getDoublePosition(d) - location[d] +  (n-1) * dxvector[d];
			sumofgaussians+= Math.exp(-(x * x) / sq_sigma[d]) ;
			
			}
				value *=  sumofgaussians;
				
		}
			
			if (halfgaussian){
			if (cursor.getDoublePosition(1) >= location[1] - (cursor.getDoublePosition(0) - location[0])/slope)
				 					value *= 0;
			}
			cursor.get().setReal(value);

		
		
		}
		
		
	}

	final public static void bestendfitsumofGaussian(final IterableInterval<FloatType> image,  final double[] location,
			final int numgaussians,
			final double[] sq_sigma, final double[] dxvector, final double slope, final double intercept, final double maxintensityline,
			final double noiselevel,
			 boolean halfgaussian) {
		final int ndims = image.numDimensions();
		final Cursor<FloatType> cursor = image.localizingCursor();
		double sumofgaussians = 0;
		while (cursor.hasNext()) {
			cursor.fwd();
			
			double value = 1;

			
			
				for (int d = 0; d < ndims; ++d) {
					sumofgaussians = 0;
					for (int n = 1; n <= numgaussians; ++n){	
				
						
				final double x = cursor.getDoublePosition(d) - location[d] -  (n-1) * dxvector[d];
				sumofgaussians+= Math.exp(-(x * x) / sq_sigma[d]) ;
				
				}
					value *=  sumofgaussians;
				
			}
			
			if (halfgaussian){
				if (cursor.getDoublePosition(1) <= location[1] - (cursor.getDoublePosition(0) - location[0])/slope)
					 					value *= 0;
				}
			cursor.get().setReal(value);

		}

	
	}
	

}