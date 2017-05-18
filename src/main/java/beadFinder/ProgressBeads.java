package beadFinder;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import beadAnalyzer.DrawPoints;
import ij.IJ;
import interactiveMT.Interactive_PSFAnalyze;
import interactiveMT.Interactive_PSFAnalyze.ValueChange;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.real.FloatType;
import psf_Tookit.GaussianFitParam;

public class ProgressBeads extends SwingWorker<Void, Void> {

	final Interactive_PSFAnalyze parent;

	public ProgressBeads(final Interactive_PSFAnalyze parent) {

		this.parent = parent;
	}

	@Override
	protected Void doInBackground() throws Exception {

		if (parent.thirdDimensionSize > 1) {
			for (int index = parent.thirdDimensionsliderInit; index < parent.thirdDimensionSize; ++index) {

				parent.thirdDimension = index;
				parent.CurrentPreprocessedView = util.CopyUtils.getCurrentPreView(parent.originalPreprocessedimg,
						parent.thirdDimension, parent.thirdDimensionSize);
				parent.CurrentView = util.CopyUtils.getCurrentView(parent.originalimg, parent.thirdDimension,
						parent.thirdDimensionSize);

				parent.updatePreview(ValueChange.THIRDDIMTrack);

				RandomAccessibleInterval<FloatType> source = parent.currentPreprocessedimg;
				RandomAccessibleInterval<FloatType> target = parent.currentimg;

				if (parent.FindBeadsViaMSER) {
					parent.updatePreview(ValueChange.SHOWMSER);
					BeadfinderInteractiveMSER newbeadMser = new BeadfinderInteractiveMSER(source, target,
							parent.newtree, parent.thirdDimension);
					parent.FittedBeads = FindbeadsVia.BeadfindingMethod(target, newbeadMser, parent.jpb, index,
							parent.thirdDimensionSize);
					parent.AllFittedBeads.addAll(parent.FittedBeads);

				}

				if (parent.FindBeadsViaDOG) {
					parent.updatePreview(ValueChange.SHOWDOG);
					BeadfinderInteractiveDoG newbeadDog = new BeadfinderInteractiveDoG(source, target, parent.sigma,
							parent.sigma2, parent.peaks, parent.thirdDimension);
					parent.FittedBeads = FindbeadsVia.BeadfindingMethod(target, newbeadDog, parent.jpb, index,
							parent.thirdDimensionSize);
					parent.AllFittedBeads.addAll(parent.FittedBeads);
				}
				IJ.log("Fitted Parameters: ");
				for (int indexx = 0; indexx < parent.FittedBeads.size(); ++indexx) {

					IJ.log(" Amplitude: " + parent.FittedBeads.get(indexx).Amplitude);
					IJ.log(" Position X: " + parent.FittedBeads.get(indexx).location.getDoublePosition(0));
					IJ.log(" Position Y: " + parent.FittedBeads.get(indexx).location.getDoublePosition(1));
					IJ.log(" Sigma X: " + parent.FittedBeads.get(indexx).Sigma[0]);
					IJ.log(" Sigma Y: " + parent.FittedBeads.get(indexx).Sigma[1]);
					IJ.log(" Background: " + parent.FittedBeads.get(indexx).Background);
				}
			}

		}

		else {

			RandomAccessibleInterval<FloatType> source = parent.currentPreprocessedimg;
			RandomAccessibleInterval<FloatType> target = parent.currentimg;

			if (parent.FindBeadsViaMSER) {
				parent.updatePreview(ValueChange.SHOWMSER);
				BeadfinderInteractiveMSER newbeadMser = new BeadfinderInteractiveMSER(source, target, parent.newtree,
						parent.thirdDimension);
				parent.FittedBeads = FindbeadsVia.BeadfindingMethod(target, newbeadMser, parent.jpb, 1, 1);
				parent.AllFittedBeads.addAll(parent.FittedBeads);

			}

			if (parent.FindBeadsViaDOG) {
				parent.updatePreview(ValueChange.SHOWDOG);
				BeadfinderInteractiveDoG newbeadDog = new BeadfinderInteractiveDoG(source, target, parent.sigma,
						parent.sigma2, parent.peaks, parent.thirdDimension);
				parent.FittedBeads = FindbeadsVia.BeadfindingMethod(target, newbeadDog, parent.jpb, 1, 1);
				parent.AllFittedBeads.addAll(parent.FittedBeads);
			}
			IJ.log("Fitted Parameters: ");
			for (int indexx = 0; indexx < parent.FittedBeads.size(); ++indexx) {

				IJ.log(" Amplitude: " + parent.FittedBeads.get(indexx).Amplitude);
				IJ.log(" Position X: " + parent.FittedBeads.get(indexx).location.getDoublePosition(0));
				IJ.log(" Position Y: " + parent.FittedBeads.get(indexx).location.getDoublePosition(1));
				IJ.log(" Sigma X: " + parent.FittedBeads.get(indexx).Sigma[0]);
				IJ.log(" Sigma Y: " + parent.FittedBeads.get(indexx).Sigma[1]);
				IJ.log(" Background: " + parent.FittedBeads.get(indexx).Background);
			}

		}

		return null;
	}

	@Override
	protected void done() {
		try {

			DrawPoints draw = new DrawPoints();
			if (parent.AllFittedBeads.size() > 1)
				draw.drawPoints(parent.AllFittedBeads);
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
