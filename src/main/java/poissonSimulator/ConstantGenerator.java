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
package poissonSimulator;

public class ConstantGenerator <T extends Number> implements NumberGenerator<T> {

	 private final T constant;

	    /**
	     * Creates a number generator that always returns the same
	     * values.
	     * @param constant The value to be returned by all invocations
	     * of the {@link #nextValue()} method.
	     */
	    public ConstantGenerator(T constant)
	    {
	        this.constant = constant;
	    }

	    /**
	     * @return The constant value specified when the generator was constructed.
	     */
	    public T nextValue()
	    {
	        return constant;
	    }

}
