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
package labeledObjects;

import ij.gui.EllipseRoi;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.real.FloatType;

public class LabelledImg {

	
	public final int label;
	public final RandomAccessibleInterval<FloatType> roiimg;
	public final RandomAccessibleInterval<FloatType> Actualroiimg;
	public final EllipseRoi roi;
	public final double[] slopeandintercept;
	public final double[] curvatureInflection;
	public final double[] mean;
	public final double[] covar;
	public final double prepline;
	
	public LabelledImg(final int label, final RandomAccessibleInterval<FloatType> roiimg,
			final RandomAccessibleInterval<FloatType> Actualroiimg,
			final EllipseRoi roi,
			final double[] slopeandintercept,
			final double[] curvatureInflection,
			final double[] mean,
			final double[] covar){
		
		this.label = label;
		this.roiimg = roiimg;
		this.Actualroiimg = Actualroiimg;
		this.roi = roi;
		this.slopeandintercept = slopeandintercept;
		this.curvatureInflection = curvatureInflection;
		this.prepline = Double.MAX_VALUE;
		this.mean = mean;
		this.covar = covar;
	}
	
	
	
	
}
