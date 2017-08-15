package listeners;

import java.awt.TextComponent;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import interactiveMT.Interactive_MTDoubleChannel;

public class StartTrackListener implements TextListener {
	
	final Interactive_MTDoubleChannel parent;
	
	public StartTrackListener(final Interactive_MTDoubleChannel parent){
		
		this.parent = parent;
	}
	

	@Override
	public void textValueChanged(TextEvent e) {
	
		 TextComponent tc = (TextComponent)e.getSource();
		    String s = tc.getText();
		   
		    if (s.length() > 0) { 
		parent.starttime = (int)Float.parseFloat(s);

		    }
		
	}

}
