package listeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import interactiveMT.Interactive_MTDoubleChannel;

public class ShowautoListener implements ItemListener {
	
	
final Interactive_MTDoubleChannel parent;
	
	public ShowautoListener(final Interactive_MTDoubleChannel parent ){
		
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
