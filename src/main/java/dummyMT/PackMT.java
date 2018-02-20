package dummyMT;

import java.io.IOException;
import java.util.Random;

import drawandOverlay.AddGaussian;
import ij.ImageJ;
import ij.ImagePlus;
import ij.io.Opener;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.stats.Normalize;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;
import poissonSimulator.Poissonprocess;
import preProcessing.Kernels;

public class PackMT {

	final double[] startpos;
	final double[] endpos;
	final double slope;
	final double intercept;

	public PackMT(final double[] startpos, final double[] endpos, final double slope, final double intercept) {

		this.startpos = startpos;
		this.endpos = endpos;
		this.slope = slope;
		this.intercept = intercept;

	}

	public static void SimulateCloseMT(final RandomAccessibleInterval<FloatType> source, double[] sigma, int numlines,
			int SNR, FinalInterval range) {

		final int n = source.numDimensions();
		double startpos[] = new double[n];
		double endpos[] = new double[n];

		double MaxLength = 100;
		double MinLength = 10;
		Random rndthird = new Random();
		Random rnd = new Random();
		Random rndsec = new Random();
		for (int d = 0; d < source.numDimensions(); ++d) {
			startpos[d] = rndthird.nextDouble() * ((source.max(d) - range.max(d) / 2 - range.min(d))) + range.min(d);

		}
		double length = MinLength + rnd.nextDouble() * (MaxLength - MinLength);

		double slope =  rndsec.nextDouble();
		endpos[0] = startpos[0] + length * Math.sqrt(1.0 / (1 + slope * slope));
		endpos[1] = startpos[1] + slope * (endpos[0] - startpos[0]);
		PackMT pass = Firstpass(source, range, sigma, startpos, endpos, slope, length);

		for (int i = 1; i < numlines; ++i) {

			Random rndd = new Random();
			Random rnddd = new Random();
			length = MinLength + rndd.nextDouble() * (MaxLength - MinLength);
			double newslope = Math.tan(Math.toRadians(Math.toDegrees(Math.atan(pass.slope)) + rnddd.nextDouble() + 50));

			for (int d = 0; d < source.numDimensions(); ++d)
				startpos[d] = pass.startpos[d] + Math.signum(slope) * 20;

			if(Distance(startpos, pass.startpos) < 5) {
				for (int d = 0; d < source.numDimensions(); ++d)
					startpos[d] = startpos[d] + Math.signum(slope) * 10;
				
			}
			
			double intercept = startpos[1] - newslope * startpos[0];

			endpos[0] = startpos[0] + length * Math.sqrt(1.0 / (1 + newslope * newslope));
			endpos[1] = startpos[1] + newslope * (endpos[0] - startpos[0]);

			if(Distance(endpos, pass.endpos) < 5) {
				for (int d = 0; d < source.numDimensions(); ++d)
					endpos[d] = endpos[d] + Math.signum(slope) * 5;
				
			}
			
			PackMT Npass = Firstpass(source, range, sigma, startpos, endpos, newslope, length);
			pass = Npass;

		}

	}

	public static PackMT Firstpass(RandomAccessibleInterval<FloatType> source, FinalInterval range, double[] sigma,
			double startpos[], double endpos[], double slope, double length) {

		final int n = source.numDimensions();

		double[] startline = new double[n];
		double[] endline = new double[n];

		
	
		double intercept = startpos[1] - slope * startpos[0];
		double[] tmppos = new double[n];

		double[] minVal = new double[n];
		double[] maxVal = new double[n];

		for (int d = 0; d < n; ++d) {

			final double locationdiff = startpos[d] - endpos[d];
			final boolean minsearch = locationdiff >= 0;
			tmppos[d] = startpos[d];

			minVal[d] = minsearch ? endpos[d] : startpos[d];
			maxVal[d] = minsearch ? tmppos[d] : endpos[d];

		}

		if (slope >= 0) {
			for (int d = 0; d < n; ++d) {

				startline[d] = (minVal[d]);
				endline[d] = (maxVal[d]);
			}

		}

		if (slope < 0) {

			startline[0] = minVal[0];
			startline[1] = maxVal[1];
			endline[0] = maxVal[0];
			endline[1] = minVal[1];

		}

		double stepsize = sigma[0];
		double steppos[] = { startline[0], startline[1] };
		double dx = stepsize / Math.sqrt(1 + slope * slope);
		double dy = slope * dx;

		AddGaussian.addGaussian(source, steppos, sigma);

		while (true) {
			steppos[0] += dx;
			steppos[1] += dy;

			AddGaussian.addGaussian(source, steppos, sigma);

			double dist = Distance(startline, steppos);

			if (dist >= length)
				break;

		}

		PackMT pass = new PackMT(startpos, endpos, slope, intercept);

		return pass;

	}

	public static RandomAccessibleInterval<FloatType> MakeNoisy(final RandomAccessibleInterval<FloatType> source) {
		FloatType minval = new FloatType(0);
		FloatType maxval = new FloatType(1);
		int SNR = 10;
		Normalize.normalize(Views.iterable(source), minval, maxval);
		Kernels.addBackground(Views.iterable(source), 0.2);
		RandomAccessibleInterval<FloatType> noisylines = Poissonprocess.poissonProcess(source, SNR);
		return noisylines;
	}

	public static double Distance(double[] cordone, double[] cordtwo) {

		int ndims = cordone.length;

		double distance = 0;

		for (int i = 0; i < ndims; ++i) {

			distance += (cordone[i] - cordtwo[i]) * (cordone[i] - cordtwo[i]);
		}

		return Math.sqrt(distance);
	}

	public static void main(String args[]) throws IncompatibleTypeException, IOException {

		new ImageJ();

		final FinalInterval range = new FinalInterval(1024, 1024);
		final FinalInterval smallrange = new FinalInterval(512, 512);
		final int ndims = range.numDimensions();
		final double[] sigma = { 2, 2 };
		int SNR = 10;
		int numlines = 4;
		final double[] Ci = new double[ndims];

		for (int d = 0; d < ndims; ++d)
			Ci[d] = 1.0 / Math.pow(sigma[d], 2);
		ImagePlus impB = new Opener().openImage("/Users/aimachine/Downloads/Neubias/SeedDist.tif");
		RandomAccessibleInterval<FloatType> source = new ArrayImgFactory<FloatType>().create(range, new FloatType());
		// ImageJFunctions.convertFloat(impB);

		SimulateCloseMT(source, sigma, numlines, SNR, smallrange);
		source = MakeNoisy(source);
		ImageJFunctions.show(source);
		// SimulateCloseMT(source, sigma);

	}

}
