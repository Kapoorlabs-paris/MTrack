package lineFinder;

import java.util.ArrayList;

import graphconstructs.Logger;
import labeledObjects.CommonOutputHF;
import net.imglib2.algorithm.OutputAlgorithm;

public interface LinefinderHF extends  OutputAlgorithm<ArrayList<CommonOutputHF>> {

	

	/**
	 * Sets the {@link Logger} instance that will receive messages from this
	 * {@link SpotTracker}.
	 *
	 * @param logger
	 *            the logger to echo messages to.
	 */
	public void setLogger( final Logger logger );
	
	
	
}
