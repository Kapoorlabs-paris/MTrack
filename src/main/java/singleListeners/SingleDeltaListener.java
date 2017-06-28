package singleListeners;

import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import interactiveMT.Interactive_MTSingleChannel;
import interactiveMT.Interactive_MTSingleChannel.ValueChange;



public class SingleDeltaListener implements AdjustmentListener {
	final Label label;
	 Interactive_MTSingleChannel parent;
	final float min, max;
	final int scrollbarSize;

	final Scrollbar deltaScrollbar;

	public SingleDeltaListener(	final Interactive_MTSingleChannel parent, final Label label, final float min, final float max, final int scrollbarSize,
			final Scrollbar deltaScrollbar) {
		this.label = label;
		this.parent = parent;
		this.min = min;
		this.max = max;
		this.scrollbarSize = scrollbarSize;

		this.deltaScrollbar = deltaScrollbar;
		deltaScrollbar.addMouseListener( new StandardMouseListener( parent, ValueChange.SHOWMSER ) );

	}


	@Override
	public void adjustmentValueChanged(final AdjustmentEvent event) {
		parent.delta = parent.computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize);

		deltaScrollbar.setValue(parent.computeScrollbarPositionFromValue(parent.delta, min, max, scrollbarSize));

		label.setText("Grey Level Seperation between Components = " + parent.delta);

		
	}
}
