package listeners;

import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;
import mpicbg.imglib.multithreading.SimpleMultiThreading;

public class MinSizeMTListener implements AdjustmentListener {
	final Label label;
	final float min, max;
	final int scrollbarSize;
	final Interactive_MTDoubleChannel parent;
	final Scrollbar minsizeScrollbar;

	public MinSizeMTListener(final Interactive_MTDoubleChannel parent, final Label label, final float min, final float max, final int scrollbarSize,
			final Scrollbar minsizeScrollbar) {
		this.label = label;
		this.min = min;
		this.max = max;
		this.scrollbarSize = scrollbarSize;
        this.parent = parent;
		this.minsizeScrollbar = minsizeScrollbar;
		
		if (parent.FindLinesViaHOUGH){
			
			minsizeScrollbar.addMouseListener( new StandardsecMouseListener( parent,ValueChange.SHOWHOUGH ) );
			minsizeScrollbar.addMouseListener( new StandardsecMouseListener( parent,ValueChange.SHOWMSERinHough ) );
						
					}
		else if (parent.FindLinesViaMSER|| parent.FindLinesViaMSERwHOUGH)

			minsizeScrollbar.addMouseListener( new StandardsecMouseListener( parent, ValueChange.SHOWMSER ) );

	else{
		minsizeScrollbar.addMouseListener( new StandardsecMouseListener( parent,ValueChange.SHOWHOUGH ) );
		minsizeScrollbar.addMouseListener( new StandardsecMouseListener( parent,ValueChange.SHOWMSERinHough ) );
		
	}
	}

	@Override
	public void adjustmentValueChanged(final AdjustmentEvent event) {
		parent.minSize = (int) parent.computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize);

		minsizeScrollbar.setValue(parent.computeScrollbarPositionFromValue(parent.minSize, min, max, scrollbarSize));

		label.setText("Min size of Ellipses = " + parent.minSize);

		
	}
}
