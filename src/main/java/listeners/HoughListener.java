package listeners;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;
import mpicbg.imglib.multithreading.SimpleMultiThreading;
import updateListeners.DefaultModel;

public class HoughListener implements ItemListener {
	
	

		
		
		final Interactive_MTDoubleChannel parent;
		
		
		public HoughListener(final Interactive_MTDoubleChannel parent){
		
			this.parent = parent;
		}
	
	@Override
	public void itemStateChanged(final ItemEvent arg0) {
		boolean oldState = parent.FindLinesViaHOUGH;

		if (arg0.getStateChange() == ItemEvent.DESELECTED)
			parent.FindLinesViaHOUGH = false;
		else if (arg0.getStateChange() == ItemEvent.SELECTED) {

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

			displayBit.addItemListener(new ShowBitimgMTListener(parent));
			displayWatershed.addItemListener(new ShowwatershedimgMTListener(parent));
			Dowatershed.addActionListener(new DowatershedListener(parent));
			AdvancedOptions.addItemListener(new AdvancedSeedListener(parent));
			FindLinesListener.addActionListener(new FindLinesListener(parent));
			parent.panelSecond.validate();
			parent.panelSecond.repaint();
            parent.Cardframe.pack();

		}

		if (parent.FindLinesViaHOUGH != oldState) {
			while (parent.isComputing)
				SimpleMultiThreading.threadWait(10);

			parent.updatePreview(ValueChange.FindLinesVia);
		}
	}
}