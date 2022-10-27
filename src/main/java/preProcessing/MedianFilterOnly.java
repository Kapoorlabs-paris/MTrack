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

import javax.swing.JProgressBar;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.BenchmarkAlgorithm;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.algorithm.gauss3.Gauss3;
import net.imglib2.algorithm.neighborhood.DiamondShape.NeighborhoodsAccessible;
import net.imglib2.algorithm.stats.Normalize;
import net.imglib2.exception.IncompatibleTypeException;
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
import peakFitter.FitterUtils;

/**
 * A simple flat field and median filter that operates on 1D, 2D or 3D images.
 * <p>
 * For 3D images, the filtering is done only in 2D XY slices. Removes shot noise,
 * corrects for uneven illumination
 * 
 * @author V. Kapoor - 2017, Jean-Yves Tinevez - 2015
 *
 * @param <FloatType>
 *            the type of the source image.
 */
public class MedianFilterOnly extends BenchmarkAlgorithm implements OutputAlgorithm< RandomAccessibleInterval< FloatType >>
{
	private static final String BASE_ERROR_MSG = "[FlatField2D] ";

	private final RandomAccessibleInterval< FloatType > source;

	private RandomAccessibleInterval< FloatType > output;

	private final int radius;
	
	private final double[] psf;
	
	private  JProgressBar jpb;

	/**
	 * Instantiate a new Flat field correction filter that will operate on the specified
	 * source.
	 * 
	 * @param currentPreprocessedimg
	 *            the source to operate on.
	 * @param radius
	 *            determines the size of the neighborhood. In 2D or 3D, a radius
	 *            of 1 will generate a 3x3 neighborhood.
	 */
	public MedianFilterOnly( final RandomAccessibleInterval<FloatType> source, final int radius, final double[] psf )
	{
		this.source = source;
		this.radius = radius;
		this.psf = psf;
	}
	
	
	public MedianFilterOnly( final RandomAccessibleInterval<FloatType> source, final int radius, final JProgressBar jpb, final double[] psf  )
	{
		this.source = source;
		this.radius = radius;
		this.psf = psf;
		this.jpb = jpb;
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

		final FloatType type = source.randomAccess().get().createVariable();
		final ImgFactory< FloatType > factory = Util.getArrayOrCellImgFactory( source, type );
		this.output = factory.create( source, type );

		
		
		if ( source.numDimensions() > 2 )
		{
			final long nz = source.dimension( 2 );
			
			double percent = 0;
			for ( long z = 0; z < nz; z++ )
			{
				
				percent++;
				final IntervalView< FloatType > slice = Views.hyperSlice( source, 2, z );
				final IntervalView< FloatType > outputSlice = Views.hyperSlice( output, 2, z );
			   
				if(jpb!=null)
				FitterUtils.SetProgressBar(jpb, 100 * percent/nz, "Doing Median Filtering, please wait..");
				
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

	
  
	
       /**
        * 
        * @param in
        * 
        * The randomaccessible interval on which we apply a gaussian filter (big radius =  image dim / n pixels) and then
        * subtract it from the original image to create a background corrected image
        * 
        * @param out
        * 
        * Median filter is applied on the background corrected image generated from the previous step and is the pre-processed image to be
        * used by the Line finders of the MTV tracker
        * 
        */
	private void processSlice( final RandomAccessibleInterval< FloatType > in, final IterableInterval< FloatType > out )
	{
		
		double[] sigma = new double[in.numDimensions()];
		for (int d = 0; d < in.numDimensions(); ++d) {
			sigma[d] = (int) Math.round((in.realMax(d) - in.realMin(d)) / 20.0);
		}
		
		

		final Cursor< FloatType > cursor = out.localizingCursor();

		final RectangleShape shape = new RectangleShape( radius, false );
		final net.imglib2.algorithm.neighborhood.RectangleShape.NeighborhoodsAccessible<FloatType> nracessible = shape.neighborhoodsRandomAccessible( Views.extendZero( in ) );
		final RandomAccess< Neighborhood< FloatType >> nra = nracessible.randomAccess( in );

		final int size = ( int ) nra.get().size();
		final double[] values = new double[ size ];

		// Fill buffer with median values.
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			nra.setPosition( cursor );
			
			
			int index = 0;
			for ( final FloatType pixel : nra.get() )
			{
				values[ index++ ] = pixel.getRealDouble();
			}

			Arrays.sort( values, 0, index );
			cursor.get().setReal( values[ ( index - 1 ) / 2 ] );
		}
	
	}

	@Override
	public RandomAccessibleInterval<FloatType> getResult()
	{
		return output;
	}
}
