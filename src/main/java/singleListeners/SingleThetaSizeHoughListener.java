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
package singleListeners;

import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;


import interactiveMT.Interactive_MTSingleChannel;
import interactiveMT.Interactive_MTSingleChannel.ValueChange;
import mpicbg.imglib.multithreading.SimpleMultiThreading;

public class SingleThetaSizeHoughListener implements AdjustmentListener {
	final Label label;
	final Label rholabel;
	final float min, max;
	final int scrollbarSize;
	final Interactive_MTSingleChannel parent;
	final Scrollbar thetaScrollbar;
	final Scrollbar rhoScrollbar;

	public SingleThetaSizeHoughListener(final Interactive_MTSingleChannel parent, final Label label, final Label rholabel, final float min, final float max,
			final int scrollbarSize, final Scrollbar thetaScrollbar, final Scrollbar rhoScrollbar) {
		this.label = label;
		this.rholabel = rholabel;
		this.min = min;
		this.max = max;
		this.parent = parent;
		this.scrollbarSize = scrollbarSize;
		this.thetaScrollbar = thetaScrollbar;
		this.rhoScrollbar = rhoScrollbar;

	}

	@Override
	public void adjustmentValueChanged(final AdjustmentEvent event) {
		parent.thetaPerPixel = parent.computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize);

		if (!parent.enablerhoPerPixel) {
			parent.rhoPerPixel = parent.thetaPerPixel;
			rholabel.setText("rhoPerPixel = " + parent.rhoPerPixel);
			rhoScrollbar.setValue(parent.computeScrollbarPositionFromValue(parent.rhoPerPixel, min, max, scrollbarSize));

		}

		thetaScrollbar.setValue(parent.computeScrollbarPositionFromValue(parent.thetaPerPixel, min, max, scrollbarSize));

		label.setText(" Pixel size of Hough Space in Theta / Pixel Space = " + parent.thetaPerPixel);

		// if ( !event.getValueIsAdjusting() )
		{
			while (parent.isComputing) {
				SimpleMultiThreading.threadWait(10);
			}
			parent.updatePreview(ValueChange.thetaPerPixel);
		}
	}
}

