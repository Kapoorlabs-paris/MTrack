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
