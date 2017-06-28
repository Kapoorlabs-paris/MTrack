package singleListeners;

import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;


import interactiveMT.Interactive_MTSingleChannel;
import interactiveMT.Interactive_MTSingleChannel.ValueChange;
import mpicbg.imglib.multithreading.SimpleMultiThreading;

public class SingleThetaSizeHoughListener implements AdjustmentListener {
	final Label label;
	final Label rholabel;
	final float min, max;
	final int scrollbarSize;
	final Interactive_MTSingleChannel parent;
	final Scrollbar thetaScrollbar;
	final Scrollbar rhoScrollbar;

	public SingleThetaSizeHoughListener(final Interactive_MTSingleChannel parent, final Label label, final Label rholabel, final float min, final float max,
			final int scrollbarSize, final Scrollbar thetaScrollbar, final Scrollbar rhoScrollbar) {
		this.label = label;
		this.rholabel = rholabel;
		this.min = min;
		this.max = max;
		this.parent = parent;
		this.scrollbarSize = scrollbarSize;
		this.thetaScrollbar = thetaScrollbar;
		this.rhoScrollbar = rhoScrollbar;

	}

	@Override
	public void adjustmentValueChanged(final AdjustmentEvent event) {
		parent.thetaPerPixel = parent.computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize);

		if (!parent.enablerhoPerPixel) {
			parent.rhoPerPixel = parent.thetaPerPixel;
			rholabel.setText("rhoPerPixel = " + parent.rhoPerPixel);
			rhoScrollbar.setValue(parent.computeScrollbarPositionFromValue(parent.rhoPerPixel, min, max, scrollbarSize));

		}

		thetaScrollbar.setValue(parent.computeScrollbarPositionFromValue(parent.thetaPerPixel, min, max, scrollbarSize));

		label.setText(" Pixel size of Hough Space in Theta / Pixel Space = " + parent.thetaPerPixel);

		// if ( !event.getValueIsAdjusting() )
		{
			while (parent.isComputing) {
				SimpleMultiThreading.threadWait(10);
			}
			parent.updatePreview(ValueChange.thetaPerPixel);
		}
	}
}

