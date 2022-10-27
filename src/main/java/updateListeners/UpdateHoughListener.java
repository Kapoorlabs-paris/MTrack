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
package updateListeners;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JOptionPane;

import interactiveMT.Interactive_MTDoubleChannel;
import listeners.DowatershedListener;

import listeners.ThresholdHoughListener;

public class UpdateHoughListener implements ItemListener {
	
	
final Interactive_MTDoubleChannel parent;
	
	
	public UpdateHoughListener(final Interactive_MTDoubleChannel parent){
	
		this.parent = parent;
	}
	
	@Override
	public void itemStateChanged(final ItemEvent arg0) {
		boolean oldState = parent.FindLinesViaHOUGH;

		if (arg0.getStateChange() == ItemEvent.DESELECTED)
			parent.FindLinesViaHOUGH = false;
		else if (arg0.getStateChange() == ItemEvent.SELECTED) {
			parent.FindLinesViaMSER = false;
			parent.FindLinesViaHOUGH = true;
			parent.FindLinesViaMSERwHOUGH = false;
			parent.UpdateHough();
			

		}

	}
	
	


	



}
