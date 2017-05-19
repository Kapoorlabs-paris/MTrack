package mt.listeners;

import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

public class MaxErrorListener implements AdjustmentListener
{
	final InteractiveRANSAC_ parent;
	final Label label;

	public MaxErrorListener( final InteractiveRANSAC_ parent, final Label label, final Scrollbar bar )
	{
		this.parent = parent;
		this.label = label;
		bar.addMouseListener( new StandardMouseListener( parent ) );
	}
	
	@Override
	public void adjustmentValueChanged( final AdjustmentEvent event )
	{
		parent.maxError = InteractiveRANSAC_.computeValueFromScrollbarPosition(
				event.getValue(),
				InteractiveRANSAC_.MAX_SLIDER,
				InteractiveRANSAC_.MIN_ERROR,
				InteractiveRANSAC_.MAX_ERROR );

		label.setText( "Max. Error (px) = " + parent.maxError );
	}
}
