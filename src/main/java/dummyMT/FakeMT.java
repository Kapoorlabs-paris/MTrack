package dummyMT;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


import drawandOverlay.AddGaussian;
import fakeMTgenerator.Dummylines;
import fakeMTgenerator.Dummyprops;
import fakeMTgenerator.Indexofline;
import houghandWatershed.Finalfunction;
import ij.IJ;
import ij.ImageJ;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.stats.Normalize;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;
import poissonSimulator.Poissonprocess;
import preProcessing.Kernels;

public class FakeMT {
	
	
	public static int getSuggestedKernelDiameter( final double sigma )
	{
	int size = 3;
    int cutoff = 5; // This number means cutoff is chosen to be cutoff times sigma. 
    if ( sigma > 0 )
	size = Math.max( cutoff, ( 2 * ( int ) ( cutoff * sigma + 0.5 ) + 1 ) );

	return size;
	}
	
	
	public static double[] GetSeeds(RandomAccessibleInterval<FloatType> outimg,

			final Interval range, final int numlines, final double[] sigma, final Random rnd, final double randomslope) throws IncompatibleTypeException {

		
		final int n = outimg.numDimensions();
		
		
		
		
		

			
			double startpos[] = new double[n];
			double endpos[] = new double[n];
			double[] startline = new double[n];
			double[] endline = new double[n];
			double MaxLength = 50 * rnd.nextFloat();

			for (int d = 0; d < range.numDimensions(); ++d) {
				startpos[d] = 150 +  (rnd.nextDouble() * (range.max(d) - range.min(d)) + range.min(d));
				
			}
			
			
			
			
			endpos[0] =  startpos[0] + MaxLength * Math.sqrt(1.0/(1 + randomslope * randomslope) );
			endpos[1] = startpos[1] + randomslope * (endpos[0] - startpos[0]);
			final double[] tmppos = new double[n];

			final double[] minVal = new double[n];
			final double[] maxVal = new double[n];
		

			double slope = (endpos[1] - startpos[1]) / (endpos[0] - startpos[0]);
			double intercept = startpos[1] - slope * startpos[0];
			
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
				startline[1] =  maxVal[1];
				endline[0] =  maxVal[0];
				endline[1] = minVal[1];

			}
			
			
			
			
			final double stepsize =   sigma[0] ;
			double steppos[] = {startline[0], startline[1]};
			double dx = stepsize / Math.sqrt(1 + slope * slope);
			double dy = slope * dx;

			int numGauss = 10;
			int count = 0;
			AddGaussian.addGaussian(outimg, steppos, sigma);

			while (true) {
				steppos[0] += dx;
				steppos[1] += dy;
				
				AddGaussian.addGaussian(outimg, steppos, sigma);
				
				count++;
				
				if (count == numGauss)
					break;
				
				
			
			}
			
		System.out.println(slope + " " + intercept);
 
			double[] line = { startline[0], startline[1], steppos[0], steppos[1], slope, intercept, 0};
			
		double dist =	Distance(startline, steppos);
			if (dist > 10)
			IJ.log( (startline[0] ) + " "  + (startline[1] ) + " "  +  (steppos[0]) + " "   + (steppos[1] ));

			
		return line;

	}
	public static double[] GetSeeds(RandomAccessibleInterval<FloatType> outimg,

			final Interval range, final int numlines, final double[] sigma) throws IncompatibleTypeException {

		
		final int n = outimg.numDimensions();
		final Random rnd = new Random(30);
		final Random rndsec = new Random(-14);
		
		
		

			
			double startpos[] = new double[n];
			double endpos[] = new double[n];
			double[] startline = new double[n];
			double[] endline = new double[n];
			double MaxLength = 50;

			for (int d = 0; d < range.numDimensions(); ++d) {
				startpos[d] = 80 +  (rnd.nextDouble() * (range.max(d) - range.min(d)) + range.min(d));
				
			}
			
			double randomslope = 2.8; //-rndsec.nextDouble();
			
			
			
			endpos[0] =  startpos[0] + MaxLength * Math.sqrt(1.0/(1 + randomslope * randomslope) );
			endpos[1] = startpos[1] + randomslope * (endpos[0] - startpos[0]);
			final double[] tmppos = new double[n];

			final double[] minVal = new double[n];
			final double[] maxVal = new double[n];
		

			double slope = (endpos[1] - startpos[1]) / (endpos[0] - startpos[0]);
			double intercept = startpos[1] - slope * startpos[0];
			
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
				startline[1] =  maxVal[1];
				endline[0] =  maxVal[0];
				endline[1] = minVal[1];

			}
			
			
			
			
			final double stepsize =  0.5 * sigma[0] ;
			double steppos[] = {startline[0], startline[1]};
			double dx = stepsize / Math.sqrt(1 + slope * slope);
			double dy = slope * dx;
			
			while (true) {
				
				
				
				AddGaussian.addGaussian(outimg, steppos, sigma);
				
				if (steppos[0] > endline[0] || steppos[1] > endline[1] && slope >= 0)
					break;
				if (steppos[0] > endline[0] || steppos[1] < endline[1] && slope < 0)
					break;
				steppos[0] += dx;
				steppos[1] += dy;
				
			}
			
			
		
 
			double[] line = { endline[0], endline[1], slope, intercept, 0};
			
			
			

			
		return line;

	}

	
public static double[] Growseeds (RandomAccessibleInterval<FloatType> outimg, double[] oldinfo,double[] original, final int frame, double[] sigma) throws IncompatibleTypeException, IOException{
		
	
		
		final int n = outimg.numDimensions();
		
        double growrate =  2 * Math.sin(0.006 * frame) ;
        
     
        
        double motion = 1;
	
		 
		 
		
       
		
               
				double[] oldpos = {oldinfo[0], oldinfo[1]};
				double slope = oldinfo[2];
				double intercept = oldinfo[3];
				double curvature = oldinfo[4];
				
				final double stepsize =  1 ;
				double steppos[] = {original[0], original[1]};
				double dx = stepsize / Math.sqrt(1 + (slope ) * (slope) );
				double dy = (slope )  * dx ;
				
				
				double[] endpos = new double[n];
				
				endpos[0] = oldpos[0] + motion* growrate;
				endpos[1] = oldpos[1] + motion*growrate;
				while (true) {
					
					
					AddGaussian.addGaussian(outimg, steppos, sigma);
					
					if (steppos[0] > endpos[0] || steppos[1] > endpos[1] && slope >= 0)
						break;
					if (steppos[0] > endpos[0] || steppos[1] < endpos[1] && slope < 0)
						break;
					
					dy+=-0.025*dx*dx;
				  
					
					
					steppos[0] += dx ;
					steppos[1] += dy;
					
					
					
					
						
				}
			
				
				
				double[] returnelement = {steppos[0], steppos[1], slope, intercept, curvature };
				
				
				return returnelement;
				
		 }
	
	
	
	public static double Distance(final double[] cordone, final double[] cordtwo) {

		double distance = 0;

		for (int d = 0; d < cordone.length; ++d) {

			distance += Math.pow((cordone[d] - cordtwo[d]), 2);

		}
		return Math.sqrt(distance);
	}

		
	
	
	public static void main(String args[]) throws IncompatibleTypeException, IOException{
		
		  new ImageJ();
			
			final FinalInterval range = new FinalInterval(512, 512);
			
			final FinalInterval smallrange = new FinalInterval(312, 412);
			
			final int ndims = range.numDimensions();
			final double [] sigma = {2,2};
			final double [] Ci = new double[ndims];
			
			for (int d = 0; d < ndims; ++d)
				Ci[d] = 1.0 / Math.pow(sigma[d],2);
			
			final int numframes = 100;
			final int numlines = 1;

			final int SNR = 10;
			
			
				
			RandomAccessibleInterval<FloatType> lineimage = new ArrayImgFactory<FloatType>().create(range, new FloatType());
			RandomAccessibleInterval<FloatType> noisylines = new ArrayImgFactory<FloatType>().create(range, new FloatType());
			
			

		    double[] lineinfo = GetSeeds(lineimage,  smallrange, numlines, sigma);
			double[] original = {lineinfo[0], lineinfo[1]};
			
			

			FloatType minval = new FloatType(0);
			FloatType maxval = new FloatType(1);
			Normalize.normalize(Views.iterable(lineimage), minval, maxval);
			
			
			Kernels.addBackground(Views.iterable(lineimage), 0.2);
			noisylines = Poissonprocess.poissonProcess(lineimage, SNR);
			ImageJFunctions.show(noisylines);
		
			
	       ArrayList<double[]> Allseedgrow = new ArrayList<double[]>();
	       
	       FileWriter fw;
	       String usefolder = "/Users/varunkapoor/Documents/DummyCurvedSame/";
			File fichierKy = new File(usefolder + "//" +"DummyendCurved17" + ".txt");
			fw = new FileWriter(fichierKy);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("\tFramenumber\tDeltaL\n");
			
			
	       
			for (int frame = 2; frame < numframes; ++frame){
				
				RandomAccessibleInterval<FloatType> noisylinesframe = new ArrayImgFactory<FloatType>().create(range, new FloatType());
				RandomAccessibleInterval<FloatType> lineimageframe = new ArrayImgFactory<FloatType>().create(range, new FloatType());
				
			
					double[] testreturn =	Growseeds(lineimageframe,lineinfo, original, frame, sigma);
			

					
					double length = Distance(new double [] {testreturn[0] , testreturn[1]}, new double[]{ lineinfo[0], lineinfo[1]});
				
					lineinfo = testreturn;
				
				Allseedgrow.add(new double[]{frame, length});
				
				Normalize.normalize(Views.iterable(lineimageframe), minval, maxval);
				
				
				Kernels.addBackground(Views.iterable(lineimageframe), 0.2);
				noisylinesframe = Poissonprocess.poissonProcess(lineimageframe, SNR);
				ImageJFunctions.show(noisylinesframe);
			
			
			
		
			}
			
			
			for (int index = 0; index < Allseedgrow.size(); ++index)
			{
				
				bw.write("\t" + (Allseedgrow.get(index)[0]) + "\t" + (Allseedgrow.get(index)[1]) + "\n");
			}
		
			bw.close();
			fw.close();
		
			
		
	}

}


