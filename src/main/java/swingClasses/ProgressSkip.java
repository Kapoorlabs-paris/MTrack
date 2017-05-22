package swingClasses;

import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import interactiveMT.Interactive_MTDoubleChannel;
import updateListeners.FinalPoint;

public class ProgressSkip extends SwingWorker<Void, Void> {

final Interactive_MTDoubleChannel parent;
final int starttime;
final int endtime;
	
	public ProgressSkip(final Interactive_MTDoubleChannel parent, final int starttime, final int endtime){
	
		this.parent = parent;
		this.starttime = starttime;
		this.endtime = endtime;
	}
	
	@Override
	protected Void doInBackground() throws Exception {

		
		
       

		int next = starttime;

		if (next < 2)
			next = 2;

		
		
		Track newtrack = new Track(parent);
		newtrack.Trackobject(next, endtime);
		

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
