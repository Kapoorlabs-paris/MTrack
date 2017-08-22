package listeners;

import java.awt.TextComponent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import interactiveMT.MainFileChooser;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;

public class CalTListener implements TextListener {

	
	final MainFileChooser parent;
	
	public CalTListener(final MainFileChooser parent){
		
		this.parent = parent;
		
	}
	
	@Override
	public void textValueChanged(TextEvent e) {
		final TextComponent tc = (TextComponent)e.getSource();
	    String s = tc.getText();
	   
	
		parent.calibration[2] = Float.parseFloat(s);
		
	}

}
