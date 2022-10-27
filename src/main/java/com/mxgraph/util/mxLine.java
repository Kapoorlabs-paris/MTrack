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
 * Copyright (c) 2007-2010, Gaudenz Alder, David Benson
 */
package com.mxgraph.util;

import java.awt.geom.Line2D;

/**
 * Implements a line with double precision coordinates.
 */

public class mxLine extends mxPoint
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4730972599169158546L;
	/**
	 * The end point of the line
	 */
	protected mxPoint endPoint;

	/**
	 * Creates a new line
	 */
	public mxLine(mxPoint startPt, mxPoint endPt)
	{
		this.setX(startPt.getX());
		this.setY(startPt.getY());
		this.endPoint = endPt;
	}
	
	/**
	 * Creates a new line
	 */
	public mxLine(double startPtX, double startPtY, mxPoint endPt)
	{
		x = startPtX;
		y = startPtY;
		this.endPoint = endPt;
	}

	/**
	 * Returns the end point of the line.
	 * 
	 * @return Returns the end point of the line.
	 */
	public mxPoint getEndPoint()
	{
		return this.endPoint;
	}

	/**
	 * Sets the end point of the rectangle.
	 * 
	 * @param value The new end point of the line
	 */
	public void setEndPoint(mxPoint value)
	{
		this.endPoint = value;
	}

	/**
	 * Sets the start and end points.
	 */
	public void setPoints(mxPoint startPt, mxPoint endPt)
	{
		this.setX(startPt.getX());
		this.setY(startPt.getY());
		this.endPoint = endPt;
	}
	
	/**
	 * Returns the square of the shortest distance from a point to this line.
	 * The line is considered extrapolated infinitely in both directions for 
	 * the purposes of the calculation.
	 *
	 * @param pt the point whose distance is being measured
	 * @return the square of the distance from the specified point to this line.
	 */
	public double ptLineDistSq(mxPoint pt)
	{
		return new Line2D.Double(getX(), getY(), endPoint.getX(), endPoint
				.getY()).ptLineDistSq(pt.getX(), pt.getY());
	}

	/**
	 * Returns the square of the shortest distance from a point to this 
	 * line segment.
	 *
	 * @param pt the point whose distance is being measured
	 * @return the square of the distance from the specified point to this segment.
	 */
	public double ptSegDistSq(mxPoint pt)
	{
		return new Line2D.Double(getX(), getY(), endPoint.getX(), endPoint
				.getY()).ptSegDistSq(pt.getX(), pt.getY());
	}

}
