package beadListener;

import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;


import interactiveMT.Interactive_PSFAnalyze;
import interactiveMT.Interactive_PSFAnalyze.ValueChange;
import mpicbg.imglib.multithreading.SimpleMultiThreading;

public class MinDiversityListener implements AdjustmentListener {
	final Label label;
	final float min, max;
	final int scrollbarSize;
	Interactive_PSFAnalyze parent;
	final Scrollbar minDiversityScrollbar;
	
	

	public MinDiversityListener(final Interactive_PSFAnalyze parent, final Label label, final float min, final float max, final int scrollbarSize,
			final Scrollbar minDiversityScrollbar) {
		this.label = label;
		this.min = min;
		this.max = max;
		this.parent = parent;
		this.scrollbarSize = scrollbarSize;
		this.minDiversityScrollbar = minDiversityScrollbar;

	}
	

	
	

	@Override
	public void adjustmentValueChanged(final AdjustmentEvent event) {
		parent.minDiversity = (parent.computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize));

		minDiversityScrollbar
				.setValue(parent.computeScrollbarPositionFromValue((float) parent.minDiversity, min, max, scrollbarSize));

		label.setText("MinDiversity = " + parent.minDiversity);

		// if ( !event.getValueIsAdjusting() )
		{
			while (parent.isComputing) {
				SimpleMultiThreading.threadWait(10);
			}
			parent.updatePreview(ValueChange.MINDIVERSITY);
		}
	}
}
