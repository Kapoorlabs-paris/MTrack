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
package LineModels;

public class GaussianSplinesecorder implements MTFitFunction {
	static double fcteps = 1.0E-30;
	@Override
	public double val(double[] x, double[] a, double[] b) {
		final int ndims = x.length;

		return a[2 * ndims + 2] * Etotal(x, a, b) + a[2 * ndims + 3];

	}

	@Override
	public double grad(double[] x, double[] a, double[] b, int k) {
		final int ndims = x.length;

		if (k < ndims) {

			return 2 * b[k] * (x[k] - a[k]) * a[2 * ndims + 2] * Estart(x, a, b);

		}

		else if (k >= ndims && k <= ndims + 1) {
			int dim = k - ndims;
			return 2 * b[dim] * (x[dim] - a[k]) * a[2 * ndims + 2] * Eend(x, a, b);

		} else if (k == 2 * ndims) {

			return a[2 * ndims + 2] * Eds(x, a, b);
		}

		else if (k == 2 * ndims + 1) {

			return a[2 * ndims + 2] * EdC(x, a, b);

		}

		else if (k == 2 * ndims + 2)

			return Etotal(x, a, b);

		else if (k == 2 * ndims + 3)
			return 1.0;

		else
			return 0;

	}

	/*
	 * PRIVATE METHODS
	 */

	/*
	 * @ Define a line analytically as a sum of gaussians, the parameters to be
	 * determined are the start and the end points of the line
	 * 
	 */

public static double numdiff(double[] x, double[] a, int dim, double[] b) {
	
	double [] newa = new double[a.length];
	final int ndims = x.length;
	double epsilon = 0.001;
	double f1 = 0;
	double f2 = 0;
	double diff = 0;
	
	
	do{
		
		
	for (int i = 0; i < a.length; ++i){
		newa[i] = a[i];
		if (i == dim)
		newa[i] = a[i] + epsilon;
	}
	f1 = (Etotal(x, newa, b)  - Etotal(x, a, b) )/ epsilon ;
	
	epsilon/=2;
	
	for (int i = 0; i < a.length; ++i){
		newa[i] = a[i];
		if (i == dim)
		newa[i] = a[i] + epsilon;
	}
	
	f2 =  (Etotal(x, newa, b)  - Etotal(x, a, b) )/ epsilon ;
	
	diff =  Math.abs(f2 - f1) ;
	
	f1 = f2;
	}while(diff> 1.0E-2);
	
	return a[2 * ndims + 2] *f2;
	
}

	private static final double Estart(final double[] x, final double[] a, final double[] b) {

		double sum = 0;
		double di;
		for (int i = 0; i < x.length; i++) {
			di = x[i] - a[i];
			sum += b[i] * di * di;
		}

		return Exponent.exp(-sum);

	}

	private static final double Eds(final double[] x, final double[] a, final double[] b) {

		double di;
		int count = 1;
		final int ndims = x.length;
		double[] minVal = new double[ndims];
		double[] maxVal = new double[ndims];
		
		double curvature = a[2 * ndims + 1];
		for (int i = 0; i < x.length; i++) {
			minVal[i] = a[i];
			maxVal[i] = a[ndims + i];
		}
		double slope = (maxVal[1] - minVal[1]) / (maxVal[0] - minVal[0] + fcteps) - curvature * (maxVal[0] + minVal[0]);

		double ds = Math.abs(a[2 * ndims]);

		
		double mplus2bxstart = slope + 2 * curvature* minVal[0] ;

		double[] dxvectorstart = { ds / Math.sqrt(1 + mplus2bxstart* mplus2bxstart), 
				mplus2bxstart* ds / Math.sqrt(1 + mplus2bxstart* mplus2bxstart) };

		
		
		

		double[] dxvectorderivstart = { 1 / Math.sqrt(1 + mplus2bxstart* mplus2bxstart), mplus2bxstart / Math.sqrt(1 + mplus2bxstart * mplus2bxstart) };
		
		
		
		

	
		
		double sumofgaussians = 0;
		
		while(true){
		double dsum = 0;
		double sum = 0;
		for (int i = 0; i < x.length; i++) {
			minVal[i] += dxvectorstart[i];
			di = x[i] - minVal[i];
			sum += b[i] * di * di;
			dsum += 2 * b[i] * di * dxvectorderivstart[i];
		}
		mplus2bxstart = slope + 2 * curvature* minVal[0];
		dxvectorderivstart[0] = 1 / Math.sqrt(1 + mplus2bxstart* mplus2bxstart);
		dxvectorderivstart[1] = mplus2bxstart / Math.sqrt(1 + mplus2bxstart * mplus2bxstart);
				
		sumofgaussians+= count * dsum * Exponent.exp(-sum);
		
		count++;
		
		if (minVal[0] >= maxVal[0] || minVal[1] >= maxVal[1] && slope >= 0)
			break;
		if (minVal[0] >= maxVal[0] || minVal[1] <= maxVal[1] && slope < 0)
			break;

	}
		
		return sumofgaussians;

	}

	private static final double EdC(final double[] x, final double[] a, final double[] b) {

		double di;
		int count = 1;
		final int ndims = x.length;
		double[] minVal = new double[ndims];
		double[] maxVal = new double[ndims];
		double sigmaX = 1/ Math.sqrt(b[0]);
		double sigmaY = 1/ Math.sqrt(b[1]);
		
		double curvature = a[2 * ndims + 1];
		for (int i = 0; i < x.length; i++) {
			minVal[i] = a[i];
			maxVal[i] = a[ndims + i];
		}
		double slope = (maxVal[1] - minVal[1]) / (maxVal[0] - minVal[0]+ fcteps) - curvature * (maxVal[0] + minVal[0]);
		double ds = Math.abs(a[2 * ndims]);
		double mplus2bxstart = slope + 2 * curvature* minVal[0];
		

		double[] dxvectorstart = { ds / Math.sqrt(1 + mplus2bxstart* mplus2bxstart), 
				mplus2bxstart* ds / Math.sqrt(1 + mplus2bxstart* mplus2bxstart) };

		
		double dxbydb = - ds * mplus2bxstart * (-(maxVal[0] + minVal[0]) + 2 * minVal[0]) / (Math.pow(1 + mplus2bxstart * mplus2bxstart, 3 / 2));
		
		double[] dxvectorCstart = {dxbydb, mplus2bxstart* dxbydb + (-(maxVal[0] + minVal[0]) + 2 * minVal[0]) * dxvectorstart[0]};
		


		double sumofgaussians = 0;
		double dsum = 0;
		double sum = 0;
		
		
		
		while(true){
			dsum = 0;
			sum = 0;
		
		
		
		for (int i = 0; i < x.length; i++) {
			minVal[i] += dxvectorstart[i];
			di = x[i] - minVal[i];
			sum += b[i] * di * di;
			dsum += 2 * b[i] * di * dxvectorCstart[i];
		}
		
			
		
		mplus2bxstart = slope + 2 * curvature* minVal[0];
		dxbydb = - ds * mplus2bxstart * (-(maxVal[0] + minVal[0]) + 2 * minVal[0]) / (Math.pow(1 + mplus2bxstart * mplus2bxstart, 3 / 2));
		dxvectorCstart[0] = dxbydb;
		dxvectorCstart[1] =  mplus2bxstart* dxbydb + (-(maxVal[0] + minVal[0]) + 2 * minVal[0]) * dxvectorstart[0];
		sumofgaussians+= count * dsum * Exponent.exp(-sum);
		
		count++;
		
		if (minVal[0] >= maxVal[0] || minVal[1] >= maxVal[1] && slope >= 0)
			break;
		if (minVal[0] >= maxVal[0] || minVal[1] <= maxVal[1] && slope < 0)
			break;
		}
	
		return sumofgaussians;

	}

	private static final double Eend(final double[] x, final double[] a, final double[] b) {

		double sum = 0;
		double di;
		int ndims = x.length;
		for (int i = 0; i < x.length; i++) {
			di = x[i] - a[i + ndims];
			sum += b[i] * di * di;
		}

		return Exponent.exp(-sum);

	}

	private static final double Etotal(final double[] x, final double[] a, final double[] b) {

		return Estart(x, a, b) + Esum(x, a, b) + Eend(x, a, b);

	}

	private static final double Esum(final double[] x, final double[] a, final double[] b) {

		final int ndims = x.length;
		double[] minVal = new double[ndims];
		double[] maxVal = new double[ndims];
		double sigmaX = 1/ Math.sqrt(b[0]);
		double sigmaY = 1/ Math.sqrt(b[1]);
		
		for (int i = 0; i < x.length; i++) {
			minVal[i] = a[i];
			maxVal[i] = a[ndims + i];
		}
		double sum = 0;
		double sumofgaussians = 0;
		double di;
		double curvature = a[2 * ndims + 1];
		double slope = (maxVal[1] - minVal[1]) / (maxVal[0] - minVal[0]+ fcteps) - curvature * (maxVal[0] + minVal[0]);

		double ds = Math.abs(a[2 * ndims]);

		while (true) {

			sum = 0;

			double dx = ds / Math.sqrt(1 + (slope+ 2 * curvature* minVal[0]) * (slope+ 2 * curvature* minVal[0]));
			double dy = (slope+ 2 * curvature* minVal[0]) * dx;
			double[] dxvector = { dx, dy };

			for (int i = 0; i < x.length; i++) {

				minVal[i] += dxvector[i];
				di = x[i] - minVal[i];
				sum += b[i] * di * di;
			}
			sumofgaussians += Exponent.exp(-sum);
			
			
			if (minVal[0] >= maxVal[0] || minVal[1] >= maxVal[1] && slope > 0)
				break;
			if (minVal[0] >= maxVal[0] || minVal[1] <= maxVal[1] && slope < 0)
				break;

		}

		return sumofgaussians;
	}

	public static double Distance(final double[] cordone, final double[] cordtwo) {

		double distance = 0;
		final double ndims = cordone.length;

		for (int d = 0; d < ndims; ++d) {

			distance += Math.pow((cordone[d] - cordtwo[d]), 2);

		}
		return Math.sqrt(distance);
	}

}
