package beadListener;

import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import interactiveMT.Interactive_PSFAnalyze;
import interactiveMT.Interactive_PSFAnalyze.ValueChange;
import mpicbg.imglib.multithreading.SimpleMultiThreading;

public class MaxSizeListener implements AdjustmentListener {
	final Label label;
	final float min, max;
	final int scrollbarSize;
    final Interactive_PSFAnalyze parent;
	final Scrollbar maxsizeScrollbar;

	public MaxSizeListener(final Interactive_PSFAnalyze parent,final Label label, final float min, final float max, final int scrollbarSize,
			final Scrollbar maxsizeScrollbar) {
		this.label = label;
		this.min = min;
		this.max = max;
		this.scrollbarSize = scrollbarSize;
        this.parent = parent;
		this.maxsizeScrollbar = maxsizeScrollbar;

	}

	@Override
	public void adjustmentValueChanged(final AdjustmentEvent event) {
		parent.maxSize = (int) parent.computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize);

		maxsizeScrollbar.setValue(parent.computeScrollbarPositionFromValue(parent.maxSize, min, max, scrollbarSize));

		label.setText("Max # of pixels inside MSER Ellipses = "+ parent.maxSize);

		// if ( !event.getValueIsAdjusting() )
		{
			while (parent.isComputing) {
				SimpleMultiThreading.threadWait(10);
			}
			parent.updatePreview(ValueChange.MAXSIZE);
		}
	}
}
