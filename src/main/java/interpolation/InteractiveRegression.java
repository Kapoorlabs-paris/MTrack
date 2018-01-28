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

import ij.ImageJ;
import ij.plugin.PlugIn;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Scrollbar;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;

import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeriesCollection;

import mt.Tracking;

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
		final ArrayList< Pair< Integer, Double > > mtspolyderiv = new ArrayList< Pair< Integer, Double > >();
		final ArrayList< Pair< Integer, Double > > mtspolysecderiv = new ArrayList< Pair< Integer, Double > >();
		final ArrayList< Pair< Integer, Double > > maxima = new ArrayList< Pair< Integer, Double > >();
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
	          double derivpoly = regression.predictderivative(t);
	          double derivsecpoly = 0;
	          
	          mtspoly.add(new ValuePair< Integer, Double>((int)t, poly));
	          mtspolyderiv.add(new ValuePair< Integer, Double>((int)t, derivpoly));
	          mtspolysecderiv.add(new ValuePair< Integer, Double>((int)t, derivsecpoly));
	          if (Math.abs(derivpoly) <= 0.01 ){
	  	        System.out.println("Maxima or Minima of the function at: " + t);
	  	      if (degree >= 2)
	        	  derivsecpoly = regression.predictsecderivative(t);
	  	      if (derivsecpoly < 0 && Math.abs(derivsecpoly) < 1.0E7)
	  	        maxima.add(new ValuePair< Integer, Double>((int)t, poly));
	  	        
	          }
			}
	        
	        
	        dataset.addSeries(Tracking.drawPoints(mtspoly, new double[]{1,1,1}, "Function fit"));
	        dataset.addSeries(Tracking.drawPoints(maxima, new double[]{1,1,1}, "Extremum"));
	        dataset.addSeries(Tracking.drawPoints(mts, new double[]{1,1,1}, "Original Data"));
	      //  dataset.addSeries(Tracking.drawPoints(mtspolyderiv, "Derivative of Function fit"));
	      //  dataset.addSeries(Tracking.drawPoints(mtspolysecderiv, " SecondDerivative of Function fit"));
	        
	       JFreeChart  chart = Tracking.makeChart(dataset);
	       Tracking.display(chart, new Dimension(600, 600));
	   	Tracking.setColor(chart, i, new Color(255, 0, 0));
		Tracking.setStroke(chart, i, 0.5f);
	       
		 //  for(int j = degree; j >=0; --j)
		  //      System.out.println(regression.GetCoefficients(j)  + " *x power  " + j );
	       
	     
		
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
