package updateListeners;

import java.awt.Button;
import java.awt.CardLayout;
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
			parent.UpdateMser();
			
		}

	}
	
	
	
	
}