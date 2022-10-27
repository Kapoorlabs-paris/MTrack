/*-
 * #%L
 * Microtubule tracker.
 * %%
 * Copyright (C) 2017 - 2022 MTrack developers.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

package swingClasses;

import java.awt.BorderLayout;
import java.util.concurrent.ExecutionException;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannelBasic;
import interactiveMT.Interactive_MTSingleChannel;
import interactiveMT.Interactive_MTSingleChannelBasic;
import interactiveMT.MainFileChooser;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.stats.Normalize;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;
import peakFitter.FitterUtils;
import preProcessing.FlatFieldCorrection;
import preProcessing.MedianFilterOnly;

public class PreprocessMedian extends SwingWorker<Void, Void> {

	
	final MainFileChooser parent;
	
	public PreprocessMedian (final MainFileChooser parent){
		
		this.parent = parent;
		
	}
	
	
	@Override
	protected Void doInBackground() throws Exception {
		
		if (parent.medianradius > 0){
		parent.frame.add(parent.jpb, BorderLayout.PAGE_END);
		parent.frame.validate();
		MedianFilterOnly flatfilter;
		if (parent.originalPreprocessedimg!=null)
		flatfilter = new MedianFilterOnly(parent.originalPreprocessedimg, parent.medianradius, parent.jpb, parent.psf);
		else
	    flatfilter = new MedianFilterOnly(parent.originalimg, parent.medianradius, parent.jpb, parent.psf);

		flatfilter.process();
		parent.originalPreprocessedimg = flatfilter.getResult();
		
		
		}
	
		
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
		
			if (parent.selectedindex == 1)
			{
				
							
					
							if (parent.Simplemode)
								new Interactive_MTDoubleChannelBasic(new Interactive_MTDoubleChannel(parent.originalimg, parent.originalPreprocessedimg,
										parent.psf, parent.calibration, parent.userfile, parent.addToName)).run(null);
							else
								new Interactive_MTDoubleChannel(parent.originalimg, parent.originalPreprocessedimg, parent.psf, parent.calibration,
										parent.userfile, parent.addToName).run(null);
				
				
				}
				
				
				
				
			
			
			
			if (parent.selectedindex == 2){
				
				// Open Reber lab images
				ImageJFunctions.show(parent.originalPreprocessedimg).setTitle("Preprocessed Movie");
			
				if (parent.Simplemode)
					new Interactive_MTDoubleChannelBasic(new Interactive_MTDoubleChannel(parent.originalimg, parent.originalPreprocessedimg,
							parent.psf, parent.calibration, parent.userfile, parent.addToName)).run(null);
				else
					new Interactive_MTDoubleChannel(parent.originalimg, parent.originalPreprocessedimg, parent.psf, parent.calibration,
							parent.userfile, parent.addToName).run(null);
				
			}
			
			
			if (parent.selectedindex == 3){
				
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
			
		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}

	}

}
