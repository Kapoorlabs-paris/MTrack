package singleListeners;

import java.awt.TextComponent;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTSingleChannel;

public class SingleBeginTrackListener implements TextListener {
	
	final Interactive_MTSingleChannel parent;
	
	public SingleBeginTrackListener(final Interactive_MTSingleChannel parent){
		
		this.parent = parent;
	}
	

	@Override
	public void textValueChanged(TextEvent e) {
		 TextComponent tc = (TextComponent)e.getSource();
		 
		 
		    String s = tc.getText();
		 if (s.length() > 0)  {
		parent.thirdDimension = (int)Float.parseFloat(s);
		 }
	}

}
