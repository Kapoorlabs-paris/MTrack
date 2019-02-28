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
import interactiveMT.Interactive_MTSingleChannel;


public class SingleCreateINIfile {

	
	final Interactive_MTSingleChannel parent;
	
	public SingleCreateINIfile(final Interactive_MTSingleChannel parent) {

		this.parent = parent;
		
	}
	

	
	
	public void RecordParent(){
		
		
		
		
		LocalPrefs.set("PSFX.double", parent.psf[0]);
		LocalPrefs.set("PSFY.double", parent.psf[1]);
		LocalPrefs.set("FindLinesViaMSER.boolean", parent.FindLinesViaMSER);
		LocalPrefs.set("FindLinesViaHough.boolean", parent.FindLinesViaHOUGH);
		LocalPrefs.set("FindLinesViaMSERwHough.boolean", parent.FindLinesViaMSERwHOUGH);
		LocalPrefs.set("IniX.int", parent.inix);
		LocalPrefs.set("IniY.int", parent.iniy);
		
		LocalPrefs.set("CalibrationX.double", parent.calibration[0]);
		LocalPrefs.set("CalibrationX.double", parent.calibration[1]);
		
		if (parent.FindLinesViaMSER){
			
			LocalPrefs.set("Delta.double", parent.delta);
			LocalPrefs.set("Unstability_Score.double", parent.Unstability_Score);
			LocalPrefs.set("minDiversity.double", parent.minDiversity);
			LocalPrefs.set("minSize.double", parent.minSize);
			LocalPrefs.set("maxSize.double", parent.maxSize);
			
		}
		
		if (parent.FindLinesViaHOUGH){
			
			LocalPrefs.set("thresholdHough.double", parent.thresholdHough);
			LocalPrefs.set("thetaPerPixel.double", parent.thetaPerPixel);
			LocalPrefs.set("rhoPerPixel.double", parent.rhoPerPixel);
			
		}
		
		if (parent.FindLinesViaMSERwHOUGH){
			
			LocalPrefs.set("Delta.double", parent.delta);
			LocalPrefs.set("Unstability_Score.double", parent.Unstability_Score);
			LocalPrefs.set("minDiversity.double", parent.minDiversity);
			LocalPrefs.set("minSize.double", parent.minSize);
			LocalPrefs.set("maxSize.double", parent.maxSize);
			LocalPrefs.set("thresholdHough.double", parent.thresholdHough);
			LocalPrefs.set("thetaPerPixel.double", parent.thetaPerPixel);
			LocalPrefs.set("rhoPerPixel.double", parent.rhoPerPixel);
			
		}

		
		
		
		if (parent.userChoiceModel == UserChoiceModel.Line)

		LocalPrefs.set("Model.int", 1);
		
		if (parent.userChoiceModel == UserChoiceModel.Splineordersec)
		LocalPrefs.set("Model.int", 2);
		
		if (parent.userChoiceModel == UserChoiceModel.Splineorderthird)
			LocalPrefs.set("Model.int", 3);
		
		LocalPrefs.set("Intensityratio.double", parent.Intensityratio);
		LocalPrefs.set("Inispacing.double", parent.Inispacing);
		LocalPrefs.set("Domask.boolean", parent.Domask);
		LocalPrefs.set("deltadcutoff.double", parent.deltadcutoff);
		
		LocalPrefs.set("Folder.file", parent.usefolder);
		
		LocalPrefs.set("ShowMser.boolean", parent.ShowMser);
		
		LocalPrefs.set("ShowHough.boolean", parent.ShowHough);
		
        LocalPrefs.set("doSegmentation.boolean", parent.doSegmentation);
		
		LocalPrefs.set("doMserSegmentation.boolean", parent.doMserSegmentation);
		
		LocalPrefs.set("update.boolean", parent.update);
		
		LocalPrefs.set("Canny.boolean", parent.Canny);
		
		LocalPrefs.set("showDeterministic .boolean", parent.showDeterministic);
		
		LocalPrefs.set("RoiViaMSER.boolean", parent.RoisViaMSER);
		
		LocalPrefs.set("RoiViaWatershed.boolean", parent.RoisViaWatershed);
		
		LocalPrefs.set("SaveTxt.boolean", parent.SaveTxt);
		 LocalPrefs.set("autothreshold.boolean", parent.autothreshold);
	        LocalPrefs.setHomeDir(parent.userfile.getPath());
	        System.out.println(LocalPrefs.getHomeDir() + " " + LocalPrefs.getPrefsDir());
		LocalPrefs.savePreferences();
		
		System.exit(1);
		
	}
	
	
	
	
	
	
}
