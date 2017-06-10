package dummyMT;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import com.sun.tools.javac.util.Pair;

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
import preProcessing.GetLocalmaxmin.IntensityType;

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

			final Interval range, final int numlines, final double[] sigma) throws IncompatibleTypeException {

		
		final int n = outimg.numDimensions();
		final Random rnd = new Random(150);
		final Random rndsec = new Random(80);
		
		
		

			
			double startpos[] = new double[n];
			double endpos[] = new double[n];
			double[] startline = new double[n];
			double[] endline = new double[n];
			double MaxLength = 50;

			for (int d = 0; d < range.numDimensions(); ++d) {
				startpos[d] = 150 +  (rnd.nextDouble() * (range.max(d) - range.min(d)) + range.min(d));
				
			}
			
			double randomslope =  -rndsec.nextDouble();
			
			
			
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
			AddGaussian.addGaussian(outimg, steppos, sigma);
			while (true) {
				
				
				
				if (steppos[0] > endline[0] || steppos[1] > endline[1] && slope >= 0)
					break;
				if (steppos[0] > endline[0] || steppos[1] < endline[1] && slope < 0)
					break;
				

				steppos[0] += dx;
				steppos[1] += dy;
				AddGaussian.addGaussian(outimg, steppos, sigma);
			}
			
			
		System.out.println(slope + " " + intercept);
 
			double[] line = { steppos[0], steppos[1], slope, intercept, 0};
			
			
			
			IJ.log("StartX: " + (startline[0] ) + " " + "StartY: " + (startline[1] ) + " " + "EndX: " +  (steppos[0]) + " " + "EndY: "  + (steppos[1] ));

			
		return line;

	}
	
	public static double[] Growseeds (RandomAccessibleInterval<FloatType> outimg, double[] oldinfo,double[] original, final int frame,
			double[] sigma, ArrayList<double[]> Allseedgrow) throws IncompatibleTypeException, IOException{
		
	
		final int n = outimg.numDimensions();
		
        double growrate =  2* Math.sin(0.2 * frame) ;
        if (frame == 0)
        	growrate+=5;
        double motion = 1;
	
               
				double[] oldpos = {oldinfo[0], oldinfo[1]};
				double slope = oldinfo[2];
				double intercept = oldinfo[3];
				double curvature = oldinfo[4];
				
				final double stepsize =  1 ;
				
				double dx = stepsize / Math.sqrt(1 + (slope + 2 * curvature * oldpos[0]) * (slope + 2 * curvature * oldpos[0]) );
				double dy = (slope + 2 * curvature * oldpos[0])  * dx ;
				double steppos[] = {original[0], original[1]};
				
				double[] endpos = new double[n];
				
				endpos[0] = oldpos[0] + motion* growrate;
				AddGaussian.addGaussian(outimg, steppos, sigma);
				while (true) {
					
					 
					if (steppos[0] > endpos[0] || steppos[1] > endpos[1] && slope >= 0)
						break;
					if (steppos[0] > endpos[0] || steppos[1] < endpos[1] && slope < 0)
						break;
					
				 
					
					 dy += 0.005*dx;
						
						steppos[0] += dx ;
						steppos[1] += dy;
					
					AddGaussian.addGaussian(outimg, steppos, sigma);
					
					
						
				}
			
				
				
				double[] returnelement = {steppos[0], steppos[1], slope, intercept, curvature };
				
				double length = Distance(new double [] {steppos[0] , steppos[1]}, oldpos);
				
				
				Allseedgrow.add(new double[]{frame, steppos[0], steppos[1], length});
				
				
				
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
			
			final FinalInterval smallrange = new FinalInterval(124, 124);
			
			final int ndims = range.numDimensions();
			final double [] sigma = {2,2};
			final double [] Ci = new double[ndims];
			
			for (int d = 0; d < ndims; ++d)
				Ci[d] = 1.0 / Math.pow(sigma[d],2);
			
			final int numframes = 200;
			final int numlines = 1;

			
			
				
			RandomAccessibleInterval<FloatType> lineimage = new ArrayImgFactory<FloatType>().create(range, new FloatType());
			RandomAccessibleInterval<FloatType> noisylines = new ArrayImgFactory<FloatType>().create(range, new FloatType());
			
			

		  
			   
			
			  double[]  lineinfo = GetSeeds(lineimage, smallrange, numlines, sigma);
			  double[] original = {lineinfo[0], lineinfo[1]};

			FloatType minval = new FloatType(0);
			FloatType maxval = new FloatType(1);
			Normalize.normalize(Views.iterable(lineimage), minval, maxval);
			
			
			Kernels.addBackground(Views.iterable(lineimage), 0.2);
			noisylines = Poissonprocess.poissonProcess(lineimage, 30);
			ImageJFunctions.show(noisylines);
			
	       ArrayList<double[]> Allseedgrow = new ArrayList<double[]>();
	       
	       FileWriter fw;
	       String usefolder = "/Users/varunkapoor/Documents/JoeVisit";
			File fichierKy = new File(usefolder + "//" +"GrowingendFakeSNR30" + ".txt");
			fw = new FileWriter(fichierKy);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("\tFramenumber\tCurrentX\tCurrentY\tLengthPerframe\n");
			
			
	       
			for (int frame = 0; frame < numframes; ++frame){
				
				RandomAccessibleInterval<FloatType> noisylinesframe = new ArrayImgFactory<FloatType>().create(range, new FloatType());
				RandomAccessibleInterval<FloatType> lineimageframe = new ArrayImgFactory<FloatType>().create(range, new FloatType());
				double[] testreturn = lineinfo;

				
				testreturn =	Growseeds(lineimageframe,lineinfo, original, frame, sigma, Allseedgrow);
			

					
				
					
					lineinfo = testreturn;
				
				
				
				Normalize.normalize(Views.iterable(lineimageframe), minval, maxval);
				
				
				Kernels.addBackground(Views.iterable(lineimageframe), 0.2);
				noisylinesframe = Poissonprocess.poissonProcess(lineimageframe, 30);
				ImageJFunctions.show(noisylinesframe);
			
			
		
			}
			
			
			for (int index = 1; index < Allseedgrow.size(); ++index)
			{
				bw.write("\t" + (Allseedgrow.get(index)[0] + 2) + "\t" + (Allseedgrow.get(index)[1]) + "\t" + (Allseedgrow.get(index)[2])
						+ "\t" + (Allseedgrow.get(index)[3])	+ "\n");
			}
		
			bw.close();
			fw.close();
			
		
			
		}

		
			
		
	

	

}
