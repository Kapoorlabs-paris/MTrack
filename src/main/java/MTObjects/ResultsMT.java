package MTObjects;

public class ResultsMT {

	
	    public final int framenumber;
	    public final int seedid;
	    public final double[] originalpoint;
	    public final double[] oldpoint;
	    public final double[] newpoint;
	    public final double[] oldpointCal;
	    public final double[] newpointCal;
	    public final double lengthrealperframe;
	    public final double totallengthreal;
	    public final double lengthpixelperframe;
	    public final double totallengthpixel;
	    
	    public ResultsMT( final int framenumber, final int seedid, final double[] originalpoint, 
	    		final double[] oldpoint, final double[] newpoint, final double[] oldpointCal, final double[] newpointCal, final double lengthrealperframe,
	    		final double totallengthreal, final double lengthpixelperframe, final double totallengthpixel){
	    	
	    	this.framenumber = framenumber;
	    	this.seedid = seedid;
	    	this.originalpoint = originalpoint;
	    	this.oldpoint = oldpoint;
	    	this.newpoint = newpoint;
	    	this.oldpointCal = oldpointCal;
	    	this.newpointCal = newpointCal;
	    	this.lengthrealperframe = lengthrealperframe;
	    	this.totallengthreal = totallengthreal;
	    	this.lengthpixelperframe = lengthpixelperframe;
	    	this.totallengthpixel = totallengthpixel;
	    	
	    	
	    }
	    
	
	
	
}
