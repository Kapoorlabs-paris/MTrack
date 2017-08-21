package listeners;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;
import updateListeners.DefaultModel;


public class MethodListener implements ActionListener {

	
	
	final Interactive_MTDoubleChannel parent;
	final JComboBox<String> choice;
	
	public MethodListener(final Interactive_MTDoubleChannel parent, final JComboBox<String> choice){
		
		this.parent = parent;
		this.choice = choice;
		
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		int selectedindex = choice.getSelectedIndex();
		
		if (selectedindex == 0){
			
			parent.FindLinesViaMSER = true;
			parent.FindLinesViaHOUGH = false;
			parent.FindLinesViaMSERwHOUGH = false;

			parent.panelSecond.removeAll();
			if (parent.inputFieldradi.getText().length() > 0)
			parent.radiusfactor = Double.parseDouble(parent.inputFieldradi.getText());
			
			
			final GridBagLayout layout = new GridBagLayout();
			final GridBagConstraints c = new GridBagConstraints();
			final Label Step = new Label("Step 2", Label.CENTER);

			parent.panelSecond.setLayout(layout);

			parent.panelSecond.add(Step, c);
			final Scrollbar deltaS = new Scrollbar(Scrollbar.HORIZONTAL, parent.deltaInit, 10, 0, 10 + parent.scrollbarSize);
			final Scrollbar Unstability_ScoreS = new Scrollbar(Scrollbar.HORIZONTAL, parent.Unstability_ScoreInit, 10, 0, 10 + parent.scrollbarSize);
			final Scrollbar minDiversityS = new Scrollbar(Scrollbar.HORIZONTAL, parent.minDiversityInit, 10, 0,
					10 + parent.scrollbarSize);
			final Scrollbar minSizeS = new Scrollbar(Scrollbar.HORIZONTAL, parent.minSizeInit, 10, 0, 10 + parent.scrollbarSize);
			final Scrollbar maxSizeS = new Scrollbar(Scrollbar.HORIZONTAL, parent.maxSizeInit, 10, 0, 10 + parent.scrollbarSize);
			final Button ComputeTree = new Button("Compute Tree and display");
			final Button FindLinesListener = new Button("Find endpoints");
			final Button FindLinesbatchListener = new Button("Find endpoints in batch");
			parent.Unstability_Score = parent.computeValueFromScrollbarPosition(parent.Unstability_ScoreInit, parent.Unstability_ScoreMin, parent.Unstability_ScoreMax, 
					parent.scrollbarSize);
			parent.delta = parent.computeValueFromScrollbarPosition(parent.deltaInit, 
					parent.deltaMin, parent.deltaMax, parent.scrollbarSize);
			parent.minDiversity = parent.computeValueFromScrollbarPosition(parent.minDiversityInit, parent.minDiversityMin, 
					parent.minDiversityMax,
					parent.scrollbarSize);
			parent.minSize = (int) parent.computeValueFromScrollbarPosition(parent.minSizeInit, 
					parent.minSizemin, parent.minSizemax, parent.scrollbarSize);
			parent.maxSize = (int) parent.computeValueFromScrollbarPosition(parent.maxSizeInit, 
					parent.maxSizemin, parent.maxSizemax, parent.scrollbarSize);

			final Label deltaText = new Label("Grey Level Seperation between Components = " + parent.delta, Label.CENTER);
			final Label Unstability_ScoreText = new Label("Unstability Score = " + parent.Unstability_Score, Label.CENTER);
			final Label minDiversityText = new Label("minDiversity = " +parent.minDiversity, Label.CENTER);
			final Label minSizeText = new Label("Min # of pixels inside MSER Ellipses = " + parent.minSize, Label.CENTER);
			final Label maxSizeText = new Label("Max # of pixels inside MSER Ellipses = " + parent.maxSize, Label.CENTER);

			

			final Label MSparam = new Label("Determine MSER parameters");
			MSparam.setBackground(new Color(1, 0, 1));
			MSparam.setForeground(new Color(255, 255, 255));

			final Checkbox AdvancedOptions = new Checkbox("Advanced Optimizer Options ", parent.AdvancedChoiceSeeds);
			DefaultModel loaddefault = new DefaultModel(parent);
			loaddefault.LoadDefault();
			
			/* Location */
			parent.panelSecond.setLayout(layout);

			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 4;
			c.weighty = 1.5;

			++c.gridy;

			parent.panelSecond.add(MSparam, c);

			++c.gridy;

			parent.panelSecond.add(deltaText, c);

			++c.gridy;
			parent.panelSecond.add(deltaS, c);

			++c.gridy;

			parent.panelSecond.add(Unstability_ScoreText, c);

			++c.gridy;
			parent.panelSecond.add(Unstability_ScoreS, c);
/*
			++c.gridy;

			parent.panelSecond.add(minDiversityText, c);

			++c.gridy;
			parent.panelSecond.add(minDiversityS, c);
*/
			++c.gridy;

			parent.panelSecond.add(minSizeText, c);

			++c.gridy;
			parent.panelSecond.add(minSizeS, c);

			++c.gridy;

			parent.panelSecond.add(maxSizeText, c);

			++c.gridy;
			parent.panelSecond.add(maxSizeS, c);

			
			++c.gridy;
			c.insets = new Insets(10, 175, 0, 175);
			parent.panelSecond.add(AdvancedOptions, c);


			++c.gridy;
			c.insets = new Insets(10, 180, 0, 180);
			parent.panelSecond.add(FindLinesListener, c);

			
		//	++c.gridy;
		//	c.insets = new Insets(10, 180, 0, 180);
		//	parent.panelSecond.add(FindLinesbatchListener, c);
			
			deltaS.addAdjustmentListener(new DeltaListener(parent, deltaText, parent.deltaMin, parent.deltaMax, 
					parent.scrollbarSize, deltaS));

			Unstability_ScoreS.addAdjustmentListener(
					new Unstability_ScoreListener(parent, Unstability_ScoreText, parent.Unstability_ScoreMin, parent.Unstability_ScoreMax, 
							parent.scrollbarSize, Unstability_ScoreS));

			minDiversityS.addAdjustmentListener(new MinDiversityListener(parent, minDiversityText, parent.minDiversityMin,
					parent.minDiversityMax, parent.scrollbarSize, minDiversityS));

			minSizeS.addAdjustmentListener(
					new MinSizeListener(parent, minSizeText,parent.minSizemin, parent.minSizemax,
                  parent.scrollbarSize, minSizeS));

			maxSizeS.addAdjustmentListener(
					new MaxSizeListener(parent,maxSizeText,parent. maxSizemin, parent.maxSizemax, 
							parent.scrollbarSize, maxSizeS));

			AdvancedOptions.addItemListener(new AdvancedSeedListener(parent));
			ComputeTree.addActionListener(new ComputeTreeListener(parent));
			FindLinesListener.addActionListener(new FindLinesListener(parent));
			FindLinesbatchListener.addActionListener(new FindLinesbatchListener(parent));
			parent.panelSecond.validate();
			parent.panelSecond.repaint();
			parent.Cardframe.pack();
			parent.updatePreview(ValueChange.SHOWMSER);
		}
		
		if (selectedindex == 1){
			

			parent.FindLinesViaHOUGH = true;
			parent.FindLinesViaMSER = false;
			parent.FindLinesViaMSERwHOUGH = false;
			/* Instantiation */
			final GridBagLayout layout = new GridBagLayout();
			final GridBagConstraints c = new GridBagConstraints();
			if (parent.inputFieldradi.getText().length() > 0)
			parent.radiusfactor = Double.parseDouble(parent.inputFieldradi.getText());

			parent.panelSecond.removeAll();
			final Label Step = new Label("Step 2", Label.CENTER);

			parent.panelSecond.setLayout(layout);
			parent.panelSecond.add(Step, c);
			final Label exthresholdText = new Label("threshold = threshold to create Bitimg for watershedding.",
					Label.CENTER);
			final Label exthetaText = new Label("thetaPerPixel = Pixel Size in theta direction for Hough space.",
					Label.CENTER);
			final Label exrhoText = new Label("rhoPerPixel = Pixel Size in rho direction for Hough space.",
					Label.CENTER);

			final Label thresholdText = new Label("thresholdValue = " + parent.thresholdHough, Label.CENTER);
			final Label thetaText = new Label("Size of Hough Space in Theta = " + parent.thetaPerPixel, Label.CENTER);
			final Label rhoText = new Label("Size of Hough Space in Rho = " + parent.rhoPerPixel, Label.CENTER);
			final Scrollbar threshold = new Scrollbar(Scrollbar.HORIZONTAL, (int) parent.thresholdHoughInit, 10, 0,
					10 + parent.scrollbarSize);
			parent.thresholdHough = parent.computeValueFromScrollbarPosition((int) parent.thresholdHoughInit, parent.thresholdHoughMin,
					parent.thresholdHoughMax, parent.scrollbarSize);

			final Scrollbar thetaSize = new Scrollbar(Scrollbar.HORIZONTAL, (int) parent.thetaPerPixelInit, 10, 0,
					10 + parent.scrollbarSize);
			parent.thetaPerPixel = parent.computeValueFromScrollbarPosition((int) parent.thetaPerPixelInit, parent.thetaPerPixelMin,
					parent.thetaPerPixelMax, parent.scrollbarSize);

			final Scrollbar rhoSize = new Scrollbar(Scrollbar.HORIZONTAL, (int) parent.rhoPerPixelInit, 10, 0,
					10 + parent.scrollbarSize);
			parent.rhoPerPixel = parent.computeValueFromScrollbarPosition((int) parent.rhoPerPixelInit, parent.rhoPerPixelMin, parent.rhoPerPixelMax,
					parent.scrollbarSize);

			final Checkbox displayBit = new Checkbox("Display Bitimage ", parent.displayBitimg);
			final Checkbox displayWatershed = new Checkbox("Display Watershedimage ", parent.displayWatershedimg);

			final Button Dowatershed = new Button("Do watershedding");
			final Button FindLinesListener = new Button("Find endpoints");
			final Label Houghparam = new Label("Determine Hough Transform parameters");
			Houghparam.setBackground(new Color(1, 0, 1));
			Houghparam.setForeground(new Color(255, 255, 255));
			final Checkbox AdvancedOptions = new Checkbox("Advanced Optimizer Options ", parent.AdvancedChoiceSeeds);
			DefaultModel loaddefault = new DefaultModel(parent);
			loaddefault.LoadDefault();
			/* Location */
			parent.panelSecond.setLayout(layout);

			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 4;
			c.weighty = 1.5;
			++c.gridy;
			parent.panelSecond.add(Houghparam, c);

			
			++c.gridy;

			parent.panelSecond.add(thresholdText, c);
			++c.gridy;

			parent.panelSecond.add(threshold, c);
			++c.gridy;

			parent.panelSecond.add(thetaText, c);
			++c.gridy;
			parent.panelSecond.add(thetaSize, c);
			++c.gridy;

			parent.panelSecond.add(rhoText, c);

			++c.gridy;

			parent.panelSecond.add(rhoSize, c);

			++c.gridy;
			c.insets = new Insets(10, 175, 0, 175);
			parent.panelSecond.add(displayBit, c);

			++c.gridy;
			c.insets = new Insets(10, 175, 0, 175);
			parent.panelSecond.add(displayWatershed, c);
		
			
			
			++c.gridy;
			c.insets = new Insets(10, 175, 0, 175);
			parent.panelSecond.add(AdvancedOptions, c);
			++c.gridy;

			c.insets = new Insets(10, 175, 0, 175);
			parent.panelSecond.add(FindLinesListener, c);

			threshold.addAdjustmentListener(new ThresholdHoughListener(parent, thresholdText, parent.thresholdHoughMin,
					parent.thresholdHoughMax, parent.scrollbarSize, threshold));

			thetaSize.addAdjustmentListener(new ThetaSizeHoughListener(parent, thetaText, rhoText, parent.thetaPerPixelMin,
					parent.thetaPerPixelMax, parent.scrollbarSize, thetaSize, rhoSize));

			rhoSize.addAdjustmentListener(
					new RhoSizeHoughListener(parent, rhoText, parent.rhoPerPixelMin, parent.rhoPerPixelMax, parent.scrollbarSize, rhoSize));

			displayBit.addItemListener(new ShowBitimgListener(parent));
			displayWatershed.addItemListener(new ShowwatershedimgListener(parent));
			Dowatershed.addActionListener(new DowatershedListener(parent));
			AdvancedOptions.addItemListener(new AdvancedSeedListener(parent));
			FindLinesListener.addActionListener(new FindLinesListener(parent));
			parent.panelSecond.validate();
			parent.panelSecond.repaint();
            parent.Cardframe.pack();
		}
		
		if (selectedindex == 2){
			

			parent.FindLinesViaMSERwHOUGH = true;
			parent.FindLinesViaMSER = false;
			parent.FindLinesViaHOUGH = false;

			final GridBagLayout layout = new GridBagLayout();
			final GridBagConstraints c = new GridBagConstraints();
			parent.panelSecond.removeAll();
			final Label Step = new Label("Step 2", Label.CENTER);
			if (parent.inputFieldradi.getText().length() > 0)
			parent.radiusfactor = Double.parseDouble(parent.inputFieldradi.getText());

			parent.panelSecond.setLayout(layout);
			

			final Checkbox rhoEnable = new Checkbox("Enable Manual Adjustment of rhoPerPixel", parent.enablerhoPerPixel);

			final Scrollbar thetaSize = new Scrollbar(Scrollbar.HORIZONTAL, (int) parent.thetaPerPixelInit, 10, 0,
					10 + parent.scrollbarSize);
			parent.thetaPerPixel = parent.computeValueFromScrollbarPosition((int) parent.thetaPerPixelInit, parent.thetaPerPixelMin,
					parent.thetaPerPixelMax, parent.scrollbarSize);

			final Scrollbar rhoSize = new Scrollbar(Scrollbar.HORIZONTAL, (int) parent.rhoPerPixelInit, 10, 0,
					10 + parent.scrollbarSize);
			parent.rhoPerPixel = parent.computeValueFromScrollbarPosition((int) parent.rhoPerPixelInit, parent.rhoPerPixelMin, parent.rhoPerPixelMax,
					parent.scrollbarSize);

			final Label thetaText = new Label("Pixel size of Hough Space in Theta / Pixel Space = " + parent.thetaPerPixel,
					Label.CENTER);
			final Label rhoText = new Label("Pixel size of Hough Space in Rho / Pixel Space = " + parent.rhoPerPixel,
					Label.CENTER);
			final Button FindLinesListener = new Button("Find endpoints");
			final Label Houghparam = new Label("Determine MSER and Hough Transform parameters");
			Houghparam.setBackground(new Color(1, 0, 1));
			Houghparam.setForeground(new Color(255, 255, 255));

			final Scrollbar deltaS = new Scrollbar(Scrollbar.HORIZONTAL, parent.deltaInit, 10, 0, 10 + parent.scrollbarSize);
			final Scrollbar Unstability_ScoreS = new Scrollbar(Scrollbar.HORIZONTAL, parent.Unstability_ScoreInit, 10, 0, 10 + parent.scrollbarSize);
			final Scrollbar minDiversityS = new Scrollbar(Scrollbar.HORIZONTAL, parent.minDiversityInit, 10, 0,
					10 + parent.scrollbarSize);
			final Scrollbar minSizeS = new Scrollbar(Scrollbar.HORIZONTAL, parent.minSizeInit, 10, 0, 10 + parent.scrollbarSize);
			final Scrollbar maxSizeS = new Scrollbar(Scrollbar.HORIZONTAL, parent.maxSizeInit, 10, 0, 10 + parent.scrollbarSize);
			final Button ComputeTree = new Button("Compute Tree and display");

			parent.Unstability_Score = parent.computeValueFromScrollbarPosition(parent.Unstability_ScoreInit, parent.Unstability_ScoreMin, parent.Unstability_ScoreMax, parent.scrollbarSize);
			parent.delta = parent.computeValueFromScrollbarPosition(parent.deltaInit, parent.deltaMin, parent.deltaMax, parent.scrollbarSize);
			parent.minDiversity = parent.computeValueFromScrollbarPosition(parent.minDiversityInit, parent.minDiversityMin,parent.minDiversityMax,
					parent.scrollbarSize);
			parent.minSize = (int) parent.computeValueFromScrollbarPosition(parent.minSizeInit, parent.minSizemin, parent.minSizemax, parent.scrollbarSize);
			parent.maxSize = (int) parent.computeValueFromScrollbarPosition(parent.maxSizeInit, parent.maxSizemin, parent.maxSizemax, parent.scrollbarSize);

			final Checkbox min = new Checkbox("Look for Minima ", parent.darktobright);

			final Label deltaText = new Label("Grey Level Seperation between Components = " + parent.delta, Label.CENTER);
			final Label Unstability_ScoreText = new Label("Unstability Score = " + parent.Unstability_Score, Label.CENTER);
			final Label minDiversityText = new Label("minDiversity = " +parent.minDiversity, Label.CENTER);
			final Label minSizeText = new Label("Min # of pixels inside MSER Ellipses = " + parent.minSize, Label.CENTER);
			final Label maxSizeText = new Label("Max # of pixels inside MSER Ellipses = " + parent.maxSize, Label.CENTER);
			
			
			final Checkbox AdvancedOptions = new Checkbox("Advanced Optimizer Options ", parent.AdvancedChoiceSeeds);
			DefaultModel loaddefault = new DefaultModel(parent);
			loaddefault.LoadDefault();
			/* Location */

			parent.panelSecond.setLayout(layout);

			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 4;
			c.weighty = 1.5;

			
			++c.gridy;
			parent.panelSecond.add(Step, c);
			
			++c.gridy;
			parent.panelSecond.add(deltaText, c);

			++c.gridy;
			parent.panelSecond.add(deltaS, c);

			++c.gridy;

			parent.panelSecond.add(Unstability_ScoreText, c);

			++c.gridy;
			parent.panelSecond.add(Unstability_ScoreS, c);

			/*
			++c.gridy;

			parent.panelSecond.add(minDiversityText, c);

			++c.gridy;
			parent.panelSecond.add(minDiversityS, c);

			*/
			++c.gridy;

			parent.panelSecond.add(minSizeText, c);

			++c.gridy;
			parent.panelSecond.add(minSizeS, c);

			++c.gridy;

			parent.panelSecond.add(maxSizeText, c);

			++c.gridy;
			parent.panelSecond.add(maxSizeS, c);

		
		

			++c.gridy;
			parent.panelSecond.add(thetaText, c);
			++c.gridy;
			parent.panelSecond.add(thetaSize, c);
			++c.gridy;

			parent.panelSecond.add(rhoText, c);

			++c.gridy;

			parent.panelSecond.add(rhoSize, c);

			++c.gridy;
			c.insets = new Insets(0, 175, 0, 175);
			parent.panelSecond.add(rhoEnable, c);

			
			++c.gridy;
			c.insets = new Insets(10, 175, 0, 175);
			parent.panelSecond.add(AdvancedOptions, c);
			
			++c.gridy;
			c.insets = new Insets(10, 175, 0, 175);
			parent.panelSecond.add(FindLinesListener, c);

			deltaS.addAdjustmentListener(new DeltaListener(parent, deltaText, parent.deltaMin, parent.deltaMax, parent.scrollbarSize, deltaS));

			Unstability_ScoreS.addAdjustmentListener(
					new Unstability_ScoreListener(parent, Unstability_ScoreText, parent.Unstability_ScoreMin, parent.Unstability_ScoreMax, parent.scrollbarSize, Unstability_ScoreS));

			minDiversityS.addAdjustmentListener(new MinDiversityListener(parent, minDiversityText, parent.minDiversityMin,
					parent.minDiversityMax, parent.scrollbarSize, minDiversityS));

			minSizeS.addAdjustmentListener(
					new MinSizeListener(parent, minSizeText, parent.minSizemin, parent.minSizemax, parent.scrollbarSize, minSizeS));

			maxSizeS.addAdjustmentListener(
					new MaxSizeListener(parent, maxSizeText, parent.maxSizemin, parent.maxSizemax, parent.scrollbarSize, maxSizeS));

			min.addItemListener(new DarktobrightListener(parent));

			FindLinesListener.addActionListener(new FindLinesListener(parent));

			thetaSize.addAdjustmentListener(new ThetaSizeHoughListener(parent, thetaText, rhoText, parent.thetaPerPixelMin,
					parent.thetaPerPixelMax, parent.scrollbarSize, thetaSize, rhoSize));

			rhoSize.addAdjustmentListener(
					new RhoSizeHoughListener(parent, rhoText, parent.rhoPerPixelMin, parent.rhoPerPixelMax, parent.scrollbarSize, rhoSize));
			AdvancedOptions.addItemListener(new AdvancedSeedListener(parent));
			ComputeTree.addActionListener(new ComputeTreeListener(parent));
			parent.panelSecond.validate();
			parent.panelSecond.repaint();

			parent.Cardframe.pack();
			parent.updatePreview(ValueChange.SHOWMSER);
		}
		
	}

}
