package mt.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ransacBatch.CreateRanfile;

public class RansacBatchmodeListener implements ActionListener {
	
	final InteractiveRANSAC parent;
	
	public RansacBatchmodeListener(final InteractiveRANSAC parent){
		
		this.parent = parent;
		
	}
	
	
	@Override
	public void actionPerformed(final ActionEvent arg0){
		
		CreateRanfile recordparam = new CreateRanfile(parent);
		recordparam.RecordParent();
		
	}

}
