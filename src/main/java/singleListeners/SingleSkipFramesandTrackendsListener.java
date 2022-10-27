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

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTSingleChannel;
import interactiveMT.Interactive_MTSingleChannelBasic;
import swingClasses.ProgressSkip;
import swingClasses.SingleProgressSkip;

public class SingleSkipFramesandTrackendsListener implements ActionListener {
	
	
	
      final Interactive_MTSingleChannel parent;
      final Interactive_MTSingleChannelBasic child;
      final int starttime;
      final int endtime;
	
      public SingleSkipFramesandTrackendsListener(final Interactive_MTSingleChannel parent, final int starttime, final int endtime){
    		
  		this.parent = parent;
  		this.child = null;
  		this.starttime = starttime;
  		this.endtime = endtime;
  	}
      
	public SingleSkipFramesandTrackendsListener(final Interactive_MTSingleChannel parent, final Interactive_MTSingleChannelBasic child, final int starttime, final int endtime){
	
		this.parent = parent;
		this.child = child;
		this.starttime = starttime;
		this.endtime = endtime;
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
		System.out.println(parent.addToName);
		
		if (child!=null){
			SingleProgressSkip trackMT = new SingleProgressSkip(parent, child);
			trackMT.execute();
			
		}
		else{
		
		SingleProgressSkip trackMT = new SingleProgressSkip(parent);
		trackMT.execute();
		}
		
		

	}
}
