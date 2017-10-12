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
	
	
	public double leastX(ArrayList< Pair< AbstractFunction2D, ArrayList< PointFunctionMatch > > > segments){
		
// Ignore the event starting from zero time
		double minstartX = Double.MAX_VALUE;
	
		for (final Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>> result : segments) {

			final Pair<Double, Double> minMax = Tracking.fromTo(result.getB());

			double startX = minMax.getB();
		
			
			
			
			if (startX <= minstartX  ){
				
				minstartX = startX;
				
			}
			
		}
		
	
		return minstartX;
		
	}
	
	public double leastStart(ArrayList< Pair< AbstractFunction2D, ArrayList< PointFunctionMatch > > > segments){
		
		
		double minstartY = Double.MAX_VALUE;
	
		double minstartX =  leastX(segments);
		
		for (final Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>> result : segments) {

			final Pair<Double, Double> minMax = Tracking.fromTo(result.getB());

			
			double startX = minMax.getA();
			Polynomial<?, Point> polynomial = (Polynomial) result.getA();
			double startY = polynomial.predict(startX);
			
			if (startY <= minstartY && startX!= 0  ){
				
				minstartY = startY;
				
			}
			
		}
		
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
			
			
			
			XYSeries timeseries = Tracking.drawPoints(Xvalues, new double[]{1,1,1}, "Time Distribution");
			XYSeries counterseries = new XYSeries("Time Distribution");
			
			
			for (final Pair<Integer, Double> key : Xvalues){
				
				counterseries.add(key.getB() * parent.calibrations[2], key.getA());
			}
			
			
			XYSeriesCollection dataset = new XYSeriesCollection(counterseries);
			
			 final JFreeChart chart =
					  ChartFactory.createScatterPlot("LifeTime Distribution",
					  "Time (sec)", "Count of growth events", dataset);
					  
					  DisplayPoints.display(chart, new Dimension(800, 500));
	

			LengthDistribution.GetLengthDistributionArray(parent.AllMoviesB, parent.calibrations);
			
		
	}

	
}
