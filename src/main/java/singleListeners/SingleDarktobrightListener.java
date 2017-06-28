package singleListeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import interactiveMT.Interactive_MTSingleChannel;
import interactiveMT.Interactive_MTSingleChannel.ValueChange;
import mpicbg.imglib.multithreading.SimpleMultiThreading;

public class SingleDarktobrightListener implements ItemListener {
	
final Interactive_MTSingleChannel parent;
	
	public SingleDarktobrightListener (final Interactive_MTSingleChannel parent ){
		
		this.parent = parent;
	}
	
	@Override
	public void itemStateChanged(final ItemEvent arg0) {
		boolean oldState = parent.darktobright;

		if (arg0.getStateChange() == ItemEvent.DESELECTED)
			parent.darktobright = false;
		else if (arg0.getStateChange() == ItemEvent.SELECTED)
			parent.darktobright = true;

		if (parent.darktobright != oldState) {
			while (parent.isComputing)
				SimpleMultiThreading.threadWait(10);

			parent.updatePreview(ValueChange.DARKTOBRIGHT);
		}
	}
}
