package fit.polynomial;

import fit.Function;
import mpicbg.models.Point;

/**
 * @author Varun Kapoor and Stephan Preibisch
 */
public interface Polynomial < F extends Polynomial< F, P >, P extends Point > extends Function< F, P >
{
	/**
	 * @return The degree of the polynomial
	 */
	public int degree();

	/**
	 * @param j - the j'th coefficient of the polynomial ( c_0 + c_1*x + c_2*x*x + ... + c_N*x^N)
	 * @return
	 */
	public double getCoefficient( final int j );

	/**
	 * @param x
	 * @return - the corresponding y value
	 */
	public double predict( final double x );
}
