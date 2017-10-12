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
package mserMethods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.EllipseRoi;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.componenttree.mser.Mser;
import net.imglib2.algorithm.componenttree.mser.MserTree;
import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;

public class GetDelta {

	/**
	 * Determines the best delta parameter for the image with known objects to be found,
	 * User must input the maximum number of objects to be detected, a rough number close to the
	 * exact number would do.
	 * 
	 * @param newimg
	 * @param delta
	 * @param minSize
	 * @param maxSize
	 * @param maxVar
	 * @param minDiversity
	 * @param minlength
	 * @param maxlines
	 * @param maxdelta
	 * @param darktoBright
	 * @return
	 */
	
	public static double Bestdeltaparam(final Img<UnsignedByteType> newimg,final double delta, final long minSize, 
			final long maxSize, final double maxVar, final double minDiversity, final long minlength, final int maxlines, final int maxdelta,  final boolean darktoBright){
	
		
		
		
			int stepdelta = 10;
			
			
			double MaxBestdelta = delta;
			ArrayList<Double> Bestdelta = new ArrayList<Double>();
			int Maxellipsecount = Integer.MIN_VALUE;
			
			
			for (int i = 0; i < maxdelta ; ++i){
			
				
				
			double bestdelta = delta +  i* stepdelta;	
				
			int ellipsecount = 0;
			
			
			ArrayList<double[]> ellipselist = new ArrayList<double[]>();
			

		MserTree<UnsignedByteType> newtree = MserTree.buildMserTree(newimg, bestdelta, minSize, maxSize, maxVar,
				minDiversity, darktoBright);
		final HashSet<Mser<UnsignedByteType>> rootset = newtree.roots();
		final Iterator<Mser<UnsignedByteType>> rootsetiterator = rootset.iterator();
		
		while (rootsetiterator.hasNext()) {

			Mser<UnsignedByteType> rootmser = rootsetiterator.next();

			if (rootmser.size() > 0) {

				final double[] meanandcov = { rootmser.mean()[0], rootmser.mean()[1], rootmser.cov()[0],
						rootmser.cov()[1], rootmser.cov()[2] };
				ellipselist.add(meanandcov);

			}
		}
		
		if (ellipselist.size() > 0){
		
		for (int index = 0; index < ellipselist.size(); ++index) {
			
			
			final double[] mean = { ellipselist.get(index)[0], ellipselist.get(index)[1] };
			final double[] covar = { ellipselist.get(index)[2], ellipselist.get(index)[3],
					ellipselist.get(index)[4] };
			final EllipseRoi ellipseroi = createEllipse(mean, covar, 3);
			
    		final double perimeter = ellipseroi.getLength();
    		
    		if (perimeter >  Math.PI * minSize ){
    			
    			ellipsecount++;
    		}
		}
		}
		if (ellipsecount > Maxellipsecount && rootset.size() <= maxlines){
			
			Maxellipsecount = ellipsecount;
			MaxBestdelta = bestdelta;
		//	System.out.println(rootset.size() + " " + MaxBestdelta);
		}
		

		Bestdelta.add(MaxBestdelta);
		
		}
			
			Set<Double> mySet = new HashSet<Double>(Bestdelta);
			double maxcollection = 0;
			double frequdelta = MaxBestdelta;
			
			for(Double s: mySet){

				 System.out.println( "Best delta:" + s + " " + "Stable over iterations: " + Collections.frequency(Bestdelta,s));

				 
				 if (Collections.frequency(Bestdelta,s) > maxcollection){
				                      maxcollection = Collections.frequency(Bestdelta,s);
				                      frequdelta = s;
				 }
				 
				}
		
			return frequdelta;
		
		
		}
		
		
	
	
	/**
	 * 2D correlated Gaussian
	 * 
	 * @param mean
	 *            (x,y) components of mean vector
	 * @param cov
	 *            (xx, xy, yy) components of covariance matrix
	 * @return ImageJ roi
	 */
	public static EllipseRoi createEllipse(final double[] mean, final double[] cov, final double nsigmas) {
		final double a = cov[0];
		final double b = cov[1];
		final double c = cov[2];
		final double d = Math.sqrt(a * a + 4 * b * b - 2 * a * c + c * c);
		final double scale1 = Math.sqrt(0.5 * (a + c + d)) * nsigmas;
		final double scale2 = Math.sqrt(0.5 * (a + c - d)) * nsigmas;
		final double theta = 0.5 * Math.atan2((2 * b), (a - c));
		final double x = mean[0];
		final double y = mean[1];
		final double dx = scale1 * Math.cos(theta);
		final double dy = scale1 * Math.sin(theta);
		final EllipseRoi ellipse = new EllipseRoi(x - dx, y - dy, x + dx, y + dy, scale2 / scale1);
		return ellipse;
	}
	
}
