package MTObjects;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import graphconstructs.KalmanTrackproperties;


public class Subgraphs {

	
	
	public final int Previousframe;
	public final int Currentframe;
	public final SimpleWeightedGraph<KalmanTrackproperties, DefaultWeightedEdge> subgraph;
	
	public Subgraphs(final int Previousframe, final int Currentframe, final SimpleWeightedGraph<KalmanTrackproperties, DefaultWeightedEdge> subgraph  ){
		
		this.Previousframe = Previousframe;
		this.Currentframe = Currentframe;
		this.subgraph = subgraph;
		
	}
}
