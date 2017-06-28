package singleListeners;

import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import interactiveMT.Interactive_MTSingleChannel;
import interactiveMT.Interactive_MTSingleChannel.ValueChange;
import interactiveMT.Interactive_PSFAnalyze;
import mpicbg.imglib.multithreading.SimpleMultiThreading;

public class SingleMinDiversityHoughListener implements AdjustmentListener {
	final Label label;
	final float min, max;
	final int scrollbarSize;
	Interactive_MTSingleChannel parent;
	final Scrollbar minDiversityScrollbar;
	
	

	public SingleMinDiversityHoughListener(final Interactive_MTSingleChannel parent, final Label label, final float min, final float max, final int scrollbarSize,
			final Scrollbar minDiversityScrollbar) {
		this.label = label;
		this.min = min;
		this.max = max;
		this.parent = parent;
		this.scrollbarSize = scrollbarSize;
		this.minDiversityScrollbar = minDiversityScrollbar;
		minDiversityScrollbar .addMouseListener( new StandardMouseListener( parent,ValueChange.SHOWMSERinHough ) );

	}
	

	
	

	@Override
	public void adjustmentValueChanged(final AdjustmentEvent event) {
		parent.minDiversity = (parent.computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize));

		minDiversityScrollbar
				.setValue(parent.computeScrollbarPositionFromValue((float) parent.minDiversity, min, max, scrollbarSize));

		label.setText("MinDiversity = " + parent.minDiversity);

		
	}
}