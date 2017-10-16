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
import java.text.DecimalFormat;

import javax.swing.JScrollBar;

public class MinSlopeListener implements AdjustmentListener
{
	final InteractiveRANSAC parent;
	final Label label;
	final JScrollBar minSlopeSB;

	public MinSlopeListener( final InteractiveRANSAC parent, final JScrollBar minSlopeSB, final Label label )
	{
		this.parent = parent;
		this.label = label;
		this.minSlopeSB = minSlopeSB;
		minSlopeSB.addMouseListener( new StandardMouseListener( parent ) );
		minSlopeSB.setUnitIncrement(1);
		minSlopeSB.setBlockIncrement(1);
	}
	
	@Override
	public void adjustmentValueChanged( final AdjustmentEvent event )
	{
		parent.minSlope = InteractiveRANSAC.computeValueFromDoubleExpScrollbarPosition(
				event.getValue(),
				InteractiveRANSAC.MAX_SLIDER,
				InteractiveRANSAC.MAX_ABS_SLOPE );

		if ( parent.minSlope > parent.maxSlope )
		{
			parent.minSlope = parent.maxSlope;
			minSlopeSB.setValue( InteractiveRANSAC.computeScrollbarPositionValueFromDoubleExp( InteractiveRANSAC.MAX_SLIDER, parent.minSlope, InteractiveRANSAC.MAX_ABS_SLOPE ) );
		}

		label.setText( "Min. Segment Slope (px/tp) = " + new DecimalFormat("#.##").format(parent.minSlope) );
	}
}
