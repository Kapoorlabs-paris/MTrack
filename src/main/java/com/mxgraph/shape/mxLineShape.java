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

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxCellState;

public class mxLineShape extends mxBasicShape
{

	/**
	 * 
	 */
	public void paintShape(mxGraphics2DCanvas canvas, mxCellState state)
	{
		if (configureGraphics(canvas, state, false))
		{
			boolean rounded = mxUtils.isTrue(state.getStyle(),
					mxConstants.STYLE_ROUNDED, false)
					&& canvas.getScale() > mxConstants.MIN_SCALE_FOR_ROUNDED_LINES;

			canvas.paintPolyline(createPoints(canvas, state), rounded);
		}
	}

	/**
	 * 
	 */
	public mxPoint[] createPoints(mxGraphics2DCanvas canvas, mxCellState state)
	{
		String direction = mxUtils.getString(state.getStyle(),
				mxConstants.STYLE_DIRECTION, mxConstants.DIRECTION_EAST);

		mxPoint p0, pe;

		if (direction.equals(mxConstants.DIRECTION_EAST)
				|| direction.equals(mxConstants.DIRECTION_WEST))
		{
			double mid = state.getCenterY();
			p0 = new mxPoint(state.getX(), mid);
			pe = new mxPoint(state.getX() + state.getWidth(), mid);
		}
		else
		{
			double mid = state.getCenterX();
			p0 = new mxPoint(mid, state.getY());
			pe = new mxPoint(mid, state.getY() + state.getHeight());
		}

		mxPoint[] points = new mxPoint[2];
		points[0] = p0;
		points[1] = pe;

		return points;
	}

}
