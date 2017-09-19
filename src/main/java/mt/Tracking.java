package mt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.resources.JFreeChartResources;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

import fit.AbstractFunction2D;
import fit.PointFunctionMatch;
import fit.polynomial.HigherOrderPolynomialFunction;
import fit.polynomial.InterpolatedPolynomial;
import fit.polynomial.LinearFunction;
import fit.polynomial.Polynomial;
import mpicbg.models.NotEnoughDataPointsException;
import mpicbg.models.Point;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;

public class Tracking
{
	
	
	public static ArrayList< FLSobject > loadMTStat( final File file )
	{
		final ArrayList< FLSobject > points = new ArrayList< FLSobject >();

		try
		{
			BufferedReader in = Util.openFileRead( file );

			while( in.ready() )
			{
				String line = in.readLine().trim();

				while ( line.contains( "\t\t" ) )
					line = line.replaceAll( "\t\t", "\t" );

				if ( line.length() >= 3 && line.matches( "[0-9].*" ) )
				{
					final String[] split = line.trim().split( "\t" );

					final int frame = (int)Double.parseDouble( split[ 0 ] );
					final double length = Double.parseDouble( split[ 1 ] );
					final int seedID = (int)Double.parseDouble( split[ 3 ] );

					FLSobject statobject = new FLSobject(frame, seedID, length);
					points.add( statobject);
				}
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			return null;
		}

		Collections.sort( points, new Comparator< FLSobject >()
		{
			@Override
			public int compare( final FLSobject o1, final FLSobject o2 )
			{
				
				final int t1 = o1.Framenumber;
				final int t2 = o2.Framenumber;
				
				
				if (t1 < t2)
					return -1;
				else if (t1 == t2)
					return 0;
				else
					return 1;
			}
		} );

		return points;
	}
	
	public static double[] loadCalibration(final File file)
	{
		
		double[] calibrations = new double[3];
		double calibrationX = 1, calibrationY = 1, calibrationT = 1;
		try
		{
			BufferedReader in = Util.openFileRead( file );

			while( in.ready() )
			{
				String line = in.readLine().trim();

				while ( line.contains( "\t\t" ) )
					line = line.replaceAll( "\t\t", "\t" );

				if ( line.length() >= 3 && line.matches( "[0-9].*" ) )
				{
					final String[] split = line.trim().split( "\t" );

					calibrationX = Double.parseDouble( split[ 10 ] );
				    calibrationY = Double.parseDouble( split[ 11 ] );
					calibrationT = Double.parseDouble( split[ 12 ] );

				}
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			return null;
		}
		
		calibrations[0] = calibrationX;
		calibrations[1] = calibrationY;
		calibrations[2] = calibrationT;
		
		return calibrations;
	}
	
	public static ArrayList< Pair< Integer, Double > > loadMT( final File file )
	{
		final ArrayList< Pair< Integer, Double > > points = new ArrayList< Pair< Integer, Double > >();

		try
		{
			BufferedReader in = Util.openFileRead( file );

			while( in.ready() )
			{
				String line = in.readLine().trim();

				while ( line.contains( "\t\t" ) )
					line = line.replaceAll( "\t\t", "\t" );

				if ( line.length() >= 3 && line.matches( "[0-9].*" ) )
				{
					final String[] split = line.trim().split( "\t" );

					final int frame = (int)Double.parseDouble( split[ 0 ] );
					final double length = Double.parseDouble( split[ 1 ] );

					points.add( new ValuePair< Integer, Double >( frame, length ) );
				}
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			return null;
		}

		Collections.sort( points, new Comparator< Pair< Integer, Double > >()
		{
			@Override
			public int compare( final Pair< Integer, Double > o1, final Pair< Integer, Double > o2 )
			{
				return o1.getA().compareTo( o2.getA() );
			}
		} );

		return points;
	}

	
	public static ArrayList< Pair< Integer, Double > > loadsimple( final File file )
	{
		final ArrayList< Pair< Integer, Double > > points = new ArrayList< Pair< Integer, Double > >();
		final ArrayList< Pair< Integer, Double > > normalpoints = new ArrayList< Pair< Integer, Double > >();
		try
		{
			BufferedReader in = Util.openFileRead( file );

			while( in.ready() )
			{
				String line = in.readLine().trim();

				while ( line.contains( "\t\t" ) )
					line = line.replaceAll( "\t\t", "\t" );

				if ( line.length() >= 3 && line.matches( "[0-9].*" ) )
				{
					final String[] split = line.trim().split( "\t" );

					final int frame = (int)Double.parseDouble( split[ 0 ] );
					final double length = Double.parseDouble( split[ 1 ] );

					points.add( new ValuePair< Integer, Double >( frame, length ) );
				}
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			return null;
		}

		double maxlength = Double.MIN_VALUE;
		for( Pair< Integer, Double> point:points){
			
			double length = point.getB();
			if ( length > maxlength)
				maxlength = length;
		}
		for( Pair< Integer, Double> point:points){
			Pair< Integer, Double> newpoint = new ValuePair< Integer, Double >(point.getA()/ 10 , point.getB() / maxlength );
			System.out.println(point.getA() + " " + point.getB());
			normalpoints.add(newpoint);
		}
		
		return normalpoints;
	}
	
	public static XYSeries drawPoints( final List< Pair< Integer, Double > > mts, double[] calibrations ) { return drawPoints( mts, calibrations, "MT Length" ); }
	public static XYSeries drawPoints( final List< Pair< Integer, Double > > mts, double[] calibrations, final String name )
	{
		XYSeries series = new XYSeries( name );

		if (mts!=null){
		for ( final Pair< Integer, Double > mt : mts )
			series.add(  mt.getA(), mt.getB() );
		}
		return series;
	}

	public static XYSeries drawFunction( final Polynomial< ?, Point > polynomial, final double from, final double to, final double step, final String name )
	{
		XYSeries series = new XYSeries( name );

		for ( double x = from; x <= to; x = x + step )
			series.add( x, polynomial.predict( x ) );

		return series;
	}
	public static XYSeries drawFunction( final Polynomial< ?, Point > polynomial, final double from, final double to, final double step, final double minY, final double maxY, final String name )
	{
		XYSeries series = new XYSeries( name );

		for ( double x = from; x <= to; x = x + step )
		{
			final double v = polynomial.predict( x );

			if ( v >= minY && v <= maxY )
				series.add( x, v );
		}

		return series;
	}
	
	public static JFreeChart makeChart( final XYSeriesCollection dataset ) { return makeChart( dataset, "XY Chart", "x-axis", "y-axis" ); }
	public static JFreeChart makeChart( final XYSeriesCollection dataset, final String title, final String x, final String y )
	{
		final JFreeChart chart = ChartFactory.createXYLineChart(
				title, x, y,
				dataset, 
				PlotOrientation.VERTICAL,
				true,
				true,
				false
				);

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
		renderer.setSeriesShape( seriesIndex, ShapeUtilities.createUpTriangle( 0.5f ) );
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

	public static ArrayList< Point > toPoints( final ArrayList< Pair< Integer, Double > > mts )
	{
		final ArrayList< Point > points = new ArrayList< Point >();

		
		for ( final Pair< Integer, Double > mt : mts )
			points.add( new Point( new double[]{ mt.getA(), mt.getB() } ) );
		
		return points;
	}

	public static ArrayList< Pair< Integer, Double > > toPairList( final ArrayList< PointFunctionMatch > points )
	{
		final ArrayList< Pair< Integer, Double > > mts = new ArrayList< Pair< Integer, Double > >();

		for ( final PointFunctionMatch p : points )
			mts.add( new ValuePair< Integer, Double >( (int)Math.round( p.getP1().getW()[ 0 ] ), p.getP1().getW()[ 1 ] ) );

		return mts;
	}

	public static < P extends AbstractFunction2D< P > > Pair< P, ArrayList< PointFunctionMatch > > findFunction( final ArrayList< Point > mts, final P function )
	{
		return findFunction( mts, function, 3.0, function.getMinNumPoints(), 6 );
	}

	public static < P extends AbstractFunction2D< P > > Pair< P, ArrayList< PointFunctionMatch > > findFunction(
			final ArrayList< Point > mts,
			final P function,
			final double maxError,
			final int minNumInliers,
			final int maxDist )
	{
		final ArrayList< PointFunctionMatch > candidates = new ArrayList<PointFunctionMatch>();
		final ArrayList< PointFunctionMatch > inliers = new ArrayList<PointFunctionMatch>();
		
		for ( final Point p : mts )
			candidates.add( new PointFunctionMatch( p ) );

		try
		{
			function.ransac( candidates, inliers, 100, maxError, 0, minNumInliers, maxDist );

			if ( inliers.size() >= function.getMinNumPoints() )
			{
				function.fit( inliers );
	
				//System.out.println( inliers.size() + "/" + candidates.size() );
				//System.out.println( function );
			}
			else
			{
				//System.out.println( "0/" + candidates.size() );
				return null;
			}
		}
		catch ( Exception e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		return new ValuePair< P, ArrayList< PointFunctionMatch > >( function, inliers );
	}

	/*
	 * 			if ( LinearFunction.slopeFits( result.getB(), linear, minSlope, maxSlope ) )
	 */
	public static < P extends AbstractFunction2D< P > > ArrayList< Pair< P, ArrayList< PointFunctionMatch > > > findAllFunctions(
			final ArrayList< Point > mts,
			final P function,
			final double maxError,
			final int minNumInliers,
			final int maxDist )
	{
		boolean fitted;

		final ArrayList< Point > remainingPoints = new ArrayList< Point >();
		remainingPoints.addAll( mts );

		final ArrayList< Pair< P, ArrayList< PointFunctionMatch > > > segments = new ArrayList< Pair<P,ArrayList<PointFunctionMatch>> >();

		
		do
		{
			fitted = false;

			final Pair< P, ArrayList< PointFunctionMatch > > f = findFunction( remainingPoints, function.copy(), maxError, minNumInliers, maxDist );

			if ( f != null && f.getB().size() > 0 )
			{
				fitted = true;
				segments.add( f );

				final ArrayList< Point > inlierPoints = new ArrayList< Point >();
				for ( final PointFunctionMatch p : f.getB() )
					inlierPoints.add( p.getP1() );

				remainingPoints.removeAll( inlierPoints );
			}
		}
		while ( fitted );

		return segments;
	}

	
	public static Pair< LinearFunction, ArrayList< PointFunctionMatch > > findLinearFunction(
			final ArrayList< Point > mts,
			final double maxError,
			final int minNumInliers,
			final int maxDist,
			final double minSlope,
			final double maxSlope )
	{
		final ArrayList< PointFunctionMatch > candidates = new ArrayList<PointFunctionMatch>();
		final ArrayList< PointFunctionMatch > inliers = new ArrayList<PointFunctionMatch>();
		
		for ( final Point p : mts )
			candidates.add( new PointFunctionMatch( p ) );

		final LinearFunction function = new LinearFunction();

		try
		{
			function.ransac( candidates, inliers, 1000, maxError, 0, minNumInliers, maxDist, minSlope, maxSlope );

			if ( inliers.size() >= function.getMinNumPoints() )
			{
				function.fit( inliers );
	
				System.out.println( inliers.size() + "/" + candidates.size() );
				System.out.println( function );
			}
			else
			{
				System.out.println( "0/" + candidates.size() );
				return null;
			}
		}
		catch ( Exception e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		return new ValuePair< LinearFunction, ArrayList< PointFunctionMatch > >( function, inliers );
	}
	public static < P extends AbstractFunction2D< P > > Pair<P, ArrayList<PointFunctionMatch>> findFunction(
			final ArrayList< Point > mts,
			final P function,
			final double maxError,
			final int minNumInliers,
			final double maxDist )
	{
		final ArrayList< PointFunctionMatch > candidates = new ArrayList<PointFunctionMatch>();
		final ArrayList< PointFunctionMatch > inliers = new ArrayList<PointFunctionMatch>();
		
		for ( final Point p : mts )
			candidates.add( new PointFunctionMatch( p ) );

		try
		{
			function.ransac( candidates, inliers, 100, maxError, 0.01, minNumInliers, maxDist );

			if ( inliers.size() >= function.getMinNumPoints() )
			{
				function.fit( inliers );
	
				//System.out.println( inliers.size() + "/" + candidates.size() );
				//System.out.println( function );
			}
			else
			{
				//System.out.println( "0/" + candidates.size() );
				return null;
			}
		}
		catch ( Exception e )
		{
			System.out.println( "Couldn't fit function: " + e );
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return null;
		}

		return new ValuePair< P, ArrayList< PointFunctionMatch > >( function, inliers );
	}

	public static Pair< Double, Double > fromTo( final ArrayList< PointFunctionMatch > points )
	{
		double min = points.get( 0 ).getP1().getW()[ 0 ];
		double max = min;

		for ( final PointFunctionMatch p : points )
		{
			final double value = p.getP1().getW()[ 0 ];
			min = Math.min( min, value );
			max = Math.max( max, value );
		}

		return new ValuePair< Double, Double >( min, max );
	}
	public static Pair< Double, Double > fromToY( final ArrayList< PointFunctionMatch > points )
	{
		double min = points.get( 1 ).getP1().getW()[ 1 ];
		double max = min;

		for ( final PointFunctionMatch p : points )
		{
			final double value = p.getP1().getW()[ 1 ];
			min = Math.min( min, value );
			max = Math.max( max, value );
		}

		return new ValuePair< Double, Double >( min, max );
	}
	
}
