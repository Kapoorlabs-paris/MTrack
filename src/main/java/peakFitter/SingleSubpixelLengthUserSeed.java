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
package peakFitter;

import java.util.ArrayList;

import ij.gui.OvalRoi;
import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTSingleChannel;
import labeledObjects.Indexedlength;

public class SingleSubpixelLengthUserSeed {

	
	final Interactive_MTSingleChannel parent;
	
	
	public SingleSubpixelLengthUserSeed(final Interactive_MTSingleChannel parent){
	
		this.parent = parent;
	}
	
	public  Indexedlength UserSeed(final double[] newseed, final int maxSeed, OvalRoi bigroi){
		
		
		
		
		
		//int maxSeed = parent.PrevFrameparam.getA().get(parent.PrevFrameparam.getA().size() - 1).seedLabel;
		
		int nextSeed = maxSeed + 1;
		
		final int currentLabel = nextSeed;
		final int framenumber = 1;
		final int seedLabel = nextSeed;
		final double ds = 0.5 *Math.min(parent.psf[0], parent.psf[1]);
		final double lineintensity = 1;
		final double background = 0;
		final double[] currentpos = newseed;
		final double[] fixedpos = newseed;
		final double slope = 0;
		final double intercept = 0;
		final double originalslope = 0;
		final double originalintercept = 0;
		
		final double[] originalds = new double[] {ds, ds};
		
		Indexedlength NewSeed = new Indexedlength(currentLabel, seedLabel, framenumber, ds, lineintensity,
				background, currentpos, fixedpos, slope, intercept, originalslope, originalintercept, originalds, bigroi);
		
		
		return NewSeed;
		
	}
	
	
	
	
}
