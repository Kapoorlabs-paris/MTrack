package swingClasses;

import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import interactiveMT.Interactive_MTDoubleChannel;
import updateListeners.FinalPoint;

public class ProgressSkip extends SwingWorker<Void, Void> {

final Interactive_MTDoubleChannel parent;
	
	
	public ProgressSkip(final Interactive_MTDoubleChannel parent){
	
		this.parent = parent;
	}
	
	@Override
	protected Void doInBackground() throws Exception {

		parent.thirdDimensionSize = parent.thirdDimensionSizeOriginal;

		parent.moveDialogue();

		int next = parent.thirdDimension;

		if (next < 2)
			next = 2;

		FinalPoint finalpoint = new FinalPoint(parent);
		finalpoint.FinalizeEnds();
		
		Track newtrack = new Track(parent);
		newtrack.Trackobject(next);
		

		return null;

	}

	@Override
	protected void done() {
		try {
			parent.jpb.setIndeterminate(false);
			get();
			parent.frame.dispose();
			JOptionPane.showMessageDialog(parent.jpb.getParent(), "Success", "Success", JOptionPane.INFORMATION_MESSAGE);
		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}

	}
	
}
