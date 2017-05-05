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
			boolean dialog = parent.DialogueModelChoice();

			if (dialog) {
				// updatePreview(ValueChange.SHOWMSER);
				LinefinderInteractiveMSER newlineMser = new LinefinderInteractiveMSER(groundframe, groundframepre,
						parent.newtree,parent.minlength, parent.thirdDimension);

				parent.PrevFrameparam = FindlinesVia.LinefindingMethod(groundframe, groundframepre, parent.minlength,
						parent.thirdDimension, parent.psf, newlineMser, UserChoiceModel.Line, parent.Domask, parent.Intensityratio, parent.Inispacing,
						parent.jpb);
				IJ.log("MSER parameters:" + " " + " thirdDimension: " + " " + parent.thirdDimension);
				IJ.log("Delta " + " " + parent.delta + " " + "minSize " + " " + parent.minSize + " " + "maxSize " + " " + parent.maxSize
						+ " " + " maxVar " + " " + parent.maxVar + " " + "minDIversity " + " " + parent.minDiversity);
				IJ.log("Optimization Parameters: " + "R" + parent.Intensityratio + " G"
						+ parent.Inispacing / Math.min(parent.psf[0], parent.psf[1]));

			}

		}

		if (parent.FindLinesViaHOUGH) {

			boolean dialog = parent.DialogueModelChoice();
			if (dialog) {
				// updatePreview(ValueChange.SHOWHOUGH);
				LinefinderInteractiveHough newlineHough = new LinefinderInteractiveHough(groundframe,
						groundframepre, parent.intimg, parent.Maxlabel, parent.thetaPerPixel, parent.rhoPerPixel, parent.thirdDimension, parent.jpb);

				parent.PrevFrameparam = FindlinesVia.LinefindingMethod(groundframe, groundframepre, parent.minlength,
						parent.thirdDimension, parent.psf, newlineHough, UserChoiceModel.Line, parent.Domask, parent.Intensityratio, parent.Inispacing,
						parent.jpb);
				IJ.log("Hough parameters:" + " " + " thirdDimension: " + " " + parent.thirdDimension);
				IJ.log("thetaPerPixel " + " " + parent.thetaPerPixel + " " + "rhoPerPixel " + " " +parent.rhoPerPixel);
				IJ.log("Optimization Parameters: " + "R" + parent.Intensityratio + " G"
						+ parent.Inispacing / Math.min(parent.psf[0], parent.psf[1]));

			}
		}

		if (parent.FindLinesViaMSERwHOUGH) {
			boolean dialog = parent.DialogueModelChoice();
			if (dialog) {
				// updatePreview(ValueChange.SHOWMSER);
				LinefinderInteractiveMSERwHough newlineMserwHough = new LinefinderInteractiveMSERwHough(groundframe,
						groundframepre, parent.newtree, parent.minlength, parent.thirdDimension, parent.thetaPerPixel, parent.rhoPerPixel);
				IJ.log("MSER parameters:" + " " + " thirdDimension: " + " " + parent.thirdDimension);
				IJ.log("Delta " + " " + parent.delta + " " + "minSize " + " " + parent.minSize + " " + "maxSize " + " " + parent.maxSize
						+ " " + " maxVar " + " " + parent.maxVar + " " + "minDIversity " + " " + parent.minDiversity);
				IJ.log("Hough parameters:" + " " + " thirdDimension: " + " " + parent.thirdDimension);
				IJ.log("thetaPerPixel " + " " + parent.thetaPerPixel + " " + "rhoPerPixel " + " " + parent.rhoPerPixel);
				IJ.log("Optimization Parameters: " + "R" + parent.Intensityratio + " G"
						+ parent.Inispacing / Math.min(parent.psf[0], parent.psf[1]));
				parent.PrevFrameparam = FindlinesVia.LinefindingMethod(groundframe, groundframepre, parent.minlength,
						parent.thirdDimension, parent.psf, newlineMserwHough, UserChoiceModel.Line, parent.Domask, parent.Intensityratio,
						parent.Inispacing, parent.jpb);

			}

		}

		ArrayList<KalmanIndexedlength> start = new ArrayList<KalmanIndexedlength>();
		ArrayList<KalmanIndexedlength> end = new ArrayList<KalmanIndexedlength>();

		for (int index = 0; index < parent.PrevFrameparam.getA().size(); ++index) {

			double dx = parent.PrevFrameparam.getA().get(index).ds / Math
					.sqrt(1 + parent.PrevFrameparam.getA().get(index).slope * parent.PrevFrameparam.getA().get(index).slope);
			double dy = parent.PrevFrameparam.getA().get(index).slope * dx;

			KalmanIndexedlength startPart = new KalmanIndexedlength(parent.PrevFrameparam.getA().get(index).currentLabel,
					parent.PrevFrameparam.getA().get(index).seedLabel, parent.PrevFrameparam.getA().get(index).framenumber,
					parent.PrevFrameparam.getA().get(index).ds, parent.PrevFrameparam.getA().get(index).lineintensity,
					parent.PrevFrameparam.getA().get(index).background, parent.PrevFrameparam.getA().get(index).currentpos,
					parent.PrevFrameparam.getA().get(index).fixedpos, parent.PrevFrameparam.getA().get(index).slope,
					parent.PrevFrameparam.getA().get(index).intercept, parent.PrevFrameparam.getA().get(index).slope,
					parent.PrevFrameparam.getA().get(index).intercept, 0, 0, new double[] { dx, dy });

			start.add(startPart);
		}
		for (int index = 0; index < parent.PrevFrameparam.getB().size(); ++index) {

			double dx = parent.PrevFrameparam.getB().get(index).ds / Math
					.sqrt(1 + parent.PrevFrameparam.getB().get(index).slope * parent.PrevFrameparam.getB().get(index).slope);
			double dy = parent.PrevFrameparam.getB().get(index).slope * dx;

			KalmanIndexedlength endPart = new KalmanIndexedlength(parent.PrevFrameparam.getB().get(index).currentLabel,
					parent.PrevFrameparam.getB().get(index).seedLabel, parent.PrevFrameparam.getB().get(index).framenumber,
					parent.PrevFrameparam.getB().get(index).ds, parent.PrevFrameparam.getB().get(index).lineintensity,
					parent.PrevFrameparam.getB().get(index).background, parent.PrevFrameparam.getB().get(index).currentpos,
					parent.PrevFrameparam.getB().get(index).fixedpos, parent.PrevFrameparam.getB().get(index).slope,
					parent.PrevFrameparam.getB().get(index).intercept, parent.PrevFrameparam.getB().get(index).slope,
					parent.PrevFrameparam.getB().get(index).intercept, 0, 0, new double[] { dx, dy });
			end.add(endPart);
		}

		parent.PrevFrameparamKalman = new ValuePair<ArrayList<KalmanIndexedlength>, ArrayList<KalmanIndexedlength>>(start,
				end);

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
