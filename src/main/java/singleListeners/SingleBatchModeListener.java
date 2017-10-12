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
package singleListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import initialization.CreateINIfile;
import initialization.SingleCreateINIfile;
import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTSingleChannel;


	public class SingleBatchModeListener implements ActionListener {
		

		final Interactive_MTSingleChannel parent;
		
		public SingleBatchModeListener(final Interactive_MTSingleChannel parent) {

			this.parent = parent;
			
		}
		

		@Override
		public void actionPerformed(final ActionEvent arg0) {
			
			SingleCreateINIfile recordparam = new SingleCreateINIfile(parent);
			recordparam.RecordParent();
			
			

	}
	
}
