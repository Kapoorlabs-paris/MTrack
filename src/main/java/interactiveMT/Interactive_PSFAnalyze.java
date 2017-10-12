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
package interactiveMT;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Rectangle;
import java.awt.Scrollbar;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;

import LineModels.UseLineModel.UserChoiceModel;
import beadListener.ChooseDirectoryListener;
import beadListener.ComputeTreeListener;
import beadListener.DeltaListener;
import beadListener.DogListener;
import beadListener.FindBeadsListener;
import beadListener.FindPolynomialListener;
import beadListener.MaxSizeListener;
import beadListener.MinDiversityListener;
import beadListener.MinSizeListener;
import beadListener.MserListener;
import beadListener.Unstability_ScoreListener;
import fiji.tool.SliceListener;
import fiji.tool.SliceObserver;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.EllipseRoi;
import ij.gui.GenericDialog;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.gui.RoiListener;
import ij.plugin.PlugIn;
import ij.plugin.frame.RoiManager;
import ij.process.ColorProcessor;
import interactiveMT.BeadFileChooser.whichModel;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;
import interactiveMT.Interactive_MTDoubleChannel.Whichend;
import interactiveMT.Interactive_MTDoubleChannel.moveInThirdDimListener;
import mpicbg.imglib.multithreading.SimpleMultiThreading;
import mpicbg.imglib.util.Util;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Point;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.componenttree.mser.MserTree;
import net.imglib2.algorithm.dog.DogDetection;
import net.imglib2.algorithm.localextrema.RefinedPeak;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.view.Views;
import preProcessing.Kernels;
import psf_Tookit.GaussianFitParam;
import psf_Tookit.GaussianLineFitParam;


public class Interactive_PSFAnalyze implements PlugIn {

	// steps per octave
		public static int standardSensitivity = 4;
		public int sensitivity = standardSensitivity;
	public JProgressBar jpb;
	public JLabel label = new JLabel("Progress..");
	
	public String usefolder = IJ.getDirectory("imagej");
	public ColorProcessor cp = null;
	public String addToName = "PSFAnalysis";
	public JFrame frame = new JFrame();
	public JPanel panel = new JPanel();
	public int thirdDimensionslider = 1;
	public int thirdDimensionsliderInit = 1;
	public int thirdDimensionSize;
	public int thirdDimensionSizeOriginal;
	public int thirdDimension;
	public final int scrollbarSize = 1000;
	public final int scrollbarSizebig = 1000;
	
	public float deltaMax = 255f;
	public float Unstability_ScoreMin = 0;
	public float Unstability_ScoreMax = 1;
	
	public ArrayList<GaussianFitParam> FittedBeads;
	public ArrayList<GaussianLineFitParam> FittedLineBeads;
	
	public ArrayList<GaussianFitParam> AllFittedBeads;
	public ArrayList<GaussianLineFitParam> AllFittedLineBeads;
	
	
	public RandomAccessibleInterval<FloatType> currentimg;
	public RandomAccessibleInterval<FloatType> currentPreprocessedimg;
	public RandomAccessibleInterval<FloatType> originalimg;
	public RandomAccessibleInterval<FloatType> originalPreprocessedimg;
	public RandomAccessibleInterval<FloatType> CurrentView;
	public RandomAccessibleInterval<FloatType> CurrentPreprocessedView;
	public RandomAccessibleInterval<UnsignedByteType> newimg;
	public RandomAccessibleInterval<IntType> intimg;
	public Color originalColor = new Color(0.8f, 0.8f, 0.8f);
	public Color inactiveColor = new Color(0.95f, 0.95f, 0.95f);
	public float sigma = 0.5f;
	public float sigma2 = 0.5f;
	public float threshold = 1f;
	public int sigmaInit = 30;
	
	public float thresholdMin = 0f;
	public float thresholdMax = 1f;
	public int thresholdInit = 1;
	public float sigmaMin = 0.5f;
	public float sigmaMax = 100f;
	public double Intensityratio = 0.5;
	public double Inispacing = 1;
	public ImagePlus imp;
	public ImagePlus impcopy;
	public ImagePlus preprocessedimp;
	public NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
	public Rectangle standardRectangle;
	public MserTree<UnsignedByteType> newtree;
	public int Progressmin = 0;
	public int Progressmax = 100;
	public int max = Progressmax;
	public int deltaInit = 20;
	public float deltaMin = 0;
	public boolean FindBeadsViaMSER = false;
	public boolean FindBeadsViaDOG = false;
	public boolean SaveTxt = false;
	public boolean RoisViaMSER = false;
	public boolean RoisViaWatershed = false;
	public boolean isComputing = false;
	
	public float minDiversityMin = 0;
	public float minDiversityMax = 1;
	public int minSizeInit = 10;
	public int maxSizeInit = 5000;
	public boolean isStarted = false;
	public int minDiversityInit = 1;
	public float Unstability_Score = 1;
	public float minDiversity = 1;
	public Overlay overlay;
	public SliceObserver sliceObserver;
	public RoiListener roiListener;
	public boolean isFinished = false;
	public boolean wasCanceled = false;
	public boolean darktobright = false;
	FinalInterval interval;
	int inix = 15;
	int iniy = 15;
	public Color colorDraw = Color.red;
	public Color colorCurrent = Color.yellow;
	public Color colorTrack = Color.yellow;
	public Color colorLineTrack = Color.GRAY;
	public Color colorUnselect = Color.MAGENTA;
	public Color colorConfirm = Color.GREEN;
	public Color colorUser = Color.ORANGE;
	public UserChoiceModel userChoiceModel;
	public float delta = 1f;
	public long minSize = 1;
	public long maxSize = 1000;
	public File dir;
	public File name;
	public long minSizemin = 0;
	public long minSizemax = 1000;
	public long maxSizemin = 100;
	public long maxSizemax = 10000;
	public int numchannels;
	public final boolean batchprocess;
	public double[] initialpsf;
	public  whichModel Usermodel;
	public ArrayList<double[]> AllmeanCovar = new ArrayList<double[]>();
	public ArrayList<Pair<double[], OvalRoi>> ClickedPoints = new ArrayList<Pair<double[], OvalRoi>>();
	public HashMap<Integer, ArrayList<EllipseRoi>> AllMSERrois = new HashMap<Integer, ArrayList<EllipseRoi>>();
	public ArrayList<RefinedPeak<Point>> peaks;
	public int Unstability_ScoreInit = 1;
	
	
	public JLabel inputLabelX, inputLabelY, inputLabelT;
	public TextField inputFieldX, inputFieldY, inputFieldT;
	
	public boolean isFinished() {
		return isFinished;
	}

	public boolean wasCanceled() {
		return wasCanceled;
	}

	public void setTime(final int value) {
		thirdDimensionslider = value;
		thirdDimensionsliderInit = 1;
	}

	public boolean getFindBeadsViaMSER() {
		return FindBeadsViaMSER;
	}

	public boolean getRoisViaMSER() {

		return RoisViaMSER;
	}

	public int getTimeMax() {

		return thirdDimensionSize;
	}

	public boolean getRoisViaWatershed() {

		return RoisViaWatershed;
	}

	public void setRoisViaMSER(final boolean RoisViaMSER) {

		this.RoisViaMSER = RoisViaMSER;
	}

	public void setRoisViaWatershed(final boolean RoisViaWatershed) {

		this.RoisViaWatershed = RoisViaWatershed;
	}

	

	public void setInitialDelta(final float value) {
		delta = value;
		deltaInit = computeScrollbarPositionFromValue(delta, deltaMin, deltaMax, scrollbarSize);
	}

	public double getInitialDelta(final float value) {

		return delta;

	}

	

	public void setInitialUnstability_Score(final float value) {
		Unstability_Score = value;
		Unstability_ScoreInit = computeScrollbarPositionFromValue(Unstability_Score, Unstability_ScoreMin, Unstability_ScoreMax, scrollbarSize);
	}

	public double getInitialUnstability_Score(final float value) {

		return Unstability_Score;

	}
	
	public void setInitialminDiversity(final float value) {
		minDiversity = value;
		minDiversityInit = computeScrollbarPositionFromValue(minDiversity, minDiversityMin, minDiversityMax,
				scrollbarSize);
	}

	public double getInitialminDiversity(final float value) {

		return minDiversity;

	}

	public void setInitialminSize(final int value) {
		minSize = value;
		minSizeInit = computeScrollbarPositionFromValue(minSize, minSizemin, minSizemax, scrollbarSize);
	}

	public double getInitialminSize(final int value) {

		return minSize;

	}

	public void setInitialmaxSize(final int value) {
		maxSize = value;
		maxSizeInit = computeScrollbarPositionFromValue(maxSize, maxSizemin, maxSizemax, scrollbarSize);
	}

	public double getInitialmaxSize(final int value) {

		return maxSize;

	}
	
	
	public String getFolder() {

		return usefolder;
	}

	public String getFile() {

		return addToName;
	}
	
	public static enum ValueChange {
		SHOWMSER, SHOWDOG, ALL, ROI, FRAME, THIRDDIM, THIRDDIMTrack, DELTA, Unstability_Score, MINDIVERSITY,MINSIZE ,
		MAXSIZE,FindBeadsVia, SIGMA, THRESHOLD;
	}
	
	
	
	public Interactive_PSFAnalyze() {
		this.batchprocess = false;
	};
	
	
	public Interactive_PSFAnalyze(final RandomAccessibleInterval<FloatType> originalimg,
			final RandomAccessibleInterval<FloatType> originalPreprocessedimg, final whichModel model, final boolean batchprocess, File dir, File name){
		
		this.originalimg = originalimg;
		this.originalPreprocessedimg = originalPreprocessedimg;
		this.dir = dir;
		this.name = name;
		
		standardRectangle = new Rectangle(inix, iniy, (int) originalimg.dimension(0) - 2 * inix,
				(int) originalimg.dimension(1) - 2 * iniy);
		imp = ImageJFunctions.show(originalimg);
		this.Usermodel = model;
		this.batchprocess = batchprocess;
		impcopy = imp.duplicate();
		numchannels = imp.getNChannels();
	};
	
	
	
	@Override
	public void run(String arg) {
		UIManager.put("ProgressBar.font", Font.BOLD);
		jpb = new JProgressBar();
	
		peaks = new ArrayList<RefinedPeak<Point>>();
		AllFittedLineBeads = new ArrayList<GaussianLineFitParam>();
		AllFittedBeads = new ArrayList<GaussianFitParam>();
		nf.setMaximumFractionDigits(3);
		setInitialUnstability_Score(Unstability_ScoreInit);
		setInitialDelta(deltaInit);
		setInitialminDiversity(minDiversityInit);
		setInitialmaxSize(maxSizeInit);
		setInitialminSize(minSizeInit);
		
		
		if (originalimg.numDimensions() < 3) {

			thirdDimensionSize = 0;
		}

		if (originalimg.numDimensions() == 3 && Usermodel == whichModel.Bead) {

			thirdDimension = 1;
			thirdDimensionSize = (int) originalimg.dimension(2);

		}
		
		if (originalimg.numDimensions() == 3 && Usermodel == whichModel.Filament) {
			thirdDimension = 1;
			thirdDimensionSize = (int) originalimg.dimension(2);
			initialpsf = new double[2];
		}
		

		if (originalimg.numDimensions() > 3) {

			System.out.println("Image has wrong dimensionality, upload an XYZ image");
			return;
		}
		
		CurrentView = util.CopyUtils.getCurrentView(originalimg, thirdDimension, thirdDimensionSize);
		CurrentPreprocessedView = util.CopyUtils.getCurrentPreView(originalPreprocessedimg, thirdDimension,
				thirdDimensionSize);
		
		thirdDimensionSizeOriginal = thirdDimensionSize;
		preprocessedimp = ImageJFunctions.show(CurrentView);

		Roi roi = preprocessedimp.getRoi();

		if (roi == null) {
			// IJ.log( "A rectangular ROI is required to define the area..." );
			preprocessedimp.setRoi(standardRectangle);
			roi = preprocessedimp.getRoi();
		}

		if (roi.getType() != Roi.RECTANGLE) {
			IJ.log("Only rectangular rois are supported...");
			return;
		}

		
		
		Card();
		
		
		// add listener to the imageplus slice slider
		sliceObserver = new SliceObserver(preprocessedimp, new ImagePlusListener());
		// compute first version#
		updatePreview(ValueChange.ALL);
		isStarted = true;

		// check whenever roi is modified to update accordingly
		roiListener = new RoiListener();
		preprocessedimp.getCanvas().addMouseListener(roiListener);

		IJ.log(" Zplane " + thirdDimensionSize);
		
	}

	
	
	
	
	/**
	 * Updates the Preview with the current parameters (sigma, threshold, roi,
	 * slicenumber)
	 * 
	 * @param change
	 *            - what did change
	 */

	public void updatePreview(final ValueChange change) {
		
		boolean roiChanged = false;
		
		overlay = preprocessedimp.getOverlay();

		if (overlay == null) {

			overlay = new Overlay();
			preprocessedimp.setOverlay(overlay);
		}
		RoiManager roimanager = RoiManager.getInstance();

		if (roimanager == null) {
			roimanager = new RoiManager();
		}

		// Re-compute MSER ellipses if neccesary
		ArrayList<EllipseRoi> MSERRois = new ArrayList<EllipseRoi>();
		ArrayList<Roi> DOGRois = new ArrayList<Roi>();

		if (change == ValueChange.THIRDDIM ) {
			System.out.println("Current Z plane: " + thirdDimension);


			if (preprocessedimp == null)
				preprocessedimp = ImageJFunctions.show(CurrentView);
			else {
				final float[] pixels = (float[]) preprocessedimp.getProcessor().getPixels();
				final Cursor<FloatType> c = Views.iterable(CurrentView).cursor();

				for (int i = 0; i < pixels.length; ++i)
					pixels[i] = c.next().get();

				preprocessedimp.updateAndDraw();

			}

			preprocessedimp.setTitle("Original image Current View in third dimension: " + " " + thirdDimension);

		}

		if (change != ValueChange.THIRDDIMTrack) {

			
			
			
			overlay = preprocessedimp.getOverlay();
			Roi roi = preprocessedimp.getRoi();
			if (roi == null || roi.getType() != Roi.RECTANGLE) {
				preprocessedimp.setRoi(new Rectangle(standardRectangle));
				roi = preprocessedimp.getRoi();
				roiChanged = true;
			}

			Rectangle rect = roi.getBounds();

			if (roiChanged || currentimg == null || currentPreprocessedimg == null || newimg == null
					|| change == ValueChange.FRAME || rect.getMinX() != standardRectangle.getMinX()
							|| rect.getMaxX() != standardRectangle.getMaxX() || rect.getMinY() != standardRectangle.getMinY()
							|| rect.getMaxY() != standardRectangle.getMaxY() ||   change == ValueChange.ALL) {
				standardRectangle = rect;

				long[] min = { (long) standardRectangle.getMinX(), (long) standardRectangle.getMinY() };
				long[] max = { (long) standardRectangle.getMaxX(), (long) standardRectangle.getMaxY() };
				interval = new FinalInterval(min, max);

				currentimg = util.CopyUtils.extractImage(CurrentView, interval);
				currentPreprocessedimg = util.CopyUtils.extractImage(CurrentPreprocessedView, interval);

				newimg = util.CopyUtils.copytoByteImage(currentPreprocessedimg,
						standardRectangle);

				roiChanged = true;

			}
		}
		// if we got some mouse click but the ROI did not change we can return
		if (!roiChanged && change == ValueChange.ROI) {
			isComputing = false;
			return;
		}
		
		if (change == ValueChange.THIRDDIMTrack ) {
			overlay.clear();
			if (preprocessedimp == null)
				preprocessedimp = ImageJFunctions.show(CurrentView);
			else {
				final float[] pixels = (float[]) preprocessedimp.getProcessor().getPixels();
				final Cursor<FloatType> c = Views.iterable(CurrentView).cursor();

				for (int i = 0; i < pixels.length; ++i)
					pixels[i] = c.next().get();

				preprocessedimp.updateAndDraw();

			}
			preprocessedimp.setTitle("Original image Current View in third dimension: " + " " + thirdDimension);

			
			long[] min = { (long) standardRectangle.getMinX(), (long) standardRectangle.getMinY() };
			long[] max = { (long) standardRectangle.getMaxX(), (long) standardRectangle.getMaxY() };
			interval = new FinalInterval(min, max);

			currentimg = util.CopyUtils.extractImage(CurrentView, interval);
			currentPreprocessedimg = util.CopyUtils.extractImage(CurrentPreprocessedView, interval);

			
			
			newimg = util.CopyUtils.copytoByteImage(currentPreprocessedimg, interval);
			
			if (FindBeadsViaMSER) {

				
				IJ.log(" Computing the Component tree");

				newtree = MserTree.buildMserTree(newimg, delta, minSize, maxSize, Unstability_Score, minDiversity, darktobright);
				MSERRois =  util.DrawingUtils.getcurrentRois(newtree, AllmeanCovar);
				
				AllMSERrois.put(thirdDimension, MSERRois);

				if (preprocessedimp != null) {

					for (int i = 0; i < overlay.size(); ++i) {
						if (overlay.get(i).getStrokeColor() == colorDraw || overlay.get(i).getStrokeColor() == colorCurrent
								|| overlay.get(i).getStrokeColor() == colorUnselect) {
							overlay.remove(i);
							--i;
						}

					}

					for (int index = 0; index < MSERRois.size(); ++index) {

						EllipseRoi or = MSERRois.get(index);
						or.setStrokeColor(colorDraw);

						for (int i = 0; i < ClickedPoints.size(); ++i) {

							if (or.contains((int) Math.round(ClickedPoints.get(i).getA()[0]),
									(int) Math.round(ClickedPoints.get(i).getA()[1])))

								or.setStrokeColor(colorCurrent);

						}

						overlay.add(or);

						roimanager.addRoi(or);

					}

				}

			}

			if (FindBeadsViaDOG) {

				// if we got some mouse click but the ROI did not change we
				// can return
				if (!roiChanged && change == ValueChange.ROI) {
					isComputing = false;
					return;
				}

				final DogDetection.ExtremaType type;

				
					type = DogDetection.ExtremaType.MINIMA;
				

				final DogDetection<FloatType> newdog = new DogDetection<FloatType>(Views.extendBorder(currentimg),
						interval, new double[] { 1, 1 }, sigma, sigma2, type, threshold, true);

				peaks = newdog.getSubpixelPeaks();

				DOGRois = util.DrawingUtils.getcurrentDoGRois(peaks, sigma, sigma2);
				for (int index = 0; index < peaks.size(); ++index) {

					double[] center = new double[] { peaks.get(index).getDoublePosition(0),
							peaks.get(index).getDoublePosition(1) };

					
					Roi or = DOGRois.get(index);

					or.setStrokeColor(Color.red);
					overlay.add(or);
					roimanager.addRoi(or);
				}

			}
		
		}
		
		if (change == ValueChange.SHOWMSER) {
			long[] min = { (long) standardRectangle.getMinX(), (long) standardRectangle.getMinY() };
			long[] max = { (long) standardRectangle.getMaxX(), (long) standardRectangle.getMaxY() };
			interval = new FinalInterval(min, max);
			final long Cannyradius = 1;

			currentimg = util.CopyUtils.extractImage(CurrentView, interval);
			currentPreprocessedimg = util.CopyUtils.extractImage(CurrentPreprocessedView, interval);

			newimg = util.CopyUtils.copytoByteImage(currentPreprocessedimg,
					standardRectangle);

			newtree = MserTree.buildMserTree(newimg, delta, minSize, maxSize, Unstability_Score, minDiversity, darktobright);
			MSERRois = util.DrawingUtils.getcurrentRois(newtree, AllmeanCovar);

			AllMSERrois.put(thirdDimension, MSERRois);
			
			if (preprocessedimp != null) {

				for (int i = 0; i < overlay.size(); ++i) {
					if (overlay.get(i).getStrokeColor() == colorDraw || overlay.get(i).getStrokeColor() == colorCurrent
							|| overlay.get(i).getStrokeColor() == colorUnselect) {
						overlay.remove(i);
						--i;
					}

				}

				for (int index = 0; index < MSERRois.size(); ++index) {

					EllipseRoi or = MSERRois.get(index);
					or.setStrokeColor(colorDraw);

					for (int i = 0; i < ClickedPoints.size(); ++i) {

						if (or.contains((int) Math.round(ClickedPoints.get(i).getA()[0]),
								(int) Math.round(ClickedPoints.get(i).getA()[1])))

							or.setStrokeColor(colorCurrent);

					}

					overlay.add(or);

					roimanager.addRoi(or);

				}

			}

		}
		

		if (change == ValueChange.SHOWDOG) {
			long[] min = { (long) standardRectangle.getMinX(), (long) standardRectangle.getMinY() };
			long[] max = { (long) standardRectangle.getMaxX(), (long) standardRectangle.getMaxY() };
			interval = new FinalInterval(min, max);
			final long Cannyradius = 1;

			currentimg = util.CopyUtils.extractImage(CurrentView, interval);
			currentPreprocessedimg = util.CopyUtils.extractImage(CurrentPreprocessedView, interval);

			newimg = util.CopyUtils.copytoByteImage(Kernels.CannyEdgeandMean(currentPreprocessedimg, Cannyradius),
					standardRectangle);
			final DogDetection.ExtremaType type;

			
				type = DogDetection.ExtremaType.MINIMA;
			

			final DogDetection<FloatType> newdog = new DogDetection<FloatType>(Views.extendBorder(currentimg),
					interval, new double[] { 1, 1 }, sigma, sigma2, type, threshold, true);

			peaks = newdog.getSubpixelPeaks();

			DOGRois = util.DrawingUtils.getcurrentDoGRois(peaks, sigma, sigma2);
			
			
			
			if (preprocessedimp != null) {

				for (int i = 0; i < overlay.size(); ++i) {
					if (overlay.get(i).getStrokeColor() == colorDraw || overlay.get(i).getStrokeColor() == colorCurrent
							|| overlay.get(i).getStrokeColor() == colorUnselect) {
						overlay.remove(i);
						--i;
					}

				}

				for (int index = 0; index < DOGRois.size(); ++index) {

					Roi or = DOGRois.get(index);
					or.setStrokeColor(colorDraw);

					for (int i = 0; i < ClickedPoints.size(); ++i) {

						if (or.contains((int) Math.round(ClickedPoints.get(i).getA()[0]),
								(int) Math.round(ClickedPoints.get(i).getA()[1])))

							or.setStrokeColor(colorCurrent);

					}

					overlay.add(or);

					roimanager.addRoi(or);

				}

			}
			

		}
		
		
		}
	
	
	
	// Making the card
		public JFrame Cardframe = new JFrame("PSF Analyzer");
		public JPanel panelCont = new JPanel();
		public JPanel panelFirst = new JPanel();
		public JPanel panelSecond = new JPanel();
		
	
		public void Card() {

			CardLayout cl = new CardLayout();
			Cardframe.setMinimumSize( new Dimension(400, 400));
			cl.preferredLayoutSize(Cardframe);
			panelCont.setLayout(cl);

			panelCont.add(panelFirst, "1");

			FindBeadsViaMSER = true;

			final GridBagLayout layout = new GridBagLayout();
			final GridBagConstraints c = new GridBagConstraints();
			final Label Step = new Label("Step 1", Label.CENTER);

			panelFirst.setLayout(layout);

			panelFirst.add(Step, c);
			final Scrollbar deltaS = new Scrollbar(Scrollbar.HORIZONTAL, deltaInit, 10, 0, 10 + scrollbarSize);
			final Scrollbar Unstability_ScoreS = new Scrollbar(Scrollbar.HORIZONTAL, Unstability_ScoreInit, 10, 0, 10 + scrollbarSize);
			final Scrollbar minDiversityS = new Scrollbar(Scrollbar.HORIZONTAL, minDiversityInit, 10, 0,
					10 + scrollbarSize);
			final Scrollbar minSizeS = new Scrollbar(Scrollbar.HORIZONTAL, minSizeInit, 10, 0, 10 + scrollbarSize);
			final Scrollbar maxSizeS = new Scrollbar(Scrollbar.HORIZONTAL, maxSizeInit, 10, 0, 10 + scrollbarSize);
			final Button ComputeTree = new Button("Compute Tree and display");
			final Button FindBeadsListener = new Button("Fit Gaussian bead Function");
			final Button FindPolynomialListener = new Button("Fit Gaussian Polynomial Function");
			final Button FindSimplePolynomialListener = new Button("Fit Simple Gaussian Polynomial Function");
			Unstability_Score = computeValueFromScrollbarPosition(Unstability_ScoreInit, Unstability_ScoreMin, Unstability_ScoreMax, 
					scrollbarSize);
			delta = computeValueFromScrollbarPosition(deltaInit, 
					deltaMin, deltaMax, scrollbarSize);
			minDiversity = computeValueFromScrollbarPosition(minDiversityInit, minDiversityMin, 
					minDiversityMax,
					scrollbarSize);
			minSize = (int) computeValueFromScrollbarPosition(minSizeInit, 
					minSizemin, minSizemax, scrollbarSize);
			maxSize = (int) computeValueFromScrollbarPosition(maxSizeInit, 
					maxSizemin, maxSizemax, scrollbarSize);

			final Label deltaText = new Label("Grey Level Seperation between Components = " + delta, Label.CENTER);
			final Label Unstability_ScoreText = new Label("Unstability Score = " + Unstability_Score, Label.CENTER);
			final Label minDiversityText = new Label("minDiversity = " +minDiversity, Label.CENTER);
			final Label minSizeText = new Label("Min # of pixels inside MSER Ellipses = " + minSize, Label.CENTER);
			final Label maxSizeText = new Label("Max # of pixels inside MSER Ellipses = " + maxSize, Label.CENTER);

			inputLabelX = new JLabel("Enter a guess for sigma of Microscope PSF (pixel units): ");
			inputFieldX = new TextField();
			inputFieldX.setColumns(10);

			inputLabelY = new JLabel("Enter a guess for sigmaY of Microscope PSF (pixel units): ");
			inputFieldY = new TextField();
			inputFieldY.setColumns(10);

			final Label MSparam = new Label("Determine MSER parameters");
			MSparam.setBackground(new Color(1, 0, 1));
			MSparam.setForeground(new Color(255, 255, 255));

		
			
			/* Location */
			panelFirst.setLayout(layout);

			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 4;
			c.weighty = 1.5;

			++c.gridy;

			panelFirst.add(MSparam, c);

			++c.gridy;

			panelFirst.add(deltaText, c);

			++c.gridy;
			panelFirst.add(deltaS, c);

			++c.gridy;

			panelFirst.add(Unstability_ScoreText, c);

			++c.gridy;
			panelFirst.add(Unstability_ScoreS, c);
/*
			++c.gridy;

			panelFirst.add(minDiversityText, c);

			++c.gridy;
			panelFirst.add(minDiversityS, c);
*/
			++c.gridy;

			panelFirst.add(minSizeText, c);

			++c.gridy;
			panelFirst.add(minSizeS, c);

			++c.gridy;

			panelFirst.add(maxSizeText, c);

			++c.gridy;
			panelFirst.add(maxSizeS, c);

			
		

			++c.gridy;
			c.insets = new Insets(10, 175, 0, 175);
			panelFirst.add(ComputeTree, c);

			if(Usermodel == whichModel.Bead){
				
				
				
				
			++c.gridy;
			c.insets = new Insets(10, 180, 0, 180);
			panelFirst.add(FindBeadsListener, c);
			}
			
			else{
			
				
				++c.gridy;
				c.insets = new Insets(10, 10, 10, 0);
				panelFirst.add(inputLabelX, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 180);
				panelFirst.add(inputFieldX, c);

			
				
				inputFieldY = inputFieldX;
			
			++c.gridy;
			c.insets = new Insets(10, 180, 0, 180);
			panelFirst.add(FindPolynomialListener, c);	
			
			
			
			
			}
			
			deltaS.addAdjustmentListener(new DeltaListener(this, deltaText, deltaMin, deltaMax, 
					scrollbarSize, deltaS));

			Unstability_ScoreS.addAdjustmentListener(
					new Unstability_ScoreListener(this, Unstability_ScoreText, Unstability_ScoreMin, Unstability_ScoreMax, 
							scrollbarSize, Unstability_ScoreS));

			minDiversityS.addAdjustmentListener(new MinDiversityListener(this, minDiversityText, minDiversityMin,
					minDiversityMax, scrollbarSize, minDiversityS));

			minSizeS.addAdjustmentListener(
					new MinSizeListener(this, minSizeText,minSizemin, minSizemax,
                  scrollbarSize, minSizeS));

			maxSizeS.addAdjustmentListener(
					new MaxSizeListener(this,maxSizeText, maxSizemin, maxSizemax, 
							scrollbarSize, maxSizeS));

			ComputeTree.addActionListener(new ComputeTreeListener(this));
			FindBeadsListener.addActionListener(new FindBeadsListener(this));
			FindPolynomialListener.addActionListener(new FindPolynomialListener(this));
			
			
			
			/*

			JPanel control = new JPanel();
			control.add(new JButton(new AbstractAction("\u22b2Prev") {

				@Override
				public void actionPerformed(ActionEvent e) {
					CardLayout cl = (CardLayout) panelCont.getLayout();
					cl.previous(panelCont);
				}
			}));
			control.add(new JButton(new AbstractAction("Next\u22b3") {

				@Override
				public void actionPerformed(ActionEvent e) {
					CardLayout cl = (CardLayout) panelCont.getLayout();
					cl.next(panelCont);
				}
			}));
			*/
			Cardframe.add(panelCont, BorderLayout.CENTER);
		//	Cardframe.add(control, BorderLayout.SOUTH);
			Cardframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			Cardframe.pack();
			Cardframe.setVisible(true);
			
		}
	
	
	
		protected class moveInThirdDimListener implements ActionListener {
			final float min, max;
			Label timeText;
			final Scrollbar thirdDimensionScroll;

			public moveInThirdDimListener(Scrollbar thirdDimensionScroll, Label timeText, float min, float max) {
				this.thirdDimensionScroll = thirdDimensionScroll;
				this.min = min;
				this.max = max;
				this.timeText = timeText;
			}

			@Override
			public void actionPerformed(final ActionEvent arg0) {

				boolean dialog = Dialogue();
				if (dialog) {

					thirdDimensionScroll
							.setValue(computeIntScrollbarPositionFromValue(thirdDimension, min, max, scrollbarSize));
					timeText.setText("Time index = " + thirdDimensionslider);

					if (thirdDimension > thirdDimensionSize) {
						IJ.log("Max frame number exceeded, moving to last frame instead");
						thirdDimension = thirdDimensionSize;
						CurrentView = util.CopyUtils.getCurrentView(originalimg, thirdDimension, thirdDimensionSize);
						CurrentPreprocessedView = util.CopyUtils.getCurrentPreView(originalPreprocessedimg, thirdDimension,
								thirdDimensionSize);

					} else {

						CurrentView = util.CopyUtils.getCurrentView(originalimg, thirdDimension, thirdDimensionSize);
						CurrentPreprocessedView = util.CopyUtils.getCurrentPreView(originalPreprocessedimg, thirdDimension,
								thirdDimensionSize);

					}

					// compute first version
					updatePreview(ValueChange.THIRDDIMTrack);

				}
			}
		}
		
		protected static int computeIntScrollbarPositionFromValue(final float thirdDimensionslider, final float min,
				final float max, final int scrollbarSize) {
			return Util.round(((thirdDimensionslider - min) / (max - min)) * max);
		}
		private boolean Dialogue() {
			GenericDialog gd = new GenericDialog("Move in time");

			if (thirdDimensionSize > 1) {
				gd.addNumericField("Move in time", thirdDimension, 0);

			}

			gd.showDialog();
			if (thirdDimensionSize > 1) {
				thirdDimension = (int) gd.getNextNumber();

				if (thirdDimension < thirdDimensionSize)
					thirdDimensionslider = thirdDimension;
				else
					thirdDimensionslider = thirdDimensionSize;
			}
			return !gd.wasCanceled();
		}
	
	/**
	 * Tests whether the ROI was changed and will recompute the preview
	 * 
	 * @author Stephan Preibisch
	 */
	protected class RoiListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(final MouseEvent e) {
			// here the ROI might have been modified, let's test for that
			final Roi roi = preprocessedimp.getRoi();

			if (roi == null || roi.getType() != Roi.RECTANGLE)
				return;

			while (isComputing)
				SimpleMultiThreading.threadWait(10);

			updatePreview(ValueChange.ROI);
		}

	}
	
	public float computeSigma2(final float sigma1, final int sensitivity) {
		final float k = (float) computeK(sensitivity);
		final float[] sigma = computeSigma(k, sigma1);

		return sigma[1];
	}
	public static float[] computeSigma( final float k, final float initialSigma )
	{
		final float[] sigma = new float[ 2 ];

		sigma[ 0 ] = initialSigma;
		sigma[ 1 ] = sigma[ 0 ] * k;

		return sigma;
	}
	public static double computeK( final float stepsPerOctave ) { return Math.pow( 2f, 1f / stepsPerOctave ); }
	public class ImagePlusListener implements SliceListener {
		@Override
		public void sliceChanged(ImagePlus arg0) {
			if (isStarted) {
				while (isComputing) {
					SimpleMultiThreading.threadWait(10);
				}
				updatePreview(ValueChange.FRAME);

			}
		}
	}
	public float computeValueFromScrollbarPosition(final int scrollbarPosition, final float min, final float max,
			final int scrollbarSize) {
		return min + (scrollbarPosition / (float) scrollbarSize) * (max - min);
	}

	public int computeScrollbarPositionFromValue(final float sigma, final float min, final float max,
			final int scrollbarSize) {
		return Util.round(((sigma - min) / (max - min)) * scrollbarSize);
	}
	
	
	public static void main(String[] args) {

		JFrame frame = new JFrame("");
		BeadFileChooser panel = new BeadFileChooser();

		frame.getContentPane().add(panel, "Center");
		frame.setSize(panel.getPreferredSize());
	}
	
}
