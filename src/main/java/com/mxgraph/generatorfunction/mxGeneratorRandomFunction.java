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
package com.mxgraph.generatorfunction;

import com.mxgraph.view.mxCellState;

/**
 * @author Mate
 * A generator random cost function
 * It will generate random (type "double") edge weights in the range of (<b>minWeight</b>, <b>maxWeight</b>) and rounds the values to <b>roundToDecimals</b>
 */
public class mxGeneratorRandomFunction extends mxGeneratorFunction
{
	private double maxWeight = 1;

	private double minWeight = 0;

	private int roundToDecimals = 2;

	public mxGeneratorRandomFunction(double minWeight, double maxWeight, int roundToDecimals)
	{
		setWeightRange(minWeight, maxWeight);
		setRoundToDecimals(roundToDecimals);
	};

	public double getCost(mxCellState state)
	{
		Double edgeWeight = null;

		edgeWeight = Math.random() * (maxWeight - minWeight) + minWeight;
		edgeWeight = (double) Math.round(edgeWeight * Math.pow(10, getRoundToDecimals())) / Math.pow(10, getRoundToDecimals());

		return edgeWeight;
	};

	public double getMaxWeight()
	{
		return maxWeight;
	};

	public void setWeightRange(double minWeight, double maxWeight)
	{
		this.maxWeight = Math.max(minWeight, maxWeight);
		this.minWeight = Math.min(minWeight, maxWeight);
	};

	public double getMinWeight()
	{
		return minWeight;
	};

	public int getRoundToDecimals()
	{
		return roundToDecimals;
	};

	public void setRoundToDecimals(int roundToDecimals)
	{
		this.roundToDecimals = roundToDecimals;
	};
};
