package beadFinder;

import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import interactiveMT.Interactive_PSFAnalyze;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.real.FloatType;

public class ProgressBeads extends SwingWorker<Void, Void> {
	
	
	final Interactive_PSFAnalyze parent;
	
	public ProgressBeads(final Interactive_PSFAnalyze parent){
		
		this.parent = parent;
	}
	
	
	@Override
	protected Void doInBackground() throws Exception {
		
		
		RandomAccessibleInterval<FloatType> source = parent.currentPreprocessedimg;
		RandomAccessibleInterval<FloatType> target = parent.currentimg;
		
		if (parent.FindBeadsViaMSER){
			
			BeadfinderInteractiveMSER newbeadMser = new BeadfinderInteractiveMSER(source, target, parent.newtree, parent.thirdDimension);
			
		}
		
		
		if (parent.FindBeadsViaDOG){
			
			
			
		}
		
		
		return null;
	}
	

	@Override
	protected void done() {
		try {
			parent.jpb.setIndeterminate(false);
			get();
			parent.frame.dispose();
			// JOptionPane.showMessageDialog(jpb.getParent(), "End Points
			// found and overlayed", "Success",
			// JOptionPane.INFORMATION_MESSAGE);
		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
