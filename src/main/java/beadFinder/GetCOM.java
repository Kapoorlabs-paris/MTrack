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
		long[] center = new long[ target.numDimensions() ];

		double Intensity = 0;
		
		
		
		
		double SumX = 0;
		double SumY = 0;
		Cursor<FloatType> currentcursor = Views.iterable(source).localizingCursor();

		RandomAccess<FloatType> targetran = target.randomAccess();
		final double[] position = new double[ target.numDimensions() ];

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
		
		
		

		return center;

	}
	
	
	public static double[] getProps(final RandomAccessibleInterval<FloatType> source, final double[] mean) {

		// 3 co-ordinates for COM 
		double[] center = new double[ source.numDimensions() ];

		double Intensity = 0;
		
		
		
		
		double SumX = 0;
		double SumY = 0;
		Cursor<FloatType> currentcursor = Views.iterable(source).localizingCursor();

		final double[] position = new double[ source.numDimensions() ];

		while (currentcursor.hasNext()) {

			currentcursor.fwd();

			currentcursor.localize(position);

			

				
				SumX += (currentcursor.getDoublePosition(0) - mean[0]) * (currentcursor.get().getRealDouble() - mean[0]);
				SumY += (currentcursor.getDoublePosition(1) - mean[1]) * (currentcursor.get().getRealDouble() - mean[1]);
				Intensity += currentcursor.get().getRealDouble();
				
				
				
			

		}
		
		center[ 0 ] = (long)(SumX / Intensity);
		center[ 1 ] = (long)(SumY / Intensity);
		
		
		

		return center;

	}
	
}
