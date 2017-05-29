package swingClasses;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import LineModels.UseLineModel.UserChoiceModel;
import ij.IJ;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.gui.Roi;
import interactiveMT.Interactive_MTDoubleChannel;
import labeledObjects.KalmanIndexedlength;
import lineFinder.FindlinesVia;
import lineFinder.LinefinderInteractiveHough;
import lineFinder.LinefinderInteractiveMSER;
import lineFinder.LinefinderInteractiveMSERwHough;
import mpicbg.imglib.util.Util;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;

public class ProgressSeeds extends SwingWorker<Void, Void> {

	
final Interactive_MTDoubleChannel parent;
	
	
	public ProgressSeeds(final Interactive_MTDoubleChannel parent){
	
		this.parent = parent;
	}
	
	
	@Override
	protected Void doInBackground() throws Exception {

		// add listener to the imageplus slice slider
		IJ.log("Starting Chosen Line finder from the seed image (first frame should be seeds)");
		IJ.log("Current frame: " + parent.thirdDimension);
		RandomAccessibleInterval<FloatType> groundframe = parent.currentimg;
		RandomAccessibleInterval<FloatType> groundframepre = parent.currentPreprocessedimg;

		if (parent.FindLinesViaMSER) {

		
				LinefinderInteractiveMSER newlineMser = new LinefinderInteractiveMSER(groundframe, groundframepre,
						parent.newtree, parent.thirdDimension);

				parent.PrevFrameparam = FindlinesVia.LinefindingMethod(groundframe, groundframepre,
						parent.thirdDimension, parent.psf, newlineMser, UserChoiceModel.Line, parent.Domask, parent.Intensityratio, parent.Inispacing,
						parent.jpb);
				IJ.log("MSER parameters:" + " " + " thirdDimension: " + " " + parent.thirdDimension);
				IJ.log("Delta " + " " + parent.delta + " " + "minSize " + " " + parent.minSize + " " + "maxSize " + " " + parent.maxSize
						+ " " + " Unstability_Score " + " " + parent.Unstability_Score + " " + "minDIversity " + " " + parent.minDiversity);
				IJ.log("Optimization Parameters: " + "R" + parent.Intensityratio + " G"
						+ parent.Inispacing / Math.min(parent.psf[0], parent.psf[1]));

			

		}

		if (parent.FindLinesViaHOUGH) {

			
				LinefinderInteractiveHough newlineHough = new LinefinderInteractiveHough(groundframe,
						groundframepre, parent.intimg, parent.Maxlabel, parent.thetaPerPixel, parent.rhoPerPixel, parent.thirdDimension, parent.jpb);

				parent.PrevFrameparam = FindlinesVia.LinefindingMethod(groundframe, groundframepre,
						parent.thirdDimension, parent.psf, newlineHough, UserChoiceModel.Line, parent.Domask, parent.Intensityratio, parent.Inispacing,
						parent.jpb);
				IJ.log("Hough parameters:" + " " + " thirdDimension: " + " " + parent.thirdDimension);
				IJ.log("thetaPerPixel " + " " + parent.thetaPerPixel + " " + "rhoPerPixel " + " " +parent.rhoPerPixel);
				IJ.log("Optimization Parameters: " + "R" + parent.Intensityratio + " G"
						+ parent.Inispacing / Math.min(parent.psf[0], parent.psf[1]));

			
		}

		if (parent.FindLinesViaMSERwHOUGH) {
			
				LinefinderInteractiveMSERwHough newlineMserwHough = new LinefinderInteractiveMSERwHough(groundframe,
						groundframepre, parent.newtree, parent.thirdDimension, parent.thetaPerPixel, parent.rhoPerPixel, parent.jpb);
				IJ.log("MSER parameters:" + " " + " thirdDimension: " + " " + parent.thirdDimension);
				IJ.log("Delta " + " " + parent.delta + " " + "minSize " + " " + parent.minSize + " " + "maxSize " + " " + parent.maxSize
						+ " " + " Unstability_Score " + " " + parent.Unstability_Score + " " + "minDIversity " + " " + parent.minDiversity);
				IJ.log("Hough parameters:" + " " + " thirdDimension: " + " " + parent.thirdDimension);
				IJ.log("thetaPerPixel " + " " + parent.thetaPerPixel + " " + "rhoPerPixel " + " " + parent.rhoPerPixel);
				IJ.log("Optimization Parameters: " + "R" + parent.Intensityratio + " G"
						+ parent.Inispacing / Math.min(parent.psf[0], parent.psf[1]));
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

		return null;
	}

	@Override
	protected void done() {
		try {
			parent.jpb.setIndeterminate(false);
			get();
			parent.frame.dispose();
			// JOptionPane.showMessageDialog(jpb.getParent(), "End Points
			// found and overlayed", "Success",
			// JOptionPane.INFORMATION_MESSAGE);
		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}

	}



}
