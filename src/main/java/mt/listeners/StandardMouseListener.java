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
	final InteractiveRANSAC parent;

	public StandardMouseListener( final InteractiveRANSAC parent )
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
