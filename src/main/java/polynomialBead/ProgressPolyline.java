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
		parent.AllFittedLineBeads.clear();
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
