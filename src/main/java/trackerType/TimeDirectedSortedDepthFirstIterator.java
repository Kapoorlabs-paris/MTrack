package trackerType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import graphconstructs.KalmanTrackproperties;


public class TimeDirectedSortedDepthFirstIterator extends SortedDepthFirstIterator<KalmanTrackproperties, DefaultWeightedEdge> {

	public TimeDirectedSortedDepthFirstIterator(final Graph<KalmanTrackproperties, DefaultWeightedEdge> g, final KalmanTrackproperties startVertex, final Comparator<KalmanTrackproperties> comparator) {
		super(g, startVertex, comparator);
	}



    @Override
	protected void addUnseenChildrenOf(final KalmanTrackproperties vertex) {

		// Retrieve target vertices, and sort them in a list
		final List< KalmanTrackproperties > sortedChildren = new ArrayList< KalmanTrackproperties >();
    	// Keep a map of matching edges so that we can retrieve them in the same order
    	final Map<KalmanTrackproperties, DefaultWeightedEdge> localEdges = new HashMap<KalmanTrackproperties, DefaultWeightedEdge>();

    	final int ts = vertex.getFeature(KalmanTrackproperties.FRAME).intValue();
        for (final DefaultWeightedEdge edge : specifics.edgesOf(vertex)) {

        	final KalmanTrackproperties oppositeV = Graphs.getOppositeVertex(graph, edge, vertex);
        	final int tt = oppositeV.getFeature(KalmanTrackproperties.FRAME).intValue();
        	if (tt <= ts) {
        		continue;
        	}

        	if (!seen.containsKey(oppositeV)) {
        		sortedChildren.add(oppositeV);
        	}
        	localEdges.put(oppositeV, edge);
        }

		Collections.sort( sortedChildren, Collections.reverseOrder( comparator ) );
		final Iterator< graphconstructs.KalmanTrackproperties > it = sortedChildren.iterator();
        while (it.hasNext()) {
			final KalmanTrackproperties child = it.next();

            if (nListeners != 0) {
                fireEdgeTraversed(createEdgeTraversalEvent(localEdges.get(child)));
            }

            if (seen.containsKey(child)) {
                encounterVertexAgain(child, localEdges.get(child));
            } else {
                encounterVertex(child, localEdges.get(child));
            }
        }
    }



}
