package peakFitter;

import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JProgressBar;

import LineModels.UseLineModel.UserChoiceModel;
import fiji.util.DistanceComparator;
import ij.gui.EllipseRoi;
import labeledObjects.CommonOutput;
import labeledObjects.CommonOutputHF;
import labeledObjects.Indexedlength;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Point;
import net.imglib2.PointSampleList;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPoint;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;
import preProcessing.GetLocalmaxmin;
import util.Boundingboxes;

public class FitterUtils {

	public static double Distance(final double[] minCorner, final double[] maxCorner) {

		double distance = 0;

		for (int d = 0; d < minCorner.length; ++d) {

			distance += Math.pow((minCorner[d] - maxCorner[d]), 2);

		}
		return Math.sqrt(distance);
	}

	public static ArrayList<Integer> Getlabel(final ArrayList<CommonOutputHF> imgs, final Point fixedpoint,
			final double originalslope, final double originalintercept) {

		int finallabel = Integer.MIN_VALUE;
		ArrayList<Integer> alllabels = new ArrayList<Integer>();


		int index = 0;
			if (imgs.get(index).intimg!= null){
				
				RandomAccess<IntType> intranac = imgs.get(index).intimg.randomAccess();

				intranac.setPosition(fixedpoint);
				finallabel = intranac.get().get();
				alllabels.add(finallabel);
			
			}
			
			
			else{
				
			for (int indexx = 0; indexx < imgs.size(); ++indexx){	
			RandomAccessibleInterval<FloatType> currentimg = imgs.get(indexx).Actualroi;
			FinalInterval interval = imgs.get(indexx).interval;
			currentimg = Views.interval(currentimg, interval);

			if (fixedpoint.getIntPosition(0) >= interval.min(0) && fixedpoint.getIntPosition(0) <= interval.max(0)
					&& fixedpoint.getIntPosition(1) >= interval.min(1)
					&& fixedpoint.getIntPosition(1) <= interval.max(1)) {

				for (int i = 0; i < imgs.get(indexx).Allrois.size(); ++i) {

					EllipseRoi roi = imgs.get(indexx).Allrois.get(i);

					if (roi.contains(fixedpoint.getIntPosition(0), fixedpoint.getIntPosition(1))) {

						finallabel = imgs.get(indexx).roilabel;

						alllabels.add(finallabel);

					}

				}
			}
			}

		}
		
		

		return alllabels;
		
	}

	public static int getlabelindex(final ArrayList<CommonOutputHF> imgs, int label) {

		int labelindex = -1;
		for (int index = 0; index < imgs.size(); ++index) {

			if (imgs.get(index).roilabel == label) {

				labelindex = index;

			}
		}

		return labelindex;

	}

	public static int getlabelindexSeed(final ArrayList<CommonOutput> imgs, int label) {

		int labelindex = -1;
		for (int index = 0; index < imgs.size(); ++index) {

			if (imgs.get(index).roilabel == label) {

				labelindex = index;

			}
		}

		return labelindex;

	}

	public static PointSampleList<FloatType> gatherfullDataSeed(final ArrayList<CommonOutput> imgs, final int label,
			final int ndims) {
		final PointSampleList<FloatType> datalist = new PointSampleList<FloatType>(ndims);

		int labelindex = getlabelindexSeed(imgs, label);

		RandomAccessibleInterval<FloatType> currentimg = imgs.get(labelindex).Actualroi;

		FinalInterval interval = imgs.get(labelindex).interval;

		currentimg = Views.interval(currentimg, interval);

		Cursor<FloatType> localcursor = Views.iterable(currentimg).localizingCursor();

		while (localcursor.hasNext()) {

			localcursor.fwd();

			if (localcursor.get().get() > 0) {
				Point newpoint = new Point(localcursor);
				datalist.add(newpoint, localcursor.get().copy());
			}
		}

		return datalist;
	}

	public static PointSampleList<FloatType> gatherfullData(final ArrayList<CommonOutputHF> imgs, final int label,
			final int ndims) {
		final PointSampleList<FloatType> datalist = new PointSampleList<FloatType>(ndims);

		int labelindex = getlabelindex(imgs, label);

		RandomAccessibleInterval<FloatType> currentimg = imgs.get(labelindex).Actualroi;

		FinalInterval interval = imgs.get(labelindex).interval;

		currentimg = Views.interval(currentimg, interval);

		Cursor<FloatType> localcursor = Views.iterable(currentimg).localizingCursor();

		while (localcursor.hasNext()) {

			localcursor.fwd();

			if (localcursor.get().get() > 0) {
				Point newpoint = new Point(localcursor);
				datalist.add(newpoint, localcursor.get().copy());
			}
		}

		return datalist;
	}

	public static final Pair<double[], double[]> MakeinitialEndpointguess(ArrayList<CommonOutputHF> imgs,
			double maxintensityline, double Intensityratio, int ndims, int label, double slope, double intercept,
			double Curvature, double Inflection) {
		long[] newposition = new long[ndims];
		double[] minVal = { Double.MAX_VALUE, Double.MAX_VALUE };
		double[] maxVal = { -Double.MIN_VALUE, -Double.MIN_VALUE };

		RandomAccessibleInterval<FloatType> currentimg = imgs.get(label).Roi;

		FinalInterval interval = imgs.get(label).interval;

		currentimg = Views.interval(currentimg, interval);

		final Cursor<FloatType> outcursor = Views.iterable(currentimg).localizingCursor();

		while (outcursor.hasNext()) {

			outcursor.fwd();

			outcursor.localize(newposition);

			long pointonline = (int) Math.round(newposition[1] - slope * newposition[0] - intercept);
			if (Math.abs(pointonline) <= 50) {
				if (outcursor.getDoublePosition(0) <= minVal[0]
						&& outcursor.get().get() / maxintensityline > Intensityratio) {
					minVal[0] = outcursor.getDoublePosition(0);
					minVal[1] = outcursor.getDoublePosition(1);
				}

				if (outcursor.getDoublePosition(0) >= maxVal[0]
						&& outcursor.get().get() / maxintensityline > Intensityratio) {
					maxVal[0] = outcursor.getDoublePosition(0);
					maxVal[1] = outcursor.getDoublePosition(1);

				}

			}
		}
		Pair<double[], double[]> minmaxpair = new ValuePair<double[], double[]>(minVal, maxVal);

		return minmaxpair;

	}

	public static final Pair<double[], double[]> MakeinitialEndpointguessUser(ArrayList<CommonOutputHF> imgs,
			double maxintensityline, double Intensityratio, int ndims, int label, double slope, double intercept,
			double Curvature, double Inflection, int rate) {
		long[] newposition = new long[ndims];
		double[] minVal = { Double.MAX_VALUE, Double.MAX_VALUE };
		double[] maxVal = { -Double.MIN_VALUE, -Double.MIN_VALUE };

		RandomAccessibleInterval<FloatType> currentimg = imgs.get(label).Roi;

		FinalInterval interval = imgs.get(label).interval;

		currentimg = Views.interval(currentimg, interval);

		final Cursor<FloatType> outcursor = Views.iterable(currentimg).localizingCursor();

		while (outcursor.hasNext()) {

			outcursor.fwd();

			outcursor.localize(newposition);

			long pointonline = (int) Math.round(newposition[1] - slope * newposition[0] - intercept);

			

			if (rate > imgs.get(0).framenumber){
				if (Math.abs(pointonline) <= 50) {
					if (outcursor.getDoublePosition(0) <= minVal[0]
							&& outcursor.get().get() / maxintensityline > Intensityratio) {
						minVal[0] = outcursor.getDoublePosition(0);
						minVal[1] = outcursor.getDoublePosition(1);
					}

					if (outcursor.getDoublePosition(0) >= maxVal[0]
							&& outcursor.get().get() / maxintensityline > Intensityratio) {
						maxVal[0] = outcursor.getDoublePosition(0);
						maxVal[1] = outcursor.getDoublePosition(1);

					}

				}
			}
			
			else{
					if (outcursor.getDoublePosition(0) <= minVal[0]
							&& outcursor.get().get() / maxintensityline > Intensityratio) {
						minVal[0] = outcursor.getDoublePosition(0);
						minVal[1] = outcursor.getDoublePosition(1);
					}

					if (outcursor.getDoublePosition(0) >= maxVal[0]
							&& outcursor.get().get() / maxintensityline > Intensityratio) {
						maxVal[0] = outcursor.getDoublePosition(0);
						maxVal[1] = outcursor.getDoublePosition(1);

					}
					
			}
					

				
		}
		
		
		
		System.out.println(maxVal[0]);
		
		
		Pair<double[], double[]> minmaxpair = new ValuePair<double[], double[]>(minVal, maxVal);

		return minmaxpair;

	}

	public static void SetProgressBar(JProgressBar jpb, double percent) {

		jpb.setValue((int) Math.round(percent));
		jpb.setOpaque(true);
		jpb.setStringPainted(true);
		jpb.setString("Finding MT ends");

	}
	
	
	public static void SetProgressBar(JProgressBar jpb, double percent, String message) {

		jpb.setValue((int) Math.round(percent));
		jpb.setOpaque(true);
		jpb.setStringPainted(true);
		jpb.setString(message);

	}

	public static void SetProgressBar(JProgressBar jpb) {
		jpb.setValue(0);
		jpb.setIndeterminate(true);
		jpb.setOpaque(true);
		jpb.setStringPainted(true);
		jpb.setString("Pre-processing Image");

	}

	public static void SetProgressBarTime(JProgressBar jpb, double percent, int framenumber, int thirdDimsize) {

		jpb.setValue((int) percent);
		jpb.setOpaque(true);
		jpb.setStringPainted(true);
		jpb.setString("Time point = " + framenumber + "/" + thirdDimsize);

	}

	public static void SetProgressBarTime(JProgressBar jpb, double percent, int framenumber, int thirdDimsize,
			String message) {

		jpb.setValue((int) percent);
		jpb.setOpaque(true);
		jpb.setStringPainted(true);
		jpb.setString(message + "= " + framenumber + "/" + thirdDimsize);

	}

}
