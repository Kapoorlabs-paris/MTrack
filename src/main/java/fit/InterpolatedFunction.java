package fit;

import java.util.Collection;

import mpicbg.models.IllDefinedDataPointsException;
import mpicbg.models.NotEnoughDataPointsException;
import mpicbg.models.Point;

/**
 * Interpolation of two AbstractFunctions, inspired by Interpolated Models by Stephan Saalfeld
 * 
 * @author Stephan Preibisch
 */
public abstract class InterpolatedFunction< A extends AbstractFunction< A >, B extends AbstractFunction< B >, M extends InterpolatedFunction< A, B, M > > extends AbstractFunction2D< M >
{
	private static final long serialVersionUID = -8524786898599474286L;

	final protected A a;
	final protected B b;
	protected double lambda;
	protected double l1;

	public InterpolatedFunction( final A a, final B b, final double lambda )
	{
		this.a = a;
		this.b = b;
		this.lambda = lambda;
		l1 = 1.0 - lambda;
	}

	public A getA() { return a; }
	public B getB() { return b; }
	public double getLambda() { return lambda; }

	public void setLambda( final double lambda )
	{
		this.lambda = lambda;
		this.l1 = 1.0f - lambda;
	}

	@Override
	public int getMinNumPoints() { return Math.max( a.getMinNumPoints(), b.getMinNumPoints() ); }

	@Override
	public void set( final M m )
	{
		a.set( m.a );
		b.set( m.b );
		lambda = m.lambda;
		l1 = m.l1;
		cost = m.cost;
	}

	@Override
	public void fitFunction( final Collection< Point > points )
			throws NotEnoughDataPointsException, IllDefinedDataPointsException
	{
		a.fitFunction( points );
		b.fitFunction( points );

		interpolate( points );
	}

	protected abstract void interpolate( final Collection< Point > points )
			throws NotEnoughDataPointsException, IllDefinedDataPointsException;
}
