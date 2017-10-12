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
package com.mxgraph.shape;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.GeneralPath;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.view.mxCellState;

public class mxActorShape extends mxBasicShape
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
		float width = w * 2 / 6;

		GeneralPath path = new GeneralPath();

		path.moveTo(x, y + h);
		path.curveTo(x, y + 3 * h / 5, x, y + 2 * h / 5, x + w / 2, y + 2 * h
				/ 5);
		path.curveTo(x + w / 2 - width, y + 2 * h / 5, x + w / 2 - width, y, x
				+ w / 2, y);
		path.curveTo(x + w / 2 + width, y, x + w / 2 + width, y + 2 * h / 5, x
				+ w / 2, y + 2 * h / 5);
		path.curveTo(x + w, y + 2 * h / 5, x + w, y + 3 * h / 5, x + w, y + h);
		path.closePath();

		return path;
	}

}
