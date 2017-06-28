package singleListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import interactiveMT.Interactive_MTSingleChannel;
import interactiveMT.Interactive_MTSingleChannel.ValueChange;


public class SingleComputeMserinHoughListener implements ActionListener {
	
final Interactive_MTSingleChannel parent;
	
	public SingleComputeMserinHoughListener (final Interactive_MTSingleChannel parent ){
		
		this.parent = parent;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		parent.ShowMser = true;
        parent.ShowHough = true;
		parent.updatePreview(ValueChange.SHOWMSERinHough);

	}
}