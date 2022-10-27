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

package singleListeners;

import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import ij.IJ;
import interactiveMT.Interactive_MTSingleChannel;
import interactiveMT.Interactive_MTSingleChannel.ValueChange;
import updateListeners.DefaultModel;
import updateListeners.FinalPoint;
import updateListeners.SingleFinalPoint;


public class SingleSegMethodListener implements ActionListener {

	
	
	final Interactive_MTSingleChannel parent;
	final JComboBox<String> choice;
	
	public SingleSegMethodListener(final Interactive_MTSingleChannel parent, final JComboBox<String> choice){
		
		this.parent = parent;
		this.choice = choice;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		int selectedindex = choice.getSelectedIndex();
		
		parent.controlnext.setVisible(true);
		
          if (selectedindex == 0){
			
			
			parent.FindLinesViaMSER = true;
			parent.doMserSegmentation = true;
			
			
			SingleFinalPoint finalpoint = new SingleFinalPoint(parent);
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


			

		}
		
		
		if (selectedindex == 1){
			
			
			parent.FindLinesViaHOUGH = true;
			parent.doSegmentation = true;
			
			parent.thirdDimension = parent.starttime;
			SingleFinalPoint finalpoint = new SingleFinalPoint(parent);
			finalpoint.FinalizeEnds();

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
			parent.UpdateHough();

			
			
		}
		
		
		
		
		parent.controlnext.removeAll();
		parent.controlprevious.removeAll();
		parent.controlnext.add(new JButton(new AbstractAction("\u22b2Prev") {

			
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout) parent.panelCont.getLayout();

				cl.previous(parent.panelCont);
				parent.Mserparam.setLayout(parent.layout);
				parent.Houghparam.setLayout(parent.layout);
				parent.MserwHoughparam.setLayout(parent.layout);
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
		
		parent.panelThird.add(parent.panelNext,  new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
	
		parent.panelThird.validate();
		JPanel controlprevpanel = new JPanel();
		JPanel prevpanel = new JPanel();
		controlprevpanel.add(new JButton(new AbstractAction("\u22b2Prev") {

			
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout) parent.panelCont.getLayout();

				cl.previous(parent.panelCont);
			
			}
		}));
	 
		controlprevpanel.add(new JButton(new AbstractAction("Next\u22b3") {

			
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout) parent.panelCont.getLayout();
				cl.next(parent.panelCont);
				parent.Mserparam.setLayout(null);
				parent.Houghparam.setLayout(null);
				parent.MserwHoughparam.setLayout(null);
			}
		}));

		prevpanel.add(controlprevpanel,  new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
	
		controlprevpanel.setVisible(true);
		
		parent.panelSecond.add(prevpanel,  new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		parent.panelSecond.validate();
		
		
		parent.Cardframe.pack();
		
		
		
		
		
		
		
		
	}

}
