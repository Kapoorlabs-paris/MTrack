package listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;
import interactiveMT.Interactive_MTDoubleChannelBasic;

/**
 * Updates when mouse is dragged
 * 
 * @author vkapoor 
 *
 */
public class NonStandardMouseListener implements MouseMotionListener
{
	final Interactive_MTDoubleChannel parent;
	final ValueChange change;

	public NonStandardMouseListener( final Interactive_MTDoubleChannel parent, final ValueChange change )
	{
		this.parent = parent;
		this.change = change;
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
  
		

		parent.updatePreview(change);
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	

}
