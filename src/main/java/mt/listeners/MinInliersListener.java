package mt.listeners;

import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

public class MinInliersListener implements AdjustmentListener
{
	final InteractiveRANSAC_ parent;
	final Label label;

	public MinInliersListener( final InteractiveRANSAC_ parent, final Label label, final Scrollbar bar )
	{
		this.parent = parent;
		this.label = label;
		bar.addMouseListener( new StandardMouseListener( parent ) );
	}
	
	@Override
	public void adjustmentValueChanged( final AdjustmentEvent event )
	{
		parent.minInliers = event.getValue();

		label.setText( "Min. #Points (tp) = " + parent.minInliers );
	}
}
