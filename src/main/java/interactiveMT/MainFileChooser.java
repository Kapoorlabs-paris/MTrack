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
package interactiveMT;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import ij.ImagePlus;
import ij.io.Opener;
import listeners.CalTListener;
import listeners.CalXListener;
import listeners.CalYListener;
import listeners.ChooseDirectoryListener;
import listeners.FileChooserDirectory;
import listeners.FilenameListener;
import listeners.FireTrigger;
import listeners.FirepreTrigger;
import listeners.FlatfieldTrigger;
import listeners.FlatfieldTriggerNext;
import listeners.LoadModuletrigger;
import listeners.MedianRadiListener;
import listeners.MedianfilterTrigger;
import listeners.PsfXListener;
import listeners.PsfYListener;
import listeners.SelfFirepreTrigger;
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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
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
	public int selectedindex;
	File[] AllMovies;
	public JFileChooser chooserA;
	public String choosertitleA;
	public RandomAccessibleInterval<FloatType> originalimg;
	public RandomAccessibleInterval<FloatType> originalPreprocessedimg;
	public RandomAccessibleInterval<FloatType> ProgramPreprocessedimg;
	public JFileChooser chooserB;
	public String choosertitleB;
	public double[] calibration = new double[3];
	public int medianradius = 2;
	float frametosec;
	public JProgressBar jpb;
	public String addToName = "MTTrack";
	JFileChooser chooserC;
	String choosertitleC;
	public double[] psf = new double[2];
	private Label inputLabelX, inputLabelY, inputLabelT, inputLabelcalX, inputLabelcalY, medianLabelradius;
	public TextField inputFieldX, inputFieldcalX;
	public TextField inputFieldY, inputFieldcalY, medianFieldradius;
	public TextField inputFieldT;
	JPanel panelCont = new JPanel();
	public JPanel panelIntro = new JPanel();
	public JPanel panelpreIntro = new JPanel();
	public File userfile;
	boolean loadpre = true;
	public boolean Simplemode = true;
	boolean Advancedmode = false;
	boolean Kymomode = false;
	boolean Loadpreimage = false;
	boolean Generatepre = false;
	boolean Batchmoderun = false;
	public TextField inputField = new TextField();
	public JLabel inputLabel = new JLabel("Filename:");
	public FloatType minval = new FloatType(0);
	public FloatType maxval = new FloatType(1);
	private static final Insets insets = new Insets(0, 0, 0, 0);

	public JPanel Modechoice = new JPanel();
	public JPanel Microscope = new JPanel();
	public JPanel Original = new JPanel();
	public JPanel Start = new JPanel();
	public JPanel Startsec = new JPanel();
	public JFrame frame = new JFrame("Welcome to MTrack ");
	public JPanel PreOriginal = new JPanel();
	JPanel controlnext = new JPanel();
	/* Instantiation */
	public GridBagLayout layout = new GridBagLayout();
	public GridBagConstraints c = new GridBagConstraints();
	final JButton FlatFieldNext = new JButton("Next\u22b3 ");
	
	JButton ChoosepreImage = new JButton("Load preprocessed movie and go next\u22b3");
	JLabel ChoosepreImagelabel = new JLabel("Optionally, load your preprocessed movie");
	
	final JButton ChooseDirectory = new JButton("Choose Directory to save results in");
   
	final Checkbox FlatField = new Checkbox("Do Flat Field Correction");
	final JButton MedianFilter = new JButton("Next\u22b3 ");
	JPanel controlprevious = new JPanel();
	Border runborder = new CompoundBorder(new TitledBorder("1.4 Preprocessing Options"), new EmptyBorder(c.insets));
	public MainFileChooser() {

		
		jpb = new JProgressBar();
		CardLayout cl = new CardLayout();
			
		inputField.setColumns(10);
		panelCont.setLayout(cl);
		panelCont.add(panelIntro, "1");
		panelCont.add(panelpreIntro, "2");
		c.insets = new Insets(5, 5, 5, 5);

		panelIntro.setLayout(layout);
		PreOriginal.setLayout(layout);
		panelpreIntro.setLayout(layout);
		Modechoice.setLayout(layout);
		Microscope.setLayout(layout);
		Original.setLayout(layout);
		Start.setLayout(layout);

		CheckboxGroup mode = new CheckboxGroup();

		final Checkbox Batchmode = new Checkbox("Batch mode", mode, Batchmoderun);
		final Checkbox Simple = new Checkbox("Simple mode ", mode, Simplemode);
		final Checkbox Advanced = new Checkbox("Advanced mode ", mode, Advancedmode);

		

		String[] preimage ={"Load preprocessed movie and begin tracking","Generate preprocessed movie and begin tracking " };
		

		Border border = new CompoundBorder(new TitledBorder("1.1 Choose Mode"), new EmptyBorder(c.insets));
		Border microborder = new CompoundBorder(new TitledBorder("1.3 Microscope Parameters"), new EmptyBorder(c.insets));
	
		Border origborder = new CompoundBorder(new TitledBorder("1.2 Open movie and enter filename for results files"), new EmptyBorder(c.insets));

		Border runbordersec = new CompoundBorder(new TitledBorder("1.4.1 Optionally, load your preprocessed movie"), new EmptyBorder(c.insets));

		

		Track = new JButton("Load pre-processed movie");
		Measure = new JButton("Open Un-preprocessed movie");
		Kymo = new JButton("Open Kymograph for the MT");
		Done = new JButton("Done");
		inputLabelX = new Label("Enter Sigma (X and Y) of PSF (in pixels): ");
		inputFieldX = new TextField(5);

		inputFieldX.setText("2");

	
		inputFieldY = new TextField(5);
		inputFieldY.setText("2");
		
		
		inputLabelcalX = new Label("Enter pixel calibration in X, Y (micrometers)");
		inputFieldcalX = new TextField(5);
		inputFieldcalX.setText("1");
		
		inputLabelcalY = new Label("Enter pixel calibration in Y");
		inputFieldcalY = new TextField(5);
		inputFieldcalY.setText("1");
		
		
	    inputLabelT = new Label("Enter time frame to second conversion: ");
		inputFieldT = new TextField(5);
		inputFieldT.setText("1");
		
		
		    medianLabelradius = new Label("Apply median filter with radius: ");
			medianFieldradius = new TextField(5);
			medianFieldradius.setText("0");
		
		
		psf[0] = Float.parseFloat(inputFieldX.getText());
		psf[1] = Float.parseFloat(inputFieldY.getText());
		calibration[0] = Float.parseFloat(inputFieldcalX.getText());
		calibration[1] = Float.parseFloat(inputFieldcalY.getText());
		calibration[2] = Float.parseFloat(inputFieldT.getText());

		String[] Imagetype = { "Two channel image as hyperstack", "Concatenated seed image followed by time-lapse images",
				"Single channel time-lapse images" };
		
		JComboBox<String> ChooseImage = new JComboBox<String>(Imagetype);
	

		controlnext.add(new JButton(new AbstractAction("Next\u22b3") {

		
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout) panelCont.getLayout();

				cl.next(panelCont);
				PreOriginal.setVisible(true);
				panelpreIntro.setVisible(true);
			}
		}));
		
		
		

		controlprevious.add(new JButton(new AbstractAction("\u22b2Prev") {

		
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout) panelCont.getLayout();

				cl.previous(panelCont);
			}
		}));
		
		/* Location */

		c.anchor = GridBagConstraints.BOTH;
		c.ipadx = 35;

		c.gridwidth = 10;
		c.gridheight = 10;
		c.gridy = 1;
		c.gridx = 0;

		Modechoice.add(Simple, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		Modechoice.add(Advanced, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		Modechoice.add(Batchmode, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		Modechoice.setBorder(border);
		Modechoice.setPreferredSize(new Dimension(200,200));
		Modechoice.setMinimumSize(new Dimension(200,200));
		panelIntro.add(Modechoice,new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
		Original.add(ChooseImage, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.RELATIVE, new Insets(10, 10, 0, 10), 0, 0));
		
		Original.add(inputLabel,  new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0) );
		Original.add(inputField,  new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0) );
		Original.add(ChooseDirectory,  new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0) );
	
		Original.setBorder(origborder);
		
		Original.setPreferredSize(new Dimension(500,200));
		Original.setMinimumSize(new Dimension(500,200));
		panelIntro.add(Original,new GridBagConstraints(3, 0, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));

		Microscope.add(inputLabelX, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.RELATIVE, insets, 0, 0));
		
		Microscope.add(inputFieldX, new GridBagConstraints(3, 4, 3, 1, 0.1, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.RELATIVE, insets, 0, 0));

		Microscope.add(inputFieldY, new GridBagConstraints(3, 4, 3, 1, 0.1, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.RELATIVE, insets, 0, 0));
		
		Microscope.add(inputLabelcalX, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.RELATIVE, insets, 0, 0));
		
		Microscope.add(inputFieldcalX, new GridBagConstraints(3, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.RELATIVE, insets, 0, 0));

		Microscope.add(inputFieldcalY, new GridBagConstraints(3, 0, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.RELATIVE, insets, 0, 0));
		
		Microscope.add(inputLabelT, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		
		Microscope.add(inputFieldT, new GridBagConstraints(3, 2, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.RELATIVE, insets, 0, 0));

		Microscope.setBorder(microborder);
		Microscope.setPreferredSize(new Dimension(500,200));
		Microscope.setMinimumSize(new Dimension(500,200));
		panelIntro.add(Microscope, new GridBagConstraints(1, 1, 5, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

	
		
	
	
		
		
		panelIntro.add(FlatFieldNext, new GridBagConstraints(3, 2, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.RELATIVE, new Insets(10, 10, 0, 10), 0, 0));
		
		panelIntro.setVisible(true);
		
		ChooseDirectory.addActionListener(new FileChooserDirectory(this));
		inputField.addTextListener(new FilenameListener(this));
		
		Batchmode.addItemListener(new RuninBatchListener(frame));
		Simple.addItemListener(new RunSimpleListener());
		Advanced.addItemListener(new RunAdvancedListener());
		ChooseImage.addActionListener(new FireTrigger(this, ChooseImage));
		FlatField.addItemListener(new FlatfieldTrigger(this));
		MedianFilter.addActionListener(new MedianfilterTrigger(this));
		FlatFieldNext.addActionListener(new FlatfieldTriggerNext(this));
		
		ChoosepreImage.addActionListener(new FirepreTrigger(this));
		inputFieldX.addTextListener(new PsfXListener(this));
		inputFieldY.addTextListener(new PsfYListener(this));
		inputFieldcalX.addTextListener(new CalXListener(this));
		inputFieldcalY.addTextListener(new CalYListener(this));
		inputFieldT.addTextListener(new CalTListener(this));
		medianFieldradius.addTextListener(new MedianRadiListener(this));
		
		
		frame.addWindowListener(new FrameListener(frame));
		frame.add(panelCont, BorderLayout.CENTER);
		frame.add(jpb, BorderLayout.PAGE_END);
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
			panelIntro.revalidate();
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
			chooserB.setAcceptAllFileFilterUsed(false);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "tif", "nd2");

			chooserB.setFileFilter(filter);
			chooserB.showOpenDialog(parent);

			AllMovies = chooserB.getSelectedFile().listFiles(new FilenameFilter() {
				
				@Override
				public boolean accept(File pathname, String filename) {
					
					return filename.endsWith(".tif");
				}
			});
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

		final FlatFieldCorrection flatfilter = new FlatFieldCorrection(originalimg, 1, psf);
		flatfilter.process();
		RandomAccessibleInterval<FloatType> ProgramPreprocessedimg = flatfilter.getResult();
		return ProgramPreprocessedimg;

	}

	public RandomAccessibleInterval<FloatType> Preprocess(IntervalView<FloatType> originalimg) {

		final FlatFieldCorrection flatfilter = new FlatFieldCorrection(originalimg, 1, psf);
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
				
				panelIntro.add(FlatFieldNext, new GridBagConstraints(3, 2, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.RELATIVE, new Insets(10, 10, 0, 10), 0, 0));
				panelIntro.remove(controlnext);
				panelIntro.repaint();
				panelIntro.validate();
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
				panelIntro.setPreferredSize(panelIntro.getPreferredSize());
				panelIntro.add(controlnext, new GridBagConstraints(3, 2, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.RELATIVE, new Insets(10, 10, 0, 10), 0, 0));
				
				
				
				PreOriginal.add(FlatField,  new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0) );
				
				
				PreOriginal.add(medianLabelradius, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

						PreOriginal.add(medianFieldradius, new GridBagConstraints(4, 3, 3, 1, 0.1, 0.0, GridBagConstraints.NORTH,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
						
				PreOriginal.add(MedianFilter, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));


			

				
				PreOriginal.add(ChoosepreImage, new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

				panelpreIntro.add(PreOriginal,  new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.NORTH,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
				
				panelpreIntro.add(controlprevious, new GridBagConstraints(0, 6, 5, 1, 0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
				panelpreIntro.setPreferredSize(panelIntro.getPreferredSize());
				PreOriginal.setBorder(runborder);
			
				PreOriginal.setVisible(false);
				panelpreIntro.setVisible(false);
				
				
			panelIntro.remove(FlatFieldNext);
			panelIntro.repaint();
			panelIntro.validate();
				 
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

				if (impA != null)
					assert (impA.getDimensions() == impB.getDimensions());

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
						if (impA != null)
							impA.setDimensions(nChannels, nFrames, nSlices);
						break;
					case JOptionPane.CANCEL_OPTION:
						return;

					}

				}

				// Tracking is done with imageA measurment is performed on
				// imageB
				calibration[0] = Float.parseFloat(inputFieldcalX.getText());
				calibration[1] = Float.parseFloat(inputFieldcalY.getText());
				calibration[2] = Float.parseFloat(inputFieldT.getText());
				psf[0] = Float.parseFloat(inputFieldX.getText());
				psf[1] = Float.parseFloat(inputFieldY.getText());
				new Normalize();

				RandomAccessibleInterval<FloatType> originalimg = ImageJFunctions.convertFloat(impB);

				final FloatType type = originalimg.randomAccess().get().createVariable();
				final ImgFactory<FloatType> factory = Util.getArrayOrCellImgFactory(originalimg, type);
				RandomAccessibleInterval<FloatType> originalPreprocessedimg = factory.create(originalimg, type);

				if (impA != null)
					originalPreprocessedimg = ImageJFunctions.convertFloat(impA);
				else
					originalPreprocessedimg = null;
				// Normalize image intnesity
				Normalize.normalize(Views.iterable(originalimg), minval, maxval);

				if (nChannels > 1) {

					switch (JOptionPane.showConfirmDialog(null,
							"Image has " + nChannels + "Channels, Is seed image in channel 1?\n", " ",
							JOptionPane.YES_NO_CANCEL_OPTION)) {

					case JOptionPane.YES_OPTION:
						// Do concetation
						RandomAccessibleInterval<FloatType> seedimgStack = Views.hyperSlice(originalimg, 2, 0);

						RandomAccessibleInterval<FloatType> dynamicimgStack = Views.hyperSlice(originalimg, 2, 1);

						long[] dim = { dynamicimgStack.dimension(0), dynamicimgStack.dimension(1),
								dynamicimgStack.dimension(2) };
						RandomAccessibleInterval<FloatType> totalimg = factory.create(dim, type);
						RandomAccessibleInterval<FloatType> pretotalimg = factory.create(dim, type);
						final long nz = dynamicimgStack.dimension(2);

						IntervalView<FloatType> slice = Views.hyperSlice(seedimgStack, 2, 0);
						IntervalView<FloatType> outputSlice = Views.hyperSlice(totalimg, 2, 0);

						processSlice(slice, outputSlice);
						for (long z = 1; z < nz; z++) {
							slice = Views.hyperSlice(dynamicimgStack, 2, z);
							outputSlice = Views.hyperSlice(totalimg, 2, z);

							processSlice(slice, outputSlice);
						}
						IntervalView<FloatType> preoutputSlice;
						if (originalPreprocessedimg != null) {

							RandomAccessibleInterval<FloatType> dynamicgpreimgStack = Views
									.hyperSlice(originalPreprocessedimg, 2, 1);

							preoutputSlice = Views.hyperSlice(pretotalimg, 2, 0);

							RandomAccessibleInterval<FloatType> seedimgpreStack = Views
									.hyperSlice(originalPreprocessedimg, 2, 0);

							IntervalView<FloatType> preslice = Views.hyperSlice(seedimgpreStack, 2, 0);

							processSlice(preslice, preoutputSlice);

							for (long z = 1; z < nz; z++) {

								preslice = Views.hyperSlice(dynamicgpreimgStack, 2, z);
								preoutputSlice = Views.hyperSlice(pretotalimg, 2, z);

								processSlice(preslice, preoutputSlice);

							}
							Normalize.normalize(Views.iterable(totalimg), minval, maxval);
							ImageJFunctions.show(pretotalimg).setTitle("Preprocessed Movie");
						}

						else {

							preoutputSlice = (IntervalView<FloatType>) Preprocess(outputSlice);
							Normalize.normalize(Views.iterable(pretotalimg), minval, maxval);
							ImageJFunctions.show(pretotalimg).setTitle("Preprocessed Movie");
						}

						Normalize.normalize(Views.iterable(totalimg), minval, maxval);

						if (Simplemode)
							new Interactive_MTDoubleChannelBasic(new Interactive_MTDoubleChannel(totalimg, pretotalimg,
									psf, calibration, userfile, addToName)).run(null);
						else
							new Interactive_MTDoubleChannel(totalimg, pretotalimg, psf, calibration,
									userfile, addToName).run(null);

						break;

					case JOptionPane.NO_OPTION:
						// Do concetation
						seedimgStack = Views.hyperSlice(originalimg, 2, 1);

						dynamicimgStack = Views.hyperSlice(originalimg, 2, 0);

						long[] dimsec = { dynamicimgStack.dimension(0), dynamicimgStack.dimension(1),
								dynamicimgStack.dimension(2) };

						totalimg = factory.create(dimsec, type);
						pretotalimg = factory.create(dimsec, type);
						long nzsec = dynamicimgStack.dimension(2);

						slice = Views.hyperSlice(seedimgStack, 2, 0);
						outputSlice = Views.hyperSlice(totalimg, 2, 0);

						processSlice(slice, outputSlice);
						for (long z = 1; z < nzsec; z++) {
							slice = Views.hyperSlice(dynamicimgStack, 2, z);
							outputSlice = Views.hyperSlice(totalimg, 2, z);

							processSlice(slice, outputSlice);
						}
						if (originalPreprocessedimg != null) {

							RandomAccessibleInterval<FloatType> dynamicgpreimgStack = Views
									.hyperSlice(originalPreprocessedimg, 2, 0);

							preoutputSlice = Views.hyperSlice(pretotalimg, 2, 0);

							RandomAccessibleInterval<FloatType> seedimgpreStack = Views
									.hyperSlice(originalPreprocessedimg, 2, 1);

							IntervalView<FloatType> preslice = Views.hyperSlice(seedimgpreStack, 2, 0);

							processSlice(preslice, preoutputSlice);

							for (long z = 1; z < nzsec; z++) {

								preslice = Views.hyperSlice(dynamicgpreimgStack, 2, z);
								preoutputSlice = Views.hyperSlice(pretotalimg, 2, z);

								processSlice(preslice, preoutputSlice);

							}
							Normalize.normalize(Views.iterable(pretotalimg), minval, maxval);
							ImageJFunctions.show(pretotalimg).setTitle("Preprocessed Movie");
						}

						else {

							preoutputSlice = (IntervalView<FloatType>) Preprocess(outputSlice);
							Normalize.normalize(Views.iterable(pretotalimg), minval, maxval);
							ImageJFunctions.show(pretotalimg).setTitle("Preprocessed Movie");
						}

						Normalize.normalize(Views.iterable(totalimg), minval, maxval);

						if (Simplemode)
							new Interactive_MTDoubleChannelBasic(new Interactive_MTDoubleChannel(totalimg, pretotalimg,
									psf, calibration, userfile, addToName)).run(null);
						else
							new Interactive_MTDoubleChannel(totalimg, pretotalimg, psf, calibration,
									userfile, addToName).run(null);

						break;

					case JOptionPane.CANCEL_OPTION:

						return;

					}
				}

				else {
					if (impA != null)
						originalPreprocessedimg = ImageJFunctions.convertFloat(impA);
					else

						originalPreprocessedimg = Preprocess(originalimg);
					Normalize.normalize(Views.iterable(originalPreprocessedimg), minval, maxval);
					switch (JOptionPane.showConfirmDialog(null, "Is this a double channel image?", "",
							JOptionPane.YES_NO_CANCEL_OPTION)) {

					case JOptionPane.YES_OPTION:
						// Put constructor for double channel
						ImageJFunctions.show(originalPreprocessedimg).setTitle("Preprocessed Movie");
						if (Simplemode)
							new Interactive_MTDoubleChannelBasic(new Interactive_MTDoubleChannel(originalimg,
									originalPreprocessedimg, psf, calibration, userfile, addToName)).run(null);
						else
							new Interactive_MTDoubleChannel(originalimg, originalPreprocessedimg, psf, calibration,
									userfile, addToName).run(null);
						break;

					case JOptionPane.NO_OPTION:
						// Put constructor for single channel
						ImageJFunctions.show(originalPreprocessedimg).setTitle("Preprocessed Movie");
						if (Simplemode)

							new Interactive_MTSingleChannelBasic(new Interactive_MTSingleChannel(originalimg,
									originalPreprocessedimg, psf, calibration, userfile, addToName)).run(null);
						else
							new Interactive_MTSingleChannel(originalimg, originalPreprocessedimg, psf, calibration,
									userfile, addToName).run(null);

						break;

					case JOptionPane.CANCEL_OPTION:

						return;

					}

				}

			}

			// frametosec = Float.parseFloat(inputFieldT.getText());

			close(parent);

		}
	}

	public void processSlice(RandomAccessibleInterval<FloatType> slice, IterableInterval<FloatType> outputSlice) {

		final Cursor<FloatType> cursor = outputSlice.localizingCursor();
		final RandomAccess<FloatType> ranac = slice.randomAccess();

		while (cursor.hasNext()) {

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
