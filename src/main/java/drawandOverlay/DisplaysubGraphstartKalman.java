package drawandOverlay;


import java.awt.Color;
import java.util.ArrayList;

import org.jgrapht.graph.DefaultWeightedEdge;


import fiji.tool.SliceListener;
import fiji.tool.SliceObserver;
import graphconstructs.KalmanTrackproperties;
import graphconstructs.Staticproperties;
import graphconstructs.Trackproperties;
import ij.ImagePlus;
import ij.gui.Line;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.io.FileSaver;
import labeledObjects.SubgraphsKalman;
import mpicbg.imglib.util.Util;
import net.imglib2.util.Pair;

public class DisplaysubGraphstartKalman {
	
	private final ImagePlus imp;
	private final ArrayList<SubgraphsKalman> subgraph;
	private final ArrayList<float[]> Kymo;
    private final int ndims;
    private final int frame;
	
	public DisplaysubGraphstartKalman(final ImagePlus imp, final ArrayList<SubgraphsKalman> subgraph, ArrayList<Pair<Integer, double[]>> ID ){
		
		this.imp = imp;
		this.subgraph = subgraph;
		ndims = imp.getNDimensions();
		this.frame = 0;
		this.Kymo = null;

		// add listener to the imageplus slice slider
				SliceObserver sliceObserver = new SliceObserver( imp, new ImagePlusListener() );
	}
	
	public DisplaysubGraphstartKalman(final ImagePlus imp, final ArrayList<SubgraphsKalman> subgraph, int frame){
		
		this.imp = imp;
		this.subgraph = subgraph;
		ndims = imp.getNDimensions();
		this.frame = frame;
		this.Kymo = null;
		// add listener to the imageplus slice slider
				SliceObserver sliceObserver = new SliceObserver( imp, new ImagePlusListener() );
	}
	
public DisplaysubGraphstartKalman(final ImagePlus imp, final ArrayList<SubgraphsKalman> subgraph, int frame, ArrayList<float[]> Kymo){
		
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
					for (DefaultWeightedEdge e : subgraph.get(index).subgraphKalman.edgeSet()){
					KalmanTrackproperties startedge = subgraph.get(index).subgraphKalman.getEdgeSource(e);
					KalmanTrackproperties targetedge = subgraph.get(index).subgraphKalman.getEdgeTarget(e);
					        
					final OvalRoi Bigroi = new OvalRoi(Util.round(startedge.currentpoint[0] - 5), Util.round(startedge.currentpoint[1] - 5), Util.round(10),
							Util.round(10));
							Bigroi.setStrokeColor(Color.GREEN);
							Bigroi.setStrokeWidth(0.8);

							
							o.add(Bigroi);
							
						
					}
				}
				
				imp.updateAndDraw();
			}
			
			
		//	final FileSaver savestart = new FileSaver(imp);
		//	savestart.saveAsJpeg("Movingend_subgraph"+arg0.getFrame());
			
		}		
	}
	
}

