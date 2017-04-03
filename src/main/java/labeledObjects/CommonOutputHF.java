package labeledObjects;

import net.imglib2.FinalInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;

public class CommonOutputHF {

	
	public final int framenumber;
	public final int roilabel;
	public final RandomAccessibleInterval<FloatType> Roi;
	public final RandomAccessibleInterval<FloatType> Actualroi;
	public final FinalInterval interval;
	public final RandomAccessibleInterval<IntType> intimg;
	
	public CommonOutputHF(final int framenumber, final int roilabel,final RandomAccessibleInterval<FloatType> Roi,
			final RandomAccessibleInterval<FloatType> Actualroi, final FinalInterval interval){
		this.framenumber = framenumber;
		this.roilabel = roilabel;
		this.Roi = Roi;
		this.Actualroi = Actualroi;
		this.interval = interval;
		this.intimg = null;
		
	}
	
	public CommonOutputHF(final int framenumber, final int roilabel,final RandomAccessibleInterval<FloatType> Roi,
			final RandomAccessibleInterval<FloatType> Actualroi,
			final RandomAccessibleInterval<IntType> intimg, final FinalInterval interval){
		this.framenumber = framenumber;
		this.roilabel = roilabel;
		this.Roi = Roi;
		this.Actualroi = Actualroi;
		this.interval = interval;
		this.intimg = intimg;
		
	}
	
	
}
