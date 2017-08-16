package interactiveMT;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Rectangle;
import java.awt.Scrollbar;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import LineModels.UseLineModel.UserChoiceModel;
import MTObjects.MTcounter;
import MTObjects.ResultsMT;
import fiji.tool.SliceListener;
import fiji.tool.SliceObserver;
import graphconstructs.Trackproperties;
import houghandWatershed.WatershedDistimg;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.Prefs;
import ij.gui.EllipseRoi;
import ij.gui.GenericDialog;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.io.Opener;
import ij.plugin.Macro_Runner;
import ij.plugin.PlugIn;
import ij.process.ColorProcessor;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;
import interactiveMT.Interactive_MTDoubleChannel.Whichend;
import labeledObjects.CommonOutputHF;
import labeledObjects.Indexedlength;
import listeners.AcceptResultsListener;
import listeners.AdvancedSeedListener;
import listeners.AdvancedTrackerListener;
import listeners.AnalyzekymoListener;
import listeners.BeginTrackListener;
import listeners.CheckResultsListener;
import listeners.ChooseDirectoryListener;
import listeners.ComputeMserinHoughListener;
import listeners.ComputeTreeListener;
import listeners.DarktobrightListener;
import listeners.DeltaListener;
import listeners.DoMserSegmentation;
import listeners.DoSegmentation;
import listeners.DowatershedListener;
import listeners.EndTrackListener;
import listeners.FindLinesListener;
import listeners.HoughListener;
import listeners.MaxSizeListener;
import listeners.Unstability_ScoreListener;
import listeners.MinDiversityListener;
import listeners.MinSizeListener;
import listeners.MserListener;
import listeners.MserwHoughListener;
import listeners.SeedDisplayListener;
import listeners.ShowBitimgListener;
import listeners.ShowwatershedimgListener;
import listeners.SkipFramesandTrackendsListener;
import listeners.ThresholdHoughListener;
import listeners.TrackendsListener;

import mpicbg.imglib.multithreading.SimpleMultiThreading;
import mpicbg.imglib.util.Util;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.componenttree.mser.MserTree;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.view.Views;
import preProcessing.GetLocalmaxmin;
import preProcessing.Kernels;
import preProcessing.MedianFilter2D;
import swingClasses.ProgressBatch;
import trackerType.MTTracker;
import updateListeners.DefaultModel;
import updateListeners.DefaultModelHF;
import updateListeners.FinalPoint;
import updateListeners.FinalizechoicesListener;
import updateListeners.MoveNextListener;
import updateListeners.MoveToFrameListener;
import util.Boundingboxes;

/**
 * An interactive tool for MT tracking using MSER and Hough Transform
 * 
 * @author Varun Kapoor
 */

public class BatchMode implements PlugIn, Runnable {

	public final Interactive_MTDoubleChannel parent;
	public ColorProcessor cp = null;
	public ArrayList<float[]> deltadstart = new ArrayList<>();
	public ArrayList<float[]> deltadend = new ArrayList<>();
	public ArrayList<float[]> deltad = new ArrayList<>();
	public ArrayList<float[]> lengthKymo;
	public final int scrollbarSize = 1000;
	public final int scrollbarSizebig = 1000;
	public boolean AdvancedChoice = false;
	public boolean AdvancedChoiceSeeds = false;

	boolean Simplemode = false;
	boolean Advancedmode = false;
	boolean Kymomode = false;

	// steps per octave
	public static int standardSensitivity = 4;
	public int sensitivity = standardSensitivity;

	public MouseListener ml;
	public MouseListener removeml;
	public OvalRoi Seedroi;
	public ArrayList<OvalRoi> AllSeedrois;

	public int radiusseed = 5;
	public JLabel inputLabelX;
	public JLabel inputLabelY;
	public JLabel inputLabelT;
	public TextField inputFieldX;
	public TextField inputFieldY;
	public TextField inputFieldT;

	public JProgressBar jpb;
	public int numgaussians = Prefs.getInt(".Numg.int", 2);
    public double maxdist = Prefs.getDouble(".Maxdist.double", 20);
	public JLabel label = new JLabel("Progress..");
	public JFrame frame = new JFrame();
	public JPanel panel = new JPanel();
	public int Progressmin = 0;
	public int Progressmax = 100;
	public int max = Progressmax;
	public float deltadcutoff = 5;

	public boolean analyzekymo = false;
	public boolean darktobright = false;
	public boolean displayBitimg = false;
	public boolean displayWatershedimg = false;
	public boolean displayoverlay = true;

	public long minSize = (long) Prefs.getDouble(".minSize.double", 1);
	public long maxSize = (long) Prefs.getDouble(".maxSize.double", 10000);

	public int selectedSeed = 0;
	public int displayselectedSeed;
	public double netdeltad = 0;

	public int thirdDimensionslider = 1;
	public int thirdDimensionsliderInit = 1;
	public int timeMin = 1;

	public double modelnumber = Prefs.get(".Model.int", 3);

	public float delta = 1f;

	public JLabel inputMaxdpixel;
	public JLabel inputMaxdmicro;
	public TextField Maxdpixel;
	private TextField Maxdmicro;

	public double[] psf = new double[] { Prefs.getDouble(".PSFX.double", 1), Prefs.getDouble(".PSFY.double", 1) };
	public boolean Domask = Prefs.getBoolean(".Domask.boolean", true);
	public double Intensityratio = Prefs.getDouble(".Intensityratio.double", 0.5);
	public double Inispacing = Prefs.getDouble(".Inispacing.double", 0.5);
	public double thetaPerPixel = Prefs.getDouble(".thetaPerPixel.double", 1.0);
	public double rhoPerPixel = Prefs.getDouble(".rhoPerPixel.double", 1.0);

	public String userfile;
	public int starttimetrack;
	public int endtimetrack;

	public int radius = 1;
	public long Size = 1;

	public boolean enablerhoPerPixel = false;

	public String batchfolder;
	public float Unstability_Score = (float) Prefs.getDouble(".Unstability_Score.double", 1);
	public float minDiversity = (float) Prefs.getDouble(".minDiversity.double", 1);

	public float thresholdHough = (float) Prefs.getDouble(".thresholdHough.double", 1);

	public Color colorDraw = Color.red;
	public Color colorCurrent = Color.yellow;
	public Color colorTrack = Color.yellow;
	public Color colorLineTrack = Color.GRAY;
	public Color colorUnselect = Color.MAGENTA;
	public Color colorConfirm = Color.GREEN;
	public Color colorUser = Color.ORANGE;

	public FloatType minval = new FloatType(0);
	public FloatType maxval = new FloatType(1);
	public SliceObserver sliceObserver;
	public RoiListener roiListener;

	public boolean numberKymo = false;
	public boolean numberTracker = true;
	public boolean isComputing = false;
	public boolean isStarted = false;
	public boolean redo = false;
	public boolean redoAccept = false;

	public boolean displayTree = false;
	public boolean GaussianLines = true;
	public boolean Mediancurr = false;
	public boolean MedianAll = false;
	public boolean AutoDelta = false;
	public boolean DoRloop = false;

	public boolean finalpoint = false;
	public boolean Trackstart;
	public int nbRois;
	public Roi rorig = null;
	public ArrayList<double[]> lengthtimestart = new ArrayList<double[]>();
	public ArrayList<double[]> lengthtimeuser = new ArrayList<double[]>();
	public HashMap<Integer, ArrayList<EllipseRoi>> AllMSERrois = new HashMap<Integer, ArrayList<EllipseRoi>>();
	public HashMap<Integer, double[]> AllPoints = new HashMap<Integer, double[]>();

	public ArrayList<double[]> lengthtimeend = new ArrayList<double[]>();
	public ArrayList<double[]> lengthtime = new ArrayList<double[]>();
	public ArrayList<MTcounter> ALLcounts = new ArrayList<MTcounter>();
	public MTTracker MTtrackerstart;
	public MTTracker MTtrackerend;

	public int maxghost = 1;

	public double sumlengthpixel = 0;
	public double sumlengthmicro = 0;

	public Overlay overlay;
	public HashMap<Integer, Boolean> whichend = new HashMap<Integer, Boolean>();
	public HashMap<Integer, Double> pixellength = new HashMap<Integer, Double>();
	public HashMap<Integer, Double> microlength = new HashMap<Integer, Double>();
	public ArrayList<float[]> finalvelocity = new ArrayList<float[]>();
	public ArrayList<float[]> finalvelocityKymo = new ArrayList<float[]>();
	public ArrayList<ArrayList<Trackproperties>> Allstart = new ArrayList<ArrayList<Trackproperties>>();
	public ArrayList<ArrayList<Trackproperties>> AllUser = new ArrayList<ArrayList<Trackproperties>>();
	public ArrayList<ArrayList<Trackproperties>> Allend = new ArrayList<ArrayList<Trackproperties>>();

	public ArrayList<ResultsMT> startlengthlist = new ArrayList<ResultsMT>();
	public ArrayList<ResultsMT> userlengthlist = new ArrayList<ResultsMT>();
	public ArrayList<ResultsMT> endlengthlist = new ArrayList<ResultsMT>();

	public int channel = 0;
	public int thirdDimensionSize;
	public int thirdDimensionSizeOriginal;

	public final File[] AllImages;

	public RandomAccessibleInterval<FloatType> originalimg;
	public RandomAccessibleInterval<FloatType> originalPreprocessedimg;
	public RandomAccessibleInterval<FloatType> Kymoimg;
	public RandomAccessibleInterval<FloatType> CurrentView;
	public RandomAccessibleInterval<FloatType> CurrentPreprocessedView;
	public int inix = Prefs.getInt(".IniX.int", 1);
	public int iniy = Prefs.getInt(".IniY.int", 1);

	public double[] calibration;
	public double radiusfactor = 1;
	public MserTree<UnsignedByteType> newtree;

	public HashMap<Integer, MserTree<UnsignedByteType>> newHoughtree;

	public ArrayList<MserTree<UnsignedByteType>> Alllocaltree;
	// Image 2d at the current slice
	public RandomAccessibleInterval<FloatType> currentimg;
	public RandomAccessibleInterval<FloatType> currentPreprocessedimg;
	public RandomAccessibleInterval<IntType> intimg;
	public Color originalColor = new Color(0.8f, 0.8f, 0.8f);
	public Color inactiveColor = new Color(0.95f, 0.95f, 0.95f);
	public ImagePlus imp;
	public ImagePlus impcopy;
	public ImagePlus preprocessedimp;
	public int count, startdim;
	public int Maxlabel;
	public int ndims;
	public Overlay overlaysec;
	public ArrayList<Pair<Integer, double[]>> IDALL = new ArrayList<Pair<Integer, double[]>>();
	public ArrayList<Pair<double[], OvalRoi>> ClickedPoints = new ArrayList<Pair<double[], OvalRoi>>();
	public Pair<ArrayList<Indexedlength>, ArrayList<Indexedlength>> PrevFrameparam;
	public ArrayList<Indexedlength> Userframe;
	public Pair<ArrayList<Indexedlength>, ArrayList<Indexedlength>> NewFrameparam;
	public ArrayList<Indexedlength> UserframeNew;
	public ArrayList<Integer> Accountedframes = new ArrayList<Integer>();
	public ArrayList<Integer> Missedframes = new ArrayList<Integer>();
	public Pair<Pair<ArrayList<Trackproperties>, ArrayList<Trackproperties>>, Pair<ArrayList<Indexedlength>, ArrayList<Indexedlength>>> returnVector;
	public Pair<ArrayList<Trackproperties>, ArrayList<Indexedlength>> returnVectorUser;

	public NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
	public ArrayList<CommonOutputHF> output;
	public ImageStack prestack;
	public Rectangle standardRectangle;
	public FinalInterval interval;
	public RandomAccessibleInterval<UnsignedByteType> newimg;
	public ArrayList<double[]> AllmeanCovar = new ArrayList<double[]>();
	public long Cannyradius;
	public HashMap<Integer, ArrayList<Roi>> AllpreviousRois;
	// first and last slice to process
	public int endStack;
	public int thirdDimension;

	public boolean isFinished = false;
	public boolean wasCanceled = false;
	public int detcount = 0;
	public boolean SecondOrderSpline;
	public boolean ThirdOrderSpline;
	public HashMap<Integer, Whichend> seedmap = new HashMap<Integer, Whichend>();

	public boolean isFinished() {
		return isFinished;
	}

	public boolean wasCanceled() {
		return wasCanceled;
	}

	public BatchMode() {
		this.parent = null;
		this.AllImages = null;
	};

	public BatchMode(final File[] AllImages, final Interactive_MTDoubleChannel parent, final File batchdirectory) {

		this.AllImages = AllImages;
		this.parent = parent;
		this.batchfolder = batchdirectory.getParent();

	}
	@Override
	public void run(String arg) {
		run();
		
	}
	@Override
	public void run() {

		goTrack();

	}

	public void goTrack() {

		
		
		
		ProgressBatch startbatch = new ProgressBatch(this);
		startbatch.execute();
		

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

		if (change == ValueChange.THIRDDIM) {
			System.out.println("Current Time point: " + thirdDimension);

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

		

		if (change == ValueChange.THIRDDIMTrack) {

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

			// check if Roi changed
			System.out.println("Current Time point: " + thirdDimension);

			long[] min = { (long) standardRectangle.getMinX(), (long) standardRectangle.getMinY() };
			long[] max = { (long) standardRectangle.getMaxX(), (long) standardRectangle.getMaxY() };
			interval = new FinalInterval(min, max);

			currentimg = util.CopyUtils.extractImage(CurrentView, interval);
			currentPreprocessedimg = util.CopyUtils.extractImage(CurrentPreprocessedView, interval);

			newimg = util.CopyUtils.copytoByteImage(Kernels.CannyEdgeandMean(currentPreprocessedimg, Cannyradius),
					standardRectangle);
		}

		if (change == ValueChange.MEDIAN) {

			if (preprocessedimp != null)
				preprocessedimp.close();
			preprocessedimp = ImageJFunctions.show(CurrentPreprocessedView);

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
					|| rect.getMaxY() != standardRectangle.getMaxY() || change == ValueChange.ALL) {
				standardRectangle = rect;

				long[] min = { (long) standardRectangle.getMinX(), (long) standardRectangle.getMinY() };
				long[] max = { (long) standardRectangle.getMaxX(), (long) standardRectangle.getMaxY() };
				interval = new FinalInterval(min, max);

				currentimg = util.CopyUtils.extractImage(CurrentView, interval);
				currentPreprocessedimg = util.CopyUtils.extractImage(CurrentPreprocessedView, interval);

				newimg = util.CopyUtils.copytoByteImage(Kernels.CannyEdgeandMean(currentPreprocessedimg, Cannyradius),
						standardRectangle);

				roiChanged = true;
			}
		}

		if (change != ValueChange.THIRDDIMTrack) {

			overlay = preprocessedimp.getOverlay();
			Roi roi = preprocessedimp.getRoi();
			if (roi == null || roi.getType() != Roi.RECTANGLE) {
				preprocessedimp.setRoi(new Rectangle(standardRectangle));
				roi = preprocessedimp.getRoi();
				roiChanged = true;
			}

			if (roiChanged || currentimg == null || currentPreprocessedimg == null || newimg == null
					|| change == ValueChange.FRAME || change == ValueChange.ALL) {

				long[] min = { (long) standardRectangle.getMinX(), (long) standardRectangle.getMinY() };
				long[] max = { (long) standardRectangle.getMaxX(), (long) standardRectangle.getMaxY() };
				interval = new FinalInterval(min, max);

				currentimg = util.CopyUtils.extractImage(CurrentView, interval);
				currentPreprocessedimg = util.CopyUtils.extractImage(CurrentPreprocessedView, interval);

				newimg = util.CopyUtils.copytoByteImage(Kernels.CannyEdgeandMean(currentPreprocessedimg, Cannyradius),
						standardRectangle);

				roiChanged = true;

			}
		}
		// if we got some mouse click but the ROI did not change we can return
		if (!roiChanged && change == ValueChange.ROI) {
			isComputing = false;
			return;
		}

		// Re-compute MSER ellipses if neccesary
		ArrayList<EllipseRoi> Rois = new ArrayList<EllipseRoi>();

		if (change == ValueChange.SHOWMSERinHough) {

			long[] min = { (long) standardRectangle.getMinX(), (long) standardRectangle.getMinY() };
			long[] max = { (long) standardRectangle.getMaxX(), (long) standardRectangle.getMaxY() };
			interval = new FinalInterval(min, max);
			currentimg = util.CopyUtils.extractImage(CurrentView, interval);
			currentPreprocessedimg = util.CopyUtils.extractImage(CurrentPreprocessedView, interval);

			ArrayList<EllipseRoi> AllRois = new ArrayList<EllipseRoi>();
			ArrayList<double[]> meanCovar = new ArrayList<double[]>();
			if (count == 1)
				startdim = thirdDimension;
			for (int label = 1; label < Maxlabel - 1; label++) {

				Pair<RandomAccessibleInterval<FloatType>, FinalInterval> pair = Boundingboxes
						.CurrentLabelImagepair(intimg, currentPreprocessedimg, label);

				RandomAccessibleInterval<FloatType> roiimg = pair.getA();

				RandomAccessibleInterval<UnsignedByteType> newimg = util.CopyUtils.copytoByteImage(
						Kernels.CannyEdgeandMean(roiimg, Cannyradius), intimg, standardRectangle, label);

				MserTree<UnsignedByteType> newtree = MserTree.buildMserTree(newimg, delta, minSize, maxSize,
						Unstability_Score, minDiversity, darktobright);

				Rois = util.DrawingUtils.getcurrentRois(newtree, meanCovar);
				AllmeanCovar.addAll(meanCovar);
				AllRois.addAll(Rois);

				newHoughtree.put(label, newtree);

				if (preprocessedimp != null) {

					for (int index = 0; index < Rois.size(); ++index) {

						EllipseRoi or = Rois.get(index);
						or.setStrokeColor(colorDraw);

						for (int i = 0; i < ClickedPoints.size(); ++i) {

							if (or.contains((int) Math.round(ClickedPoints.get(i).getA()[0]),
									(int) Math.round(ClickedPoints.get(i).getA()[1])))

								or.setStrokeColor(colorCurrent);

						}

						overlay.add(or);


					}
				}

			}

			AllMSERrois.put(thirdDimension, AllRois);
			count++;

		}

		if (change == ValueChange.SHOWHOUGH) {
			long[] min = { (long) standardRectangle.getMinX(), (long) standardRectangle.getMinY() };
			long[] max = { (long) standardRectangle.getMaxX(), (long) standardRectangle.getMaxY() };
			interval = new FinalInterval(min, max);

			currentimg = util.CopyUtils.extractImage(CurrentView, interval);
			currentPreprocessedimg = util.CopyUtils.extractImage(CurrentPreprocessedView, interval);

			newimg = util.CopyUtils.copytoByteImage(currentPreprocessedimg, standardRectangle);

			RandomAccessibleInterval<BitType> bitimg = new ArrayImgFactory<BitType>().create(newimg, new BitType());
			FloatType T = new FloatType(Math.round(thresholdHough));
			GetLocalmaxmin.ThresholdingBit(currentPreprocessedimg, bitimg, T);

			if (displayBitimg)
				ImageJFunctions.show(bitimg);

			WatershedDistimg<UnsignedByteType> WaterafterDisttransform = new WatershedDistimg<UnsignedByteType>(newimg,
					bitimg);
			WaterafterDisttransform.checkInput();
			WaterafterDisttransform.process();
			intimg = WaterafterDisttransform.getResult();
			Maxlabel = WaterafterDisttransform.GetMaxlabelsseeded(intimg);

			if (displayWatershedimg)
				ImageJFunctions.show(intimg);

			for (int i = 0; i < overlay.size(); ++i) {
				if (overlay.get(i).getStrokeColor() == colorDraw || overlay.get(i).getStrokeColor() == colorCurrent
						|| overlay.get(i).getStrokeColor() == colorUnselect) {
					overlay.remove(i);
					--i;
				}

			}

		}

		if (change == ValueChange.SHOWMSER) {
			long[] min = { (long) standardRectangle.getMinX(), (long) standardRectangle.getMinY() };
			long[] max = { (long) standardRectangle.getMaxX(), (long) standardRectangle.getMaxY() };
			interval = new FinalInterval(min, max);
			final long Cannyradius = (long) (radiusfactor * Math.ceil(Math.sqrt(psf[0] * psf[0] + psf[1] * psf[1])));

			currentimg = util.CopyUtils.extractImage(CurrentView, interval);
			currentPreprocessedimg = util.CopyUtils.extractImage(CurrentPreprocessedView, interval);

			newimg = util.CopyUtils.copytoByteImage(Kernels.CannyEdgeandMean(currentPreprocessedimg, Cannyradius),
					standardRectangle);

			newtree = MserTree.buildMserTree(newimg, delta, minSize, maxSize, Unstability_Score, minDiversity,
					darktobright);
			Rois = util.DrawingUtils.getcurrentRois(newtree, AllmeanCovar);

			AllMSERrois.put(thirdDimension, Rois);
			count++;

			if (count == 1)
				startdim = thirdDimension;
			if (preprocessedimp != null) {

				for (int i = 0; i < overlay.size(); ++i) {
					if (overlay.get(i).getStrokeColor() == colorDraw || overlay.get(i).getStrokeColor() == colorCurrent
							|| overlay.get(i).getStrokeColor() == colorUnselect) {
						overlay.remove(i);
						--i;
					}

				}

				for (int index = 0; index < Rois.size(); ++index) {

					EllipseRoi or = Rois.get(index);
					or.setStrokeColor(colorDraw);

					for (int i = 0; i < ClickedPoints.size(); ++i) {

						if (or.contains((int) Math.round(ClickedPoints.get(i).getA()[0]),
								(int) Math.round(ClickedPoints.get(i).getA()[1])))

							or.setStrokeColor(colorCurrent);

					}

					overlay.add(or);


				}

			}

		}

		if (preprocessedimp != null)
			preprocessedimp.updateAndDraw();
		roiListener = new RoiListener();
		preprocessedimp.getCanvas().addMouseListener(roiListener);
		isComputing = false;

	}

	public boolean maxStack() {
		GenericDialog gd = new GenericDialog("Choose Final Frame");
		if (thirdDimensionSize > 1) {

			gd.addNumericField("Do till frame", thirdDimensionSizeOriginal, 0);

			assert (int) gd.getNextNumber() > 1;
		}

		gd.showDialog();
		if (thirdDimensionSize > 1) {
			thirdDimensionSize = (int) gd.getNextNumber();

		}
		return !gd.wasCanceled();

	}

	protected static int computeIntScrollbarPositionFromValue(final float thirdDimensionslider, final float min,
			final float max, final int scrollbarSize) {
		return Util.round(((thirdDimensionslider - min) / (max - min)) * max);
	}

	public Comparator<Indexedlength> Seedcompare = new Comparator<Indexedlength>() {

		@Override
		public int compare(final Indexedlength A, final Indexedlength B) {

			return A.seedLabel - B.seedLabel;

		}

	};

	public Comparator<Trackproperties> Seedcomparetrack = new Comparator<Trackproperties>() {

		@Override
		public int compare(final Trackproperties A, final Trackproperties B) {

			return A.seedlabel - B.seedlabel;

		}

	};

	public void displaystack() {

		ImagePlus Localimp = ImageJFunctions.show(originalimg);

		for (int i = thirdDimensionsliderInit; i < thirdDimensionSize; ++i) {

			prestack.addSlice(Localimp.getImageStack().getProcessor(i).convertToRGB());
			cp = (ColorProcessor) (prestack.getProcessor(i).duplicate());

			if (AllMSERrois.get(i) != null) {
				ArrayList<EllipseRoi> Rois = AllMSERrois.get(i);
				for (int index = 0; index < Rois.size(); ++index) {

					EllipseRoi or = Rois.get(index);

					or.setStrokeColor(Color.red);

					if (displayoverlay) {

						cp.setColor(Color.red);
						cp.setLineWidth(1);
						cp.draw(or);

					}

				}

				ArrayList<Roi> AllBigRoi = new ArrayList<Roi>();

				if (endlengthlist != null) {
					for (int secindex = 0; secindex < endlengthlist.size(); ++secindex) {

						if (endlengthlist.get(secindex).framenumber == i) {
							double[] newendpoint = new double[ndims];

							newendpoint = endlengthlist.get(secindex).currentpointpixel;

							final OvalRoi Bigroi = new OvalRoi(Util.round(newendpoint[0] - 2.5),
									Util.round(newendpoint[1] - 2.5), Util.round(5), Util.round(5));
							AllBigRoi.add(Bigroi);

						}

					}
				}

				if (startlengthlist != null) {
					for (int secindex = 0; secindex < startlengthlist.size(); ++secindex) {

						if (startlengthlist.get(secindex).framenumber == i) {
							double[] newstartpoint = new double[ndims];

							newstartpoint = startlengthlist.get(secindex).currentpointpixel;

							final OvalRoi Bigroi = new OvalRoi(Util.round(newstartpoint[0] - 2.5),
									Util.round(newstartpoint[1] - 2.5), Util.round(5), Util.round(5));

							AllBigRoi.add(Bigroi);

						}

					}
				}

				if (userlengthlist != null) {
					for (int secindex = 0; secindex < userlengthlist.size(); ++secindex) {

						if (userlengthlist.get(secindex).framenumber == i) {
							double[] newstartpoint = new double[ndims];

							newstartpoint = userlengthlist.get(secindex).currentpointpixel;

							final OvalRoi Bigroi = new OvalRoi(Util.round(newstartpoint[0] - 2.5),
									Util.round(newstartpoint[1] - 2.5), Util.round(5), Util.round(5));

							AllBigRoi.add(Bigroi);

						}

					}
				}

				for (int index = 0; index < AllBigRoi.size(); ++index) {

					cp.draw(AllBigRoi.get(index));

				}

				if (displayoverlay && prestack != null)
					prestack.setPixels(cp.getPixels(), i);
				Localimp.hide();

			}

		}

	}

	protected final void close(final Frame parent, final SliceObserver sliceObserver, final ImagePlus imp,
			RoiListener roiListener) {
		if (parent != null)
			parent.dispose();

		if (sliceObserver != null)
			sliceObserver.unregister();

		if (imp != null) {
			if (roiListener != null)
				imp.getCanvas().removeMouseListener(roiListener);
			if (imp.getOverlay() != null) {
				imp.getOverlay().clear();
				imp.updateAndDraw();
			}
		}

		isFinished = true;
	}

	protected final void close(final Frame parent, final SliceObserver sliceObserver, RoiListener roiListener) {
		if (parent != null)
			parent.dispose();

		if (sliceObserver != null)
			sliceObserver.unregister();
		if (roiListener != null)
			imp.getCanvas().removeMouseListener(roiListener);
		if (imp != null)
			imp.close();
		if (preprocessedimp != null)
			preprocessedimp.close();

		isFinished = true;
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

	public float computeValueFromScrollbarPosition(final int scrollbarPosition, final float min, final float max,
			final int scrollbarSize) {
		return min + (scrollbarPosition / (float) scrollbarSize) * (max - min);
	}

	public int computeScrollbarPositionFromValue(final float sigma, final float min, final float max,
			final int scrollbarSize) {
		return Util.round(((sigma - min) / (max - min)) * scrollbarSize);
	}

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

	public static void main(String[] args) {
		new ImageJ();

		JFrame frame = new JFrame("");
		FileChooser panel = new FileChooser();

		frame.getContentPane().add(panel, "Center");
		frame.setSize(panel.getPreferredSize());
	}

	

}
