package updateListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import initialization.CreateINIfile;
import interactiveMT.Interactive_MTDoubleChannel;


	public class BatchModeListener implements ActionListener {
		

		final Interactive_MTDoubleChannel parent;
		
		public BatchModeListener(final Interactive_MTDoubleChannel parent) {

			this.parent = parent;
			
		}
		

		@Override
		public void actionPerformed(final ActionEvent arg0) {
			
			CreateINIfile recordparam = new CreateINIfile(parent);
			recordparam.RecordParent();
			
			

	}
	
}
