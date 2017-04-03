package trackerType;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import snakes.SnakeObject;

public class TimeDirectedDepthFirstIterator extends SortedDepthFirstIterator<SnakeObject, DefaultWeightedEdge> {

	public TimeDirectedDepthFirstIterator(Graph<SnakeObject, DefaultWeightedEdge> g, SnakeObject startVertex) {
		super(g, startVertex, null);
	}
	
	
	
    protected void addUnseenChildrenOf(SnakeObject vertex) {
    	
    	int ts = vertex.getFeature(SnakeObject.FRAME).intValue();
        for (DefaultWeightedEdge edge : specifics.edgesOf(vertex)) {
            if (nListeners != 0) {
                fireEdgeTraversed(createEdgeTraversalEvent(edge));
            }

            SnakeObject oppositeV = Graphs.getOppositeVertex(graph, edge, vertex);
            int tt = oppositeV.getFeature(SnakeObject.FRAME).intValue();
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
