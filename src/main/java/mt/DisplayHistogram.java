package mt;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import net.imglib2.util.ValuePair;

public class DisplayHistogram {

	protected static  double min;
	protected static double max;
	public static List<ValuePair<Double, Integer>> binData(final List<Double> data, final double min, final double max,
			final int numBins) {
		// avoid the one value that is exactly 100%
		final double size = max - min + 0.000001;

		// bin and count the entries
		final int[] bins = new int[numBins];

		for (final double v : data)
			++bins[(int) Math.floor(((v - min) / size) * numBins)];

		// make the list of bins
		final ArrayList<ValuePair<Double, Integer>> hist = new ArrayList<ValuePair<Double, Integer>>();

		final double binSize = size / numBins;
		for (int bin = 0; bin < numBins; ++bin)
			hist.add(new ValuePair<Double, Integer>(min + binSize / 2 + binSize * bin, bins[bin]));

		return hist;
	}

	public static double getMin() {
		return min;
	}

	public static  double getMax() {
		return max;
	}

	public static ValuePair<Double, Double> getMinMax(final List<Double> data) {
		// compute min/max/size
		double min = data.get(0);
		double max = data.get(0);

		for (final double v : data) {
			min = Math.min(min, v);
			max = Math.max(max, v);
		}

		return new ValuePair<Double, Double>(min, max);
	}

	public static IntervalXYDataset createDataset(final List<Double> values, final int numBins, final String title) {
		final XYSeries series = new XYSeries(title);

		final ValuePair<Double, Double> minmax = getMinMax(values);
		min = minmax.getA();
		max = minmax.getB();

		final List<ValuePair<Double, Integer>> hist = binData(values, min, max, numBins);

		for (final ValuePair<Double, Integer> pair : hist)
			series.add(pair.getA(), pair.getB());

		final XYSeriesCollection dataset = new XYSeriesCollection(series);
		dataset.setAutoWidth(true);

		return dataset;
	}

	public static JFreeChart makehistXChart(final List<Double> Xdataset, final int numBins) {
		return makehistXChart(Xdataset, "MT Lifetime Distribution", "px", "Count", numBins);
	}

	public static JFreeChart makehistXChart(final List<Double> Xdataset, final String title, final String x, final String y,
			final int numBins) {

		final IntervalXYDataset SigmaXdataset = createDataset(Xdataset, numBins, title);

		final JFreeChart sigmaXchart = createChart(SigmaXdataset, title, x);

		return sigmaXchart;
	}

	protected static JFreeChart createChart(final IntervalXYDataset dataset, final String title, final String units) {
		final JFreeChart chart = ChartFactory.createXYBarChart(title, "Lifetime [" + units + "]", false, "Count",
				dataset, PlotOrientation.VERTICAL, false, // legend
				false, false);

		NumberAxis range = (NumberAxis) chart.getXYPlot().getDomainAxis();
		range.setRange(getMin(), getMax());

		XYPlot plot = chart.getXYPlot();
		XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();

		renderer.setSeriesPaint(0, Color.red);
		renderer.setDrawBarOutline(true);
		renderer.setSeriesOutlinePaint(0, Color.black);
		renderer.setBarPainter(new StandardXYBarPainter());

		return chart;
	}

	
}
