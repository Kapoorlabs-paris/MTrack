package peakFitter;


import java.util.ArrayList;

import com.sun.tools.javac.util.Pair;

import LineModels.GaussianLineds;
import LineModels.UseLineModel.UserChoiceModel;
import ij.gui.EllipseRoi;
import labeledObjects.CommonOutput;
import labeledObjects.Indexedlength;
import labeledObjects.LabelledImg;
import labeledObjects.Simpleobject;
import lineFinder.Linefinder;
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
import preProcessing.GetLocalmaxmin;
import util.Boundingboxes;

public class SubpixelLengthCline extends BenchmarkAlgorithm
implements OutputAlgorithm<ArrayList<Indexedlength>> {
	
	private static final String BASE_ERROR_MSG = "[SubpixelLineMSER] ";
	private final RandomAccessibleInterval<FloatType> source;
	private final ArrayList<CommonOutput> imgs;
	private final int ndims;
	private ArrayList<Indexedlength> paramlist;
	
	private final double[] psf;
	private final int minlength;
	private final int framenumber;
	// LM solver iteration params
	public int maxiter = 500;
	public double lambda = 1e-3;
	public double termepsilon = 1e-1;
	//Mask fits iteration param
	public int iterations = 500;
	public double cutoffdistance = 20;
	public boolean halfgaussian = false;
    public double Intensityratio = 0.5;
    private final UserChoiceModel model;
    
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
    

    
    
	
	public SubpixelLengthCline( final RandomAccessibleInterval<FloatType> source, 
			             final Linefinder finder,
			             final double[] psf,
			             final int minlength,
			             final UserChoiceModel model,
			             final int framenumber){
		finder.checkInput();
		finder.process();
		this.source = source;
		imgs = finder.getResult();
		this.model = model;
		this.psf = psf;
		this.minlength = minlength;
		this.framenumber = framenumber;
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
		
		
		paramlist = new ArrayList<Indexedlength>();
		
		for (int index = 0; index < imgs.size() ; ++index) {
			
			final int Label = imgs.get(index).roilabel;
			final double slope = imgs.get(index).lineparam[0];
			final double intercept = imgs.get(index).lineparam[1];
			final double Curvature = imgs.get(index).lineparam[2];
			final double Inflection = imgs.get(index).lineparam[3];
			if ( slope!= Double.MAX_VALUE && intercept!= Double.MAX_VALUE){
			final Indexedlength returnparam = Getfinallineparam(Label, slope, intercept,Curvature, Inflection, psf, minlength);
			if (returnparam!= null )
			paramlist.add(returnparam);
			}
		}
		
	

		return true;
	}

	@Override
	public ArrayList<Indexedlength> getResult() {
		
		
		return paramlist;
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
       			
       			if (inputcursor.get().get()/maxintensityline > Intensityratio){
       			
       				inputcursor.localize(newposition);
       				long pointonline = (long) (newposition[1] - slope * newposition[0] - intercept);

       				// To get the min and max co-rodinates along the line so we have
       				// starting points to
       				// move on the line smoothly
       				
       				if (pointonline == 0) {

       					for (int d = 0; d < ndims; ++d) {
       						if (inputcursor.getDoublePosition(d) <= minVal[d])
       							minVal[d] = inputcursor.getDoublePosition(d);

       						if (inputcursor.getDoublePosition(d) >= maxVal[d])
       							maxVal[d] = inputcursor.getDoublePosition(d);

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

		// This parameter is guess estimate for spacing between the Gaussians
		MinandMax[2 * ndims] =  0.5 * Math.min(psf[0], psf[1]);
		MinandMax[2 * ndims + 1] = maxintensityline; 
		// This parameter guess estimates the background noise level
		MinandMax[2 * ndims + 2] = 0; 
		
		
		System.out.println("Label: " + label + " " + "Detection: " + " StartX: " + MinandMax[0] + " StartY: "
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
           
           else {
        	   while(inputcursor.hasNext()){
       			
       			inputcursor.fwd();
       			
       			if (inputcursor.get().get()/maxintensityline > Intensityratio){
       			
       				inputcursor.localize(newposition);
       				long pointoncurve = (long) (newposition[1] - slope * newposition[0] 
       						- Curvature * newposition[0]  * newposition[0]  - Inflection *newposition[0] *newposition[0] *newposition[0]   - intercept);

       				// To get the min and max co-rodinates along the line so we have
       				// starting points to
       				// move on the line smoothly
       				
       				if (pointoncurve == 0) {

       					for (int d = 0; d < ndims; ++d) {
       						if (inputcursor.getDoublePosition(d) <= minVal[d])
       							minVal[d] = inputcursor.getDoublePosition(d);

       						if (inputcursor.getDoublePosition(d) >= maxVal[d])
       							maxVal[d] = inputcursor.getDoublePosition(d);

       					}

       				}

       			
       			}
                  }
   			final double[] MinandMax = new double[2 * ndims + 6];
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

   			// This parameter is guess estimate for spacing between the Gaussians
   			MinandMax[2 * ndims] =  0.5 * Math.min(psf[0], psf[1]);
   			MinandMax[2 * ndims + 1] = maxintensityline; 
   			// This parameter guess estimates the background noise level
   			MinandMax[2 * ndims + 2] = 0; 
   			
   			MinandMax[2 * ndims + 3] = slope; 
   			MinandMax[2 * ndims + 4] = 0; 
   			MinandMax[2 * ndims + 5] = 0; 
   			System.out.println("Label: " + label + " " + "Detection: " + " StartX: " + MinandMax[0] + " StartY: "
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

	}
	
	// Get line parameters for fitting line to a line in a label

		public Indexedlength Getfinallineparam(final int label, final double slope, final double intercept, final double Curvature, final double Inflection, final double[] psf,
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

				final double[] fixed_param = new double[ndims];

				for (int d = 0; d < ndims; ++d) {

					fixed_param[d] = 1.0 / Math.pow(psf[d], 2);
				}

			
				double inicutoffdistance = 0;
				final double[] inistartpos = { start_param[0], start_param[1] };
				final double[] iniendpos = { start_param[2], start_param[3] };
				inicutoffdistance = Distance(inistartpos, iniendpos);
				
				
				
				
				
				if (inicutoffdistance > minlength) {
					try {
						LevenbergMarquardtSolverLine.solve(X, finalparamstart, fixed_param, I, new GaussianLineds(), lambda,
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


					double newslope = (endpos[1] - startpos[1]) / (endpos[0] - startpos[0]);
					double newintercept = (endpos[1] - newslope * endpos[0]);
					double ds = finalparamstart[2 * ndims];
					double dx = ds/ Math.sqrt(1 + newslope * newslope);
					final double background = finalparamstart[2 * ndims + 2];
					double dy = newslope * dx;
					final double LMdist = sqDistance(startpos, endpos);
					double[] dxvector = { dx, dy };
					double sigmas = 0;
					 
					for (int d  = 0; d < ndims; ++d){
						
						sigmas+=dxvector[d] * dxvector[d];
					}
				sigmas = Math.sqrt(sigmas);
				final int numgaussians = (int) Math.round(ds / sigmas);
					double[] startfit = new double[ndims];
					double[] endfit = new double[ndims];
					final double maxintensityline = GetLocalmaxmin.computeMaxIntensity(currentimg);


					System.out.println("Doing Mask Fits: ");
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

					final double Maskdist = sqDistance(startfit, endfit);
					// If mask fits fail, return LM solver results, very crucial for
					// noisy data

					
					/**
					 * dimensions of returnparam =  2 * ndims + 3 for the free parameters
					 * + 4 for the label and the frame number information.
					 */
					double[] returnparam = new double[2 * ndims];
					double[] startparam = new double[ ndims];
					double[] endparam = new double[ ndims];
					
					for (int d = 0; d < ndims; ++d) {
						returnparam[d] = startfit[d];
						returnparam[ndims + d] = endfit[d];
						startparam[d] = startfit[d];
						endparam[d] = endfit[d];
					}
					
					
					if (Math.abs(Math.sqrt(Maskdist)) - Math.sqrt(LMdist) >= cutoffdistance){
					if (Math.abs(startpos[0] - startfit[0]) >= cutoffdistance / 2 && Math.abs(startpos[1] - startfit[1]) >= cutoffdistance / 2
							|| Math.abs(endpos[0] - endfit[0]) >= cutoffdistance / 2 && Math.abs(endpos[1] - endfit[1]) >= cutoffdistance / 2 ){
						System.out.println("Mask fits fail, both cords move far, returning LM solver results!");

						for (int d = 0; d < ndims; ++d) {
							returnparam[d] = startpos[d];
							returnparam[ndims + d] = endpos[d];
							startparam[d] = startpos[d];
							endparam[d] = endpos[d];
						}
						}
					if (Math.abs(startpos[0] - startfit[0]) >= cutoffdistance || Math.abs(startpos[1] - startfit[1]) >= cutoffdistance 
							|| Math.abs(endpos[0] - endfit[0]) >= cutoffdistance  || Math.abs(endpos[1] - endfit[1]) >= cutoffdistance  ){
						System.out.println("Mask fits fail, one cord moves too much, returning LM solver results!");
						for (int d = 0; d < ndims; ++d) {
							returnparam[d] = startpos[d];
							returnparam[ndims + d] = endpos[d];
							startparam[d] = startpos[d];
							endparam[d] = endpos[d];
						}
						
					}
					
					
					
					
					}

					for (int d = 0; d < ndims; ++d) {
						if (Double.isNaN(startfit[d]) || Double.isNaN(endfit[d])) {
							System.out.println("Mask fits fail, returning LM solver results!");
							returnparam[d] = startpos[d];
							returnparam[ndims + d] = endpos[d];
							startparam[d] = startpos[d];
							endparam[d] = endpos[d];

						}
					}
					
					final double currentslope = (returnparam[3] - returnparam[1]) / (returnparam[2] - returnparam[0]);
					final double currentintercept = returnparam[3] - currentslope * returnparam[2];

					System.out.println("Fits :" + "StartX:" + returnparam[0] + " StartY:" + returnparam[1] + " " + "EndX:"
							+ returnparam[2] + "EndY: " + returnparam[3] + " " + "ds: " + finalparamstart[4] );

					System.out.println("Length: " + Distance(new double[]{returnparam[0],  returnparam[1]},new double[]{returnparam[2],  returnparam[3]} ));

					final Indexedlength BothPart = new Indexedlength(label, label, framenumber, finalparamstart[4],
							finalparamstart[5], finalparamstart[6], startparam,
							endparam , currentslope, currentintercept , currentslope, currentintercept, 0 , 0 , new double[]{dx, dy});

					System.out.println("Fits :" + "StartX:" + returnparam[0] + " StartY:" + returnparam[1] + " " + "EndX:"
							+ returnparam[2] + "EndY: " + returnparam[3] + " " + "ds: " + finalparamstart[4] );

					System.out.println("Length: " + Distance(new double[]{returnparam[0],  returnparam[1]},new double[]{returnparam[2],  returnparam[3]} ));
					
					
					
					
					
					
					return BothPart;

				}

				else
					return null;

			}
			}
			
			else 
				return null;
		}
		public int Getlabel(final Point linepoint, final double oldslope, final double oldintercept) {

			ArrayList<Integer> currentlabel = new ArrayList<Integer>();
			int finallabel = Integer.MIN_VALUE;
			int pointonline = Integer.MAX_VALUE;
			for (int index = 0; index < imgs.size(); ++index) {

				RandomAccessibleInterval<FloatType> currentimg = imgs.get(index).Actualroi;
				FinalInterval interval = imgs.get(index).interval;
				currentimg = Views.interval(currentimg, interval);

				if (linepoint.getIntPosition(0) >= interval.min(0) && linepoint.getIntPosition(0) <= interval.max(0)
						&& linepoint.getIntPosition(1) >= interval.min(1)
						&& linepoint.getIntPosition(1) <= interval.max(1)) {

					currentlabel.add(imgs.get(index).roilabel);
				}

			}
			for (int index = 0; index < currentlabel.size(); ++index) {
				int distfromline = (int) Math
						.abs(linepoint.getIntPosition(1) - oldslope * linepoint.getIntPosition(0) - oldintercept);

				if (distfromline < pointonline) {

					pointonline = distfromline;
					finallabel = currentlabel.get(index);

				}

			}

			return finallabel;
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