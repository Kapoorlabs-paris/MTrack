package interactiveMT;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Rectangle;
import java.awt.Scrollbar;
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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;


import LineModels.UseLineModel.UserChoiceModel;
import ch.qos.logback.core.rolling.helper.RollingCalendar;
import costMatrix.CostFunction;
import costMatrix.SquareDistCostFunction;
import drawandOverlay.DisplayGraph;
import drawandOverlay.DisplayGraphKalman;
import drawandOverlay.DisplaysubGraphend;
import drawandOverlay.DisplaysubGraphstart;
import fiji.tool.SliceListener;
import fiji.tool.SliceObserver;
import graphconstructs.KalmanTrackproperties;
import graphconstructs.Trackproperties;
import houghandWatershed.WatershedDistimg;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.EllipseRoi;
import ij.gui.GenericDialog;
import ij.gui.Line;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.io.Opener;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.plugin.frame.RoiManager;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import interactiveMT.InteractiveKymoAnalyze.GetBaseCords;
import interactiveMT.InteractiveKymoAnalyze.GetCords;
import interactiveMT.InteractiveKymoAnalyze.GetLength;
import labeledObjects.CommonOutputHF;
import labeledObjects.Indexedlength;
import labeledObjects.KalmanIndexedlength;
import labeledObjects.Shrink;
import labeledObjects.Subgraphs;
import lineFinder.FindlinesVia;
import lineFinder.LinefinderInteractiveHFHough;
import lineFinder.LinefinderInteractiveHFMSER;
import lineFinder.LinefinderInteractiveHFMSERwHough;
import lineFinder.LinefinderInteractiveHough;
import lineFinder.LinefinderInteractiveMSER;
import lineFinder.LinefinderInteractiveMSERwHough;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import mpicbg.imglib.multithreading.SimpleMultiThreading;
import mpicbg.imglib.util.Util;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.componenttree.mser.Mser;
import net.imglib2.algorithm.componenttree.mser.MserTree;
import net.imglib2.algorithm.stats.Normalize;
import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.img.imageplus.FloatImagePlus;
import net.imglib2.type.Type;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Intervals;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;
import peakFitter.SortListbyproperty;
import preProcessing.GetLocalmaxmin;
import preProcessing.Kernels;
import preProcessing.MedianFilter2D;
import trackerType.KFsearch;
import trackerType.MTTracker;
import trackerType.TrackModel;
import util.Boundingboxes;
import velocityanalyser.Trackend;
import velocityanalyser.Trackstart;

/**
 * An interactive tool for MT tracking using MSER and Hough Transform
 * 
 * @author Varun Kapoor
 */

public class Interactive_MT implements PlugIn {

	String usefolder = IJ.getDirectory("imagej");
	ColorProcessor cp = null;
	String addToName = "MTTrack";
	String addTrackToName = "MTTrack";
	ArrayList<float[]> deltadstart = new ArrayList<>();
	ArrayList<float[]> deltadend = new ArrayList<>();
	ArrayList<float[]> deltad = new ArrayList<>();
	ArrayList<float[]> Length;
	final int scrollbarSize = 1000;
	final int scrollbarSizebig = 1000;
	// steps per octave
	public static int standardSensitivity = 4;
	int sensitivity = standardSensitivity;
	float deltaMin = 0;
	float thetaPerPixelMin = new Float(0.2);
	float rhoPerPixelMin = new Float(0.2);
	MouseListener ml;
	float thresholdHoughMin = 0;
	float thresholdHoughMax = 250;
	float deltaMax = 400f;
	float maxVarMin = 0;
	float maxVarMax = 1;
	float thetaPerPixelMax = 2;
	float rhoPerPixelMax = 2;
	JProgressBar jpb;
	JLabel label = new JLabel("Progress..");
	JFrame frame = new JFrame();
	JPanel panel = new JPanel();
	int Progressmin = 0;
	int Progressmax = 100;
	int max = Progressmax;
	float deltadcutoff = 5;
	boolean analyzekymo = false;
	boolean darktobright = false;
	boolean displayBitimg = false;
	boolean displayWatershedimg = false;
	boolean displayoverlay = true;
	long minSize = 1;
	long maxSize = 1000;
	long minSizemin = 0;
	long minSizemax = 1000;
	long maxSizemin = 100;
	long maxSizemax = 10000;
	double netdeltad = 0;
	double Intensityratio = 0.5;
	double Inispacing = 0.5;
	int thirdDimensionslider = 0;
	int thirdDimensionsliderInit = 1;
	int timeMin = 1;

	float minDiversityMin = 0;
	float minDiversityMax = 1;

	UserChoiceModel userChoiceModel;
	float delta = 1f;
	int deltaInit = 10;
	int maxVarInit = 1;

	int minSizeInit = 1;
	int maxSizeInit = 100;
	float thresholdHoughInit = 100;
	float rhoPerPixelInit = new Float(0.5);
	float thetaPerPixelInit = new Float(0.5);

	float frametosec;
	public int minDiversityInit = 1;
	public int radius = 1;
	public long Size = 1;
	public float thetaPerPixel = 1;
	public float rhoPerPixel = 1;
	boolean enablerhoPerPixel = false;
	public float maxVar = 1;
	public float minDiversity = 1;
	public float thresholdHough = 1;
	double netdeltadstart = 0;
	double netdeltadend = 0;
	Color colorDraw = null;
	FloatType minval = new FloatType(0);
	FloatType maxval = new FloatType(1);
	SliceObserver sliceObserver;
	RoiListener roiListener;
	boolean numberKymo = false;
	boolean numberTracker = true;
	boolean isComputing = false;
	boolean isStarted = false;
	boolean redo = false;
	boolean redoAccept = false;
	boolean FindLinesViaMSER = false;
	boolean FindLinesViaHOUGH = false;
	boolean FindLinesViaMSERwHOUGH = false;
	boolean ShowMser = false;
	boolean ShowHough = false;
	boolean update = false;
	boolean Canny = false;
	boolean showKalman = false;
	boolean showDeterministic = false;
	boolean RoisViaMSER = false;
	boolean RoisViaWatershed = false;
	boolean displayTree = false;
	boolean GaussianLines = true;
	boolean Mediancurr = false;
	boolean MedianAll = false;
	boolean AutoDelta = false;
	boolean Domask = false;
	boolean DoRloop = false;
	boolean SaveTxt = true;
	boolean SaveXLS = true;
	boolean finalpoint = false;
	boolean Trackstart;
	int nbRois;
	Roi rorig = null;
	ArrayList<double[]> lengthtimestart = new ArrayList<double[]>();
	ArrayList<double[]> lengthtimeend = new ArrayList<double[]>();
	ArrayList<double[]> lengthtime = new ArrayList<double[]>();
	MTTracker MTtrackerstart;
	MTTracker MTtrackerend;
	CostFunction<KalmanTrackproperties, KalmanTrackproperties> UserchosenCostFunction;
	float initialSearchradius = 20;
	int starttime = 0;
	int endtime = 0;
	float maxSearchradius = 15;
	int missedframes = 1;
	public int initialSearchradiusInit = (int) initialSearchradius;
	public float initialSearchradiusMin = 0;
	public float initialSearchradiusMax = 100;

	public int maxSearchradiusInit = (int) maxSearchradius;
	public float maxSearchradiusMin = 10;
	public float maxSearchradiusMax = 500;

	public int missedframesInit = missedframes;
	public float missedframesMin = 0;
	public float missedframesMax = 100;
	HashMap<Integer, Boolean> whichend = new HashMap<Integer, Boolean>();
	ArrayList<float[]> finalvelocity = new ArrayList<float[]>();
	ArrayList<float[]> finalvelocityKymo = new ArrayList<float[]>();
	ArrayList<ArrayList<Trackproperties>> Allstart = new ArrayList<ArrayList<Trackproperties>>();
	ArrayList<ArrayList<Trackproperties>> Allend = new ArrayList<ArrayList<Trackproperties>>();

	ArrayList<ArrayList<KalmanTrackproperties>> AllstartKalman = new ArrayList<ArrayList<KalmanTrackproperties>>();
	ArrayList<ArrayList<KalmanTrackproperties>> AllendKalman = new ArrayList<ArrayList<KalmanTrackproperties>>();

	int channel = 0;
	int thirdDimensionSize = 0;
	ImagePlus Kymoimp;
	RandomAccessibleInterval<FloatType> originalimg;
	RandomAccessibleInterval<FloatType> originalPreprocessedimg;
	RandomAccessibleInterval<FloatType> Kymoimg;
	RandomAccessibleInterval<FloatType> CurrentView;
	RandomAccessibleInterval<FloatType> CurrentPreprocessedView;
	int inix = 20;
	int iniy = 20;
	double[] calibration;
	double radiusfactor = 0.8;
	MserTree<UnsignedByteType> newtree;
	// Image 2d at the current slice
	RandomAccessibleInterval<FloatType> currentimg;
	RandomAccessibleInterval<FloatType> currentPreprocessedimg;
	RandomAccessibleInterval<IntType> intimg;
	Color originalColor = new Color(0.8f, 0.8f, 0.8f);
	Color inactiveColor = new Color(0.95f, 0.95f, 0.95f);
	ImagePlus imp;
	ImagePlus impcopy;
	ImagePlus preprocessedimp;
	double[] psf;
	int count, startdim;
	int minlength;
	int Maxlabel;
	private int ndims;
	ArrayList<int[]> ClickedPoints = new ArrayList<int[]>();
	Pair<ArrayList<Indexedlength>, ArrayList<Indexedlength>> PrevFrameparam;
	Pair<ArrayList<Indexedlength>, ArrayList<Indexedlength>> NewFrameparam;
	ArrayList<Integer> Accountedframes = new ArrayList<Integer>();
	ArrayList<Integer> Missedframes = new ArrayList<Integer>();
	Pair<Pair<ArrayList<Trackproperties>, ArrayList<Trackproperties>>, Pair<ArrayList<Indexedlength>, ArrayList<Indexedlength>>> returnVector;

	Pair<ArrayList<KalmanIndexedlength>, ArrayList<KalmanIndexedlength>> PrevFrameparamKalman;
	Pair<ArrayList<KalmanIndexedlength>, ArrayList<KalmanIndexedlength>> NewFrameparamKalman;
	Pair<Pair<ArrayList<KalmanTrackproperties>, ArrayList<KalmanTrackproperties>>, Pair<ArrayList<KalmanIndexedlength>, ArrayList<KalmanIndexedlength>>> returnVectorKalman;

	ArrayList<CommonOutputHF> output;
	ImageStack prestack;
	public Rectangle standardRectangle;
	public FinalInterval interval;
	RandomAccessibleInterval<UnsignedByteType> newimg;
	ArrayList<double[]> AllmeanCovar;

	// first and last slice to process
	int endStack, thirdDimension;

	public static enum ValueChange {
		ROI, ALL, DELTA, FindLinesVia, MAXVAR, MINDIVERSITY, DARKTOBRIGHT, MINSIZE, 
		MAXSIZE, SHOWMSER, FRAME, SHOWHOUGH, thresholdHough, DISPLAYBITIMG, DISPLAYWATERSHEDIMG,
		rhoPerPixel, thetaPerPixel, THIRDDIM, iniSearch, maxSearch, missedframes, THIRDDIMTrack, MEDIAN, kymo;
	}

	boolean isFinished = false;
	boolean wasCanceled = false;
	boolean SecondOrderSpline;
	boolean ThirdOrderSpline;

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

	public boolean getFindLinesViaMSER() {
		return FindLinesViaMSER;
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

	public boolean getFindLinesViaHOUGH() {
		return FindLinesViaHOUGH;
	}

	public boolean getFindLinesViaMSERwHOUGH() {
		return FindLinesViaMSERwHOUGH;
	}

	public void setFindLinesViaMSER(final boolean FindLinesViaMSER) {
		this.FindLinesViaMSER = FindLinesViaMSER;
	}

	public void setFindLinesViaHOUGH(final boolean FindLinesViaHOUGH) {
		this.FindLinesViaHOUGH = FindLinesViaHOUGH;
	}

	public void setFindLinesViaMSERwHOUGH(final boolean FindLinesViaMSERwHOUGH) {
		this.FindLinesViaMSERwHOUGH = FindLinesViaMSERwHOUGH;
	}

	public void setInitialDelta(final float value) {
		delta = value;
		deltaInit = computeScrollbarPositionFromValue(delta, deltaMin, deltaMax, scrollbarSize);
	}

	public double getInitialDelta(final float value) {

		return delta;

	}

	public void setInitialsearchradius(final float value) {
		initialSearchradius = value;
		initialSearchradiusInit = computeScrollbarPositionFromValue(initialSearchradius, initialSearchradiusMin,
				initialSearchradiusMax, scrollbarSize);
	}

	public void setInitialmaxsearchradius(final float value) {
		maxSearchradius = value;
		maxSearchradiusInit = computeScrollbarPositionFromValue(maxSearchradius, maxSearchradiusMin, maxSearchradiusMax,
				scrollbarSize);
	}

	public double getInitialsearchradius(final float value) {

		return delta;

	}

	public void setInitialmaxVar(final float value) {
		maxVar = value;
		maxVarInit = computeScrollbarPositionFromValue(maxVar, maxVarMin, maxVarMax, scrollbarSize);
	}

	public double getInitialmaxVar(final float value) {

		return maxVar;

	}

	public void setInitialthresholdHough(final float value) {
		thresholdHough = value;
		thresholdHoughInit = computeScrollbarPositionFromValue(thresholdHough, thresholdHoughMin, thresholdHoughMax,
				scrollbarSize);
	}

	public void setInitialthetaPerPixel(final float value) {
		thetaPerPixel = value;
		thetaPerPixelInit = computeScrollbarPositionFromValue(thetaPerPixel, thetaPerPixelMin, thetaPerPixelMax,
				scrollbarSize);
	}

	public void setInitialrhoPerPixel(final float value) {
		rhoPerPixel = value;
		rhoPerPixelInit = computeScrollbarPositionFromValue(rhoPerPixel, rhoPerPixelMin, rhoPerPixelMax, scrollbarSize);
	}

	public double getInitialthresholdHough(final float value) {

		return thresholdHough;

	}

	public double getInitialthetaPerPixel(final float value) {

		return thetaPerPixel;

	}

	public double getInitialrhoPerPixel(final float value) {

		return rhoPerPixel;

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

	public Interactive_MT(){};
	
	public Interactive_MT(final RandomAccessibleInterval<FloatType> originalimg,
			final RandomAccessibleInterval<FloatType> originalPreprocessedimg, final double[] psf,
			final double[] imgCal, final int minlength, final float frametosec) {

		this.originalimg = originalimg;
		this.originalPreprocessedimg = originalPreprocessedimg;
		this.psf = psf;
		this.Kymoimg = null;
		this.minlength = minlength;
		this.frametosec = frametosec;
		standardRectangle = new Rectangle(inix, iniy, (int) originalimg.dimension(0) - 2 * inix,
				(int) originalimg.dimension(1) - 2 * iniy);
		imp = ImageJFunctions.show(originalimg);
		impcopy = imp.duplicate();

		calibration = imgCal;
		System.out.println(calibration[0] + " " + calibration[1]);

	}

	public Interactive_MT(final RandomAccessibleInterval<FloatType> originalimg,
			final RandomAccessibleInterval<FloatType> originalPreprocessedimg,
			final RandomAccessibleInterval<FloatType> kymoimg, final double[] psf, final double[] imgCal,
			final int minlength, final float frametosec) {

		this.originalimg = originalimg;
		this.originalPreprocessedimg = originalPreprocessedimg;
		this.Kymoimg = kymoimg;
		this.psf = psf;
		this.minlength = minlength;
		this.frametosec = frametosec;
		standardRectangle = new Rectangle(inix, iniy, (int) originalimg.dimension(0) - 2 * inix,
				(int) originalimg.dimension(1) - 2 * iniy);
		imp = ImageJFunctions.show(originalimg);
		impcopy = imp.duplicate();

		calibration = imgCal;
		System.out.println(calibration[0] + " " + calibration[1]);

	}

	@Override
	public void run(String arg) {
		jpb = new JProgressBar();
		UserchosenCostFunction = new SquareDistCostFunction();
		Inispacing = 0.5 * Math.min(psf[0], psf[1]);
		count = 0;
		if (originalimg.numDimensions() < 3) {

			thirdDimensionSize = 0;
		}

		if (originalimg.numDimensions() == 3) {

			thirdDimension = 1;
			startdim = 1;
			thirdDimensionSize = (int) originalimg.dimension(2);

		}

		if (originalimg.numDimensions() > 3) {

			System.out.println("Image has wrong dimensionality, upload an XYT image");
			return;
		}

		if (Kymoimg != null) {
			Kymoimp = ImageJFunctions.show(Kymoimg);

		}
		prestack = new ImageStack((int) originalimg.dimension(0), (int) originalimg.dimension(1),
				java.awt.image.ColorModel.getRGBdefault());
		
		CurrentView = getCurrentView();
		CurrentPreprocessedView = getCurrentPreView();

		output = new ArrayList<CommonOutputHF>();
		endStack = thirdDimensionSize;
		preprocessedimp = ImageJFunctions.show(CurrentPreprocessedView);

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

		// copy the ImagePlus into an ArrayImage<FloatType> for faster access
		// displaySliders();
		Card();
		// add listener to the imageplus slice slider
		sliceObserver = new SliceObserver(preprocessedimp, new ImagePlusListener());
		// compute first version#
		updatePreview(ValueChange.ALL);
		isStarted = true;

		// check whenever roi is modified to update accordingly
		roiListener = new RoiListener();
		preprocessedimp.getCanvas().addMouseListener(roiListener);

		IJ.log(" Third Dimension Size " + thirdDimensionSize);

	}

	/**
	 * Updates the Preview with the current parameters (sigma, threshold, roi,
	 * slicenumber)
	 * 
	 * @param change
	 *            - what did change
	 */

	protected void updatePreview(final ValueChange change) {
		
		boolean roiChanged = false;
		if (change == ValueChange.THIRDDIM) {
			System.out.println("Current Time point: " + thirdDimension);
			if (preprocessedimp != null)
				preprocessedimp.close();

			preprocessedimp = ImageJFunctions.show(CurrentPreprocessedView);
			preprocessedimp.setTitle("Preprocessed image Current View in third dimension: " + " " + thirdDimension);
		}

		RoiManager roimanager = RoiManager.getInstance();

		if (roimanager == null) {
			roimanager = new RoiManager();
		}
		if (roiChanged) {
			roimanager.close();
			roimanager = new RoiManager();
		}

		if (change == ValueChange.THIRDDIMTrack) {
			// check if Roi changed
			System.out.println("Current Time point: " + thirdDimension);

			long[] min = { (long) standardRectangle.getMinX(), (long) standardRectangle.getMinY() };
			long[] max = { (long) standardRectangle.getMaxX(), (long) standardRectangle.getMaxY() };
			interval = new FinalInterval(min, max);
			final long Cannyradius = (long) (radiusfactor * Math.ceil(Math.sqrt(psf[0] * psf[0] + psf[1] * psf[1])));

			currentimg = extractImage(CurrentView);
			currentPreprocessedimg = extractImage(CurrentPreprocessedView);
			// Expand the image by 10 pixels

			Interval spaceinterval = Intervals.createMinMax(
					new long[] { currentimg.min(0), currentimg.min(1), currentimg.max(0), currentimg.max(1) });
			Interval interval = Intervals.expand(spaceinterval, 10);
			currentimg = Views.interval(Views.extendBorder(currentimg), interval);
			currentPreprocessedimg = Views.interval(Views.extendBorder(currentPreprocessedimg), interval);

			newimg = copytoByteImage(Kernels.CannyEdgeandMean(currentPreprocessedimg, Cannyradius));
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
				final long Cannyradius = (long) (radiusfactor
						* Math.ceil(Math.sqrt(psf[0] * psf[0] + psf[1] * psf[1])));

				currentimg = extractImage(CurrentView);
				currentPreprocessedimg = extractImage(CurrentPreprocessedView);
				// Expand the image by 10 pixels

				Interval spaceinterval = Intervals.createMinMax(
						new long[] { currentimg.min(0), currentimg.min(1), currentimg.max(0), currentimg.max(1) });
				Interval interval = Intervals.expand(spaceinterval, 10);
				currentimg = Views.interval(Views.extendBorder(currentimg), interval);
				currentPreprocessedimg = Views.interval(Views.extendBorder(currentPreprocessedimg), interval);

				newimg = copytoByteImage(Kernels.CannyEdgeandMean(currentPreprocessedimg, Cannyradius));

				roiChanged = true;
			}
		}

		if (change != ValueChange.THIRDDIMTrack) {
			
			
			
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
				final long Cannyradius = (long) (radiusfactor
						* Math.ceil(Math.sqrt(psf[0] * psf[0] + psf[1] * psf[1])));

				currentimg = extractImage(CurrentView);
				currentPreprocessedimg = extractImage(CurrentPreprocessedView);
				// Expand the image by 10 pixels

				Interval spaceinterval = Intervals.createMinMax(
						new long[] { currentimg.min(0), currentimg.min(1), currentimg.max(0), currentimg.max(1) });
				Interval interval = Intervals.expand(spaceinterval, 10);
				currentimg = Views.interval(Views.extendBorder(currentimg), interval);
				currentPreprocessedimg = Views.interval(Views.extendBorder(currentPreprocessedimg), interval);

				newimg = copytoByteImage(Kernels.CannyEdgeandMean(currentPreprocessedimg, Cannyradius));

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

		if (change == ValueChange.SHOWHOUGH) {

			IJ.log("Doing watershedding on the distance transformed image ");

			RandomAccessibleInterval<BitType> bitimg = new ArrayImgFactory<BitType>().create(newimg, new BitType());
			GetLocalmaxmin.ThresholdingBit(newimg, bitimg, thresholdHough);

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

		}
          
		if (change == ValueChange.SHOWMSER) {

			IJ.log(" Computing the Component tree");

			newtree = MserTree.buildMserTree(newimg, delta, minSize, maxSize, maxVar, minDiversity, darktobright);
			Rois = getcurrentRois(newtree);
			
			
            
			count++;
			
			if (count == 1)
				startdim = thirdDimension;
				
			if (preprocessedimp != null) {
			
				Overlay o = preprocessedimp.getOverlay();

				if (o == null) {
					o = new Overlay();
					preprocessedimp.setOverlay(o);
				}
				
				
				
				prestack.addSlice(ImageJFunctions.wrap(CurrentView , "display stack").getImageStack().getProcessor(thirdDimension).convertToRGB());
				cp = (ColorProcessor) (prestack.getProcessor(thirdDimension).duplicate());
				
				
				
				o.clear();

				for (int index = 0; index < Rois.size(); ++index) {

					EllipseRoi or = Rois.get(index);

					or.setStrokeColor(Color.red);
					o.add(or);
					
					if (displayoverlay ){
						
						cp.setColor(Color.red);
						cp.setLineWidth(1);
						cp.draw(or);
						
						
						}
					
					
					
					roimanager.addRoi(or);
					
					
					
				}
				
			
				
				if (displayoverlay && prestack!=null ){
				prestack.setPixels(cp.getPixels(), thirdDimension);
				
				}
				if (count > 1 && thirdDimension == startdim){
					//for (int index = 1; index < prestack.getSize(); ++index)
						prestack.deleteLastSlice();
					
				
				}
				
				
			}

		}

		if (preprocessedimp != null)
			preprocessedimp.updateAndDraw();
		roiListener = new RoiListener();
		preprocessedimp.getCanvas().addMouseListener(roiListener);
		isComputing = false;

	}

	private boolean maxStack() {
		GenericDialog gd = new GenericDialog("Choose Final Frame");
		if (thirdDimensionSize > 1) {

			gd.addNumericField("Do till frame", thirdDimensionSize, 0);

			assert (int) gd.getNextNumber() > 1;
		}

		gd.showDialog();
		if (thirdDimensionSize > 1) {
			thirdDimensionSize = (int) gd.getNextNumber();

		}
		return !gd.wasCanceled();

	}

	// Making the card
	JFrame Cardframe = new JFrame("MicroTubule Tracker");
	JPanel panelCont = new JPanel();
	JPanel panelFirst = new JPanel();
	JPanel panelSecond = new JPanel();
	JPanel panelThird = new JPanel();
	JPanel panelFourth = new JPanel();
	JPanel panelFifth = new JPanel();
	JPanel panelSixth = new JPanel();
	JPanel panelSeventh = new JPanel();
	JPanel panelEighth = new JPanel();
	JPanel panelNinth = new JPanel();

	public void Card() {

		CardLayout cl = new CardLayout();

		panelCont.setLayout(cl);

		panelCont.add(panelFirst, "1");
		panelCont.add(panelSecond, "2");
		panelCont.add(panelThird, "3");
		panelCont.add(panelFourth, "4");
		panelCont.add(panelFifth, "5");
		panelCont.add(panelSixth, "6");
		panelCont.add(panelSeventh, "7");
		panelCont.add(panelEighth, "8");
		panelCont.add(panelNinth, "9");

		// First Panel
		panelFirst.setName("Preprocess and Determine Seeds");

		CheckboxGroup Finders = new CheckboxGroup();
		final Checkbox MedFilterAll = new Checkbox("Apply Median Filter to Stack", MedianAll);
		final Scrollbar thirdDimensionslider = new Scrollbar(Scrollbar.HORIZONTAL, thirdDimensionsliderInit, 0, 0,
				thirdDimensionSize);
		thirdDimensionslider.setBlockIncrement(1);
		this.thirdDimensionslider = (int) computeIntValueFromScrollbarPosition(thirdDimensionsliderInit,
				timeMin, thirdDimensionSize, thirdDimensionSize);
		final Label timeText = new Label("Time index = " + this.thirdDimensionslider, Label.CENTER);
		final Button JumpinTime = new Button("Jump in time :");
		final Label MTText = new Label("Preprocess and Determine Seed Ends (Green Channel)", Label.CENTER);
		final Label Step = new Label("Step 1", Label.CENTER);
		final Checkbox Analyzekymo = new Checkbox("Analyze Kymograph");

		final Checkbox mser = new Checkbox("MSER", Finders, FindLinesViaMSER);
		final Checkbox hough = new Checkbox("HOUGH", Finders, FindLinesViaHOUGH);
		final Checkbox mserwhough = new Checkbox("MSERwHOUGH", Finders, FindLinesViaMSERwHOUGH);

		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints c = new GridBagConstraints();

		panelFirst.setLayout(layout);
		panelSecond.setLayout(layout);
		panelFirst.add(Step, c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;

		final Label Pre = new Label("Preprocess");
		final Label Ends = new Label("Method Choice for Seed Ends Determination");
		final Label Kymo = new Label("Analyze Kymo");
		++c.gridy;
		c.insets = new Insets(10, 10, 0, 0);
		panelFirst.add(MTText, c);

		++c.gridy;
		panelFirst.add(Pre, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 0);
		panelFirst.add(MedFilterAll, c);

		++c.gridy;
		panelFirst.add(Ends, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 0);
		panelFirst.add(mser, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 0);
		panelFirst.add(hough, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 0);
		panelFirst.add(mserwhough, c);
		++c.gridy;
		if (Kymoimg!=null){
		c.insets = new Insets(10, 10, 0, 0);
		panelFirst.add(Kymo, c);
		++c.gridy;
		c.insets = new Insets(10, 10, 0, 0);
		panelFirst.add(Analyzekymo, c);
		}
		
		
		
		if (thirdDimensionSize > 1) {
			++c.gridy;
			panelFirst.add(thirdDimensionslider, c);

			++c.gridy;
			panelFirst.add(timeText, c);

			++c.gridy;
			c.insets = new Insets(0, 175, 0, 175);
			panelFirst.add(JumpinTime, c);
		}

		panelFirst.setVisible(true);

		cl.show(panelCont, "1");

		// MedFiltercur.addItemListener(new MediancurrListener() );
		MedFilterAll.addItemListener(new MedianAllListener());

		// ChoiceofTracker.addActionListener(new
		// TrackerButtonListener(Cardframe));
		mser.addItemListener(new MserListener());
		Analyzekymo.addItemListener(new AnalyzekymoListener());
		hough.addItemListener(new HoughListener());
		mserwhough.addItemListener(new MserwHoughListener());

		JPanel control = new JPanel();

		control.add(new JButton(new AbstractAction("\u22b2Prev") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout) panelCont.getLayout();
				cl.previous(panelCont);
			}
		}));
		control.add(new JButton(new AbstractAction("Next\u22b3") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout) panelCont.getLayout();
				cl.next(panelCont);
			}
		}));
		// Panel Third
		final Button MoveNext = new Button("Update method and parameters (first image in dynamic channel)");
		final Button JumptoFrame = new Button("Update method and parameters (choose an image in dynamic channel)");
		final Button ClickFast = new Button("Click here to track only fast ends, click seed ends on image after that");
		final Checkbox Finalize = new Checkbox("Confirm the end point(s)");
		final Label MTTextHF = new Label("Choose End point finding method for Higher Frames (dynamic channel)",
				Label.CENTER);
		final Label Step3 = new Label("Step 3", Label.CENTER);
		final Checkbox txtfile = new Checkbox("Save tracks as TXT file", SaveTxt);
		final Checkbox xlsfile = new Checkbox("Save tracks as XLS file", SaveXLS);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;

		panelThird.setLayout(layout);
		panelThird.add(Step3, c);
		panelEighth.setLayout(layout);
		++c.gridy;
		c.insets = new Insets(10, 10, 0, 0);
		panelThird.add(MTTextHF, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 220);
		panelThird.add(MoveNext, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 180);
		panelThird.add(JumptoFrame, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 180);
		panelThird.add(ClickFast, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 180);
		panelThird.add(Finalize, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 0);
		panelThird.add(txtfile, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 0);
		panelThird.add(xlsfile, c);

		MoveNext.addActionListener(new moveNextListener());
		JumptoFrame.addActionListener(new moveToFrameListener());
		ClickFast.addActionListener(new chooseendListener());
		thirdDimensionslider
				.addAdjustmentListener(new thirdDimensionsliderListener(timeText, timeMin, thirdDimensionSize));
		Cardframe.addWindowListener(new FrameListener(Cardframe));
		JumpinTime.addActionListener(
				new moveInThirdDimListener(thirdDimensionslider, timeText, timeMin, thirdDimensionSize));

		txtfile.addItemListener(new SaveasTXT());
		Finalize.addItemListener(new finalpoint());
		xlsfile.addItemListener(new SaveasXLS());

		MTText.setFont(MTText.getFont().deriveFont(Font.BOLD));
		Pre.setBackground(new Color(1, 0, 1));
		Pre.setForeground(new Color(255, 255, 255));
		Ends.setBackground(new Color(1, 0, 1));
		Ends.setForeground(new Color(255, 255, 255));
		Kymo.setBackground(new Color(1, 0, 1));
		Kymo.setForeground(new Color(255, 255, 255));
		MTTextHF.setFont(MTTextHF.getFont().deriveFont(Font.BOLD));

		if (analyzekymo == false && Kymoimg==null) {
			
			final Label Step6 = new Label("Step 6", Label.CENTER);
			panelSixth.setLayout(layout);
			
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			panelSixth.add(Step6, c);
			final Checkbox KalmanTracker = new Checkbox("Use Kalman Filter for tracking");
			final Checkbox DeterTracker = new Checkbox("Use Deterministic method for tracking");
			final Label Kal = new Label("Use Kalman Filter for probabilistic tracking");
			final Label Det = new Label("Use Deterministic tracker using the fixed Seed points");
			Kal.setBackground(new Color(1, 0, 1));
			Kal.setForeground(new Color(255, 255, 255));
			Det.setBackground(new Color(1, 0, 1));
			Det.setForeground(new Color(255, 255, 255));

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			panelSixth.add(Kal, c);

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			panelSixth.add(KalmanTracker, c);

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			panelSixth.add(Det, c);

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			panelSixth.add(DeterTracker, c);

			KalmanTracker.addItemListener(new KalmanchoiceListener());
			DeterTracker.addItemListener(new DeterchoiceListener());
		}
		
		if (analyzekymo == false || Kymoimg == null){
		panelEighth.removeAll();
		final Label Step8 = new Label("Step 6", Label.CENTER);
		panelEighth.setLayout(layout);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 4;
		c.weighty = 1.5;

		panelEighth.add(Step8, c);
	
		final Label SuccessB = new Label(" Now you can compute rates, choose start and end frame: ",
				Label.CENTER);

		final Label Done = new Label("The results have been compiled and stored, you can now exit",
				Label.CENTER);

		final Button Analyze = new Button("Do Rough Analysis");

		final Scrollbar startS = new Scrollbar(Scrollbar.HORIZONTAL, thirdDimensionsliderInit, 10, 0,
				10 + scrollbarSize);
		final Scrollbar endS = new Scrollbar(Scrollbar.HORIZONTAL, thirdDimensionsliderInit, 10, 0,
				10 + scrollbarSize);
		starttime = (int) computeValueFromScrollbarPosition(thirdDimensionsliderInit, 0, thirdDimensionSize,
				scrollbarSize);
		endtime = (int) computeValueFromScrollbarPosition(thirdDimensionsliderInit, 0, thirdDimensionSize,
				scrollbarSize);
		final Label startText = new Label("startFrame = ", Label.CENTER);
		final Label endText = new Label("endFrame = ", Label.CENTER);

		

		SuccessB.setBackground(new Color(1, 0, 1));
		SuccessB.setForeground(new Color(255, 255, 255));
		
		++c.gridy;
		c.insets = new Insets(10, 10, 0, 50);
		panelEighth.add(SuccessB, c);
		
		
		++c.gridy;
		c.insets = new Insets(10, 10, 0, 50);
		panelEighth.add(startText, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 50);
		panelEighth.add(startS, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 50);
		panelEighth.add(endText, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 50);
		panelEighth.add(endS, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 50);
		panelEighth.add(Analyze, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 50);
		panelEighth.add(Done, c);

		startS.addAdjustmentListener(new starttimeListener(startText, thirdDimensionsliderInit,
				thirdDimensionSize, scrollbarSize, startS));
		endS.addAdjustmentListener(new endtimeListener(endText, thirdDimensionsliderInit, thirdDimensionSize,
				scrollbarSize, endS));
		Analyze.addActionListener(new AnalyzeListener());

		panelEighth.validate();
		panelEighth.repaint();
		}

		
		

		panelNinth.setLayout(layout);
		final Label Done = new Label("Hope that everything was to your satisfaction!");
		final Button Exit= new Button("Close and exit");
		
		
		Done.setBackground(new Color(1, 0, 1));
		Done.setForeground(new Color(255, 255, 255));
		++c.gridy;
		c.insets = new Insets(10, 10, 0, 50);
		panelNinth.add(Done, c);
		
		++c.gridy;
		c.insets = new Insets(10, 10, 0, 50);
		panelNinth.add(Exit, c);
		
		

		Exit.addActionListener(new FinishedButtonListener(Cardframe, true));
		
		Cardframe.add(panelCont, BorderLayout.CENTER);
		Cardframe.add(control, BorderLayout.SOUTH);
		Cardframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Cardframe.pack();
		Cardframe.setVisible(true);

	}

	protected class starttimeListener implements AdjustmentListener {
		final Label label;
		final float min, max;
		final int scrollbarSize;

		final Scrollbar deltaScrollbar;

		public starttimeListener(final Label label, final float min, final float max, final int scrollbarSize,
				final Scrollbar deltaScrollbar) {
			this.label = label;
			this.min = min;
			this.max = max;
			this.scrollbarSize = scrollbarSize;

			this.deltaScrollbar = deltaScrollbar;

		}

		@Override
		public void adjustmentValueChanged(final AdjustmentEvent event) {
			starttime = (int) computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize);

			deltaScrollbar.setValue(computeScrollbarPositionFromValue(starttime, min, max, scrollbarSize));

			label.setText("startFrame = " + starttime);

		}
	}

	protected class endtimeListener implements AdjustmentListener {
		final Label label;
		final float min, max;
		final int scrollbarSize;

		final Scrollbar deltaScrollbar;

		public endtimeListener(final Label label, final float min, final float max, final int scrollbarSize,
				final Scrollbar deltaScrollbar) {
			this.label = label;
			this.min = min;
			this.max = max;
			this.scrollbarSize = scrollbarSize;

			this.deltaScrollbar = deltaScrollbar;

		}

		@Override
		public void adjustmentValueChanged(final AdjustmentEvent event) {
			endtime = (int) computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize);

			deltaScrollbar.setValue(computeScrollbarPositionFromValue(endtime, min, max, scrollbarSize));

			label.setText("endFrame = " + endtime);

		}
	}

	protected class SearchradiusListener implements AdjustmentListener {
		final Label label;
		final float min, max;

		public SearchradiusListener(final Label label, final float min, final float max) {
			this.label = label;
			this.min = min;
			this.max = max;
		}

		@Override
		public void adjustmentValueChanged(final AdjustmentEvent event) {
			initialSearchradius = computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize);
			label.setText("Initial Search Radius:  = " + initialSearchradius);

			if (!isComputing) {
				updatePreview(ValueChange.iniSearch);
			} else if (!event.getValueIsAdjusting()) {
				while (isComputing) {
					SimpleMultiThreading.threadWait(10);
				}
				updatePreview(ValueChange.iniSearch);
			}
		}
	}

	protected class maxSearchradiusListener implements AdjustmentListener {
		final Label label;
		final float min, max;

		public maxSearchradiusListener(final Label label, final float min, final float max) {
			this.label = label;
			this.min = min;
			this.max = max;
		}

		@Override
		public void adjustmentValueChanged(final AdjustmentEvent event) {
			maxSearchradius = computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize);
			label.setText("Max Search Radius:  = " + maxSearchradius);

			if (!isComputing) {
				updatePreview(ValueChange.maxSearch);
			} else if (!event.getValueIsAdjusting()) {
				while (isComputing) {
					SimpleMultiThreading.threadWait(10);
				}
				updatePreview(ValueChange.maxSearch);
			}
		}
	}

	protected class missedFrameListener implements AdjustmentListener {
		final Label label;
		final float min, max;

		public missedFrameListener(final Label label, final float min, final float max) {
			this.label = label;
			this.min = min;
			this.max = max;
		}

		@Override
		public void adjustmentValueChanged(final AdjustmentEvent event) {
			missedframes = (int) computeIntValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize);
			label.setText("Missed frames:  = " + missedframes);

			if (!isComputing) {
				updatePreview(ValueChange.missedframes);
			} else if (!event.getValueIsAdjusting()) {
				while (isComputing) {
					SimpleMultiThreading.threadWait(10);
				}
				updatePreview(ValueChange.missedframes);
			}
		}
	}

	protected class KymoExtractListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent arg0) {

			final GridBagLayout layout = new GridBagLayout();
			final GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			
			RoiManager roimanager = RoiManager.getInstance();

			if (roimanager != null) {

				roimanager.close();
				roimanager = new RoiManager();

			}

			panelSeventh.removeAll();
			final Label Step7 = new Label("Step 7", Label.CENTER);
			panelSeventh.setLayout(layout);
			panelSeventh.add(Step7, c);
			if (Kymoimg!=null)
			Kymoimp = ImageJFunctions.show(Kymoimg);
			final Label Select = new Label(
					"Make Segmented Line selection (Generates a file containing time (row 1) and length (row 2))");
			final Button ExtractKymo = new Button("Extract Mask Co-ordinates :");
			Select.setBackground(new Color(1, 0, 1));
			Select.setForeground(new Color(255, 255, 255));

			final Label Checkres = new Label("The tracker now performs an internal check on the results");
			Checkres.setBackground(new Color(1, 0, 1));
			Checkres.setForeground(new Color(255, 255, 255));

			if (analyzekymo ) {
				
				++c.gridy;
				c.insets = new Insets(10, 10, 0, 0);
				panelSeventh.add(Select, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 200);
				panelSeventh.add(ExtractKymo, c);

				ExtractKymo.addActionListener(new GetCords());
				
			}
			if (showDeterministic) {
				final Button TrackEndPoints = new Button("Track EndPoints (From first to a chosen last frame)");
				final Button SkipframeandTrackEndPoints = new Button(
						"TrackEndPoint (User specified first and last frame)");
				final Button CheckResults = new Button("Check Results (then click next)");
				
				
				++c.gridy;
				c.insets = new Insets(10, 10, 0, 175);
				panelSeventh.add(TrackEndPoints, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 175);
				panelSeventh.add(SkipframeandTrackEndPoints, c);

				if (analyzekymo && Kymoimg!=null){
				++c.gridy;
				c.insets = new Insets(10, 10, 0, 0);
				panelSeventh.add(Checkres, c);

				++c.gridy;
				c.insets = new Insets(10, 175, 0, 175);
				panelSeventh.add(CheckResults, c);
				}
				
				
				
				TrackEndPoints.addActionListener(new TrackendsListener());
				SkipframeandTrackEndPoints.addActionListener(new SkipFramesandTrackendsListener());
				CheckResults.addActionListener(new CheckResultsListener());
				
			}

			if (showKalman) {
				final Scrollbar rad = new Scrollbar(Scrollbar.HORIZONTAL, initialSearchradiusInit, 10, 0,
						10 + scrollbarSize);
				initialSearchradius = computeValueFromScrollbarPosition(initialSearchradiusInit, initialSearchradiusMin,
						initialSearchradiusMax, scrollbarSize);

				final Label SearchText = new Label("Initial Search Radius: " + initialSearchradius, Label.CENTER);

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelSeventh.add(SearchText, c);
				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelSeventh.add(rad, c);

				final Scrollbar Maxrad = new Scrollbar(Scrollbar.HORIZONTAL, maxSearchradiusInit, 10, 0,
						10 + scrollbarSize);
				maxSearchradius = computeValueFromScrollbarPosition(maxSearchradiusInit, maxSearchradiusMin,
						maxSearchradiusMax, scrollbarSize);
				final Label MaxMovText = new Label("Max Movment of Objects per frame: " + maxSearchradius,
						Label.CENTER);
				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelSeventh.add(MaxMovText, c);
				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelSeventh.add(Maxrad, c);

				final Scrollbar Miss = new Scrollbar(Scrollbar.HORIZONTAL, missedframesInit, 10, 0, 10 + scrollbarSize);
				Miss.setBlockIncrement(1);
				missedframes = (int) computeValueFromScrollbarPosition(missedframesInit, missedframesMin,
						missedframesMax, scrollbarSize);
				final Label LostText = new Label("Objects allowed to be lost for #frames" + missedframes, Label.CENTER);
				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelSeventh.add(LostText, c);
				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelSeventh.add(Miss, c);

				final Checkbox Costfunc = new Checkbox("Squared Distance Cost Function");
				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelSeventh.add(Costfunc, c);

				rad.addAdjustmentListener(
						new SearchradiusListener(SearchText, initialSearchradiusMin, initialSearchradiusMax));
				Maxrad.addAdjustmentListener(
						new maxSearchradiusListener(MaxMovText, maxSearchradiusMin, maxSearchradiusMax));
				Miss.addAdjustmentListener(new missedFrameListener(LostText, missedframesMin, missedframesMax));

				Costfunc.addItemListener(new CostfunctionListener());

				MTtrackerstart = new KFsearch(AllstartKalman, UserchosenCostFunction, maxSearchradius,
						initialSearchradius, thirdDimension, thirdDimensionSize, missedframes);

				MTtrackerend = new KFsearch(AllendKalman, UserchosenCostFunction, maxSearchradius, initialSearchradius,
						thirdDimension, thirdDimensionSize, missedframes);

				final Button TrackEndPoints = new Button("Track EndPoints (From first to a chosen last frame)");
				final Button SkipframeandTrackEndPoints = new Button(
						"TrackEndPoint (User specified first and last frame)");
				final Button CheckResults = new Button("Check Results (then click next)");

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 175);
				panelSeventh.add(TrackEndPoints, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 175);
				panelSeventh.add(SkipframeandTrackEndPoints, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 0);
				panelSeventh.add(Checkres, c);

				++c.gridy;
				c.insets = new Insets(10, 175, 0, 175);
				panelSeventh.add(CheckResults, c);

				TrackEndPoints.addActionListener(new TrackendsListener());
				SkipframeandTrackEndPoints.addActionListener(new SkipFramesandTrackendsListener());
				CheckResults.addActionListener(new CheckResultsListener());
				

			}
			panelSeventh.repaint();
			panelSeventh.validate();

		}

	}

	public void MakeRois() {

		RoiManager roimanager = RoiManager.getInstance();

		rorig = Kymoimp.getRoi();

		if (rorig == null) {
			IJ.showMessage("Roi required");
		}
		nbRois = roimanager.getCount();
		Roi[] RoisOrig = roimanager.getRoisAsArray();

		Overlay overlaysec = Kymoimp.getOverlay();

		if (overlaysec == null) {
			overlaysec = new Overlay();

			Kymoimp.setOverlay(overlaysec);

		}
		overlaysec.clear();
		Length = new ArrayList<float[]>();
		for (int i = 0; i < nbRois; ++i) {

			PolygonRoi l = (PolygonRoi) RoisOrig[i];

			int n = l.getNCoordinates();
			float[] xCord = l.getFloatPolygon().xpoints;
			int[] yCord = l.getYCoordinates();

			for (int index = 0; index < n - 1; index++) {

				float[] cords = { xCord[index], (int) yCord[index] };
				float[] nextcords = { xCord[index + 1], (int) yCord[index + 1] };

				float slope = (float) ((nextcords[1] - cords[1]) / (nextcords[0] - cords[0]));
				float intercept = nextcords[1] - slope * nextcords[0];

				Line newlineKymo = new Line(cords[0], cords[1], nextcords[0], nextcords[1]);
				overlaysec.setStrokeColor(Color.RED);

				overlaysec.add(newlineKymo);
				Kymoimp.setOverlay(overlaysec);
				float[] cordsLine = new float[n];

				for (int y = (int) cords[1]; y < nextcords[1]; ++y) {
					cordsLine[1] = y;
					cordsLine[0] = (y - intercept) / (slope);
					if (slope != 0)
						Length.add(new float[] { cordsLine[0], cordsLine[1] });

				}

			}

		}

		/********
		 * The part below removes the duplicate entries in the array dor the
		 * time co-ordinate
		 ********/

		int j = 0;

		for (int index = 0; index < Length.size() - 1; ++index) {

			j = index + 1;

			while (j < Length.size()) {

				if (Length.get(index)[1] == Length.get(j)[1]) {

					Length.remove(index);
				}

				else {
					++j;

				}

			}
		}
		try {
			FileWriter fw;
			File fichierKy = new File(usefolder + "//" + addToName + "KymoWill-start" + ".txt");
			fw = new FileWriter(fichierKy);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("\tFramenumber\tLength\n");
			for (int index = 0; index < Length.size(); ++index) {
				System.out.println(Length.get(index)[1] + " " + Length.get(index)[0]);
				bw.write("\t" + (Length.get(index)[1]) + "\t" + (Length.get(index)[0] + "\n"));
			}

			bw.close();
			fw.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Kymoimp.show();

	}

	protected class GetCords implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent arg0) {

			MakeRois();

		}

	}

	protected class DeterchoiceListener implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent arg0) {
			if (arg0.getStateChange() == ItemEvent.DESELECTED)
				showDeterministic = false;
			else if (arg0.getStateChange() == ItemEvent.SELECTED) {

				showDeterministic = true;
				final GridBagLayout layout = new GridBagLayout();
				final GridBagConstraints c = new GridBagConstraints();
				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridx = 0;
				c.gridy = 0;
				c.weightx = 1;
				
				RoiManager roimanager = RoiManager.getInstance();

				if (roimanager != null) {

					roimanager.close();
					roimanager = new RoiManager();

				}
				panelSeventh.removeAll();
				final Label Step7 = new Label("Step 7", Label.CENTER);
				panelSeventh.setLayout(layout);
				panelSeventh.add(Step7, c);
				final Button TrackEndPoints = new Button("Track EndPoints (From first to a chosen last frame)");
				final Button SkipframeandTrackEndPoints = new Button(
						"TrackEndPoint (User specified first and last frame)");
				final Button CheckResults = new Button("Check Results (then click next)");
				final Label Checkres = new Label("The tracker now performs an internal check on the results");
				Checkres.setBackground(new Color(1, 0, 1));
				Checkres.setForeground(new Color(255, 255, 255));
				++c.gridy;
				c.insets = new Insets(10, 10, 0, 175);
				panelSeventh.add(TrackEndPoints, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 175);
				panelSeventh.add(SkipframeandTrackEndPoints, c);
				if (analyzekymo && Kymoimg!=null){
				++c.gridy;
				c.insets = new Insets(10, 10, 0, 0);
				panelSeventh.add(Checkres, c);

				++c.gridy;
				c.insets = new Insets(10, 175, 0, 175);
				panelSeventh.add(CheckResults, c);
				}
				TrackEndPoints.addActionListener(new TrackendsListener());
				SkipframeandTrackEndPoints.addActionListener(new SkipFramesandTrackendsListener());
				CheckResults.addActionListener(new CheckResultsListener());
				panelSeventh.repaint();
				panelSeventh.validate();

			}

		}
	}

	protected class KalmanchoiceListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent arg0) {
			if (arg0.getStateChange() == ItemEvent.DESELECTED) {
				showKalman = false;

			} else if (arg0.getStateChange() == ItemEvent.SELECTED) {

				showKalman = true;
				final GridBagLayout layout = new GridBagLayout();
				final GridBagConstraints c = new GridBagConstraints();
				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridx = 0;
				c.gridy = 0;
				c.weightx = 1;
				
				RoiManager roimanager = RoiManager.getInstance();

				if (roimanager != null) {

					roimanager.close();
					roimanager = new RoiManager();

				}

				panelSeventh.removeAll();
				final Label Step7 = new Label("Step 7", Label.CENTER);
				panelSeventh.setLayout(layout);
				panelSeventh.add(Step7, c);
				final Scrollbar rad = new Scrollbar(Scrollbar.HORIZONTAL, initialSearchradiusInit, 10, 0,
						10 + scrollbarSize);
				initialSearchradius = computeValueFromScrollbarPosition(initialSearchradiusInit, initialSearchradiusMin,
						initialSearchradiusMax, scrollbarSize);

				final Label SearchText = new Label("Initial Search Radius: " + initialSearchradius, Label.CENTER);

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelSeventh.add(SearchText, c);
				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelSeventh.add(rad, c);

				final Scrollbar Maxrad = new Scrollbar(Scrollbar.HORIZONTAL, maxSearchradiusInit, 10, 0,
						10 + scrollbarSize);
				maxSearchradius = computeValueFromScrollbarPosition(maxSearchradiusInit, maxSearchradiusMin,
						maxSearchradiusMax, scrollbarSize);
				final Label MaxMovText = new Label("Max Movment of Objects per frame: " + maxSearchradius,
						Label.CENTER);
				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelSeventh.add(MaxMovText, c);
				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelSeventh.add(Maxrad, c);

				final Scrollbar Miss = new Scrollbar(Scrollbar.HORIZONTAL, missedframesInit, 10, 0, 10 + scrollbarSize);
				Miss.setBlockIncrement(1);
				missedframes = (int) computeValueFromScrollbarPosition(missedframesInit, missedframesMin,
						missedframesMax, scrollbarSize);
				final Label LostText = new Label("Objects allowed to be lost for #frames" + missedframes, Label.CENTER);
				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelSeventh.add(LostText, c);
				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelSeventh.add(Miss, c);

				final Checkbox Costfunc = new Checkbox("Squared Distance Cost Function");
				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelSeventh.add(Costfunc, c);

				rad.addAdjustmentListener(
						new SearchradiusListener(SearchText, initialSearchradiusMin, initialSearchradiusMax));
				Maxrad.addAdjustmentListener(
						new maxSearchradiusListener(MaxMovText, maxSearchradiusMin, maxSearchradiusMax));
				Miss.addAdjustmentListener(new missedFrameListener(LostText, missedframesMin, missedframesMax));

				Costfunc.addItemListener(new CostfunctionListener());

				MTtrackerstart = new KFsearch(AllstartKalman, UserchosenCostFunction, maxSearchradius,
						initialSearchradius, thirdDimension, thirdDimensionSize, missedframes);

				MTtrackerend = new KFsearch(AllendKalman, UserchosenCostFunction, maxSearchradius, initialSearchradius,
						thirdDimension, thirdDimensionSize, missedframes);

				final Button TrackEndPoints = new Button("Track EndPoints (From first to a chosen last frame)");
				final Button SkipframeandTrackEndPoints = new Button(
						"TrackEndPoint (User specified first and last frame)");
				final Button CheckResults = new Button("Check Results (then click next)");

				final Label Checkres = new Label("The tracker now performs an internal check on the results");
				Checkres.setBackground(new Color(1, 0, 1));
				Checkres.setForeground(new Color(255, 255, 255));
				++c.gridy;
				c.insets = new Insets(10, 10, 0, 175);
				panelSeventh.add(TrackEndPoints, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 175);
				panelSeventh.add(SkipframeandTrackEndPoints, c);
				if (analyzekymo && Kymoimg!=null){
				++c.gridy;
				c.insets = new Insets(10, 10, 0, 0);
				panelSeventh.add(Checkres, c);

				++c.gridy;
				c.insets = new Insets(10, 175, 0, 175);
				panelSeventh.add(CheckResults, c);
				}
				TrackEndPoints.addActionListener(new TrackendsListener());
				SkipframeandTrackEndPoints.addActionListener(new SkipFramesandTrackendsListener());
				CheckResults.addActionListener(new CheckResultsListener());
				panelSeventh.repaint();
				panelSeventh.validate();
				
			}

		}

	}

	protected class TrackerButtonListener implements ActionListener {
		final Frame parent;

		public TrackerButtonListener(Frame parent) {
			this.parent = parent;
		}

		public void actionPerformed(final ActionEvent arg0) {

			IJ.log("Making Tracker choice");

			DialogueTracker();

		}
	}

	protected class moveNextListener implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent arg0) {

			if (thirdDimension > thirdDimensionSize) {
				IJ.log("Max frame number exceeded, moving to last frame instead");
				thirdDimension = thirdDimensionSize;
				CurrentView = getCurrentView();
				CurrentPreprocessedView = getCurrentPreView();
			} else {

				thirdDimension = thirdDimension + 1;
				CurrentView = getCurrentView();
				CurrentPreprocessedView = getCurrentPreView();

			}

			updatePreview(ValueChange.THIRDDIM);

			// DialogueMethodChange();
			CheckboxGroup Finders = new CheckboxGroup();
			final Checkbox mser = new Checkbox("MSER", Finders, FindLinesViaMSER = false);
			final Checkbox hough = new Checkbox("HOUGH", Finders, FindLinesViaHOUGH = false);
			final Checkbox mserwhough = new Checkbox("MSERwHOUGH", Finders, FindLinesViaMSERwHOUGH = false);
			final GridBagLayout layout = new GridBagLayout();
			final GridBagConstraints c = new GridBagConstraints();

			panelFourth.removeAll();
			final Label Step4 = new Label("Step 4", Label.CENTER);
			panelFourth.setLayout(layout);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 4;
			c.weighty = 1.5;
			panelFourth.add(Step4, c);
			++c.gridy;
			final Label Msertxt = new Label("Update Mser params to find MT in dynamic channel");
			final Label Houghtxt = new Label("Update Hough params to find MT in dynamic channel");
			final Label MserwHtxt = new Label("Update MserwHough params to find MT in dynamic channel");

			++c.gridy;
			c.insets = new Insets(10, 175, 0, 175);
			panelFourth.add(Msertxt, c);

			++c.gridy;
			c.insets = new Insets(10, 175, 0, 175);
			panelFourth.add(mser, c);

			++c.gridy;
			c.insets = new Insets(10, 175, 0, 175);
			panelFourth.add(Houghtxt, c);

			++c.gridy;
			c.insets = new Insets(10, 175, 0, 175);
			panelFourth.add(hough, c);

			++c.gridy;
			c.insets = new Insets(10, 175, 0, 175);
			panelFourth.add(MserwHtxt, c);

			++c.gridy;
			c.insets = new Insets(10, 175, 0, 175);
			panelFourth.add(mserwhough, c);

			Msertxt.setBackground(new Color(1, 0, 1));
			Msertxt.setForeground(new Color(255, 255, 255));
			Houghtxt.setBackground(new Color(1, 0, 1));
			Houghtxt.setForeground(new Color(255, 255, 255));
			MserwHtxt.setBackground(new Color(1, 0, 1));
			MserwHtxt.setForeground(new Color(255, 255, 255));

			mser.addItemListener(new UpdateMserListener());
			hough.addItemListener(new UpdateHoughListener());
			mserwhough.addItemListener(new UpdateMserwHoughListener());
			panelFourth.validate();
			panelFourth.repaint();
		}
	}

	private boolean moveDialogue() {
		GenericDialog gd = new GenericDialog("Choose Frame");

		if (thirdDimensionSize > 1) {
			gd.addNumericField("Move to frame", thirdDimension, 0);

		}

		gd.showDialog();
		if (thirdDimensionSize > 1) {
			thirdDimension = (int) gd.getNextNumber();

		}
		return !gd.wasCanceled();
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

	protected static int computeIntScrollbarPositionFromValue(final float thirdDimensionslider, final float min,
			final float max, final int scrollbarSize) {
		return Util.round(((thirdDimensionslider - min) / (max - min)) * max);
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
					CurrentView = getCurrentView();
					CurrentPreprocessedView = getCurrentPreView();

				} else {

					CurrentView = getCurrentView();
					CurrentPreprocessedView = getCurrentPreView();

				}

				// compute first version
				updatePreview(ValueChange.THIRDDIM);

			}
		}
	}

	protected class chooseendListener implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent arg0) {

			preprocessedimp.getCanvas().addMouseListener(ml = new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
					int x = e.getX();
					int y = e.getY();
					System.out.println("You chose: " + x + "," + y);// these
																	// co-ords
																	// are
																	// relative
																	// to the
																	// component
					ClickedPoints.add(new int[] { x, y });
					
					
					Overlay overlaysec = preprocessedimp.getOverlay();

					if (overlaysec == null) {
						overlaysec = new Overlay();

						preprocessedimp.setOverlay(overlaysec);

					}
					
				
					
					final OvalRoi Bigroi = new OvalRoi(Util.round(x - 5), Util.round(y - 5), Util.round(10),
							Util.round(10));
					overlaysec.add(Bigroi);

				}

				@Override
				public void mousePressed(MouseEvent e) {

				}

				@Override
				public void mouseReleased(MouseEvent e) {

				}

				@Override
				public void mouseEntered(MouseEvent e) {

				}

				@Override
				public void mouseExited(MouseEvent e) {

				}
			});

		}

	}

	protected class moveToFrameListener implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent arg0) {

			moveDialogue();

			if (thirdDimension > thirdDimensionSize) {
				IJ.log("Max frame number exceeded, moving to last frame instead");
				thirdDimension = thirdDimensionSize;
				CurrentView = getCurrentView();
				CurrentPreprocessedView = getCurrentPreView();
			} else {

				CurrentView = getCurrentView();
				CurrentPreprocessedView = getCurrentPreView();
			}

			updatePreview(ValueChange.THIRDDIM);

			// DialogueMethodChange();

			CheckboxGroup Finders = new CheckboxGroup();
			final Checkbox mser = new Checkbox("MSER", Finders, FindLinesViaMSER);
			final Checkbox hough = new Checkbox("HOUGH", Finders, FindLinesViaHOUGH);
			final Checkbox mserwhough = new Checkbox("MSERwHOUGH", Finders, FindLinesViaMSERwHOUGH);
			final GridBagLayout layout = new GridBagLayout();
			final GridBagConstraints c = new GridBagConstraints();
			panelFourth.removeAll();
			final Label Step4 = new Label("Step 4", Label.CENTER);
			panelFourth.setLayout(layout);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 4;
			c.weighty = 1.5;
			panelFourth.add(Step4, c);
			final Label Msertxt = new Label("Update Mser params to find MT in dynamic channel");
			final Label Houghtxt = new Label("Update Hough params to find MT in dynamic channel");
			final Label MserwHtxt = new Label("Update MserwHough params to find MT in dynamic channel");

			++c.gridy;
			c.insets = new Insets(10, 175, 0, 175);
			panelFourth.add(Msertxt, c);

			++c.gridy;
			c.insets = new Insets(10, 175, 0, 175);
			panelFourth.add(mser, c);

			++c.gridy;
			c.insets = new Insets(10, 175, 0, 175);
			panelFourth.add(Houghtxt, c);

			++c.gridy;
			c.insets = new Insets(10, 175, 0, 175);
			panelFourth.add(hough, c);

			++c.gridy;
			c.insets = new Insets(10, 175, 0, 175);
			panelFourth.add(MserwHtxt, c);

			++c.gridy;
			c.insets = new Insets(10, 175, 0, 175);
			panelFourth.add(mserwhough, c);

			Msertxt.setBackground(new Color(1, 0, 1));
			Msertxt.setForeground(new Color(255, 255, 255));
			Houghtxt.setBackground(new Color(1, 0, 1));
			Houghtxt.setForeground(new Color(255, 255, 255));
			MserwHtxt.setBackground(new Color(1, 0, 1));
			MserwHtxt.setForeground(new Color(255, 255, 255));

			mser.addItemListener(new UpdateMserListener());
			hough.addItemListener(new UpdateHoughListener());
			mserwhough.addItemListener(new UpdateMserwHoughListener());
			panelFourth.validate();
			panelFourth.repaint();
		}

	}

	
	public void goSeeds() {

		jpb.setIndeterminate(false);

		jpb.setMaximum(max);
		panel.add(label);
		panel.add(jpb);
		frame.add(panel);
		frame.pack();
		frame.setSize(200, 100);
		frame.setLocationRelativeTo(panelCont);
		frame.setVisible(true);

		ProgressSeeds trackMT = new ProgressSeeds();
		trackMT.execute();

	}
	
	
	class ProgressSeeds extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() throws Exception {
			

			// add listener to the imageplus slice slider
			IJ.log("Starting Chosen Line finder from the seed image (first frame should be seeds)");

			IJ.log("Current frame: " + thirdDimension);

			RandomAccessibleInterval<FloatType> groundframe = currentimg;
			RandomAccessibleInterval<FloatType> groundframepre = currentPreprocessedimg;
			if (FindLinesViaMSER) {
				boolean dialog = DialogueModelChoice();
				if (dialog) {
				//	updatePreview(ValueChange.SHOWMSER);
					LinefinderInteractiveMSER newlineMser = new LinefinderInteractiveMSER(groundframe, groundframepre,
							newtree, minlength, thirdDimension);

					PrevFrameparam = FindlinesVia.LinefindingMethod(groundframe, groundframepre, minlength,
							thirdDimension, psf, newlineMser, UserChoiceModel.Line, Domask, Intensityratio, Inispacing, jpb);

					ArrayList<KalmanIndexedlength> start = new ArrayList<KalmanIndexedlength>();
					ArrayList<KalmanIndexedlength> end = new ArrayList<KalmanIndexedlength>();

					for (int index = 0; index < PrevFrameparam.getA().size(); ++index) {

						double dx = PrevFrameparam.getA().get(index).ds / Math
								.sqrt(1 + PrevFrameparam.getA().get(index).slope * PrevFrameparam.getA().get(index).slope);
						double dy = PrevFrameparam.getA().get(index).slope * dx;

						KalmanIndexedlength startPart = new KalmanIndexedlength(
								PrevFrameparam.getA().get(index).currentLabel, PrevFrameparam.getA().get(index).seedLabel,
								PrevFrameparam.getA().get(index).framenumber, PrevFrameparam.getA().get(index).ds,
								PrevFrameparam.getA().get(index).lineintensity, PrevFrameparam.getA().get(index).background,
								PrevFrameparam.getA().get(index).currentpos, PrevFrameparam.getA().get(index).fixedpos,
								PrevFrameparam.getA().get(index).slope, PrevFrameparam.getA().get(index).intercept,
								PrevFrameparam.getA().get(index).slope, PrevFrameparam.getA().get(index).intercept, 0, 0,
								new double[] { dx, dy });

						start.add(startPart);
					}
					for (int index = 0; index < PrevFrameparam.getB().size(); ++index) {

						double dx = PrevFrameparam.getB().get(index).ds / Math
								.sqrt(1 + PrevFrameparam.getB().get(index).slope * PrevFrameparam.getB().get(index).slope);
						double dy = PrevFrameparam.getB().get(index).slope * dx;

						KalmanIndexedlength endPart = new KalmanIndexedlength(
								PrevFrameparam.getB().get(index).currentLabel, PrevFrameparam.getB().get(index).seedLabel,
								PrevFrameparam.getB().get(index).framenumber, PrevFrameparam.getB().get(index).ds,
								PrevFrameparam.getB().get(index).lineintensity, PrevFrameparam.getB().get(index).background,
								PrevFrameparam.getB().get(index).currentpos, PrevFrameparam.getB().get(index).fixedpos,
								PrevFrameparam.getB().get(index).slope, PrevFrameparam.getB().get(index).intercept,
								PrevFrameparam.getB().get(index).slope, PrevFrameparam.getB().get(index).intercept, 0, 0,
								new double[] { dx, dy });
						end.add(endPart);
					}

					PrevFrameparamKalman = new ValuePair<ArrayList<KalmanIndexedlength>, ArrayList<KalmanIndexedlength>>(
							start, end);

				}

			}

			if (FindLinesViaHOUGH) {

				boolean dialog = DialogueModelChoice();
				if (dialog) {
				//	updatePreview(ValueChange.SHOWHOUGH);
					LinefinderInteractiveHough newlineHough = new LinefinderInteractiveHough(groundframe,
							groundframepre, intimg, Maxlabel, thetaPerPixel, rhoPerPixel, thirdDimension);

					PrevFrameparam = FindlinesVia.LinefindingMethod(groundframe, groundframepre, minlength,
							thirdDimension, psf, newlineHough, UserChoiceModel.Line, Domask, Intensityratio,
							Inispacing, jpb);

					ArrayList<KalmanIndexedlength> start = new ArrayList<KalmanIndexedlength>();
					ArrayList<KalmanIndexedlength> end = new ArrayList<KalmanIndexedlength>();

					for (int index = 0; index < PrevFrameparam.getA().size(); ++index) {

						double dx = PrevFrameparam.getA().get(index).ds / Math
								.sqrt(1 + PrevFrameparam.getA().get(index).slope * PrevFrameparam.getA().get(index).slope);
						double dy = PrevFrameparam.getA().get(index).slope * dx;

						KalmanIndexedlength startPart = new KalmanIndexedlength(
								PrevFrameparam.getA().get(index).currentLabel, PrevFrameparam.getA().get(index).seedLabel,
								PrevFrameparam.getA().get(index).framenumber, PrevFrameparam.getA().get(index).ds,
								PrevFrameparam.getA().get(index).lineintensity, PrevFrameparam.getA().get(index).background,
								PrevFrameparam.getA().get(index).currentpos, PrevFrameparam.getA().get(index).fixedpos,
								PrevFrameparam.getA().get(index).slope, PrevFrameparam.getA().get(index).intercept,
								PrevFrameparam.getA().get(index).slope, PrevFrameparam.getA().get(index).intercept, 0, 0,
								new double[] { dx, dy });

						start.add(startPart);
					}
					for (int index = 0; index < PrevFrameparam.getB().size(); ++index) {

						double dx = PrevFrameparam.getB().get(index).ds / Math
								.sqrt(1 + PrevFrameparam.getB().get(index).slope * PrevFrameparam.getB().get(index).slope);
						double dy = PrevFrameparam.getB().get(index).slope * dx;

						KalmanIndexedlength endPart = new KalmanIndexedlength(
								PrevFrameparam.getB().get(index).currentLabel, PrevFrameparam.getB().get(index).seedLabel,
								PrevFrameparam.getB().get(index).framenumber, PrevFrameparam.getB().get(index).ds,
								PrevFrameparam.getB().get(index).lineintensity, PrevFrameparam.getB().get(index).background,
								PrevFrameparam.getB().get(index).currentpos, PrevFrameparam.getB().get(index).fixedpos,
								PrevFrameparam.getB().get(index).slope, PrevFrameparam.getB().get(index).intercept,
								PrevFrameparam.getB().get(index).slope, PrevFrameparam.getB().get(index).intercept, 0, 0,
								new double[] { dx, dy });
						end.add(endPart);
					}

					PrevFrameparamKalman = new ValuePair<ArrayList<KalmanIndexedlength>, ArrayList<KalmanIndexedlength>>(
							start, end);

				}
			}

			if (FindLinesViaMSERwHOUGH) {
				boolean dialog = DialogueModelChoice();
				if (dialog) {
				//	updatePreview(ValueChange.SHOWMSER);
					LinefinderInteractiveMSERwHough newlineMserwHough = new LinefinderInteractiveMSERwHough(groundframe,
							groundframepre, newtree, minlength, thirdDimension, thetaPerPixel, rhoPerPixel);

					PrevFrameparam = FindlinesVia.LinefindingMethod(groundframe, groundframepre, minlength,
							thirdDimension, psf, newlineMserwHough, UserChoiceModel.Line, Domask, Intensityratio,
							Inispacing, jpb);

					ArrayList<KalmanIndexedlength> start = new ArrayList<KalmanIndexedlength>();
					ArrayList<KalmanIndexedlength> end = new ArrayList<KalmanIndexedlength>();

					for (int index = 0; index < PrevFrameparam.getA().size(); ++index) {

						double dx = PrevFrameparam.getA().get(index).ds / Math
								.sqrt(1 + PrevFrameparam.getA().get(index).slope * PrevFrameparam.getA().get(index).slope);
						double dy = PrevFrameparam.getA().get(index).slope * dx;

						KalmanIndexedlength startPart = new KalmanIndexedlength(
								PrevFrameparam.getA().get(index).currentLabel, PrevFrameparam.getA().get(index).seedLabel,
								PrevFrameparam.getA().get(index).framenumber, PrevFrameparam.getA().get(index).ds,
								PrevFrameparam.getA().get(index).lineintensity, PrevFrameparam.getA().get(index).background,
								PrevFrameparam.getA().get(index).currentpos, PrevFrameparam.getA().get(index).fixedpos,
								PrevFrameparam.getA().get(index).slope, PrevFrameparam.getA().get(index).intercept,
								PrevFrameparam.getA().get(index).slope, PrevFrameparam.getA().get(index).intercept, 0, 0,
								new double[] { dx, dy });

						start.add(startPart);
					}
					for (int index = 0; index < PrevFrameparam.getB().size(); ++index) {

						double dx = PrevFrameparam.getB().get(index).ds / Math
								.sqrt(1 + PrevFrameparam.getB().get(index).slope * PrevFrameparam.getB().get(index).slope);
						double dy = PrevFrameparam.getB().get(index).slope * dx;

						KalmanIndexedlength endPart = new KalmanIndexedlength(
								PrevFrameparam.getB().get(index).currentLabel, PrevFrameparam.getB().get(index).seedLabel,
								PrevFrameparam.getB().get(index).framenumber, PrevFrameparam.getB().get(index).ds,
								PrevFrameparam.getB().get(index).lineintensity, PrevFrameparam.getB().get(index).background,
								PrevFrameparam.getB().get(index).currentpos, PrevFrameparam.getB().get(index).fixedpos,
								PrevFrameparam.getB().get(index).slope, PrevFrameparam.getB().get(index).intercept,
								PrevFrameparam.getB().get(index).slope, PrevFrameparam.getB().get(index).intercept, 0, 0,
								new double[] { dx, dy });
						end.add(endPart);
					}

					PrevFrameparamKalman = new ValuePair<ArrayList<KalmanIndexedlength>, ArrayList<KalmanIndexedlength>>(
							start, end);

				}

			}
			
			

			return null;
		}
		
		@Override
		protected void done() {
			try {
				jpb.setIndeterminate(false);
				get();
				frame.dispose();
				JOptionPane.showMessageDialog(jpb.getParent(), "Success", "Success", JOptionPane.INFORMATION_MESSAGE);
			} catch (ExecutionException | InterruptedException e) {
				e.printStackTrace();
			}

		}
		
	}
	
	protected class FindLinesListener implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent arg0) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {

					goSeeds();

				}

			});

		}
	}

	public RandomAccessibleInterval<FloatType> getCurrentView() {

		final FloatType type = originalimg.randomAccess().get().createVariable();
		long[] dim = { originalimg.dimension(0), originalimg.dimension(1) };
		final ImgFactory<FloatType> factory = net.imglib2.util.Util.getArrayOrCellImgFactory(originalimg, type);
		RandomAccessibleInterval<FloatType> totalimg = factory.create(dim, type);

		if (thirdDimensionSize == 0) {

			totalimg = originalimg;
		}

		if (thirdDimensionSize > 0) {

			totalimg = Views.hyperSlice(originalimg, 2, thirdDimension - 1);

		}

		return totalimg;

	}

	public RandomAccessibleInterval<FloatType> getCurrentPreView() {

		final FloatType type = originalPreprocessedimg.randomAccess().get().createVariable();
		long[] dim = { originalPreprocessedimg.dimension(0), originalPreprocessedimg.dimension(1) };
		final ImgFactory<FloatType> factory = net.imglib2.util.Util.getArrayOrCellImgFactory(originalPreprocessedimg,
				type);
		RandomAccessibleInterval<FloatType> totalimg = factory.create(dim, type);

		if (thirdDimensionSize == 0) {

			totalimg = originalPreprocessedimg;
		}

		if (thirdDimensionSize > 0) {

			totalimg = Views.hyperSlice(originalPreprocessedimg, 2, thirdDimension - 1);

		}

		return totalimg;

	}

	private boolean DialogueTracker() {

		String[] colors = { "Red", "Green", "Blue", "Cyan", "Magenta", "Yellow", "Black", "White" };
		String[] whichtracker = { "Deterministic (For Non Contact MT)", "Kalman Tracker (MT Spaghetti)" };
		String[] whichcost = { "Distance based" };
		int indexcol = 0;
		int trackertype = 0;
		int functiontype = 0;

		// Create dialog
		GenericDialog gd = new GenericDialog("Tracker");

		gd.addChoice("Choose your tracker :", whichtracker, whichtracker[trackertype]);
		gd.addChoice("Choose your Cost function (for Kalman) :", whichcost, whichcost[functiontype]);
		gd.addChoice("Draw tracks with this color :", colors, colors[indexcol]);

		gd.addNumericField("Initial Search Radius", initialSearchradius, 0);
		gd.addNumericField("Max Movment of Ends per frame", maxSearchradius, 0);
		gd.addNumericField("Ends allowed to be lost for #frames", missedframes, 0);

		initialSearchradius = (int) gd.getNextNumber();
		maxSearchradius = (int) gd.getNextNumber();
		missedframes = (int) gd.getNextNumber();
		// Choice of tracker
		trackertype = gd.getNextChoiceIndex();
		if (trackertype == 0) {
			showKalman = true;
			showDeterministic = false;

			functiontype = gd.getNextChoiceIndex();
			switch (functiontype) {

			case 0:
				UserchosenCostFunction = new SquareDistCostFunction();
				break;

			}

			MTtrackerstart = new KFsearch(AllstartKalman, UserchosenCostFunction, maxSearchradius, initialSearchradius,
					thirdDimension, thirdDimensionSize, missedframes);

			MTtrackerend = new KFsearch(AllendKalman, UserchosenCostFunction, maxSearchradius, initialSearchradius,
					thirdDimension, thirdDimensionSize, missedframes);

		}

		if (trackertype == 1) {

			showKalman = false;
			showDeterministic = true;
		}

		switch (indexcol) {
		case 0:
			colorDraw = Color.red;
			break;
		case 1:
			colorDraw = Color.green;
			break;
		case 2:
			colorDraw = Color.blue;
			break;
		case 3:
			colorDraw = Color.cyan;
			break;
		case 4:
			colorDraw = Color.magenta;
			break;
		case 5:
			colorDraw = Color.yellow;
			break;
		case 6:
			colorDraw = Color.black;
			break;
		case 7:
			colorDraw = Color.white;
			break;
		default:
			colorDraw = Color.yellow;
		}

		gd.showDialog();
		// color choice of display
		indexcol = gd.getNextChoiceIndex();
		return !gd.wasCanceled();
	}

	protected class CostfunctionListener implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent arg0) {

			if (arg0.getStateChange() == ItemEvent.SELECTED) {

				UserchosenCostFunction = new SquareDistCostFunction();

			}

		}
	}

	protected class CannyListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent arg0) {

			if (arg0.getStateChange() == ItemEvent.DESELECTED)
				Canny = false;
			else if (arg0.getStateChange() == ItemEvent.SELECTED) {
				Canny = true;

			}
		}

	}

	protected class SaveasTXT implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent arg0) {
			if (arg0.getStateChange() == ItemEvent.DESELECTED)
				SaveTxt = false;

			else if (arg0.getStateChange() == ItemEvent.SELECTED)
				SaveTxt = true;

		}

	}

	protected class ShowKalman implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent arg0) {
			if (arg0.getStateChange() == ItemEvent.DESELECTED)
				showKalman = false;

			else if (arg0.getStateChange() == ItemEvent.SELECTED)
				showKalman = true;

		}

	}

	protected class AnalyzeListener implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent arg0) {

			if (analyzekymo)
				numberKymo = true;

			ArrayList<float[]> deltaL = new ArrayList<>();
			if (numberKymo) {

				for (int index = 1; index < Length.size(); ++index) {

					float delta = Length.get(index)[0] - Length.get(index - 1)[0];

					float[] deltalt = { delta, Length.get(index)[1] };

					deltaL.add(deltalt);

				}

				double velocity = 0;
				for (int index = 0; index < deltaL.size(); ++index) {

					if (deltaL.get(index)[1] >= starttime && deltaL.get(index)[1] <= endtime) {
						velocity += deltaL.get(index)[0];

					}
				}

				velocity /= endtime - starttime;

				float[] rates = { starttime, endtime, (float) velocity,
						(float) (velocity * calibration[0] / frametosec) };
				finalvelocityKymo.add(rates);

				FileWriter vw;
				File fichierKyvel = new File(usefolder + "//" + addToName + "KymoWill-velocity" + ".txt");
				try {
					vw = new FileWriter(fichierKyvel);
					BufferedWriter bvw = new BufferedWriter(vw);

					for (int i = 0; i < finalvelocityKymo.size(); ++i) {
						System.out.println("KymoResult: " + "\t" + finalvelocityKymo.get(i)[0] + "\t"
								+ finalvelocityKymo.get(i)[1] + "\t" + finalvelocityKymo.get(i)[2] + "\t "
								+ finalvelocityKymo.get(i)[3] + "\n");
						IJ.log("KymoResult: " + "\t" + finalvelocityKymo.get(i)[0] + "\t" + finalvelocityKymo.get(i)[1]
								+ "\t" + finalvelocityKymo.get(i)[2] + "\t " + finalvelocityKymo.get(i)[3] + "\n");
						bvw.write("\tStarttime\tEndtime\tRate(velocity pixel units)\tRate(velocity real units)\n");
						bvw.write("\t" + finalvelocityKymo.get(i)[0] + "\t" + finalvelocityKymo.get(i)[1] + "\t"
								+ finalvelocityKymo.get(i)[2] + "\t " + finalvelocityKymo.get(i)[3] + "\n");

					}
					bvw.close();
					vw.close();
				}

				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			ArrayList<float[]> deltadeltaL = new ArrayList<>();
			ArrayList<float[]> deltaLMT = new ArrayList<>();
			for (int index = 1; index < lengthtime.size(); ++index) {

				float delta = (float) (lengthtime.get(index)[0] - lengthtime.get(index - 1)[0]);

				float[] deltalt = { delta, (int) lengthtime.get(index)[1] };

				deltaLMT.add(deltalt);

			}

			if (numberKymo) {
				for (int index = 0; index < deltaLMT.size(); ++index) {

					int time = (int) deltaLMT.get(index)[1];

					for (int secindex = 0; secindex < deltaL.size(); ++secindex) {

						if ((int) deltaL.get(secindex)[1] == time) {

							float delta = deltaLMT.get(index)[0] - deltaL.get(secindex)[0];
							float[] cudeltadeltaLstart = { delta, time };
							deltadeltaL.add(cudeltadeltaLstart);

						}

					}

				}

			}

			// Choosing the faster end

			double velocity = 0;
			for (int index = 0; index < deltaLMT.size(); ++index) {

				if (deltaLMT.get(index)[1] >= starttime && deltaLMT.get(index)[1] <= endtime) {
					velocity += deltaLMT.get(index)[0];

				}
			}

			velocity /= endtime - starttime;

			if (numberKymo) {

				FileWriter deltal;
				File fichierKylel = new File(usefolder + "//" + addToName + "MTtracker-deltadeltal" + ".txt");

				try {
					deltal = new FileWriter(fichierKylel);
					BufferedWriter bdeltal = new BufferedWriter(deltal);

					bdeltal.write("\ttime\tDeltaDeltal(pixel units)\n");

					for (int index = 0; index < deltadeltaL.size(); ++index) {
						bdeltal.write("\t" + deltadeltaL.get(index)[1] + "\t" + deltadeltaL.get(index)[0] + "\n");

					}

					bdeltal.close();
					deltal.close();
				}

				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			float[] rates = { starttime, endtime, (float) velocity, (float) (velocity * calibration[0] / frametosec) };
			finalvelocity.add(rates);

			FileWriter vw;
			File fichierKyvel = new File(usefolder + "//" + addToName + "MTtracker-velocity" + ".txt");
			try {
				vw = new FileWriter(fichierKyvel);
				BufferedWriter bvw = new BufferedWriter(vw);

				for (int i = 0; i < finalvelocity.size(); ++i) {

					System.out.println("MT tracker: " + "\t" + finalvelocity.get(i)[0] + "\t" + finalvelocity.get(i)[1]
							+ "\t" + finalvelocity.get(i)[2] + "\t" + finalvelocity.get(i)[3] + "\n");

					IJ.log("MT tracker: " + "\t" + finalvelocity.get(i)[0] + "\t" + finalvelocity.get(i)[1] + "\t"
							+ finalvelocity.get(i)[2] + "\t" + finalvelocity.get(i)[3] + "\n");
					bvw.write("\tStarttime\tEndtime\tRate(velocity pixel units)\tRate (velocity in real units)\n");
					bvw.write("\t" + finalvelocity.get(i)[0] + "\t" + finalvelocity.get(i)[1] + "\t"
							+ finalvelocity.get(i)[2] + "\t" + finalvelocity.get(i)[3] + "\n");

				}
				bvw.close();
				vw.close();
			}

			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public boolean DialogueAnalysis() {

		GenericDialog gd = new GenericDialog("Analyze");

		gd.addNumericField("Start time of event", starttime, 0);
		gd.addNumericField("Enfd time of event", endtime, 0);

		gd.addCheckbox("Compute Velocity from Kymograph", numberKymo);

		gd.addCheckbox("Compute Velocity by Tracker", numberTracker);

		gd.showDialog();

		starttime = (int) gd.getNextNumber();
		endtime = (int) gd.getNextNumber();
		numberKymo = gd.getNextBoolean();
		numberTracker = gd.getNextBoolean();

		if (starttime < 0)
			starttime = 0;
		if (endtime > thirdDimensionSize)
			endtime = thirdDimensionSize;

		return !gd.wasCanceled();

	}

	protected class SaveasXLS implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent arg0) {
			if (arg0.getStateChange() == ItemEvent.DESELECTED)
				SaveXLS = false;

			else if (arg0.getStateChange() == ItemEvent.SELECTED)
				SaveXLS = true;

		}

	}

	protected class finalpoint implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent arg0) {
			if (arg0.getStateChange() == ItemEvent.DESELECTED)
				finalpoint = false;

			else if (arg0.getStateChange() == ItemEvent.SELECTED) {
				finalpoint = true;

				preprocessedimp.getCanvas().removeMouseListener(ml);

				ArrayList<double[]> filepair = new ArrayList<double[]>();
				for (int index = 0; index < ClickedPoints.size(); ++index) {
					double minDist = Double.MAX_VALUE;
					double[] closestpoint = { Double.MAX_VALUE, Double.MAX_VALUE };
					int fileindex = Double.MAX_EXPONENT;
					for (int secindex = 0; secindex < PrevFrameparam.getA().size(); ++secindex) {
						double currDist = util.Boundingboxes.Distance(
								new double[] { ClickedPoints.get(index)[0], ClickedPoints.get(index)[1] },
								PrevFrameparam.getA().get(secindex).currentpos);

						if (currDist < minDist) {

							minDist = currDist;
							fileindex = secindex;
							closestpoint = PrevFrameparam.getA().get(secindex).currentpos;

						}

					}

					filepair.add(new double[] { fileindex, minDist, closestpoint[0], closestpoint[1] });

				}

				for (int index = 0; index < filepair.size(); ++index) {

					int Frameindex = (int) filepair.get(index)[0];
					double[] investigatepoint = new double[] { filepair.get(index)[2], filepair.get(index)[3] };
					double investigatedist = filepair.get(index)[1];

					double currDist = util.Boundingboxes.Distance(investigatepoint,
							PrevFrameparam.getB().get(Frameindex).currentpos);

					Trackstart = (currDist > investigatedist) ? true : false;

					whichend.put(Frameindex, Trackstart);

				}

			}

		}

	}

	protected class MedianAllListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent arg0) {

			if (arg0.getStateChange() == ItemEvent.DESELECTED)
				MedianAll = false;
			else if (arg0.getStateChange() == ItemEvent.SELECTED) {

				MedianAll = true;

				DialogueMedian();

				IJ.log(" Applying Median Filter to the whole stack (takes some time)");

				final MedianFilter2D<FloatType> medfilter = new MedianFilter2D<FloatType>(originalPreprocessedimg,
						radius);
				medfilter.process();
				IJ.log(" Median filter sucessfully applied to the whole stack");
				originalPreprocessedimg = medfilter.getResult();
				CurrentPreprocessedView = getCurrentPreView();
				currentPreprocessedimg = extractImage(CurrentPreprocessedView);
				updatePreview(ValueChange.MEDIAN);

			}
		}

	}

	public ImagePlus getImp() {
		return this.imp;
	}

	protected class AcceptResultsListener implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent arg0) {
			redoAccept = false;

			final GridBagLayout layout = new GridBagLayout();
			final GridBagConstraints c = new GridBagConstraints();

			panelEighth.removeAll();
			final Label Step8 = new Label("Step 8", Label.CENTER);
			panelEighth.setLayout(layout);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 4;
			c.weighty = 1.5;

			panelEighth.add(Step8, c);
			final Button Analyze = new Button("Do Rough Analysis");

			final Scrollbar startS = new Scrollbar(Scrollbar.HORIZONTAL, thirdDimensionsliderInit, 10, 0,
					10 + scrollbarSize);
			final Scrollbar endS = new Scrollbar(Scrollbar.HORIZONTAL, thirdDimensionsliderInit, 10, 0,
					10 + scrollbarSize);
			starttime = (int) computeValueFromScrollbarPosition(thirdDimensionsliderInit, 0, thirdDimensionSize,
					scrollbarSize);
			endtime = (int) computeValueFromScrollbarPosition(thirdDimensionsliderInit, 0, thirdDimensionSize,
					scrollbarSize);
			final Label startText = new Label("startFrame = ", Label.CENTER);
			final Label endText = new Label("endFrame = ", Label.CENTER);
			final Label Done = new Label("The results have been compiled and stored, you can now exit", Label.CENTER);
			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			panelEighth.add(startText, c);

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			panelEighth.add(startS, c);

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			panelEighth.add(endText, c);

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			panelEighth.add(endS, c);

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			panelEighth.add(Analyze, c);
			++c.gridy;
			c.insets = new Insets(10, 10, 0, 200);
			panelEighth.add(Done, c);
			startS.addAdjustmentListener(new starttimeListener(startText, thirdDimensionsliderInit, thirdDimensionSize,
					scrollbarSize, startS));
			endS.addAdjustmentListener(
					new endtimeListener(endText, thirdDimensionsliderInit, thirdDimensionSize, scrollbarSize, endS));
			Analyze.addActionListener(new AnalyzeListener());

			panelEighth.validate();
			panelEighth.repaint();

		}

	}

	protected class RefuseResultsListener implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent arg0) {
			redoAccept = true;

			Allstart.clear();
			Allend.clear();
			AllstartKalman.clear();
			AllendKalman.clear();
			lengthtimestart.clear();
			lengthtimeend.clear();
			deltad.clear();
			deltadstart.clear();
			deltadend.clear();
			Accountedframes.clear();
			final GridBagLayout layout = new GridBagLayout();
			final GridBagConstraints c = new GridBagConstraints();
			panelEighth.removeAll();
			final Label Step8 = new Label("Step 8", Label.CENTER);
			panelEighth.setLayout(layout);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 4;
			c.weighty = 1.5;

			panelEighth.add(Step8, c);
			if (showDeterministic) {
				final Button TrackEndPoints = new Button("Track EndPoints (From first to a chosen last frame)");
				final Button SkipframeandTrackEndPoints = new Button(
						"TrackEndPoint (User specified first and last frame)");
				final Button CheckResults = new Button("Check Results (then click next)");

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 175);
				panelEighth.add(TrackEndPoints, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 175);
				panelEighth.add(SkipframeandTrackEndPoints, c);

				++c.gridy;
				c.insets = new Insets(10, 175, 0, 175);
				panelEighth.add(CheckResults, c);

				TrackEndPoints.addActionListener(new TrackendsListener());
				SkipframeandTrackEndPoints.addActionListener(new SkipFramesandTrackendsListener());
				CheckResults.addActionListener(new CheckResultsListener());

				panelEighth.validate();
				panelEighth.repaint();

			}

			if (showKalman) {

				final Scrollbar rad = new Scrollbar(Scrollbar.HORIZONTAL, initialSearchradiusInit, 10, 0,
						10 + scrollbarSize);
				initialSearchradius = computeValueFromScrollbarPosition(initialSearchradiusInit, initialSearchradiusMin,
						initialSearchradiusMax, scrollbarSize);

				final Label SearchText = new Label("Initial Search Radius: " + initialSearchradius, Label.CENTER);

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelEighth.add(SearchText, c);
				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelEighth.add(rad, c);

				final Scrollbar Maxrad = new Scrollbar(Scrollbar.HORIZONTAL, maxSearchradiusInit, 10, 0,
						10 + scrollbarSize);
				maxSearchradius = computeValueFromScrollbarPosition(maxSearchradiusInit, maxSearchradiusMin,
						maxSearchradiusMax, scrollbarSize);
				final Label MaxMovText = new Label("Max Movment of Objects per frame: " + maxSearchradius,
						Label.CENTER);
				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelEighth.add(MaxMovText, c);
				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelEighth.add(Maxrad, c);

				final Scrollbar Miss = new Scrollbar(Scrollbar.HORIZONTAL, missedframesInit, 10, 0, 10 + scrollbarSize);
				Miss.setBlockIncrement(1);
				missedframes = (int) computeValueFromScrollbarPosition(missedframesInit, missedframesMin,
						missedframesMax, scrollbarSize);
				final Label LostText = new Label("Objects allowed to be lost for #frames" + missedframes, Label.CENTER);
				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelEighth.add(LostText, c);
				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelEighth.add(Miss, c);

				final Checkbox Costfunc = new Checkbox("Squared Distance Cost Function");
				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelEighth.add(Costfunc, c);

				rad.addAdjustmentListener(
						new SearchradiusListener(SearchText, initialSearchradiusMin, initialSearchradiusMax));
				Maxrad.addAdjustmentListener(
						new maxSearchradiusListener(MaxMovText, maxSearchradiusMin, maxSearchradiusMax));
				Miss.addAdjustmentListener(new missedFrameListener(LostText, missedframesMin, missedframesMax));

				Costfunc.addItemListener(new CostfunctionListener());

				MTtrackerstart = new KFsearch(AllstartKalman, UserchosenCostFunction, maxSearchradius,
						initialSearchradius, thirdDimension, thirdDimensionSize, missedframes);

				MTtrackerend = new KFsearch(AllendKalman, UserchosenCostFunction, maxSearchradius, initialSearchradius,
						thirdDimension, thirdDimensionSize, missedframes);

				final Button TrackEndPoints = new Button("Track EndPoints (From first to a chosen last frame)");
				final Button SkipframeandTrackEndPoints = new Button(
						"TrackEndPoint (User specified first and last frame)");
				final Button CheckResults = new Button("Check Results (then click next)");
				final Button AcceptResults = new Button("Accept Results");
				final Button RefuseResults = new Button("Refuse Results");
				++c.gridy;
				c.insets = new Insets(10, 10, 0, 175);
				panelEighth.add(TrackEndPoints, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 175);
				panelEighth.add(SkipframeandTrackEndPoints, c);
				++c.gridy;
				c.insets = new Insets(10, 175, 0, 175);
				panelEighth.add(CheckResults, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 200);
				panelEighth.add(AcceptResults, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 200);
				panelEighth.add(RefuseResults, c);

				TrackEndPoints.addActionListener(new TrackendsListener());
				SkipframeandTrackEndPoints.addActionListener(new SkipFramesandTrackendsListener());
				CheckResults.addActionListener(new CheckResultsListener());
				AcceptResults.addActionListener(new AcceptResultsListener());

				RefuseResults.addActionListener(new RefuseResultsListener());

				panelEighth.validate();
				panelEighth.repaint();

			}

		}

	}

	protected class CheckResultsListener implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent arg0) {

			if (redo) {
				final GridBagLayout layout = new GridBagLayout();
				final GridBagConstraints c = new GridBagConstraints();
				panelEighth.removeAll();
				final Label Step8 = new Label("Step 8", Label.CENTER);
				panelEighth.setLayout(layout);
				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridx = 0;
				c.gridy = 0;
				c.weightx = 4;
				c.weighty = 1.5;

				panelEighth.add(Step8, c);
				JLabel warning = new JLabel(UIManager.getIcon("OptionPane.warningIcon"));

				final Label RedotextA = new Label("MTtracker redo = ", Label.CENTER);
				JTextArea textArea = new JTextArea(
						"On average the result was " + (float) netdeltad
								+ "(pixels) away from the Kymo (your cutoff was " + deltadcutoff + "(pixels) ) "
								+ " When MT is very small the optimizer identifies the nearest bright spot as part of the line, adding a wrong length for that frame "
								+ " this causes the wrong number to be added over all frames causing a deviation from Kymo (which does not affects the rates) "
								+ " If However the mistake was in Kymograph, then ignore this step and go next to compute rates.",
						6, 20);
				textArea.setFont(new Font("Serif", Font.PLAIN, 16));
				textArea.setLineWrap(true);
				textArea.setWrapStyleWord(true);
				textArea.setOpaque(false);
				textArea.setEditable(false);

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 200);
				panelEighth.add(warning, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 200);
				panelEighth.add(textArea, c);

				final Button AcceptResults = new Button("Accept Results");
				final Button RefuseResults = new Button("Refuse Results, do over");
				++c.gridy;
				c.insets = new Insets(10, 175, 0, 200);
				panelEighth.add(AcceptResults, c);
				++c.gridy;
				c.insets = new Insets(10, 175, 0, 200);
				panelEighth.add(RefuseResults, c);

				AcceptResults.addActionListener(new AcceptResultsListener());
				RefuseResults.addActionListener(new RefuseResultsListener());

				RedotextA.setBackground(new Color(1, 0, 1));
				RedotextA.setForeground(new Color(255, 255, 255));
				panelEighth.validate();
				panelEighth.repaint();

			}

			else {
				final GridBagLayout layout = new GridBagLayout();
				final GridBagConstraints c = new GridBagConstraints();
				panelEighth.removeAll();

				panelEighth.setLayout(layout);
				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridx = 0;
				c.gridy = 0;
				c.weightx = 4;
				c.weighty = 1.5;

				
				final Label SuccessA = new Label(" Congratulations: that is quite close to the Kymograph, + ",
						Label.CENTER);
				final Label SuccessB = new Label(" Now you can compute rates, choose start and end frame: ",
						Label.CENTER);

				final Label Done = new Label("The results have been compiled and stored, you can now exit",
						Label.CENTER);

				final Button Analyze = new Button("Do Rough Analysis");

				final Scrollbar startS = new Scrollbar(Scrollbar.HORIZONTAL, thirdDimensionsliderInit, 10, 0,
						10 + scrollbarSize);
				final Scrollbar endS = new Scrollbar(Scrollbar.HORIZONTAL, thirdDimensionsliderInit, 10, 0,
						10 + scrollbarSize);
				starttime = (int) computeValueFromScrollbarPosition(thirdDimensionsliderInit, 0, thirdDimensionSize,
						scrollbarSize);
				endtime = (int) computeValueFromScrollbarPosition(thirdDimensionsliderInit, 0, thirdDimensionSize,
						scrollbarSize);
				final Label startText = new Label("startFrame = ", Label.CENTER);
				final Label endText = new Label("endFrame = ", Label.CENTER);

				SuccessA.setBackground(new Color(1, 0, 1));
				SuccessA.setForeground(new Color(255, 255, 255));

				SuccessB.setBackground(new Color(1, 0, 1));
				SuccessB.setForeground(new Color(255, 255, 255));
				if(analyzekymo && Kymoimg!=null){
				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelEighth.add(SuccessA, c);
				}
				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelEighth.add(SuccessB, c);
				
				
				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelEighth.add(startText, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelEighth.add(startS, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelEighth.add(endText, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelEighth.add(endS, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelEighth.add(Analyze, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelEighth.add(Done, c);

				startS.addAdjustmentListener(new starttimeListener(startText, thirdDimensionsliderInit,
						thirdDimensionSize, scrollbarSize, startS));
				endS.addAdjustmentListener(new endtimeListener(endText, thirdDimensionsliderInit, thirdDimensionSize,
						scrollbarSize, endS));
				Analyze.addActionListener(new AnalyzeListener());

				panelEighth.validate();
				panelEighth.repaint();

			}

		}

	}

	public void goSkip() {

		jpb.setIndeterminate(false);

		jpb.setMaximum(max);
		panel.add(label);
		panel.add(jpb);
		frame.add(panel);
		frame.pack();
		frame.setSize(200, 100);
		frame.setLocationRelativeTo(panelCont);
		frame.setVisible(true);

		ProgressSkip trackMT = new ProgressSkip();
		trackMT.execute();

	}

	class ProgressSkip extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() throws Exception {

			moveDialogue();

			int next = thirdDimension;

			if (next < 2)
				next = 2;

			maxStack();
			/*
			 * int loopsize = 0;
			 * 
			 * double currentrationeg = Intensityratio; double currentratiopos =
			 * Intensityratio; int negcount = 0; int poscount = 0; while
			 * (currentrationeg >= 0.2) { negcount++; currentrationeg-=0.1;
			 * 
			 * };
			 * 
			 * while (currentratiopos <= 0.2) { poscount++;
			 * currentratiopos+=0.1;
			 * 
			 * }; loopsize = negcount + poscount;
			 * 
			 * for (int loop = 0; loop < loopsize && DoRloop;++loop){
			 * 
			 * 
			 * Intensityratio = currentrationeg + loop*0.1;
			 * 
			 * }
			 */

			int Kalmancount = 0;
			
			
			for (int index = next; index <= thirdDimensionSize; ++index) {

				Kalmancount++;

				thirdDimension = index;

				CurrentPreprocessedView = getCurrentPreView();
				CurrentView = getCurrentView();
				
				
				
				updatePreview(ValueChange.THIRDDIMTrack);

				
				isStarted = true;

				Roi roi = preprocessedimp.getRoi();
				if (roi == null) {
					// IJ.log( "A rectangular ROI is required to define the
					// area..."
					// );
					preprocessedimp.setRoi(standardRectangle);
					roi = preprocessedimp.getRoi();
				}
				IJ.log("Current frame: " + thirdDimension);

				boolean dialog;
				boolean dialogupdate;
				ArrayList<Pair<Integer, double[]>> ID = new ArrayList<Pair<Integer, double[]>>();
				RandomAccessibleInterval<FloatType> groundframe = currentimg;
				RandomAccessibleInterval<FloatType> groundframepre = currentPreprocessedimg;
				if (FindLinesViaMSER) {
					if (index == next)
						dialog = DialogueModelChoiceHF();

					else
						dialog = false;

					updatePreview(ValueChange.SHOWMSER);

					LinefinderInteractiveHFMSER newlineMser = new LinefinderInteractiveHFMSER(groundframe,
							groundframepre, newtree, minlength, thirdDimension);

					if (showDeterministic) {
						returnVector = FindlinesVia.LinefindingMethodHF(groundframe, groundframepre, PrevFrameparam,
								minlength, thirdDimension, psf, newlineMser, userChoiceModel, Domask, Intensityratio,
								Inispacing, Trackstart,jpb, thirdDimensionSize);

						Accountedframes.add(FindlinesVia.getAccountedframes());
					}
					if (showKalman) {
						returnVectorKalman = FindlinesVia.LinefindingMethodHFKalman(groundframe, groundframepre,
								PrevFrameparamKalman, minlength, thirdDimension, psf, newlineMser, userChoiceModel,
								Domask, Kalmancount, Intensityratio, Inispacing, Trackstart,jpb, thirdDimensionSize);
						Accountedframes.add(FindlinesVia.getAccountedframes());
					}

				}

				if (FindLinesViaHOUGH) {

					if (index == next)
						dialog = DialogueModelChoiceHF();
					else
						dialog = false;

					updatePreview(ValueChange.SHOWHOUGH);
					LinefinderInteractiveHFHough newlineHough = new LinefinderInteractiveHFHough(groundframe,
							groundframepre, intimg, Maxlabel, thetaPerPixel, rhoPerPixel, thirdDimension);
					if (showDeterministic) {
						returnVector = FindlinesVia.LinefindingMethodHF(groundframe, groundframepre, PrevFrameparam,
								minlength, thirdDimension, psf, newlineHough, userChoiceModel, Domask, Intensityratio,
								Inispacing, Trackstart,jpb, thirdDimensionSize);
						Accountedframes.add(FindlinesVia.getAccountedframes());

					}
					if (showKalman) {
						returnVectorKalman = FindlinesVia.LinefindingMethodHFKalman(groundframe, groundframepre,
								PrevFrameparamKalman, minlength, thirdDimension, psf, newlineHough, userChoiceModel,
								Domask, Kalmancount, Intensityratio, Inispacing, Trackstart,jpb, thirdDimensionSize);

						Accountedframes.add(FindlinesVia.getAccountedframes());
					}
				}

				if (FindLinesViaMSERwHOUGH) {
					if (index == next)
						dialog = DialogueModelChoiceHF();
					else
						dialog = false;

					updatePreview(ValueChange.SHOWMSER);
					LinefinderInteractiveHFMSERwHough newlineMserwHough = new LinefinderInteractiveHFMSERwHough(
							groundframe, groundframepre, newtree, minlength, thirdDimension, thetaPerPixel,
							rhoPerPixel);

					if (showDeterministic) {
						returnVector = FindlinesVia.LinefindingMethodHF(groundframe, groundframepre, PrevFrameparam,
								minlength, thirdDimension, psf, newlineMserwHough, userChoiceModel, Domask,
								Intensityratio, Inispacing, Trackstart,jpb, thirdDimensionSize);
						Accountedframes.add(FindlinesVia.getAccountedframes());

					}

					if (showKalman) {
						returnVectorKalman = FindlinesVia.LinefindingMethodHFKalman(groundframe, groundframepre,
								PrevFrameparamKalman, minlength, thirdDimension, psf, newlineMserwHough,
								userChoiceModel, Domask, Kalmancount, Intensityratio, Inispacing, Trackstart,jpb, thirdDimensionSize);

						Accountedframes.add(FindlinesVia.getAccountedframes());

					}

				}

				if (showDeterministic) {

					NewFrameparam = returnVector.getB();

					ArrayList<Trackproperties> startStateVectors = returnVector.getA().getA();
					ArrayList<Trackproperties> endStateVectors = returnVector.getA().getB();

					PrevFrameparam = NewFrameparam;

					Allstart.add(startStateVectors);
					Allend.add(endStateVectors);

				}

				if (showKalman) {
					NewFrameparamKalman = returnVectorKalman.getB();

					ArrayList<KalmanTrackproperties> startStateVectorsKalman = returnVectorKalman.getA().getA();
					ArrayList<KalmanTrackproperties> endStateVectorsKalman = returnVectorKalman.getA().getB();

					PrevFrameparamKalman = NewFrameparamKalman;

					AllstartKalman.add(startStateVectorsKalman);
					AllendKalman.add(endStateVectorsKalman);

				}
			}

			if (showDeterministic) {
				
				
				final Trackstart trackerstart = new Trackstart(Allstart, thirdDimensionSize - next);
				final Trackend trackerend = new Trackend(Allend, thirdDimensionSize - next);
				if (Trackstart) {
					ImagePlus impstart = ImageJFunctions.show(originalimg);
					ImagePlus impstartsec = ImageJFunctions.show(originalimg);
					trackerstart.process();
					SimpleWeightedGraph<double[], DefaultWeightedEdge> graphstart = trackerstart.getResult();
					ArrayList<Subgraphs> subgraphstart = trackerstart.getFramedgraph();
					ArrayList<Pair<Integer, double[]>> ID = trackerstart.getSeedID();

					DisplaysubGraphstart displaytrackstart = new DisplaysubGraphstart(impstart, subgraphstart, next);
					displaytrackstart.getImp();
					impstart.draw();
					impstart.setTitle("Subgraph A");

					DisplayGraph displaygraphtrackstart = new DisplayGraph(impstartsec, graphstart, ID);
					displaygraphtrackstart.getImp();
					impstartsec.draw();
					impstartsec.setTitle("Graph A");
				}

				if (Trackstart == false) {
					ImagePlus impend = ImageJFunctions.show(originalPreprocessedimg);

					
					ImagePlus impendsec = ImageJFunctions.show(originalPreprocessedimg);
					trackerend.process();
					SimpleWeightedGraph<double[], DefaultWeightedEdge> graphend = trackerend.getResult();
					ArrayList<Subgraphs> subgraphend = trackerend.getFramedgraph();
					ArrayList<Pair<Integer, double[]>> ID = trackerend.getSeedID();
					DisplaysubGraphend displaytrackend = new DisplaysubGraphend(impend, subgraphend, next);
					displaytrackend.getImp();
					impend.draw();
					impend.setTitle("Subgraph B");
					DisplayGraph displaygraphtrackend = new DisplayGraph(impendsec, graphend, ID);
					displaygraphtrackend.getImp();
					impendsec.draw();
					impendsec.setTitle("Graph B");
				}
			}

			if (showKalman) {
				ResultsTable rtAll = new ResultsTable();
				if (Trackstart) {
					MTtrackerstart.reset();
					MTtrackerstart.process();

					ImagePlus impstartKalman = ImageJFunctions.show(originalimg);
					impstartKalman.setTitle("Kalman Start MT");
					SimpleWeightedGraph<KalmanTrackproperties, DefaultWeightedEdge> graphstartKalman = MTtrackerstart
							.getResult();
					DisplayGraphKalman Startdisplaytracks = new DisplayGraphKalman(impstartKalman, graphstartKalman);
					Startdisplaytracks.getImp();

					TrackModel modelstart = new TrackModel(graphstartKalman);
					modelstart.getDirectedNeighborIndex();
					IJ.log(" " + graphstartKalman.vertexSet().size());

					// Get all the track id's
					for (final Integer id : modelstart.trackIDs(true)) {

						ResultsTable rt = new ResultsTable();
						// Get the corresponding set for each id
						modelstart.setName(id, "Track" + id);
						final HashSet<KalmanTrackproperties> Snakeset = modelstart.trackKalmanTrackpropertiess(id);
						ArrayList<KalmanTrackproperties> list = new ArrayList<KalmanTrackproperties>();

						Comparator<KalmanTrackproperties> ThirdDimcomparison = new Comparator<KalmanTrackproperties>() {

							@Override
							public int compare(final KalmanTrackproperties A, final KalmanTrackproperties B) {

								return A.thirdDimension - B.thirdDimension;

							}

						};

						Iterator<KalmanTrackproperties> Snakeiter = Snakeset.iterator();

						while (Snakeiter.hasNext()) {

							KalmanTrackproperties currentsnake = Snakeiter.next();

							list.add(currentsnake);

						}
						Collections.sort(list, ThirdDimcomparison);

						final double[] originalpoint = list.get(0).currentpoint;
						double startlength = 0;
						double startlengthpixel = 0;
						for (int index = 1; index < list.size(); ++index) {

							final double[] currentpoint = list.get(index).currentpoint;
							final double[] oldpoint = list.get(index - 1).currentpoint;
							final double[] currentpointCal = new double[] { currentpoint[0] * calibration[0],
									currentpoint[1] * calibration[1] };
							final double[] oldpointCal = new double[] { oldpoint[0] * calibration[0],
									oldpoint[1] * calibration[1] };
							final double lengthpixel = util.Boundingboxes.Distance(currentpoint, oldpoint);
							final double length = util.Boundingboxes.Distance(currentpointCal, oldpointCal);
							final double seedtocurrent = util.Boundingboxes.Distancesq(originalpoint, currentpoint);
							final double seedtoold = util.Boundingboxes.Distancesq(originalpoint, oldpoint);

							final boolean shrink = seedtoold > seedtocurrent ? true : false;
							final boolean growth = seedtoold > seedtocurrent ? false : true;
							if (shrink) {

								// MT shrank

								startlength -= length;
								startlengthpixel -= lengthpixel;

							}
							if (growth) {

								// MT grew

								startlength += length;
								startlengthpixel += lengthpixel;

							}

							rt.incrementCounter();

							rt.addValue("FrameNumber", list.get(index).thirdDimension);
							rt.addValue("Track iD", id);
							rt.addValue("PreviousPosition X (px units)", oldpoint[0]);
							rt.addValue("PreviousPosition Y (px units)", oldpoint[1]);
							rt.addValue("CurrentPosition X (px units)", currentpoint[0]);
							rt.addValue("CurrentPosition Y (px units)", currentpoint[1]);
							rt.addValue("PreviousPosition X (real units)", oldpointCal[0]);
							rt.addValue("PreviousPosition Y (real units)", oldpointCal[1]);
							rt.addValue("CurrentPosition X (real units)", currentpointCal[0]);
							rt.addValue("CurrentPosition Y (real units)", currentpointCal[1]);
							rt.addValue("Length in real units", length);
							rt.addValue("Cummulative Length in real units", startlength);
							double[] landt = { startlengthpixel, list.get(index).thirdDimension };
							lengthtimestart.add(landt);
							rtAll.incrementCounter();

							rtAll.addValue("FrameNumber", list.get(index).thirdDimension);
							rtAll.addValue("Track iD", id);
							rtAll.addValue("PreviousPosition X (px units)", oldpoint[0]);
							rtAll.addValue("PreviousPosition Y (px units)", oldpoint[1]);
							rtAll.addValue("CurrentPosition X (px units)", currentpoint[0]);
							rtAll.addValue("CurrentPosition Y (px units)", currentpoint[1]);
							rtAll.addValue("PreviousPosition X (real units)", oldpointCal[0]);
							rtAll.addValue("PreviousPosition Y (real units)", oldpointCal[1]);
							rtAll.addValue("CurrentPosition X (real units)", currentpointCal[0]);
							rtAll.addValue("CurrentPosition Y (real units)", currentpointCal[1]);
							rtAll.addValue("Length in real units", length);
							rtAll.addValue("Cummulative Length in real units", startlength);

						}

						if (SaveXLS )
							saveResultsToExcel(usefolder + "//" + addTrackToName + "KalmanStart" + id + ".xls", rt);

					}

				}

				if (Trackstart == false) {
					MTtrackerend.reset();
					MTtrackerend.process();

					SimpleWeightedGraph<KalmanTrackproperties, DefaultWeightedEdge> graphendKalman = MTtrackerend
							.getResult();

					ImagePlus impendKalman = ImageJFunctions.show(originalPreprocessedimg);
					impendKalman.setTitle("Kalman End MT");

					IJ.log("KalmanTracking Complete " + " " + "Displaying results");

					DisplayGraphKalman Enddisplaytracks = new DisplayGraphKalman(impendKalman, graphendKalman);
					Enddisplaytracks.getImp();

					TrackModel modelend = new TrackModel(graphendKalman);
					modelend.getDirectedNeighborIndex();
					IJ.log(" " + graphendKalman.vertexSet().size());
					// Get all the track id's
					for (final Integer id : modelend.trackIDs(true)) {
						ResultsTable rt = new ResultsTable();
						// Get the corresponding set for each id
						modelend.setName(id, "Track" + id);
						final HashSet<KalmanTrackproperties> Snakeset = modelend.trackKalmanTrackpropertiess(id);
						ArrayList<KalmanTrackproperties> list = new ArrayList<KalmanTrackproperties>();

						Comparator<KalmanTrackproperties> ThirdDimcomparison = new Comparator<KalmanTrackproperties>() {

							@Override
							public int compare(final KalmanTrackproperties A, final KalmanTrackproperties B) {

								return A.thirdDimension - B.thirdDimension;

							}

						};

						Iterator<KalmanTrackproperties> Snakeiter = Snakeset.iterator();

						while (Snakeiter.hasNext()) {

							KalmanTrackproperties currentsnake = Snakeiter.next();

							list.add(currentsnake);

						}
						Collections.sort(list, ThirdDimcomparison);

						final double[] originalpoint = list.get(0).currentpoint;
						double endlength = 0;
						double endlengthpixel = 0;
						for (int index = 1; index < list.size() - 1; ++index) {

							final double[] currentpoint = list.get(index).currentpoint;
							final double[] oldpoint = list.get(index - 1).currentpoint;
							final double[] currentpointCal = new double[] { currentpoint[0] * calibration[0],
									currentpoint[1] * calibration[1] };
							final double[] oldpointCal = new double[] { oldpoint[0] * calibration[0],
									oldpoint[1] * calibration[1] };
							final double lengthpixel = util.Boundingboxes.Distance(currentpoint, oldpoint);
							final double length = util.Boundingboxes.Distance(currentpointCal, oldpointCal);
							final double seedtocurrent = util.Boundingboxes.Distancesq(originalpoint, currentpoint);
							final double seedtoold = util.Boundingboxes.Distancesq(originalpoint, oldpoint);
							final boolean shrink = seedtoold > seedtocurrent ? true : false;
							final boolean growth = seedtoold > seedtocurrent ? false : true;

							if (shrink) {
								// MT shrank
								endlength -= length;

								endlengthpixel -= lengthpixel;

							}
							if (growth) {

								// MT grew
								endlength += length;
								endlengthpixel += lengthpixel;

							}
							rt.incrementCounter();

							rt.addValue("FrameNumber", list.get(index).thirdDimension);
							rt.addValue("Track iD", id);
							rt.addValue("PreviousPosition X (px units)", oldpoint[0]);
							rt.addValue("PreviousPosition Y (px units)", oldpoint[1]);
							rt.addValue("CurrentPosition X (px units)", currentpoint[0]);
							rt.addValue("CurrentPosition Y (px units)", currentpoint[1]);
							rt.addValue("PreviousPosition X (real units)", oldpointCal[0]);
							rt.addValue("PreviousPosition Y (real units)", oldpointCal[1]);
							rt.addValue("CurrentPosition X (real units)", currentpointCal[0]);
							rt.addValue("CurrentPosition Y (real units)", currentpointCal[1]);
							rt.addValue("Length in real units", length);
							rt.addValue("Cummulative Length in real units", endlength);
							double[] landt = { endlengthpixel, list.get(index).thirdDimension };
							lengthtimeend.add(landt);
							rtAll.incrementCounter();

							rtAll.addValue("FrameNumber", list.get(index).thirdDimension);
							rtAll.addValue("Track iD", id);
							rtAll.addValue("PreviousPosition X (px units)", oldpoint[0]);
							rtAll.addValue("PreviousPosition Y (px units)", oldpoint[1]);
							rtAll.addValue("CurrentPosition X (px units)", currentpoint[0]);
							rtAll.addValue("CurrentPosition Y (px units)", currentpoint[1]);
							rtAll.addValue("PreviousPosition X (real units)", oldpointCal[0]);
							rtAll.addValue("PreviousPosition Y (real units)", oldpointCal[1]);
							rtAll.addValue("CurrentPosition X (real units)", currentpointCal[0]);
							rtAll.addValue("CurrentPosition Y (real units)", currentpointCal[1]);
							rtAll.addValue("Length in real units", length);
							rtAll.addValue("Cummulative Length in real units", endlength);

						}

						if (SaveXLS )
							saveResultsToExcel(usefolder + "//" + addTrackToName + "KalmanEnd" + id + ".xls", rt);

					}

					rtAll.show("Results");

				}
			}
			if (showDeterministic) {

				NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
				nf.setMaximumFractionDigits(3);

				ResultsTable rtAll = new ResultsTable();

				if (Trackstart) {
					ArrayList<Pair<Integer[], double[]>> lengthliststart = new ArrayList<Pair<Integer[], double[]>>();

					final ArrayList<Trackproperties> first = Allstart.get(0);
					int MaxSeedLabel = first.get(first.size() - 1).seedlabel;
					int MinSeedLabel = first.get(0).seedlabel;

					for (int currentseed = MinSeedLabel; currentseed < MaxSeedLabel + 1; ++currentseed) {
						double startlength = 0;
						double startlengthpixel = 0;
						for (int index = 0; index < Allstart.size(); ++index) {

						
							final ArrayList<Trackproperties> thirdDimension = Allstart.get(index);

							for (int frameindex = 0; frameindex < thirdDimension.size(); ++frameindex) {

								final Integer SeedID = thirdDimension.get(frameindex).seedlabel;

								final int framenumber = thirdDimension.get(frameindex).Framenumber;
								if (SeedID == currentseed) {

									final Integer[] FrameID = { framenumber, SeedID };
									final double[] originalpoint = thirdDimension.get(frameindex).originalpoint;
									final double[] newpoint = thirdDimension.get(frameindex).newpoint;
									final double[] oldpoint = thirdDimension.get(frameindex).oldpoint;
									final double[] newpointCal = new double[] {
											thirdDimension.get(frameindex).newpoint[0] * calibration[0],
											thirdDimension.get(frameindex).newpoint[1] * calibration[1] };
									final double[] oldpointCal = new double[] {
											thirdDimension.get(frameindex).oldpoint[0] * calibration[0],
											thirdDimension.get(frameindex).oldpoint[1] * calibration[1] };
									final double lengthpixel = util.Boundingboxes.Distance(newpoint, oldpoint);
									final double length = util.Boundingboxes.Distance(newpointCal, oldpointCal);
									final double seedtocurrent = util.Boundingboxes.Distancesq(originalpoint, newpoint);
									final double seedtoold = util.Boundingboxes.Distancesq(originalpoint, oldpoint);
									final boolean shrink = seedtoold > seedtocurrent ? true : false;
									final boolean growth = seedtoold > seedtocurrent ? false : true;

									if (shrink) {
										// MT shrank
										startlength -= length;
										startlengthpixel -= lengthpixel;

									}
									if (growth) {

										// MT grew
										startlength += length;
										startlengthpixel += lengthpixel;

									}
									final double[] startinfo = { oldpoint[0], oldpoint[1], newpoint[0], newpoint[1],
											oldpointCal[0], oldpointCal[1], newpointCal[0], newpointCal[1], length,
											startlength, lengthpixel, startlengthpixel };
									Pair<Integer[], double[]> lengthpair = new ValuePair<Integer[], double[]>(FrameID,
											startinfo);

									lengthliststart.add(lengthpair);

								}
							}

						}

					}

					for (int seedID = MinSeedLabel; seedID <= MaxSeedLabel; ++seedID) {
						ResultsTable rt = new ResultsTable();
						if (SaveTxt) {
							try {

								File fichier = new File(
										usefolder + "//" + addTrackToName + "SeedLabel" + seedID + "-start" + ".txt");
								File fichierMy = new File(
										usefolder + "//" + addToName + "KymoVarun-start" + seedID + ".txt");
								FileWriter fw = new FileWriter(fichier);
								BufferedWriter bw = new BufferedWriter(fw);

								bw.write(
										"\tFramenumber\tSeedLabel\tOldX (px)\tOldY (px)\tNewX (px)\tNewY (px)\tOldX (real)\tOldY (real)"
												+ "\tNewX (real)\tNewY (real)"
												+ "\tLength ( real)\tCummulativeLength (real)n");

								FileWriter fwmy = new FileWriter(fichierMy);
								BufferedWriter bwmy = new BufferedWriter(fwmy);

								bwmy.write("\tFramenumber\tLength\n");

								for (int index = 0; index < lengthliststart.size(); ++index) {
									if (lengthliststart.get(index).getA()[1] == seedID) {
										bw.write("\t" + lengthliststart.get(index).getA()[0] + "\t" + "\t"
												+ nf.format(lengthliststart.get(index).getA()[1]) + "\t" + "\t"
												+ nf.format(lengthliststart.get(index).getB()[0]) + "\t" + "\t"
												+ nf.format(lengthliststart.get(index).getB()[1]) + "\t" + "\t"
												+ nf.format(lengthliststart.get(index).getB()[2]) + "\t" + "\t"
												+ nf.format(lengthliststart.get(index).getB()[3]) + "\t" + "\t"
												+ nf.format(lengthliststart.get(index).getB()[4]) + "\t" + "\t"
												+ nf.format(lengthliststart.get(index).getB()[5]) + "\t" + "\t"
												+ nf.format(lengthliststart.get(index).getB()[6]) + "\t" + "\t"
												+ nf.format(lengthliststart.get(index).getB()[7]) + "\t" + "\t"
												+ nf.format(lengthliststart.get(index).getB()[8]) + "\t" + "\t"
												+ nf.format(lengthliststart.get(index).getB()[9]) + "\n");

										bwmy.write("\t" + lengthliststart.get(index).getA()[0] + "\t" + "\t"
												+ nf.format(lengthliststart.get(index).getB()[11]) + "\n");

									}

								}
								bw.close();
								fw.close();
								bwmy.close();
								fwmy.close();

							} catch (IOException e) {
							}
						}

						for (int index = 0; index < lengthliststart.size(); ++index) {
							if (lengthliststart.get(index).getA()[1] == seedID) {
								rt.incrementCounter();
								rt.addValue("FrameNumber", lengthliststart.get(index).getA()[0]);
								rt.addValue("SeedLabel", lengthliststart.get(index).getA()[1]);
								rt.addValue("OldX in px units", lengthliststart.get(index).getB()[0]);
								rt.addValue("OldY in px units", lengthliststart.get(index).getB()[1]);
								rt.addValue("NewX in px units", lengthliststart.get(index).getB()[2]);
								rt.addValue("NewY in px units", lengthliststart.get(index).getB()[3]);
								rt.addValue("OldX in real units", lengthliststart.get(index).getB()[4]);
								rt.addValue("OldY in real units", lengthliststart.get(index).getB()[5]);
								rt.addValue("NewX in real units", lengthliststart.get(index).getB()[6]);
								rt.addValue("NewY in real units", lengthliststart.get(index).getB()[7]);
								rt.addValue("Length in real units", lengthliststart.get(index).getB()[8]);
								rt.addValue("Cummulative Length in real units", lengthliststart.get(index).getB()[9]);
								double[] landt = { lengthliststart.get(index).getB()[11],
										lengthliststart.get(index).getA()[0] };
								lengthtimestart.add(landt);

								rtAll.incrementCounter();
								rtAll.addValue("FrameNumber", lengthliststart.get(index).getA()[0]);
								rtAll.addValue("SeedLabel", lengthliststart.get(index).getA()[1]);
								rtAll.addValue("OldX in px units", lengthliststart.get(index).getB()[0]);
								rtAll.addValue("OldY in px units", lengthliststart.get(index).getB()[1]);
								rtAll.addValue("NewX in px units", lengthliststart.get(index).getB()[2]);
								rtAll.addValue("NewY in px units", lengthliststart.get(index).getB()[3]);
								rtAll.addValue("OldX in real units", lengthliststart.get(index).getB()[4]);
								rtAll.addValue("OldY in real units", lengthliststart.get(index).getB()[5]);
								rtAll.addValue("NewX in real units", lengthliststart.get(index).getB()[6]);
								rtAll.addValue("NewY in real units", lengthliststart.get(index).getB()[7]);
								rtAll.addValue("Length in real units", lengthliststart.get(index).getB()[8]);
								rtAll.addValue("Cummulative Length in real units", lengthliststart.get(index).getB()[9]);

							}

						}
						ArrayList<Line> allline = new ArrayList<Line>();

						if (SaveXLS)
							saveResultsToExcel(
									usefolder + "//" + addTrackToName + "start" + "SeedLabel" + seedID + ".xls", rt);

					}
				}
				if (Trackstart == false) {
					final ArrayList<Trackproperties> first = Allend.get(0);
					int MaxSeedLabel = first.get(first.size() - 1).seedlabel;
					int MinSeedLabel = first.get(0).seedlabel;
					ArrayList<Pair<Integer[], double[]>> lengthlistend = new ArrayList<Pair<Integer[], double[]>>();
					for (int currentseed = MinSeedLabel; currentseed < MaxSeedLabel + 1; ++currentseed) {
						double endlengthpixel = 0;
						double endlength = 0;
						for (int index = 0; index < Allend.size(); ++index) {

							

							final ArrayList<Trackproperties> thirdDimension = Allend.get(index);

							for (int frameindex = 0; frameindex < thirdDimension.size(); ++frameindex) {
								final Integer SeedID = thirdDimension.get(frameindex).seedlabel;
								final int framenumber = thirdDimension.get(frameindex).Framenumber;
								if (SeedID == currentseed) {

									final Integer[] FrameID = { framenumber, SeedID };
									final double[] originalpoint = thirdDimension.get(frameindex).originalpoint;
									final double[] newpoint = thirdDimension.get(frameindex).newpoint;
									final double[] oldpoint = thirdDimension.get(frameindex).oldpoint;

									final double[] newpointCal = new double[] {
											thirdDimension.get(frameindex).newpoint[0] * calibration[0],
											thirdDimension.get(frameindex).newpoint[1] * calibration[1] };
									final double[] oldpointCal = new double[] {
											thirdDimension.get(frameindex).oldpoint[0] * calibration[0],
											thirdDimension.get(frameindex).oldpoint[1] * calibration[1] };

									final double length = util.Boundingboxes.Distance(newpointCal, oldpointCal);
									final double lengthpixel = util.Boundingboxes.Distance(newpoint, oldpoint);
									final double seedtocurrent = util.Boundingboxes.Distancesq(originalpoint, newpoint);
									final double seedtoold = util.Boundingboxes.Distancesq(originalpoint, oldpoint);
									final boolean shrink = seedtoold > seedtocurrent ? true : false;
									final boolean growth = seedtoold > seedtocurrent ? false : true;

									if (shrink) {

										// MT shrank
										endlength -= length;
										endlengthpixel -= lengthpixel;

									}
									if (growth) {

										// MT grew
										endlength += length;
										endlengthpixel += lengthpixel;

									}

									final double[] endinfo = { oldpoint[0], oldpoint[1], newpoint[0], newpoint[1],
											oldpointCal[0], oldpointCal[1], newpointCal[0], newpointCal[1], length,
											endlength, lengthpixel, endlengthpixel };
									Pair<Integer[], double[]> lengthpair = new ValuePair<Integer[], double[]>(FrameID,
											endinfo);

									lengthlistend.add(lengthpair);

								}
							}

						}

					}

					for (int seedID = MinSeedLabel; seedID <= MaxSeedLabel; ++seedID) {
						ResultsTable rtend = new ResultsTable();
						if (SaveTxt) {
							try {
								File fichier = new File(
										usefolder + "//" + addTrackToName + "SeedLabel" + seedID + "-end" + ".txt");
								File fichierMy = new File(
										usefolder + "//" + addToName + "KymoVarun-end" + seedID + ".txt");

								File Rates = new File(usefolder + "//" + addToName + "Rates" + seedID + ".txt");
								FileWriter fw = new FileWriter(fichier);
								BufferedWriter bw = new BufferedWriter(fw);
								bw.write(
										"\tFramenumber\tSeedLabel\tOldX (px)\tOldY (px)\tNewX (px)\tNewY (px)\tOldX (real)\tOldY (real)"
												+ "\tNewX (real)\tNewY (real)"
												+ "\tLength ( real)\tCummulativeLength(real)\n");
								FileWriter fwmy = new FileWriter(fichierMy);
								BufferedWriter bwmy = new BufferedWriter(fwmy);

								bwmy.write("\tFramenumber\tLength\n");
								for (int index = 0; index < lengthlistend.size(); ++index) {
									if (lengthlistend.get(index).getA()[1] == seedID) {
										bw.write("\t" + lengthlistend.get(index).getA()[0] + "\t" + "\t"
												+ nf.format(lengthlistend.get(index).getA()[1]) + "\t" + "\t"
												+ nf.format(lengthlistend.get(index).getB()[0]) + "\t" + "\t"
												+ nf.format(lengthlistend.get(index).getB()[1]) + "\t" + "\t"
												+ nf.format(lengthlistend.get(index).getB()[2]) + "\t" + "\t"
												+ nf.format(lengthlistend.get(index).getB()[3]) + "\t" + "\t"
												+ nf.format(lengthlistend.get(index).getB()[4]) + "\t" + "\t"
												+ nf.format(lengthlistend.get(index).getB()[5]) + "\t" + "\t"
												+ nf.format(lengthlistend.get(index).getB()[6]) + "\t" + "\t"
												+ nf.format(lengthlistend.get(index).getB()[7]) + "\t" + "\t"
												+ nf.format(lengthlistend.get(index).getB()[8]) + "\t" + "\t"
												+ nf.format(lengthlistend.get(index).getB()[9]) + "\n");

										bwmy.write("\t" + lengthlistend.get(index).getA()[0] + "\t" + "\t"
												+ nf.format(lengthlistend.get(index).getB()[11]) + "\n");

									}
								}
								bw.close();
								fw.close();
								bwmy.close();
								fwmy.close();

							} catch (IOException e) {
							}
						}

						for (int index = 0; index < lengthlistend.size(); ++index) {
							if (lengthlistend.get(index).getA()[1] == seedID) {
								rtend.incrementCounter();
								rtend.addValue("FrameNumber", lengthlistend.get(index).getA()[0]);
								rtend.addValue("SeedLabel", lengthlistend.get(index).getA()[1]);
								rtend.addValue("OldX in px units", lengthlistend.get(index).getB()[0]);
								rtend.addValue("OldY in px units", lengthlistend.get(index).getB()[1]);
								rtend.addValue("NewX in px units", lengthlistend.get(index).getB()[2]);
								rtend.addValue("NewY in px units", lengthlistend.get(index).getB()[3]);
								rtend.addValue("OldX in real units", lengthlistend.get(index).getB()[4]);
								rtend.addValue("OldY in real units", lengthlistend.get(index).getB()[5]);
								rtend.addValue("NewX in real units", lengthlistend.get(index).getB()[6]);
								rtend.addValue("NewY in real units", lengthlistend.get(index).getB()[7]);
								rtend.addValue("Length in real units", lengthlistend.get(index).getB()[8]);
								rtend.addValue("Cummulative Length in real units", lengthlistend.get(index).getB()[9]);
								double[] landt = { lengthlistend.get(index).getB()[11], lengthlistend.get(index).getA()[0] };
								lengthtimeend.add(landt);
								rtAll.incrementCounter();
								rtAll.addValue("FrameNumber", lengthlistend.get(index).getA()[0]);
								rtAll.addValue("SeedLabel", lengthlistend.get(index).getA()[1]);
								rtAll.addValue("OldX in px units", lengthlistend.get(index).getB()[0]);
								rtAll.addValue("OldY in px units", lengthlistend.get(index).getB()[1]);
								rtAll.addValue("NewX in px units", lengthlistend.get(index).getB()[2]);
								rtAll.addValue("NewY in px units", lengthlistend.get(index).getB()[3]);
								rtAll.addValue("OldX in real units", lengthlistend.get(index).getB()[4]);
								rtAll.addValue("OldY in real units", lengthlistend.get(index).getB()[5]);
								rtAll.addValue("NewX in real units", lengthlistend.get(index).getB()[6]);
								rtAll.addValue("NewY in real units", lengthlistend.get(index).getB()[7]);
								rtAll.addValue("Length in real units", lengthlistend.get(index).getB()[8]);
								rtAll.addValue("Cummulative Length in real units", lengthlistend.get(index).getB()[9]);

							}

						}

						if (SaveXLS)
							saveResultsToExcel(
									usefolder + "//" + addTrackToName + "end" + "seedLabel" + seedID + ".xls", rtend);

					}
				}
				rtAll.show("Start and End of MT, respectively");
				if (Trackstart) 
					lengthtime = lengthtimestart;
				else
					lengthtime = lengthtimeend;
				if (analyzekymo) {
					double lengthcheckstart = 0;
					double lengthcheckend = 0;
					if (Trackstart) {
						lengthtime = lengthtimestart;
						for (int index = 0; index < lengthtimestart.size(); ++index) {

							int time = (int) lengthtimestart.get(index)[1];

							lengthcheckstart += lengthtimestart.get(index)[0];

							for (int secindex = 0; secindex < Length.size(); ++secindex) {
								if ((int) Length.get(secindex)[1] == time) {
									for (int accountindex = 0; accountindex < Accountedframes.size(); ++accountindex) {

										if (Accountedframes.get(accountindex) == time) {

											float delta = (float) (lengthtimestart.get(index)[0]
													- Length.get(secindex)[0]);
											float[] cudeltadeltaLstart = { delta, time };
											deltadstart.add(cudeltadeltaLstart);

										}
									}
								}

							}
						}

						/********
						 * The part below removes the duplicate entries in the
						 * array dor the time co-ordinate
						 ********/

						int j = 0;

						for (int index = 0; index < deltadstart.size() - 1; ++index) {

							j = index + 1;

							while (j < deltadstart.size()) {

								if (deltadstart.get(index)[1] == deltadstart.get(j)[1]) {

									deltadstart.remove(index);
								}

								else {
									++j;

								}

							}
						}

						for (int index = 0; index < deltadstart.size(); ++index) {

							for (int secindex = 0; secindex < Accountedframes.size(); ++secindex) {

								if ((int) deltadstart.get(index)[1] == Accountedframes.get(secindex)) {

									netdeltadstart += Math.abs(deltadstart.get(index)[0]);

								}

							}

						}
						deltad = deltadstart;
						
					}
					if (Trackstart == false) {
						for (int index = 0; index < lengthtimeend.size(); ++index) {

							int time = (int) lengthtimeend.get(index)[1];

							lengthcheckend += lengthtimeend.get(index)[0];

							for (int secindex = 0; secindex < Length.size(); ++secindex) {
								if ((int) Length.get(secindex)[1] == time) {
									for (int accountindex = 0; accountindex < Accountedframes.size(); ++accountindex) {

										if (Accountedframes.get(accountindex) == time) {

											float delta = (float) (lengthtimeend.get(index)[0]
													- Length.get(secindex)[0]);
											float[] cudeltadeltaLend = { delta, time };
											deltadend.add(cudeltadeltaLend);
										}

									}

								}

							}
						}
						/********
						 * The part below removes the duplicate entries in the
						 * array dor the time co-ordinate
						 ********/

						int j = 0;

						for (int index = 0; index < deltadend.size() - 1; ++index) {

							j = index + 1;

							while (j < deltadend.size()) {

								if (deltadend.get(index)[1] == deltadend.get(j)[1]) {

									deltadend.remove(index);
								}

								else {
									++j;

								}

							}
						}

						for (int index = 0; index < deltadend.size(); ++index) {

							for (int secindex = 0; secindex < Accountedframes.size(); ++secindex) {

								if ((int) deltadend.get(index)[1] == Accountedframes.get(secindex)) {

									netdeltadend += Math.abs(deltadend.get(index)[0]);

								}

							}

						}
						deltad = deltadend;
						lengthtime = lengthtimeend;
					}

					if (lengthtimestart.size() > 0 && lengthtimeend.size() > 0) {
						deltad = (netdeltadstart >= netdeltadend) ? deltadend : deltadstart;
						lengthtime = (netdeltadstart >= netdeltadend) ? lengthtimeend : lengthtimestart;
					}

					FileWriter deltaw;
					File fichierKydel = new File(usefolder + "//" + addToName + "MTtracker-deltad" + ".txt");

					try {
						deltaw = new FileWriter(fichierKydel);
						BufferedWriter bdeltaw = new BufferedWriter(deltaw);

						bdeltaw.write("\ttime\tDeltad(pixel units)\n");

						for (int index = 0; index < deltad.size(); ++index) {
							bdeltaw.write("\t" + deltad.get(index)[1] + "\t" + deltad.get(index)[0] + "\n");

						}

						bdeltaw.close();
						deltaw.close();
					}

					catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					for (int index = 0; index < deltad.size(); ++index) {

						for (int secindex = 0; secindex < Accountedframes.size(); ++secindex) {

							if ((int) deltad.get(index)[1] == Accountedframes.get(secindex)) {

								netdeltad += Math.abs(deltad.get(index)[0]);

							}

						}

					}
					netdeltad /= deltad.size();

					if (netdeltad > deltadcutoff) {

						redo = true;

					} else
						redo = false;

				}
			}
			if (Kymoimg != null) {
				ImagePlus newimp = Kymoimp.duplicate();
				for (int index = 0; index < lengthtime.size() - 1; ++index) {

					Overlay overlayB = Kymoimp.getOverlay();
					if (overlayB == null) {
						overlayB = new Overlay();

						Kymoimp.setOverlay(overlayB);

					}
					Line newline = new Line(lengthtime.get(index)[0], lengthtime.get(index)[1],
							lengthtime.get(index + 1)[0], lengthtime.get(index + 1)[1]);
					newline.setFillColor(colorDraw);

					overlayB.add(newline);

					Kymoimp.setOverlay(overlayB);
					RoiManager roimanager = RoiManager.getInstance();

					roimanager.addRoi(newline);

				}
				
				Kymoimp.show();
			}
			if (displayoverlay)
				new ImagePlus("Overlays", prestack).show();
			return null;

		}

		@Override
		protected void done() {
			try {
				jpb.setIndeterminate(false);
				get();
				frame.dispose();
				JOptionPane.showMessageDialog(jpb.getParent(), "Success", "Success", JOptionPane.INFORMATION_MESSAGE);
			} catch (ExecutionException | InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

	protected class SkipFramesandTrackendsListener implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent arg0) {

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {

					goSkip();

				}

			});
		}
	}

	public void goTrack() {

		jpb.setIndeterminate(false);

		jpb.setMaximum(max);
		panel.add(label);
		panel.add(jpb);
		frame.add(panel);
		frame.pack();
		frame.setSize(200, 100);
		frame.setLocationRelativeTo(panelCont);
		frame.setVisible(true);

		ProgressTrack trackMT = new ProgressTrack();
		trackMT.execute();

	}

	class ProgressTrack extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() throws Exception {

		
			
			int next = 2;

			maxStack();
			int Kalmancount = 0;
			
			for (int index = next; index <= thirdDimensionSize; ++index) {

				Kalmancount++;
				thirdDimension = index;
				isStarted = true;
				CurrentPreprocessedView = getCurrentPreView();
				CurrentView = getCurrentView();
				updatePreview(ValueChange.THIRDDIMTrack);

				IJ.log("Current frame: " + thirdDimension);

				boolean dialog;
				boolean dialogupdate;

				RandomAccessibleInterval<FloatType> groundframe = currentimg;
				RandomAccessibleInterval<FloatType> groundframepre = currentPreprocessedimg;
				if (FindLinesViaMSER) {
					if (index == next)
						dialog = DialogueModelChoiceHF();

					else
						dialog = false;

					updatePreview(ValueChange.SHOWMSER);

					LinefinderInteractiveHFMSER newlineMser = new LinefinderInteractiveHFMSER(groundframe,
							groundframepre, newtree, minlength, thirdDimension);
					if (showDeterministic) {
						returnVector = FindlinesVia.LinefindingMethodHF(groundframe, groundframepre, PrevFrameparam,
								minlength, thirdDimension, psf, newlineMser, userChoiceModel, Domask, Intensityratio,
								Inispacing, Trackstart, jpb, thirdDimensionSize);
						Accountedframes.add(FindlinesVia.getAccountedframes());
					}

					if (showKalman) {
						returnVectorKalman = FindlinesVia.LinefindingMethodHFKalman(groundframe, groundframepre,
								PrevFrameparamKalman, minlength, thirdDimension, psf, newlineMser, userChoiceModel,
								Domask, Kalmancount, Intensityratio, Inispacing, Trackstart, jpb, thirdDimensionSize);

						Accountedframes.add(FindlinesVia.getAccountedframes());
					}

				}

				if (FindLinesViaHOUGH) {

					if (index == next)
						dialog = DialogueModelChoiceHF();

					else
						dialog = false;

					updatePreview(ValueChange.SHOWHOUGH);
					LinefinderInteractiveHFHough newlineHough = new LinefinderInteractiveHFHough(groundframe,
							groundframepre, intimg, Maxlabel, thetaPerPixel, rhoPerPixel, thirdDimension);
					if (showDeterministic) {
						returnVector = FindlinesVia.LinefindingMethodHF(groundframe, groundframepre, PrevFrameparam,
								minlength, thirdDimension, psf, newlineHough, userChoiceModel, Domask, Intensityratio,
								Inispacing, Trackstart, jpb, thirdDimensionSize);

						Accountedframes.add(FindlinesVia.getAccountedframes());
					}

					if (showKalman) {
						returnVectorKalman = FindlinesVia.LinefindingMethodHFKalman(groundframe, groundframepre,
								PrevFrameparamKalman, minlength, thirdDimension, psf, newlineHough, userChoiceModel,
								Domask, Kalmancount, Intensityratio, Inispacing, Trackstart, jpb, thirdDimensionSize);
						Accountedframes.add(FindlinesVia.getAccountedframes());
					}

				}

				if (FindLinesViaMSERwHOUGH) {
					if (index == next)
						dialog = DialogueModelChoice();
					else
						dialog = false;

					updatePreview(ValueChange.SHOWMSER);
					LinefinderInteractiveHFMSERwHough newlineMserwHough = new LinefinderInteractiveHFMSERwHough(
							groundframe, groundframepre, newtree, minlength, thirdDimension, thetaPerPixel,
							rhoPerPixel);
					if (showDeterministic) {
						returnVector = FindlinesVia.LinefindingMethodHF(groundframe, groundframepre, PrevFrameparam,
								minlength, thirdDimension, psf, newlineMserwHough, userChoiceModel, Domask,
								Intensityratio, Inispacing, Trackstart, jpb, thirdDimensionSize);

						Accountedframes.add(FindlinesVia.getAccountedframes());
					}
					if (showKalman) {
						returnVectorKalman = FindlinesVia.LinefindingMethodHFKalman(groundframe, groundframepre,
								PrevFrameparamKalman, minlength, thirdDimension, psf, newlineMserwHough,
								userChoiceModel, Domask, Kalmancount, Intensityratio, Inispacing, Trackstart, jpb, thirdDimensionSize);

						Accountedframes.add(FindlinesVia.getAccountedframes());
					}

				}

				if (showDeterministic) {
					NewFrameparam = returnVector.getB();

					ArrayList<Trackproperties> startStateVectors = returnVector.getA().getA();
					ArrayList<Trackproperties> endStateVectors = returnVector.getA().getB();

					PrevFrameparam = NewFrameparam;

					Allstart.add(startStateVectors);
					Allend.add(endStateVectors);
				}

				if (showKalman) {
					NewFrameparamKalman = returnVectorKalman.getB();

					ArrayList<KalmanTrackproperties> startStateVectorsKalman = returnVectorKalman.getA().getA();
					ArrayList<KalmanTrackproperties> endStateVectorsKalman = returnVectorKalman.getA().getB();

					PrevFrameparamKalman = NewFrameparamKalman;

					AllstartKalman.add(startStateVectorsKalman);
					AllendKalman.add(endStateVectorsKalman);
				}
			}

			if (showDeterministic) {

				
				

				
			
				if (Trackstart) {
					ImagePlus impstart = ImageJFunctions.show(originalimg);
					ImagePlus impstartsec = ImageJFunctions.show(originalimg);
					final Trackstart trackerstart = new Trackstart(Allstart, thirdDimensionSize - next);
					trackerstart.process();
					SimpleWeightedGraph<double[], DefaultWeightedEdge> graphstart = trackerstart.getResult();
					ArrayList<Pair<Integer, double[]>> ID = trackerstart.getSeedID();
					DisplayGraph displaygraphtrackstart = new DisplayGraph(impstartsec, graphstart, ID);
					displaygraphtrackstart.getImp();
					impstartsec.draw();
					ArrayList<Subgraphs> subgraphstart = trackerstart.getFramedgraph();
					DisplaysubGraphstart displaytrackstart = new DisplaysubGraphstart(impstart, subgraphstart,
							next - 1);
					displaytrackstart.getImp();
					impstart.draw();
				}
				if (Trackstart == false) {
					ImagePlus impendsec = ImageJFunctions.show(originalPreprocessedimg);
					final Trackend trackerend = new Trackend(Allend, thirdDimensionSize - next);
					
					ImagePlus impend = ImageJFunctions.show(originalPreprocessedimg);
					trackerend.process();
					SimpleWeightedGraph<double[], DefaultWeightedEdge> graphend = trackerend.getResult();
					ArrayList<Pair<Integer, double[]>> ID = trackerend.getSeedID();
					DisplayGraph displaygraphtrackend = new DisplayGraph(impendsec, graphend, ID);
					displaygraphtrackend.getImp();
					impendsec.draw();
					ArrayList<Subgraphs> subgraphend = trackerend.getFramedgraph();
					DisplaysubGraphend displaytrackend = new DisplaysubGraphend(impend, subgraphend, next - 1);
					displaytrackend.getImp();
					impend.draw();
				}

				

				
			}

			if (showKalman) {

				ResultsTable rtAll = new ResultsTable();
				if (Trackstart) {
					MTtrackerstart = new KFsearch(AllstartKalman, UserchosenCostFunction, maxSearchradius,
							initialSearchradius, thirdDimension, thirdDimensionSize, missedframes);
					MTtrackerstart.reset();
					MTtrackerstart.process();

					SimpleWeightedGraph<KalmanTrackproperties, DefaultWeightedEdge> graphstartKalman = MTtrackerstart
							.getResult();

					ImagePlus impstartKalman = ImageJFunctions.show(originalimg);
					impstartKalman.setTitle("Kalman Start MT");

					DisplayGraphKalman Startdisplaytracks = new DisplayGraphKalman(impstartKalman, graphstartKalman);
					Startdisplaytracks.getImp();

					TrackModel modelstart = new TrackModel(graphstartKalman);
					modelstart.getDirectedNeighborIndex();
					IJ.log(" " + graphstartKalman.vertexSet().size());

					// Get all the track id's
					for (final Integer id : modelstart.trackIDs(true)) {
						ResultsTable rt = new ResultsTable();
						// Get the corresponding set for each id
						modelstart.setName(id, "Track" + id);
						final HashSet<KalmanTrackproperties> Snakeset = modelstart.trackKalmanTrackpropertiess(id);
						ArrayList<KalmanTrackproperties> list = new ArrayList<KalmanTrackproperties>();

						Comparator<KalmanTrackproperties> ThirdDimcomparison = new Comparator<KalmanTrackproperties>() {

							@Override
							public int compare(final KalmanTrackproperties A, final KalmanTrackproperties B) {

								return A.thirdDimension - B.thirdDimension;

							}

						};

						Iterator<KalmanTrackproperties> Snakeiter = Snakeset.iterator();

						while (Snakeiter.hasNext()) {

							KalmanTrackproperties currentsnake = Snakeiter.next();

							list.add(currentsnake);

						}
						Collections.sort(list, ThirdDimcomparison);

						final double[] originalpoint = list.get(0).originalpoint;
						double startlength = 0;
						double startlengthpixel = 0;

						for (int index = 1; index < list.size() - 1; ++index) {

							final double[] currentpoint = list.get(index).currentpoint;
							final double[] oldpoint = list.get(index - 1).currentpoint;
							final double[] currentpointCal = new double[] { currentpoint[0] * calibration[0],
									currentpoint[1] * calibration[1] };
							final double[] oldpointCal = new double[] { oldpoint[0] * calibration[0],
									oldpoint[1] * calibration[1] };
							final double lengthpixel = util.Boundingboxes.Distance(currentpoint, oldpoint);
							final double length = util.Boundingboxes.Distance(currentpointCal, oldpointCal);
							final double seedtocurrent = util.Boundingboxes.Distancesq(originalpoint, currentpoint);
							final double seedtoold = util.Boundingboxes.Distancesq(originalpoint, oldpoint);
							final boolean shrink = seedtoold > seedtocurrent ? true : false;
							final boolean growth = seedtoold > seedtocurrent ? false : true;

							if (shrink && startlengthpixel - lengthpixel > -minlength) {
								// MT shrank

								startlength -= length;
								startlengthpixel -= lengthpixel;

							}
							if (growth) {

								// MT grew
								startlength += length;
								startlengthpixel += lengthpixel;

							}

							rt.incrementCounter();

							rt.addValue("FrameNumber", list.get(index).thirdDimension);
							rt.addValue("Track iD", id);
							rt.addValue("PreviousPosition X (px units)", oldpoint[0]);
							rt.addValue("PreviousPosition Y (px units)", oldpoint[1]);
							rt.addValue("CurrentPosition X (px units)", currentpoint[0]);
							rt.addValue("CurrentPosition Y (px units)", currentpoint[1]);
							rt.addValue("PreviousPosition X (real units)", oldpointCal[0]);
							rt.addValue("PreviousPosition Y (real units)", oldpointCal[1]);
							rt.addValue("CurrentPosition X (real units)", currentpointCal[0]);
							rt.addValue("CurrentPosition Y (real units)", currentpointCal[1]);
							rt.addValue("Length in real units", length);
							rt.addValue("Cummulative Length in real units", startlength);
							double[] landt = { startlengthpixel, list.get(index).thirdDimension };
							lengthtimestart.add(landt);
							rtAll.incrementCounter();

							rtAll.addValue("FrameNumber", list.get(index).thirdDimension);
							rtAll.addValue("Track iD", id);
							rtAll.addValue("PreviousPosition X (px units)", oldpoint[0]);
							rtAll.addValue("PreviousPosition Y (px units)", oldpoint[1]);
							rtAll.addValue("CurrentPosition X (px units)", currentpoint[0]);
							rtAll.addValue("CurrentPosition Y (px units)", currentpoint[1]);
							rtAll.addValue("PreviousPosition X (real units)", oldpointCal[0]);
							rtAll.addValue("PreviousPosition Y (real units)", oldpointCal[1]);
							rtAll.addValue("CurrentPosition X (real units)", currentpointCal[0]);
							rtAll.addValue("CurrentPosition Y (real units)", currentpointCal[1]);
							rtAll.addValue("Length in real units", length);
							rtAll.addValue("Cummulative Length in real units", startlength);

						}

						if (SaveXLS )
							saveResultsToExcel(usefolder + "//" + addTrackToName + "KalmanStart" + id + ".xls", rt);

					}
				}

				if (Trackstart == false) {
					MTtrackerend = new KFsearch(AllendKalman, UserchosenCostFunction, maxSearchradius,
							initialSearchradius, thirdDimension, thirdDimensionSize, missedframes);

					MTtrackerend.reset();
					MTtrackerend.process();
					SimpleWeightedGraph<KalmanTrackproperties, DefaultWeightedEdge> graphendKalman = MTtrackerend
							.getResult();

					ImagePlus impendKalman = ImageJFunctions.show(originalPreprocessedimg);
					impendKalman.setTitle("Kalman End MT");

					IJ.log("KalmanTracking Complete " + " " + "Displaying results");

					DisplayGraphKalman Enddisplaytracks = new DisplayGraphKalman(impendKalman, graphendKalman);
					Enddisplaytracks.getImp();
					TrackModel modelend = new TrackModel(graphendKalman);
					modelend.getDirectedNeighborIndex();
					IJ.log(" " + graphendKalman.vertexSet().size());
					// Get all the track id's
					for (final Integer id : modelend.trackIDs(true)) {
						ResultsTable rt = new ResultsTable();
						// Get the corresponding set for each id
						modelend.setName(id, "Track" + id);
						final HashSet<KalmanTrackproperties> Snakeset = modelend.trackKalmanTrackpropertiess(id);
						ArrayList<KalmanTrackproperties> list = new ArrayList<KalmanTrackproperties>();

						Comparator<KalmanTrackproperties> ThirdDimcomparison = new Comparator<KalmanTrackproperties>() {

							@Override
							public int compare(final KalmanTrackproperties A, final KalmanTrackproperties B) {

								return A.thirdDimension - B.thirdDimension;

							}

						};

						Iterator<KalmanTrackproperties> Snakeiter = Snakeset.iterator();

						while (Snakeiter.hasNext()) {

							KalmanTrackproperties currentsnake = Snakeiter.next();

							list.add(currentsnake);

						}
						Collections.sort(list, ThirdDimcomparison);

						double endlength = 0;
						double endlengthpixel = 0;
						final double[] originalpoint = list.get(0).originalpoint;

						for (int index = 1; index < list.size() - 1; ++index) {

							final double[] currentpoint = list.get(index).currentpoint;
							final double[] oldpoint = list.get(index - 1).currentpoint;
							final double[] currentpointCal = new double[] { currentpoint[0] * calibration[0],
									currentpoint[1] * calibration[1] };
							final double[] oldpointCal = new double[] { oldpoint[0] * calibration[0],
									oldpoint[1] * calibration[1] };
							final double lengthpixel = util.Boundingboxes.Distance(currentpoint, oldpoint);
							final double length = util.Boundingboxes.Distance(currentpointCal, oldpointCal);
							final double seedtocurrent = util.Boundingboxes.Distancesq(originalpoint, currentpoint);
							final double seedtoold = util.Boundingboxes.Distancesq(originalpoint, oldpoint);
							final boolean shrink = seedtoold > seedtocurrent ? true : false;
							final boolean growth = seedtoold > seedtocurrent ? false : true;

							if (shrink && endlengthpixel - lengthpixel > -minlength) {

								// MT shrank
								endlength -= length;
								endlengthpixel -= lengthpixel;

							}
							if (growth) {

								// MT grew
								endlength += length;
								endlengthpixel += lengthpixel;

							}

							rt.incrementCounter();

							rt.addValue("FrameNumber", list.get(index).thirdDimension);
							rt.addValue("Track iD", id);
							rt.addValue("PreviousPosition X (px units)", oldpoint[0]);
							rt.addValue("PreviousPosition Y (px units)", oldpoint[1]);
							rt.addValue("CurrentPosition X (px units)", currentpoint[0]);
							rt.addValue("CurrentPosition Y (px units)", currentpoint[1]);
							rt.addValue("PreviousPosition X (real units)", oldpointCal[0]);
							rt.addValue("PreviousPosition Y (real units)", oldpointCal[1]);
							rt.addValue("CurrentPosition X (real units)", currentpointCal[0]);
							rt.addValue("CurrentPosition Y (real units)", currentpointCal[1]);
							rt.addValue("Length in real units", length);
							rt.addValue("Cummulative Length in real units", endlength);

							double[] landt = { endlengthpixel, list.get(index).thirdDimension };
							lengthtimeend.add(landt);
							rtAll.incrementCounter();

							rtAll.addValue("FrameNumber", list.get(index).thirdDimension);
							rtAll.addValue("Track iD", id);
							rtAll.addValue("PreviousPosition X (px units)", oldpoint[0]);
							rtAll.addValue("PreviousPosition Y (px units)", oldpoint[1]);
							rtAll.addValue("CurrentPosition X (px units)", currentpoint[0]);
							rtAll.addValue("CurrentPosition Y (px units)", currentpoint[1]);
							rtAll.addValue("PreviousPosition X (real units)", oldpointCal[0]);
							rtAll.addValue("PreviousPosition Y (real units)", oldpointCal[1]);
							rtAll.addValue("CurrentPosition X (real units)", currentpointCal[0]);
							rtAll.addValue("CurrentPosition Y (real units)", currentpointCal[1]);
							rtAll.addValue("Length in real units", length);
							rtAll.addValue("Cummulative Length in real units", endlength);

						}

						if (SaveXLS )
							saveResultsToExcel(usefolder + "//" + addTrackToName + "KalmanEnd" + id + ".xls", rt);

					}

					rtAll.show("Results");

				}
			}
			if (showDeterministic) {
				NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
				nf.setMaximumFractionDigits(3);

				ResultsTable rtAll = new ResultsTable();
				if (Trackstart) {
					final ArrayList<Trackproperties> first = Allstart.get(0);
					int MaxSeedLabel = first.get(first.size() - 1).seedlabel;
					int MinSeedLabel = first.get(0).seedlabel;

					ArrayList<Pair<Integer[], double[]>> lengthliststart = new ArrayList<Pair<Integer[], double[]>>();
					for (int currentseed = MinSeedLabel; currentseed < MaxSeedLabel + 1; ++currentseed) {
						double startlength = 0;
						double startlengthpixel = 0;

						for (int index = 0; index < Allstart.size(); ++index) {

							final ArrayList<Trackproperties> thirdDimension = Allstart.get(index);

							for (int frameindex = 0; frameindex < thirdDimension.size(); ++frameindex) {

								final Integer SeedID = thirdDimension.get(frameindex).seedlabel;
								final int framenumber =  thirdDimension.get(frameindex).Framenumber;
								if (SeedID == currentseed) {

									final Integer[] FrameID = { framenumber, SeedID };
									final double[] originalpoint = thirdDimension.get(frameindex).originalpoint;
									final double[] newpoint = thirdDimension.get(frameindex).newpoint;
									final double[] oldpoint = thirdDimension.get(frameindex).oldpoint;
									final double[] newpointCal = new double[] {
											thirdDimension.get(frameindex).newpoint[0] * calibration[0],
											thirdDimension.get(frameindex).newpoint[1] * calibration[1] };
									final double[] oldpointCal = new double[] {
											thirdDimension.get(frameindex).oldpoint[0] * calibration[0],
											thirdDimension.get(frameindex).oldpoint[1] * calibration[1] };

									final double length = util.Boundingboxes.Distance(newpointCal, oldpointCal);
									final double lengthpixel = util.Boundingboxes.Distance(newpoint, oldpoint);
									final double seedtocurrent = util.Boundingboxes.Distancesq(originalpoint, newpoint);
									final double seedtoold = util.Boundingboxes.Distancesq(originalpoint, oldpoint);
									final boolean shrink = seedtoold > seedtocurrent ? true : false;
									final boolean growth = seedtoold > seedtocurrent ? false : true;

									if (shrink && startlengthpixel - lengthpixel > -minlength) {
										// MT shrank

										startlength -= length;
										startlengthpixel -= lengthpixel;

									}
									if (growth) {

										// MT grew
										startlength += length;
										startlengthpixel += lengthpixel;

									}

									final double[] startinfo = { oldpoint[0], oldpoint[1], newpoint[0], newpoint[1],
											oldpointCal[0], oldpointCal[1], newpointCal[0], newpointCal[1], length,
											startlength, lengthpixel, startlengthpixel };
									Pair<Integer[], double[]> lengthpair = new ValuePair<Integer[], double[]>(FrameID,
											startinfo);

									lengthliststart.add(lengthpair);

								}
							}

						}

					}

					for (int seedID = MinSeedLabel; seedID <= MaxSeedLabel; ++seedID) {
						ResultsTable rt = new ResultsTable();
						if (SaveTxt) {
							try {
								File fichier = new File(
										usefolder + "//" + addTrackToName + "SeedLabel" + seedID + "-start" + ".txt");
								File fichierMy = new File(
										usefolder + "//" + addToName + "KymoVarun-start" + seedID + ".txt");
								FileWriter fw = new FileWriter(fichier);
								BufferedWriter bw = new BufferedWriter(fw);

								bw.write(
										"\tFramenumber\tSeedLabel\tOldX (px)\tOldY (px)\tNewX (px)\tNewY (px)\tOldX (real)\tOldY (real)"
												+ "\tNewX (real)\tNewY (real)"
												+ "\tLength ( real)\tCummulativeLength (real)\n");

								FileWriter fwmy = new FileWriter(fichierMy);
								BufferedWriter bwmy = new BufferedWriter(fwmy);

								bwmy.write("\tFramenumber\tLength\n");

								for (int index = 0; index < Allstart.size(); ++index) {
									if (lengthliststart.get(index).getA()[1] == seedID) {
										bw.write("\t" + lengthliststart.get(index).getA()[0] + "\t" + "\t"
												+ nf.format(lengthliststart.get(index).getA()[1]) + "\t" + "\t"
												+ nf.format(lengthliststart.get(index).getB()[0]) + "\t" + "\t"
												+ nf.format(lengthliststart.get(index).getB()[1]) + "\t" + "\t"
												+ nf.format(lengthliststart.get(index).getB()[2]) + "\t" + "\t"
												+ nf.format(lengthliststart.get(index).getB()[3]) + "\t" + "\t"
												+ nf.format(lengthliststart.get(index).getB()[4]) + "\t" + "\t"
												+ nf.format(lengthliststart.get(index).getB()[5]) + "\t" + "\t"
												+ nf.format(lengthliststart.get(index).getB()[6]) + "\t" + "\t"
												+ nf.format(lengthliststart.get(index).getB()[7]) + "\t" + "\t"
												+ nf.format(lengthliststart.get(index).getB()[8]) + "\t" + "\t"
												+ nf.format(lengthliststart.get(index).getB()[9]) + "\n");

										bwmy.write("\t" + lengthliststart.get(index).getA()[0] + "\t" + "\t"
												+ nf.format(lengthliststart.get(index).getB()[11]) + "\n");

									}
								}
								bw.close();
								fw.close();
								bwmy.close();
								fwmy.close();

							} catch (IOException e) {
							}
						}

						for (int index = 0; index < Allstart.size(); ++index) {
							if (lengthliststart.get(index).getA()[1] == seedID) {
								rt.incrementCounter();
								rt.addValue("FrameNumber", lengthliststart.get(index).getA()[0]);
								rt.addValue("SeedLabel", lengthliststart.get(index).getA()[1]);
								rt.addValue("OldX in px units", (float) lengthliststart.get(index).getB()[0]);
								rt.addValue("OldY in px units", (float) lengthliststart.get(index).getB()[1]);
								rt.addValue("NewX in px units", (float) lengthliststart.get(index).getB()[2]);
								rt.addValue("NewY in px units", (float) lengthliststart.get(index).getB()[3]);
								rt.addValue("OldX in real units", (float) lengthliststart.get(index).getB()[4]);
								rt.addValue("OldY in real units", (float) lengthliststart.get(index).getB()[5]);
								rt.addValue("NewX in real units", (float) lengthliststart.get(index).getB()[6]);
								rt.addValue("NewY in real units", (float) lengthliststart.get(index).getB()[7]);
								rt.addValue("Length in real units", (float) lengthliststart.get(index).getB()[8]);
								rt.addValue("Cummulative Length in real units",
										(float) lengthliststart.get(index).getB()[9]);
								double[] landt = { lengthliststart.get(index).getB()[11],
										lengthliststart.get(index).getA()[0] };
								lengthtimestart.add(landt);

								rtAll.incrementCounter();
								rtAll.addValue("FrameNumber", lengthliststart.get(index).getA()[0]);
								rtAll.addValue("SeedLabel", lengthliststart.get(index).getA()[1]);
								rtAll.addValue("OldX in px units", lengthliststart.get(index).getB()[0]);
								rtAll.addValue("OldY in px units", lengthliststart.get(index).getB()[1]);
								rtAll.addValue("NewX in px units", lengthliststart.get(index).getB()[2]);
								rtAll.addValue("NewY in px units", lengthliststart.get(index).getB()[3]);
								rtAll.addValue("OldX in real units", lengthliststart.get(index).getB()[4]);
								rtAll.addValue("OldY in real units", lengthliststart.get(index).getB()[5]);
								rtAll.addValue("NewX in real units", lengthliststart.get(index).getB()[6]);
								rtAll.addValue("NewY in real units", lengthliststart.get(index).getB()[7]);
								rtAll.addValue("Length in real units", lengthliststart.get(index).getB()[8]);
								rtAll.addValue("Cummulative Length in real units", lengthliststart.get(index).getB()[9]);

							}

						}

						if (SaveXLS)
							saveResultsToExcel(
									usefolder + "//" + addTrackToName + "start" + "SeedLabel" + seedID + ".xls", rt);

					}
				}

				if (Trackstart == false) {
					ArrayList<Pair<Integer[], double[]>> lengthlistend = new ArrayList<Pair<Integer[], double[]>>();
					final ArrayList<Trackproperties> first = Allend.get(0);
					int MaxSeedLabel = first.get(first.size() - 1).seedlabel;
					int MinSeedLabel = first.get(0).seedlabel;
					for (int currentseed = MinSeedLabel; currentseed < MaxSeedLabel + 1; ++currentseed) {
						double endlength = 0;
						double endlengthpixel = 0;
						for (int index = 0; index < Allend.size(); ++index) {

							final int framenumber = index;
							final ArrayList<Trackproperties> thirdDimension = Allend.get(index);

							for (int frameindex = 0; frameindex < thirdDimension.size(); ++frameindex) {

								final Integer SeedID = thirdDimension.get(frameindex).seedlabel;

								if (SeedID == currentseed) {

									final Integer[] FrameID = { framenumber, SeedID };
									final double[] originalpoint = thirdDimension.get(frameindex).originalpoint;
									final double[] newpoint = thirdDimension.get(frameindex).newpoint;
									final double[] oldpoint = thirdDimension.get(frameindex).oldpoint;

									final double[] newpointCal = new double[] {
											thirdDimension.get(frameindex).newpoint[0] * calibration[0],
											thirdDimension.get(frameindex).newpoint[1] * calibration[1] };
									final double[] oldpointCal = new double[] {
											thirdDimension.get(frameindex).oldpoint[0] * calibration[0],
											thirdDimension.get(frameindex).oldpoint[1] * calibration[1] };

									final double length = util.Boundingboxes.Distance(newpointCal, oldpointCal);
									final double lengthpixel = util.Boundingboxes.Distance(newpoint, oldpoint);
									final double seedtocurrent = util.Boundingboxes.Distancesq(originalpoint, newpoint);
									final double seedtoold = util.Boundingboxes.Distancesq(originalpoint, oldpoint);
									final boolean shrink = seedtoold > seedtocurrent ? true : false;
									final boolean growth = seedtoold > seedtocurrent ? false : true;

									if (shrink) {

										// MT shrank

										endlength -= length;
										endlengthpixel -= lengthpixel;

									}

									if (growth) {

										// MT grew

										endlength += length;
										endlengthpixel += lengthpixel;

									}

									final double[] endinfo = { oldpoint[0], oldpoint[1], newpoint[0], newpoint[1],
											oldpointCal[0], oldpointCal[1], newpointCal[0], newpointCal[1], length,
											endlength, lengthpixel, endlengthpixel };
									Pair<Integer[], double[]> lengthpair = new ValuePair<Integer[], double[]>(FrameID,
											endinfo);

									lengthlistend.add(lengthpair);

								}

							}

						}

					}

					for (int seedID = MinSeedLabel; seedID <= MaxSeedLabel; ++seedID) {
						ResultsTable rtend = new ResultsTable();
						if (SaveTxt) {
							try {
								File fichier = new File(
										usefolder + "//" + addTrackToName + "SeedLabel" + seedID + "-end" + ".txt");

								File fichierMy = new File(
										usefolder + "//" + addToName + "KymoVarun-end" + seedID + ".txt");
								File Rates = new File(usefolder + "//" + addToName + "Rates" + seedID + ".txt");

								FileWriter fw = new FileWriter(fichier);
								BufferedWriter bw = new BufferedWriter(fw);

								FileWriter fr = new FileWriter(Rates);
								BufferedWriter br = new BufferedWriter(fr);

								br.write("\tStartframe\tEndframe\trate\tShrink\n");

								bw.write(
										"\tFramenumber\tSeedLabel\tOldX (px)\tOldY (px)\tNewX (px)\tNewY (px)\tOldX (real)\tOldY (real)"
												+ "\tNewX (real)\tNewY (real)"
												+ "\tLength ( real)\tCummulativeLength (real)\n");

								FileWriter fwmy = new FileWriter(fichierMy);
								BufferedWriter bwmy = new BufferedWriter(fwmy);

								bwmy.write("\tFramenumber\tLength\n");
								for (int index = 0; index < Allend.size(); ++index) {
									if (lengthlistend.get(index).getA()[1] == seedID) {
										bw.write("\t" + lengthlistend.get(index).getA()[0] + "\t" + "\t"
												+ nf.format(lengthlistend.get(index).getA()[1]) + "\t" + "\t"
												+ nf.format(lengthlistend.get(index).getB()[0]) + "\t" + "\t"
												+ nf.format(lengthlistend.get(index).getB()[1]) + "\t" + "\t"
												+ nf.format(lengthlistend.get(index).getB()[2]) + "\t" + "\t"
												+ nf.format(lengthlistend.get(index).getB()[3]) + "\t" + "\t"
												+ nf.format(lengthlistend.get(index).getB()[4]) + "\t" + "\t"
												+ nf.format(lengthlistend.get(index).getB()[5]) + "\t" + "\t"
												+ nf.format(lengthlistend.get(index).getB()[6]) + "\t" + "\t"
												+ nf.format(lengthlistend.get(index).getB()[7]) + "\t" + "\t"
												+ nf.format(lengthlistend.get(index).getB()[8]) + "\t" + "\t"
												+ nf.format(lengthlistend.get(index).getB()[9]) + "\n");

										bwmy.write("\t" + lengthlistend.get(index).getA()[0] + "\t" + "\t"
												+ nf.format(lengthlistend.get(index).getB()[11]) + "\n");

									}

								}
								bwmy.close();
								fwmy.close();
								bw.close();
								fw.close();
								br.close();
								fr.close();
							} catch (IOException e) {
							}
						}

						for (int index = 0; index < Allend.size(); ++index) {

							if (lengthlistend.get(index).getA()[1] == seedID) {
								rtend.incrementCounter();
								rtend.addValue("FrameNumber", lengthlistend.get(index).getA()[0]);
								rtend.addValue("SeedLabel", lengthlistend.get(index).getA()[1]);
								rtend.addValue("OldX in px units", (float) lengthlistend.get(index).getB()[0]);
								rtend.addValue("OldY in px units", (float) lengthlistend.get(index).getB()[1]);
								rtend.addValue("NewX in px units", (float) lengthlistend.get(index).getB()[2]);
								rtend.addValue("NewY in px units", (float) lengthlistend.get(index).getB()[3]);
								rtend.addValue("OldX in real units", (float) lengthlistend.get(index).getB()[4]);
								rtend.addValue("OldY in real units", (float) lengthlistend.get(index).getB()[5]);
								rtend.addValue("NewX in real units", (float) lengthlistend.get(index).getB()[6]);
								rtend.addValue("NewY in real units", (float) lengthlistend.get(index).getB()[7]);
								rtend.addValue("Length in real units", (float) lengthlistend.get(index).getB()[8]);
								rtend.addValue("Cummulative Length in real units",
										(float) lengthlistend.get(index).getB()[9]);

								double[] landt = { lengthlistend.get(index).getB()[11], lengthlistend.get(index).getA()[0] };
								lengthtimeend.add(landt);
								rtAll.incrementCounter();
								rtAll.addValue("FrameNumber", lengthlistend.get(index).getA()[0]);
								rtAll.addValue("SeedLabel", lengthlistend.get(index).getA()[1]);
								rtAll.addValue("OldX in px units", (float) lengthlistend.get(index).getB()[0]);
								rtAll.addValue("OldY in px units", (float) lengthlistend.get(index).getB()[1]);
								rtAll.addValue("NewX in px units", (float) lengthlistend.get(index).getB()[2]);
								rtAll.addValue("NewY in px units", (float) lengthlistend.get(index).getB()[3]);
								rtAll.addValue("OldX in real units", (float) lengthlistend.get(index).getB()[4]);
								rtAll.addValue("OldY in real units", (float) lengthlistend.get(index).getB()[5]);
								rtAll.addValue("NewX in real units", (float) lengthlistend.get(index).getB()[6]);
								rtAll.addValue("NewY in real units", (float) lengthlistend.get(index).getB()[7]);
								rtAll.addValue("Length in real units", (float) lengthlistend.get(index).getB()[8]);
								rtAll.addValue("Cummulative Length in real units",
										(float) lengthlistend.get(index).getB()[9]);

							}

						}

						if (SaveXLS)
							saveResultsToExcel(
									usefolder + "//" + addTrackToName + "end" + "SeedLabel" + seedID + ".xls", rtend);

					}
				}
				rtAll.show("Start and End of MT");
				if (Trackstart)
					lengthtime = lengthtimestart;
				else
					lengthtime = lengthtimeend;
				if (analyzekymo) {
					double lengthcheckstart = 0;
					double lengthcheckend = 0;
					if (Trackstart) {

						for (int index = 0; index < lengthtimestart.size(); ++index) {

							int time = (int) lengthtimestart.get(index)[1];

							lengthcheckstart += lengthtimestart.get(index)[0];

							for (int secindex = 0; secindex < Length.size(); ++secindex) {

								for (int accountindex = 0; accountindex < Accountedframes.size(); ++accountindex) {

									if ((int) Length.get(secindex)[1] == time
											&& Accountedframes.get(accountindex) == time) {

										float delta = (float) (lengthtimestart.get(index)[0] - Length.get(secindex)[0]);
										float[] cudeltadeltaLstart = { delta, time };
										deltadstart.add(cudeltadeltaLstart);

									}
								}

							}
						}

						/********
						 * The part below removes the duplicate entries in the
						 * array dor the time co-ordinate
						 ********/

						int j = 0;

						for (int index = 0; index < deltadstart.size() - 1; ++index) {

							j = index + 1;

							while (j < deltadstart.size()) {

								if (deltadstart.get(index)[1] == deltadstart.get(j)[1]) {

									deltadstart.remove(index);
								}

								else {
									++j;

								}

							}
						}

						for (int index = 0; index < deltadstart.size(); ++index) {

							for (int secindex = 0; secindex < Accountedframes.size(); ++secindex) {

								if ((int) deltadstart.get(index)[1] == Accountedframes.get(secindex)) {

									netdeltadstart += Math.abs(deltadstart.get(index)[0]);

								}

							}

						}
						deltad = deltadstart;
						lengthtime = lengthtimestart;

					}

					if (Trackstart == false) {
						for (int index = 0; index < lengthtimeend.size(); ++index) {

							int time = (int) lengthtimeend.get(index)[1];

							lengthcheckend += lengthtimeend.get(index)[0];

							for (int secindex = 0; secindex < Length.size(); ++secindex) {

								for (int accountindex = 0; accountindex < Accountedframes.size(); ++accountindex) {

									if ((int) Length.get(secindex)[1] == time
											&& Accountedframes.get(accountindex) == time) {

										if ((int) Length.get(secindex)[1] == time
												&& Accountedframes.get(accountindex) == time) {

											float delta = (float) (lengthtimeend.get(index)[0]
													- Length.get(secindex)[0]);
											float[] cudeltadeltaLend = { delta, time };
											deltadend.add(cudeltadeltaLend);
										}
									}

								}

							}
						}
						/********
						 * The part below removes the duplicate entries in the
						 * array dor the time co-ordinate
						 ********/

						int j = 0;

						for (int index = 0; index < deltadend.size() - 1; ++index) {

							j = index + 1;

							while (j < deltadend.size()) {

								if (deltadend.get(index)[1] == deltadend.get(j)[1]) {

									deltadend.remove(index);
								}

								else {
									++j;

								}

							}
						}

						for (int index = 0; index < deltadend.size(); ++index) {

							for (int secindex = 0; secindex < Accountedframes.size(); ++secindex) {

								if ((int) deltadend.get(index)[1] == Accountedframes.get(secindex)) {

									netdeltadend += Math.abs(deltadend.get(index)[0]);

								}

							}

						}

						deltad = deltadend;
						lengthtime = lengthtimeend;
					}

					if (lengthtimestart.size() > 0 && lengthtimeend.size() > 0) {
						deltad = (netdeltadstart >= netdeltadend) ? deltadend : deltadstart;
						lengthtime = (netdeltadstart >= netdeltadend) ? lengthtimeend : lengthtimestart;
					}

					FileWriter deltaw;
					File fichierKydel = new File(usefolder + "//" + addToName + "MTtracker-deltad" + ".txt");

					try {
						deltaw = new FileWriter(fichierKydel);
						BufferedWriter bdeltaw = new BufferedWriter(deltaw);

						bdeltaw.write("\ttime\tDeltad(pixel units)\n");

						for (int index = 0; index < deltad.size(); ++index) {
							bdeltaw.write("\t" + deltad.get(index)[1] + "\t" + deltad.get(index)[0] + "\n");

						}

						bdeltaw.close();
						deltaw.close();
					}

					catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					for (int index = 0; index < deltad.size(); ++index) {

						for (int secindex = 0; secindex < Accountedframes.size(); ++secindex) {

							if ((int) deltad.get(index)[1] == Accountedframes.get(secindex)) {

								netdeltad += Math.abs(deltad.get(index)[0]);

							}

						}

					}
					netdeltad /= deltad.size();

					if (netdeltad > deltadcutoff) {

						redo = true;

					} else
						redo = false;

				}
			}
			if (Kymoimg != null) {
				ImagePlus newimp = Kymoimp.duplicate();
				for (int index = 0; index < lengthtime.size() - 1; ++index) {

					Overlay overlay = Kymoimp.getOverlay();
					if (overlay == null) {
						overlay = new Overlay();
						Kymoimp.setOverlay(overlay);
					}
					Line newline = new Line(lengthtime.get(index)[0], lengthtime.get(index)[1],
							lengthtime.get(index + 1)[0], lengthtime.get(index + 1)[1]);
					newline.setFillColor(colorDraw);

					overlay.add(newline);

					Kymoimp.setOverlay(overlay);
					RoiManager roimanager = RoiManager.getInstance();

					roimanager.addRoi(newline);

				}
				
				Kymoimp.show();
			}
			if (displayoverlay)
				new ImagePlus("Overlays", prestack).show();
			return null;
		}

		@Override
		protected void done() {
			try {
				jpb.setIndeterminate(false);
				get();
				frame.dispose();
				JOptionPane.showMessageDialog(jpb.getParent(), "Success", "Success", JOptionPane.INFORMATION_MESSAGE);
			} catch (ExecutionException | InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

	protected class TrackendsListener implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent arg0) {

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {

					goTrack();

				}

			});

		}
	}

	public void saveResultsToExcel(String xlFile, ResultsTable rt) {

		FileOutputStream xlOut = null;
		try {

			xlOut = new FileOutputStream(xlFile);
		} catch (FileNotFoundException ex) {

			Logger.getLogger(Interactive_MT.class.getName()).log(Level.SEVERE, null, ex);
		}

		HSSFWorkbook xlBook = new HSSFWorkbook();
		HSSFSheet xlSheet = xlBook.createSheet("Results Object Tracker");

		HSSFRow r = null;
		HSSFCell c = null;
		HSSFCellStyle cs = xlBook.createCellStyle();
		HSSFCellStyle cb = xlBook.createCellStyle();
		HSSFFont f = xlBook.createFont();
		HSSFFont fb = xlBook.createFont();
		HSSFDataFormat df = xlBook.createDataFormat();
		f.setFontHeightInPoints((short) 12);
		fb.setFontHeightInPoints((short) 12);
		fb.setBoldweight((short) Font.BOLD);
		cs.setFont(f);
		cb.setFont(fb);
		cs.setDataFormat(df.getFormat("#,##0.000"));
		cb.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));
		cb.setFont(fb);

		int numRows = rt.size();
		String[] colHeaders = rt.getHeadings();
		int rownum = 0;

		// Create a Header
		r = xlSheet.createRow(rownum);

		for (int cellnum = 0; cellnum < colHeaders.length; cellnum++) {

			c = r.createCell((short) cellnum);
			c.setCellStyle(cb);
			c.setCellValue(colHeaders[cellnum]);

		}
		rownum++;

		for (int row = 0; row < numRows; row++) {

			r = xlSheet.createRow(rownum + row);
			int numCols = rt.getLastColumn() + 1;

			for (int cellnum = 0; cellnum < numCols; cellnum++) {

				c = r.createCell((short) cellnum);

				c.setCellValue(rt.getValueAsDouble(cellnum, row));

			}

		}

		try {
			xlBook.write(xlOut);
			xlOut.close();
		} catch (IOException ex) {
			Logger.getLogger(Interactive_MT.class.getName()).log(Level.SEVERE, null, ex);

		}

	}

	protected class UpdateHoughListener implements ItemListener {
		@Override
		public void itemStateChanged(final ItemEvent arg0) {
			boolean oldState = FindLinesViaHOUGH;

			if (arg0.getStateChange() == ItemEvent.DESELECTED)
				FindLinesViaHOUGH = false;
			else if (arg0.getStateChange() == ItemEvent.SELECTED) {
				FindLinesViaMSER = false;
				FindLinesViaHOUGH = true;
				FindLinesViaMSERwHOUGH = false;
				// UpdateHough();

				final GridBagLayout layout = new GridBagLayout();
				final GridBagConstraints c = new GridBagConstraints();
				panelFifth.removeAll();

				panelFifth.setLayout(layout);

				final Label exthresholdText = new Label("threshold = threshold to create Bitimg for watershedding.",
						Label.CENTER);
				final Label exthetaText = new Label("thetaPerPixel = Pixel Size in theta direction for Hough space.",
						Label.CENTER);
				final Label exrhoText = new Label("rhoPerPixel = Pixel Size in rho direction for Hough space.",
						Label.CENTER);

				// IJ.log("Determining the initial threshold for the image");
				// thresholdHoughInit =
				// GlobalThresholding.AutomaticThresholding(currentPreprocessedimg);
				final Scrollbar threshold = new Scrollbar(Scrollbar.HORIZONTAL, (int) thresholdHoughInit, 10, 0,
						10 + scrollbarSize);
				thresholdHough = computeValueFromScrollbarPosition((int) thresholdHoughInit, thresholdHoughMin,
						thresholdHoughMax, scrollbarSize);

				final Scrollbar thetaSize = new Scrollbar(Scrollbar.HORIZONTAL, (int) thetaPerPixelInit, 10, 0,
						10 + scrollbarSize);
				thetaPerPixel = computeValueFromScrollbarPosition((int) thetaPerPixelInit, thetaPerPixelMin,
						thetaPerPixelMax, scrollbarSize);

				final Scrollbar rhoSize = new Scrollbar(Scrollbar.HORIZONTAL, (int) rhoPerPixelInit, 10, 0,
						10 + scrollbarSize);
				rhoPerPixel = computeValueFromScrollbarPosition((int) rhoPerPixelInit, rhoPerPixelMin, rhoPerPixelMax,
						scrollbarSize);

				final Checkbox displayBit = new Checkbox("Display Bitimage ", displayBitimg);
				final Checkbox displayWatershed = new Checkbox("Display Watershedimage ", displayWatershedimg);
				final Label thresholdText = new Label("thresholdValue = ", Label.CENTER);
				final Label thetaText = new Label("Size of Hough Space in Theta = ", Label.CENTER);
				final Label rhoText = new Label("Size of Hough Space in Rho = ", Label.CENTER);
				final Button Dowatershed = new Button("Do watershedding");
				final Label Update = new Label("Update parameters for dynamic channel");
				Update.setBackground(new Color(1, 0, 1));
				Update.setForeground(new Color(255, 255, 255));
				/* Location */
				panelFifth.setLayout(layout);

				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridx = 0;
				c.gridy = 0;
				c.weightx = 4;
				c.weighty = 1.5;

				++c.gridy;
				panelFifth.add(Update, c);

				++c.gridy;
				panelFifth.add(exthresholdText, c);
				++c.gridy;

				panelFifth.add(exthetaText, c);
				++c.gridy;

				panelFifth.add(exrhoText, c);
				++c.gridy;

				panelFifth.add(thresholdText, c);
				++c.gridy;

				panelFifth.add(threshold, c);
				++c.gridy;

				panelFifth.add(thetaText, c);
				++c.gridy;
				panelFifth.add(thetaSize, c);
				++c.gridy;

				panelFifth.add(rhoText, c);

				++c.gridy;

				panelFifth.add(rhoSize, c);

				++c.gridy;
				c.insets = new Insets(10, 175, 0, 175);
				panelFifth.add(displayBit, c);

				++c.gridy;
				c.insets = new Insets(10, 175, 0, 175);
				panelFifth.add(displayWatershed, c);
				++c.gridy;
				c.insets = new Insets(10, 175, 0, 175);
				panelFifth.add(Dowatershed, c);

				threshold.addAdjustmentListener(new thresholdHoughListener(thresholdText, thresholdHoughMin,
						thresholdHoughMax, scrollbarSize, threshold));

				thetaSize.addAdjustmentListener(new thetaSizeHoughListener(thetaText, rhoText, thetaPerPixelMin,
						thetaPerPixelMax, scrollbarSize, thetaSize, rhoSize));

				rhoSize.addAdjustmentListener(
						new rhoSizeHoughListener(rhoText, rhoPerPixelMin, rhoPerPixelMax, scrollbarSize, rhoSize));

				displayBit.addItemListener(new ShowBitimgListener());
				displayWatershed.addItemListener(new ShowwatershedimgListener());
				Dowatershed.addActionListener(new DowatershedListener());
				panelFifth.repaint();
				panelFifth.validate();

			}

		}

	}

	protected class UpdateMserListener implements ItemListener {
		@Override
		public void itemStateChanged(final ItemEvent arg0) {
			boolean oldState = FindLinesViaMSER;

			if (arg0.getStateChange() == ItemEvent.DESELECTED)
				FindLinesViaMSER = false;
			else if (arg0.getStateChange() == ItemEvent.SELECTED) {
				FindLinesViaMSER = true;
				FindLinesViaHOUGH = false;
				FindLinesViaMSERwHOUGH = false;
				// UpdateMSER();
				final GridBagLayout layout = new GridBagLayout();
				final GridBagConstraints c = new GridBagConstraints();
				panelFifth.removeAll();

				panelFifth.setLayout(layout);
				final Scrollbar deltaS = new Scrollbar(Scrollbar.HORIZONTAL, deltaInit, 10, 0, 10 + scrollbarSize);
				final Scrollbar maxVarS = new Scrollbar(Scrollbar.HORIZONTAL, maxVarInit, 10, 0, 10 + scrollbarSize);
				final Scrollbar minDiversityS = new Scrollbar(Scrollbar.HORIZONTAL, minDiversityInit, 10, 0,
						10 + scrollbarSize);
				final Scrollbar minSizeS = new Scrollbar(Scrollbar.HORIZONTAL, minSizeInit, 10, 0, 10 + scrollbarSize);
				final Scrollbar maxSizeS = new Scrollbar(Scrollbar.HORIZONTAL, maxSizeInit, 10, 0, 10 + scrollbarSize);
				maxVar = computeValueFromScrollbarPosition(maxVarInit, maxVarMin, maxVarMax, scrollbarSize);
				delta = computeValueFromScrollbarPosition(deltaInit, deltaMin, deltaMax, scrollbarSize);
				minDiversity = computeValueFromScrollbarPosition(minDiversityInit, minDiversityMin, minDiversityMax,
						scrollbarSize);
				minSize = (int) computeValueFromScrollbarPosition(minSizeInit, minSizemin, minSizemax, scrollbarSize);
				maxSize = (int) computeValueFromScrollbarPosition(maxSizeInit, maxSizemin, maxSizemax, scrollbarSize);

				final Checkbox min = new Checkbox("Look for Minima ", darktobright);

				final Button ComputeTree = new Button("Compute Tree and display");
				/* Location */
				final Label deltaText = new Label("delta = ", Label.CENTER);
				final Label maxVarText = new Label("maxVar = ", Label.CENTER);
				final Label minDiversityText = new Label("minDiversity = ", Label.CENTER);
				final Label minSizeText = new Label("MinSize = ", Label.CENTER);
				final Label maxSizeText = new Label("MaxSize = ", Label.CENTER);
				final Label Update = new Label("Update parameters for dynamic channel");
				Update.setBackground(new Color(1, 0, 1));
				Update.setForeground(new Color(255, 255, 255));
				panelFifth.setLayout(layout);
				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridx = 0;
				c.gridy = 0;
				c.weightx = 4;
				c.weighty = 1.5;
				++c.gridy;
				panelFifth.add(Update, c);

				++c.gridy;
				panelFifth.add(deltaText, c);

				++c.gridy;
				panelFifth.add(deltaS, c);

				++c.gridy;

				panelFifth.add(maxVarText, c);

				++c.gridy;
				panelFifth.add(maxVarS, c);

				++c.gridy;

				panelFifth.add(minDiversityText, c);

				++c.gridy;
				panelFifth.add(minDiversityS, c);

				++c.gridy;

				panelFifth.add(minSizeText, c);

				++c.gridy;
				panelFifth.add(minSizeS, c);

				++c.gridy;

				panelFifth.add(maxSizeText, c);

				++c.gridy;
				panelFifth.add(maxSizeS, c);

				++c.gridy;
				c.insets = new Insets(10, 175, 0, 175);
				panelFifth.add(min, c);

				++c.gridy;
				c.insets = new Insets(10, 175, 0, 175);
				panelFifth.add(ComputeTree, c);

				deltaS.addAdjustmentListener(new DeltaListener(deltaText, deltaMin, deltaMax, scrollbarSize, deltaS));

				maxVarS.addAdjustmentListener(
						new maxVarListener(maxVarText, maxVarMin, maxVarMax, scrollbarSize, maxVarS));

				minDiversityS.addAdjustmentListener(new minDiversityListener(minDiversityText, minDiversityMin,
						minDiversityMax, scrollbarSize, minDiversityS));

				minSizeS.addAdjustmentListener(
						new minSizeListener(minSizeText, minSizemin, minSizemax, scrollbarSize, minSizeS));

				maxSizeS.addAdjustmentListener(
						new maxSizeListener(maxSizeText, maxSizemin, maxSizemax, scrollbarSize, maxSizeS));

				min.addItemListener(new DarktobrightListener());
				ComputeTree.addActionListener(new ComputeTreeListener());
				panelFifth.validate();
				panelFifth.repaint();

			}

		}

	}

	protected class UpdateMserwHoughListener implements ItemListener {
		@Override
		public void itemStateChanged(final ItemEvent arg0) {
			boolean oldState = FindLinesViaMSERwHOUGH;

			if (arg0.getStateChange() == ItemEvent.DESELECTED)
				FindLinesViaMSERwHOUGH = false;
			else if (arg0.getStateChange() == ItemEvent.SELECTED) {
				FindLinesViaMSER = false;
				FindLinesViaHOUGH = false;
				FindLinesViaMSERwHOUGH = true;
				// UpdateMSER();
				final GridBagLayout layout = new GridBagLayout();
				final GridBagConstraints c = new GridBagConstraints();
				panelFifth.removeAll();

				panelFifth.setLayout(layout);
				final Scrollbar deltaS = new Scrollbar(Scrollbar.HORIZONTAL, deltaInit, 10, 0, 10 + scrollbarSize);
				final Scrollbar maxVarS = new Scrollbar(Scrollbar.HORIZONTAL, maxVarInit, 10, 0, 10 + scrollbarSize);
				final Scrollbar minDiversityS = new Scrollbar(Scrollbar.HORIZONTAL, minDiversityInit, 10, 0,
						10 + scrollbarSize);
				final Scrollbar minSizeS = new Scrollbar(Scrollbar.HORIZONTAL, minSizeInit, 10, 0, 10 + scrollbarSize);
				final Scrollbar maxSizeS = new Scrollbar(Scrollbar.HORIZONTAL, maxSizeInit, 10, 0, 10 + scrollbarSize);
				maxVar = computeValueFromScrollbarPosition(maxVarInit, maxVarMin, maxVarMax, scrollbarSize);
				delta = computeValueFromScrollbarPosition(deltaInit, deltaMin, deltaMax, scrollbarSize);
				minDiversity = computeValueFromScrollbarPosition(minDiversityInit, minDiversityMin, minDiversityMax,
						scrollbarSize);
				minSize = (int) computeValueFromScrollbarPosition(minSizeInit, minSizemin, minSizemax, scrollbarSize);
				maxSize = (int) computeValueFromScrollbarPosition(maxSizeInit, maxSizemin, maxSizemax, scrollbarSize);

				final Checkbox min = new Checkbox("Look for Minima ", darktobright);

				final Button ComputeTree = new Button("Compute Tree and display");
				/* Location */
				final Label deltaText = new Label("delta = ", Label.CENTER);
				final Label maxVarText = new Label("maxVar = ", Label.CENTER);
				final Label minDiversityText = new Label("minDiversity = ", Label.CENTER);
				final Label minSizeText = new Label("MinSize = ", Label.CENTER);
				final Label maxSizeText = new Label("MaxSize = ", Label.CENTER);
				final Label Update = new Label("Update parameters for dynamic channel");
				Update.setBackground(new Color(1, 0, 1));
				Update.setForeground(new Color(255, 255, 255));
				panelFifth.setLayout(layout);

				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridx = 0;
				c.gridy = 0;
				c.weightx = 4;
				c.weighty = 1.5;

				++c.gridy;
				panelFifth.add(Update, c);

				++c.gridy;
				panelFifth.add(deltaText, c);

				++c.gridy;
				panelFifth.add(deltaS, c);

				++c.gridy;

				panelFifth.add(maxVarText, c);

				++c.gridy;
				panelFifth.add(maxVarS, c);

				++c.gridy;

				panelFifth.add(minDiversityText, c);

				++c.gridy;
				panelFifth.add(minDiversityS, c);

				++c.gridy;

				panelFifth.add(minSizeText, c);

				++c.gridy;
				panelFifth.add(minSizeS, c);

				++c.gridy;

				panelFifth.add(maxSizeText, c);

				++c.gridy;
				panelFifth.add(maxSizeS, c);

				++c.gridy;
				c.insets = new Insets(10, 175, 0, 175);
				panelFifth.add(min, c);

				++c.gridy;
				c.insets = new Insets(10, 175, 0, 175);
				panelFifth.add(ComputeTree, c);

				deltaS.addAdjustmentListener(new DeltaListener(deltaText, deltaMin, deltaMax, scrollbarSize, deltaS));

				maxVarS.addAdjustmentListener(
						new maxVarListener(maxVarText, maxVarMin, maxVarMax, scrollbarSize, maxVarS));

				minDiversityS.addAdjustmentListener(new minDiversityListener(minDiversityText, minDiversityMin,
						minDiversityMax, scrollbarSize, minDiversityS));

				minSizeS.addAdjustmentListener(
						new minSizeListener(minSizeText, minSizemin, minSizemax, scrollbarSize, minSizeS));

				maxSizeS.addAdjustmentListener(
						new maxSizeListener(maxSizeText, maxSizemin, maxSizemax, scrollbarSize, maxSizeS));

				min.addItemListener(new DarktobrightListener());
				ComputeTree.addActionListener(new ComputeTreeListener());
				panelFifth.validate();
				panelFifth.repaint();

			}

		}

	}

	protected class MserListener implements ItemListener {
		@Override
		public void itemStateChanged(final ItemEvent arg0) {
			boolean oldState = FindLinesViaMSER;

			if (arg0.getStateChange() == ItemEvent.DESELECTED)
				FindLinesViaMSER = false;
			else if (arg0.getStateChange() == ItemEvent.SELECTED) {

				FindLinesViaMSER = true;
				FindLinesViaHOUGH = false;
				FindLinesViaMSERwHOUGH = false;

				panelSecond.removeAll();

				final GridBagLayout layout = new GridBagLayout();
				final GridBagConstraints c = new GridBagConstraints();

				panelSecond.setLayout(layout);

				final Scrollbar deltaS = new Scrollbar(Scrollbar.HORIZONTAL, deltaInit, 10, 0, 10 + scrollbarSize);
				final Scrollbar maxVarS = new Scrollbar(Scrollbar.HORIZONTAL, maxVarInit, 10, 0, 10 + scrollbarSize);
				final Scrollbar minDiversityS = new Scrollbar(Scrollbar.HORIZONTAL, minDiversityInit, 10, 0,
						10 + scrollbarSize);
				final Scrollbar minSizeS = new Scrollbar(Scrollbar.HORIZONTAL, minSizeInit, 10, 0, 10 + scrollbarSize);
				final Scrollbar maxSizeS = new Scrollbar(Scrollbar.HORIZONTAL, maxSizeInit, 10, 0, 10 + scrollbarSize);
				final Button ComputeTree = new Button("Compute Tree and display");
				final Button FindLinesListener = new Button("Find endpoints");
				maxVar = computeValueFromScrollbarPosition(maxVarInit, maxVarMin, maxVarMax, scrollbarSize);
				delta = computeValueFromScrollbarPosition(deltaInit, deltaMin, deltaMax, scrollbarSize);
				minDiversity = computeValueFromScrollbarPosition(minDiversityInit, minDiversityMin, minDiversityMax,
						scrollbarSize);
				minSize = (int) computeValueFromScrollbarPosition(minSizeInit, minSizemin, minSizemax, scrollbarSize);
				maxSize = (int) computeValueFromScrollbarPosition(maxSizeInit, maxSizemin, maxSizemax, scrollbarSize);

				final Checkbox min = new Checkbox("Look for Minima ", darktobright);

				final Label deltaText = new Label("delta = ", Label.CENTER);
				final Label maxVarText = new Label("maxVar = ", Label.CENTER);
				final Label minDiversityText = new Label("minDiversity = ", Label.CENTER);
				final Label minSizeText = new Label("MinSize = ", Label.CENTER);
				final Label maxSizeText = new Label("MaxSize = ", Label.CENTER);
				final Label MSparam = new Label("Determine MSER parameters");
				MSparam.setBackground(new Color(1, 0, 1));
				MSparam.setForeground(new Color(255, 255, 255));

				/* Location */
				panelSecond.setLayout(layout);

				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridx = 0;
				c.gridy = 0;
				c.weightx = 4;
				c.weighty = 1.5;

				++c.gridy;

				panelSecond.add(MSparam, c);

				++c.gridy;

				panelSecond.add(deltaText, c);

				++c.gridy;
				panelSecond.add(deltaS, c);

				++c.gridy;

				panelSecond.add(maxVarText, c);

				++c.gridy;
				panelSecond.add(maxVarS, c);

				++c.gridy;

				panelSecond.add(minDiversityText, c);

				++c.gridy;
				panelSecond.add(minDiversityS, c);

				++c.gridy;

				panelSecond.add(minSizeText, c);

				++c.gridy;
				panelSecond.add(minSizeS, c);

				++c.gridy;

				panelSecond.add(maxSizeText, c);

				++c.gridy;
				panelSecond.add(maxSizeS, c);

				++c.gridy;
				c.insets = new Insets(10, 175, 0, 175);
				panelSecond.add(min, c);

				++c.gridy;
				c.insets = new Insets(10, 175, 0, 175);
				panelSecond.add(ComputeTree, c);

				++c.gridy;
				c.insets = new Insets(10, 180, 0, 180);
				panelSecond.add(FindLinesListener, c);

				deltaS.addAdjustmentListener(new DeltaListener(deltaText, deltaMin, deltaMax, scrollbarSize, deltaS));

				maxVarS.addAdjustmentListener(
						new maxVarListener(maxVarText, maxVarMin, maxVarMax, scrollbarSize, maxVarS));

				minDiversityS.addAdjustmentListener(new minDiversityListener(minDiversityText, minDiversityMin,
						minDiversityMax, scrollbarSize, minDiversityS));

				minSizeS.addAdjustmentListener(
						new minSizeListener(minSizeText, minSizemin, minSizemax, scrollbarSize, minSizeS));

				maxSizeS.addAdjustmentListener(
						new maxSizeListener(maxSizeText, maxSizemin, maxSizemax, scrollbarSize, maxSizeS));

				min.addItemListener(new DarktobrightListener());
				ComputeTree.addActionListener(new ComputeTreeListener());
				FindLinesListener.addActionListener(new FindLinesListener());
				panelSecond.validate();
				panelSecond.repaint();

			}

			if (FindLinesViaMSER != oldState) {
				while (isComputing)
					SimpleMultiThreading.threadWait(10);

				updatePreview(ValueChange.FindLinesVia);
			}
		}
	}

	protected class HoughListener implements ItemListener {
		@Override
		public void itemStateChanged(final ItemEvent arg0) {
			boolean oldState = FindLinesViaHOUGH;

			if (arg0.getStateChange() == ItemEvent.DESELECTED)
				FindLinesViaHOUGH = false;
			else if (arg0.getStateChange() == ItemEvent.SELECTED) {

				FindLinesViaHOUGH = true;
				FindLinesViaMSER = false;
				FindLinesViaMSERwHOUGH = false;
				/* Instantiation */
				final GridBagLayout layout = new GridBagLayout();
				final GridBagConstraints c = new GridBagConstraints();

				panelSecond.removeAll();

				panelSecond.setLayout(layout);
				final Label exthresholdText = new Label("threshold = threshold to create Bitimg for watershedding.",
						Label.CENTER);
				final Label exthetaText = new Label("thetaPerPixel = Pixel Size in theta direction for Hough space.",
						Label.CENTER);
				final Label exrhoText = new Label("rhoPerPixel = Pixel Size in rho direction for Hough space.",
						Label.CENTER);

				// IJ.log("Determining the initial threshold for the image");
				// thresholdHoughInit =
				// GlobalThresholding.AutomaticThresholding(currentPreprocessedimg);
				final Scrollbar threshold = new Scrollbar(Scrollbar.HORIZONTAL, (int) thresholdHoughInit, 10, 0,
						10 + scrollbarSize);
				thresholdHough = computeValueFromScrollbarPosition((int) thresholdHoughInit, thresholdHoughMin,
						thresholdHoughMax, scrollbarSize);

				final Scrollbar thetaSize = new Scrollbar(Scrollbar.HORIZONTAL, (int) thetaPerPixelInit, 10, 0,
						10 + scrollbarSize);
				thetaPerPixel = computeValueFromScrollbarPosition((int) thetaPerPixelInit, thetaPerPixelMin,
						thetaPerPixelMax, scrollbarSize);

				final Scrollbar rhoSize = new Scrollbar(Scrollbar.HORIZONTAL, (int) rhoPerPixelInit, 10, 0,
						10 + scrollbarSize);
				rhoPerPixel = computeValueFromScrollbarPosition((int) rhoPerPixelInit, rhoPerPixelMin, rhoPerPixelMax,
						scrollbarSize);

				final Checkbox displayBit = new Checkbox("Display Bitimage ", displayBitimg);
				final Checkbox displayWatershed = new Checkbox("Display Watershedimage ", displayWatershedimg);
				final Label thresholdText = new Label("thresholdValue = ", Label.CENTER);
				final Label thetaText = new Label("Size of Hough Space in Theta = ", Label.CENTER);
				final Label rhoText = new Label("Size of Hough Space in Rho = ", Label.CENTER);
				final Button Dowatershed = new Button("Do watershedding");
				final Button FindLinesListener = new Button("Find endpoints");
				final Label Houghparam = new Label("Determine Hough Transform parameters");
				Houghparam.setBackground(new Color(1, 0, 1));
				Houghparam.setForeground(new Color(255, 255, 255));

				/* Location */
				panelSecond.setLayout(layout);

				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridx = 0;
				c.gridy = 0;
				c.weightx = 4;
				c.weighty = 1.5;
				++c.gridy;
				panelSecond.add(Houghparam, c);

				++c.gridy;
				panelSecond.add(exthresholdText, c);
				++c.gridy;

				panelSecond.add(exthetaText, c);
				++c.gridy;

				panelSecond.add(exrhoText, c);
				++c.gridy;

				panelSecond.add(thresholdText, c);
				++c.gridy;

				panelSecond.add(threshold, c);
				++c.gridy;

				panelSecond.add(thetaText, c);
				++c.gridy;
				panelSecond.add(thetaSize, c);
				++c.gridy;

				panelSecond.add(rhoText, c);

				++c.gridy;

				panelSecond.add(rhoSize, c);

				++c.gridy;
				c.insets = new Insets(10, 175, 0, 175);
				panelSecond.add(displayBit, c);

				++c.gridy;
				c.insets = new Insets(10, 175, 0, 175);
				panelSecond.add(displayWatershed, c);
				++c.gridy;
				c.insets = new Insets(10, 175, 0, 175);
				panelSecond.add(Dowatershed, c);
				++c.gridy;

				c.insets = new Insets(10, 175, 0, 175);
				panelSecond.add(FindLinesListener, c);

				threshold.addAdjustmentListener(new thresholdHoughListener(thresholdText, thresholdHoughMin,
						thresholdHoughMax, scrollbarSize, threshold));

				thetaSize.addAdjustmentListener(new thetaSizeHoughListener(thetaText, rhoText, thetaPerPixelMin,
						thetaPerPixelMax, scrollbarSize, thetaSize, rhoSize));

				rhoSize.addAdjustmentListener(
						new rhoSizeHoughListener(rhoText, rhoPerPixelMin, rhoPerPixelMax, scrollbarSize, rhoSize));

				displayBit.addItemListener(new ShowBitimgListener());
				displayWatershed.addItemListener(new ShowwatershedimgListener());
				Dowatershed.addActionListener(new DowatershedListener());
				FindLinesListener.addActionListener(new FindLinesListener());
				panelSecond.validate();
				panelSecond.repaint();

			}

			if (FindLinesViaHOUGH != oldState) {
				while (isComputing)
					SimpleMultiThreading.threadWait(10);

				updatePreview(ValueChange.FindLinesVia);
			}
		}
	}

	protected class AnalyzekymoListener implements ItemListener {
		@Override
		public void itemStateChanged(final ItemEvent arg0) {

			if (arg0.getStateChange() == ItemEvent.SELECTED) {
				analyzekymo = true;
				panelSixth.removeAll();

				final GridBagLayout layout = new GridBagLayout();
				final GridBagConstraints c = new GridBagConstraints();
				panelSixth.setLayout(layout);
				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridx = 0;
				c.gridy = 0;
				c.weightx = 1;
				final Checkbox KalmanTracker = new Checkbox("Use Kalman Filter for tracking");
				final Checkbox DeterTracker = new Checkbox("Use Deterministic method for tracking");
				final Checkbox KymoExtract = new Checkbox("Extract Kymo (for the single chosen MT)");
				final Label Kal = new Label("Use Kalman Filter for probabilistic tracking");
				final Label Det = new Label("Use Deterministic tracker using the fixed Seed points");
				Kal.setBackground(new Color(1, 0, 1));
				Kal.setForeground(new Color(255, 255, 255));
				Det.setBackground(new Color(1, 0, 1));
				Det.setForeground(new Color(255, 255, 255));

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelSixth.add(Kal, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelSixth.add(KalmanTracker, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelSixth.add(Det, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelSixth.add(DeterTracker, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelSixth.add(KymoExtract, c);

				KalmanTracker.addItemListener(new KalmanchoiceListener());
				DeterTracker.addItemListener(new DeterchoiceListener());
				KymoExtract.addItemListener(new KymoExtractListener());
				
				panelSixth.validate();
				panelSixth.repaint();

			}
			if (arg0.getStateChange() == ItemEvent.DESELECTED) {
				analyzekymo = false;

			}

		}

	}

	protected class MserwHoughListener implements ItemListener {
		@Override
		public void itemStateChanged(final ItemEvent arg0) {
			boolean oldState = FindLinesViaMSERwHOUGH;

			if (arg0.getStateChange() == ItemEvent.DESELECTED)
				FindLinesViaMSERwHOUGH = false;
			else if (arg0.getStateChange() == ItemEvent.SELECTED) {

				FindLinesViaMSERwHOUGH = true;
				FindLinesViaMSER = false;
				FindLinesViaHOUGH = false;
				// DisplayMSERwHough();

				final GridBagLayout layout = new GridBagLayout();
				final GridBagConstraints c = new GridBagConstraints();
				panelSecond.removeAll();

				panelSecond.setLayout(layout);
				final Label exthetaText = new Label("thetaPerPixel = Pixel Size in theta direction for Hough space.",
						Label.CENTER);
				final Label exrhoText = new Label("rhoPerPixel = Pixel Size in rho direction for Hough space.",
						Label.CENTER);

				final Checkbox rhoEnable = new Checkbox("Enable Manual Adjustment of rhoPerPixel", enablerhoPerPixel);

				final Scrollbar thetaSize = new Scrollbar(Scrollbar.HORIZONTAL, (int) thetaPerPixelInit, 10, 0,
						10 + scrollbarSize);
				thetaPerPixel = computeValueFromScrollbarPosition((int) thetaPerPixelInit, thetaPerPixelMin,
						thetaPerPixelMax, scrollbarSize);

				final Scrollbar rhoSize = new Scrollbar(Scrollbar.HORIZONTAL, (int) rhoPerPixelInit, 10, 0,
						10 + scrollbarSize);
				rhoPerPixel = computeValueFromScrollbarPosition((int) rhoPerPixelInit, rhoPerPixelMin, rhoPerPixelMax,
						scrollbarSize);

				final Label thetaText = new Label("Size of Hough Space in Theta = ", Label.CENTER);
				final Label rhoText = new Label("Size of Hough Space in Rho = ", Label.CENTER);
				final Button FindLinesListener = new Button("Find endpoints");
				final Label Houghparam = new Label("Determine MSER and Hough Transform parameters");
				Houghparam.setBackground(new Color(1, 0, 1));
				Houghparam.setForeground(new Color(255, 255, 255));

				final Label exdeltaText = new Label("delta = stepsize of thresholds.", Label.CENTER);

				final Label exmaxVarText = new Label("maxVar = maximum instability score of the region. ",
						Label.CENTER);
				final Label exminDiversityText = new Label("minDiversity = minimum diversity of adjacent regions. ",
						Label.CENTER);
				final Label exminSizeText = new Label("MinSize = mimimum size of accepted region. ", Label.CENTER);
				final Label exmaxSizeText = new Label("MaxSize = maximum size of accepted region. ", Label.CENTER);

				final Scrollbar deltaS = new Scrollbar(Scrollbar.HORIZONTAL, deltaInit, 10, 0, 10 + scrollbarSize);
				final Scrollbar maxVarS = new Scrollbar(Scrollbar.HORIZONTAL, maxVarInit, 10, 0, 10 + scrollbarSize);
				final Scrollbar minDiversityS = new Scrollbar(Scrollbar.HORIZONTAL, minDiversityInit, 10, 0,
						10 + scrollbarSize);
				final Scrollbar minSizeS = new Scrollbar(Scrollbar.HORIZONTAL, minSizeInit, 10, 0, 10 + scrollbarSize);
				final Scrollbar maxSizeS = new Scrollbar(Scrollbar.HORIZONTAL, maxSizeInit, 10, 0, 10 + scrollbarSize);
				final Button ComputeTree = new Button("Compute Tree and display");

				final Label HoughText = new Label("Now determine the Hough space parameters.", Label.CENTER);

				maxVar = computeValueFromScrollbarPosition(maxVarInit, maxVarMin, maxVarMax, scrollbarSize);
				delta = computeValueFromScrollbarPosition(deltaInit, deltaMin, deltaMax, scrollbarSize);
				minDiversity = computeValueFromScrollbarPosition(minDiversityInit, minDiversityMin, minDiversityMax,
						scrollbarSize);
				minSize = (int) computeValueFromScrollbarPosition(minSizeInit, minSizemin, minSizemax, scrollbarSize);
				maxSize = (int) computeValueFromScrollbarPosition(maxSizeInit, maxSizemin, maxSizemax, scrollbarSize);

				final Checkbox min = new Checkbox("Look for Minima ", darktobright);

				final Label deltaText = new Label("delta = ", Label.CENTER);
				final Label maxVarText = new Label("maxVar = ", Label.CENTER);
				final Label minDiversityText = new Label("minDiversity = ", Label.CENTER);
				final Label minSizeText = new Label("MinSize = ", Label.CENTER);
				final Label maxSizeText = new Label("MaxSize = ", Label.CENTER);
				/* Location */
				panelSecond.setLayout(layout);

				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridx = 0;
				c.gridy = 0;
				c.weightx = 4;
				c.weighty = 1.5;

				panelSecond.add(deltaText, c);

				++c.gridy;
				panelSecond.add(deltaS, c);

				++c.gridy;

				panelSecond.add(maxVarText, c);

				++c.gridy;
				panelSecond.add(maxVarS, c);

				++c.gridy;

				panelSecond.add(minDiversityText, c);

				++c.gridy;
				panelSecond.add(minDiversityS, c);

				++c.gridy;

				panelSecond.add(minSizeText, c);

				++c.gridy;
				panelSecond.add(minSizeS, c);

				++c.gridy;

				panelSecond.add(maxSizeText, c);

				++c.gridy;
				panelSecond.add(maxSizeS, c);

				++c.gridy;
				c.insets = new Insets(10, 175, 0, 175);
				panelSecond.add(min, c);

				++c.gridy;
				c.insets = new Insets(10, 175, 0, 175);

				panelSecond.add(ComputeTree, c);
				++c.gridy;

				++c.gridy;
				panelSecond.add(thetaText, c);
				++c.gridy;
				panelSecond.add(thetaSize, c);
				++c.gridy;

				panelSecond.add(rhoText, c);

				++c.gridy;

				panelSecond.add(rhoSize, c);

				++c.gridy;
				c.insets = new Insets(0, 175, 0, 175);
				panelSecond.add(rhoEnable, c);

				++c.gridy;
				c.insets = new Insets(10, 175, 0, 175);
				panelSecond.add(FindLinesListener, c);

				deltaS.addAdjustmentListener(new DeltaListener(deltaText, deltaMin, deltaMax, scrollbarSize, deltaS));

				maxVarS.addAdjustmentListener(
						new maxVarListener(maxVarText, maxVarMin, maxVarMax, scrollbarSize, maxVarS));

				minDiversityS.addAdjustmentListener(new minDiversityListener(minDiversityText, minDiversityMin,
						minDiversityMax, scrollbarSize, minDiversityS));

				minSizeS.addAdjustmentListener(
						new minSizeListener(minSizeText, minSizemin, minSizemax, scrollbarSize, minSizeS));

				maxSizeS.addAdjustmentListener(
						new maxSizeListener(maxSizeText, maxSizemin, maxSizemax, scrollbarSize, maxSizeS));

				min.addItemListener(new DarktobrightListener());

				FindLinesListener.addActionListener(new FindLinesListener());

				thetaSize.addAdjustmentListener(new thetaSizeHoughListener(thetaText, rhoText, thetaPerPixelMin,
						thetaPerPixelMax, scrollbarSize, thetaSize, rhoSize));

				rhoSize.addAdjustmentListener(
						new rhoSizeHoughListener(rhoText, rhoPerPixelMin, rhoPerPixelMax, scrollbarSize, rhoSize));

				ComputeTree.addActionListener(new ComputeTreeListener());
				panelSecond.validate();
				panelSecond.repaint();
			}

			if (FindLinesViaMSERwHOUGH != oldState) {
				while (isComputing)
					SimpleMultiThreading.threadWait(10);

				updatePreview(ValueChange.FindLinesVia);
			}
		}
	}

	protected class FinishedButtonListener implements ActionListener {
		final Frame parent;
		final boolean cancel;

		public FinishedButtonListener(Frame parent, final boolean cancel) {
			this.parent = parent;
			this.cancel = cancel;
		}

		@Override
		public void actionPerformed(final ActionEvent arg0) {
			wasCanceled = cancel;
			close(parent, sliceObserver, roiListener);
		}
	}

	protected class DoneandmovebackButtonListener implements ActionListener {
		final Frame parent;
		final boolean cancel;

		public DoneandmovebackButtonListener(Frame parent, final boolean cancel) {
			this.parent = parent;
			this.cancel = cancel;
		}

		@Override
		public void actionPerformed(final ActionEvent arg0) {
			wasCanceled = cancel;
			close(parent, sliceObserver, roiListener);
			preprocessedimp.setPosition(channel, 0, 1);
			imp.setPosition(channel, 0, 1);

		}
	}

	protected class thirdDimensionsliderListener implements AdjustmentListener {
		final Label label;
		final float min, max;

		public thirdDimensionsliderListener(final Label label, final float min, final float max) {
			this.label = label;
			this.min = min;
			this.max = max;
		}

		@Override
		public void adjustmentValueChanged(final AdjustmentEvent event) {
			thirdDimensionslider = (int) computeIntValueFromScrollbarPosition(event.getValue(), min, max,
					scrollbarSize);
			label.setText("Time index = " + thirdDimensionslider);

			thirdDimension = thirdDimensionslider;

			if (thirdDimension > thirdDimensionSize) {
				IJ.log("Max frame number exceeded, moving to last frame instead");
				thirdDimension = thirdDimensionSize;
				CurrentView = getCurrentView();
				CurrentPreprocessedView = getCurrentPreView();
			} else {
				CurrentView = getCurrentView();
				CurrentPreprocessedView = getCurrentPreView();

			}

			/*
			 * if ((change == ValueChange.ROI || change == ValueChange.SIGMA ||
			 * change == ValueChange.MINMAX || change == ValueChange.FOURTHDIM
			 * || change == ValueChange.THRESHOLD && RoisOrig != null)) {
			 */
			if (!event.getValueIsAdjusting()) {
				// compute first version
				while (isComputing) {
					SimpleMultiThreading.threadWait(10);
				}
				updatePreview(ValueChange.THIRDDIM);

			}

		}
	}

	protected class ComputeTreeListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			ShowMser = true;
			updatePreview(ValueChange.SHOWMSER);

		}
	}

	protected class DowatershedListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			ShowHough = true;
			updatePreview(ValueChange.SHOWHOUGH);

		}
	}

	protected class DarktobrightListener implements ItemListener {
		@Override
		public void itemStateChanged(final ItemEvent arg0) {
			boolean oldState = darktobright;

			if (arg0.getStateChange() == ItemEvent.DESELECTED)
				darktobright = false;
			else if (arg0.getStateChange() == ItemEvent.SELECTED)
				darktobright = true;

			if (darktobright != oldState) {
				while (isComputing)
					SimpleMultiThreading.threadWait(10);

				updatePreview(ValueChange.DARKTOBRIGHT);
			}
		}
	}

	protected class ShowBitimgListener implements ItemListener {
		@Override
		public void itemStateChanged(final ItemEvent arg0) {
			boolean oldState = displayBitimg;

			if (arg0.getStateChange() == ItemEvent.DESELECTED)
				displayBitimg = false;
			else if (arg0.getStateChange() == ItemEvent.SELECTED)
				displayBitimg = true;

			if (displayBitimg != oldState) {
				while (isComputing)
					SimpleMultiThreading.threadWait(10);

				updatePreview(ValueChange.DISPLAYBITIMG);
			}
		}
	}

	protected class ShowwatershedimgListener implements ItemListener {
		@Override
		public void itemStateChanged(final ItemEvent arg0) {
			boolean oldState = displayWatershedimg;

			if (arg0.getStateChange() == ItemEvent.DESELECTED)
				displayWatershedimg = false;
			else if (arg0.getStateChange() == ItemEvent.SELECTED)
				displayWatershedimg = true;

			if (displayWatershedimg != oldState) {
				while (isComputing)
					SimpleMultiThreading.threadWait(10);

				updatePreview(ValueChange.DISPLAYWATERSHEDIMG);
			}
		}
	}

	protected static float computeIntValueFromScrollbarPosition(final int scrollbarPosition, final float min,
			final float max, final int scrollbarSize) {
		return min + (scrollbarPosition / (max)) * (max - min);
	}

	protected class FrameListener extends WindowAdapter {
		final Frame parent;

		public FrameListener(Frame parent) {
			super();
			this.parent = parent;
		}

		@Override
		public void windowClosing(WindowEvent e) {
			close(parent, sliceObserver, preprocessedimp, roiListener);
		}
	}

	protected class thetaSizeHoughListener implements AdjustmentListener {
		final Label label;
		final Label rholabel;
		final float min, max;
		final int scrollbarSize;

		final Scrollbar thetaScrollbar;
		final Scrollbar rhoScrollbar;

		public thetaSizeHoughListener(final Label label, final Label rholabel, final float min, final float max,
				final int scrollbarSize, final Scrollbar thetaScrollbar, final Scrollbar rhoScrollbar) {
			this.label = label;
			this.rholabel = rholabel;
			this.min = min;
			this.max = max;
			this.scrollbarSize = scrollbarSize;
			this.thetaScrollbar = thetaScrollbar;
			this.rhoScrollbar = rhoScrollbar;

		}

		@Override
		public void adjustmentValueChanged(final AdjustmentEvent event) {
			thetaPerPixel = computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize);

			if (!enablerhoPerPixel) {
				rhoPerPixel = thetaPerPixel;
				rholabel.setText("rhoPerPixel = " + rhoPerPixel);
				rhoScrollbar.setValue(computeScrollbarPositionFromValue(rhoPerPixel, min, max, scrollbarSize));

			}

			thetaScrollbar.setValue(computeScrollbarPositionFromValue(thetaPerPixel, min, max, scrollbarSize));

			label.setText("thetaPerPixel = " + thetaPerPixel);

			// if ( !event.getValueIsAdjusting() )
			{
				while (isComputing) {
					SimpleMultiThreading.threadWait(10);
				}
				updatePreview(ValueChange.thetaPerPixel);
			}
		}
	}

	protected class rhoSizeHoughListener implements AdjustmentListener {
		final Label label;
		final float min, max;
		final int scrollbarSize;

		final Scrollbar rhoScrollbar;

		public rhoSizeHoughListener(final Label label, final float min, final float max, final int scrollbarSize,
				final Scrollbar rhoScrollbar) {
			this.label = label;
			this.min = min;
			this.max = max;
			this.scrollbarSize = scrollbarSize;

			this.rhoScrollbar = rhoScrollbar;

		}

		@Override
		public void adjustmentValueChanged(final AdjustmentEvent event) {

			rhoPerPixel = computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize);

			rhoScrollbar.setValue(computeScrollbarPositionFromValue(rhoPerPixel, min, max, scrollbarSize));

			label.setText("rhoPerPixel = " + rhoPerPixel);

			// if ( !event.getValueIsAdjusting() )
			{
				while (isComputing) {
					SimpleMultiThreading.threadWait(10);
				}
				updatePreview(ValueChange.rhoPerPixel);
			}

		}
	}

	protected class thresholdHoughListener implements AdjustmentListener {
		final Label label;
		final float min, max;
		final int scrollbarSize;

		final Scrollbar thresholdScrollbar;

		public thresholdHoughListener(final Label label, final float min, final float max, final int scrollbarSize,
				final Scrollbar thresholdScrollbar) {
			this.label = label;
			this.min = min;
			this.max = max;
			this.scrollbarSize = scrollbarSize;

			this.thresholdScrollbar = thresholdScrollbar;

		}

		@Override
		public void adjustmentValueChanged(final AdjustmentEvent event) {
			thresholdHough = computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize);

			thresholdScrollbar.setValue(computeScrollbarPositionFromValue(thresholdHough, min, max, scrollbarSize));

			label.setText("thresholdBitimg = " + thresholdHough);

			// if ( !event.getValueIsAdjusting() )
			{
				while (isComputing) {
					SimpleMultiThreading.threadWait(10);
				}
				updatePreview(ValueChange.thresholdHough);
			}
		}
	}

	protected class DeltaListener implements AdjustmentListener {
		final Label label;
		final float min, max;
		final int scrollbarSize;

		final Scrollbar deltaScrollbar;

		public DeltaListener(final Label label, final float min, final float max, final int scrollbarSize,
				final Scrollbar deltaScrollbar) {
			this.label = label;
			this.min = min;
			this.max = max;
			this.scrollbarSize = scrollbarSize;

			this.deltaScrollbar = deltaScrollbar;

		}

		@Override
		public void adjustmentValueChanged(final AdjustmentEvent event) {
			delta = computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize);

			deltaScrollbar.setValue(computeScrollbarPositionFromValue(delta, min, max, scrollbarSize));

			label.setText("Delta = " + delta);

			// if ( !event.getValueIsAdjusting() )
			{
				while (isComputing) {
					SimpleMultiThreading.threadWait(10);
				}
				updatePreview(ValueChange.DELTA);
			}
		}
	}

	protected class minSizeListener implements AdjustmentListener {
		final Label label;
		final float min, max;
		final int scrollbarSize;

		final Scrollbar minsizeScrollbar;

		public minSizeListener(final Label label, final float min, final float max, final int scrollbarSize,
				final Scrollbar minsizeScrollbar) {
			this.label = label;
			this.min = min;
			this.max = max;
			this.scrollbarSize = scrollbarSize;

			this.minsizeScrollbar = minsizeScrollbar;

		}

		@Override
		public void adjustmentValueChanged(final AdjustmentEvent event) {
			minSize = (int) computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize);

			minsizeScrollbar.setValue(computeScrollbarPositionFromValue(minSize, min, max, scrollbarSize));

			label.setText("MinSize = " + minSize);

			// if ( !event.getValueIsAdjusting() )
			{
				while (isComputing) {
					SimpleMultiThreading.threadWait(10);
				}
				updatePreview(ValueChange.MINSIZE);
			}
		}
	}

	protected class maxSizeListener implements AdjustmentListener {
		final Label label;
		final float min, max;
		final int scrollbarSize;

		final Scrollbar maxsizeScrollbar;

		public maxSizeListener(final Label label, final float min, final float max, final int scrollbarSize,
				final Scrollbar maxsizeScrollbar) {
			this.label = label;
			this.min = min;
			this.max = max;
			this.scrollbarSize = scrollbarSize;

			this.maxsizeScrollbar = maxsizeScrollbar;

		}

		@Override
		public void adjustmentValueChanged(final AdjustmentEvent event) {
			maxSize = (int) computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize);

			maxsizeScrollbar.setValue(computeScrollbarPositionFromValue(maxSize, min, max, scrollbarSize));

			label.setText("MaxSize = " + maxSize);

			// if ( !event.getValueIsAdjusting() )
			{
				while (isComputing) {
					SimpleMultiThreading.threadWait(10);
				}
				updatePreview(ValueChange.MAXSIZE);
			}
		}
	}

	protected class maxVarListener implements AdjustmentListener {
		final Label label;
		final float min, max;
		final int scrollbarSize;

		final Scrollbar maxVarScrollbar;

		public maxVarListener(final Label label, final float min, final float max, final int scrollbarSize,
				final Scrollbar maxVarScrollbar) {
			this.label = label;
			this.min = min;
			this.max = max;
			this.scrollbarSize = scrollbarSize;
			this.maxVarScrollbar = maxVarScrollbar;

		}

		@Override
		public void adjustmentValueChanged(final AdjustmentEvent event) {
			maxVar = (computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize));

			maxVarScrollbar.setValue(computeScrollbarPositionFromValue((float) maxVar, min, max, scrollbarSize));

			label.setText("MaxVar = " + maxVar);

			// if ( !event.getValueIsAdjusting() )
			{
				while (isComputing) {
					SimpleMultiThreading.threadWait(10);
				}
				updatePreview(ValueChange.MAXVAR);
			}
		}
	}

	protected class minDiversityListener implements AdjustmentListener {
		final Label label;
		final float min, max;
		final int scrollbarSize;

		final Scrollbar minDiversityScrollbar;

		public minDiversityListener(final Label label, final float min, final float max, final int scrollbarSize,
				final Scrollbar minDiversityScrollbar) {
			this.label = label;
			this.min = min;
			this.max = max;
			this.scrollbarSize = scrollbarSize;
			this.minDiversityScrollbar = minDiversityScrollbar;

		}

		@Override
		public void adjustmentValueChanged(final AdjustmentEvent event) {
			minDiversity = (computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize));

			minDiversityScrollbar
					.setValue(computeScrollbarPositionFromValue((float) minDiversity, min, max, scrollbarSize));

			label.setText("MinDiversity = " + minDiversity);

			// if ( !event.getValueIsAdjusting() )
			{
				while (isComputing) {
					SimpleMultiThreading.threadWait(10);
				}
				updatePreview(ValueChange.MINDIVERSITY);
			}
		}
	}

	public ArrayList<EllipseRoi> getcurrentRois(MserTree<UnsignedByteType> newtree) {

		final HashSet<Mser<UnsignedByteType>> rootset = newtree.roots();

		ArrayList<EllipseRoi> Allrois = new ArrayList<EllipseRoi>();

		ArrayList<EllipseRoi> Allroiscopy = new ArrayList<EllipseRoi>();
		final Iterator<Mser<UnsignedByteType>> rootsetiterator = rootset.iterator();

		AllmeanCovar = new ArrayList<double[]>();

		while (rootsetiterator.hasNext()) {

			Mser<UnsignedByteType> rootmser = rootsetiterator.next();

			if (rootmser.size() > 0) {

				final double[] meanandcov = { rootmser.mean()[0], rootmser.mean()[1], rootmser.cov()[0],
						rootmser.cov()[1], rootmser.cov()[2] };
				AllmeanCovar.add(meanandcov);

			}
		}

		// We do this so the ROI remains attached the the same label and is not
		// changed if the program is run again
		SortListbyproperty.sortpointList(AllmeanCovar);
		for (int index = 0; index < AllmeanCovar.size(); ++index) {

			final double[] mean = { AllmeanCovar.get(index)[0], AllmeanCovar.get(index)[1] };
			final double[] covar = { AllmeanCovar.get(index)[2], AllmeanCovar.get(index)[3],
					AllmeanCovar.get(index)[4] };

			EllipseRoi roi = createEllipse(mean, covar, 3);

			Allrois.add(roi);

		}

		return Allrois;

	}

	public boolean DialogueModelChoice() {

		GenericDialog gd = new GenericDialog("Model Choice for sub-pixel Localization");
		String[] LineModel = { "GaussianLines", "SecondOrderPolynomial", "ThridOrderPolynomial" };

		int indexmodel = 0;

		gd.addChoice("Choose your model: ", LineModel, LineModel[indexmodel]);
		gd.addCheckbox("Do Gaussian Mask Fits", Domask);
		
		gd.addTextAreas("Advanced Options for the optimizer", null, 1 , 35);
		gd.addNumericField("Min Intensity = R * Max Intensity along MT, R (enter 0.2 to 0.9) = ", Intensityratio, 2);
		gd.addNumericField("Spacing between Gaussians = G * Min(Psf), G (enter 0.3 to 1.0) = ",
				Inispacing / Math.min(psf[0], psf[1]), 2);
		
		gd.addStringField("Use_folder:", usefolder);
		gd.addStringField("Choose_filestartname:", addToName);
		
		if (analyzekymo && Kymoimg!=null){
		gd.addNumericField("Average max difference between Kymo and tracker = ", deltadcutoff, 2);
		}

		gd.showDialog();
		indexmodel = gd.getNextChoiceIndex();
		Domask = gd.getNextBoolean();
	
		if (indexmodel == 0)
			userChoiceModel = UserChoiceModel.Line;
		if (indexmodel == 1)
			userChoiceModel = UserChoiceModel.Splineordersec;
		if (indexmodel == 2)
			userChoiceModel = UserChoiceModel.Splineorderthird;
		Intensityratio = gd.getNextNumber();
		Inispacing = gd.getNextNumber() * Math.min(psf[0], psf[1]);
		
		
		usefolder = gd.getNextString();
		addToName = gd.getNextString();
		
		if (analyzekymo && Kymoimg!=null)
		deltadcutoff = (float) gd.getNextNumber();

		return !gd.wasCanceled();
	}

	public boolean DialogueModelChoiceHF() {

		GenericDialog gd = new GenericDialog("Model Choice for sub-pixel Localization");
		String[] LineModel = { "GaussianLines", "SecondOrderPolynomial", "ThridOrderPolynomial" };

		int indexmodel = 0;

		gd.addChoice("Choose your model: ", LineModel, LineModel[indexmodel]);
		gd.addCheckbox("Display rois:", displayoverlay);
		gd.addTextAreas("Advanced Options for the optimizer", null ,1, 35);
		gd.addNumericField("Min Intensity = R * Max Intensity along MT, R (enter 0.2 to 0.9) = ", Intensityratio, 2);
		gd.addNumericField("Spacing between Gaussians = G * Min(Psf), G (enter 0.3 to 1.0) = ",
				Inispacing / Math.min(psf[0], psf[1]), 2);
		gd.addStringField("Use_folder:", usefolder);
		gd.addStringField("Choose_filestartname:", addToName);
		gd.showDialog();
		indexmodel = gd.getNextChoiceIndex();
		displayoverlay = gd.getNextBoolean();
		Domask = false;

		if (indexmodel == 0)
			userChoiceModel = UserChoiceModel.Line;
		if (indexmodel == 1)
			userChoiceModel = UserChoiceModel.Splineordersec;
		if (indexmodel == 2)
			userChoiceModel = UserChoiceModel.Splineorderthird;
		Intensityratio = gd.getNextNumber();
		Inispacing = gd.getNextNumber() * Math.min(psf[0], psf[1]);
		
		
		usefolder = gd.getNextString();
		addToName = gd.getNextString();
		
		

		return !gd.wasCanceled();
	}

	public boolean DialogueMedian() {
		// Create dialog
		GenericDialog gd = new GenericDialog("Choose the radius of the filter");
		gd.addNumericField("Radius:", radius, 0);
		gd.showDialog();
		radius = ((int) gd.getNextNumber());

		return !gd.wasCanceled();
	}

	protected RandomAccessibleInterval<FloatType> extractImage(final RandomAccessibleInterval<FloatType> intervalView) {

		final FloatType type = intervalView.randomAccess().get().createVariable();
		final ImgFactory<FloatType> factory = net.imglib2.util.Util.getArrayOrCellImgFactory(intervalView, type);
		RandomAccessibleInterval<FloatType> totalimg = factory.create(intervalView, type);
		final RandomAccessibleInterval<FloatType> img = Views.interval(intervalView, interval);

		totalimg = Views.interval(Views.extendZero(img), intervalView);

		return totalimg;
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

	protected static float computeValueFromScrollbarPosition(final int scrollbarPosition, final float min,
			final float max, final int scrollbarSize) {
		return min + (scrollbarPosition / (float) scrollbarSize) * (max - min);
	}

	protected static int computeScrollbarPositionFromValue(final float sigma, final float min, final float max,
			final int scrollbarSize) {
		return Util.round(((sigma - min) / (max - min)) * scrollbarSize);
	}

	public static FloatImagePlus<net.imglib2.type.numeric.real.FloatType> createImgLib2(final List<float[]> img,
			final int w, final int h) {
		final ImagePlus imp;

		if (img.size() > 1) {
			final ImageStack stack = new ImageStack(w, h);
			for (int z = 0; z < img.size(); ++z)
				stack.addSlice(new FloatProcessor(w, h, img.get(z)));
			imp = new ImagePlus("ImgLib2 FloatImagePlus (3d)", stack);
		} else {
			imp = new ImagePlus("ImgLib2 FloatImagePlus (2d)", new FloatProcessor(w, h, img.get(0)));
		}

		return ImagePlusAdapter.wrapFloat(imp);
	}

	protected class ImagePlusListener implements SliceListener {
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

	public Img<FloatType> copyImage(final RandomAccessibleInterval<FloatType> input) {
		// create a new Image with the same dimensions but the other imgFactory
		// note that the input provides the size for the new image by
		// implementing the Interval interface
		Img<FloatType> output = new ArrayImgFactory<FloatType>().create(input, Views.iterable(input).firstElement());

		// create a cursor that automatically localizes itself on every move
		Cursor<FloatType> cursorInput = Views.iterable(input).localizingCursor();
		RandomAccess<FloatType> randomAccess = output.randomAccess();

		// iterate over the input cursor
		while (cursorInput.hasNext()) {
			// move input cursor forward
			cursorInput.fwd();

			// set the output cursor to the position of the input cursor
			randomAccess.setPosition(cursorInput);

			// set the value of this pixel of the output image, every Type
			// supports T.set( T type )
			randomAccess.get().set(cursorInput.get());
		}

		// return the copy
		return output;
	}

	/**
	 * Generic, type-agnostic method to create an identical copy of an Img
	 *
	 * @param currentPreprocessedimg2
	 *            - the Img to copy
	 * @return - the copy of the Img
	 */
	public Img<UnsignedByteType> copytoByteImage(final RandomAccessibleInterval<FloatType> input) {
		// create a new Image with the same properties
		// note that the input provides the size for the new image as it
		// implements
		// the Interval interface
		final RandomAccessibleInterval<FloatType> inputcopy = copyImage(input);
		Normalize.normalize(Views.iterable(inputcopy), new FloatType(0), new FloatType(255));
		final UnsignedByteType type = new UnsignedByteType();
		final ImgFactory<UnsignedByteType> factory = net.imglib2.util.Util.getArrayOrCellImgFactory(input, type);
		final Img<UnsignedByteType> output = factory.create(input, type);
		// create a cursor for both images
		RandomAccess<FloatType> ranac = inputcopy.randomAccess();
		Cursor<UnsignedByteType> cursorOutput = output.cursor();

		// iterate over the input
		while (cursorOutput.hasNext()) {
			// move both cursors forward by one pixel
			cursorOutput.fwd();

			int x = cursorOutput.getIntPosition(0);
			int y = cursorOutput.getIntPosition(1);

			if (standardRectangle.contains(x, y)) {

				ranac.setPosition(cursorOutput);

				// set the value of this pixel of the output image to the same
				// as
				// the input,
				// every Type supports T.set( T type )
				cursorOutput.get().set((int) ranac.get().get());
			}
		}

		// return the copy
		return output;
	}

	/**
	 * 2D correlated Gaussian
	 * 
	 * @param mean
	 *            (x,y) components of mean vector
	 * @param cov
	 *            (xx, xy, yy) components of covariance matrix
	 * @return ImageJ roi
	 */
	public EllipseRoi createEllipse(final double[] mean, final double[] cov, final double nsigmas) {

		final double a = cov[0];
		final double b = cov[1];
		final double c = cov[2];
		final double d = Math.sqrt(a * a + 4 * b * b - 2 * a * c + c * c);
		final double scale1 = Math.sqrt(0.5 * (a + c + d)) * nsigmas;
		final double scale2 = Math.sqrt(0.5 * (a + c - d)) * nsigmas;
		final double theta = 0.5 * Math.atan2((2 * b), (a - c));
		final double x = mean[0];
		final double y = mean[1];
		final double dx = scale1 * Math.cos(theta);
		final double dy = scale1 * Math.sin(theta);
		final EllipseRoi ellipse = new EllipseRoi(x - dx, y - dy, x + dx, y + dy, scale2 / scale1);

		return ellipse;
	}

	public static void main(String[] args) {
		new ImageJ();

		JFrame frame = new JFrame("");
		FileChooser panel = new FileChooser();

		frame.getContentPane().add(panel, "Center");
		frame.setSize(panel.getPreferredSize());
	}
}
