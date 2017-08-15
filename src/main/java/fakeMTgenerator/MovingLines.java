package fakeMTgenerator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


import ij.ImageJ;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.stats.Normalize;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.view.Views;
import poissonSimulator.Poissonprocess;
import preProcessing.Kernels;

public class MovingLines {

	
	
	public static void main (String[] args) throws IncompatibleTypeException, IOException{
        new ImageJ();
		
		final FinalInterval range = new FinalInterval(512, 512);
		final FinalInterval smallrange = new FinalInterval(112, 112);
		
		
		
		final int ndims = range.numDimensions();
		final double [] sigma = {1.65,1.47};
		final double [] Ci = new double[ndims];
		
		for (int d = 0; d < ndims; ++d)
			Ci[d] = 1.0 / Math.pow(sigma[d],2);
		
		final int numframes = 50;
		final int numlines = 1;

		
		
			
		RandomAccessibleInterval<FloatType> lineimage = new ArrayImgFactory<FloatType>().create(range, new FloatType());
		RandomAccessibleInterval<FloatType> noisylines = new ArrayImgFactory<FloatType>().create(range, new FloatType());
		
		
		ArrayList<double[]> startseeds = new ArrayList<double[]>();
		ArrayList<double[]> endseeds = new ArrayList<double[]>();
		Dummylines.GetSeeds(lineimage, startseeds, endseeds, smallrange, numlines, sigma);
		
		
		

		FloatType minval = new FloatType(0);
		FloatType maxval = new FloatType(1);
		Normalize.normalize(Views.iterable(lineimage), minval, maxval);
		Kernels.addBackground(Views.iterable(lineimage), 0.2);
		noisylines = Poissonprocess.poissonProcess(lineimage, 30);
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
		
			
			
			
			
			for (int index = 0; index < pair.getA().getA().size(); ++index){
				double[] prevst = new double[ndims];
				double[] nextst = new double[ndims];
				 
				for (int d = 0; d < ndims; ++d){
					
					prevst[d] = pair.getA().getA().get(index).originalpoint[d];
					
					nextst[d] = pair.getA().getA().get(index).newpoint[d];
					
				}
				Indexofline linest = new Indexofline(index, frame, prevst, nextst);
				linestlist.add(linest);	

		
			}
			 
			for (int index = 0; index < pair.getA().getA().size(); ++index){
				
				double[] preven = new double[ndims];
				double[] nexten = new double[ndims];
				
				  
				for (int d = 0; d < ndims; ++d){
					
					preven[d] = pair.getA().getB().get(index).originalpoint[d];
					
					nexten[d] = pair.getA().getB().get(index).newpoint[d];
				}
				
				Indexofline lineend = new Indexofline(index, frame,preven, nexten);
				lineendlist.add(lineend);	
             	
				
				

		
			}
	    

	   
		
		Normalize.normalize(Views.iterable(lineimageframe), minval, maxval);
		Kernels.addBackground(Views.iterable(lineimageframe), 0.2);
		noisylinesframe = Poissonprocess.poissonProcess(lineimageframe, 30);
		
	
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
}

