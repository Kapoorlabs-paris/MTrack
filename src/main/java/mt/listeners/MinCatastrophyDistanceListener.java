package mt.listeners;

import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;


public class MinCatastrophyDistanceListener implements AdjustmentListener
{
	final InteractiveRANSAC parent;
	final Label label;

	public MinCatastrophyDistanceListener( final InteractiveRANSAC parent, final Label label, final Scrollbar bar )
	{
		this.parent = parent;
		this.label = label;
		bar.addMouseListener( new StandardMouseListener( parent ) );
	}
	
	@Override
	public void adjustmentValueChanged( final AdjustmentEvent event )
	{
		parent.minDistanceCatastrophe = InteractiveRANSAC.computeValueFromScrollbarPosition(
				event.getValue(),
				InteractiveRANSAC.MAX_SLIDER,
				InteractiveRANSAC.MIN_CAT,
				InteractiveRANSAC.MAX_CAT );

		label.setText( "Min. Catatastrophy height (tp) = " + parent.minDistanceCatastrophe );
	}
}
