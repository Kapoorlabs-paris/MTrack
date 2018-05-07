/*-
 * #%L
 * Microtubule tracker.
 * %%
 * Copyright (C) 2017 MTrack developers.
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

import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


public class ManualCatastrophyCheckBoxListener implements ItemListener
{
	final InteractiveRANSAC parent;
	final Checkbox checkbox;
	final Label label;
	final Scrollbar scrollbar;

	public ManualCatastrophyCheckBoxListener(
			final InteractiveRANSAC parent,
			final Checkbox checkbox,
			final Label label,
			final Scrollbar scrollbar )
	{
		this.parent = parent;
		this.checkbox = checkbox;
		this.label = label;
		this.scrollbar = scrollbar;

		enableDisable( checkbox.getState() );
	}

	@Override
	public void itemStateChanged( final ItemEvent e )
	{
		boolean state = parent.detectmanualCatastrophe;
		enableDisable( checkbox.getState() );

		if ( checkbox.getState() != state )
		{
			while ( parent.updateCount > 0 )
			{
				try { Thread.sleep( 10 ); } catch ( InterruptedException ex ) {}
			}

			
			parent.updateRANSAC();
		}
	}

	protected void enableDisable( final boolean state )
	{
		label.setEnabled( state );
		
		boolean otherstate = parent.detectCatastrophe;
        boolean actualstate = true;
			
		scrollbar.setEnabled( actualstate );

		if ( state ) 
			label.setForeground( Color.black );
		else 
			label.setForeground( Color.GRAY );
		parent.detectmanualCatastrophe = state;
	}
}
