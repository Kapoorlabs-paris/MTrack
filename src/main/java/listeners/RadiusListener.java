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
		 
			 if(parent.FindLinesViaMSER)
				    parent.updatePreview(ValueChange.SHOWMSER);
				    if(parent.FindLinesViaHOUGH){
				    	parent.updatePreview(ValueChange.SHOWHOUGH);
				    	parent.updatePreview(ValueChange.SHOWMSERinHough );	
				    }
	}
	
	
	@Override
	public void textValueChanged(TextEvent e) {
	
		final TextComponent tc = (TextComponent)e.getSource();
		    String s = tc.getText();
		   
			 tc.addKeyListener(new KeyListener(){
				 @Override
				    public void keyTyped(KeyEvent arg0) {
					   
				    }

				    @Override
				    public void keyReleased(KeyEvent arg0) {
				    	

				    }

				    @Override
				    public void keyPressed(KeyEvent arg0) {
				    	String s = tc.getText();
				    	if (arg0.getKeyChar() == KeyEvent.VK_ENTER)
						 {
							
				    		parent.radiusfactor = (int)Float.parseFloat(s);
						 
				   				parent.radiusfactor = Double.parseDouble(parent.inputFieldradi.getText());
				   		    if(parent.FindLinesViaMSER)
				   		    parent.updatePreview(ValueChange.SHOWMSER);
				   		    if(parent.FindLinesViaHOUGH){
				   		    	parent.updatePreview(ValueChange.SHOWHOUGH);
				   		    	parent.updatePreview(ValueChange.SHOWMSERinHough );	
				   		    }
				    		
						 }

				    }
				});
		    
		    
		    
		   
		   
		 
		    	
		   
	}

}
