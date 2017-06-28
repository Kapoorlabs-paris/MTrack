package singleListeners;

import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;


import interactiveMT.Interactive_MTSingleChannel;
import interactiveMT.Interactive_MTSingleChannel.ValueChange;
import mpicbg.imglib.multithreading.SimpleMultiThreading;

public class SingleThresholdHoughListener implements AdjustmentListener {
	final Label label;
	final float min, max;
	final Interactive_MTSingleChannel parent;
	final int scrollbarSize;

	final Scrollbar thresholdScrollbar;

	public SingleThresholdHoughListener(final Interactive_MTSingleChannel parent , final Label label, final float min, final float max, final int scrollbarSize,
			final Scrollbar thresholdScrollbar) {
		this.label = label;
		this.min = min;
		this.parent = parent;
		this.max = max;
		this.scrollbarSize = scrollbarSize;

		this.thresholdScrollbar = thresholdScrollbar;
		thresholdScrollbar.addMouseListener( new StandardMouseListener( parent,ValueChange.SHOWHOUGH ) );
	//	thresholdScrollbar.addMouseListener( new StandardMouseListener( parent,ValueChange.SHOWMSERinHough ) );
		
		
	}

	@Override
	public void adjustmentValueChanged(final AdjustmentEvent event) {
		parent.thresholdHough = parent.computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize);

		thresholdScrollbar.setValue(parent.computeScrollbarPositionFromValue(parent.thresholdHough, min, max, scrollbarSize));

		label.setText("threshold Value = " + parent.thresholdHough);

		
	}
}
