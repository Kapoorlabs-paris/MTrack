package drawandOverlay;

import java.awt.Color;
import java.util.ArrayList;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.sun.tools.javac.util.Pair;

import fiji.tool.SliceListener;
import fiji.tool.SliceObserver;
import graphconstructs.KalmanTrackproperties;
import ij.ImagePlus;
import ij.gui.Line;
import ij.gui.Overlay;


public class DisplayGraph {
	// add listener to the imageplus slice slider
	private ImagePlus imp;
	private final SimpleWeightedGraph<double[], DefaultWeightedEdge> graph;
	private final ArrayList<Pair<Integer, double[]>> ID;
	public DisplayGraph(final ImagePlus imp, SimpleWeightedGraph<double[], DefaultWeightedEdge> graph, ArrayList<Pair<Integer, double[]>> ID){
		
		this.imp = imp;
		this.graph = graph;
		this.ID = ID;
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
				newline.setStrokeColor(Color.RED);
				newline.setStrokeWidth(0.8);
				o.add(newline);

				
				imp.updateAndDraw();
				//System.out.println( arg0.getCurrentSlice() );
				
			}
			
			
			for (int index = 0; index < ID.size(); ++index){
				
				 Line newellipse = new Line(ID.get(index).snd[0], ID.get(index).snd[1], ID.get(index).snd[0], ID.get(index).snd[1]);
					

					newellipse.setStrokeColor(Color.WHITE);
					newellipse.setStrokeWidth(1);
					newellipse.setName("TrackID: " + ID.get(index).fst);
					
					o.add(newellipse);
					o.drawLabels(true);
					
					o.drawNames(true);
				
				
			}
			
			
		}		
	}
	
}
