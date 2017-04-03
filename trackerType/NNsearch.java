package trackerType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import graphconstructs.Logger;
import kdTreeBlobs.FlagNode;
import kdTreeBlobs.NNFlagsearchKDtree;
import net.imglib2.KDTree;
import net.imglib2.RealPoint;
import snakes.SnakeObject;

public class NNsearch implements BlobTracker {

	private final ArrayList<ArrayList<SnakeObject>> Allblobs;
	private final double maxdistance;
	private final long maxframe;
	private SimpleWeightedGraph< SnakeObject, DefaultWeightedEdge > graph;
	protected Logger logger = Logger.DEFAULT_LOGGER;
	protected String errorMessage;

	public NNsearch(
			final ArrayList<ArrayList<SnakeObject>> Allblobs, final double maxdistance, 
			final long maxframe){
		this.Allblobs = Allblobs;
		this.maxdistance = maxdistance;
		this.maxframe = maxframe;
		
		
	}
	
	
	
	

	@Override
	public boolean process() {

		reset();
		
		
		for (int frame = 0; frame < maxframe - 1; ++frame){
		
		
			ArrayList<SnakeObject> Spotmaxbase = Allblobs.get(frame);
			
			ArrayList<SnakeObject> Spotmaxtarget = Allblobs.get(frame + 1);
			
			Iterator<SnakeObject> baseobjectiterator = Spotmaxbase.iterator();
			
			
			
	        final int Targetblobs = Spotmaxtarget.size();
	        
			final List<RealPoint> targetCoords = new ArrayList<RealPoint>(Targetblobs);

			final List<FlagNode<SnakeObject>> targetNodes = new ArrayList<FlagNode<SnakeObject>>(Targetblobs);
			
			
	      
			for (int index = 0; index < Spotmaxtarget.size(); ++index){
				
				
				
				targetCoords.add(new RealPoint(Spotmaxtarget.get(index).centreofMass));

				targetNodes.add(new FlagNode<SnakeObject>(Spotmaxtarget.get(index)));
				
				
			}
			
			if (targetNodes.size() > 0 && targetCoords.size() > 0){
			
			final KDTree<FlagNode<SnakeObject>> Tree = new KDTree<FlagNode<SnakeObject>>(targetNodes, targetCoords);
			
			final NNFlagsearchKDtree<SnakeObject> Search = new NNFlagsearchKDtree<SnakeObject>(Tree);
			
			
			
			while(baseobjectiterator.hasNext()){
				
				final SnakeObject source = baseobjectiterator.next();
				final RealPoint sourceCoords = new RealPoint(source.centreofMass);
				Search.search(sourceCoords);
				final double squareDist = Search.getSquareDistance();
				final FlagNode<SnakeObject> targetNode = Search.getSampler().get();
				if (squareDist > maxdistance)
					continue;

				targetNode.setVisited(true);
				
				synchronized (graph) {
					
					graph.addVertex(source);
					graph.addVertex(targetNode.getValue());
					final DefaultWeightedEdge edge = graph.addEdge(source, targetNode.getValue());
					graph.setEdgeWeight(edge, squareDist);
					
					
				}
			
		       
			}
			
			System.out.println("NN detected, moving to next frame!");
		}
		}
		
			return true;
			
		}
	

	@Override
	public void setLogger( final Logger logger) {
		this.logger = logger;
		
	}
	

	@Override
	public SimpleWeightedGraph< SnakeObject, DefaultWeightedEdge > getResult()
	{
		return graph;
	}
	
	
	
	@Override
	public boolean checkInput() {
		final StringBuilder errrorHolder = new StringBuilder();;
		final boolean ok = checkInput();
		if (!ok) {
			errorMessage = errrorHolder.toString();
		}
		return ok;
	}
	
	@Override
	public void reset() {
		
		graph = new SimpleWeightedGraph<SnakeObject, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		final Iterator<SnakeObject> it = Allblobs.get(0).iterator();
		while (it.hasNext()) {
			graph.addVertex(it.next());
		}
	}

	@Override
	public String getErrorMessage() {
		
		return errorMessage;
	}
}
