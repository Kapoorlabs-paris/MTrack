package listeners;

import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import ij.ImagePlus;
import ij.io.Opener;
import interactiveMT.MainFileChooser;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.stats.Normalize;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Util;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public class FireTrigger implements ActionListener {

	
	final MainFileChooser parent;
	final JComboBox<String> choice;
	
	public FireTrigger(MainFileChooser parent, JComboBox<String> choice ){
		
		this.parent = parent;
		this.choice = choice;
	}
	TextField LoadtrackText ;
	@Override
	public void actionPerformed(ActionEvent e) {
		
		int selectedindex = choice.getSelectedIndex();
	
		if(selectedindex == 0){
			
			
			parent.selectedindex = selectedindex;
			parent.panelIntro.validate();
			parent.frame.pack();
			
		}
		
		if (selectedindex == 1){
			
			parent.selectedindex = selectedindex;
			BufferedImage img = null;
			try {
			    img = ImageIO.read(new File("images/smallseed.png"));
			} catch (IOException eee) {
			   
			}
			
			BufferedImage imgsec = null;
			try {
			    imgsec = ImageIO.read(new File("images/smallseedmoves.png"));
			} catch (IOException ee) {
			   
			}
			
			BufferedImage imgthird = null;
			try {
			    imgthird = ImageIO.read(new File("images/smallseedmove2.png"));
			} catch (IOException ee) {
			    
			}
			
			int scale = 100;
			if (img !=null && imgsec!=null && imgthird!=null){
			Image dimg = img.getScaledInstance(scale, scale,
			        Image.SCALE_SMOOTH);
			
			ImageIcon image = new ImageIcon(dimg);
			JLabel seedimage = new JLabel("", image, JLabel.CENTER);
			
			
			Image dimgsec = imgsec.getScaledInstance(scale, scale,
			        Image.SCALE_SMOOTH);
			
			ImageIcon imagesec = new ImageIcon(dimgsec);
			JLabel dynamicimage = new JLabel("", imagesec, JLabel.CENTER);
			
			
			
			Image dimgthird = imgthird.getScaledInstance(scale, scale,
			        Image.SCALE_SMOOTH);
			
			ImageIcon imagethird = new ImageIcon(dimgthird);
			JLabel dynamicimagesec = new JLabel("", imagethird, JLabel.CENTER);
			
			
			
			parent.panelIntro.add(seedimage,new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			
			parent.panelIntro.add(dynamicimage,new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			parent.panelIntro.add(dynamicimagesec,new GridBagConstraints(3, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			
			parent.panelIntro.validate();
			
			parent.frame.pack();
			}
		}
		
		
		if(selectedindex == 2){
			
			parent.selectedindex = selectedindex;
			parent.panelIntro.validate();
			parent.frame.pack();
			
		}
		
		
		
		
		parent.chooserB = new JFileChooser();
		if (parent.chooserA != null)
			parent.chooserB.setCurrentDirectory(parent.chooserA.getCurrentDirectory());
		else
			parent.chooserB.setCurrentDirectory(new java.io.File("."));
		parent.chooserB.setDialogTitle(parent.choosertitleB);
		parent.chooserB.setFileSelectionMode(JFileChooser.FILES_ONLY);

		if (parent.chooserB.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
			System.out.println("getCurrentDirectory(): " + parent.chooserB.getCurrentDirectory());
			System.out.println("getSelectedFile() : " + parent.chooserB.getSelectedFile());
			System.out.println(parent.chooserB.getSelectedFile().getName());
		} else {
			System.out.println("No Selection ");
			parent.chooserB = null;
		}
		if (parent.chooserB!=null){
		// Actual image
		ImagePlus impB = new Opener().openImage(parent.chooserB.getSelectedFile().getPath());
		// Tracking is done with imageA measurment is performed on
		// imageB
		parent.inputFieldcalX.setText(String.valueOf(new DecimalFormat("#.###").format(impB.getCalibration().pixelWidth)));
		parent.inputFieldcalY.setText(String.valueOf(new DecimalFormat("#.###").format(impB.getCalibration().pixelHeight)));
		parent.inputFieldT.setText(String.valueOf(new DecimalFormat("#.###").format(impB.getCalibration().pixelDepth)));
		parent.inputFieldX.setText(String.valueOf(new DecimalFormat("#.###").format(parent.psf[0])));
		parent.inputFieldY.setText(String.valueOf(new DecimalFormat("#.###").format(parent.psf[1])));
	
		
		TextField LoadtrackText = new TextField(
				"Image read: " + parent.chooserB.getSelectedFile());
		LoadtrackText.setColumns(30);
		new Normalize();
		/*
		parent.panelIntro.add(LoadtrackText, new GridBagConstraints(0, 4, 20, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));
		parent.panelIntro.validate();
		parent.frame.pack();
		*/
		
		
		parent.originalimg = ImageJFunctions.convertFloat(impB);

		final FloatType type = parent.originalimg.randomAccess().get().createVariable();
		final ImgFactory<FloatType> factory = Util.getArrayOrCellImgFactory(parent.originalimg, type);
		parent.originalPreprocessedimg = factory.create(parent.originalimg, type);
		// Normalize image intnesity
		Normalize.normalize(Views.iterable(parent.originalimg), parent.minval, parent.maxval);
		parent.inputField.setText(parent.chooserB.getSelectedFile().getName().replaceFirst("[.][^.]+$", ""));

		parent.addToName = parent.inputField.getText();
		parent.userfile = parent.chooserB.getSelectedFile();
		parent.panelIntro.validate();
		
	//	LoadtrackText.addTextListener(new InputListener(this, parent));
		if (selectedindex == 0)
		{
			// Open Hyperstack images
			
			// Do concetation
			RandomAccessibleInterval<FloatType> seedimgStack = Views.hyperSlice(parent.originalimg, 2, 0);

			RandomAccessibleInterval<FloatType> dynamicimgStack = Views.hyperSlice(parent.originalimg, 2, 1);

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
			
			parent.originalimg = totalimg;
			
			
			
		}
		
		
		}
		
		
		
	}

}
