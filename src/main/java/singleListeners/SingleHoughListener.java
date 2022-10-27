/*-
 * #%L
 * Microtubule tracker.
 * %%
 * Copyright (C) 2017 - 2022 MTrack developers.
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
package singleListeners;

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

import interactiveMT.Interactive_MTSingleChannel;
import interactiveMT.Interactive_MTSingleChannel.ValueChange;
import mpicbg.imglib.multithreading.SimpleMultiThreading;
import updateListeners.DefaultModel;
import updateListeners.SingleDefaultModel;

public class SingleHoughListener implements ItemListener {
	
	

		
		
		final Interactive_MTSingleChannel parent;
		
		
		public SingleHoughListener(final Interactive_MTSingleChannel parent){
		
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
			SingleDefaultModel loaddefault = new SingleDefaultModel(parent);
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
			parent.panelSecond.add(exthresholdText, c);
			++c.gridy;

			parent.panelSecond.add(exthetaText, c);
			++c.gridy;

			parent.panelSecond.add(exrhoText, c);
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

			threshold.addAdjustmentListener(new SingleThresholdHoughListener(parent, thresholdText, parent.thresholdHoughMin,
					parent.thresholdHoughMax, parent.scrollbarSize, threshold));

			thetaSize.addAdjustmentListener(new SingleThetaSizeHoughListener(parent, thetaText, rhoText, parent.thetaPerPixelMin,
					parent.thetaPerPixelMax, parent.scrollbarSize, thetaSize, rhoSize));

			rhoSize.addAdjustmentListener(
					new SingleRhoSizeHoughListener(parent, rhoText, parent.rhoPerPixelMin, parent.rhoPerPixelMax, parent.scrollbarSize, rhoSize));

			displayBit.addItemListener(new SingleShowBitimgListener(parent));
			displayWatershed.addItemListener(new SingleShowwatershedimgListener(parent));
			Dowatershed.addActionListener(new SingleDowatershedListener(parent));
			AdvancedOptions.addItemListener(new SingleAdvancedSeedListener(parent));
			FindLinesListener.addActionListener(new SingleFindLinesListener(parent));
			parent.panelSecond.validate();
			parent.panelSecond.repaint();

		}

		if (parent.FindLinesViaHOUGH != oldState) {
			while (parent.isComputing)
				SimpleMultiThreading.threadWait(10);

			parent.updatePreview(ValueChange.FindLinesVia);
		}
	}
}
