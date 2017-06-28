package singleListeners;

import java.awt.TextComponent;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTSingleChannel;

public class SingleEndTrackListener implements TextListener {
	
	final Interactive_MTSingleChannel parent;
	
	public SingleEndTrackListener(final Interactive_MTSingleChannel parent){
		
		this.parent = parent;
	}
	

	@Override
	public void textValueChanged(TextEvent e) {
	
		 TextComponent tc = (TextComponent)e.getSource();
		    String s = tc.getText();
		   
		    if (s.length() > 0) { 
		parent.thirdDimensionSize = (int)Float.parseFloat(s);

		    }
		
	}

}
