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

public final class KalmanIndexedlength {


		public final int currentLabel;
		public final int seedLabel;
		public final int framenumber;
		public final double ds;
		public final double lineintensity;
		public final double background;
		public final double[] currentpos;
		public final double[] fixedpos;
		public final double slope;
		public final double intercept;
		public final double originalslope;
		public final double originalintercept;
		public final double Curvature;
		public final double Inflection;
		public final double[] originalds;
		

		public KalmanIndexedlength(final int currentLabel, final int seedLabel, final int framenumber,
				final double ds, final double lineintensity, final double background,
				final double[] currentpos, final double[] fixedpos, 
				final double slope, final double intercept, final double originalslope, final double originalintercept, final double[] originalds) {
			this.currentLabel = currentLabel;
			this.seedLabel = seedLabel;
			this.framenumber = framenumber;
			this.ds = ds;
			this.lineintensity = lineintensity;
			this.background = background;
			this.currentpos = currentpos;
			this.fixedpos = fixedpos;
			this.slope = slope;
			this.intercept = intercept;
			this.originalslope = originalslope;
			this.originalintercept = originalintercept;
			this.originalds = originalds;
			this.Curvature = 0;
			this.Inflection = 0;

			
		}

		public KalmanIndexedlength(final int currentLabel, final int seedLabel, final int framenumber,
				final double ds, final double lineintensity, final double background,
				final double[] currentpos, final double[] fixedpos, 
				final double slope, final double intercept, final double originalslope, final double originalintercept, final double Curvature,
				final double Inflection, final double[] originalds) {
			this.currentLabel = currentLabel;
			this.seedLabel = seedLabel;
			this.framenumber = framenumber;
			this.ds = ds;
			this.lineintensity = lineintensity;
			this.background = background;
			this.currentpos = currentpos;
			this.fixedpos = fixedpos;
			this.slope = slope;
			this.intercept = intercept;
			this.originalslope = originalslope;
			this.originalintercept = originalintercept;
			this.Curvature = Curvature;
			this.Inflection = Inflection;
			this.originalds = originalds;

			
		}
		
		
	}

	

