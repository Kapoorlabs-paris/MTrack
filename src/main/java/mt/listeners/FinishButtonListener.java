package mt.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FinishButtonListener implements ActionListener
{
	final InteractiveRANSAC_ parent;
	final boolean cancel;

	public FinishButtonListener( final InteractiveRANSAC_ parent, final boolean cancel )
	{
		this.parent = parent;
		this.cancel = cancel;
	}

	@Override
	public void actionPerformed( final ActionEvent arg0 )
	{
		parent.updateRANSAC();
		//parent.close();
		//parent.wasCanceled = cancel;
	}
}