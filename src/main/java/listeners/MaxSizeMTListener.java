package listeners;

import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;
import mpicbg.imglib.multithreading.SimpleMultiThreading;

public class MaxSizeMTListener implements AdjustmentListener {
	final Label label;
	final float min, max;
	final int scrollbarSize;
    final Interactive_MTDoubleChannel parent;
	final Scrollbar maxsizeScrollbar;

	public MaxSizeMTListener(final Interactive_MTDoubleChannel parent,final Label label, final float min, final float max, final int scrollbarSize,
			final Scrollbar maxsizeScrollbar) {
		this.label = label;
		this.min = min;
		this.max = max;
		this.scrollbarSize = scrollbarSize;
        this.parent = parent;
		this.maxsizeScrollbar = maxsizeScrollbar;
if (parent.FindLinesViaHOUGH){
			
	maxsizeScrollbar.addMouseListener( new StandardsecMouseListener( parent,ValueChange.SHOWHOUGH ) );
	maxsizeScrollbar.addMouseListener( new StandardsecMouseListener( parent,ValueChange.SHOWMSERinHough ) );
						
					}
else if (parent.FindLinesViaMSER|| parent.FindLinesViaMSERwHOUGH)

		maxsizeScrollbar.addMouseListener( new StandardsecMouseListener( parent, ValueChange.SHOWMSER ) );

else{
	maxsizeScrollbar.addMouseListener( new StandardsecMouseListener( parent,ValueChange.SHOWHOUGH ) );
	maxsizeScrollbar.addMouseListener( new StandardsecMouseListener( parent,ValueChange.SHOWMSERinHough ) );
	
}
	}
	
		

	@Override
	public void adjustmentValueChanged(final AdjustmentEvent event) {
		parent.maxSize = (int) parent.computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize);

		maxsizeScrollbar.setValue(parent.computeScrollbarPositionFromValue(parent.maxSize, min, max, scrollbarSize));

		label.setText("Max size of Ellipses = "+ parent.maxSize);

	}
}
