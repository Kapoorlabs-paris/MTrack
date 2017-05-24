package initialization;

import java.io.File;

import ij.Prefs;
import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannelBasic;

public class CreateINIfile {

	
	final Interactive_MTDoubleChannel parent;
	final Interactive_MTDoubleChannelBasic child;
	
	public CreateINIfile(final Interactive_MTDoubleChannel parent) {

		this.parent = parent;
        this.child = null;
		
	}
	
	public CreateINIfile(final Interactive_MTDoubleChannel parent, final Interactive_MTDoubleChannelBasic child) {

		this.parent = parent;
		this.child = child;

	}
	
	public void RecordParent(){
		
		File fileini = new File(parent.usefolder + "//" + "ConfirmedParams" + ".ini");
		
		Prefs.set("PSFX.double", parent.psf[0]);
		Prefs.set("PSFY.double", parent.psf[1]);
		Prefs.set("FindLinesViaMSER.boolean", parent.FindLinesViaMSER);
		Prefs.set("FindLinesViaHough.boolean", parent.FindLinesViaHOUGH);
		Prefs.set("FindLinesViaMSERwHough.boolean", parent.FindLinesViaMSERwHOUGH);
		
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


		
		
		
		
		
	}
	
	
	
}
