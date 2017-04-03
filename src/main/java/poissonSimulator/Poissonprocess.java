package poissonSimulator;

import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class Poissonprocess {

	
	final static Random rnd = new Random( 464232194 );
	final public static float minValue = 0.0001f;
	public final static float avgIntensity = 1;
	
	public static RandomAccessibleInterval< FloatType > poissonProcess( final RandomAccessibleInterval< FloatType > in, final float poissonSNR )
	{
		final RandomAccessibleInterval< FloatType > out = new ArrayImgFactory< FloatType >().create( in, new FloatType() );

		final Cursor< FloatType > c = Views.iterable(out).localizingCursor();
		final RandomAccess< FloatType > r = in.randomAccess();
		
		while ( c.hasNext() )
		{
			c.fwd();
			r.setPosition( c );
			c.get().set( r.get() );
			
			
		}
		
		// based on an average intensity of 5 inside the sample
		poissonProcess( out, poissonSNR, rnd );
		
		return out;
	}
	public static void poissonProcess( final RandomAccessibleInterval<FloatType> img, final double SNR, final Random rnd )
	{
		// based on an average intensity of 5, a multiplicator of 1 corresponds to a SNR of 2.23 = sqrt( 5 );	
		final double mul = Math.pow( SNR / Math.sqrt( 5 ), 2 );
		
		final NumberGeneratorImage< FloatType> ng = new NumberGeneratorImage< FloatType>( img, mul );
		final PoissonGenerator pg = new PoissonGenerator( ng, rnd );
		
		
		
		for ( final FloatType v : Views.iterable( img ) )
		{
			ng.fwd();
			v.set( pg.nextValue().floatValue() );
		}
		
		
		
	}
	
}
