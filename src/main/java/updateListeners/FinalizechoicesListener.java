package updateListeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import interactiveMT.Interactive_MTDoubleChannel;

public class FinalizechoicesListener implements ItemListener {
	
	
	final Interactive_MTDoubleChannel parent;
	
public FinalizechoicesListener(final Interactive_MTDoubleChannel parent ){
		
		this.parent = parent;
		
	}

@Override
public void itemStateChanged(ItemEvent arg0) {
	 if (arg0.getStateChange() == ItemEvent.SELECTED) {
		

		


			parent.Deterministic();
		

}

}



}
