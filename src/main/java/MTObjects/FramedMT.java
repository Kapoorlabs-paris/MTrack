package MTObjects;

import java.util.ArrayList;

import graphconstructs.KalmanTrackproperties;

public class FramedMT {

	
	public final int frame;
	public  KalmanTrackproperties MT;
	
	
	public FramedMT( final int frame, KalmanTrackproperties MT ){
		
		this.frame = frame;
		this.MT = MT;
		
	}
	
	
}