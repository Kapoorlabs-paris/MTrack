package pointModels;

import LineModels.MTFitFunction;

public class GaussianPoints implements MTFitFunction {

	@Override
	public double val(double[] x, double[] a, double[] b) {
		return a[0] * E(x, a) + a[2*x.length +1];
	}

	@Override
	public double grad(double[] x, double[] a, double[] b, int k) {
		final int ndims = x.length;
		if (k == 0) {
			// With respect to A
			return E(x, a);

		} else if (k <= ndims) {
			// With respect to xi (mean)
			int dim = k - 1;
			return 2 * a[dim+ndims] * (x[dim] - a[dim+1]) * a[0] * E(x, a);

		} else if (k > ndims && k < 2*ndims + 1)  {
			// With respect to ai (sigma)
			int dim = k - ndims - 1;
			double di = x[dim] - a[dim+1];
			return - di * di * a[0] * E(x, a);
		}
	
        else if (k == 2* ndims + 1)

        return 1.0;	
		
		else{
			
			return 0;
		}
	}
	/*
	 * PRIVATE METHODS
	 */

	private static final double E(final double[] x, final double[] a) {
		final int ndims = x.length;
		double sum = 0;
		double di;
		for (int i = 0; i < x.length; i++) {
			di = x[i] - a[i+1];
			sum += a[i+ndims+1] * di * di;
		}
		return Math.exp(-sum);
	}
	
}
