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


import fiji.tool.SliceListener;
import fiji.tool.SliceObserver;
import ij.ImagePlus;
import ij.gui.Line;
import ij.gui.Overlay;
import ij.io.FileSaver;
import labeledObjects.Subgraphs;
import net.imglib2.util.Pair;

public class DisplaysubGraphstart {
	
	private final ImagePlus imp;
	private final ArrayList<Subgraphs> subgraph;
	private final ArrayList<float[]> Kymo;
    private final int ndims;
    private final int frame;
	
	public DisplaysubGraphstart(final ImagePlus imp, final ArrayList<Subgraphs> subgraph, ArrayList<Pair<Integer, double[]>> ID ){
		
		this.imp = imp;
		this.subgraph = subgraph;
		ndims = imp.getNDimensions();
		this.frame = 0;
		this.Kymo = null;

		// add listener to the imageplus slice slider
				SliceObserver sliceObserver = new SliceObserver( imp, new ImagePlusListener() );
	}
	
	public DisplaysubGraphstart(final ImagePlus imp, final ArrayList<Subgraphs> subgraph, int frame){
		
		this.imp = imp;
		this.subgraph = subgraph;
		ndims = imp.getNDimensions();
		this.frame = frame;
		this.Kymo = null;
		// add listener to the imageplus slice slider
				SliceObserver sliceObserver = new SliceObserver( imp, new ImagePlusListener() );
	}
	
public DisplaysubGraphstart(final ImagePlus imp, final ArrayList<Subgraphs> subgraph, int frame, ArrayList<float[]> Kymo){
		
		this.imp = imp;
		this.subgraph = subgraph;
		ndims = imp.getNDimensions();
		this.frame = frame;
		this.Kymo = Kymo;
		// add listener to the imageplus slice slider
				SliceObserver sliceObserver = new SliceObserver( imp, new ImagePlusListener() );
	}
	
public ImagePlus getImp() { return this.imp; } 
	
	
	protected  class ImagePlusListener implements SliceListener
	{
		@Override
		public void sliceChanged(ImagePlus arg0)
		{
			
			int maxFrame = 0;
			if (subgraph.size() > 1)
			maxFrame = frame + subgraph.get(subgraph.size() - 1).Currentframe + 1;
			
			imp.show();
			
			
			Overlay o = imp.getOverlay();
			
			if( getImp().getOverlay() == null )
			{
				o = new Overlay();
				getImp().setOverlay( o ); 
			}

			o.clear();
			
			int currentFrame = arg0.getCurrentSlice();

			for (int index = 0; index < subgraph.size(); ++index){
			
				if (currentFrame  == frame + subgraph.get(index).Previousframe + 1 && currentFrame< maxFrame){
					for (DefaultWeightedEdge e : subgraph.get(index).subgraph.edgeSet()){
						double[] startedge = subgraph.get(index).subgraph.getEdgeSource(e);
						double[] targetedge = subgraph.get(index).subgraph.getEdgeTarget(e);
					        
					        
					        Line newline = new Line(startedge[0], startedge[1], targetedge[0], targetedge[1]);
							newline.setStrokeColor(Color.GREEN);
							newline.setStrokeWidth(0.8);

							
							o.add(newline);
							
						
					}
				}
				
				imp.updateAndDraw();
			}
			
			
		//	final FileSaver savestart = new FileSaver(imp);
		//	savestart.saveAsJpeg("Movingend_subgraph"+arg0.getFrame());
			
		}		
	}
	
}

