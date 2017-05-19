package visualize;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import fit.polynomial.HigherOrderPolynomialFunction;
import fit.polynomial.InterpolatedPolynomial;
import fit.polynomial.LinearFunction;
import fit.polynomial.QuadraticFunction;
import mpicbg.models.IllDefinedDataPointsException;
import mpicbg.models.NotEnoughDataPointsException;
import mpicbg.models.Point;

/**
 * @author Stephan Preibisch
 */
public class Visualize
{
	public static void main( String[] args ) throws NotEnoughDataPointsException, IllDefinedDataPointsException
	{
		XYSeries seriesQ = new XYSeries("quadratic");
		XYSeries seriesL = new XYSeries("linear");
		XYSeries seriesI = new XYSeries("intepolated");

		final ArrayList< Point > pointsQ = new ArrayList<Point>();

		for ( double x = -5.0; x <= 5.0; x = x + 0.5 )
			pointsQ.add( new Point( new double[]{ x, 2.0*x*x*x - 10*x*x } ) );

		final LinearFunction fl = new LinearFunction();
		final HigherOrderPolynomialFunction fq = new HigherOrderPolynomialFunction( 3 );
		final InterpolatedPolynomial< LinearFunction, HigherOrderPolynomialFunction > fi =
				new InterpolatedPolynomial< LinearFunction, HigherOrderPolynomialFunction >(
						new LinearFunction(),
						fq.copy(),
						0.5 );

		fl.fitFunction( pointsQ );
		fq.fitFunction( pointsQ );
		fi.fitFunction( pointsQ );

		System.out.println( fl );
		System.out.println( fq );
		System.out.println( fi.interpolatedFunction );

		for ( double x = -5.0; x <= 5.0; x = x + 0.5 )
		{
			seriesQ.add( x, fq.predict( x ) );
			seriesL.add( x, fl.predict( x ) );
			seriesI.add( x, fi.predict( x ) );
		}

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries( seriesQ );
		dataset.addSeries( seriesL );
		dataset.addSeries( seriesI );

		JFreeChart chart = ChartFactory.createXYLineChart(
			"XY Chart",
			"x-axis",
			"y-axis",
			dataset, 
			PlotOrientation.VERTICAL,
			true,
			true,
			false
			);

		final XYPlot plot = chart.getXYPlot();
		final XYItemRenderer renderer = plot.getRenderer();
		renderer.setSeriesPaint( 0, new Color(0, 0, 255) );
		renderer.setSeriesStroke( 0, new BasicStroke ( 0.5f ) );
		renderer.setSeriesPaint( 1, new Color(255, 0, 0) );
		renderer.setSeriesStroke( 1, new BasicStroke ( 0.5f ) );
		renderer.setSeriesPaint( 2, new Color(0, 200, 40) );
		renderer.setSeriesStroke( 2, new BasicStroke ( 1.5f ) );

		//chart.getXYPlot().setRenderer(new XYSplineRenderer(100));

		JPanel panel = new JPanel();
		ChartPanel chartPanel = new ChartPanel(chart);
		panel.add(chartPanel);

		JFrame frame = new JFrame();
		frame.setContentPane( panel );
		frame.validate();
		Dimension d = new Dimension( 800, 500 );
		frame.setSize( d );

		frame.setVisible( true );

		try { Thread.sleep( 3000 ); } catch ( InterruptedException e ) { e.printStackTrace(); }
		System.out.println( "starting" );

		for ( int lambda = 0; lambda <= 100; ++lambda )
		{
			fi.setLambda( lambda / 100.0 );
			fi.fitFunction( pointsQ );
			System.out.println( fi.interpolatedFunction );
	
			dataset.getSeries( 2 ).clear();
			for ( double x = -5.0; x <= 5.0; x = x + 0.5 )
				seriesI.add( x, fi.predict( x ) );

			try { Thread.sleep( 100 ); } catch ( InterruptedException e ) { e.printStackTrace(); }
		//	makeScreenshot( lambda );
		}

	}

	public static void makeScreenshot( final int index )
	{
		makeScreenshot( new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()), index);
	}

	public static void makeScreenshot( final Rectangle rect, final int index )
	{
		try
		{
			BufferedImage image = new Robot().createScreenCapture( rect );
			ImageIO.write( image, "png", new File( "screenshot_" + index + ".png" ) );
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	public static GraphFrame plotData( final Function< ?, Point > f )
	{
        Color errorColorMin = new Color(240, 50, 50);
        Color errorColorAvg = new Color(255, 0, 0);
        Color errorColorMax = errorColorMin;

        Color ratioColorMin = new Color( 50, 50, 240 );
        Color ratioColorAvg = new Color( 0, 0, 255 );
        Color ratioColorMax = ratioColorMin;

		XYSeries seriesMinError = new XYSeries("minError");
		XYSeries seriesAvgError = new XYSeries("avgError");
		XYSeries seriesMaxError = new XYSeries("maxError");

		for ( RegistrationStatistics tp : data ) {
			seriesMinError.add( tp.timePoint, tp.minError );
			seriesAvgError.add( tp.timePoint, tp.avgError );
			seriesMaxError.add( tp.timePoint, tp.maxError );
		}

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries( seriesMinError );
		dataset.addSeries( seriesAvgError );
		dataset.addSeries( seriesMaxError );

		XYSeries seriesMinRatio = new XYSeries("minRatio");
		XYSeries seriesAvgRatio = new XYSeries("avgRatio");
		XYSeries seriesMaxRatio = new XYSeries("maxRatio");

		for ( RegistrationStatistics tp : data ) {
			seriesMinRatio.add( tp.timePoint, tp.minRatio*100 );
			seriesAvgRatio.add( tp.timePoint, tp.avgRatio*100 );
			seriesMaxRatio.add( tp.timePoint, tp.maxRatio*100 );
		}

		XYSeriesCollection dataset2 = new XYSeriesCollection();
		dataset2.addSeries( seriesMinRatio );
		dataset2.addSeries( seriesAvgRatio );
		dataset2.addSeries( seriesMaxRatio );

		JFreeChart chart = ChartFactory.createXYLineChart
							( "Registration Quality",  // Title
		                      "Timepoint",             // X-Axis label
		                      "Error [px]",                 // Y-Axis label
		                      dataset,
		                      PlotOrientation.VERTICAL,
		                      true,                    // Show legend
		                      false,				   // show tooltips
		                      false
		                     );
        final XYPlot plot = chart.getXYPlot();
        final NumberAxis axis2 = new NumberAxis( "Correspondence Ratio  [%]" );
        plot.getRangeAxis( 0 ).setLabelPaint( errorColorAvg );
        axis2.setLabelPaint( ratioColorAvg );
        axis2.setLabelFont( plot.getRangeAxis( 0 ).getLabelFont() );
        axis2.setRange( 0.0, 100 );
        plot.setRangeAxis( 1, axis2 );
        plot.setDataset( 1, dataset2);
        plot.mapDatasetToRangeAxis( 1, 1 );
        final XYItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesPaint( 0, errorColorMin );
        renderer.setSeriesStroke( 0, new BasicStroke ( 0.5f ) );
        renderer.setSeriesPaint( 1, errorColorAvg );
        renderer.setSeriesStroke( 1, new BasicStroke ( 1.5f ) );
        renderer.setSeriesPaint( 2, errorColorMax );
        renderer.setSeriesStroke( 2, new BasicStroke ( 0.5f ) );

        final StandardXYItemRenderer renderer2 = new StandardXYItemRenderer();
        renderer2.setSeriesPaint( 0, ratioColorMin );
        renderer2.setSeriesStroke( 0, new BasicStroke ( 0.5f ) );
        renderer2.setSeriesPaint( 1, ratioColorAvg );
        renderer2.setSeriesStroke( 1, new BasicStroke ( 1.5f ) );
        renderer2.setSeriesPaint( 2, ratioColorMax );
        renderer2.setSeriesStroke( 2, new BasicStroke ( 0.5f ) );
        renderer2.setPlotImages( true );
        plot.setRenderer( 1, renderer2 );

        // Is it somehow possible to add a new tab to this Properties menu item?        
		GraphFrame graphFrame = new GraphFrame( chart );
		
		Dimension d = new Dimension( 800, 400 );	
		graphFrame.setSize( d );

		// resizing fucks up the interaction
		graphFrame.setResizable( false );
		
		graphFrame.setVisible(true);
		
		return graphFrame;//.getReferenceTimePoint();
	}
	*/
}
