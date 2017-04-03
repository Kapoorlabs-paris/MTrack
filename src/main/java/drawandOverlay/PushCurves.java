package drawandOverlay;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import houghandWatershed.Finalfunction;
import houghandWatershed.TransformCordinates;
import ij.gui.EllipseRoi;
import labeledObjects.Indexedlength;
import labeledObjects.LabelledImg;
import net.imglib2.Cursor;
import net.imglib2.Point;
import net.imglib2.PointSampleList;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;
import preProcessing.GetLocalmaxmin.IntensityType;

public class PushCurves {

	public static double slopethresh = 90;
	
	public static void drawCircle(Img<FloatType> imgout, double[] min, double[] max, double[] center, double radius) {
		int n = imgout.numDimensions();
		double[] realpos = new double[n];
		double[] size = new double[n];
		double[] location = new double[n];
		double[] position = new double[n];
		double[] iniposition = new double[n];
		double[] backini = new double[n];
		double[] newpos = new double[n];
		double[] backpos = new double[n];
		double[] sigma = new double[n];
		final RandomAccess<FloatType> outbound = imgout.randomAccess();
		double stepsize = 0.1;
		int[] setpos = new int[n];
		for (int d = 0; d < n; ++d)
			size[d] = imgout.dimension(d);

		Cursor<FloatType> cursor = Views.iterable(imgout).localizingCursor();
		while (cursor.hasNext()) {
			cursor.fwd();
			cursor.localize(location);
			realpos = TransformCordinates.transformfwd(location, size, min, max);

			// To get a starting point on the circle
			if (Math.pow(realpos[0] - center[0], 2) + Math.pow(realpos[1] - center[1], 2)
					- radius * radius <= 1.0E-50) {
				for (int d = 0; d < n; ++d)
					position[d] = realpos[d];
				break;

			}

		}

		for (int d = 0; d < n; ++d)
			iniposition[d] = position[d];

		double initheta = Math.atan2(iniposition[1] - center[1], iniposition[0] - center[0]);
		double increment = Math.acos((2 * radius * radius - stepsize * stepsize) / (2 * radius * radius));

		backini = TransformCordinates.transformback(iniposition, size, min, max);
		sigma[0] = 1;
		sigma[1] = 1;
		while (true) {

			// Move the current point along the curve

			newpos[0] = center[0] + radius * Math.cos((initheta - increment));
			newpos[1] = center[1] + radius * Math.sin((initheta - increment));
			initheta = Math.atan2(newpos[1] - center[1], newpos[0] - center[0]);

			// Transform the co-ordinates back as double[]
			backpos = TransformCordinates.transformback(newpos, size, min, max);

			setpos[0] = (int) Math.round(backpos[0]);
			setpos[1] = (int) Math.round(backpos[1]);

			// To set the pixel intensity
			AddGaussian.addGaussian(imgout, backpos, sigma);

			// To make sure that the values transformed back are not out of
			// bounds
			if (backpos[0] < imgout.realMax(0) - imgout.realMin(0) || backpos[0] > imgout.realMin(0)
					|| backpos[1] < imgout.realMax(1) - imgout.realMin(1) || backpos[1] > imgout.realMin(1))

				outbound.setPosition(setpos);

			// Stopping criteria of moving along the circular arc
			if (Math.abs(setpos[0] - (int) Math.round(backini[0])) == 0
					&& Math.abs(setpos[1] - (int) Math.round(backini[1])) == 0)
				break;

			// General Stopping criteria of moving along a curve, when we hit a
			// boundary
			if (newpos[0] >= max[0] || newpos[0] <= min[0] || newpos[1] >= max[1] || newpos[1] <= min[1])

				break;
		}
	}

	public static void DrawSine(RandomAccessibleInterval<FloatType> imgout, double[] min, double[] max,
			double amplitude, double phase) {

		int n = imgout.numDimensions();
		double[] size = new double[n];
		double[] position = new double[n];
		double[] newpos = new double[n];
		double[] backpos = new double[n];
		double[] sigma = new double[n];
		final RandomAccess<FloatType> outbound = imgout.randomAccess();
		// SinCosinelut.getTable();
		double stepsize = 0.1;
		int[] setpos = new int[n];
		for (int d = 0; d < n; ++d)
			size[d] = imgout.dimension(d);

		// Starting position, for explicit curves its easier to choose a
		// starting point
		// Input angles in degrees for the lut.
		position[0] = min[0];
		position[1] = // amplitude * SinCosinelut.getTable().getSine(position[0]
						// + phase);
				amplitude * Math.sin(Math.toRadians(position[0] + phase));
		newpos[0] = position[0];
		newpos[1] = position[1];
		sigma[0] = 1;
		sigma[1] = 1;

		while (true) {
			// increment = stepsize * amplitude *
			// Math.cos(Math.toRadians(position[0] + phase));

			for (int d = 0; d < n; ++d)
				position[d] = newpos[d];

			// Transform the co-ordinates back as double[]
			backpos = TransformCordinates.transformback(newpos, size, min, max);

			setpos[0] = (int) Math.round(backpos[0]);
			setpos[1] = (int) Math.round(backpos[1]);

			// To set the pixel intensity
			AddGaussian.addGaussian(imgout, backpos, sigma);

			// To make sure that the values transformed back are not out of
			// bounds
			if (backpos[0] < imgout.realMax(0) - imgout.realMin(0) || backpos[0] > imgout.realMin(0)
					|| backpos[1] < imgout.realMax(1) - imgout.realMin(1) || backpos[1] > imgout.realMin(1))

				outbound.setPosition(setpos);
			// Increment from starting position (min) towards max

			newpos[0] = position[0] + stepsize;
			newpos[1] = 
					amplitude * Math.sin(Math.toRadians(newpos[0] + phase));
			// General Stopping criteria of moving along a curve, when we hit a
			// boundary
			if (newpos[0] >= max[0] || newpos[0] <= min[0] || newpos[1] >= max[1] || newpos[1] <= min[1])

				break;
		}

	}

	public static void DrawLine(RandomAccessibleInterval<FloatType> imgout, double[] min, double[] max, double slope,
			double intercept) {

		int n = imgout.numDimensions();
		double[] size = new double[n];
		double[] position = new double[n];
		double[] newpos = new double[n];
		double[] backpos = new double[n];
		double[] sigma = new double[n];
		final RandomAccess<FloatType> outbound = imgout.randomAccess();
		double stepsize = 0.1;
		int[] setpos = new int[n];
		for (int d = 0; d < n; ++d)
			size[d] = imgout.dimension(d);

		// Starting position, for explicit curves its easier to choose a
		// starting point
		position[0] = min[0];
		position[1] = slope * min[0] + intercept;
		newpos[0] = position[0];
		newpos[1] = position[1];
		sigma[0] = 1;
		sigma[1] = 1;
		double dx = stepsize / (1 + slope * slope);
		double dy = slope * dx;
		if (Math.abs(Math.toDegrees(Math.atan2(dy, dx))) <= slopethresh && Math.abs(Math.toDegrees(Math.atan2(dy, dx))) > slopethresh - 1  ){
			
			dx = 0;
			dy = stepsize* Math.signum(slope);
			
		}
		while (true) {
			 
			for (int d = 0; d < n; ++d)
				position[d] = newpos[d];

			// Transform the co-ordinates back as double[]
			backpos = TransformCordinates.transformback(newpos, size, min, max);

			setpos[0] = (int) Math.round(backpos[0]);
			setpos[1] = (int) Math.round(backpos[1]);

			// To set the pixel intensity
			AddGaussian.addGaussian(imgout, backpos, sigma);

			// To make sure that the values transformed back are not out of
			// bounds
			if (backpos[0] < imgout.realMax(0) - imgout.realMin(0) || backpos[0] > imgout.realMin(0)
					|| backpos[1] < imgout.realMax(1) - imgout.realMin(1) || backpos[1] > imgout.realMin(1))

				outbound.setPosition(setpos);
			// Increment from starting position (min) towards max

			newpos[0] = position[0] + dx;
			newpos[1] = position[1] + dy;
			// General Stopping criteria of moving along a curve, when we hit a
			// boundary

			if (newpos[0] >= max[0] || newpos[0] <= min[0] || newpos[1] >= max[1] || newpos[1] <= min[1])

				break;

		}

	}

	public static void DrawDetectedGaussians(RandomAccessibleInterval<FloatType> imgout,
			final ArrayList<double[]> parameters) {

		final int n = imgout.numDimensions();
		ArrayList<Double> Amplitudelist = new ArrayList<Double>();
		ArrayList<double[]> Meanlist = new ArrayList<double[]>();
		ArrayList<double[]> Sigmalist = new ArrayList<double[]>();
		for (int index = 0; index < parameters.size(); ++index) {

			final double Amplitude = parameters.get(index)[0];
			final double Mean[] = new double[n];
			final double Sigma[] = new double[n];

			for (int d = 0; d < n; ++d) {
				Mean[d] = parameters.get(index)[d + 1];
			}
			for (int d = 0; d < n; ++d) {
				Sigma[d] = parameters.get(index)[n + d + 1];

			}

			Amplitudelist.add(Amplitude);

			Meanlist.add(Mean);

			Sigmalist.add(Sigma);

		}

		for (int index = 0; index < parameters.size(); ++index) {

			AddGaussian.addGaussian(imgout, Amplitudelist.get(index), Meanlist.get(index), Sigmalist.get(index));

		}

	}

	

	
	
	// Draw a line between start end of the MT
		public static void DrawstartLine(RandomAccessibleInterval<FloatType> imgout, final ArrayList<Indexedlength> startlist,
				final ArrayList<Indexedlength> endlist,
				final double[] sigma) {

			int ndims = imgout.numDimensions();

			for (int index  = 0; index < startlist.size(); ++index){
			
			double[] startline = new double[ndims];
			double[] endline = new double[ndims];

			for (int d = 0; d < ndims; ++d) {
				startline[d] = startlist.get(index).currentpos[d];
				endline[d] = endlist.get(index).currentpos[d];
			}

			double slope = (endline[1] - startline[1]) / (endline[0] - startline[0]);
			final double[] steppos = new double[ndims];
			double stepsize = 1;
			double[] dxvector = {stepsize / Math.sqrt(1 + slope * slope),  stepsize * slope / Math.sqrt(1 + slope * slope) };
			
			if (Math.abs(Math.toDegrees(Math.atan2(dxvector[1], dxvector[0]))) <= slopethresh && Math.abs(Math.toDegrees(Math.atan2(dxvector[1], dxvector[0]))) > slopethresh - 1  ){
				
				dxvector[0] = 0;
				dxvector[1] = stepsize* Math.signum(slope);
				
			}
				for (int d  = 0; d < ndims ; ++d)
					steppos[d] = startline[d];
				while (true) {

					AddGaussian.addGaussian(imgout, steppos, sigma);
					steppos[0] +=  dxvector[0];
					steppos[1] +=  dxvector[1];
					


					if (steppos[0] > endline[0] || steppos[1] > endline[1] && slope >= 0)
						break;
					if (steppos[0] > endline[0] || steppos[1] < endline[1] && slope < 0)
						break;
				}
			
			
			}
		}

		
	public static void Drawexactline(RandomAccessibleInterval<FloatType> imgout, double slope, double intercept,
			final IntensityType setintensity) {

		int n = imgout.numDimensions();
		final double[] realpos = new double[n];
		double sigmasq, sigma = 1;
		sigmasq = sigma * sigma;
		sigma = Math.sqrt(sigmasq);
		final Cursor<FloatType> inputcursor = Views.iterable(imgout).localizingCursor();
		final RandomAccess<FloatType> outbound = imgout.randomAccess();

		while (inputcursor.hasNext()) {

			inputcursor.fwd();
			inputcursor.localize(realpos);

			// To set the pixel intensity as the shortest distance to the curve
			double distance = 0;
			double intensity = 0;

			Finalfunction linefunction = new Finalfunction(realpos, slope, intercept);
			distance = linefunction.Linefunctiondist();

			outbound.setPosition(inputcursor);
			if (distance < sigma)
				intensity = (1 / (sigma * Math.sqrt(2 * Math.PI))) * Math.exp(-distance * distance / (2 * sigmasq));
			else
				intensity = 0;

			switch (setintensity) {
			case Original:
				outbound.get().setReal(distance);
				break;

			case Gaussian:
				outbound.get().setReal(intensity);
				break;
			case One:
				outbound.get().setReal(intensity);
				break;
			default:
				outbound.get().setReal(intensity);
				break;

			}

		}
	}

	public static void DrawRoiimageline(
			final RandomAccessibleInterval<FloatType> imgout,
			final RandomAccessibleInterval<FloatType> roiimage,
			final double slope, final double intercept){
		
		int n = imgout.numDimensions();
		double  sigma = 1.0;
		
		final Cursor<FloatType> inputcursor = Views.iterable(roiimage).localizingCursor();
		double[] newposition = new double[n];
		double[] minVal = { Double.MAX_VALUE, Double.MAX_VALUE };
		double[] maxVal = { Double.MIN_VALUE, Double.MIN_VALUE };
		while(inputcursor.hasNext()){
			
			inputcursor.fwd();
			
			
				inputcursor.localize(newposition);
				long pointonline = (long) (newposition[1] - slope * newposition[0] - intercept);

				// To get the min and max co-rodinates along the line so we have
				// starting points to
				// move on the line smoothly

				if (pointonline == 0) {

					for (int d = 0; d < n; ++d) {
						if (inputcursor.getDoublePosition(d) <= minVal[d])
							minVal[d] = inputcursor.getDoublePosition(d);

						if (inputcursor.getDoublePosition(d) >= maxVal[d])
							maxVal[d] = inputcursor.getDoublePosition(d);

					}

				}

			
				
			
			
		}
		
		double stepsize = 1;
		final double[] steppos = new double[n];
		double dx = stepsize / Math.sqrt(1 + slope * slope);
		double dy = slope * dx;
		if (Math.abs(Math.toDegrees(Math.atan2(dy, dx))) <= slopethresh && Math.abs(Math.toDegrees(Math.atan2(dy, dx))) > slopethresh - 1  ){
			
			dx = 0;
			dy = stepsize* Math.signum(slope);
			
		}
		
		if (slope >= 0) {
			for (int d  = 0; d < n ; ++d)
				steppos[d] = minVal[d];
			while (true) {

				AddGaussian.addGaussian(imgout, steppos, new double[] { sigma, sigma });
				steppos[0] +=  dx;
				steppos[1] += dy;
				

				

				if (steppos[0] > maxVal[0] || steppos[1] > maxVal[1])
					break;
			}
		}
		int negcount = 0;
		if (slope < 0) {
			steppos[0] = minVal[0];
			steppos[1] = maxVal[1];
			while (true) {
				AddGaussian.addGaussian(imgout, steppos, new double[] { sigma, sigma });
				steppos[0] = minVal[0] + negcount * stepsize / Math.sqrt(1 + slope * slope);
				steppos[1] = maxVal[1] + negcount * stepsize * slope / Math.sqrt(1 + slope * slope);

				

				negcount++;

				if (steppos[0] > maxVal[0] || steppos[1] < minVal[1])
					break;
			}
		}

		
		
	}
	
	public static void DrawRoiimagelineprep(
			final RandomAccessibleInterval<FloatType> imgout,
			final RandomAccessibleInterval<FloatType> roiimage,
			final double prepline){
		
		int n = imgout.numDimensions();
		double  sigma = 1.0;
		
		final Cursor<FloatType> inputcursor = Views.iterable(roiimage).localizingCursor();
		double[] newposition = new double[n];
		double[] minVal = { Double.MAX_VALUE, Double.MAX_VALUE };
		double[] maxVal = { Double.MIN_VALUE, Double.MIN_VALUE };
		while(inputcursor.hasNext()){
			
			inputcursor.fwd();
			
			
				inputcursor.localize(newposition);
				long pointonline = (long) (newposition[0] - prepline);

				// To get the min and max co-rodinates along the line so we have
				// starting points to
				// move on the line smoothly

				if (pointonline == 0) {

					for (int d = 0; d < n; ++d) {
						if (inputcursor.getDoublePosition(d) <= minVal[d])
							minVal[d] = inputcursor.getDoublePosition(d);

						if (inputcursor.getDoublePosition(d) >= maxVal[d])
							maxVal[d] = inputcursor.getDoublePosition(d);

					}

			}
			
		}
		
		double stepsize = 1;
		final double[] steppos = new double[n];
		double dy = stepsize;
		
		
			for (int d  = 0; d < n ; ++d)
				steppos[d] = minVal[d];
			while (true) {

				AddGaussian.addGaussian(imgout, steppos, new double[] { sigma, sigma });
				
				steppos[1] += dy;
				

				

				if (steppos[1] > maxVal[1])
					break;
			}
		

		
		
	}
	
	

	public static void DrawRoiline(
			final RandomAccessibleInterval<FloatType> imgout,
			final EllipseRoi roi,
			final double slope, final double intercept){
		
		int n = imgout.numDimensions();
		double  sigma = 1.0;
		
		final Cursor<FloatType> inputcursor = Views.iterable(imgout).localizingCursor();
		double[] newposition = new double[n];
		double[] minVal = { Double.MAX_VALUE, Double.MAX_VALUE };
		double[] maxVal = { Double.MIN_VALUE, Double.MIN_VALUE };
		while(inputcursor.hasNext()){
			
			inputcursor.fwd();
			
			final int x = inputcursor.getIntPosition(0);
			final int y = inputcursor.getIntPosition(1);
			
			if (roi.contains(x, y)){
				inputcursor.localize(newposition);
				long pointonline = (long) (newposition[1] - slope * newposition[0] - intercept);

				// To get the min and max co-rodinates along the line so we have
				// starting points to
				// move on the line smoothly

				if (pointonline == 0) {

					for (int d = 0; d < n; ++d) {
						if (inputcursor.getDoublePosition(d) <= minVal[d])
							minVal[d] = inputcursor.getDoublePosition(d);

						if (inputcursor.getDoublePosition(d) >= maxVal[d])
							maxVal[d] = inputcursor.getDoublePosition(d);

					}

				}

			
				
			}
			
		}
		
		double stepsize = 1;
		final double[] steppos = new double[n];
		double dx = stepsize / Math.sqrt(1 + slope * slope);
		double dy = slope * dx;
		if (Math.abs(Math.toDegrees(Math.atan2(dy, dx))) <= slopethresh && Math.abs(Math.toDegrees(Math.atan2(dy, dx))) > slopethresh - 1  ){
			
			dx = 0;
			dy = stepsize* Math.signum(slope);
			
		}
		
		if (slope >= 0) {
			for (int d  = 0; d < n ; ++d)
				steppos[d] = minVal[d];
			while (true) {

				AddGaussian.addGaussian(imgout, steppos, new double[] { sigma, sigma });
				steppos[0] +=  dx;
				steppos[1] += dy;
				

				

				if (steppos[0] > maxVal[0] || steppos[1] > maxVal[1])
					break;
			}
		}
		int negcount = 0;
		if (slope < 0) {
			steppos[0] = minVal[0];
			steppos[1] = maxVal[1];
			while (true) {
				AddGaussian.addGaussian(imgout, steppos, new double[] { sigma, sigma });
				steppos[0] = minVal[0] + negcount * stepsize / Math.sqrt(1 + slope * slope);
				steppos[1] = maxVal[1] + negcount * stepsize * slope / Math.sqrt(1 + slope * slope);

				

				negcount++;

				if (steppos[0] > maxVal[0] || steppos[1] < minVal[1])
					break;
			}
		}

		
		
	}
	
	public static void DrawRoilineprep(
			final RandomAccessibleInterval<FloatType> imgout,
			final EllipseRoi roi,
			final double prepline){
		
		int n = imgout.numDimensions();
		double  sigma = 1.0;
		
		final Cursor<FloatType> inputcursor = Views.iterable(imgout).localizingCursor();
		double[] newposition = new double[n];
		double[] minVal = { Double.MAX_VALUE, Double.MAX_VALUE };
		double[] maxVal = { Double.MIN_VALUE, Double.MIN_VALUE };
		while(inputcursor.hasNext()){
			
			inputcursor.fwd();
			
			final int x = inputcursor.getIntPosition(0);
			final int y = inputcursor.getIntPosition(1);
			
			if (roi.contains(x, y)){
				inputcursor.localize(newposition);
				long pointonline = (long) (newposition[0] - prepline);

				// To get the min and max co-rodinates along the line so we have
				// starting points to
				// move on the line smoothly

				if (pointonline == 0) {

					for (int d = 0; d < n; ++d) {
						if (inputcursor.getDoublePosition(d) <= minVal[d])
							minVal[d] = inputcursor.getDoublePosition(d);

						if (inputcursor.getDoublePosition(d) >= maxVal[d])
							maxVal[d] = inputcursor.getDoublePosition(d);

					}

				}
			}
			
		}
		
		double stepsize = 1;
		final double[] steppos = new double[n];
		double dy = stepsize;
		
		
			for (int d  = 0; d < n ; ++d)
				steppos[d] = minVal[d];
			while (true) {

				AddGaussian.addGaussian(imgout, steppos, new double[] { sigma, sigma });
				
				steppos[1] += dy;
				

				

				if (steppos[1] > maxVal[1])
					break;
			}
		

		
		
	}
	
	
	
	public static void DrawTruncatedline(RandomAccessibleInterval<FloatType> imgout,
			RandomAccessibleInterval<IntType> intimg, double slope, double intercept,
			int label) {

		int n = imgout.numDimensions();
		final double[] realpos = new double[n];
		double  sigma = 1.0;
		
		final Cursor<FloatType> inputcursor = Views.iterable(imgout).localizingCursor();
		double[] newposition = new double[n];
		RandomAccess<IntType> ranac = intimg.randomAccess();
		double[] minVal = { Double.MAX_VALUE, Double.MAX_VALUE };
		double[] maxVal = { Double.MIN_VALUE, Double.MIN_VALUE };
		while (inputcursor.hasNext()) {

			inputcursor.fwd();
			inputcursor.localize(realpos);

			ranac.setPosition(inputcursor);
			int i = ranac.get().get();

			if (i == label) {
				inputcursor.localize(newposition);
				long pointonline = (long) (newposition[1] - slope * newposition[0] - intercept);

				// To get the min and max co-rodinates along the line so we have
				// starting points to
				// move on the line smoothly

				if (pointonline == 0) {

					for (int d = 0; d < n; ++d) {
						if (inputcursor.getDoublePosition(d) <= minVal[d])
							minVal[d] = inputcursor.getDoublePosition(d);

						if (inputcursor.getDoublePosition(d) >= maxVal[d])
							maxVal[d] = inputcursor.getDoublePosition(d);

					}

				}

			}

		}

		final double[] steppos = new double[n];
		int count = 0;
		double stepsize = 1;
		if (slope >= 0) {
			for (int d  = 0; d < n ; ++d)
				steppos[d] = minVal[d];
			while (true) {

				AddGaussian.addGaussian(imgout, 1.0, steppos, new double[] { sigma, sigma });
				steppos[0] = minVal[0] + count * stepsize / Math.sqrt(1 + slope * slope);
				steppos[1] = minVal[1] + count * stepsize * slope / Math.sqrt(1 + slope * slope);

				

				count++;

				if (steppos[0] > maxVal[0] || steppos[1] > maxVal[1])
					break;
			}
		}
		int negcount = 0;
		if (slope < 0) {
			steppos[0] = minVal[0];
			steppos[1] = maxVal[1];
			while (true) {
				AddGaussian.addGaussian(imgout, 1.0, steppos, new double[] { sigma, sigma });
				steppos[0] = minVal[0] + negcount * stepsize / Math.sqrt(1 + slope * slope);
				steppos[1] = maxVal[1] + negcount * stepsize * slope / Math.sqrt(1 + slope * slope);

				

				negcount++;

				if (steppos[0] > maxVal[0] || steppos[1] < minVal[1])
					break;
			}
		}

	}

	public static void DrawTruncatedprepline(RandomAccessibleInterval<FloatType> imgout,
			RandomAccessibleInterval<IntType> intimg, double ifprep,
			int label) {

		int n = imgout.numDimensions();
		final double[] realpos = new double[n];
		double  sigma = 1.0;
		
		final Cursor<FloatType> inputcursor = Views.iterable(imgout).localizingCursor();
		double[] newposition = new double[n];
		RandomAccess<IntType> ranac = intimg.randomAccess();
		double[] minVal = { Double.MAX_VALUE, Double.MAX_VALUE };
		double[] maxVal = { Double.MIN_VALUE, Double.MIN_VALUE };
		while (inputcursor.hasNext()) {

			inputcursor.fwd();
			inputcursor.localize(realpos);

			ranac.setPosition(inputcursor);
			int i = ranac.get().get();

			if (i == label) {
				inputcursor.localize(newposition);
				long pointonline = (long) (newposition[1] - ifprep);

				// To get the min and max co-rodinates along the line so we have
				// starting points to
				// move on the line smoothly

				if (pointonline == 0) {

					for (int d = 0; d < n; ++d) {
						if (inputcursor.getDoublePosition(d) <= minVal[d])
							minVal[d] = inputcursor.getDoublePosition(d);

						if (inputcursor.getDoublePosition(d) >= maxVal[d])
							maxVal[d] = inputcursor.getDoublePosition(d);

					}

				}

			}

		}

		double stepsize = 1;
		final double[] steppos = new double[n];
		double dy = stepsize;
		
		
			for (int d  = 0; d < n ; ++d)
				steppos[d] = minVal[d];
			while (true) {

				AddGaussian.addGaussian(imgout, steppos, new double[] { sigma, sigma });
				
				steppos[1] += dy;
				

				

				if (steppos[1] > maxVal[1])
					break;
			}
		

	}
	
	
	public static void Drawexactline(RandomAccessibleInterval<FloatType> imgout, Img<IntType> intimg, double slope,
			double intercept, int label) {

		int n = imgout.numDimensions();
		final double[] realpos = new double[n];
		double sigmasq, sigma = 1.0;
		sigmasq = sigma * sigma;
		final Cursor<FloatType> inputcursor = Views.iterable(imgout).localizingCursor();

		while (inputcursor.hasNext()) {

			inputcursor.fwd();
			inputcursor.localize(realpos);

			// To set the pixel intensity as the shortest distance to the curve
			double distance = 0;
			double intensity = 0;

			Finalfunction linefunction = new Finalfunction(realpos, slope, intercept);
			distance = linefunction.Linefunctiondist();
			final RandomAccess<FloatType> outbound = imgout.randomAccess();
			outbound.setPosition(inputcursor);

			if (distance < 5 * sigma)
				intensity = (1 / (sigma * Math.sqrt(2 * Math.PI))) * Math.exp(-distance * distance / (2 * sigmasq));
			else
				intensity = 0;

			RandomAccess<IntType> ranac = intimg.randomAccess();
			ranac.setPosition(inputcursor);
			int i = ranac.get().get();

			if (i == label) {

				outbound.get().setReal(intensity);

				final double[] position = new double[n];
				outbound.localize(position);

			}

		}

	}

	public static double Distance(final double[] cordone, final double[] cordtwo) {

		double distance = 0;

		for (int d = 0; d < cordone.length; ++d) {

			distance += Math.pow((cordone[d] - cordtwo[d]), 2);

		}
		return Math.sqrt(distance);
	}
}