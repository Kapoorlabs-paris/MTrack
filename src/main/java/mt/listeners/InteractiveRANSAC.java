package mt.listeners;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FilenameFilter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.util.ShapeUtilities;

import fit.AbstractFunction2D;
import fit.PointFunctionMatch;
import fit.polynomial.HigherOrderPolynomialFunction;
import fit.polynomial.InterpolatedPolynomial;
import fit.polynomial.LinearFunction;
import fit.polynomial.Polynomial;
import fit.polynomial.QuadraticFunction;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.Overlay;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import mpicbg.models.Point;
import mt.Averagerate;
import mt.DisplayPoints;
import mt.FLSobject;
import mt.LengthCounter;
import mt.LengthDistribution;
import mt.RansacFileChooser;
import mt.Rateobject;
import mt.Tracking;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import ransacBatch.BatchRANSAC;
import trackerType.TrackModel;

public class InteractiveRANSAC implements PlugIn {
	public static int MIN_SLIDER = 0;
	public static int MAX_SLIDER = 500;

	public static double MIN_ERROR = 0.0;
	public static double MAX_ERROR = 30.0;

	public static double MIN_RES = 1.0;
	public static double MAX_RES = 30.0;

	public static double MAX_ABS_SLOPE = 100.0;
	
	public static double MIN_CAT = 0.0;
	public static double MAX_CAT = 100.0;
	public File inputfile;
	public File[] inputfiles;
	public String inputdirectory;
	public NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
	public ArrayList<Pair<LinearFunction, ArrayList<PointFunctionMatch>>> linearlist;
	final Frame  jFreeChartFrame;
	public int functionChoice = 1; // 0 == Linear, 1 == Quadratic interpolated, 2 ==
								// cubic interpolated
	AbstractFunction2D function;
	public double lambda;
	ArrayList<Pair<Integer, Double>> mts;
	ArrayList<Point> points;
	public final int numTimepoints, minTP, maxTP;

	public int previousrow = 0;
	public int countfile;
	Scrollbar lambdaSB;
	Label lambdaLabel;

	final XYSeriesCollection dataset;
	final JFreeChart chart;
	final SVGGraphics2D svgchart;
	int updateCount = 0;
	public ArrayList<Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>>> segments;
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

	public double maxError = 3.0;
	public double minSlope = 0.1;
	public double maxSlope = 100;
	public double restolerance = 5;
	public double tptolerance = 2;
	public int maxDist = 300;
	public int minInliers = 50;
	public boolean detectCatastrophe = true;
	ArrayList<Pair<Integer, Double>> lifecount  ;
	public double minDistanceCatastrophe = 2;
	public final boolean serial;
	File[] AllMovies;
	
	ArrayList<File> AllMoviesB;
	protected boolean wasCanceled = false;

	public InteractiveRANSAC(final ArrayList<Pair<Integer, Double>> mts, File file) {
		this(mts, 0, 300, 3.0, 0.1, 10.0, 10, 50, 1, 0.1, file);
		nf.setMaximumFractionDigits(5);
	}

	
	public InteractiveRANSAC(File[] file) {
		this(0, 300, 3.0, 0.1, 10.0, 10, 50, 1, 0.1, file);
		nf.setMaximumFractionDigits(5);
	}
	
	
	public InteractiveRANSAC(final ArrayList<Pair<Integer, Double>> mts, final int minTP, final int maxTP,
			final double maxError, final double minSlope, final double maxSlope, final int maxDist,
			final int minInliers, final int functionChoice, final double lambda, final File file) {
		this.minTP = minTP;
		this.maxTP = maxTP;
		this.numTimepoints = maxTP - minTP + 1;
		this.functionChoice = functionChoice;
		this.lambda = lambda;
		this.mts = mts;
		this.points = Tracking.toPoints(mts);
		this.inputfile = file;
		this.inputdirectory = file.getParent();
		this.maxError = maxError;
		this.minSlope = minSlope;
		this.maxSlope = maxSlope;
		this.maxDist = Math.min(maxDist, numTimepoints);
		this.minInliers = Math.min(minInliers, numTimepoints);

		this.serial = false;
		if (this.minSlope >= this.maxSlope)
			this.minSlope = this.maxSlope - 0.1;

		this.maxErrorInt = computeScrollbarPositionFromValue(MAX_SLIDER, this.maxError, MIN_ERROR, MAX_ERROR);
		this.lambdaInt = computeScrollbarPositionFromValue(MAX_SLIDER, this.lambda, 0.0, 1.0);
		this.minSlopeInt = computeScrollbarPositionValueFromDoubleExp(MAX_SLIDER, this.minSlope, MAX_ABS_SLOPE);
		this.maxSlopeInt = computeScrollbarPositionValueFromDoubleExp(MAX_SLIDER, this.maxSlope, MAX_ABS_SLOPE);
		this.maxError = computeValueFromScrollbarPosition(this.maxErrorInt, MAX_SLIDER, MIN_ERROR, MAX_ERROR);
		this.minSlope = computeValueFromDoubleExpScrollbarPosition(this.minSlopeInt, MAX_SLIDER, MAX_ABS_SLOPE);
		this.maxSlope = computeValueFromDoubleExpScrollbarPosition(this.maxSlopeInt, MAX_SLIDER, MAX_ABS_SLOPE);
		this.dataset = new XYSeriesCollection();
		this.chart = Tracking.makeChart(dataset, "Microtubule Length Plot", "Timepoint", "MT Length");
		this.svgchart = new SVGGraphics2D(500, 500);
		this.jFreeChartFrame = Tracking.display(chart, new Dimension(500, 500));
		this.chart.draw(svgchart, new Rectangle2D.Double(0, 0, 500, 500), null);
	};

	public InteractiveRANSAC(final int minTP, final int maxTP,
			final double maxError, final double minSlope, final double maxSlope, final int maxDist,
			final int minInliers, final int functionChoice, final double lambda, final File[] file) {
		this.minTP = minTP;
		this.maxTP = maxTP;
		this.numTimepoints = maxTP - minTP + 1;
		this.functionChoice = functionChoice;
		this.lambda = lambda;
		this.mts = null;
		this.points= null;
		this.inputfiles = file;
		this.inputdirectory = file[0].getParent();
		this.maxError = maxError;
		this.minSlope = minSlope;
		this.maxSlope = maxSlope;
		this.maxDist = Math.min(maxDist, numTimepoints);
		this.minInliers = Math.min(minInliers, numTimepoints);

		this.serial = true;
		if (this.minSlope >= this.maxSlope)
			this.minSlope = this.maxSlope - 0.1;

		this.maxErrorInt = computeScrollbarPositionFromValue(MAX_SLIDER, this.maxError, MIN_ERROR, MAX_ERROR);
		this.lambdaInt = computeScrollbarPositionFromValue(MAX_SLIDER, this.lambda, 0.0, 1.0);
		this.minSlopeInt = computeScrollbarPositionValueFromDoubleExp(MAX_SLIDER, this.minSlope, MAX_ABS_SLOPE);
		this.maxSlopeInt = computeScrollbarPositionValueFromDoubleExp(MAX_SLIDER, this.maxSlope, MAX_ABS_SLOPE);
		this.maxError = computeValueFromScrollbarPosition(this.maxErrorInt, MAX_SLIDER, MIN_ERROR, MAX_ERROR);
		this.minSlope = computeValueFromDoubleExpScrollbarPosition(this.minSlopeInt, MAX_SLIDER, MAX_ABS_SLOPE);
		this.maxSlope = computeValueFromDoubleExpScrollbarPosition(this.maxSlopeInt, MAX_SLIDER, MAX_ABS_SLOPE);
		this.dataset = new XYSeriesCollection();
		this.chart = Tracking.makeChart(dataset, "Microtubule Length Plot", "Timepoint", "MT Length");
		this.svgchart = new SVGGraphics2D(500, 500);
		this.jFreeChartFrame = Tracking.display(chart, new Dimension(500, 500));
		this.chart.draw(svgchart, new Rectangle2D.Double(0, 0, 500, 500), null);
	};
	
	
	
	@Override
	public void run(String arg) {
		/* JFreeChart */
		allrates = new ArrayList<Rateobject>();
		averagerates = new ArrayList<Averagerate>();
		
		Compilepositiverates = new HashMap<Integer, ArrayList<Rateobject>>();
		Compilenegativerates = new HashMap<Integer, ArrayList<Rateobject>>();
		Compileaverage = new HashMap<Integer, Averagerate>();
		
	 lifecount = new ArrayList<Pair<Integer, Double>>();
	 countfile = 0;
	 AllMoviesB = new ArrayList<File>();
	 
	 segments = new ArrayList<Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>>>();
	 indexedsegments = new HashMap <Integer,   Pair<Double, Double>>(); 
	 linearsegments = new HashMap<Integer, LinearFunction>();
	
		
		if (serial){
			
			
			
			
	    CardTable();
	    
	    
		}
		

	}

	
	public JFrame Cardframe = new JFrame("Welcome to Ransac Rate and Statistics Analyzer ");
	public JPanel panelCont = new JPanel();
	public JPanel panelFirst = new JPanel();
	public JPanel panelSecond = new JPanel();
	JPanel PanelSelectFile = new JPanel();
	JPanel PanelDirectory = new JPanel();
	private JPanel PanelParameteroptions = new JPanel();
	private JPanel PanelSavetoFile = new JPanel();
	private JPanel Panelfunction = new JPanel();
	private JPanel Panelslope = new JPanel();
	private JPanel PanelCompileRes = new JPanel();
	public JTable table;
	JFileChooser chooserA;
	String choosertitleA;
	DefaultTableModel userTableModel; 
	public int row;
	 public JScrollPane scrollPane ;
	static final Insets insets = new Insets(10, 0, 0, 0);
	
	public final GridBagLayout layout = new GridBagLayout();
	public final GridBagConstraints c = new GridBagConstraints();
	Border selectfile = new CompoundBorder(new TitledBorder("Select file"),
			new EmptyBorder(c.insets));
	Border selectparam = new CompoundBorder(new TitledBorder("Select Ransac parameters"),
			new EmptyBorder(c.insets));
	Border selectslope = new CompoundBorder(new TitledBorder("Slope constraints"),
			new EmptyBorder(c.insets));
	
	Border selectfunction = new CompoundBorder(new TitledBorder("Function for inlier detection"),
			new EmptyBorder(c.insets));

	Border compileres = new CompoundBorder(new TitledBorder("Compile results"),
			new EmptyBorder(c.insets));
	
	Border selectdirectory = new CompoundBorder(new TitledBorder("Load directory"),
			new EmptyBorder(c.insets));
	
         public void CardTable() {
		
		CardLayout cl = new CardLayout();
		userTableModel = new DefaultTableModel(new Object[]{"Track File"}, 0) {
		    @Override
		    public boolean isCellEditable(int row, int column) {
		        return false;
		    }
		};
		
		
				
		
		for (int i = 0; i < inputfiles.length; ++i) {
			
			String[] currenttrack = {(inputfiles[i].getName())};
			userTableModel.addRow(currenttrack);
		}
		
		
		  table = new JTable(userTableModel);
		
		 scrollPane = new JScrollPane(table);
		 scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		 scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		 scrollPane.setPreferredSize(new Dimension(300, 200));
		 
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

		c.gridwidth = 10;
		c.gridheight = 10;
		c.gridy = 1;
		c.gridx = 0;
	
		final Scrollbar maxErrorSB = new Scrollbar(Scrollbar.HORIZONTAL, this.maxErrorInt, 1, MIN_SLIDER,
				MAX_SLIDER + 1);
		final Scrollbar minInliersSB = new Scrollbar(Scrollbar.HORIZONTAL, this.minInliers, 1, 2, numTimepoints + 1);
		final Scrollbar maxDistSB = new Scrollbar(Scrollbar.HORIZONTAL, this.maxDist, 1, 0, numTimepoints + 1);
		final Scrollbar minSlopeSB = new Scrollbar(Scrollbar.HORIZONTAL, this.minSlopeInt, 1, MIN_SLIDER,
				MAX_SLIDER + 1);
		final Scrollbar maxSlopeSB = new Scrollbar(Scrollbar.HORIZONTAL, this.maxSlopeInt, 1, MIN_SLIDER,
				MAX_SLIDER + 1);

		final Choice choice = new Choice();
		choice.add("Linear Function only");
		choice.add("Linearized Quadratic function");
		choice.add("Linearized Cubic function");

		this.lambdaSB = new Scrollbar(Scrollbar.HORIZONTAL, this.lambdaInt, 1, MIN_SLIDER, MAX_SLIDER + 1);

		final Label maxErrorLabel = new Label("Max. Error (pixels) (px)) = " + new DecimalFormat("#.##").format(this.maxError), Label.CENTER);
		final Label minInliersLabel = new Label("Min. #Points (timepoints (tp) = " + new DecimalFormat("#.##").format(this.minInliers), Label.CENTER);
		final Label maxDistLabel = new Label("Max. Gap (tp) = " + new DecimalFormat("#.##").format(this.maxDist), Label.CENTER);
		this.lambdaLabel = new Label("Linearity (fraction) = " + new DecimalFormat("#.##").format(this.lambda), Label.CENTER);
		final Label minSlopeLabel = new Label("Min. Segment Slope (px/tp) = " + new DecimalFormat("#.##").format(this.minSlope), Label.CENTER);
		final Label maxSlopeLabel = new Label("Max. Segment Slope (px/tp) = " + new DecimalFormat("#.##").format(this.maxSlope), Label.CENTER);
		final Label maxResLabel = new Label(
				"MT is rescued if the start of event# i + 1 > start of event# i by px =  " + this.restolerance,
				Label.CENTER);

		final Checkbox findCatastrophe = new Checkbox("Detect Catastrophies", this.detectCatastrophe);
		final Scrollbar minCatDist = new Scrollbar(Scrollbar.HORIZONTAL, this.minDistCatInt, 1, MIN_SLIDER,
				MAX_SLIDER + 1);
		final Scrollbar maxRes = new Scrollbar(Scrollbar.HORIZONTAL, this.restoleranceInt, 1, MIN_SLIDER,
				MAX_SLIDER + 1);
		final Label minCatDistLabel = new Label("Min. Catatastrophy height (tp) = " + this.minDistanceCatastrophe,
				Label.CENTER);
		final Button done = new Button("Done");
		final Button batch = new Button("Save Parameters for Batch run");
		final Button cancel = new Button("Cancel");
		final Button Compile = new Button("Compile results till current file");
		final Button AutoCompile = new Button("Auto Fit and compile results");
		final Button Measureserial = new Button("Select directory of MTV tracker generated files");
		
		final Button WriteStats = new Button("Compute Length and Lifetime distribution");
		final Button WriteAgain = new Button("Save Rates and Frequencies to File");
		choice.select(functionChoice);
		setFunction();

		PanelDirectory.add(Measureserial,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
	
		PanelDirectory.setBorder(selectdirectory);
		panelFirst.add(PanelDirectory,  new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.RELATIVE, new Insets(10, 10, 0, 10), 0, 0));
		
		
		PanelSelectFile.add(scrollPane,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		
		PanelSelectFile.setBorder(selectfile);
		
		panelFirst.add(PanelSelectFile, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.RELATIVE, new Insets(10, 10, 0, 10), 0, 0));
		
		
		PanelParameteroptions.add(maxErrorSB,new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.RELATIVE, insets, 0, 0) );
		PanelParameteroptions.add(maxErrorLabel,new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.RELATIVE, insets, 0, 0) );
		
		PanelParameteroptions.add(minInliersSB,new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.RELATIVE, insets, 0, 0) );
		
		PanelParameteroptions.add(minInliersLabel,new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.RELATIVE, insets, 0, 0) );
		PanelParameteroptions.add(maxDistSB,new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.RELATIVE, insets, 0, 0) );
		PanelParameteroptions.add(maxDistLabel,new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.RELATIVE, insets, 0, 0) );
		
		PanelParameteroptions.setBorder(selectparam);
		panelFirst.add(PanelParameteroptions, new GridBagConstraints(3, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.RELATIVE, new Insets(10, 10, 0, 10), 0, 0));
		
		

		Panelfunction.add(choice,new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.RELATIVE, insets, 0, 0) );
		
		Panelfunction.add(lambdaSB,new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.RELATIVE, insets, 0, 0) );
		Panelfunction.add(lambdaLabel,new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.RELATIVE, insets, 0, 0) );
		
		Panelfunction.setBorder(selectfunction);
		panelFirst.add(Panelfunction, new GridBagConstraints(3, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.RELATIVE, new Insets(10, 10, 0, 10), 0, 0));
		
		

		Panelslope.add(minSlopeSB,new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.RELATIVE, insets, 0, 0) );
		Panelslope.add(minSlopeLabel,new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.RELATIVE, insets, 0, 0) );
		Panelslope.add(maxSlopeSB,new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.RELATIVE, insets, 0, 0) );
		Panelslope.add(maxSlopeLabel,new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.RELATIVE, insets, 0, 0) );
		
		Panelslope.add(findCatastrophe,new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.RELATIVE, insets, 0, 0) );
		Panelslope.add(minCatDist,new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.RELATIVE, insets, 0, 0) );
		Panelslope.add(minCatDistLabel,new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.RELATIVE, insets, 0, 0) );
	     Panelslope.setBorder(selectslope);
		
		panelFirst.add(Panelslope, new GridBagConstraints(3, 2, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.RELATIVE, new Insets(10, 10, 0, 10), 0, 0));
		
		
		PanelCompileRes.add(Compile,new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.RELATIVE, insets, 0, 0) );
		PanelCompileRes.add(AutoCompile,new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.RELATIVE, insets, 0, 0) );
	
		PanelCompileRes.setBorder(compileres);
		
		panelFirst.add(PanelCompileRes,  new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
		
		
	/*
		++c.gridy;
		c.insets = new Insets(20, 120, 0, 120);
		panelFirst.add(Write, c);
	
		++c.gridy;
		c.insets = new Insets(20, 120, 0, 120);
		panelFirst.add(WriteStats, c);
		
		++c.gridy;
		c.insets = new Insets(20, 120, 0, 120);
		panelSecond.add(WriteAgain, c);
	

		++c.gridy;
		c.insets = new Insets(20, 120, 0, 120);
		panelSecond.add(batch, c);

		*/
		table.addMouseListener(new MouseAdapter() {
			  public void mouseClicked(MouseEvent e) {
			    if (e.getClickCount() == 1) {
			      JTable target = (JTable)e.getSource();
			     row = target.getSelectedRow();
			      // do some action if appropriate column
			      if (row > 0)
			      displayclicked(row);
			      else
			      displayclicked(0);	  
			    }
			  }
			});

		maxErrorSB.addAdjustmentListener(new MaxErrorListener(this, maxErrorLabel, maxErrorSB));
		minInliersSB.addAdjustmentListener(new MinInliersListener(this, minInliersLabel, minInliersSB));
		maxDistSB.addAdjustmentListener(new MaxDistListener(this, maxDistLabel, maxDistSB));
		choice.addItemListener(new FunctionItemListener(this));
		lambdaSB.addAdjustmentListener(new LambdaListener(this, lambdaLabel, lambdaSB));
		minSlopeSB.addAdjustmentListener(new MinSlopeListener(this, minSlopeSB, minSlopeLabel));
		maxSlopeSB.addAdjustmentListener(new MaxSlopeListener(this, maxSlopeSB, maxSlopeLabel));
		findCatastrophe
				.addItemListener(new CatastrophyCheckBoxListener(this, findCatastrophe, minCatDistLabel, minCatDist));
		minCatDist.addAdjustmentListener(new MinCatastrophyDistanceListener(this, minCatDistLabel, minCatDist));
        Measureserial.addActionListener(new MeasureserialListener(this));
		Compile.addActionListener(new CompileResultsListener(this));
		AutoCompile.addActionListener(new AutoCompileResultsListener(this, row));
		WriteStats.addActionListener(new WriteStatsListener(this));
		WriteAgain.addActionListener(new WriteRatesListener(this));
		done.addActionListener(new FinishButtonListener(this, false));
		batch.addActionListener(new RansacBatchmodeListener(this));
		cancel.addActionListener(new FinishButtonListener(this, true));
		
		
		panelFirst.setVisible(true);
		
		
		
		cl.show(panelCont, "1");
		
		
		Cardframe.add(panelCont, BorderLayout.CENTER);
	
		Cardframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Cardframe.pack();
		Cardframe.setVisible(true);
		Cardframe.pack();
		
	}
	
         CompileRes compile = new CompileRes(this);
         public void displayclicked(final int trackindex){
     		

        	 this.inputfile = this.inputfiles[trackindex];
        	 this.inputdirectory = this.inputfiles[trackindex].getParent();
     		this.mts = Tracking.loadMT(this.inputfiles[trackindex]);
     		this.points = Tracking.toPoints(mts);
     		linearlist = new ArrayList<Pair<LinearFunction, ArrayList<PointFunctionMatch>>>();
     		dataset.removeAllSeries();
            this.dataset.addSeries(Tracking.drawPoints(mts));
			Tracking.setColor(chart, 0, new Color(64, 64, 64));
			Tracking.setStroke(chart, 0, 0.2f);
	         row = trackindex;
	         System.out.println(row);
     		updateRANSAC();
     		compile.compileresults();
     		
     	}
     	
	
	
	public void setFunction() {
		if (functionChoice == 0) {
			this.function = new LinearFunction();
			this.setLambdaEnabled(false);
		} else if (functionChoice == 1) {
			this.setLambdaEnabled(true);
			// this.function = new QuadraticFunction();
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

	public void updateRANSAC()
	{
		++updateCount;

		linearsegments.clear();
		indexedsegments.clear();
		ArrayList<Rateobject> allrates = new ArrayList<Rateobject>();
		 ArrayList<Averagerate> averagerates = new ArrayList<Averagerate>();  
		for ( int i = dataset.getSeriesCount() - 1; i > 0; --i )
			dataset.removeSeries( i );

		final ArrayList< Pair< AbstractFunction2D, ArrayList< PointFunctionMatch > > > segments =
				Tracking.findAllFunctions( points, function, maxError, minInliers, maxDist );

		if ( segments == null || segments.size() == 0 )
		{
			--updateCount;
			return;
		}

		// sort the segments according to time relative to each other and the PointFunctionMatches internally
		sort( segments );

		final LinearFunction linear = new LinearFunction();
		int i = 1, segment = 1, linearcount = 1;
		
		int count = 0;
		int negcount = 0;
		int rescount = 0;
		double timediff = 0;
		double restimediff = 0;
		double negtimediff = 0;
		double averagegrowth = 0;
		double averageshrink = 0;
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
		for ( final Pair< AbstractFunction2D, ArrayList< PointFunctionMatch > > result : segments )
		{
			if ( LinearFunction.slopeFits( result.getB(), linear, minSlope, maxSlope )  )
			{
				
				
				final Pair< Double, Double > minMax = Tracking.fromTo( result.getB() );
		
				double startX = minMax.getA();
				double endX = minMax.getB();
				

				if (startX < minstartX) {

					minstartX = startX;
					minendX = endX;
				}
				Polynomial<?, Point> polynomial = (Polynomial) result.getA();
				
			
				
				dataset.addSeries( Tracking.drawFunction( polynomial, minMax.getA(), minMax.getB(), 0.5, "Segment " + segment ) );

				
				
				
				
				if ( functionChoice > 0 )
				{
					Tracking.setColor( chart, i, new Color( 255, 0, 0 ) );
					Tracking.setDisplayType( chart, i, true, false );
					Tracking.setStroke( chart, i, 0.5f );
				}
				else
				{
					Tracking.setColor( chart, i, new Color( 0, 128, 0 ) );
					Tracking.setDisplayType( chart, i, true, false );
					Tracking.setStroke( chart, i, 2f );
				}

				++i;

				
			
				if ( functionChoice > 0 )
				{
					
					dataset.addSeries( Tracking.drawFunction( linear, minMax.getA(), minMax.getB(), 0.5, "Linear Segment " + segment ) );
					
					
					Tracking.setColor( chart, i, new Color( 0, 128, 0 ) );
					Tracking.setDisplayType( chart, i, true, false );
					Tracking.setStroke( chart, i, 2f );
	
					++i;
					
					
				
				}
				if (points.get(points.size() - 1).getW()[0] - endX >= tptolerance) {
					double startY = polynomial.predict(startX);
					double linearrate = linear.getCoefficient(1);
				if (linearrate > 0 && startY - minstartY > restolerance 
						&& previousendX.size() > 0 ) {
					rescount++;
					restimediff += -previousendX.get(previousendX.size() - 1) + startX;

				}

				
				
				if (linearrate > 0  ) {

					count++;
					growthrate = linearrate;
					timediff += endX - startX;
					lifetime = endX - startX;
					averagegrowth += linearrate;
					lifecount.add(new ValuePair<Integer, Double>(count, lifetime));
					
					
					Rateobject rate = new Rateobject(linearrate, (int)startX, (int)endX);
					allrates.add(rate);
					rt.incrementCounter();
					rt.addValue("Start time", startX);
					rt.addValue("End time", endX);
					rt.addValue("Growth Rate", linearrate);

				}

				if(linearrate > 0){
				previousendX.add(endX);
				
				
				}
				
				}
				
				dataset.addSeries( Tracking.drawPoints( Tracking.toPairList( result.getB() ), "Inliers " + segment ) );

				Tracking.setColor( chart, i, new Color( 255, 0, 0 ) );
				Tracking.setDisplayType( chart, i, false, true );
				Tracking.setSmallUpTriangleShape( chart, i );

				++i;
				++segment;
				
			}
			else
			{
				System.out.println( "Removed segment because slope is wrong." );

			}
		
		}

		if ( this.detectCatastrophe )
		{
			if ( segments.size() < 2 )
			{
				System.out.println( "We have only " + segments.size() + " segments, need at least two to detect catastrophies." );
			}
			else
			{
				for ( int catastrophy = 0; catastrophy < segments.size() - 1; ++catastrophy )
				{
					final Pair< AbstractFunction2D, ArrayList< PointFunctionMatch > > start = segments.get( catastrophy );
					final Pair< AbstractFunction2D, ArrayList< PointFunctionMatch > > end = segments.get( catastrophy + 1 );

					final double tStart = start.getB().get( start.getB().size() -1 ).getP1().getL()[ 0 ];
					final double tEnd = end.getB().get( 0 ).getP1().getL()[ 0 ];

					final double lStart = start.getB().get( start.getB().size() -1 ).getP1().getL()[ 1 ];
					final double lEnd = end.getB().get( 0 ).getP1().getL()[ 1 ];

					final ArrayList< Point > catastropyPoints = new ArrayList< Point >();

					for ( final Point p : points )
						if ( p.getL()[ 0 ] >= tStart && p.getL()[ 0 ] <= tEnd )
							catastropyPoints.add( p );

					/*
					System.out.println( "\ncatastropy" );
					for ( final Point p : catastropyPoints)
						System.out.println( p.getL()[ 0 ] + ", " + p.getL()[ 1 ] );
					*/

					if ( catastropyPoints.size() > 2 )
					{
						if ( Math.abs( lStart - lEnd ) >= this.minDistanceCatastrophe )
						{
							// maximally 1.1 timepoints between points on a line
							final Pair< LinearFunction, ArrayList< PointFunctionMatch > > fit = Tracking.findFunction( catastropyPoints, new LinearFunction(), 0.75, 3, 1.1 );
	
							if ( fit != null )
							{
								if ( fit.getA().getM() < 0 )
								{
									sort( fit );


									double minY = Math.min( fit.getB().get( 0 ).getP1().getL()[ 1 ], fit.getB().get( fit.getB().size() -1 ).getP1().getL()[ 1 ] );
									double maxY = Math.max( fit.getB().get( 0 ).getP1().getL()[ 1 ], fit.getB().get( fit.getB().size() -1 ).getP1().getL()[ 1 ] );

									final Pair< Double, Double > minMax = Tracking.fromTo( fit.getB() );

									dataset.addSeries( Tracking.drawFunction( (Polynomial)fit.getA(), minMax.getA()-1, minMax.getB()+1, 0.1, minY - 2.5, maxY + 2.5, "C " + catastrophy ) );
									double startX = minMax.getA();
									double endX = minMax.getB();
								
							double	linearrate = fit.getA().getCoefficient(1);
							if (linearrate < 0) {

								negcount++;
								negtimediff += endX - startX;

								shrinkrate = linearrate;
								averageshrink += linearrate;

								rt.incrementCounter();
								rt.addValue("Start time", startX);
								rt.addValue("End time", endX);
								rt.addValue("Growth Rate", linearrate);
							}
							
							Rateobject rate = new Rateobject(linearrate, (int)startX, (int)endX);
							allrates.add(rate);
									Tracking.setColor( chart, i, new Color( 0, 0, 255 ) );
									Tracking.setDisplayType( chart, i, true, false );
									Tracking.setStroke( chart, i, 2f );

									++i;

									dataset.addSeries( Tracking.drawPoints( Tracking.toPairList( fit.getB() ), "C(inl) " + catastrophy ) );

									Tracking.setColor( chart, i, new Color( 0, 0, 255 ) );
									Tracking.setDisplayType( chart, i, false, true );
									Tracking.setShape( chart, i, ShapeUtilities.createDownTriangle( 4f ) );

									++i;
									++segment;
								}
								else
								{
									System.out.println( "Slope not negative: " + fit.getA() );
								}
							}
							else
							{
								System.out.println( "No function found." );
							}
						}
						else
						{
							System.out.println( "Catastrophy height not sufficient " + Math.abs( lStart - lEnd ) + " < " + this.minDistanceCatastrophe );
						}
					}
					else
					{
						System.out.println( "We have only " + catastropyPoints.size() + " points, need at least three to detect this catastrophy." );
					}
				}
			}
		}
		if (count > 0)
			averagegrowth /= count;

		if (count > 0) 

			catfrequ = count / timediff;
		
		if (rescount > 0)

			resfrequ = rescount / restimediff;
		
		if (negcount > 0)
			averageshrink /= negcount;
		
		if(resfrequ < 0)
			resfrequ = 0;
		
		rt.show("Rates(pixel units)");

		rtAll.incrementCounter();
		rtAll.addValue("Average Growth", averagegrowth);
		rtAll.addValue("Average Shrink", averageshrink);
		rtAll.addValue("Catastrophe Frequency", catfrequ);
		rtAll.addValue("Rescue Frequency", resfrequ);

		rtAll.show("Average Rates and Frequencies (pixel units)");

		Averagerate avrate = new Averagerate(averagegrowth, averageshrink, catfrequ, resfrequ);
		averagerates.add(avrate);
		Compilepositiverates.put(row, allrates);

        Compileaverage.put(row, avrate);
		 
		--updateCount;
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

	public double leastStart(final ArrayList< Pair< AbstractFunction2D, ArrayList< PointFunctionMatch > > > segments ) {

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

		System.out.println(minstartY);
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
		for (final Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>> segment : segments) {
			System.out.println("\nSEGMENT");
			for (final PointFunctionMatch pm : segment.getB())
				System.out.println(pm.getP1().getL()[0] + ", " + pm.getP1().getL()[1]);
		}
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

	protected static double computeValueFromScrollbarPosition(final int scrollbarPosition, final int scrollbarMax,
			final double minValue, final double maxValue) {
		return minValue + (scrollbarPosition / (double) scrollbarMax) * (maxValue - minValue);
	}

	protected static int computeScrollbarPositionFromValue(final int scrollbarMax, final double value,
			final double minValue, final double maxValue) {
		return (int) Math.round(((value - minValue) / (maxValue - minValue)) * scrollbarMax);
	}

	
		    
	public static void main(String[] args) {
		
		new ImageJ();
		

		    JFrame frame = new JFrame("");
		    RansacFileChooser panel = new RansacFileChooser();
		 
		    frame.getContentPane().add(panel,"Center");
		    frame.setSize(panel.getPreferredSize());

	}
}
