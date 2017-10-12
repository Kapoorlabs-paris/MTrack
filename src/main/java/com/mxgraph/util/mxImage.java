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
 * Copyright (c) 2007, Gaudenz Alder
 */
package com.mxgraph.util;

import java.io.Serializable;

/**
 * Implements a 2-dimensional point with double precision coordinates.
 */
public class mxImage implements Serializable, Cloneable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8541229679513497585L;

	/**
	 * Holds the path or URL for the image.
	 */
	protected String src;

	/**
	 * Holds the image width and height.
	 */
	protected int width, height;

	/**
	 * Constructs a new point at (0, 0).
	 */
	public mxImage(String src, int width, int height)
	{
		this.src = src;
		this.width = width;
		this.height = height;
	}

	/**
	 * @return the src
	 */
	public String getSrc()
	{
		return src;
	}

	/**
	 * @param src the src to set
	 */
	public void setSrc(String src)
	{
		this.src = src;
	}

	/**
	 * @return the width
	 */
	public int getWidth()
	{
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(int width)
	{
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public int getHeight()
	{
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(int height)
	{
		this.height = height;
	}

}
