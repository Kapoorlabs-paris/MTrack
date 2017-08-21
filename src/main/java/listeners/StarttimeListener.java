package listeners;

import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollBar;

import interactiveMT.Interactive_MTDoubleChannel;

public class StarttimeListener implements AdjustmentListener {
	final Label label;
	final String string;
	final float min, max;
	final int scrollbarSize;
	final Interactive_MTDoubleChannel parent;
	final JScrollBar deltaScrollbar;

	public StarttimeListener(final Interactive_MTDoubleChannel parent,final Label label, final String string, final float min, final float max, final int scrollbarSize,
			final JScrollBar deltaScrollbar) {
		this.label = label;
		this.string = string;
		this.min = min;
		this.max = max;
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