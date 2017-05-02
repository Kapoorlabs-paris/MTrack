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
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
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
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import LineModels.UseLineModel.UserChoiceModel;
import MTObjects.MTcounter;
import MTObjects.ResultsMT;
import ch.qos.logback.core.rolling.helper.RollingCalendar;
import costMatrix.CostFunction;
import costMatrix.SquareDistCostFunction;
import drawandOverlay.DisplayGraph;
import drawandOverlay.DisplayGraphKalman;
import drawandOverlay.DisplaysubGraphend;
import drawandOverlay.DisplaysubGraphstart;
import drawandOverlay.DisplaysubGraphstartKalman;
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
import ij.gui.ImageCanvas;
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
import labeledObjects.CommonOutputHF;
import labeledObjects.Indexedlength;
import labeledObjects.KalmanIndexedlength;
import labeledObjects.Shrink;
import labeledObjects.Subgraphs;
import labeledObjects.SubgraphsKalman;
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

public class Interactive_MTDoubleChannel implements PlugIn {

	String usefolder = IJ.getDirectory("imagej");
	ColorProcessor cp = null;
	String addToName = "MTTrack";
	ArrayList<float[]> deltadstart = new ArrayList<>();
	ArrayList<float[]> deltadend = new ArrayList<>();
	ArrayList<float[]> deltad = new ArrayList<>();
	ArrayList<float[]> lengthKymo;
	final int scrollbarSize = 1000;
	final int scrollbarSizebig = 1000;
	// steps per octave
	public static int standardSensitivity = 4;
	int sensitivity = standardSensitivity;
	float deltaMin = 0;
	float thetaPerPixelMin = new Float(0.2);
	float rhoPerPixelMin = new Float(0.2);
	MouseListener ml;
	MouseListener removeml;
	OvalRoi Seedroi;
	ArrayList<OvalRoi> AllSeedrois;
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
	int selectedSeed = 0;
	int displayselectedSeed;
	double netdeltad = 0;
	double Intensityratio = 0.5;
	double Inispacing = 0.5;
	int thirdDimensionslider = 1;
	int thirdDimensionsliderInit = 1;
	int timeMin = 1;

	float minDiversityMin = 0;
	float minDiversityMax = 1;

	UserChoiceModel userChoiceModel;
	float delta = 1f;

	int deltaInit = 20;
	int maxVarInit = 1;

	int minSizeInit = 100;
	int maxSizeInit = 500;

	float thresholdHoughInit = 100;
	float rhoPerPixelInit = new Float(1);
	float thetaPerPixelInit = new Float(1);
	JLabel inputMaxdpixel;
	JLabel inputMaxdmicro;
	private TextField Maxdpixel, Maxdmicro;
	float frametosec;

	public int minDiversityInit = 100;

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
	Color colorDraw = Color.red;
	Color colorTrack = Color.gray;
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
	boolean showDeterministic = true;
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
	boolean SaveXLS = false;
	boolean finalpoint = false;
	boolean Trackstart;
	int nbRois;
	Roi rorig = null;
	ArrayList<double[]> lengthtimestart = new ArrayList<double[]>();
	HashMap<Integer, ArrayList<EllipseRoi>> AllMSERrois = new HashMap<Integer, ArrayList<EllipseRoi>>();
	HashMap<Integer, double[]> AllPoints = new HashMap<Integer, double[]>();

	ArrayList<double[]> lengthtimeend = new ArrayList<double[]>();
	ArrayList<double[]> lengthtime = new ArrayList<double[]>();
	ArrayList<MTcounter> ALLcounts = new ArrayList<MTcounter>();
	MTTracker MTtrackerstart;
	MTTracker MTtrackerend;
	CostFunction<KalmanTrackproperties, KalmanTrackproperties> UserchosenCostFunction;

	float initialSearchradius = 20;
	int starttime = 0;
	int endtime = 0;
	float maxSearchradius = 15;
	int missedframes = 5;
	int maxghost = 5;
	public int initialSearchradiusInit = 200;
	public float initialSearchradiusMin = 0;
	public float initialSearchradiusMax = 100;

	double sumlengthpixel = 0;
	double sumlengthmicro = 0;

	public int maxSearchradiusInit = 200;
	public float maxSearchradiusMin = 10;
	public float maxSearchradiusMax = 500;

	public int missedframesInit = missedframes;
	public float missedframesMin = 10;
	public float missedframesMax = 100;
	Overlay overlay;
	HashMap<Integer, Boolean> whichend = new HashMap<Integer, Boolean>();
	HashMap<Integer, Double> pixellength = new HashMap<Integer, Double>();
	HashMap<Integer, Double> microlength = new HashMap<Integer, Double>();
	ArrayList<float[]> finalvelocity = new ArrayList<float[]>();
	ArrayList<float[]> finalvelocityKymo = new ArrayList<float[]>();
	ArrayList<ArrayList<Trackproperties>> Allstart = new ArrayList<ArrayList<Trackproperties>>();
	ArrayList<ArrayList<Trackproperties>> Allend = new ArrayList<ArrayList<Trackproperties>>();

	ArrayList<ResultsMT> startlengthlist = new ArrayList<ResultsMT>();
	ArrayList<ResultsMT> endlengthlist = new ArrayList<ResultsMT>();

	ArrayList<ArrayList<KalmanTrackproperties>> AllstartKalman = new ArrayList<ArrayList<KalmanTrackproperties>>();
	ArrayList<ArrayList<KalmanTrackproperties>> AllendKalman = new ArrayList<ArrayList<KalmanTrackproperties>>();
	int channel = 0;
	int thirdDimensionSize;
	int thirdDimensionSizeOriginal;
	ImagePlus Kymoimp;
	RandomAccessibleInterval<FloatType> originalimg;
	RandomAccessibleInterval<FloatType> originalPreprocessedimg;
	RandomAccessibleInterval<FloatType> Kymoimg;
	RandomAccessibleInterval<FloatType> CurrentView;
	RandomAccessibleInterval<FloatType> CurrentPreprocessedView;
	int inix = 20;
	int iniy = 20;
	double[] calibration;
	double radiusfactor = 1;
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
	Overlay overlaysec;
	ArrayList<Pair<Integer, double[]>> IDALL = new ArrayList<Pair<Integer, double[]>>();
	ArrayList<double[]> ClickedPoints = new ArrayList<double[]>();
	Pair<ArrayList<Indexedlength>, ArrayList<Indexedlength>> PrevFrameparam;
	Pair<ArrayList<Indexedlength>, ArrayList<Indexedlength>> NewFrameparam;
	ArrayList<Integer> Accountedframes = new ArrayList<Integer>();
	ArrayList<Integer> Missedframes = new ArrayList<Integer>();
	Pair<Pair<ArrayList<Trackproperties>, ArrayList<Trackproperties>>, Pair<ArrayList<Indexedlength>, ArrayList<Indexedlength>>> returnVector;

	Pair<ArrayList<KalmanIndexedlength>, ArrayList<KalmanIndexedlength>> PrevFrameparamKalman;
	Pair<ArrayList<KalmanIndexedlength>, ArrayList<KalmanIndexedlength>> NewFrameparamKalman;
	Pair<Pair<ArrayList<KalmanTrackproperties>, ArrayList<KalmanTrackproperties>>, Pair<ArrayList<KalmanIndexedlength>, ArrayList<KalmanIndexedlength>>> returnVectorKalman;
	NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);

	ArrayList<CommonOutputHF> output;
	ImageStack prestack;
	public Rectangle standardRectangle;
	public FinalInterval interval;
	RandomAccessibleInterval<UnsignedByteType> newimg;
	ArrayList<double[]> AllmeanCovar;

	// first and last slice to process
	int endStack, thirdDimension;

	public static enum Whichend {

		start, end, both, none;
	}

	public static enum ValueChange {
		ROI, ALL, DELTA, FindLinesVia, MAXVAR, MINDIVERSITY, DARKTOBRIGHT, MINSIZE, MAXSIZE, SHOWMSER, FRAME, SHOWHOUGH, thresholdHough, DISPLAYBITIMG, DISPLAYWATERSHEDIMG, rhoPerPixel, thetaPerPixel, THIRDDIM, iniSearch, maxSearch, missedframes, THIRDDIMTrack, MEDIAN, kymo;
	}

	boolean isFinished = false;
	boolean wasCanceled = false;
	boolean SecondOrderSpline;
	boolean ThirdOrderSpline;
	HashMap<Integer, Whichend> seedmap = new HashMap<Integer, Whichend>();

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

		return initialSearchradius;

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

	public Interactive_MTDoubleChannel() {
	};

	public Interactive_MTDoubleChannel(final RandomAccessibleInterval<FloatType> originalimg,
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

	public Interactive_MTDoubleChannel(final RandomAccessibleInterval<FloatType> originalimg,
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

		AllSeedrois = new ArrayList<OvalRoi>();
		jpb = new JProgressBar();
		UserchosenCostFunction = new SquareDistCostFunction();
		Inispacing = 0.5 * Math.min(psf[0], psf[1]);
		count = 0;
		nf.setMaximumFractionDigits(3);
		setInitialmaxVar(maxVarInit);
		setInitialDelta(deltaInit);
		setInitialrhoPerPixel(rhoPerPixelInit);
		setInitialthetaPerPixel(thetaPerPixelInit);
		setInitialthresholdHough(thresholdHoughInit);

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

		CurrentView = util.CopyUtils.getCurrentView(originalimg, thirdDimension, thirdDimensionSize);
		CurrentPreprocessedView = util.CopyUtils.getCurrentPreView(originalPreprocessedimg, thirdDimension,
				thirdDimensionSize);

		output = new ArrayList<CommonOutputHF>();
		endStack = thirdDimensionSize;
		thirdDimensionSizeOriginal = thirdDimensionSize;
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

			if (preprocessedimp == null)
				preprocessedimp = ImageJFunctions.show(CurrentPreprocessedView);
			else {
				final float[] pixels = (float[]) preprocessedimp.getProcessor().getPixels();
				final Cursor<FloatType> c = Views.iterable(CurrentPreprocessedView).cursor();

				for (int i = 0; i < pixels.length; ++i)
					pixels[i] = c.next().get();

				preprocessedimp.updateAndDraw();

			}

			preprocessedimp.setTitle("Preprocessed image Current View in third dimension: " + " " + thirdDimension);
		}

		RoiManager roimanager = RoiManager.getInstance();

		if (roimanager == null) {
			roimanager = new RoiManager();
		}

		if (change == ValueChange.THIRDDIMTrack) {

			if (preprocessedimp == null)
				preprocessedimp = ImageJFunctions.show(CurrentPreprocessedView);
			else {
				final float[] pixels = (float[]) preprocessedimp.getProcessor().getPixels();
				final Cursor<FloatType> c = Views.iterable(CurrentPreprocessedView).cursor();

				for (int i = 0; i < pixels.length; ++i)
					pixels[i] = c.next().get();

				preprocessedimp.updateAndDraw();

			}

			preprocessedimp.setTitle("Preprocessed image Current View in third dimension: " + " " + thirdDimension);

			// check if Roi changed
			System.out.println("Current Time point: " + thirdDimension);

			long[] min = { (long) standardRectangle.getMinX(), (long) standardRectangle.getMinY() };
			long[] max = { (long) standardRectangle.getMaxX(), (long) standardRectangle.getMaxY() };
			interval = new FinalInterval(min, max);
			final long Cannyradius = (long) (radiusfactor * Math.ceil(Math.sqrt(psf[0] * psf[0] + psf[1] * psf[1])));

			currentimg = util.CopyUtils.extractImage(CurrentView, interval);
			currentPreprocessedimg = util.CopyUtils.extractImage(CurrentPreprocessedView, interval);
			// Expand the image by 10 pixels

			Interval spaceinterval = Intervals.createMinMax(
					new long[] { currentimg.min(0), currentimg.min(1), currentimg.max(0), currentimg.max(1) });
			Interval interval = Intervals.expand(spaceinterval, 10);
			currentimg = Views.interval(Views.extendBorder(currentimg), interval);
			currentPreprocessedimg = Views.interval(Views.extendBorder(currentPreprocessedimg), interval);

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
				final long Cannyradius = (long) (radiusfactor
						* Math.ceil(Math.sqrt(psf[0] * psf[0] + psf[1] * psf[1])));

				currentimg = util.CopyUtils.extractImage(CurrentView, interval);
				currentPreprocessedimg = util.CopyUtils.extractImage(CurrentPreprocessedView, interval);
				// Expand the image by 10 pixels

				Interval spaceinterval = Intervals.createMinMax(
						new long[] { currentimg.min(0), currentimg.min(1), currentimg.max(0), currentimg.max(1) });
				Interval interval = Intervals.expand(spaceinterval, 10);
				currentimg = Views.interval(Views.extendBorder(currentimg), interval);
				currentPreprocessedimg = Views.interval(Views.extendBorder(currentPreprocessedimg), interval);

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

				currentimg = util.CopyUtils.extractImage(CurrentView, interval);
				currentPreprocessedimg = util.CopyUtils.extractImage(CurrentPreprocessedView, interval);
				// Expand the image by 10 pixels

				Interval spaceinterval = Intervals.createMinMax(
						new long[] { currentimg.min(0), currentimg.min(1), currentimg.max(0), currentimg.max(1) });
				Interval interval = Intervals.expand(spaceinterval, 10);
				currentimg = Views.interval(Views.extendBorder(currentimg), interval);
				currentPreprocessedimg = Views.interval(Views.extendBorder(currentPreprocessedimg), interval);

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

		if (change == ValueChange.SHOWHOUGH) {
			long[] min = { (long) standardRectangle.getMinX(), (long) standardRectangle.getMinY() };
			long[] max = { (long) standardRectangle.getMaxX(), (long) standardRectangle.getMaxY() };
			interval = new FinalInterval(min, max);
			final long Cannyradius = (long) (radiusfactor * Math.ceil(Math.sqrt(psf[0] * psf[0] + psf[1] * psf[1])));

			currentimg = util.CopyUtils.extractImage(CurrentView, interval);
			currentPreprocessedimg = util.CopyUtils.extractImage(CurrentPreprocessedView, interval);
			// Expand the image by 10 pixels

			Interval spaceinterval = Intervals.createMinMax(
					new long[] { currentimg.min(0), currentimg.min(1), currentimg.max(0), currentimg.max(1) });
			Interval interval = Intervals.expand(spaceinterval, 10);
			currentimg = Views.interval(Views.extendBorder(currentimg), interval);
			currentPreprocessedimg = Views.interval(Views.extendBorder(currentPreprocessedimg), interval);

			newimg = util.CopyUtils.copytoByteImage(Kernels.CannyEdgeandMean(currentPreprocessedimg, Cannyradius),
					standardRectangle);

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
			long[] min = { (long) standardRectangle.getMinX(), (long) standardRectangle.getMinY() };
			long[] max = { (long) standardRectangle.getMaxX(), (long) standardRectangle.getMaxY() };
			interval = new FinalInterval(min, max);
			final long Cannyradius = (long) (radiusfactor * Math.ceil(Math.sqrt(psf[0] * psf[0] + psf[1] * psf[1])));

			currentimg = util.CopyUtils.extractImage(CurrentView, interval);
			currentPreprocessedimg = util.CopyUtils.extractImage(CurrentPreprocessedView, interval);
			// Expand the image by 10 pixels

			Interval spaceinterval = Intervals.createMinMax(
					new long[] { currentimg.min(0), currentimg.min(1), currentimg.max(0), currentimg.max(1) });
			Interval interval = Intervals.expand(spaceinterval, 10);
			currentimg = Views.interval(Views.extendBorder(currentimg), interval);
			currentPreprocessedimg = Views.interval(Views.extendBorder(currentPreprocessedimg), interval);

			newimg = util.CopyUtils.copytoByteImage(Kernels.CannyEdgeandMean(currentPreprocessedimg, Cannyradius),
					standardRectangle);

			newtree = MserTree.buildMserTree(newimg, delta, minSize, maxSize, maxVar, minDiversity, darktobright);
			Rois = util.DrawingUtils.getcurrentRois(newtree, AllmeanCovar);

			AllMSERrois.put(thirdDimension, Rois);
			count++;

			if (count == 1)
				startdim = thirdDimension;

			if (preprocessedimp != null) {

				Overlay o = preprocessedimp.getOverlay();

				if (o == null) {
					o = new Overlay();
					preprocessedimp.setOverlay(o);
				}

				o.clear();
				for (int index = 0; index < Rois.size(); ++index) {

					EllipseRoi or = Rois.get(index);

					or.setStrokeColor(Color.red);
					o.add(or);

					roimanager.addRoi(or);

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

			gd.addNumericField("Do till frame", thirdDimensionSizeOriginal, 0);

			assert (int) gd.getNextNumber() > 1;
		}

		gd.showDialog();
		if (thirdDimensionSize > 1) {
			thirdDimensionSize = (int) gd.getNextNumber();

		}
		return !gd.wasCanceled();

	}

	protected class ChooseDirectoryListener implements ActionListener {
		final TextField filename;

		public ChooseDirectoryListener(TextField filename) {

			this.filename = filename;

		}

		@Override
		public void actionPerformed(final ActionEvent arg0) {

			JFileChooser chooserA = new JFileChooser();
			chooserA.setCurrentDirectory(new java.io.File("."));
			chooserA.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooserA.showOpenDialog(panelFirst);
			usefolder = chooserA.getSelectedFile().getAbsolutePath();

			addToName = filename.getText();

		}

	}

	protected class ConfirmDirectoryListener implements ActionListener {

		final TextField filename;

		public ConfirmDirectoryListener(TextField filename) {

			this.filename = filename;

		}

		@Override
		public void actionPerformed(final ActionEvent arg0) {

			addToName = filename.getText();

		}

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
	JPanel panelTenth = new JPanel();

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
		panelCont.add(panelTenth, "10");

		// First Panel
		panelFirst.setName("Preprocess and Determine Seeds");

		CheckboxGroup Finders = new CheckboxGroup();
		final Checkbox MedFilterAll = new Checkbox("Apply Median Filter to Stack", MedianAll);
		final Scrollbar thirdDimensionslider = new Scrollbar(Scrollbar.HORIZONTAL, thirdDimensionsliderInit, 0, 0,
				thirdDimensionSize);
		thirdDimensionslider.setBlockIncrement(1);
		this.thirdDimensionslider = (int) computeIntValueFromScrollbarPosition(thirdDimensionsliderInit, timeMin,
				thirdDimensionSize, thirdDimensionSize);
		final Label timeText = new Label("Time index = " + this.thirdDimensionslider, Label.CENTER);
		final Button JumpinTime = new Button("Jump in time :");
		final Label MTText = new Label("Preprocess and Determine Seed Ends (Green Channel)", Label.CENTER);
		final Label Step = new Label("Step 1", Label.CENTER);
		final Checkbox Analyzekymo = new Checkbox("Analyze Kymograph");
		final JButton ChooseDirectory = new JButton("Choose Directory");
		final JLabel outputfilename = new JLabel("Enter output filename: ");
		TextField inputField = new TextField();
		inputField.setColumns(10);
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
		if (Kymoimg != null) {
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
		++c.gridy;
		c.insets = new Insets(10, 10, 10, 0);
		panelFirst.add(outputfilename, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 10, 0);
		panelFirst.add(inputField, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 0);
		panelFirst.add(ChooseDirectory, c);

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
		ChooseDirectory.addActionListener(new ChooseDirectoryListener(inputField));
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
		final Button MoveNext = new Button("Choose first image in the dynamic channel)");
		final Button JumptoFrame = new Button("Dynamic end not visible? Choose image at another time point");

		final Label ORText = new Label("OR", Label.CENTER);

		final Label ANDText = new Label("AND", Label.CENTER);

		ORText.setBackground(new Color(1, 0, 1));
		ORText.setForeground(new Color(255, 255, 255));

		ANDText.setBackground(new Color(1, 0, 1));
		ANDText.setForeground(new Color(255, 255, 255));

		final Button ClickFast = new Button("Choose more");

		final Button RemoveFast = new Button("Remove wrongly selected ends");
		final Checkbox Finalize = new Checkbox("Confirm the dynamic seed end (s)");
		final Label MTTextHF = new Label("Select ends for tracking", Label.CENTER);
		final Label Step3 = new Label("Step 3", Label.CENTER);
		final Checkbox txtfile = new Checkbox("Save tracks as TXT file", SaveTxt);

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
		c.insets = new Insets(10, 10, 0, 180);
		panelThird.add(MoveNext, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 10);
		panelThird.add(ORText, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 180);
		panelThird.add(JumptoFrame, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 10);
		panelThird.add(ANDText, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 180);
		panelThird.add(RemoveFast, c);

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
		MoveNext.addActionListener(new moveNextListener());
		JumptoFrame.addActionListener(new moveToFrameListener());
		RemoveFast.addActionListener(new removeendListener());
		ClickFast.addActionListener(new chooseendListener());
		thirdDimensionslider
				.addAdjustmentListener(new thirdDimensionsliderListener(timeText, timeMin, thirdDimensionSize));
		Cardframe.addWindowListener(new FrameListener(Cardframe));
		JumpinTime.addActionListener(
				new moveInThirdDimListener(thirdDimensionslider, timeText, timeMin, thirdDimensionSize));

		txtfile.addItemListener(new SaveasTXT());
		Finalize.addItemListener(new finalpoint());
		// xlsfile.addItemListener(new SaveasXLS());

		MTText.setFont(MTText.getFont().deriveFont(Font.BOLD));
		Pre.setBackground(new Color(1, 0, 1));
		Pre.setForeground(new Color(255, 255, 255));
		Ends.setBackground(new Color(1, 0, 1));
		Ends.setForeground(new Color(255, 255, 255));
		Kymo.setBackground(new Color(1, 0, 1));
		Kymo.setForeground(new Color(255, 255, 255));
		MTTextHF.setFont(MTTextHF.getFont().deriveFont(Font.BOLD));

		if (analyzekymo == false && Kymoimg == null) {

			/*
			 * final Label Step6 = new Label("Step 6", Label.CENTER);
			 * panelSixth.setLayout(layout);
			 * 
			 * c.fill = GridBagConstraints.HORIZONTAL; c.gridx = 0; c.gridy = 0;
			 * c.weightx = 1; panelSixth.add(Step6, c); // final Checkbox
			 * KalmanTracker = new Checkbox("Use Kalman Filter for tracking");
			 * final Checkbox DeterTracker = new Checkbox(
			 * "Use Deterministic method for tracking"); // final Label Kal =
			 * new Label("Use Kalman Filter for probabilistic tracking"); final
			 * Label Det = new Label(
			 * "Use Deterministic tracker using the fixed Seed points"); //
			 * Kal.setBackground(new Color(1, 0, 1)); // Kal.setForeground(new
			 * Color(255, 255, 255)); Det.setBackground(new Color(1, 0, 1));
			 * Det.setForeground(new Color(255, 255, 255));
			 * 
			 * // ++c.gridy; // c.insets = new Insets(10, 10, 0, 50); //
			 * panelSixth.add(Kal, c);
			 * 
			 * // ++c.gridy; // c.insets = new Insets(10, 10, 0, 50); //
			 * panelSixth.add(KalmanTracker, c);
			 * 
			 * ++c.gridy; c.insets = new Insets(10, 10, 0, 50);
			 * panelSixth.add(Det, c);
			 * 
			 * ++c.gridy; c.insets = new Insets(10, 10, 0, 50);
			 * panelSixth.add(DeterTracker, c);
			 * 
			 * // KalmanTracker.addItemListener(new KalmanchoiceListener());
			 * DeterTracker.addItemListener(new DeterchoiceListener());
			 */
		}

		panelNinth.setLayout(layout);
		final Label Done = new Label("Hope that everything was to your satisfaction!");
		final Button Exit = new Button("Close and exit");

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

	public void Kymo() {

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

		panelFifth.removeAll();
		final Label Step5 = new Label("Step 5", Label.CENTER);
		panelFifth.setLayout(layout);
		panelFifth.add(Step5, c);
		if (Kymoimg != null)
			Kymoimp = ImageJFunctions.show(Kymoimg);
		final Label Select = new Label(
				"Make Segmented Line selection (Generates a file containing time (row 1) and length (row 2))");
		final Button ExtractKymo = new Button("Extract Mask Co-ordinates :");
		Select.setBackground(new Color(1, 0, 1));
		Select.setForeground(new Color(255, 255, 255));

		final Label Checkres = new Label("The tracker now performs an internal check on the results");
		Checkres.setBackground(new Color(1, 0, 1));
		Checkres.setForeground(new Color(255, 255, 255));

		if (analyzekymo) {

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 0);
			panelFifth.add(Select, c);

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 200);
			panelFifth.add(ExtractKymo, c);

			ExtractKymo.addActionListener(new GetCords());

		}

		if (showDeterministic) {
			final Button TrackEndPoints = new Button("Track EndPoints (From first to a chosen last frame)");
			final Button SkipframeandTrackEndPoints = new Button("TrackEndPoint (User specified first and last frame)");
			final Button CheckResults = new Button("Check Results (then click next)");

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 175);
			panelFifth.add(TrackEndPoints, c);

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 175);
			panelFifth.add(SkipframeandTrackEndPoints, c);

			if (analyzekymo && Kymoimg != null) {
				++c.gridy;
				c.insets = new Insets(10, 10, 0, 0);
				panelFifth.add(Checkres, c);

				++c.gridy;
				c.insets = new Insets(10, 175, 0, 175);
				panelFifth.add(CheckResults, c);

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
			panelFifth.add(SearchText, c);
			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			panelFifth.add(rad, c);

			final Scrollbar Maxrad = new Scrollbar(Scrollbar.HORIZONTAL, maxSearchradiusInit, 10, 0,
					10 + scrollbarSize);
			maxSearchradius = computeValueFromScrollbarPosition(maxSearchradiusInit, maxSearchradiusMin,
					maxSearchradiusMax, scrollbarSize);
			final Label MaxMovText = new Label("Max Movment of Objects per frame: " + maxSearchradius, Label.CENTER);
			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			panelFifth.add(MaxMovText, c);
			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			panelFifth.add(Maxrad, c);

			final Scrollbar Miss = new Scrollbar(Scrollbar.HORIZONTAL, missedframesInit, 10, 0, 10 + scrollbarSize);
			Miss.setBlockIncrement(1);
			missedframes = (int) computeValueFromScrollbarPosition(missedframesInit, missedframesMin, missedframesMax,
					scrollbarSize);
			final Label LostText = new Label("Objects allowed to be lost for #frames" + missedframes, Label.CENTER);
			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			panelFifth.add(LostText, c);
			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			panelFifth.add(Miss, c);

			final Checkbox Costfunc = new Checkbox("Squared Distance Cost Function");
			// ++c.gridy;
			// c.insets = new Insets(10, 10, 0, 50);
			// panelFifth.add(Costfunc, c);

			rad.addAdjustmentListener(
					new SearchradiusListener(SearchText, initialSearchradiusMin, initialSearchradiusMax));
			Maxrad.addAdjustmentListener(
					new maxSearchradiusListener(MaxMovText, maxSearchradiusMin, maxSearchradiusMax));
			Miss.addAdjustmentListener(new missedFrameListener(LostText, missedframesMin, missedframesMax));

			// Costfunc.addItemListener(new CostfunctionListener());

			MTtrackerstart = new KFsearch(AllstartKalman, UserchosenCostFunction, maxSearchradius, initialSearchradius,
					thirdDimension, thirdDimensionSize, missedframes);

			MTtrackerend = new KFsearch(AllendKalman, UserchosenCostFunction, maxSearchradius, initialSearchradius,
					thirdDimension, thirdDimensionSize, missedframes);

			final Button TrackEndPoints = new Button("Track EndPoints (From first to a chosen last frame)");
			final Button SkipframeandTrackEndPoints = new Button("TrackEndPoint (User specified first and last frame)");
			final Button CheckResults = new Button("Check Results (then click next)");

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 175);
			panelFifth.add(TrackEndPoints, c);

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 175);
			panelFifth.add(SkipframeandTrackEndPoints, c);

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 0);
			panelFifth.add(Checkres, c);

			++c.gridy;
			c.insets = new Insets(10, 175, 0, 175);
			panelFifth.add(CheckResults, c);

			TrackEndPoints.addActionListener(new TrackendsListener());
			SkipframeandTrackEndPoints.addActionListener(new SkipFramesandTrackendsListener());
			CheckResults.addActionListener(new CheckResultsListener());

		}
		panelFifth.repaint();
		panelFifth.validate();
		Cardframe.pack();
	}

	protected class AnalyzekymoListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent arg0) {

			Kymo();

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
		lengthKymo = new ArrayList<float[]>();
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
						lengthKymo.add(new float[] { cordsLine[0], cordsLine[1] });

				}

			}

		}

		/********
		 * The part below removes the duplicate entries in the array dor the
		 * time co-ordinate
		 ********/

		int j = 0;

		for (int index = 0; index < lengthKymo.size() - 1; ++index) {

			j = index + 1;

			while (j < lengthKymo.size()) {

				if (lengthKymo.get(index)[1] == lengthKymo.get(j)[1]) {

					lengthKymo.remove(index);
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
			bw.write("\tFramenumber\tlengthKymo\n");
			for (int index = 0; index < lengthKymo.size(); ++index) {
				bw.write("\t" + (lengthKymo.get(index)[1]) + "\t" + (lengthKymo.get(index)[0] + "\n"));
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

	public void Deterministic() {

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
		panelFifth.removeAll();

		final Label Step5 = new Label("Step 5", Label.CENTER);
		panelFifth.setLayout(layout);
		panelFifth.add(Step5, c);
		final Button TrackEndPoints = new Button("Track EndPoints (From first to a chosen last frame)");
		final Button SkipframeandTrackEndPoints = new Button("TrackEndPoint (User specified first and last frame)");
		final Button CheckResults = new Button("Check Results (then click next)");
		final Checkbox RoughResults = new Checkbox("Rates and Statistical Analysis");

		final Label Checkres = new Label("The tracker now performs an internal check on the results");
		Checkres.setBackground(new Color(1, 0, 1));
		Checkres.setForeground(new Color(255, 255, 255));
		++c.gridy;
		c.insets = new Insets(10, 10, 0, 175);
		panelFifth.add(TrackEndPoints, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 175);
		panelFifth.add(SkipframeandTrackEndPoints, c);
		if (analyzekymo && Kymoimg != null) {
			++c.gridy;
			c.insets = new Insets(10, 10, 0, 0);
			panelFifth.add(Checkres, c);

			++c.gridy;
			c.insets = new Insets(10, 175, 0, 175);
			panelFifth.add(CheckResults, c);
		}
		++c.gridy;
		c.insets = new Insets(10, 10, 0, 175);
		panelFifth.add(RoughResults, c);

		TrackEndPoints.addActionListener(new TrackendsListener());
		SkipframeandTrackEndPoints.addActionListener(new SkipFramesandTrackendsListener());
		CheckResults.addActionListener(new CheckResultsListener());
		RoughResults.addItemListener(new AcceptResultsListener());
		panelFifth.repaint();
		panelFifth.validate();
		Cardframe.pack();

	}

	protected class DeterchoiceListener implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent arg0) {
			if (arg0.getStateChange() == ItemEvent.DESELECTED)
				showDeterministic = false;
			else if (arg0.getStateChange() == ItemEvent.SELECTED) {

				Deterministic();
			}

		}
	}

	public void Kalman() {

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

		panelFifth.removeAll();
		final Label Step5 = new Label("Step 5", Label.CENTER);
		panelFifth.setLayout(layout);
		panelFifth.add(Step5, c);
		final Scrollbar rad = new Scrollbar(Scrollbar.HORIZONTAL, initialSearchradiusInit, 10, 0, 10 + scrollbarSize);
		initialSearchradius = computeValueFromScrollbarPosition(initialSearchradiusInit, initialSearchradiusMin,
				initialSearchradiusMax, scrollbarSize);

		final Label SearchText = new Label("Initial Search Radius: " + initialSearchradius, Label.CENTER);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 50);
		panelFifth.add(SearchText, c);
		++c.gridy;
		c.insets = new Insets(10, 10, 0, 50);
		panelFifth.add(rad, c);

		final Scrollbar Maxrad = new Scrollbar(Scrollbar.HORIZONTAL, maxSearchradiusInit, 10, 0, 10 + scrollbarSize);
		maxSearchradius = computeValueFromScrollbarPosition(maxSearchradiusInit, maxSearchradiusMin, maxSearchradiusMax,
				scrollbarSize);
		final Label MaxMovText = new Label("Max Movment of Objects per frame: " + maxSearchradius, Label.CENTER);
		++c.gridy;
		c.insets = new Insets(10, 10, 0, 50);
		panelFifth.add(MaxMovText, c);
		++c.gridy;
		c.insets = new Insets(10, 10, 0, 50);
		panelFifth.add(Maxrad, c);

		final Scrollbar Miss = new Scrollbar(Scrollbar.HORIZONTAL, missedframesInit, 10, 0, 10 + scrollbarSize);
		Miss.setBlockIncrement(1);
		missedframes = (int) computeValueFromScrollbarPosition(missedframesInit, missedframesMin, missedframesMax,
				scrollbarSize);
		final Label LostText = new Label("Objects allowed to be lost for #frames" + missedframes, Label.CENTER);
		++c.gridy;
		c.insets = new Insets(10, 10, 0, 50);
		panelFifth.add(LostText, c);
		++c.gridy;
		c.insets = new Insets(10, 10, 0, 50);
		panelFifth.add(Miss, c);

		// final Checkbox Costfunc = new Checkbox("Squared Distance Cost
		// Function");
		// ++c.gridy;
		// c.insets = new Insets(10, 10, 0, 50);
		// panelFifth.add(Costfunc, c);

		rad.addAdjustmentListener(new SearchradiusListener(SearchText, initialSearchradiusMin, initialSearchradiusMax));
		Maxrad.addAdjustmentListener(new maxSearchradiusListener(MaxMovText, maxSearchradiusMin, maxSearchradiusMax));
		Miss.addAdjustmentListener(new missedFrameListener(LostText, missedframesMin, missedframesMax));

		// Costfunc.addItemListener(new CostfunctionListener());

		MTtrackerstart = new KFsearch(AllstartKalman, UserchosenCostFunction, maxSearchradius, initialSearchradius,
				thirdDimension, thirdDimensionSize, missedframes);

		MTtrackerend = new KFsearch(AllendKalman, UserchosenCostFunction, maxSearchradius, initialSearchradius,
				thirdDimension, thirdDimensionSize, missedframes);

		final Button TrackEndPoints = new Button("Track EndPoints (From first to a chosen last frame)");
		final Button SkipframeandTrackEndPoints = new Button("TrackEndPoint (User specified first and last frame)");
		final Button CheckResults = new Button("Check Results (then click next)");
		final Checkbox RoughResults = new Checkbox("Analyze Rates");
		final Label Checkres = new Label("The tracker now performs an internal check on the results");
		Checkres.setBackground(new Color(1, 0, 1));
		Checkres.setForeground(new Color(255, 255, 255));
		++c.gridy;
		c.insets = new Insets(10, 10, 0, 175);
		panelFifth.add(TrackEndPoints, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 175);
		panelFifth.add(SkipframeandTrackEndPoints, c);
		if (analyzekymo && Kymoimg != null) {
			++c.gridy;
			c.insets = new Insets(10, 10, 0, 0);
			panelFifth.add(Checkres, c);

			++c.gridy;
			c.insets = new Insets(10, 175, 0, 175);
			panelFifth.add(CheckResults, c);
		}
		++c.gridy;
		c.insets = new Insets(10, 175, 0, 175);
		panelFifth.add(RoughResults, c);

		TrackEndPoints.addActionListener(new TrackendsListener());
		SkipframeandTrackEndPoints.addActionListener(new SkipFramesandTrackendsListener());
		CheckResults.addActionListener(new CheckResultsListener());
		RoughResults.addItemListener(new AcceptResultsListener());

		panelFifth.repaint();
		panelFifth.validate();
		Cardframe.pack();

	}

	protected class KalmanchoiceListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent arg0) {
			if (arg0.getStateChange() == ItemEvent.DESELECTED) {
				showKalman = false;

			} else if (arg0.getStateChange() == ItemEvent.SELECTED) {

				showKalman = true;
				Kalman();
			}

		}

	}

	protected class moveNextListener implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent arg0) {

			if (thirdDimension > thirdDimensionSize) {
				IJ.log("Max frame number exceeded, moving to last frame instead");
				thirdDimension = thirdDimensionSize;
				CurrentView = util.CopyUtils.getCurrentView(originalimg, thirdDimension, thirdDimensionSize);
				CurrentPreprocessedView = util.CopyUtils.getCurrentPreView(originalPreprocessedimg, thirdDimension,
						thirdDimensionSize);
			} else {

				thirdDimension = thirdDimension + 1;
				CurrentView = util.CopyUtils.getCurrentView(originalimg, thirdDimension, thirdDimensionSize);
				CurrentPreprocessedView = util.CopyUtils.getCurrentPreView(originalPreprocessedimg, thirdDimension,
						thirdDimensionSize);

			}

			updatePreview(ValueChange.THIRDDIM);

			markend();
			UpdateMser();

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
					CurrentView = util.CopyUtils.getCurrentView(originalimg, thirdDimension, thirdDimensionSize);
					CurrentPreprocessedView = util.CopyUtils.getCurrentPreView(originalPreprocessedimg, thirdDimension,
							thirdDimensionSize);

				} else {

					CurrentView = util.CopyUtils.getCurrentView(originalimg, thirdDimension, thirdDimensionSize);
					CurrentPreprocessedView = util.CopyUtils.getCurrentPreView(originalPreprocessedimg, thirdDimension,
							thirdDimensionSize);

				}

				// compute first version
				updatePreview(ValueChange.THIRDDIM);

			}
		}
	}

	protected void markend() {

		preprocessedimp.getCanvas().addMouseListener(ml = new MouseListener() {
			final ImageCanvas canvas = preprocessedimp.getWindow().getCanvas();

			@Override
			public void mouseClicked(MouseEvent e) {
				int x = canvas.offScreenX(e.getX());
				int y = canvas.offScreenY(e.getY());

				overlaysec = preprocessedimp.getOverlay();

				if (overlaysec == null) {
					overlaysec = new Overlay();

					preprocessedimp.setOverlay(overlaysec);

				}
				Roi nearestRoiCurr = util.DrawingUtils.getNearestRois(AllSeedrois, new double[] { x, y });

				Rectangle rect = nearestRoiCurr.getBounds();

				double newx = rect.x + rect.width / 2.0;
				double newy = rect.y + rect.height / 2.0;
				final OvalRoi Bigroi = new OvalRoi(Util.round(newx - 5), Util.round(newy - 5), Util.round(10),
						Util.round(10));
				overlaysec.add(Bigroi);
				ClickedPoints.add(new double[] { newx, newy });
				System.out.println("You chose: " + newx + "," + newy);

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

	protected class chooseendListener implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent arg0) {

			preprocessedimp.getCanvas().addMouseListener(ml = new MouseListener() {
				final ImageCanvas canvas = preprocessedimp.getWindow().getCanvas();

				@Override
				public void mouseClicked(MouseEvent e) {
					int x = canvas.offScreenX(e.getX());
					int y = canvas.offScreenY(e.getY());

					overlaysec = preprocessedimp.getOverlay();

					if (overlaysec == null) {
						overlaysec = new Overlay();

						preprocessedimp.setOverlay(overlaysec);

					}
					Roi nearestRoiCurr = util.DrawingUtils.getNearestRois(AllSeedrois, new double[] { x, y });

					Rectangle rect = nearestRoiCurr.getBounds();

					double newx = rect.x + rect.width / 2.0;
					double newy = rect.y + rect.height / 2.0;
					final OvalRoi Bigroi = new OvalRoi(Util.round(newx - 5), Util.round(newy - 5), Util.round(10),
							Util.round(10));
					overlaysec.add(Bigroi);
					ClickedPoints.add(new double[] { newx, newy });
					System.out.println("You chose: " + newx + "," + newy);
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

	protected class removeendListener implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent arg0) {
			preprocessedimp.getCanvas().removeMouseListener(ml);
			preprocessedimp.getCanvas().addMouseListener(removeml = new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
					final ImageCanvas canvas = preprocessedimp.getWindow().getCanvas();
					int x = canvas.offScreenX(e.getX());
					int y = canvas.offScreenY(e.getY());

					Roi nearestRoiCurr = util.DrawingUtils.getNearestRois(AllSeedrois, new double[] { x, y });

					Rectangle rect = nearestRoiCurr.getBounds();

					double newx = rect.x + rect.width / 2.0;
					double newy = rect.y + rect.height / 2.0;
					final OvalRoi Bigroi = new OvalRoi(Util.round(newx - 5), Util.round(newy - 5), Util.round(10),
							Util.round(10));

					System.out.println("You removed: " + x + "," + y);

					if (Bigroi != null) {
						ClickedPoints.remove(Bigroi);

					}

					if (Bigroi != null) {
						if (overlaysec.contains(Bigroi)) {
							overlaysec.remove(Bigroi);
						}
					}

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
				CurrentView = util.CopyUtils.getCurrentView(originalimg, thirdDimension, thirdDimensionSize);
				CurrentPreprocessedView = util.CopyUtils.getCurrentPreView(originalPreprocessedimg, thirdDimension,
						thirdDimensionSize);
			} else {

				CurrentView = util.CopyUtils.getCurrentView(originalimg, thirdDimension, thirdDimensionSize);
				CurrentPreprocessedView = util.CopyUtils.getCurrentPreView(originalPreprocessedimg, thirdDimension,
						thirdDimensionSize);
			}

			updatePreview(ValueChange.THIRDDIM);
			markend();
			UpdateMser();

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
		displayBitimg = false;
		displayWatershedimg = false;

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
					// updatePreview(ValueChange.SHOWMSER);
					LinefinderInteractiveMSER newlineMser = new LinefinderInteractiveMSER(groundframe, groundframepre,
							newtree, minlength, thirdDimension);

					PrevFrameparam = FindlinesVia.LinefindingMethod(groundframe, groundframepre, minlength,
							thirdDimension, psf, newlineMser, UserChoiceModel.Line, Domask, Intensityratio, Inispacing,
							jpb);
					IJ.log("MSER parameters:" + " " + " thirdDimension: " + " " + thirdDimension);
					IJ.log("Delta " + " " + delta + " " + "minSize " + " " + minSize + " " + "maxSize " + " " + maxSize
							+ " " + " maxVar " + " " + maxVar + " " + "minDIversity " + " " + minDiversity);
					IJ.log("Optimization Parameters: " + "R" + Intensityratio + " G"
							+ Inispacing / Math.min(psf[0], psf[1]));
					ArrayList<KalmanIndexedlength> start = new ArrayList<KalmanIndexedlength>();
					ArrayList<KalmanIndexedlength> end = new ArrayList<KalmanIndexedlength>();

					for (int index = 0; index < PrevFrameparam.getA().size(); ++index) {

						double dx = PrevFrameparam.getA().get(index).ds / Math.sqrt(
								1 + PrevFrameparam.getA().get(index).slope * PrevFrameparam.getA().get(index).slope);
						double dy = PrevFrameparam.getA().get(index).slope * dx;

						KalmanIndexedlength startPart = new KalmanIndexedlength(
								PrevFrameparam.getA().get(index).currentLabel,
								PrevFrameparam.getA().get(index).seedLabel,
								PrevFrameparam.getA().get(index).framenumber, PrevFrameparam.getA().get(index).ds,
								PrevFrameparam.getA().get(index).lineintensity,
								PrevFrameparam.getA().get(index).background,
								PrevFrameparam.getA().get(index).currentpos, PrevFrameparam.getA().get(index).fixedpos,
								PrevFrameparam.getA().get(index).slope, PrevFrameparam.getA().get(index).intercept,
								PrevFrameparam.getA().get(index).slope, PrevFrameparam.getA().get(index).intercept, 0,
								0, new double[] { dx, dy });

						start.add(startPart);
					}
					for (int index = 0; index < PrevFrameparam.getB().size(); ++index) {

						double dx = PrevFrameparam.getB().get(index).ds / Math.sqrt(
								1 + PrevFrameparam.getB().get(index).slope * PrevFrameparam.getB().get(index).slope);
						double dy = PrevFrameparam.getB().get(index).slope * dx;

						KalmanIndexedlength endPart = new KalmanIndexedlength(
								PrevFrameparam.getB().get(index).currentLabel,
								PrevFrameparam.getB().get(index).seedLabel,
								PrevFrameparam.getB().get(index).framenumber, PrevFrameparam.getB().get(index).ds,
								PrevFrameparam.getB().get(index).lineintensity,
								PrevFrameparam.getB().get(index).background,
								PrevFrameparam.getB().get(index).currentpos, PrevFrameparam.getB().get(index).fixedpos,
								PrevFrameparam.getB().get(index).slope, PrevFrameparam.getB().get(index).intercept,
								PrevFrameparam.getB().get(index).slope, PrevFrameparam.getB().get(index).intercept, 0,
								0, new double[] { dx, dy });
						end.add(endPart);
					}

					PrevFrameparamKalman = new ValuePair<ArrayList<KalmanIndexedlength>, ArrayList<KalmanIndexedlength>>(
							start, end);

				}

			}

			if (FindLinesViaHOUGH) {

				boolean dialog = DialogueModelChoice();
				if (dialog) {
					// updatePreview(ValueChange.SHOWHOUGH);
					LinefinderInteractiveHough newlineHough = new LinefinderInteractiveHough(groundframe,
							groundframepre, intimg, Maxlabel, thetaPerPixel, rhoPerPixel, thirdDimension, jpb);

					PrevFrameparam = FindlinesVia.LinefindingMethod(groundframe, groundframepre, minlength,
							thirdDimension, psf, newlineHough, UserChoiceModel.Line, Domask, Intensityratio, Inispacing,
							jpb);
					IJ.log("Hough parameters:" + " " + " thirdDimension: " + " " + thirdDimension);
					IJ.log("thetaPerPixel " + " " + thetaPerPixel + " " + "rhoPerPixel " + " " + rhoPerPixel);
					IJ.log("Optimization Parameters: " + "R" + Intensityratio + " G"
							+ Inispacing / Math.min(psf[0], psf[1]));
					ArrayList<KalmanIndexedlength> start = new ArrayList<KalmanIndexedlength>();
					ArrayList<KalmanIndexedlength> end = new ArrayList<KalmanIndexedlength>();

					for (int index = 0; index < PrevFrameparam.getA().size(); ++index) {

						double dx = PrevFrameparam.getA().get(index).ds / Math.sqrt(
								1 + PrevFrameparam.getA().get(index).slope * PrevFrameparam.getA().get(index).slope);
						double dy = PrevFrameparam.getA().get(index).slope * dx;

						KalmanIndexedlength startPart = new KalmanIndexedlength(
								PrevFrameparam.getA().get(index).currentLabel,
								PrevFrameparam.getA().get(index).seedLabel,
								PrevFrameparam.getA().get(index).framenumber, PrevFrameparam.getA().get(index).ds,
								PrevFrameparam.getA().get(index).lineintensity,
								PrevFrameparam.getA().get(index).background,
								PrevFrameparam.getA().get(index).currentpos, PrevFrameparam.getA().get(index).fixedpos,
								PrevFrameparam.getA().get(index).slope, PrevFrameparam.getA().get(index).intercept,
								PrevFrameparam.getA().get(index).slope, PrevFrameparam.getA().get(index).intercept, 0,
								0, new double[] { dx, dy });

						start.add(startPart);
					}
					for (int index = 0; index < PrevFrameparam.getB().size(); ++index) {

						double dx = PrevFrameparam.getB().get(index).ds / Math.sqrt(
								1 + PrevFrameparam.getB().get(index).slope * PrevFrameparam.getB().get(index).slope);
						double dy = PrevFrameparam.getB().get(index).slope * dx;

						KalmanIndexedlength endPart = new KalmanIndexedlength(
								PrevFrameparam.getB().get(index).currentLabel,
								PrevFrameparam.getB().get(index).seedLabel,
								PrevFrameparam.getB().get(index).framenumber, PrevFrameparam.getB().get(index).ds,
								PrevFrameparam.getB().get(index).lineintensity,
								PrevFrameparam.getB().get(index).background,
								PrevFrameparam.getB().get(index).currentpos, PrevFrameparam.getB().get(index).fixedpos,
								PrevFrameparam.getB().get(index).slope, PrevFrameparam.getB().get(index).intercept,
								PrevFrameparam.getB().get(index).slope, PrevFrameparam.getB().get(index).intercept, 0,
								0, new double[] { dx, dy });
						end.add(endPart);
					}

					PrevFrameparamKalman = new ValuePair<ArrayList<KalmanIndexedlength>, ArrayList<KalmanIndexedlength>>(
							start, end);

				}
			}

			if (FindLinesViaMSERwHOUGH) {
				boolean dialog = DialogueModelChoice();
				if (dialog) {
					// updatePreview(ValueChange.SHOWMSER);
					LinefinderInteractiveMSERwHough newlineMserwHough = new LinefinderInteractiveMSERwHough(groundframe,
							groundframepre, newtree, minlength, thirdDimension, thetaPerPixel, rhoPerPixel);
					IJ.log("MSER parameters:" + " " + " thirdDimension: " + " " + thirdDimension);
					IJ.log("Delta " + " " + delta + " " + "minSize " + " " + minSize + " " + "maxSize " + " " + maxSize
							+ " " + " maxVar " + " " + maxVar + " " + "minDIversity " + " " + minDiversity);
					IJ.log("Hough parameters:" + " " + " thirdDimension: " + " " + thirdDimension);
					IJ.log("thetaPerPixel " + " " + thetaPerPixel + " " + "rhoPerPixel " + " " + rhoPerPixel);
					IJ.log("Optimization Parameters: " + "R" + Intensityratio + " G"
							+ Inispacing / Math.min(psf[0], psf[1]));
					PrevFrameparam = FindlinesVia.LinefindingMethod(groundframe, groundframepre, minlength,
							thirdDimension, psf, newlineMserwHough, UserChoiceModel.Line, Domask, Intensityratio,
							Inispacing, jpb);

					ArrayList<KalmanIndexedlength> start = new ArrayList<KalmanIndexedlength>();
					ArrayList<KalmanIndexedlength> end = new ArrayList<KalmanIndexedlength>();

					for (int index = 0; index < PrevFrameparam.getA().size(); ++index) {

						double dx = PrevFrameparam.getA().get(index).ds / Math.sqrt(
								1 + PrevFrameparam.getA().get(index).slope * PrevFrameparam.getA().get(index).slope);
						double dy = PrevFrameparam.getA().get(index).slope * dx;

						KalmanIndexedlength startPart = new KalmanIndexedlength(
								PrevFrameparam.getA().get(index).currentLabel,
								PrevFrameparam.getA().get(index).seedLabel,
								PrevFrameparam.getA().get(index).framenumber, PrevFrameparam.getA().get(index).ds,
								PrevFrameparam.getA().get(index).lineintensity,
								PrevFrameparam.getA().get(index).background,
								PrevFrameparam.getA().get(index).currentpos, PrevFrameparam.getA().get(index).fixedpos,
								PrevFrameparam.getA().get(index).slope, PrevFrameparam.getA().get(index).intercept,
								PrevFrameparam.getA().get(index).slope, PrevFrameparam.getA().get(index).intercept, 0,
								0, new double[] { dx, dy });

						start.add(startPart);
					}
					for (int index = 0; index < PrevFrameparam.getB().size(); ++index) {

						double dx = PrevFrameparam.getB().get(index).ds / Math.sqrt(
								1 + PrevFrameparam.getB().get(index).slope * PrevFrameparam.getB().get(index).slope);
						double dy = PrevFrameparam.getB().get(index).slope * dx;

						KalmanIndexedlength endPart = new KalmanIndexedlength(
								PrevFrameparam.getB().get(index).currentLabel,
								PrevFrameparam.getB().get(index).seedLabel,
								PrevFrameparam.getB().get(index).framenumber, PrevFrameparam.getB().get(index).ds,
								PrevFrameparam.getB().get(index).lineintensity,
								PrevFrameparam.getB().get(index).background,
								PrevFrameparam.getB().get(index).currentpos, PrevFrameparam.getB().get(index).fixedpos,
								PrevFrameparam.getB().get(index).slope, PrevFrameparam.getB().get(index).intercept,
								PrevFrameparam.getB().get(index).slope, PrevFrameparam.getB().get(index).intercept, 0,
								0, new double[] { dx, dy });
						end.add(endPart);
					}

					PrevFrameparamKalman = new ValuePair<ArrayList<KalmanIndexedlength>, ArrayList<KalmanIndexedlength>>(
							start, end);

				}

			}
			Overlay o = preprocessedimp.getOverlay();

			if (preprocessedimp.getOverlay() == null) {
				o = new Overlay();
				preprocessedimp.setOverlay(o);
			}
			for (int index = 0; index < PrevFrameparam.getA().size(); ++index) {

				Seedroi = new OvalRoi(Util.round(PrevFrameparam.getA().get(index).currentpos[0] - 2.5),
						Util.round(PrevFrameparam.getA().get(index).currentpos[1] - 2.5), Util.round(5), Util.round(5));
				Seedroi.setStrokeColor(Color.GREEN);
				Seedroi.setStrokeWidth(0.8);

				AllSeedrois.add(Seedroi);
				o.add(Seedroi);

			}

			for (int index = 0; index < PrevFrameparam.getB().size(); ++index) {

				Seedroi = new OvalRoi(Util.round(PrevFrameparam.getB().get(index).currentpos[0] - 2.5),
						Util.round(PrevFrameparam.getB().get(index).currentpos[1] - 2.5), Util.round(5), Util.round(5));
				Seedroi.setStrokeColor(Color.GREEN);
				Seedroi.setStrokeWidth(0.8);
				AllSeedrois.add(Seedroi);
				o.add(Seedroi);

			}

			preprocessedimp.updateAndDraw();

			return null;
		}

		@Override
		protected void done() {
			try {
				jpb.setIndeterminate(false);
				get();
				frame.dispose();
				// JOptionPane.showMessageDialog(jpb.getParent(), "End Points
				// found and overlayed", "Success",
				// JOptionPane.INFORMATION_MESSAGE);
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

	/*
	 * protected class CostfunctionListener implements ItemListener {
	 * 
	 * @Override public void itemStateChanged(ItemEvent arg0) {
	 * 
	 * if (arg0.getStateChange() == ItemEvent.SELECTED) {
	 * 
	 * UserchosenCostFunction = new SquareDistCostFunction();
	 * 
	 * }
	 * 
	 * } }
	 */
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

	protected class StatsAnalyzeListener implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent arg0) {

			final GridBagLayout layout = new GridBagLayout();
			final GridBagConstraints c = new GridBagConstraints();

			panelNinth.removeAll();
			final Label Step9 = new Label("Step 9", Label.CENTER);
			panelNinth.setLayout(layout);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 4;
			c.weighty = 1.5;

			panelNinth.add(Step9, c);
			final Label NumberMT = new Label("Get average MT length", Label.CENTER);
			final Button Nlength = new Button("Time averaged MT lengths");

			final Label NumberMTMax = new Label("MT length distribution", Label.CENTER);
			final Button NlengthMax = new Button("Get length distribution");

			inputMaxdpixel = new JLabel("Enter maxLength of MT (pixel units): ");
			Maxdpixel = new TextField();
			Maxdpixel.setColumns(10);

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			panelNinth.add(NumberMT, c);

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			panelNinth.add(Nlength, c);
			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			panelNinth.add(inputMaxdpixel, c);

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			panelNinth.add(Maxdpixel, c);

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			panelNinth.add(NumberMTMax, c);

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			panelNinth.add(NlengthMax, c);
			++c.gridy;

			Nlength.addActionListener(new NlengthListener());

			NlengthMax.addActionListener(new NlengthMaxListener());

		}
	}

	protected class NlengthListener implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent arg0) {
			NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
			nf.setMaximumFractionDigits(3);
			if (Allstart.get(0).size() > 0) {
				int MaxFrame = Allstart.get(Allstart.size() - 1).get(0).Framenumber;
				int MinFrame = Allstart.get(0).get(0).Framenumber;

				final ArrayList<Trackproperties> first = Allstart.get(0);
				Collections.sort(first, Seedcomparetrack);
				int MaxSeedLabel = first.get(first.size() - 1).seedlabel;
				int MinSeedLabel = first.get(0).seedlabel;

				for (int frameindex = MinFrame; frameindex < MaxFrame; ++frameindex) {

					for (int currentseed = MinSeedLabel; currentseed < MaxSeedLabel + 1; ++currentseed) {

						if (pixellength.get(currentseed) != null)
							sumlengthpixel = pixellength.get(currentseed);

						if (microlength.get(currentseed) != null)
							sumlengthmicro = microlength.get(currentseed);

						for (int listindex = 0; listindex < startlengthlist.size(); ++listindex) {

							int currentframe = startlengthlist.get(listindex).framenumber;

							if (currentframe == frameindex) {

								int seedID = startlengthlist.get(listindex).seedid;

								if (seedID == currentseed) {

									sumlengthpixel += startlengthlist.get(listindex).totallengthpixel;
									sumlengthmicro += startlengthlist.get(listindex).totallengthreal;

									pixellength.put(seedID, sumlengthpixel);
									microlength.put(seedID, sumlengthmicro);

								}

							}

						}
					}

				}

				int timeInterval = MaxFrame - MinFrame;
				try {
					File meanfile = new File(usefolder + "//" + addToName + "Start" + "-MeanLength" + ".txt");
					FileWriter fw = new FileWriter(meanfile);
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write("\tSeedLabel\tMeanLength(px)\tMeanLength (real units) \n");

					for (int currentseed = MinSeedLabel; currentseed < MaxSeedLabel + 1; ++currentseed) {
						System.out.println("Seed ID : " + currentseed + " " + "Average Length Pixels "
								+ pixellength.get(currentseed) / timeInterval + " " + "Average Length Real Units "
								+ microlength.get(currentseed) / timeInterval);

						bw.write("\t" + currentseed + "\t" + "\t"

								+ nf.format(pixellength.get(currentseed) / timeInterval) + "\t" + "\t"
								+ nf.format(microlength.get(currentseed) / timeInterval) + "\n");

					}

					bw.close();
					fw.close();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (Allend.get(0).size() > 0) {
				int MaxFrame = Allend.get(Allend.size() - 1).get(0).Framenumber;
				int MinFrame = Allend.get(0).get(0).Framenumber;

				final ArrayList<Trackproperties> first = Allend.get(0);
				Collections.sort(first, Seedcomparetrack);
				int MaxSeedLabel = first.get(first.size() - 1).seedlabel;
				int MinSeedLabel = first.get(0).seedlabel;

				for (int frameindex = MinFrame; frameindex < MaxFrame; ++frameindex) {

					for (int currentseed = MinSeedLabel; currentseed < MaxSeedLabel + 1; ++currentseed) {

						if (pixellength.get(currentseed) != null)
							sumlengthpixel = pixellength.get(currentseed);

						if (microlength.get(currentseed) != null)
							sumlengthmicro = microlength.get(currentseed);

						for (int listindex = 0; listindex < endlengthlist.size(); ++listindex) {

							int currentframe = endlengthlist.get(listindex).framenumber;

							if (currentframe == frameindex) {

								int seedID = endlengthlist.get(listindex).seedid;

								if (seedID == currentseed) {

									sumlengthpixel += endlengthlist.get(listindex).totallengthpixel;
									sumlengthmicro += endlengthlist.get(listindex).totallengthreal;

									pixellength.put(seedID, sumlengthpixel);
									microlength.put(seedID, sumlengthmicro);

								}

							}

						}
					}

				}
				try {
					File meanfile = new File(usefolder + "//" + addToName + "End" + "-MeanLength" + ".txt");
					FileWriter fw = new FileWriter(meanfile);
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write("\tSeedLabel\tMeanLength(px)\tMeanLength (real units) \n");

					int timeInterval = MaxFrame - MinFrame;

					for (int currentseed = MinSeedLabel; currentseed < MaxSeedLabel + 1; ++currentseed) {
						System.out.println("Seed ID : " + currentseed + " " + "Average Length "
								+ pixellength.get(currentseed) / timeInterval + " " + "Average Length Real Units "
								+ microlength.get(currentseed) / timeInterval);

						bw.write("\t" + currentseed + "\t" + "\t"

								+ nf.format(pixellength.get(currentseed) / timeInterval) + "\t" + "\t"
								+ nf.format(microlength.get(currentseed) / timeInterval) + "\n");

					}
					bw.close();
					fw.close();

				} catch (IOException e) {
					e.printStackTrace();
				}

			}

		}

	}

	protected class NlengthMaxListener implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent arg0) {

			int MinFrame = 0;
			int MaxFrame = 0;
			int term = 0;
			ArrayList<MTcounter> ALLcountsstart = new ArrayList<MTcounter>();
			ArrayList<MTcounter> ALLcountsend = new ArrayList<MTcounter>();
			if (Allstart.get(0).size() > 0) {
				MaxFrame = Allstart.get(Allstart.size() - 1).get(0).Framenumber;
				MinFrame = Allstart.get(0).get(0).Framenumber;

				final ArrayList<Trackproperties> first = Allstart.get(0);
				Collections.sort(first, Seedcomparetrack);
				int MaxSeedLabel = first.get(first.size() - 1).seedlabel;
				int MinSeedLabel = first.get(0).seedlabel;

				for (int frameindex = MinFrame; frameindex < MaxFrame; ++frameindex) {

					for (int maxlength = 0; maxlength < Double.parseDouble(Maxdpixel.getText()); maxlength += 5) {

						int MTcount = 0;

						for (int currentseed = MinSeedLabel; currentseed < MaxSeedLabel + 1; ++currentseed) {

							for (int listindex = 0; listindex < startlengthlist.size(); ++listindex) {

								int currentframe = startlengthlist.get(listindex).framenumber;

								if (currentframe == frameindex) {

									int seedID = startlengthlist.get(listindex).seedid;

									if (seedID == currentseed) {

										double pixellength = startlengthlist.get(listindex).totallengthpixel;

										if (pixellength >= maxlength) {

											MTcount++;

										}

									}

								}

							}

						}

						MTcounter newcounter = new MTcounter(frameindex, MTcount, maxlength);

						ALLcountsstart.add(newcounter);

					}
				}

			}
			term = 0;
			if (Allend.get(0).size() > 0) {
				MaxFrame = Allend.get(Allend.size() - 1).get(0).Framenumber;
				MinFrame = Allend.get(0).get(0).Framenumber;

				final ArrayList<Trackproperties> first = Allend.get(0);
				Collections.sort(first, Seedcomparetrack);
				int MaxSeedLabel = first.get(first.size() - 1).seedlabel;
				int MinSeedLabel = first.get(0).seedlabel;

				for (int frameindex = MinFrame; frameindex < MaxFrame; ++frameindex) {

					for (int maxlength = 0; maxlength < Double.parseDouble(Maxdpixel.getText()); maxlength += 5) {

						int MTcount = 0;

						for (int currentseed = MinSeedLabel; currentseed < MaxSeedLabel + 1; ++currentseed) {

							for (int listindex = 0; listindex < endlengthlist.size(); ++listindex) {

								int currentframe = endlengthlist.get(listindex).framenumber;

								if (currentframe == frameindex) {

									int seedID = endlengthlist.get(listindex).seedid;

									if (seedID == currentseed) {

										double pixellength = endlengthlist.get(listindex).totallengthpixel;

										if (pixellength >= maxlength) {

											MTcount++;

										}

									}

								}

							}

						}

						MTcounter newcounter = new MTcounter(frameindex, MTcount, maxlength);

						ALLcountsend.add(newcounter);

					}
				}

			}

			if (ALLcountsstart.size() > 0 && ALLcountsend.size() > 0) {

				for (int index = 0; index < ALLcountsstart.size(); ++index) {

					for (int secindex = 0; secindex < ALLcountsend.size(); ++secindex) {

						if (ALLcountsstart.get(index).framenumber == ALLcountsend.get(secindex).framenumber
								&& ALLcountsstart.get(index).maxlength == ALLcountsend.get(secindex).maxlength) {
							MTcounter newcounter = new MTcounter(ALLcountsstart.get(index).framenumber,
									ALLcountsstart.get(index).MTcountnumber + ALLcountsend.get(secindex).MTcountnumber,
									ALLcountsstart.get(index).maxlength);
							ALLcounts.add(newcounter);

						}

					}

				}

			}

			else

				ALLcounts = (ALLcountsstart.size() == 0) ? ALLcountsend : ALLcountsstart;

			NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
			nf.setMaximumFractionDigits(3);
			try {
				File newfile = new File(usefolder + "//" + addToName + "STAT" + "N_L" + ".txt");

				FileWriter fw;

				fw = new FileWriter(newfile);

				BufferedWriter bw = new BufferedWriter(fw);

				bw.write("\tFramenumber\tMTcount\tMaxLength\n");

				for (int index = 0; index < ALLcounts.size(); ++index) {

					System.out.println("\t" + nf.format(ALLcounts.get(index).framenumber) + "\t" + "\t"
							+ nf.format(ALLcounts.get(index).MTcountnumber) + "\t" + "\t"
							+ nf.format(ALLcounts.get(index).maxlength) + "\n");

					bw.write("\t" + nf.format(ALLcounts.get(index).framenumber) + "\t" + "\t"
							+ nf.format(ALLcounts.get(index).MTcountnumber) + "\t" + "\t"
							+ nf.format(ALLcounts.get(index).maxlength) + "\n");

				}

				bw.close();
				fw.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	protected class AnalyzeListener implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent arg0) {

			if (analyzekymo)
				numberKymo = true;

			ArrayList<float[]> deltaL = new ArrayList<>();
			if (numberKymo) {

				for (int index = 1; index < lengthKymo.size(); ++index) {

					float delta = lengthKymo.get(index)[0] - lengthKymo.get(index - 1)[0];

					float[] deltalt = { delta, lengthKymo.get(index)[1] };

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
			if (lengthtimestart != null) {
				for (int index = 1; index < lengthtimestart.size(); ++index) {

					if ((int) lengthtimestart.get(index)[2] == selectedSeed) {
						float delta = (float) (lengthtimestart.get(index)[0] - lengthtimestart.get(index - 1)[0]);

						float[] deltalt = { delta, (int) lengthtimestart.get(index)[1], selectedSeed };

						deltaLMT.add(deltalt);

					}

				}
			}
			if (lengthtimeend != null) {
				for (int index = 1; index < lengthtimeend.size(); ++index) {
					if ((int) lengthtimeend.get(index)[2] == selectedSeed) {
						float delta = (float) (lengthtimeend.get(index)[0] - lengthtimeend.get(index - 1)[0]);

						float[] deltalt = { delta, (int) lengthtimeend.get(index)[1], selectedSeed };

						deltaLMT.add(deltalt);

					}
				}
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

			float[] rates = { starttime, endtime, (float) velocity, (float) (velocity * calibration[0] / frametosec),
					selectedSeed };
			finalvelocity.add(rates);

			FileWriter vw;
			File fichierKyvel = new File(usefolder + "//" + addToName + "MTtracker-velocity" + ".txt");
			try {
				vw = new FileWriter(fichierKyvel);
				BufferedWriter bvw = new BufferedWriter(vw);
				bvw.write(
						"\tStarttime\tEndtime\tRate(velocity pixel units)\tRate (velocity in real units)\tSelectedSeed\n");
				for (int i = 0; i < finalvelocity.size(); ++i) {

					System.out.println("MT tracker: " + "\t" + finalvelocity.get(i)[0] + "\t" + finalvelocity.get(i)[1]
							+ "\t" + finalvelocity.get(i)[2] + "\t" + finalvelocity.get(i)[3] + "\t"
							+ finalvelocity.get(i)[4] + "\n");

					IJ.log("MT tracker: " + "\t" + finalvelocity.get(i)[0] + "\t" + finalvelocity.get(i)[1] + "\t"
							+ finalvelocity.get(i)[2] + "\t" + finalvelocity.get(i)[3] + "\t" + finalvelocity.get(i)[4]
							+ "\n");

					bvw.write("\t" + finalvelocity.get(i)[0] + "\t" + finalvelocity.get(i)[1] + "\t"
							+ finalvelocity.get(i)[2] + "\t" + finalvelocity.get(i)[3] + "\t" + finalvelocity.get(i)[4]
							+ "\n");

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

	Comparator<Indexedlength> Seedcompare = new Comparator<Indexedlength>() {

		@Override
		public int compare(final Indexedlength A, final Indexedlength B) {

			return A.seedLabel - B.seedLabel;

		}

	};

	Comparator<Trackproperties> Seedcomparetrack = new Comparator<Trackproperties>() {

		@Override
		public int compare(final Trackproperties A, final Trackproperties B) {

			return A.seedlabel - B.seedlabel;

		}

	};

	protected class finalpoint implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent arg0) {
			if (arg0.getStateChange() == ItemEvent.DESELECTED)
				finalpoint = false;

			else if (arg0.getStateChange() == ItemEvent.SELECTED) {
				finalpoint = true;

				preprocessedimp.getCanvas().removeMouseListener(removeml);
				preprocessedimp.getCanvas().removeMouseListener(ml);

				HashMap<Integer, double[]> endAmap = new HashMap<Integer, double[]>();

				HashMap<Integer, double[]> endBmap = new HashMap<Integer, double[]>();

				Collections.sort(PrevFrameparam.getA(), Seedcompare);
				Collections.sort(PrevFrameparam.getB(), Seedcompare);

				int minSeed = PrevFrameparam.getA().get(0).seedLabel;
				int maxSeed = PrevFrameparam.getA().get(PrevFrameparam.getA().size() - 1).seedLabel;

				for (int i = 0; i < PrevFrameparam.getA().size(); ++i) {

					endAmap.put(PrevFrameparam.getA().get(i).seedLabel, PrevFrameparam.getA().get(i).fixedpos);

				}

				for (int i = 0; i < PrevFrameparam.getB().size(); ++i) {

					endBmap.put(PrevFrameparam.getB().get(i).seedLabel, PrevFrameparam.getB().get(i).fixedpos);

				}

				for (int i = minSeed; i < maxSeed + 1; ++i) {

					for (int index = 0; index < ClickedPoints.size(); ++index) {

						double mindistA = 0;
						double mindistB = 0;

						mindistA = util.Boundingboxes.Distance(ClickedPoints.get(index), endAmap.get(i));
						mindistB = util.Boundingboxes.Distance(ClickedPoints.get(index), endBmap.get(i));

						if (mindistA <= 1 && seedmap.get(i) != Whichend.end) {

							seedmap.put(i, Whichend.start);

						}

						else if (mindistB <= 1 && seedmap.get(i) != Whichend.start) {

							seedmap.put(i, Whichend.end);
						}

						else if (seedmap.get(i) == Whichend.start && mindistB <= 1)
							seedmap.put(i, Whichend.both);

						else if (seedmap.get(i) == Whichend.end && mindistA <= 1)
							seedmap.put(i, Whichend.both);

						else if (seedmap.get(i) == null)
							seedmap.put(i, Whichend.none);

						System.out.println(seedmap.get(i) + "" + i);
					}

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
				CurrentPreprocessedView = util.CopyUtils.getCurrentPreView(originalPreprocessedimg, thirdDimension,
						thirdDimensionSize);
				currentPreprocessedimg = util.CopyUtils.extractImage(CurrentPreprocessedView, interval);
				updatePreview(ValueChange.MEDIAN);

			}
		}

	}

	public ImagePlus getImp() {
		return this.imp;
	}

	protected class AcceptResultsListener implements ItemListener {

		@Override
		public void itemStateChanged(final ItemEvent arg0) {

			if (arg0.getStateChange() == ItemEvent.SELECTED) {

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

				final Label Optional = new Label("Do rate analysis for MT of your choosing (optional)", Label.CENTER);
				Optional.setBackground(new Color(1, 0, 1));
				Optional.setForeground(new Color(255, 255, 255));

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
				final Label Done = new Label("Proceed to Statistical analysis", Label.CENTER);
				Done.setBackground(new Color(1, 0, 1));
				Done.setForeground(new Color(255, 255, 255));

				final Button Stats = new Button("Do Statistical analysis");
				JLabel lbl = new JLabel("Select the seedID of the MT for analysis");

				String[] choices = new String[IDALL.size()];

				JLabel lbltrack = new JLabel("Select the seedID of the MT for displaying tracks");

				String[] choicestrack = new String[IDALL.size() + 1];
				choicestrack[0] = "Display All";
				Comparator<Pair<Integer, double[]>> seedIDcomparison = new Comparator<Pair<Integer, double[]>>() {

					@Override
					public int compare(final Pair<Integer, double[]> A, final Pair<Integer, double[]> B) {

						return A.getA() - B.getA();

					}

				};

				Collections.sort(IDALL, seedIDcomparison);
				for (int index = 0; index < IDALL.size(); ++index) {

					String currentseed = Double.toString(IDALL.get(index).getA());

					choices[index] = "Seed " + currentseed;
					choicestrack[index + 1] = "Seed " + currentseed;
				}

				JComboBox<String> cb = new JComboBox<String>(choices);

				JComboBox<String> cbtrack = new JComboBox<String>(choicestrack);

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelEighth.add(Optional, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelEighth.add(lbl, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelEighth.add(cb, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelEighth.add(lbltrack, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelEighth.add(cbtrack, c);

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

				// ++c.gridy;
				// c.insets = new Insets(10, 10, 0, 50);
				// panelEighth.add(Stats, c);

				startS.addAdjustmentListener(new starttimeListener(startText, thirdDimensionsliderInit,
						thirdDimensionSize, scrollbarSize, startS));
				endS.addAdjustmentListener(new endtimeListener(endText, thirdDimensionsliderInit, thirdDimensionSize,
						scrollbarSize, endS));
				Analyze.addActionListener(new AnalyzeListener());
				Stats.addActionListener(new StatsAnalyzeListener());
				cb.addActionListener(new SeedchoiceListener(cb));

				cbtrack.addActionListener(new SeedDisplayListener(cbtrack, Views.hyperSlice(originalimg, 2, 1)));
				panelEighth.validate();
				panelEighth.repaint();
				Cardframe.pack();
				// Stat Analysis

				panelNinth.removeAll();

				panelNinth.setLayout(layout);
				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridx = 0;
				c.gridy = 0;
				c.weightx = 4;
				c.weighty = 1.5;
				final Label Step9 = new Label("Step 9", Label.CENTER);
				panelNinth.add(Step9, c);
				final Button Nlength = new Button("Time averaged MT lengths");

				final Button NlengthMax = new Button("Get length distribution");

				inputMaxdpixel = new JLabel("Enter maxLength of MT (pixel units): ");
				Maxdpixel = new TextField();
				Maxdpixel.setColumns(10);

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelNinth.add(Nlength, c);
				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelNinth.add(inputMaxdpixel, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelNinth.add(Maxdpixel, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				panelNinth.add(NlengthMax, c);
				++c.gridy;

				Nlength.addActionListener(new NlengthListener());

				NlengthMax.addActionListener(new NlengthMaxListener());
				panelNinth.validate();
				panelNinth.repaint();
				Cardframe.pack();
			}
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

				Cardframe.pack();
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

				// Costfunc.addItemListener(new CostfunctionListener());

				MTtrackerstart = new KFsearch(AllstartKalman, UserchosenCostFunction, maxSearchradius,
						initialSearchradius, thirdDimension, thirdDimensionSize, missedframes);

				MTtrackerend = new KFsearch(AllendKalman, UserchosenCostFunction, maxSearchradius, initialSearchradius,
						thirdDimension, thirdDimensionSize, missedframes);

				final Button TrackEndPoints = new Button("Track EndPoints (From first to a chosen last frame)");
				final Button SkipframeandTrackEndPoints = new Button(
						"TrackEndPoint (User specified first and last frame)");
				final Button CheckResults = new Button("Check Results (then click next)");
				final Checkbox AcceptResults = new Checkbox("Accept Results");
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
				AcceptResults.addItemListener(new AcceptResultsListener());

				RefuseResults.addActionListener(new RefuseResultsListener());

				panelEighth.validate();
				panelEighth.repaint();
				Cardframe.pack();

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

				final Checkbox AcceptResults = new Checkbox("Accept Results");
				final Button RefuseResults = new Button("Refuse Results, do over");
				++c.gridy;
				c.insets = new Insets(10, 175, 0, 200);
				panelEighth.add(AcceptResults, c);
				++c.gridy;
				c.insets = new Insets(10, 175, 0, 200);
				panelEighth.add(RefuseResults, c);

				AcceptResults.addItemListener(new AcceptResultsListener());
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
				if (analyzekymo && Kymoimg != null) {
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
				Cardframe.pack();
			}

		}

	}

	public class SeedchoiceListener implements ActionListener {

		final JComboBox<String> cb;

		public SeedchoiceListener(JComboBox<String> cb) {

			this.cb = cb;

		}

		@Override
		public void actionPerformed(final ActionEvent arg0) {

			selectedSeed = cb.getSelectedIndex();

		}

	}

	public class SeedDisplayListener implements ActionListener {

		final JComboBox<String> cb;
		final RandomAccessibleInterval<FloatType> seedimg;

		public SeedDisplayListener(JComboBox<String> cb, RandomAccessibleInterval<FloatType> seedimg) {

			this.cb = cb;
			this.seedimg = seedimg;

		}

		@Override
		public void actionPerformed(final ActionEvent arg0) {

			displayselectedSeed = cb.getSelectedIndex();

			ImagePlus displayimp;

			displayimp = ImageJFunctions.show(seedimg);
			displayimp.setTitle("Display Tracks");

			Overlay o = displayimp.getOverlay();

			if (displayimp.getOverlay() == null) {
				o = new Overlay();
				displayimp.setOverlay(o);
			}

			o.clear();

			for (int index = 0; index < IDALL.size(); ++index) {

				Line newellipse = new Line(IDALL.get(index).getB()[0], IDALL.get(index).getB()[1],
						IDALL.get(index).getB()[0], IDALL.get(index).getB()[1]);

				if (displayselectedSeed == 0) {
					newellipse.setStrokeColor(Color.WHITE);
					newellipse.setStrokeWidth(1);
					newellipse.setName("TrackID: " + IDALL.get(index).getA());

					o.add(newellipse);

					o.drawLabels(true);

					o.drawNames(true);
				} else if (displayselectedSeed == index + 1) {

					newellipse.setStrokeColor(Color.WHITE);
					newellipse.setStrokeWidth(1);
					newellipse.setName("TrackID: " + IDALL.get(index).getA());

					o.add(newellipse);

					o.drawLabels(true);

					o.drawNames(true);

				}

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

			thirdDimensionSize = thirdDimensionSizeOriginal;

			moveDialogue();

			int next = thirdDimension;

			if (next < 2)
				next = 2;

			Track(next);

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

			Track(next);

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

			Logger.getLogger(Interactive_MTDoubleChannel.class.getName()).log(Level.SEVERE, null, ex);
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
			Logger.getLogger(Interactive_MTDoubleChannel.class.getName()).log(Level.SEVERE, null, ex);

		}

	}

	public void Track(final int next) {

		maxStack();
		int Kalmancount = 0;

		for (int index = next; index <= thirdDimensionSize; ++index) {

			Kalmancount++;
			thirdDimension = index;
			isStarted = true;
			CurrentPreprocessedView = util.CopyUtils.getCurrentPreView(originalPreprocessedimg, thirdDimension,
					thirdDimensionSize);
			CurrentView = util.CopyUtils.getCurrentView(originalimg, thirdDimension, thirdDimensionSize);
			updatePreview(ValueChange.THIRDDIMTrack);

			boolean dialog;
			boolean dialogupdate;

			RandomAccessibleInterval<FloatType> groundframe = currentimg;
			RandomAccessibleInterval<FloatType> groundframepre = currentPreprocessedimg;
			if (FindLinesViaMSER) {
				if (index == next) {
					dialog = DialogueModelChoiceHF();

					IJ.log("MSER parameters:" + " " + " thirdDimension: " + " " + thirdDimension);
					IJ.log("Delta " + " " + delta + " " + "minSize " + " " + minSize + " " + "maxSize " + " " + maxSize
							+ " " + " maxVar " + " " + maxVar + " " + "minDIversity " + " " + minDiversity);

					IJ.log("Optimization Parameters: " + "R" + Intensityratio + " G"
							+ Inispacing / Math.min(psf[0], psf[1]));

				}

				else
					dialog = false;

				updatePreview(ValueChange.SHOWMSER);

				LinefinderInteractiveHFMSER newlineMser = new LinefinderInteractiveHFMSER(groundframe, groundframepre,
						newtree, minlength, thirdDimension);
				if (showDeterministic) {
					returnVector = FindlinesVia.LinefindingMethodHF(groundframe, groundframepre, PrevFrameparam,
							minlength, thirdDimension, psf, newlineMser, userChoiceModel, Domask, Intensityratio,
							Inispacing, seedmap, jpb, thirdDimensionSize);
					Accountedframes.add(FindlinesVia.getAccountedframes());
				}

				if (showKalman) {
					returnVectorKalman = FindlinesVia.LinefindingMethodHFKalman(groundframe, groundframepre,
							PrevFrameparamKalman, minlength, thirdDimension, psf, newlineMser, userChoiceModel, Domask,
							Kalmancount, Intensityratio, Inispacing, seedmap, jpb, thirdDimensionSize);

					Accountedframes.add(FindlinesVia.getAccountedframes());
				}

			}

			if (FindLinesViaHOUGH) {

				if (index == next) {
					dialog = DialogueModelChoiceHF();

					IJ.log("Hough parameters:" + " " + " thirdDimension: " + " " + thirdDimension);
					IJ.log("thetaPerPixel " + " " + thetaPerPixel + " " + "rhoPerPixel " + " " + rhoPerPixel);
					IJ.log("Optimization Parameters: " + "R" + Intensityratio + " G"
							+ Inispacing / Math.min(psf[0], psf[1]));

				}

				else
					dialog = false;

				updatePreview(ValueChange.SHOWHOUGH);
				LinefinderInteractiveHFHough newlineHough = new LinefinderInteractiveHFHough(groundframe,
						groundframepre, intimg, Maxlabel, thetaPerPixel, rhoPerPixel, thirdDimension);
				if (showDeterministic) {
					returnVector = FindlinesVia.LinefindingMethodHF(groundframe, groundframepre, PrevFrameparam,
							minlength, thirdDimension, psf, newlineHough, userChoiceModel, Domask, Intensityratio,
							Inispacing, seedmap, jpb, thirdDimensionSize);

					Accountedframes.add(FindlinesVia.getAccountedframes());
				}

				if (showKalman) {
					returnVectorKalman = FindlinesVia.LinefindingMethodHFKalman(groundframe, groundframepre,
							PrevFrameparamKalman, minlength, thirdDimension, psf, newlineHough, userChoiceModel, Domask,
							Kalmancount, Intensityratio, Inispacing, seedmap, jpb, thirdDimensionSize);
					Accountedframes.add(FindlinesVia.getAccountedframes());
				}

			}

			if (FindLinesViaMSERwHOUGH) {
				if (index == next) {
					dialog = DialogueModelChoice();

					IJ.log("MSER parameters:" + " " + " thirdDimension: " + " " + thirdDimension);
					IJ.log("Delta " + " " + delta + " " + "minSize " + " " + minSize + " " + "maxSize " + " " + maxSize
							+ " " + " maxVar " + " " + maxVar + " " + "minDIversity " + " " + minDiversity);
					IJ.log("Hough parameters:" + " " + " thirdDimension: " + " " + thirdDimension);
					IJ.log("thetaPerPixel " + " " + thetaPerPixel + " " + "rhoPerPixel " + " " + rhoPerPixel);
					IJ.log("Optimization Parameters: " + "R" + Intensityratio + " G"
							+ Inispacing / Math.min(psf[0], psf[1]));

				} else
					dialog = false;

				updatePreview(ValueChange.SHOWMSER);
				LinefinderInteractiveHFMSERwHough newlineMserwHough = new LinefinderInteractiveHFMSERwHough(groundframe,
						groundframepre, newtree, minlength, thirdDimension, thetaPerPixel, rhoPerPixel);
				if (showDeterministic) {
					returnVector = FindlinesVia.LinefindingMethodHF(groundframe, groundframepre, PrevFrameparam,
							minlength, thirdDimension, psf, newlineMserwHough, userChoiceModel, Domask, Intensityratio,
							Inispacing, seedmap, jpb, thirdDimensionSize);

					Accountedframes.add(FindlinesVia.getAccountedframes());
				}
				if (showKalman) {
					returnVectorKalman = FindlinesVia.LinefindingMethodHFKalman(groundframe, groundframepre,
							PrevFrameparamKalman, minlength, thirdDimension, psf, newlineMserwHough, userChoiceModel,
							Domask, Kalmancount, Intensityratio, Inispacing, seedmap, jpb, thirdDimensionSize);

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

			if (Allstart.get(0).size() > 0) {
				ImagePlus impstartsec = ImageJFunctions.show(originalimg);
				final Trackstart trackerstart = new Trackstart(Allstart, thirdDimensionSize - next);
				trackerstart.process();
				SimpleWeightedGraph<double[], DefaultWeightedEdge> graphstart = trackerstart.getResult();
				ArrayList<Pair<Integer, double[]>> ID = trackerstart.getSeedID();
				DisplayGraph displaygraphtrackstart = new DisplayGraph(impstartsec, graphstart, ID);
				IDALL.addAll(ID);
				displaygraphtrackstart.getImp();
				impstartsec.draw();
				impstartsec.setTitle("Graph Start A MT");
			}
			if (Allend.get(0).size() > 0) {
				ImagePlus impendsec = ImageJFunctions.show(originalimg);
				final Trackend trackerend = new Trackend(Allend, thirdDimensionSize - next);

				trackerend.process();
				SimpleWeightedGraph<double[], DefaultWeightedEdge> graphend = trackerend.getResult();
				ArrayList<Pair<Integer, double[]>> ID = trackerend.getSeedID();
				DisplayGraph displaygraphtrackend = new DisplayGraph(impendsec, graphend, ID);
				IDALL.addAll(ID);
				displaygraphtrackend.getImp();
				impendsec.draw();
				impendsec.setTitle("Graph Start B MT");
			}

		}

		if (showKalman) {

			ResultsTable rtAll = new ResultsTable();
			if (AllstartKalman.get(0).size() > 0) {

				MTtrackerstart = new KFsearch(AllstartKalman, UserchosenCostFunction, maxSearchradius,
						initialSearchradius, thirdDimension, thirdDimensionSize, missedframes);
				MTtrackerstart.reset();
				MTtrackerstart.process();

				ImagePlus impstartsecKalman = ImageJFunctions.show(originalimg);

				impstartsecKalman.setTitle("Kalman Graph Start A MT");
				SimpleWeightedGraph<KalmanTrackproperties, DefaultWeightedEdge> graphstartKalman = MTtrackerstart
						.getResult();

				DisplayGraphKalman Startdisplaytracks = new DisplayGraphKalman(impstartsecKalman, graphstartKalman);
				Startdisplaytracks.getImp();
				impstartsecKalman.draw();

				TrackModel modelstart = new TrackModel(graphstartKalman);
				modelstart.getDirectedNeighborIndex();

				// Get all the track id's
				for (final Integer id : modelstart.trackIDs(true)) {
					if (SaveTxt) {
						try {
							File fichier = new File(usefolder + "//" + addToName + "Trackid" + id + "-endA" + ".txt");

							FileWriter fw = new FileWriter(fichier);
							BufferedWriter bw = new BufferedWriter(fw);

							bw.write(
									"\tFramenumber\tTotal Length (pixel)\tTotal Length (real)\tSeed iD\tCurrentPosition X (px units)\tCurrentPosition Y (px units)\tCurrentPosition X (real units)\tCurrentPosition Y (real units)"
											+ "\tLength per frame (px units)" + "\tLength per frame (real units)\n");

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
							double startlengthreal = 0;
							double startlengthpixel = 0;
							for (int index = 1; index < list.size() - 1; ++index) {

								final double[] currentpoint = list.get(index).currentpoint;
								final double[] oldpoint = list.get(index - 1).currentpoint;
								final double[] currentpointCal = new double[] { currentpoint[0] * calibration[0],
										currentpoint[1] * calibration[1] };
								final double[] oldpointCal = new double[] { oldpoint[0] * calibration[0],
										oldpoint[1] * calibration[1] };
								final double lengthpixelperframe = util.Boundingboxes.Distance(currentpoint, oldpoint);
								final double lengthrealperframe = util.Boundingboxes.Distance(currentpointCal,
										oldpointCal);
								final double seedtocurrent = util.Boundingboxes.Distancesq(originalpoint, currentpoint);
								final double seedtoold = util.Boundingboxes.Distancesq(originalpoint, oldpoint);
								final boolean shrink = seedtoold > seedtocurrent ? true : false;
								final boolean growth = seedtoold > seedtocurrent ? false : true;

								if (shrink) {
									// MT shrank

									startlengthreal -= lengthrealperframe;
									startlengthpixel -= lengthpixelperframe;

								}
								if (growth) {

									// MT grew
									startlengthreal += lengthrealperframe;
									startlengthpixel += lengthpixelperframe;

								}

								double[] currentlocationpixel = new double[ndims];

								if (list.get(index).thirdDimension == thirdDimensionsliderInit)
									currentlocationpixel = originalpoint;
								else
									currentlocationpixel = currentpoint;

								double[] currentlocationreal = new double[ndims];

								currentlocationreal = new double[] { currentlocationpixel[0] * calibration[0],
										currentlocationpixel[1] * calibration[1] };

								ResultsMT startMT = new ResultsMT(list.get(index).thirdDimension, startlengthpixel,
										startlengthreal, id, currentlocationpixel, currentlocationreal,
										lengthpixelperframe, lengthrealperframe);

								startlengthlist.add(startMT);

								bw.write("\t" + list.get(index).thirdDimension + "\t" + "\t"
										+ nf.format(startlengthpixel) + "\t" + "\t" + nf.format(startlengthreal) + "\t"
										+ "\t" + nf.format(id) + "\t" + "\t" + nf.format(currentlocationpixel[0]) + "\t"
										+ "\t" + nf.format(currentlocationpixel[1]) + "\t" + "\t"
										+ nf.format(currentlocationreal[0]) + "\t" + "\t"
										+ nf.format(currentlocationreal[1]) + "\t" + "\t"
										+ nf.format(lengthpixelperframe) + "\t" + "\t" + nf.format(lengthrealperframe)
										+ "\n");

								double[] landt = { startlengthpixel, list.get(index).thirdDimension, id };
								lengthtimestart.add(landt);
								rtAll.incrementCounter();
								rtAll.addValue("FrameNumber", list.get(index).thirdDimension);
								rtAll.addValue("Total Length (pixel)", startlengthpixel);
								rtAll.addValue("Total Length (real)", startlengthreal);
								rtAll.addValue("Track iD", id);
								rtAll.addValue("CurrentPosition X (px units)", currentlocationpixel[0]);
								rtAll.addValue("CurrentPosition Y (px units)", currentlocationpixel[1]);
								rtAll.addValue("CurrentPosition X (real units)", currentlocationreal[0]);
								rtAll.addValue("CurrentPosition Y (real units)", currentlocationreal[1]);
								rtAll.addValue("Length per frame (px units)", lengthpixelperframe);
								rtAll.addValue("Length per frame (real units)", lengthrealperframe);

							}

							bw.close();
							fw.close();

						} catch (IOException e) {
						}
					}

				}
			}

			if (AllendKalman.get(0).size() > 0) {

				ImagePlus impendKalman = ImageJFunctions.show(originalimg);

				MTtrackerend = new KFsearch(AllendKalman, UserchosenCostFunction, maxSearchradius, initialSearchradius,
						thirdDimension, thirdDimensionSize, missedframes);

				MTtrackerend.reset();
				MTtrackerend.process();
				SimpleWeightedGraph<KalmanTrackproperties, DefaultWeightedEdge> graphendKalman = MTtrackerend
						.getResult();

				impendKalman.draw();

				ImagePlus impendsecKalman = ImageJFunctions.show(originalimg);
				impendsecKalman.setTitle("Kalman Graph Start B MT");
				DisplayGraphKalman Enddisplaytracks = new DisplayGraphKalman(impendsecKalman, graphendKalman);
				Enddisplaytracks.getImp();
				impendsecKalman.draw();
				TrackModel modelend = new TrackModel(graphendKalman);
				modelend.getDirectedNeighborIndex();
				// Get all the track id's

				for (final Integer id : modelend.trackIDs(true)) {

					if (SaveTxt) {
						try {
							File fichier = new File(usefolder + "//" + addToName + "Trackid" + id + "-endB" + ".txt");

							FileWriter fw = new FileWriter(fichier);
							BufferedWriter bw = new BufferedWriter(fw);

							bw.write(
									"\tFramenumber\tTotal Length (pixel)\tTotal Length (real)\tSeed iD\tCurrentPosition X (px units)\tCurrentPosition Y (px units)\tCurrentPosition X (real units)\tCurrentPosition Y (real units)"
											+ "\tLength per frame (px units)" + "\tLength per frame (real units)\n");
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

							double endlengthreal = 0;
							double endlengthpixel = 0;
							final double[] originalpoint = list.get(0).originalpoint;
							for (int index = 1; index < list.size() - 1; ++index) {

								final double[] currentpoint = list.get(index).currentpoint;
								final double[] oldpoint = list.get(index - 1).currentpoint;
								final double[] currentpointCal = new double[] { currentpoint[0] * calibration[0],
										currentpoint[1] * calibration[1] };
								final double[] oldpointCal = new double[] { oldpoint[0] * calibration[0],
										oldpoint[1] * calibration[1] };
								final double lengthpixelperframe = util.Boundingboxes.Distance(currentpoint, oldpoint);
								final double lengthrealperframe = util.Boundingboxes.Distance(currentpointCal,
										oldpointCal);
								final double seedtocurrent = util.Boundingboxes.Distancesq(originalpoint, currentpoint);
								final double seedtoold = util.Boundingboxes.Distancesq(originalpoint, oldpoint);
								final boolean shrink = seedtoold > seedtocurrent ? true : false;
								final boolean growth = seedtoold > seedtocurrent ? false : true;

								if (shrink) {

									// MT shrank
									endlengthreal -= lengthrealperframe;
									endlengthpixel -= lengthpixelperframe;

								}
								if (growth) {

									// MT grew
									endlengthreal += lengthrealperframe;
									endlengthpixel += lengthpixelperframe;

								}

								double[] currentlocationpixel = new double[ndims];

								if (list.get(index).thirdDimension == thirdDimensionsliderInit)
									currentlocationpixel = originalpoint;
								else
									currentlocationpixel = currentpoint;

								double[] currentlocationreal = new double[ndims];

								currentlocationreal = new double[] { currentlocationpixel[0] * calibration[0],
										currentlocationpixel[1] * calibration[1] };

								ResultsMT endMT = new ResultsMT(list.get(index).thirdDimension, endlengthpixel,
										endlengthreal, id, currentlocationpixel, currentlocationreal,
										lengthpixelperframe, lengthrealperframe);

								endlengthlist.add(endMT);

								bw.write("\t" + list.get(index).thirdDimension + "\t" + "\t" + nf.format(endlengthpixel)
										+ "\t" + "\t" + nf.format(endlengthreal) + "\t" + "\t" + nf.format(id) + "\t"
										+ "\t" + nf.format(currentlocationpixel[0]) + "\t" + "\t"
										+ nf.format(currentlocationpixel[1]) + "\t" + "\t"
										+ nf.format(currentlocationreal[0]) + "\t" + "\t"
										+ nf.format(currentlocationreal[1]) + "\t" + "\t"
										+ nf.format(lengthpixelperframe) + "\t" + "\t" + nf.format(lengthrealperframe)
										+ "\n");

								double[] landt = { endlengthpixel, list.get(index).thirdDimension, id };
								lengthtimeend.add(landt);
								rtAll.incrementCounter();
								rtAll.addValue("FrameNumber", list.get(index).thirdDimension);
								rtAll.addValue("Total Length (pixel)", endlengthpixel);
								rtAll.addValue("Total Length (real)", endlengthreal);
								rtAll.addValue("Track iD", id);
								rtAll.addValue("CurrentPosition X (px units)", currentlocationpixel[0]);
								rtAll.addValue("CurrentPosition Y (px units)", currentlocationpixel[1]);
								rtAll.addValue("CurrentPosition X (real units)", currentlocationreal[0]);
								rtAll.addValue("CurrentPosition Y (real units)", currentlocationreal[1]);
								rtAll.addValue("Length per frame (px units)", lengthpixelperframe);
								rtAll.addValue("Length per frame (real units)", lengthrealperframe);

							}
							bw.close();
							fw.close();

						} catch (IOException e) {
						}
					}

				}

			}
			rtAll.show("Results");
		}
		if (showDeterministic) {

			ResultsTable rtAll = new ResultsTable();
			if (Allstart.get(0).size() > 0) {
				final ArrayList<Trackproperties> first = Allstart.get(0);

				Collections.sort(first, Seedcomparetrack);

				int MaxSeedLabel = first.get(first.size() - 1).seedlabel;
				int MinSeedLabel = first.get(0).seedlabel;
				for (int currentseed = MinSeedLabel; currentseed < MaxSeedLabel + 1; ++currentseed) {
					double startlengthreal = 0;
					double startlengthpixel = 0;
					System.out.println(currentseed);
					for (int index = 0; index < Allstart.size(); ++index) {

						final ArrayList<Trackproperties> thirdDimension = Allstart.get(index);

						for (int frameindex = 0; frameindex < thirdDimension.size(); ++frameindex) {

							final Integer seedID = thirdDimension.get(frameindex).seedlabel;
							final int framenumber = thirdDimension.get(frameindex).Framenumber;
							if (seedID == currentseed) {
								final Integer[] FrameID = { framenumber, seedID };
								final double[] originalpoint = thirdDimension.get(frameindex).originalpoint;
								final double[] newpoint = thirdDimension.get(frameindex).newpoint;
								final double[] oldpoint = thirdDimension.get(frameindex).oldpoint;
								final double[] newpointCal = new double[] {
										thirdDimension.get(frameindex).newpoint[0] * calibration[0],
										thirdDimension.get(frameindex).newpoint[1] * calibration[1] };
								final double[] oldpointCal = new double[] {
										thirdDimension.get(frameindex).oldpoint[0] * calibration[0],
										thirdDimension.get(frameindex).oldpoint[1] * calibration[1] };

								final double lengthrealperframe = util.Boundingboxes.Distance(newpointCal, oldpointCal);
								final double lengthpixelperframe = util.Boundingboxes.Distance(newpoint, oldpoint);
								final double seedtocurrent = util.Boundingboxes.Distancesq(originalpoint, newpoint);
								final double seedtoold = util.Boundingboxes.Distancesq(originalpoint, oldpoint);
								final boolean shrink = seedtoold > seedtocurrent ? true : false;
								final boolean growth = seedtoold > seedtocurrent ? false : true;

								if (shrink) {
									// MT shrank

									startlengthreal -= lengthrealperframe;
									startlengthpixel -= lengthpixelperframe;

								}
								if (growth) {

									// MT grew
									startlengthreal += lengthrealperframe;
									startlengthpixel += lengthpixelperframe;

								}

								double[] currentlocationpixel = new double[ndims];

								if (framenumber == thirdDimensionsliderInit)
									currentlocationpixel = originalpoint;
								else
									currentlocationpixel = newpoint;

								double[] currentlocationreal = new double[ndims];

								currentlocationreal = new double[] { currentlocationpixel[0] * calibration[0],
										currentlocationpixel[1] * calibration[1] };

								ResultsMT startMT = new ResultsMT(framenumber, startlengthpixel, startlengthreal,
										seedID, currentlocationpixel, currentlocationreal, lengthpixelperframe,
										lengthrealperframe);

								startlengthlist.add(startMT);

							}
						}
					}
				}
				for (int seedID = MinSeedLabel; seedID <= MaxSeedLabel; ++seedID) {
					if (SaveTxt) {
						try {
							File fichier = new File(
									usefolder + "//" + addToName + "SeedLabel" + seedID + "-endA" + ".txt");

							FileWriter fw = new FileWriter(fichier);
							BufferedWriter bw = new BufferedWriter(fw);

							bw.write(
									"\tFramenumber\tTotal Length (pixel)\tTotal Length (real)\tSeed iD\tCurrentPosition X (px units)\tCurrentPosition Y (px units)\tCurrentPosition X (real units)\tCurrentPosition Y (real units)"
											+ "\tLength per frame (px units)" + "\tLength per frame (real units)\n");

							for (int index = 0; index < startlengthlist.size(); ++index) {
								if (startlengthlist.get(index).seedid == seedID) {

									bw.write("\t" + startlengthlist.get(index).framenumber + "\t" + "\t"
											+ nf.format(startlengthlist.get(index).totallengthpixel) + "\t" + "\t"
											+ nf.format(startlengthlist.get(index).totallengthreal) + "\t" + "\t"
											+ nf.format(startlengthlist.get(index).seedid) + "\t" + "\t"
											+ nf.format(startlengthlist.get(index).currentpointpixel[0]) + "\t" + "\t"
											+ nf.format(startlengthlist.get(index).currentpointpixel[1]) + "\t" + "\t"
											+ nf.format(startlengthlist.get(index).currentpointreal[0]) + "\t" + "\t"
											+ nf.format(startlengthlist.get(index).currentpointreal[1]) + "\t" + "\t"
											+ nf.format(startlengthlist.get(index).lengthpixelperframe) + "\t" + "\t"
											+ nf.format(startlengthlist.get(index).lengthrealperframe) + "\n");

								}

							}
							bw.close();
							fw.close();

						} catch (IOException e) {
						}
					}
				}
				for (int index = 0; index < startlengthlist.size(); ++index) {

					double[] landt = { startlengthlist.get(index).totallengthpixel,
							startlengthlist.get(index).framenumber, startlengthlist.get(index).seedid };
					lengthtimestart.add(landt);

					rtAll.incrementCounter();
					rtAll.addValue("FrameNumber", startlengthlist.get(index).framenumber);
					rtAll.addValue("Total Length (pixel)", startlengthlist.get(index).totallengthpixel);
					rtAll.addValue("Total Length (real)", startlengthlist.get(index).totallengthreal);
					rtAll.addValue("Track iD", startlengthlist.get(index).seedid);
					rtAll.addValue("CurrentPosition X (px units)", startlengthlist.get(index).currentpointpixel[0]);
					rtAll.addValue("CurrentPosition Y (px units)", startlengthlist.get(index).currentpointpixel[1]);
					rtAll.addValue("CurrentPosition X (real units)", startlengthlist.get(index).currentpointreal[0]);
					rtAll.addValue("CurrentPosition Y (real units)", startlengthlist.get(index).currentpointreal[1]);
					rtAll.addValue("Length per frame (px units)", startlengthlist.get(index).lengthpixelperframe);
					rtAll.addValue("Length per frame (real units)", startlengthlist.get(index).lengthrealperframe);

				}

			}

			if (Allend.get(0).size() > 0) {
				final ArrayList<Trackproperties> first = Allend.get(0);
				Collections.sort(first, Seedcomparetrack);
				int MaxSeedLabel = first.get(first.size() - 1).seedlabel;
				int MinSeedLabel = first.get(0).seedlabel;
				for (int currentseed = MinSeedLabel; currentseed < MaxSeedLabel + 1; ++currentseed) {
					System.out.println(currentseed);
					double endlengthreal = 0;
					double endlengthpixel = 0;
					for (int index = 0; index < Allend.size(); ++index) {

						final ArrayList<Trackproperties> thirdDimension = Allend.get(index);

						for (int frameindex = 0; frameindex < thirdDimension.size(); ++frameindex) {
							final int framenumber = thirdDimension.get(frameindex).Framenumber;
							final Integer seedID = thirdDimension.get(frameindex).seedlabel;

							if (seedID == currentseed) {
								final Integer[] FrameID = { framenumber, seedID };
								final double[] originalpoint = thirdDimension.get(frameindex).originalpoint;
								final double[] newpoint = thirdDimension.get(frameindex).newpoint;
								final double[] oldpoint = thirdDimension.get(frameindex).oldpoint;

								final double[] newpointCal = new double[] {
										thirdDimension.get(frameindex).newpoint[0] * calibration[0],
										thirdDimension.get(frameindex).newpoint[1] * calibration[1] };
								final double[] oldpointCal = new double[] {
										thirdDimension.get(frameindex).oldpoint[0] * calibration[0],
										thirdDimension.get(frameindex).oldpoint[1] * calibration[1] };

								final double lengthrealperframe = util.Boundingboxes.Distance(newpointCal, oldpointCal);
								final double lengthpixelperframe = util.Boundingboxes.Distance(newpoint, oldpoint);
								final double seedtocurrent = util.Boundingboxes.Distancesq(originalpoint, newpoint);
								final double seedtoold = util.Boundingboxes.Distancesq(originalpoint, oldpoint);
								final boolean shrink = seedtoold > seedtocurrent ? true : false;
								final boolean growth = seedtoold > seedtocurrent ? false : true;

								if (shrink) {

									// MT shrank

									endlengthreal -= lengthrealperframe;
									endlengthpixel -= lengthpixelperframe;

								}

								if (growth) {

									// MT grew

									endlengthreal += lengthrealperframe;
									endlengthpixel += lengthpixelperframe;

								}

								double[] currentlocationpixel = new double[ndims];

								if (framenumber == thirdDimensionsliderInit)
									currentlocationpixel = originalpoint;
								else
									currentlocationpixel = newpoint;

								double[] currentlocationreal = new double[ndims];

								currentlocationreal = new double[] { currentlocationpixel[0] * calibration[0],
										currentlocationpixel[1] * calibration[1] };

								ResultsMT endMT = new ResultsMT(framenumber, endlengthpixel, endlengthreal, seedID,
										currentlocationpixel, currentlocationreal, lengthpixelperframe,
										lengthrealperframe);

								endlengthlist.add(endMT);

							}
						}
					}

				}
				for (int seedID = MinSeedLabel; seedID <= MaxSeedLabel; ++seedID) {
					if (SaveTxt) {
						try {
							File fichier = new File(
									usefolder + "//" + addToName + "SeedLabel" + seedID + "-endA" + ".txt");

							FileWriter fw = new FileWriter(fichier);
							BufferedWriter bw = new BufferedWriter(fw);

							bw.write(
									"\tFramenumber\tTotal Length (pixel)\tTotal Length (real)\tSeed iD\tCurrentPosition X (px units)\tCurrentPosition Y (px units)\tCurrentPosition X (real units)\tCurrentPosition Y (real units)"
											+ "\tLength per frame (px units)" + "\tLength per frame (real units)\n");

							for (int index = 0; index < endlengthlist.size(); ++index) {
								if (endlengthlist.get(index).seedid == seedID) {

									bw.write("\t" + endlengthlist.get(index).framenumber + "\t" + "\t"
											+ nf.format(endlengthlist.get(index).totallengthpixel) + "\t" + "\t"
											+ nf.format(endlengthlist.get(index).totallengthreal) + "\t" + "\t"
											+ nf.format(endlengthlist.get(index).seedid) + "\t" + "\t"
											+ nf.format(endlengthlist.get(index).currentpointpixel[0]) + "\t" + "\t"
											+ nf.format(endlengthlist.get(index).currentpointpixel[1]) + "\t" + "\t"
											+ nf.format(endlengthlist.get(index).currentpointreal[0]) + "\t" + "\t"
											+ nf.format(endlengthlist.get(index).currentpointreal[1]) + "\t" + "\t"
											+ nf.format(endlengthlist.get(index).lengthpixelperframe) + "\t" + "\t"
											+ nf.format(endlengthlist.get(index).lengthrealperframe) + "\n");

								}

							}
							bw.close();
							fw.close();

						} catch (IOException e) {
						}
					}
				}
				for (int index = 0; index < endlengthlist.size(); ++index) {

					double[] landt = { endlengthlist.get(index).totallengthpixel, endlengthlist.get(index).framenumber,
							endlengthlist.get(index).seedid };
					lengthtimestart.add(landt);

					rtAll.incrementCounter();
					rtAll.addValue("FrameNumber", endlengthlist.get(index).framenumber);
					rtAll.addValue("Total Length (pixel)", endlengthlist.get(index).totallengthpixel);
					rtAll.addValue("Total Length (real)", endlengthlist.get(index).totallengthreal);
					rtAll.addValue("Track iD", endlengthlist.get(index).seedid);
					rtAll.addValue("CurrentPosition X (px units)", endlengthlist.get(index).currentpointpixel[0]);
					rtAll.addValue("CurrentPosition Y (px units)", endlengthlist.get(index).currentpointpixel[1]);
					rtAll.addValue("CurrentPosition X (real units)", endlengthlist.get(index).currentpointreal[0]);
					rtAll.addValue("CurrentPosition Y (real units)", endlengthlist.get(index).currentpointreal[1]);
					rtAll.addValue("Length per frame (px units)", endlengthlist.get(index).lengthpixelperframe);
					rtAll.addValue("Length per frame (real units)", endlengthlist.get(index).lengthrealperframe);

				}

			}

			rtAll.show("Start and End of MT");
			if (lengthtimestart != null)
				lengthtime = lengthtimestart;
			else
				lengthtime = lengthtimeend;
			if (analyzekymo) {
				double lengthcheckstart = 0;
				double lengthcheckend = 0;
				if (lengthtimestart != null) {

					lengthtime = lengthtimestart;
					for (int index = 0; index < lengthtimestart.size(); ++index) {

						int time = (int) lengthtimestart.get(index)[1];

						lengthcheckstart += lengthtimestart.get(index)[0];

						for (int secindex = 0; secindex < lengthKymo.size(); ++secindex) {

							for (int accountindex = 0; accountindex < Accountedframes.size(); ++accountindex) {

								if ((int) lengthKymo.get(secindex)[1] == time
										&& Accountedframes.get(accountindex) == time) {

									float delta = (float) (lengthtimestart.get(index)[0] - lengthKymo.get(secindex)[0]);
									float[] cudeltadeltaLstart = { delta, time };
									deltadstart.add(cudeltadeltaLstart);

								}
							}

						}
					}

					/********
					 * The part below removes the duplicate entries in the array
					 * dor the time co-ordinate
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

				if (lengthtimeend != null) {
					lengthtime = lengthtimeend;
					for (int index = 0; index < lengthtimeend.size(); ++index) {

						int time = (int) lengthtimeend.get(index)[1];

						lengthcheckend += lengthtimeend.get(index)[0];

						for (int secindex = 0; secindex < lengthKymo.size(); ++secindex) {

							for (int accountindex = 0; accountindex < Accountedframes.size(); ++accountindex) {

								if ((int) lengthKymo.get(secindex)[1] == time
										&& Accountedframes.get(accountindex) == time) {

									if ((int) lengthKymo.get(secindex)[1] == time
											&& Accountedframes.get(accountindex) == time) {

										float delta = (float) (lengthtimeend.get(index)[0]
												- lengthKymo.get(secindex)[0]);
										float[] cudeltadeltaLend = { delta, time };
										deltadend.add(cudeltadeltaLend);
									}
								}

							}

						}
					}
					/********
					 * The part below removes the duplicate entries in the array
					 * dor the time co-ordinate
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
		displaystack();
		if (displayoverlay) {
			prestack.deleteLastSlice();
			new ImagePlus("Overlays", prestack).show();
		}

	}

	public void UpdateMser() {
		FindLinesViaMSER = true;
		FindLinesViaHOUGH = false;
		FindLinesViaMSERwHOUGH = false;
		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints c = new GridBagConstraints();
		panelFourth.removeAll();
		final Label Step = new Label("Step 4", Label.CENTER);
		panelFourth.setLayout(layout);
		panelFourth.add(Step, c);
		final Scrollbar deltaS = new Scrollbar(Scrollbar.HORIZONTAL, deltaInit, 10, 0, 10 + scrollbarSize);
		final Scrollbar maxVarS = new Scrollbar(Scrollbar.HORIZONTAL, maxVarInit, 10, 0, 10 + scrollbarSize);
		final Scrollbar minDiversityS = new Scrollbar(Scrollbar.HORIZONTAL, minDiversityInit, 10, 0,
				10 + scrollbarSize);
		final Scrollbar minSizeS = new Scrollbar(Scrollbar.HORIZONTAL, minSizeInit, 10, 0, 10 + scrollbarSize);
		final Scrollbar maxSizeS = new Scrollbar(Scrollbar.HORIZONTAL, maxSizeInit, 10, 0, 10 + scrollbarSize);

		final Label deltaText = new Label("delta = " + delta, Label.CENTER);
		final Label maxVarText = new Label("maxVar = " + maxVar, Label.CENTER);
		final Label minDiversityText = new Label("minDiversity = " + minDiversity, Label.CENTER);
		final Label minSizeText = new Label("MinSize = " + minSize, Label.CENTER);
		final Label maxSizeText = new Label("MaxSize = " + maxSize, Label.CENTER);

		final Checkbox min = new Checkbox("Look for Minima ", darktobright);

		final Button ComputeTree = new Button("Compute Tree and display");
		/* Location */

		final Label Update = new Label("Update parameters for dynamic channel");
		Update.setBackground(new Color(1, 0, 1));
		Update.setForeground(new Color(255, 255, 255));
		panelFourth.setLayout(layout);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 4;
		c.weighty = 1.5;
		++c.gridy;
		panelFourth.add(Update, c);

		++c.gridy;
		panelFourth.add(deltaText, c);

		++c.gridy;
		panelFourth.add(deltaS, c);

		++c.gridy;

		panelFourth.add(maxVarText, c);

		++c.gridy;
		panelFourth.add(maxVarS, c);

		++c.gridy;

		panelFourth.add(minDiversityText, c);

		++c.gridy;
		panelFourth.add(minDiversityS, c);

		++c.gridy;

		panelFourth.add(minSizeText, c);

		++c.gridy;
		panelFourth.add(minSizeS, c);

		++c.gridy;

		panelFourth.add(maxSizeText, c);

		++c.gridy;
		panelFourth.add(maxSizeS, c);

		++c.gridy;
		c.insets = new Insets(10, 175, 0, 175);
		panelFourth.add(min, c);

		++c.gridy;
		c.insets = new Insets(10, 175, 0, 175);
		panelFourth.add(ComputeTree, c);

		deltaS.addAdjustmentListener(new DeltaListener(deltaText, deltaMin, deltaMax, scrollbarSize, deltaS));

		maxVarS.addAdjustmentListener(new maxVarListener(maxVarText, maxVarMin, maxVarMax, scrollbarSize, maxVarS));

		minDiversityS.addAdjustmentListener(new minDiversityListener(minDiversityText, minDiversityMin, minDiversityMax,
				scrollbarSize, minDiversityS));

		minSizeS.addAdjustmentListener(
				new minSizeListener(minSizeText, minSizemin, minSizemax, scrollbarSize, minSizeS));

		maxSizeS.addAdjustmentListener(
				new maxSizeListener(maxSizeText, maxSizemin, maxSizemax, scrollbarSize, maxSizeS));

		min.addItemListener(new DarktobrightListener());
		ComputeTree.addActionListener(new ComputeTreeListener());

		if (analyzekymo && Kymoimg != null) {

			Kymo();
		}

		else

			Deterministic();

		panelFourth.validate();
		panelFourth.repaint();
		Cardframe.pack();
	}

	protected class UpdateMserListener implements ItemListener {
		@Override
		public void itemStateChanged(final ItemEvent arg0) {

			if (arg0.getStateChange() == ItemEvent.DESELECTED)
				FindLinesViaMSER = false;
			else if (arg0.getStateChange() == ItemEvent.SELECTED) {
				FindLinesViaMSER = true;
				UpdateMser();

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
				final Label Step = new Label("Step 2", Label.CENTER);

				panelSecond.setLayout(layout);

				panelSecond.add(Step, c);
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

				final Label deltaText = new Label("delta = " + delta, Label.CENTER);
				final Label maxVarText = new Label("maxVar = " + maxVar, Label.CENTER);
				final Label minDiversityText = new Label("minDiversity = " + minDiversity, Label.CENTER);
				final Label minSizeText = new Label("MinSize = " + minSize, Label.CENTER);
				final Label maxSizeText = new Label("MaxSize = " + maxSize, Label.CENTER);

				final Checkbox min = new Checkbox("Look for Minima ", darktobright);

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
				final Label Step = new Label("Step 2", Label.CENTER);

				panelSecond.setLayout(layout);
				panelSecond.add(Step, c);
				final Label exthresholdText = new Label("threshold = threshold to create Bitimg for watershedding.",
						Label.CENTER);
				final Label exthetaText = new Label("thetaPerPixel = Pixel Size in theta direction for Hough space.",
						Label.CENTER);
				final Label exrhoText = new Label("rhoPerPixel = Pixel Size in rho direction for Hough space.",
						Label.CENTER);

				final Label thresholdText = new Label("thresholdValue = " + thresholdHough, Label.CENTER);
				final Label thetaText = new Label("Size of Hough Space in Theta = " + thetaPerPixel, Label.CENTER);
				final Label rhoText = new Label("Size of Hough Space in Rho = " + rhoPerPixel, Label.CENTER);
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
				final Label Step = new Label("Step 1", Label.CENTER);

				panelSecond.setLayout(layout);
				panelSecond.add(Step, c);
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

				final Label thetaText = new Label("Pixel size of Hough Space in Theta / Pixel Space = " + thetaPerPixel,
						Label.CENTER);
				final Label rhoText = new Label("Pixel size of Hough Space in Rho / Pixel Space = " + rhoPerPixel,
						Label.CENTER);
				final Button FindLinesListener = new Button("Find endpoints");
				final Label Houghparam = new Label("Determine MSER and Hough Transform parameters");
				Houghparam.setBackground(new Color(1, 0, 1));
				Houghparam.setForeground(new Color(255, 255, 255));

				final Scrollbar deltaS = new Scrollbar(Scrollbar.HORIZONTAL, deltaInit, 10, 0, 10 + scrollbarSize);
				final Scrollbar maxVarS = new Scrollbar(Scrollbar.HORIZONTAL, maxVarInit, 10, 0, 10 + scrollbarSize);
				final Scrollbar minDiversityS = new Scrollbar(Scrollbar.HORIZONTAL, minDiversityInit, 10, 0,
						10 + scrollbarSize);
				final Scrollbar minSizeS = new Scrollbar(Scrollbar.HORIZONTAL, minSizeInit, 10, 0, 10 + scrollbarSize);
				final Scrollbar maxSizeS = new Scrollbar(Scrollbar.HORIZONTAL, maxSizeInit, 10, 0, 10 + scrollbarSize);
				final Button ComputeTree = new Button("Compute Tree and display");

				maxVar = computeValueFromScrollbarPosition(maxVarInit, maxVarMin, maxVarMax, scrollbarSize);
				delta = computeValueFromScrollbarPosition(deltaInit, deltaMin, deltaMax, scrollbarSize);
				minDiversity = computeValueFromScrollbarPosition(minDiversityInit, minDiversityMin, minDiversityMax,
						scrollbarSize);
				minSize = (int) computeValueFromScrollbarPosition(minSizeInit, minSizemin, minSizemax, scrollbarSize);
				maxSize = (int) computeValueFromScrollbarPosition(maxSizeInit, maxSizemin, maxSizemax, scrollbarSize);

				final Checkbox min = new Checkbox("Look for Minima ", darktobright);

				final Label deltaText = new Label("delta = " + delta, Label.CENTER);
				final Label maxVarText = new Label("maxVar = " + maxVar, Label.CENTER);
				final Label minDiversityText = new Label("minDiversity = " + minDiversity, Label.CENTER);
				final Label minSizeText = new Label("MinSize = " + minSize, Label.CENTER);
				final Label maxSizeText = new Label("MaxSize = " + maxSize, Label.CENTER);
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
				Cardframe.pack();
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
				CurrentView = util.CopyUtils.getCurrentView(originalimg, thirdDimension, thirdDimensionSize);
				CurrentPreprocessedView = util.CopyUtils.getCurrentPreView(originalPreprocessedimg, thirdDimension,
						thirdDimensionSize);
			} else {
				CurrentView = util.CopyUtils.getCurrentView(originalimg, thirdDimension, thirdDimensionSize);
				CurrentPreprocessedView = util.CopyUtils.getCurrentPreView(originalPreprocessedimg, thirdDimension,
						thirdDimensionSize);

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

	protected void displaystack() {

		ImagePlus Localimp = ImageJFunctions.show(originalimg);

		for (int i = thirdDimensionsliderInit; i < thirdDimensionSize; ++i) {

			prestack.addSlice(Localimp.getImageStack().getProcessor(i).convertToRGB());
			cp = (ColorProcessor) (prestack.getProcessor(i).duplicate());

			if (FindLinesViaHOUGH == false) {
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

			for (int index = 0; index < AllBigRoi.size(); ++index) {

				cp.draw(AllBigRoi.get(index));

			}

			if (displayoverlay && prestack != null)
				prestack.setPixels(cp.getPixels(), i);
			Localimp.hide();
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

	public boolean DialogueModelChoice() {

		GenericDialog gd = new GenericDialog("Model Choice for sub-pixel Localization");
		String[] LineModel = { "GaussianLines", "SecondOrderPolynomial", "ThridOrderPolynomial" };

		int indexmodel = 0;

		gd.addChoice("Choose your model: ", LineModel, LineModel[indexmodel]);
		gd.addCheckbox("Do Gaussian Mask Fits", Domask);

		gd.addTextAreas("Advanced Options for the optimizer", null, 1, 35);
		gd.addNumericField("Min Intensity = R * Max Intensity along MT, R (enter 0.2 to 0.9) = ", Intensityratio, 2);
		gd.addNumericField("Spacing between Gaussians = G * Min(Psf), G (enter 0.3 to 1.0) = ",
				Inispacing / Math.min(psf[0], psf[1]), 2);

		if (analyzekymo && Kymoimg != null) {
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

		if (analyzekymo && Kymoimg != null)
			deltadcutoff = (float) gd.getNextNumber();

		return !gd.wasCanceled();
	}

	public boolean DialogueModelChoiceHF() {

		GenericDialog gd = new GenericDialog("Model Choice for sub-pixel Localization");
		String[] LineModel = { "GaussianLines", "SecondOrderPolynomial", "ThridOrderPolynomial" };

		int indexmodel = 0;

		gd.addChoice("Choose your model: ", LineModel, LineModel[indexmodel]);
		gd.addCheckbox("Do Gaussian Mask Fits", Domask);
		gd.addCheckbox("Display rois:", displayoverlay);
		gd.addTextAreas("Advanced Options for the optimizer", null, 1, 35);
		gd.addNumericField("Min Intensity = R * Max Intensity along MT, R (enter 0.2 to 0.9) = ", Intensityratio, 2);
		gd.addNumericField("Spacing between Gaussians = G * Min(Psf), G (enter 0.3 to 1.0) = ",
				Inispacing / Math.min(psf[0], psf[1]), 2);
		gd.addStringField("Choose a different Directory?:", usefolder);
		gd.addStringField("Choose a different filename?:", addToName);
		gd.showDialog();
		indexmodel = gd.getNextChoiceIndex();
		displayoverlay = gd.getNextBoolean();
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

	public static void main(String[] args) {
		new ImageJ();

		JFrame frame = new JFrame("");
		FileChooser panel = new FileChooser();

		frame.getContentPane().add(panel, "Center");
		frame.setSize(panel.getPreferredSize());
	}
}
