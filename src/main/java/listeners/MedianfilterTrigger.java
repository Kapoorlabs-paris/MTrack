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
import swingClasses.PreprocessFlat;
import swingClasses.PreprocessMedian;

public class MedianfilterTrigger implements ActionListener {

	
	final MainFileChooser parent;
	
	public MedianfilterTrigger(MainFileChooser parent ){
		
		this.parent = parent;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
	
		
			
			new Normalize();
			
			
			PreprocessMedian prestep = new PreprocessMedian(parent);
			
		    prestep.execute();
			
		
		
	

	}

	

	

	
}
