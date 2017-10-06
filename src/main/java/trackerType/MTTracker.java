package trackerType;



import java.util.ArrayList;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import net.imglib2.algorithm.OutputAlgorithm;
import graphconstructs.Trackproperties;
import labeledObjects.Subgraphs;
/**
 * 
 * links objects across multiple frames in time-lapse images, Creates a new graph from a list of blobs, the blob properties of the current frame
 * are enumerated in the static properties
 * @author varunkapoor
 *
 */


public interface MTTracker extends OutputAlgorithm< SimpleWeightedGraph< Trackproperties, DefaultWeightedEdge > >
	{
		/**
		 * Sets the {@link Logger} instance that will receive messages from this
		 * {@link SpotTracker}.
		 *
		 * @param logger
		 *            the logger to echo messages to.
		 */
		public void reset();
		public ArrayList<Subgraphs>  getFramedgraph();
	}