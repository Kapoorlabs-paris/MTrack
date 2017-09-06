package mt.listeners;

import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.text.DecimalFormat;

public class MaxErrorListener implements AdjustmentListener
{
	final InteractiveRANSAC parent;
	final Label label;

	public MaxErrorListener( final InteractiveRANSAC parent, final Label label, final Scrollbar bar )
	{
		this.parent = parent;
		this.label = label;
		bar.addMouseListener( new StandardMouseListener( parent ) );
	}
	
	@Override
	public void adjustmentValueChanged( final AdjustmentEvent event )
	{
		parent.maxError = InteractiveRANSAC.computeValueFromScrollbarPosition(
				event.getValue(),
				InteractiveRANSAC.MAX_SLIDER,
				InteractiveRANSAC.MIN_ERROR,
				InteractiveRANSAC.MAX_ERROR );

		label.setText( "Max. Error (px) = " + new DecimalFormat("#.##").format(parent.maxError) );
	}
}
