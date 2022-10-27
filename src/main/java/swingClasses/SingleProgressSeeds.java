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
package swingClasses;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import LineModels.UseLineModel.UserChoiceModel;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import interactiveMT.Interactive_MTSingleChannel;
import interactiveMT.Interactive_MTSingleChannelBasic;
import lineFinder.FindlinesVia;
import lineFinder.LinefinderInteractiveHough;
import lineFinder.LinefinderInteractiveMSER;
import lineFinder.LinefinderInteractiveMSERwHough;
import mpicbg.imglib.util.Util;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import singleListeners.SingleSecondPanel;
import singleListeners.SingleThirdPanel;

public class SingleProgressSeeds extends SwingWorker<Void, Void> {

	
final Interactive_MTSingleChannel parent;
final Interactive_MTSingleChannelBasic child;
	
	public SingleProgressSeeds(final Interactive_MTSingleChannel parent){
	
		this.parent = parent;
		this.child = null;
	}

	public SingleProgressSeeds(final Interactive_MTSingleChannel parent, final Interactive_MTSingleChannelBasic child){
		
		this.parent = parent;
		this.child = child;
	}
	
	
	@Override
	protected Void doInBackground() throws Exception {

	
		RandomAccessibleInterval<FloatType> groundframe = parent.currentimg;
		RandomAccessibleInterval<FloatType> groundframepre = parent.currentPreprocessedimg;

		if (parent.FindLinesViaMSER) {

		
				LinefinderInteractiveMSER newlineMser = new LinefinderInteractiveMSER(groundframe, groundframepre,
						parent.newtree, parent.thirdDimension);

				parent.PrevFrameparam = FindlinesVia.LinefindingMethod(groundframe, groundframepre,
						parent.thirdDimension, parent.psf, newlineMser, UserChoiceModel.Line, parent.Domask, parent.Intensityratio, parent.Inispacing,
						parent.jpb);
		

			

		}

		if (parent.FindLinesViaHOUGH) {

			
				LinefinderInteractiveHough newlineHough = new LinefinderInteractiveHough(groundframe,
						groundframepre, parent.intimg, parent.Maxlabel, parent.thetaPerPixel, parent.rhoPerPixel, parent.thirdDimension, parent.jpb);

				parent.PrevFrameparam = FindlinesVia.LinefindingMethod(groundframe, groundframepre,
						parent.thirdDimension, parent.psf, newlineHough, UserChoiceModel.Line, parent.Domask, parent.Intensityratio, parent.Inispacing,
						parent.jpb);
			

			
		}

		if (parent.FindLinesViaMSERwHOUGH) {
			
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

			if (parent.PrevFrameparam.getB() != null) {

				if (Math.abs(Math.round(parent.PrevFrameparam.getA().get(index).currentpos[0]
						- parent.PrevFrameparam.getB().get(index).currentpos[0])) > 0
						&& Math.abs(Math.round(parent.PrevFrameparam.getA().get(index).currentpos[1]
								- parent.PrevFrameparam.getB().get(index).currentpos[1])) > 0) {
			parent.Seedroi = new OvalRoi(Util.round(parent.PrevFrameparam.getA().get(index).currentpos[0] - parent.radiusseed),
					Util.round(parent.PrevFrameparam.getA().get(index).currentpos[1] - parent.radiusseed), Util.round(2 * parent.radiusseed), Util.round(2 * parent.radiusseed));
			parent.Seedroi.setStrokeColor(parent.colorConfirm);
			parent.Seedroi.setStrokeWidth(0.8);

			
			
			parent.AllSeedrois.add(parent.Seedroi);
			o.add(parent.Seedroi);

				}
			}
		}

		for (int index = 0; index < parent.PrevFrameparam.getB().size(); ++index) {
			if (parent.PrevFrameparam.getA() != null) {

				if (Math.abs(Math.round(parent.PrevFrameparam.getA().get(index).currentpos[0]
						- parent.PrevFrameparam.getB().get(index).currentpos[0])) > 0
						&& Math.abs(Math.round(parent.PrevFrameparam.getA().get(index).currentpos[1]
								- parent.PrevFrameparam.getB().get(index).currentpos[1])) > 0) {
			parent.Seedroi = new OvalRoi(Util.round(parent.PrevFrameparam.getB().get(index).currentpos[0] - parent.radiusseed),
					Util.round(parent.PrevFrameparam.getB().get(index).currentpos[1] - parent.radiusseed), Util.round(2 * parent.radiusseed), Util.round(2 * parent.radiusseed));
			parent.Seedroi.setStrokeColor(parent.colorConfirm);
			parent.Seedroi.setStrokeWidth(0.8);
			
			
			
			parent.AllSeedrois.add(parent.Seedroi);
			o.add(parent.Seedroi);

				}
				
			}
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
			
			
			if (child!=null){
				child.controlnext.removeAll();
				child.controlprevious.removeAll();
				
				
				JPanel controlprevpanel = new JPanel();
				JPanel prevpanel = new JPanel();
				controlprevpanel.add(new JButton(new AbstractAction("Next\u22b3") {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {
						CardLayout cl = (CardLayout) child.panelCont.getLayout();
						cl.previous(child.panelCont);
					}
				}));
				
				prevpanel.add(controlprevpanel,  new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			
				controlprevpanel.setVisible(true);
				
				child.panelFirst.add(prevpanel,  new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
				child.panelFirst.validate();
			
				
					SingleSecondPanel paintsecond = new SingleSecondPanel(parent, child);
					paintsecond.Paint();
			}
			if (child==null){
			
			
				parent.controlnext.removeAll();
				parent.controlprevious.removeAll();
				parent.controlnext.add(new JButton(new AbstractAction("\u22b2Prev") {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {
						CardLayout cl = (CardLayout) parent.panelCont.getLayout();

						cl.previous(parent.panelCont);
					}
				}));
			 
				parent.controlnext.add(new JButton(new AbstractAction("Next\u22b3") {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {
						CardLayout cl = (CardLayout) parent.panelCont.getLayout();
						cl.next(parent.panelCont);
						
						parent.Mserparam.setLayout(null);
						parent.Houghparam.setLayout(null);
						parent.MserwHoughparam.setLayout(null);
						SingleThirdPanel paintthird = new SingleThirdPanel(parent);
						paintthird.Paint();
					}
				}));

				parent.panelNext.add(parent.controlnext,  new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			
				parent.controlnext.setVisible(true);
				
				parent.panelSecond.add(parent.panelNext,  new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			
				parent.panelSecond.validate();
				
				JPanel controlprevpanel = new JPanel();
				JPanel prevpanel = new JPanel();
				controlprevpanel.add(new JButton(new AbstractAction("Next\u22b3") {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {
						CardLayout cl = (CardLayout) parent.panelCont.getLayout();
						cl.next(parent.panelCont);
					}
				}));
				
				prevpanel.add(controlprevpanel,  new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			
				controlprevpanel.setVisible(true);
				
				parent.panelFirst.add(prevpanel,  new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
				parent.panelFirst.validate();
		
			
			
			
			
			}
			final int maxSeed = parent.PrevFrameparam.getA().get(parent.PrevFrameparam.getA().size() - 1).seedLabel;
			parent.nextseed = maxSeed;
			parent.preprocessedimp.getCanvas().removeMouseListener(parent.ml);
			parent.newends.markendnew();
			parent.frame.dispose();
		}
		
			// JOptionPane.showMessageDialog(jpb.getParent(), "End Points
			// found and overlayed", "Success",
			// JOptionPane.INFORMATION_MESSAGE);
		 catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}

	}
}




