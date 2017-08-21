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

public class FirepreTrigger implements ActionListener {

	
	final MainFileChooser parent;
	final JComboBox<String> choice;
	
	public FirepreTrigger(MainFileChooser parent,final JComboBox<String> choice ){
		
		this.parent = parent;
		this.choice = choice;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
	
		int preindex = choice.getSelectedIndex();
		
		if (preindex == 0){
		
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
		
		if (parent.selectedindex == 0)
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
					
			//preoutputSlice = (IntervalView<FloatType>) parent.Preprocess(outputSlice);
			//			Normalize.normalize(Views.iterable(pretotalimg), parent.minval, parent.maxval);
			//			ImageJFunctions.show(pretotalimg).setTitle("Preprocessed Movie");
						
				
						if (parent.Simplemode)
							new Interactive_MTDoubleChannelBasic(new Interactive_MTDoubleChannel(parent.originalimg, parent.originalPreprocessedimg,
									parent.psf, parent.calibration, parent.chooserB.getSelectedFile())).run(null);
						else
							new Interactive_MTDoubleChannel(parent.originalimg, parent.originalPreprocessedimg, parent.psf, parent.calibration,
									parent.chooserB.getSelectedFile()).run(null);
			
			
			}
			
			
			
			
		
		
		
		if (parent.selectedindex == 1){
			
			// Open Reber lab images
			ImageJFunctions.show(parent.originalPreprocessedimg).setTitle("Preprocessed Movie");
		
			if (parent.Simplemode)
				new Interactive_MTDoubleChannelBasic(new Interactive_MTDoubleChannel(parent.originalimg, parent.originalPreprocessedimg,
						parent.psf, parent.calibration, parent.chooserB.getSelectedFile())).run(null);
			else
				new Interactive_MTDoubleChannel(parent.originalimg, parent.originalPreprocessedimg, parent.psf, parent.calibration,
						parent.chooserB.getSelectedFile()).run(null);
			
		}
		
		
		if (parent.selectedindex == 2){
			
			// Open Surrey lab images
			
			ImageJFunctions.show(parent.originalPreprocessedimg).setTitle("Preprocessed Movie");

			if (parent.Simplemode)
				new Interactive_MTDoubleChannelBasic(new Interactive_MTDoubleChannel(parent.originalimg, parent.originalPreprocessedimg,
						parent.psf, parent.calibration, parent.chooserB.getSelectedFile())).run(null);
			else
				new Interactive_MTDoubleChannel(parent.originalimg, parent.originalPreprocessedimg, parent.psf, parent.calibration,
						parent.chooserB.getSelectedFile()).run(null);
			
			
		}
		parent.frame.dispose();
		}
		}
		else{
			
			
			new Normalize();
			
			
			Preprocess prestep = new Preprocess(parent);
			
		    prestep.execute();
			
		}
		
	

	}
}
