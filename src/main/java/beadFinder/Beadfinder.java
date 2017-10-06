package beadFinder;

import java.util.ArrayList;

import beadObjects.Beadprop;
import net.imglib2.algorithm.OutputAlgorithm;

public interface Beadfinder extends OutputAlgorithm< ArrayList<Beadprop> >
{
	/**
	 * Sets the {@link Logger} instance that will receive messages from this
	 * {@link SpotTracker}.
	 *
	 * @param logger
	 *            the logger to echo messages to.
	 */
}

