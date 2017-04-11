package velocityanalyser;
import java.util.ArrayList;
import java.util.Iterator;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;


import graphconstructs.Logger;
import graphconstructs.Trackproperties;
import labeledObjects.Subgraphs;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;


public class Trackstart implements Linetracker {

	

		private final ArrayList<ArrayList<Trackproperties>> Allandend;
		private final long maxframe;
		private SimpleWeightedGraph< double[], DefaultWeightedEdge > graph;
		private ArrayList<Subgraphs> Framedgraph;
		private ArrayList<Pair<Integer, double[]>> ID;
		protected Logger logger = Logger.DEFAULT_LOGGER;
		protected String errorMessage;

		public Trackstart(
				final ArrayList<ArrayList<Trackproperties>> Allandend,  
				final int maxframe){
			this.Allandend = Allandend;
			this.maxframe = maxframe;
			
			
		}
		

		public ArrayList<Subgraphs> getFramedgraph() {

			return Framedgraph;
		}
		
		public ArrayList<Pair<Integer, double[]>> getSeedID() {

			return ID;
		}

		@Override
		public boolean process() {

			reset();
			/*
			 * Outputs
			 */

			ID = new ArrayList<Pair<Integer, double[]>>();
			graph = new SimpleWeightedGraph<double[], DefaultWeightedEdge>(DefaultWeightedEdge.class);
			Framedgraph = new ArrayList<Subgraphs>();
			for (int frame = 1; frame < maxframe   ; ++frame){
			
			
				ArrayList<Trackproperties> Baseframeend = Allandend.get(frame - 1);
				
				
				
				Iterator<Trackproperties> baseobjectiterator = Baseframeend.iterator();
				
				SimpleWeightedGraph<double[], DefaultWeightedEdge> subgraph = new SimpleWeightedGraph<double[], DefaultWeightedEdge>(
						DefaultWeightedEdge.class);
		      
				
				while(baseobjectiterator.hasNext()){
					
					final Trackproperties source = baseobjectiterator.next();
					
					
					double sqdist = Distance(source.oldpoint, source.newpoint);
					
					if (sqdist > 0){
					synchronized (graph) {
						
						graph.addVertex(source.oldpoint);
						graph.addVertex(source.newpoint);
						final DefaultWeightedEdge edge = graph.addEdge(source.oldpoint, source.newpoint);
						graph.setEdgeWeight(edge, sqdist);
						
					}
					
					if (frame == 1){
					Pair<Integer, double[]> currentid = new ValuePair<Integer, double[]>(source.seedlabel, source.oldpoint);
					ID.add(currentid);
					}
					subgraph.addVertex(source.oldpoint);
					subgraph.addVertex(source.newpoint);
					final DefaultWeightedEdge subedge = subgraph.addEdge(source.oldpoint, source.newpoint);
					subgraph.setEdgeWeight(subedge, sqdist);

					Subgraphs currentframegraph = new Subgraphs(frame - 1, frame, subgraph);
					Framedgraph.add(currentframegraph);
				}
				}
			}
			
			
				return true;
				
			}
		

		@Override
		public void setLogger( final Logger logger) {
			this.logger = logger;
			
		}
		

		@Override
		public SimpleWeightedGraph< double[], DefaultWeightedEdge > getResult()
		{
			return graph;
		}
		
		@Override
		public boolean checkInput() {
			final StringBuilder errrorHolder = new StringBuilder();
			final boolean ok = checkInput();
			if (!ok) {
				errorMessage = errrorHolder.toString();
			}
			return ok;
		}
		
		public void reset() {
			graph = new SimpleWeightedGraph<double[], DefaultWeightedEdge>(DefaultWeightedEdge.class);
//			final Iterator<Trackproperties> it = Allandend.get(0).iterator();
				graph.addVertex(Allandend.get(0).get(0).oldpoint);
		}

		@Override
		public String getErrorMessage() {
			
			return errorMessage;
		}
		
		
		public double Distance(final double[] cordone, final double[] cordtwo) {

			double distance = 0;

			for (int d = 0; d < cordone.length; ++d) {

				distance += Math.pow((cordone[d] - cordtwo[d]), 2);

			}
			return Math.sqrt(distance);
		}
	}


