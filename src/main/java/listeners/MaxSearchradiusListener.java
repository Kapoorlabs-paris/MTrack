package listeners;

import java.awt.Label;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;
import mpicbg.imglib.multithreading.SimpleMultiThreading;

public class MaxSearchradiusListener implements AdjustmentListener {
	final Label label;
	final float min, max;
	final Interactive_MTDoubleChannel parent;
	public MaxSearchradiusListener(final Interactive_MTDoubleChannel parent, final Label label, final float min, final float max) {
		this.label = label;
		this.min = min;
		this.max = max;
		this.parent = parent;
	}

	@Override
	public void adjustmentValueChanged(final AdjustmentEvent event) {
		parent.maxSearchradius = parent.computeValueFromScrollbarPosition(event.getValue(), min, max, parent.scrollbarSize);
		label.setText("Max Search Radius:  = " + parent.maxSearchradius);

		if (!parent.isComputing) {
			parent.updatePreview(ValueChange.maxSearch);
		} else if (!event.getValueIsAdjusting()) {
			while (parent.isComputing) {
				SimpleMultiThreading.threadWait(10);
			}
			parent.updatePreview(ValueChange.maxSearch);
		}
	}
}
