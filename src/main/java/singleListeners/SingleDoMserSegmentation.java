package singleListeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import interactiveMT.Interactive_MTSingleChannel;
import updateListeners.UpdateMserListener;

public class SingleDoMserSegmentation implements ItemListener {

final Interactive_MTSingleChannel parent;
	
	
	public SingleDoMserSegmentation(final Interactive_MTSingleChannel parent){
	
		this.parent = parent;
	}
	
	@Override
	public void itemStateChanged(ItemEvent arg0) {
		if (arg0.getStateChange() == ItemEvent.DESELECTED){
			parent.FindLinesViaMSER = false;
		}
		else if (arg0.getStateChange() == ItemEvent.SELECTED) {
			parent.FindLinesViaMSER = true;
			parent.doMserSegmentation = true;
			
			
			parent.UpdateMser();

		}

	}

}
