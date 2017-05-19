package mt.listeners;

import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

public class MinSlopeListener implements AdjustmentListener
{
	final InteractiveRANSAC_ parent;
	final Label label;
	final Scrollbar minSlopeSB;

	public MinSlopeListener( final InteractiveRANSAC_ parent, final Scrollbar minSlopeSB, final Label label )
	{
		this.parent = parent;
		this.label = label;
		this.minSlopeSB = minSlopeSB;
		minSlopeSB.addMouseListener( new StandardMouseListener( parent ) );
	}
	
	@Override
	public void adjustmentValueChanged( final AdjustmentEvent event )
	{
		parent.minSlope = InteractiveRANSAC_.computeValueFromDoubleExpScrollbarPosition(
				event.getValue(),
				InteractiveRANSAC_.MAX_SLIDER,
				InteractiveRANSAC_.MAX_ABS_SLOPE );

		if ( parent.minSlope > parent.maxSlope )
		{
			parent.minSlope = parent.maxSlope;
			minSlopeSB.setValue( InteractiveRANSAC_.computeScrollbarPositionValueFromDoubleExp( InteractiveRANSAC_.MAX_SLIDER, parent.minSlope, InteractiveRANSAC_.MAX_ABS_SLOPE ) );
		}

		label.setText( "Min. Segment Slope (px/tp) = " + parent.minSlope );
	}
}
