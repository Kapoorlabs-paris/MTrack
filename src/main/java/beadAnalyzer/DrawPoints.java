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
package beadAnalyzer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import net.imglib2.util.ValuePair;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.util.ShapeUtils;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import psf_Tookit.GaussianFitParam;
import psf_Tookit.GaussianLineFitParam;

public class DrawPoints {

	
	protected double min, max;
	
	public DrawPoints (){
		
	};
	
	
     public  XYSeries drawLinePoints(final ArrayList<GaussianLineFitParam> beadLineparams){
		
		return drawLinePoints(beadLineparams, "Sigma", 1000);
	}
	
	
	
	public  XYSeries drawPoints(final ArrayList<GaussianFitParam> beadparams){
		
		return drawPoints(beadparams, "Sigma", 1000);
	}
	
	public  XYSeries drawPoints(final ArrayList<GaussianFitParam> beadparams, final String name, final int numBins){
		
		final XYSeriesCollection dataset = new XYSeriesCollection();
		
		
		XYSeries seriesname = new XYSeries(name);
		
		List<Double> Xvalues = new ArrayList<Double>();
		List<Double> Yvalues =  new ArrayList<Double>();
		
		
		if (name == "Sigma"){
		
		for (final GaussianFitParam param : beadparams ){
			if (Math.abs(param.Sigma[0])<=10000 && Math.abs(param.Sigma[1])<=100000){ 
				
			seriesname.add(param.Sigma[0], param.Sigma[1]);
			Xvalues.add(param.Sigma[0]);
			Yvalues.add(param.Sigma[1]);
			
		
			
			}
		}
		
		}
		
		dataset.addSeries(seriesname);
	
	
		
		final JFreeChart histXchart = makehistXChart(Xvalues, numBins);
		final JFreeChart histYchart = makehistYChart(Yvalues, numBins);
		
		
		
		
		display( histXchart, new Dimension( 500, 500 ) );
		display( histYchart, new Dimension( 500, 500 ) );
		
		
		return seriesname;
	}
	
	 
       public  XYSeries drawLinePoints(final ArrayList<GaussianLineFitParam> beadLineparams, final String name, final int numBins){
		
		final XYSeriesCollection dataset = new XYSeriesCollection();
		
		
		XYSeries seriesname = new XYSeries(name);
		
		List<Double> Xvalues = new ArrayList<Double>();
		List<Double> Yvalues =  new ArrayList<Double>();
		
		
		if (name == "Sigma"){
		
		for (final GaussianLineFitParam param : beadLineparams ){
			if (Math.abs(param.Sigma[0])<=10000 && Math.abs(param.Sigma[1])<=100000){ 
			seriesname.add(param.Sigma[0], param.Sigma[1]);
			Xvalues.add(param.Sigma[0]);
			Yvalues.add(param.Sigma[1]);
			}
		}
		
		}
		
		dataset.addSeries(seriesname);
	
		
		
		final JFreeChart histXchart = makehistXChart(Xvalues, numBins);
		final JFreeChart histYchart = makehistYChart(Yvalues, numBins);
		
		
		
		
		display( histXchart, new Dimension( 500, 500 ) );
		display( histYchart, new Dimension( 500, 500 ) );
		
		return seriesname;
	}
	
	
	
	
	public static List< ValuePair< Double, Integer > > binData( final List< Double > data, final double min, final double max, final int numBins )
	{
		// avoid the one value that is exactly 100%
		final double size = max - min + 0.000001;

		// bin and count the entries
		final int[] bins = new int[ numBins ];

		for ( final double v : data )
			++bins[ (int)Math.floor( ( ( v - min ) / size ) * numBins ) ];

		// make the list of bins
		final ArrayList< ValuePair< Double, Integer > > hist = new ArrayList< ValuePair< Double, Integer > >();

		final double binSize = size / numBins;
		for ( int bin = 0; bin < numBins; ++bin )
			hist.add( new ValuePair< Double, Integer >( min + binSize/2 + binSize * bin, bins[ bin ] ) );

		return hist;
	}
	
	public double getMin() { return min; }
	public double getMax() { return max; }

	public static ValuePair< Double, Double > getMinMax( final List< Double > data )
	{
		// compute min/max/size
		double min = data.get( 0 );
		double max = data.get( 0 );

		for ( final double v : data )
		{
			min = Math.min( min, v );
			max = Math.max( max, v );
		}

		return new ValuePair< Double, Double >( min, max );
	}
	public  IntervalXYDataset createDataset( final List< Double > values, final int numBins, final String title )
	{
		final XYSeries series = new XYSeries( title );

		final ValuePair< Double, Double > minmax = getMinMax( values );
		this.min = minmax.getA();
		this.max = minmax.getB();

		final List< ValuePair< Double, Integer > > hist = binData( values, min, max, numBins );
		
		for ( final ValuePair< Double, Integer > pair : hist )
			series.add( pair.getA(), pair.getB() );

		final XYSeriesCollection dataset = new XYSeriesCollection( series );
		dataset.setAutoWidth( true );

		return dataset;
	}
	
	public  JFreeChart makehistXChart( final List<Double> Xdataset, final int numBins ){
		return makehistXChart( Xdataset, "Histogram", "SigmaX", "Count", numBins );
	}
		
		
	public  JFreeChart makehistXChart( final List<Double> Xdataset,  final String title, final String x, final String y, final int numBins ){
		
		
		final IntervalXYDataset SigmaXdataset = createDataset( Xdataset, numBins, title );
		
		final JFreeChart sigmaXchart = createChart( SigmaXdataset, title, x );
		
		
		

		return sigmaXchart;
	}
	
	public  JFreeChart makehistYChart( final List<Double> Ydataset, final int numBins ){
		return makehistXChart( Ydataset, "Histogram", "SigmaY", "Count", numBins );
	}
		
		
	public  JFreeChart makehistYChart( final List<Double> Ydataset,  final String title, final String x, final String y, final int numBins ){
		
		
		final IntervalXYDataset SigmaYdataset = createDataset( Ydataset, numBins, title );
		
		final JFreeChart sigmaYchart = createChart( SigmaYdataset, title, x );
		

		return sigmaYchart;
	}
	
	protected JFreeChart createChart( final IntervalXYDataset dataset, final String title, final String units )
	{
		
		
		
		final JFreeChart chart = ChartFactory.createXYBarChart(
			title,
			"Pixel [" + units + "]", 
			false,
			"Count", 
			dataset,
			PlotOrientation.VERTICAL,
			false, // legend
			false,
			false );

		NumberAxis range = (NumberAxis) chart.getXYPlot().getDomainAxis();
		range.setRange( getMin(), getMax() );

		XYPlot plot = chart.getXYPlot();
		XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
		
		renderer.setSeriesPaint( 0, Color.red );
		renderer.setDrawBarOutline( true );
		renderer.setSeriesOutlinePaint( 0, Color.black );
		renderer.setBarPainter( new StandardXYBarPainter() );

		return chart;
	}
	
	
	
	
	
	public static void setColor( final JFreeChart chart, final int seriesIndex, final Color col )
	{
		final XYPlot plot = chart.getXYPlot();
		final XYItemRenderer renderer = plot.getRenderer();
		renderer.setSeriesPaint( seriesIndex, col );
	}

	public static void setStroke( final JFreeChart chart, final int seriesIndex, final float stroke )
	{
		final XYPlot plot = chart.getXYPlot();
		final XYItemRenderer renderer = plot.getRenderer();
		renderer.setSeriesStroke( seriesIndex, new BasicStroke( stroke ) );
	}

	public static void setShape( final JFreeChart chart, final int seriesIndex, final Shape shape )
	{
		final XYPlot plot = chart.getXYPlot();
		final XYItemRenderer renderer = plot.getRenderer();
		renderer.setSeriesShape( seriesIndex, shape );
	}

	public static void setSmallUpTriangleShape( final JFreeChart chart, final int seriesIndex )
	{
		final XYPlot plot = chart.getXYPlot();
		final XYItemRenderer renderer = plot.getRenderer();
		renderer.setSeriesShape( seriesIndex, ShapeUtils.createUpTriangle( 0.5f ) );
	}

	public static void setDisplayType( final JFreeChart chart, final int seriesIndex, final boolean line, final boolean shape )
	{
		final XYPlot plot = chart.getXYPlot();
		final XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)plot.getRenderer();
		renderer.setSeriesLinesVisible( seriesIndex, line );
		renderer.setSeriesShapesVisible( seriesIndex, shape );
	}

	

	
	public static JFrame display( final JFreeChart chart ) { return display( chart, new Dimension( 800, 500 ) ); }
	public static JFrame display( final JFreeChart chart, final Dimension d )
	{
		final JPanel panel = new JPanel();
		final ChartPanel chartPanel = new ChartPanel(
				chart,
				d.width - 10,
				d.height - 35,
				ChartPanel.DEFAULT_MINIMUM_DRAW_WIDTH,
				ChartPanel.DEFAULT_MINIMUM_DRAW_HEIGHT,
				ChartPanel.DEFAULT_MAXIMUM_DRAW_WIDTH,
				ChartPanel.DEFAULT_MAXIMUM_DRAW_HEIGHT,
				ChartPanel.DEFAULT_BUFFER_USED,
				true,  // properties
				true,  // save
				true,  // print
				true,  // zoom
				true   // tooltips
				);
		panel.add( chartPanel );

		final JFrame frame = new JFrame();
		frame.setContentPane( panel );
		frame.validate();
		frame.setSize( d );

		frame.setVisible( true );
		return frame;
	}

	
}
