package updateListeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import interactiveMT.Interactive_MTSingleChannel;
import listeners.AnalyzekymoListener;

public class SingleFinalizechoicesListener implements ItemListener {
	
	
	final Interactive_MTSingleChannel parent;
	
public SingleFinalizechoicesListener(final Interactive_MTSingleChannel parent ){
		
		this.parent = parent;
		
	}

@Override
public void itemStateChanged(ItemEvent arg0) {
	 if (arg0.getStateChange() == ItemEvent.SELECTED) {
		



			parent.Deterministic();
		

}

}



}
