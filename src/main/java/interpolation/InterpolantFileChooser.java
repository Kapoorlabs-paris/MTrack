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
package interpolation;

import java.awt.BorderLayout;
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
import ij.plugin.PlugIn;
import mt.Tracking;
import mt.listeners.InteractiveRANSAC;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;
import ransacBatch.BatchRANSAC;

public class InterpolantFileChooser extends JPanel  {

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
	JButton Track;
	String choosertitleA;
	boolean Batchmoderun = false;
	boolean Simplefile = false;
	File[] AllMovies;
	public NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
	
	public InterpolantFileChooser()  {

		final JFrame frame = new JFrame("Welcome to the Regression fits");

		Track = new JButton("Choose file");

		panelCont.add(panelIntro, "1");
		/* Instantiation */
		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints c = new GridBagConstraints();

		panelIntro.setLayout(layout);

		final Label LoadtrackText = new Label("Input the .txt file of time-series");

		LoadtrackText.setBackground(new Color(1, 0, 1));
		LoadtrackText.setForeground(new Color(255, 255, 255));
		final Checkbox Simplemode = new Checkbox("Open simple time-series file", Simplefile);
		/* Location */

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1.5;
	
		++c.gridy;
		c.insets = new Insets(10, 10, 10, 0);
		panelIntro.add(LoadtrackText, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 10, 0);
		panelIntro.add(Track, c);

		panelIntro.setVisible(true);
		Track.addActionListener(new OpenTrackListener(frame));
		Simplemode.addItemListener(new SimpleListener(frame));
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

		new InteractiveRegression(Tracking.loadsimple(new File(chooserA.getSelectedFile().getPath())),
				chooserA.getSelectedFile()).run(null);
		close(parent);

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
			Done(parent);
		}
	}

	public Dimension getPreferredSize() {
		return new Dimension(800, 300);
	}

	

}
