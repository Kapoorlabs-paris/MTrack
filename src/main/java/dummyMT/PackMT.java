package dummyMT;

import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import ch.qos.logback.core.db.dialect.MySQLDialect;
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
import net.imglib2.util.Intervals;
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
	final static double minangle = -360;
	final static double maxangle = 360;
	final static int maxiter = 400000;

	public PackMT(final double[] startpos, final double[] endpos, final double slope, final double intercept) {

		this.startpos = startpos;
		this.endpos = endpos;
		this.slope = slope;
		this.intercept = intercept;

	}

	public static ArrayList<PackMT> SimulateRandomMT(final RandomAccessibleInterval<FloatType> source, double[] sigma,
			int numlines, int SNR, int random) {

		final int n = source.numDimensions();
		
		Random rnd = new Random(random);
		
		ArrayList<PackMT> MTlist = new ArrayList<PackMT>();

		int iter = 0;
		for (int i = 0; i < numlines; ++i) {

		
		


			System.out.println("Drawing Line" + i);
			iter = 0;
			// Determine if the lines intersect
			boolean repeat = false;
			PackMT pack = null;
			
			if(MTlist.size() == 0) {
				
				double startposnew[] = new double[n];
				double endposnew[] = new double[n];
				for (int d = 0; d < source.numDimensions(); ++d)
					startposnew[d] = (rnd.nextFloat()  * (source.max(d) - source.min(d) - 10)  + source.min(d) + 10) ;

				// Look for the end point

				double length = MinLength + rnd.nextDouble() * (MaxLength - MinLength);

				double angle = Math.toRadians(minangle + rnd.nextDouble() * (maxangle - minangle)     )  ;

				endposnew[0] = startposnew[0] + length * Math.cos(angle);
				endposnew[1] = startposnew[1] + length * Math.sin(angle);
				double signedslope = Math.tan(angle);
				double currentintercept = startposnew[1] - signedslope * startposnew[0];
			
				
			  pack = new PackMT(startposnew, endposnew, signedslope, currentintercept);
			  MTlist.add(pack);
				
			}
			
			if(MTlist.size() > 0) {
				double startposnew[] = new double[n];
				double endposnew[] = new double[n];
			do {
				
				for (int d = 0; d < source.numDimensions(); ++d)
					startposnew[d] = (rnd.nextFloat() * (source.max(d) - source.min(d) - 10)   +  source.min(d) + 10) ;

				// Look for the end point

				double length = MinLength + rnd.nextDouble() * (MaxLength - MinLength);

				double angle = Math.toRadians(minangle + rnd.nextDouble() * (maxangle - minangle)     )  ;

				endposnew[0] = startposnew[0] + length * Math.cos(angle);
				endposnew[1] = startposnew[1] + length * Math.sin(angle);
				double signedslope = Math.tan(angle);
				double currentintercept = startposnew[1] - signedslope * startposnew[0];
				
				
				repeat = Check(MTlist, endposnew, startposnew, source, signedslope);

				iter++;
				if(iter >= maxiter - 1) 
				System.out.println("Iteration:  " + iter + " " + repeat);
				
			 
				
				if(!repeat) {
				System.out.println("Iteration:  " + iter + " " + "No intersecting lines");
				 pack = new PackMT(startposnew, endposnew, signedslope, currentintercept);
				}
				
				
				if (iter > maxiter)

					break;

			} while (repeat);

		
			}

		
		
			if(pack!=null)
				MTlist.add(pack);
			
		}


	

		return MTlist;
	}

	public static Boolean Check(ArrayList<PackMT> MTlist, double[] endposnew, double[] startposnew, RandomAccessibleInterval<FloatType> source, double signedslope) {

		double currentintercept = startposnew[1] - signedslope * startposnew[0];

		// Determine if the lines intersect
		double[] currentlineparam = new double[] { signedslope, currentintercept };

		int truecount = 0;
		Boolean repeat = true;

		for (PackMT pack : MTlist) {

			double[] lineparam = new double[] { pack.slope, pack.intercept };

	
			
			double bigx = pack.startpos[0] > pack.endpos[0] ? pack.startpos[0]:pack.endpos[0];
			double smallx = pack.startpos[0] > pack.endpos[0] ? pack.endpos[0]:pack.startpos[0];
			
			double bigy = pack.startpos[1] > pack.endpos[1] ? pack.startpos[1]:pack.endpos[1];
			double smally = pack.startpos[1] > pack.endpos[1] ? pack.endpos[1]:pack.startpos[1];
			
			double[] posintersect = Intersectionpoint(lineparam, currentlineparam, endposnew.length);

			if (posintersect[0]>= smallx && posintersect[0] <= bigx && posintersect[1] >= smally
					&& posintersect[1] <= bigy ) {
				truecount++;
			}
			
			
			
			
		}

		
		if (truecount >= 1)
			repeat = true;
		else
			repeat = false;

		return repeat;

	}

	public static void Draw(RandomAccessibleInterval<FloatType> source, double[] sigma, ArrayList<PackMT> MTlist) {

		for (PackMT pack : MTlist) {
			final int n = source.numDimensions();

			double[] startline = new double[n];
			double[] endline = new double[n];

		
		

			for (int d = 0; d < n; ++d) {

				startline[d] = (pack.startpos[d]);
				endline[d] = (pack.endpos[d]);
			}

			double stepsize = sigma[0];
			double steppos[] = { startline[0], startline[1] };
			double slope = (endline[1] - startline[1]) / (endline[0] - startline[0]);
			double dx = stepsize / Math.sqrt(1 + slope * slope);
			double dy = Math.abs(slope) * dx;

			AddGaussian.addGaussian(source, steppos, sigma);
			while (true) {

				if (pack.endpos[0] >= pack.startpos[0])
					steppos[0] += dx;
				else
					steppos[0] -= dx;

				if (pack.endpos[1] >= pack.startpos[1])
					steppos[1] += dy;
				else
					steppos[1] -= dy;

				AddGaussian.addGaussian(source, steppos, sigma);
				if (Distance(endline, steppos) <= 2 *sigma[0])
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

	public static RandomAccessibleInterval<FloatType> MakeNoisy(final RandomAccessibleInterval<FloatType> source,
			int SNR) {
		FloatType minval = new FloatType(0);
		FloatType maxval = new FloatType(1);
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
	
	public static double DistanceLine(double[] cordone, double slope, double intercept, double[] startpos, double[] endpos) {

		

		boolean outside = false;
		double distance = Math.abs(cordone[1] - slope * cordone[0] - intercept) / (Math.sqrt(1.0 + slope *slope));

		double xintersect = (cordone[0] + slope * (cordone[1] -intercept)) / (1 + slope * slope);
		double yintersect = slope * xintersect + intercept;
		
		double bigx = startpos[0] > endpos[0] ? startpos[0]:endpos[0];
		double smallx = startpos[0] > endpos[0] ? endpos[0]:startpos[0];
		
		double bigy = startpos[1] > endpos[1] ? startpos[1]:endpos[1];
		double smally = startpos[1] > endpos[1] ? endpos[1]:startpos[1];
		
		
		if (xintersect >= smallx - 5 && xintersect <= bigx + 5 && yintersect >= smally - 5 && yintersect <= bigy + 5)
			outside = false;
		else
			outside = true;
		

		double returndistance;
		if (!outside)
                  returndistance = distance;		
		else
			returndistance = Double.MAX_VALUE;
			
		return returndistance;
	}

	public static ArrayList<Double> getNearestRois(ArrayList<PackMT> MTList) {

		ArrayList<PackMT> MTListcopy = new ArrayList<PackMT>();

		MTListcopy.addAll(MTList);
		PackMT targetPack = null;
		PackMT secondtargetPack = null;
		ArrayList<Double> distanceList = new ArrayList<Double>();
		for (int index = 0; index < MTListcopy.size(); ++index) {

			PackMT clicked = MTListcopy.get(index);

			MTListcopy.remove(clicked);

			final List<RealPoint> targetCoords = new ArrayList<RealPoint>(MTListcopy.size());
			final List<util.FlagNode<PackMT>> targetNodes = new ArrayList<util.FlagNode<PackMT>>(
					MTListcopy.size());

			for (PackMT pack : MTListcopy) {

				targetCoords.add(new RealPoint(midpoint(pack.startpos, pack.endpos)));
				targetNodes.add(new util.FlagNode<PackMT>(pack));

			}
			if (targetCoords.size() > 0) {

				final KDTree<util.FlagNode<PackMT>> Tree = new KDTree<util.FlagNode<PackMT>>(targetNodes,
						targetCoords);
				final util.NNFlagsearchKDtree<PackMT> Search = new util.NNFlagsearchKDtree<PackMT>(Tree);
				final double[] source = clicked.startpos;
				final double[] secondsource = clicked.endpos;

				final RealPoint sourceCoords = new RealPoint(source);
				Search.search(sourceCoords);
				final util.FlagNode<PackMT> targetNode = Search.getSampler().get();

				targetPack = targetNode.getValue();

				final RealPoint secondsourceCoords = new RealPoint(secondsource);
				Search.search(secondsourceCoords);
				final util.FlagNode<PackMT> secondtargetNode = Search.getSampler().get();

				secondtargetPack = secondtargetNode.getValue();
				
				double slopetarget =  targetPack.slope;
				double intercepttarget = targetPack.intercept; 
				double[] startpos = targetPack.startpos;
				double[] endpos = targetPack.endpos;
				
				double distance = Distance(
						targetPack.startpos,
						source);
				
				double distancesec = Distance(
						targetPack.endpos,
						source);
				double minpointdist = Math.min(distance, distancesec);
				
				double distanceline = DistanceLine(source , slopetarget, intercepttarget, startpos, endpos);
				
				double minpointfinaldist = Math.min(minpointdist, distanceline);
				
				double seconddistance = Distance(
						secondtargetPack.startpos,
						secondsource);
				double seconddistancesec = Distance(
						secondtargetPack.endpos,
						secondsource);
				double minpointdistsec = Math.min(seconddistance, seconddistancesec);
				
				double seconddistanceline = DistanceLine(secondsource , slopetarget, intercepttarget, startpos, endpos);
				
				
				double minlinedist = Math.min(minpointdistsec, seconddistanceline);
				
				//System.out.println(minpointdist + " " + minlinedist + " " + slopetarget);
			
				
				distanceList.add(Math.min(minlinedist, minpointfinaldist));

				
			
			}
			MTListcopy.add(clicked);

		}

		Collections.sort(distanceList);

		return distanceList;
	}
	
	public static double[] midpoint(double[] cordone, double[] cordtwo) {
		
		double [] midpoint = new double[cordone.length];
		
		for (int d = 0; d < cordone.length; ++d) {
			
			midpoint[d] = ( cordone[d] + cordtwo[d] )/ 2;
			
		}
		
		return midpoint;
	}

	public static double getMeanList(ArrayList<Double> distlist) {

		double mean = 0;

		for (int index = 0; index < distlist.size(); ++index) {

			mean += distlist.get(index);

		}

		return mean / distlist.size();

	}
	public static double getSTDList(ArrayList<Double> distlist) {

		double mean = getMeanList(distlist);
		double stdev = 0;

		
		
		for (int index = 0; index < distlist.size(); ++index) {
		
			stdev+= (distlist.get(index) - mean) * (distlist.get(index) - mean);
			
		}

		return Math.sqrt(stdev / distlist.size() );

	}
	public static double getSTDminList(ArrayList<Double> distlist) {

		double min = distlist.get(0);
		double stdev = 0;

		
		
		for (int index = 0; index < distlist.size(); ++index) {
		
			stdev+= (distlist.get(index) - min) * (distlist.get(index) - min);
			
		}

		return Math.sqrt(stdev / distlist.size() );

	}
	
	public static double getMedianList(ArrayList<Double> distlist) {

		if (distlist.size() % 2 == 0)
			return distlist.get(distlist.size() / 2);
		else
			return (distlist.get((distlist.size() - 1) / 2) + distlist.get((distlist.size() + 1) / 2)) / 2.0;

	}

	public static void main(String args[]) throws IncompatibleTypeException, IOException {

		new ImageJ();

		final FinalInterval range = new FinalInterval(512, 512);
		final double[] sigma = { 2, 2 };

		int SNR = 10;
		int numlines = 65;
		int numsims = 100;
		//3893
		int min = 1000;
		int[] random = new int[numsims];

		Random randomNum = new Random(min);
		ImageStack prestack = new ImageStack((int) range.dimension(0), (int) range.dimension(1),
				java.awt.image.ColorModel.getRGBdefault());
		ImagePlus resultimp = null;

		for (int i = 0; i < numsims; ++i) {

			random[i] = randomNum.nextInt();

			System.out.println("Simulation number" + " " + i);
			RandomAccessibleInterval<FloatType> source = new ArrayImgFactory<FloatType>().create(range,
					new FloatType());

			

			ArrayList<PackMT> MTlistfinal = SimulateRandomMT(source, sigma, numlines, SNR, random[i]);
			Draw(source, sigma, MTlistfinal);
			ArrayList<Double> distlist = getNearestRois(MTlistfinal);

			try {
				File filedist = new File(
						"/Users/aimachine/Documents/MTrack+Mser/New/Density65/All_SeedsSNR10DIST" + numlines + "Run" + i + ".txt");

				FileWriter fwdist = new FileWriter(filedist);
				BufferedWriter bwdist = new BufferedWriter(fwdist);
				bwdist.write("\tminDist \tSTDDist \tMeanDist \tMedianDist \tSTDminDist \n");

				bwdist.write(distlist.get(0) + " " + getSTDList(distlist) + " " + getMeanList(distlist)
						+ " " + getMedianList(distlist) + " " + getSTDminList(distlist));

				bwdist.close();
				fwdist.close();
			} catch (IOException te) {
			}

			try {
				File file = new File(
						"/Users/aimachine/Documents/MTrack+Mser/New/Density65/All_SeedsSNR10Number" + numlines + "Run" + i + ".txt");

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
			
			RandomAccessibleInterval<FloatType> sourcenoise = MakeNoisy(source, SNR);

			resultimp = ImageJFunctions.show(sourcenoise);
			prestack.addSlice(resultimp.getImageStack().getProcessor(i));

			resultimp.hide();
			}

		new ImagePlus("Simulation", prestack).show();

	}

}
