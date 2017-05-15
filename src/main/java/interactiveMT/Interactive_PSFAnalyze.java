package interactiveMT;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
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
import beadListener.DogListener;
import beadListener.MserListener;
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
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;
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
	
	public float deltaMax = 400f;
	public float Unstability_ScoreMin = 0;
	public float Unstability_ScoreMax = 1;
	
	public ArrayList<GaussianFitParam> FittedBeads;
	
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
	
	public ArrayList<double[]> AllmeanCovar = new ArrayList<double[]>();
	public ArrayList<Pair<double[], OvalRoi>> ClickedPoints = new ArrayList<Pair<double[], OvalRoi>>();
	public HashMap<Integer, ArrayList<EllipseRoi>> AllMSERrois = new HashMap<Integer, ArrayList<EllipseRoi>>();
	public ArrayList<RefinedPeak<Point>> peaks;
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
		SHOWMSER, SHOWDOG, ALL, ROI, FRAME, THIRDDIM, THIRDDIMTrack, DELTA, Unstability_Score, MINDIVERSITY,MINSIZE ,
		MAXSIZE,FindBeadsVia, SIGMA, THRESHOLD;
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

			

			if (roiChanged || currentimg == null || currentPreprocessedimg == null || newimg == null
					|| change == ValueChange.FRAME  || change == ValueChange.ALL) {
				

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
			final long Cannyradius = 2;

			currentimg = util.CopyUtils.extractImage(CurrentView, interval);
			currentPreprocessedimg = util.CopyUtils.extractImage(CurrentPreprocessedView, interval);

			newimg = util.CopyUtils.copytoByteImage(Kernels.CannyEdgeandMean(currentPreprocessedimg, Cannyradius),
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
			final long Cannyradius = 2;

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

			cl.preferredLayoutSize(Cardframe);
			panelCont.setLayout(cl);

			panelCont.add(panelFirst, "1");
			panelCont.add(panelSecond, "2");
		
			CheckboxGroup Finders = new CheckboxGroup();
			
			

			final Checkbox mser = new Checkbox("MSER", Finders, FindBeadsViaMSER);
			final Checkbox dog = new Checkbox("DoG", Finders, FindBeadsViaDOG);
			

			final JButton ChooseWorkspace = new JButton("Choose Directory");
			final JLabel outputfilename = new JLabel("Enter output filename: ");
			TextField inputField = new TextField();
			inputField.setColumns(10);
			final JButton ChooseDirectory = new JButton("Choose Directory");
			final Button JumpFrame = new Button("Jump in third dimension to :");
			
			final Scrollbar thirdDimensionsliderS = new Scrollbar(Scrollbar.HORIZONTAL, thirdDimensionsliderInit, 0, 0,
					thirdDimensionSize);
			thirdDimensionsliderS.setBlockIncrement(1);
			this.thirdDimensionslider = (int) computeValueFromScrollbarPosition(thirdDimensionsliderInit, thirdDimensionsliderInit,
					thirdDimensionSize, thirdDimensionSize);
			
			final Label zText = new Label("Third Dimensíonal slice = " + this.thirdDimensionslider, Label.CENTER);

			

			/* Instantiation */
			final GridBagLayout layout = new GridBagLayout();
			final GridBagConstraints c = new GridBagConstraints();

			panelFirst.setLayout(layout);

			final Label Name = new Label("Step 1", Label.CENTER);
			panelFirst.add(Name, c);

			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 4;
			c.weightx = 1;

			final Label Ends = new Label("Method Choice for finding Blobs");

			++c.gridy;
			panelFirst.add(Ends, c);

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 0);
			panelFirst.add(mser, c);

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 0);
			panelFirst.add(dog, c);

			if (thirdDimensionSize > 1) {
				++c.gridy;
				panelFirst.add(thirdDimensionsliderS, c);

				++c.gridy;
				panelFirst.add(zText, c);

				++c.gridy;
				c.insets = new Insets(0, 175, 0, 175);
				panelFirst.add(JumpFrame, c);
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

			mser.addItemListener(new MserListener(this));
			dog.addItemListener(new DogListener(this));
			JumpFrame.addActionListener(
					new moveInThirdDimListener(thirdDimensionsliderS, zText, thirdDimensionsliderInit, thirdDimensionSize));
			ChooseDirectory.addActionListener(new ChooseDirectoryListener(this, inputField));
			
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
			
			Cardframe.add(panelCont, BorderLayout.CENTER);
			Cardframe.add(control, BorderLayout.SOUTH);
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
					updatePreview(ValueChange.THIRDDIM);

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
		new ImageJ();

		JFrame frame = new JFrame("");
		BeadFileChooser panel = new BeadFileChooser();

		frame.getContentPane().add(panel, "Center");
		frame.setSize(panel.getPreferredSize());
	}
	
}
