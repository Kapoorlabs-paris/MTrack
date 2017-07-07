package interpolation;

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
	final InteractiveRegression parent;

	public StandardMouseListener( final InteractiveRegression parent )
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

		parent.updateRegression();
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
