package listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;
import interactiveMT.Interactive_MTDoubleChannelBasic;

/**
 * Updates when mouse is released
 * 
 * @author spreibi
 *
 */
public class StandardsecMouseListener implements MouseListener
{
	final Interactive_MTDoubleChannel parent;
	final ValueChange change;

	public StandardsecMouseListener( final Interactive_MTDoubleChannel parent, final ValueChange change )
	{
		this.parent = parent;
		this.change = change;
	}
	
	

	@Override
	public void mouseReleased( MouseEvent arg0 )
	{
		
		
			try { Thread.sleep( 10 ); } catch ( InterruptedException e ) {}
		

		parent.updatePreview(change);
	}

	@Override
	public void mousePressed( MouseEvent arg0 ){}

	@Override
	public void mouseExited( MouseEvent arg0 ) {}

	@Override
	public void mouseEntered( MouseEvent arg0 ) {}

	@Override
	public void mouseClicked( MouseEvent arg0 ) {}
}
