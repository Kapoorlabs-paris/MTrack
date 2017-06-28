package singleListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import interactiveMT.Interactive_MTSingleChannel;
import interactiveMT.Interactive_MTSingleChannel.ValueChange;

public class SingleDowatershedListener implements ActionListener {

	
final Interactive_MTSingleChannel parent;
	
	public SingleDowatershedListener (final Interactive_MTSingleChannel parent ){
		
		this.parent = parent;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		parent.ShowHough = true;
		parent.updatePreview(ValueChange.SHOWHOUGH);

	}
}
