package mt.listeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class FunctionItemListener implements ItemListener
{
	final InteractiveRANSAC parent;

	public FunctionItemListener( final InteractiveRANSAC parent )
	{
		this.parent = parent;
	}

	@Override
	public void itemStateChanged( final ItemEvent arg0 )
	{
		if ( arg0.getItem().toString().startsWith( "Linear" ) )
			parent.functionChoice = 0;
		else if ( arg0.getItem().toString().startsWith( "Quadratic" ) )
			parent.functionChoice = 1;
		else
			parent.functionChoice = 2;

		parent.setFunction();
		parent.updateRANSAC();
	}
}
