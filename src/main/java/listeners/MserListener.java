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

import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;
import interactiveMT.Interactive_PSFAnalyze;
import mpicbg.imglib.multithreading.SimpleMultiThreading;
import updateListeners.DefaultModel;
import updateListeners.DefaultModelHF;



	
	public class MserListener implements ItemListener {
		
		
		Interactive_MTDoubleChannel parent;
		
		public MserListener(final Interactive_MTDoubleChannel parent){
		
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
				
				
				

				parent.panelSecond.setLayout(parent.layout);
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
				final Label minSizeText = new Label("Min size = " + parent.minSize, Label.CENTER);
				final Label maxSizeText = new Label("Max size = " + parent.maxSize, Label.CENTER);

				

				parent.controlnext.setEnabled(false);
				final Label MSparam = new Label("Determine MSER parameters");
				MSparam.setBackground(new Color(1, 0, 1));
				MSparam.setForeground(new Color(255, 255, 255));

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

				
				 parent.Mserparam.add(deltaText,  new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
							GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
				 
				 parent.Mserparam.add(deltaS,  new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
							GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

				 parent.Mserparam.add(Unstability_ScoreText,  new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
							GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

				 parent.Mserparam.add(Unstability_ScoreS,  new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
							GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
				   
				 parent.Mserparam.add(minSizeText,  new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
							GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

				 parent.Mserparam.add(minSizeS,  new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
							GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
				 parent.Mserparam.add(maxSizeText,  new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
							GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
				 parent.Mserparam.add(maxSizeS,  new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
							GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
				 
				 parent.Mserparam.add(AdvancedOptions, new GridBagConstraints(0,7, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
							GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
				 
				 parent.Mserparam.add(FindLinesListener, new GridBagConstraints(0,8, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
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

				
		}
	}
	

