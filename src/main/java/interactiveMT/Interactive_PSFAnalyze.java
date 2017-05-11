package interactiveMT;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;

import LineModels.UseLineModel.UserChoiceModel;
import fiji.tool.SliceListener;
import fiji.tool.SliceObserver;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.EllipseRoi;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.gui.RoiListener;
import ij.plugin.PlugIn;
import ij.plugin.frame.RoiManager;
import ij.process.ColorProcessor;
import mpicbg.imglib.multithreading.SimpleMultiThreading;
import mpicbg.imglib.util.Util;
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

public class Interactive_PSFAnalyze implements PlugIn {

	
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
	
	public float deltaMax = 400f;
	public float Unstability_ScoreMin = 0;
	public float Unstability_ScoreMax = 1;
	
	public RandomAccessibleInterval<FloatType> currentimg;
	public RandomAccessibleInterval<FloatType> othercurrentimg;
	public RandomAccessibleInterval<FloatType> currentPreprocessedimg;
	public RandomAccessibleInterval<FloatType> originalimg;
	public RandomAccessibleInterval<FloatType> originalPreprocessedimg;
	public RandomAccessibleInterval<FloatType> CurrentView;
	public RandomAccessibleInterval<FloatType> otherCurrentView;
	public RandomAccessibleInterval<FloatType> CurrentPreprocessedView;
	public RandomAccessibleInterval<UnsignedByteType> newimg;
	public RandomAccessibleInterval<IntType> intimg;
	public Color originalColor = new Color(0.8f, 0.8f, 0.8f);
	public Color inactiveColor = new Color(0.95f, 0.95f, 0.95f);
	float sigma = 0.5f;
	float sigma2 = 0.5f;
	float threshold = 1f;
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
	public SliceObserver sliceObserver;
	public RoiListener roiListener;
	public boolean isFinished = false;
	public boolean wasCanceled = false;
	boolean darktobright = false;
	FinalInterval interval;
	int inix = 20;
	int iniy = 20;
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
	public long minSizemin = 0;
	public long minSizemax = 1000;
	public long maxSizemin = 100;
	public long maxSizemax = 10000;
	ArrayList<EllipseRoi> MSERRois;
	ArrayList<Roi> DOGRois;
	public ArrayList<double[]> AllmeanCovar = new ArrayList<double[]>();
	public ArrayList<Pair<double[], OvalRoi>> ClickedPoints = new ArrayList<Pair<double[], OvalRoi>>();
	public HashMap<Integer, ArrayList<EllipseRoi>> AllMSERrois = new HashMap<Integer, ArrayList<EllipseRoi>>();
	ArrayList<RefinedPeak<Point>> peaks;
	public int Unstability_ScoreInit = 1;
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
		SHOWMSER, SHOWDOG, ALL, ROI, FRAME, THIRDDIM, THIRDDIMTrack ;
	}
	
	
	
	public Interactive_PSFAnalyze() {
	};
	
	
	public Interactive_PSFAnalyze(final RandomAccessibleInterval<FloatType> originalimg,
			final RandomAccessibleInterval<FloatType> originalPreprocessedimg){
		
		this.originalimg = originalimg;
		this.originalPreprocessedimg = originalPreprocessedimg;
		standardRectangle = new Rectangle(inix, iniy, (int) originalimg.dimension(0) - 2 * inix,
				(int) originalimg.dimension(1) - 2 * iniy);
		imp = ImageJFunctions.show(originalimg);
		impcopy = imp.duplicate();
	};
	
	
	
	@Override
	public void run(String arg) {
		UIManager.put("ProgressBar.font", Font.BOLD);
		jpb = new JProgressBar();
		MSERRois = new ArrayList<EllipseRoi>();
		DOGRois = new ArrayList<Roi>();
		peaks = new ArrayList<RefinedPeak<Point>>();
		
		nf.setMaximumFractionDigits(3);
		setInitialUnstability_Score(Unstability_ScoreInit);
		setInitialDelta(deltaInit);
		setInitialminDiversity(minDiversityInit);
		setInitialmaxSize(maxSizeInit);
		setInitialminSize(minSizeInit);
		
		
		if (originalimg.numDimensions() < 3) {

			thirdDimensionSize = 0;
		}

		if (originalimg.numDimensions() == 3) {

			thirdDimension = 1;
			thirdDimensionSize = (int) originalimg.dimension(2);

		}

		if (originalimg.numDimensions() > 3) {

			System.out.println("Image has wrong dimensionality, upload an XYT image");
			return;
		}
		
		CurrentView = util.CopyUtils.getCurrentView(originalimg, thirdDimension, thirdDimensionSize);
		CurrentPreprocessedView = util.CopyUtils.getCurrentPreView(originalPreprocessedimg, thirdDimension,
				thirdDimensionSize);
		
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

		
		
		//Put Card here Card();
		
		
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
		
		
		RoiManager roimanager = RoiManager.getInstance();

		if (roimanager == null) {
			roimanager = new RoiManager();
		}

		// Re-compute MSER ellipses if neccesary

		if (change == ValueChange.THIRDDIM ) {
			System.out.println("Current Z plane: " + thirdDimension);

			if (imp != null)
				imp.close();
			imp = ImageJFunctions.show(CurrentView);
			imp.setTitle("Current View in Z planen: " + " " + thirdDimension );

		}

		boolean roiChanged = false;
		Overlay overlay = imp.getOverlay();
		if (overlay == null) {
			overlay = new Overlay();
			imp.setOverlay(overlay);
		}

		overlay.clear();
		
		if (change == ValueChange.THIRDDIMTrack ) {

			if (MSERRois != null)
				MSERRois.clear();
			
			if (DOGRois != null)
				DOGRois.clear();
			
			// imp = ImageJFunctions.wrapFloat(CurrentView, "current");

			long[] min = { (long) standardRectangle.getMinX(), (long) standardRectangle.getMinY() };
			long[] max = { (long) standardRectangle.getMaxX(), (long) standardRectangle.getMaxY() };
			interval = new FinalInterval(min, max);

			currentimg = util.CopyUtils.extractImage(CurrentView, interval);
			othercurrentimg = util.CopyUtils.extractImage(otherCurrentView, interval);

			newimg = util.CopyUtils.copytoByteImage(currentimg, interval);


			if (FindBeadsViaMSER) {

				overlay.clear();
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

				overlay.clear();
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
}
