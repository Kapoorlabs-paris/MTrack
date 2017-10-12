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

import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;


public class MinCatastrophyDistanceListener implements AdjustmentListener
{
	final InteractiveRANSAC parent;
	final Label label;

	public MinCatastrophyDistanceListener( final InteractiveRANSAC parent, final Label label, final Scrollbar bar )
	{
		this.parent = parent;
		this.label = label;
		bar.addMouseListener( new StandardMouseListener( parent ) );
	}
	
	@Override
	public void adjustmentValueChanged( final AdjustmentEvent event )
	{
		parent.minDistanceCatastrophe = Math.round(InteractiveRANSAC.computeValueFromScrollbarPosition(
				event.getValue(),
				InteractiveRANSAC.MAX_SLIDER,
				InteractiveRANSAC.MIN_CAT,
				InteractiveRANSAC.MAX_CAT ));

		label.setText( "Min. Catatastrophy height (tp) = " + parent.minDistanceCatastrophe );
	}
}
