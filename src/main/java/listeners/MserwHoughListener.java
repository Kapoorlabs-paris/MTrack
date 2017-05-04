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

import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;
import mpicbg.imglib.multithreading.SimpleMultiThreading;

public class MserwHoughListener implements ItemListener {
	
	
	final Interactive_MTDoubleChannel parent;
	
	
	public MserwHoughListener(final Interactive_MTDoubleChannel parent){
	
		this.parent = parent;
	}
	
	
	
	@Override
	public void itemStateChanged(final ItemEvent arg0) {
		boolean oldState = parent.FindLinesViaMSERwHOUGH;

		if (arg0.getStateChange() == ItemEvent.DESELECTED)
			parent.FindLinesViaMSERwHOUGH = false;
		else if (arg0.getStateChange() == ItemEvent.SELECTED) {

			parent.FindLinesViaMSERwHOUGH = true;
			parent.FindLinesViaMSER = false;
			parent.FindLinesViaHOUGH = false;
			// DisplayMSERwHough();

			final GridBagLayout layout = new GridBagLayout();
			final GridBagConstraints c = new GridBagConstraints();
			parent.panelSecond.removeAll();
			final Label Step = new Label("Step 1", Label.CENTER);

			parent.panelSecond.setLayout(layout);
			parent.panelSecond.add(Step, c);

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
			final Scrollbar maxVarS = new Scrollbar(Scrollbar.HORIZONTAL, parent.maxVarInit, 10, 0, 10 + parent.scrollbarSize);
			final Scrollbar minDiversityS = new Scrollbar(Scrollbar.HORIZONTAL, parent.minDiversityInit, 10, 0,
					10 + parent.scrollbarSize);
			final Scrollbar minSizeS = new Scrollbar(Scrollbar.HORIZONTAL, parent.minSizeInit, 10, 0, 10 + parent.scrollbarSize);
			final Scrollbar maxSizeS = new Scrollbar(Scrollbar.HORIZONTAL, parent.maxSizeInit, 10, 0, 10 + parent.scrollbarSize);
			final Button ComputeTree = new Button("Compute Tree and display");

			parent.maxVar = parent.computeValueFromScrollbarPosition(parent.maxVarInit, parent.maxVarMin, parent.maxVarMax, parent.scrollbarSize);
			parent.delta = parent.computeValueFromScrollbarPosition(parent.deltaInit, parent.deltaMin, parent.deltaMax, parent.scrollbarSize);
			parent.minDiversity = parent.computeValueFromScrollbarPosition(parent.minDiversityInit, parent.minDiversityMin,parent.minDiversityMax,
					parent.scrollbarSize);
			parent.minSize = (int) parent.computeValueFromScrollbarPosition(parent.minSizeInit, parent.minSizemin, parent.minSizemax, parent.scrollbarSize);
			parent.maxSize = (int) parent.computeValueFromScrollbarPosition(parent.maxSizeInit, parent.maxSizemin, parent.maxSizemax, parent.scrollbarSize);

			final Checkbox min = new Checkbox("Look for Minima ", parent.darktobright);

			final Label deltaText = new Label("delta = " + parent.delta, Label.CENTER);
			final Label maxVarText = new Label("maxVar = " + parent.maxVar, Label.CENTER);
			final Label minDiversityText = new Label("minDiversity = " + parent.minDiversity, Label.CENTER);
			final Label minSizeText = new Label("MinSize = " + parent.minSize, Label.CENTER);
			final Label maxSizeText = new Label("MaxSize = " + parent.maxSize, Label.CENTER);
			/* Location */

			parent.panelSecond.setLayout(layout);

			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 4;
			c.weighty = 1.5;

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
			parent.panelSecond.add(FindLinesListener, c);

			deltaS.addAdjustmentListener(new DeltaListener(parent, deltaText, parent.deltaMin, parent.deltaMax, parent.scrollbarSize, deltaS));

			maxVarS.addAdjustmentListener(
					new MaxVarListener(parent, maxVarText, parent.maxVarMin, parent.maxVarMax, parent.scrollbarSize, maxVarS));

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

			ComputeTree.addActionListener(new ComputeTreeListener(parent));
			parent.panelSecond.validate();
			parent.panelSecond.repaint();
			parent.Cardframe.pack();
		}

		if (parent.FindLinesViaMSERwHOUGH != oldState) {
			while (parent.isComputing)
				SimpleMultiThreading.threadWait(10);

			parent.updatePreview(ValueChange.FindLinesVia);
		}
	}
}

