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
package preProcessing;

import java.util.Arrays;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.BenchmarkAlgorithm;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.algorithm.neighborhood.DiamondShape.NeighborhoodsAccessible;
import net.imglib2.algorithm.neighborhood.Neighborhood;
import net.imglib2.algorithm.neighborhood.RectangleShape;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Util;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

/**
 * A simple median filter that operates on 1D, 2D or 3D images.
 * <p>
 * For 3D images, the filtering is done only in 2D XY slices. Indeed, this
 * filter is made mainly to remove shot noise on cameras, for which the median
 * calculated on a simple 2D neighborhood is enough.
 * 
 * @author Jean-Yves Tinevez - 2015
 *
 * @param <T>
 *            the type of the source image.
 */
public class MedianFilter2D< T extends RealType< T > & NativeType< T >> extends BenchmarkAlgorithm implements OutputAlgorithm< RandomAccessibleInterval< T >>
{
	private static final String BASE_ERROR_MSG = "[MedianFiler2D] ";

	private final RandomAccessibleInterval< T > source;

	private RandomAccessibleInterval< T > output;

	private final int radius;

	/**
	 * Instantiate a new median filter that will operate on the specified
	 * source.
	 * 
	 * @param currentPreprocessedimg
	 *            the source to operate on.
	 * @param radius
	 *            determines the size of the neighborhood. In 2D or 3D, a radius
	 *            of 1 will generate a 3x3 neighborhood.
	 */
	public MedianFilter2D( final RandomAccessibleInterval<T> source, final int radius )
	{
		this.source = source;
		this.radius = radius;
	}

	@Override
	public boolean checkInput()
	{
		if ( source.numDimensions() > 3 )
		{
			errorMessage = BASE_ERROR_MSG + " Can only operate on 1D, 2D or 3D images. Got " + source.numDimensions() + "D.";
			return false;
		}
		if ( radius < 1 )
		{
			errorMessage = BASE_ERROR_MSG + "Radius cannot be smaller than 1. Got " + radius + ".";
			return false;
		}
		return true;
	}

	@Override
	public boolean process()
	{
		final long start = System.currentTimeMillis();

		final T type = source.randomAccess().get().createVariable();
		final ImgFactory< T > factory = Util.getArrayOrCellImgFactory( source, type );
		this.output = factory.create( source, type );

		if ( source.numDimensions() > 2 )
		{
			final long nz = source.dimension( 2 );
			for ( long z = 0; z < nz; z++ )
			{
				final IntervalView< T > slice = Views.hyperSlice( source, 2, z );
				final IntervalView< T > outputSlice = Views.hyperSlice( output, 2, z );
			   
				processSlice( slice, outputSlice );
			}
		}
		else
		{
			processSlice( source, Views.iterable(output) );
		}

		this.processingTime = System.currentTimeMillis() - start;
		return true;
	}

	private void processSlice( final RandomAccessibleInterval< T > in, final IterableInterval< T > out )
	{
		final Cursor< T > cursor = out.localizingCursor();

		final RectangleShape shape = new RectangleShape( radius, false );
		final net.imglib2.algorithm.neighborhood.RectangleShape.NeighborhoodsAccessible<T> nracessible = shape.neighborhoodsRandomAccessible( Views.extendZero( in ) );
		final RandomAccess< Neighborhood< T >> nra = nracessible.randomAccess( in );

		final int size = ( int ) nra.get().size();
		final double[] values = new double[ size ];

		// Fill buffer with median values.
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			nra.setPosition( cursor );
			int index = 0;
			for ( final T pixel : nra.get() )
			{
				values[ index++ ] = pixel.getRealDouble();
			}

			Arrays.sort( values, 0, index );
			cursor.get().setReal( values[ ( index - 1 ) / 2 ] );
		}
	}

	@Override
	public RandomAccessibleInterval<T> getResult()
	{
		return output;
	}
}
