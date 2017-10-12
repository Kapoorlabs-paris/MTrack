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

