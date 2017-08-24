package singleListeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import interactiveMT.Interactive_MTSingleChannel;
import interactiveMT.Interactive_MTSingleChannel.ValueChange;



/**
 * Updates when mouse is dragged
 * 
 * @author vkapoor 
 *
 */
public class SingleNonStandardMouseListener implements MouseMotionListener
{
	final Interactive_MTSingleChannel parent;
	final ValueChange change;

	public SingleNonStandardMouseListener( final Interactive_MTSingleChannel parent, final ValueChange change )
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
