package peakFitter;

import java.util.ArrayList;

import javax.swing.JProgressBar;

import LineModels.GaussianLineds;
import LineModels.GaussianLinedsHF;
import LineModels.GaussianLinefixedds;
import LineModels.GaussianPSFPoly;
import LineModels.GaussianSplinesecorder;
import LineModels.Gaussiansplinesecfixedds;
import LineModels.MTFitFunction;
import LineModels.UseLineModel;
import LineModels.UseLineModel.UserChoiceModel;
import beadFinder.GetCOM;
import ij.IJ;
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
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.BenchmarkAlgorithm;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;
import peakFitter.GaussianMaskFitMSER.EndfitMSER;
import preProcessing.GetLocalmaxmin;
import psf_Tookit.GaussianLineFitParam;
import util.Boundingboxes;

public class SubpixelLocationLine extends BenchmarkAlgorithm
		implements OutputAlgorithm<ArrayList<GaussianLineFitParam>> {

	private static final String BASE_ERROR_MSG = "[SubpixelLocationLine] ";
	private final RandomAccessibleInterval<FloatType> source;
	private final ArrayList<CommonOutput> imgs;
	private final int ndims;
	private ArrayList<GaussianLineFitParam> startlist;
	private final int framenumber;
	private final int thirdDimensionSize;
	// LM solver iteration params
	public int maxiter = 500;
	public double lambda = 1e-3;
	public double termepsilon = 1e-1;
	// Mask fits iteration param
	public int iterations = 500;
	public double cutoffdistance = 10;
	public final double[] initialpsf;
	public boolean halfgaussian = false;
	public double Intensityratio;
	public double Inispacing;
	final JProgressBar jpb;
	double percent = 0;

	public void setInispacing(double Inispacing) {

		this.Inispacing = Inispacing;

	}

	public double getInispacing() {

		return Inispacing;
	}

	/**
	 * 
	 * 
	 * @param cutoffdistance
	 *            for recoganizing the faliure of Gaussian Mask fits, if the
	 *            co-ordinates move by more than this distance than predicted by
	 *            the LM solver, call it a faliure of the Mask fits
	 */
	public void setCutoffdistance(double cutoffdistance) {
		this.cutoffdistance = cutoffdistance;
	}

	/**
	 * 
	 * @param halfgaussian
	 *            for Gaussian Mask fits, to cutoff half part of the last
	 *            Gaussian for 1.5 Gaussian fit
	 */
	public void setHalfgaussian(boolean halfgaussian) {
		this.halfgaussian = halfgaussian;
	}

	/**
	 * 
	 * @param intensityratio
	 *            for making an intensity cutoff along the line to make the
	 *            start and end point guess
	 */

	public void setIntensityratio(double intensityratio) {
		Intensityratio = intensityratio;
	}

	/**
	 * 
	 * @param iterations
	 *            for the Gaussian Mask fits
	 */
	public void setIterations(int iterations) {
		this.iterations = iterations;
	}

	/**
	 * 
	 * @param lambda
	 *            for LM solver optimizer
	 */
	public void setLambda(double lambda) {
		this.lambda = lambda;
	}

	/**
	 * 
	 * @param maxiter,
	 *            maximum iterations for the LM solver optimizer
	 */
	public void setMaxiter(int maxiter) {
		this.maxiter = maxiter;
	}

	/**
	 * 
	 * @param termepsilon
	 *            for the LM solver optimizer
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

	public double getLambda() {
		return lambda;
	}

	public double getTermepsilon() {
		return termepsilon;
	}

	public SubpixelLocationLine(final RandomAccessibleInterval<FloatType> source, final Linefinder finder, final double[] initialpsf, final JProgressBar jpb, 
			final int framenumber, final int thirdDimensionSize) {

		finder.checkInput();
		finder.process();
		imgs = finder.getResult();
		this.source = source;
		this.initialpsf = initialpsf;
        this.framenumber = framenumber;
        this.thirdDimensionSize = thirdDimensionSize;
		
		this.jpb = jpb;
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
		startlist = new ArrayList<GaussianLineFitParam>();
		
		percent = (Math.round(100 * (framenumber + 1) / (thirdDimensionSize)));
		
		for (int index = 0; index < imgs.size(); ++index) {

			

			final int Label = imgs.get(index).roilabel;
			final double slope = imgs.get(index).lineparam[0];
			final double intercept = imgs.get(index).lineparam[1];
		
			if (slope != Double.MAX_VALUE && intercept != Double.MAX_VALUE) {
				GaussianLineFitParam returnparam;
				try {
					returnparam = Getfinallineparam(Label, slope, intercept,
							initialpsf);
					startlist.add(returnparam);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					
					
			}
		}

		
		return true;
	}

	@Override
	public ArrayList<GaussianLineFitParam> getResult() {

		return startlist;
	}

	

	private final double[] MakeimprovedLineguess(double slope, double intercept,
			int label, final double[] initialpsf, final double[][] X, final double[] I) {

		long[] newposition = new long[ndims];
		double[] minVal = { Double.MAX_VALUE, Double.MAX_VALUE };
		double[] maxVal = { -Double.MIN_VALUE, -Double.MIN_VALUE };

		RandomAccessibleInterval<FloatType> currentimg = imgs.get(label).Actualroi;

		FinalInterval interval = imgs.get(label).interval;

		currentimg = Views.interval(currentimg, interval);

		final Cursor<FloatType> inputcursor = Views.iterable(currentimg).localizingCursor();

		final double maxintensityline = GetLocalmaxmin.computeMaxIntensity(currentimg);

		while (inputcursor.hasNext()) {

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
		final double[] MinandMax = new double[2 * ndims + 5];

		for (int d = 0; d < ndims; ++d) {

			MinandMax[d] = minVal[d];
			MinandMax[d + ndims] = maxVal[d];
		}

		// This parameter is guess estimate for spacing between the Gaussians
		MinandMax[2 * ndims] = Inispacing;
		MinandMax[2 * ndims + 1] = maxintensityline;
		// This parameter guess estimates the background noise level
		MinandMax[2 * ndims + 2] = 0.0;
		

		for (int i = 0; i < ndims ; ++i){
		MinandMax[ 2 * ndims + 3  + i] = 1 / Math.pow(initialpsf[i], 2);
				
		}
		

		System.out.println("Label: " + label + " " + "Detection: " + " StartX: " + MinandMax[0] + " StartY: "
				+ MinandMax[1] + " EndX: " + MinandMax[2] + " EndY: " + MinandMax[3] + " Initial Sigma X: " + 1 /Math.sqrt(MinandMax[7]) + " Initial Sigma Y: " + 1 /Math.sqrt(MinandMax[8]));

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

	// Get line parameters for fitting line to a line in a label

	public GaussianLineFitParam Getfinallineparam(final int label, final double slope,
			final double intercept, final double[] initialpsf) throws Exception {

		PointSampleList<FloatType> datalist = FitterUtils.gatherfullDataSeed(imgs, label, ndims);
		if (datalist != null) {
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

			final double[] start_param = MakeimprovedLineguess(slope, intercept,  label, initialpsf, X, I);
		
			

				double[] finalparamstart = start_param.clone();
				// LM solver part

				RandomAccessibleInterval<FloatType> currentimg = imgs.get(label).Actualroi;

				FinalInterval interval = imgs.get(label).interval;

				currentimg = Views.interval(currentimg, interval);

			
				final double[] inistartpos = { start_param[0], start_param[1] };
				final double[] iniendpos = { start_param[2], start_param[3] };
				double inicutoffdistanceY = Math.abs(inistartpos[1] - iniendpos[1]);
				double inicutoffdistanceX = Math.abs(inistartpos[0] - iniendpos[0]);
					
				if (inicutoffdistanceY > 1 && inicutoffdistanceX > 1) {
				


						LevenbergMarquardtSolverLine.solve(X, finalparamstart, null, I, new GaussianPSFPoly(),
								lambda, termepsilon, maxiter);
					
				

				}
				else
					finalparamstart = start_param;


				final double[] startpos = { finalparamstart[0], finalparamstart[1] };
				final double[] endpos = { finalparamstart[2], finalparamstart[3] };
				// NaN protection: we prefer returning the crude estimate than
				// NaN
				for (int j = 0; j < finalparamstart.length; j++) {
					if (Double.isNaN(finalparamstart[j]))
						finalparamstart[j] = start_param[j];
					
				}
				


				final double lineIntensity = finalparamstart[2 * ndims + 1];
				final double background = finalparamstart[2 * ndims + 2];
				final double[] sigma = new double[ndims];
				
				
				for (int d = 0; d < ndims; ++d ){
					
					sigma[d] =  1.0 /Math.sqrt(finalparamstart[2 * ndims + 3 + d]);
					
				}

				double[] startfit = startpos;
				double[] endfit = endpos;

				
				FitterUtils.SetProgressBarTime(jpb, percent, framenumber, thirdDimensionSize);

				

				if(finalparamstart!=start_param){
			
				GaussianLineFitParam PointofInterest = new GaussianLineFitParam(startfit, endfit, lineIntensity, sigma, background);
				
			
					System.out.println("End Ax: " + startfit[0] + " End Ay: " + startfit[1]);
					System.out.println("End Bx: " + endfit[0] + " End By: " + endfit[1]);

					System.out.println("SigmaX: " + sigma[0] + " SigmaY: " + sigma[1] );

					IJ.log("Fitted Parameters: " );
					IJ.log("End Ax: " + startfit[0] + " End Ay: " + startfit[1]);
					IJ.log("End Bx: " + endfit[0] + " End By: " + endfit[1]);

					IJ.log("SigmaX: " + sigma[0] + " SigmaY: " + sigma[1] );
					
			
					return PointofInterest;
				}
				
				else
					return null;
		
		}
		else
			return null;

		}

	

	public int Getlabel(final Point linepoint) {

		int currentlabel = Integer.MIN_VALUE;
		for (int index = 0; index < imgs.size(); ++index) {

			if (imgs.get(index).intimg != null) {

				RandomAccess<IntType> intranac = imgs.get(index).intimg.randomAccess();

				intranac.setPosition(linepoint);
				currentlabel = intranac.get().get();

				return currentlabel;

			}

			RandomAccessibleInterval<FloatType> currentimg = imgs.get(index).Actualroi;
			FinalInterval interval = imgs.get(index).interval;
			currentimg = Views.interval(currentimg, interval);
			for (int d = 0; d < ndims; ++d) {

				if (linepoint.getIntPosition(d) >= interval.min(d) && linepoint.getIntPosition(d) <= interval.max(d)) {

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

			distance += Math.min((cordone[d] - cordtwo[d]), 1);

		}
		return (distance);
	}

}
