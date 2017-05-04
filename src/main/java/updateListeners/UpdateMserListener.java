package updateListeners;

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
import listeners.AnalyzekymoListener;
import listeners.ComputeTreeListener;
import listeners.DarktobrightListener;
import listeners.DeltaListener;
import listeners.MaxSizeListener;
import listeners.MaxVarListener;
import listeners.MinDiversityListener;
import listeners.MinSizeListener;

public class UpdateMserListener implements ItemListener {
	
	
	final Interactive_MTDoubleChannel parent;
	
	
	public UpdateMserListener(final Interactive_MTDoubleChannel parent){
	
		this.parent = parent;
	}
	
	
	@Override
	public void itemStateChanged(final ItemEvent arg0) {

		if (arg0.getStateChange() == ItemEvent.DESELECTED)
			parent.FindLinesViaMSER = false;
		else if (arg0.getStateChange() == ItemEvent.SELECTED) {
			parent.FindLinesViaMSER = true;
			UpdateMser();

		}

	}
	
	
	
	public void UpdateMser() {
		parent.FindLinesViaMSER = true;
		parent.FindLinesViaHOUGH = false;
		parent.FindLinesViaMSERwHOUGH = false;
		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints c = new GridBagConstraints();
		parent.panelFourth.removeAll();
		final Label Step = new Label("Step 4", Label.CENTER);
		parent.panelFourth.setLayout(layout);
		parent.panelFourth.add(Step, c);
		final Scrollbar deltaS = new Scrollbar(Scrollbar.HORIZONTAL, parent.deltaInit, 10, 0, 10 + parent.scrollbarSize);
		final Scrollbar maxVarS = new Scrollbar(Scrollbar.HORIZONTAL, parent.maxVarInit, 10, 0, 10 + parent.scrollbarSize);
		final Scrollbar minDiversityS = new Scrollbar(Scrollbar.HORIZONTAL, parent.minDiversityInit, 10, 0,
				10 + parent.scrollbarSize);
		final Scrollbar minSizeS = new Scrollbar(Scrollbar.HORIZONTAL, parent.minSizeInit, 10, 0, 10 + parent.scrollbarSize);
		final Scrollbar maxSizeS = new Scrollbar(Scrollbar.HORIZONTAL, parent.maxSizeInit, 10, 0, 10 + parent.scrollbarSize);

		final Label deltaText = new Label("delta = " + parent.delta, Label.CENTER);
		final Label maxVarText = new Label("maxVar = " + parent.maxVar, Label.CENTER);
		final Label minDiversityText = new Label("minDiversity = " + parent.minDiversity, Label.CENTER);
		final Label minSizeText = new Label("MinSize = " + parent.minSize, Label.CENTER);
		final Label maxSizeText = new Label("MaxSize = " + parent.maxSize, Label.CENTER);

		final Checkbox min = new Checkbox("Look for Minima ", parent.darktobright);

		final Button ComputeTree = new Button("Compute Tree and display");
		/* Location */

		final Label Update = new Label("Update parameters for dynamic channel");
		Update.setBackground(new Color(1, 0, 1));
		Update.setForeground(new Color(255, 255, 255));
		parent.panelFourth.setLayout(layout);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 4;
		c.weighty = 1.5;
		++c.gridy;
		parent.panelFourth.add(Update, c);

		++c.gridy;
		parent.panelFourth.add(deltaText, c);

		++c.gridy;
		parent.panelFourth.add(deltaS, c);

		++c.gridy;

		parent.panelFourth.add(maxVarText, c);

		++c.gridy;
		parent.panelFourth.add(maxVarS, c);

		++c.gridy;

		parent.panelFourth.add(minDiversityText, c);

		++c.gridy;
		parent.panelFourth.add(minDiversityS, c);

		++c.gridy;

		parent.panelFourth.add(minSizeText, c);

		++c.gridy;
		parent.panelFourth.add(minSizeS, c);

		++c.gridy;

		parent.panelFourth.add(maxSizeText, c);

		++c.gridy;
		parent.panelFourth.add(maxSizeS, c);

		++c.gridy;
		c.insets = new Insets(10, 175, 0, 175);
		parent.panelFourth.add(min, c);

		++c.gridy;
		c.insets = new Insets(10, 175, 0, 175);
		parent.panelFourth.add(ComputeTree, c);

		deltaS.addAdjustmentListener(new DeltaListener(parent, deltaText, parent.deltaMin, parent.deltaMax, parent.scrollbarSize, deltaS));

		maxVarS.addAdjustmentListener(new MaxVarListener(parent, maxVarText, parent.maxVarMin, parent.maxVarMax, parent.scrollbarSize, maxVarS));

		minDiversityS.addAdjustmentListener(new MinDiversityListener(parent, minDiversityText, parent.minDiversityMin, parent.minDiversityMax,
				parent.scrollbarSize, minDiversityS));

		minSizeS.addAdjustmentListener(
				new MinSizeListener(parent, minSizeText, parent.minSizemin, parent.minSizemax, parent.scrollbarSize, minSizeS));

		maxSizeS.addAdjustmentListener(
				new MaxSizeListener(parent, maxSizeText, parent.maxSizemin, parent.maxSizemax, parent.scrollbarSize, maxSizeS));

		min.addItemListener(new DarktobrightListener(parent));
		ComputeTree.addActionListener(new ComputeTreeListener(parent));

		if (parent.analyzekymo && parent.Kymoimg != null) {

			AnalyzekymoListener newkymo = new AnalyzekymoListener(parent);
			
			newkymo.Kymo();
		}

		else{
			
			
			DeterchoiceListener newdeter = new DeterchoiceListener(parent);
			newdeter.Deterministic();
		}

			

		parent.panelFourth.validate();
		parent.panelFourth.repaint();
		parent.Cardframe.pack();
	}

}