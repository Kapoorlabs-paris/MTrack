package polynomialBead;

import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import beadAnalyzer.DrawPoints;
import beadFinder.BeadfinderInteractiveDoG;
import beadFinder.BeadfinderInteractiveMSER;
import beadFinder.FindbeadsVia;
import ij.IJ;
import interactiveMT.Interactive_PSFAnalyze;
import lineFinder.LinefinderInteractiveMSER;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.real.FloatType;

public class ProgressPolyline extends SwingWorker<Void, Void>{

	
     final Interactive_PSFAnalyze parent;
	
	public ProgressPolyline(final Interactive_PSFAnalyze parent){
		
		this.parent = parent;
	}
	
	
	@Override
	protected Void doInBackground() throws Exception {
		
		
		RandomAccessibleInterval<FloatType> source = parent.currentPreprocessedimg;
		RandomAccessibleInterval<FloatType> target = parent.currentimg;
		
		
			
			LinefinderInteractiveMSER newbeadMser = new LinefinderInteractiveMSER(source, target, parent.newtree, parent.thirdDimension);
			
			
			
			parent.FittedLineBeads = FindbeadsVia.BeadfindingMethod(source, newbeadMser, parent.jpb, parent.initialpsf, parent.Intensityratio, parent.Inispacing);
			
			
		
		
		
		
		DrawPoints draw = new DrawPoints();
		if (parent.FittedLineBeads.size() > 1)
		draw.drawLinePoints(parent.FittedLineBeads);
		
		
	
		
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
