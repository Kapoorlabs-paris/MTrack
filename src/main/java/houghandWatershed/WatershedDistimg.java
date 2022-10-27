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
package houghandWatershed;

import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.KDTree;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPoint;
import net.imglib2.RealPointSampleList;
import net.imglib2.algorithm.BenchmarkAlgorithm;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.algorithm.labeling.AllConnectedComponents;
import net.imglib2.algorithm.labeling.Watershed;
import net.imglib2.algorithm.stats.Normalize;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.labeling.DefaultROIStrategyFactory;
import net.imglib2.labeling.Labeling;
import net.imglib2.labeling.LabelingROIStrategy;
import net.imglib2.labeling.NativeImgLabeling;
import net.imglib2.neighborsearch.NearestNeighborSearchOnKDTree;
import net.imglib2.roi.labeling.ImgLabeling;
import net.imglib2.type.NativeType;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.util.Util;
import net.imglib2.view.Views;
@SuppressWarnings("deprecation")
public class WatershedDistimg <T extends NativeType<T>> extends BenchmarkAlgorithm
implements OutputAlgorithm <RandomAccessibleInterval<IntType>> {
	
	private static final String BASE_ERROR_MSG = "[WatershedDistimg] ";
	private final RandomAccessibleInterval<T> source;
	
	private final RandomAccessibleInterval<BitType> bitimg;
	private RandomAccessibleInterval<IntType> watershedimage;
	RandomAccessibleInterval<UnsignedByteType> distimg;
	/**
	 * Do watershedding after doing distance transformation on the biimg
	 * provided by the user using a user set threshold value.
	 * 
	 * @param source
	 *              The image to be watershedded.
	 * @param bitimg
	 *              The image used to compute distance transform and seeds for watershedding.
	 */
	public WatershedDistimg(final RandomAccessibleInterval<T> source, final RandomAccessibleInterval<BitType> bitimg){
		
		this.source = source;
		this.bitimg = bitimg;
	}
	

	
	@Override
	public boolean checkInput() {
		if (source.numDimensions() > 2) {
			errorMessage = BASE_ERROR_MSG + " Can only operate on 1D, 2D, make slices of your stack . Got "
					+ source.numDimensions() + "D.";
			return false;
		}
		return true;
	}

	@Override
	public boolean process() {

		// Perform the distance transform
		final T type = source.randomAccess().get().createVariable();
		final ImgFactory<UnsignedByteType> factory = Util.getArrayOrCellImgFactory(source, new UnsignedByteType());
		distimg = factory.create(source, new UnsignedByteType());

		DistanceTransformImage(source, distimg);
		//ImageJFunctions.show(distimg);
		
		// Prepare seed image for watershedding
				NativeImgLabeling<Integer, IntType> oldseedLabeling = new NativeImgLabeling<Integer, IntType>(
						new ArrayImgFactory<IntType>().create(source, new IntType()));
				oldseedLabeling = PrepareSeedImage(source);
		// Do watershedding on the distance transformed image

				NativeImgLabeling<Integer, IntType> outputLabeling = new NativeImgLabeling<Integer, IntType>(
						new ArrayImgFactory<IntType>().create(source, new IntType()));

				outputLabeling = GetlabeledImage(distimg, oldseedLabeling);
				
				watershedimage = outputLabeling.getStorageImg();
		
		
		return true;
	}

	@Override
	public RandomAccessibleInterval<IntType> getResult() {
		
		return watershedimage;
	}
    public RandomAccessibleInterval<UnsignedByteType> getDistanceTransformedimg() {
		
		return distimg;
	}
	

	/***
	 * 
	 * Do the distance transform of the input image using the bit image
	 * provided.
	 * 
	 * @param inputimg
	 *            The pre-processed input image as RandomAccessibleInterval <T>
	 * @param outimg
	 *            The distance transormed image having the same dimensions as
	 *            the input image.
	 * @param invtype
	 *            Straight: The intensity value is set to the distance, gives
	 *            white on black background. Inverse: The intensity is set to
	 *            the negative of the distance, gives black on white background.
	 */

	private void DistanceTransformImage(RandomAccessibleInterval<T> inputimg,
			RandomAccessibleInterval<UnsignedByteType> outimg) {
		int n = inputimg.numDimensions();

		// make an empty list
		final RealPointSampleList<BitType> list = new RealPointSampleList<BitType>(n);

		// cursor on the binary image
		final Cursor<BitType> cursor = Views.iterable(bitimg).localizingCursor();

		// for every pixel that is 1, make a new RealPoint at that location
		while (cursor.hasNext())
			if (cursor.next().getInteger() == 1)
				list.add(new RealPoint(cursor), cursor.get());

		// build the KD-Tree from the list of points that == 1
		final KDTree<BitType> tree = new KDTree<BitType>(list);

		// Instantiate a nearest neighbor search on the tree (does not modifiy
		// the tree, just uses it)
		final NearestNeighborSearchOnKDTree<BitType> search = new NearestNeighborSearchOnKDTree<BitType>(tree);

		// randomaccess on the output
		final RandomAccess<UnsignedByteType> ranac = outimg.randomAccess();

		// reset cursor for the input (or make a new one)
		cursor.reset();

		// for every pixel of the binary image
		while (cursor.hasNext()) {
			cursor.fwd();

			// set the randomaccess to the same location
			ranac.setPosition(cursor);

			// if value == 0, look for the nearest 1-valued pixel
			if (cursor.get().getInteger() == 0) {
				// search the nearest 1 to the location of the cursor (the
				// current 0)
				search.search(cursor);

				// get the distance (the previous call could return that, this
				// for generality that it is two calls)
 
				
				ranac.get().setReal(search.getDistance());

			} else {
				// if value == 1, no need to search
				ranac.get().setZero();
			}
		}

	}

	private NativeImgLabeling<Integer, IntType> PrepareSeedImage(RandomAccessibleInterval<T> inputimg) {

		// New Labeling type
		final ImgLabeling<Integer, IntType> seedLabeling = new ImgLabeling<Integer, IntType>(
				new ArrayImgFactory<IntType>().create(inputimg, new IntType()));

		// Old Labeling type
		final NativeImgLabeling<Integer, IntType> oldseedLabeling = new NativeImgLabeling<Integer, IntType>(
				new ArrayImgFactory<IntType>().create(inputimg, new IntType()));

		// The label generator for both new and old type
		final Iterator<Integer> labelGenerator = AllConnectedComponents.getIntegerNames(0);

		

		// Getting unique labelled image (old version)
		AllConnectedComponents.labelAllConnectedComponents(oldseedLabeling, bitimg, labelGenerator,
				AllConnectedComponents.getStructuringElement(inputimg.numDimensions()));
		return oldseedLabeling;
	}
	

	public int GetMaxlabelsseeded(RandomAccessibleInterval<IntType> intimg) {

		// To get maximum Labels on the image
		Cursor<IntType> intCursor = Views.iterable(intimg).cursor();
		int currentLabel = 1;
		boolean anythingFound = true;
		while (anythingFound) {
			anythingFound = false;
			intCursor.reset();
			while (intCursor.hasNext()) {
				intCursor.fwd();
				int i = intCursor.get().get();
				if (i == currentLabel) {

					anythingFound = true;

				}
			}
			currentLabel++;
		}

		return currentLabel;

	}

	public NativeImgLabeling<Integer, IntType> GetlabeledImage(RandomAccessibleInterval<UnsignedByteType> inputimg,
			NativeImgLabeling<Integer, IntType> seedLabeling) {

		int n = inputimg.numDimensions();
		long[] dimensions = new long[n];

		for (int d = 0; d < n; ++d)
			dimensions[d] = inputimg.dimension(d);
		final NativeImgLabeling<Integer, IntType> outputLabeling = new NativeImgLabeling<Integer, IntType>(
				new ArrayImgFactory<IntType>().create(inputimg, new IntType()));

		final Watershed<UnsignedByteType, Integer> watershed = new Watershed<UnsignedByteType, Integer>();

		watershed.setSeeds(seedLabeling);
		watershed.setIntensityImage(inputimg);
		watershed.setStructuringElement(AllConnectedComponents.getStructuringElement(2));
		watershed.setOutputLabeling(outputLabeling);
		watershed.process();
		DefaultROIStrategyFactory<Integer> deffactory = new DefaultROIStrategyFactory<Integer>();
		LabelingROIStrategy<Integer, Labeling<Integer>> factory = deffactory
				.createLabelingROIStrategy(watershed.getResult());
		outputLabeling.setLabelingCursorStrategy(factory);

		return outputLabeling;

	}
	
}

