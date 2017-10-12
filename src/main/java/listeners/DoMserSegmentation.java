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
package listeners;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import ij.IJ;
import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;
import updateListeners.FinalPoint;
import updateListeners.UpdateMserListener;

public class DoMserSegmentation implements ItemListener {

final Interactive_MTDoubleChannel parent;
	
	
	public DoMserSegmentation(final Interactive_MTDoubleChannel parent){
	
		this.parent = parent;
	}
	
	@Override
	public void itemStateChanged(ItemEvent arg0) {
		if (arg0.getStateChange() == ItemEvent.DESELECTED){
			parent.FindLinesViaMSER = false;
		}
		else if (arg0.getStateChange() == ItemEvent.SELECTED) {
			parent.FindLinesViaMSER = true;
			parent.doMserSegmentation = true;
			
			
			FinalPoint finalpoint = new FinalPoint(parent);
			finalpoint.FinalizeEnds();
			
			parent.thirdDimension = parent.starttime;

			if (parent.thirdDimension > parent.thirdDimensionSize) {
				IJ.log("Max frame number exceeded, moving to last frame instead");
				parent.thirdDimension = parent.thirdDimensionSize;
				parent.CurrentView = util.CopyUtils.getCurrentView(parent.originalimg, parent.thirdDimension,
						parent.thirdDimensionSize);
				parent.CurrentPreprocessedView = util.CopyUtils.getCurrentPreView(parent.originalPreprocessedimg,
						parent.thirdDimension, parent.thirdDimensionSize);
			} else {

				parent.CurrentView = util.CopyUtils.getCurrentView(parent.originalimg, parent.thirdDimension,
						parent.thirdDimensionSize);
				parent.CurrentPreprocessedView = util.CopyUtils.getCurrentPreView(parent.originalPreprocessedimg,
						parent.thirdDimension, parent.thirdDimensionSize);
			}
			parent.updatePreview(ValueChange.THIRDDIM);
			parent.UpdateMser();

/*
			parent.controlnext.removeAll();
			parent.controlnext.add(new JButton(new AbstractAction("\u22b2Prev") {

				
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					CardLayout cl = (CardLayout) parent.panelCont.getLayout();

					cl.previous(parent.panelCont);
				}
			}));
		 
			parent.controlnext.add(new JButton(new AbstractAction("Next\u22b3") {

				
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					CardLayout cl = (CardLayout) parent.panelCont.getLayout();
					cl.next(parent.panelCont);
				}
			}));

			parent.panelNext.add(parent.controlnext,  new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
			parent.controlnext.setVisible(true);
			
			parent.panelThird.add(parent.panelNext,  new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
			parent.panelThird.validate();
			
			*/
		}

	}

}
