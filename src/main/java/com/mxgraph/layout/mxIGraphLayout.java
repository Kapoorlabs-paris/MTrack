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
/**
 * Copyright (c) 2007, Gaudenz Alder
 */
package com.mxgraph.layout;

/**
 * Defines the requirements for an object that implements a graph layout.
 */
public interface mxIGraphLayout
{

	/**
	 * Executes the layout for the children of the specified parent.
	 * 
	 * @param parent Parent cell that contains the children to be layed out.
	 */
	void execute(Object parent);

	/**
	 * Notified when a cell is being moved in a parent that has automatic
	 * layout to update the cell state (eg. index) so that the outcome of the
	 * layout will position the vertex as close to the point (x, y) as
	 * possible.
	 * 
	 * @param cell Cell which is being moved.
	 * @param x X-coordinate of the new cell location.
	 * @param y Y-coordinate of the new cell location.
	 */
	void moveCell(Object cell, double x, double y);

}
