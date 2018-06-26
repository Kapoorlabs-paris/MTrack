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


public class MaxSlopeLocListener implements TextListener {
	
	
	final InteractiveRANSAC parent;
	boolean pressed;
	public MaxSlopeLocListener(final InteractiveRANSAC parent, final boolean pressed) {
		
		this.parent = parent;
		this.pressed = pressed;
		
	}
	
	@Override
	public void textValueChanged(TextEvent e) {
		final TextComponent tc = (TextComponent)e.getSource();
	 	String s = tc.getText();
	 	
	 	if (s.contains("-")) {
			s.replace("-", "").trim();
		}

		int neg = 1;
		if (s.length() > 0 && s.charAt(0) == '-') {
			s = s.substring(1).trim();
			neg = -1;
		}

	 	if(s.length() > 0)
		parent.maxSlope = Float.parseFloat(s);
	 	
	 	parent.maxSlope = neg * parent.maxSlope;
		parent.maxSlopeLabel.setText(parent.maxslopestring + " = " + parent.df.format((parent.maxSlope)) );
		
		
		parent.maxSlopeSB.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.maxSlope, 
				parent.MIN_ABS_SLOPE, parent.MAX_ABS_SLOPE, parent.scrollbarSize));
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
					parent.maxSlopeSB.repaint();
					parent.maxSlopeSB.validate();
					
					
					
			    		
					 }

			    }
			});
	

	

}

}
