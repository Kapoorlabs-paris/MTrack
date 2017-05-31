package mt.listeners;

import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

public class MaxSlopeListener implements AdjustmentListener
{
	final InteractiveRANSAC parent;
	final Label label;
	final Scrollbar maxSlopeSB;

	public MaxSlopeListener( final InteractiveRANSAC parent, final Scrollbar maxSlopeSB, final Label label )
	{
		this.parent = parent;
		this.label = label;
		this.maxSlopeSB = maxSlopeSB;
		maxSlopeSB.addMouseListener( new StandardMouseListener( parent ) );
	}
	
	@Override
	public void adjustmentValueChanged( final AdjustmentEvent event )
	{
		parent.maxSlope = InteractiveRANSAC.computeValueFromDoubleExpScrollbarPosition(
				event.getValue(),
				InteractiveRANSAC.MAX_SLIDER,
				InteractiveRANSAC.MAX_ABS_SLOPE );

		if ( parent.maxSlope < parent.minSlope )
		{
			parent.maxSlope = parent.minSlope;
			maxSlopeSB.setValue( InteractiveRANSAC.computeScrollbarPositionValueFromDoubleExp( InteractiveRANSAC.MAX_SLIDER, parent.maxSlope, InteractiveRANSAC.MAX_ABS_SLOPE ) );
		}

		label.setText( "Max. Segment Slope (px/tp) = " + parent.maxSlope );
	}
}
