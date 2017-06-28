package singleListeners;

import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import interactiveMT.Interactive_MTSingleChannel;
import interactiveMT.Interactive_MTSingleChannel.ValueChange;
import mpicbg.imglib.multithreading.SimpleMultiThreading;

public class SingleRhoSizeHoughListener implements AdjustmentListener {
	final Label label;
	final float min, max;
	final int scrollbarSize;
	final Interactive_MTSingleChannel parent;
	final Scrollbar rhoScrollbar;

	public SingleRhoSizeHoughListener(final Interactive_MTSingleChannel parent, final Label label, final float min, final float max, final int scrollbarSize,
			final Scrollbar rhoScrollbar) {
		this.label = label;
		this.min = min;
		this.max = max;
		this.scrollbarSize = scrollbarSize;
        this.parent = parent;
		this.rhoScrollbar = rhoScrollbar;

	}

	@Override
	public void adjustmentValueChanged(final AdjustmentEvent event) {

		parent.rhoPerPixel = parent.computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize);

		rhoScrollbar.setValue(parent.computeScrollbarPositionFromValue(parent.rhoPerPixel, min, max, scrollbarSize));

		label.setText("Pixel size of Hough Space in Rho / Pixel Space =   " + parent.rhoPerPixel);

		// if ( !event.getValueIsAdjusting() )
		{
			while (parent.isComputing) {
				SimpleMultiThreading.threadWait(10);
			}
			parent.updatePreview(ValueChange.rhoPerPixel);
		}

	}
}
