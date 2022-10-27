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
package listeners;

import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import ij.IJ;
import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;
import updateListeners.Markends;

public class DeltaMTListener implements AdjustmentListener {
	final Label label;
	 Interactive_MTDoubleChannel parent;
	final float min, max;
	final int scrollbarSize;

	final Scrollbar deltaScrollbar;

	public DeltaMTListener(	final Interactive_MTDoubleChannel parent, final Label label, final float min, final float max, final int scrollbarSize,
			final Scrollbar deltaScrollbar) {
		this.label = label;
		this.parent = parent;
		this.min = min;
		this.max = max;
		this.scrollbarSize = scrollbarSize;

		this.deltaScrollbar = deltaScrollbar;
		
       if (parent.FindLinesViaHOUGH){
    	   deltaScrollbar.addMouseListener( new StandardsecMouseListener( parent,ValueChange.SHOWHOUGH ) );
    	   deltaScrollbar.addMouseListener( new StandardsecMouseListener( parent,ValueChange.SHOWMSERinHough ) );
			
		}
       else if (parent.FindLinesViaMSER || parent.FindLinesViaMSERwHOUGH)
		deltaScrollbar.addMouseListener( new StandardsecMouseListener( parent, ValueChange.SHOWMSER ) );

       else{
    	   
    	   deltaScrollbar.addMouseListener( new StandardsecMouseListener( parent,ValueChange.SHOWHOUGH ) );
    	   deltaScrollbar.addMouseListener( new StandardsecMouseListener( parent,ValueChange.SHOWMSERinHough ) );
    	   
       }
		
		
	}


	@Override
	public void adjustmentValueChanged(final AdjustmentEvent event) {
		parent.delta = parent.computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize);

		deltaScrollbar.setValue(parent.computeScrollbarPositionFromValue(parent.delta, min, max, scrollbarSize));

		label.setText("Threshold difference = " + parent.delta);

		
	}
	
	
	
}
