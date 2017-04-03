package labeledObjects;

import ij.gui.EllipseRoi;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.real.FloatType;

public class LabelledImg {

	
	public final int label;
	public final RandomAccessibleInterval<FloatType> roiimg;
	public final RandomAccessibleInterval<FloatType> Actualroiimg;
	public final EllipseRoi roi;
	public final double[] slopeandintercept;
	public final double[] curvatureInflection;
	public final double[] mean;
	public final double[] covar;
	public final double prepline;
	
	public LabelledImg(final int label, final RandomAccessibleInterval<FloatType> roiimg,
			final RandomAccessibleInterval<FloatType> Actualroiimg,
			final EllipseRoi roi,
			final double[] slopeandintercept,
			final double[] curvatureInflection,
			final double[] mean,
			final double[] covar){
		
		this.label = label;
		this.roiimg = roiimg;
		this.Actualroiimg = Actualroiimg;
		this.roi = roi;
		this.slopeandintercept = slopeandintercept;
		this.curvatureInflection = curvatureInflection;
		this.prepline = Double.MAX_VALUE;
		this.mean = mean;
		this.covar = covar;
	}
	
	
	
	
}
