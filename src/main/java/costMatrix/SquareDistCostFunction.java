package costMatrix;

import graphconstructs.KalmanTrackproperties;

/**
 * Implementation of various cost functions
 * 
 * 
 */

// Cost function base don minimizing the squared distances

public class SquareDistCostFunction implements CostFunction< KalmanTrackproperties, KalmanTrackproperties >
{

	@Override
	public double linkingCost( final KalmanTrackproperties source, final KalmanTrackproperties target )
	{
		
		return source.squareDistanceTo(target );
	}
	
	
	
	

}
