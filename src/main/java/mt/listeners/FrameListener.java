package mt.listeners;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class FrameListener extends WindowAdapter
{
	final InteractiveRANSAC parent;
	
	public FrameListener( InteractiveRANSAC parent )
	{
		super();
		this.parent = parent;
	}
	
	@Override
	public void windowClosing ( WindowEvent e )
	{ 
		parent.close();
		parent.wasCanceled = true;
	}
}
