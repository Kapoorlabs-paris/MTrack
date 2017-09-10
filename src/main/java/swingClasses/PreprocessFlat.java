
package swingClasses;

import java.awt.BorderLayout;
import java.util.concurrent.ExecutionException;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannelBasic;
import interactiveMT.MainFileChooser;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.stats.Normalize;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;
import peakFitter.FitterUtils;
import preProcessing.FlatFieldCorrection;
import preProcessing.FlatFieldOnly;

public class PreprocessFlat extends SwingWorker<Void, Void> {

	
	final MainFileChooser parent;
	
	public PreprocessFlat (final MainFileChooser parent){
		
		this.parent = parent;
		
	}
	
	
	@Override
	protected Void doInBackground() throws Exception {
		
		
		
		final FlatFieldOnly flatfilter = new FlatFieldOnly(parent.originalimg, 2, parent.jpb, parent.psf);
		flatfilter.process();
		parent.originalPreprocessedimg = flatfilter.getResult();
		
		
		
	
	
		
		return null;
	}
	
	@Override
	protected void done() {
		try {
			
			
			
			
			parent.jpb.setIndeterminate(false);
			parent.frame.remove(parent.jpb);
			parent.frame.validate();
			get();
			
			// Normalize image intnesity
			Normalize.normalize(Views.iterable(parent.originalPreprocessedimg), parent.minval, parent.maxval);
		
			
		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}

	}

}
