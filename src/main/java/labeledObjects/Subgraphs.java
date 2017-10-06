package labeledObjects;


import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;



public class Subgraphs {

	
	
	public final int Previousframe;
	public final int Currentframe;
	public final SimpleWeightedGraph<double[], DefaultWeightedEdge> subgraph;
	
	
	
	
	public Subgraphs(final int Previousframe, final int Currentframe, final SimpleWeightedGraph<double[], DefaultWeightedEdge> subgraph ){
		
		this.Previousframe = Previousframe;
		this.Currentframe = Currentframe;
		this.subgraph = subgraph;
		
	}
	
	
    
	
}

