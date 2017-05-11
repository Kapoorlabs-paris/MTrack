package beadFinder;

import java.util.ArrayList;

import javax.swing.JProgressBar;

import beadObjects.Beadprop;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.real.FloatType;
import peakFitter.SubpixelLocationPoint;
import psf_Tookit.GaussianFitParam;

public class FindbeadsVia {

	
	public static enum BeadfindingMethod {
		
		MSER, DOG;
	}

	
	
	protected BeadfindingMethod MSER, DOG;
	
	public static ArrayList<GaussianFitParam> BeadfindingMethod(RandomAccessibleInterval<FloatType> source, final Beadfinder beadfinder, JProgressBar jpb){
		
		
		SubpixelLocationPoint Beadpoint = new SubpixelLocationPoint(source, beadfinder, jpb);
		Beadpoint.process();
		ArrayList<GaussianFitParam> Allbeads = Beadpoint.getResult();
		
		return Allbeads;
		
	}
	
	
}
