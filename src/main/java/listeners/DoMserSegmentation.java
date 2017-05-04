package listeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import interactiveMT.Interactive_MTDoubleChannel;
import updateListeners.UpdateMserListener;

public class DoMserSegmentation implements ItemListener {

final Interactive_MTDoubleChannel parent;
	
	
	public DoMserSegmentation(final Interactive_MTDoubleChannel parent){
	
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
			
			
			UpdateMserListener newmser = new UpdateMserListener(parent);
			newmser.UpdateMser();

		}

	}

}
