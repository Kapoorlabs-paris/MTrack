package psf_Tookit;

import net.imglib2.Localizable;

public class GaussianLineFitParam {

	public final double[] locationA;
	public final double[] locationB;
	public final double Amplitude;
	public final double[] Sigma;
	public final double Background;
	

	public GaussianLineFitParam(final double[] locationA, final double[] locationB, final double Amplitude, final double[] Sigma, final double Background) {

		this.locationA = locationA;
		this.locationB = locationB;
		this.Amplitude = Amplitude;
		this.Sigma = Sigma;
		
		this.Background = Background;

	}

}
