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
			parent.UpdateHough();
			

		}

	}
	
	


	



}
