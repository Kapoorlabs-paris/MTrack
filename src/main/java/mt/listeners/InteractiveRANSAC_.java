package mt.listeners;

import java.awt.Button;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Scrollbar;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeriesCollection;

import fit.AbstractFunction2D;
import fit.PointFunctionMatch;
import fit.polynomial.HigherOrderPolynomialFunction;
import fit.polynomial.InterpolatedPolynomial;
import fit.polynomial.LinearFunction;
import fit.polynomial.Polynomial;
import fit.polynomial.QuadraticFunction;
import ij.ImageJ;
import ij.plugin.PlugIn;
import mpicbg.models.Point;
import mt.RansacFileChooser_;
import mt.Tracking;
import net.imglib2.util.Pair;

public class InteractiveRANSAC_ implements PlugIn
{
	public static int MIN_SLIDER = 0;
	public static int MAX_SLIDER = 500;

	public static double MIN_ERROR = 0.0;
	public static double MAX_ERROR = 30.0;

	public static double MAX_ABS_SLOPE = 100.0;

	final Frame frame, jFreeChartFrame;
	int functionChoice; // 0 == Linear, 1 == Quadratic interpolated, 2 == cubic interpolated
	AbstractFunction2D function;
	double lambda;
	final ArrayList< Pair< Integer, Double > > mts;
	final ArrayList< Point > points;
	final int numTimepoints, minTP, maxTP;

	Scrollbar lambdaSB;
	Label lambdaLabel;

	final XYSeriesCollection dataset;
	final JFreeChart chart;
	int updateCount = 0;

	// for scrollbars
	int maxErrorInt, lambdaInt, minSlopeInt, maxSlopeInt;

	double maxError = 3.0;
	double minSlope = 0.1;
	double maxSlope = 100;
	int maxDist = 300;
	int minInliers = 50;

	protected boolean wasCanceled = false;
	
	public InteractiveRANSAC_( final ArrayList< Pair< Integer, Double > > mts )
	{
		this( mts, 0, 300, 3.0, 0.1, 10.0, 10, 50, 1, 0.1 );
	}

	public InteractiveRANSAC_(
			final ArrayList< Pair< Integer, Double > > mts,
			final int minTP,
			final int maxTP,
			final double maxError,
			final double minSlope,
			final double maxSlope,
			final int maxDist,
			final int minInliers,
			final int functionChoice,
			final double lambda )
	{
		this.minTP = minTP;
		this.maxTP = maxTP;
		this.numTimepoints = maxTP - minTP + 1;
		this.functionChoice = functionChoice;
		this.lambda = lambda;
		this.mts = mts;
		this.points = Tracking.toPoints( mts );

		this.maxError = maxError;
		this.minSlope = minSlope;
		this.maxSlope = maxSlope;
		this.maxDist = Math.min( maxDist, numTimepoints );
		this.minInliers = Math.min( minInliers, numTimepoints );

		if ( this.minSlope >= this.maxSlope )
			this.minSlope = this.maxSlope - 0.1;

		this.maxErrorInt = computeScrollbarPositionFromValue( MAX_SLIDER, this.maxError, MIN_ERROR, MAX_ERROR );
		this.lambdaInt = computeScrollbarPositionFromValue( MAX_SLIDER, this.lambda, 0.0, 1.0 );
		this.minSlopeInt = computeScrollbarPositionValueFromDoubleExp( MAX_SLIDER, this.minSlope, MAX_ABS_SLOPE );
		this.maxSlopeInt = computeScrollbarPositionValueFromDoubleExp( MAX_SLIDER, this.maxSlope, MAX_ABS_SLOPE );

		this.maxError = computeValueFromScrollbarPosition( this.maxErrorInt, MAX_SLIDER, MIN_ERROR, MAX_ERROR );
		this.minSlope = computeValueFromDoubleExpScrollbarPosition( this.minSlopeInt, MAX_SLIDER, MAX_ABS_SLOPE );
		this.maxSlope = computeValueFromDoubleExpScrollbarPosition( this.maxSlopeInt, MAX_SLIDER, MAX_ABS_SLOPE );
		this.dataset = new XYSeriesCollection();
		this.chart = Tracking.makeChart( dataset, "Microtubule Length Plot", "Timepoint", "MT Length" );
		this.jFreeChartFrame = Tracking.display( chart, new Dimension( 1000, 800 ) );
		this.frame = new Frame( "Interactive MicroTubule Finder" );


	};
	@Override
	public void run(String arg){
		/* JFreeChart */
		
		this.dataset.addSeries( Tracking.drawPoints( mts ) );
		Tracking.setColor( chart, 0, new Color( 64, 64, 64 ) );
		Tracking.setStroke( chart, 0, 0.75f );
		Card();
		updateRANSAC();
		
	}
	
	public void Card(){
	
		

		/* GUI */
		this.frame.setSize( 400, 450 );

		/* Instantiation */
		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints c = new GridBagConstraints();

		final Scrollbar maxErrorSB = new Scrollbar( Scrollbar.HORIZONTAL, this.maxErrorInt, 1, MIN_SLIDER, MAX_SLIDER + 1 );
		final Scrollbar minInliersSB = new Scrollbar( Scrollbar.HORIZONTAL, this.minInliers, 1, 2, numTimepoints + 1 );
		final Scrollbar maxDistSB = new Scrollbar( Scrollbar.HORIZONTAL, this.maxDist, 1, 0, numTimepoints + 1 );
		final Scrollbar minSlopeSB = new Scrollbar( Scrollbar.HORIZONTAL, this.minSlopeInt, 1, MIN_SLIDER, MAX_SLIDER + 1 );
		final Scrollbar maxSlopeSB = new Scrollbar( Scrollbar.HORIZONTAL, this.maxSlopeInt, 1, MIN_SLIDER, MAX_SLIDER + 1 );

		final Choice choice = new Choice();
		choice.add( "Linear Function only" );
		choice.add( "Quadratic function regularized with Linear Function" );
		choice.add( "Cubic Function regularized with Linear Function" );

		this.lambdaSB = new Scrollbar( Scrollbar.HORIZONTAL, this.lambdaInt, 1, MIN_SLIDER, MAX_SLIDER + 1 );

		final Label maxErrorLabel = new Label( "Max. Error (px) = " + this.maxError, Label.CENTER );
		final Label minInliersLabel = new Label( "Min. #Points (tp) = " + this.minInliers, Label.CENTER );
		final Label maxDistLabel = new Label( "Max. Gap (tp) = " + this.maxDist, Label.CENTER );
		this.lambdaLabel = new Label( "Linearity (fraction) = " + this.lambda, Label.CENTER );
		final Label minSlopeLabel = new Label( "Min. Segment Slope (px/tp) = " + this.minSlope, Label.CENTER );
		final Label maxSlopeLabel = new Label( "Max. Segment Slope (px/tp) = " + this.maxSlope, Label.CENTER );

		final Button done = new Button( "Done" );
		final Button cancel = new Button( "Cancel" );

		choice.select( functionChoice );
		setFunction();

		// Location 
		frame.setLayout( layout );
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		frame.add ( maxErrorSB, c );

		++c.gridy;
		frame.add( maxErrorLabel, c );

		++c.gridy;
		c.insets = new Insets( 10, 0, 0, 0 );
		frame.add ( minInliersSB, c );
		c.insets = new Insets( 0, 0, 0, 0 );

		++c.gridy;
		frame.add( minInliersLabel, c );

		++c.gridy;
		c.insets = new Insets( 10, 0, 0, 0 );
		frame.add ( maxDistSB, c );
		c.insets = new Insets( 0, 0, 0, 0 );

		++c.gridy;
		frame.add( maxDistLabel, c );

		++c.gridy;
		c.insets = new Insets( 30, 0, 0, 0 );
		frame.add ( choice, c );
		c.insets = new Insets( 0, 0, 0, 0 );

		++c.gridy;
		c.insets = new Insets( 10, 0, 0, 0 );
		frame.add ( lambdaSB, c );
		c.insets = new Insets( 0, 0, 0, 0 );

		++c.gridy;
		frame.add( lambdaLabel, c );

		++c.gridy;
		c.insets = new Insets( 30, 0, 0, 0 );
		frame.add ( minSlopeSB, c );
		c.insets = new Insets( 0, 0, 0, 0 );

		++c.gridy;
		frame.add( minSlopeLabel, c );

		++c.gridy;
		c.insets = new Insets( 10, 0, 0, 0 );
		frame.add ( maxSlopeSB, c );
		c.insets = new Insets( 0, 0, 0, 0 );

		++c.gridy;
		frame.add( maxSlopeLabel, c );

		++c.gridy;
		c.insets = new Insets( 30, 150, 0, 150 );
		frame.add( done, c );
		
		++c.gridy;
		c.insets = new Insets( 10, 150, 0, 150 );
		frame.add( cancel, c );

		maxErrorSB.addAdjustmentListener( new MaxErrorListener( this, maxErrorLabel, maxErrorSB ) );
		minInliersSB.addAdjustmentListener( new MinInliersListener( this, minInliersLabel, minInliersSB ) );
		maxDistSB.addAdjustmentListener( new MaxDistListener( this, maxDistLabel, maxDistSB ) );
		choice.addItemListener( new FunctionItemListener( this ) );
		lambdaSB.addAdjustmentListener( new LambdaListener( this, lambdaLabel, lambdaSB ) );
		minSlopeSB.addAdjustmentListener( new MinSlopeListener( this, minSlopeSB, minSlopeLabel ) );
		maxSlopeSB.addAdjustmentListener( new MaxSlopeListener( this, maxSlopeSB, maxSlopeLabel ) );
		done.addActionListener( new FinishButtonListener( this, false ) );
		cancel.addActionListener( new FinishButtonListener( this, true ) );

		frame.addWindowListener( new FrameListener( this ) );
		frame.setVisible( true );

		updateRANSAC();
	}

	public void setFunction()
	{
		if ( functionChoice == 0 )
		{
			this.function = new LinearFunction();
			this.setLambdaEnabled( false );
		}
		else if ( functionChoice == 1 )
		{
			this.setLambdaEnabled( true );
			//this.function = new QuadraticFunction();
			this.function = new InterpolatedPolynomial< LinearFunction, QuadraticFunction >(
					new LinearFunction(), new QuadraticFunction(), 1 - this.lambda );
		}
		else
		{
			this.setLambdaEnabled( true );
			this.function = new InterpolatedPolynomial< LinearFunction, HigherOrderPolynomialFunction >(
					new LinearFunction(), new HigherOrderPolynomialFunction( 3 ), 1 - this.lambda );
		}

	}

	public void setLambdaEnabled( final boolean state )
	{
		if ( state )
		{
			if ( !lambdaSB.isEnabled() )
			{
				lambdaSB.setEnabled( true );
				lambdaLabel.setEnabled( true );
				lambdaLabel.setForeground( Color.BLACK );
			}
		}
		else
		{
			if ( lambdaSB.isEnabled() )
			{
				lambdaSB.setEnabled( false );
				lambdaLabel.setEnabled( false );
				lambdaLabel.setForeground( Color.GRAY );
			}
		}
	}

	public void updateRANSAC()
	{
		++updateCount;

		for ( int i = dataset.getSeriesCount() - 1; i > 0; --i )
			dataset.removeSeries( i );

		final ArrayList< Pair< AbstractFunction2D, ArrayList< PointFunctionMatch > > > segments =
				Tracking.findAllFunctions( points, function, maxError, minInliers, maxDist );

		if ( segments == null || segments.size() == 0 )
		{
			--updateCount;
			return;
		}

		final LinearFunction linear = new LinearFunction();
		int i = 1, segment = 1;

		for ( final Pair< AbstractFunction2D, ArrayList< PointFunctionMatch > > result : segments )
		{
			if ( LinearFunction.slopeFits( result.getB(), linear, minSlope, maxSlope ) )
			{
				final Pair< Double, Double > minMax = Tracking.fromTo( result.getB() );
		
				dataset.addSeries( Tracking.drawFunction( (Polynomial)result.getA(), minMax.getA(), minMax.getB(), 0.5, "Segment " + segment ) );

				if ( functionChoice > 0 )
				{
					Tracking.setColor( chart, i, new Color( 255, 0, 0 ) );
					Tracking.setStroke( chart, i, 0.5f );
				}
				else
				{
					Tracking.setColor( chart, i, new Color( 0, 128, 0 ) );
					Tracking.setStroke( chart, i, 2f );
				}

				++i;

				if ( functionChoice > 0 )
				{
					dataset.addSeries( Tracking.drawFunction( linear, minMax.getA(), minMax.getB(), 0.5, "Linear Segment " + segment ) );
	
					Tracking.setColor( chart, i, new Color( 0, 128, 0 ) );
					Tracking.setStroke( chart, i, 2f );
	
					++i;
				}

				dataset.addSeries( Tracking.drawPoints( Tracking.toPairList( result.getB() ), "Inliers " + segment ) );

				Tracking.setColor( chart, i, new Color( 255, 0, 0 ) );
				Tracking.setDisplayType( chart, i, false, true );
				Tracking.setSmallUpTriangleShape( chart, i );

				++i;
				++segment;
			}
			else
			{
				System.out.println( "Removed segment because slope is wrong." );
			}
		}

		--updateCount;
	}

	public void close()
	{
		frame.setVisible( false );
		frame.dispose();
		jFreeChartFrame.setVisible( false );
		jFreeChartFrame.dispose();
	}

	protected static double computeValueFromDoubleExpScrollbarPosition( final int scrollbarPosition, final int scrollbarMax, final double maxValue )
	{
		final int maxScrollHalf = scrollbarMax/2;
		final int scrollPos = scrollbarPosition - maxScrollHalf;

		final double logMax = Math.log10( maxScrollHalf + 1 );

		final double value = Math.min( maxValue, ( ( logMax - Math.log10( maxScrollHalf + 1 - Math.abs( scrollPos ) ) ) / logMax ) * maxValue );

		if ( scrollPos < 0 )
			return -value;
		else
			return value;
	}

	protected static int computeScrollbarPositionValueFromDoubleExp( final int scrollbarMax, final double value, final double maxValue )
	{
		final int maxScrollHalf = scrollbarMax/2;
		final double logMax = Math.log10( maxScrollHalf + 1 );

		int scrollPos = (int) Math.round( maxScrollHalf + 1 - Math.pow( 10, logMax - ( Math.abs( value ) / maxValue ) * logMax ) );

		if ( value < 0 )
			scrollPos *= -1;

		return scrollPos + maxScrollHalf;
	}

	protected static double computeValueFromScrollbarPosition( final int scrollbarPosition, final int scrollbarMax, final double minValue, final double maxValue )
	{
		return minValue + ( scrollbarPosition/(double)scrollbarMax ) * ( maxValue - minValue );
	}

	protected static int computeScrollbarPositionFromValue( final int scrollbarMax, final double value, final double minValue, final double maxValue )
	{
		return (int)Math.round( ( ( value - minValue ) / ( maxValue - minValue ) ) * scrollbarMax );
	}

	
	public static void main( String[] args )
	{

		JFrame frame = new JFrame("");
		RansacFileChooser_ panel = new RansacFileChooser_();

		frame.getContentPane().add(panel, "Center");
		frame.setSize(panel.getPreferredSize());

	//	new InteractiveRANSAC( Tracking.loadMT( new File( "/Users/varunkapoor/Documents/MTAnalysisRansac/TestRanSacSeedLabel3-endA.txt" ) ) );
	}
	
	
}
