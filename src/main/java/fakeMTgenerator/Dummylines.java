package fakeMTgenerator;

import java.util.ArrayList;
import java.util.Random;


import drawandOverlay.AddGaussian;
import drawandOverlay.PushCurves;
import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;

public class Dummylines {

	public static void GetSeeds(RandomAccessibleInterval<FloatType> outimg, ArrayList<double[]> startseeds,
			ArrayList<double[]> endseeds,

			final Interval range, final int numlines, final double[] sigma) throws IncompatibleTypeException {

		final Random rnd = new Random(40);
		final Random rndsec = new Random(8);
		final int n = outimg.numDimensions();

		for (int index = 0; index < numlines; ++index) {

			double startpos[] = new double[n];
			double endpos[] = new double[n];
			double[] startline = new double[n];
			double[] endline = new double[n];
			double MaxLength = 55.82;

			for (int d = 0; d < range.numDimensions(); ++d) {
				startpos[d] = 150 + (rnd.nextDouble() * (range.max(d) - range.min(d)) + range.min(d));
				endpos[d] = ((rndsec.nextDouble() * (range.max(d) - range.min(d)) + range.min(d)));
			}
			final double[] tmppos = new double[n];

			final double[] minVal = new double[n];
			final double[] maxVal = new double[n];
			while (true) {
				if (Distance(startpos, endpos) > MaxLength) {

					for (int d = 0; d < range.numDimensions(); ++d) {

						endpos[d] = (startpos[d] + endpos[d]) / 2;
					}

				}
				if (Distance(startpos, endpos) <= MaxLength)
					break;
			}

			double slope = (endpos[1] - startpos[1]) / (endpos[0] - startpos[0]);
			double intercept = startpos[1] - slope * startpos[0];
			for (int d = 0; d < n; ++d) {

				final double locationdiff = startpos[d] - endpos[d];
				final boolean minsearch = locationdiff > 0;
				tmppos[d] = startpos[d];

				minVal[d] = minsearch ? endpos[d] : startpos[d];
				maxVal[d] = minsearch ? tmppos[d] : endpos[d];

			}

			if (slope >= 0) {
				for (int d = 0; d < n; ++d) {

					startline[d] = minVal[d];
					endline[d] = maxVal[d];
				}

			}

			if (slope < 0) {

				startline[0] = minVal[0];
				startline[1] = maxVal[1];
				endline[0] = maxVal[0];
				endline[1] = minVal[1];

			}

			double stepsize = 1.0;
			double steppos[] = { startline[0], startline[1] };
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
			for (int d = 0; d < n; ++d)
				endline[d] = steppos[d];

			final double[] startinfo = { startline[0], startline[1], slope, intercept };
			final double[] endinfo = { endline[0], endline[1], slope, intercept };
			startseeds.add(startinfo);
			endseeds.add(endinfo);

		}

	}
	
	public static Pair<Pair<ArrayList<Dummyprops>, ArrayList<Dummyprops>>, Pair<ArrayList<double[]>, ArrayList<double[]>>> Growseeds (RandomAccessibleInterval<FloatType> outimg, 
			ArrayList<double[]> startseeds, ArrayList<double[]> endseeds, final int frame, double[] sigma) throws IncompatibleTypeException{
		
	
		
		final int n = outimg.numDimensions();
		
        double growrate = 12* Math.sin(0.2 * frame) ;
        double motion = 5;
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
	             oldpos[1] = startseeds.get(index)[1];
	             
	            
	             
				 startpos[0] = oldpos[0] + Math.abs(motion* growrate) ;
				 startpos[1] = oldpos[1] + Math.abs(motion* growrate) ;
				
			 
				
			
			 
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
					
					 Dummyprops newst = new Dummyprops(frame, oldpos, steppos, slope, intercept);
					 newstlist.add(newst);
			 
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
			    oldpos[1] = endseeds.get(index)[1];
			 
				endpos[0] = oldpos[0] + Math.abs(motion* growrate) ;
				endpos[1] = oldpos[1] + Math.abs(motion* growrate) ;
				
				
				
				
				final double stepsize =  1 ;
				double steppos[] = {startpos[0], startpos[1]};
				double dx = stepsize / Math.sqrt(1 + slope * slope);
				double dy = slope * dx ;
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
	
	
		 Pair<ArrayList<Dummyprops>, ArrayList<Dummyprops>> pair = new  ValuePair<ArrayList<Dummyprops>, ArrayList<Dummyprops>> (newstlist, newendlist);
	
		 Pair<ArrayList<double[]>, ArrayList<double[]>> pairdoub = new  ValuePair<ArrayList<double[]>, ArrayList<double[]>> (startseeds, endseeds);
		 Pair<Pair<ArrayList<Dummyprops>, ArrayList<Dummyprops>>, Pair<ArrayList<double[]>, ArrayList<double[]>>> totalpair = new
				 ValuePair<Pair<ArrayList<Dummyprops>, ArrayList<Dummyprops>>, Pair<ArrayList<double[]>, ArrayList<double[]>>> (pair, pairdoub);
	return totalpair;
		
	}
	
	
	public static double Distance(final double[] cordone, final double[] cordtwo) {

		double distance = 0;

		for (int d = 0; d < cordone.length; ++d) {

			distance += Math.pow((cordone[d] - cordtwo[d]), 2);

		}
		return Math.sqrt(distance);
	}

}
