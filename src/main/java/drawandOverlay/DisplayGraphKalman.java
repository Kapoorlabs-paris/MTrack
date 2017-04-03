package drawandOverlay;

import java.awt.Color;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import fiji.tool.SliceListener;
import fiji.tool.SliceObserver;
import graphconstructs.KalmanTrackproperties;
import ij.ImagePlus;
import ij.gui.Line;
import ij.gui.Overlay;


public class DisplayGraphKalman {
	// add listener to the imageplus slice slider
	private ImagePlus imp;
	private final SimpleWeightedGraph<KalmanTrackproperties, DefaultWeightedEdge> graph;
	
	public DisplayGraphKalman(final ImagePlus imp, SimpleWeightedGraph<KalmanTrackproperties, DefaultWeightedEdge> graph){
		
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
				
		        final KalmanTrackproperties startedge = graph.getEdgeSource(e);
		        final KalmanTrackproperties targetedge = graph.getEdgeTarget(e);
		        
		        Line newline = new Line(startedge.currentpoint[0], startedge.currentpoint[1], targetedge.currentpoint[0], targetedge.currentpoint[1]);
				newline.setStrokeColor(Color.RED);
				newline.setStrokeWidth(0.8);
				o.add(newline);
					
				imp.updateAndDraw();
				//System.out.println( arg0.getCurrentSlice() );
				
			}
			
		}		
	}
	
}

