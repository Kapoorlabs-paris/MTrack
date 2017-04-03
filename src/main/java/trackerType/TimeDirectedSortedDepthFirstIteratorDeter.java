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

import graphconstructs.Trackproperties;


public class TimeDirectedSortedDepthFirstIteratorDeter extends SortedDepthFirstIterator<Trackproperties, DefaultWeightedEdge> {

	public TimeDirectedSortedDepthFirstIteratorDeter(final Graph<Trackproperties, DefaultWeightedEdge> g, final Trackproperties startVertex, final Comparator<Trackproperties> comparator) {
		super(g, startVertex, comparator);
	}



    @Override
	protected void addUnseenChildrenOf(final Trackproperties vertex) {

		// Retrieve target vertices, and sort them in a list
		final List< Trackproperties > sortedChildren = new ArrayList< Trackproperties >();
    	// Keep a map of matching edges so that we can retrieve them in the same order
    	final Map<Trackproperties, DefaultWeightedEdge> localEdges = new HashMap<Trackproperties, DefaultWeightedEdge>();

    	final int ts = vertex.getFeature(Trackproperties.FRAME).intValue();
        for (final DefaultWeightedEdge edge : specifics.edgesOf(vertex)) {

        	final Trackproperties oppositeV = Graphs.getOppositeVertex(graph, edge, vertex);
        	final int tt = oppositeV.getFeature(Trackproperties.FRAME).intValue();
        	if (tt <= ts) {
        		continue;
        	}

        	if (!seen.containsKey(oppositeV)) {
        		sortedChildren.add(oppositeV);
        	}
        	localEdges.put(oppositeV, edge);
        }

		Collections.sort( sortedChildren, Collections.reverseOrder( comparator ) );
		final Iterator< graphconstructs.Trackproperties > it = sortedChildren.iterator();
        while (it.hasNext()) {
			final Trackproperties child = it.next();

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
