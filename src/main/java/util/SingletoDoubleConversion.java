/*-
 * #%L
 * Microtubule tracker.
 * %%
 * Copyright (C) 2017 - 2022 MTrack developers.
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
package util;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public class SingletoDoubleConversion {

	
	
	public static void MConverter(RandomAccessibleInterval<FloatType> originalimg) {
		
		
		if (originalimg.numDimensions() == 3) {
			
			
			RandomAccessibleInterval<FloatType> seed = Views.hyperSlice(originalimg, 2, originalimg.min(2));
			
			
          final long endt = originalimg.dimension( 2 );
			
			for ( long t = originalimg.min(2) + 1; t < endt; t++ )
			{
				
				final RandomAccessibleInterval< FloatType > slice = Views.hyperSlice( originalimg, 2, t );
				final RandomAccessibleInterval< FloatType > outputslice = Views.hyperSlice( originalimg, 2, t );
				processSlice(slice, seed, outputslice);
				
				
			}
		}
		
		
	}
	
	public static void processSlice( final RandomAccessibleInterval< FloatType > slice, final RandomAccessibleInterval< FloatType > seed, final RandomAccessibleInterval< FloatType > outputslice )
	{
		
		RandomAccessibleInterval<FloatType> copyslice = util.CopyUtils.copyImage(slice);
		RandomAccessibleInterval<FloatType> copyseed = util.CopyUtils.copyImage(seed);
		
		subtract(copyslice, copyseed);
		
		
		final Cursor<FloatType> cursor = Views.iterable(outputslice).localizingCursor();
		final RandomAccess<FloatType> sliceran = copyslice.randomAccess();
		
		
		while(cursor.hasNext()) {
			
			
			
			cursor.fwd();
			sliceran.setPosition(cursor);
			cursor.get().set(sliceran.get());
			
		}
		
		
	}
	
	 public static void subtract(RandomAccessibleInterval<FloatType> slice, RandomAccessibleInterval<FloatType> seed){
			
			
			Cursor<FloatType> cursor = Views.iterable(slice).localizingCursor();
			RandomAccess<FloatType> seedran = seed.randomAccess();
			double value;
			while(cursor.hasNext()){
				
				cursor.fwd();
				
				seedran.setPosition(cursor);
				
				value = cursor.get().get() - seedran.get().get();
				cursor.get().setReal(value);
				
				
			}
			
	 }
}
