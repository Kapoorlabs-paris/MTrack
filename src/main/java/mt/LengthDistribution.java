package mt;

import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class LengthDistribution {

	public static double Lengthdistro(File file) {

		ArrayList<FLSobject> currentobject = Tracking.loadMTStat(file);
		

		double meanlength = 0;


		if (currentobject!=null){
			for (int index = 0; index < currentobject.size(); ++index) {

				meanlength += currentobject.get(index).length;
	
			}
			
		meanlength/=currentobject.size();
		
		System.out.println(meanlength);
		}		
		return meanlength;

	}
	
	public static void GetLengthDistribution(File[] AllMovies){
		
		
		
		
		
		ArrayList<Double> Allmeans = new ArrayList<Double>();
		
		for (int i = 0; i < AllMovies.length ; ++i){
			
			double meanlength = LengthDistribution.Lengthdistro(AllMovies[i]);
			
			if (meanlength > 0  && meanlength!=Double.NaN)
           Allmeans.add(meanlength);
			
	
			
		}
		
		Collections.sort(Allmeans);
		
		int min = (int)Math.round(Allmeans.get(0)) - 1;
		int max = (int)Math.round(Allmeans.get(Allmeans.size() - 1)) + 1;
		
		XYSeries counterseries = new XYSeries( "MT length distribution" );
		for (int maxlength = min; maxlength < max; ++maxlength) {

			int MTcount = 0;
			
			
			for (int index = 0; index < Allmeans.size(); ++index){
				
				if (Allmeans.get(index) >= maxlength)
					MTcount++;
				}
			
			counterseries.add(MTcount, maxlength);
			
		}
		
		
		
		
		final XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(counterseries);
		final JFreeChart chart = ChartFactory.createScatterPlot("MT length distribution", "Number of MT", "Length (px)", dataset);
		
		DisplayPoints.display( chart, new Dimension( 800, 500 ) );

	}


}
