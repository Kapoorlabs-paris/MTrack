package velocityanalyser;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import graphconstructs.Logger;
import graphconstructs.Staticproperties;
import net.imglib2.algorithm.OutputAlgorithm;
public interface Linetracker extends OutputAlgorithm< SimpleWeightedGraph< double[], DefaultWeightedEdge > > {


		/**
		 * Sets the {@link Logger} instance that will receive messages from this
		 * {@link SpotTracker}.
		 *
		 * @param logger
		 *            the logger to echo messages to.
		 */
		public void setLogger( final Logger logger );
	}
	

