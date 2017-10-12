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
package labeledObjects;

import java.util.ArrayList;

// Objects containing the label and the correspoing rho and theta information
	public  final class Lineobjects {
		public final int Label;
		
        public ArrayList<double[]> slopeandintercept;  		
		public final long [] boxmin;
		public final long [] boxmax;
		public double[] singleslopeandintercept;
		

		public Lineobjects(
				final int Label,
				final ArrayList<double[]> slopeandintercept,
				
				final long[] minCorner, 
				final long[] maxCorner
				) {
			this.Label = Label;
		    this.slopeandintercept = slopeandintercept;
			this.boxmin = minCorner;
			this.boxmax = maxCorner;
			
			
		}
		
		public Lineobjects(
				final int Label,
				double[] singleslopeandintercept,
				
				final long[] minCorner, 
				final long[] maxCorner
				) {
			this.Label = Label;
		    this.singleslopeandintercept = singleslopeandintercept;
			this.boxmin = minCorner;
			this.boxmax = maxCorner;
			
			
		}
		
	}
