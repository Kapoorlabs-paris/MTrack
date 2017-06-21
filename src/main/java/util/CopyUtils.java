package util;

import java.awt.Rectangle;

import ij.IJ;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.gauss3.Gauss3;
import net.imglib2.algorithm.stats.Normalize;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Util;
import net.imglib2.view.Views;
import preProcessing.FlatFieldCorrection;
import preProcessing.MedianFilter2D;

public class CopyUtils {

	public static RandomAccessibleInterval<FloatType> Preprocess(RandomAccessibleInterval<FloatType> originalimg) {


		final FlatFieldCorrection flatfilter = new FlatFieldCorrection(originalimg, 1);
		flatfilter.process();
		RandomAccessibleInterval<FloatType> ProgramPreprocessedimg = flatfilter.getResult();
		return ProgramPreprocessedimg;
				
			}
	public static Img<FloatType> copyImage(final RandomAccessibleInterval<FloatType> input) {
		// create a new Image with the same dimensions but the other imgFactory
		// note that the input provides the size for the new image by
		// implementing the Interval interface
		Img<FloatType> output = new ArrayImgFactory<FloatType>().create(input, Views.iterable(input).firstElement());

		// create a cursor that automatically localizes itself on every move
		Cursor<FloatType> cursorInput = Views.iterable(input).localizingCursor();
		RandomAccess<FloatType> randomAccess = output.randomAccess();

		// iterate over the input cursor
		while (cursorInput.hasNext()) {
			// move input cursor forward
			cursorInput.fwd();

			// set the output cursor to the position of the input cursor
			randomAccess.setPosition(cursorInput);

			// set the value of this pixel of the output image, every Type
			// supports T.set( T type )
			randomAccess.get().set(cursorInput.get());
		}

		// return the copy
		return output;
	}
	/**
	 * Generic, type-agnostic method to create an identical copy of an Img
	 *
	 * @param currentPreprocessedimg2
	 *            - the Img to copy
	 * @return - the copy of the Img
	 */
	public static Img<FloatType> copytoByteFloatImage(final RandomAccessibleInterval<FloatType> input) {
		// create a new Image with the same properties
		// note that the input provides the size for the new image as it
		// implements
		// the Interval interface
		final RandomAccessibleInterval<FloatType> inputcopy = copyImage(input);
		Normalize.normalize(Views.iterable(inputcopy), new FloatType(0), new FloatType(255));
		final FloatType type = new FloatType();
		final ImgFactory<FloatType> factory = net.imglib2.util.Util.getArrayOrCellImgFactory(input, type);
		final Img<FloatType> output = factory.create(input, type);
		// create a cursor for both images
		RandomAccess<FloatType> ranac = inputcopy.randomAccess();
		Cursor<FloatType> cursorOutput = output.cursor();

		// iterate over the input
		while (cursorOutput.hasNext()) {
			// move both cursors forward by one pixel
			cursorOutput.fwd();

			int x = cursorOutput.getIntPosition(0);
			int y = cursorOutput.getIntPosition(1);


				ranac.setPosition(cursorOutput);

				// set the value of this pixel of the output image to the same
				// as
				// the input,
				// every Type supports T.set( T type )
				cursorOutput.get().set((int) ranac.get().get());
		}

		// return the copy
		return output;
	}
	
	/**
	 * Generic, type-agnostic method to create an identical copy of an Img
	 *
	 * @param currentPreprocessedimg2
	 *            - the Img to copy
	 * @return - the copy of the Img
	 */
	public static Img<UnsignedByteType> copytoByteImage(final RandomAccessibleInterval<FloatType> input, final Rectangle standardRectangle) {
		// create a new Image with the same properties
		// note that the input provides the size for the new image as it
		// implements
		// the Interval interface
		final RandomAccessibleInterval<FloatType> inputcopy = copyImage(input);
		Normalize.normalize(Views.iterable(inputcopy), new FloatType(0), new FloatType(255));
		final UnsignedByteType type = new UnsignedByteType();
		final ImgFactory<UnsignedByteType> factory = net.imglib2.util.Util.getArrayOrCellImgFactory(input, type);
		final Img<UnsignedByteType> output = factory.create(input, type);
		// create a cursor for both images
		RandomAccess<FloatType> ranac = inputcopy.randomAccess();
		Cursor<UnsignedByteType> cursorOutput = output.cursor();

		// iterate over the input
		while (cursorOutput.hasNext()) {
			// move both cursors forward by one pixel
			cursorOutput.fwd();

			int x = cursorOutput.getIntPosition(0);
			int y = cursorOutput.getIntPosition(1);

			if (standardRectangle.contains(x, y)) {

				ranac.setPosition(cursorOutput);

				// set the value of this pixel of the output image to the same
				// as
				// the input,
				// every Type supports T.set( T type )
				cursorOutput.get().set((int) Math.round(ranac.get().getRealFloat()));
			}
		}

		// return the copy
		return output;
	}
	
	
	
	public static Img<UnsignedByteType> copytoByteImage(final RandomAccessibleInterval<FloatType> input, final FinalInterval standardInterval) {
		// create a new Image with the same properties
		// note that the input provides the size for the new image as it
		// implements
		// the Interval interface
		RandomAccessibleInterval<FloatType> inputcopy = copyImage(input);
		Normalize.normalize(Views.iterable(inputcopy), new FloatType(0), new FloatType(255));
		
		inputcopy = Views.interval(input, standardInterval);
		final UnsignedByteType type = new UnsignedByteType();
		final ImgFactory<UnsignedByteType> factory = net.imglib2.util.Util.getArrayOrCellImgFactory(input, type);
		final Img<UnsignedByteType> output = factory.create(input, type);
		// create a cursor for both images
		RandomAccess<FloatType> ranac = inputcopy.randomAccess();
		
		
		Cursor<UnsignedByteType> cursorOutput = output.cursor();

		// iterate over the input
		while (cursorOutput.hasNext()) {
			// move both cursors forward by one pixel
			cursorOutput.fwd();

			int x = cursorOutput.getIntPosition(0);
			int y = cursorOutput.getIntPosition(1);


				ranac.setPosition(cursorOutput);

				// set the value of this pixel of the output image to the same
				// as
				// the input,
				// every Type supports T.set( T type )
				cursorOutput.get().set((int) ranac.get().get());
		}

		// return the copy
		return output;
	}
	
	public static Img<UnsignedByteType> copytoByteImage(final RandomAccessibleInterval<FloatType> input, final RandomAccessibleInterval<IntType> intimg,
			final Rectangle standardRectangle, int label) {
		// create a new Image with the same properties
		// note that the input provides the size for the new image as it
		// implements
		// the Interval interface
		RandomAccess<IntType> intran = intimg.randomAccess();
		final RandomAccessibleInterval<FloatType> inputcopy = copyImage(input);
		Normalize.normalize(Views.iterable(inputcopy), new FloatType(0), new FloatType(255));
		final UnsignedByteType type = new UnsignedByteType();
		final ImgFactory<UnsignedByteType> factory = net.imglib2.util.Util.getArrayOrCellImgFactory(input, type);
		final Img<UnsignedByteType> output = factory.create(input, type);
		// create a cursor for both images
		RandomAccess<FloatType> ranac = inputcopy.randomAccess();
		Cursor<UnsignedByteType> cursorOutput = output.cursor();

		// iterate over the input
		while (cursorOutput.hasNext()) {
			// move both cursors forward by one pixel
			cursorOutput.fwd();

			int x = cursorOutput.getIntPosition(0);
			int y = cursorOutput.getIntPosition(1);

			if (standardRectangle.contains(x, y)) {

				intran.setPosition(cursorOutput);
				if(intran.get().get() == label){
				ranac.setPosition(cursorOutput);

				// set the value of this pixel of the output image to the same
				// asintfra
				// the input,
				// every Type supports T.set( T type )
				cursorOutput.get().set((int) ranac.get().get());
				}
			}
		}

		// return the copy
		return output;
	}
	
	
	
	public static  double[] Transformback(double[] location, double[] size, double[] min,
			double[] max) {

		int n = location.length;

		double[] delta = new double[n];

		final double[] realpos = new double[n];

		for (int d = 0; d < n; ++d){
			
			delta[d] = (max[d] - min[d]) / size[d];
		    
			realpos[d] = (location[d] - min[d]) / delta[d];
		}
		return realpos;

	}
	
	
	public static RandomAccessibleInterval<FloatType> extractImage(final RandomAccessibleInterval<FloatType> intervalView, final FinalInterval interval) {

		return intervalView;
	}
	
	
	public static RandomAccessibleInterval<FloatType> oldextractImage(final RandomAccessibleInterval<FloatType> intervalView, final FinalInterval interval) {

		final FloatType type = intervalView.randomAccess().get().createVariable();
		final ImgFactory<FloatType> factory = net.imglib2.util.Util.getArrayOrCellImgFactory(intervalView, type);
		RandomAccessibleInterval<FloatType> totalimg = factory.create(intervalView, type);
		final RandomAccessibleInterval<FloatType> img = Views.interval(intervalView, interval);

		double[] newmin = Transformback(new double[] { img.min(0), img.min(1) },
				new double[] { totalimg.dimension(0), totalimg.dimension(1) }, new double[] { img.min(0), img.min(1) },
				new double[] { img.max(0), img.max(1) });

		double[] newmax = Transformback(new double[] { img.max(0), img.max(1) },
				new double[] { totalimg.dimension(0), totalimg.dimension(1) },
				new double[] { totalimg.min(0), totalimg.min(1) }, new double[] { totalimg.max(0), totalimg.max(1) });
		long[] newminlong = new long[] { Math.round(newmin[0]), Math.round(newmin[1]) };
		long[] newmaxlong = new long[] { Math.round(newmax[0]), Math.round(newmax[1]) };

		RandomAccessibleInterval<FloatType> outimg = factory.create(new FinalInterval(newminlong, newmaxlong), type);
		RandomAccess<FloatType> ranac = outimg.randomAccess();
		final Cursor<FloatType> cursor = Views.iterable(img).localizingCursor();

		while (cursor.hasNext()) {

			cursor.fwd();

			double[] newlocation = Transformback(
					new double[] { cursor.getDoublePosition(0), cursor.getDoublePosition(1) },
					new double[] { totalimg.dimension(0), totalimg.dimension(1) },
					new double[] { totalimg.min(0), totalimg.min(1) },
					new double[] { totalimg.max(0), totalimg.max(1) });
			long[] newlocationlong = new long[] { Math.round(newlocation[0]), Math.round(newlocation[1]) };
			ranac.setPosition(newlocationlong);
			ranac.get().set(cursor.get());

		}

		return intervalView;
	}
	
	
	public static RandomAccessibleInterval<IntType> extractIntImage(final RandomAccessibleInterval<IntType> intervalView, final FinalInterval interval) {

		return intervalView;
	}
	public static RandomAccessibleInterval<IntType> oldextractIntImage(final RandomAccessibleInterval<IntType> intervalView, final FinalInterval interval) {
		
				final IntType type = intervalView.randomAccess().get().createVariable();
				final ImgFactory<IntType> factory = net.imglib2.util.Util.getArrayOrCellImgFactory(intervalView, type);
				RandomAccessibleInterval<IntType> totalimg = factory.create(intervalView, type);
				final RandomAccessibleInterval<IntType> img = Views.interval(intervalView, interval);

				double[] newmin = Transformback(new double[] { img.min(0), img.min(1) },
						new double[] { totalimg.dimension(0), totalimg.dimension(1) }, new double[] { img.min(0), img.min(1) },
						new double[] { img.max(0), img.max(1) });

				double[] newmax = Transformback(new double[] { img.max(0), img.max(1) },
						new double[] { totalimg.dimension(0), totalimg.dimension(1) },
						new double[] { totalimg.min(0), totalimg.min(1) }, new double[] { totalimg.max(0), totalimg.max(1) });
				long[] newminlong = new long[] { Math.round(newmin[0]), Math.round(newmin[1]) };
				long[] newmaxlong = new long[] { Math.round(newmax[0]), Math.round(newmax[1]) };

				RandomAccessibleInterval<IntType> outimg = factory.create(new FinalInterval(newminlong, newmaxlong), type);
				RandomAccess<IntType> ranac = outimg.randomAccess();
				final Cursor<IntType> cursor = Views.iterable(img).localizingCursor();

				while (cursor.hasNext()) {

					cursor.fwd();

					double[] newlocation = Transformback(
							new double[] { cursor.getDoublePosition(0), cursor.getDoublePosition(1) },
							new double[] { totalimg.dimension(0), totalimg.dimension(1) },
							new double[] { totalimg.min(0), totalimg.min(1) },
							new double[] { totalimg.max(0), totalimg.max(1) });
					long[] newlocationlong = new long[] { Math.round(newlocation[0]), Math.round(newlocation[1]) };
					ranac.setPosition(newlocationlong);
					ranac.get().set(cursor.get());

				}
		
				return intervalView;
			}
			
	public static  RandomAccessibleInterval<FloatType> getCurrentPreView(RandomAccessibleInterval<FloatType> originalPreprocessedimg,int thirdDimension, int thirdDimensionSize) {

		final FloatType type = originalPreprocessedimg.randomAccess().get().createVariable();
		long[] dim = { originalPreprocessedimg.dimension(0), originalPreprocessedimg.dimension(1) };
		final ImgFactory<FloatType> factory = net.imglib2.util.Util.getArrayOrCellImgFactory(originalPreprocessedimg,
				type);
		RandomAccessibleInterval<FloatType> totalimg = factory.create(dim, type);

		if (thirdDimensionSize == 0) {

			totalimg = originalPreprocessedimg;
		}

		if (thirdDimensionSize > 0) {

			totalimg = Views.hyperSlice(originalPreprocessedimg, 2, thirdDimension - 1);

		}

		return totalimg;

	}
	
	public static RandomAccessibleInterval<FloatType> getCurrentView(RandomAccessibleInterval<FloatType> originalimg, int thirdDimension, int thirdDimensionSize) {

		final FloatType type = originalimg.randomAccess().get().createVariable();
		long[] dim = { originalimg.dimension(0), originalimg.dimension(1) };
		final ImgFactory<FloatType> factory = net.imglib2.util.Util.getArrayOrCellImgFactory(originalimg, type);
		RandomAccessibleInterval<FloatType> totalimg = factory.create(dim, type);

		if (thirdDimensionSize == 0) {

			totalimg = originalimg;
		}

		if (thirdDimensionSize > 0) {

			totalimg = Views.hyperSlice(originalimg, 2, thirdDimension - 1);

		}

		return totalimg;

	}
	
	
}
