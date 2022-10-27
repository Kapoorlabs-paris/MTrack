/*-
 * #%L
 * Microtubule tracker.
 * %%
 * Copyright (C) 2017 - 2022 MTrack developers.
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
package mt.listeners;

import ij.IJ;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.TextField;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;

import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.XYShapeRenderer;
import org.jfree.chart.util.ShapeUtils;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;

import comboListeners.ErrorListener;
import comboListeners.ErrorLocListener;
import comboListeners.MaxDistListener;
import comboListeners.MaxDistLocListener;
import comboListeners.MaxSlopeListener;
import comboListeners.MaxSlopeLocListener;
import comboListeners.MinInlierListener;
import comboListeners.MinInlierLocListener;
import comboListeners.MinSlopeListener;
import comboListeners.MinSlopeLocListener;
import comboListeners.SliderBoxGUI;
import fit.AbstractFunction2D;
import fit.PointFunctionMatch;
import fit.polynomial.HigherOrderPolynomialFunction;
import fit.polynomial.InterpolatedPolynomial;
import fit.polynomial.LinearFunction;
import fit.polynomial.Polynomial;
import fit.polynomial.QuadraticFunction;
import mpicbg.models.Point;
import mt.Averagerate;
import mt.RansacFileChooser;
import mt.Rateobject;
import mt.Tracking;

public class InteractiveRANSAC implements PlugIn {
	public static int MIN_SLIDER = 0;
	public static int MAX_SLIDER = 100;

	public float MIN_ERROR = 0.0f;
	public float MAX_ERROR = 100.0f;

	public float MIN_Inlier = 0.0f;
	public float MAX_Inlier = 100.0f;

	public float MIN_Gap = 0.0f;
	public float MAX_Gap = 1000.0f;

	public static double MIN_RES = 1.0;
	public static double MAX_RES = 100.0;

	private Label inputLabelT;
	private Label inputLabelTcont;
	public TextField inputFieldT, maxErrorField, minInlierField, maxGapField, maxSlopeField, minSlopeField;
	public float MAX_ABS_SLOPE = 360.0f;
	public float MIN_ABS_SLOPE = -360.0f;
	public static double MIN_CAT = 0.0;
	public static double MAX_CAT = 100.0;
	public File inputfile;
	public File[] inputfiles;
	public double[] calibrations;
	public String inputdirectory;
	public NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);

	public ArrayList<Pair<LinearFunction, ArrayList<PointFunctionMatch>>> linearlist;
	Frame jFreeChartFrame;
	public int functionChoice = 0; // 0 == Linear, 1 == Quadratic interpolated,
									// 2 ==
									// cubic interpolated
	AbstractFunction2D function;
	public double lambda;
	ArrayList<Pair<Integer, Double>> mts;
	ArrayList<Point> points;
	public final int numTimepoints, minTP, maxTP;
	public static boolean wrongfile = false;
	public static Pair<Boolean, Integer> wrongfileindex;
	public static HashMap<Integer, Boolean> wrongfileindexlist;
	public int previousrow = 0;
	public int countfile;
	Scrollbar lambdaSB;
	Label lambdaLabel;
	ArrayList<Pair<LinearFunction, ArrayList<PointFunctionMatch>>> negsegments;
	public boolean manualcat = false;
	int framenumber = 1;
	final XYSeriesCollection dataset;
	final JFreeChart chart;
	// final SVGGraphics2D svgchart;
	int updateCount = 0;
	public static ArrayList<Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>>> segments;
	public HashMap<Integer, Pair<Double, Double>> indexedsegments;
	public HashMap<Integer, LinearFunction> linearsegments;
	public ArrayList<Rateobject> allrates;
	public ArrayList<Averagerate> averagerates;

	public HashMap<Integer, ArrayList<Rateobject>> Compilepositiverates;
	public HashMap<Integer, ArrayList<Rateobject>> Compilenegativerates;
	public HashMap<Integer, Averagerate> Compileaverage;

	public
	// for scrollbars
	int maxErrorInt, lambdaInt, minSlopeInt, maxSlopeInt, minDistCatInt, restoleranceInt;

	public float maxError = 1.8f;
	public final int scrollbarSize = 1000;
	public float minSlope = 1;
	public float maxSlope = 100;
	public DecimalFormat df = new DecimalFormat("#.###");
	public double restolerance = 5;
	public double tptolerance = 2;
	public int maxDist = 300;
	public int minInliers = 10;
	public boolean detectCatastrophe = false;
	public boolean detectmanualCatastrophe = false;
	ArrayList<Pair<Integer, Double>> lifecount;
	public double minDistanceCatastrophe = 2;
	public final boolean serial;
	File[] AllMovies;

	ArrayList<File> AllMoviesB;
	protected boolean wasCanceled = false;

	public InteractiveRANSAC(final ArrayList<Pair<Integer, Double>> mts, File file) {
		this(mts, 0, 300, 3.0f, 0.1f, 10.0f, 10, 50, 1, 0.1, file);
		nf.setMaximumFractionDigits(3);
	}

	public InteractiveRANSAC(File[] file) {
		this(0, 300, 3.0f, 0.1f, 10.0f, 10, 50, 1, 0.1, file);
		nf.setMaximumFractionDigits(3);
	}

	public InteractiveRANSAC() {
		this(0, 300, 3.0f, 0.1f, 10.0f, 10, 50, 1, 0.1, null);
		nf.setMaximumFractionDigits(3);
	}

	public InteractiveRANSAC(final ArrayList<Pair<Integer, Double>> mts, final int minTP, final int maxTP,
			final float maxError, final float minSlope, final float maxSlope, final int maxDist, final int minInliers,
			final int functionChoice, final double lambda, final File file) {
		this.minTP = minTP;
		this.maxTP = maxTP;
		this.numTimepoints = maxTP - minTP + 1;
		this.functionChoice = functionChoice;
		this.lambda = lambda;
		this.mts = mts;
		this.points = Tracking.toPoints(mts);
		this.inputfile = file;
		this.inputdirectory = file.getParent();
		this.minSlope = minSlope;
		this.maxSlope = maxSlope;
		this.maxDist = Math.min(maxDist, numTimepoints);

		this.serial = false;
		if (this.minSlope >= this.maxSlope)
			this.minSlope = this.maxSlope - 0.1f;

		this.dataset = new XYSeriesCollection();
		this.chart = Tracking.makeChart(dataset, "Microtubule Length Plot", "Timepoint", "MT Length");
		// this.svgchart = new SVGGraphics2D(500, 500);
		this.jFreeChartFrame = Tracking.display(chart, new Dimension(500, 500));

		// this.chart.draw(svgchart, new Rectangle2D.Double(0, 0, 500, 500),
		// null);
	};

	public InteractiveRANSAC(final int minTP, final int maxTP, final float maxError, final float minSlope,
			final float maxSlope, final int maxDist, final int minInliers, final int functionChoice,
			final double lambda, final File[] file) {
		this.minTP = minTP;
		this.maxTP = maxTP;
		this.numTimepoints = maxTP - minTP + 1;
		this.functionChoice = functionChoice;
		this.lambda = lambda;
		this.mts = null;
		this.points = null;
		if (file != null) {
			this.inputfiles = file;
			this.inputdirectory = file[0].getParent();
		}
		this.minSlope = minSlope;
		this.maxSlope = maxSlope;
		this.maxDist = Math.min(maxDist, numTimepoints);

		this.serial = true;
		if (this.minSlope >= this.maxSlope)
			this.minSlope = this.maxSlope - 0.1f;

		this.dataset = new XYSeriesCollection();
		this.chart = Tracking.makeChart(dataset, "Microtubule Length Plot", "Timepoint", "MT Length");
		// this.svgchart = new SVGGraphics2D(500, 500);
		this.jFreeChartFrame = Tracking.display(chart, new Dimension(500, 500));
		// this.chart.draw(svgchart, new Rectangle2D.Double(0, 0, 500, 500),
		// null);
	};

	@Override
	public void run(String arg) {
		/* JFreeChart */
		allrates = new ArrayList<Rateobject>();
		averagerates = new ArrayList<Averagerate>();
		wrongfileindexlist = new HashMap<Integer, Boolean>();
		Compilepositiverates = new HashMap<Integer, ArrayList<Rateobject>>();
		Compilenegativerates = new HashMap<Integer, ArrayList<Rateobject>>();
		Compileaverage = new HashMap<Integer, Averagerate>();

		calibrations = new double[3];
		lifecount = new ArrayList<Pair<Integer, Double>>();
		countfile = 0;
		AllMoviesB = new ArrayList<File>();

		segments = new ArrayList<Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>>>();
		negsegments = new ArrayList<Pair<LinearFunction, ArrayList<PointFunctionMatch>>>();
		indexedsegments = new HashMap<Integer, Pair<Double, Double>>();
		linearsegments = new HashMap<Integer, LinearFunction>();
		if (serial) {

			CardTable();

		}

	}

	public JFrame Cardframe = new JFrame("Welcome to Ransac velocity and Statistics Analyzer ");
	public JPanel panelCont = new JPanel();
	public JPanel panelFirst = new JPanel();
	public JPanel panelSecond = new JPanel();
	JPanel PanelDirectory = new JPanel();
	private JPanel PanelParameteroptions = new JPanel();
	private JPanel PanelSavetoFile = new JPanel();
	public JPanel Panelfunction = new JPanel();
	private JPanel Panelslope = new JPanel();
	private JPanel PanelCompileRes = new JPanel();
	public JTable table;
	JFileChooser chooserA;
	String choosertitleA;
	public int row;
	public JScrollPane scrollPane;
	public Insets insets = new Insets(10, 0, 0, 0);

	public final GridBagLayout layout = new GridBagLayout();
	public final GridBagConstraints c = new GridBagConstraints();
	Border selectparam = new CompoundBorder(new TitledBorder("Select Ransac parameters"), new EmptyBorder(c.insets));
	Border selectslope = new CompoundBorder(new TitledBorder("Slope constraints"), new EmptyBorder(c.insets));

	Border selectfunction = new CompoundBorder(new TitledBorder("Function for inlier detection"),
			new EmptyBorder(c.insets));

	Border compileres = new CompoundBorder(new TitledBorder("Compile results"), new EmptyBorder(c.insets));

	Border selectdirectory = new CompoundBorder(new TitledBorder("Load directory"), new EmptyBorder(c.insets));

	public String errorstring = "Maximum Error (px)";
	public String inlierstring = "Minimum No. of timepoints (tp)";
	public String maxgapstring = "Maximum Gap (tp)";
	public String maxslopestring = "Max. Segment Slope (px/tp)";
	public String minslopestring = "Min. Segment Slope (px/tp)";

	public int SizeX = 500;
	public int SizeY = 400;
	public JScrollBar maxErrorSB = new JScrollBar(Scrollbar.HORIZONTAL, (int) this.maxError, 10, 0, 10 + scrollbarSize);
	public JScrollBar minInliersSB = new JScrollBar(Scrollbar.HORIZONTAL, (int) this.minInliers, 10, 0,
			10 + scrollbarSize);
	public JScrollBar maxDistSB = new JScrollBar(Scrollbar.HORIZONTAL, (int) this.maxDist, 10, 0, 10 + scrollbarSize);
	public JScrollBar minSlopeSB = new JScrollBar(Scrollbar.HORIZONTAL, (int) this.minSlope, 10, 0, 10 + scrollbarSize);
	public JScrollBar maxSlopeSB = new JScrollBar(Scrollbar.HORIZONTAL, (int) this.maxSlope, 10, 0, 10 + scrollbarSize);

	public Label maxErrorLabel = new Label(
			"Maximum Error (px) = " + new DecimalFormat("#.##").format(this.maxError) + "      ", Label.CENTER);
	public Label minInliersLabel = new Label(
			"Minimum No. of timepoints (tp) = " + new DecimalFormat("#.##").format(this.minInliers), Label.CENTER);
	public Label maxDistLabel = new Label("Maximum Gap (tp) = " + new DecimalFormat("#.##").format(this.maxDist),
			Label.CENTER);

	public Label minSlopeLabel = new Label(
			"Min. Segment Slope (px/tp) = " + new DecimalFormat("#.##").format(this.minSlope), Label.CENTER);
	public Label maxSlopeLabel = new Label(
			"Max. Segment Slope (px/tp) = " + new DecimalFormat("#.##").format(this.maxSlope), Label.CENTER);
	public Label maxResLabel = new Label(
			"MT is rescued if the start of event# i + 1 > start of event# i by px =  " + this.restolerance,
			Label.CENTER);

	public void CardTable() {
		this.lambdaSB = new Scrollbar(Scrollbar.HORIZONTAL, this.lambdaInt, 1, MIN_SLIDER, MAX_SLIDER + 1);

		maxSlopeSB.setValue(utility.Slicer.computeScrollbarPositionFromValue((float) maxSlope, (float) MIN_ABS_SLOPE,
				(float) MAX_ABS_SLOPE, scrollbarSize));

		maxDistSB.setValue(utility.Slicer.computeScrollbarPositionFromValue(maxDist, MIN_Gap, MAX_Gap, scrollbarSize));

		minInliersSB.setValue(
				utility.Slicer.computeScrollbarPositionFromValue(minInliers, MIN_Inlier, MAX_Inlier, scrollbarSize));
		maxErrorSB.setValue(
				utility.Slicer.computeScrollbarPositionFromValue(maxError, MIN_ERROR, MAX_ERROR, scrollbarSize));

		minSlopeSB.setValue(utility.Slicer.computeScrollbarPositionFromValue((float) minSlope, (float) MIN_ABS_SLOPE,
				(float) MAX_ABS_SLOPE, scrollbarSize));

		lambdaLabel = new Label("Linearity (fraction) = " + new DecimalFormat("#.##").format(lambda), Label.CENTER);
		maxErrorLabel = new Label("Maximum Error (px) = " + new DecimalFormat("#.##").format(maxError) + "      ",
				Label.CENTER);
		minInliersLabel = new Label("Minimum No. of timepoints (tp) = " + new DecimalFormat("#.##").format(minInliers),
				Label.CENTER);
		maxDistLabel = new Label("Maximum Gap (tp) = " + new DecimalFormat("#.##").format(maxDist), Label.CENTER);

		minSlopeLabel = new Label("Min. Segment Slope (px/tp) = " + new DecimalFormat("#.##").format(minSlope),
				Label.CENTER);
		maxSlopeLabel = new Label("Max. Segment Slope (px/tp) = " + new DecimalFormat("#.##").format(maxSlope),
				Label.CENTER);
		maxResLabel = new Label(
				"MT is rescued if the start of event# i + 1 > start of event# i by px =  " + this.restolerance,
				Label.CENTER);

		CardLayout cl = new CardLayout();
		Object[] colnames = new Object[] { "Track File", "Growth velocity", "Shrink velocity", "Growth events",
				"Shrink events", "fcat", "fres", "Error" };

		Object[][] rowvalues = new Object[0][colnames.length];

		if (inputfiles != null) {
			rowvalues = new Object[inputfiles.length][colnames.length];
			for (int i = 0; i < inputfiles.length; ++i) {

				rowvalues[i][0] = inputfiles[i].getName();
			}
		}

		table = new JTable(rowvalues, colnames);

		table.setFillsViewportHeight(true);

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		table.isOpaque();
		int size = 100;
		table.getColumnModel().getColumn(0).setPreferredWidth(size);
		table.getColumnModel().getColumn(1).setPreferredWidth(size);
		table.getColumnModel().getColumn(2).setPreferredWidth(size);
		table.getColumnModel().getColumn(3).setPreferredWidth(size);
		table.getColumnModel().getColumn(4).setPreferredWidth(size);
		table.getColumnModel().getColumn(5).setPreferredWidth(size);
		table.getColumnModel().getColumn(6).setPreferredWidth(size);
		table.getColumnModel().getColumn(7).setPreferredWidth(size);
		maxErrorField = new TextField(5);
		maxErrorField.setText(Float.toString(maxError));

		minInlierField = new TextField(5);
		minInlierField.setText(Float.toString(minInliers));

		maxGapField = new TextField(5);
		maxGapField.setText(Float.toString(maxDist));

		maxSlopeField = new TextField(5);
		maxSlopeField.setText(Float.toString(maxSlope));

		minSlopeField = new TextField(5);
		minSlopeField.setText(Float.toString(minSlope));

		maxErrorSB.setSize(new Dimension(SizeX, 20));
		minSlopeSB.setSize(new Dimension(SizeX, 20));
		minInliersSB.setSize(new Dimension(SizeX, 20));
		maxDistSB.setSize(new Dimension(SizeX, 20));
		maxSlopeSB.setSize(new Dimension(SizeX, 20));
		maxErrorField.setSize(new Dimension(SizeX, 20));
		minInlierField.setSize(new Dimension(SizeX, 20));
		maxGapField.setSize(new Dimension(SizeX, 20));
		minSlopeField.setSize(new Dimension(SizeX, 20));
		maxSlopeField.setSize(new Dimension(SizeX, 20));

		scrollPane = new JScrollPane(table);
		scrollPane.setMinimumSize(new Dimension(300, 200));
		scrollPane.setPreferredSize(new Dimension(300, 200));

		scrollPane.getViewport().add(table);
		scrollPane.setAutoscrolls(true);

		// Location
		panelFirst.setLayout(layout);
		panelSecond.setLayout(layout);
		PanelSavetoFile.setLayout(layout);
		PanelParameteroptions.setLayout(layout);
		Panelfunction.setLayout(layout);
		Panelslope.setLayout(layout);
		PanelCompileRes.setLayout(layout);
		PanelDirectory.setLayout(layout);

		panelCont.setLayout(cl);

		panelCont.add(panelFirst, "1");
		panelCont.add(panelSecond, "2");

		panelFirst.setName("Ransac fits for rates and frequency analysis");
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.BOTH;
		c.ipadx = 35;

		inputLabelT = new Label("Compute length distribution at time: ");
		inputLabelTcont = new Label("(Press Enter to start computation) ");
		inputFieldT = new TextField(5);
		inputFieldT.setText("1");

		c.gridwidth = 10;
		c.gridheight = 10;
		c.gridy = 1;
		c.gridx = 0;

		String[] Method = { "Linear Function only", "Linearized Quadratic function", "Linearized Cubic function" };
		JComboBox<String> ChooseMethod = new JComboBox<String>(Method);

		final Checkbox findCatastrophe = new Checkbox("Detect Catastrophies", this.detectCatastrophe);
		final Checkbox findmanualCatastrophe = new Checkbox("Detect Catastrophies without fit",
				this.detectmanualCatastrophe);
		final Scrollbar minCatDist = new Scrollbar(Scrollbar.HORIZONTAL, this.minDistCatInt, 1, MIN_SLIDER,
				MAX_SLIDER + 1);
		final Scrollbar maxRes = new Scrollbar(Scrollbar.HORIZONTAL, this.restoleranceInt, 1, MIN_SLIDER,
				MAX_SLIDER + 1);
		final Label minCatDistLabel = new Label("Min. Catastrophy height (tp) = " + this.minDistanceCatastrophe,
				Label.CENTER);
		final Button done = new Button("Done");
		final Button batch = new Button("Save Parameters for Batch run");
		final Button cancel = new Button("Cancel");
		final Button Compile = new Button("Compute rates and freq. till current file");
		final Button AutoCompile = new Button("Auto Compute Velocity and Frequencies");
		final Button Measureserial = new Button("Select directory of MTrack generated files");
		final Button WriteLength = new Button("Compute length distribution at framenumber : ");
		final Button WriteStats = new Button("Compute lifetime and mean length distribution");
		final Button WriteAgain = new Button("Save Velocity and Frequencies to File");

		PanelDirectory.add(Measureserial, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		PanelDirectory.add(scrollPane, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		PanelDirectory.setBorder(selectdirectory);
		PanelDirectory.setPreferredSize(new Dimension(SizeX, SizeY));
		panelFirst.add(PanelDirectory, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		SliderBoxGUI combomaxerror = new SliderBoxGUI(errorstring, maxErrorSB, maxErrorField, maxErrorLabel,
				scrollbarSize, maxError, MAX_ERROR);

		PanelParameteroptions.add(combomaxerror.BuildDisplay(), new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		SliderBoxGUI combomininlier = new SliderBoxGUI(inlierstring, minInliersSB, minInlierField, minInliersLabel,
				scrollbarSize, minInliers, MAX_Inlier);

		PanelParameteroptions.add(combomininlier.BuildDisplay(), new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		SliderBoxGUI combomaxdist = new SliderBoxGUI(maxgapstring, maxDistSB, maxGapField, maxDistLabel, scrollbarSize,
				maxDist, MAX_Gap);

		PanelParameteroptions.add(combomaxdist.BuildDisplay(), new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		PanelParameteroptions.add(ChooseMethod, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		PanelParameteroptions.add(lambdaSB, new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		PanelParameteroptions.add(lambdaLabel, new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		PanelParameteroptions.setPreferredSize(new Dimension(SizeX, SizeY));
		PanelParameteroptions.setBorder(selectparam);
		panelFirst.add(PanelParameteroptions, new GridBagConstraints(3, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		SliderBoxGUI combominslope = new SliderBoxGUI(minslopestring, minSlopeSB, minSlopeField, minSlopeLabel,
				scrollbarSize, (float) minSlope, (float) MAX_ABS_SLOPE);

		Panelslope.add(combominslope.BuildDisplay(), new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		SliderBoxGUI combomaxslope = new SliderBoxGUI(maxslopestring, maxSlopeSB, maxSlopeField, maxSlopeLabel,
				scrollbarSize, (float) maxSlope, (float) MAX_ABS_SLOPE);

		Panelslope.add(combomaxslope.BuildDisplay(), new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Panelslope.add(findCatastrophe, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		Panelslope.add(findmanualCatastrophe, new GridBagConstraints(4, 4, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		Panelslope.add(minCatDist, new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		Panelslope.add(minCatDistLabel, new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		Panelslope.setBorder(selectslope);
		Panelslope.setPreferredSize(new Dimension(SizeX, SizeY));

		panelFirst.add(Panelslope, new GridBagConstraints(3, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		PanelCompileRes.add(AutoCompile, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		PanelCompileRes.add(WriteLength, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		PanelCompileRes.add(inputFieldT, new GridBagConstraints(3, 1, 3, 1, 0.1, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		PanelCompileRes.add(WriteStats, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		PanelCompileRes.setPreferredSize(new Dimension(SizeX, SizeY));

		PanelCompileRes.setBorder(compileres);

		panelFirst.add(PanelCompileRes, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		if (inputfiles != null) {

			table.addMouseListener(new MouseAdapter() {

				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() >= 1) {

						if (!jFreeChartFrame.isVisible())
							jFreeChartFrame = Tracking.display(chart, new Dimension(500, 500));
						JTable target = (JTable) e.getSource();
						row = target.getSelectedRow();
						// do some action if appropriate column
						if (row > 0)
							displayclicked(row);
						else
							displayclicked(0);
					}
				}
			});
		}

		maxErrorSB.addAdjustmentListener(
				new ErrorListener(this, maxErrorLabel, errorstring, MIN_ERROR, MAX_ERROR, scrollbarSize, maxErrorSB));

		minInliersSB.addAdjustmentListener(new MinInlierListener(this, minInliersLabel, inlierstring, MIN_Inlier,
				MAX_Inlier, scrollbarSize, minInliersSB));

		maxDistSB.addAdjustmentListener(
				new MaxDistListener(this, maxDistLabel, maxgapstring, MIN_Gap, MAX_Gap, scrollbarSize, maxDistSB));

		ChooseMethod.addActionListener(new FunctionItemListener(this, ChooseMethod));
		lambdaSB.addAdjustmentListener(new LambdaListener(this, lambdaLabel, lambdaSB));
		minSlopeSB.addAdjustmentListener(new MinSlopeListener(this, minSlopeLabel, minslopestring,
				(float) MIN_ABS_SLOPE, (float) MAX_ABS_SLOPE, scrollbarSize, minSlopeSB));

		maxSlopeSB.addAdjustmentListener(new MaxSlopeListener(this, maxSlopeLabel, maxslopestring,
				(float) MIN_ABS_SLOPE, (float) MAX_ABS_SLOPE, scrollbarSize, maxSlopeSB));
		findCatastrophe
				.addItemListener(new CatastrophyCheckBoxListener(this, findCatastrophe, minCatDistLabel, minCatDist));
		findmanualCatastrophe.addItemListener(
				new ManualCatastrophyCheckBoxListener(this, findmanualCatastrophe, minCatDistLabel, minCatDist));
		minCatDist.addAdjustmentListener(new MinCatastrophyDistanceListener(this, minCatDistLabel, minCatDist));
		Measureserial.addActionListener(new MeasureserialListener(this));
		Compile.addActionListener(new CompileResultsListener(this));
		AutoCompile.addActionListener(new AutoCompileResultsListener(this));
		WriteLength.addActionListener(new WriteLengthListener(this));
		WriteStats.addActionListener(new WriteStatsListener(this));
		WriteAgain.addActionListener(new WriteRatesListener(this));
		done.addActionListener(new FinishButtonListener(this, false));
		batch.addActionListener(new RansacBatchmodeListener(this));
		cancel.addActionListener(new FinishButtonListener(this, true));
		inputFieldT.addTextListener(new LengthdistroListener(this));

		maxSlopeField.addTextListener(new MaxSlopeLocListener(this, false));
		minSlopeField.addTextListener(new MinSlopeLocListener(this, false));
		maxErrorField.addTextListener(new ErrorLocListener(this, false));
		minInlierField.addTextListener(new MinInlierLocListener(this, false));
		maxGapField.addTextListener(new MaxDistLocListener(this, false));

		panelFirst.setVisible(true);
		functionChoice = 0;

		cl.show(panelCont, "1");

		Cardframe.add(panelCont, BorderLayout.CENTER);

		setFunction();
		Cardframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Cardframe.pack();
		Cardframe.setVisible(true);
		Cardframe.pack();

	}

	CompileRes compile = new CompileRes(this);

	public void displayclicked(final int trackindex) {

		if (!jFreeChartFrame.isVisible())
			jFreeChartFrame = Tracking.display(chart, new Dimension(500, 500));
		this.inputfile = this.inputfiles[trackindex];
		this.inputdirectory = this.inputfiles[trackindex].getParent();
		this.mts = Tracking.loadMT(this.inputfiles[trackindex]);
		if (mts != null) {
			if (mts.size() > 5) {
				this.points = Tracking.toPoints(mts);
				this.calibrations = Tracking.loadCalibration(this.inputfiles[trackindex]);

				linearlist = new ArrayList<Pair<LinearFunction, ArrayList<PointFunctionMatch>>>();
				dataset.removeAllSeries();
				this.dataset.addSeries(Tracking.drawPoints(mts, calibrations));
				Tracking.setColor(chart, 0, new Color(64, 64, 64));
				Tracking.setStroke(chart, 0, 0.2f);
				row = trackindex;
				setFunction();
				updateRANSAC();
				compile.compileresults();
			} else {
				IJ.log("Warning:  Loading an empty file");

				chart.setTitle("Bad File: No Donut for you");
				dataset.removeAllSeries();
				Tracking.setColor(chart, 0, new Color(64, 64, 64));
				Tracking.setStroke(chart, 0, 0.2f);

			}
		}

	}

	public void setFunction() {
		if (functionChoice == 0) {
			this.function = new LinearFunction();
			this.setLambdaEnabled(false);
		} else if (functionChoice == 1) {
			this.setLambdaEnabled(true);
			this.function = new InterpolatedPolynomial<LinearFunction, QuadraticFunction>(new LinearFunction(),
					new QuadraticFunction(), 1 - this.lambda);
		} else {
			this.setLambdaEnabled(true);
			this.function = new InterpolatedPolynomial<LinearFunction, HigherOrderPolynomialFunction>(
					new LinearFunction(), new HigherOrderPolynomialFunction(3), 1 - this.lambda);
		}

	}

	public void setLambdaEnabled(final boolean state) {
		if (state) {
			if (!lambdaSB.isEnabled()) {
				lambdaSB.setEnabled(true);
				lambdaLabel.setEnabled(true);
				lambdaLabel.setForeground(Color.BLACK);
			}
		} else {
			if (lambdaSB.isEnabled()) {
				lambdaSB.setEnabled(false);
				lambdaLabel.setEnabled(false);
				lambdaLabel.setForeground(Color.GRAY);
			}
		}
	}

	int negcount = 0;
	double negtimediff = 0;
	double averageshrink = 0;
	int i = 1, segment = 1;

	int catindex = 0;

	public void updateRANSAC() {

		negcount = 0;
		negtimediff = 0;
		averageshrink = 0;
		catindex = 0;
		negsegments = new ArrayList<Pair<LinearFunction, ArrayList<PointFunctionMatch>>>();
		ArrayList<Rateobject> allrates = new ArrayList<Rateobject>();
		ArrayList<Averagerate> averagerates = new ArrayList<Averagerate>();

		++updateCount;

		dataset.removeAllSeries();
		this.dataset.addSeries(Tracking.drawPoints(mts, calibrations));

		@SuppressWarnings("unchecked")
		ArrayList<Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>>> segments = Tracking.findAllFunctions(points,
				function, maxError, minInliers, maxDist);

		if (segments == null || segments.size() == 0) {
			--updateCount;
			return;
		}

		// sort the segments according to time HORIZONTAL to each other and the
		// PointFunctionMatches internally
		sort(segments);

		final LinearFunction linear = new LinearFunction();
		int linearcount = 1;
		i = 1;
		segment = 1;
		int count = 0;

		int rescount = 0;
		int catcount = 0;
		double timediff = 0;
		double restimediff = 0;

		double averagegrowth = 0;

		double growthrate = 0;
		double shrinkrate = 0;

		double minstartY = leastStart(segments);

		double minstartX = Double.MAX_VALUE;
		double minendX = Double.MAX_VALUE;
		double catfrequ = 0;
		double resfrequ = 0;
		double lifetime = 0;

		ArrayList<Double> previousendX = new ArrayList<Double>();
		ResultsTable rt = new ResultsTable();
		ResultsTable rtAll = new ResultsTable();

		List<Pair<Float, Float>> starttimerates = new ArrayList<Pair<Float, Float>>();
		List<Pair<Float, Float>> catstarttimerates = new ArrayList<Pair<Float, Float>>();
		for (final Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>> result : segments) {
			if (LinearFunction.slopeFits(result.getB(), linear, minSlope, maxSlope)) {

				final Pair<Double, Double> minMax = Tracking.fromTo(result.getB());

				double startX = minMax.getA();
				double endX = minMax.getB();

				if (startX < minstartX) {

					minstartX = startX;
				}
				if (endX < minendX) {

					minendX = endX;
				}

				Polynomial<?, Point> polynomial = (Polynomial) result.getA();

				dataset.addSeries(
						Tracking.drawFunction(polynomial, minMax.getA(), minMax.getB(), 0.5, "Segment " + segment));

				if (functionChoice > 0) {
					Tracking.setColor(chart, i, new Color(255, 0, 0));
					Tracking.setDisplayType(chart, i, true, false);
					Tracking.setStroke(chart, i, 0.5f);
					chart.setTitle("Length plot for" + " " + this.inputfiles[row].getName());
				} else {
					Tracking.setColor(chart, i, new Color(0, 128, 0));
					Tracking.setDisplayType(chart, i, true, false);
					Tracking.setStroke(chart, i, 2f);
					chart.setTitle("Length plot for" + " " + this.inputfiles[row].getName());
				}

				++i;

				if (functionChoice > 0) {

					dataset.addSeries(Tracking.drawFunction(linear, minMax.getA(), minMax.getB(), 0.5,
							"Linear Segment " + segment));

					Tracking.setColor(chart, i, new Color(0, 128, 0));
					Tracking.setDisplayType(chart, i, true, false);
					Tracking.setStroke(chart, i, 2f);

					++i;

				}

				double startY = polynomial.predict(startX);
				double linearrate = linear.getCoefficient(1);
				if (linearrate > 0 && startY - minstartY > restolerance && previousendX.size() > 0) {
					rescount++;
					restimediff += -previousendX.get(previousendX.size() - 1) + startX;

				}

				if (linearrate > 0) {

					count++;
					growthrate = linearrate;
					// Ignore last growth event for getting fcat
					if (points.get(points.size() - 1).getW()[0] - endX >= tptolerance)
						catcount++;
					timediff += endX - startX;
					lifetime = endX - startX;
					averagegrowth += linearrate;
					lifecount.add(new ValuePair<Integer, Double>(count, lifetime));

					Rateobject velocity = new Rateobject(linearrate * calibrations[0] / calibrations[2],
							(int) (startX * calibrations[2]), (int) (endX * calibrations[2]));
					allrates.add(velocity);
					rt.incrementCounter();
					rt.addValue("Start time", startX * calibrations[2]);
					rt.addValue("End time", endX * calibrations[2]);
					rt.addValue("Growth velocity", linearrate * calibrations[0] / calibrations[2]);

					Pair<Float, Float> startrate = new ValuePair<Float, Float>((float) startX, (float) linearrate);

					starttimerates.add(startrate);
				}
				if (linearrate < 0) {

					negcount++;
					negtimediff += endX - startX;

					shrinkrate = linearrate;
					averageshrink += linearrate;

					rt.incrementCounter();
					rt.addValue("Start time", startX * calibrations[2]);
					rt.addValue("End time", endX * calibrations[2]);
					rt.addValue("Growth velocity", linearrate * calibrations[0] / calibrations[2]);

					Pair<Float, Float> startrate = new ValuePair<Float, Float>((float) startX, (float) linearrate);

					starttimerates.add(startrate);

				}
				if (linearrate > 0) {
					previousendX.add(endX);

				}
				dataset.addSeries(
						Tracking.drawPoints(Tracking.toPairList(result.getB()), calibrations, "Inliers " + segment));

				Tracking.setColor(chart, i, new Color(255, 0, 0));
				Tracking.setDisplayType(chart, i, false, true);
				Tracking.setSmallUpTriangleShape(chart, i);

				++i;
				++segment;

			} else {
				System.out.println("Removed segment because slope is wrong.");

			}

		}

		if (this.detectCatastrophe) {

			if (segments.size() < 2) {

				System.out.println("Only two points found");

			} else {
				for (int catastrophy = 0; catastrophy < segments.size() - 1; ++catastrophy) {
					final Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>> start = segments.get(catastrophy);
					final Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>> end = segments.get(catastrophy + 1);

					double tStart = start.getB().get(start.getB().size() - 1).getP1().getL()[0];
					double tEnd = end.getB().get(0).getP1().getL()[0];

					final double lStart = start.getB().get(start.getB().size() - 1).getP1().getL()[1];
					final double lEnd = end.getB().get(0).getP1().getL()[1];

					final ArrayList<Point> catastropyPoints = new ArrayList<Point>();

					for (final Point p : points)
						if (p.getL()[0] >= tStart && p.getL()[0] <= tEnd)
							catastropyPoints.add(p);

					if (catastropyPoints.size() > 2) {
						if (Math.abs(lStart - lEnd) >= this.minDistanceCatastrophe) {
							// maximally 1.1 timepoints between points on a line
							final Pair<LinearFunction, ArrayList<PointFunctionMatch>> fit = Tracking
									.findFunction(catastropyPoints, new LinearFunction(), 0.75, 3, 1.1);

							if (fit != null) {
								if (fit.getA().getM() < 0) {
									sort(fit);
									negsegments.add(fit);
									double minY = Math.min(fit.getB().get(0).getP1().getL()[1],
											fit.getB().get(fit.getB().size() - 1).getP1().getL()[1]);
									double maxY = Math.max(fit.getB().get(0).getP1().getL()[1],
											fit.getB().get(fit.getB().size() - 1).getP1().getL()[1]);

									final Pair<Double, Double> minMax = Tracking.fromTo(fit.getB());

									double startX = minMax.getA();
									double endX = minMax.getB();

									double linearrate = fit.getA().getCoefficient(1);

									if (linearrate < 0) {
										dataset.addSeries(Tracking.drawFunction((Polynomial) fit.getA(),
												minMax.getA() - 1, minMax.getB() + 1, 0.1, minY - 2.5, maxY + 2.5,
												"CRansac " + catastrophy));
										negcount++;
										negtimediff += endX - startX;

										shrinkrate = linearrate;
										averageshrink += linearrate;

										rt.incrementCounter();
										rt.addValue("Start time", startX * calibrations[2]);
										rt.addValue("End time", endX * calibrations[2]);
										rt.addValue("Growth velocity", linearrate * calibrations[0] / calibrations[2]);

										Pair<Float, Float> startrate = new ValuePair<Float, Float>((float) startX,
												(float) linearrate);

										starttimerates.add(startrate);

										Rateobject velocity = new Rateobject(
												linearrate * calibrations[0] / calibrations[2],
												(int) (startX * calibrations[2]), (int) (endX * calibrations[2]));
										allrates.add(velocity);
										Tracking.setColor(chart, i, new Color(0, 0, 255));
										Tracking.setDisplayType(chart, i, true, false);
										Tracking.setStroke(chart, i, 2f);

										++i;
										dataset.addSeries(Tracking.drawPoints(Tracking.toPairList(fit.getB()),
												calibrations, "C(inl) " + catastrophy));

										Tracking.setColor(chart, i, new Color(0, 0, 255));
										Tracking.setDisplayType(chart, i, false, true);
										Tracking.setShape(chart, i, ShapeUtils.createDownTriangle(4f));

										++i;
									}
								}
							}

						}

						else {
							System.out.println("Catastrophy height not sufficient " + Math.abs(lStart - lEnd) + " < "
									+ this.minDistanceCatastrophe);

						}
					}

				}

			}
		}

		if (this.detectmanualCatastrophe) {

			catindex++;
			catstarttimerates = ManualCat(segments, allrates, shrinkrate, rt);

		}

		if (count > 0)
			averagegrowth /= count;

		if (catcount > 0)

			catfrequ = catcount / (timediff * calibrations[2]);

		if (rescount > 0)

			resfrequ = rescount / (restimediff * calibrations[2]);

		if (negcount > 0)
			averageshrink /= negcount;

		if (resfrequ < 0)
			resfrequ = 0;

		rt.show("Rates(real units) for" + " " + this.inputfile.getName());
		averageshrink *= calibrations[0] / calibrations[2];
		averagegrowth *= calibrations[0] / calibrations[2];
		rtAll.incrementCounter();
		rtAll.addValue("Average Growth", averagegrowth);
		rtAll.addValue("Growth events", count);
		rtAll.addValue("Average Shrink", averageshrink);
		rtAll.addValue("Shrink events", negcount);
		rtAll.addValue("Catastrophe Frequency", catfrequ);
		rtAll.addValue("Catastrophe events", catcount);
		rtAll.addValue("Rescue Frequency", resfrequ);
		rtAll.addValue("Rescue events", rescount);
		// rtAll.show("Average Rates and Frequencies (real units)");

		starttimerates.addAll(catstarttimerates);
		sortTime(starttimerates);

		for (int index = 0; index < starttimerates.size() - 1; ++index) {

			int prevsign = (int) Math.signum(starttimerates.get(index).getB());
			int nextsign = (int) Math.signum(starttimerates.get(index + 1).getB());

			if (nextsign == prevsign)
				wrongfile = true;
			else
				wrongfile = false;

			wrongfileindex = new ValuePair<Boolean, Integer>(wrongfile, row);
			wrongfileindexlist.put(row, wrongfile);
		}
		table.getModel().setValueAt(new DecimalFormat("#.###").format(averagegrowth), row, 1);
		table.getModel().setValueAt(new DecimalFormat("#.###").format(averageshrink), row, 2);
		table.getModel().setValueAt(new DecimalFormat("#").format(count), row, 3);
		table.getModel().setValueAt(new DecimalFormat("#").format(negcount), row, 4);
		table.getModel().setValueAt(new DecimalFormat("#.###").format(catfrequ), row, 5);
		table.getModel().setValueAt(new DecimalFormat("#.###").format(resfrequ), row, 6);
		if (wrongfileindexlist.get(row) != null) {
			table.getModel().setValueAt(wrongfileindexlist.get(row).toString(), row, 7);
		}
		int size = 100;
		table.getColumnModel().getColumn(0).setPreferredWidth(size);
		table.getColumnModel().getColumn(1).setPreferredWidth(size);
		table.getColumnModel().getColumn(2).setPreferredWidth(size);
		table.getColumnModel().getColumn(3).setPreferredWidth(size);
		table.getColumnModel().getColumn(4).setPreferredWidth(size);
		table.getColumnModel().getColumn(5).setPreferredWidth(size);
		table.getColumnModel().getColumn(6).setPreferredWidth(size);
		table.getColumnModel().getColumn(7).setPreferredWidth(size);
		table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int rowA, int col) {

				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, rowA, col);

				String status = (String) table.getModel().getValueAt(row, 7);
				if ("true".equals(status)) {
					setBackground(Color.GRAY);

				} else {
					setBackground(Color.GRAY);
				}
				return this;
			}
		});
		table.validate();

		scrollPane.validate();

		Averagerate avrate = new Averagerate(averagegrowth, averageshrink, catfrequ, resfrequ, count, negcount,
				catcount, rescount, this.inputfile);
		averagerates.add(avrate);
		Compilepositiverates.put(row, allrates);

		Compileaverage.put(row, avrate);

		--updateCount;

	}

	public List<Pair<Float, Float>> ManualCat(
			ArrayList<Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>>> segments, ArrayList<Rateobject> allrates,
			double shrinkrate, ResultsTable rt) {

		List<Pair<Float, Float>> catstarttimerates = new ArrayList<Pair<Float, Float>>();

		

		for (int catastrophy = 0; catastrophy < segments.size() - 1; ++catastrophy) {
			boolean alreadyfitted = false;
			final Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>> start = segments.get(catastrophy);
			final Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>> end = segments.get(catastrophy + 1);

				
				
			double tStart = start.getB().get(start.getB().size() - 1).getP1().getL()[0];
			double tEnd = end.getB().get(0).getP1().getL()[0];

			final double lStart = start.getB().get(start.getB().size() - 1).getP1().getL()[1];
			final double lEnd = end.getB().get(0).getP1().getL()[1];

			if (negsegments != null) {
				for (int i = 0; i < negsegments.size(); ++i) {
					double tStartold = negsegments.get(i).getB().get(negsegments.get(i).getB().size() - 1).getP1()
							.getL()[0];
					double tEndold = negsegments.get(i).getB().get(0).getP1().getL()[0];
					if (Math.abs(tStartold - tStart) < 5 || Math.abs(tEnd - tEndold) < 5) {

						alreadyfitted = true;

						break;
					}
				}
			}

			if (tEnd > tStart && !alreadyfitted) {
				if (Math.abs(lStart - lEnd) >= this.minDistanceCatastrophe) {

					final double slope = (lEnd - lStart) / (tEnd - tStart);
					final double intercept = lEnd - slope * tEnd;

					LinearFunction linearfunc = new LinearFunction(slope, intercept);

					double startX = tStart;
					double endX = tEnd;

					double linearrate = linearfunc.getCoefficient(1);

					if (linearrate < 0) {

						System.out.println("Overriding Ransac, Detecting without fiting a function");

						negcount++;
						negtimediff += endX - startX;

						shrinkrate = linearrate;
						averageshrink += linearrate;

						rt.incrementCounter();
						rt.addValue("Start time", startX * calibrations[2]);
						rt.addValue("End time", endX * calibrations[2]);
						rt.addValue("Growth velocity", linearrate * calibrations[0] / calibrations[2]);
						Pair<Float, Float> startrate = new ValuePair<Float, Float>((float) startX, (float) linearrate);

						catstarttimerates.add(startrate);

						ArrayList<PointFunctionMatch> p = new ArrayList<PointFunctionMatch>();

						p.add(new PointFunctionMatch(new Point(new double[] { tStart, lStart })));
						p.add(new PointFunctionMatch(new Point(new double[] { tEnd, lEnd })));

						Rateobject velocity = new Rateobject(linearrate * calibrations[0] / calibrations[2],
								(int) (startX * calibrations[2]), (int) (endX * calibrations[2]));
						allrates.add(velocity);

						dataset.addSeries(Tracking.drawPoints(Tracking.toPairList(p), calibrations,
								"CManual" + catindex + catastrophy));

						Tracking.setColor(chart, i, new Color(255, 192, 255));
						Tracking.setDisplayType(chart, i, true, false);
						Tracking.setStroke(chart, i, 2f);
						++i;
						chart.setTitle("Length plot for" + " " + this.inputfiles[row].getName());
					}
				}
			}
		}
		return catstarttimerates;

	}

	protected void sort(final Pair<? extends AbstractFunction2D, ArrayList<PointFunctionMatch>> segment) {
		Collections.sort(segment.getB(), new Comparator<PointFunctionMatch>() {

			@Override
			public int compare(final PointFunctionMatch o1, final PointFunctionMatch o2) {
				final double t1 = o1.getP1().getL()[0];
				final double t2 = o2.getP1().getL()[0];

				if (t1 < t2)
					return -1;
				else if (t1 == t2)
					return 0;
				else
					return 1;
			}
		});
	}

	protected void sortTime(final List<Pair<Float, Float>> starttimerates) {

		Collections.sort(starttimerates, new Comparator<Pair<Float, Float>>() {

			@Override
			public int compare(final Pair<Float, Float> o1, final Pair<Float, Float> o2) {
				final float t1 = o1.getA();
				final float t2 = o2.getA();

				if (t1 < t2)
					return -1;
				else if (t1 == t2)
					return 0;
				else
					return 1;
			}

		});
	}

	public double leastX() {

		// Ignore the event starting from zero time
		double minstartX = Double.MAX_VALUE;

		for (final Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>> result : segments) {

			final Pair<Double, Double> minMax = Tracking.fromTo(result.getB());

			double startX = minMax.getB();

			if (startX <= minstartX) {

				minstartX = startX;

			}

		}

		return minstartX;

	}

	public double leastStart(final ArrayList<Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>>> segments) {

		double minstartY = Double.MAX_VALUE;

		double minstartX = leastX();

		for (final Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>> result : segments) {

			final Pair<Double, Double> minMax = Tracking.fromTo(result.getB());

			double startX = minMax.getA();
			Polynomial<?, Point> polynomial = (Polynomial) result.getA();
			double startY = polynomial.predict(startX);

			if (startY <= minstartY && startX != 0) {

				minstartY = startY;

			}

		}

		return minstartY;

	}

	protected void sortPoints(final ArrayList<Point> points) {
		Collections.sort(points, new Comparator<Point>() {

			@Override
			public int compare(final Point o1, final Point o2) {
				final double t1 = o1.getL()[0];
				final double t2 = o2.getL()[0];

				if (t1 < t2)
					return -1;
				else if (t1 == t2)
					return 0;
				else
					return 1;
			}
		});
	}

	protected void sort(final ArrayList<Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>>> segments) {
		for (final Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>> segment : segments)
			sort(segment);

		Collections.sort(segments, new Comparator<Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>>>() {
			@Override
			public int compare(Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>> o1,
					Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>> o2) {
				final double t1 = o1.getB().get(0).getP1().getL()[0];
				final double t2 = o2.getB().get(0).getP1().getL()[0];

				if (t1 < t2)
					return -1;
				else if (t1 == t2)
					return 0;
				else
					return 1;
			}
		});

		/*
		 * for (final Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>> segment :
		 * segments) { System.out.println("\nSEGMENT"); for (final PointFunctionMatch pm
		 * : segment.getB()) System.out.println(pm.getP1().getL()[0] + ", " +
		 * pm.getP1().getL()[1]); }
		 */
	}

	public void close() {
		panelFirst.setVisible(false);
		Cardframe.dispose();
		jFreeChartFrame.setVisible(false);
		jFreeChartFrame.dispose();
	}

	protected static double computeValueFromDoubleExpScrollbarPosition(final int scrollbarPosition,
			final int scrollbarMax, final double maxValue) {
		final int maxScrollHalf = scrollbarMax / 2;
		final int scrollPos = scrollbarPosition - maxScrollHalf;

		final double logMax = Math.log10(maxScrollHalf + 1);

		final double value = Math.min(maxValue,
				((logMax - Math.log10(maxScrollHalf + 1 - Math.abs(scrollPos))) / logMax) * maxValue);

		if (scrollPos < 0)
			return -value;
		else
			return value;
	}

	protected static int computeScrollbarPositionValueFromDoubleExp(final int scrollbarMax, final double value,
			final double maxValue) {
		final int maxScrollHalf = scrollbarMax / 2;
		final double logMax = Math.log10(maxScrollHalf + 1);

		int scrollPos = (int) Math
				.round(maxScrollHalf + 1 - Math.pow(10, logMax - (Math.abs(value) / maxValue) * logMax));

		if (value < 0)
			scrollPos *= -1;

		return scrollPos + maxScrollHalf;
	}

	public static double computeValueFromScrollbarPosition(final int scrollbarPosition, final int scrollbarMax,
			final double minValue, final double maxValue) {
		return minValue + (scrollbarPosition / (double) scrollbarMax) * (maxValue - minValue);
	}

	public static int computeScrollbarPositionFromValue(final int scrollbarMax, final double value,
			final double minValue, final double maxValue) {
		return (int) Math.round(((value - minValue) / (maxValue - minValue)) * scrollbarMax);
	}

	public static void main(String[] args) {

		JFrame frame = new JFrame("");
		RansacFileChooser panel = new RansacFileChooser();

		frame.getContentPane().add(panel, "Center");
		frame.setSize(panel.getPreferredSize());
	}
}
