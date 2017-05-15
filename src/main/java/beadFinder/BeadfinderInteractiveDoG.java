package beadFinder;

import java.awt.Color;
import java.util.ArrayList;

import beadObjects.Beadprop;
import graphconstructs.Logger;
import ij.gui.OvalRoi;
import ij.gui.Roi;
import mpicbg.imglib.util.Util;
import net.imglib2.Cursor;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.localextrema.RefinedPeak;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class BeadfinderInteractiveDoG implements Beadfinder {

	
	private static final String BASE_ERROR_MSG = "[BeadfinderDoG] ";
	protected Logger logger = Logger.DEFAULT_LOGGER;
	protected String errorMessage;
	private ArrayList<Beadprop> ProbBlobs;
	private final RandomAccessibleInterval<FloatType> source;
	private final RandomAccessibleInterval<FloatType> target;
	private final int zplane;
	private final int ndims;
	private final ArrayList<RefinedPeak<Point>> peaks;

	public final double sigma;
	public final double sigma2;
	
	
	public BeadfinderInteractiveDoG(final RandomAccessibleInterval<FloatType> source, final RandomAccessibleInterval<FloatType> target,
			final double sigma, final double sigma2, ArrayList<RefinedPeak<Point>> peaks,
		  final int zplane){
		
		this.source = source;
		this.target = target;
	
		this.sigma = sigma;
		this.sigma2 = sigma2;
		this.peaks = peaks;
		this.zplane = zplane;
		ndims = source.numDimensions();
	}
	
	
	
	
	
	
	
	@Override
	public ArrayList<Beadprop> getResult() {

		return ProbBlobs;
	}

	@Override
	public boolean checkInput() {
		
		if (source.numDimensions() > 3 ) {
			errorMessage = BASE_ERROR_MSG + " Can only operate on images upto 3D . Got "
					+ source.numDimensions() + "D.";
			return false;
		}
		return true;
	}

	@Override
	public boolean process() {

		ProbBlobs = new ArrayList<Beadprop>();
		
		
		ArrayList<Roi> ovalrois = new ArrayList<Roi>();
		for (final RefinedPeak<Point> peak : peaks) {
			float x = (float) (peak.getFloatPosition(0));
			float y = (float) (peak.getFloatPosition(1));

			final OvalRoi or = new OvalRoi(Util.round(x - sigma), Util.round(y - sigma), Util.round(sigma + sigma2),
					Util.round(sigma + sigma2));


			ovalrois.add(or);
			
			final long[] center = GetCOM.getProps(source, target, or, zplane); 
			long radius = (long) (sigma + sigma2);
			
			Beadprop currentbead = new Beadprop(zplane, new Point(center), or, radius);
			
			
			
			ProbBlobs.add(currentbead);
		
		}
		
	
	return true;
	}

	public ArrayList<Roi> getRois(ArrayList<RefinedPeak<Point>> peaks){
		
		ArrayList<Roi> ovalrois = new ArrayList<Roi>();
		
		for (final RefinedPeak<Point> peak : peaks) {
			float x = (float) (peak.getFloatPosition(0));
			float y = (float) (peak.getFloatPosition(1));

			final OvalRoi or = new OvalRoi(Util.round(x - sigma), Util.round(y - sigma), Util.round(sigma + sigma2),
					Util.round(sigma + sigma2));

			
				or.setStrokeColor(Color.red);
			

			ovalrois.add(or);
			
			
			
		}
		
		return ovalrois;
		
		
	}
	
	
	
	@Override
	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLogger(Logger logger) {
		// TODO Auto-generated method stub
		
	}

	
	
	
	
	
}
