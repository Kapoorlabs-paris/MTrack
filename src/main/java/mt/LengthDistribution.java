package mt;

import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class LengthDistribution {

	public static ArrayList<LengthCounter> Lengthdistro(File file) {

		ArrayList<FLSobject> currentobject = Tracking.loadMTStat(file);

		ArrayList<LengthCounter> currentcounter = new ArrayList<LengthCounter>();
		int starttime = currentobject.get(0).Framenumber;
		int endtime = currentobject.get(currentobject.size() - 1).Framenumber;

		for (int frameindex = starttime; frameindex <= endtime; ++frameindex) {

			double length = 0;

			for (int index = 0; index < currentobject.size(); ++index) {

				int time = currentobject.get(index).Framenumber;

				if (time == frameindex) {

					length += currentobject.get(index).length;

				
				}

			}

			if (length > 0)
			currentcounter.add(new LengthCounter(frameindex, length));
			
		}
		
		return currentcounter;

	}
	
	public static void GetLengthDistribution(File[] AllMovies){
		
		
		ArrayList<ArrayList<LengthCounter>> Allcurrentcounter = new ArrayList<ArrayList<LengthCounter>>();
		
		for (int i = 0; i < AllMovies.length ; ++i){
			
			
			ArrayList<LengthCounter> currentcounter = 	LengthDistribution.Lengthdistro(AllMovies[i]);
			
			Allcurrentcounter.add(currentcounter);
	
			
			
		}
		
		XYSeries counterseries = new XYSeries( "MT length distribution" );
		
		for (int index = 0; index < Allcurrentcounter.size(); ++index){
			
			for (final LengthCounter lvst : Allcurrentcounter.get(index))
				counterseries.add(lvst.Framenumber, lvst.totallength);
			
		}
		final XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(counterseries);
		final JFreeChart chart = ChartFactory.createScatterPlot("MT length distribution", "Time (px)", "Length (px)", dataset);
		
		DisplayPoints.display( chart, new Dimension( 800, 500 ) );

	}


}
