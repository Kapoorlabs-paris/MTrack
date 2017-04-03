package trackerType;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import graphconstructs.KalmanTrackproperties;

public class TimeDirectedDepthFirstIterator extends SortedDepthFirstIterator<KalmanTrackproperties, DefaultWeightedEdge> {

	public TimeDirectedDepthFirstIterator(Graph<KalmanTrackproperties, DefaultWeightedEdge> g, KalmanTrackproperties startVertex) {
		super(g, startVertex, null);
	}
	
	
	
    protected void addUnseenChildrenOf(KalmanTrackproperties vertex) {
    	
    	int ts = vertex.getFeature(KalmanTrackproperties.FRAME).intValue();
        for (DefaultWeightedEdge edge : specifics.edgesOf(vertex)) {
            if (nListeners != 0) {
                fireEdgeTraversed(createEdgeTraversalEvent(edge));
            }

            KalmanTrackproperties oppositeV = Graphs.getOppositeVertex(graph, edge, vertex);
            int tt = oppositeV.getFeature(KalmanTrackproperties.FRAME).intValue();
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
