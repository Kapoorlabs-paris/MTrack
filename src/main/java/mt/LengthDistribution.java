package mt;

import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;

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

	public static void GetLengthDistribution(File[] AllMovies) {

		ArrayList<Double> maxlist = new ArrayList<Double>();
		for (int i = 0; i < AllMovies.length; ++i) {

			double maxlength = LengthDistribution.Lengthdistro(AllMovies[i]);

			if (maxlength != Double.NaN && maxlength > 0)
				maxlist.add(maxlength);

		}
		Collections.sort(maxlist);

		int min = 0;
		int max = (int) Math.round(maxlist.get(maxlist.size() - 1)) + 1;
		XYSeries counterseries = new XYSeries("MT length distribution");

		for (int length = 0; length < max; ++length) {

			HashMap<Integer, Integer> frameseed = new HashMap<Integer, Integer>();

			int count = 0;
			for (int i = 0; i < AllMovies.length; ++i) {

				File file = AllMovies[i];

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
			
			if (maxvalue!=Integer.MIN_VALUE)
				counterseries.add(length, maxvalue );

			
			
			  
			 

		}
		
		final XYSeriesCollection dataset = new XYSeriesCollection();
		  dataset.addSeries(counterseries); final JFreeChart chart =
		  ChartFactory.createScatterPlot("MT length distribution",
		  "Length (px)", "Number of MT", dataset);
		  
		  DisplayPoints.display(chart, new Dimension(800, 500));
	}
}