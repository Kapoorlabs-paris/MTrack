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

import javax.swing.JScrollBar;

import ij.IJ;
import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;
import updateListeners.Markends;

public class MTimeListener implements AdjustmentListener {
	final Label label;
	final String string;
	Interactive_MTDoubleChannel parent;
	final float min, max;
	final int scrollbarSize;

	final JScrollBar deltaScrollbar;

	public MTimeListener(final Interactive_MTDoubleChannel parent, final Label label, final String string, final float min, final float max,
			final int scrollbarSize, final JScrollBar deltaScrollbar) {
		this.label = label;
		this.parent = parent;
		this.string = string;
		this.min = min;
		this.max = max;
		this.scrollbarSize = scrollbarSize;

		this.deltaScrollbar = deltaScrollbar;
		deltaScrollbar.addMouseMotionListener(new NonStandardMouseListener(parent, ValueChange.THIRDDIMmouse));
		deltaScrollbar.setBlockIncrement(parent.computeScrollbarPositionFromValue(2, min, max, scrollbarSize));
		deltaScrollbar.setUnitIncrement(parent.computeScrollbarPositionFromValue(2, min, max, scrollbarSize));
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		parent.thirdDimension = (int) parent.computeValueFromScrollbarPosition(e.getValue(), min, max, scrollbarSize);

		deltaScrollbar
				.setValue(parent.computeScrollbarPositionFromValue(parent.thirdDimension, min, max, scrollbarSize));

		label.setText(string +  " = "  + parent.thirdDimension);

		shownew();

	}
	
	public void shownew() {

		if (parent.thirdDimension > parent.thirdDimensionSize) {
			IJ.log("Max frame number exceeded, moving to last frame instead");
			parent.thirdDimension = parent.thirdDimensionSize;
			parent.CurrentView = util.CopyUtils.getCurrentView(parent.originalimg, parent.thirdDimension,
					parent.thirdDimensionSize);
			parent.CurrentPreprocessedView = util.CopyUtils.getCurrentPreView(parent.originalPreprocessedimg,
					parent.thirdDimension, parent.thirdDimensionSize);
		} else {

			parent.CurrentView = util.CopyUtils.getCurrentView(parent.originalimg, parent.thirdDimension,
					parent.thirdDimensionSize);
			parent.CurrentPreprocessedView = util.CopyUtils.getCurrentPreView(parent.originalPreprocessedimg,
					parent.thirdDimension, parent.thirdDimensionSize);
		}

		
		
	

		
	}
}
