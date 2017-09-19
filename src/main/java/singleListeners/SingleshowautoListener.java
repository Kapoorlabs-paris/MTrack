package singleListeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import interactiveMT.Interactive_MTSingleChannel;

public class SingleshowautoListener implements ItemListener {
	
	
final Interactive_MTSingleChannel parent;
	
	public SingleshowautoListener(final Interactive_MTSingleChannel parent ){
		
		this.parent = parent;
	}
	
	
	
	@Override
	public void itemStateChanged(final ItemEvent arg0) {

		if (arg0.getStateChange() == ItemEvent.DESELECTED)
			parent.autothreshold = false;
		else if (arg0.getStateChange() == ItemEvent.SELECTED)
			parent.autothreshold = true;

	}
}
