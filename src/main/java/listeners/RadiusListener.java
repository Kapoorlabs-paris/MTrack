package listeners;

import java.awt.TextComponent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import ij.IJ;
import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;

public class RadiusListener implements TextListener  {
	
	
final Interactive_MTDoubleChannel parent;
	
	public RadiusListener(final Interactive_MTDoubleChannel parent){
		
		this.parent = parent;
		
	}
	
	
	@Override
	public void textValueChanged(TextEvent e) {
	
		final TextComponent tc = (TextComponent)e.getSource();
		    String s = tc.getText();
		 
				    
							if (s.length() > 0)
				    		parent.radiusfactor = (int)Float.parseFloat(s);
				    	
				   				
				   		
		    
		    
		    
		   
		   
		 
		    	
		   
	}

}
