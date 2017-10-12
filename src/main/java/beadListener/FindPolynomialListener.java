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
package beadListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import beadFinder.ProgressBeads;
import interactiveMT.Interactive_PSFAnalyze;
import polynomialBead.ProgressPolyline;

public class FindPolynomialListener implements ActionListener {

	
     final Interactive_PSFAnalyze parent;
	
	public FindPolynomialListener(final Interactive_PSFAnalyze parent){
		
		this.parent = parent;
		
	}
	
	
	
	@Override
	public void actionPerformed(final ActionEvent arg0) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				parent.initialpsf[0] = Float.parseFloat(parent.inputFieldX.getText());
				parent.initialpsf[1] = Float.parseFloat(parent.inputFieldY.getText());
				goPolynomialLine();

			}

		});

	}
	
public void goPolynomialLine() {
		
		parent.jpb.setIndeterminate(false);

		parent.jpb.setMaximum(parent.max);
		parent.panel.add(parent.label);
		parent.panel.add(parent.jpb);
		parent.frame.add(parent.panel);
		parent.frame.pack();
		parent.frame.setSize(200, 100);
		parent.frame.setLocationRelativeTo(parent.panelCont);
		parent.frame.setVisible(true);

		ProgressPolyline fitpoly = new ProgressPolyline(parent);
		fitpoly.execute();
		
		
		
	}
	
}
