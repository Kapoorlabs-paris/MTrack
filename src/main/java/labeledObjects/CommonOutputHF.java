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

import java.util.ArrayList;

import ij.gui.EllipseRoi;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;

public class CommonOutputHF {

	
	public final int framenumber;
	public final int roilabel;
	public final RandomAccessibleInterval<FloatType> Roi;
	public final RandomAccessibleInterval<FloatType> Actualroi;
	public final FinalInterval interval;
	public final RandomAccessibleInterval<IntType> intimg;
	public final ArrayList<EllipseRoi> Allrois;
	
	
	public CommonOutputHF(final int framenumber, final int roilabel,final RandomAccessibleInterval<FloatType> Roi,
			final RandomAccessibleInterval<FloatType> Actualroi, final FinalInterval interval, final ArrayList<EllipseRoi> Allrois){
		this.framenumber = framenumber;
		this.roilabel = roilabel;
		this.Roi = Roi;
		this.Actualroi = Actualroi;
		this.interval = interval;
		this.intimg = null;
		this.Allrois = Allrois;
		
	}
	
	public CommonOutputHF(final int framenumber, final int roilabel,final RandomAccessibleInterval<FloatType> Roi,
			final RandomAccessibleInterval<FloatType> Actualroi,
			final RandomAccessibleInterval<IntType> intimg, final FinalInterval interval){
		this.framenumber = framenumber;
		this.roilabel = roilabel;
		this.Roi = Roi;
		this.Actualroi = Actualroi;
		this.interval = interval;
		this.intimg = intimg;
		this.Allrois = null;
		
	}
	public CommonOutputHF(final int framenumber, final int roilabel,final RandomAccessibleInterval<FloatType> Roi,
			final RandomAccessibleInterval<FloatType> Actualroi,
			final RandomAccessibleInterval<IntType> intimg, final FinalInterval interval, final ArrayList<EllipseRoi> Allrois){
		this.framenumber = framenumber;
		this.roilabel = roilabel;
		this.Roi = Roi;
		this.Actualroi = Actualroi;
		this.interval = interval;
		this.intimg = intimg;
		this.Allrois = Allrois;
		
	}
	
}
