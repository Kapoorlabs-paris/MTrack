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

public class Preprocess extends SwingWorker<Void, Void> {

	
	final MainFileChooser parent;
	
	public Preprocess (final MainFileChooser parent){
		
		this.parent = parent;
		
	}
	
	
	@Override
	protected Void doInBackground() throws Exception {
		
		
		
		final FlatFieldCorrection flatfilter = new FlatFieldCorrection(parent.originalimg, 2, parent.jpb, parent.psf);
		flatfilter.process();
		parent.originalPreprocessedimg = flatfilter.getResult();
		
		
		
	
	
		
		return null;
	}
	
	@Override
	protected void done() {
		try {
			
			
			
			
			parent.jpb.setIndeterminate(true);
			get();
			parent.frame.dispose();
			
			// Normalize image intnesity
			Normalize.normalize(Views.iterable(parent.originalPreprocessedimg), parent.minval, parent.maxval);
			ImageJFunctions.show(parent.originalPreprocessedimg).setTitle("Preprocessed Movie");
			
							if (parent.Simplemode)
								new Interactive_MTDoubleChannelBasic(new Interactive_MTDoubleChannel(parent.originalimg, parent.originalPreprocessedimg,
										parent.psf, parent.calibration, parent.chooserB.getSelectedFile())).run(null);
							else
								new Interactive_MTDoubleChannel(parent.originalimg, parent.originalPreprocessedimg, parent.psf, parent.calibration,
										parent.chooserB.getSelectedFile()).run(null);
			
			
			
		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}

	}

}
