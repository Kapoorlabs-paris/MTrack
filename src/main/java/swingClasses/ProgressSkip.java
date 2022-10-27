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
import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import ij.IJ;
import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannelBasic;
import mt.RansacFileChooser;
import mt.listeners.InteractiveRANSAC;
import updateListeners.FinalPoint;

public class ProgressSkip extends SwingWorker<Void, Void> {

final Interactive_MTDoubleChannel parent;
final Interactive_MTDoubleChannelBasic child;
	
	public ProgressSkip(final Interactive_MTDoubleChannel parent){
	
		this.parent = parent;
		this.child = null;
	}
	
	public ProgressSkip(final Interactive_MTDoubleChannel parent, final Interactive_MTDoubleChannelBasic child){
		
		this.parent = parent;
		this.child = child;
	}
	
	
	@Override
	protected Void doInBackground() throws Exception {

		
		
       

		int next = parent.starttime;

		if (next < 2)
			next = 2;

		
		
		Track newtrack = new Track(parent);
		newtrack.Trackobject(next);
		

		return null;

	}

	@Override
	protected void done() {
		try {
			parent.jpb.setIndeterminate(false);
			get();
			parent.frame.dispose();
			
			if (child!=null){
				
				child.panelNext.removeAll();
				
				
				child.controlnext.add(new JButton(new AbstractAction("Enter RANSAC stage\u22b3") {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {
						new InteractiveRANSAC().run(null);
						
					}
				}));

				child.panelNext.add(child.controlnext, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

				child.controlnext.setVisible(true);

				child.panelSecond.add(child.panelNext, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

			
				child.panelSecond.validate();
				
			}
			if (child == null){
				parent.panelNext.removeAll();
				
				
				
				parent.controlprevious.add(new JButton(new AbstractAction("Enter RANSAC stage\u22b3") {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {
						new InteractiveRANSAC().run(null);
						
					}
				}));

				parent.panelPrevious.add(parent.controlprevious,  new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			
				
				parent.panelFourth.add(parent.panelPrevious,  new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
				parent.panelFourth.validate();
				
			}
			
	IJ.log("Tracking Done and track files written in the chosen folder"  + parent.userfile);
		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}

	}
	
}
