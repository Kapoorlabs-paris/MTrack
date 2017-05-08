package mserMethods;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import ij.gui.EllipseRoi;
import labeledObjects.CommonOutputHF;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.componenttree.mser.Mser;
import net.imglib2.algorithm.componenttree.mser.MserTree;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Util;
import net.imglib2.view.Views;
import peakFitter.SortListbyproperty;

public class MserHF {

	public static ArrayList<CommonOutputHF> output(final RandomAccessibleInterval<FloatType> source,
			final RandomAccessibleInterval<FloatType> Preprocessedsource, MserTree<UnsignedByteType> newtree,
			final int minlength, final int framenumber, int Roiindex) {

		ArrayList<CommonOutputHF> output = new ArrayList<CommonOutputHF>();

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

			final ImgFactory<FloatType> factory = Util.getArrayOrCellImgFactory(Preprocessedsource, type);
			RandomAccessibleInterval<FloatType> Roiimg = factory.create(Preprocessedsource, type);
			RandomAccessibleInterval<FloatType> ActualRoiimg = factory.create(source, type);

			final double[] mean = { ellipselist.get(index)[0], ellipselist.get(index)[1] };
			final double[] covar = { ellipselist.get(index)[2], ellipselist.get(index)[3], ellipselist.get(index)[4] };
			EllipseRoi ellipseroi = GetDelta.createEllipse(mean, covar, 3);

			final double perimeter = ellipseroi.getLength();
			final double smalleigenvalue = SmallerEigenvalue(mean, covar);
		//	if (perimeter > 2 * Math.PI * minlength && smalleigenvalue < 30) {

				Roiindex = count;
				count++;
				ellipseroi.setStrokeColor(Color.green);

				Cursor<FloatType> sourcecursor = Views.iterable(Preprocessedsource).localizingCursor();
				RandomAccess<FloatType> ranac = Roiimg.randomAccess();
				while (sourcecursor.hasNext()) {

					sourcecursor.fwd();

					final int x = sourcecursor.getIntPosition(0);
					final int y = sourcecursor.getIntPosition(1);
					ranac.setPosition(sourcecursor);
					if (ellipseroi.contains(x, y)) {

						ranac.get().set(sourcecursor.get());

					}

				}

				FinalInterval interval = util.Boundingboxes.CurrentroiInterval(Roiimg, ellipseroi);

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

				CommonOutputHF currentOutput = new CommonOutputHF(framenumber, Roiindex, Roiimg, ActualRoiimg,
						interval);

				output.add(currentOutput);

		//	}

		}

		return output;
	}

	/**
	 * Returns the smallest eigenvalue of the ellipse
	 * 
	 * 
	 * @param mean
	 *            (x,y) components of mean vector
	 * @param cov
	 *            (xx, xy, yy) components of covariance matrix
	 * @return slope and intercept of the line along the major axis
	 */
	public static double SmallerEigenvalue(final double[] mean, final double[] cov) {

		// For inifinite slope lines support is provided
		final double a = cov[0];
		final double b = cov[1];
		final double c = cov[2];
		final double d = Math.sqrt(a * a + 4 * b * b - 2 * a * c + c * c);

		final double smalleigenvalue = (a + c - d) / 2;

		return smalleigenvalue;

	}

}
