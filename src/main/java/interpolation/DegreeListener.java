package interpolation;

import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

public class DegreeListener implements AdjustmentListener
{
	final InteractiveRegression parent;
	final Label label;

	public DegreeListener( final InteractiveRegression parent, final Label label, final Scrollbar bar )
	{
		this.parent = parent;
		this.label = label;
		bar.addMouseListener( new StandardMouseListener( parent ) );
	}
	
	@Override
	public void adjustmentValueChanged( final AdjustmentEvent event )
	{
		parent.degree = (int) InteractiveRegression.computeValueFromScrollbarPosition(
				event.getValue(),
				InteractiveRegression.MAX_SLIDER,
				InteractiveRegression.MIN_DEGREE,
				InteractiveRegression.MAX_DEGREE );

		label.setText( "Polynomial Degree = " + parent.degree );
	}
}
