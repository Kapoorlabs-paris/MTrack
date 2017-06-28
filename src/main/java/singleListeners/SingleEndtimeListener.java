package singleListeners;

import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTSingleChannel;

public class SingleEndtimeListener implements AdjustmentListener {
	
	final Interactive_MTSingleChannel parent;
	final Label label;
	final float min, max;
	final int scrollbarSize;

	final Scrollbar deltaScrollbar;

	public SingleEndtimeListener(final Interactive_MTSingleChannel parent,final Label label, final float min, final float max, final int scrollbarSize,
			final Scrollbar deltaScrollbar) {
		this.label = label;
		this.min = min;
		this.max = max;
		this.parent = parent;
		this.scrollbarSize = scrollbarSize;

		this.deltaScrollbar = deltaScrollbar;

	}

	@Override
	public void adjustmentValueChanged(final AdjustmentEvent event) {
		parent.endtime = (int) parent.computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize);

		deltaScrollbar.setValue(parent.computeScrollbarPositionFromValue(parent.endtime, min, max, scrollbarSize));

		label.setText("endFrame = " + parent.endtime);

	}
}

