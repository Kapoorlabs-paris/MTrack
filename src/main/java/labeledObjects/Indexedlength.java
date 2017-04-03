package labeledObjects;



public final class Indexedlength {

	public final int currentLabel;
	public final int seedLabel;
	public final int framenumber;
	public final double ds;
	public final double lineintensity;
	public final double background;
	public final double[] currentpos;
	public final double[] fixedpos;
	public final double slope;
	public final double intercept;
	public final double originalslope;
	public final double originalintercept;
	public final double Curvature;
	public final double Inflection;
	public final double[] originalds;
	

	public Indexedlength(final int currentLabel, final int seedLabel, final int framenumber,
			final double ds, final double lineintensity, final double background,
			final double[] currentpos, final double[] fixedpos, 
			final double slope, final double intercept, final double originalslope, final double originalintercept, final double[] originalds) {
		this.currentLabel = currentLabel;
		this.seedLabel = seedLabel;
		this.framenumber = framenumber;
		this.ds = ds;
		this.lineintensity = lineintensity;
		this.background = background;
		this.currentpos = currentpos;
		this.fixedpos = fixedpos;
		this.slope = slope;
		this.intercept = intercept;
		this.originalslope = originalslope;
		this.originalintercept = originalintercept;
		this.originalds = originalds;
		this.Curvature = 0;
		this.Inflection = 0;

		
	}

	public Indexedlength(final int currentLabel, final int seedLabel, final int framenumber,
			final double ds, final double lineintensity, final double background,
			final double[] currentpos, final double[] fixedpos, 
			final double slope, final double intercept, final double originalslope, final double originalintercept, final double Curvature,
			final double Inflection, final double[] originalds) {
		this.currentLabel = currentLabel;
		this.seedLabel = seedLabel;
		this.framenumber = framenumber;
		this.ds = ds;
		this.lineintensity = lineintensity;
		this.background = background;
		this.currentpos = currentpos;
		this.fixedpos = fixedpos;
		this.slope = slope;
		this.intercept = intercept;
		this.originalslope = originalslope;
		this.originalintercept = originalintercept;
		this.Curvature = Curvature;
		this.Inflection = Inflection;
		this.originalds = originalds;

		
	}
	
	
}
