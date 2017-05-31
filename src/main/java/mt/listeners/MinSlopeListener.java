package mt.listeners;

import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

public class MinSlopeListener implements AdjustmentListener
{
	final InteractiveRANSAC parent;
	final Label label;
	final Scrollbar minSlopeSB;

	public MinSlopeListener( final InteractiveRANSAC parent, final Scrollbar minSlopeSB, final Label label )
	{
		this.parent = parent;
		this.label = label;
		this.minSlopeSB = minSlopeSB;
		minSlopeSB.addMouseListener( new StandardMouseListener( parent ) );
	}
	
	@Override
	public void adjustmentValueChanged( final AdjustmentEvent event )
	{
		parent.minSlope = InteractiveRANSAC.computeValueFromDoubleExpScrollbarPosition(
				event.getValue(),
				InteractiveRANSAC.MAX_SLIDER,
				InteractiveRANSAC.MAX_ABS_SLOPE );

		if ( parent.minSlope > parent.maxSlope )
		{
			parent.minSlope = parent.maxSlope;
			minSlopeSB.setValue( InteractiveRANSAC.computeScrollbarPositionValueFromDoubleExp( InteractiveRANSAC.MAX_SLIDER, parent.minSlope, InteractiveRANSAC.MAX_ABS_SLOPE ) );
		}

		label.setText( "Min. Segment Slope (px/tp) = " + parent.minSlope );
	}
}
