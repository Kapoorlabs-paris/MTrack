package listeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import interactiveMT.Interactive_MTDoubleChannel;

public class ShowwatershedimgListener implements ItemListener {
	
	final Interactive_MTDoubleChannel parent;
	
	public ShowwatershedimgListener(final Interactive_MTDoubleChannel parent ){
		
		this.parent = parent;
	}
	
	
	
	@Override
	public void itemStateChanged(final ItemEvent arg0) {

		if (arg0.getStateChange() == ItemEvent.DESELECTED)
			parent.displayWatershedimg = false;
		else if (arg0.getStateChange() == ItemEvent.SELECTED)
			parent.displayWatershedimg = true;

	}
}

