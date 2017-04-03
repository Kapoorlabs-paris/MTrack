package fakeMTgenerator;

public class Indexofline {

	public final int index;
	public final int frame;
	public final double[] original;
	public final double[] position;
	
	
	public Indexofline(final int index, final int frame, final double[] original, final double[] position){
		
		this.index = index;
		this.frame = frame;
		this.position = position;
		this.original = original;
	}
	
}
