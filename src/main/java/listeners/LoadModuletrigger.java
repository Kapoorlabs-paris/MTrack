package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannelBasic;
import interactiveMT.Interactive_MTSingleChannel;
import interactiveMT.Interactive_MTSingleChannelBasic;
import interactiveMT.MainFileChooser;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.stats.Normalize;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Util;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public class LoadModuletrigger implements ActionListener {

	final MainFileChooser parent;
	
	
	public LoadModuletrigger(MainFileChooser parent){
		
		this.parent = parent;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		final FloatType type = parent.originalimg.randomAccess().get().createVariable();
		final ImgFactory<FloatType> factory = Util.getArrayOrCellImgFactory(parent.originalimg, type);
		if (parent.selectedindex == 0)
		{
			
						
				
						if (parent.Simplemode)
							new Interactive_MTDoubleChannelBasic(new Interactive_MTDoubleChannel(parent.originalimg, parent.originalPreprocessedimg,
									parent.psf, parent.calibration, parent.userfile, parent.addToName)).run(null);
						else
							new Interactive_MTDoubleChannel(parent.originalimg, parent.originalPreprocessedimg, parent.psf, parent.calibration,
									parent.userfile, parent.addToName).run(null);
			
			
			}
			
			
			
			
		
		
		
		if (parent.selectedindex == 1){
			
			// Open Reber lab images
			ImageJFunctions.show(parent.originalPreprocessedimg).setTitle("Preprocessed Movie");
		
			if (parent.Simplemode)
				new Interactive_MTDoubleChannelBasic(new Interactive_MTDoubleChannel(parent.originalimg, parent.originalPreprocessedimg,
						parent.psf, parent.calibration, parent.userfile, parent.addToName)).run(null);
			else
				new Interactive_MTDoubleChannel(parent.originalimg, parent.originalPreprocessedimg, parent.psf, parent.calibration,
						parent.userfile, parent.addToName).run(null);
			
		}
		
		
		if (parent.selectedindex == 2){
			
			// Open Surrey lab images
			
			ImageJFunctions.show(parent.originalPreprocessedimg).setTitle("Preprocessed Movie");

			if (parent.Simplemode)
				new Interactive_MTSingleChannelBasic(new Interactive_MTSingleChannel(parent.originalimg, parent.originalPreprocessedimg,
						parent.psf, parent.calibration, parent.userfile, parent.addToName)).run(null);
			else
				new Interactive_MTSingleChannel(parent.originalimg, parent.originalPreprocessedimg, parent.psf, parent.calibration,
						parent.userfile, parent.addToName).run(null);
			
			
		}
		
		parent.frame.dispose();
		
	}
	
	
}
