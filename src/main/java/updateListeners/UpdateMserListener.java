package updateListeners;


import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


import interactiveMT.Interactive_MTDoubleChannel;


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