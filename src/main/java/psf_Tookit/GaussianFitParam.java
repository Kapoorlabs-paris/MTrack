package psf_Tookit;

import net.imglib2.Localizable;

public class GaussianFitParam {

	public final Localizable location;
	public final double Amplitude;
	public final double[] Sigma;
	public final double Background;

	public GaussianFitParam(final Localizable location, final double Amplitude, final double[] Sigma, final double Background) {

		this.location = location;
		this.Amplitude = Amplitude;
		this.Sigma = Sigma;
		this.Background = Background;

	}

}
