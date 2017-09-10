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
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

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
import ij.gui.EllipseRoi;
import ij.gui.GenericDialog;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.plugin.Macro_Runner;
import ij.plugin.PlugIn;
import ij.plugin.frame.RoiManager;
import ij.process.ColorProcessor;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;
import labeledObjects.CommonOutputHF;
import labeledObjects.Indexedlength;
import listeners.AdvancedSeedListener;
import listeners.AdvancedTrackerListener;
import listeners.BeginTrackListener;
import listeners.ChooseDirectoryListener;
import listeners.ComputeMserinHoughListener;
import listeners.ComputeTreeListener;
import listeners.DarktobrightListener;
import listeners.DeltaHoughListener;
import listeners.DeltaListener;
import listeners.DoMserSegmentation;
import listeners.DoSegmentation;
import listeners.DowatershedListener;
import listeners.EndTrackListener;
import listeners.FindLinesListener;
import listeners.HoughListener;
import listeners.MaxSizeHoughListener;
import listeners.MaxSizeListener;
import listeners.MethodListener;
import listeners.MinDiversityHoughListener;
import listeners.Unstability_ScoreListener;
import listeners.MinDiversityListener;
import listeners.MinSizeHoughListener;
import listeners.MinSizeListener;
import listeners.MserListener;
import listeners.MserwHoughListener;
import listeners.RadiusListener;
import listeners.SeedDisplayListener;
import listeners.ShowBitimgListener;
import listeners.ShowwatershedimgListener;
import listeners.SkipFramesandTrackendsListener;
import listeners.ThresholdHoughHFListener;
import listeners.ThresholdHoughListener;
import listeners.TrackendsListener;
import listeners.Unstability_ScoreHoughListener;
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
import preProcessing.GetLocalmaxmin.IntensityType;
import preProcessing.GlobalThresholding;
import preProcessing.Kernels;
import preProcessing.MedianFilter2D;
import singleListeners.SingleAdvancedTrackerListener;
import singleListeners.SingleBatchModeListener;
import singleListeners.SingleBeginTrackListener;
import singleListeners.SingleChooseDirectoryListener;
import singleListeners.SingleComputeMserinHoughListener;
import singleListeners.SingleDarktobrightListener;
import singleListeners.SingleDeltaHoughListener;
import singleListeners.SingleDeltaListener;
import singleListeners.SingleDoMserSegmentation;
import singleListeners.SingleDoSegmentation;
import singleListeners.SingleEndTrackListener;
import singleListeners.SingleHoughListener;
import singleListeners.SingleMaxSizeHoughListener;
import singleListeners.SingleMaxSizeListener;
import singleListeners.SingleMethodListener;
import singleListeners.SingleMinDiversityHoughListener;
import singleListeners.SingleMinDiversityListener;
import singleListeners.SingleMinSizeHoughListener;
import singleListeners.SingleMinSizeListener;
import singleListeners.SingleMoveNextListener;
import singleListeners.SingleMserListener;
import singleListeners.SingleMserwHoughListener;
import singleListeners.SingleRadiusListener;
import singleListeners.SingleSeedDisplayListener;
import singleListeners.SingleShowBitimgListener;
import singleListeners.SingleShowwatershedimgListener;
import singleListeners.SingleSkipFramesandTrackendsListener;
import singleListeners.SingleThresholdHoughHFListener;
import singleListeners.SingleUnstability_ScoreHoughListener;
import singleListeners.SingleUnstability_ScoreListener;
import trackerType.MTTracker;
import updateListeners.BatchModeListener;
import updateListeners.DefaultModel;
import updateListeners.DefaultModelHF;
import updateListeners.FinalPoint;
import updateListeners.FinalizechoicesListener;
import updateListeners.Markends;
import updateListeners.MoveNextListener;
import updateListeners.MoveToFrameListener;
import updateListeners.SingleDefaultModelHF;
import updateListeners.SingleFinalPoint;
import updateListeners.SingleFinalizechoicesListener;
import updateListeners.SingleMarkends;
import updateListeners.SingleMarkendsnew;
import updateListeners.SingleMoveToFrameListener;
import util.Boundingboxes;

/**
 * An interactive tool for MT tracking using MSER and Hough Transform
 * 
 * @author Varun Kapoor
 */

public class Interactive_MTSingleChannel implements PlugIn {

	public String usefolder = IJ.getDirectory("imagej");
	public ColorProcessor cp = null;
	public String addToName = "MTTrack";
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
	public double maxdist = 5;
	public JLabel inputradi;
	public TextField inputFieldradi;
	// steps per octave
	public static int standardSensitivity = 4;
	public int sensitivity = standardSensitivity;
	public float deltaMin = 0;
	public float thetaPerPixelMin = new Float(0.2);
	public float rhoPerPixelMin = new Float(0.2);
	public MouseListener ml;
	public MouseListener removeml;
	public Color colorUnselectUser = Color.RED;
	public OvalRoi Seedroi;
	public ArrayList<OvalRoi> AllSeedrois;
	public float thresholdHoughMin = 0;
	public float thresholdHoughMax = 1;
	public float deltaMax = 400f;
	public float Unstability_ScoreMin = 0;
	public float Unstability_ScoreMax = 1;
	public SingleMarkendsnew newends;
	public int radiusseed = 5;
	public JLabel inputLabelX;
	public JLabel inputLabelY;
	public JLabel inputLabelT;
	public TextField inputFieldX;
	public TextField inputFieldY;
	public TextField inputFieldT;
	public float thetaPerPixelMax = 2;
	public float rhoPerPixelMax = 2;
	public JProgressBar jpb;
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
	public long minSize = 1;
	public long maxSize = 1000;
	public long minSizemin = 0;
	public long minSizemax = 1000;
	public long maxSizemin = 100;
	public long maxSizemax = 10000;
	public int selectedSeed = 0;
	public int displayselectedSeed;
	public double netdeltad = 0;
	public double Intensityratio = 0.5;
	public double slopetolerance = 5;
	public double Inispacing = 0.5;
	public int thirdDimensionslider = 1;
	public int thirdDimensionsliderInit = 1;
	public int timeMin = 1;

	public int numgaussians = 2;
	public float minDiversityMin = 0;
	public float minDiversityMax = 1;

	public UserChoiceModel userChoiceModel;
	public float delta = 1f;

	public int deltaInit = 20;
	public int Unstability_ScoreInit = 1;

	public int minSizeInit = 10;
	public int maxSizeInit = 5000;

	public float thresholdHoughInit = new Float(0.25);
	public float rhoPerPixelInit = new Float(1);
	public float thetaPerPixelInit = new Float(1);
	public JLabel inputMaxdpixel;
	public JLabel inputMaxdmicro;
	public TextField Maxdpixel;
	private TextField Maxdmicro;

	public  File userfile;

	public int minDiversityInit = 1;

	public int radius = 1;
	public long Size = 1;
	public float thetaPerPixel = 1;
	public float rhoPerPixel = 1;
	public boolean enablerhoPerPixel = false;
	public float Unstability_Score = 1;
	public float minDiversity = 1;
	public float thresholdHough = 1;
	public double netdeltadstart = 0;
	public double netdeltadend = 0;
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
	public boolean FindLinesViaMSER = false;
	public boolean doSegmentation = false;
	public boolean doMserSegmentation = false;
	public boolean FindLinesViaHOUGH = false;
	public boolean FindLinesViaMSERwHOUGH = false;
	public boolean ShowMser = false;
	public boolean ShowHough = false;
	public boolean update = false;
	public boolean Canny = false;
	public boolean showDeterministic = true;
	public boolean RoisViaMSER = false;
	public boolean RoisViaWatershed = false;
	public boolean displayTree = false;
	public boolean GaussianLines = true;
	public boolean Mediancurr = false;
	public boolean MedianAll = false;
	public boolean AutoDelta = false;
	public boolean Domask = false;
	public boolean DoRloop = false;
	public boolean SaveTxt = true;
	public boolean SaveXLS = false;
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

	public float initialSearchradius = 20;
	public int starttime ;
	public int endtime ;
	public float maxSearchradius = 15;
	public int missedframes = 5;
	public int maxghost = 1;
	public int initialSearchradiusInit = 20;
	public float initialSearchradiusMin = 0;
	public float initialSearchradiusMax = 100;

	public double sumlengthpixel = 0;
	public double sumlengthmicro = 0;
	
	public int maxSearchradiusInit = 200;
	public float maxSearchradiusMin = 10;
	public float maxSearchradiusMax = 500;

	public int missedframesInit = missedframes;
	public float missedframesMin = 0;
	public float missedframesMax = 100;
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
	public ImagePlus Kymoimp;
	public RandomAccessibleInterval<FloatType> originalimg;
	public RandomAccessibleInterval<FloatType> originalPreprocessedimg;
	public RandomAccessibleInterval<FloatType> Kymoimg;
	public RandomAccessibleInterval<FloatType> CurrentView;
	public RandomAccessibleInterval<FloatType> CurrentPreprocessedView;
	public int inix = 1;
	public int iniy = 1;
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
	public double[] psf;
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
	public RandomAccessibleInterval<BitType> bitimg;
	public RandomAccessibleInterval<FloatType> bitimgFloat;
	public ArrayList<double[]> AllmeanCovar = new ArrayList<double[]>();
	public long Cannyradius;
	public HashMap<Integer, ArrayList<Roi>> AllpreviousRois;
	// first and last slice to process
	int endStack;
	public int thirdDimension;

	public static enum Whichend {

		start, end, both, none, user;
	}

	public static enum ValueChange {
		ROI, ALL, DELTA, FindLinesVia, Unstability_Score, MINDIVERSITY, DARKTOBRIGHT, MINSIZE, MAXSIZE, SHOWMSER, FRAME, SHOWHOUGH, THIRDDIMmouse,
		thresholdHough, DISPLAYBITIMG, DISPLAYWATERSHEDIMG, rhoPerPixel, thetaPerPixel, THIRDDIM, iniSearch, maxSearch, missedframes, THIRDDIMTrack, MEDIAN, kymo, SHOWMSERinHough;
	}

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

	public void setInitialUnstability_Score(final float value) {
		Unstability_Score = value;
		Unstability_ScoreInit = computeScrollbarPositionFromValue(Unstability_Score, Unstability_ScoreMin, Unstability_ScoreMax, scrollbarSize);
	}

	public double getInitialUnstability_Score(final float value) {

		return Unstability_Score;

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

	public Interactive_MTSingleChannel() {
		
	};

	public Interactive_MTSingleChannel(final RandomAccessibleInterval<FloatType> originalimg,
			final RandomAccessibleInterval<FloatType> originalPreprocessedimg, final double[] psf,
			final double[] imgCal, final File userfile) {

		this.originalimg = originalimg;
		this.originalPreprocessedimg = originalPreprocessedimg;
		this.psf = psf;
		this.Kymoimg = null;
		standardRectangle = new Rectangle(inix, iniy, (int) originalimg.dimension(0) - 2 * inix,
				(int) originalimg.dimension(1) - 2 * iniy);
		imp = ImageJFunctions.show(originalimg);
		impcopy = imp.duplicate();

		imp.setTitle("Original movie");
		this.userfile = userfile;
		calibration = imgCal;
		System.out.println(calibration[0] + " " + calibration[1]);

	}

	public Interactive_MTSingleChannel(final RandomAccessibleInterval<FloatType> originalimg,
			final RandomAccessibleInterval<FloatType> originalPreprocessedimg,
			final RandomAccessibleInterval<FloatType> kymoimg, final double[] psf, final double[] imgCal, final File userfile) {

		this.originalimg = originalimg;
		this.originalPreprocessedimg = originalPreprocessedimg;
		this.Kymoimg = kymoimg;
		this.psf = psf;
		standardRectangle = new Rectangle(inix, iniy, (int) originalimg.dimension(0) - 2 * inix,
				(int) originalimg.dimension(1) - 2 * iniy);
		imp = ImageJFunctions.show(originalimg);
		impcopy = imp.duplicate();
		imp.setTitle("Original movie");
		this.userfile = userfile;
		calibration = imgCal;
		System.out.println(calibration[0] + " " + calibration[1]);

	}

	@Override
	public void run(String arg) {
		
	
		usefolder  = userfile.getParentFile().getAbsolutePath();
		 newends = new SingleMarkendsnew(this);
		SaveTxt = true;
		
		
		AllSeedrois = new ArrayList<OvalRoi>();
		jpb = new JProgressBar();
		newHoughtree = new HashMap<Integer, MserTree<UnsignedByteType>>();
		Userframe = new ArrayList<Indexedlength>();
		AllpreviousRois = new HashMap<Integer, ArrayList<Roi>>();
		Inispacing = 0.5 * Math.min(psf[0], psf[1]);
		count = 0;
		overlay = new Overlay();
		nf.setMaximumFractionDigits(3);
		setInitialUnstability_Score(Unstability_ScoreInit);
		setInitialDelta(deltaInit);
		setInitialrhoPerPixel(rhoPerPixelInit);
		setInitialthetaPerPixel(thetaPerPixelInit);
		setInitialthresholdHough(thresholdHoughInit);
		setInitialminDiversity(minDiversityInit);
		setInitialmaxSize(maxSizeInit);
		setInitialminSize(minSizeInit);
		setInitialsearchradius(initialSearchradiusInit);
		setInitialmaxsearchradius(maxSearchradius);
		
		Cannyradius = (long) (radiusfactor * Math.ceil(Math.sqrt(psf[0] * psf[0] + psf[1] * psf[1])));
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
		
		starttime = 2;
		endtime = thirdDimensionSize;
		prestack = new ImageStack((int) originalimg.dimension(0), (int) originalimg.dimension(1),
				java.awt.image.ColorModel.getRGBdefault());

		CurrentView = util.CopyUtils.getCurrentView(originalimg, thirdDimension, thirdDimensionSize);
		CurrentPreprocessedView = util.CopyUtils.getCurrentPreView(originalPreprocessedimg, thirdDimension,
				thirdDimensionSize);

		output = new ArrayList<CommonOutputHF>();
		endStack = thirdDimensionSize;
		thirdDimensionSizeOriginal = thirdDimensionSize;
		preprocessedimp = ImageJFunctions.show(CurrentView);
        preprocessedimp.setTitle("Preprocessed movie");
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

		if (change == ValueChange.THIRDDIMmouse) {

			if (preprocessedimp == null){
				preprocessedimp = ImageJFunctions.show(CurrentView);
			
			}
			
			else {
				final float[] pixels = (float[]) preprocessedimp.getProcessor().getPixels();
				final Cursor<FloatType> c = Views.iterable(CurrentView).cursor();

				for (int i = 0; i < pixels.length; ++i)
					pixels[i] = c.next().get();

				preprocessedimp.updateAndDraw();

			}

			preprocessedimp.setTitle("Active image" + " " + "time point : " + thirdDimension);
			
			preprocessedimp.getCanvas().removeMouseListener(ml);
			newends.markendnew();
			
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
					|| change == ValueChange.FRAME  || change == ValueChange.ALL) {
				

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
			for (int i = 0; i < overlay.size(); ++i) {
				if (overlay.get(i).getStrokeColor() == colorDraw || overlay.get(i).getStrokeColor() == colorCurrent
						|| overlay.get(i).getStrokeColor() == colorUnselect) {
					overlay.remove(i);
					--i;
				}
			}
			for (int label = 1; label < Maxlabel - 1; label++) {
				Pair<RandomAccessibleInterval<FloatType>, FinalInterval> pair = Boundingboxes
						.CurrentLabelImagepair(intimg, bitimgFloat, label);

				RandomAccessibleInterval<FloatType> roiimg = pair.getA();
						
				newimg = util.CopyUtils.copytoByteImage(
						Kernels.CannyEdgeandMean(roiimg, Cannyradius), standardRectangle);

				MserTree<UnsignedByteType> newtree = MserTree.buildMserTree(newimg, delta, minSize, maxSize, Unstability_Score,
						minDiversity, darktobright);

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
			
			final long Cannyradius = (long) (radiusfactor * Math.ceil(Math.sqrt(psf[0] * psf[0] + psf[1] * psf[1])));

			
			newimg = util.CopyUtils.copytoByteImage(Kernels.CannyEdgeandMean(currentPreprocessedimg, Cannyradius),
					standardRectangle);

			bitimg = new ArrayImgFactory<BitType>().create(newimg, new BitType());
			
			bitimgFloat = new ArrayImgFactory<FloatType>().create(newimg, new FloatType());
			
			GetLocalmaxmin.ThresholdingBit(currentPreprocessedimg, bitimg, thresholdHough);
			GetLocalmaxmin.Thresholding(currentPreprocessedimg, bitimgFloat, thresholdHough, IntensityType.Gaussian, new double[]{0.5, 0.5});

			if (displayBitimg)
				ImageJFunctions.show(bitimg);

			
			WatershedDistimg<FloatType> WaterafterDisttransform = new WatershedDistimg<FloatType>(Kernels.CannyEdgeandMean(currentPreprocessedimg, Cannyradius),
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

			newtree = MserTree.buildMserTree(newimg, delta, minSize, maxSize, Unstability_Score, minDiversity, darktobright);
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
	public JFrame Cardframe = new JFrame("MicroTubule Velocity Tracker (Advanced Mode)");
	
	public JPanel panelCont = new JPanel();
	public JPanel panelNext = new JPanel();
	public JPanel panelPrevious = new JPanel();
	public JPanel panelFirst = new JPanel();
	public JPanel panelSecond = new JPanel();
	public JPanel panelThird = new JPanel();
	public JPanel panelFourth = new JPanel();
	

	
	
	
	public JPanel controlnext = new JPanel();
	public JPanel controlprevious = new JPanel();
	private JPanel Methodchoice = new JPanel();
	private JPanel Cannychoice = new JPanel();
	private JPanel Directoryoptions = new JPanel();
	public JPanel Mserparam = new JPanel();
	public JPanel MserparamHF = new JPanel();
	public JPanel Houghparam = new JPanel();
	public JPanel HoughparamHF = new JPanel();
	public JPanel MserwHoughparam = new JPanel();
	public JPanel Optimize = new JPanel();
	
	
	
private static final Insets insets = new Insets(10, 0, 0, 0);
	
	public final GridBagLayout layout = new GridBagLayout();
	public final GridBagConstraints c = new GridBagConstraints();
	
	
	
	
	
	
	public void Card() {

		CardLayout cl = new CardLayout();

		panelCont.setLayout(cl);
		
		panelCont.add(panelFirst, "1");
		panelCont.add(panelSecond, "2");
		panelCont.add(panelThird, "3");
		panelCont.add(panelFourth, "4");

		//Insets for labels : c.insets = new Insets(20, 100, 0, 200);
		
		// First Panel
				panelFirst.setName("Choose method");
				inputradi = new JLabel("Edge enhancement factor: ");
				inputFieldradi = new TextField();
				inputFieldradi.setColumns(10);
				inputFieldradi.setText(String.valueOf(radiusfactor));
				
				final Scrollbar thirdDimensionslider = new Scrollbar(Scrollbar.HORIZONTAL, thirdDimensionsliderInit, 0, 0,
						thirdDimensionSize);
				thirdDimensionslider.setBlockIncrement(1);
				this.thirdDimensionslider = (int) computeIntValueFromScrollbarPosition(thirdDimensionsliderInit, timeMin,
						thirdDimensionSize, thirdDimensionSize);
				final Label timeText = new Label("Time index = " + this.thirdDimensionslider, Label.CENTER);
				final Button JumpinTime = new Button("Jump in time :");
				final JButton ChooseDirectory = new JButton("Choose Directory");
				TextField inputField = new TextField();
				inputField.setColumns(20);
				inputField.setText(userfile.getName().replaceFirst("[.][^.]+$", ""));

				addToName = inputField.getText();
				

				String[] Method = { "MSER", "HOUGH","MSERwHOUGH" };
				JComboBox<String> ChooseMethod = new JComboBox<String>(Method);

				panelFirst.setLayout(layout);
		        panelThird.setLayout(layout);
				panelNext.setLayout(layout);
				Cannychoice.setLayout(layout);
				Directoryoptions.setLayout(layout);
				
				
				
				c.insets = new Insets(5, 5, 5, 5);
				c.anchor = GridBagConstraints.CENTER;

				c.weightx = 0;
				c.weighty = 0;

				c.gridy = 1;
				c.gridx = 0;

				Border methodborder = new CompoundBorder(new TitledBorder("Object recognition methods"),
						new EmptyBorder(c.insets));

				
				Border cannyborder = new CompoundBorder(new TitledBorder("Edge enhancment (optional)"),
						new EmptyBorder(c.insets));
				
				Border dirborder = new CompoundBorder(new TitledBorder("File name"),
						new EmptyBorder(c.insets));
				
				Cannychoice.add(inputradi, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
						GridBagConstraints.RELATIVE, insets, 0, 0));
				
				Cannychoice.add(inputFieldradi, new GridBagConstraints(3, 0, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
						GridBagConstraints.RELATIVE, insets, 0, 0));
				
				Cannychoice.setBorder(cannyborder);
				panelFirst.add(Cannychoice, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
				
				Methodchoice.add(ChooseMethod, new GridBagConstraints(1, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			
				Methodchoice.setBorder(methodborder);
				panelFirst.add(Methodchoice,  new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
				
/*				
				Directoryoptions.add(inputField,  new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0) );
				Directoryoptions.add(ChooseDirectory,  new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0) );
			
				Directoryoptions.setBorder(dirborder);

				
				
				panelFirst.add(Directoryoptions, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

*/
		       
				panelFirst.setVisible(true);
				cl.show(panelCont, "1");

				// MedFiltercur.addItemListener(new MediancurrListener() );

				// ChoiceofTracker.addActionListener(new
				// TrackerButtonListener(Cardframe));
				inputFieldradi.addTextListener(new SingleRadiusListener(this));
			//	mser.addItemListener(new MserListener(this));
			//	hough.addItemListener(new HoughListener(this));
			//	mserwhough.addItemListener(new MserwHoughListener(this));
				ChooseDirectory.addActionListener(new SingleChooseDirectoryListener(this, inputField, userfile));
		        ChooseMethod.addActionListener(new SingleMethodListener(this, ChooseMethod));
				
				
				



			
				
				Cardframe.add(panelCont, BorderLayout.CENTER);

				Cardframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				Cardframe.pack();
				Cardframe.setVisible(true);
		


	

		Cardframe.add(panelCont, BorderLayout.CENTER);
		Cardframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Cardframe.pack();
		Cardframe.setVisible(true);

	}

	public void Deterministic() {
/*
		showDeterministic = true;
		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;

		panelFifth.removeAll();

		final Label Step5 = new Label("Step 5", Label.CENTER);
		panelFifth.setLayout(layout);
		panelFifth.add(Step5, c);

		JLabel lbltrack = new JLabel("Display SeedID's");

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

			choicestrack[index + 1] = "Seed " + currentseed;
		}


		JComboBox<String> cbtrack = new JComboBox<String>(choicestrack);
		
		
		
		final Button SkipframeandTrackEndPoints = new Button("TrackEndPoint from User specified first and last timepoint");
		final Button CheckResults = new Button("Check Results (then click next)");
		final Checkbox RoughResults = new Checkbox("Rates and Statistical Analysis");
		final Checkbox AdvancedOptions = new Checkbox("Advanced Optimizer Options ", AdvancedChoice);
		
		
		final Label Checkres = new Label("The tracker now performs an internal check on the results");
		Checkres.setBackground(new Color(1, 0, 1));
		Checkres.setForeground(new Color(255, 255, 255));
		final Label Done = new Label("Hope that everything was to your satisfaction! You can now exit.");
		final Button Exit = new Button("Close and exit");
		final Button Record = new Button("Save program parameters for batch mode");
		Done.setBackground(new Color(1, 0, 1));
		Done.setForeground(new Color(255, 255, 255));
	

		++c.gridy;
		c.insets = new Insets(10, 175, 0, 175);
		panelFifth.add(AdvancedOptions, c);
		

		
		
		++c.gridy;
		c.insets = new Insets(10, 10, 0, 175);
		panelFifth.add(SkipframeandTrackEndPoints, c);


		++c.gridy;
		c.insets = new Insets(10, 10, 0, 50);
		panelFifth.add(lbltrack, c);
		
		++c.gridy;
		c.insets = new Insets(10, 10, 0, 50);
		panelFifth.add(cbtrack, c);
		/*
		++c.gridy;
		c.insets = new Insets(10, 10, 0, 175);
		panelFifth.add(RoughResults, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 50);
		panelFifth.add(Record, c);
		++c.gridy;
		c.insets = new Insets(10, 10, 0, 50);
		panelFifth.add(Done, c);

	//	++c.gridy;
	//	c.insets = new Insets(10, 10, 0, 50);
	//	panelFifth.add(Exit, c);

		Exit.addActionListener(new FinishedButtonListener(Cardframe, true));
		SkipframeandTrackEndPoints.addActionListener(new SingleSkipFramesandTrackendsListener(this, thirdDimension,  thirdDimensionSize));
		
		Record.addActionListener(new SingleBatchModeListener(this));
		AdvancedOptions.addItemListener(new SingleAdvancedTrackerListener(this));
		cbtrack.addActionListener(new SingleSeedDisplayListener(cbtrack, Views.hyperSlice(originalimg, 2, 0),this));
		panelFifth.repaint();
		panelFifth.validate();
		Cardframe.pack();
*/
	}


	public void UpdateHough() {

panelFourth.removeAll();
FindLinesViaMSERwHOUGH = false;
panelNext.removeAll();
panelPrevious.removeAll();
		FindLinesViaMSER = false;
		FindLinesViaHOUGH = true;
		FindLinesViaMSERwHOUGH = false;
		

		final Button Record = new Button("Save program parameters for batch mode");
		final JButton Finalize = new JButton("Start tracking");
		panelFourth.setLayout(layout);
		HoughparamHF.setLayout(layout);
		Optimize.setLayout(layout);
		
		Border houghborder = new CompoundBorder(new TitledBorder("Watershed and MSER parameters"), new EmptyBorder(c.insets));
		Border optborder = new CompoundBorder(new TitledBorder("Tracker options"), new EmptyBorder(c.insets));
		
		
		final Label exthresholdText = new Label("threshold = threshold to create Bitimg for watershedding.",
				Label.CENTER);

		final Label thresholdText = new Label("thresholdValue = " + thresholdHough, Label.CENTER);

		final Scrollbar threshold = new Scrollbar(Scrollbar.HORIZONTAL, (int) thresholdHoughInit, 10, 0,
				10 + scrollbarSize);

		final Checkbox displayBit = new Checkbox("Display Bitimage ", displayBitimg);
		final Checkbox displayWatershed = new Checkbox("Display Watershedimage ", displayWatershedimg);



		final Scrollbar deltaS = new Scrollbar(Scrollbar.HORIZONTAL, deltaInit, 10, 0, 10 + scrollbarSize);
		final Scrollbar Unstability_ScoreS = new Scrollbar(Scrollbar.HORIZONTAL, Unstability_ScoreInit, 10, 0,
				10 + scrollbarSize);
		final Scrollbar minDiversityS = new Scrollbar(Scrollbar.HORIZONTAL, minDiversityInit, 10, 0,
				10 + scrollbarSize);
		final Scrollbar minSizeS = new Scrollbar(Scrollbar.HORIZONTAL, minSizeInit, 10, 0, 10 + scrollbarSize);
		final Scrollbar maxSizeS = new Scrollbar(Scrollbar.HORIZONTAL, maxSizeInit, 10, 0, 10 + scrollbarSize);

		final Label deltaText = new Label("Intensity threshold = " + delta, Label.CENTER);
		final Label Unstability_ScoreText = new Label("Unstability score = " + Unstability_Score, Label.CENTER);
		final Label minDiversityText = new Label("minDiversity = " +minDiversity, Label.CENTER);
		final Label minSizeText = new Label("Min size of ellipses = " + minSize, Label.CENTER);
		final Label maxSizeText = new Label("Max size of ellipses = " + maxSize, Label.CENTER);

		final Checkbox AdvancedOptions = new Checkbox("Advanced Optimizer Options ", AdvancedChoiceSeeds);
		SingleDefaultModelHF loaddefault = new SingleDefaultModelHF(this);
		loaddefault.LoadDefault();
		
		
	
		panelFourth.add(Cannychoice, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
		HoughparamHF.add(thresholdText,  new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
	 
		HoughparamHF.add(threshold,  new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		HoughparamHF.add(displayBit,  new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		HoughparamHF.add(displayWatershed,  new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
	   
	
		
		HoughparamHF.add(deltaText,  new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		 
		HoughparamHF.add(deltaS,  new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		HoughparamHF.add(Unstability_ScoreText,  new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		HoughparamHF.add(Unstability_ScoreS,  new GridBagConstraints(0, 7, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		   
		HoughparamHF.add(minSizeText,  new GridBagConstraints(0, 8, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		HoughparamHF.add(minSizeS,  new GridBagConstraints(0, 9, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		  
		HoughparamHF.add(maxSizeText,  new GridBagConstraints(0, 10, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		 
		HoughparamHF.add(maxSizeS,  new GridBagConstraints(0, 11, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		 
		

		HoughparamHF.setBorder(houghborder);
		
		
		 panelFourth.add(HoughparamHF, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
		Optimize.add(AdvancedOptions, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		 
		Optimize.add(Finalize, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
		Optimize.add(Record, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
		Optimize.setBorder(optborder);
	
		 panelFourth.add(Optimize, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
		 
		 
			controlnext.removeAll();
			controlprevious.add(new JButton(new AbstractAction("\u22b2Prev") {

			
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					CardLayout cl = (CardLayout) panelCont.getLayout();

					cl.previous(panelCont);
				}
			}));
		 
		

			panelPrevious.add(controlprevious,  new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
			controlprevious.setVisible(true);
			
			panelFourth.add(panelPrevious,  new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
			
		 
		 
		 
		 
		inputFieldradi.addTextListener(new SingleRadiusListener(this));
		Finalize.addActionListener(new SingleSkipFramesandTrackendsListener(this, starttime, endtime));
		Record.addActionListener(new SingleBatchModeListener(this));
		AdvancedOptions.addItemListener(new SingleAdvancedTrackerListener(this));

		 
		deltaS.addAdjustmentListener(new SingleDeltaListener(this, deltaText, deltaMin, deltaMax,scrollbarSize, deltaS));

		Unstability_ScoreS.addAdjustmentListener(
				new SingleUnstability_ScoreListener(this, Unstability_ScoreText, Unstability_ScoreMin, Unstability_ScoreMax, scrollbarSize, Unstability_ScoreS));

		minDiversityS.addAdjustmentListener(new SingleMinDiversityListener(this, minDiversityText, minDiversityMin,
				minDiversityMax, scrollbarSize, minDiversityS));

		minSizeS.addAdjustmentListener(
				new SingleMinSizeListener(this, minSizeText, minSizemin, minSizemax, scrollbarSize, minSizeS));

		maxSizeS.addAdjustmentListener(
				new SingleMaxSizeListener(this, maxSizeText, maxSizemin, maxSizemax, scrollbarSize, maxSizeS));
		threshold.addAdjustmentListener(new SingleThresholdHoughHFListener(this, thresholdText, thresholdHoughMin,
				thresholdHoughMax, scrollbarSize, threshold));

		displayBit.addItemListener(new SingleShowBitimgListener(this));
		displayWatershed.addItemListener(new SingleShowwatershedimgListener(this));
		// Dowatershed.addActionListener(new DowatershedListener(this));
		displayBitimg = false;
		displayWatershedimg = false;

		updatePreview(ValueChange.SHOWHOUGH);
		updatePreview(ValueChange.SHOWMSERinHough);
		panelFourth.repaint();
		panelFourth.validate();
		Cardframe.pack();
	}

	public void UpdateMser() {
		
		panelFourth.removeAll();
		FindLinesViaMSERwHOUGH = false;
		panelNext.removeAll();
		panelPrevious.removeAll();
		FindLinesViaMSER = true;
		FindLinesViaHOUGH = false;
		FindLinesViaMSERwHOUGH = false;
		
		final Button Record = new Button("Save program parameters for batch mode");
		final JButton Finalize = new JButton("Start tracking");
		panelFourth.setLayout(layout);
		MserparamHF.setLayout(layout);
		Optimize.setLayout(layout);
		
		Border msborder = new CompoundBorder(new TitledBorder("MSER parameters"), new EmptyBorder(c.insets));
		Border optborder = new CompoundBorder(new TitledBorder("Tracker options"), new EmptyBorder(c.insets));
		
		final Scrollbar deltaS = new Scrollbar(Scrollbar.HORIZONTAL, deltaInit, 10, 0, 10 + scrollbarSize);
		final Scrollbar Unstability_ScoreS = new Scrollbar(Scrollbar.HORIZONTAL, Unstability_ScoreInit, 10, 0, 10 + scrollbarSize);
		final Scrollbar minDiversityS = new Scrollbar(Scrollbar.HORIZONTAL, minDiversityInit, 10, 0,
				10 + scrollbarSize);
		final Scrollbar minSizeS = new Scrollbar(Scrollbar.HORIZONTAL, minSizeInit, 10, 0, 10 + scrollbarSize);
		final Scrollbar maxSizeS = new Scrollbar(Scrollbar.HORIZONTAL, maxSizeInit, 10, 0, 10 + scrollbarSize);
	
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

		final Label deltaText = new Label("Intensity threshold = " + delta, Label.CENTER);
		final Label Unstability_ScoreText = new Label("Unstability score = " + Unstability_Score, Label.CENTER);
		final Label minDiversityText = new Label("minDiversity = " +minDiversity, Label.CENTER);
		final Label minSizeText = new Label("Min size of ellipses = " + minSize, Label.CENTER);
		final Label maxSizeText = new Label("Max size of ellipses = " + maxSize, Label.CENTER);

		

		
		final Label MSparam = new Label("Determine MSER parameters");
		MSparam.setBackground(new Color(1, 0, 1));
		MSparam.setForeground(new Color(255, 255, 255));

		final Checkbox AdvancedOptions = new Checkbox("Advanced Optimizer Options ", AdvancedChoiceSeeds);
		SingleDefaultModelHF loaddefault = new SingleDefaultModelHF(this);
		loaddefault.LoadDefault();
		
		
		
		panelFourth.add(Cannychoice, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
		
		MserparamHF.add(deltaText,  new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		 
		MserparamHF.add(deltaS,  new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		MserparamHF.add(Unstability_ScoreText,  new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		MserparamHF.add(Unstability_ScoreS,  new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		   
		MserparamHF.add(minSizeText,  new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		MserparamHF.add(minSizeS,  new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		  
		MserparamHF.add(maxSizeText,  new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		 
		MserparamHF.add(maxSizeS,  new GridBagConstraints(0, 7, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		 
		

		MserparamHF.setBorder(msborder);
		
		 panelFourth.add(MserparamHF, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
		Optimize.add(AdvancedOptions, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		 
		Optimize.add(Finalize, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
		Optimize.add(Record, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
		Optimize.setBorder(optborder);
	
		 panelFourth.add(Optimize, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
		 
		 controlnext.removeAll();
			controlprevious.add(new JButton(new AbstractAction("\u22b2Prev") {

			
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					CardLayout cl = (CardLayout) panelCont.getLayout();

					cl.previous(panelCont);
				}
			}));
		 
		

			panelPrevious.add(controlprevious,  new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
			controlprevious.setVisible(true);
			
			panelFourth.add(panelPrevious,  new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
			
		 
			deltaS.addAdjustmentListener(new SingleDeltaListener(this, deltaText, deltaMin, deltaMax,scrollbarSize, deltaS));

			Unstability_ScoreS.addAdjustmentListener(
					new SingleUnstability_ScoreListener(this, Unstability_ScoreText, Unstability_ScoreMin, Unstability_ScoreMax, scrollbarSize, Unstability_ScoreS));

			minDiversityS.addAdjustmentListener(new SingleMinDiversityListener(this, minDiversityText, minDiversityMin,
					minDiversityMax, scrollbarSize, minDiversityS));

			minSizeS.addAdjustmentListener(
					new SingleMinSizeListener(this, minSizeText, minSizemin, minSizemax, scrollbarSize, minSizeS));

			maxSizeS.addAdjustmentListener(
					new SingleMaxSizeListener(this, maxSizeText, maxSizemin, maxSizemax, scrollbarSize, maxSizeS));


		inputFieldradi.addTextListener(new SingleRadiusListener(this));
		Finalize.addActionListener(new SingleSkipFramesandTrackendsListener(this, starttime, endtime));
		Record.addActionListener(new SingleBatchModeListener(this));
		AdvancedOptions.addItemListener(new SingleAdvancedTrackerListener(this));
		panelFourth.validate();
		panelFourth.repaint();
		Cardframe.pack();
	}

	public boolean moveDialogue() {
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
					thirdDimension = thirdDimensionslider;
					CurrentView = util.CopyUtils.getCurrentView(originalimg, thirdDimension, thirdDimensionSize);
					CurrentPreprocessedView = util.CopyUtils.getCurrentPreView(originalPreprocessedimg, thirdDimension,
							thirdDimensionSize);

				}

				// compute first version
				updatePreview(ValueChange.THIRDDIM);

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

	protected class MedianAllListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent arg0) {

			if (arg0.getStateChange() == ItemEvent.DESELECTED)
				MedianAll = false;
			else if (arg0.getStateChange() == ItemEvent.SELECTED) {

				MedianAll = true;

				DialogueMedian();


				final MedianFilter2D<FloatType> medfilter = new MedianFilter2D<FloatType>(originalPreprocessedimg,
						radius);
				medfilter.process();
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

	public float computeIntValueFromScrollbarPosition(final int scrollbarPosition, final float min, final float max,
			final int scrollbarSize) {
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

	public void displaystack() {

		ImagePlus Localimp = ImageJFunctions.show(originalimg);

		for (int i = thirdDimensionsliderInit; i < thirdDimensionSize; ++i) {

			prestack.addSlice(Localimp.getImageStack().getProcessor(i).convertToRGB());
			cp = (ColorProcessor) (prestack.getProcessor(i).duplicate());

			if (AllMSERrois.get(i)!=null){
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

	public boolean DialogueModelChoice() {

		GenericDialog gd = new GenericDialog("Model Choice for sub-pixel Localization");
		String[] LineModel = { "Linear Growth Model", "Beam Model", "Higher order Beam Model" };

		int indexmodel = 0;

		gd.addChoice("Choose your model: ", LineModel, LineModel[indexmodel]);
		gd.addCheckbox("Do Gaussian Mask Fits", Domask);

		gd.addNumericField(
				"Initial guess for Min Pixel Intensity (MinPI) belonging to MT (  R =  MinPI / MaxPI), R (enter 0.2 to 0.9) = ",
				Intensityratio, 2);
		gd.addNumericField(
				"Initial Spacing between Gaussians along the Polynomial curve = G * Min(Psf), G (enter positive number) = ",
				Inispacing / Math.min(psf[0], psf[1]), 2);
		gd.addNumericField("Maximum direction change per frame (in pixels)", maxdist, 2);

		if (analyzekymo && Kymoimg != null) {
			gd.addNumericField(
					"On an average, maximum difference in pixels between Kymograph and Program generated curve = ",
					deltadcutoff, 2);
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
		maxdist = gd.getNextNumber();

		if (analyzekymo && Kymoimg != null)
			deltadcutoff = (float) gd.getNextNumber();

		return !gd.wasCanceled();
	}

	public boolean DialogueModelChoiceHF() {

		GenericDialog gd = new GenericDialog("Model Choice for sub-pixel Localization");
		String[] LineModel = { "Linear Growth Model", "Beam Model", "Higher order Beam Model" };

		int indexmodel = 0;

		gd.addChoice("Choose your model: ", LineModel, LineModel[indexmodel]);

		gd.addCheckbox("Do Gaussian Mask Fits", Domask);
		gd.addCheckbox("DisplayRoi stack (after tracking)", displayoverlay);
		gd.addNumericField(
				"Initial guess for Min Pixel Intensity (MinPI) belonging to MT (  R =  MinPI / MaxPI), R (enter 0.2 to 0.9) = ",
				Intensityratio, 2);
		gd.addNumericField(
				"Initial Spacing between Gaussians along the Polynomial curve = G * Min(Psf), G (enter positive number ) = ",
				Inispacing / Math.min(psf[0], psf[1]), 2);

		gd.addNumericField("Maximum direction change per frame (in pixels)", maxdist, 2);
		gd.addNumericField("Number of Gaussians for mask fits ", numgaussians, 2);
		
		gd.showDialog();
		indexmodel = gd.getNextChoiceIndex();
		Domask = gd.getNextBoolean();
		displayoverlay = gd.getNextBoolean();

		if (indexmodel == 0)
			userChoiceModel = UserChoiceModel.Line;
		if (indexmodel == 1)
			userChoiceModel = UserChoiceModel.Splineordersec;
		if (indexmodel == 2)
			userChoiceModel = UserChoiceModel.Splineorderthird;
		Intensityratio = gd.getNextNumber();
		Inispacing = gd.getNextNumber() * Math.min(psf[0], psf[1]);
		maxdist = gd.getNextNumber();

		numgaussians = (int) gd.getNextNumber();
	

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

	public final void close(final Frame parent, final SliceObserver sliceObserver, final ImagePlus imp,
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

	public final void close(final Frame parent, final SliceObserver sliceObserver, RoiListener roiListener) {
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
		MainFileChooser panel = new MainFileChooser();

		frame.getContentPane().add(panel, "Center");
		frame.setSize(panel.getPreferredSize());
	}
}
