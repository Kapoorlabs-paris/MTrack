package beadListener;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.TextField;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import interactiveMT.BeadFileChooser.whichModel;
import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_PSFAnalyze;
import interactiveMT.Interactive_PSFAnalyze.ValueChange;
import mpicbg.imglib.multithreading.SimpleMultiThreading;
import updateListeners.DefaultModel;
import updateListeners.DefaultModelHF;



	
	public class MserListener implements ItemListener {
		
		
		Interactive_PSFAnalyze parent;
		
		public MserListener(final Interactive_PSFAnalyze parent){
		
			this.parent = parent;
		}
	
		
		@Override
		public void itemStateChanged(final ItemEvent arg0) {
			boolean oldState = parent.FindBeadsViaMSER;

			if (arg0.getStateChange() == ItemEvent.DESELECTED)
				parent.FindBeadsViaMSER = false;
			else if (arg0.getStateChange() == ItemEvent.SELECTED) {

				parent.FindBeadsViaMSER = true;
				parent.FindBeadsViaDOG = false;

				parent.panelSecond.removeAll();

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
				final Button FindBeadsListener = new Button("Fit Gaussian bead Function");
				final Button FindPolynomialListener = new Button("Fit Gaussian Polynomial Function");
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

				parent.inputLabelX = new JLabel("Enter a guess for sigmaX of Microscope PSF (pixel units): ");
				parent.inputFieldX = new TextField();
				parent.inputFieldX.setColumns(10);

				parent.inputLabelY = new JLabel("Enter a guess for sigmaY of Microscope PSF (pixel units): ");
				parent.inputFieldY = new TextField();
				parent.inputFieldY.setColumns(10);

				final Label MSparam = new Label("Determine MSER parameters");
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
				parent.panelSecond.add(ComputeTree, c);

				if(parent.Usermodel == whichModel.Bead){
					
					
					
					
				++c.gridy;
				c.insets = new Insets(10, 180, 0, 180);
				parent.panelSecond.add(FindBeadsListener, c);
				}
				
				else{
					
					
					++c.gridy;
					c.insets = new Insets(10, 10, 10, 0);
					parent.panelSecond.add(parent.inputLabelX, c);

					++c.gridy;
					c.insets = new Insets(10, 10, 0, 180);
					parent.panelSecond.add(parent.inputFieldX, c);

					++c.gridy;
					c.insets = new Insets(10, 10, 10, 0);
					parent.panelSecond.add(parent.inputLabelY, c);

					++c.gridy;
					c.insets = new Insets(10, 10, 0, 180);
					parent.panelSecond.add(parent.inputFieldY, c);	
					
					
				
				++c.gridy;
				c.insets = new Insets(10, 180, 0, 180);
				parent.panelSecond.add(FindPolynomialListener, c);	
				
				
				
				
				}
				
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

				ComputeTree.addActionListener(new ComputeTreeListener(parent));
				FindBeadsListener.addActionListener(new FindBeadsListener(parent));
				FindPolynomialListener.addActionListener(new FindPolynomialListener(parent));
				
				parent.panelSecond.validate();
				parent.panelSecond.repaint();
				parent.Cardframe.pack();

			}

			if (parent.FindBeadsViaMSER != oldState) {
				while (parent.isComputing)
					SimpleMultiThreading.threadWait(10);

				parent.updatePreview(ValueChange.FindBeadsVia);
			}
		}
	}
	

