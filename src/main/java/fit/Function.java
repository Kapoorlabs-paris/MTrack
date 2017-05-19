package fit;

import java.util.Collection;

import mpicbg.models.IllDefinedDataPointsException;
import mpicbg.models.NotEnoughDataPointsException;
import mpicbg.models.Point;

/**
 * Interface for a {@link Function} that can be fit to {@link Point}s
 * 
 * @author Stephan Preibisch
 */
public interface Function< F extends Function< F, P >, P extends Point >
{
	/**
	 * @return - how many points are at least necessary to fit the function
	 */
	public int getMinNumPoints();

	/**
	 * Fits this Function to the set of {@link Point}s.

	 * @param points - {@link Collection} of {@link Point}s
	 * @throws NotEnoughDataPointsException - thrown if not enough {@link Point}s are in the {@link Collection}
	 */
	public void fitFunction( final Collection<P> points ) throws NotEnoughDataPointsException, IllDefinedDataPointsException;
	
	/**
	 * Computes the minimal distance of a {@link Point} to this function
	 *  
	 * @param point - the {@link Point}
	 * @return - distance to the {@link Function}
	 */
	public double distanceTo( final P point );

	/**
	 * @return - a copy of the function object
	 */
	public F copy();
}
