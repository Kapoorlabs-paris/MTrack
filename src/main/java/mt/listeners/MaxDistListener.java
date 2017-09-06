package mt.listeners;

import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.text.DecimalFormat;

public class MaxDistListener implements AdjustmentListener
{
	final InteractiveRANSAC parent;
	final Label label;
	
	public MaxDistListener( final InteractiveRANSAC parent, final Label label, final Scrollbar bar )
	{
		this.parent = parent;
		this.label = label;
		bar.addMouseListener( new StandardMouseListener( parent ) );
	}
	
	@Override
	public void adjustmentValueChanged( final AdjustmentEvent event )
	{
		parent.maxDist = event.getValue();

		label.setText( "Max. Gap (tp) = " + new DecimalFormat("#.##").format(parent.maxDist) );
	}
}
