package listeners;

import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

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
		
		parent.controlnext.removeAll();
		parent.controlprevious.removeAll();
		parent.panelSecond.removeAll();
		parent.panelNext.removeAll();
		parent.controlnext.add(new JButton(new AbstractAction("Next\u22b3") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout) parent.panelCont.getLayout();
				cl.next(parent.panelCont);
			}
		}));

		
	
		
		
		parent.panelNext.add(parent.controlnext,  new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
	
		parent.controlnext.setVisible(true);
		parent.panelFirst.add(parent.panelNext,  new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		parent.panelFirst.revalidate();
		parent.panelFirst.repaint();
	
		
		
		if (selectedindex == 0){
			
			parent.FindLinesViaMSER = true;
			parent.FindLinesViaHOUGH = false;
			parent.FindLinesViaMSERwHOUGH = false;

			parent.panelSecond.removeAll();
			if (parent.inputFieldradi.getText().length() > 0)
			parent.radiusfactor = Double.parseDouble(parent.inputFieldradi.getText());
			
			
			

			parent.Mserparam.setLayout(parent.layout);
			Border msborder = new CompoundBorder(new TitledBorder("MSER parameters"), new EmptyBorder(parent.c.insets));
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

			final Label deltaText = new Label("Intensity threshold = " + parent.delta, Label.CENTER);
			final Label Unstability_ScoreText = new Label("Unstability score = " + parent.Unstability_Score, Label.CENTER);
			final Label minDiversityText = new Label("minDiversity = " +parent.minDiversity, Label.CENTER);
			final Label minSizeText = new Label("Min size of red ellipses = " + parent.minSize, Label.CENTER);
			final Label maxSizeText = new Label("Max size of red ellipses = " + parent.maxSize, Label.CENTER);

			

			
			final Label MSparam = new Label("Determine MSER parameters");
			MSparam.setBackground(new Color(1, 0, 1));
			MSparam.setForeground(new Color(255, 255, 255));

			final Checkbox AdvancedOptions = new Checkbox("Advanced Optimizer Options ", parent.AdvancedChoiceSeeds);
			DefaultModel loaddefault = new DefaultModel(parent);
			loaddefault.LoadDefault();
			
			/* Location */
			parent.panelSecond.setLayout(parent.layout);

			
			 parent.Mserparam.add(deltaText,  new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			 
			 parent.Mserparam.add(deltaS,  new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

			 parent.Mserparam.add(Unstability_ScoreText,  new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

			 parent.Mserparam.add(Unstability_ScoreS,  new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			 
			 parent.Mserparam.add(minDiversityText,  new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

			 parent.Mserparam.add(minDiversityS,  new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			   
			 parent.Mserparam.add(minSizeText,  new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

			 parent.Mserparam.add(minSizeS,  new GridBagConstraints(0, 7, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			  
			 parent.Mserparam.add(maxSizeText,  new GridBagConstraints(0, 8, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			 
			 parent.Mserparam.add(maxSizeS,  new GridBagConstraints(0, 9, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			 
			 parent.Mserparam.add(AdvancedOptions, new GridBagConstraints(0, 10, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			 
			 parent.Mserparam.add(FindLinesListener, new GridBagConstraints(0,11, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

			 parent.Mserparam.setBorder(msborder);
			
			 parent.panelSecond.add(parent.Mserparam, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			
			
			

			
		//	++c.gridy;
		//	c.insets = new Insets(10, 180, 0, 180);
		//	parent.panelSecond.add(FindLinesbatchListener, c);
			
			deltaS.addAdjustmentListener(new DeltaMTListener(parent, deltaText, parent.deltaMin, parent.deltaMax, 
					parent.scrollbarSize, deltaS));

			Unstability_ScoreS.addAdjustmentListener(
					new Unstability_ScoreListener(parent, Unstability_ScoreText, parent.Unstability_ScoreMin, parent.Unstability_ScoreMax, 
							parent.scrollbarSize, Unstability_ScoreS));

			minDiversityS.addAdjustmentListener(new MinDiversityMTListener(parent, minDiversityText, parent.minDiversityMin,
					parent.minDiversityMax, parent.scrollbarSize, minDiversityS));

			minSizeS.addAdjustmentListener(
					new MinSizeMTListener(parent, minSizeText,parent.minSizemin, parent.minSizemax,
                  parent.scrollbarSize, minSizeS));

			maxSizeS.addAdjustmentListener(
					new MaxSizeMTListener(parent,maxSizeText,parent. maxSizemin, parent.maxSizemax, 
							parent.scrollbarSize, maxSizeS));

			AdvancedOptions.addItemListener(new AdvancedSeedListener(parent));
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
			
			if (parent.inputFieldradi.getText().length() > 0)
			parent.radiusfactor = Double.parseDouble(parent.inputFieldradi.getText());

			parent.panelSecond.removeAll();
			

			parent.panelSecond.setLayout(parent.layout);
		    parent.Houghparam.setLayout(parent.layout);
		

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
			parent.panelSecond.setLayout(parent.layout);

			parent.c.fill = GridBagConstraints.HORIZONTAL;
			parent.c.gridx = 0;
			parent.c.gridy = 0;
			parent.c.weightx = 4;
			parent.c.weighty = 1.5;
			
			Border houghborder = new CompoundBorder(new TitledBorder("Hough Transform parameters"), new EmptyBorder(parent.c.insets));
			
			 parent.Houghparam.add(thresholdText,  new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			 
			 parent.Houghparam.add(threshold,  new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

			 parent.Houghparam.add(thetaText,  new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

			 parent.Houghparam.add(thetaSize,  new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			   
			 parent.Houghparam.add(rhoText,  new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

			 parent.Houghparam.add(rhoSize,  new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			  
			 parent.Houghparam.add(displayBit,  new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			 
			 parent.Houghparam.add(displayWatershed,  new GridBagConstraints(0, 7, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			 
			 parent.Houghparam.add(AdvancedOptions, new GridBagConstraints(0, 8, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			 
			 parent.Houghparam.add(FindLinesListener, new GridBagConstraints(0, 9, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

			 parent.Houghparam.setBorder(houghborder);
			
			 parent.panelSecond.add(parent.Houghparam, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			
			
			
			

			threshold.addAdjustmentListener(new ThresholdHoughListener(parent, thresholdText, parent.thresholdHoughMin,
					parent.thresholdHoughMax, parent.scrollbarSize, threshold));

			thetaSize.addAdjustmentListener(new ThetaSizeHoughListener(parent, thetaText, rhoText, parent.thetaPerPixelMin,
					parent.thetaPerPixelMax, parent.scrollbarSize, thetaSize, rhoSize));

			rhoSize.addAdjustmentListener(
					new RhoSizeHoughListener(parent, rhoText, parent.rhoPerPixelMin, parent.rhoPerPixelMax, parent.scrollbarSize, rhoSize));

			displayBit.addItemListener(new ShowBitimgMTListener(parent));
			displayWatershed.addItemListener(new ShowwatershedimgMTListener(parent));
			Dowatershed.addActionListener(new DowatershedListener(parent));
			AdvancedOptions.addItemListener(new AdvancedSeedListener(parent));
			FindLinesListener.addActionListener(new FindLinesListener(parent));
			parent.panelSecond.revalidate();
			parent.panelSecond.repaint();
            parent.Cardframe.pack();
            parent.updatePreview(ValueChange.SHOWHOUGH);
            
		}
		
		if (selectedindex == 2){
			

			parent.FindLinesViaMSERwHOUGH = true;
			parent.FindLinesViaMSER = false;
			parent.FindLinesViaHOUGH = false;

			
			parent.panelSecond.removeAll();
			if (parent.inputFieldradi.getText().length() > 0)
			parent.radiusfactor = Double.parseDouble(parent.inputFieldradi.getText());

			parent.panelSecond.setLayout(parent.layout);
			parent.MserwHoughparam.setLayout(parent.layout);
			Border mserwhoughborder = new CompoundBorder(new TitledBorder("Mser w Hough Transform parameters"), new EmptyBorder(parent.c.insets));
			
			
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

			parent.panelSecond.setLayout(parent.layout);

			parent.c.fill = GridBagConstraints.HORIZONTAL;
			parent.c.gridx = 0;
			parent.c.gridy = 0;
			parent.c.weightx = 4;
			parent.c.weighty = 1.5;

			
			
			 parent.MserwHoughparam.add(deltaText,  new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			 
			 parent.MserwHoughparam.add(deltaS,  new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

			 parent.MserwHoughparam.add(Unstability_ScoreText,  new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

			 parent.MserwHoughparam.add(Unstability_ScoreS,  new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			 
			 parent.MserwHoughparam.add(minDiversityText,  new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

			 parent.MserwHoughparam.add(minDiversityS,  new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			   
			 parent.MserwHoughparam.add(minSizeText,  new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

			 parent.MserwHoughparam.add(minSizeS,  new GridBagConstraints(0, 7, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			  
			 parent.MserwHoughparam.add(maxSizeText,  new GridBagConstraints(0, 8, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			 
			 parent.MserwHoughparam.add(maxSizeS,  new GridBagConstraints(0, 9, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			 
			  
			 parent.MserwHoughparam.add(thetaText,  new GridBagConstraints(0, 10, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			 
			 parent.MserwHoughparam.add(thetaSize,  new GridBagConstraints(0, 11, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			 
			 parent.MserwHoughparam.add(rhoText,  new GridBagConstraints(0, 12, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			 
			 parent.MserwHoughparam.add(rhoSize,  new GridBagConstraints(0, 13, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			 
			 parent.MserwHoughparam.add(rhoEnable,  new GridBagConstraints(0, 14, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

			 
			 
			 
			 
			 
			 
			 parent.MserwHoughparam.add(AdvancedOptions, new GridBagConstraints(0, 15, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			 
			 parent.MserwHoughparam.add(FindLinesListener, new GridBagConstraints(0, 16, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

			 parent.MserwHoughparam.setBorder(mserwhoughborder);
			
			 parent.panelSecond.add(parent.MserwHoughparam, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			
			
			
			
			
			

			deltaS.addAdjustmentListener(new DeltaMTListener(parent, deltaText, parent.deltaMin, parent.deltaMax, parent.scrollbarSize, deltaS));

			Unstability_ScoreS.addAdjustmentListener(
					new Unstability_ScoreListener(parent, Unstability_ScoreText, parent.Unstability_ScoreMin, parent.Unstability_ScoreMax, parent.scrollbarSize, Unstability_ScoreS));

			minDiversityS.addAdjustmentListener(new MinDiversityMTListener(parent, minDiversityText, parent.minDiversityMin,
					parent.minDiversityMax, parent.scrollbarSize, minDiversityS));

			minSizeS.addAdjustmentListener(
					new MinSizeMTListener(parent, minSizeText, parent.minSizemin, parent.minSizemax, parent.scrollbarSize, minSizeS));

			maxSizeS.addAdjustmentListener(
					new MaxSizeMTListener(parent, maxSizeText, parent.maxSizemin, parent.maxSizemax, parent.scrollbarSize, maxSizeS));

			min.addItemListener(new DarktobrightListener(parent));

			FindLinesListener.addActionListener(new FindLinesListener(parent));

			thetaSize.addAdjustmentListener(new ThetaSizeHoughListener(parent, thetaText, rhoText, parent.thetaPerPixelMin,
					parent.thetaPerPixelMax, parent.scrollbarSize, thetaSize, rhoSize));

			rhoSize.addAdjustmentListener(
					new RhoSizeHoughListener(parent, rhoText, parent.rhoPerPixelMin, parent.rhoPerPixelMax, parent.scrollbarSize, rhoSize));
			AdvancedOptions.addItemListener(new AdvancedSeedListener(parent));
			parent.panelSecond.revalidate();
			parent.panelSecond.repaint();

			
			parent.Cardframe.pack();
			parent.updatePreview(ValueChange.SHOWMSER);
		}

		// Previous button for the second panel
		
				parent.controlprevious.add(new JButton(new AbstractAction("\u22b2Prev") {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {
						CardLayout cl = (CardLayout) parent.panelCont.getLayout();
						cl.previous(parent.panelCont);
					}
				}));
				
				parent.panelPrevious.add(parent.controlprevious,  new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			
				parent.controlprevious.setVisible(true);
				
				parent.panelSecond.add(parent.panelPrevious,  new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
				
		
		parent.panelSecond.validate();
		
		parent.Cardframe.pack();
		
	}

}
