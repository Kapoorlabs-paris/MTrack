package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;

public class ComputeTreeListener implements ActionListener {
	
final Interactive_MTDoubleChannel parent;
	
	public ComputeTreeListener (final Interactive_MTDoubleChannel parent ){
		
		this.parent = parent;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		parent.ShowMser = true;

		parent.updatePreview(ValueChange.SHOWMSER);

	}
}