package singleListeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTSingleChannel;
import updateListeners.UpdateHoughListener;

public class SingleDoSegmentation implements ItemListener {

	
final Interactive_MTSingleChannel parent;
	
	
	public SingleDoSegmentation(final Interactive_MTSingleChannel parent){
	
		this.parent = parent;
	}
	
	@Override
	public void itemStateChanged(ItemEvent arg0) {
		if (arg0.getStateChange() == ItemEvent.DESELECTED){
			parent.FindLinesViaHOUGH = false;
		}

		else if (arg0.getStateChange() == ItemEvent.SELECTED) {
			parent.FindLinesViaHOUGH = true;
			parent.doSegmentation = true;
			
			
			parent.UpdateHough();

		}

	}

}
