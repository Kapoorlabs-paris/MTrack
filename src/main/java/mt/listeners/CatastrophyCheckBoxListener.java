package mt.listeners;

import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


public class CatastrophyCheckBoxListener implements ItemListener
{
	final InteractiveRANSAC parent;
	final Checkbox checkbox;
	final Label label;
	final Scrollbar scrollbar;

	public CatastrophyCheckBoxListener(
			final InteractiveRANSAC parent,
			final Checkbox checkbox,
			final Label label,
			final Scrollbar scrollbar )
	{
		this.parent = parent;
		this.checkbox = checkbox;
		this.label = label;
		this.scrollbar = scrollbar;

		enableDisable( checkbox.getState() );
	}

	@Override
	public void itemStateChanged( final ItemEvent e )
	{
		boolean state = parent.detectCatastrophe;
		enableDisable( checkbox.getState() );

		if ( checkbox.getState() != state )
		{
			while ( parent.updateCount > 0 )
			{
				try { Thread.sleep( 10 ); } catch ( InterruptedException ex ) {}
			}

			parent.updateRANSAC();
		}
	}

	protected void enableDisable( final boolean state )
	{
		label.setEnabled( state );
		scrollbar.setEnabled( state );

		if ( state )
			label.setForeground( Color.black );
		else
			label.setForeground( Color.GRAY );
		parent.detectCatastrophe = state;
	}
}
