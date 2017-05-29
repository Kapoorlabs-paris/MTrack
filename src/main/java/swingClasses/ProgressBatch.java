package swingClasses;

import java.awt.Rectangle;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import LineModels.UseLineModel.UserChoiceModel;
import ij.IJ;
import ij.Prefs;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import interactiveMT.BatchMode;
import lineFinder.FindlinesVia;
import lineFinder.LinefinderInteractiveHough;
import lineFinder.LinefinderInteractiveMSER;
import lineFinder.LinefinderInteractiveMSERwHough;
import mpicbg.imglib.util.Util;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import updateListeners.FinalPoint;

public class ProgressBatch extends SwingWorker<Void, Void>  {

	
	
final BatchMode parent;
	
	
	public ProgressBatch(final BatchMode parent){
	
		this.parent = parent;
	}
	
	
	@Override
	protected Void doInBackground() throws Exception {
		
		RandomAccessibleInterval<FloatType> groundframe = parent.currentimg;
		RandomAccessibleInterval<FloatType> groundframepre = parent.currentPreprocessedimg;
		
		// Step 1 Locate the seeds and find the end points
		
		if (parent.parent.FindLinesViaMSER) {

			
			
			LinefinderInteractiveMSER newlineMser = new LinefinderInteractiveMSER(groundframe, groundframepre,
					parent.newtree, parent.thirdDimension);

			parent.PrevFrameparam = FindlinesVia.LinefindingMethod(groundframe, groundframepre,
					parent.thirdDimension, parent.psf, newlineMser, UserChoiceModel.Line, parent.Domask, parent.Intensityratio, parent.Inispacing,
					parent.jpb);
			

		

	}

	if (parent.parent.FindLinesViaHOUGH) {

		
			LinefinderInteractiveHough newlineHough = new LinefinderInteractiveHough(groundframe,
					groundframepre, parent.intimg, parent.Maxlabel, parent.thetaPerPixel, parent.rhoPerPixel, parent.thirdDimension, parent.jpb);

			parent.PrevFrameparam = FindlinesVia.LinefindingMethod(groundframe, groundframepre,
					parent.thirdDimension, parent.psf, newlineHough, UserChoiceModel.Line, parent.Domask, parent.Intensityratio, parent.Inispacing,
					parent.jpb);
		

		
	}

	if (parent.parent.FindLinesViaMSERwHOUGH) {
		
			LinefinderInteractiveMSERwHough newlineMserwHough = new LinefinderInteractiveMSERwHough(groundframe,
					groundframepre, parent.newtree, parent.thirdDimension, parent.thetaPerPixel, parent.rhoPerPixel, parent.jpb);
		
			parent.PrevFrameparam = FindlinesVia.LinefindingMethod(groundframe, groundframepre,
					parent.thirdDimension, parent.psf, newlineMserwHough, UserChoiceModel.Line, parent.Domask, parent.Intensityratio,
					parent.Inispacing, parent.jpb);

		

	}


	Overlay o = parent.preprocessedimp.getOverlay();

	if (parent.preprocessedimp.getOverlay() == null) {
		o = new Overlay();
		parent.preprocessedimp.setOverlay(o);
	}
	o.clear();
	for (int index = 0; index < parent.PrevFrameparam.getA().size(); ++index) {

		parent.Seedroi = new OvalRoi(Util.round(parent.PrevFrameparam.getA().get(index).currentpos[0] - parent.radiusseed),
				Util.round(parent.PrevFrameparam.getA().get(index).currentpos[1] - parent.radiusseed), Util.round(2 * parent.radiusseed), Util.round(2 * parent.radiusseed));
		parent.Seedroi.setStrokeColor(parent.colorConfirm);
		parent.Seedroi.setStrokeWidth(0.8);

		
		
		parent.AllSeedrois.add(parent.Seedroi);
		o.add(parent.Seedroi);

	}

	for (int index = 0; index < parent.PrevFrameparam.getB().size(); ++index) {

		parent.Seedroi = new OvalRoi(Util.round(parent.PrevFrameparam.getB().get(index).currentpos[0] - parent.radiusseed),
				Util.round(parent.PrevFrameparam.getB().get(index).currentpos[1] - parent.radiusseed), Util.round(2 * parent.radiusseed), Util.round(2 * parent.radiusseed));
		parent.Seedroi.setStrokeColor(parent.colorConfirm);
		parent.Seedroi.setStrokeWidth(0.8);
		
		
		
		parent.AllSeedrois.add(parent.Seedroi);
		o.add(parent.Seedroi);

	}
	for(int index = 0; index < parent.AllSeedrois.size(); ++index){
		
		Rectangle rect = parent.AllSeedrois.get(index).getBounds();
		double newx = rect.x + rect.width / 2.0;
		double newy = rect.y + rect.height / 2.0;
		Pair<double[], OvalRoi> newpoint = new ValuePair<double[], OvalRoi>(new double[]{newx, newy}, parent.AllSeedrois.get(index));

		parent.ClickedPoints.add(newpoint);
	}
	parent.preprocessedimp.updateAndDraw();

	// After the seed ends are found, the hash map fo both ends to be tracked is created
	 
	FinalPoint Allpoints = new FinalPoint(parent.parent);
	
	 Allpoints.DefaultEnds();
	
	 // Now we track it from the first image in the dynamic channel to the last
	 
	 int next = 2;
	 
	 TrackBatch newtrack = new TrackBatch(parent);
	 newtrack.Trackobject(next, parent.thirdDimensionSize);
	 
	
		
		
		
		return null;
		
	}
	
	@Override
	protected void done() {
		try {
			parent.jpb.setIndeterminate(false);
			get();
			
		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}

	}
	
	
}
