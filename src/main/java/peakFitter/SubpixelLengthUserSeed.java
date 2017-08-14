package peakFitter;

import java.util.ArrayList;

import ij.gui.OvalRoi;
import interactiveMT.Interactive_MTDoubleChannel;
import labeledObjects.Indexedlength;

public class SubpixelLengthUserSeed {

	
	final Interactive_MTDoubleChannel parent;
	
	
	public SubpixelLengthUserSeed(final Interactive_MTDoubleChannel parent){
	
		this.parent = parent;
	}
	
	public  Indexedlength UserSeed(final double[] newseed, final int maxSeed, OvalRoi roi){
		
		
		
		
		
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
		
		Indexedlength NewSeed = new Indexedlength(currentLabel, seedLabel, framenumber, ds, lineintensity, background, currentpos, fixedpos, slope, intercept, originalslope, originalintercept, originalds, roi);
		
		
		return NewSeed;
		
	}
	
	
	
	
}
