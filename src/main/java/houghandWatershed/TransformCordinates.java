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
package houghandWatershed;

import net.imglib2.type.numeric.RealType;

public class TransformCordinates {

	public static <T extends RealType<T>> double[] transformfwd(double[] location, double[] size, double[] min,
			double[] max) {

		int n = location.length;

		double[] delta = new double[n];

		final double[] realpos = new double[n];

		for (int d = 0; d < n; ++d){
			
			delta[d] = (max[d] - min[d]) / size[d];

			realpos[d] = location[d] * delta[d] + min[d];
		}
		return realpos;

	}

	public static <T extends RealType<T>> double[] transformback(double[] location, double[] size, double[] min,
			double[] max) {

		int n = location.length;

		double[] delta = new double[n];

		final double[] realpos = new double[n];

		for (int d = 0; d < n; ++d){
			
			delta[d] = (max[d] - min[d]) / size[d];
		    
			realpos[d] = (location[d] - min[d]) / delta[d];
		}
		return realpos;

	}
	
	public static <T extends RealType<T>> double transformsinglefwd(double location, double size, double min, double max){
		
		double delta;
		final double realpos;
		
		delta = (max - min) / size;
		
		realpos = location * delta - min;
		
		return realpos;
	}
	
public static <T extends RealType<T>> double transformsingleback(double location, double size, double min, double max){
		
		double delta;
		final double realpos;
		
		delta = (max - min) / size;
		
		realpos = (location  - min)/delta;
		
		return realpos;
	}

}
