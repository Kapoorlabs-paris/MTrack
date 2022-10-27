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
package beadListener;

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
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import interactiveMT.Interactive_PSFAnalyze;
import interactiveMT.Interactive_PSFAnalyze.ValueChange;
import mpicbg.imglib.multithreading.SimpleMultiThreading;


public class DogListener implements ItemListener {

	

	Interactive_PSFAnalyze parent;
	
	public DogListener(final Interactive_PSFAnalyze parent){
	
		this.parent = parent;
	}
	
		@Override
		public void itemStateChanged(final ItemEvent arg0) {

			boolean oldState = parent.FindBeadsViaDOG;
			if (arg0.getStateChange() == ItemEvent.DESELECTED)
				parent.FindBeadsViaDOG = false;
			else if (arg0.getStateChange() == ItemEvent.SELECTED) {

				parent.FindBeadsViaDOG = true;
				parent.FindBeadsViaMSER = false;
				parent.updatePreview(ValueChange.ROI);

				parent.panelSecond.removeAll();

				final GridBagLayout layout = new GridBagLayout();
				final GridBagConstraints c = new GridBagConstraints();

				parent.panelSecond.setLayout(layout);
				final Label Name = new Label("Step 2", Label.CENTER);
				parent.panelSecond.add(Name, c);
				final Label DogText = new Label("Use DoG to find Blobs ", Label.CENTER);
				final Scrollbar sigma1 = new Scrollbar(Scrollbar.HORIZONTAL, parent.sigmaInit, 10, 0, 10 + parent.scrollbarSize);

				final Scrollbar thresholdS = new Scrollbar(Scrollbar.HORIZONTAL, parent.thresholdInit, 10, 0,
						10 + parent.scrollbarSize);
				parent.sigma = parent.computeValueFromScrollbarPosition(parent.sigmaInit, parent.sigmaMin, parent.sigmaMax, parent.scrollbarSize);
				parent.threshold = parent.computeValueFromScrollbarPosition(parent.thresholdInit, parent.thresholdMin, parent.thresholdMax, parent.scrollbarSize);
				parent.sigma2 = parent.computeSigma2(parent.sigma, parent.sensitivity);
				final int sigma2init = parent.computeScrollbarPositionFromValue(parent.sigma2, parent.sigmaMin, parent.sigmaMax, parent.scrollbarSize);
				final Scrollbar sigma2S = new Scrollbar(Scrollbar.HORIZONTAL, sigma2init, 10, 0, 10 + parent.scrollbarSize);
				final Button FindBeadsListener = new Button("Fit Gaussian Function");

				final Label sigmaText1 = new Label("Sigma 1 = " + parent.sigma, Label.CENTER);
				final Label sigmaText2 = new Label("Sigma 2 = " + parent.sigma2, Label.CENTER);

				final Label thresholdText = new Label("Threshold = " + parent.threshold, Label.CENTER);

				
				final Button DisplayBlobs = new Button("Display Blobs");

				final Label MSparam = new Label("Determine DoG parameters");
				MSparam.setBackground(new Color(1, 0, 1));
				MSparam.setForeground(new Color(255, 255, 255));
				/* Location */
				parent.panelSecond.setLayout(layout);

				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridx = 0;
				c.gridy = 0;
				c.weightx = 4;
				c.weighty = 1.5;

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 0);
				parent.panelSecond.add(MSparam, c);
				++c.gridy;
				c.insets = new Insets(10, 10, 0, 0);
				parent.panelSecond.add(sigma1, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 0);
				parent.panelSecond.add(sigmaText1, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 0);
				parent.panelSecond.add(sigma2S, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 0);
				parent.panelSecond.add(sigmaText2, c);

				++c.gridy;
				c.insets = new Insets(10, 0, 0, 0);
				parent.panelSecond.add(thresholdS, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 0, 0);
				parent.panelSecond.add(thresholdText, c);

				
				++c.gridy;
				c.insets = new Insets(0, 180, 0, 180);
				parent.panelSecond.add(DisplayBlobs, c);

				++c.gridy;
				c.insets = new Insets(10, 180, 0, 180);
				parent.panelSecond.add(FindBeadsListener, c);
				
				/* Configuration */
				sigma1.addAdjustmentListener(
						new SigmaListener(sigmaText1, parent.sigmaMin, parent.sigmaMax, parent.scrollbarSize, sigma1, sigma2S, sigmaText2));
			
				thresholdS.addAdjustmentListener(new ThresholdListener(thresholdText, parent.thresholdMin, parent.thresholdMax));
			
				DisplayBlobs.addActionListener(new DisplayBlobsListener());
				FindBeadsListener.addActionListener(new FindBeadsListener(parent));
				parent.panelSecond.repaint();
				parent.panelSecond.validate();
				parent.Cardframe.pack();
			}

			if (parent.FindBeadsViaDOG != oldState) {
				while (parent.isComputing)
					SimpleMultiThreading.threadWait(10);

				parent.updatePreview(ValueChange.FindBeadsVia);
			}
		}
		protected class DisplayBlobsListener implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				parent.FindBeadsViaDOG = true;
				parent.updatePreview(ValueChange.SHOWDOG);

			}
		}
		
		protected class SigmaListener implements AdjustmentListener {
			final Label label;
			final float min, max;
			final int scrollbarSize;

			final Scrollbar sigmaScrollbar1;
			final Scrollbar sigmaScrollbar2;
			final Label sigmaText2;

			public SigmaListener(final Label label, final float min, final float max, final int scrollbarSize,
					final Scrollbar sigmaScrollbar1, final Scrollbar sigmaScrollbar2, final Label sigmaText2) {
				this.label = label;
				this.min = min;
				this.max = max;
				this.scrollbarSize = scrollbarSize;

				this.sigmaScrollbar1 = sigmaScrollbar1;
				this.sigmaScrollbar2 = sigmaScrollbar2;
				this.sigmaText2 = sigmaText2;
			}

			@Override
			public void adjustmentValueChanged(final AdjustmentEvent event) {
				parent.sigma = parent.computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize);

				
				parent.sigma2 = parent.computeSigma2(parent.sigma, parent.sensitivity);
					sigmaText2.setText("Sigma 2 = " + parent.sigma2);
					sigmaScrollbar2.setValue(parent.computeScrollbarPositionFromValue(parent.sigma2, min, max, scrollbarSize));
				

				label.setText("Sigma 1 = " + parent.sigma);

				// if ( !event.getValueIsAdjusting() )
				{
					while (parent.isComputing) {
						SimpleMultiThreading.threadWait(10);
					}
					parent.updatePreview(ValueChange.SIGMA);
				}
			}
		}
		
		protected class ThresholdListener implements AdjustmentListener {
			final Label label;
			final float min, max;

			public ThresholdListener(final Label label, final float min, final float max) {
				this.label = label;
				this.min = min;
				this.max = max;
			}

			@Override
			public void adjustmentValueChanged(final AdjustmentEvent event) {
				parent.threshold = parent.computeValueFromScrollbarPosition(event.getValue(), min, max, parent.scrollbarSize);
				label.setText("Threshold = " + parent.threshold);

				if (!parent.isComputing) {
					parent.updatePreview(ValueChange.THRESHOLD);
				} else if (!event.getValueIsAdjusting()) {
					while (parent.isComputing) {
						SimpleMultiThreading.threadWait(10);
					}
					parent.updatePreview(ValueChange.THRESHOLD);
				}
			}
		}
		

	}
