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
package com.mxgraph.shape;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.GeneralPath;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.view.mxCellState;

public class mxCloudShape extends mxBasicShape
{

	/**
	 * 
	 */
	public Shape createShape(mxGraphics2DCanvas canvas, mxCellState state)
	{
		Rectangle temp = state.getRectangle();
		int x = temp.x;
		int y = temp.y;
		int w = temp.width;
		int h = temp.height;
		GeneralPath path = new GeneralPath();

		path.moveTo((float) (x + 0.25 * w), (float) (y + 0.25 * h));
		path.curveTo((float) (x + 0.05 * w), (float) (y + 0.25 * h), x,
				(float) (y + 0.5 * h), (float) (x + 0.16 * w),
				(float) (y + 0.55 * h));
		path.curveTo(x, (float) (y + 0.66 * h), (float) (x + 0.18 * w),
				(float) (y + 0.9 * h), (float) (x + 0.31 * w),
				(float) (y + 0.8 * h));
		path.curveTo((float) (x + 0.4 * w), (y + h), (float) (x + 0.7 * w),
				(y + h), (float) (x + 0.8 * w), (float) (y + 0.8 * h));
		path.curveTo((x + w), (float) (y + 0.8 * h), (x + w),
				(float) (y + 0.6 * h), (float) (x + 0.875 * w),
				(float) (y + 0.5 * h));
		path.curveTo((x + w), (float) (y + 0.3 * h), (float) (x + 0.8 * w),
				(float) (y + 0.1 * h), (float) (x + 0.625 * w),
				(float) (y + 0.2 * h));
		path.curveTo((float) (x + 0.5 * w), (float) (y + 0.05 * h),
				(float) (x + 0.3 * w), (float) (y + 0.05 * h),
				(float) (x + 0.25 * w), (float) (y + 0.25 * h));
		path.closePath();

		return path;
	}

}
