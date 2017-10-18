package mt.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WriteLengthListener implements ActionListener {

	
	
	final InteractiveRANSAC parent;

	public WriteLengthListener(final InteractiveRANSAC parent) {
		this.parent = parent;
	}

	@Override
	public void actionPerformed(final ActionEvent arg0) {

		
		WriteStatsListener stats = new WriteStatsListener(parent);
  		stats.lengthDistro(parent.framenumber);

	}
}
