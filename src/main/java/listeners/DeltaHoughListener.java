package listeners;

import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;

public class DeltaHoughListener implements AdjustmentListener {
	final Label label;
	 Interactive_MTDoubleChannel parent;
	final float min, max;
	final int scrollbarSize;

	final Scrollbar deltaScrollbar;

	public DeltaHoughListener(	final Interactive_MTDoubleChannel parent, final Label label, final float min, final float max, final int scrollbarSize,
			final Scrollbar deltaScrollbar) {
		this.label = label;
		this.parent = parent;
		this.min = min;
		this.max = max;
		this.scrollbarSize = scrollbarSize;

		this.deltaScrollbar = deltaScrollbar;
		deltaScrollbar.addMouseListener( new StandardsecMouseListener( parent, ValueChange.SHOWHOUGH ) );

	}


	@Override
	public void adjustmentValueChanged(final AdjustmentEvent event) {
		parent.delta = parent.computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize);

		deltaScrollbar.setValue(parent.computeScrollbarPositionFromValue(parent.delta, min, max, scrollbarSize));

		label.setText("Grey Level Seperation between Components = " + parent.delta);

		
	}
}