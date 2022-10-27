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

import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import ij.IJ;
import interactiveMT.Interactive_MTSingleChannel;

public class SingleProgressTrack extends SwingWorker<Void, Void> {

final Interactive_MTSingleChannel parent;
final int starttime;
final int endtime;
	
	public SingleProgressTrack(final Interactive_MTSingleChannel parent, final int starttime, final int endtime){
	
		this.parent = parent;
		this.starttime = starttime;
		this.endtime = endtime;
	}
	
	
	
	
	@Override
	protected Void doInBackground() throws Exception {

		int next = parent.starttime;
		if(next == 1)
			next = 2;
		
		SingleTrack newtrack = new SingleTrack(parent);
		newtrack.Trackobject(next);

		return null;
	}
	@Override
	protected void done() {
		try {
			parent.jpb.setIndeterminate(false);
			get();
			parent.frame.dispose();
			IJ.log("Tracking Done and track files written in the chosen folder");

		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}

	}
	
	

	

}
