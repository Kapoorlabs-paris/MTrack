package lineFinder;

import java.util.ArrayList;

import com.sun.tools.javac.util.Pair;

import graphconstructs.Logger;
import labeledObjects.CommonOutputHF;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import util.Boundingboxes;

public class LinefinderInteractiveHFHough implements LinefinderHF {
	
	
	private static final String BASE_ERROR_MSG = "[Line-Finder]";
	protected String errorMessage;
	protected Logger logger = Logger.DEFAULT_LOGGER;
	private final RandomAccessibleInterval<FloatType> source;
	private final RandomAccessibleInterval<FloatType> Preprocessedsource;
	
	private final RandomAccessibleInterval<IntType> intimg;
	private final int framenumber;
	private ArrayList<CommonOutputHF> output;
	private final int ndims;
	private  int Maxlabel;
	private int Roiindex;
	
	public int mintheta = 0;

	// Usually is 180 but to allow for detection of vertical
	// lines,allowing a few more degrees

	public int maxtheta = 240;
	private final double thetaPerPixel;
	private final double rhoPerPixel;
	
	
	public LinefinderInteractiveHFHough (final RandomAccessibleInterval<FloatType> source, 
			final RandomAccessibleInterval<FloatType> Preprocessedsource, 
			final RandomAccessibleInterval<IntType> intimg,
			final int MaxLabel, final double thetaPerPixel, final double rhoPerPixel,
			final int framenumber){
		
		this.source = source;
		this.Preprocessedsource = Preprocessedsource;
		this.intimg = intimg;
		this.Maxlabel = MaxLabel;
		this.thetaPerPixel = thetaPerPixel;
		this.rhoPerPixel = rhoPerPixel;
		this.framenumber = framenumber;
		ndims = source.numDimensions();
		
		
	}

	@Override
	public boolean checkInput() {
		if (source.numDimensions() > 2) {
			errorMessage = BASE_ERROR_MSG + " Can only operate on 2D, make slices of your stack . Got "
					+ source.numDimensions() + "D.";
			return false;
		}
		return true;
	}

	@Override
	public boolean process() {
		output = new ArrayList<CommonOutputHF>();
		
		int count = 0;
		for (int label = 1; label < Maxlabel ; label++) {

			
           // Do not offset here
			Pair<RandomAccessibleInterval<FloatType>, FinalInterval> pair =  Boundingboxes.CurrentLabelImagepair(intimg, Preprocessedsource, count);
			RandomAccessibleInterval<FloatType> ActualRoiimg = Boundingboxes.CurrentLabelImage(intimg, source, count);
			RandomAccessibleInterval<FloatType> roiimg = pair.fst;
			
			FinalInterval Realinterval = pair.snd;
			
			
				
			 Roiindex = count;
			CommonOutputHF currentOutput = new CommonOutputHF(framenumber, Roiindex , roiimg, ActualRoiimg, intimg, Realinterval);
			
			
			output.add(currentOutput);
			count++;
			
			}
		
		
		return true;
	}

	@Override
	public ArrayList<CommonOutputHF> getResult() {

		return output;
	}

	@Override
	public String getErrorMessage() {

		return errorMessage;
	}


@Override
public void setLogger(Logger logger) {
	this.logger = logger;
	
}

	

}
