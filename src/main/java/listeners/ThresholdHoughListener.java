package listeners;

import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;
import mpicbg.imglib.multithreading.SimpleMultiThreading;

public class ThresholdHoughListener implements AdjustmentListener {
	final Label label;
	final float min, max;
	final Interactive_MTDoubleChannel parent;
	final int scrollbarSize;

	final Scrollbar thresholdScrollbar;

	public ThresholdHoughListener(final Interactive_MTDoubleChannel parent , final Label label, final float min, final float max, final int scrollbarSize,
			final Scrollbar thresholdScrollbar) {
		this.label = label;
		this.min = min;
		this.parent = parent;
		this.max = max;
		this.scrollbarSize = scrollbarSize;

		this.thresholdScrollbar = thresholdScrollbar;

	}

	@Override
	public void adjustmentValueChanged(final AdjustmentEvent event) {
		parent.thresholdHough = parent.computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize);

		thresholdScrollbar.setValue(parent.computeScrollbarPositionFromValue(parent.thresholdHough, min, max, scrollbarSize));

		label.setText("threshold Value = " + parent.thresholdHough);

		// if ( !event.getValueIsAdjusting() )
		{
			while (parent.isComputing) {
				SimpleMultiThreading.threadWait(10);
			}
			parent.updatePreview(ValueChange.thresholdHough);
		}
	}
}
