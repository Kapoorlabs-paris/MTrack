package graphconstructs;

public class Staticproperties {

	
	public final int Label;
	public final double[] oldstartpoint;
	public final double[] oldendpoint;
	public final double[] newstartpoint;
	public final double[] newendpoint;
	public final double[] directionstart;
	public final double[] directionend;
	
	
	public Staticproperties(final int Label,
			final double[] oldstartpoint, final double[] oldendpoint, final double[] newstartpoint, final double[] newendpoint,
			final double[] directionstart, final double[] directionend) {
		this.Label = Label;
		this.oldstartpoint = oldstartpoint;
		this.oldendpoint = oldendpoint;
		this.newstartpoint = newstartpoint;
		this.newendpoint = newendpoint;
		this.directionstart = directionstart;
		this.directionend = directionend;

	}
	
}