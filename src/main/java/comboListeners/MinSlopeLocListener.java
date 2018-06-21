package comboListeners;

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


public class MinSlopeLocListener implements TextListener {
	
	
	final InteractiveRANSAC parent;
	boolean pressed;
	public MinSlopeLocListener(final InteractiveRANSAC parent, final boolean pressed) {
		
		this.parent = parent;
		this.pressed = pressed;
		
	}
	
	@Override
	public void textValueChanged(TextEvent e) {
		final TextComponent tc = (TextComponent)e.getSource();
	 	String s = tc.getText();
	 	if(s.length() > 0)
		parent.minSlope = Float.parseFloat(s);
		parent.minSlopeLabel.setText(parent.minslopestring + " = " + parent.nf.format((parent.minSlope)) + "      ");
		parent.MIN_ABS_SLOPE = Math.max(parent.minSlope, parent.MIN_ABS_SLOPE);
		if(parent.MIN_ABS_SLOPE  > 1.0E5)
			parent.MIN_ABS_SLOPE  = 100;
		parent.minSlopeSB.setValue(utility.Slicer.computeScrollbarPositionFromValue((float)parent.minSlope, 
				(float)parent.MIN_ABS_SLOPE, (float)parent.MAX_ABS_SLOPE, parent.scrollbarSize));
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
					parent.minSlopeSB.repaint();
					parent.minSlopeSB.validate();
					
					
					
			    		
					 }

			    }
			});
	

	

}

}
