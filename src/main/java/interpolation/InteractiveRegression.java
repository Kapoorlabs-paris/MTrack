package interpolation;

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
import java.io.File;
import java.io.FilenameFilter;
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
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

import fit.AbstractFunction2D;
import fit.PointFunctionMatch;
import fit.polynomial.HigherOrderPolynomialFunction;
import fit.polynomial.InterpolatedPolynomial;
import fit.polynomial.LinearFunction;
import fit.polynomial.Polynomial;
import fit.polynomial.QuadraticFunction;
import ij.ImageJ;
import ij.plugin.PlugIn;
import mpicbg.models.Point;
import mt.DisplayPoints;
import mt.FLSobject;
import mt.LengthCounter;
import mt.LengthDistribution;
import mt.Tracking;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;

public class InteractiveRegression implements PlugIn {
	public static int MIN_SLIDER = 0;
	public static int MAX_SLIDER = 500;

	public static int MIN_DEGREE = 1;
	public static int MAX_DEGREE = 50;

	
	public File inputfile;
	public String inputdirectory;
	public NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);



	final ArrayList<Pair<Integer, Double>> mts;
	

	Scrollbar lambdaSB;
	Label lambdaLabel;

	
	int updateCount = 0;

	public
	// for scrollbars
	int degreeInt;

	public int degree = 3;
	
	protected boolean wasCanceled = false;

	public InteractiveRegression(final ArrayList<Pair<Integer, Double>> mts, File file) {
		this(mts, 3, file);
		nf.setMaximumFractionDigits(5);
	}

	public InteractiveRegression(final ArrayList<Pair<Integer, Double>> mts,
			final int degree, final File file) {
	
		this.mts = mts;
		this.inputfile = file;
		this.inputdirectory = file.getParent();
		this.degree = degree;
		

		
	
		this.degree = (int)computeValueFromScrollbarPosition(this.degreeInt, MAX_SLIDER, MIN_DEGREE, MAX_DEGREE);
	
		
	

	};

	@Override
	public void run(String arg) {
		/* JFreeChart */
		
		Card();
		

	}

	
	public JFrame Cardframe = new JFrame("Do polynomial fits ");
	public JPanel panelCont = new JPanel();
	public JPanel panelFirst = new JPanel();
	public JPanel panelSecond = new JPanel();
	JFileChooser chooserA;
	String choosertitleA;
	public void Card() {
		
		CardLayout cl = new CardLayout();

		panelCont.setLayout(cl);
		
		panelCont.add(panelFirst, "1");
		panelCont.add(panelSecond, "2");
		
		panelFirst.setName("Regression Polynomial Fits");
		

		/* Instantiation */
		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints c = new GridBagConstraints();

		final Scrollbar degreeSB = new Scrollbar(Scrollbar.HORIZONTAL, this.degreeInt, 1, MIN_SLIDER,
				MAX_SLIDER + 1);
	
	


		final Label degreeLabel = new Label("Degree of polynomial = " + this.degree, Label.CENTER);
		
	
		

		// Location
		panelFirst.setLayout(layout);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		
		++c.gridy;
		c.insets = new Insets(30, 150, 0, 150);
		panelFirst.add(degreeSB, c);

		++c.gridy;
		c.insets = new Insets(30, 150, 0, 150);
		panelFirst.add(degreeLabel, c);


	



		degreeSB.addAdjustmentListener(new DegreeListener(this, degreeLabel, degreeSB));
		
		
		
		panelFirst.setVisible(true);
		cl.show(panelCont, "1");
		Cardframe.add(panelCont, BorderLayout.CENTER);
	
		Cardframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Cardframe.pack();
		Cardframe.setVisible(true);
		updateRegression();
	}
	
	

		
	
	
	



	public void updateRegression() {
	

		
		final XYSeriesCollection dataset = new XYSeriesCollection();
		final ArrayList< Pair< Integer, Double > > mtspoly = new ArrayList< Pair< Integer, Double > >();
		  double[] x = new double[mts.size()];
	        double[] y = new double[mts.size()];
	        
	        int i = 0;
	        for ( Pair< Integer, Double > point: mts){
	        	
	        	x[i] = point.getA();
	        	y[i] = point.getB();
	        	i++;
	        }
	        Polyfit regression = new Polyfit(x, y, degree);
	        for ( double t = x[0]; t <=x[x.length - 1] ; ++t )
			{
	          double poly = regression.predict(t);
	          
	          mtspoly.add(new ValuePair< Integer, Double>((int)t, poly));
			}
	        
	        
	        dataset.addSeries(Tracking.drawPoints(mtspoly, "Function fit"));
	        dataset.addSeries(Tracking.drawPoints(mts, "Original Data"));
	     
	        
	       JFreeChart  chart = Tracking.makeChart(dataset);
	       Tracking.display(chart, new Dimension(500, 400));
	   	Tracking.setColor(chart, i, new Color(255, 0, 0));
		Tracking.setStroke(chart, i, 0.5f);
	       
	       for(int j = degree; j >=0; --j)
	        System.out.println(regression.GetCoefficients(j)  + " *x power  " + j );
	     
		
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

		for (final Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>> segment : segments) {
			System.out.println("\nSEGMENT");
			for (final PointFunctionMatch pm : segment.getB())
				System.out.println(pm.getP1().getL()[0] + ", " + pm.getP1().getL()[1]);
		}
	}

	public void close() {
		panelFirst.setVisible(false);
		Cardframe.dispose();
		
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
		    InterpolantFileChooser panel = new InterpolantFileChooser();
		 
		    frame.getContentPane().add(panel,"Center");
		    frame.setSize(panel.getPreferredSize());

	}
}
