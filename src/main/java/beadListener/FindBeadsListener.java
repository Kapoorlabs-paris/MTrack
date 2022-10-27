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
package beadListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import beadFinder.ProgressBeads;
import interactiveMT.Interactive_PSFAnalyze;
import psf_Tookit.GaussianFitParam;
import swingClasses.ProgressSeeds;

public class FindBeadsListener implements ActionListener {
	
	
	final Interactive_PSFAnalyze parent;
	
	public FindBeadsListener(final Interactive_PSFAnalyze parent){
		
		this.parent = parent;
		
	}
	
	@Override
	public void actionPerformed(final ActionEvent arg0) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				goBeads();

			}

		});

	}
	
	
	
	public void goBeads() {
		
		parent.jpb.setIndeterminate(false);

		parent.jpb.setMaximum(parent.max);
		parent.panel.add(parent.label);
		parent.panel.add(parent.jpb);
		parent.frame.add(parent.panel);
		parent.frame.pack();
		parent.frame.setSize(200, 100);
		parent.frame.setLocationRelativeTo(parent.panelCont);
		parent.frame.setVisible(true);

		ProgressBeads fitbeads = new ProgressBeads(parent);
		fitbeads.execute();
		
		
		
	}
	
	

}
