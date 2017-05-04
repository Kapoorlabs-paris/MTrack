package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;

public class DowatershedListener implements ActionListener {

	
final Interactive_MTDoubleChannel parent;
	
	public DowatershedListener (final Interactive_MTDoubleChannel parent ){
		
		this.parent = parent;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		parent.ShowHough = true;
		parent.updatePreview(ValueChange.SHOWHOUGH);

	}
}
