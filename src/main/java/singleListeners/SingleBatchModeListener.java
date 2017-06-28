package singleListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import initialization.CreateINIfile;
import initialization.SingleCreateINIfile;
import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTSingleChannel;


	public class SingleBatchModeListener implements ActionListener {
		

		final Interactive_MTSingleChannel parent;
		
		public SingleBatchModeListener(final Interactive_MTSingleChannel parent) {

			this.parent = parent;
			
		}
		

		@Override
		public void actionPerformed(final ActionEvent arg0) {
			
			SingleCreateINIfile recordparam = new SingleCreateINIfile(parent);
			recordparam.RecordParent();
			
			

	}
	
}
