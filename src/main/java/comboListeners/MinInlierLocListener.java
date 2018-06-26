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
