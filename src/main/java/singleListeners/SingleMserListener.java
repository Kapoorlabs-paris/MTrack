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
import updateListeners.SingleDefaultModel;



	
	public class SingleMserListener implements ItemListener {
		
		
		Interactive_MTSingleChannel parent;
		
		public SingleMserListener(final Interactive_MTSingleChannel parent){
		
			this.parent = parent;
		}
		
		
		
		@Override
		public void itemStateChanged(final ItemEvent arg0) {
			boolean oldState = parent.FindLinesViaMSER;

			if (arg0.getStateChange() == ItemEvent.DESELECTED)
				parent.FindLinesViaMSER = false;
			else if (arg0.getStateChange() == ItemEvent.SELECTED) {

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
				final Label minSizeText = new Label("Min size = " + parent.minSize, Label.CENTER);
				final Label maxSizeText = new Label("Max size = " + parent.maxSize, Label.CENTER);

				

				final Label MSparam = new Label("Determine MSER parameters");
				MSparam.setBackground(new Color(1, 0, 1));
				MSparam.setForeground(new Color(255, 255, 255));

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

				deltaS.addAdjustmentListener(new SingleDeltaListener(parent, deltaText, parent.deltaMin, parent.deltaMax, 
						parent.scrollbarSize, deltaS));

				Unstability_ScoreS.addAdjustmentListener(
						new SingleUnstability_ScoreListener(parent, Unstability_ScoreText, parent.Unstability_ScoreMin, parent.Unstability_ScoreMax, 
								parent.scrollbarSize, Unstability_ScoreS));

				minDiversityS.addAdjustmentListener(new SingleMinDiversityListener(parent, minDiversityText, parent.minDiversityMin,
						parent.minDiversityMax, parent.scrollbarSize, minDiversityS));

				minSizeS.addAdjustmentListener(
						new SingleMinSizeListener(parent, minSizeText,parent.minSizemin, parent.minSizemax,
                      parent.scrollbarSize, minSizeS));

				maxSizeS.addAdjustmentListener(
						new SingleMaxSizeListener(parent,maxSizeText,parent. maxSizemin, parent.maxSizemax, 
								parent.scrollbarSize, maxSizeS));

				AdvancedOptions.addItemListener(new SingleAdvancedSeedListener(parent));
				ComputeTree.addActionListener(new SingleComputeTreeListener(parent));
				FindLinesListener.addActionListener(new SingleFindLinesListener(parent));
				parent.panelSecond.validate();
				parent.panelSecond.repaint();
				parent.Cardframe.pack();
				parent.updatePreview(ValueChange.SHOWMSER);
			}

				
		}
	}
	

