package fit;

import java.util.ArrayList;
import java.util.Collection;

import mpicbg.models.AbstractModel;
import mpicbg.models.IllDefinedDataPointsException;
import mpicbg.models.NotEnoughDataPointsException;
import mpicbg.models.Point;
import mpicbg.models.PointMatch;

/**
 * @author Stephan Preibisch (stephan.preibisch@gmx.de) & Timothee Lionnet
 */
public abstract class AbstractFunction< M extends AbstractFunction< M > > extends AbstractModel< M > implements Function< M, Point >
{
	private static final long serialVersionUID = 26767772990350414L;

	@Override
	public int getMinNumMatches() { return getMinNumPoints(); }

	@Deprecated
	@Override
	public <P extends PointMatch> void fit( final Collection< P > matches ) throws NotEnoughDataPointsException, IllDefinedDataPointsException
	{
		final ArrayList<Point> list = new ArrayList<Point>();

		for ( final P pm : matches )
			list.add( pm.getP1() );

		fitFunction( list );
	}

	@Override
	public double[] apply( final double[] location ) { return null; }

	@Override
	public void applyInPlace( final double[] location ) {}
}
