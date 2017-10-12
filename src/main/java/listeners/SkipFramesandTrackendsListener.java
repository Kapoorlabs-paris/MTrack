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

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannelBasic;
import swingClasses.ProgressSkip;

public class SkipFramesandTrackendsListener implements ActionListener {
	
	
	
      final Interactive_MTDoubleChannel parent;
      final Interactive_MTDoubleChannelBasic child;
	
	public SkipFramesandTrackendsListener(final Interactive_MTDoubleChannel parent, final Interactive_MTDoubleChannelBasic child){
	
		this.parent = parent;
		this.child = child;
	}
public SkipFramesandTrackendsListener(final Interactive_MTDoubleChannel parent){
		
		this.parent = parent;
		this.child = null;
	}
	
	@Override
	public void actionPerformed(final ActionEvent arg0) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				goSkip();

			}

		});
	}
	
	public void goSkip() {

		parent.jpb.setIndeterminate(false);

		parent.jpb.setMaximum(parent.max);
		parent.panel.add(parent.label);
		parent.panel.add(parent.jpb);
		parent.frame.add(parent.panel);
		parent.frame.pack();
		parent.frame.setSize(200, 100);
		if (child == null)
		parent.frame.setLocationRelativeTo(parent.panelCont);
		else
		parent.frame.setLocationRelativeTo(child.panelCont);	
		parent.frame.setVisible(true);

		
		if (child!=null){
			ProgressSkip trackMT = new ProgressSkip(parent, child);
			trackMT.execute();
			
		}
		else{
		
		ProgressSkip trackMT = new ProgressSkip(parent);
		trackMT.execute();
		}
	}
}
