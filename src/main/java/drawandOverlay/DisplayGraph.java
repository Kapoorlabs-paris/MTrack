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
package drawandOverlay;

import java.awt.Color;
import java.util.ArrayList;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;


import fiji.tool.SliceListener;
import fiji.tool.SliceObserver;
import ij.ImagePlus;
import ij.gui.Line;
import ij.gui.Overlay;
import net.imglib2.util.Pair;


public class DisplayGraph {
	// add listener to the imageplus slice slider
	private ImagePlus imp;
	private final SimpleWeightedGraph<double[], DefaultWeightedEdge> graph;
	public DisplayGraph(final ImagePlus imp, SimpleWeightedGraph<double[], DefaultWeightedEdge> graph){
		
		this.imp = imp;
		this.graph = graph;
		// add listener to the imageplus slice slider
				SliceObserver sliceObserver = new SliceObserver( imp, new ImagePlusListener() );
	}

	
	
	public ImagePlus getImp() { return this.imp; } 
	
	
	protected  class ImagePlusListener implements SliceListener
	{
		@Override
		public void sliceChanged(ImagePlus arg0)
		{
			
			
			imp.show();
			Overlay o = imp.getOverlay();
			
			if( getImp().getOverlay() == null )
			{
				o = new Overlay();
				getImp().setOverlay( o ); 
			}

			o.clear();
			for (DefaultWeightedEdge e : graph.edgeSet()) {
				
		        final double[] startedge = graph.getEdgeSource(e);
		        final double[] targetedge = graph.getEdgeTarget(e);
		        
		        Line newline = new Line(startedge[0], startedge[1], targetedge[0], targetedge[1]);
				newline.setStrokeColor(Color.red);
				newline.setStrokeWidth(0.8);
				o.add(newline);

				
				imp.updateAndDraw();
				
			}
		
			
		}		
	}
	
}
