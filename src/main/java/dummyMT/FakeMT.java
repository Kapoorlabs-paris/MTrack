package dummyMT;

import java.util.ArrayList;
import java.util.Random;

import com.sun.tools.javac.util.Pair;

import drawandOverlay.AddGaussian;
import fakeMTgenerator.Dummylines;
import fakeMTgenerator.Dummyprops;
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
	
	
	public static void GetSeeds(RandomAccessibleInterval<FloatType> outimg, ArrayList<double[]> startseeds,
			ArrayList<double[]> endseeds,

			final Interval range, final int numlines, final double[] sigma) throws IncompatibleTypeException {

		
		final int n = outimg.numDimensions();
		final Random rnd = new Random(150);
		final Random rndsec = new Random(80);
		
		
		for (int index = 0; index < numlines; ++index) {

			
			double startpos[] = new double[n];
			double endpos[] = new double[n];
			double[] startline = new double[n];
			double[] endline = new double[n];
			double MaxLength = 50;

			for (int d = 0; d < range.numDimensions(); ++d) {
				startpos[d] = 150 +  (rnd.nextDouble() * (range.max(d) - range.min(d)) + range.min(d));
				
			}
			
			double randomslope =  -rndsec.nextDouble();
			
			if (index >= numlines / 2)
				randomslope = rndsec.nextDouble();
			
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
			
			
			
			
			final double stepsize =  2* sigma[0] ;
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
			
			
		

			final double[] startinfo = { startline[0], startline[1], slope, intercept };
			final double[] endinfo = { endline[0], endline[1], slope, intercept };
			startseeds.add(startinfo);
			endseeds.add(endinfo);
			
			
			
			IJ.log("StartX: " + (startline[0] ) + " " + "StartY: " + (startline[1] ) + " " + "EndX: " +  (steppos[0]) + " " + "EndY: "  + (steppos[1] ));

			
		}

	}
	
	public static Pair<Pair<ArrayList<Dummyprops>, ArrayList<Dummyprops>>, Pair<ArrayList<double[]>, ArrayList<double[]>>> Growseeds (RandomAccessibleInterval<FloatType> outimg, 
			ArrayList<double[]> startseeds, ArrayList<double[]> endseeds, final int frame, double[] sigma) throws IncompatibleTypeException{
		
	
		
		final int n = outimg.numDimensions();
		
        double growrate = 22* Math.sin(0.2 * frame) ;

		ArrayList<Dummyprops> newstlist = new ArrayList<Dummyprops>();
		ArrayList<Dummyprops> newendlist = new ArrayList<Dummyprops>();
		 for (int index = 0; index < startseeds.size(); ++index){
			 double[] startpos = new double[n];
			 double[] endpos = new double[n];
			 double[] oldpos = new double[n];
			 double slope = startseeds.get(index)[n];
			 double intercept = startseeds.get(index)[n + 1];
			 
			 
			 for (int d = 0; d < n; ++d){
				 
				 endpos[d] = startseeds.get(index)[d];
				 
			 }
			 
	             oldpos[0] = startseeds.get(index)[0];
	             oldpos[1] = slope * oldpos[0] + intercept;
	             
				 startpos[0] = startseeds.get(index)[0] -  3.5* Math.abs((growrate)) - 15.5 ;
				 startpos[1] = slope * startpos[0] + intercept;
				
			 
				 Dummyprops newst = new Dummyprops(frame, oldpos, startpos, slope, intercept);
				 newstlist.add(newst);
			
			 
					final double stepsize =  1 ;
					double steppos[] = {startpos[0], startpos[1]};
					double dx = stepsize / Math.sqrt(1 + slope * slope);
					double dy = slope * dx;
					
					while (true) {
						
						AddGaussian.addGaussian(outimg, steppos, sigma);
						
						if (steppos[0] > endpos[0] || steppos[1] > endpos[1] && slope >= 0)
							break;
						if (steppos[0] > endpos[0] || steppos[1] < endpos[1] && slope < 0)
							break;
						steppos[0] += dx;
						steppos[1] += dy;
						
					}
					
				
			 
		 }
		 
		
       
		 for (int index = 0; index < endseeds.size(); ++index){
			 double[] startpos = new double[n];
			 double[] endpos = new double[n];
			 double[] oldpos = new double[n];
			 double slope = endseeds.get(index)[n];
			 double intercept = endseeds.get(index)[n + 1];
			 
			 for (int d = 0; d < n; ++d){
				 
				 startpos[d] = endseeds.get(index)[d];
			 }
			 
			    oldpos[0] = endseeds.get(index)[0];
			    oldpos[1] = slope * oldpos[0] + intercept;
			 
				endpos[0] = endseeds.get(index)[0] +  5.5* Math.abs((growrate)) ;
				endpos[1] = slope * endpos[0] + intercept;
				
				final double[] en = {endpos[0], endpos[1], slope, intercept};
				
				
				final double stepsize =  1 ;
				double steppos[] = {startpos[0], startpos[1]};
				double dx = Math.round(stepsize / Math.sqrt(1 + slope * slope));
				double dy = Math.round(slope * dx) ;
				while (true) {
					
					
					AddGaussian.addGaussian(outimg, steppos, sigma);
					
					if (steppos[0] > endpos[0] || steppos[1] > endpos[1] && slope >= 0)
						break;
					if (steppos[0] > endpos[0] || steppos[1] < endpos[1] && slope < 0)
						break;
					steppos[0] += dx ;
					steppos[1] += dy;
					
					dy += 0.02 * dx* dx  ;
					
					
						
				}
				Dummyprops newend = new Dummyprops(frame, oldpos, steppos, slope, intercept);
				newendlist.add(newend);
			 
		 }
	
	
		 Pair<ArrayList<Dummyprops>, ArrayList<Dummyprops>> pair = new  Pair<ArrayList<Dummyprops>, ArrayList<Dummyprops>> (newstlist, newendlist);
	
		 Pair<ArrayList<double[]>, ArrayList<double[]>> pairdoub = new  Pair<ArrayList<double[]>, ArrayList<double[]>> (startseeds, endseeds);
		 Pair<Pair<ArrayList<Dummyprops>, ArrayList<Dummyprops>>, Pair<ArrayList<double[]>, ArrayList<double[]>>> totalpair = new
				 Pair<Pair<ArrayList<Dummyprops>, ArrayList<Dummyprops>>, Pair<ArrayList<double[]>, ArrayList<double[]>>> (pair, pairdoub);
	return totalpair;
		
	}
	
	
	public static double Distance(final double[] cordone, final double[] cordtwo) {

		double distance = 0;

		for (int d = 0; d < cordone.length; ++d) {

			distance += Math.pow((cordone[d] - cordtwo[d]), 2);

		}
		return Math.sqrt(distance);
	}

		
	
	
	public static void main(String args[]) throws IncompatibleTypeException{
		
		  new ImageJ();
			
			final FinalInterval range = new FinalInterval(1024, 1024);
			
			final FinalInterval smallrange = new FinalInterval(812, 812);
			
			final int ndims = range.numDimensions();
			final double [] sigma = {2,2};
			final double [] Ci = new double[ndims];
			
			for (int d = 0; d < ndims; ++d)
				Ci[d] = 1.0 / Math.pow(sigma[d],2);
			
			final int numframes = 50;
			final int numlines = 20;

			
			
				
			RandomAccessibleInterval<FloatType> lineimage = new ArrayImgFactory<FloatType>().create(range, new FloatType());
			RandomAccessibleInterval<FloatType> noisylines = new ArrayImgFactory<FloatType>().create(range, new FloatType());
			
			
			ArrayList<double[]> startseeds = new ArrayList<double[]>();
			ArrayList<double[]> endseeds = new ArrayList<double[]>();
		 GetSeeds(lineimage, startseeds, endseeds, smallrange, numlines, sigma);
			
			
			

			FloatType minval = new FloatType(0);
			FloatType maxval = new FloatType(1);
			Normalize.normalize(Views.iterable(lineimage), minval, maxval);
			
			ImageJFunctions.show(lineimage);
			/*
			Kernels.addBackground(Views.iterable(lineimage), 0.2);
			noisylines = Poissonprocess.poissonProcess(lineimage, 10);
			ImageJFunctions.show(noisylines);
			ArrayList<double[]> startseedscopy =  new ArrayList<double[]>();
			ArrayList<double[]> endseedscopy = new ArrayList<double[]>();
			for (int index = 0; index < startseeds.size(); ++index){
				
				startseedscopy.add(index, startseeds.get(index));
				
			}
	       for (int index = 0; index < endseeds.size(); ++index){
				
				endseedscopy.add(index, endseeds.get(index));
				
			}
			ArrayList<Indexofline> linestlist = new ArrayList<Indexofline>();
			ArrayList<Indexofline> lineendlist = new ArrayList<Indexofline>();
			for (int frame = 0; frame < numframes; ++frame){
				
				RandomAccessibleInterval<FloatType> noisylinesframe = new ArrayImgFactory<FloatType>().create(range, new FloatType());
				RandomAccessibleInterval<FloatType> lineimageframe = new ArrayImgFactory<FloatType>().create(range, new FloatType());
				
				Pair<Pair<ArrayList<Dummyprops>, ArrayList<Dummyprops>>, Pair<ArrayList<double[]>, ArrayList<double[]>>>  pair	 = 
						Dummylines.Growseeds(lineimageframe, startseeds, endseeds, frame, sigma);
			
				
				
				
				
				for (int index = 0; index < pair.fst.fst.size(); ++index){
					double[] prevst = new double[ndims];
					double[] nextst = new double[ndims];
					 
					for (int d = 0; d < ndims; ++d){
						
						prevst[d] = pair.fst.fst.get(index).originalpoint[d];
						
						nextst[d] = pair.fst.fst.get(index).newpoint[d];
						
					}
					Indexofline linest = new Indexofline(index, frame, prevst, nextst);
					linestlist.add(linest);	

			
				}
				 
				for (int index = 0; index < pair.fst.fst.size(); ++index){
					
					double[] preven = new double[ndims];
					double[] nexten = new double[ndims];
					
					  
					for (int d = 0; d < ndims; ++d){
						
						preven[d] = pair.fst.snd.get(index).originalpoint[d];
						
						nexten[d] = pair.fst.snd.get(index).newpoint[d];
					}
					
					Indexofline lineend = new Indexofline(index, frame,preven, nexten);
					lineendlist.add(lineend);	
	             	
					
					

			
				}
		    

		   
			
			Normalize.normalize(Views.iterable(lineimageframe), minval, maxval);
			Kernels.addBackground(Views.iterable(lineimageframe), 0.2);
			noisylinesframe = Poissonprocess.poissonProcess(lineimageframe, 10);
			
		
			ImageJFunctions.show(noisylinesframe);
			
			
			
			
			
			
			
			
		
			}
			
			
			
			double endlength = 0;
			double[] olden = new double[ndims];
			double[] newen = new double[ndims];
			 FileWriter writerend = new FileWriter("../res/BtestlengthendSNR10.txt", true);
			for (int i = 1; i < lineendlist.size() - 1 ; ++i){
				
				
					for (int d = 0; d < ndims; ++d){
						
						olden[d] = lineendlist.get(i - 1).position[d];
						newen[d] = lineendlist.get(i).position[d];
					}
				
				
				
					double length = Distance(olden, newen);
					
					final double seedtocurrent = util.Boundingboxes.Distancesq(lineendlist.get(i).original, newen);
					final double seedtoold = util.Boundingboxes.Distancesq(lineendlist.get(i).original, olden);
					
					
					
					
					if (seedtoold > seedtocurrent && lineendlist.get(i).frame > 5 ){
						
						// MT shrank
						
	                endlength-=length;					
						
					}
					else{
						
						
						// MT grew
						
					endlength+=length;
						
					}
					
					
					
					writerend.write( lineendlist.get(i-1).frame + 3+ " " + olden[0] + " " + olden[1]
							+ " " + newen[0] + " " + newen[1] + " " +  endlength );
					writerend.write("\r\n");
					
				
			}
			
			
			double startlength = 0;
			double[] oldst = new double[ndims];
			double[] newst = new double[ndims];
			 FileWriter writerstart = new FileWriter("../res/BtestlengthstartSNR10.txt", true);
				for (int i = 1; i < linestlist.size() - 1 ; ++i){
					
					
	               for (int d = 0; d < ndims; ++d){
						
						oldst[d] = linestlist.get(i - 1).position[d];
						newst[d] = linestlist.get(i).position[d];
					}
				
				
				
					double length = Distance(oldst, newst);
					
					final double seedtocurrent = util.Boundingboxes.Distancesq(lineendlist.get(i).original, newst);
					final double seedtoold = util.Boundingboxes.Distancesq(lineendlist.get(i).original, oldst);
					
					
					
				
						
						
						if (seedtoold > seedtocurrent && linestlist.get(i).frame > 5  ){
							
							// MT shrank
							
		                startlength-=length;					
							
						}
						else{
							
							
							// MT grew
							
						startlength+=length;
							
						}
						
						writerstart.write( linestlist.get(i-1).frame + 3 + " " + oldst[0] + " " + oldst[1]
								+ " " + newst[0] + " " + newst[1] + " " + startlength );
						writerstart.write("\r\n");
						
					}
				
				
			
			System.out.println("done");
			
			writerend.close();
			writerstart.close();
			
		}
		public static double Distance(final double[] cordone, final double[] cordtwo) {

			double distance = 0;

			for (int d = 0; d < cordone.length; ++d) {

				distance += Math.pow((cordone[d] - cordtwo[d]), 2);

			}
			return Math.sqrt(distance);
}
		*/
			
		
	

	
}	
}
