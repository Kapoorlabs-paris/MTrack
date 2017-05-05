package listeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import interactiveMT.Interactive_MTDoubleChannel;
import updateListeners.UpdateHoughListener;

public class DoSegmentation implements ItemListener {

	
final Interactive_MTDoubleChannel parent;
	
	
	public DoSegmentation(final Interactive_MTDoubleChannel parent){
	
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
