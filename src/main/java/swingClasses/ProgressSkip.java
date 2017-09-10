package swingClasses;

import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import ij.IJ;
import interactiveMT.Interactive_MTDoubleChannel;
import updateListeners.FinalPoint;

public class ProgressSkip extends SwingWorker<Void, Void> {

final Interactive_MTDoubleChannel parent;

	
	public ProgressSkip(final Interactive_MTDoubleChannel parent){
	
		this.parent = parent;
		
	}
	
	@Override
	protected Void doInBackground() throws Exception {

		
		
       

		int next = parent.starttime;

		if (next < 2)
			next = 2;

		
		
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
	IJ.log("Tracking Done and track files written in the chosen folder");
		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}

	}
	
}
