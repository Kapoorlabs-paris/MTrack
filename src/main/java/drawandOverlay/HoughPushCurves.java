package drawandOverlay;


import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class HoughPushCurves {

	public static void Houghspace(RandomAccessibleInterval<FloatType> inputimage,
			RandomAccessibleInterval<FloatType> imgout, 
			double[] min, double[] max, Float threshold) {

		int n = inputimage.numDimensions();

		final long[] position = new long[n];
		double Amplitude, Phase;

		
		final Cursor<FloatType> inputcursor = Views.iterable(inputimage).localizingCursor();
		
		// for every function (as defined by an individual pixel)
		while (inputcursor.hasNext()) {

			inputcursor.fwd();
			
				if (inputcursor.get().get() > threshold  ) {
					inputcursor.localize(position);
					Amplitude = Math.sqrt(Math.pow(position[0], 2) + Math.pow(position[1], 2));
					Phase = Math.toDegrees(Math.atan2(position[0], position[1]));

					// draw the function into the hough space
					PushCurves.DrawSine(imgout, min, max, Amplitude, Phase);
				
			}
		}
		
	}

}
