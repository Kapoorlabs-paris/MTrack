package beadListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import interactiveMT.Interactive_PSFAnalyze;
import interactiveMT.Interactive_PSFAnalyze.ValueChange;



public class ComputeTreeListener implements ActionListener {
	
final Interactive_PSFAnalyze parent;
	
	public ComputeTreeListener (final Interactive_PSFAnalyze parent ){
		
		this.parent = parent;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		parent.FindBeadsViaMSER = true;

		parent.updatePreview(ValueChange.SHOWMSER);

	}
}