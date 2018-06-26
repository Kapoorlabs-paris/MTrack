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


public class ErrorLocListener implements TextListener {
	
	
	final InteractiveRANSAC parent;
	boolean pressed;
	public ErrorLocListener(final InteractiveRANSAC parent, final boolean pressed) {
		
		this.parent = parent;
		this.pressed = pressed;
		
	}
	
	@Override
	public void textValueChanged(TextEvent e) {
		final TextComponent tc = (TextComponent)e.getSource();
	 	String s = tc.getText();
	 	if(s.length() > 0)
		parent.maxError = Float.parseFloat(s);
		parent.maxErrorLabel.setText("Maximum Error (px) = " + parent.nf.format((parent.maxError)) + "      ");
		parent.MAX_ERROR = Math.max(parent.maxError, parent.MAX_ERROR);
		if(parent.MAX_ERROR > 1.0E5)
			parent.MAX_ERROR = 100;
		parent.maxErrorSB.setValue(utility.Slicer.computeScrollbarPositionFromValue((float)parent.maxError, parent.MIN_ERROR, parent.MAX_ERROR, parent.scrollbarSize));
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
					parent.maxErrorSB.repaint();
					parent.maxErrorSB.validate();
					
					
					
			    		
					 }

			    }
			});
	

	

}

}
