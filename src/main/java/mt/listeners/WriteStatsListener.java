package mt.listeners;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import fit.AbstractFunction2D;
import fit.PointFunctionMatch;
import fit.polynomial.LinearFunction;
import fit.polynomial.Polynomial;
import ij.measure.ResultsTable;
import mpicbg.models.Point;
import mt.DisplayPoints;
import mt.LengthDistribution;
import mt.Tracking;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;

public class WriteStatsListener implements ActionListener {

	final InteractiveRANSAC parent;

	public WriteStatsListener(final InteractiveRANSAC parent) {
		this.parent = parent;
	}

	@Override
	public void actionPerformed(final ActionEvent arg0) {

		
	writeStatstofile();

	}
	
	
	public double leastX(){
		
// Ignore the event starting from zero time
		double minstartX = Double.MAX_VALUE;
	
		for (final Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>> result : parent.segments) {

			final Pair<Double, Double> minMax = Tracking.fromTo(result.getB());

			double startX = minMax.getB();
		
			
			
			
			if (startX <= minstartX  ){
				
				minstartX = startX;
				
			}
			
		}
		
	
		return minstartX;
		
	}
	
	public double leastStart(){
		
		
		double minstartY = Double.MAX_VALUE;
	
		double minstartX =  leastX();
		
		for (final Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>> result : parent.segments) {

			final Pair<Double, Double> minMax = Tracking.fromTo(result.getB());

			
			double startX = minMax.getA();
			Polynomial<?, Point> polynomial = (Polynomial) result.getA();
			double startY = polynomial.predict(startX);
			
			if (startY <= minstartY && startX!= 0  ){
				
				minstartY = startY;
				
			}
			
		}
		
		System.out.println(minstartY);
		return minstartY;
		
	}
	
	
        public  void writeStatstofile(){
			ArrayList<Pair<Integer, ArrayList<Pair<Integer, Double>>>> Alllife = new ArrayList<Pair<Integer, ArrayList<Pair<Integer, Double>>>>();

        	for (int i = 0; i < parent.lifecount.size(); ++i) {
        		Pair<Integer, ArrayList<Pair<Integer,Double>>> life = new ValuePair<Integer, ArrayList<Pair<Integer,Double>>>(i, parent.lifecount);
				Alllife.add(life);
        		
        		
        	}
        	
        	List<Pair<Integer,Double>> Xvalues = new ArrayList<Pair<Integer,Double>>();

			for (final Pair<Integer, ArrayList<Pair<Integer,Double>>> key : Alllife){
			
				Xvalues.addAll(key.getB());
			}
			
			
			
			XYSeries timeseries = Tracking.drawPoints(Xvalues, "Time Distribution");
			XYSeries counterseries = new XYSeries("Time Distribution");
			
			
			for (final Pair<Integer, Double> key : Xvalues){
				
				counterseries.add(key.getB(), key.getA());
			}
			
			
			XYSeriesCollection dataset = new XYSeriesCollection(counterseries);
			
			 final JFreeChart chart =
					  ChartFactory.createScatterPlot("Time Distribution",
					  "Time (px)", "Count of growth events", dataset);
					  
					  DisplayPoints.display(chart, new Dimension(800, 500));
	

			LengthDistribution.GetLengthDistributionArray(parent.AllMovies);
			
		
	}

	
}
