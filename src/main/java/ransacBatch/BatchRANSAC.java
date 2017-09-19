package ransacBatch;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Scrollbar;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

import fit.AbstractFunction2D;
import fit.PointFunctionMatch;
import fit.polynomial.HigherOrderPolynomialFunction;
import fit.polynomial.InterpolatedPolynomial;
import fit.polynomial.LinearFunction;
import fit.polynomial.Polynomial;
import fit.polynomial.QuadraticFunction;
import ij.ImageJ;
import ij.Prefs;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import interactiveMT.Ransac_MT;
import mpicbg.models.Point;
import mt.Averagerate;
import mt.FLSobject;
import mt.Rateobject;
import mt.Tracking;
import mt.Util;
import mt.listeners.WriteRatesListener;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;

public class BatchRANSAC implements PlugIn {

	public static int MIN_SLIDER = 0;
	public static int MAX_SLIDER = 500;

	public static double MIN_ERROR = 0.0;
	public static double MAX_ERROR = 30.0;

	public static double MIN_RES = 1.0;
	public static double MAX_RES = 30.0;

	public static double MAX_ABS_SLOPE = 100.0;

	public static double MIN_CAT = 0.0;
	public static double MAX_CAT = 100.0;
	public HashMap<Integer, Pair<Double, Double>> indexedsegments;
	public HashMap<Integer, LinearFunction> linearsegments;
	public ArrayList<Rateobject> allrates;
	public ArrayList<Averagerate> averagerates;
	public NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);

	public ArrayList<Pair<LinearFunction, ArrayList<PointFunctionMatch>>> linearlist;
	final Frame frame, jFreeChartFrame;
	public static int functionChoice = Prefs.getInt(".Functionchoice.int", 2);
	public File inputfile;
	public String inputdirectory;
	AbstractFunction2D function;
	public static double lambda = Prefs.get(".Linearity.double", 0.1);
	final ArrayList<Pair<Integer, Double>> mts;
	final ArrayList<Point> points;

	public static int numTimepoints = (int) Prefs.get(".numTp.int", 300);
	Scrollbar lambdaSB;
	Label lambdaLabel;

	final XYSeriesCollection dataset;
	final JFreeChart chart;
	int updateCount = 0;
	public ArrayList<Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>>> segments;
	public
	// for scrollbars
	int maxErrorInt, lambdaInt, minSlopeInt, maxSlopeInt, minDistCatInt, restoleranceInt;

	public double maxError = Prefs.getDouble(".MaxError.double", 2.0);
	public double minSlope = Prefs.getDouble(".Minslope.double", 0.1);
	public double maxSlope = Prefs.getDouble(".Maxslope.double", 100);
	public double restolerance = Prefs.getDouble(".Rescue.double", 5);
	public double tptolerance = Prefs.getDouble(".Timepoint.double", 5);
	public int maxDist = (int) Prefs.getDouble(".MaxGap.double", 300);
	public int minInliers = (int) Prefs.getDouble(".MinPoints.double", 10);
	public boolean detectCatastrophe = Prefs.getBoolean(".DetectCat.boolean", false);
	public double minDistanceCatastrophe = Prefs.getDouble(".MinDist.double", 5);
	public final ResultsTable rtAll;
	protected boolean wasCanceled = false;

	public BatchRANSAC(final ArrayList<Pair<Integer, Double>> mts, final File file, ResultsTable rtAll) {
		nf.setMaximumFractionDigits(5);

		this.mts = mts;
		this.points = Tracking.toPoints(mts);
		this.inputfile = file;
		this.inputdirectory = file.getParent();
		this.rtAll = rtAll;
		this.dataset = new XYSeriesCollection();
		this.chart = Tracking.makeChart(dataset, "Microtubule Length Plot", "Timepoint", "MT Length");
		this.jFreeChartFrame = Tracking.display(chart, new Dimension(500, 400));
		this.frame = new Frame("Welcome to Ransac Rate Analyzer ");

	};

	public ArrayList<Pair<Integer, Double>> lifetime;
	ArrayList<Pair<Integer, Double>> lifecount  ;
	@Override
	public void run(String arg) {
		/* JFreeChart */
		allrates = new ArrayList<Rateobject>();
		averagerates = new ArrayList<Averagerate>();
	 lifecount = new ArrayList<Pair<Integer, Double>>();
		linearlist = new ArrayList<Pair<LinearFunction, ArrayList<PointFunctionMatch>>>();
		this.dataset.addSeries(Tracking.drawPoints(mts, new double[]{1,1,1}));
		Tracking.setColor(chart, 0, new Color(64, 64, 64));
		Tracking.setStroke(chart, 0, 0.75f);
		setFunction();
		updateRANSAC();
		lifetime = writeratestofile();

	}

	public ArrayList<Pair<Integer, Double>> writeratestofile() {

		double lifetime = 0;
		String file = inputfile.getName().replaceFirst("[.][^.]+$", "");
		ArrayList<Pair<Integer, Double>> lifecount = new ArrayList<Pair<Integer, Double>>();

		try {
			File ratesfile = new File(inputdirectory + "//" + file + "Rates" + ".txt");
			File frequfile = new File(inputdirectory + "//" + file + "Averages" + ".txt");

			FileWriter fw = new FileWriter(ratesfile);

			BufferedWriter bw = new BufferedWriter(fw);

			FileWriter fwfrequ = new FileWriter(frequfile);

			BufferedWriter bwfrequ = new BufferedWriter(fwfrequ);

			bw.write("\tStartTime (px)\tEndTime(px)\tLinearRateSlope(px)\n");
			bwfrequ.write(
					"\tAverageGrowthrate(px)\tAverageShrinkrate(px)\tCatastropheFrequency(px)\tRescueFrequency(px)\n");
			ResultsTable rt = new ResultsTable();
			int count = 0;
			int negcount = 0;
			int rescount = 0;
			double timediff = 0;
			double restimediff = 0;
			double negtimediff = 0;
			double averagegrowth = 0;
			double averageshrink = 0;

			double minstartY = leastStart();

			double minstartX = Double.MAX_VALUE;
			double minendX = Double.MAX_VALUE;
			double catfrequ = 0;
			double resfrequ = 0;
			ArrayList<Double> previousendX = new ArrayList<Double>();
			for (final Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>> result : segments) {

				final Pair<Double, Double> minMax = Tracking.fromTo(result.getB());

				double startX = minMax.getA();
				double endX = minMax.getB();

				if (startX < minstartX) {

					minstartX = startX;
					minendX = endX;
				}

				Polynomial<?, Point> polynomial = (Polynomial) result.getA();

				sortPoints(points);
				if (points.get(points.size() - 1).getW()[0] - endX >= tptolerance) {

					double startY = polynomial.predict(startX);
					LinearFunction linear = new LinearFunction();
					boolean lineartrue = LinearFunction.slopeFits(result.getB(), linear, minSlope, maxSlope);

					double linearrate = linear.getCoefficient(1);

					if (linearrate > 0 && startY - minstartY > restolerance && startX > tptolerance
							&& previousendX.size() > 0 && lineartrue) {
						System.out.println(startY + " " + previousendX.size());
						rescount++;
						restimediff += -previousendX.get(previousendX.size() - 1) + startX;

					}

					if (linearrate > 0 && startX > tptolerance && lineartrue) {

						count++;
						timediff += endX - startX;
						lifetime = endX - startX;
						averagegrowth += linearrate;
						
						rt.incrementCounter();
						rt.addValue("Start time", startX);
						rt.addValue("End time", endX);
						rt.addValue("Growth Rate", linearrate);

						lifecount.add(new ValuePair<Integer, Double>(count, lifetime));
					}

					if (linearrate < 0) {

						negcount++;
						negtimediff += endX - startX;

						averageshrink += linearrate;

						rt.incrementCounter();
						rt.addValue("Start time", startX);
						rt.addValue("End time", endX);
						rt.addValue("Growth Rate", linearrate);
					}

				
					bw.write("\t" + nf.format(startX) + "\t" + "\t" + nf.format(endX) + "\t" + "\t"
							+ nf.format(linearrate) + "\t" + "\t" + "\t" + "\t" + "\n");
					if (linearrate > 0 && lineartrue)
						previousendX.add(endX);

				}

			}

			if (count > 0)
				averagegrowth /= count;
			if (negcount > 0)
				averageshrink /= negcount;

			if (count > 0) {

				catfrequ = count / timediff;

			}

			if (rescount > 0) {

				resfrequ = rescount / restimediff;
			}
			
			System.out.println(count + " " + rescount);
			rt.show("Rates(pixel units)");

			rtAll.incrementCounter();
			rtAll.addValue("Average Growth", averagegrowth);
			rtAll.addValue("Average Shrink", averageshrink);
			rtAll.addValue("Catastrophe Frequency", catfrequ);
			rtAll.addValue("Rescue Frequency", resfrequ);

			rtAll.show("Average Rates and Frequencies (pixel units)");

			bwfrequ.write("\t" + nf.format(averagegrowth) + "\t" + "\t" + "\t" + "\t" + nf.format(averageshrink) + "\t"
					+ "\t" + "\t" + nf.format(catfrequ) + "\t" + "\t" + "\t" + nf.format(resfrequ)

					+ "\n" + "\n");

			bw.close();
			fw.close();

			bwfrequ.close();
			fwfrequ.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lifecount;

	}

	public void updateRANSAC()
	{
		++updateCount;

		linearsegments.clear();
		indexedsegments.clear();
		allrates.clear();
		averagerates.clear();
		for ( int i = dataset.getSeriesCount() - 1; i > 0; --i )
			dataset.removeSeries( i );

		segments =
				Tracking.findAllFunctions( points, function, maxError, minInliers, maxDist );

		if ( segments == null || segments.size() == 0 )
		{
			--updateCount;
			return;
		}

		// sort the segments according to time relative to each other and the PointFunctionMatches internally
		sort( segments );

		final LinearFunction linear = new LinearFunction();
		int i = 1, segment = 1, linearcount = 1;
		int count = 0;
		int negcount = 0;
		int rescount = 0;
		double timediff = 0;
		double restimediff = 0;
		double negtimediff = 0;
		double averagegrowth = 0;
		double averageshrink = 0;
		double growthrate = 0;
		double shrinkrate = 0;

		double minstartY = leastStart();

		double minstartX = Double.MAX_VALUE;
		double minendX = Double.MAX_VALUE;
		double catfrequ = 0;
		double resfrequ = 0;
		double lifetime = 0;
	
		ArrayList<Double> previousendX = new ArrayList<Double>();
		ResultsTable rt = new ResultsTable();
		ResultsTable rtAll = new ResultsTable();
		for ( final Pair< AbstractFunction2D, ArrayList< PointFunctionMatch > > result : segments )
		{
			if ( LinearFunction.slopeFits( result.getB(), linear, minSlope, maxSlope ) || i > 0 )
			{
				
				
				
				final Pair< Double, Double > minMax = Tracking.fromTo( result.getB() );
		
				double startX = minMax.getA();
				double endX = minMax.getB();
				

				if (startX < minstartX) {

					minstartX = startX;
					minendX = endX;
				}
				Polynomial<?, Point> polynomial = (Polynomial) result.getA();
				
				sortPoints(points);
				
				dataset.addSeries( Tracking.drawFunction( polynomial, minMax.getA(), minMax.getB(), 0.5, "Segment " + segment ) );

				
				
				
				
				if ( functionChoice > 0 )
				{
					Tracking.setColor( chart, i, new Color( 255, 0, 0 ) );
					Tracking.setDisplayType( chart, i, true, false );
					Tracking.setStroke( chart, i, 0.5f );
				}
				else
				{
					Tracking.setColor( chart, i, new Color( 0, 128, 0 ) );
					Tracking.setDisplayType( chart, i, true, false );
					Tracking.setStroke( chart, i, 2f );
				}

				++i;

				
				if (points.get(points.size() - 1).getW()[0] - endX >= tptolerance) {
					double startY = polynomial.predict(startX);
					double linearrate = linear.getCoefficient(1);
				if ( functionChoice > 0 )
				{
					
					dataset.addSeries( Tracking.drawFunction( linear, minMax.getA(), minMax.getB(), 0.5, "Linear Segment " + segment ) );
					
					
					Tracking.setColor( chart, i, new Color( 0, 128, 0 ) );
					Tracking.setDisplayType( chart, i, true, false );
					Tracking.setStroke( chart, i, 2f );
	
					++i;
					
					
				
				}
				
				if (linearrate > 0 && startY - minstartY > restolerance 
						&& previousendX.size() > 0 ) {
					rescount++;
					restimediff += -previousendX.get(previousendX.size() - 1) + startX;

				}

				
				
				if (linearrate > 0  ) {

					count++;
					growthrate = linearrate;
					timediff += endX - startX;
					lifetime = endX - startX;
					averagegrowth += linearrate;
					lifecount.add(new ValuePair<Integer, Double>(count, lifetime));
					
					
					Rateobject rate = new Rateobject(linearrate, (int)startX, (int)endX);
					allrates.add(rate);
					rt.incrementCounter();
					rt.addValue("Start time", startX);
					rt.addValue("End time", endX);
					rt.addValue("Growth Rate", linearrate);

				}

				if(linearrate > 0){
				previousendX.add(endX);
				
				
				}
				
				
				
				dataset.addSeries( Tracking.drawPoints( Tracking.toPairList( result.getB() ), new double[]{1,1,1}, "Inliers " + segment ) );

				Tracking.setColor( chart, i, new Color( 255, 0, 0 ) );
				Tracking.setDisplayType( chart, i, false, true );
				Tracking.setSmallUpTriangleShape( chart, i );

				++i;
				++segment;
				}
			}
			else
			{
				System.out.println( "Removed segment because slope is wrong." );
			}
		}

		if ( this.detectCatastrophe )
		{
			if ( segments.size() < 2 )
			{
				System.out.println( "We have only " + segments.size() + " segments, need at least two to detect catastrophies." );
			}
			else
			{
				for ( int catastrophy = 0; catastrophy < segments.size() - 1; ++catastrophy )
				{
					final Pair< AbstractFunction2D, ArrayList< PointFunctionMatch > > start = segments.get( catastrophy );
					final Pair< AbstractFunction2D, ArrayList< PointFunctionMatch > > end = segments.get( catastrophy + 1 );

					final double tStart = start.getB().get( start.getB().size() -1 ).getP1().getL()[ 0 ];
					final double tEnd = end.getB().get( 0 ).getP1().getL()[ 0 ];

					final double lStart = start.getB().get( start.getB().size() -1 ).getP1().getL()[ 1 ];
					final double lEnd = end.getB().get( 0 ).getP1().getL()[ 1 ];

					final ArrayList< Point > catastropyPoints = new ArrayList< Point >();

					for ( final Point p : points )
						if ( p.getL()[ 0 ] >= tStart && p.getL()[ 0 ] <= tEnd )
							catastropyPoints.add( p );

					/*
					System.out.println( "\ncatastropy" );
					for ( final Point p : catastropyPoints)
						System.out.println( p.getL()[ 0 ] + ", " + p.getL()[ 1 ] );
					*/

					if ( catastropyPoints.size() > 2 )
					{
						if ( Math.abs( lStart - lEnd ) >= this.minDistanceCatastrophe )
						{
							// maximally 1.1 timepoints between points on a line
							final Pair< LinearFunction, ArrayList< PointFunctionMatch > > fit = Tracking.findFunction( catastropyPoints, new LinearFunction(), 0.75, 3, 1.1 );
	
							if ( fit != null )
							{
								if ( fit.getA().getM() < 0 )
								{
									sort( fit );


									double minY = Math.min( fit.getB().get( 0 ).getP1().getL()[ 1 ], fit.getB().get( fit.getB().size() -1 ).getP1().getL()[ 1 ] );
									double maxY = Math.max( fit.getB().get( 0 ).getP1().getL()[ 1 ], fit.getB().get( fit.getB().size() -1 ).getP1().getL()[ 1 ] );

									final Pair< Double, Double > minMax = Tracking.fromTo( fit.getB() );

									dataset.addSeries( Tracking.drawFunction( (Polynomial)fit.getA(), minMax.getA()-1, minMax.getB()+1, 0.1, minY - 2.5, maxY + 2.5, "C " + catastrophy ) );
									double startX = minMax.getA();
									double endX = minMax.getB();
								
							double	linearrate = fit.getA().getCoefficient(1);
							if (linearrate < 0) {

								negcount++;
								negtimediff += endX - startX;

								shrinkrate = linearrate;
								averageshrink += linearrate;

								rt.incrementCounter();
								rt.addValue("Start time", startX);
								rt.addValue("End time", endX);
								rt.addValue("Growth Rate", linearrate);
							}
							
							Rateobject rate = new Rateobject(linearrate, (int)startX, (int)endX);
							allrates.add(rate);
									Tracking.setColor( chart, i, new Color( 0, 0, 255 ) );
									Tracking.setDisplayType( chart, i, true, false );
									Tracking.setStroke( chart, i, 2f );

									++i;

									dataset.addSeries( Tracking.drawPoints( Tracking.toPairList( fit.getB() ), new double[]{1,1,1}, "C(inl) " + catastrophy ) );

									Tracking.setColor( chart, i, new Color( 0, 0, 255 ) );
									Tracking.setDisplayType( chart, i, false, true );
									Tracking.setShape( chart, i, ShapeUtilities.createDownTriangle( 4f ) );

									++i;
									++segment;
								}
								else
								{
									System.out.println( "Slope not negative: " + fit.getA() );
								}
							}
							else
							{
								System.out.println( "No function found." );
							}
						}
						else
						{
							System.out.println( "Catastrophy height not sufficient " + Math.abs( lStart - lEnd ) + " < " + this.minDistanceCatastrophe );
						}
					}
					else
					{
						System.out.println( "We have only " + catastropyPoints.size() + " points, need at least three to detect this catastrophy." );
					}
				}
			}
		}
		if (count > 0)
			averagegrowth /= count;

		if (count > 0) 

			catfrequ = count / timediff;
		
		if (rescount > 0)

			resfrequ = rescount / restimediff;
		
		if (negcount > 0)
			averageshrink /= negcount;
		
		rt.show("Rates(pixel units)");

		rtAll.incrementCounter();
		rtAll.addValue("Average Growth", averagegrowth);
		rtAll.addValue("Growth events", count);
		rtAll.addValue("Average Shrink", averageshrink);
		rtAll.addValue("Shrink events", negcount);
		rtAll.addValue("Catastrophe Frequency", catfrequ);
		rtAll.addValue("Catastrophe events", count - 1);
		rtAll.addValue("Rescue Frequency", resfrequ);
		rtAll.addValue("Rescue events", rescount);
		rtAll.show("Average Rates and Frequencies (real units)");

		Averagerate avrate = new Averagerate(averagegrowth, averageshrink, catfrequ, resfrequ, count, negcount, count - 1, rescount);
		averagerates.add(avrate);

		--updateCount;
	}

	protected void sortPoints(final ArrayList<Point> points) {
		Collections.sort(points, new Comparator<Point>() {

			@Override
			public int compare(final Point o1, final Point o2) {
				final double t1 = o1.getL()[0];
				final double t2 = o2.getL()[0];

				if (t1 < t2)
					return -1;
				else if (t1 == t2)
					return 0;
				else
					return 1;
			}
		});
	}

	public double leastStart() {

		double minstartX = Double.MAX_VALUE;

		for (final Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>> result : segments) {

			final Pair<Double, Double> minMax = Tracking.fromTo(result.getB());

			double startX = minMax.getA();

			if (minstartX <= startX) {

				minstartX = startX;

			}

		}

		return minstartX;

	}

	protected void sort(final Pair<? extends AbstractFunction2D, ArrayList<PointFunctionMatch>> segment) {
		Collections.sort(segment.getB(), new Comparator<PointFunctionMatch>() {

			@Override
			public int compare(final PointFunctionMatch o1, final PointFunctionMatch o2) {
				final double t1 = o1.getP1().getL()[0];
				final double t2 = o2.getP1().getL()[0];

				if (t1 < t2)
					return -1;
				else if (t1 == t2)
					return 0;
				else
					return 1;
			}
		});
	}

	protected void sort(final ArrayList<Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>>> segments) {
		for (final Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>> segment : segments)
			sort(segment);

		Collections.sort(segments, new Comparator<Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>>>() {
			@Override
			public int compare(Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>> o1,
					Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>> o2) {
				final double t1 = o1.getB().get(0).getP1().getL()[0];
				final double t2 = o2.getB().get(0).getP1().getL()[0];

				if (t1 < t2)
					return -1;
				else if (t1 == t2)
					return 0;
				else
					return 1;
			}
		});

	}

	public void setFunction() {
		if (functionChoice == 0) {
			this.function = new LinearFunction();
		} else if (functionChoice == 1) {
			// this.function = new QuadraticFunction();
			this.function = new InterpolatedPolynomial<LinearFunction, QuadraticFunction>(new LinearFunction(),
					new QuadraticFunction(), 1 - lambda);
		} else {
			this.function = new InterpolatedPolynomial<LinearFunction, HigherOrderPolynomialFunction>(
					new LinearFunction(), new HigherOrderPolynomialFunction(3), 1 - lambda);
		}

	}

	protected static double computeValueFromDoubleExpScrollbarPosition(final int scrollbarPosition,
			final int scrollbarMax, final double maxValue) {
		final int maxScrollHalf = scrollbarMax / 2;
		final int scrollPos = scrollbarPosition - maxScrollHalf;

		final double logMax = Math.log10(maxScrollHalf + 1);

		final double value = Math.min(maxValue,
				((logMax - Math.log10(maxScrollHalf + 1 - Math.abs(scrollPos))) / logMax) * maxValue);

		if (scrollPos < 0)
			return -value;
		else
			return value;
	}

	protected static int computeScrollbarPositionValueFromDoubleExp(final int scrollbarMax, final double value,
			final double maxValue) {
		final int maxScrollHalf = scrollbarMax / 2;
		final double logMax = Math.log10(maxScrollHalf + 1);

		int scrollPos = (int) Math
				.round(maxScrollHalf + 1 - Math.pow(10, logMax - (Math.abs(value) / maxValue) * logMax));

		if (value < 0)
			scrollPos *= -1;

		return scrollPos + maxScrollHalf;
	}

	protected static double computeValueFromScrollbarPosition(final int scrollbarPosition, final int scrollbarMax,
			final double minValue, final double maxValue) {
		return minValue + (scrollbarPosition / (double) scrollbarMax) * (maxValue - minValue);
	}

	protected static int computeScrollbarPositionFromValue(final int scrollbarMax, final double value,
			final double minValue, final double maxValue) {
		return (int) Math.round(((value - minValue) / (maxValue - minValue)) * scrollbarMax);
	}

	public static void main(String[] args) {

		Ransac_MT newran = new Ransac_MT();
		newran.run(null);

	}

}
