package lineFinder;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JProgressBar;

import LineModels.UseLineModel.UserChoiceModel;
import graphconstructs.Trackproperties;
import interactiveMT.Interactive_MTSingleChannel.Whichend;
import labeledObjects.Indexedlength;
import lineFinder.FindlinesVia.LinefindingMethod;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import peakFitter.SubpixelLengthCline;
import peakFitter.SubpixelLengthPCLine;
import peakFitter.SubpixelVelocityCline;
import peakFitter.SubpixelVelocityUserSeed;

public class SingleFindlinesVia {

	
	

    protected LinefindingMethod MSER, Hough, MSERwHough;


	
	public static Pair<Pair<ArrayList<Trackproperties>, ArrayList<Trackproperties>>, Pair<ArrayList<Indexedlength>, ArrayList<Indexedlength>>> 
	LinefindingMethodHF(final RandomAccessibleInterval<FloatType> source,
			final RandomAccessibleInterval<FloatType> Preprocessedsource,Pair<ArrayList<Indexedlength>,ArrayList<Indexedlength>> PrevFrameparam,
			 final int framenumber, final double[] psf,  final LinefinderHF linefinder, final UserChoiceModel model,
			final boolean DoMask, final double intensityratio, final double Inispacing, final HashMap<Integer, Whichend> Trackstart, final JProgressBar jpb,
			final int thirdDimsize) {

		Pair<Pair<ArrayList<Trackproperties>, ArrayList<Trackproperties>>,Pair<ArrayList<Indexedlength>,ArrayList<Indexedlength>>> returnVector = null;
		
		

			final SubpixelVelocityCline growthtracker = new SubpixelVelocityCline(source, linefinder,
					PrevFrameparam.getA(), PrevFrameparam.getB(), psf, framenumber, model, DoMask, Trackstart,jpb, thirdDimsize);
			growthtracker.setIntensityratio(intensityratio);
			growthtracker.setInispacing(Inispacing);
			//growthtracker.setSlopetolerance(slopetolerance);
			growthtracker.checkInput();
			growthtracker.process();
			
			Pair<ArrayList<Indexedlength>,ArrayList<Indexedlength>> NewFrameparam = growthtracker.getResult();
			ArrayList<Trackproperties> startStateVectors = growthtracker.getstartStateVectors();
			ArrayList<Trackproperties> endStateVectors = growthtracker.getendStateVectors();
			Pair<ArrayList<Trackproperties>, ArrayList<Trackproperties>> Statevectors = new ValuePair<ArrayList<Trackproperties>, ArrayList<Trackproperties>>(startStateVectors, endStateVectors); 
			returnVector = 
					new ValuePair<Pair<ArrayList<Trackproperties>, ArrayList<Trackproperties>>,Pair<ArrayList<Indexedlength>,ArrayList<Indexedlength>>>(Statevectors, NewFrameparam);
			
			
			
			
		
			
		
		
		
		return returnVector;

	}
	
	public static Pair<ArrayList<Trackproperties>, ArrayList<Indexedlength>> 
	LinefindingMethodHFUser(final RandomAccessibleInterval<FloatType> source,
			final RandomAccessibleInterval<FloatType> Preprocessedsource,ArrayList<Indexedlength> PrevFrameparam,
			 final int framenumber, final double[] psf,  final LinefinderHF linefinder, final UserChoiceModel model,
			final boolean DoMask, final double intensityratio, final double Inispacing, final JProgressBar jpb,
			final int thirdDimsize) {

		Pair<ArrayList<Trackproperties>,ArrayList<Indexedlength>> returnVector = null;
		
		

			final SubpixelVelocityUserSeed growthtracker = new SubpixelVelocityUserSeed(source, linefinder,
					PrevFrameparam, psf, framenumber, model, DoMask,jpb, thirdDimsize);
			growthtracker.setIntensityratio(intensityratio);
			growthtracker.setInispacing(Inispacing);
			growthtracker.checkInput();
			growthtracker.process();
			
			ArrayList<Indexedlength> NewFrameparam = growthtracker.getResult();
			ArrayList<Trackproperties> startStateVectors = growthtracker.getstartStateVectors();
			returnVector = 
					new ValuePair<ArrayList<Trackproperties>,ArrayList<Indexedlength>>(startStateVectors, NewFrameparam);
			
			
			
			
		
			
		
		
		
		return returnVector;

	}
	
	
}
