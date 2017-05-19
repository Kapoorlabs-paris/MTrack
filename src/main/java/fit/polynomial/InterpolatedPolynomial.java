package fit.polynomial;

import java.util.ArrayList;
import java.util.Collection;

import fit.AbstractFunction;
import fit.InterpolatedFunction;
import mpicbg.models.IllDefinedDataPointsException;
import mpicbg.models.NotEnoughDataPointsException;
import mpicbg.models.Point;

/**
 * @author Stephan Preibisch
 */

public class InterpolatedPolynomial<
		A extends AbstractFunction< A > & Polynomial< A, Point >,
		B extends AbstractFunction< B > & Polynomial< B, Point > >
	extends InterpolatedFunction< A, B, InterpolatedPolynomial< A, B > >
	implements Polynomial< InterpolatedPolynomial< A, B >, Point >
{
	private static final long serialVersionUID = 6929934343495578299L;

	public Polynomial< ?, Point > interpolatedFunction;

	public InterpolatedPolynomial( final A a, final B b, double lambda )
	{
		super( a, b, lambda );

		// use the higher-order polynom to fit a function to interpolated points
		if ( a.degree() > b.degree() )
			interpolatedFunction = a.copy();
		else
			interpolatedFunction = b.copy();
	}

	@Override
	protected void interpolate( final Collection< Point > points ) throws NotEnoughDataPointsException, IllDefinedDataPointsException
	{
		final ArrayList< Point > interpolatedPoints = new ArrayList< Point >();

		for ( final Point p : points )
		{
			final double x = p.getW()[ 0 ];

			final double y1 = a.predict( x );
			final double y2 = b.predict( x );

			interpolatedPoints.add( new Point( new double[]{ x, l1 * y1 + lambda * y2 } ) );
		}

		interpolatedFunction.fitFunction( interpolatedPoints );
	}

	@Override
	public double predict( final double x ) { return interpolatedFunction.predict( x ); }

	@Override
	public double distanceTo( final Point point ) { return interpolatedFunction.distanceTo( point ); }

	@Override
	public int degree() { return interpolatedFunction.degree(); }

	@Override
	public double getCoefficient( final int j ) { return interpolatedFunction.getCoefficient( j ); }

	@Override
	public InterpolatedPolynomial< A, B > copy()
	{
		final InterpolatedPolynomial< A, B > copy = new InterpolatedPolynomial< A, B >( a.copy(), b.copy(), lambda );

		// it must be and AbstractFunction since it is A or B
		copy.interpolatedFunction = interpolatedFunction.copy();

		copy.setCost( getCost() );

		return copy;
	}

	public static void main( String[] args )
	{
		
	}
}
