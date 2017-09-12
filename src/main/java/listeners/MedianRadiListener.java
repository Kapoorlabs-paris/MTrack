package listeners;

import java.awt.TextComponent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import interactiveMT.MainFileChooser;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;

public class MedianRadiListener implements TextListener {

	
	final MainFileChooser parent;
	
	public MedianRadiListener(final MainFileChooser parent){
		
		this.parent = parent;
		
	}
	
	@Override
	public void textValueChanged(TextEvent e) {
		final TextComponent tc = (TextComponent)e.getSource();
	    String s = tc.getText();
	   
	
	    if (s.length() > 0)
		parent.medianradius =  (int) Math.round(Float.parseFloat(s));
		
	}

}
