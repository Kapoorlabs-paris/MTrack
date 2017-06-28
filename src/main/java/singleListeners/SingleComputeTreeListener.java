package singleListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import interactiveMT.Interactive_MTDoubleChannel;

import interactiveMT.Interactive_MTSingleChannel;
import interactiveMT.Interactive_MTSingleChannel.ValueChange;

public class SingleComputeTreeListener implements ActionListener {
	
final Interactive_MTSingleChannel parent;
	
	public SingleComputeTreeListener (final Interactive_MTSingleChannel parent ){
		
		this.parent = parent;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		parent.ShowMser = true;

		parent.updatePreview(ValueChange.SHOWMSER);

	}
}