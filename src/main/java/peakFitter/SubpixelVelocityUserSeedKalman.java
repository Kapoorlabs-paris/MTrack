package peakFitter;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.RoundRectangle2D;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JProgressBar;

import LineModels.GaussianLineds;
import LineModels.GaussianLinedsHF;
import LineModels.GaussianLinefixedds;
import LineModels.GaussianSplinesecorder;
import LineModels.GaussianSplinethirdorder;
import LineModels.Gaussiansplinesecfixedds;
import LineModels.Gaussiansplinethirdorderfixedds;
import LineModels.MTFitFunction;
import LineModels.UseLineModel.UserChoiceModel;
import graphconstructs.KalmanTrackproperties;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.gui.Roi;
import interactiveMT.Interactive_MTDoubleChannel.Whichend;
import labeledObjects.CommonOutputHF;
import labeledObjects.KalmanIndexedlength;
import lineFinder.LinefinderHF;
import mpicbg.imglib.util.Util;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Point;
import net.imglib2.PointSampleList;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.BenchmarkAlgorithm;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;
import peakFitter.GaussianMaskFitMSER.EndfitMSER;
import preProcessing.GetLocalmaxmin;

public class SubpixelVelocityUserSeedKalman extends BenchmarkAlgorithm
		implements OutputAlgorithm<ArrayList<KalmanIndexedlength>> {

	private static final String BASE_ERROR_MSG = "[SubpixelVelocityUserSeed] ";
	private final RandomAccessibleInterval<FloatType> source;
	private final ArrayList<CommonOutputHF> imgs;
	private final ArrayList<KalmanIndexedlength> UserframeKalman;
	private final int ndims;
	private final int framenumber;
	private ArrayList<KalmanIndexedlength> final_paramlistuser;
	private ArrayList<KalmanTrackproperties> startinUserframeKalman;
	public int Accountedframes;
	private final double[] psf;
	private final boolean DoMask;
	private boolean Maskfail = false;
	// LM solver iteration params
	public int maxiter = 500;
	public double lambda = 1e-2;
	public double termepsilon = 1e-3;
	// Mask fits iteration param
	public int iterations = 500;
	public double cutoffdistance = 15;
	public boolean halfgaussian = false;
	public double Intensityratio;
	final JProgressBar jpb;
	final int thirdDimsize;
	private final UserChoiceModel model;
	public double Inispacing;
	
	double percent = 0;
	int maxghost = 5;

	public void setInispacing(double Inispacing) {

		this.Inispacing = Inispacing;

	}

	public double getInispacing() {

		return Inispacing;
	}

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

	public SubpixelVelocityUserSeedKalman(final RandomAccessibleInterval<FloatType> source, final LinefinderHF linefinder,
			final ArrayList<KalmanIndexedlength> UserframeKalman,
			final double[] psf, final int framenumber, final UserChoiceModel model, final boolean DoMask,
			final JProgressBar jpb, final int thirdDimsize) {

		linefinder.checkInput();
		linefinder.process();
		imgs = linefinder.getResult();
		this.source = source;
		this.UserframeKalman = UserframeKalman;
		this.psf = psf;
		this.framenumber = framenumber;
		this.ndims = source.numDimensions();
		this.model = model;
		this.DoMask = DoMask;
		this.jpb = jpb;
		this.thirdDimsize = thirdDimsize;
		
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

	public enum StartorEnd {

		Start, End

	}

	@Override
	public boolean process() {

		Intensityratio = getIntensityratio();
		Inispacing = getInispacing();
		ArrayList<Roi> onlyroi = new ArrayList<Roi>();
		final_paramlistuser = new ArrayList<KalmanIndexedlength>();
		double size = Math.sqrt(psf[0] * psf[0] + psf[1] * psf[1]);
		startinUserframeKalman = new ArrayList<KalmanTrackproperties>();

		for (int index = 0; index < UserframeKalman.size(); ++index) {

			final int oldframenumber = UserframeKalman.get(UserframeKalman.size() - 1).framenumber;
			final int framediff = framenumber - oldframenumber;

		

				percent = (Math.round(100 * (index + 1) / (UserframeKalman.size())));

				final double originalslope = UserframeKalman.get(index).originalslope;

				final double originalintercept = UserframeKalman.get(index).originalintercept;

				final Point linepoint = new Point(ndims);
				linepoint.setPosition(new long[] { (long) UserframeKalman.get(index).currentpos[0],
						(long) UserframeKalman.get(index).currentpos[1] });

				final OvalRoi Bigroi = new OvalRoi(Util.round(UserframeKalman.get(index).currentpos[0] - 2.5),
						Util.round(UserframeKalman.get(index).currentpos[1] - 2.5), Util.round(5), Util.round(5));
				onlyroi.add(Bigroi);
				
				
				
				

				final Point fixedstartpoint = new Point(ndims);
				fixedstartpoint.setPosition(new long[] { (long) UserframeKalman.get(index).fixedpos[0],
						(long) UserframeKalman.get(index).fixedpos[1] });

				int labelstart = FitterUtils.Getlabel(imgs, fixedstartpoint, originalslope, originalintercept);
				KalmanIndexedlength paramnextframestart;

				if (labelstart != Integer.MIN_VALUE)

					paramnextframestart = Getfinaltrackparam(UserframeKalman.get(index), labelstart, psf,
							framenumber);
				else
					paramnextframestart = UserframeKalman.get(index);
				if (paramnextframestart == null)
					paramnextframestart = UserframeKalman.get(index);

				final_paramlistuser.add(paramnextframestart);


				final double[] newstartpoint = paramnextframestart.currentpos;

				final double newstartslope = paramnextframestart.slope;
				final double newstartintercept = paramnextframestart.intercept;

			
				final double[] originalposition = UserframeKalman.get(index).fixedpos;
				final double[] oldstartpoint = UserframeKalman.get(index).currentpos;

				final double oldstartslope = UserframeKalman.get(index).slope;
				final double oldstartintercept = UserframeKalman.get(index).intercept;
				
			
				
				final KalmanTrackproperties startedge = new KalmanTrackproperties(framenumber,labelstart, size, oldstartpoint, originalposition,
						oldstartslope, oldstartintercept, originalslope, originalintercept,
						UserframeKalman.get(index).seedLabel, UserframeKalman.get(index).originalds);
				
				startinUserframeKalman.add(startedge);
			}

		

		
		

		
		
		
		return true;
	}

	@Override
	public ArrayList<KalmanIndexedlength> getResult() {

		ArrayList<KalmanIndexedlength> listpair =  final_paramlistuser;

		return listpair;
	}

	public ArrayList<KalmanTrackproperties> getstartStateVectors() {
		return startinUserframeKalman;
	}

	

	public int getAccountedframes() {

		return Accountedframes;
	}

	private final double[] MakerepeatedLineguess(KalmanIndexedlength iniparam, int label) {

		double[] minVal = new double[ndims];
		double[] maxVal = new double[ndims];
        int labelindex = FitterUtils.getlabelindex(imgs, label);
		
        if (labelindex!=-1){
		
		RandomAccessibleInterval<FloatType> currentimg  = imgs.get(labelindex).Roi;
		FinalInterval interval =  imgs.get(labelindex).interval;
		 

	

		currentimg = Views.interval(currentimg, interval);

		final double maxintensityline = GetLocalmaxmin.computeMaxIntensity(currentimg);
		final double minintensityline = 0;
		Pair<double[], double[]> minmaxpair = FitterUtils.MakeinitialEndpointguess(imgs, maxintensityline,
				Intensityratio, ndims, labelindex, iniparam.slope, iniparam.intercept, iniparam.Curvature,
				iniparam.Inflection);
		for (int d = 0; d < ndims; ++d) {

			minVal[d] = minmaxpair.getA()[d];
			maxVal[d] = minmaxpair.getB()[d];

		}

		if (model == UserChoiceModel.Line) {

			final double[] MinandMax = new double[2 * ndims + 3];

			for (int d = 0; d < ndims; ++d) {

				MinandMax[d] = minVal[d];
				MinandMax[d + ndims] = maxVal[d];
			}

			MinandMax[2 * ndims] = Inispacing;
			MinandMax[2 * ndims + 1] = maxintensityline;
			MinandMax[2 * ndims + 2] = minintensityline;
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

		if (model == UserChoiceModel.Splineordersec) {

			final double[] MinandMax = new double[2 * ndims + 4];

			for (int d = 0; d < ndims; ++d) {

				MinandMax[d] = minVal[d];
				MinandMax[d + ndims] = maxVal[d];
			}

			MinandMax[2 * ndims + 2] = maxintensityline;
			MinandMax[2 * ndims + 3] = minintensityline;
			MinandMax[2 * ndims + 1] = iniparam.Curvature;
			MinandMax[2 * ndims] = Inispacing;

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
		if (model == UserChoiceModel.Splineorderthird) {

			final double[] MinandMax = new double[2 * ndims + 5];

			for (int d = 0; d < ndims; ++d) {

				MinandMax[d] = minVal[d];
				MinandMax[d + ndims] = maxVal[d];
			}

			MinandMax[2 * ndims + 2] = iniparam.Inflection;
			MinandMax[2 * ndims + 3] = maxintensityline;
			MinandMax[2 * ndims + 4] = minintensityline;
			MinandMax[2 * ndims + 1] = iniparam.Curvature;
			MinandMax[2 * ndims] = Inispacing;

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
		
		else 
			return null;
	}

	public KalmanIndexedlength Getfinaltrackparam(final KalmanIndexedlength iniparam, final int label, final double[] psf,
			final int rate) {

		final double[] LMparam = MakerepeatedLineguess(iniparam, label);
		if (LMparam == null)
			return iniparam;

		else {

			final double[] inipos = iniparam.currentpos;

			int labelindex = FitterUtils.getlabelindex(imgs, label);
			
			RandomAccessibleInterval<FloatType> currentimg = imgs.get(labelindex).Actualroi;

			FinalInterval interval = imgs.get(labelindex).interval;

			currentimg = Views.interval(currentimg, interval);

			final double[] fixed_param = new double[ndims + 3];

			for (int d = 0; d < ndims; ++d) {

				fixed_param[d] = 1.0 / Math.pow(psf[d], 2);

			}
			fixed_param[ndims] = iniparam.originalslope;
			fixed_param[ndims + 1] = iniparam.originalintercept;
			fixed_param[ndims + 2] = Inispacing;

			PointSampleList<FloatType> datalist = FitterUtils.gatherfullData(imgs, label, ndims);

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

			Accountedframes = framenumber;

			System.out.println("Label: " + label + " " + "Initial guess: " + " StartX: " + LMparam[0] + " StartY: "
					+ LMparam[1] + " EndX: " + LMparam[2] + " EndY: " + LMparam[3]);

			final double[] safeparam = LMparam.clone();
			MTFitFunction UserChoiceFunction = null;
			if (model == UserChoiceModel.Line) {
				fixed_param[ndims] = iniparam.slope;
				fixed_param[ndims + 1] = iniparam.intercept;
				UserChoiceFunction = new GaussianLineds();

			}

			if (model == UserChoiceModel.Splineordersec) {
				fixed_param[ndims] = iniparam.slope;
				fixed_param[ndims + 1] = iniparam.intercept;

				UserChoiceFunction = new GaussianSplinesecorder();

			}

			if (model == UserChoiceModel.Splineorderthird) {
				fixed_param[ndims] = iniparam.slope;
				fixed_param[ndims + 1] = iniparam.intercept;

				UserChoiceFunction = new GaussianSplinethirdorder();

			}
			final double[] inistartpos = { LMparam[0], LMparam[1] };
			final double[] iniendpos = { LMparam[2], LMparam[3] };

			double inicutoffdistanceY = Math.abs(inistartpos[1] - iniendpos[1]);
			double inicutoffdistanceX = Math.abs(inistartpos[0] - iniendpos[0]);
			// LM solver part
			if (inicutoffdistanceY > 0 && inicutoffdistanceX > 0) {
				// LM solver part

				try {
					LevenbergMarquardtSolverLine.solve(X, LMparam, fixed_param, I, UserChoiceFunction, lambda,
							termepsilon, maxiter);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			} else {
				for (int j = 0; j < LMparam.length; j++) {

					LMparam[j] = safeparam[j];
				}

			}

			for (int j = 0; j < LMparam.length; j++) {
				if (Double.isNaN(LMparam[j]))
					LMparam[j] = safeparam[j];
			}

			double[] startpos = new double[ndims];
			double[] endpos = new double[ndims];

			for (int d = 0; d < ndims; ++d) {
				startpos[d] = LMparam[d];
				endpos[d] = LMparam[d + ndims];

			}

			final int seedLabel = iniparam.seedLabel;

			if (model == UserChoiceModel.Line) {
				
					double ds = LMparam[2 * ndims];
					double Intensity = LMparam[2 * ndims + 1];
					final double background = LMparam[2 * ndims + 2];
					final double newslope = (endpos[1] - startpos[1]) / (endpos[0] - startpos[0]);
					final double newintercept = endpos[1] - newslope * endpos[0];
					double dx = ds / Math.sqrt(1 + (newslope) * (newslope));
					double dy = (newslope) * dx;
					double[] dxvector = { dx, dy };
					double sigmas = 0;

					for (int d = 0; d < ndims; ++d) {

						sigmas += psf[d] * psf[d];
					}
					final int numgaussians = (int) Math.max(0.5 * Math.round(Math.sqrt(sigmas) / ds), 2);

					double[] endfit = endpos;
					double[] startfit = startpos;

					if (DoMask) {

						try {
							endfit = GaussianMaskFitMSER.sumofgaussianMaskFit(currentimg, endpos.clone(), psf,
									numgaussians, iterations, dxvector, newslope, newintercept, Intensity, halfgaussian,
									EndfitMSER.EndfitMSER, label, background, Intensityratio);
							startfit = GaussianMaskFitMSER.sumofgaussianMaskFit(currentimg, startpos.clone(), psf,
									numgaussians, iterations, dxvector, newslope, newintercept, Intensity, halfgaussian,
									EndfitMSER.StartfitMSER, label, background, Intensityratio);

						} catch (Exception e) {
							e.printStackTrace();
						}

						for (int d = 0; d < ndims; ++d) {
							if (Double.isNaN(endfit[d])) {
								Maskfail = true;
								endfit[d] = endpos[d];

							}

						}

						if (Math.abs(endpos[0] - endfit[0]) >= cutoffdistance
								|| Math.abs(endpos[1] - endfit[1]) >= cutoffdistance) {
							Maskfail = true;
							for (int d = 0; d < ndims; ++d) {
								endfit[d] = endpos[d];
							}
						}

						if (Math.abs(startpos[0] - startfit[0]) >= cutoffdistance
								|| Math.abs(startpos[1] - startfit[1]) >= cutoffdistance) {
							Maskfail = true;

							for (int d = 0; d < ndims; ++d) {
								startfit[d] = startpos[d];
							}
						}
						for (int d = 0; d < ndims; ++d) {
							if (Double.isNaN(startfit[d])) {
								Maskfail = true;
								startfit[d] = startpos[d];

							}
						}

					}
					double dist = Distance(iniparam.fixedpos, startfit) - Distance(iniparam.fixedpos, endfit);

					for (int d = 0; d < ndims; ++d)
						endfit[d] = (dist > 0) ? startfit[d] : endfit[d];

					KalmanIndexedlength PointofInterest = new KalmanIndexedlength(label, seedLabel, framenumber, LMparam[2 * ndims],
							LMparam[2 * ndims + 1], LMparam[2 * ndims + 2], endfit, iniparam.fixedpos, newslope,
							newintercept, iniparam.originalslope, iniparam.originalintercept, iniparam.originalds);

					if (Maskfail == true)
						System.out.println("New XLM: " + endfit[0] + " New YLM: " + endfit[1]);
					else
						System.out.println("New XMask: " + endfit[0] + " New YMask: " + endfit[1]);
					System.out.println("Number of Gaussians used: " + numgaussians + "ds: " + ds);

					FitterUtils.SetProgressBarTime(jpb, percent, framenumber, thirdDimsize);

					return PointofInterest;

				}
			

			else if (model == UserChoiceModel.Splineordersec) {
				
					final double Curvature = LMparam[2 * ndims + 1];

					final double currentintercept = iniparam.originalintercept;
					final double ds = (LMparam[2 * ndims]);
					final double lineIntensity = LMparam[2 * ndims + 2];
					final double background = LMparam[2 * ndims + 3];
					System.out.println("Curvature: " + Curvature);
					final double newslope = (endpos[1] - startpos[1]) / (endpos[0] - startpos[0])
							- Curvature * (endpos[0] + startpos[0]);

					double[] endfit = endpos;
					double[] startfit = startpos;

					double dxend = ds / Math
							.sqrt(1 + (newslope + 2 * Curvature * endpos[0]) * (newslope + 2 * Curvature * endpos[0]));
					double dyend = (newslope + 2 * Curvature * endpos[0]) * dxend;
					double[] dxvectorend = { dxend, dyend };

					double dxstart = ds / Math.sqrt(
							1 + (newslope + 2 * Curvature * startpos[0]) * (newslope + 2 * Curvature * startpos[0]));
					double dystart = (newslope + 2 * Curvature * startpos[0]) * dxstart;
					double[] dxvectorstart = { dxstart, dystart };

					double sigmas = 0;

					for (int d = 0; d < ndims; ++d) {

						sigmas += psf[d] * psf[d];
					}

					final int numgaussians = (int) Math.max(0.5 * Math.round(Math.sqrt(sigmas) / ds), 2);

					if (DoMask) {

						try {

							startfit = GaussianMaskFitMSER.sumofgaussianMaskFit(currentimg, startpos.clone(), psf,
									numgaussians, iterations, dxvectorstart, newslope, currentintercept, lineIntensity,
									halfgaussian, EndfitMSER.StartfitMSER, label, background, Intensityratio);
							endfit = GaussianMaskFitMSER.sumofgaussianMaskFit(currentimg, endpos.clone(), psf,
									numgaussians, iterations, dxvectorend, newslope, currentintercept, lineIntensity,
									halfgaussian, EndfitMSER.EndfitMSER, label, background, Intensityratio);

						} catch (Exception e) {
							e.printStackTrace();
						}

						for (int d = 0; d < ndims; ++d) {
							if (Double.isNaN(endfit[d])) {
								Maskfail = true;
								endfit[d] = endpos[d];

							}

						}

						if (Math.abs(endpos[0] - endfit[0]) >= cutoffdistance
								|| Math.abs(endpos[1] - endfit[1]) >= cutoffdistance) {
							Maskfail = true;
							for (int d = 0; d < ndims; ++d) {
								endfit[d] = endpos[d];
							}
						}

						for (int d = 0; d < ndims; ++d) {
							if (Double.isNaN(startfit[d])) {
								Maskfail = true;
								// System.out.println("Mask fits fail, returning
								// LM solver results!");
								startfit[d] = startpos[d];

							}
						}

						if (Math.abs(startpos[0] - startfit[0]) >= cutoffdistance
								|| Math.abs(startpos[1] - startfit[1]) >= cutoffdistance) {
							// System.out.println("Mask fits fail, returning LM
							// solver results!");
							Maskfail = true;
							for (int d = 0; d < ndims; ++d) {
								startfit[d] = startpos[d];
							}
						}

					}

					double dist = Distance(iniparam.fixedpos, startfit) - Distance(iniparam.fixedpos, endfit);

					for (int d = 0; d < ndims; ++d)
						endfit[d] = (dist > 0) ? startfit[d] : endfit[d];

					KalmanIndexedlength PointofInterest = new KalmanIndexedlength(label, seedLabel, framenumber, ds, lineIntensity,
							background, endfit, iniparam.fixedpos, newslope, currentintercept, iniparam.originalslope,
							iniparam.originalintercept, Curvature, 0, iniparam.originalds);
					if (Maskfail == true)
						System.out.println("New XLM: " + endfit[0] + " New YLM: " + endfit[1]);
					else
						System.out.println("New XMask: " + endfit[0] + " New YMask: " + endfit[1]);
					System.out.println("Number of Gaussians used: " + (numgaussians) + " " + ds);

					FitterUtils.SetProgressBarTime(jpb, percent, framenumber, thirdDimsize);

					return PointofInterest;

				
			} else if (model == UserChoiceModel.Splineorderthird) {
				

					final double Curvature = LMparam[2 * ndims + 1];
					final double Inflection = LMparam[2 * ndims + 2];

					final double currentintercept = iniparam.originalintercept;
					final double ds = (LMparam[2 * ndims]);
					final double lineIntensity = LMparam[2 * ndims + 3];
					final double background = LMparam[2 * ndims + 4];
					System.out.println("Curvature: " + Curvature);
					System.out.println("Inflection: " + Inflection);
					final double newslope = (endpos[1] - startpos[1]) / (endpos[0] - startpos[0])
							- Curvature * (endpos[0] + startpos[0]) - Inflection
									* (startpos[0] * startpos[0] + endpos[0] * endpos[0] + startpos[0] * endpos[0]);

					double[] endfit = endpos;
					double[] startfit = startpos;

					double dxend = ds / Math
							.sqrt(1 + (newslope + 2 * Curvature * endpos[0] + 3 * Inflection * endpos[0] * endpos[0])
									* (newslope + 2 * Curvature * endpos[0] + 3 * Inflection * endpos[0] * endpos[0]));
					double dyend = (newslope + 2 * Curvature * endpos[0] + 3 * Inflection * endpos[0] * endpos[0])
							* dxend;
					double[] dxvectorend = { dxend, dyend };

					double dxstart = ds / Math.sqrt(1 + (newslope + 2 * Curvature * startpos[0]
							+ 3 * Inflection * startpos[0] * startpos[0])
							* (newslope + 2 * Curvature * startpos[0] + 3 * Inflection * startpos[0] * startpos[0]));
					double dystart = (newslope + 2 * Curvature * startpos[0]
							+ 3 * Inflection * startpos[0] * startpos[0]) * dxstart;
					double[] dxvectorstart = { dxstart, dystart };

					double sigmas = 0;

					for (int d = 0; d < ndims; ++d) {

						sigmas += psf[d] * psf[d];
					}

					final int numgaussians = (int) Math.max(0.5 * Math.round(Math.sqrt(sigmas) / ds), 2);

					if (DoMask) {

						try {
							startfit = GaussianMaskFitMSER.sumofgaussianMaskFit(currentimg, startpos.clone(), psf,
									numgaussians, iterations, dxvectorstart, newslope, currentintercept, lineIntensity,
									halfgaussian, EndfitMSER.StartfitMSER, label, background, Intensityratio);

							endfit = GaussianMaskFitMSER.sumofgaussianMaskFit(currentimg, endpos.clone(), psf,
									numgaussians, iterations, dxvectorend, newslope, currentintercept, lineIntensity,
									halfgaussian, EndfitMSER.EndfitMSER, label, background, Intensityratio);

						} catch (Exception e) {
							e.printStackTrace();
						}

						for (int d = 0; d < ndims; ++d) {
							if (Double.isNaN(endfit[d])) {
								Maskfail = true;
								endfit[d] = endpos[d];

							}

						}

						if (Math.abs(endpos[0] - endfit[0]) >= cutoffdistance
								|| Math.abs(endpos[1] - endfit[1]) >= cutoffdistance) {
							Maskfail = true;
							for (int d = 0; d < ndims; ++d) {
								endfit[d] = endpos[d];
							}
						}

						for (int d = 0; d < ndims; ++d) {
							if (Double.isNaN(startfit[d])) {
								Maskfail = true;
								// System.out.println("Mask fits fail, returning
								// LM solver results!");
								startfit[d] = startpos[d];

							}
						}

						if (Math.abs(startpos[0] - startfit[0]) >= cutoffdistance
								|| Math.abs(startpos[1] - startfit[1]) >= cutoffdistance) {
							// System.out.println("Mask fits fail, returning LM
							// solver results!");
							Maskfail = true;
							for (int d = 0; d < ndims; ++d) {
								startfit[d] = startpos[d];
							}
						}

					}

					double dist = Distance(iniparam.fixedpos, startfit) - Distance(iniparam.fixedpos, endfit);

					for (int d = 0; d < ndims; ++d)
						endfit[d] = (dist > 0) ? startfit[d] : endfit[d];

					KalmanIndexedlength PointofInterest = new KalmanIndexedlength(label, seedLabel, framenumber, ds, lineIntensity,
							background, endfit, iniparam.fixedpos, newslope, currentintercept, iniparam.originalslope,
							iniparam.originalintercept, Curvature, Inflection, iniparam.originalds);
					if (Maskfail == true)
						System.out.println("New XLM: " + endfit[0] + " New YLM: " + endfit[1]);
					else
						System.out.println("New XMask: " + endfit[0] + " New YMask: " + endfit[1]);
					System.out.println("Number of Gaussians used: " + (numgaussians) + " " + ds);

					FitterUtils.SetProgressBarTime(jpb, percent, framenumber, thirdDimsize);

					return PointofInterest;

				
			} else
				return null;

		}
	}



	

	public static double Distance(final double[] cordone, final double[] cordtwo) {

		double distance = 0;

		int ndims = cordone.length;
		for (int d = 0; d < ndims; ++d) {

			distance += Math.pow((cordone[d] - cordtwo[d]), 2);

		}
		return Math.sqrt(distance);
	}

	public static double sqDistance(final double[] cordone, final double[] cordtwo) {

		double distance = 0;
		int ndims = cordone.length;
		for (int d = 0; d < ndims; ++d) {

			distance += Math.pow((cordone[d] - cordtwo[d]), 2);

		}
		return (distance);
	}

	public static double Xcorddist(final double Xcordone, final double Xcordtwo) {

		double distance = Math.abs(Xcordone - Xcordtwo);

		return distance;
	}

	public static double dsdist(final double[] cordone, final double[] cordtwo) {

		double distance = Math.pow((cordone[0] - cordtwo[0]), 2) + Math.pow((cordone[1] - cordtwo[1]), 2);

		return distance;
	}

	public static double[] Midpoint(final double[] cordone, final double[] cordtwo) {
		int ndims = cordone.length;
		final double[] midpoint = new double[ndims];

		for (int d = 0; d < ndims; ++d) {

			midpoint[d] = (cordone[d] + cordtwo[d]) / 2;
		}

		return midpoint;

	}

}