package interactiveMT;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.io.Opener;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.gauss.Gauss;
import net.imglib2.algorithm.gauss3.Gauss3;
import net.imglib2.algorithm.stats.Normalize;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.realtransform.Scale3D;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Util;
import net.imglib2.view.Views;
import peakFitter.FitterUtils;
import preProcessing.FlatFieldCorrection;
import preProcessing.Kernels;
import preProcessing.MedianFilter2D;

import java.awt.event.*;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.awt.*;
import java.util.*;

public class FileChooser extends JPanel {
	boolean wasDone = false;
	boolean isFinished = false;
	JButton Track;
	JButton Measure;
	JButton Measurebatch;
	JButton Kymo;
	JButton Done;
	File[] AllMovies;
	JFileChooser chooserA;
	String choosertitleA;
	RandomAccessibleInterval<FloatType> ProgramPreprocessedimg;
	JFileChooser chooserB;
	String choosertitleB;
	double[] calibration = new double[2];
	float frametosec;
	JFileChooser chooserC;
	String choosertitleC;
	double[] psf = new double[2];
	private JLabel inputLabelX, inputLabelY, inputLabelT;
	private TextField inputFieldX, inputFieldY, inputFieldT;
	JPanel panelCont = new JPanel();
	JPanel panelIntro = new JPanel();
	boolean Simplemode = true;
	boolean Advancedmode = false;
	boolean Kymomode = false;
	boolean Loadpreimage = false;
	boolean Batchmoderun = false;

	FloatType minval = new FloatType(0);
	FloatType maxval = new FloatType(1);
	public FileChooser() {
		final JFrame frame = new JFrame("Welcome to MTV Tracker");

		panelCont.add(panelIntro, "1");
		/* Instantiation */
		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints c = new GridBagConstraints();

		panelIntro.setLayout(layout);
		CheckboxGroup mode = new CheckboxGroup();
		final Checkbox Batchmode = new Checkbox("Run in Batch Mode",mode, Batchmoderun);
		final Label LoadtrackText = new Label("Load pre-processed tracking image");
		final Label LoadMeasureText = new Label("Load original image");
		final Label KymoText = new Label("Kymo mode (only 1 MT, pick Kymograph image) else skip");
		final Label StartText = new Label("Input Microscope parameters");

		final Checkbox Simple = new Checkbox("Run in Simple mode ", mode, Simplemode);
		final Checkbox Advanced = new Checkbox("Run in Advanced mode ", mode, Advancedmode);
		final Checkbox Kymochoice = new Checkbox("Analyze single MT, compare with Kymograph ", Kymomode);

		LoadtrackText.setBackground(new Color(1, 0, 1));
		LoadtrackText.setForeground(new Color(255, 255, 255));

		LoadMeasureText.setBackground(new Color(1, 0, 1));
		LoadMeasureText.setForeground(new Color(255, 255, 255));

		KymoText.setBackground(new Color(1, 0, 1));
		KymoText.setForeground(new Color(255, 255, 255));

		StartText.setBackground(new Color(1, 0, 1));
		StartText.setForeground(new Color(255, 255, 255));

		Track = new JButton("Optionally, Open Pre-processed movie (Done internally if movie not supplied)");
		Measure = new JButton("Open UN-preprocessed movie performing measurments");
		Kymo = new JButton("Open Kymograph for the MT");
		Done = new JButton("Done");
		inputLabelX = new JLabel("Enter sigmaX of Microscope PSF (pixel units): ");
		inputFieldX = new TextField();
		inputFieldX.setColumns(10);

		inputLabelY = new JLabel("Enter sigmaY of Microscope PSF (pixel units): ");
		inputFieldY = new TextField();
		inputFieldY.setColumns(10);

		// inputLabelT = new JLabel("Enter time frame to second conversion: ");
		// inputFieldT = new TextField();
		// inputFieldT.setColumns(2);

		/* Location */

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1.5;
		++c.gridy;
		c.insets = new Insets(10, 10, 10, 0);
		panelIntro.add(Batchmode, c);
		
		//++c.gridy;
		//c.insets = new Insets(10, 10, 10, 0);
		//panelIntro.add(Kymochoice, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 10, 0);
		panelIntro.add(Simple, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 10, 0);
		panelIntro.add(Advanced, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 10, 0);
		panelIntro.add(LoadMeasureText, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 10, 0);
		panelIntro.add(Measure, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 10, 0);
		panelIntro.add(LoadtrackText, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 10, 0);
		panelIntro.add(Track, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 10, 0);
		panelIntro.add(StartText, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 10, 0);
		panelIntro.add(inputLabelX, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 10, 0);
		panelIntro.add(inputFieldX, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 10, 0);
		panelIntro.add(inputLabelY, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 10, 0);
		panelIntro.add(inputFieldY, c);

		/*
		 * ++c.gridy; c.insets = new Insets(10, 10, 10, 0);
		 * panelIntro.add(inputLabelT, c);
		 * 
		 * ++c.gridy; c.insets = new Insets(10, 10, 10, 0);
		 * panelIntro.add(inputFieldT, c);
		 */
		Kymochoice.addItemListener(new KymochoiceListener());
		++c.gridy;
		++c.gridy;
		++c.gridy;
		++c.gridy;
		c.insets = new Insets(10, 10, 10, 0);
		panelIntro.add(Done, c);
		panelIntro.setVisible(true);
		Track.addActionListener(new OpenTrackListener(frame));
		Measure.addActionListener(new MeasureListener(frame));
		Batchmode.addItemListener(new RuninBatchListener(frame));
		Done.addActionListener(new DoneButtonListener(frame, true));
		Simple.addItemListener(new RunSimpleListener());
		Advanced.addItemListener(new RunAdvancedListener());
		frame.addWindowListener(new FrameListener(frame));
		frame.add(panelCont, BorderLayout.CENTER);
		frame.pack();

		frame.setVisible(true);
	}

	protected class RuninBatchListener implements ItemListener {
		
		
		final Frame parent;
		
		public RuninBatchListener(Frame parent) {

			this.parent = parent;

		}
		
		@Override
		public void itemStateChanged(ItemEvent e) {
			
			close(parent);
			
			
			
			panelIntro.removeAll();
			
			/* Instantiation */
			final GridBagLayout layout = new GridBagLayout();
			final GridBagConstraints c = new GridBagConstraints();

			panelIntro.setLayout(layout);
			
			final JFrame frame = new JFrame("Welcome to MTV Tracker (Batch Mode)");
			Batchmoderun = true;
			Kymomode = false;
			Simplemode = true;
			Done = new JButton("Start batch processing");
			
			

			final Label LoadDirectoryText = new Label("Using Fiji Prefs we execute the program for all tif files");

			LoadDirectoryText.setBackground(new Color(1, 0, 1));
			LoadDirectoryText.setForeground(new Color(255, 255, 255));

			Measurebatch = new JButton("Select directory of tif files to process");

			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 1.5;

			++c.gridy;
			c.insets = new Insets(10, 10, 10, 0);
			panelIntro.add(LoadDirectoryText, c);

			++c.gridy;
			c.insets = new Insets(10, 10, 10, 0);
			panelIntro.add(Measurebatch, c);
			++c.gridy;
			c.insets = new Insets(10, 10, 10, 0);
			panelIntro.add(Done, c);
			Measurebatch.addActionListener(new MeasurebatchListener(frame));
			Done.addActionListener(new DoneButtonListener(frame, true));
			panelIntro.validate();
			panelIntro.repaint();
			frame.addWindowListener(new FrameListener(frame));
			frame.add(panelCont, BorderLayout.CENTER);

			frame.setSize(getPreferredSizeSmall());
			frame.setVisible(true);
			

		}

	}

	protected class MeasurebatchListener implements ActionListener {

		final Frame parent;

		public MeasurebatchListener(Frame parent) {

			this.parent = parent;

		}

		@Override
		public void actionPerformed(final ActionEvent arg0) {

			int result;

			chooserB = new JFileChooser();
			if (chooserA != null)
				chooserB.setCurrentDirectory(chooserA.getCurrentDirectory());
			else
				chooserB.setCurrentDirectory(new java.io.File("."));
			chooserB.setDialogTitle(choosertitleB);
			chooserB.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			//
			// disable the "All files" option.
			//
			chooserB.setAcceptAllFileFilterUsed(false);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "tif");

			chooserB.setFileFilter(filter);
			chooserB.showOpenDialog(parent);

		
			
			AllMovies = chooserB.getSelectedFile().listFiles(new FilenameFilter() {
				
				@Override
				public boolean accept(File pathname, String filename) {
					
					return filename.endsWith(".tif");
				}
			});

			
		

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

	public RandomAccessibleInterval<FloatType> Preprocess(RandomAccessibleInterval<FloatType> originalimg) {

	

		final FlatFieldCorrection flatfilter = new FlatFieldCorrection(originalimg, 1);
		flatfilter.process();
		RandomAccessibleInterval<FloatType> ProgramPreprocessedimg = flatfilter.getResult();
		return ProgramPreprocessedimg;

	}

	
	
	
	protected class KymochoiceListener implements ItemListener {

		@Override
		public void itemStateChanged(final ItemEvent arg0) {

			if (arg0.getStateChange() == ItemEvent.SELECTED) {

				final JFrame frame = new JFrame("Welcome to MTV Tracker");
				Kymomode = true;
				panelIntro.removeAll();
				/* Instantiation */
				final GridBagLayout layout = new GridBagLayout();
				final GridBagConstraints c = new GridBagConstraints();

				panelIntro.setLayout(layout);

				final Label LoadtrackText = new Label("Load pre-processed tracking image");
				final Label LoadMeasureText = new Label("Load original image");
				final Label KymoText = new Label("Kymo mode (only 1 MT, pick Kymograph image) else skip");
				final Label StartText = new Label("Input Microscope parameters");
				final Checkbox Simple = new Checkbox("Run in Simple mode ", Simplemode);
				final Checkbox Advanced = new Checkbox("Run in Advanced mode ", Advancedmode);
				final Checkbox Kymochoice = new Checkbox("Analyze single MT, compare with Kymograph ", Kymomode);

				LoadtrackText.setBackground(new Color(1, 0, 1));
				LoadtrackText.setForeground(new Color(255, 255, 255));

				LoadMeasureText.setBackground(new Color(1, 0, 1));
				LoadMeasureText.setForeground(new Color(255, 255, 255));

				KymoText.setBackground(new Color(1, 0, 1));
				KymoText.setForeground(new Color(255, 255, 255));

				StartText.setBackground(new Color(1, 0, 1));
				StartText.setForeground(new Color(255, 255, 255));

				Track = new JButton("Optionally, Open Pre-processed movie (done internally if movie not supplied)");
				Measure = new JButton("Open UN-preprocessed movie performing measurments");
				Kymo = new JButton("Open Kymograph for the MT");
				Done = new JButton("Done");
				inputLabelX = new JLabel("Enter sigmaX of Microscope PSF (pixel units): ");
				inputFieldX = new TextField();
				inputFieldX.setColumns(10);

				inputLabelY = new JLabel("Enter sigmaY of Microscope PSF (pixel units): ");
				inputFieldY = new TextField();
				inputFieldY.setColumns(10);

				// inputLabelT = new JLabel("Enter time frame to second
				// conversion: ");
				// inputFieldT = new TextField();
				// inputFieldT.setColumns(2);

				/* Location */

				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridx = 0;
				c.gridy = 0;
				c.weightx = 1;
				c.weighty = 1.5;

				++c.gridy;
				c.insets = new Insets(10, 10, 10, 0);
				panelIntro.add(Simple, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 10, 0);
				panelIntro.add(Advanced, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 10, 0);
				panelIntro.add(LoadMeasureText, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 10, 0);
				panelIntro.add(Measure, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 10, 0);
				panelIntro.add(LoadtrackText, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 10, 0);
				panelIntro.add(Track, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 10, 0);
				panelIntro.add(KymoText, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 10, 0);
				panelIntro.add(Kymo, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 10, 0);
				panelIntro.add(StartText, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 10, 0);
				panelIntro.add(inputLabelX, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 10, 0);
				panelIntro.add(inputFieldX, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 10, 0);
				panelIntro.add(inputLabelY, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 10, 0);
				panelIntro.add(inputFieldY, c);

				/*
				 * ++c.gridy; c.insets = new Insets(10, 10, 10, 0);
				 * panelIntro.add(inputLabelT, c);
				 * 
				 * ++c.gridy; c.insets = new Insets(10, 10, 10, 0);
				 * panelIntro.add(inputFieldT, c);
				 */
				Kymochoice.addItemListener(new KymochoiceListener());
				++c.gridy;
				++c.gridy;
				++c.gridy;
				++c.gridy;
				c.insets = new Insets(10, 10, 10, 0);
				panelIntro.add(Done, c);
				panelIntro.setVisible(true);
				Track.addActionListener(new OpenTrackListener(frame));
				Measure.addActionListener(new MeasureListener(frame));

				Kymo.addActionListener(new KymoListener(frame));
				Done.addActionListener(new DoneButtonListener(frame, true));
				Simple.addItemListener(new RunSimpleListener());

				frame.addWindowListener(new FrameListener(frame));
				panelIntro.validate();
				panelIntro.repaint();

				frame.add(panelCont, BorderLayout.CENTER);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.pack();
				frame.setVisible(true);
			}

		}

	}

	protected class RunSimpleListener implements ItemListener {

		@Override
		public void itemStateChanged(final ItemEvent arg0) {

			if (arg0.getStateChange() == ItemEvent.DESELECTED) {
				Simplemode = false;
				Advancedmode = true;
			} else if (arg0.getStateChange() == ItemEvent.SELECTED) {
				Simplemode = true;
				Advancedmode = false;

			}

		}

	}

	protected class RunAdvancedListener implements ItemListener {

		@Override
		public void itemStateChanged(final ItemEvent arg0) {

			if (arg0.getStateChange() == ItemEvent.DESELECTED) {
				Advancedmode = false;
				Simplemode = true;
			} else if (arg0.getStateChange() == ItemEvent.SELECTED) {
				Advancedmode = true;
				Simplemode = false;
			}

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
			if (chooserB != null)
				chooserA.setCurrentDirectory(chooserB.getCurrentDirectory());
			else
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
				chooserA = null;
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
			if (chooserA != null)
				chooserB.setCurrentDirectory(chooserA.getCurrentDirectory());
			else
				chooserB.setCurrentDirectory(new java.io.File("."));
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
				chooserB = null;
			}

		}

	}

	protected class KymoListener implements ActionListener {

		final Frame parent;

		public KymoListener(Frame parent) {

			this.parent = parent;

		}

		@Override
		public void actionPerformed(final ActionEvent arg0) {

			int result;

			chooserC = new JFileChooser();
			if (chooserB != null)
				chooserC.setCurrentDirectory(chooserB.getCurrentDirectory());
			else if (chooserA != null)
				chooserC.setCurrentDirectory(chooserA.getCurrentDirectory());
			else
				chooserC.setCurrentDirectory(new java.io.File("."));
			chooserC.setDialogTitle(choosertitleC);
			chooserC.setFileSelectionMode(JFileChooser.FILES_ONLY);
			//
			// disable the "All files" option.
			//
			chooserC.setAcceptAllFileFilterUsed(false);
			//
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "tif");

			chooserC.setFileFilter(filter);

			if (chooserC.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
				System.out.println("getCurrentDirectory(): " + chooserC.getCurrentDirectory());
				System.out.println("getSelectedFile() : " + chooserC.getSelectedFile());
			} else {
				System.out.println("No Selection ");
				chooserC = null;
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

			if (Batchmoderun){
				
				
				
				
				new BatchMode(AllMovies, new Interactive_MTDoubleChannel(), AllMovies[0]).run(null);
				
				
				
			}
			else {

				ImagePlus impA = null;
				if (chooserA!= null){
					impA = new Opener().openImage(chooserA.getSelectedFile().getPath());
				}
				ImagePlus impB = new Opener().openImage(chooserB.getSelectedFile().getPath());
				ImagePlus impC = null;
				if (chooserC!= null)
					impC = new Opener().openImage(chooserC.getSelectedFile().getPath());
				// Tracking is done with imageA measurment is performed on both
				// the
				// images
				calibration[0] = impB.getCalibration().pixelWidth;
				calibration[1] = impB.getCalibration().pixelHeight;
				new Normalize();

				
				RandomAccessibleInterval<FloatType> originalimg = ImageJFunctions.convertFloat(impB);
			
				
				final FloatType type = originalimg.randomAccess().get().createVariable();
				final ImgFactory<FloatType> factory = Util.getArrayOrCellImgFactory(originalimg, type);
				RandomAccessibleInterval<FloatType> originalPreprocessedimg = factory.create(originalimg, type);

				if (impA != null)
					originalPreprocessedimg = ImageJFunctions.convertFloat(impA);
				else

					originalPreprocessedimg = Preprocess(originalimg);

				RandomAccessibleInterval<FloatType> kymoimg = null;
				if (impC != null)
					kymoimg = ImageJFunctions.convertFloat(impC);

				// Normalize image intnesity
				Normalize.normalize(Views.iterable(originalimg), minval, maxval);
				Normalize.normalize(Views.iterable(originalPreprocessedimg), minval, maxval);
				
				
				psf[0] = Float.parseFloat(inputFieldX.getText());
				psf[1] = Float.parseFloat(inputFieldY.getText());
				// frametosec = Float.parseFloat(inputFieldT.getText());
				ImageJFunctions.show(originalPreprocessedimg).setTitle("ProgramPreprocessed");

				if (kymoimg != null) {

					if (Simplemode)
						new Interactive_MTDoubleChannelBasic(new Interactive_MTDoubleChannel(originalimg,
								originalPreprocessedimg, kymoimg, psf, calibration, chooserB.getSelectedFile()))
										.run(null);
					else
						new Interactive_MTDoubleChannel(originalimg, originalPreprocessedimg, kymoimg, psf, calibration,
								chooserB.getSelectedFile()).run(null);

				} else {

					if (Simplemode)
						new Interactive_MTDoubleChannelBasic(new Interactive_MTDoubleChannel(originalimg,
								originalPreprocessedimg, psf, calibration, chooserB.getSelectedFile())).run(null);
					else
						new Interactive_MTDoubleChannel(originalimg, originalPreprocessedimg, psf, calibration,
								chooserB.getSelectedFile()).run(null);

				}
				close(parent);
			}
			
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

	public Dimension getPreferredSizeSmall() {
		return new Dimension(500, 200);
	}
	
}