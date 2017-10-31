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
import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

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
import listeners.AdvancedSeedListener;
import listeners.AdvancedTrackerListener;
import listeners.BeginTrackListener;
import listeners.ChooseDirectoryListener;
import listeners.DeltaMTListener;
import listeners.EndTrackListener;
import listeners.FindLinesListener;
import listeners.MinSizeMTListener;
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
import updateListeners.Markends;
import updateListeners.Markendsnew;
import updateListeners.MoveNextListener;
import updateListeners.MoveToFrameListener;

public class Interactive_MTDoubleChannelBasic implements PlugIn {

	final Interactive_MTDoubleChannel parent;
	public JLabel inputLabelX;
	public JLabel inputLabelY;
	public JLabel inputLabelT;
	public TextField inputFieldX;
	public TextField inputFieldY;
	public TextField inputFieldT;

	public Interactive_MTDoubleChannelBasic() {
		this.parent = null;
	};

	public Interactive_MTDoubleChannelBasic(final Interactive_MTDoubleChannel parent) {

		this.parent = parent;

	}

	@Override
	public void run(String arg) {
		
		System.out.println(parent.addToName + " " + parent.userfile);
		
		parent.usefolder = parent.userfile.getParentFile().getAbsolutePath();

		parent.FindLinesViaMSER = true;
		parent.doMserSegmentation = true;

		
		parent.newends = new Markendsnew(parent);
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
		parent.setInitialmaxSize((int)(parent.originalimg.dimension(0) * parent.originalimg.dimension(1)));

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
		parent.preprocessedimp.setTitle("Active image" + " " +  " time point : " + parent.thirdDimension);
		parent.starttime = 2;
		parent.endtime = parent.thirdDimensionSize;
		Roi roi = parent.preprocessedimp.getRoi();

		if (roi == null) {
			roi = parent.preprocessedimp.getRoi();
		}

		

		// copy the ImagePlus into an ArrayImage<FloatType> for faster access
		// displaySliders();
		CardSmall();

		// compute first version#
		parent.updatePreview(ValueChange.ALL);
		parent.isStarted = true;

		// check whenever roi is modified to update accordingly
		parent.preprocessedimp.getCanvas().addMouseListener(parent.roiListener);


	}

	public JFrame CardframeSimple = new JFrame("MTrack (Simple Mode)");
	public JPanel controlnext = new JPanel();
	public JPanel controlprevious = new JPanel();
	public JPanel panelCont = new JPanel();
	public JPanel panelFirst = new JPanel();
	public JPanel panelSecond = new JPanel();
	public JPanel panelNext = new JPanel();
	public JPanel panelThird = new JPanel();
	private JPanel Directoryoptions = new JPanel();
	public JPanel Mserparam = new JPanel();
	public final GridBagLayout layout = new GridBagLayout();
	public final GridBagConstraints c = new GridBagConstraints();

	public void CardSmall() {

		CardLayout cl = new CardLayout();

	
		CardframeSimple.setMinimumSize(new Dimension(400,400));

		panelCont.setLayout(cl);

		panelCont.add(panelFirst, "1");
		panelCont.add(panelSecond, "2");
		panelNext.setLayout(layout);
		panelFirst.setName("Choose parameters to find Seeds");

	
	
		final Label deltaText = new Label("Threshold difference = " + parent.delta, Label.CENTER);
		final Label Unstability_ScoreText = new Label("Unstability Score = " + parent.Unstability_Score, Label.CENTER);
		final Label minSizeText = new Label("Min size of  ellipses = " + parent.minSize, Label.CENTER);
		final Scrollbar deltaS = new Scrollbar(Scrollbar.HORIZONTAL, parent.deltaInit, 10, 0,
				10 + parent.scrollbarSize);
		final Scrollbar Unstability_ScoreS = new Scrollbar(Scrollbar.HORIZONTAL, parent.Unstability_ScoreInit, 10, 0,
				10 + parent.scrollbarSize);
		final Scrollbar minSizeS = new Scrollbar(Scrollbar.HORIZONTAL, parent.minSizeInit, 10, 0,
				10 + parent.scrollbarSize);

		final Button FindLinesListener = new Button("Find endpoints");

		parent.Unstability_Score = parent.computeValueFromScrollbarPosition(parent.Unstability_ScoreInit,
				parent.Unstability_ScoreMin, parent.Unstability_ScoreMax, parent.scrollbarSize);
		parent.delta = parent.computeValueFromScrollbarPosition(parent.deltaInit, parent.deltaMin, parent.deltaMax,
				parent.scrollbarSize);

		final Checkbox AdvancedOptions = new Checkbox("Advanced Optimizer Options ", parent.AdvancedChoiceSeeds);
		DefaultModel loaddefault = new DefaultModel(parent);
		loaddefault.LoadDefault();

		

		
		
		
		panelFirst.setLayout(layout);
		panelSecond.setLayout(layout);
		
		Mserparam.setLayout(layout);
		Directoryoptions.setLayout(layout);
		
		
		Border msborder = new CompoundBorder(new TitledBorder("1.5 MSER parameters"), new EmptyBorder(c.insets));
		Directoryoptions.setLayout(layout);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;

		
		
	
	   
	   Mserparam.add(deltaText,  new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
	 
	   Mserparam.add(deltaS,  new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

	   Mserparam.add(Unstability_ScoreText,  new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

	   Mserparam.add(Unstability_ScoreS,  new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
	   
	   Mserparam.add(minSizeText,  new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

	   Mserparam.add(minSizeS,  new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
	  
	   Mserparam.add(FindLinesListener, new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

	   Mserparam.setBorder(msborder);
	  
	  panelFirst.add(Mserparam, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

	

	
		panelFirst.setVisible(true);
		cl.show(panelCont, "1");

		deltaS.addAdjustmentListener(
				new DeltaMTListener(parent, deltaText, parent.deltaMin, parent.deltaMax, parent.scrollbarSize, deltaS));
		minSizeS.addAdjustmentListener(new MinSizeMTListener(parent, minSizeText, parent.minSizemin, parent.minSizemax,
				parent.scrollbarSize, minSizeS));
		Unstability_ScoreS.addAdjustmentListener(new Unstability_ScoreListener(parent, Unstability_ScoreText,
				parent.Unstability_ScoreMin, parent.Unstability_ScoreMax, parent.scrollbarSize, Unstability_ScoreS));

		AdvancedOptions.addItemListener(new AdvancedSeedListener(parent));
		FindLinesListener.addActionListener(new FindLinesListener(parent, this));
		
		parent.updatePreview(ValueChange.SHOWMSER);
		controlnext.add(new JButton(new AbstractAction("\u22b2Prev") {

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
		controlnext.add(new JButton(new AbstractAction("Next\u22b3") {

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

		
		
		CardframeSimple.add(panelCont, BorderLayout.CENTER);
		CardframeSimple.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		CardframeSimple.pack();
		CardframeSimple.setVisible(true);
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
