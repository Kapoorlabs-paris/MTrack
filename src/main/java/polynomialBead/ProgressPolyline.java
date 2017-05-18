package polynomialBead;

import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import beadAnalyzer.DrawPoints;
import beadFinder.BeadfinderInteractiveDoG;
import beadFinder.BeadfinderInteractiveMSER;
import beadFinder.FindbeadsVia;
import ij.IJ;
import interactiveMT.Interactive_PSFAnalyze;
import interactiveMT.Interactive_PSFAnalyze.ValueChange;
import lineFinder.LinefinderInteractiveMSER;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.real.FloatType;

public class ProgressPolyline extends SwingWorker<Void, Void> {

	final Interactive_PSFAnalyze parent;

	public ProgressPolyline(final Interactive_PSFAnalyze parent) {

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

				parent.updatePreview(ValueChange.SHOWMSER);
				LinefinderInteractiveMSER newbeadMser = new LinefinderInteractiveMSER(source, target, parent.newtree,
						parent.thirdDimension);

				parent.FittedLineBeads = FindbeadsVia.BeadfindingMethod(target, newbeadMser, parent.jpb,
						parent.initialpsf, parent.Intensityratio, parent.Inispacing, index, parent.thirdDimensionSize);

				parent.AllFittedLineBeads.addAll(parent.FittedLineBeads);

			}
		}

		else {

			RandomAccessibleInterval<FloatType> source = parent.currentPreprocessedimg;
			RandomAccessibleInterval<FloatType> target = parent.currentimg;

			parent.updatePreview(ValueChange.SHOWMSER);
			LinefinderInteractiveMSER newbeadMser = new LinefinderInteractiveMSER(source, target, parent.newtree,
					parent.thirdDimension);

			parent.FittedLineBeads = FindbeadsVia.BeadfindingMethod(target, newbeadMser, parent.jpb, parent.initialpsf,
					parent.Intensityratio, parent.Inispacing, 1, 1);

			parent.AllFittedLineBeads.addAll(parent.FittedLineBeads);

		}

		return null;
	}

	@Override
	protected void done() {
		try {

			DrawPoints draw = new DrawPoints();
			if (parent.AllFittedLineBeads.size() > 1)
				draw.drawLinePoints(parent.AllFittedLineBeads);
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
