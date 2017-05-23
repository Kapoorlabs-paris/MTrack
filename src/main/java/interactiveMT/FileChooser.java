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
import java.awt.*;
import java.util.*;

public class FileChooser extends JPanel {
	boolean wasDone = false;
	boolean isFinished = false;
	JButton Track;
	JButton Measure;
	JButton Kymo;
	JButton Done;
	JFileChooser chooserA;
	String choosertitleA;

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
	boolean Simplemode = false;
	boolean Advancedmode = false;
	boolean Kymomode = false;
	
	public FileChooser() {
		final JFrame frame = new JFrame("Welcome to MTV Tracker");
		
		panelCont.add(panelIntro, "1");
		/* Instantiation */
		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints c = new GridBagConstraints();

		panelIntro.setLayout(layout);
		
		
		final Label LoadtrackText = new Label("Load pre-processed tracking image");
		final Label LoadMeasureText = new Label("Load original image");
		final Label KymoText = new Label("Kymo mode (only 1 MT, pick Kymograph image) else skip");
		final Label StartText = new Label("Input Microscope parameters");
		final Checkbox Simple = new Checkbox("Run in Simple mode ",  Simplemode);
		final Checkbox Advanced = new Checkbox("Run in Advanced mode ",  Advancedmode);
		final Checkbox Kymochoice = new Checkbox("Analyze single MT, compare with Kymograph ",  Kymomode);
		
		LoadtrackText.setBackground(new Color(1, 0, 1));
		LoadtrackText.setForeground(new Color(255, 255, 255));

		LoadMeasureText.setBackground(new Color(1, 0, 1));
		LoadMeasureText.setForeground(new Color(255, 255, 255));
		
		KymoText.setBackground(new Color(1, 0, 1));
		KymoText.setForeground(new Color(255, 255, 255));
		
		StartText.setBackground(new Color(1, 0, 1));
		StartText.setForeground(new Color(255, 255, 255));

		Track = new JButton("Open Pre-processed movie");
		Measure = new JButton("Open UN-preprocessed movie performing measurments");
		Kymo = new JButton("Open Kymograph for the MT");
		Done = new JButton("Done");
		inputLabelX = new JLabel("Enter sigmaX of Microscope PSF (pixel units): ");
		inputFieldX = new TextField();
		inputFieldX.setColumns(10);

		inputLabelY = new JLabel("Enter sigmaY of Microscope PSF (pixel units): ");
		inputFieldY = new TextField();
		inputFieldY.setColumns(10);

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
		c.insets = new Insets(10, 10, 10, 0);
		panelIntro.add(Kymochoice, c);
		
		++c.gridy;
		c.insets = new Insets(10, 10, 10, 0);
		panelIntro.add(Simple, c);
		
		
		++c.gridy;
		c.insets = new Insets(10, 10, 10, 0);
		panelIntro.add(Advanced, c);
		
		
		
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
		++c.gridy;
		c.insets = new Insets(10, 10, 10, 0);
		panelIntro.add(inputLabelT, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 10, 0);
		panelIntro.add(inputFieldT, c);
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
		
		Done.addActionListener(new DoneButtonListener(frame, true));
		Simple.addItemListener(new RunSimpleListener());
		frame.addWindowListener(new FrameListener(frame));
		frame.add(panelCont, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.pack();
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

	protected class KymochoiceListener implements ItemListener{
		
		@Override
		public void itemStateChanged(final ItemEvent arg0) {

			
			if (arg0.getStateChange() == ItemEvent.SELECTED){
				
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
				final Checkbox Simple = new Checkbox("Run in Simple mode ",  Simplemode);
				final Checkbox Advanced = new Checkbox("Run in Advanced mode ",  Advancedmode);
				final Checkbox Kymochoice = new Checkbox("Analyze single MT, compare with Kymograph ",  Kymomode);
				
				LoadtrackText.setBackground(new Color(1, 0, 1));
				LoadtrackText.setForeground(new Color(255, 255, 255));

				LoadMeasureText.setBackground(new Color(1, 0, 1));
				LoadMeasureText.setForeground(new Color(255, 255, 255));
				
				KymoText.setBackground(new Color(1, 0, 1));
				KymoText.setForeground(new Color(255, 255, 255));
				
				StartText.setBackground(new Color(1, 0, 1));
				StartText.setForeground(new Color(255, 255, 255));

				Track = new JButton("Open Pre-processed movie");
				Measure = new JButton("Open UN-preprocessed movie performing measurments");
				Kymo = new JButton("Open Kymograph for the MT");
				Done = new JButton("Done");
				inputLabelX = new JLabel("Enter sigmaX of Microscope PSF (pixel units): ");
				inputFieldX = new TextField();
				inputFieldX.setColumns(10);

				inputLabelY = new JLabel("Enter sigmaY of Microscope PSF (pixel units): ");
				inputFieldY = new TextField();
				inputFieldY.setColumns(10);

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
				c.insets = new Insets(10, 10, 10, 0);
				panelIntro.add(Kymochoice, c);
				
				++c.gridy;
				c.insets = new Insets(10, 10, 10, 0);
				panelIntro.add(Simple, c);
				
				
				++c.gridy;
				c.insets = new Insets(10, 10, 10, 0);
				panelIntro.add(Advanced, c);
				
				
				
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
				++c.gridy;
				c.insets = new Insets(10, 10, 10, 0);
				panelIntro.add(inputLabelT, c);

				++c.gridy;
				c.insets = new Insets(10, 10, 10, 0);
				panelIntro.add(inputFieldT, c);
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
	protected class RunSimpleListener implements ItemListener{
		
		
		@Override
		public void itemStateChanged(final ItemEvent arg0) {

			if (arg0.getStateChange() == ItemEvent.DESELECTED){
				Simplemode = false;
			    Advancedmode = true;	
			}
			else if (arg0.getStateChange() == ItemEvent.SELECTED){
				Simplemode = true;
			    Advancedmode = false;	
			    
			   
			}
		
		}
		
	}
	
	/*
	 * 
	 * Joke:
	 * 
	 *  ArrayList<Component> theList = new ArrayList<>();
			    addAllChildren(panelIntro, theList);
			    for (Component c : theList)
			    	
			    	c.setBackground(Color.MAGENTA);
			    
	 * 
	 * 
	 * 
	 */
	
	public static void addAllChildren(Component comp, ArrayList<Component> thelist)
	{
		thelist.add(comp);
		if (comp instanceof Container)
				for (Component child : ((Container) comp).getComponents())
				{
					addAllChildren(child, thelist);
				}
	}
	
	protected class RunAdvancedListener implements ItemListener{
		
		
		@Override
		public void itemStateChanged(final ItemEvent arg0) {

			if (arg0.getStateChange() == ItemEvent.DESELECTED){
				Advancedmode = false;
			    Simplemode = true;	
			}
			else if (arg0.getStateChange() == ItemEvent.SELECTED){
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

	protected class KymoListener implements ActionListener {

		final Frame parent;

		public KymoListener(Frame parent) {

			this.parent = parent;

		}

		@Override
		public void actionPerformed(final ActionEvent arg0) {

			int result;

			chooserC = new JFileChooser();
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
			ImagePlus impC = null;
			if(chooserC!=null)
			impC = new Opener().openImage(chooserC.getSelectedFile().getPath());
			// Tracking is done with imageA measurment is performed on both the
			// images
			calibration[0] = impA.getCalibration().pixelWidth;
			calibration[1] = impA.getCalibration().pixelHeight;

			RandomAccessibleInterval<FloatType> originalPreprocessedimg = ImageJFunctions.convertFloat(impA);
			RandomAccessibleInterval<FloatType> originalimg = ImageJFunctions.convertFloat(impB);
			RandomAccessibleInterval<FloatType> kymoimg = null;
			if (impC!=null)
			kymoimg = ImageJFunctions.convertFloat(impC);

			new Normalize();

			FloatType minval = new FloatType(0);
			FloatType maxval = new FloatType(1);
			Normalize.normalize(Views.iterable(originalimg), minval, maxval);
			Normalize.normalize(Views.iterable(originalPreprocessedimg), minval, maxval);
			psf[0] = Float.parseFloat(inputFieldX.getText());
			psf[1] = Float.parseFloat(inputFieldY.getText());
		//	frametosec = Float.parseFloat(inputFieldT.getText());

			if (kymoimg!=null){
				
				
			   
			    if(Simplemode)
			    new Interactive_MTDoubleChannelBasic(new Interactive_MTDoubleChannel(originalimg, originalPreprocessedimg, kymoimg, psf, calibration,chooserB.getSelectedFile())).run(null);	
			    else
			    new Interactive_MTDoubleChannel(originalimg, originalPreprocessedimg, kymoimg, psf, calibration, chooserB.getSelectedFile()).run(null);	
			
			}
			else{

				
				if(Simplemode)
				new Interactive_MTDoubleChannelBasic(new Interactive_MTDoubleChannel(originalimg, originalPreprocessedimg, psf, calibration, chooserB.getSelectedFile())).run(null);
				else
				new Interactive_MTDoubleChannel(originalimg, originalPreprocessedimg, psf, calibration, chooserB.getSelectedFile()).run(null);		
			
			}
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
