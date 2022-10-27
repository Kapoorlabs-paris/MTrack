/*-
 * #%L
 * Microtubule tracker.
 * %%
 * Copyright (C) 2017 - 2022 MTrack developers.
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
package comboListeners;

import java.awt.Dimension;
import java.awt.TextComponent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import ij.IJ;
import mt.listeners.InteractiveRANSAC;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.logic.BitType;


public class MinInlierLocListener implements TextListener {
	
	
	final InteractiveRANSAC parent;
	boolean pressed;
	public MinInlierLocListener(final InteractiveRANSAC parent, final boolean pressed) {
		
		this.parent = parent;
		this.pressed = pressed;
		
	}
	
	@Override
	public void textValueChanged(TextEvent e) {
		final TextComponent tc = (TextComponent)e.getSource();
	 	String s = tc.getText();
	 	if(s.length() > 0)
		parent.minInliers = (int) Float.parseFloat(s);
		parent.minInliersLabel.setText("Minimum No. of timepoints (tp) = " + parent.nf.format((parent.minInliers)) + "      ");
		parent.MAX_Inlier = Math.max(parent.minInliers, parent.MAX_Inlier);
		if(parent.MAX_Inlier > 1.0E5)
			parent.MAX_Inlier = 100;
		parent.minInliersSB.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.minInliers, parent.MIN_Inlier, parent.MAX_Inlier, parent.scrollbarSize));
		

		
		tc.addKeyListener(new KeyListener(){
			 @Override
			    public void keyTyped(KeyEvent arg0) {
				   
			    }

			    @Override
			    public void keyReleased(KeyEvent arg0) {
			    	
			    	if (arg0.getKeyChar() == KeyEvent.VK_ENTER ) {
						
						
						pressed = false;
						
					}

			    }

			    @Override
			    public void keyPressed(KeyEvent arg0) {
			   
			    	if (arg0.getKeyChar() == KeyEvent.VK_ENTER&& !pressed) {
						pressed = true;
			    		
						
			  
				
					parent.updateRANSAC();
					parent.minInliersSB.repaint();
					parent.minInliersSB.validate();
					
					
					
			    		
					 }

			    }
			});
	

	

}

}
