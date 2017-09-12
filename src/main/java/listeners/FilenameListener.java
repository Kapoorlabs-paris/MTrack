package listeners;

import java.awt.TextComponent;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import interactiveMT.MainFileChooser;

public class FilenameListener implements TextListener {

	
	final MainFileChooser parent;
	
	public FilenameListener(final MainFileChooser parent){
		
		this.parent = parent;
		
	}
	
	@Override
	public void textValueChanged(TextEvent e) {
		final TextComponent tc = (TextComponent)e.getSource();
	    String s = tc.getText();
	   
	    if (s.length() > 0)
		parent.addToName = s;
		
	}


}
