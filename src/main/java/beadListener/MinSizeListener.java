package beadListener;

import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import interactiveMT.Interactive_PSFAnalyze;
import interactiveMT.Interactive_PSFAnalyze.ValueChange;
import mpicbg.imglib.multithreading.SimpleMultiThreading;

public class MinSizeListener implements AdjustmentListener {
	final Label label;
	final float min, max;
	final int scrollbarSize;
	final Interactive_PSFAnalyze parent;
	final Scrollbar minsizeScrollbar;

	public MinSizeListener(final Interactive_PSFAnalyze parent, final Label label, final float min, final float max, final int scrollbarSize,
			final Scrollbar minsizeScrollbar) {
		this.label = label;
		this.min = min;
		this.max = max;
		this.scrollbarSize = scrollbarSize;
        this.parent = parent;
		this.minsizeScrollbar = minsizeScrollbar;

	}

	@Override
	public void adjustmentValueChanged(final AdjustmentEvent event) {
		parent.minSize = (int) parent.computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize);

		minsizeScrollbar.setValue(parent.computeScrollbarPositionFromValue(parent.minSize, min, max, scrollbarSize));

		label.setText("Min # of pixels inside MSER Ellipses = " + parent.minSize);

		// if ( !event.getValueIsAdjusting() )
		{
			while (parent.isComputing) {
				SimpleMultiThreading.threadWait(10);
			}
			parent.updatePreview(ValueChange.MINSIZE);
		}
	}
}
