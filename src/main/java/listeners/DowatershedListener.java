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
package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;

public class DowatershedListener implements ActionListener {

	
final Interactive_MTDoubleChannel parent;
	
	public DowatershedListener (final Interactive_MTDoubleChannel parent ){
		
		this.parent = parent;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		parent.ShowHough = true;
		parent.updatePreview(ValueChange.SHOWHOUGH);

	}
}
