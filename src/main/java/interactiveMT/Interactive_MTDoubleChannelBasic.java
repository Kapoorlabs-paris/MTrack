package interactiveMT;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.Color;
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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import fiji.tool.SliceObserver;
import ij.IJ;
import ij.ImageStack;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.plugin.Macro_Runner;
import ij.plugin.PlugIn;
import initialization.CreateINIfile;
import interactiveMT.Interactive_MTDoubleChannel.FinishedButtonListener;
import interactiveMT.Interactive_MTDoubleChannel.FrameListener;
import interactiveMT.Interactive_MTDoubleChannel.ImagePlusListener;
import interactiveMT.Interactive_MTDoubleChannel.RoiListener;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;
import labeledObjects.CommonOutputHF;
import labeledObjects.Indexedlength;
import listeners.AcceptResultsListener;
import listeners.AdvancedSeedListener;
import listeners.AdvancedTrackerListener;
import listeners.AnalyzekymoListener;
import listeners.BeginTrackListener;
import listeners.CheckResultsListener;
import listeners.ChooseDirectoryListener;
import listeners.ComputeTreeAgainListener;
import listeners.ComputeTreeListener;
import listeners.DeltaListener;
import listeners.EndTrackListener;
import listeners.FindLinesListener;
import listeners.MinSizeListener;
import listeners.SeedDisplayListener;
import listeners.SkipFramesandTrackendsListener;
import listeners.TrackendsListener;
import listeners.Unstability_ScoreListener;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.componenttree.mser.MserTree;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.view.Views;
import updateListeners.BatchModeListener;
import updateListeners.DefaultModel;
import updateListeners.DefaultModelHF;
import updateListeners.FinalPoint;
import updateListeners.MoveNextListener;
import updateListeners.MoveToFrameListener;

public class Interactive_MTDoubleChannelBasic implements PlugIn {

	final Interactive_MTDoubleChannel parent;
	private JLabel inputLabelX, inputLabelY, inputLabelT;
	private TextField inputFieldX, inputFieldY, inputFieldT;

	public Interactive_MTDoubleChannelBasic() {
		this.parent = null;
	};

	public Interactive_MTDoubleChannelBasic(final Interactive_MTDoubleChannel parent) {

		this.parent = parent;

	}

	@Override
	public void run(String arg) {
	
		parent.usefolder = parent.userfile.getParentFile().getAbsolutePath();

		parent.FindLinesViaMSER = true;
		parent.doMserSegmentation = true;

		parent.AllSeedrois = new ArrayList<OvalRoi>();
		parent.jpb = new JProgressBar();
		parent.newHoughtree = new HashMap<Integer, MserTree<UnsignedByteType>>();
		parent.Userframe = new ArrayList<Indexedlength>();
		parent.AllpreviousRois = new HashMap<Integer, ArrayList<Roi>>();
		parent.Inispacing = 0.5 * Math.min(parent.psf[0], parent.psf[1]);
		parent.count = 0;
		parent.overlay = new Overlay();
		parent.nf.setMaximumFractionDigits(3);
		parent.setInitialUnstability_Score(parent.Unstability_ScoreInit);
		parent.setInitialDelta(parent.deltaInit);
		parent.setInitialrhoPerPixel(parent.rhoPerPixelInit);
		parent.setInitialthetaPerPixel(parent.thetaPerPixelInit);
		parent.setInitialthresholdHough(parent.thresholdHoughInit);
		parent.setInitialminDiversity(parent.minDiversityInit);
		parent.setInitialmaxSize(parent.maxSizeInit);
		parent.setInitialminSize(parent.minSizeInit);
		parent.setInitialsearchradius(parent.initialSearchradiusInit);
		parent.setInitialmaxsearchradius(parent.maxSearchradius);

		parent.Cannyradius = (long) (parent.radiusfactor
				* Math.ceil(Math.sqrt(parent.psf[0] * parent.psf[0] + parent.psf[1] * parent.psf[1])));
		if (parent.originalimg.numDimensions() < 3) {

			parent.thirdDimensionSize = 0;
		}

		if (parent.originalimg.numDimensions() == 3) {

			parent.thirdDimension = 1;
			parent.startdim = 1;
			parent.thirdDimensionSize = (int) parent.originalimg.dimension(2);

		}

		if (parent.originalimg.numDimensions() > 3) {

			System.out.println("Image has wrong dimensionality, upload an XYT image");
			return;
		}

		if (parent.Kymoimg != null) {
			parent.Kymoimp = ImageJFunctions.show(parent.Kymoimg);

		}
		parent.prestack = new ImageStack((int) parent.originalimg.dimension(0), (int) parent.originalimg.dimension(1),
				java.awt.image.ColorModel.getRGBdefault());

		parent.CurrentView = util.CopyUtils.getCurrentView(parent.originalimg, parent.thirdDimension,
				parent.thirdDimensionSize);
		parent.CurrentPreprocessedView = util.CopyUtils.getCurrentPreView(parent.originalPreprocessedimg,
				parent.thirdDimension, parent.thirdDimensionSize);

		parent.output = new ArrayList<CommonOutputHF>();
		parent.endStack = parent.thirdDimensionSize;
		parent.thirdDimensionSizeOriginal = parent.thirdDimensionSize;
		parent.preprocessedimp = ImageJFunctions.show(parent.CurrentPreprocessedView);

		Roi roi = parent.preprocessedimp.getRoi();

		if (roi == null) {
			// IJ.log( "A rectangular ROI is required to define the area..." );
			parent.preprocessedimp.setRoi(parent.standardRectangle);
			roi = parent.preprocessedimp.getRoi();
		}

		if (roi.getType() != Roi.RECTANGLE) {
			IJ.log("Only rectangular rois are supported...");
			return;
		}

		// copy the ImagePlus into an ArrayImage<FloatType> for faster access
		// displaySliders();
		CardSmall();

		// compute first version#
		parent.updatePreview(ValueChange.ALL);
		parent.isStarted = true;

		// check whenever roi is modified to update accordingly
		parent.preprocessedimp.getCanvas().addMouseListener(parent.roiListener);

		IJ.log(" Third Dimension Size " + parent.thirdDimensionSize);

	}

	public JFrame CardframeSimple = new JFrame("MicroTubule Velocity Tracker (Simple Mode)");

	public JPanel panelCont = new JPanel();
	public JPanel panelFirst = new JPanel();
	public JPanel panelSecond = new JPanel();
	public JPanel panelThird = new JPanel();

	public void CardSmall() {

		CardLayout cl = new CardLayout();

		parent.Cardframe.setSize(CardframeSimple.getSize());

		panelCont.setLayout(cl);

		panelCont.add(panelFirst, "1");
		panelCont.add(panelSecond, "2");
		panelCont.add(panelThird, "3");

		panelFirst.setName("Choose parameters to find Seeds");

		final Label Step = new Label("Step 1", Label.CENTER);
		final Checkbox Analyzekymo = new Checkbox("Analyze Kymograph");
		final JButton ChooseDirectory = new JButton("Choose Directory");
		final JLabel outputfilename = new JLabel("Enter output filename: ");
		TextField inputField = new TextField();
		inputField.setColumns(10);
		inputField.setText(parent.userfile.getName().replaceFirst("[.][^.]+$", ""));
		final Label deltaText = new Label("Grey Level Seperation between Components = " + parent.delta, Label.CENTER);
		final Label Unstability_ScoreText = new Label("Unstability Score = " + parent.Unstability_Score, Label.CENTER);
		final Label minSizeText = new Label("Min # of pixels inside MSER Ellipses = " + parent.minSize, Label.CENTER);
		final Scrollbar deltaS = new Scrollbar(Scrollbar.HORIZONTAL, parent.deltaInit, 10, 0,
				10 + parent.scrollbarSize);
		final Scrollbar Unstability_ScoreS = new Scrollbar(Scrollbar.HORIZONTAL, parent.Unstability_ScoreInit, 10, 0,
				10 + parent.scrollbarSize);
		final Scrollbar minSizeS = new Scrollbar(Scrollbar.HORIZONTAL, parent.minSizeInit, 10, 0,
				10 + parent.scrollbarSize);

		final Button ComputeTree = new Button("Show MSER Ellipses");
		final Button FindLinesListener = new Button("Find endpoints");

		parent.Unstability_Score = parent.computeValueFromScrollbarPosition(parent.Unstability_ScoreInit,
				parent.Unstability_ScoreMin, parent.Unstability_ScoreMax, parent.scrollbarSize);
		parent.delta = parent.computeValueFromScrollbarPosition(parent.deltaInit, parent.deltaMin, parent.deltaMax,
				parent.scrollbarSize);

		final Checkbox AdvancedOptions = new Checkbox("Advanced Optimizer Options ", parent.AdvancedChoiceSeeds);
		DefaultModel loaddefault = new DefaultModel(parent);
		loaddefault.LoadDefault();

		final Label MSparam = new Label("Determine MSER parameters");
		MSparam.setBackground(new Color(1, 0, 1));
		MSparam.setForeground(new Color(255, 255, 255));

		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints c = new GridBagConstraints();

		panelFirst.setLayout(layout);
		panelFirst.add(Step, c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;

		final Label Kymo = new Label("Analyze Kymo");

		if (parent.Kymoimg != null) {
			c.insets = new Insets(10, 10, 0, 0);
			panelFirst.add(Kymo, c);
			++c.gridy;
			c.insets = new Insets(10, 10, 0, 0);
			panelFirst.add(Analyzekymo, c);
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
		++c.gridy;

		panelFirst.add(minSizeText, c);

		++c.gridy;
		panelFirst.add(minSizeS, c);

		++c.gridy;
		c.insets = new Insets(10, 175, 0, 175);
		panelFirst.add(AdvancedOptions, c);

		++c.gridy;
		c.insets = new Insets(10, 180, 0, 180);
		panelFirst.add(FindLinesListener, c);
		panelFirst.setVisible(true);
		cl.show(panelCont, "1");

		Analyzekymo.addItemListener(new AnalyzekymoListener(parent));
		ChooseDirectory.addActionListener(new ChooseDirectoryListener(parent, inputField, parent.userfile));
		deltaS.addAdjustmentListener(
				new DeltaListener(parent, deltaText, parent.deltaMin, parent.deltaMax, parent.scrollbarSize, deltaS));
		minSizeS.addAdjustmentListener(new MinSizeListener(parent, minSizeText, parent.minSizemin, parent.minSizemax,
				parent.scrollbarSize, minSizeS));
		Unstability_ScoreS.addAdjustmentListener(new Unstability_ScoreListener(parent, Unstability_ScoreText,
				parent.Unstability_ScoreMin, parent.Unstability_ScoreMax, parent.scrollbarSize, Unstability_ScoreS));

		AdvancedOptions.addItemListener(new AdvancedSeedListener(parent));
		ComputeTree.addActionListener(new ComputeTreeListener(parent));
		FindLinesListener.addActionListener(new FindLinesListener(parent));
		JPanel control = new JPanel();
		parent.updatePreview(ValueChange.SHOWMSER);
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

		// Panel Second
		final Button MoveNext = new Button("Choose first image in the dynamic channel to mark ends to track");
		final Button JumptoFrame = new Button("Choose a later time point in the dynamic channel to mark ends to track");

		final Label ORText = new Label("If the dynamic ends are not visible", Label.CENTER);

		ORText.setBackground(new Color(1, 0, 1));
		ORText.setForeground(new Color(255, 255, 255));
		inputLabelX = new JLabel("Enter start time point for tracking");
		inputFieldX = new TextField();
		inputFieldX.setColumns(5);

		inputLabelY = new JLabel("Enter end time point for tracking");
		inputFieldY = new TextField();
		inputFieldY.setColumns(5);
		inputFieldX.setText(String.valueOf(2));
		inputFieldY.setText(String.valueOf(parent.thirdDimensionSize));
		final Label LeftClick = new Label(
				"Left click deselects an end, Shift + left click selects a deselected end, Shift + Alt + left click marks a user defined seed.");

		LeftClick.setBackground(new Color(1, 0, 1));
		LeftClick.setForeground(new Color(255, 255, 255));
		final Checkbox Finalize = new Checkbox("Confirm the dynamic seed end(s)");

		final Label MTTextHF = new Label("Select ends for tracking", Label.CENTER);
		final Label Step2 = new Label("Step 2", Label.CENTER);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.5;

		panelSecond.setLayout(layout);
		panelSecond.add(Step2, c);
		++c.gridy;
		c.insets = new Insets(10, 10, 0, 0);
		panelSecond.add(LeftClick, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 0);
		panelSecond.add(MTTextHF, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 150);
		panelSecond.add(MoveNext, c);

		++c.gridy;
		c.insets = new Insets(20, 100, 0, 200);
		panelSecond.add(ORText, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 150);
		panelSecond.add(JumptoFrame, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 10, 0);
		panelSecond.add(inputLabelX, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 10, 0);
		panelSecond.add(inputFieldX, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 10, 0);
		panelSecond.add(inputLabelY, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 10, 0);
		panelSecond.add(inputFieldY, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 0);
		panelSecond.add(Finalize, c);

		MoveNext.addActionListener(new MoveNextListener(parent));
		JumptoFrame.addActionListener(new MoveToFrameListener(parent));

		CardframeSimple.addWindowListener(new FrameListener(CardframeSimple));

		inputFieldX.addTextListener(new BeginTrackListener(parent));
		inputFieldY.addTextListener(new EndTrackListener(parent));
		Finalize.addItemListener(new FinalPoint(parent, this));

		CardframeSimple.add(panelCont, BorderLayout.CENTER);
		CardframeSimple.add(control, BorderLayout.SOUTH);
		CardframeSimple.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		CardframeSimple.pack();
		CardframeSimple.setVisible(true);
	}

	public void DeterministicSimple() {

		parent.showDeterministic = true;
		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;

		panelThird.removeAll();

		final Label Step3 = new Label("Step 3", Label.CENTER);
		panelThird.setLayout(layout);
		panelThird.add(Step3, c);

		JLabel lbltrack = new JLabel("Display SeedID's");

		String[] choicestrack = new String[parent.IDALL.size() + 1];
		choicestrack[0] = "Display All";
		Comparator<Pair<Integer, double[]>> seedIDcomparison = new Comparator<Pair<Integer, double[]>>() {

			@Override
			public int compare(final Pair<Integer, double[]> A, final Pair<Integer, double[]> B) {

				return A.getA() - B.getA();

			}

		};

		Collections.sort(parent.IDALL, seedIDcomparison);
		for (int index = 0; index < parent.IDALL.size(); ++index) {

			String currentseed = Double.toString(parent.IDALL.get(index).getA());

			choicestrack[index + 1] = "Seed " + currentseed;
		}

		JComboBox<String> cbtrack = new JComboBox<String>(choicestrack);
		DefaultModelHF loaddefault = new DefaultModelHF(parent);
		loaddefault.LoadDefault();

		final Button SkipframeandTrackEndPoints = new Button(
				"TrackEndPoints from user specified first and last timepoint");
		final Button CheckResults = new Button("Check Results (then click next)");
		final Checkbox RoughResults = new Checkbox("Rates and Statistical Analysis");
		final Checkbox AdvancedOptions = new Checkbox("Advanced Optimizer Options ", parent.AdvancedChoice);

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
		panelThird.add(AdvancedOptions, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 175);
		panelThird.add(SkipframeandTrackEndPoints, c);
		if (parent.analyzekymo && parent.Kymoimg != null) {
			++c.gridy;
			c.insets = new Insets(10, 10, 0, 0);
			panelThird.add(Checkres, c);

			++c.gridy;
			c.insets = new Insets(10, 175, 0, 175);
			panelThird.add(CheckResults, c);
		}

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 50);
		panelThird.add(lbltrack, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 50);
		panelThird.add(cbtrack, c);
		/*
		 * ++c.gridy; c.insets = new Insets(10, 10, 0, 175);
		 * panelFifth.add(RoughResults, c);
		 */

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 50);
		panelThird.add(Record, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 50);
		panelThird.add(Done, c);

		// ++c.gridy;
		// c.insets = new Insets(10, 10, 0, 50);
		// panelThird.add(Exit, c);

		Exit.addActionListener(new FinishedButtonListener(CardframeSimple, true));

		Record.addActionListener(new BatchModeListener(parent));
		SkipframeandTrackEndPoints.addActionListener(
				new SkipFramesandTrackendsListener(parent, parent.thirdDimension, parent.thirdDimensionSize));
		CheckResults.addActionListener(new CheckResultsListener(parent));
		RoughResults.addItemListener(new AcceptResultsListener(parent));
		AdvancedOptions.addItemListener(new AdvancedTrackerListener(parent));
		cbtrack.addActionListener(new SeedDisplayListener(cbtrack, Views.hyperSlice(parent.originalimg, 2, 0), parent));
		panelThird.repaint();
		panelThird.validate();
		CardframeSimple.pack();

	}

	

	protected class FinishedButtonListener implements ActionListener {
		final Frame parentB;
		final boolean cancel;

		public FinishedButtonListener(Frame parentB, final boolean cancel) {
			this.parentB = parentB;
			this.cancel = cancel;
		}

		@Override
		public void actionPerformed(final ActionEvent arg0) {
			parent.wasCanceled = cancel;
			parent.close(parentB, parent.sliceObserver, parent.roiListener);
		}
	}

	protected class FrameListener extends WindowAdapter {
		final Frame parentB;

		public FrameListener(Frame parentB) {
			super();
			this.parentB = parentB;
		}

		@Override
		public void windowClosing(WindowEvent e) {
			parent.close(parentB, parent.sliceObserver, parent.preprocessedimp, parent.roiListener);
		}
	}

}
