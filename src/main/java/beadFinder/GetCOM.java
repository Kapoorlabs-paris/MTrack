package beadFinder;

import ij.gui.Roi;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class GetCOM {
	public static long[] getProps(final RandomAccessibleInterval<FloatType> source, final RandomAccessibleInterval<FloatType> target, final Roi roi, final int zplane) {

		// 3 co-ordinates for COM 
		long[] center = new long[ 3 ];

		double Intensity = 0;
		
		
		
		
		double SumX = 0;
		double SumY = 0;
		Cursor<FloatType> currentcursor = Views.iterable(source).localizingCursor();

		RandomAccess<FloatType> targetran = target.randomAccess();
		final double[] position = new double[ 2 ];

		while (currentcursor.hasNext()) {

			currentcursor.fwd();

			currentcursor.localize(position);

			int x = (int) position[0];
			int y = (int) position[1];

			if (roi.contains(x, y)) {

				targetran.setPosition(currentcursor);
				
				SumX += currentcursor.getDoublePosition(0) * currentcursor.get().getRealDouble();
				SumY += currentcursor.getDoublePosition(1) * currentcursor.get().getRealDouble();
				Intensity += currentcursor.get().getRealDouble();
				
				
				
			}

		}
		
		center[ 0 ] = (long)(SumX / Intensity);
		center[ 1 ] = (long)(SumY / Intensity);
		
		center[ 2 ] = zplane;
		
		

		return center;

	}
	
}
