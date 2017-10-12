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
/**
 * Copyright (c) 2007-2009, JGraph Ltd
 */
package com.mxgraph.analysis;

import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxCellState;

/**
 * Implements a cost function for the Euclidean length of an edge.
 */
public class mxDistanceCostFunction implements mxICostFunction
{

	/**
	 * Returns the Euclidean length of the edge defined by the absolute
	 * points in the given state or 0 if no points are defined.
	 */
	public double getCost(mxCellState state)
	{
		double cost = 0;
		int pointCount = state.getAbsolutePointCount();

		if (pointCount > 0)
		{
			mxPoint last = state.getAbsolutePoint(0);

			for (int i = 1; i < pointCount; i++)
			{
				mxPoint point = state.getAbsolutePoint(i);
				cost += point.getPoint().distance(last.getPoint());
				last = point;
			}
		}

		return cost;
	}
}
