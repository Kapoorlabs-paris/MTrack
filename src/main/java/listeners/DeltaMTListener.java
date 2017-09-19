package listeners;

import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import ij.IJ;
import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;
import updateListeners.Markends;

public class DeltaMTListener implements AdjustmentListener {
	final Label label;
	 Interactive_MTDoubleChannel parent;
	final float min, max;
	final int scrollbarSize;

	final Scrollbar deltaScrollbar;

	public DeltaMTListener(	final Interactive_MTDoubleChannel parent, final Label label, final float min, final float max, final int scrollbarSize,
			final Scrollbar deltaScrollbar) {
		this.label = label;
		this.parent = parent;
		this.min = min;
		this.max = max;
		this.scrollbarSize = scrollbarSize;

		this.deltaScrollbar = deltaScrollbar;
		
       if (parent.FindLinesViaHOUGH){
    	   deltaScrollbar.addMouseListener( new StandardsecMouseListener( parent,ValueChange.SHOWHOUGH ) );
    	   deltaScrollbar.addMouseListener( new StandardsecMouseListener( parent,ValueChange.SHOWMSERinHough ) );
			
		}
       else if (parent.FindLinesViaMSER || parent.FindLinesViaMSERwHOUGH)
		deltaScrollbar.addMouseListener( new StandardsecMouseListener( parent, ValueChange.SHOWMSER ) );

       else{
    	   
    	   deltaScrollbar.addMouseListener( new StandardsecMouseListener( parent,ValueChange.SHOWHOUGH ) );
    	   deltaScrollbar.addMouseListener( new StandardsecMouseListener( parent,ValueChange.SHOWMSERinHough ) );
    	   
       }
		
		
	}


	@Override
	public void adjustmentValueChanged(final AdjustmentEvent event) {
		parent.delta = parent.computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize);

		deltaScrollbar.setValue(parent.computeScrollbarPositionFromValue(parent.delta, min, max, scrollbarSize));

		label.setText("Intensity threshold = " + parent.delta);

		
	}
	
	
	
}
