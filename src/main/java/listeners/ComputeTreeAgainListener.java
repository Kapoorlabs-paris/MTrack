package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;
import interactiveMT.Interactive_MTDoubleChannelBasic;

public class ComputeTreeAgainListener implements ActionListener {
	
final Interactive_MTDoubleChannelBasic child;
final Interactive_MTDoubleChannel parent;
	
	public ComputeTreeAgainListener (final Interactive_MTDoubleChannel parent , final Interactive_MTDoubleChannelBasic child ){
		
		this.parent = parent;
		this.child = child;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		parent.ShowMser = true;

		parent.updatePreview(ValueChange.SHOWMSER);
		child.DeterministicSimple();

	}
}