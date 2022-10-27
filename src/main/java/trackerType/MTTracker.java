/*-
 * #%L
 * Microtubule tracker.
 * %%
 * Copyright (C) 2017 - 2022 MTrack developers.
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
package trackerType;



import java.util.ArrayList;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import net.imglib2.algorithm.OutputAlgorithm;
import graphconstructs.Trackproperties;
import labeledObjects.Subgraphs;
/**
 * 
 * links objects across multiple frames in time-lapse images, Creates a new graph from a list of blobs, the blob properties of the current frame
 * are enumerated in the static properties
 * @author varunkapoor
 *
 */


public interface MTTracker extends OutputAlgorithm< SimpleWeightedGraph< Trackproperties, DefaultWeightedEdge > >
	{
		/**
		 * Sets the {@link Logger} instance that will receive messages from this
		 * {@link SpotTracker}.
		 *
		 * @param logger
		 *            the logger to echo messages to.
		 */
		public void reset();
		public ArrayList<Subgraphs>  getFramedgraph();
	}
