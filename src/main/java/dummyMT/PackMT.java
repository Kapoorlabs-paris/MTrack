package dummyMT;

import java.io.IOException;
import java.util.ArrayList;
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
	final static double MaxLength = 50;
	final static double MinLength = 10;
	final static int maxiter = 4000;

	public PackMT(final double[] startpos, final double[] endpos, final double slope, final double intercept) {

		this.startpos = startpos;
		this.endpos = endpos;
		this.slope = slope;
		this.intercept = intercept;

	}

	public static void SimulateRandomMT(final RandomAccessibleInterval<FloatType> source, double[] sigma, int numlines,
			int SNR, ArrayList<PackMT> MTlist) {

		final int n = source.numDimensions();
		double startpos[] = new double[n];
		double endpos[] = new double[n];

		
		//-60, -90, 5648, 50, 5, -100, -10, -200, -40, -400
		Random rnd = new Random(-400);

		for (int d = 0; d < source.numDimensions(); ++d) {
			startpos[d] = rnd.nextDouble() * ((source.max(d) - source.min(d))) + source.min(d);

		}
		double length = MinLength + rnd.nextDouble() * (MaxLength - MinLength);

		double angle = Math.toRadians(rnd.nextDouble()*360);
		endpos[0] = startpos[0] + length * Math.cos(angle) ;
		endpos[1] = startpos[1] + length * Math.sin(angle);

		double signedslope = (endpos[1] - startpos[1]) / (endpos[0] - startpos[0]);
		double signedintercept = startpos[1] - signedslope * startpos[0];

		PackMT pass = new PackMT(startpos, endpos, signedslope, signedintercept);

		MTlist.add(pass);

		for (int i = 0; i < numlines; ++i) {

			
			double startposnew[] = new double[n];
			double endposnew[] = new double[n];
			for (int d = 0; d < source.numDimensions(); ++d)
				startposnew[d] = rnd.nextDouble() * (source.max(d) - source.min(d)) + source.min(d);

			// Look for the end point

			length = MinLength + rnd.nextDouble() * (MaxLength - MinLength);

			angle = Math.toRadians(rnd.nextDouble()*360);

			endposnew[0] = startposnew[0] + length * Math.cos(angle);
			endposnew[1] = startposnew[1] + length * Math.sin(angle);
			signedslope = (endposnew[1] - startposnew[1]) / (endposnew[0] - startposnew[0]);
			double currentintercept = startposnew[1] - signedslope * startposnew[0];

			// Determine if the lines intersect
			double[] currentlineparam = new double[] { signedslope, currentintercept };

			for (PackMT pack : MTlist) {

				double xstart = pack.startpos[0];
				double ystart = pack.startpos[1];
				double xend = pack.endpos[0];
				double yend = pack.endpos[1];

				double[] lineparam = new double[] { pack.slope, pack.intercept };
				double[] posintersect = Intersectionpoint(lineparam, currentlineparam, n);
				int iter = 0;
				if (posintersect != null) {
					double xi = posintersect[0];
					double yi = posintersect[1];

					if (pack.slope > 0) {
						
						do {
							for (int d = 0; d < source.numDimensions(); ++d)
								startposnew[d] = rnd.nextDouble() * (source.max(d) - source.min(d)) + source.min(d);

							// Look for the end point

							length = MinLength + rnd.nextDouble() * (MaxLength - MinLength);

							angle = Math.toRadians(rnd.nextDouble()*360);

							endposnew[0] = startposnew[0] + length * Math.cos(angle);
							endposnew[1] = startposnew[1] + length * Math.sin(angle);
							signedslope = (endposnew[1] - startposnew[1]) / (endposnew[0] - startposnew[0]);
							currentintercept = startposnew[1] - signedslope * startposnew[0];

							// Determine if the lines intersect
							currentlineparam = new double[] { signedslope, currentintercept };
							posintersect = Intersectionpoint(lineparam, currentlineparam, n);
							xi = posintersect[0];
							yi = posintersect[1];
							iter++;
							System.out.println("plus" + iter);
							if (iter > maxiter)
								break;
						} while (xend <= xi || xi <= xstart && (yend <= yi || yi <= ystart));

					} else {

						do {
							
							
							for (int d = 0; d < source.numDimensions(); ++d)
								startposnew[d] =  rnd.nextDouble() * (source.max(d) - source.min(d)) + source.min(d);
							System.out.println("minus" + iter);
							// Look for the end point

							length = MinLength + rnd.nextDouble() * (MaxLength - MinLength);

							angle = Math.toRadians(rnd.nextDouble()*360);

							endposnew[0] = startposnew[0] + length * Math.cos(angle);
							endposnew[1] = startposnew[1] + length * Math.sin(angle);
							signedslope = (endposnew[1] - startposnew[1]) / (endposnew[0] - startposnew[0]);
							currentintercept = startposnew[1] - signedslope * startposnew[0];
							// Determine if the lines intersect
							currentlineparam = new double[] { signedslope, currentintercept };
							posintersect = Intersectionpoint(lineparam, currentlineparam, n);
							xi = posintersect[0];
							yi = posintersect[1];

							iter++;
							if (iter > maxiter)
								break;
						} while (xstart >= xi || xi >= xend && (yend >= yi || yi >= ystart));

					}

				}

			}

			PackMT pack = new PackMT(startposnew, endposnew, signedslope, signedintercept);

			MTlist.add(pack);

		}

		Draw(source, sigma, MTlist, length);

	}

	public static void Draw(RandomAccessibleInterval<FloatType> source, double[] sigma, ArrayList<PackMT> MTlist,
			double length) {

		for (PackMT pack : MTlist) {
			final int n = source.numDimensions();
			System.out.println(pack.startpos[0] + " " + pack.startpos[1] + " " + pack.endpos[0] + " " + pack.endpos[1] + " " + pack.slope);

			double[] startline = new double[n];
			double[] endline = new double[n];

			double intercept = pack.startpos[1] - pack.slope * pack.startpos[0];
			double[] tmppos = new double[n];

			double[] minVal = new double[n];
			double[] maxVal = new double[n];

			for (int d = 0; d < n; ++d) {

				final double locationdiff = pack.startpos[d] - pack.endpos[d];
				final boolean minsearch = locationdiff >= 0;
				tmppos[d] = pack.startpos[d];

				minVal[d] = minsearch ? pack.endpos[d] : pack.startpos[d];
				maxVal[d] = minsearch ? tmppos[d] : pack.endpos[d];

			}

				for (int d = 0; d < n; ++d) {

					startline[d] = (pack.startpos[d]);
					endline[d] = (pack.endpos[d]);
				}


			

			double stepsize = sigma[0];
			double steppos[] = { startline[0], startline[1] };
			double slope = (pack.endpos[1] - pack.startpos[1]) / (pack.endpos[0] - pack.startpos[0]);
			double dx = stepsize / Math.sqrt(1 + slope * slope);
			double dy = Math.abs(slope) * dx;

			AddGaussian.addGaussian(source, steppos, sigma);

			while (true) {
				
				if(pack.endpos[0] > pack.startpos[0] )
				steppos[0] += dx;
				else
				steppos[0] -= dx;	
				
				if(pack.endpos[1] > pack.startpos[1] )
				steppos[1] += dy;
				else
				steppos[1] -= dy;	

				AddGaussian.addGaussian(source, steppos, sigma);

				if (Math.abs(steppos[0] - endline[0]) <= 2 && Math.abs(steppos[1] - endline[1]) <= 2)
					break;

			}

		}

	}

	public static double[] Intersectionpoint(double[] lineparamA, double[] lineparamB, int ndims) {

		double[] pointintersect = new double[ndims];

		double slopeA = lineparamA[0];
		double interceptA = lineparamA[1];

		double slopeB = lineparamB[0];
		double interceptB = lineparamB[1];

		pointintersect[0] = (interceptB - interceptA) / (slopeA - slopeB);
		pointintersect[1] = slopeA * pointintersect[0] + interceptA;

		return pointintersect;

	}

	public static void SimulateCloseMT(final RandomAccessibleInterval<FloatType> source, double[] sigma, int numlines,
			int SNR, FinalInterval range, double distance) {

		final int n = source.numDimensions();
		double startpos[] = new double[n];
		double endpos[] = new double[n];

		double MaxLength = 100;
		double MinLength = 40;
		Random rndthird = new Random();
		Random rnd = new Random();
		Random rndsec = new Random();
		for (int d = 0; d < source.numDimensions(); ++d) {
			startpos[d] = rndthird.nextDouble() * ((source.max(d) - range.max(d) / 2 - range.min(d))) + range.min(d);

		}
		double length = MinLength + rnd.nextDouble() * (MaxLength - MinLength);

		double slope = rndsec.nextDouble();
		endpos[0] = startpos[0] + length * Math.sqrt(1.0 / (1 + slope * slope));
		endpos[1] = startpos[1] + slope * (endpos[0] - startpos[0]);
		PackMT pass = Firstpass(source, range, sigma, startpos, endpos, slope, length);

		for (int i = 1; i < numlines; ++i) {

			Random rndd = new Random();
			Random rnddd = new Random(rndsec.nextInt());
			length = MinLength + rndd.nextDouble() * (MaxLength - MinLength);
			double newslope = (rnddd.nextDouble() + 1);

			for (int d = 0; d < source.numDimensions(); ++d)
				startpos[d] = pass.endpos[d] + distance * Math.sqrt(2);

			double intercept = startpos[1] - newslope * startpos[0];

			endpos[0] = startpos[0] + length * Math.sqrt(1.0 / (1 + newslope * newslope));
			endpos[1] = startpos[1] + newslope * (endpos[0] - startpos[0]);

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

		final FinalInterval range = new FinalInterval(512, 512);
		final int ndims = range.numDimensions();
		final double[] sigma = { 2, 2 };
		int SNR = 10;
		int numlines = 10;
		final double[] Ci = new double[ndims];

		for (int d = 0; d < ndims; ++d)
			Ci[d] = 1.0 / Math.pow(sigma[d], 2);
		RandomAccessibleInterval<FloatType> source = new ArrayImgFactory<FloatType>().create(range, new FloatType());

		double distance = 2.5;
		ArrayList<PackMT> MTlist = new ArrayList<PackMT>();
		SimulateRandomMT(source, sigma, numlines, SNR, MTlist);
		// SimulateCloseMT(source, sigma, numlines, SNR, smallrange, distance);
		source = MakeNoisy(source);
		ImageJFunctions.show(source);

	}

}
