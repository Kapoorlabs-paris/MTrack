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
package listeners;

import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;
import mpicbg.imglib.multithreading.SimpleMultiThreading;

public class RhoSizeHoughListener implements AdjustmentListener {
	final Label label;
	final float min, max;
	final int scrollbarSize;
	final Interactive_MTDoubleChannel parent;
	final Scrollbar rhoScrollbar;

	public RhoSizeHoughListener(final Interactive_MTDoubleChannel parent, final Label label, final float min, final float max, final int scrollbarSize,
			final Scrollbar rhoScrollbar) {
		this.label = label;
		this.min = min;
		this.max = max;
		this.scrollbarSize = scrollbarSize;
        this.parent = parent;
		this.rhoScrollbar = rhoScrollbar;

	}

	@Override
	public void adjustmentValueChanged(final AdjustmentEvent event) {

		parent.rhoPerPixel = parent.computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize);

		rhoScrollbar.setValue(parent.computeScrollbarPositionFromValue(parent.rhoPerPixel, min, max, scrollbarSize));

		label.setText("Pixel size of Hough Space in Rho / Pixel Space =   " + parent.rhoPerPixel);

		// if ( !event.getValueIsAdjusting() )
		{
			while (parent.isComputing) {
				SimpleMultiThreading.threadWait(10);
			}
			parent.updatePreview(ValueChange.rhoPerPixel);
		}

	}
}
