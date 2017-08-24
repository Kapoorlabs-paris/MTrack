package singleListeners;

import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollBar;

import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTSingleChannel;

public class SingleStarttimeListener implements AdjustmentListener {
	final Label label;
	final float min, max;
	final String string;
	final int scrollbarSize;
	final Interactive_MTSingleChannel parent;
	final JScrollBar deltaScrollbar;

	public SingleStarttimeListener(final Interactive_MTSingleChannel parent,final Label label, final String string, final float min, final float max, final int scrollbarSize,
			final JScrollBar deltaScrollbar) {
		this.label = label;
		this.min = min;
		this.max = max;
		this.string = string;
		this.scrollbarSize = scrollbarSize;
        this.parent = parent;
		this.deltaScrollbar = deltaScrollbar;

	}

	@Override
	public void adjustmentValueChanged(final AdjustmentEvent event) {
		parent.starttime = (int) parent.computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize);

		deltaScrollbar.setValue(parent.computeScrollbarPositionFromValue(parent.starttime, min, max, scrollbarSize));

		label.setText(string +  " = " + parent.starttime);

	}
}