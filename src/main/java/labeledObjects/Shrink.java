package labeledObjects;

public class Shrink {

	
	public final int Frame;
	public final int Startframe;
	public final double Rate;
	public final boolean shrink;
	
	
	public Shrink(final int Startframe, final int Frame, final double Rate, final boolean shrink ){
		
		this.Startframe = Startframe;
		this.Frame = Frame;
		this.Rate = Rate;
		this.shrink = shrink;
		
		
	}
	
	
}
