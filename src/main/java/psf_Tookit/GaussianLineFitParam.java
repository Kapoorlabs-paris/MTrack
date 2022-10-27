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
package psf_Tookit;

import net.imglib2.Localizable;

public class GaussianLineFitParam {

	public final double[] locationA;
	public final double[] locationB;
	public final double Amplitude;
	public final double[] Sigma;
	public final double Background;
	

	public GaussianLineFitParam(final double[] locationA, final double[] locationB, final double Amplitude, final double[] Sigma, final double Background) {

		this.locationA = locationA;
		this.locationB = locationB;
		this.Amplitude = Amplitude;
		this.Sigma = Sigma;
		
		this.Background = Background;

	}

}
