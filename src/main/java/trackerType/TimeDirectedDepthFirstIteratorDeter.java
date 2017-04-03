package trackerType;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import graphconstructs.Trackproperties;

public class TimeDirectedDepthFirstIteratorDeter extends SortedDepthFirstIterator<Trackproperties, DefaultWeightedEdge> {

	public TimeDirectedDepthFirstIteratorDeter(Graph<Trackproperties, DefaultWeightedEdge> g, Trackproperties startVertex) {
		super(g, startVertex, null);
	}
	
	
	
    protected void addUnseenChildrenOf(Trackproperties vertex) {
    	
    	int ts = vertex.getFeature(Trackproperties.FRAME).intValue();
        for (DefaultWeightedEdge edge : specifics.edgesOf(vertex)) {
            if (nListeners != 0) {
                fireEdgeTraversed(createEdgeTraversalEvent(edge));
            }

            Trackproperties oppositeV = Graphs.getOppositeVertex(graph, edge, vertex);
            int tt = oppositeV.getFeature(Trackproperties.FRAME).intValue();
            if (tt <= ts) {
            	continue;
            }

            if ( seen.containsKey(oppositeV)) {
                encounterVertexAgain(oppositeV, edge);
            } else {
                encounterVertex(oppositeV, edge);
            }
        }
    }

	
	
}

