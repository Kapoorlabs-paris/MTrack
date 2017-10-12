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
package initialization;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import LineModels.UseLineModel.UserChoiceModel;
import ij.Prefs;
import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannelBasic;

public class CreateINIfile {

	
	final Interactive_MTDoubleChannel parent;
	
	public CreateINIfile(final Interactive_MTDoubleChannel parent) {

		this.parent = parent;
		
	}
	

	
	
	public void RecordParent(){
		
		
		
		
		Prefs.set("PSFX.double", parent.psf[0]);
		Prefs.set("PSFY.double", parent.psf[1]);
		Prefs.set("FindLinesViaMSER.boolean", parent.FindLinesViaMSER);
		Prefs.set("FindLinesViaHough.boolean", parent.FindLinesViaHOUGH);
		Prefs.set("FindLinesViaMSERwHough.boolean", parent.FindLinesViaMSERwHOUGH);
		Prefs.set("IniX.int", parent.inix);
		Prefs.set("IniY.int", parent.iniy);
		Prefs.set("Numg.int", parent.numgaussians);
		Prefs.set("CalibrationX.double", parent.calibration[0]);
		Prefs.set("CalibrationX.double", parent.calibration[1]);
		Prefs.set("Maxdist.double", parent.maxdist);
		if (parent.FindLinesViaMSER){
			
			Prefs.set("Delta.double", parent.delta);
			Prefs.set("Unstability_Score.double", parent.Unstability_Score);
			Prefs.set("minDiversity.double", parent.minDiversity);
			Prefs.set("minSize.double", parent.minSize);
			Prefs.set("maxSize.double", parent.maxSize);
			
		}
		
		if (parent.FindLinesViaHOUGH){
			
			Prefs.set("thresholdHough.double", parent.thresholdHough);
			Prefs.set("thetaPerPixel.double", parent.thetaPerPixel);
			Prefs.set("rhoPerPixel.double", parent.rhoPerPixel);
			
		}
		
		if (parent.FindLinesViaMSERwHOUGH){
			
			Prefs.set("Delta.double", parent.delta);
			Prefs.set("Unstability_Score.double", parent.Unstability_Score);
			Prefs.set("minDiversity.double", parent.minDiversity);
			Prefs.set("minSize.double", parent.minSize);
			Prefs.set("maxSize.double", parent.maxSize);
			Prefs.set("thresholdHough.double", parent.thresholdHough);
			Prefs.set("thetaPerPixel.double", parent.thetaPerPixel);
			Prefs.set("rhoPerPixel.double", parent.rhoPerPixel);
			
		}

		
		
		
		if (parent.userChoiceModel == UserChoiceModel.Line)

		Prefs.set("Model.int", 1);
		
		if (parent.userChoiceModel == UserChoiceModel.Splineordersec)
		Prefs.set("Model.int", 2);
		
		if (parent.userChoiceModel == UserChoiceModel.Splineorderthird)
			Prefs.set("Model.int", 3);
		
		Prefs.set("Intensityratio.double", parent.Intensityratio);
		Prefs.set("Inispacing.double", parent.Inispacing);
		Prefs.set("Domask.boolean", parent.Domask);
		Prefs.set("deltadcutoff.double", parent.deltadcutoff);
		
		Prefs.set("Folder.file", parent.usefolder);
		
		Prefs.set("ShowMser.boolean", parent.ShowMser);
		
		Prefs.set("ShowHough.boolean", parent.ShowHough);
		
        Prefs.set("doSegmentation.boolean", parent.doSegmentation);
		
		Prefs.set("doMserSegmentation.boolean", parent.doMserSegmentation);
		
		Prefs.set("update.boolean", parent.update);
		
		Prefs.set("Canny.boolean", parent.Canny);
		
		Prefs.set("showDeterministic .boolean", parent.showDeterministic);
		
		Prefs.set("RoiViaMSER.boolean", parent.RoisViaMSER);
		
		Prefs.set("RoiViaWatershed.boolean", parent.RoisViaWatershed);
		
        Prefs.set("autothreshold", parent.autothreshold);
		
		Prefs.savePreferences();
		
		System.exit(1);
		
	}
	
	
	
	
	
	
}
