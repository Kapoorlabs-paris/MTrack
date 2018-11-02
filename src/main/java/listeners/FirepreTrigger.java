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

import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import ij.ImagePlus;
import ij.io.Opener;
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
import swingClasses.Preprocess;
import util.SingletoDoubleConversion;

public class FirepreTrigger implements ActionListener {

	
	final MainFileChooser parent;
	
	public FirepreTrigger(MainFileChooser parent ){
		
		this.parent = parent;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
	
	
		
		
		
		parent.chooserA = new JFileChooser();
		if (parent.chooserB != null)
			parent.chooserA.setCurrentDirectory(parent.chooserB.getCurrentDirectory());
		else
			parent.chooserA.setCurrentDirectory(new java.io.File("."));
		parent.chooserA.setDialogTitle(parent.choosertitleA);
		parent.chooserA.setFileSelectionMode(JFileChooser.FILES_ONLY);

		if (parent.chooserA.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
			System.out.println("getCurrentDirectory(): " + parent.chooserA.getCurrentDirectory());
			System.out.println("getSelectedFile() : " + parent.chooserA.getSelectedFile());
		} else {
			System.out.println("No Selection ");
			parent.chooserA = null;
		}
		
		if(parent.chooserA!=null){
		ImagePlus impA = new Opener().openImage(parent.chooserA.getSelectedFile().getPath());
		// Tracking is done with imageA measurment is performed on
		// imageB
		final TextField LoadtrackText = new TextField(
				"PreProcessed Image read: " + parent.chooserA.getSelectedFile());
		LoadtrackText.setColumns(20);
		parent.panelIntro.add(LoadtrackText, new GridBagConstraints(0, 13, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.RELATIVE, new Insets(0, 10, 0, 10), 0, 0));
		parent.panelIntro.validate();
		parent.frame.pack();
		new Normalize();
		
		
		parent.originalPreprocessedimg = ImageJFunctions.convertFloat(impA);
		final FloatType type = parent.originalPreprocessedimg.randomAccess().get().createVariable();
		final ImgFactory<FloatType> factory = Util.getArrayOrCellImgFactory(parent.originalPreprocessedimg, type);
		// Normalize image intnesity
		Normalize.normalize(Views.iterable(parent.originalPreprocessedimg), parent.minval, parent.maxval);
		
		if (parent.selectedindex == 1)
		{
			
			// Do concetation
						RandomAccessibleInterval<FloatType> seedimgStack = Views.hyperSlice(parent.originalPreprocessedimg, 2, 0);

						RandomAccessibleInterval<FloatType> dynamicimgStack = Views.hyperSlice(parent.originalPreprocessedimg, 2, 1);

						long[] dim = { dynamicimgStack.dimension(0), dynamicimgStack.dimension(1),
								dynamicimgStack.dimension(2) };
						RandomAccessibleInterval<FloatType> totalimg = factory.create(dim, type);
						final long nz = dynamicimgStack.dimension(2);

						IntervalView<FloatType> slice = Views.hyperSlice(seedimgStack, 2, 0);
						IntervalView<FloatType> outputSlice = Views.hyperSlice(totalimg, 2, 0);

						parent.processSlice(slice, outputSlice);
						for (long z = 1; z < nz; z++) {
							slice = Views.hyperSlice(dynamicimgStack, 2, z);
							outputSlice = Views.hyperSlice(totalimg, 2, z);

							parent.processSlice(slice, outputSlice);
						}
						Normalize.normalize(Views.iterable(totalimg), parent.minval, parent.maxval);
						ImageJFunctions.show(totalimg).setTitle("Preprocessed Movie");
						
						parent.originalPreprocessedimg = totalimg;
					
		
						
				
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
		}
		
		
		
	

	}
}
