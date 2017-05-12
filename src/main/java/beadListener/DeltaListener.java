package beadListener;

import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;


import interactiveMT.Interactive_PSFAnalyze;
import interactiveMT.Interactive_PSFAnalyze.ValueChange;
import mpicbg.imglib.multithreading.SimpleMultiThreading;

public class DeltaListener implements AdjustmentListener {
	final Label label;
	 Interactive_PSFAnalyze parent;
	final float min, max;
	final int scrollbarSize;

	final Scrollbar deltaScrollbar;

	public DeltaListener(	final Interactive_PSFAnalyze parent, final Label label, final float min, final float max, final int scrollbarSize,
			final Scrollbar deltaScrollbar) {
		this.label = label;
		this.parent = parent;
		this.min = min;
		this.max = max;
		this.scrollbarSize = scrollbarSize;

		this.deltaScrollbar = deltaScrollbar;

	}


	@Override
	public void adjustmentValueChanged(final AdjustmentEvent event) {
		parent.delta = parent.computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize);

		deltaScrollbar.setValue(parent.computeScrollbarPositionFromValue(parent.delta, min, max, scrollbarSize));

		label.setText("Grey Level Seperation between Components = " + parent.delta);

		// if ( !event.getValueIsAdjusting() )
		{
			while (parent.isComputing) {
				SimpleMultiThreading.threadWait(10);
			}
			parent.updatePreview(ValueChange.DELTA);
		}
	}
}
