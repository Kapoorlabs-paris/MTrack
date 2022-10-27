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

import java.util.ArrayList;

import javax.swing.JProgressBar;

import beadObjects.Beadprop;
import lineFinder.Linefinder;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.real.FloatType;
import peakFitter.SubpixelLocationLine;
import peakFitter.SubpixelLocationPoint;
import psf_Tookit.GaussianFitParam;
import psf_Tookit.GaussianLineFitParam;

public class FindbeadsVia {

	
	public static enum BeadfindingMethod {
		
		MSER, DOG;
	}

	
	
	protected BeadfindingMethod MSER, DOG;
	
	public static ArrayList<GaussianFitParam> BeadfindingMethod(RandomAccessibleInterval<FloatType> source, final Beadfinder beadfinder, JProgressBar jpb, final int framenumber, final int thirdDimensionsize){
		
		
		SubpixelLocationPoint Beadpoint = new SubpixelLocationPoint(source, beadfinder, jpb, framenumber, thirdDimensionsize);
		Beadpoint.process();
		ArrayList<GaussianFitParam> Allbeads = Beadpoint.getResult();
		
		return Allbeads;
		
	}
	
    public static ArrayList<GaussianLineFitParam> BeadfindingMethod(RandomAccessibleInterval<FloatType> source, final Linefinder linefinder, JProgressBar jpb, final double[] initialpsf,
    		double Intensityratio, double Inispacing, final int framenumber, final int thirdDimensionsize){
		
    	
		SubpixelLocationLine BeadLinepoint = new SubpixelLocationLine(source, linefinder, initialpsf, jpb, framenumber, thirdDimensionsize);
		BeadLinepoint.setIntensityratio(Intensityratio);
		BeadLinepoint.setInispacing(Inispacing);
		BeadLinepoint.process();
		ArrayList<GaussianLineFitParam> AllLinebeads = BeadLinepoint.getResult();
		
		return AllLinebeads;
		
	}
    
    
  
    
	
}
