/*-
 * #%L
 * Microtubule tracker.
 * %%
 * Copyright (C) 2017 MTrack developers.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
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
