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

import javax.swing.JOptionPane;

import interactiveMT.Interactive_MTDoubleChannel;
import listeners.DowatershedListener;
import listeners.ShowBitimgListener;
import listeners.ShowwatershedimgListener;
import listeners.ThresholdHoughListener;

public class UpdateHoughListener implements ItemListener {
	
	
final Interactive_MTDoubleChannel parent;
	
	
	public UpdateHoughListener(final Interactive_MTDoubleChannel parent){
	
		this.parent = parent;
	}
	
	@Override
	public void itemStateChanged(final ItemEvent arg0) {
		boolean oldState = parent.FindLinesViaHOUGH;

		if (arg0.getStateChange() == ItemEvent.DESELECTED)
			parent.FindLinesViaHOUGH = false;
		else if (arg0.getStateChange() == ItemEvent.SELECTED) {
			parent.FindLinesViaMSER = false;
			parent.FindLinesViaHOUGH = true;
			parent.FindLinesViaMSERwHOUGH = false;
			UpdateHough();
			

		}

	}
	
	


	

	

	public void UpdateHough() {

		parent.FindLinesViaMSER = false;
		parent.FindLinesViaHOUGH = true;
		parent.FindLinesViaMSERwHOUGH = false;
		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints c = new GridBagConstraints();
		parent.panelFourth.removeAll();
		final Label Step = new Label("Step 4", Label.CENTER);
		parent.panelFourth.setLayout(layout);

		parent.panelFourth.add(Step, c);
		final Label exthresholdText = new Label("threshold = threshold to create Bitimg for watershedding.",
				Label.CENTER);

		final Label thresholdText = new Label("thresholdValue = " + parent.thresholdHough, Label.CENTER);

		final Scrollbar threshold = new Scrollbar(Scrollbar.HORIZONTAL, (int) parent.thresholdHoughInit, 10, 0,
				10 + parent.scrollbarSize);

		final Checkbox displayBit = new Checkbox("Display Bitimage ", parent.displayBitimg);
		final Checkbox displayWatershed = new Checkbox("Display Watershedimage ", parent.displayWatershedimg);

		final Button Dowatershed = new Button("Do watershedding");
		final Label Update = new Label("Update parameters for dynamic channel");
		Update.setBackground(new Color(1, 0, 1));
		Update.setForeground(new Color(255, 255, 255));
		/* Location */
		parent.panelFourth.setLayout(layout);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 4;
		c.weighty = 1.5;

		++c.gridy;
		parent.panelFourth.add(Update, c);

		++c.gridy;
		parent.panelFourth.add(exthresholdText, c);
		++c.gridy;

		parent.panelFourth.add(thresholdText, c);
		++c.gridy;

		parent.panelFourth.add(threshold, c);

		++c.gridy;
		c.insets = new Insets(10, 175, 0, 175);
		parent.panelFourth.add(displayBit, c);

		++c.gridy;
		c.insets = new Insets(10, 175, 0, 175);
		parent.panelFourth.add(displayWatershed, c);
		++c.gridy;
		c.insets = new Insets(10, 175, 0, 175);
		parent.panelFourth.add(Dowatershed, c);

		threshold.addAdjustmentListener(new ThresholdHoughListener(parent, thresholdText, parent.thresholdHoughMin, parent.thresholdHoughMax,
				parent.scrollbarSize, threshold));

		displayBit.addItemListener(new ShowBitimgListener(parent));
		displayWatershed.addItemListener(new ShowwatershedimgListener(parent));
		Dowatershed.addActionListener(new DowatershedListener(parent));
		parent.displayBitimg = false;
		parent.displayWatershedimg = false;
		
		
		
		parent.panelFourth.repaint();
		parent.panelFourth.validate();
		parent.Cardframe.pack();

	}


}
