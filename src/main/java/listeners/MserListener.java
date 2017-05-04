package listeners;

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

import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;
import mpicbg.imglib.multithreading.SimpleMultiThreading;



	
	public class MserListener implements ItemListener {
		
		
		final Interactive_MTDoubleChannel parent;
		
		
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

				final GridBagLayout layout = new GridBagLayout();
				final GridBagConstraints c = new GridBagConstraints();
				final Label Step = new Label("Step 2", Label.CENTER);

				parent.panelSecond.setLayout(layout);

				parent.panelSecond.add(Step, c);
				final Scrollbar deltaS = new Scrollbar(Scrollbar.HORIZONTAL, parent.deltaInit, 10, 0, 10 + parent.scrollbarSize);
				final Scrollbar maxVarS = new Scrollbar(Scrollbar.HORIZONTAL, parent.maxVarInit, 10, 0, 10 + parent.scrollbarSize);
				final Scrollbar minDiversityS = new Scrollbar(Scrollbar.HORIZONTAL, parent.minDiversityInit, 10, 0,
						10 + parent.scrollbarSize);
				final Scrollbar minSizeS = new Scrollbar(Scrollbar.HORIZONTAL, parent.minSizeInit, 10, 0, 10 + parent.scrollbarSize);
				final Scrollbar maxSizeS = new Scrollbar(Scrollbar.HORIZONTAL, parent.maxSizeInit, 10, 0, 10 + parent.scrollbarSize);
				final Button ComputeTree = new Button("Compute Tree and display");
				final Button FindLinesListener = new Button("Find endpoints");
				parent.maxVar = parent.computeValueFromScrollbarPosition(parent.maxVarInit, parent.maxVarMin, parent.maxVarMax, 
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

				final Label deltaText = new Label("delta = " + parent.delta, Label.CENTER);
				final Label maxVarText = new Label("maxVar = " + parent.maxVar, Label.CENTER);
				final Label minDiversityText = new Label("minDiversity = " +parent.minDiversity, Label.CENTER);
				final Label minSizeText = new Label("MinSize = " + parent.minSize, Label.CENTER);
				final Label maxSizeText = new Label("MaxSize = " + parent.maxSize, Label.CENTER);

				final Checkbox min = new Checkbox("Look for Minima ", parent.darktobright);

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

				parent.panelSecond.add(maxVarText, c);

				++c.gridy;
				parent.panelSecond.add(maxVarS, c);

				++c.gridy;

				parent.panelSecond.add(minDiversityText, c);

				++c.gridy;
				parent.panelSecond.add(minDiversityS, c);

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
				parent.panelSecond.add(min, c);

				++c.gridy;
				c.insets = new Insets(10, 175, 0, 175);
				parent.panelSecond.add(ComputeTree, c);

				++c.gridy;
				c.insets = new Insets(10, 180, 0, 180);
				parent.panelSecond.add(FindLinesListener, c);

				deltaS.addAdjustmentListener(new DeltaListener(parent, deltaText, parent.deltaMin, parent.deltaMax, 
						parent.scrollbarSize, deltaS));

				maxVarS.addAdjustmentListener(
						new MaxVarListener(parent, maxVarText, parent.maxVarMin, parent.maxVarMax, 
								parent.scrollbarSize, maxVarS));

				minDiversityS.addAdjustmentListener(new MinDiversityListener(parent, minDiversityText, parent.minDiversityMin,
						parent.minDiversityMax, parent.scrollbarSize, minDiversityS));

				minSizeS.addAdjustmentListener(
						new MinSizeListener(parent, minSizeText,parent.minSizemin, parent.minSizemax,
                      parent.scrollbarSize, minSizeS));

				maxSizeS.addAdjustmentListener(
						new MaxSizeListener(parent,maxSizeText,parent. maxSizemin, parent.maxSizemax, 
								parent.scrollbarSize, maxSizeS));

				min.addItemListener(new DarktobrightListener(parent));
				ComputeTree.addActionListener(new ComputeTreeListener(parent));
				FindLinesListener.addActionListener(new FindLinesListener(parent));
				parent.panelSecond.validate();
				parent.panelSecond.repaint();
				parent.Cardframe.pack();

			}

			if (parent.FindLinesViaMSER != oldState) {
				while (parent.isComputing)
					SimpleMultiThreading.threadWait(10);

				parent.updatePreview(ValueChange.FindLinesVia);
			}
		}
	}
	

