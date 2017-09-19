package mt;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import ij.ImageJ;
import ij.ImagePlus;
import ij.io.Opener;
import ij.measure.ResultsTable;
import mt.Tracking;
import mt.listeners.InteractiveRANSAC;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;
import ransacBatch.BatchRANSAC;

public class RansacFileChooser extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5915579156379418824L;
	/**
	 * 
	 */

	JPanel panelCont = new JPanel();
	JPanel panelIntro = new JPanel();
	JFileChooser chooserA;
	boolean wasDone = false;
	boolean isFinished = false;
	String choosertitleA;
	boolean Batchmoderun = false;
	boolean Simplefile = false;
	File[] AllMovies;
	private static final Insets insets = new Insets(10, 0, 0, 0);
	private JPanel PanelDirectory = new JPanel();
	public NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
	public final GridBagLayout layout = new GridBagLayout();
	public final GridBagConstraints c = new GridBagConstraints();
	
	Dimension dim = new Dimension(400, 400);
	
	public RansacFileChooser() {
		panelIntro.setPreferredSize(dim);
		PanelDirectory.setPreferredSize(dim);
		
		final JFrame frame = new JFrame("Welcome to the Ransac Part of MTV tracker");
		Border selectdirectory = new CompoundBorder(new TitledBorder("Load directory"),
				new EmptyBorder(c.insets));
		final Button Measureserial = new Button("Select directory of MTV tracker generated files");
		panelCont.add(panelIntro, "1");
	

		panelIntro.setLayout(layout);
		PanelDirectory.setLayout(layout);
		
		
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.BOTH;
		c.ipadx = 35;

		c.gridwidth = 10;
		c.gridheight = 10;
		c.gridy = 1;
		c.gridx = 0;
		
		PanelDirectory.add(Measureserial,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
	
		PanelDirectory.setBorder(selectdirectory);
		panelIntro.add(PanelDirectory,  new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.RELATIVE, new Insets(10, 10, 0, 10), 0, 0));
		
		

		panelIntro.setVisible(true);
		Measureserial.addActionListener(new MeasureserialListener(frame));
		frame.addWindowListener(new FrameListener(frame));
		frame.add(panelCont, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);

	}

	protected class SimpleListener implements ItemListener {

		final Frame parent;

		public SimpleListener(Frame parent) {

			this.parent = parent;

		}

		@Override
		public void itemStateChanged(ItemEvent e) {
			Simplefile = true;
			
		}
		
	}
	protected class RansacRuninBatchListener implements ItemListener {

		final Frame parent;

		public RansacRuninBatchListener(Frame parent) {

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

			final JFrame frame = new JFrame("Welcome to Ransac Rate Analyzer (Batch Mode)");
			Batchmoderun = true;

			JButton Done = new JButton("Exit");

			final Label LoadDirectoryText = new Label("Using Fiji Prefs Ransac fits are done on all MTV tracker generated files");

			LoadDirectoryText.setBackground(new Color(1, 0, 1));
			LoadDirectoryText.setForeground(new Color(255, 255, 255));

			JButton Measurebatch = new JButton("Run RANSAC fits on directory of files (Pre-set parameters)");
		
			
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
			
			Done.addActionListener(new DoneButtonListener(frame, true));
			panelIntro.validate();
			panelIntro.repaint();
			frame.addWindowListener(new FrameListener(frame));
			frame.add(panelCont, BorderLayout.CENTER);

			frame.pack();
			frame.setVisible(true);

		}

	}

	
	protected class MeasureserialListener implements ActionListener {

		final Frame parent;

		public MeasureserialListener(Frame parent) {

			this.parent = parent;

		}

		@Override
		public void actionPerformed(final ActionEvent arg0) {
			chooserA = new JFileChooser();

			chooserA.setCurrentDirectory(new java.io.File("."));
			chooserA.setDialogTitle(choosertitleA);
			chooserA.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			//
			// disable the "All files" option.
			//
			chooserA.setAcceptAllFileFilterUsed(false);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Rate Files", "txt");

			chooserA.setFileFilter(filter);
			chooserA.showOpenDialog(parent);

			AllMovies = chooserA.getSelectedFile().listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File pathname, String filename) {

					return (filename.endsWith(".txt") && !filename.contains("Rates") && !filename.contains("Average")
							&& !filename.contains("All"));
				}
			});

			
			new InteractiveRANSAC(AllMovies).run(null);
		parent.dispose();
			
		}
		
		
	}
	
	

	public void Singlefile() {

		File[] Averagefiles = chooserA.getSelectedFile().listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File pathname, String filename) {

				return (filename.endsWith(".txt") && !filename.contains("Rates") && filename.contains("Average")
						&& !filename.contains("All"));
			}
		});
		File singlefile = new File(
				chooserA.getSelectedFile() + "//" + "Final_Experimental_results" + "All" + ".txt");
		
		try {
			FileWriter fw = new FileWriter(singlefile);

			BufferedWriter bw = new BufferedWriter(fw);
			
			bw.write("\tAverageGrowthrate(px)\tAverageShrinkrate(px)\tCatastropheFrequency(px)\tRescueFrequency(px)\n");
			for (int i = 0; i < Averagefiles.length; ++i) {

				File file = Averagefiles[i];

				try {
					BufferedReader in = Util.openFileRead(file);

					while (in.ready()) {
						String line = in.readLine().trim();

						while (line.contains("\t\t"))
							line = line.replaceAll("\t\t", "\t");

						if (line.length() >= 3 && line.matches("[0-3].*")) {
							final String[] split = line.trim().split("\t");

							final double growthrate = Double.parseDouble(split[0]);
							final double shrinkrate = Double.parseDouble(split[1]);
							final double catfrequ = Double.parseDouble(split[2]);
							final double resfrequ = Double.parseDouble(split[3]);

							if(growthrate>0 || shrinkrate < 0 || catfrequ > 0 || resfrequ > 0)
							bw.write("\t" + (growthrate) + "\t" + "\t" + "\t" + "\t" + (shrinkrate)
									+ "\t" + "\t" + "\t" + (catfrequ) + "\t" + "\t" + "\t"
									+ (resfrequ)

									+ "\n" + "\n");

						}
					}
					
				}
				
				catch (Exception e) {
					e.printStackTrace();

				}
				

			}
			bw.close();
			fw.close();
		} 
		
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
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

	protected final void close(final Frame parent) {
		if (parent != null)
			parent.dispose();

		isFinished = true;
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
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");

			chooserA.setFileFilter(filter);
			//
			if (chooserA.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
				System.out.println("getCurrentDirectory(): " + chooserA.getCurrentDirectory());
				System.out.println("getSelectedFile() : " + chooserA.getSelectedFile());
			} else {
				System.out.println("No Selection ");
			}

			Done(parent);
		}

	}

	protected void Done(final Frame parent) {

		wasDone = true;

		if (!Batchmoderun)
			new InteractiveRANSAC(Tracking.loadMT(new File(chooserA.getSelectedFile().getPath())),
					chooserA.getSelectedFile()).run(null);
		
		if (Simplefile)
			new InteractiveRANSAC(Tracking.loadsimple(new File(chooserA.getSelectedFile().getPath())),
					chooserA.getSelectedFile()).run(null);
		

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

			if (!Batchmoderun)
				new InteractiveRANSAC(Tracking.loadMT(new File(chooserA.getSelectedFile().getPath())),
						chooserA.getSelectedFile()).run(null);
			if (Simplefile)
				new InteractiveRANSAC(Tracking.loadsimple(new File(chooserA.getSelectedFile().getPath())),
						chooserA.getSelectedFile()).run(null);
			
		}
	}

	public Dimension getPreferredSize() {
		return new Dimension(800, 300);
	}

}
