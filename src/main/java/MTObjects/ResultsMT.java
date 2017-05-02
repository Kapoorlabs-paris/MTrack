package MTObjects;

public class ResultsMT {

	
	    public final int framenumber;
	    public final double totallengthpixel;
	    public final double totallengthreal;
	    public final int seedid;
	    public final double[] currentpointpixel;
	  
	    public final double[] currentpointreal;
	    public final double lengthpixelperframe;
	    public final double lengthrealperframe;
	   
	   
	   
	    public ResultsMT(final int framenumber, final double totallengthpixel, final double totallengthreal, final int seedid, final double[] currentpointpixel,
	    		final double[] currentpointreal, final double lengthpixelperframe, final double lengthrealperframe){
	    	    
	    	    this.framenumber = framenumber;
	    	    this.totallengthpixel = totallengthpixel;
	    	    this.totallengthreal = totallengthreal;
	    	    this.seedid = seedid;
	    	    this.currentpointpixel = currentpointpixel;
	    	    this.currentpointreal = currentpointreal;
	    	    this.lengthpixelperframe = lengthpixelperframe;
	    	    this.lengthrealperframe = lengthrealperframe;
	    	
	    	
	    }
	    
	    
	   
	    
	
	
	
}
