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
package houghandWatershed;

public class Finalfunction extends Finaldistance {

	public Finalfunction(double[] realpoints, double[] funcparamone, double funcparamtwo, double funcparamthree) {

		super(realpoints, funcparamone, funcparamtwo, funcparamthree);

	}

	public Finalfunction(double[] realpoints, double funcparamtwo, double funcparamthree) {

		super(realpoints, funcparamtwo, funcparamthree);

	}

// Shortest distance of a point from a user defined circle
	public double Circlefunctiondist() {

		// funcparamone = center fo the circle, funcparamtwo = radius;

		double distance;

		distance = Math.abs(Math
				.sqrt(Math.pow((realpoints[1] - funcparamone[1]), 2) + Math.pow((realpoints[0] - funcparamone[0]), 2))
				- funcparamtwo);

		return distance;

	}
	// Shortest distance of a point from a user defined line
	public double Linefunctiondist() {

		// funcparamtwo = slope, funcparamthree = intercept along x-axis

		double distance;

		double minX = (realpoints[0] - funcparamtwo * (funcparamthree - realpoints[1]))
				/ (1 + funcparamtwo * funcparamtwo);
		double minY = minX * funcparamtwo + funcparamthree;

		distance = Math.pow((minX - realpoints[0]), 2) + Math.pow((minY - realpoints[1]), 2);

		return Math.sqrt(distance);
	}

	
}
