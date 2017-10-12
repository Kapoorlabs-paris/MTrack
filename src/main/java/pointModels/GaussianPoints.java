/*-
 * #%L
 * Microtubule tracker.
 * %%
 * Copyright (C) 2017 MTrack developers.
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
