/*-
 * #%L
 * Microtubule tracker.
 * %%
 * Copyright (C) 2017 MTrack developers.
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
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;
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

public class ProgressSeedsBatch extends SwingWorker<Void, Void> {

	
final Interactive_MTDoubleChannel parent;
	
	
	public ProgressSeedsBatch(final Interactive_MTDoubleChannel parent){
	
		this.parent = parent;
	}
	
	
	@Override
	protected Void doInBackground() throws Exception {

		
		
		
		for (int timeindex = parent.thirdDimensionsliderInit; timeindex < parent.thirdDimensionSize; ++timeindex) {
			parent.thirdDimension = timeindex;
			parent.CurrentPreprocessedView = util.CopyUtils.getCurrentPreView(parent.originalPreprocessedimg, parent.thirdDimension,
					parent.thirdDimensionSize);
			parent.CurrentView = util.CopyUtils.getCurrentView(parent.originalimg, parent.thirdDimension, parent.thirdDimensionSize);
		
		parent.updatePreview(ValueChange.THIRDDIMTrack);
		// add listener to the imageplus slice slider
		
		parent.updatePreview(ValueChange.SHOWMSER);
		
		
		
		RandomAccessibleInterval<FloatType> groundframe = parent.currentimg;
		RandomAccessibleInterval<FloatType> groundframepre = parent.currentPreprocessedimg;

		

		
				LinefinderInteractiveMSER newlineMser = new LinefinderInteractiveMSER(groundframe, groundframepre,
						parent.newtree, parent.thirdDimension);

				parent.PrevFrameparam = FindlinesVia.LinefindingMethod(groundframe, groundframepre,
						parent.thirdDimension, parent.psf, newlineMser, UserChoiceModel.Line, parent.Domask, parent.Intensityratio, parent.Inispacing,
						parent.jpb);
			

			

		

		

		

	

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
		}
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
