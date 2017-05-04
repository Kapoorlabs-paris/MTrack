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
import listeners.AcceptResultsListener;
import listeners.CheckResultsListener;
import listeners.MaxSearchradiusListener;
import listeners.MissedFrameListener;
import listeners.SearchradiusListener;
import listeners.SkipFramesandTrackendsListener;
import listeners.TrackendsListener;
import trackerType.KFsearch;

public class KalmanchoiceListener implements ItemListener {

	
final Interactive_MTDoubleChannel parent;
	
	
	public KalmanchoiceListener(final Interactive_MTDoubleChannel parent){
	
		this.parent = parent;
	}
	
	
	
	@Override
	public void itemStateChanged(ItemEvent arg0) {
		if (arg0.getStateChange() == ItemEvent.DESELECTED) {
			parent.showKalman = false;

		} else if (arg0.getStateChange() == ItemEvent.SELECTED) {

			parent.showKalman = true;
			parent.Kalman();
		}

	}
	
	


}
