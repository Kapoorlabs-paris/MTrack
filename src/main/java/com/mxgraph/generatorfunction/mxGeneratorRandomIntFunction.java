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
package com.mxgraph.generatorfunction;

import com.mxgraph.view.mxCellState;

/**
 * @author Mate
 * A generator random cost function
 * It will generate random integer edge weights in the range of (<b>minWeight</b>, <b>maxWeight</b>) and rounds the values to <b>roundToDecimals</b>
 */
public class mxGeneratorRandomIntFunction extends mxGeneratorFunction
{
	private double maxWeight = 10;

	private double minWeight = 0;

	public mxGeneratorRandomIntFunction(double minWeight, double maxWeight)
	{
		setWeightRange(minWeight, maxWeight);
	};

	public double getCost(mxCellState state)
	{
		//assumed future parameters
		//		mxGraph graph = state.getView().getGraph();
		//		Object cell = state.getCell();

		if (minWeight == maxWeight)
		{
			return minWeight;
		}

		double currValue = minWeight + Math.round((Math.random() * (maxWeight - minWeight)));
		return currValue;
	};

	public double getMaxWeight()
	{
		return maxWeight;
	};

	public void setWeightRange(double minWeight, double maxWeight)
	{
		this.maxWeight = Math.round(Math.max(minWeight, maxWeight));
		this.minWeight = Math.round(Math.min(minWeight, maxWeight));
	};

	public double getMinWeight()
	{
		return minWeight;
	};
};
