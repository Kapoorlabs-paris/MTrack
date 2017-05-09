package peakFitter;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import LineModels.GaussianLineds;
import LineModels.UseLineModel.UserChoiceModel;
import graphconstructs.Staticproperties;
import ij.gui.EllipseRoi;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import labeledObjects.CommonOutput;
import labeledObjects.CommonOutputHF;
import labeledObjects.Indexedlength;
import labeledObjects.LabelledImg;
import lineFinder.LinefinderHF;
import mpicbg.imglib.util.Util;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Point;
import net.imglib2.PointSampleList;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.BenchmarkAlgorithm;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;
import peakFitter.GaussianMaskFitMSER.EndfitMSER;
import peakFitter.SubpixelVelocityPCLine.StartorEnd;
import preProcessing.GetLocalmaxmin;
import util.Boundingboxes;

public class SubpixelVelocityCline extends BenchmarkAlgorithm
implements OutputAlgorithm<ArrayList<Indexedlength>> {

	private static final String BASE_ERROR_MSG = "[SubpixelVelocity] ";
	private final RandomAccessibleInterval<FloatType> source;
	private final ArrayList<CommonOutputHF> imgs;
	private final ArrayList<Indexedlength> PrevFrameparam;
	private final int ndims;
	private final int framenumber;
	private ArrayList<Indexedlength> final_paramlist;
	private ArrayList<Staticproperties> startandendinframe;
	private final double[] psf;
	private final UserChoiceModel model;
	private final Overlay overlay;
	
	// LM solver iteration params
	public int maxiter = 500;
	public double lambda = 1e-3;
	 public double termepsilon = 1e-1;
	//Mask fits iteration param
	 int iterations = 500;
	public double cutoffdistance = 20;
	public boolean halfgaussian = false;
	public double Intensityratio = 0.5;
	
	public void setCutoffdistance(double cutoffdistance) {
		this.cutoffdistance = cutoffdistance;
	}
	public double getCutoffdistance() {
		return cutoffdistance;
	}
	
	public void setIntensityratio(double intensityratio) {
		Intensityratio = intensityratio;
	}
	
	public double getIntensityratio() {
		return Intensityratio;
	}
	public void setMaxiter(int maxiter) {
		this.maxiter = maxiter;
	}
	
	public int getMaxiter() {
		return maxiter;
	}
	
	public void setLambda(double lambda) {
		this.lambda = lambda;
	}
	
	public double getLambda() {
		return lambda;
	}
	
	public void setTermepsilon(double termepsilon) {
		this.termepsilon = termepsilon;
	}
	
	public double getTermepsilon() {
		return termepsilon;
	}
	
	public void setHalfgaussian(boolean halfgaussian) {
		this.halfgaussian = halfgaussian;
	}
	
	public  SubpixelVelocityCline(final RandomAccessibleInterval<FloatType> source, 
			                      final LinefinderHF finder,
			                       final ArrayList<Indexedlength> PrevFrameparam,
			                       final double[] psf,
			                       final UserChoiceModel model,
			                       final int framenumber,
			                       final Overlay overlay) {
		finder.checkInput();
		finder.process();
		imgs = finder.getResult();
		this.source = source;
		this.model = model;
		this.PrevFrameparam = PrevFrameparam;
		this.psf = psf;
		this.framenumber = framenumber;
		this.overlay = overlay;
		this.ndims = source.numDimensions();
		
	}
	
	@Override
	public boolean checkInput() {
		if (source.numDimensions() > 2) {
			errorMessage = BASE_ERROR_MSG + " Can only operate on 1D, 2D, make slices of your stack . Got "
					+ source.numDimensions() + "D.";
			return false;
		}
		return true;
	}

	@Override
	public boolean process() {
		
		final_paramlist = new ArrayList<Indexedlength>();
		startandendinframe = new ArrayList<Staticproperties>();
		
		for (int index = 0; index < PrevFrameparam.size(); ++index) {

			Point linepoint = new Point(ndims);
			linepoint.setPosition(
					new long[] { (long) PrevFrameparam.get(index).currentpos[0], (long) PrevFrameparam.get(index).currentpos[1] });
			
			
			
			 ArrayList<Integer> label = Getlabel(linepoint);
			 Set<Integer> multilabel = new HashSet<Integer>(label);
			 
			
			 Point secondlinepoint = new Point(ndims);
				secondlinepoint.setPosition(
						new long[] { (long) PrevFrameparam.get(index).fixedpos[0], (long) PrevFrameparam.get(index).fixedpos[1] });
				
				
				 
				 ArrayList<Integer> seclabel = Getlabel(secondlinepoint);
				 Set<Integer> secmultilabel = new HashSet<Integer>(seclabel);
				 Set<Integer> finallabel = new HashSet<Integer>();
				 if (multilabel.size() > 0 && secmultilabel.size() > 0){
				secmultilabel.retainAll(multilabel);
				finallabel = secmultilabel;
				 }
				 else if (multilabel.size() == 0 &&secmultilabel.size() > 0 ){
					 finallabel = secmultilabel;
				 }
				 else if (secmultilabel.size() == 0 && multilabel.size() > 0){
					 finallabel = multilabel;
					 
				 }
				 
				 int currentlabel = Integer.MIN_VALUE;
				Iterator<Integer> iter = finallabel.iterator();
				if(iter.hasNext()){
				 currentlabel = iter.next();
				 
				}
				 
				 
				 if (currentlabel != Integer.MIN_VALUE){
					 System.out.println(currentlabel);
			 Indexedlength paramnextframe =Getfinaltrackparam(PrevFrameparam.get(index),
							currentlabel, psf, framenumber);

			 final double[] oldstartpoint = PrevFrameparam.get(index).currentpos;
			 
			 final double[] oldendpoint = PrevFrameparam.get(index).fixedpos;
			
			 
			 
			 
			 
			 if (paramnextframe==null)
				 paramnextframe = PrevFrameparam.get(index);
			     final_paramlist.add(paramnextframe);
			 
                  final double[] newstartpoint = paramnextframe.currentpos;
			 
                  
                  final OvalRoi Bigroi = new OvalRoi(Util.round(newstartpoint[0] - 2.5), Util.round(newstartpoint[1] - 2.5), Util.round(5),
							Util.round(5));
							Bigroi.setStrokeColor(Color.GREEN);
							Bigroi.setStrokeWidth(0.8);

							
							overlay.add(Bigroi);
                  
                  
			 final double[] newendpoint = paramnextframe.fixedpos;
			 
			 final double[] directionstart = {newstartpoint[0] - oldstartpoint[0] , newstartpoint[1] - oldstartpoint[1] };
			 
			 final double[] directionend = {newendpoint[0] - oldendpoint[0] , newendpoint[1] - oldendpoint[1] };
			 
			System.out.println("Frame:" + framenumber + " " +  "Fits :" + currentlabel + " "+ "StartX:" + paramnextframe.currentpos[0] 
					+ " StartY:" + paramnextframe.currentpos[1]+ " " + "EndX:"
					+ paramnextframe.fixedpos[0] + "EndY: " + paramnextframe.fixedpos[1]);
			 
		
			final Staticproperties edge = 
		   new Staticproperties(currentlabel, oldstartpoint, oldendpoint, newstartpoint, newendpoint, directionstart , directionend );
			

					startandendinframe.add(edge);	
				 }
				 
				 
		
		
}
		return false;
	}

	@Override
	public ArrayList<Indexedlength> getResult() {
		return final_paramlist;
	} 
	
	public ArrayList<Staticproperties> getStateVectors() {
		return startandendinframe;
	} 
	
	private final double[] MakerepeatedLineguess(Indexedlength iniparam, int label)  {
		long[] newposition = new long[ndims];
		double[] minVal = { Double.MAX_VALUE, Double.MAX_VALUE };
		double[] maxVal = { -Double.MIN_VALUE, -Double.MIN_VALUE };

		RandomAccessibleInterval<FloatType> currentimg = imgs.get(label).Actualroi;

		FinalInterval interval = imgs.get(label).interval;
		
		currentimg = Views.interval(currentimg, interval);
		double slope = iniparam.originalslope;
		double intercept = iniparam.originalintercept;
		


		final Cursor<FloatType> outcursor = Views.iterable(currentimg).localizingCursor();

		final double maxintensityline = GetLocalmaxmin.computeMaxIntensity(currentimg);
		while (outcursor.hasNext()) {

			outcursor.fwd();
			
			if (outcursor.get().get()/maxintensityline > Intensityratio){
				
				outcursor.localize(newposition);

				long pointonline = (long) (outcursor.getLongPosition(1) - slope * outcursor.getLongPosition(0) - intercept);
				
				// To get the min and max co-rodinates along the line so we
				// have starting points to
				// move on the line smoothly

				if (pointonline == 0) {
					for (int d = 0; d < ndims; ++d) {
						if (outcursor.getDoublePosition(d) <= minVal[d])
							minVal[d] = outcursor.getDoublePosition(d);

						if (outcursor.getDoublePosition(d) >= maxVal[d])
							maxVal[d] = outcursor.getDoublePosition(d);

					}
				}
			}
		}
		final double[] MinandMax = new double[2 * ndims + 3];

		if (slope >= 0) {
			for (int d = 0; d < ndims; ++d) {

				MinandMax[d] = minVal[d];
				MinandMax[d + ndims] = maxVal[d];
			}

		}

		if (slope < 0) {

			MinandMax[0] = minVal[0];
			MinandMax[1] = maxVal[1];
			MinandMax[2] = maxVal[0];
			MinandMax[3] = minVal[1];

		}

		MinandMax[2 * ndims] = 0.5 * Math.min(psf[0], psf[1]);
		MinandMax[2 * ndims + 1] = iniparam.lineintensity;
		MinandMax[2 * ndims + 2] = iniparam.background;
		
		System.out.println("Label: " + label + " " + "Initial guess: " + " StartX: " + MinandMax[0] + " StartY: "
				+ MinandMax[1] + " EndX: " + MinandMax[2] + " EndY: " + MinandMax[3]);

		
			for (int d = 0; d < ndims; ++d) {

				if (MinandMax[d] == Double.MAX_VALUE || MinandMax[d + ndims] == -Double.MIN_VALUE)
					return null;
				if (MinandMax[d] >= source.dimension(d) || MinandMax[d + ndims] >= source.dimension(d))
					return null;
				if (MinandMax[d] <= 0 || MinandMax[d + ndims] <= 0)
					return null;

			}
		

		
		return MinandMax;

	}
	public Indexedlength Getfinaltrackparam(final Indexedlength iniparam, final int label, final double[] psf, final int rate)  {

		if (iniparam == null || label == Integer.MIN_VALUE)
			return null;

		else {

			PointSampleList<FloatType> datalist = gatherfullData(label);
			final Cursor<FloatType> listcursor = datalist.localizingCursor();
			double[][] X = new double[(int) datalist.size()][ndims];
			double[] I = new double[(int) datalist.size()];
			int index = 0;
			while (listcursor.hasNext()) {
				listcursor.fwd();

				for (int d = 0; d < ndims; d++) {
					X[index][d] = listcursor.getDoublePosition(d);
				}

				I[index] = listcursor.get().getRealDouble();

				index++;
			}

			final double[] finalparamstart = MakerepeatedLineguess(iniparam, label);
			if (finalparamstart == null)
				return null;

			else {
				RandomAccessibleInterval<FloatType> currentimg = imgs.get(label).Actualroi;

				FinalInterval interval = imgs.get(label).interval;
				
				currentimg = Views.interval(currentimg, interval);

				final double[] fixed_param = new double[ndims];

				for (int d = 0; d < ndims; ++d) {

					fixed_param[d] = 1.0 / Math.pow(psf[d], 2);
				}

				final double[] inistartpos = { finalparamstart[0], finalparamstart[1] };
				final double[] iniendpos = { finalparamstart[2], finalparamstart[3] };

				double inicutoffdistance = Distance(inistartpos, iniendpos);
				final long radius = (long) ( Math.min(psf[0], psf[1]));
				// LM solver part
				final double[] safeparam = finalparamstart.clone();
				
					try {
						LevenbergMarquardtSolverLine.solve(X, finalparamstart, fixed_param, I, new GaussianLineds(), lambda,
								termepsilon, maxiter);
					} catch (Exception e) {
						e.printStackTrace();
					}

					final double[] startpos = { finalparamstart[0], finalparamstart[1] };
					final double[] endpos = { finalparamstart[2], finalparamstart[3] };
					// NaN protection: we prefer returning the crude estimate
					// than
					// NaN
					for (int j = 0; j < finalparamstart.length; j++) {
						if (Double.isNaN(finalparamstart[j]))
							finalparamstart[j] = safeparam[j];
					}

					final double LMdist = sqDistance(startpos, endpos);

					double[] returnparam = new double[2 * ndims + 5];

					final double maxintensityline = GetLocalmaxmin.computeMaxIntensity(currentimg);

					

					double newslope = (endpos[1] - startpos[1]) / (endpos[0] - startpos[0]);
					double newintercept = (endpos[1] - newslope * endpos[0]);
					double ds = finalparamstart[2 * ndims];
					double dx = ds / Math.sqrt(1 + newslope * newslope);
					double dy = newslope * dx;
					final double background = finalparamstart[2 * ndims + 2];
					double[] dxvector = { dx,  dy };

					double[] startfit = new double[ndims];
					double[] endfit = new double[ndims];

					double sigmas = 0;
					 
					for (int d  = 0; d < ndims; ++d){
						
						sigmas+=dxvector[d] * dxvector[d];
					}
				sigmas = Math.sqrt(sigmas);
				final int numgaussians = (int) Math.round(ds / sigmas);
					

					try {
						startfit = GaussianMaskFitMSER.sumofgaussianMaskFit(currentimg,  startpos.clone(),
								psf, numgaussians, iterations, dxvector, newslope, newintercept, maxintensityline,  halfgaussian,
								EndfitMSER.StartfitMSER, label, background, Intensityratio);
					} catch (Exception e) {
						e.printStackTrace();
					}

					try {
						endfit = GaussianMaskFitMSER.sumofgaussianMaskFit(currentimg,  endpos.clone(), psf, numgaussians,
								iterations, dxvector, newslope, newintercept, maxintensityline,  halfgaussian,
								EndfitMSER.EndfitMSER, label, background, Intensityratio);
					} catch (Exception e) {
						e.printStackTrace();
					}

					final double Maskdist = sqDistance(startfit, endfit);
					// If mask fits fail, return LM solver results


					for (int d = 0; d < ndims; ++d) {
						finalparamstart[d]= startfit[d];
						finalparamstart[ndims + d] = endfit[d];
					}

					
					if (Math.abs(Math.sqrt(Maskdist)) - Math.sqrt(LMdist) > cutoffdistance){
						if (Math.abs(startpos[0] - startfit[0]) >= cutoffdistance / 2 && Math.abs(startpos[1] - startfit[1]) >= cutoffdistance / 2
								|| Math.abs(endpos[0] - endfit[0]) >= cutoffdistance / 2 && Math.abs(endpos[1] - endfit[1]) >= cutoffdistance / 2 ){
							System.out.println("Mask fits fail, returning LM solver results!");
						
							for (int d = 0; d < ndims; ++d) {
								finalparamstart[d] = startpos[d];
								finalparamstart[ndims + d] = endpos[d];
						}
						}
					
						if (Math.abs(startpos[0] - startfit[0]) >= cutoffdistance || Math.abs(startpos[1] - startfit[1]) >= cutoffdistance 
								|| Math.abs(endpos[0] - endfit[0]) >= cutoffdistance  || Math.abs(endpos[1] - endfit[1]) >= cutoffdistance  ){
							System.out.println("Mask fits fail, returning LM solver results!");
							for (int d = 0; d < ndims; ++d) {
								finalparamstart[d] = startpos[d];
								finalparamstart[ndims + d] = endpos[d];
							}
							
						}
					
					
					}
					
					
					
					for (int d = 0; d < ndims; ++d) {
						if (Double.isNaN(startfit[d]) || Double.isNaN(endfit[d])) {
							System.out.println("Mask fits fail, returning LM solver results!");
							finalparamstart[d] = startpos[d];
							finalparamstart[ndims + d] = endpos[d];

						}

					}

					final int seedLabel = iniparam.seedLabel;

					final double[] finalstartpoint = { finalparamstart[0], finalparamstart[1] };
					final double[] finalendpoint = { finalparamstart[2], finalparamstart[3] };
					
						final double currentslope = (finalstartpoint[1] - finalendpoint[1]) / (finalstartpoint[0] - finalendpoint[0]);
						final double currentintercept = finalstartpoint[1] - currentslope * finalstartpoint[0];
						
						Indexedlength PointofInterest = new Indexedlength(label, seedLabel, framenumber, 
								finalparamstart[2 * ndims],finalparamstart[2 * ndims + 1], 
								finalparamstart[2 * ndims + 2], finalstartpoint, finalendpoint, currentslope, currentintercept,
								iniparam.originalslope, iniparam.originalintercept, iniparam.originalds);
					System.out.println("New X: " + finalstartpoint[0] + " New Y: " + finalstartpoint[1]);
						return PointofInterest;

				
					
			
			}

		}

	}
	
	

	private PointSampleList<FloatType> gatherfullData(final int label) {
		final PointSampleList<FloatType> datalist = new PointSampleList<FloatType>(ndims);

		RandomAccessibleInterval<FloatType> currentimg = imgs.get(label).Actualroi;

		FinalInterval interval = imgs.get(label).interval;
		
		currentimg = Views.interval(currentimg, interval);
		
		Cursor<FloatType> localcursor = Views.iterable(currentimg).localizingCursor();

		while (localcursor.hasNext()) {
			localcursor.fwd();
			
			Point newpoint = new Point(localcursor);
			datalist.add(newpoint, localcursor.get().copy());

		}

		return datalist;
	}
	public ArrayList<Integer> Getlabel(final Point linepoint) {

		
		ArrayList<Integer> currentlabel = new ArrayList<Integer>();
		for (int index = 0; index < imgs.size(); ++index){
			
			RandomAccessibleInterval<FloatType> currentimg = imgs.get(index).Actualroi;
			FinalInterval interval = imgs.get(index).interval;
			currentimg = Views.interval(currentimg, interval);
			for (int d = 0; d < ndims; ++d){
				
				if (linepoint.getIntPosition(d) >= interval.min(d) && linepoint.getIntPosition(d)<= interval.max(d)){
					
					currentlabel.add(index);
				}
			
			}
			
		}
		
	

		return currentlabel;
	}

	public double Distance(final double[] cordone, final double[] cordtwo) {

		double distance = 0;

		for (int d = 0; d < ndims; ++d) {

			distance += Math.pow((cordone[d] - cordtwo[d]), 2);

		}
		return Math.sqrt(distance);
	}

	public double sqDistance(final double[] cordone, final double[] cordtwo) {

		double distance = 0;

		for (int d = 0; d < ndims; ++d) {

			distance += Math.pow((cordone[d] - cordtwo[d]), 2);

		}
		return (distance);
	}
	
}