package dummyMT;

import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import drawandOverlay.AddGaussian;
import ij.ImageJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.OvalRoi;
import ij.gui.Roi;
import ij.io.Opener;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.KDTree;
import net.imglib2.Point;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPoint;
import net.imglib2.algorithm.region.hypersphere.HyperSphere;
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
	final static int maxiter = 400000;

	public PackMT(final double[] startpos, final double[] endpos, final double slope, final double intercept) {

		this.startpos = startpos;
		this.endpos = endpos;
		this.slope = slope;
		this.intercept = intercept;

	}

	public static ArrayList<PackMT> SimulateRandomMT(final RandomAccessibleInterval<FloatType> source, double[] sigma,
			int numlines, int SNR, ArrayList<PackMT> MTlist, int random) {

		final int n = source.numDimensions();
		double startpos[] = new double[n];
		double endpos[] = new double[n];

		Random rnd = new Random(random);

		for (int d = 0; d < source.numDimensions(); ++d) {
			startpos[d] = rnd.nextDouble() * ((source.max(d) - source.min(d))) + source.min(d);

		}
		double length = MinLength + rnd.nextDouble() * (MaxLength - MinLength);

		double angle = Math.toRadians(rnd.nextDouble() * 360);
		endpos[0] = startpos[0] + length * Math.cos(angle);
		endpos[1] = startpos[1] + length * Math.sin(angle);

		double signedslope = (endpos[1] - startpos[1]) / (endpos[0] - startpos[0]);
		double signedintercept = startpos[1] - signedslope * startpos[0];

		PackMT pass = new PackMT(startpos, endpos, signedslope, signedintercept);

		MTlist.add(pass);

		int iter = 0;
		for (int i = 0; i < numlines; ++i) {

			double startposnew[] = new double[n];
			double endposnew[] = new double[n];
			for (int d = 0; d < source.numDimensions(); ++d)
				startposnew[d] = rnd.nextDouble() * (source.max(d) - source.min(d)) + source.min(d);

			// Look for the end point

			length = MinLength + rnd.nextDouble() * (MaxLength - MinLength);

			angle = Math.toRadians(rnd.nextDouble() * 360);

			endposnew[0] = startposnew[0] + length * Math.cos(angle);
			endposnew[1] = startposnew[1] + length * Math.sin(angle);
			signedslope = (endposnew[1] - startposnew[1]) / (endposnew[0] - startposnew[0]);
			double currentintercept = startposnew[1] - signedslope * startposnew[0];

			// Determine if the lines intersect
			double[] currentlineparam = new double[] { signedslope, currentintercept };

			System.out.println("Drawing Line" + i);
			iter = 0;
			for (PackMT pack : MTlist) {
				iter = 0;

				double[] lineparam = new double[] { pack.slope, pack.intercept };
				double[] posintersect = Intersectionpoint(lineparam, currentlineparam, n);

				if (posintersect != null && posintersect[0] > 0 && posintersect[1] > 0) {
					double xi = posintersect[0];
					double yi = posintersect[1];
					if (pack.slope > 0) {

						do {
							for (int d = 0; d < source.numDimensions(); ++d)
								startposnew[d] = (rnd.nextDouble() * (source.max(d) - source.min(d)) + source.min(d));

							// Look for the end point

							length = MinLength + rnd.nextDouble() * (MaxLength - MinLength);

							angle = Math.toRadians(rnd.nextDouble() * 360);

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
							System.out.println(iter);
							if (iter > maxiter)

								break;
						} while (xi >= 0 && xi <= source.dimension(0) && yi >= 0 && yi <= source.dimension(1));

					} else {

						do {

							for (int d = 0; d < source.numDimensions(); ++d)
								startposnew[d] = (rnd.nextDouble() * (source.max(d) - source.min(d)) + source.min(d));

							// Look for the end point

							length = MinLength + rnd.nextDouble() * (MaxLength - MinLength);

							angle = Math.toRadians(rnd.nextDouble() * 360);

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
							System.out.println(iter);
							if (iter > maxiter)

								break;
						} while (xi >= 0 && xi <= source.dimension(0) && yi >= 0 && yi <= source.dimension(1));
					}

				}

			}

			PackMT pack = new PackMT(startposnew, endposnew, signedslope, signedintercept);

			MTlist.add(pack);
		}
		Draw(source, sigma, MTlist, length);

		return MTlist;
	}

	public static void Draw(RandomAccessibleInterval<FloatType> source, double[] sigma, ArrayList<PackMT> MTlist,
			double length) {

		for (PackMT pack : MTlist) {
			final int n = source.numDimensions();
			System.out.println(pack.startpos[0] + " " + pack.startpos[1] + " " + pack.endpos[0] + " " + pack.endpos[1]
					+ " " + pack.slope);

			double[] startline = new double[n];
			double[] endline = new double[n];

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

				if (pack.endpos[0] > pack.startpos[0])
					steppos[0] += dx;
				else
					steppos[0] -= dx;

				if (pack.endpos[1] > pack.startpos[1])
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

	public static ArrayList<Double> getNearestRois(ArrayList<PackMT> MTList) {
		
	
		
		ArrayList<PackMT> MTListcopy = new ArrayList<PackMT>();
		
			MTListcopy.addAll(MTList);
		RealPoint targetPack = null;
		RealPoint secondtargetPack = null;
		ArrayList<Double> distanceList = new ArrayList<Double>();
		for (int index = 0; index < MTListcopy.size(); ++index) {
			
			PackMT clicked = MTListcopy.get(index);
			
			MTListcopy.remove(clicked);
			
			final List<RealPoint> targetCoords = new ArrayList<RealPoint>(MTListcopy.size());
			final List<util.FlagNode<RealPoint>> targetNodes = new ArrayList<util.FlagNode<RealPoint>>(MTListcopy.size());
		
		for(PackMT pack: MTListcopy) {
			
			targetCoords.add(new RealPoint(pack.startpos));
			targetCoords.add(new RealPoint(pack.endpos));
			targetNodes.add(new util.FlagNode<RealPoint>(new RealPoint(pack.startpos)));
			targetNodes.add(new util.FlagNode<RealPoint>(new RealPoint(pack.endpos)));
			
			
		}
		if(targetCoords.size() > 0) {
			
			final KDTree<util.FlagNode<RealPoint>> Tree = new KDTree<util.FlagNode<RealPoint>>(targetNodes, targetCoords);
			final util.NNFlagsearchKDtree<RealPoint> Search = new util.NNFlagsearchKDtree<RealPoint>(Tree);
			final double[] source = clicked.startpos;
			final double[] secondsource = clicked.endpos;
			
			final RealPoint sourceCoords = new RealPoint(source);
			Search.search(sourceCoords);
			final util.FlagNode<RealPoint> targetNode = Search.getSampler().get();
			
			targetPack = targetNode.getValue();
			double distance = Distance(new double[] {targetPack.getDoublePosition(0), targetPack.getDoublePosition(1)}, clicked.startpos);
			distanceList.add(distance);
			
			
			final RealPoint secondsourceCoords = new RealPoint(secondsource);
			Search.search(secondsourceCoords);
			final util.FlagNode<RealPoint> secondtargetNode = Search.getSampler().get();
			
			secondtargetPack = secondtargetNode.getValue();
			double seconddistance = Distance(new double[] {secondtargetPack.getDoublePosition(0), secondtargetPack.getDoublePosition(1)}, clicked.startpos);
			distanceList.add(seconddistance);
		}
	     
		
		}
		
		Collections.sort(distanceList);
		
		
		return distanceList;
	}
	
	public static double getMeanList(ArrayList<Double> distlist) {
		
		double mean = 0;
		
		for (int index = 0; index < distlist.size(); ++index) {
			
			mean += distlist.get(index);
			
		}
		
		return mean / distlist.size();
		
	}

	public static double getMedianList(ArrayList<Double> distlist) {
		
		
		
		if(distlist.size()%2 == 0)
			return distlist.get(distlist.size() / 2.0);
		else
			return (distlist.get((distlist.size() - 1) / 2 ) + distlist.get((distlist.size() + 1) / 2 )) / 2.0;
		
		
		
	}
	
	public static void main(String args[]) throws IncompatibleTypeException, IOException {

		new ImageJ();

		final FinalInterval range = new FinalInterval(512, 512);
		final double[] sigma = { 2, 2 };

		int SNR = 10;
		int numlines = 5;
		int numsims = 100;
		int min = -1000;
		int[] random = new int[numsims];

		Random randomNum = new Random(min);
		ImageStack prestack = new ImageStack((int) range.dimension(0), (int) range.dimension(1),
				java.awt.image.ColorModel.getRGBdefault());
		ImagePlus resultimp = null;

		for (int i = 0; i < numsims; ++i) {

			random[i] = min + randomNum.nextInt();

			RandomAccessibleInterval<FloatType> source = new ArrayImgFactory<FloatType>().create(range,
					new FloatType());

			ArrayList<PackMT> MTlist = new ArrayList<PackMT>();

			ArrayList<PackMT> MTlistfinal = SimulateRandomMT(source, sigma, numlines, SNR, MTlist, random[i]);
			
			ArrayList<Double> distlist = getNearestRois(MTlistfinal);
			
			try {
				File filedist = new File(
						"/Users/aimachine/Downloads/MTrackStuff/All_SeedsSNR10DIST" + "Run" + i + ".txt");

				FileWriter fwdist = new FileWriter(filedist);
				BufferedWriter bwdist = new BufferedWriter(fwdist);
				bwdist.write("\tminDist \tmaxDist \tMedianDist \tMeanDist  \n");
			
				bwdist.write(distlist.get(0) + " " + distlist.get(distlist.size() - 1) + " " + getMeanList(distlist)  + " " + getMedianList(distlist));
				
				bwdist.close();
				fwdist.close();
			} catch (IOException te) {
			}
			
			
			try {
				File file = new File(
						"/Users/aimachine/Downloads/MTrackStuff/All_SeedsSNR10Number" + numlines + "Run" + i + ".txt");

				FileWriter fw = new FileWriter(file);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write("\tStartX \t StartY \tEndX \t EndY  \n");
				for (PackMT pack : MTlistfinal) {

					bw.write((pack.startpos[0] + " " + pack.startpos[1] + " " + pack.endpos[0] + " " + pack.endpos[1])
							+ "\n");
				}
				bw.close();
				fw.close();
			} catch (IOException te) {
			}

			RandomAccessibleInterval<FloatType> sourcenoise = MakeNoisy(source);

			resultimp = ImageJFunctions.show(sourcenoise);
			prestack.addSlice(resultimp.getImageStack().getProcessor(i));

			resultimp.hide();

		}

		new ImagePlus("Simulation", prestack).show();

	}

}
