package peakFitter;


import java.util.ArrayList;

import com.sun.tools.javac.util.Pair;

import LineModels.GaussianLineds;
import LineModels.GaussianLinedsHF;
import LineModels.GaussianLinefixedds;
import LineModels.GaussianSplinesecorder;
import LineModels.Gaussiansplinesecfixedds;
import LineModels.MTFitFunction;
import LineModels.UseLineModel;
import LineModels.UseLineModel.UserChoiceModel;
import ij.gui.EllipseRoi;
import labeledObjects.CommonOutput;
import labeledObjects.Indexedlength;
import labeledObjects.KalmanIndexedlength;
import labeledObjects.LabelledImg;
import labeledObjects.Simpleobject;
import lineFinder.Linefinder;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Point;
import net.imglib2.PointSampleList;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.BenchmarkAlgorithm;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;
import peakFitter.GaussianMaskFitMSER.EndfitMSER;
import preProcessing.GetLocalmaxmin;
import util.Boundingboxes;

public class SubpixelLengthPCKalmanLine extends BenchmarkAlgorithm
implements OutputAlgorithm<Pair<ArrayList<KalmanIndexedlength>, ArrayList<KalmanIndexedlength>>> {
	
	private static final String BASE_ERROR_MSG = "[SubpixelLine] ";
	private final RandomAccessibleInterval<FloatType> source;
	private final ArrayList<CommonOutput> imgs;
	private final int ndims;
	private Pair<ArrayList<KalmanIndexedlength>, ArrayList<KalmanIndexedlength>> pair_paramlist;
	private ArrayList<KalmanIndexedlength> startlist;
	private ArrayList<KalmanIndexedlength> endlist;
	private final double[] psf;
	private final int minlength;
	private final int framenumber;
	private final boolean DoMask;
	private final UserChoiceModel model;
	// LM solver iteration params
	public int maxiter = 500;
	public double lambda = 1e-3;
	public double termepsilon = 1e-1;
	//Mask fits iteration param
	public int iterations = 500;
	public double cutoffdistance = 10;
	public boolean halfgaussian = false;
    public double Intensityratio;
    public double Inispacing;
	
  	public void setInispacing (double Inispacing){
  		
  		this.Inispacing = Inispacing;
  		
  	}
  	
  	public double getInispacing (){
  		
  		return Inispacing;
  	}
    
    /**
     * 
     * 
     * @param cutoffdistance for recoganizing the faliure of Gaussian Mask fits,
     * if the co-ordinates move by more than this distance than predicted by the LM 
     * solver, call it a faliure of the Mask fits
     */
    public void setCutoffdistance(double cutoffdistance) {
		this.cutoffdistance = cutoffdistance;
	}
    
    /**
     * 
     * @param halfgaussian for Gaussian Mask fits, to
     * cutoff half part of the last Gaussian for 1.5 Gaussian fit
     */
    public void setHalfgaussian(boolean halfgaussian) {
		this.halfgaussian = halfgaussian;
	}
    
    /**
     * 
     * @param intensityratio for making an intensity cutoff along the 
     * line to make the start and end point guess
     */
    
    public void setIntensityratio(double intensityratio) {
		Intensityratio = intensityratio;
	}
    
    /**
     * 
     * @param iterations for the Gaussian Mask fits
     */
    public void setIterations(int iterations) {
		this.iterations = iterations;
	}
    /**
     * 
     * @param lambda for LM solver optimizer
     */
    public void setLambda(double lambda) {
		this.lambda = lambda;
	}
    
    /**
     * 
     * @param maxiter, maximum iterations for the LM solver optimizer
     */
    public void setMaxiter(int maxiter) {
		this.maxiter = maxiter;
	}
    
    /**
     * 
     * @param termepsilon for the LM solver optimizer
     */
    public void setTermepsilon(double termepsilon) {
		this.termepsilon = termepsilon;
	}
	
    public double getCutoffdistance() {
		return cutoffdistance;
	}
    
    public int getFramenumber() {
		return framenumber;
	}
    
    public double getIntensityratio() {
		return Intensityratio;
	}
    
    public int getIterations() {
		return iterations;
	}
    
    public int getMaxiter() {
		return maxiter;
	}
    
    public int getMinlength() {
		return minlength;
	}
    
    public double getLambda() {
		return lambda;
	}
    
    public double[] getPsf() {
		return psf;
	}
    
    public double getTermepsilon() {
		return termepsilon;
	}
    

    
    
	
	public SubpixelLengthPCKalmanLine( final RandomAccessibleInterval<FloatType> source, 
			             final Linefinder finder,
			             final double[] psf,
			             final int minlength,
			             final UserChoiceModel model,
			             final int framenumber, 
			             final boolean DoMask){
		
		finder.checkInput();
		finder.process();
		imgs = finder.getResult();
		this.source = source;
		this.psf = psf;
		this.minlength = minlength;
		this.framenumber = framenumber;
		this.model = model;
		this.DoMask = DoMask;
		this.ndims = source.numDimensions();
		
	}

	@Override
	public boolean checkInput() {
		if (source.numDimensions() > 2) {
			errorMessage = BASE_ERROR_MSG + " Can only operate on 2D, make slices of your stack . Got "
					+ source.numDimensions() + "D.";
			return false;
		}
		return true;
	}

	@Override
	public boolean process() {
		
		Intensityratio = getIntensityratio();
		Inispacing = getInispacing();
		startlist = new ArrayList<KalmanIndexedlength>();
		endlist = new ArrayList<KalmanIndexedlength>();
		for (int index = 0; index < imgs.size() ; ++index) {
			
			final int Label = imgs.get(index).roilabel;
			final double slope = imgs.get(index).lineparam[0];
			final double intercept = imgs.get(index).lineparam[1];
			final double Curvature = imgs.get(index).lineparam[2];
			final double Inflection = imgs.get(index).lineparam[3];
			if ( slope!= Double.MAX_VALUE && intercept!= Double.MAX_VALUE){
			final Pair<KalmanIndexedlength, KalmanIndexedlength> returnparam = Getfinallineparam(Label, slope, intercept, Curvature, Inflection, psf, minlength);
			if (returnparam!= null ){
			startlist.add(returnparam.fst);
			endlist.add(returnparam.snd);
			}
			}
		}
		
		pair_paramlist = new Pair<ArrayList<KalmanIndexedlength>, ArrayList<KalmanIndexedlength>>(startlist, endlist);

		return true;
	}

	@Override
	public Pair<ArrayList<KalmanIndexedlength>, ArrayList<KalmanIndexedlength>> getResult() {
		
		
		return pair_paramlist;
	}

	public ArrayList<KalmanIndexedlength> getStartPoints(){
		
		return startlist;
	}
	
public ArrayList<KalmanIndexedlength> getEndPoints(){
		
		return endlist;
	}
	
	private final double[] MakeimprovedLineguess(double slope, double intercept, double Curvature, double Inflection, double[] psf, int label)  {
		long[] newposition = new long[ndims];
		double[] minVal = { Double.MAX_VALUE, Double.MAX_VALUE };
		double[] maxVal = { -Double.MIN_VALUE, -Double.MIN_VALUE };

		RandomAccessibleInterval<FloatType> currentimg = imgs.get(label).Actualroi;

		FinalInterval interval = imgs.get(label).interval;
		
		currentimg = Views.interval(currentimg, interval);

		final Cursor<FloatType> inputcursor = Views.iterable(currentimg).localizingCursor();

		final double maxintensityline = GetLocalmaxmin.computeMaxIntensity(currentimg);

           

           
           if (model ==  UserChoiceModel.Line){
        	   
        	   while(inputcursor.hasNext()){
       			
       			inputcursor.fwd();
       			
       			
       				inputcursor.localize(newposition);
 
       				if (inputcursor.getDoublePosition(0) <= minVal[0]
    						&& inputcursor.get().get() / maxintensityline > Intensityratio) {
    					minVal[0] = inputcursor.getDoublePosition(0);
    					minVal[1] = inputcursor.getDoublePosition(1);
    				}

    				if (inputcursor.getDoublePosition(0) >= maxVal[0]
    						&& inputcursor.get().get() / maxintensityline > Intensityratio) {
    					maxVal[0] = inputcursor.getDoublePosition(0);
    					maxVal[1] = inputcursor.getDoublePosition(1);
    				}

       			
                  }
		final double[] MinandMax = new double[2 * ndims + 3];

		
			for (int d = 0; d < ndims; ++d) {

				MinandMax[d] = minVal[d];
				MinandMax[d + ndims] = maxVal[d];
			}

		

		// This parameter is guess estimate for spacing between the Gaussians
		MinandMax[2 * ndims] =   Inispacing;
		MinandMax[2 * ndims + 1] = maxintensityline; 
		// This parameter guess estimates the background noise level
		MinandMax[2 * ndims + 2] = 0.0; 
		
		

		
		
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
          if (model ==  UserChoiceModel.Linefixedds){
        	   
        	   while(inputcursor.hasNext()){
       			
       			inputcursor.fwd();
       			
       			
       				inputcursor.localize(newposition);

       				if (inputcursor.getDoublePosition(0) <= minVal[0]
    						&& inputcursor.get().get() / maxintensityline > Intensityratio) {
    					minVal[0] = inputcursor.getDoublePosition(0);
    					minVal[1] = inputcursor.getDoublePosition(1);
    				}

    				if (inputcursor.getDoublePosition(0) >= maxVal[0]
    						&& inputcursor.get().get() / maxintensityline > Intensityratio) {
    					maxVal[0] = inputcursor.getDoublePosition(0);
    					maxVal[1] = inputcursor.getDoublePosition(1);
    				}

       			
                  }
		final double[] MinandMax = new double[2 * ndims + 2];

		
			for (int d = 0; d < ndims; ++d) {

				MinandMax[d] = minVal[d];
				MinandMax[d + ndims] = maxVal[d];
			}

		

		// This parameter is guess estimate for spacing between the Gaussians
		MinandMax[2 * ndims] = maxintensityline; 
		// This parameter guess estimates the background noise level
		MinandMax[2 * ndims + 1] = 0.5; 
		
		
		

		
		
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
           
          
           if (model ==  UserChoiceModel.Splineordersec ) {
        	   while(inputcursor.hasNext()){
       			
       			inputcursor.fwd();
       			
       			
       				// To get the min and max co-rodinates along the line so we have
       				// starting points to
       				// move on the line smoothly
       				

       				if (inputcursor.getDoublePosition(0) <= minVal[0]
    						&& inputcursor.get().get() / maxintensityline > Intensityratio) {
    					minVal[0] = inputcursor.getDoublePosition(0);
    					minVal[1] = inputcursor.getDoublePosition(1);
    				}

    				if (inputcursor.getDoublePosition(0) >= maxVal[0]
    						&& inputcursor.get().get() / maxintensityline > Intensityratio) {
    					maxVal[0] = inputcursor.getDoublePosition(0);
    					maxVal[1] = inputcursor.getDoublePosition(1);
    				}

       			
                  }
   			final double[] MinandMax = new double[2 * ndims + 6];
   			
   				for (int d = 0; d < ndims; ++d) {

   					MinandMax[d] = minVal[d];
   					MinandMax[d + ndims] = maxVal[d];
   				}

   			

   			// This parameter is guess estimate for spacing between the Gaussians
   			MinandMax[2 * ndims] = Inispacing;
   			MinandMax[2 * ndims + 1] = maxintensityline; 
   			// This parameter guess estimates the background noise level
   			MinandMax[2 * ndims + 2] = 0; 
   			
   			MinandMax[2 * ndims + 3] = slope; 
   			MinandMax[2 * ndims + 4] = Curvature; 
   			MinandMax[2 * ndims + 5] = intercept; 
   		

   			
   			
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
           
           else 
        	   return null;
           
	}
	
	// Get line parameters for fitting line to a line in a label

		public Pair<KalmanIndexedlength, KalmanIndexedlength> Getfinallineparam(final int label, final double slope, final double intercept, final double Curvature, final double Inflection, final double[] psf,
				final double minlength)  {

			PointSampleList<FloatType> datalist = gatherfullData(label);
			if (datalist!= null){
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

			final double[] start_param = MakeimprovedLineguess(slope, intercept, Curvature, Inflection, psf, label);
			if (start_param == null)
				return null;

			else {

				final double[] finalparamstart = start_param.clone();
				// LM solver part

				RandomAccessibleInterval<FloatType> currentimg = imgs.get(label).Actualroi;
				
				FinalInterval interval = imgs.get(label).interval;
				
				currentimg = Views.interval(currentimg, interval);

				final double[] fixed_param = new double[ndims + 3];

				for (int d = 0; d < ndims; ++d) {

					fixed_param[d] = 1.0 / Math.pow(psf[d], 2);
				}
				fixed_param[ndims] = slope;
				fixed_param[ndims + 1] = intercept;
				fixed_param[ndims + 2] = 0.5 * Math.min(psf[0], psf[1]);
			
				
				
				
				MTFitFunction UserChoiceFunction = null;
				
				if (model == UserChoiceModel.Line) {
					
					UserChoiceFunction = new GaussianLineds();

				}
					if (model == UserChoiceModel.Linefixedds) {
						
						UserChoiceFunction = new GaussianLinefixedds();

					}

					if (model == UserChoiceModel.LineHF) {

						UserChoiceFunction = new GaussianLinedsHF();
					}
					if (model == UserChoiceModel.Splineordersec) {

						UserChoiceFunction = new GaussianLineds();
					}
					if (model == UserChoiceModel.Splineordersecfixedds) {

						UserChoiceFunction = new GaussianLineds();
					}
					
				
				
					try {
						LevenbergMarquardtSolverLine.solve(X, finalparamstart, fixed_param, I, UserChoiceFunction, lambda,
								termepsilon, maxiter);
					} catch (Exception e) {
						e.printStackTrace();
					}

					final double[] startpos = { finalparamstart[0], finalparamstart[1] };
					final double[] endpos = { finalparamstart[2], finalparamstart[3] };
					// NaN protection: we prefer returning the crude estimate than
					// NaN
					for (int j = 0; j < finalparamstart.length; j++) {
						if (Double.isNaN(finalparamstart[j]))
							finalparamstart[j] = start_param[j];
					}

					if (model == UserChoiceModel.Line || model == UserChoiceModel.LineHF) {
					double newslope = (endpos[1] - startpos[1]) / (endpos[0] - startpos[0]);
					double newintercept = (endpos[1] - newslope * endpos[0]);
					double ds = finalparamstart[2 * ndims];
					double Intensity = finalparamstart[2* ndims + 1];
					double dx = ds/ Math.sqrt(1 + newslope * newslope);
					double dy = newslope * dx;
					final double background = finalparamstart[2 * ndims + 2];
					double[] dxvector = { dx, dy };

					double[] startfit = new double[ndims];
					double[] endfit = new double[ndims];
					final double maxintensityline = GetLocalmaxmin.computeMaxIntensity(currentimg);
					double[] startparam = new double[ndims ];
					double[] endparam = new double[ndims];
					for (int d = 0; d < ndims; ++d) {
						
						startparam[d] = startpos[d];
						endparam[d] = endpos[d];
					}
					double sigmas = 0;
					 
					for (int d  = 0; d < ndims; ++d){
						
						sigmas+= psf[d] * psf[d];
					}
					final int numgaussians = (int) Math.max(Math.round(Math.sqrt(sigmas) /  ds), 2);
				
				if (DoMask){
					try {
								
							startfit =	peakFitter.GaussianMaskFitMSER.sumofgaussianMaskFit(currentimg, startpos.clone(), psf, numgaussians,
								iterations, dxvector, newslope, newintercept, Intensity, halfgaussian, EndfitMSER.StartfitMSER,
								label, background);
					} catch (Exception e) {
						e.printStackTrace();
					}

					try {
						endfit = peakFitter.GaussianMaskFitMSER.sumofgaussianMaskFit(currentimg, endpos.clone(), psf, numgaussians,
								iterations, dxvector, newslope, newintercept, Intensity,  halfgaussian, EndfitMSER.EndfitMSER,
								label, background);
					} catch (Exception e) {
						e.printStackTrace();
					}

					// If mask fits fail, return LM solver results, very crucial for
					// noisy data

					
					/**
					 * dimensions of returnparam =  2 * ndims + 3 for the free parameters
					 * + 4 for the label and the frame number information.
					 */
					
					
					for (int d = 0; d < ndims; ++d) {
						
						startparam[d] = startfit[d];
						endparam[d] = endfit[d];
					}
					
					
					if (Math.abs(endpos[0] - endfit[0]) >= cutoffdistance || Math.abs(endpos[1] - endfit[1]) >= cutoffdistance){

						

						for (int d = 0; d < ndims; ++d) {
							
							startparam[d] = startpos[d];
							endparam[d] = endpos[d];
						}
						}
				
					
				
					
					

					for (int d = 0; d < ndims; ++d) {
						if (Double.isNaN(startfit[d]) || Double.isNaN(endfit[d])) {
							
							startparam[d] = startpos[d];
							endparam[d] = endpos[d];

						}
					}
					
				}
					final double currentslope = (endparam[1] - startparam[1]) / (endparam[0] - startparam[0]);
					final double currentintercept = endparam[1] - currentslope * endparam[0];

					
					final KalmanIndexedlength startPart = new KalmanIndexedlength(label, label, framenumber, finalparamstart[4],
							finalparamstart[5], finalparamstart[6], startparam,
							startparam , currentslope, currentintercept , currentslope, currentintercept, 0, 0, new double[]{dx, dy});
					
					final KalmanIndexedlength endPart = new KalmanIndexedlength(label, label, framenumber, finalparamstart[4],
							finalparamstart[5], finalparamstart[6], endparam,
							endparam, currentslope, currentintercept, currentslope, currentintercept, 0, 0,  new double[]{dx, dy});
					
					
					
					final Pair<KalmanIndexedlength, KalmanIndexedlength> pair = new Pair<KalmanIndexedlength, KalmanIndexedlength> ( startPart, endPart);
					return pair;
					
					}
					
					if (model == UserChoiceModel.Linefixedds) {
						double newslope = (endpos[1] - startpos[1]) / (endpos[0] - startpos[0]);
						double newintercept = (endpos[1] - newslope * endpos[0]);
						double ds = fixed_param[ndims + 2];
						double dx = ds/ Math.sqrt(1 + newslope * newslope);
						double dy = newslope * dx;
						final double LMdist = sqDistance(startpos, endpos);
						double[] dxvector = { dx, dy };
						final double Intensity = finalparamstart[2 * ndims];
						final double background = finalparamstart[2 * ndims + 1];
						double[] startfit = new double[ndims];
						double[] endfit = new double[ndims];
						final double maxintensityline = GetLocalmaxmin.computeMaxIntensity(currentimg);
						double[] startparam = new double[ndims ];
						double[] endparam = new double[ndims];
						for (int d = 0; d < ndims; ++d) {
							
							startparam[d] = startpos[d];
							endparam[d] = endpos[d];
						}
						double sigmas = 0;
						 
						for (int d  = 0; d < ndims; ++d){
							
							sigmas+= psf[d] * psf[d];
						}
						final int numgaussians = (int) Math.max(Math.round(Math.sqrt(sigmas) /  ds), 2);
					
					if (DoMask){
					
						try {
									
								startfit =	peakFitter.GaussianMaskFitMSER.sumofgaussianMaskFit(currentimg, startpos.clone(), psf, numgaussians,
									iterations, dxvector, newslope, newintercept, Intensity, halfgaussian, EndfitMSER.StartfitMSER,
									label, background);
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							endfit = peakFitter.GaussianMaskFitMSER.sumofgaussianMaskFit(currentimg, endpos.clone(), psf, numgaussians,
									iterations, dxvector, newslope, newintercept, Intensity,  halfgaussian, EndfitMSER.EndfitMSER,
									label, background);
						} catch (Exception e) {
							e.printStackTrace();
						}

						
						/**
						 * dimensions of returnparam =  2 * ndims + 3 for the free parameters
						 * + 4 for the label and the frame number information.
						 */
						
						
						for (int d = 0; d < ndims; ++d) {
							
							startparam[d] = startfit[d];
							endparam[d] = endfit[d];
						}
						
						if (Math.abs(endpos[0] - endfit[0]) >= cutoffdistance || Math.abs(endpos[1] - endfit[1]) >= cutoffdistance){


							for (int d = 0; d < ndims; ++d) {
								
								startparam[d] = startpos[d];
								endparam[d] = endpos[d];
							}
							}
						
						
						
						
						

						for (int d = 0; d < ndims; ++d) {
							if (Double.isNaN(startfit[d]) || Double.isNaN(endfit[d])) {
								
								startparam[d] = startpos[d];
								endparam[d] = endpos[d];

							}
						}
						
				    }
						
						final double currentslope = (endparam[1] - startparam[1]) / (endparam[0] - startparam[0]);
						final double currentintercept = endparam[1] - currentslope * endparam[0];

					
						final KalmanIndexedlength startPart = new KalmanIndexedlength(label, label, framenumber, ds,
								finalparamstart[4], finalparamstart[5], startparam,
								startparam , currentslope, currentintercept , currentslope, currentintercept, 0, 0, new double[]{dx, dy});
						
						final KalmanIndexedlength endPart = new KalmanIndexedlength(label, label, framenumber, ds,
								finalparamstart[4], finalparamstart[5], endparam,
								endparam, currentslope, currentintercept, currentslope, currentintercept, 0 , 0 , new double[]{dx, dy});
						
						
						
						final Pair<KalmanIndexedlength, KalmanIndexedlength> pair = new Pair<KalmanIndexedlength, KalmanIndexedlength> ( startPart, endPart);
						return pair;
						
						}
						
					// default
					else {
						double newslope = (endpos[1] - startpos[1]) / (endpos[0] - startpos[0]);
					double newintercept = (endpos[1] - newslope * endpos[0]);
					double ds = finalparamstart[4];
					double dx = finalparamstart[4]/ Math.sqrt(1 + newslope * newslope);
					double dy = newslope * dx;
					final double LMdist = sqDistance(startpos, endpos);
					double[] dxvector = { dx, dy };
					final double background = finalparamstart[2 * ndims + 1];
					double[] startfit = new double[ndims];
					double[] endfit = new double[ndims];
					final double maxintensityline = GetLocalmaxmin.computeMaxIntensity(currentimg);
					double[] startparam = new double[ndims ];
					double[] endparam = new double[ndims];
					for (int d = 0; d < ndims; ++d) {
						
						startparam[d] = startpos[d];
						endparam[d] = endpos[d];
					}
					double sigmas = 0;
					 
					for (int d  = 0; d < ndims; ++d){
						
						sigmas+= psf[d] * psf[d];
					}
					final int numgaussians = (int) Math.max(Math.round(Math.sqrt(sigmas) /  ds), 2);
				
				if (DoMask){
					try {
								
							startfit =	peakFitter.GaussianMaskFitMSER.sumofgaussianMaskFit(currentimg, startpos.clone(), psf, numgaussians,
								iterations, dxvector, newslope, newintercept, maxintensityline, halfgaussian, EndfitMSER.StartfitMSER,
								label, background);
					} catch (Exception e) {
						e.printStackTrace();
					}

					try {
						endfit = peakFitter.GaussianMaskFitMSER.sumofgaussianMaskFit(currentimg, endpos.clone(), psf, numgaussians,
								iterations, dxvector, newslope, newintercept, maxintensityline,  halfgaussian, EndfitMSER.EndfitMSER,
								label, background);
					} catch (Exception e) {
						e.printStackTrace();
					}

			
					/**
					 * dimensions of returnparam =  2 * ndims + 3 for the free parameters
					 * + 4 for the label and the frame number information.
					 */
					
					
					for (int d = 0; d < ndims; ++d) {
						
						startparam[d] = startfit[d];
						endparam[d] = endfit[d];
					}
					
					
					
					if (Math.abs(endpos[0] - endfit[0]) >= cutoffdistance || Math.abs(endpos[1] - endfit[1]) >= cutoffdistance){
                       {

						for (int d = 0; d < ndims; ++d) {
							
							startparam[d] = startpos[d];
							endparam[d] = endpos[d];
						}
						
				
                       }
					
					
					
					}

					for (int d = 0; d < ndims; ++d) {
						if (Double.isNaN(startfit[d]) || Double.isNaN(endfit[d])) {
							
							startparam[d] = startpos[d];
							endparam[d] = endpos[d];

						}
					}
					
				}
					final double currentslope = (endparam[1] - startparam[1]) / (endparam[0] - startparam[0]);
					final double currentintercept = endparam[1] - currentslope * endparam[0];

					
					final KalmanIndexedlength startPart = new KalmanIndexedlength(label, label, framenumber, finalparamstart[4],
							finalparamstart[5], finalparamstart[6], startparam,
							startparam , currentslope, currentintercept , currentslope, currentintercept, 0 , 0 , new double[]{dx, dy});
					
					final KalmanIndexedlength endPart = new KalmanIndexedlength(label, label, framenumber, finalparamstart[4],
							finalparamstart[5], finalparamstart[6], endparam,
							endparam, currentslope, currentintercept, currentslope, currentintercept, 0 , 0, new double[]{dx, dy});
					
					
					
					final Pair<KalmanIndexedlength, KalmanIndexedlength> pair = new Pair<KalmanIndexedlength, KalmanIndexedlength> ( startPart, endPart);
					return pair;
					}

			

			}
			
			}
			
			else 
				return null;
		}
		public int Getlabel(final Point linepoint) {

			
			int currentlabel = Integer.MIN_VALUE;
			for (int index = 0; index < imgs.size(); ++index){
				
				
				if (imgs.get(index).intimg!= null){
					
					RandomAccess<IntType> intranac = imgs.get(index).intimg.randomAccess();

					intranac.setPosition(linepoint);
					currentlabel = intranac.get().get();

					return currentlabel;
					
				}
				
				RandomAccessibleInterval<FloatType> currentimg = imgs.get(index).Actualroi;
				FinalInterval interval = imgs.get(index).interval;
				currentimg = Views.interval(currentimg, interval);
				for (int d = 0; d < ndims; ++d){
					
					if (linepoint.getIntPosition(d) >= interval.min(d) && linepoint.getIntPosition(d)<= interval.max(d)){
						
						currentlabel = index;
						break;
					}
					
				}
				
				
				
				
			}
			
		

			return currentlabel;
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
		
		
		public double cordDistance(final double[] cordone, final double[] cordtwo) {

			double distance = 0;

			for (int d = 0; d < ndims; ++d) {

				distance += Math.min((cordone[d] - cordtwo[d]),1);

			}
			return (distance);
		}
		
}


