package singleListeners;

import java.awt.TextComponent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import ij.IJ;
import interactiveMT.Interactive_MTSingleChannel;


public class SingleRadiusListener implements TextListener  {
	
	
final Interactive_MTSingleChannel parent;
	
	public SingleRadiusListener(final Interactive_MTSingleChannel parent){
		
		this.parent = parent;
		
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
				    	
				   				
				   		  
				    		
						 }

				    }
				});
		    
		    
		    
		   
		   
		 
		    	
		   
	}

}
