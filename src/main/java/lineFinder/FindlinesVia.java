package lineFinder;

import java.util.ArrayList;

import javax.security.auth.login.AccountExpiredException;
import javax.swing.JProgressBar;


import LineModels.UseLineModel.UserChoiceModel;
import graphconstructs.KalmanTrackproperties;
import graphconstructs.Trackproperties;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.Overlay;
import labeledObjects.Indexedlength;
import labeledObjects.KalmanIndexedlength;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import peakFitter.SubpixelLengthPCKalmanLine;
import peakFitter.SubpixelLengthPCLine;
import peakFitter.SubpixelVelocityPCKalmanLine;
import peakFitter.SubpixelVelocityPCLine;
import preProcessing.Kernels;

public  class FindlinesVia {

	public static  enum LinefindingMethod {

		MSER, Hough, MSERwHough;

	}
	
	public static int Accountedframes ;
	
      protected LinefindingMethod MSER, Hough, MSERwHough;

	public static Pair<ArrayList<Indexedlength>,ArrayList<Indexedlength>> LinefindingMethod(final RandomAccessibleInterval<FloatType> source,
			final RandomAccessibleInterval<FloatType> Preprocessedsource, final int minlength, 
			final int framenumber, final double[] psf, final Linefinder linefinder, final UserChoiceModel model, 
			final boolean DoMask, final double Intensityratio, final double Inispacing, JProgressBar jpb ) {

		
		Pair<ArrayList<Indexedlength>,ArrayList<Indexedlength>>	PrevFrameparam = null;
		

			
			SubpixelLengthPCLine MTline = new SubpixelLengthPCLine(source, linefinder, psf, minlength, model, 0, DoMask, jpb);
			MTline.setIntensityratio(Intensityratio);
			MTline.setInispacing(Inispacing);
			MTline.checkInput();
			MTline.process();
			PrevFrameparam = MTline.getResult();
			
		
		return PrevFrameparam;

	}
	
	public static Pair<ArrayList<KalmanIndexedlength>,ArrayList<KalmanIndexedlength>> LinefindingMethodKalman(final RandomAccessibleInterval<FloatType> source,
			final RandomAccessibleInterval<FloatType> Preprocessedsource, final int minlength, 
			final int framenumber, final double[] psf, final Linefinder linefinder, final UserChoiceModel model, 
			final boolean DoMask, final double Intensityratio, final double Inispacing, JProgressBar jpb ) {

		
		Pair<ArrayList<KalmanIndexedlength>,ArrayList<KalmanIndexedlength>>	PrevFrameparam = null;
		

			
			SubpixelLengthPCKalmanLine MTline = new SubpixelLengthPCKalmanLine(source, linefinder, psf, minlength, model, 0, DoMask, jpb);
			MTline.setIntensityratio(Intensityratio);
			MTline.setInispacing(Inispacing);
			MTline.checkInput();
			MTline.process();
			PrevFrameparam = MTline.getResult();
		
		return PrevFrameparam;

	}

	public static Pair<Pair<ArrayList<Trackproperties>, ArrayList<Trackproperties>>, Pair<ArrayList<Indexedlength>, ArrayList<Indexedlength>>> 
	LinefindingMethodHF(final RandomAccessibleInterval<FloatType> source,
			final RandomAccessibleInterval<FloatType> Preprocessedsource,Pair<ArrayList<Indexedlength>,ArrayList<Indexedlength>> PrevFrameparam,
			final int minlength, final int framenumber, final double[] psf,  final LinefinderHF linefinder, final UserChoiceModel model,
			final boolean DoMask, final double intensityratio, final double Inispacing, final boolean Trackstart, final JProgressBar jpb,
			final int thirdDimsize) {

		Pair<Pair<ArrayList<Trackproperties>, ArrayList<Trackproperties>>,Pair<ArrayList<Indexedlength>,ArrayList<Indexedlength>>> returnVector = null;
		
		

			final SubpixelVelocityPCLine growthtracker = new SubpixelVelocityPCLine(source, linefinder,
					PrevFrameparam.getA(), PrevFrameparam.getB(), psf, framenumber, model, DoMask, Trackstart,jpb, thirdDimsize);
			growthtracker.setIntensityratio(intensityratio);
			growthtracker.setInispacing(Inispacing);
			growthtracker.checkInput();
			growthtracker.process();
			Accountedframes  = growthtracker.getAccountedframes();
			
			Pair<ArrayList<Indexedlength>,ArrayList<Indexedlength>> NewFrameparam = growthtracker.getResult();
			ArrayList<Trackproperties> startStateVectors = growthtracker.getstartStateVectors();
			ArrayList<Trackproperties> endStateVectors = growthtracker.getendStateVectors();
			Pair<ArrayList<Trackproperties>, ArrayList<Trackproperties>> Statevectors = new ValuePair<ArrayList<Trackproperties>, ArrayList<Trackproperties>>(startStateVectors, endStateVectors); 
			returnVector = 
					new ValuePair<Pair<ArrayList<Trackproperties>, ArrayList<Trackproperties>>,Pair<ArrayList<Indexedlength>,ArrayList<Indexedlength>>>(Statevectors, NewFrameparam);
			
			
			
			
		
			
		
		
		
		return returnVector;

	}
	
	
	public static Pair<Pair<ArrayList<KalmanTrackproperties>, ArrayList<KalmanTrackproperties>>, Pair<ArrayList<KalmanIndexedlength>, ArrayList<KalmanIndexedlength>>> 
	LinefindingMethodHFKalman(final RandomAccessibleInterval<FloatType> source,
			final RandomAccessibleInterval<FloatType> Preprocessedsource,Pair<ArrayList<KalmanIndexedlength>,ArrayList<KalmanIndexedlength>> PrevFrameparam,
			final int minlength, final int framenumber, final double[] psf,  final LinefinderHF linefinder, final UserChoiceModel model,
			final boolean DoMask, final int KalmanCount, final double Intensityratio, final double Inispacing, final boolean Trackstart, final JProgressBar jpb,
			final int thirdDimsize) {

		Pair<Pair<ArrayList<KalmanTrackproperties>, ArrayList<KalmanTrackproperties>>,Pair<ArrayList<KalmanIndexedlength>,ArrayList<KalmanIndexedlength>>> returnVector = null;
		
		

			final SubpixelVelocityPCKalmanLine growthtracker = new SubpixelVelocityPCKalmanLine(source, linefinder,
					PrevFrameparam.getA(), PrevFrameparam.getB(), psf, framenumber, model, DoMask, KalmanCount, Trackstart,jpb, thirdDimsize);
			growthtracker.setIntensityratio(Intensityratio);
			growthtracker.setInispacing(Inispacing);
			growthtracker.checkInput();
			growthtracker.process();
			Accountedframes  = growthtracker.getAccountedframes();
			
			Pair<ArrayList<KalmanIndexedlength>,ArrayList<KalmanIndexedlength>> NewFrameparam = growthtracker.getResult();
			ArrayList<KalmanTrackproperties> startStateVectors = growthtracker.getcurrstartStateVectors();
			ArrayList<KalmanTrackproperties> endStateVectors = growthtracker.getcurrendStateVectors();
			Pair<ArrayList<KalmanTrackproperties>, ArrayList<KalmanTrackproperties>> Statevectors = 
					new ValuePair<ArrayList<KalmanTrackproperties>, ArrayList<KalmanTrackproperties>>(startStateVectors, endStateVectors); 
			returnVector = 
					new ValuePair<Pair<ArrayList<KalmanTrackproperties>, ArrayList<KalmanTrackproperties>>,Pair<ArrayList<KalmanIndexedlength>,ArrayList<KalmanIndexedlength>>>(Statevectors, NewFrameparam);
			
			
			
			
		
			
		
		
		
		return returnVector;

	}
	
	public static int getAccountedframes(){
		
		return Accountedframes;
	}
	
	
}
