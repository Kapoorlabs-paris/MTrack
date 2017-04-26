package labeledObjects;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import graphconstructs.KalmanTrackproperties;

public class SubgraphsKalman {

	
	
	
	public final int Previousframe;
	public final int Currentframe;
	public final SimpleWeightedGraph<KalmanTrackproperties, DefaultWeightedEdge> subgraphKalman;
	
	 public SubgraphsKalman(final int Previousframe, final int Currentframe,  final SimpleWeightedGraph<KalmanTrackproperties, DefaultWeightedEdge> subgraphKalman ){
			
			this.Previousframe = Previousframe;
			this.Currentframe = Currentframe;
			this.subgraphKalman = subgraphKalman;
			
		}
	 
	 
	 
}
