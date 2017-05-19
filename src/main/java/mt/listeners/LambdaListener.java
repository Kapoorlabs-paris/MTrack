package mt.listeners;

import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

public class LambdaListener implements AdjustmentListener
{
	final InteractiveRANSAC_ parent;
	final Label label;

	public LambdaListener( final InteractiveRANSAC_ parent, final Label label, final Scrollbar bar )
	{
		this.parent = parent;
		this.label = label;
		bar.addMouseListener( new StandardMouseListener( parent ) );
	}
	
	@Override
	public void adjustmentValueChanged( final AdjustmentEvent event )
	{
		parent.lambda = InteractiveRANSAC_.computeValueFromScrollbarPosition(
				event.getValue(),
				InteractiveRANSAC_.MAX_SLIDER,
				0.0,
				1.0 );

		label.setText( "Linearity (fraction) = " + parent.lambda );
		parent.setFunction();
	}
}
