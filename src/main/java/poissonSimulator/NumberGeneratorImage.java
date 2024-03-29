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
package poissonSimulator;

import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

//import org.uncommons.maths.number.NumberGenerator;

	/**
	 * Helper class for the poisson process
	 * 
	 * This software is free software: you can redistribute it and/or modify
	 * it under the terms of the GNU General Public License as published by
	 * the Free Software Foundation, either version 2 of the License, or
	 * (at your option) any later version.
	 * This software is distributed in the hope that it will be useful,
	 * but WITHOUT ANY WARRANTY; without even the implied warranty of
	 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	 * GNU General Public License for more details.
	 * You should have received a copy of the GNU General Public License
	 * along with this software.  If not, see <http://www.gnu.org/licenses/>.
	 * 
	 * @author Stephan Preibisch (stephan.preibisch@gmx.de)
	 */
	public class NumberGeneratorImage< T extends RealType< T > > implements NumberGenerator< Double >
	{
		final Cursor< T > cursor;
		final double multiplicativeFactor;
		
		public NumberGeneratorImage( final RandomAccessibleInterval<T> image, final double multiplicativeFactor )
		{
			this.cursor = Views.iterable( image ).cursor();
			this.multiplicativeFactor = multiplicativeFactor;
		}
		
		/**
		 * Otherwise it gets out of sync for some reason
		 */
		public void fwd()
		{
			cursor.fwd();
		}
		
		public void reset()
		{
			cursor.reset();
		}
		
		@Override
		public Double nextValue()
		{
			return cursor.get().getRealDouble() * multiplicativeFactor;
		}
	}

