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
package MTObjects;

	
	public final class Objprop {

        public  int Label;
		public  double diameter;
		public double[] sigma;
		public  double totalintensity;
		public double[] location;
		public double corr;
		public double noise;
		public double Circularity;
	

		public Objprop(final int Label, final double diameter,final double totalintensity, final double Circularity) {
			this.Label = Label;
			this.diameter = diameter;
			this.totalintensity = totalintensity;
			this.Circularity = Circularity;
			

		}
		
		public Objprop(final int Label, final double diameter, final double[] location, final double[] sigma, 
				final double corr, final double noise,
				final double totalintensity, final double Circularity){
			
			this.Label = Label;
			this.sigma = sigma;
			this.totalintensity = totalintensity;
			this.location = location;
			this.diameter = diameter;
			this.corr = corr;
			this.noise = noise;
			this.Circularity = Circularity;
			
			
			
		}
		
		
		
	}



