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

import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;
import interactiveMT.Interactive_PSFAnalyze;
import mpicbg.imglib.multithreading.SimpleMultiThreading;

public class MinDiversityMTListener implements AdjustmentListener {
	final Label label;
	final float min, max;
	final int scrollbarSize;
	Interactive_MTDoubleChannel parent;
	final Scrollbar minDiversityScrollbar;
	
	

	public MinDiversityMTListener(final Interactive_MTDoubleChannel parent, final Label label, final float min, final float max, final int scrollbarSize,
			final Scrollbar minDiversityScrollbar) {
		this.label = label;
		this.min = min;
		this.max = max;
		this.parent = parent;
		this.scrollbarSize = scrollbarSize;
		this.minDiversityScrollbar = minDiversityScrollbar;
		
if (parent.FindLinesViaHOUGH){
			
	minDiversityScrollbar.addMouseListener( new StandardsecMouseListener( parent,ValueChange.SHOWHOUGH ) );
	minDiversityScrollbar.addMouseListener( new StandardsecMouseListener( parent,ValueChange.SHOWMSERinHough ) );
				
			}
else if (parent.FindLinesViaMSER || parent.FindLinesViaMSERwHOUGH)
		minDiversityScrollbar .addMouseListener( new StandardsecMouseListener( parent,ValueChange.SHOWMSER ) );
else{
	minDiversityScrollbar.addMouseListener( new StandardsecMouseListener( parent,ValueChange.SHOWHOUGH ) );
	minDiversityScrollbar.addMouseListener( new StandardsecMouseListener( parent,ValueChange.SHOWMSERinHough ) );
	
}
	}
	

	
	

	@Override
	public void adjustmentValueChanged(final AdjustmentEvent event) {
		parent.minDiversity = (parent.computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize));

		minDiversityScrollbar
				.setValue(parent.computeScrollbarPositionFromValue((float) parent.minDiversity, min, max, scrollbarSize));

		label.setText("MinDiversity = " + parent.minDiversity);

		
	}
}
