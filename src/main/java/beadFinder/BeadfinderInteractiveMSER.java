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
package beadFinder;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import beadObjects.Beadprop;
import ij.gui.Roi;
import mserMethods.GetDelta;
import net.imglib2.Cursor;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.componenttree.mser.Mser;
import net.imglib2.algorithm.componenttree.mser.MserTree;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Util;
import net.imglib2.view.Views;

public class BeadfinderInteractiveMSER implements Beadfinder {

	private static final String BASE_ERROR_MSG = "[BeadfinderMSER] ";
	protected String errorMessage;
	private ArrayList<Beadprop> ProbBlobs;
	private final RandomAccessibleInterval<FloatType> source;

	private final RandomAccessibleInterval<FloatType> target;
	private final MserTree<UnsignedByteType> newtree;

	public boolean darktoBright = false;

	private final int ndims;
	private final int zplane;
	private int Roiindex;
	private Roi ellipseroi;
	

	public BeadfinderInteractiveMSER(final RandomAccessibleInterval<FloatType> source,
			final RandomAccessibleInterval<FloatType> target, MserTree<UnsignedByteType> newtree, final int zplane) {

		this.source = source;
		this.target = target;
		this.newtree = newtree;
		this.zplane = zplane;
		
		ndims = source.numDimensions();
	}

	@Override
	public ArrayList<Beadprop> getResult() {

		return ProbBlobs;
	}

	@Override
	public boolean checkInput() {

		if (source.numDimensions() > 3) {
			errorMessage = BASE_ERROR_MSG + " Can only operate on images upto 3D . Got " + source.numDimensions()
					+ "D.";
			return false;
		}

		return true;
	}

	@Override
	public boolean process() {

		ProbBlobs = new ArrayList<Beadprop>();

		final FloatType type = source.randomAccess().get().createVariable();

		ArrayList<double[]> ellipselist = new ArrayList<double[]>();
		ArrayList<double[]> meanandcovlist = new ArrayList<double[]>();

		final HashSet<Mser<UnsignedByteType>> rootset = newtree.roots();

		final Iterator<Mser<UnsignedByteType>> rootsetiterator = rootset.iterator();

		while (rootsetiterator.hasNext()) {

			Mser<UnsignedByteType> rootmser = rootsetiterator.next();

			if (rootmser.size() > 0) {

				final double[] meanandcov = { rootmser.mean()[0], rootmser.mean()[1], rootmser.cov()[0],
						rootmser.cov()[1], rootmser.cov()[2] };
				meanandcovlist.add(meanandcov);
				ellipselist.add(meanandcov);

			}
		}

		// We do this so the ROI remains attached the the same label and is not
		// changed if the program is run again
		SortListbyproperty.sortpointList(ellipselist);
		int count = 0;
		for (int index = 0; index < ellipselist.size(); ++index) {

			final ImgFactory<FloatType> factory = Util.getArrayOrCellImgFactory(source, type);

			RandomAccessibleInterval<FloatType> ActualRoiimg = factory.create(source, type);

			final double[] mean = { ellipselist.get(index)[0], ellipselist.get(index)[1] };
			final double[] covar = { ellipselist.get(index)[2], ellipselist.get(index)[3], ellipselist.get(index)[4] };
			ellipseroi = GetDelta.createEllipse(mean, covar, 3);
			final double perimeter = ellipseroi.getLength();

			Roiindex = count;
			count++;
			// ellipseroi.setStrokeColor(Color.green);

			Cursor<FloatType> Actualsourcecursor = Views.iterable(source).localizingCursor();
			RandomAccess<FloatType> Actualranac = ActualRoiimg.randomAccess();
			while (Actualsourcecursor.hasNext()) {

				Actualsourcecursor.fwd();

				final int x = Actualsourcecursor.getIntPosition(0);
				final int y = Actualsourcecursor.getIntPosition(1);
				Actualranac.setPosition(Actualsourcecursor);
				if (ellipseroi.contains(x, y)) {

					Actualranac.get().set(Actualsourcecursor.get());

				}

			}

			
			final long[] center = GetCOM.getProps(source, target, ellipseroi, zplane);

			Rectangle rect = ellipseroi.getBounds();
			final long radius = (long) (0.5 * Math.sqrt(rect.height * rect.height + rect.width * rect.width));
			
			
			Beadprop currentbead = new Beadprop(zplane, new Point(center), ellipseroi, radius);

			ProbBlobs.add(currentbead);

		}

		return true;
	}

	public Roi getRoi() {

		return ellipseroi;

	}

	/**
	 * Returns the slope and the intercept of the line passing through the major
	 * axis of the ellipse
	 * 
	 * 
	 * @param mean
	 *            (x,y) components of mean vector
	 * @param cov
	 *            (xx, xy, yy) components of covariance matrix
	 * @return slope and intercept of the line along the major axis
	 */
	public double[] LargestEigenvector(final double[] mean, final double[] cov) {

		// For inifinite slope lines support is provided
		final double a = cov[0];
		final double b = cov[1];
		final double c = cov[2];
		final double d = Math.sqrt(a * a + 4 * b * b - 2 * a * c + c * c);
		final double[] eigenvector1 = { 2 * b, c - a + d };
		double[] LargerVec = new double[eigenvector1.length + 1];

		LargerVec = eigenvector1;

		final double slope = LargerVec[1] / (LargerVec[0]);
		final double intercept = mean[1] - mean[0] * slope;

		if (Math.abs(slope) != Double.POSITIVE_INFINITY) {
			double[] pair = { slope, intercept };
			return pair;

		} else
			return null;

	}

	/**
	 * Returns the largest eigenvalue of the ellipse
	 * 
	 * 
	 * @param mean
	 *            (x,y) components of mean vector
	 * @param cov
	 *            (xx, xy, yy) components of covariance matrix
	 * @return slope and intercept of the line along the major axis
	 */
	public long LargerEigenvalue(final double[] mean, final double[] cov) {

		// For inifinite slope lines support is provided
		final double a = cov[0];
		final double b = cov[1];
		final double c = cov[2];
		final double d = Math.sqrt(a * a + 4 * b * b - 2 * a * c + c * c);

		final double largereigenvalue = (a + c + d) / 2;

		return (long) largereigenvalue;

	}

	@Override
	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	

}
