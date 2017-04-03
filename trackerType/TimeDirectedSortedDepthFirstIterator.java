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

import snakes.SnakeObject;


public class TimeDirectedSortedDepthFirstIterator extends SortedDepthFirstIterator<SnakeObject, DefaultWeightedEdge> {

	public TimeDirectedSortedDepthFirstIterator(final Graph<SnakeObject, DefaultWeightedEdge> g, final SnakeObject startVertex, final Comparator<SnakeObject> comparator) {
		super(g, startVertex, comparator);
	}



    @Override
	protected void addUnseenChildrenOf(final SnakeObject vertex) {

		// Retrieve target vertices, and sort them in a list
		final List< SnakeObject > sortedChildren = new ArrayList< SnakeObject >();
    	// Keep a map of matching edges so that we can retrieve them in the same order
    	final Map<SnakeObject, DefaultWeightedEdge> localEdges = new HashMap<SnakeObject, DefaultWeightedEdge>();

    	final int ts = vertex.getFeature(SnakeObject.FRAME).intValue();
        for (final DefaultWeightedEdge edge : specifics.edgesOf(vertex)) {

        	final SnakeObject oppositeV = Graphs.getOppositeVertex(graph, edge, vertex);
        	final int tt = oppositeV.getFeature(SnakeObject.FRAME).intValue();
        	if (tt <= ts) {
        		continue;
        	}

        	if (!seen.containsKey(oppositeV)) {
        		sortedChildren.add(oppositeV);
        	}
        	localEdges.put(oppositeV, edge);
        }

		Collections.sort( sortedChildren, Collections.reverseOrder( comparator ) );
		final Iterator< SnakeObject > it = sortedChildren.iterator();
        while (it.hasNext()) {
			final SnakeObject child = it.next();

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
