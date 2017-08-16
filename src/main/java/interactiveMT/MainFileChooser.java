package interactiveMT;

import javax.swing.*;
import ij.ImagePlus;
import ij.io.Opener;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.stats.Normalize;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Util;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;
import preProcessing.FlatFieldCorrection;
import java.awt.event.*;
import java.io.File;
import java.awt.*;

public class MainFileChooser extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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

	public MainFileChooser() {
		final JFrame frame = new JFrame("Welcome to MTV Tracker ");

		panelCont.add(panelIntro, "1");
		/* Instantiation */
		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints c = new GridBagConstraints();

		panelIntro.setLayout(layout);
		CheckboxGroup mode = new CheckboxGroup();
		final Checkbox Batchmode = new Checkbox("Run in Batch Mode", mode, Batchmoderun);
		final Label LoadtrackText = new Label("Load pre-processed tracking image");
		final Label LoadMeasureText = new Label("Load original image");
		final Label StartText = new Label("Input Microscope parameters");

		final Checkbox Simple = new Checkbox("Run in Simple mode ", mode, Simplemode);
		final Checkbox Advanced = new Checkbox("Run in Advanced mode ", mode, Advancedmode);

		LoadtrackText.setBackground(new Color(1, 0, 1));
		LoadtrackText.setForeground(new Color(255, 255, 255));

		LoadMeasureText.setBackground(new Color(1, 0, 1));
		LoadMeasureText.setForeground(new Color(255, 255, 255));

		StartText.setBackground(new Color(1, 0, 1));
		StartText.setForeground(new Color(255, 255, 255));

		Track = new JButton("Optionally, Open Pre-processed movie (Done internally if movie not supplied)");
		Measure = new JButton("Open UN-preprocessed movie for performing measurments");
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

			chooserB = new JFileChooser();
			if (chooserA != null)
				chooserB.setCurrentDirectory(chooserA.getCurrentDirectory());
			else
				chooserB.setCurrentDirectory(new java.io.File("."));
			chooserB.setDialogTitle(choosertitleB);
			chooserB.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

			chooserB.showOpenDialog(parent);

			AllMovies = chooserB.getSelectedFile().listFiles();

			new BatchMode(AllMovies, new Interactive_MTDoubleChannel(), AllMovies[0]).run(null);
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

			chooserA = new JFileChooser();
			if (chooserB != null)
				chooserA.setCurrentDirectory(chooserB.getCurrentDirectory());
			else
				chooserA.setCurrentDirectory(new java.io.File("."));
			chooserA.setDialogTitle(choosertitleA);
			chooserA.setFileSelectionMode(JFileChooser.FILES_ONLY);

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

			chooserB = new JFileChooser();
			if (chooserA != null)
				chooserB.setCurrentDirectory(chooserA.getCurrentDirectory());
			else
				chooserB.setCurrentDirectory(new java.io.File("."));
			chooserB.setDialogTitle(choosertitleB);
			chooserB.setFileSelectionMode(JFileChooser.FILES_ONLY);

			if (chooserB.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
				System.out.println("getCurrentDirectory(): " + chooserB.getCurrentDirectory());
				System.out.println("getSelectedFile() : " + chooserB.getSelectedFile());
			} else {
				System.out.println("No Selection ");
				chooserB = null;
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

			if (Batchmoderun) {

				new BatchMode(AllMovies, new Interactive_MTDoubleChannel(), AllMovies[0]).run(null);

			} else {

				// Preprocessed image
				ImagePlus impA = null;
				if (chooserA != null) {
					
					impA = new Opener().openImage(chooserA.getSelectedFile().getPath());

				}
				// Actual image
				ImagePlus impB = new Opener().openImage(chooserB.getSelectedFile().getPath());
				
				if (impA!=null)
					assert(impA.getDimensions() == impB.getDimensions());
				
				int nChannels = impB.getNChannels();
				int nSlices = impB.getStackSize();
				int nFrames = impB.getNFrames();

				// Stupid user did not know they had slices instead of frames
				if (nFrames == 1 && nSlices > 1) {

					switch (JOptionPane.showConfirmDialog(null,
							"It appears this image has 1 timepoint but " + nSlices + " slices.\n"
									+ "Do you want to swap Z and T?",
							"Z/T swapped?", JOptionPane.YES_NO_CANCEL_OPTION)) {

					case JOptionPane.YES_OPTION:
						impB.setDimensions(nChannels, nFrames, nSlices);
						if (impA!=null)
						impA.setDimensions(nChannels, nFrames, nSlices);
						break;
					case JOptionPane.CANCEL_OPTION:
						return;

					}

				}
				
				
				// Tracking is done with imageA measurment is performed on imageB
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

			

				// Normalize image intnesity
				Normalize.normalize(Views.iterable(originalimg), minval, maxval);
				Normalize.normalize(Views.iterable(originalPreprocessedimg), minval, maxval);

				if (nChannels > 1){
					
					
					switch (JOptionPane.showConfirmDialog(null,
							"It appears this image has " + nChannels + ".\n"
									, "Is seed image in channel 1? ", JOptionPane.YES_NO_CANCEL_OPTION)) {

					case JOptionPane.YES_OPTION:
						// Do concetation
						RandomAccessibleInterval<FloatType> seedimgStack = Views.hyperSlice(originalimg, 4 , 0);
						RandomAccessibleInterval<FloatType> seedimgpreStack = Views.hyperSlice(originalPreprocessedimg, 4 , 0);
						
					
						
						RandomAccessibleInterval<FloatType> dynamicimgStack = Views.hyperSlice(originalimg, 4 , 1);
						RandomAccessibleInterval<FloatType> dynamicgpreimgStack = Views.hyperSlice(originalPreprocessedimg, 4 , 1);
						
						
						long[] dim = { dynamicimgStack.dimension(0), dynamicimgStack.dimension(1), dynamicimgStack.dimension(2) };
						RandomAccessibleInterval<FloatType> totalimg = factory.create(dim, type);
						RandomAccessibleInterval<FloatType> pretotalimg = factory.create(dim, type);
						final long nz = totalimg.dimension( 2 );
						
						
						IntervalView< FloatType > slice = Views.hyperSlice( seedimgStack, 2, 0 );
						IntervalView< FloatType > outputSlice = Views.hyperSlice( totalimg, 2, 0 );

						IntervalView< FloatType > preslice = Views.hyperSlice( seedimgpreStack, 2, 0 );
						IntervalView< FloatType > preoutputSlice = Views.hyperSlice( pretotalimg, 2, 0 );
						
						
						processSlice( slice, outputSlice );
						processSlice( preslice, preoutputSlice );
						for ( long z = 1; z < nz; z++ )
						{
							slice = Views.hyperSlice( dynamicimgStack, 2, z );
							outputSlice = Views.hyperSlice( totalimg, 2, z );
						   

							preslice = Views.hyperSlice( dynamicgpreimgStack, 2, 0 );
							preoutputSlice = Views.hyperSlice( pretotalimg, 2, 0 );
							
							processSlice( slice, outputSlice );
							processSlice( preslice, preoutputSlice );
						}
						if (Simplemode)
							new Interactive_MTDoubleChannelBasic(new Interactive_MTDoubleChannel(outputSlice,
									preoutputSlice, psf, calibration, chooserB.getSelectedFile())).run(null);
						else
							new Interactive_MTDoubleChannel(outputSlice, preoutputSlice, psf, calibration,
									chooserB.getSelectedFile()).run(null);
						
						break;
					case JOptionPane.CANCEL_OPTION:
						// Choose single or double channel
						
						
						switch (JOptionPane.showConfirmDialog(null,
								"Is this a double channel image?","" , JOptionPane.YES_NO_CANCEL_OPTION)) {
						
										case JOptionPane.YES_OPTION:
											// Put constructor for double channel
											if (Simplemode)
												new Interactive_MTDoubleChannelBasic(new Interactive_MTDoubleChannel(originalimg,
														originalPreprocessedimg, psf, calibration, chooserB.getSelectedFile())).run(null);
											else
												new Interactive_MTDoubleChannel(originalimg, originalPreprocessedimg, psf, calibration,
														chooserB.getSelectedFile()).run(null);
											break;
											
										case JOptionPane.CANCEL_OPTION:
											// Put constructor for single channel
											if (Simplemode)
												
												new Interactive_MTSingleChannelBasic(new Interactive_MTSingleChannel(originalimg,
														originalPreprocessedimg, psf, calibration, chooserB.getSelectedFile())).run(null);
											else
												new Interactive_MTSingleChannel(originalimg, originalPreprocessedimg, psf, calibration,
														chooserB.getSelectedFile()).run(null);
											
											break;
											
						
						
						
						}
						
						
						
						
						return;

					}

					
					
					
					
				}
				
				
			
				psf[0] = Float.parseFloat(inputFieldX.getText());
				psf[1] = Float.parseFloat(inputFieldY.getText());
				// frametosec = Float.parseFloat(inputFieldT.getText());
				ImageJFunctions.show(originalPreprocessedimg).setTitle("ProgramPreprocessed");

			
					if (Simplemode)
						new Interactive_MTDoubleChannelBasic(new Interactive_MTDoubleChannel(originalimg,
								originalPreprocessedimg, psf, calibration, chooserB.getSelectedFile())).run(null);
					else
						new Interactive_MTDoubleChannel(originalimg, originalPreprocessedimg, psf, calibration,
								chooserB.getSelectedFile()).run(null);

				
				close(parent);
			}

		}
	}

	
	protected void processSlice(RandomAccessibleInterval<FloatType> slice, IterableInterval<FloatType> outputSlice){
		
		final Cursor< FloatType > cursor = outputSlice.localizingCursor();
		final RandomAccess< FloatType > ranac = slice.randomAccess();
		
		while(cursor.hasNext()){
			
			
			cursor.fwd();
			
			
			ranac.setPosition(cursor);
			
			cursor.get().set(ranac.get());
			
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