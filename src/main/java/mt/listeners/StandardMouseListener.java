package mt.listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Updates when mouse is released
 * 
 * @author spreibi
 *
 */
public class StandardMouseListener implements MouseListener
{
	final InteractiveRANSAC_ parent;

	public StandardMouseListener( final InteractiveRANSAC_ parent )
	{
		this.parent = parent;
	}

	@Override
	public void mouseReleased( MouseEvent arg0 )
	{
		while ( parent.updateCount > 0 )
		{
			try { Thread.sleep( 10 ); } catch ( InterruptedException e ) {}
		}

		parent.updateRANSAC();
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
