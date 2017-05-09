package psf_Tookit;

public class GaussianFitParam {

	public final int label;
	public final double Amplitude;
	public final double[] Sigma;
	public final double Background;

	public GaussianFitParam(final int label, final double Amplitude, final double[] Sigma, final double Background) {

		this.label = label;
		this.Amplitude = Amplitude;
		this.Sigma = Sigma;
		this.Background = Background;

	}

}
