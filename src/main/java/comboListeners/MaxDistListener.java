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
package comboListeners;

import java.awt.Label;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.text.DecimalFormat;

import javax.swing.JScrollBar;

import mt.listeners.InteractiveRANSAC;
import mt.listeners.StandardMouseListener;


public class MaxDistListener implements AdjustmentListener {
	final Label label;
	final String string;
	InteractiveRANSAC parent;
	final float min;
	final int scrollbarSize;

	float max;
	final JScrollBar deltaScrollbar;

	public MaxDistListener(final InteractiveRANSAC parent, final Label label, final String string, final float min, float max,
			final int scrollbarSize, final JScrollBar deltaScrollbar) {
		this.label = label;
		this.parent = parent;
		this.string = string;
		this.min = min;
	
		this.scrollbarSize = scrollbarSize;

		deltaScrollbar.addMouseListener( new StandardMouseListener( parent ) );
		this.deltaScrollbar = deltaScrollbar;
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		
		max =  parent.MAX_Gap;
		
		
		parent.maxDist = (int) Math.round(utility.Slicer.computeValueFromScrollbarPosition(e.getValue(), min, max, scrollbarSize));

	
		deltaScrollbar
				.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.maxDist, min, max, scrollbarSize));

		label.setText(string +  " = "  + parent.df.format(parent.maxDist) + "      ");
		if(e.getValueIsAdjusting())
		parent.maxGapField.setText(Float.toString((parent.maxDist)));
		parent.panelFirst.validate();
		parent.panelFirst.repaint();
	
		
	}
	
}


