/*-
 * #%L
 * Microtubule tracker.
 * %%
 * Copyright (C) 2017 MTrack developers.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package peakFitter;

import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JProgressBar;

import LineModels.GaussianLineds;
import LineModels.GaussianSplinesecorder;
import LineModels.GaussianSplinethirdorder;
import LineModels.MTFitFunction;
import LineModels.UseLineModel.UserChoiceModel;
import graphconstructs.Trackproperties;
import ij.IJ;
import ij.gui.OvalRoi;
import ij.gui.Roi;
import interactiveMT.Interactive_MTSingleChannel.Whichend;
import labeledObjects.CommonOutputHF;
import labeledObjects.Indexedlength;
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
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;
import peakFitter.GaussianMaskFitMSER.EndfitMSER;

public class SubpixelVelocityCline extends BenchmarkAlgorithm
		implements OutputAlgorithm<Pair<ArrayList<Indexedlength>, ArrayList<Indexedlength>>> {

	private static final String BASE_ERROR_MSG = "[SubpixelVelocity] ";
	private final RandomAccessibleInterval<FloatType> source;
	private final ArrayList<CommonOutputHF> imgs;
	private final ArrayList<Indexedlength> PrevFrameparamstart;
	private final ArrayList<Indexedlength> PrevFrameparamend;
	private final int ndims;
	private final int framenumber;
	private ArrayList<Indexedlength> final_paramliststart;
	private ArrayList<Indexedlength> final_paramlistend;
	private ArrayList<Trackproperties> startinframe;
	private ArrayList<Trackproperties> endinframe;
	private final double[] psf;
	private final UserChoiceModel model;

	final JProgressBar jpb;
	private boolean Maskfail = false;
	// LM solver iteration params
	public int maxiter = 200;
	public double lambda = 1e-3;
	public double termepsilon = 1e-2;
	// Mask fits iteration param
	int iterations = 200;
	private final boolean DoMask;
	public double cutoffdistance = 250;
	public double maxdist;
	public boolean halfgaussian = false;
	public double Intensityratio;
	private final HashMap<Integer, Whichend> Trackstart;
	public double Inispacing;
	final int thirdDimsize;
	final int startframe;
	double percent = 0;
	int maxghost = 5;

	public final int numgaussians;

	public void setMaxdist(double maxdist) {

		this.maxdist = maxdist;
	}

	public double getMaxdist() {

		return maxdist;
	}

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

	public SubpixelVelocityCline(final RandomAccessibleInterval<FloatType> source, final LinefinderHF finder,
			final ArrayList<Indexedlength> PrevFrameparamstart, final ArrayList<Indexedlength> PrevFrameparamend,
			final double[] psf, final int framenumber, final UserChoiceModel model, final boolean DoMask,
			final HashMap<Integer, Whichend> Trackstart, final JProgressBar jpb, final int startframe,
			final int thirdDimsize, final int numgaussians) {
		finder.checkInput();
		finder.process();
		imgs = finder.getResult();
		this.source = source;
		this.model = model;
		this.DoMask = DoMask;
		this.jpb = jpb;
		this.PrevFrameparamstart = PrevFrameparamstart;
		this.PrevFrameparamend = PrevFrameparamend;
		this.psf = psf;
		this.framenumber = framenumber;
		this.ndims = source.numDimensions();
		this.Trackstart = Trackstart;
		this.thirdDimsize = thirdDimsize;
		this.startframe = startframe;
		this.numgaussians = numgaussians;

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
		Intensityratio = getIntensityratio();
		Inispacing = getInispacing();
		ArrayList<Roi> onlyroi = new ArrayList<Roi>();
		final_paramliststart = new ArrayList<Indexedlength>();
		final_paramlistend = new ArrayList<Indexedlength>();
		startinframe = new ArrayList<Trackproperties>();
		endinframe = new ArrayList<Trackproperties>();

		for (int index = 0; index < PrevFrameparamstart.size(); ++index) {

			if (Trackstart.get(PrevFrameparamstart.get(index).seedLabel) == Whichend.both) {
				percent = (Math.round(100 * (index + 1) / (PrevFrameparamstart.size())));

				final double originalslope = PrevFrameparamstart.get(index).originalslope;

				final double originalintercept = PrevFrameparamstart.get(index).originalintercept;

				final double originalslopeend = PrevFrameparamend.get(index).originalslope;

				final double originalinterceptend = PrevFrameparamend.get(index).originalintercept;

				final Point linepoint = new Point(ndims);
				linepoint.setPosition(new long[] { (long) PrevFrameparamstart.get(index).currentpos[0],
						(long) PrevFrameparamstart.get(index).currentpos[1] });

				final Point fixedstartpoint = new Point(ndims);
				fixedstartpoint.setPosition(new long[] { (long) PrevFrameparamstart.get(index).fixedpos[0],
						(long) PrevFrameparamstart.get(index).fixedpos[1] });

				final Point fixedstartpointend = new Point(ndims);
				fixedstartpointend.setPosition(new long[] { (long) PrevFrameparamend.get(index).fixedpos[0],
						(long) PrevFrameparamend.get(index).fixedpos[1] });

				ArrayList<Integer> labelstart = FitterUtils.Getlabel(imgs, fixedstartpoint, originalslope,
						originalintercept);

				ArrayList<Integer> labelend = FitterUtils.Getlabel(imgs, fixedstartpointend, originalslopeend,
						originalinterceptend);

				Indexedlength paramnextframestart;
				Indexedlength paramnextframeend;
				Pair<Indexedlength, Indexedlength> paramnextframe;

				int labelindex = Integer.MIN_VALUE;
				int labelindexend = Integer.MIN_VALUE;

				if (labelstart.size() > 0)

					labelindex = labelstart.get(0);

				if (labelend.size() > 0)

					labelindexend = labelend.get(0);

				System.out.println(labelindex + " "+ labelindexend);
				if (labelindex != Integer.MIN_VALUE && labelindexend!= Integer.MIN_VALUE) {
					paramnextframe = Getfinaltrackparam(PrevFrameparamstart.get(index), labelstart.get(0), psf,
							framenumber);

					paramnextframestart = paramnextframe.getA();
					paramnextframeend = paramnextframe.getB();

					// Start point
					double[] currentposinistart = paramnextframestart.currentpos;
					double[] previousposinistart = PrevFrameparamstart.get(index).currentpos;

					// End point
					double[] currentposiniend = paramnextframeend.currentpos;
					double[] previousposiniend = PrevFrameparamend.get(index).currentpos;

					double distmin = Distance(currentposinistart, previousposinistart);
					double distminend = Distance(currentposiniend, previousposiniend);

					if (labelstart.size() > 1) {
						for (int j = 1; j < labelstart.size(); ++j) {
							System.out.println("Fitting multiple Labels");
							Indexedlength teststart = Getfinaltrackparam(PrevFrameparamstart.get(index),
									labelstart.get(j), psf, framenumber, StartorEnd.Start);
							double[] currentposstart = teststart.currentpos;

							double dist = Distance(currentposstart, previousposinistart);
							if (dist < distmin && dist != 0) {
								distmin = dist;
								labelindex = j;
								paramnextframestart = teststart;

							}

							if (distmin == 0) {
								labelindex = labelstart.get(j);
								paramnextframestart = teststart;

							}

						}
					}
					if (labelend.size() > 1) {
						for (int j = 1; j < labelend.size(); ++j) {
							System.out.println("Fitting multiple Labels");
							Indexedlength testend = Getfinaltrackparam(PrevFrameparamend.get(index),
									labelend.get(j), psf, framenumber, StartorEnd.End);
							double[] currentposend = testend.currentpos;

							double distend = Distance(currentposend, previousposiniend);
							if (distend < distminend && distend != 0) {
								distminend = distend;
								labelindexend = j;
								paramnextframeend = testend;

							}

							if (distminend == 0) {
								labelindexend = labelend.get(j);
								paramnextframeend = testend;

							}

						}
					}
					

				}

				else{
					paramnextframestart = PrevFrameparamstart.get(index);
					paramnextframeend = PrevFrameparamend.get(index);
					 paramnextframe = new ValuePair<Indexedlength,
					 Indexedlength>(PrevFrameparamstart.get(index),
					 PrevFrameparamend.get(index));
				}
				

				paramnextframestart = paramnextframe.getA();
				paramnextframeend = paramnextframe.getB();

				double[] oldstartpointstart = PrevFrameparamstart.get(index).currentpos;
				double[] newstartpointstart = paramnextframestart.currentpos;
				double newstartslope = paramnextframestart.slope;
				double newstartintercept = paramnextframestart.intercept;

				if (framenumber > startframe + 1 || Math.abs(newstartslope) != Double.NaN) {
					// TCASM
					double oldslope = (PrevFrameparamstart.get(index).currentpos[1]
							- PrevFrameparamstart.get(index).fixedpos[1])
							/ (PrevFrameparamstart.get(index).currentpos[0]
									- PrevFrameparamstart.get(index).fixedpos[0]);

					double oldintercept = PrevFrameparamstart.get(index).currentpos[1]
							- oldslope * PrevFrameparamstart.get(index).currentpos[0];

					double dist = (paramnextframestart.currentpos[1] - oldslope * paramnextframestart.currentpos[0]
							- oldintercept) / Math.sqrt(1 + oldslope * oldslope);

					// Math.toDegrees(Math.atan(((newslope - oldslope)/(1 +
					// newslope * oldslope))));

					// (paramnextframe.currentpos[1] - oldslope *
					// paramnextframe.currentpos[0] -oldintercept)/Math.sqrt(1 +
					// oldslope *oldslope);
					// Math.toDegrees(Math.atan(((newslope - oldslope)/(1 +
					// newslope * oldslope))));

					// Math.toDegrees(Math.atan(Math.toRadians((newslope -
					// oldslope)/(1 + newslope * oldslope))));

					if (dist != Double.NaN) {

						// TCASM
						if (Math.abs(dist) > maxdist) {
							IJ.log("Miss Assingment detected, activating TCASM layer at " + " " + oldstartpointstart[0]
									+ " " + oldstartpointstart[1]);
							paramnextframestart = PrevFrameparamstart.get(index);
							newstartpointstart = oldstartpointstart;
							newstartslope = PrevFrameparamstart.get(index).slope;
							newstartintercept = PrevFrameparamstart.get(index).intercept;
						}

					}
				}

				final_paramliststart.add(paramnextframestart);

				final Trackproperties startedge = new Trackproperties(framenumber, labelindex, oldstartpointstart,
						newstartpointstart, newstartslope, newstartintercept, originalslope, originalintercept,
						PrevFrameparamstart.get(index).seedLabel, PrevFrameparamstart.get(index).fixedpos,
						PrevFrameparamstart.get(index).originalds);

				startinframe.add(startedge);

				double[] oldstartpointend = PrevFrameparamend.get(index).currentpos;
				double[] newstartpointend = paramnextframeend.currentpos;
				double newendslope = paramnextframeend.slope;
				double newendintercept = paramnextframeend.intercept;

				if (framenumber > startframe + 1 || Math.abs(newendslope) != Double.NaN) {
					// TCASM
					double oldslope = (PrevFrameparamend.get(index).currentpos[1]
							- PrevFrameparamend.get(index).fixedpos[1])
							/ (PrevFrameparamend.get(index).currentpos[0] - PrevFrameparamend.get(index).fixedpos[0]);

					double oldintercept = PrevFrameparamend.get(index).currentpos[1]
							- oldslope * PrevFrameparamend.get(index).currentpos[0];

					double dist = (paramnextframeend.currentpos[1] - oldslope * paramnextframeend.currentpos[0]
							- oldintercept) / Math.sqrt(1 + oldslope * oldslope);
					// Math.toDegrees(Math.atan(((newslope - oldslope)/(1 +
					// newslope * oldslope))));

					// (paramnextframe.currentpos[1] - oldslope *
					// paramnextframe.currentpos[0] -oldintercept)/Math.sqrt(1 +
					// oldslope *oldslope);
					// Math.toDegrees(Math.atan(((newslope - oldslope)/(1 +
					// newslope * oldslope))));

					// Math.toDegrees(Math.atan(Math.toRadians((newslope -
					// oldslope)/(1 + newslope * oldslope))));

					if (dist != Double.NaN) {

						// TCASM
						if (Math.abs(dist) > maxdist) {
							IJ.log("Miss Assingment detected, activating TCASM layer at " + " " + oldstartpointend[0]
									+ " " + oldstartpointend[1]);
							paramnextframeend = PrevFrameparamend.get(index);
							newstartpointend = oldstartpointend;
							newendslope = PrevFrameparamend.get(index).slope;
							newendintercept = PrevFrameparamend.get(index).intercept;
						}

					}
				}

				final_paramlistend.add(paramnextframeend);

				final Trackproperties endedge = new Trackproperties(framenumber, labelindexend, oldstartpointend,
						newstartpointend, newendslope, newendintercept, originalslope, originalintercept,
						PrevFrameparamend.get(index).seedLabel, PrevFrameparamend.get(index).fixedpos,
						PrevFrameparamend.get(index).originalds);

				endinframe.add(endedge);

			}

		}

		for (int index = 0; index < PrevFrameparamstart.size(); ++index) {

			if (Trackstart.get(PrevFrameparamstart.get(index).seedLabel) == Whichend.start) {
				percent = (Math.round(100 * (index + 1) / (PrevFrameparamstart.size())));

				final double originalslope = PrevFrameparamstart.get(index).originalslope;

				final double originalintercept = PrevFrameparamstart.get(index).originalintercept;

				final Point linepoint = new Point(ndims);
				linepoint.setPosition(new long[] { (long) PrevFrameparamstart.get(index).currentpos[0],
						(long) PrevFrameparamstart.get(index).currentpos[1] });

				final OvalRoi Bigroi = new OvalRoi(Util.round(PrevFrameparamstart.get(index).currentpos[0] - 2.5),
						Util.round(PrevFrameparamstart.get(index).currentpos[1] - 2.5), Util.round(5), Util.round(5));
				onlyroi.add(Bigroi);

				final Point fixedstartpoint = new Point(ndims);
				fixedstartpoint.setPosition(new long[] { (long) PrevFrameparamstart.get(index).fixedpos[0],
						(long) PrevFrameparamstart.get(index).fixedpos[1] });

				ArrayList<Integer> labelstart = FitterUtils.Getlabel(imgs, fixedstartpoint, originalslope,
						originalintercept);
				Indexedlength paramnextframe;

				int labelindex = Integer.MIN_VALUE;

				if (labelstart.size() > 0)

					labelindex = labelstart.get(0);

				if (labelindex != Integer.MIN_VALUE) {
					paramnextframe = Getfinaltrackparam(PrevFrameparamstart.get(index), labelstart.get(0), psf,
							framenumber, StartorEnd.Start);

					double[] currentposini = paramnextframe.currentpos;
					double[] previousposini = PrevFrameparamstart.get(index).currentpos;

					double distmin = Distance(currentposini, previousposini);
					if (labelstart.size() > 1) {
						for (int j = 0; j < labelstart.size(); ++j) {
							System.out.println("Fitting multiple Labels");
							Indexedlength test = Getfinaltrackparam(PrevFrameparamstart.get(index), labelstart.get(j),
									psf, framenumber, StartorEnd.Start);
							double[] currentpos = test.currentpos;

							double dist = Distance(currentpos, previousposini);

							if (dist < distmin && dist != 0) {
								distmin = dist;
								labelindex = j;
								paramnextframe = test;

							}

							if (distmin == 0) {
								labelindex = labelstart.get(j);
								paramnextframe = test;

							}
						}
					}
				}

				else
					paramnextframe = PrevFrameparamstart.get(index);
				if (paramnextframe == null)
					paramnextframe = PrevFrameparamstart.get(index);
				double[] oldstartpoint = PrevFrameparamstart.get(index).currentpos;
				double[] newstartpoint = paramnextframe.currentpos;
				double newstartslope = paramnextframe.slope;
				double newstartintercept = paramnextframe.intercept;

				if (framenumber > startframe + 1 || Math.abs(newstartslope) != Double.NaN) {
					// TCASM
					double oldslope = (PrevFrameparamstart.get(index).currentpos[1]
							- PrevFrameparamstart.get(index).fixedpos[1])
							/ (PrevFrameparamstart.get(index).currentpos[0]
									- PrevFrameparamstart.get(index).fixedpos[0]);

					double oldintercept = PrevFrameparamstart.get(index).currentpos[1]
							- oldslope * PrevFrameparamstart.get(index).currentpos[0];

					double dist = (paramnextframe.currentpos[1] - oldslope * paramnextframe.currentpos[0]
							- oldintercept) / Math.sqrt(1 + oldslope * oldslope);

					// Math.toDegrees(Math.atan(((newslope - oldslope)/(1 +
					// newslope * oldslope))));

					// (paramnextframe.currentpos[1] - oldslope *
					// paramnextframe.currentpos[0] -oldintercept)/Math.sqrt(1 +
					// oldslope *oldslope);
					// Math.toDegrees(Math.atan(((newslope - oldslope)/(1 +
					// newslope * oldslope))));

					// Math.toDegrees(Math.atan(Math.toRadians((newslope -
					// oldslope)/(1 + newslope * oldslope))));

					if (dist != Double.NaN) {

						// TCASM
						if (Math.abs(dist) > maxdist) {
							IJ.log("Miss Assingment detected, activating TCASM layer at " + " " + oldstartpoint[0] + " "
									+ oldstartpoint[1]);
							paramnextframe = PrevFrameparamstart.get(index);
							newstartpoint = oldstartpoint;
							newstartslope = PrevFrameparamstart.get(index).slope;
							newstartintercept = PrevFrameparamstart.get(index).intercept;
						}

					}
				}

				final_paramliststart.add(paramnextframe);

				final Trackproperties startedge = new Trackproperties(framenumber, labelindex, oldstartpoint,
						newstartpoint, newstartslope, newstartintercept, originalslope, originalintercept,
						PrevFrameparamstart.get(index).seedLabel, PrevFrameparamstart.get(index).fixedpos,
						PrevFrameparamstart.get(index).originalds);

				startinframe.add(startedge);
			}

		}

		for (int index = 0; index < PrevFrameparamend.size(); ++index) {
			final int oldframenumber = PrevFrameparamend.get(PrevFrameparamend.size() - 1).framenumber;

			if (Trackstart.get(PrevFrameparamend.get(index).seedLabel) == Whichend.end) {
				percent = (Math.round(100 * (index + 1) / (PrevFrameparamend.size())));

				Point secondlinepoint = new Point(ndims);
				secondlinepoint.setPosition(new long[] { (long) PrevFrameparamend.get(index).currentpos[0],
						(long) PrevFrameparamend.get(index).currentpos[1] });
				Point fixedendpoint = new Point(ndims);
				fixedendpoint.setPosition(new long[] { (long) PrevFrameparamend.get(index).fixedpos[0],
						(long) PrevFrameparamend.get(index).fixedpos[1] });

				final OvalRoi Bigroi = new OvalRoi(Util.round(PrevFrameparamend.get(index).currentpos[0] - 2.5),
						Util.round(PrevFrameparamend.get(index).currentpos[1] - 2.5), Util.round(5), Util.round(5));
				onlyroi.add(Bigroi);

				final double originalslopeend = PrevFrameparamend.get(index).originalslope;

				final double originalinterceptend = PrevFrameparamend.get(index).originalintercept;
				ArrayList<Integer> labelend = FitterUtils.Getlabel(imgs, fixedendpoint, originalslopeend,
						originalinterceptend);
				Indexedlength paramnextframeend;
				int labelindex = Integer.MIN_VALUE;

				if (labelend.size() > 0)

					labelindex = labelend.get(0);
				if (labelindex != Integer.MIN_VALUE) {
					paramnextframeend = Getfinaltrackparam(PrevFrameparamend.get(index), labelend.get(0), psf,
							framenumber, StartorEnd.End);

					double[] currentposini = paramnextframeend.currentpos;
					double[] previousposini = PrevFrameparamend.get(index).currentpos;

					double distmin = Distance(currentposini, previousposini);

					if (labelend.size() > 1) {
						for (int j = 0; j < labelend.size(); ++j) {
							System.out.println("Fitting multiple Labels");

							Indexedlength test = Getfinaltrackparam(PrevFrameparamend.get(index), labelend.get(j), psf,
									framenumber, StartorEnd.End);
							double[] currentpos = test.currentpos;

							double dist = Distance(currentpos, previousposini);
							if (dist < distmin && dist != 0) {
								distmin = dist;
								labelindex = j;
								paramnextframeend = test;
							}

							if (distmin == 0) {
								labelindex = labelend.get(j);
								paramnextframeend = test;

							}
						}
					}
				}

				else
					paramnextframeend = PrevFrameparamend.get(index);
				if (paramnextframeend == null)
					paramnextframeend = PrevFrameparamend.get(index);
				final double[] oldendpoint = PrevFrameparamend.get(index).currentpos;

				double[] newendpoint = paramnextframeend.currentpos;
				double newendslope = paramnextframeend.slope;
				double newendintercept = paramnextframeend.intercept;
				if (framenumber > startframe + 1 || Math.abs(newendslope) != Double.NaN) {

					// TCASM
					double oldslope = (PrevFrameparamend.get(index).currentpos[1]
							- PrevFrameparamend.get(index).fixedpos[1])
							/ (PrevFrameparamend.get(index).currentpos[0] - PrevFrameparamend.get(index).fixedpos[0]);

					double oldintercept = PrevFrameparamend.get(index).currentpos[1]
							- oldslope * PrevFrameparamend.get(index).currentpos[0];
					double newslope = (paramnextframeend.currentpos[1] - paramnextframeend.fixedpos[1])
							/ (paramnextframeend.currentpos[0] - paramnextframeend.fixedpos[0]);

					double dist = Math.toDegrees(Math.atan(((newslope - oldslope) / (1 + newslope * oldslope))));

					// (paramnextframeend.currentpos[1] - oldslope *
					// paramnextframeend.currentpos[0]
					// -oldintercept)/Math.sqrt(1 + oldslope *oldslope);

					// Math.toDegrees(Math.atan(((newslope - oldslope)/(1 +
					// newslope * oldslope))));

					// (paramnextframeend.currentpos[1] - oldslope *
					// paramnextframeend.currentpos[0]
					// -oldintercept)/Math.sqrt(1 + oldslope *oldslope);
					// Math.toDegrees(Math.atan(((newslope - oldslope)/(1 +
					// newslope * oldslope))));

					if (dist != Double.NaN) {
						System.out.println(dist);

						// TCASM
						if (Math.abs(dist) > maxdist) {
							IJ.log("Miss Assingment detected, activating TCASM layer at " + " " + oldendpoint[0] + " "
									+ oldendpoint[1]);
							paramnextframeend = PrevFrameparamend.get(index);
							newendpoint = oldendpoint;
							newendslope = PrevFrameparamend.get(index).slope;
							newendintercept = PrevFrameparamend.get(index).intercept;

						}
					}

				}

				final_paramlistend.add(paramnextframeend);

				final Trackproperties endedge = new Trackproperties(framenumber, labelindex, oldendpoint, newendpoint,
						newendslope, newendintercept, originalslopeend, originalinterceptend,
						PrevFrameparamend.get(index).seedLabel, PrevFrameparamend.get(index).fixedpos,
						PrevFrameparamend.get(index).originalds);

				endinframe.add(endedge);

			}
		}

		return false;
	}

	@Override
	public Pair<ArrayList<Indexedlength>, ArrayList<Indexedlength>> getResult() {

		Pair<ArrayList<Indexedlength>, ArrayList<Indexedlength>> listpair = new ValuePair<ArrayList<Indexedlength>, ArrayList<Indexedlength>>(
				final_paramliststart, final_paramlistend);

		return listpair;
	}

	public ArrayList<Trackproperties> getstartStateVectors() {
		return startinframe;
	}

	public ArrayList<Trackproperties> getendStateVectors() {
		return endinframe;
	}

	public enum StartorEnd {

		Start, End, Both

	}

	public Indexedlength Getfinaltrackparam(final Indexedlength iniparam, final int label, final double[] psf,
			final int rate, final StartorEnd startorend) {
		FitterUtils.SetProgressBarTime(jpb, percent, framenumber, thirdDimsize);
		final double[] LMparam = FitterUtils.MakerepeatedLineguess(imgs, iniparam, model, Intensityratio, Inispacing,
				label, ndims, startframe, framenumber);
		if (LMparam == null)
			return iniparam;

		else {

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

				if (startorend == StartorEnd.Both) {
					double ds = LMparam[2 * ndims];
					double Intensity = LMparam[2 * ndims + 1];
					final double background = LMparam[2 * ndims + 2];
					final double newslope = (startpos[1] - endpos[1]) / (startpos[0] - endpos[0]);
					final double newintercept = startpos[1] - newslope * startpos[0];
					double dx = ds / Math.sqrt(1 + (newslope) * (newslope));
					double dy = (newslope) * dx;
					double[] dxvector = { dx, dy };
					double sigmas = 0;

					for (int d = 0; d < ndims; ++d) {

						sigmas += psf[d] * psf[d];
					}

					double[] startfit = startpos;
					double[] endfit = endpos;

					if (DoMask) {

						try {
							startfit = GaussianMaskFitMSER.sumofgaussianMaskFit(currentimg, startpos.clone(), psf,
									numgaussians, iterations, dxvector, newslope, newintercept, Intensity, halfgaussian,
									EndfitMSER.StartfitMSER, label, background, Intensityratio);
							endfit = GaussianMaskFitMSER.sumofgaussianMaskFit(currentimg, endpos.clone(), psf,
									numgaussians, iterations, dxvector, newslope, newintercept, Intensity, halfgaussian,
									EndfitMSER.EndfitMSER, label, background, Intensityratio);

						} catch (Exception e) {
							e.printStackTrace();
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

					}

					final double[] bothpos = new double[] { startfit[0], startfit[1], endfit[0], endfit[1] };

					Indexedlength PointofInterest = new Indexedlength(label, seedLabel, framenumber, LMparam[2 * ndims],
							LMparam[2 * ndims + 1], LMparam[2 * ndims + 2], bothpos, iniparam.fixedpos, newslope,
							newintercept, iniparam.originalslope, iniparam.originalintercept, iniparam.originalds);
					if (Maskfail == true)
						System.out.println("New XLM: " + startfit[0] + " New YLM: " + startfit[1]);
					else
						System.out.println("New XMask: " + startfit[0] + " New YMask: " + startfit[1]);

					System.out.println("Number of Gaussians used: " + numgaussians + " ds: " + ds);

					return PointofInterest;
				}

				if (startorend == StartorEnd.Start) {
					double ds = LMparam[2 * ndims];
					double Intensity = LMparam[2 * ndims + 1];
					final double background = LMparam[2 * ndims + 2];
					final double newslope = (startpos[1] - endpos[1]) / (startpos[0] - endpos[0]);
					final double newintercept = startpos[1] - newslope * startpos[0];
					double dx = ds / Math.sqrt(1 + (newslope) * (newslope));
					double dy = (newslope) * dx;
					double[] dxvector = { dx, dy };
					double sigmas = 0;

					for (int d = 0; d < ndims; ++d) {

						sigmas += psf[d] * psf[d];
					}

					double[] startfit = startpos;

					if (DoMask) {

						try {
							startfit = GaussianMaskFitMSER.sumofgaussianMaskFit(currentimg, startpos.clone(), psf,
									numgaussians, iterations, dxvector, newslope, newintercept, Intensity, halfgaussian,
									EndfitMSER.StartfitMSER, label, background, Intensityratio);

						} catch (Exception e) {
							e.printStackTrace();
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

					Indexedlength PointofInterest = new Indexedlength(label, seedLabel, framenumber, LMparam[2 * ndims],
							LMparam[2 * ndims + 1], LMparam[2 * ndims + 2], startfit, iniparam.fixedpos, newslope,
							newintercept, iniparam.originalslope, iniparam.originalintercept, iniparam.originalds);
					if (Maskfail == true)
						System.out.println("New XLM: " + startfit[0] + " New YLM: " + startfit[1]);
					else
						System.out.println("New XMask: " + startfit[0] + " New YMask: " + startfit[1]);

					System.out.println("Number of Gaussians used: " + numgaussians + " ds: " + ds);

					return PointofInterest;
				} else {
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

					double[] endfit = endpos;

					if (DoMask) {

						try {
							endfit = GaussianMaskFitMSER.sumofgaussianMaskFit(currentimg, endpos.clone(), psf,
									numgaussians, iterations, dxvector, newslope, newintercept, Intensity, halfgaussian,
									EndfitMSER.EndfitMSER, label, background, Intensityratio);

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

					}

					Indexedlength PointofInterest = new Indexedlength(label, seedLabel, framenumber, LMparam[2 * ndims],
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
			}

			else if (model == UserChoiceModel.Splineordersec) {
				if (startorend == StartorEnd.Both) {
					final double Curvature = LMparam[2 * ndims + 1];

					final double currentintercept = iniparam.originalintercept;

					final double ds = (LMparam[2 * ndims]);
					final double lineIntensity = LMparam[2 * ndims + 2];
					final double background = LMparam[2 * ndims + 3];

					double[] startfit = startpos;
					double[] endfit = endpos;

					final double newslope = (startpos[1] - endpos[1]) / (startpos[0] - endpos[0])
							- Curvature * (startpos[0] + endpos[0]);

					double dxstart = ds / Math.sqrt(
							1 + (newslope + 2 * Curvature * startpos[0]) * (newslope + 2 * Curvature * startpos[0]));
					double dystart = (newslope + 2 * Curvature * startpos[0]) * dxstart;
					double[] dxvectorstart = { dxstart, dystart };

					double dxend = ds / Math
							.sqrt(1 + (newslope + 2 * Curvature * endpos[0]) * (newslope + 2 * Curvature * endpos[0]));
					double dyend = (newslope + 2 * Curvature * endpos[0]) * dxend;
					double[] dxvectorend = { dxend, dyend };
					double sigmas = 0;

					for (int d = 0; d < ndims; ++d) {

						sigmas += psf[d] * psf[d];
					}

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

					}
					final double[] bothpos = new double[] { startfit[0], startfit[1], endfit[0], endfit[1] };
					System.out.println("Curvature: " + Curvature);

					Indexedlength PointofInterest = new Indexedlength(label, seedLabel, framenumber, ds, lineIntensity,
							background, bothpos, iniparam.fixedpos, newslope, currentintercept, iniparam.originalslope,
							iniparam.originalintercept, Curvature, 0, iniparam.originalds);
					if (Maskfail == true)
						System.out.println("New XLM: " + startfit[0] + " New YLM: " + startfit[1]);
					else
						System.out.println("New XMask: " + startfit[0] + " New YMask: " + startfit[1]);
					System.out.println("Number of Gaussians used: " + (numgaussians) + " " + ds);

					FitterUtils.SetProgressBarTime(jpb, percent, framenumber, thirdDimsize);

					return PointofInterest;
				}

				if (startorend == StartorEnd.Start) {
					final double Curvature = LMparam[2 * ndims + 1];

					final double currentintercept = iniparam.originalintercept;

					final double ds = (LMparam[2 * ndims]);
					final double lineIntensity = LMparam[2 * ndims + 2];
					final double background = LMparam[2 * ndims + 3];

					double[] startfit = startpos;

					final double newslope = (startpos[1] - endpos[1]) / (startpos[0] - endpos[0])
							- Curvature * (startpos[0] + endpos[0]);

					double dxstart = ds / Math.sqrt(
							1 + (newslope + 2 * Curvature * startpos[0]) * (newslope + 2 * Curvature * startpos[0]));
					double dystart = (newslope + 2 * Curvature * startpos[0]) * dxstart;
					double[] dxvectorstart = { dxstart, dystart };

					double dxend = ds / Math
							.sqrt(1 + (newslope + 2 * Curvature * endpos[0]) * (newslope + 2 * Curvature * endpos[0]));
					double dyend = (newslope + 2 * Curvature * endpos[0]) * dxend;
					double[] dxvectorend = { dxend, dyend };
					double sigmas = 0;

					for (int d = 0; d < ndims; ++d) {

						sigmas += psf[d] * psf[d];
					}

					if (DoMask) {

						try {
							startfit = GaussianMaskFitMSER.sumofgaussianMaskFit(currentimg, startpos.clone(), psf,
									numgaussians, iterations, dxvectorstart, newslope, currentintercept, lineIntensity,
									halfgaussian, EndfitMSER.StartfitMSER, label, background, Intensityratio);

						} catch (Exception e) {
							e.printStackTrace();
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

					System.out.println("Curvature: " + Curvature);

					Indexedlength PointofInterest = new Indexedlength(label, seedLabel, framenumber, ds, lineIntensity,
							background, startfit, iniparam.fixedpos, newslope, currentintercept, iniparam.originalslope,
							iniparam.originalintercept, Curvature, 0, iniparam.originalds);
					if (Maskfail == true)
						System.out.println("New XLM: " + startfit[0] + " New YLM: " + startfit[1]);
					else
						System.out.println("New XMask: " + startfit[0] + " New YMask: " + startfit[1]);
					System.out.println("Number of Gaussians used: " + (numgaussians) + " " + ds);

					FitterUtils.SetProgressBarTime(jpb, percent, framenumber, thirdDimsize);

					return PointofInterest;
				} else {

					final double Curvature = LMparam[2 * ndims + 1];

					final double currentintercept = iniparam.originalintercept;
					final double ds = (LMparam[2 * ndims]);
					final double lineIntensity = LMparam[2 * ndims + 2];
					final double background = LMparam[2 * ndims + 3];
					System.out.println("Curvature: " + Curvature);
					final double newslope = (endpos[1] - startpos[1]) / (endpos[0] - startpos[0])
							- Curvature * (endpos[0] + startpos[0]);

					double[] endfit = endpos;

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

					if (DoMask) {

						try {

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

					}

					Indexedlength PointofInterest = new Indexedlength(label, seedLabel, framenumber, ds, lineIntensity,
							background, endfit, iniparam.fixedpos, newslope, currentintercept, iniparam.originalslope,
							iniparam.originalintercept, Curvature, 0, iniparam.originalds);
					if (Maskfail == true)
						System.out.println("New XLM: " + endfit[0] + " New YLM: " + endfit[1]);
					else
						System.out.println("New XMask: " + endfit[0] + " New YMask: " + endfit[1]);
					System.out.println("Number of Gaussians used: " + (numgaussians) + " " + ds);

					FitterUtils.SetProgressBarTime(jpb, percent, framenumber, thirdDimsize);

					return PointofInterest;

				}
			} else if (model == UserChoiceModel.Splineorderthird) {

				if (startorend == StartorEnd.Both) {
					final double Curvature = LMparam[2 * ndims + 1];
					final double Inflection = LMparam[2 * ndims + 2];

					final double currentintercept = iniparam.originalintercept;

					final double ds = (LMparam[2 * ndims]);
					final double lineIntensity = LMparam[2 * ndims + 3];
					final double background = LMparam[2 * ndims + 4];

					double[] startfit = startpos;
					double[] endfit = endpos;

					final double newslope = (startpos[1] - endpos[1]) / (startpos[0] - endpos[0])
							- Curvature * (startpos[0] + endpos[0]) - Inflection
									* (startpos[0] * startpos[0] + endpos[0] * endpos[0] + startpos[0] * endpos[0]);

					double dxstart = ds / Math.sqrt(1 + (newslope + 2 * Curvature * startpos[0]
							+ 3 * Inflection * startpos[0] * startpos[0])
							* (newslope + 2 * Curvature * startpos[0] + 3 * Inflection * startpos[0] * startpos[0]));
					double dystart = (newslope + 2 * Curvature * startpos[0]
							+ 3 * Inflection * startpos[0] * startpos[0]) * dxstart;
					double[] dxvectorstart = { dxstart, dystart };

					double dxend = ds / Math
							.sqrt(1 + (newslope + 2 * Curvature * endpos[0] + 3 * Inflection * endpos[0] * endpos[0])
									* (newslope + 2 * Curvature * endpos[0] + 3 * Inflection * endpos[0] * endpos[0]));
					double dyend = (newslope + 2 * Curvature * endpos[0] + 3 * Inflection * endpos[0] * endpos[0])
							* dxend;
					double[] dxvectorend = { dxend, dyend };
					double sigmas = 0;

					for (int d = 0; d < ndims; ++d) {

						sigmas += psf[d] * psf[d];
					}

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
							if (Double.isNaN(startfit[d])) {
								Maskfail = true;
								startfit[d] = startpos[d];

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

					}
					final double[] bothpos = new double[] { startfit[0], startfit[1], endfit[0], endfit[1] };

					System.out.println("Curvature: " + Curvature);
					System.out.println("Inflection: " + Inflection);
					Indexedlength PointofInterest = new Indexedlength(label, seedLabel, framenumber, ds, lineIntensity,
							background, bothpos, iniparam.fixedpos, newslope, currentintercept, iniparam.originalslope,
							iniparam.originalintercept, Curvature, Inflection, iniparam.originalds);
					if (Maskfail == true)
						System.out.println("New XLM: " + startfit[0] + " New YLM: " + startfit[1]);
					else
						System.out.println("New XMask: " + startfit[0] + " New YMask: " + startfit[1]);
					System.out.println("Number of Gaussians used: " + (numgaussians) + " " + ds);

					FitterUtils.SetProgressBarTime(jpb, percent, framenumber, thirdDimsize);

					return PointofInterest;
				}

				if (startorend == StartorEnd.Start) {
					final double Curvature = LMparam[2 * ndims + 1];
					final double Inflection = LMparam[2 * ndims + 2];

					final double currentintercept = iniparam.originalintercept;

					final double ds = (LMparam[2 * ndims]);
					final double lineIntensity = LMparam[2 * ndims + 3];
					final double background = LMparam[2 * ndims + 4];

					double[] startfit = startpos;
					final double newslope = (startpos[1] - endpos[1]) / (startpos[0] - endpos[0])
							- Curvature * (startpos[0] + endpos[0]) - Inflection
									* (startpos[0] * startpos[0] + endpos[0] * endpos[0] + startpos[0] * endpos[0]);

					double dxstart = ds / Math.sqrt(1 + (newslope + 2 * Curvature * startpos[0]
							+ 3 * Inflection * startpos[0] * startpos[0])
							* (newslope + 2 * Curvature * startpos[0] + 3 * Inflection * startpos[0] * startpos[0]));
					double dystart = (newslope + 2 * Curvature * startpos[0]
							+ 3 * Inflection * startpos[0] * startpos[0]) * dxstart;
					double[] dxvectorstart = { dxstart, dystart };

					double dxend = ds / Math
							.sqrt(1 + (newslope + 2 * Curvature * endpos[0] + 3 * Inflection * endpos[0] * endpos[0])
									* (newslope + 2 * Curvature * endpos[0] + 3 * Inflection * endpos[0] * endpos[0]));
					double dyend = (newslope + 2 * Curvature * endpos[0] + 3 * Inflection * endpos[0] * endpos[0])
							* dxend;
					double[] dxvectorend = { dxend, dyend };
					double sigmas = 0;

					for (int d = 0; d < ndims; ++d) {

						sigmas += psf[d] * psf[d];
					}

					if (DoMask) {

						try {
							startfit = GaussianMaskFitMSER.sumofgaussianMaskFit(currentimg, startpos.clone(), psf,
									numgaussians, iterations, dxvectorstart, newslope, currentintercept, lineIntensity,
									halfgaussian, EndfitMSER.StartfitMSER, label, background, Intensityratio);

						} catch (Exception e) {
							e.printStackTrace();
						}

						for (int d = 0; d < ndims; ++d) {
							if (Double.isNaN(startfit[d])) {
								Maskfail = true;
								startfit[d] = startpos[d];

							}
						}

						if (Math.abs(startpos[0] - startfit[0]) >= cutoffdistance
								|| Math.abs(startpos[1] - startfit[1]) >= cutoffdistance) {
							Maskfail = true;
							for (int d = 0; d < ndims; ++d) {
								startfit[d] = startpos[d];
							}
						}

					}

					System.out.println("Curvature: " + Curvature);
					System.out.println("Inflection: " + Inflection);
					Indexedlength PointofInterest = new Indexedlength(label, seedLabel, framenumber, ds, lineIntensity,
							background, startfit, iniparam.fixedpos, newslope, currentintercept, iniparam.originalslope,
							iniparam.originalintercept, Curvature, Inflection, iniparam.originalds);
					if (Maskfail == true)
						System.out.println("New XLM: " + startfit[0] + " New YLM: " + startfit[1]);
					else
						System.out.println("New XMask: " + startfit[0] + " New YMask: " + startfit[1]);
					System.out.println("Number of Gaussians used: " + (numgaussians) + " " + ds);

					FitterUtils.SetProgressBarTime(jpb, percent, framenumber, thirdDimsize);

					return PointofInterest;
				} else {

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

					if (DoMask) {

						try {

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

					}

					Indexedlength PointofInterest = new Indexedlength(label, seedLabel, framenumber, ds, lineIntensity,
							background, endfit, iniparam.fixedpos, newslope, currentintercept, iniparam.originalslope,
							iniparam.originalintercept, Curvature, Inflection, iniparam.originalds);
					if (Maskfail == true)
						System.out.println("New XLM: " + endfit[0] + " New YLM: " + endfit[1]);
					else
						System.out.println("New XMask: " + endfit[0] + " New YMask: " + endfit[1]);
					System.out.println("Number of Gaussians used: " + (numgaussians) + " " + ds);

					FitterUtils.SetProgressBarTime(jpb, percent, framenumber, thirdDimsize);

					return PointofInterest;

				}

			}

			else
				return null;

		}

	}

	public Pair<Indexedlength, Indexedlength> Getfinaltrackparam(final Indexedlength iniparam, final int label,
			final double[] psf, final int rate) {
		FitterUtils.SetProgressBarTime(jpb, percent, framenumber, thirdDimsize);
		final double[] LMparam = FitterUtils.MakerepeatedLineguess(imgs, iniparam, model, Intensityratio, Inispacing,
				label, ndims, startframe, framenumber);
		if (LMparam == null)
			return new ValuePair<Indexedlength, Indexedlength>(iniparam, iniparam);

		else {

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
				final double newslope = (startpos[1] - endpos[1]) / (startpos[0] - endpos[0]);
				final double newintercept = startpos[1] - newslope * startpos[0];
				double dx = ds / Math.sqrt(1 + (newslope) * (newslope));
				double dy = (newslope) * dx;
				double[] dxvector = { dx, dy };
				double sigmas = 0;

				for (int d = 0; d < ndims; ++d) {

					sigmas += psf[d] * psf[d];
				}

				double[] startfit = startpos;
				double[] endfit = endpos;

				if (DoMask) {

					try {
						startfit = GaussianMaskFitMSER.sumofgaussianMaskFit(currentimg, startpos.clone(), psf,
								numgaussians, iterations, dxvector, newslope, newintercept, Intensity, halfgaussian,
								EndfitMSER.StartfitMSER, label, background, Intensityratio);
						endfit = GaussianMaskFitMSER.sumofgaussianMaskFit(currentimg, endpos.clone(), psf, numgaussians,
								iterations, dxvector, newslope, newintercept, Intensity, halfgaussian,
								EndfitMSER.EndfitMSER, label, background, Intensityratio);

					} catch (Exception e) {
						e.printStackTrace();
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

				}

				Indexedlength PointofIntereststart = new Indexedlength(label, seedLabel, framenumber,
						LMparam[2 * ndims], LMparam[2 * ndims + 1], LMparam[2 * ndims + 2], startfit, iniparam.fixedpos,
						newslope, newintercept, iniparam.originalslope, iniparam.originalintercept,
						iniparam.originalds);
				Indexedlength PointofInterestend = new Indexedlength(label, seedLabel, framenumber, LMparam[2 * ndims],
						LMparam[2 * ndims + 1], LMparam[2 * ndims + 2], endfit, iniparam.fixedpos, newslope,
						newintercept, iniparam.originalslope, iniparam.originalintercept, iniparam.originalds);
				if (Maskfail == true)
					System.out.println("New XLM: " + startfit[0] + " New YLM: " + startfit[1]);
				else
					System.out.println("New XMask: " + startfit[0] + " New YMask: " + startfit[1]);

				System.out.println("Number of Gaussians used: " + numgaussians + " ds: " + ds);

				return new ValuePair<Indexedlength, Indexedlength>(PointofIntereststart, PointofInterestend);
			}

			else if (model == UserChoiceModel.Splineordersec) {
				final double Curvature = LMparam[2 * ndims + 1];

				final double currentintercept = iniparam.originalintercept;

				final double ds = (LMparam[2 * ndims]);
				final double lineIntensity = LMparam[2 * ndims + 2];
				final double background = LMparam[2 * ndims + 3];

				double[] startfit = startpos;
				double[] endfit = endpos;

				final double newslope = (startpos[1] - endpos[1]) / (startpos[0] - endpos[0])
						- Curvature * (startpos[0] + endpos[0]);

				double dxstart = ds / Math
						.sqrt(1 + (newslope + 2 * Curvature * startpos[0]) * (newslope + 2 * Curvature * startpos[0]));
				double dystart = (newslope + 2 * Curvature * startpos[0]) * dxstart;
				double[] dxvectorstart = { dxstart, dystart };

				double dxend = ds / Math
						.sqrt(1 + (newslope + 2 * Curvature * endpos[0]) * (newslope + 2 * Curvature * endpos[0]));
				double dyend = (newslope + 2 * Curvature * endpos[0]) * dxend;
				double[] dxvectorend = { dxend, dyend };
				double sigmas = 0;

				for (int d = 0; d < ndims; ++d) {

					sigmas += psf[d] * psf[d];
				}

				if (DoMask) {

					try {
						startfit = GaussianMaskFitMSER.sumofgaussianMaskFit(currentimg, startpos.clone(), psf,
								numgaussians, iterations, dxvectorstart, newslope, currentintercept, lineIntensity,
								halfgaussian, EndfitMSER.StartfitMSER, label, background, Intensityratio);
						endfit = GaussianMaskFitMSER.sumofgaussianMaskFit(currentimg, endpos.clone(), psf, numgaussians,
								iterations, dxvectorend, newslope, currentintercept, lineIntensity, halfgaussian,
								EndfitMSER.EndfitMSER, label, background, Intensityratio);

					} catch (Exception e) {
						e.printStackTrace();
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

				}
				System.out.println("Curvature: " + Curvature);
				Indexedlength PointofIntereststart = new Indexedlength(label, seedLabel, framenumber,
						LMparam[2 * ndims], LMparam[2 * ndims + 1], LMparam[2 * ndims + 2], startfit, iniparam.fixedpos,
						newslope, currentintercept, iniparam.originalslope, iniparam.originalintercept,
						iniparam.originalds);
				Indexedlength PointofInterestend = new Indexedlength(label, seedLabel, framenumber, LMparam[2 * ndims],
						LMparam[2 * ndims + 1], LMparam[2 * ndims + 2], endfit, iniparam.fixedpos, newslope,
						currentintercept, iniparam.originalslope, iniparam.originalintercept, iniparam.originalds);
				if (Maskfail == true)
					System.out.println("New XLM: " + startfit[0] + " New YLM: " + startfit[1]);
				else
					System.out.println("New XMask: " + startfit[0] + " New YMask: " + startfit[1]);

				System.out.println("Number of Gaussians used: " + numgaussians + " ds: " + ds);

				FitterUtils.SetProgressBarTime(jpb, percent, framenumber, thirdDimsize);

				return new ValuePair<Indexedlength, Indexedlength>(PointofIntereststart, PointofInterestend);

			}

			else if (model == UserChoiceModel.Splineorderthird) {

				final double Curvature = LMparam[2 * ndims + 1];
				final double Inflection = LMparam[2 * ndims + 2];

				final double currentintercept = iniparam.originalintercept;

				final double ds = (LMparam[2 * ndims]);
				final double lineIntensity = LMparam[2 * ndims + 3];
				final double background = LMparam[2 * ndims + 4];

				double[] startfit = startpos;
				double[] endfit = endpos;

				final double newslope = (startpos[1] - endpos[1]) / (startpos[0] - endpos[0])
						- Curvature * (startpos[0] + endpos[0])
						- Inflection * (startpos[0] * startpos[0] + endpos[0] * endpos[0] + startpos[0] * endpos[0]);

				double dxstart = ds / Math.sqrt(1 + (newslope + 2 * Curvature * startpos[0]
						+ 3 * Inflection * startpos[0] * startpos[0])
						* (newslope + 2 * Curvature * startpos[0] + 3 * Inflection * startpos[0] * startpos[0]));
				double dystart = (newslope + 2 * Curvature * startpos[0] + 3 * Inflection * startpos[0] * startpos[0])
						* dxstart;
				double[] dxvectorstart = { dxstart, dystart };

				double dxend = ds
						/ Math.sqrt(1 + (newslope + 2 * Curvature * endpos[0] + 3 * Inflection * endpos[0] * endpos[0])
								* (newslope + 2 * Curvature * endpos[0] + 3 * Inflection * endpos[0] * endpos[0]));
				double dyend = (newslope + 2 * Curvature * endpos[0] + 3 * Inflection * endpos[0] * endpos[0]) * dxend;
				double[] dxvectorend = { dxend, dyend };

				if (DoMask) {

					try {
						startfit = GaussianMaskFitMSER.sumofgaussianMaskFit(currentimg, startpos.clone(), psf,
								numgaussians, iterations, dxvectorstart, newslope, currentintercept, lineIntensity,
								halfgaussian, EndfitMSER.StartfitMSER, label, background, Intensityratio);

						endfit = GaussianMaskFitMSER.sumofgaussianMaskFit(currentimg, endpos.clone(), psf, numgaussians,
								iterations, dxvectorend, newslope, currentintercept, lineIntensity, halfgaussian,
								EndfitMSER.EndfitMSER, label, background, Intensityratio);
					} catch (Exception e) {
						e.printStackTrace();
					}

					for (int d = 0; d < ndims; ++d) {
						if (Double.isNaN(startfit[d])) {
							Maskfail = true;
							startfit[d] = startpos[d];

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

				}

				System.out.println("Curvature: " + Curvature);
				System.out.println("Inflection: " + Inflection);
				Indexedlength PointofIntereststart = new Indexedlength(label, seedLabel, framenumber,
						LMparam[2 * ndims], LMparam[2 * ndims + 1], LMparam[2 * ndims + 2], startfit, iniparam.fixedpos,
						newslope, currentintercept, iniparam.originalslope, iniparam.originalintercept,
						iniparam.originalds);
				Indexedlength PointofInterestend = new Indexedlength(label, seedLabel, framenumber, LMparam[2 * ndims],
						LMparam[2 * ndims + 1], LMparam[2 * ndims + 2], endfit, iniparam.fixedpos, newslope,
						currentintercept, iniparam.originalslope, iniparam.originalintercept, iniparam.originalds);
				if (Maskfail == true)
					System.out.println("New XLM: " + startfit[0] + " New YLM: " + startfit[1]);
				else
					System.out.println("New XMask: " + startfit[0] + " New YMask: " + startfit[1]);

				System.out.println("Number of Gaussians used: " + numgaussians + " ds: " + ds);

				FitterUtils.SetProgressBarTime(jpb, percent, framenumber, thirdDimsize);

				return new ValuePair<Indexedlength, Indexedlength>(PointofIntereststart, PointofInterestend);

			} else
				return null;
		}

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
