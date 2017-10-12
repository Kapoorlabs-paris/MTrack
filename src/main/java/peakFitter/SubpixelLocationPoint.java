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
package peakFitter;

import java.util.ArrayList;

import javax.swing.JProgressBar;

import beadFinder.Beadfinder;
import beadObjects.Beadprop;
import labeledObjects.Indexedlength;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.PointSampleList;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.BenchmarkAlgorithm;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.algorithm.region.hypersphere.HyperSphere;
import net.imglib2.algorithm.region.hypersphere.HyperSphereCursor;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.view.Views;
import pointModels.GaussianPoints;
import psf_Tookit.GaussianFitParam;

public class SubpixelLocationPoint extends BenchmarkAlgorithm
implements OutputAlgorithm<ArrayList<GaussianFitParam>> {

	
	private final RandomAccessibleInterval<FloatType> source;
	
	private final int ndims;
	private static final String BASE_ERROR_MSG = "[SubpixelLocationPoint] ";
	private ArrayList<GaussianFitParam> Params;
	private final ArrayList<Beadprop> Allbeads;
	private final int framenumber;
	private final int thirdDimensionsize;
	final JProgressBar jpb;
	 double percent = 0;
	public SubpixelLocationPoint(RandomAccessibleInterval<FloatType> source, Beadfinder beadfinder,final JProgressBar jpb, final int framenumber, final int thirdDimensionsize) {

		beadfinder.process();
		Allbeads = beadfinder.getResult();
		this.framenumber = framenumber;
		this.thirdDimensionsize = thirdDimensionsize;
		this.source = source;
		this.jpb = jpb;
		this.ndims = source.numDimensions();

	}


	@Override
	public boolean checkInput() {
		
		return true;
	}


	@Override
	public boolean process() {
		
		Params = new ArrayList<GaussianFitParam>();
		
		try {
			
			percent = (Math.round(100 * (framenumber + 1) / (thirdDimensionsize)));
			for (int index = 0; index < Allbeads.size(); ++index){
			
				
				
				
				
				Localizable point = Allbeads.get(index).point;
				long radius = Allbeads.get(index).radius;
				
			    Params.add(Getfinalparam(point, radius));
				FitterUtils.SetProgressBarTime(jpb, percent, framenumber, thirdDimensionsize);

			}
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		return true;
	}


	@Override
	public ArrayList<GaussianFitParam> getResult() {
		
		return Params;
	}
	
	
	public GaussianFitParam Getfinalparam(final Localizable point, final long radius) throws Exception {

		PointSampleList<FloatType> datalist = gatherfullData(point, radius);

		final Cursor<FloatType> listcursor = datalist.localizingCursor();
		double[] sigma = new double[ndims];
		double[][] X = new double[(int) datalist.size()][ndims];
		double[] I = new double[(int) datalist.size()];
		int index = 0;
		while (listcursor.hasNext()) {
			listcursor.fwd();

			for (int d = 0; d < ndims; d++) {
				X[index][d] = listcursor.getDoublePosition(d);
			}

			I[index] = listcursor.get().getRealDouble();

			index++;
		}

		final double[] start_param = makeBestGuess(point, X, I);

		final double[] finalparam = start_param.clone();
		int maxiter = 1000;
		double lambda = 1e-3;
		double termepsilon = 1e-3;

		LevenbergMarquardtSolverLine.solve(X, finalparam, null, I, new GaussianPoints(), lambda,
				termepsilon, maxiter);

		// NaN protection: we prefer returning the crude estimate than NaN
		for (int j = 0; j < finalparam.length; j++) {
			if (Double.isNaN(finalparam[j]))
				finalparam[j] = start_param[j];
		}
		for (int j = 0; j < ndims; j++) {
			
			sigma[j] =  1.0 / Math.sqrt(finalparam[ndims + j + 1]);
			
		}
		GaussianFitParam guessparams = new GaussianFitParam(point, finalparam[0], sigma, finalparam[ 2 * ndims + 1]);

		return guessparams;

	}
	
	private final double[] makeBestGuess(final Localizable point, final double[][] X, final double[] I) {

		double[] start_param = new double[2 * ndims + 2];

		double[] sigma = new double[ndims];
		double I_sum = 0;
		double[] X_sum = new double[ndims];
		for (int j = 0; j < ndims; j++) {
			X_sum[j] = 0;
			for (int i = 0; i < X.length; i++) {
				X_sum[j] += X[i][j] * I[i];
			}
		}
		double max_I = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < X.length; i++) {
			I_sum += I[i];
			if (I[i] > max_I) {
				max_I = I[i];
			}

		}

		start_param[0] = max_I;

		for (int j = 0; j < ndims; j++) {
			start_param[j + 1] = X_sum[j] / I_sum;
		}

		for (int j = 0; j < ndims; j++) {
			double C = 0;
			double dx;
			for (int i = 0; i < X.length; i++) {
				dx = X[i][j] - start_param[j + 1];
				C += I[i] * dx * dx;
			}
			C /= I_sum;
			start_param[ndims + j + 1] = 1 / C;
			sigma[j] = 1 / C;
		}
		start_param[2 * ndims + 1] = 0;
		
		
		

		return start_param;
	}

	
	private PointSampleList<FloatType> gatherfullData(final Localizable point, final long radius) {
		final PointSampleList<FloatType> datalist = new PointSampleList<FloatType>(ndims);

		HyperSphere<FloatType> region = new HyperSphere<FloatType>(source, point, radius);

		HyperSphereCursor<FloatType> localcursorsphere = region.localizingCursor();
		// Gather data around the point
		boolean outofbounds = false;
		for (int d = 0; d < ndims; d++) {
			
			if (point.getDoublePosition(d) <= 0 || point.getDoublePosition(d)>= source.dimension(d)){
				
				outofbounds = true;
				break;
			}
		}
		
		while(localcursorsphere.hasNext()){
			
			localcursorsphere.fwd();
			for (int d = 0; d < ndims; d++) {

				if (localcursorsphere.getDoublePosition(d) < 0 || localcursorsphere.getDoublePosition(d) >= source.dimension(d)) {
					outofbounds = true;
					break;
				}
			}
			if (outofbounds) {
				outofbounds = false;
				continue;
			}
		

				Point newpoint = new Point(localcursorsphere);
				datalist.add(newpoint, localcursorsphere.get().copy());

			
			
			
		}
		
		

		
		return datalist;
      
      
	}
	
	
}
