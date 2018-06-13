package utility;

import java.awt.Rectangle;
import java.util.ArrayList;

import ij.IJ;
import ij.gui.Roi;
import mpicbg.imglib.util.Util;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.NativeType;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class Slicer {

	public static RandomAccessibleInterval<FloatType> getCurrentViewLarge(
			RandomAccessibleInterval<FloatType> originalimg, int thirdDimension) {

		final FloatType type = originalimg.randomAccess().get().createVariable();
		long[] dim = { originalimg.dimension(0), originalimg.dimension(1), originalimg.dimension(2) };
		final ImgFactory<FloatType> factory = net.imglib2.util.Util.getArrayOrCellImgFactory(originalimg, type);
		RandomAccessibleInterval<FloatType> totalimg = factory.create(dim, type);

		totalimg = Views.hyperSlice(originalimg, 2, thirdDimension - 1);

		return totalimg;

	}

	public static  < T extends NumericType< T > & NativeType< T > > RandomAccessibleInterval<T> getCurrentView(RandomAccessibleInterval<T> originalimg,
			int thirdDimension, int thirdDimensionSize, int fourthDimension, int fourthDimensionSize) {

		final T type = originalimg.randomAccess().get().createVariable();
		long[] dim = { originalimg.dimension(0), originalimg.dimension(1) };
		final ImgFactory<T> factory = net.imglib2.util.Util.getArrayOrCellImgFactory(originalimg, type);
		RandomAccessibleInterval<T> totalimg = factory.create(dim, type);

		if (thirdDimensionSize == 0) {

			totalimg = originalimg;
		}

		if (thirdDimensionSize > 0 && fourthDimensionSize == 0) {

			totalimg = Views.hyperSlice(originalimg, 2, thirdDimension - 1);

		}

		if (fourthDimensionSize > 0) {

			RandomAccessibleInterval<T> pretotalimg = Views.hyperSlice(originalimg, 2, thirdDimension - 1);

			totalimg = Views.hyperSlice(pretotalimg, 2, fourthDimension - 1);
		}

		return totalimg;

	}

	public static RandomAccessibleInterval<BitType> getCurrentViewBit(RandomAccessibleInterval<BitType> originalimg,
			int thirdDimension, int thirdDimensionSize, int fourthDimension, int fourthDimensionSize) {

		final BitType type = originalimg.randomAccess().get().createVariable();
		long[] dim = { originalimg.dimension(0), originalimg.dimension(1) };
		final ImgFactory<BitType> factory = net.imglib2.util.Util.getArrayOrCellImgFactory(originalimg, type);
		RandomAccessibleInterval<BitType> totalimg = factory.create(dim, type);

		if (thirdDimensionSize == 0) {

			totalimg = originalimg;
		}
		if (thirdDimensionSize > 0 && fourthDimensionSize == 0) {

			totalimg = Views.hyperSlice(originalimg, 2, thirdDimension - 1);
		}

		if (fourthDimensionSize > 0) {

			RandomAccessibleInterval<BitType> pretotalimg = Views.hyperSlice(originalimg, 2, thirdDimension - 1);

			totalimg = Views.hyperSlice(pretotalimg, 2, fourthDimension - 1);
		}

		return totalimg;

	}

	public static RandomAccessibleInterval<IntType> getCurrentViewInt(RandomAccessibleInterval<IntType> originalimg,
			int thirdDimension, int thirdDimensionSize, int fourthDimension, int fourthDimensionSize) {

		final IntType type = originalimg.randomAccess().get().createVariable();
		long[] dim = { originalimg.dimension(0), originalimg.dimension(1) };
		final ImgFactory<IntType> factory = net.imglib2.util.Util.getArrayOrCellImgFactory(originalimg, type);
		RandomAccessibleInterval<IntType> totalimg = factory.create(dim, type);

		if (thirdDimensionSize == 0) {

			totalimg = originalimg;
		}

		if (thirdDimensionSize > 0 && fourthDimensionSize == 0) {

			totalimg = Views.hyperSlice(originalimg, 2, thirdDimension - 1);

		}

		if (fourthDimensionSize > 0) {

			RandomAccessibleInterval<IntType> pretotalimg = Views.hyperSlice(originalimg, 2, thirdDimension - 1);

			totalimg = Views.hyperSlice(pretotalimg, 2, fourthDimension - 1);
		}

		return totalimg;

	}

	public static RandomAccessibleInterval<BitType> getCurrentViewBitRectangle(
			RandomAccessibleInterval<BitType> originalimg, int thirdDimension, int thirdDimensionSize,
			int fourthDimension, int fourthDimensionSize, Rectangle rect) {

		final BitType type = originalimg.randomAccess().get().createVariable();
		long[] dim = { originalimg.dimension(0), originalimg.dimension(1) };
		final ImgFactory<BitType> factory = net.imglib2.util.Util.getArrayOrCellImgFactory(originalimg, type);
		RandomAccessibleInterval<BitType> totalimg = factory.create(dim, type);
		RandomAccessibleInterval<BitType> totalimgout = factory.create(dim, type);
		long maxY = 0, minY = 0, maxX = 0, minX = 0;
		if (rect != null) {

			maxY = (long) rect.getMaxY();
			maxX = (long) rect.getMaxX();
			minY = (long) rect.getMinY();
			minX = (long) rect.getMinX();
		}
		if (thirdDimensionSize == 0) {

			totalimg = originalimg;
		}

		if (thirdDimensionSize > 0 && fourthDimensionSize == 0) {

			totalimg = Views.hyperSlice(originalimg, 2, thirdDimension - 1);

		}

		if (fourthDimensionSize > 0) {

			RandomAccessibleInterval<BitType> pretotalimg = Views.hyperSlice(originalimg, 2, thirdDimension - 1);

			totalimg = Views.hyperSlice(pretotalimg, 2, fourthDimension - 1);
		}

		RandomAccessibleInterval<BitType> view;
		if (rect != null)

			view = Views.interval(totalimg, new long[] { minX, minY }, new long[] { maxX, maxY });
		else
			view = totalimg;
		RandomAccess<BitType> ranout = totalimgout.randomAccess();
		Cursor<BitType> viewcursor = Views.iterable(view).localizingCursor();

		while (viewcursor.hasNext()) {

			viewcursor.next();
			ranout.setPosition(viewcursor);
			ranout.get().set(viewcursor.get());

		}

		return totalimgout;

	}

	public static RandomAccessibleInterval<IntType> getCurrentViewIntRectangle(
			RandomAccessibleInterval<IntType> originalimg, int thirdDimension, int thirdDimensionSize,
			int fourthDimension, int fourthDimensionSize, Rectangle rect) {

		final IntType type = originalimg.randomAccess().get().createVariable();
		long[] dim = { originalimg.dimension(0), originalimg.dimension(1) };
		final ImgFactory<IntType> factory = net.imglib2.util.Util.getArrayOrCellImgFactory(originalimg, type);
		RandomAccessibleInterval<IntType> totalimg = factory.create(dim, type);
		RandomAccessibleInterval<IntType> totalimgout = factory.create(dim, type);
		long maxY = 0, minY = 0, maxX = 0, minX = 0;
		if (rect != null) {

			maxY = (long) rect.getMaxY();
			maxX = (long) rect.getMaxX();
			minY = (long) rect.getMinY();
			minX = (long) rect.getMinX();
		}

		if (thirdDimensionSize == 0) {

			totalimg = originalimg;
		}

		if (thirdDimensionSize > 0 && fourthDimensionSize == 0) {

			totalimg = Views.hyperSlice(originalimg, 2, thirdDimension - 1);

		}

		if (fourthDimensionSize > 0) {

			RandomAccessibleInterval<IntType> pretotalimg = Views.hyperSlice(originalimg, 2, thirdDimension - 1);

			totalimg = Views.hyperSlice(pretotalimg, 2, fourthDimension - 1);
		}
		RandomAccessibleInterval<IntType> view;
		if (rect != null)

			view = Views.interval(totalimg, new long[] { minX, minY }, new long[] { maxX, maxY });
		else
			view = totalimg;
		RandomAccess<IntType> ranout = totalimgout.randomAccess();
		Cursor<IntType> viewcursor = Views.iterable(view).localizingCursor();

		while (viewcursor.hasNext()) {

			viewcursor.next();
			ranout.setPosition(viewcursor);
			ranout.get().set(viewcursor.get());

		}

		return totalimgout;

	}

	

	public static float computeValueFromScrollbarPosition(final int scrollbarPosition, final float min, final float max,
			final int scrollbarSize) {
		return min + (scrollbarPosition / (float) scrollbarSize) * (max - min);
	}

	public static int computeScrollbarPositionFromValue(final float sigma, final float min, final float max,
			final int scrollbarSize) {
		return Util.round(((sigma - min) / (max - min)) * scrollbarSize);
	}

}
