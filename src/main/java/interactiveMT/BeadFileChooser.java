package interactiveMT;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;


import ij.ImagePlus;
import ij.io.Opener;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.stats.Normalize;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

import java.awt.event.*;
import java.io.File;
import java.awt.*;
import java.util.*;

public class BeadFileChooser extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean wasDone = false;
	boolean isFinished = false;
	JButton Track;
	JButton Measure;
	JButton Done;
	JFileChooser chooserA;
	String choosertitleA;
	public JLabel inputLabelX, inputLabelY, inputLabelT;
	public TextField inputFieldX, inputFieldY, inputFieldT;
	boolean batchprocess;
	JFileChooser chooserB;
	String choosertitleB;
	double[] calibration = new double[2];
	float frametosec;
	JFileChooser chooserC;
	String choosertitleC;
	double[] psf = new double[2];
	
	JPanel panelCont = new JPanel();
	JPanel panelIntro = new JPanel();
	
	public enum whichModel{
		
		Bead, Filament;
		
	}
	
	whichModel UserModel = whichModel.Bead;
	
	public BeadFileChooser() {
		final JFrame frame = new JFrame("Welcome to PSF Analyzer");
		
		panelCont.add(panelIntro, "1");
		/* Instantiation */
		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints c = new GridBagConstraints();

		panelIntro.setLayout(layout);
		

		JLabel lbltrack = new JLabel("Choose object type in the image");

		String[] choicestrack = new String[2];
		
		choicestrack[0] = "Beads";
		choicestrack[1] = "Filaments";
		JComboBox<String> cbtrack = new JComboBox<String>(choicestrack);
		
		final Label LoadtrackText = new Label("Load pre-processed bead/filament image");
		final Label LoadMeasureText = new Label("Load original image");
		
		final Checkbox batch = new Checkbox("Split Channels and do Batch Processing", false);
		
		
		LoadtrackText.setBackground(new Color(1, 0, 1));
		LoadtrackText.setForeground(new Color(255, 255, 255));

		LoadMeasureText.setBackground(new Color(1, 0, 1));
		LoadMeasureText.setForeground(new Color(255, 255, 255));
		
		inputLabelX = new JLabel("If batch processing (beads) enter first common characters of filename: ");
		inputFieldX = new TextField();
		inputFieldX.setColumns(10);

		Track = new JButton("Open pre-processed bead/ filament image");
		Measure = new JButton("Open image for performing measurments");
		Done = new JButton("Done");
		

	//	inputLabelT = new JLabel("Enter time frame to second conversion: ");
	//	inputFieldT = new TextField();
	//	inputFieldT.setColumns(2);

		/* Location */

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1.5;
		
		
		++c.gridy;
		c.insets = new Insets(10, 10, 0, 50);
		panelIntro.add(lbltrack, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 50);
		panelIntro.add(cbtrack, c);
		
		
		++c.gridy;
		c.insets = new Insets(10, 10, 10, 0);
		panelIntro.add(LoadtrackText, c);

		

		++c.gridy;
		c.insets = new Insets(10, 10, 10, 0);
		panelIntro.add(Track, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 10, 0);
		panelIntro.add(LoadMeasureText, c);

	
		++c.gridy;
		c.insets = new Insets(10, 10, 10, 0);
		panelIntro.add(Measure, c);
        /*
		++c.gridy;
		c.insets = new Insets(10, 10, 0, 50);
		panelIntro.add(batch, c);
		++c.gridy;
		c.insets = new Insets(10, 10, 10, 0);
		panelIntro.add(inputLabelX, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 180);
		panelIntro.add(inputFieldX, c);
		*/
		++c.gridy;
		c.insets = new Insets(10, 10, 10, 0);
		panelIntro.add(Done, c);
		
		
		
		
		panelIntro.setVisible(true);
		Track.addActionListener(new OpenTrackListener(frame));
		Measure.addActionListener(new MeasureListener(frame));
		Done.addActionListener(new DoneButtonListener(frame, true));
		cbtrack.addActionListener(new ModelListener(cbtrack));
		inputFieldX.addTextListener(new StringFilesListener());
		frame.addWindowListener(new FrameListener(frame));
		frame.add(panelCont, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	
	
	protected class StringFilesListener implements TextListener{

		@Override
		public void textValueChanged(TextEvent e) {
		
		  TextComponent tc = (TextComponent) e.getSource();
		  String s = tc.getText();
		 
		  File folder = new File(chooserB.getCurrentDirectory().getAbsolutePath());
		  File[] listofFiles = folder.listFiles();
		  
		  for (int i = 0; i < listofFiles.length; ++i){
			  
			  String filename = listofFiles[i].getName();
			  if(filename.startsWith(s)){
				  
				  // Now you have a bunch of image files and you have to do something with them
				  
			  }
			  
		  }
			
		}
		
		
		
		
	}
	
	protected class ModelListener implements ActionListener{

		
		final JComboBox<String> cb;
		
		public ModelListener(final JComboBox<String> cb){
			
			this.cb = cb;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			if(cb.getSelectedItem() == "Beads" ){
				
				UserModel = whichModel.Bead;
			}
			
			else
				UserModel = whichModel.Filament;
				
			
			
		}
		
		
		
		
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

	
	
	
	
	
	protected class OpenTrackListener implements ActionListener {

		final Frame parent;

		public OpenTrackListener(Frame parent) {

			this.parent = parent;

		}

		@Override
		public void actionPerformed(final ActionEvent arg0) {

			int result;

			chooserA = new JFileChooser();
			chooserA.setCurrentDirectory(new java.io.File("."));
			chooserA.setDialogTitle(choosertitleA);
			chooserA.setFileSelectionMode(JFileChooser.FILES_ONLY);
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

		}

	}

	protected class MeasureListener implements ActionListener {

		final Frame parent;

		public MeasureListener(Frame parent) {

			this.parent = parent;

		}

		@Override
		public void actionPerformed(final ActionEvent arg0) {

			int result;

			chooserB = new JFileChooser();
			chooserB.setCurrentDirectory(chooserA.getCurrentDirectory());
			chooserB.setDialogTitle(choosertitleB);
			chooserB.setFileSelectionMode(JFileChooser.FILES_ONLY);
			//
			// disable the "All files" option.
			//
			chooserB.setAcceptAllFileFilterUsed(false);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "tif");

			chooserB.setFileFilter(filter);
			//
			if (chooserB.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
				System.out.println("getCurrentDirectory(): " + chooserB.getCurrentDirectory());
				System.out.println("getSelectedFile() : " + chooserB.getSelectedFile());
			} else {
				System.out.println("No Selection ");
			}

		}

	}

	

	protected class DoneButtonListener implements ActionListener {
		final Frame parent;
		final boolean Done;

		public DoneButtonListener(Frame parent, final boolean Done) {
			this.parent = parent;
			this.Done = Done;
		}

		@Override
		public void actionPerformed(final ActionEvent arg0) {
			wasDone = Done;

			ImagePlus impA = new Opener().openImage(chooserA.getSelectedFile().getPath());
			ImagePlus impB = new Opener().openImage(chooserB.getSelectedFile().getPath());
			
			// Tracking is done with imageA measurment is performed on both the
			// images
			calibration[0] = impA.getCalibration().pixelWidth;
			calibration[1] = impA.getCalibration().pixelHeight;

			RandomAccessibleInterval<FloatType> originalPreprocessedimg = ImageJFunctions.convertFloat(impA);
			RandomAccessibleInterval<FloatType> originalimg = ImageJFunctions.convertFloat(impB);
		

			new Normalize();

			FloatType minval = new FloatType(0);
			FloatType maxval = new FloatType(1);
			Normalize.normalize(Views.iterable(originalimg), minval, maxval);
			Normalize.normalize(Views.iterable(originalPreprocessedimg), minval, maxval);
		
		    new Interactive_PSFAnalyze(originalimg, originalPreprocessedimg, UserModel, batchprocess, chooserB.getCurrentDirectory(), chooserB.getSelectedFile()).run(null);
			
		    close(parent);
		}
	}

	protected final void close(final Frame parent) {
		if (parent != null)
			parent.dispose();

		isFinished = true;
	}

	public Dimension getPreferredSize() {
		return new Dimension(800, 300);
	}

}
