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
