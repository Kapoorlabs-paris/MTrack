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
package mt;

import java.awt.Dimension;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import mpicbg.models.NotEnoughDataPointsException;
import mpicbg.models.Point;

public class LengthDistribution {
	
	
	

	public static double Lengthdistro(File file) {

		ArrayList<FLSobject> currentobject = Tracking.loadMTStat(file);

		double maxlength = 0;

		double minlength = 0;

		if (currentobject != null) {
			for (int index = 0; index < currentobject.size(); ++index) {

				for (int secindex = 0; secindex < currentobject.size(); ++secindex) {

					maxlength = Math.max(currentobject.get(index).length, currentobject.get(secindex).length);

				}
			}

		}

		return maxlength;

	}
	
	
	public static ArrayList<Pair<Integer, Double>> LengthdistroatTime(File file, final int framenumber) {

		ArrayList<FLSobject> currentobject = Tracking.loadMTStat(file);


		ArrayList<Pair<Integer, Double>> lengthlist = new ArrayList<Pair<Integer, Double>>();

		if (currentobject != null) {
			for (int index = 0; index < currentobject.size(); ++index) {


				if(currentobject.get(index).Framenumber == framenumber){
					lengthlist.add (new ValuePair<Integer, Double>(currentobject.get(index).seedID, currentobject.get(index).length));
					
				}

			}

		}

		return lengthlist;

	}

	
	public static void GetLengthDistributionArray(ArrayList<File> AllMovies, double[] calibration) {

		ArrayList<Double> maxlist = new ArrayList<Double>();
		for (int i = 0; i < AllMovies.size(); ++i) {

			double maxlength = LengthDistribution.Lengthdistro(AllMovies.get(i));

			if (maxlength != Double.NaN && maxlength > 0)
				maxlist.add(maxlength);

		}
		Collections.sort(maxlist);

		int min = 0;
		int max = (int) Math.round(maxlist.get(maxlist.size() - 1)) + 1;
		XYSeries counterseries = new XYSeries("MT length distribution");
		XYSeries Logcounterseries = new XYSeries("MT Log length distribution");
		final ArrayList<Point> points = new ArrayList<Point>();
		for (int length = 0; length < max; ++length) {

			HashMap<Integer, Integer> frameseed = new HashMap<Integer, Integer>();

			int count = 0;
			for (int i = 0; i < AllMovies.size(); ++i) {

				File file = AllMovies.get(i);

				double currentlength = LengthDistribution.Lengthdistro(file);

				ArrayList<FLSobject> currentobject = Tracking.loadMTStat(file);

				if (currentlength > length) {


					
					for (int index = 0; index < currentobject.size(); ++index) {
						ArrayList<Integer> seedlist = new ArrayList<Integer>();
						if (currentobject.get(index).length >= length) {
							seedlist.add(currentobject.get(index).seedID);
							if (frameseed.get(currentobject.get(index).Framenumber) != null
									&& frameseed.get(currentobject.get(index).Framenumber) != Double.NaN) {

								int currentcount = frameseed.get(currentobject.get(index).Framenumber);
								frameseed.put(currentobject.get(index).Framenumber, seedlist.size() + currentcount);
							} else if (currentobject.get(index) != null)
								frameseed.put(currentobject.get(index).Framenumber, seedlist.size() );

						}

					}

				}

			}
			
			
			// Get maxima length, count
			int maxvalue = Integer.MIN_VALUE;
			
			for (int key: frameseed.keySet()){
				
				int Count = frameseed.get(key);
				
				if (Count >= maxvalue)
					maxvalue = Count;
			}
			
			if (maxvalue!=Integer.MIN_VALUE){
				counterseries.add(length , maxvalue );

				if (maxvalue > 0){
			 Logcounterseries.add((length ), Math.log(maxvalue));
			 points.add(new Point(new double[]{length , Math.log(maxvalue) }));
				}
			
			}
		}
		
		final XYSeriesCollection dataset = new XYSeriesCollection();
		final XYSeriesCollection nofitdataset = new XYSeriesCollection();
		  dataset.addSeries(counterseries); 
		  nofitdataset.addSeries(counterseries); 
		  final XYSeriesCollection Logdataset = new XYSeriesCollection();
		  Logdataset.addSeries(Logcounterseries); 
		  
		  final JFreeChart chart =
		  ChartFactory.createScatterPlot("MT length distribution",
		  "Number of MT","Length (micrometer)",  dataset);
		  
		  final JFreeChart nofitchart =
				  ChartFactory.createScatterPlot("MT length distribution",
				  "Number of MT","Length (micrometer)",  nofitdataset);
		  
		  // Fitting line to log of the length distribution
		  interpolation.Polynomial poly = new interpolation.Polynomial(1);
			 try {
				
				 
				 poly.fitFunction(points);
				
				 
				
			} catch (NotEnoughDataPointsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 DisplayPoints.display(nofitchart, new Dimension(800, 500));
			 dataset.addSeries(Tracking.drawexpFunction(poly, counterseries.getMinX(), counterseries.getMaxX(), 0.5, "Exponential fit"));
			 NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
				nf.setMaximumFractionDigits(3);
			 TextTitle legendText = new TextTitle("Mean Length" + " : "
		  			 + nf.format(-1.0/poly.getCoefficients(1)) +"  " +  "Standard Deviation" + " : " + nf.format(poly.SSE));
		  				 legendText.setPosition(RectangleEdge.RIGHT);
			 
		  DisplayPoints.display(chart, new Dimension(800, 500));
		  chart.addSubtitle(legendText);
		  
		  final JFreeChart logchart =
				  ChartFactory.createScatterPlot("MT Log length distribution",
						  "Number of MT","Length (micrometer)", Logdataset);
	//	  DisplayPoints.display(logchart, new Dimension(800, 500));
		  for (int i = 1; i >= 0; --i)
				System.out.println(poly.getCoefficients(i) + "  " + "x" + " X to the power of "  + i );
		  
		  
		//  Logdataset.addSeries(Tracking.drawFunction(poly, counterseries.getMinX(), counterseries.getMaxX(), 0.5, "Straight line fit"));
		  
		  WriteLengthdistroFile(AllMovies, counterseries, 0);
		  
	}
	
	public static void WriteLengthdistroFile(ArrayList<File> AllMovies, XYSeries counterseries, int framenumber) {
		

			try {
				
				
				File ratesfile = new File(AllMovies.get(0).getParentFile() +  "//" + "Length-Distribution At T " + " = " + framenumber + ".txt");

				if (framenumber == 0)
					ratesfile = new File(AllMovies.get(0).getParentFile()  + "//" + "Mean Length-Distribution" + ".txt");
					
				
				FileWriter fw = new FileWriter(ratesfile);

				BufferedWriter bw = new BufferedWriter(fw);



				bw.write("\tLength(real units) \tCount\n");
				

				
	                          for (int index =  0; index < counterseries.getItems().size(); ++index){
	                        	  
	                        	  double Count = counterseries.getX(index).doubleValue();
	                        	  double Length = counterseries.getY(index).doubleValue();
	                        	  
	                        	  
								bw.write("\t" + Length+ "\t" + "\t" + Count  + "\t" + "\n");
								

							}

					

					
			
				bw.close();
				fw.close();

			
			}

			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		
	}
	
	
	public static void GetLengthDistributionArrayatTime(ArrayList<File> AllMovies, double[] calibration, final int framenumber) {

		ArrayList<Double> maxlist = new ArrayList<Double>();
		for (int i = 0; i < AllMovies.size(); ++i) {

			ArrayList<Pair<Integer, Double>> lengthlist = LengthDistribution.LengthdistroatTime(AllMovies.get(i), framenumber);

			for (int index = 0; index < lengthlist.size(); ++index){
			if (lengthlist.get(index).getB() != Double.NaN && lengthlist.get(index).getB() > 0)
				maxlist.add(lengthlist.get(index).getB());

		}
		}
		Collections.sort(maxlist);

		int min = 0;
		int max = 0;
		if(maxlist.size() > 0)
	    max = (int) Math.round(maxlist.get(maxlist.size() - 1)) + 1;
		XYSeries counterseries = new XYSeries("MT length distribution");
		XYSeries Logcounterseries = new XYSeries("MT Log length distribution");
		final ArrayList<Point> points = new ArrayList<Point>();
		for (int length = 0; length < max; ++length) {

			HashMap<Integer, Integer> frameseed = new HashMap<Integer, Integer>();

			int count = 0;
			for (int i = 0; i < AllMovies.size(); ++i) {

				File file = AllMovies.get(i);


				ArrayList<FLSobject> currentobject = Tracking.loadMTStat(file);



					if (currentobject!=null)
					for (int index = 0; index < currentobject.size(); ++index) {
						ArrayList<Integer> seedlist = new ArrayList<Integer>();
						if (currentobject.get(index).length >= length && currentobject.get(index).Framenumber == framenumber) {
							seedlist.add(currentobject.get(index).seedID);
							if (frameseed.get(currentobject.get(index).Framenumber) != null 
									&& frameseed.get(currentobject.get(index).Framenumber) != Double.NaN) {

								int currentcount = frameseed.get(currentobject.get(index).Framenumber);
								frameseed.put(currentobject.get(index).Framenumber, seedlist.size() + currentcount);
							} else if (currentobject.get(index) != null )
								frameseed.put(currentobject.get(index).Framenumber, seedlist.size() );

						}


				}

			}
			
			
			// Get maxima length, count
			int maxvalue = Integer.MIN_VALUE;
			
			for (int key: frameseed.keySet()){
				
				int Count = frameseed.get(key);
				
				if (Count >= maxvalue)
					maxvalue = Count;
			}
			
			if (maxvalue!=Integer.MIN_VALUE){
				counterseries.add(length , maxvalue );

				if (maxvalue > 0){
					
					System.out.println("Max " + maxvalue);
			 Logcounterseries.add((length ), Math.log(maxvalue));
			 points.add(new Point(new double[]{length , Math.log(maxvalue) }));
				}
			
			}
		}
		
		final XYSeriesCollection dataset = new XYSeriesCollection();
		final XYSeriesCollection nofitdataset = new XYSeriesCollection();
		  dataset.addSeries(counterseries); 
		  nofitdataset.addSeries(counterseries); 
		  final XYSeriesCollection Logdataset = new XYSeriesCollection();
		  Logdataset.addSeries(Logcounterseries); 
		  
		  final JFreeChart chart =
		  ChartFactory.createScatterPlot("MT length distribution",
				  "Number of MT","Length (micrometer)",  dataset);
		  final JFreeChart nofitchart =
				  ChartFactory.createScatterPlot("MT length distribution",
				  "Number of MT","Length (micrometer)",  nofitdataset);
		  // Fitting line to log of the length distribution
		  interpolation.Polynomial poly = new interpolation.Polynomial(1);
			 try {
				
				 
				 poly.fitFunction(points);
				
				 
				
			} catch (NotEnoughDataPointsException e) {
				
			}
			 DisplayPoints.display(nofitchart, new Dimension(800, 500));
			 dataset.addSeries(Tracking.drawexpFunction(poly, counterseries.getMinX(), counterseries.getMaxX(), 0.5, "Exponential fit"));
			 NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
				nf.setMaximumFractionDigits(3);
			 TextTitle legendText = new TextTitle("Mean Length" + " : "
		  			 + nf.format(-1.0/poly.getCoefficients(1)) +"  " +  "Standard Deviation" + " : " + nf.format(poly.SSE));
		  				 legendText.setPosition(RectangleEdge.RIGHT);
			 
		  DisplayPoints.display(chart, new Dimension(800, 500));
		  chart.addSubtitle(legendText);
		  
		  System.out.println("Series count" + dataset.getSeriesCount());
		  final JFreeChart logchart =
				  ChartFactory.createScatterPlot("MT Log length distribution",
				  "Length (micrometer)", "Number of MT", Logdataset);
	//	  DisplayPoints.display(logchart, new Dimension(800, 500));
		  for (int i = 1; i >= 0; --i)
				System.out.println(poly.getCoefficients(i) + "  " + "x" + " X to the power of "  + i );
		  
		  
		//  Logdataset.addSeries(Tracking.drawFunction(poly, counterseries.getMinX(), counterseries.getMaxX(), 0.5, "Straight line fit"));
		  WriteLengthdistroFile(AllMovies, counterseries, framenumber);
	}
	
}
