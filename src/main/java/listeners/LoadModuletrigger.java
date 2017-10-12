/*-
 * #%L
 * Microtubule tracker.
 * %%
 * Copyright (C) 2017 MTrack developers.
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
