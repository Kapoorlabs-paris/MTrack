/*-
 * #%L
 * Microtubule tracker.
 * %%
 * Copyright (C) 2017 MTrack developers.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package velocityanalyser;
import java.util.ArrayList;
import java.util.Iterator;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;


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


