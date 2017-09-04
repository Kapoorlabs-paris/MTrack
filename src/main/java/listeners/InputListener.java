package listeners;

import java.awt.TextComponent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import interactiveMT.MainFileChooser;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;

public class InputListener implements TextListener {

	
	final FireTrigger parent;
	final MainFileChooser mainparent;
	
	public InputListener(final FireTrigger parent, final MainFileChooser mainparent){
		
		this.parent = parent;
		this.mainparent = mainparent;
		
	}
	
	@Override
	public void textValueChanged(TextEvent e) {
		final TextComponent tc = (TextComponent)e.getSource();
	  
		parent.LoadtrackText.setText("Image read: " + mainparent.chooserB.getSelectedFile());
		
	}

}
