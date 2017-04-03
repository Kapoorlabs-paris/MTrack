package LineModels;

public interface MTFitFunction {
	
        // Defines the Microtubule fit function as a line
		
		/**
		 * Evaluate this function at point <code>x</code>. The function is
		 * otherwise defined over an array of parameters <code>a</code>, that
		 * is the target of the fitting procedure.
		 * @param x  the multidimensional to evaluate the fonction at
		 * @param a  the set of parameters that defines the function
		 * @param b the set of parameters which are fixed
		 * @return  a double value, the function evaluated at <code>x</code>
		 *  
		 */
		public double val(double[] x, double[] a, double [] b);
		

		/**
		 * Evaluate the gradient value of the function, taken with respect to the 
		 * <code>ak</code><sup>th</sup> parameter, evaluated at point <code>x</code>.
		 * @param x  the point to evaluate the gradient at
		 * @param a  the set of parameters that defines the function
		 *  @param b the set of parameters which are fixed
		 * @param ak the index of the parameter to compute the gradient 
		 * @return the kth component of the gradient <code>df(x,a)/da_k</code>
		 * @see #val(double[], double[])
		 */
		public double grad(double[] x, double[] a, double[] b, int ak);
	}


