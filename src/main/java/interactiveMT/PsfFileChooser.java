package interactiveMT;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import ij.IJ;
import ij.ImagePlus;
import ij.io.Opener;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;

import java.awt.event.*;
import java.awt.*;
import java.util.*;

public class PsfFileChooser extends JPanel {
	boolean wasDone = false;
	boolean isFinished = false;
	JButton TrackMeasure;
	JButton Done;
	JFileChooser chooserA;
	String choosertitleA;
	ImagePlus impA;
	JFileChooser chooserB;
	String choosertitleB;

	public PsfFileChooser() {
		final JFrame frame = new JFrame("Welcome to PSF Analyzer");
		frame.setSize(800, 300);

		/* Instantiation */
		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints c = new GridBagConstraints();

		final Label LoadtrackText = new Label("Load image for PSF analysis");
		
		final Label ORText = new Label("OR", Label.CENTER);
		
		LoadtrackText.setBackground(new Color(1, 0, 1));
		LoadtrackText.setForeground(new Color(255, 255, 255));
		ORText.setBackground(new Color(1, 0, 1));
		ORText.setForeground(new Color(255, 255, 255));
	
		
		
		
		TrackMeasure = new JButton("Open image");
		JButton Current = new JButton("Use currently open image");
		

		/* Location */
		frame.setLayout(layout);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 4;
		c.weighty = 1.5;

		
		++c.gridy;
		c.insets = new Insets(0, 170, 0, 75);
		frame.add(LoadtrackText, c);
		

		++c.gridy;
		++c.gridy;
		++c.gridy;
		
		
		++c.gridy;
		c.insets = new Insets(0, 170, 0, 75);
		frame.add(TrackMeasure, c);
		
		++c.gridy;
		c.insets = new Insets(0, 170, 0, 75);
		frame.add(ORText, c);

		++c.gridy;
		c.insets = new Insets(0, 170, 0, 75);
		frame.add(Current, c);
		
		
		
	

		TrackMeasure.addActionListener(new UploadTrackListener(frame));
		Current.addActionListener(new CurrentListener(frame));
		frame.addWindowListener(new FrameListener(frame));
		frame.setVisible(true);

	}

	protected class FrameListener extends WindowAdapter {
		final Frame parent;

		public FrameListener(Frame parent) {
			super();
			this.parent = parent;
		}

		@Override
		public void windowClosing(WindowEvent e) {
			close(parent);
		}
	}

	protected class UploadTrackListener implements ActionListener {

		final Frame parent;

		public UploadTrackListener(Frame parent) {

			this.parent = parent;

		}

		@Override
		public void actionPerformed(final ActionEvent arg0) {

			int result;

			chooserA = new JFileChooser();
			chooserA.setCurrentDirectory(new java.io.File("."));
			chooserA.setDialogTitle(choosertitleA);
			chooserA.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			//
			// disable the "All files" option.
			//
			chooserA.setAcceptAllFileFilterUsed(false);
			 FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "tif");
				
				chooserA.setFileFilter(filter);
			//
			if (chooserA.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
				System.out.println("getCurrentDirectory(): " + chooserA.getCurrentDirectory());
				System.out.println("getSelectedFile() : " + chooserA.getSelectedFile());
			} else {
				System.out.println("No Selection ");
			}
			
			impA = new Opener().openImage(chooserA.getSelectedFile().getPath());
			
			Done(parent);

		
		}

	}
	
	public void Done(Frame parent){
		
		// Tracking and Measurement is done with imageA 
        
	    
		RandomAccessibleInterval<FloatType> originalimgA = ImageJFunctions.convertFloat(impA);
		
		new InteractivePSFAnalyzer_(originalimgA).run(null);
		close(parent);

		
	}

	protected class CurrentListener implements ActionListener {

		final Frame parent;

		public CurrentListener(Frame parent) {

			this.parent = parent;

		}

		@Override
		public void actionPerformed(final ActionEvent arg0) {

			impA = IJ.getImage();
			
			
			Done(parent);
		}

	}

	



	protected final void close(final Frame parent) {
		if (parent != null)
			parent.dispose();

		isFinished = true;
	}

	public Dimension getPreferredSize() {
		return new Dimension(500, 300);
	}

}
