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
package ransacBatch;

import ij.Prefs;
import mt.listeners.InteractiveRANSAC;

public class CreateRanfile {

	final InteractiveRANSAC parent;

	public CreateRanfile(final InteractiveRANSAC parent) {

		this.parent = parent;
	}

	

	public void RecordParent() {


		Prefs.set("MaxError.double", parent.maxError);
		Prefs.set("MinPoints.double", parent.minInliers);

		Prefs.set("MaxGap.double", parent.maxDist);
		Prefs.set("Rescue.double", parent.restolerance);
		Prefs.set("Timepoint.double", parent.tptolerance);
		Prefs.set("Linearity.double", parent.lambda);
		Prefs.set("Minslope.double", parent.minSlope);
		Prefs.set("Maxslope.double", parent.maxSlope);
		Prefs.set("DetectCat.boolean", parent.detectCatastrophe);
		Prefs.set("MinDist.double", parent.minDistanceCatastrophe);
		Prefs.set("Functionchoice.int", parent.functionChoice);
		Prefs.set("numTp.int", parent.numTimepoints);
		
		Prefs.savePreferences();

		 System.exit(1);

	}

}
