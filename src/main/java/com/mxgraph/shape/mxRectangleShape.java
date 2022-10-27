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
 * Copyright (c) 2007-2012, JGraph Ltd
 */
package com.mxgraph.shape;

import java.awt.Rectangle;
import java.util.Map;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxCellState;

public class mxRectangleShape extends mxBasicShape
{

	/**
	 * 
	 */
	public void paintShape(mxGraphics2DCanvas canvas, mxCellState state)
	{
		Map<String, Object> style = state.getStyle();

		if (mxUtils.isTrue(style, mxConstants.STYLE_ROUNDED, false))
		{
			Rectangle tmp = state.getRectangle();

			int x = tmp.x;
			int y = tmp.y;
			int w = tmp.width;
			int h = tmp.height;
			int radius = getArcSize(w, h);

			boolean shadow = hasShadow(canvas, state);
			int shadowOffsetX = (shadow) ? mxConstants.SHADOW_OFFSETX : 0;
			int shadowOffsetY = (shadow) ? mxConstants.SHADOW_OFFSETY : 0;

			if (canvas.getGraphics().hitClip(x, y, w + shadowOffsetX,
					h + shadowOffsetY))
			{
				// Paints the optional shadow
				if (shadow)
				{
					canvas.getGraphics().setColor(mxSwingConstants.SHADOW_COLOR);
					canvas.getGraphics().fillRoundRect(
							x + mxConstants.SHADOW_OFFSETX,
							y + mxConstants.SHADOW_OFFSETY, w, h, radius,
							radius);
				}

				// Paints the background
				if (configureGraphics(canvas, state, true))
				{
					canvas.getGraphics().fillRoundRect(x, y, w, h, radius,
							radius);
				}

				// Paints the foreground
				if (configureGraphics(canvas, state, false))
				{
					canvas.getGraphics().drawRoundRect(x, y, w, h, radius,
							radius);
				}
			}
		}
		else
		{
			Rectangle rect = state.getRectangle();

			// Paints the background
			if (configureGraphics(canvas, state, true))
			{
				canvas.fillShape(rect, hasShadow(canvas, state));
			}

			// Paints the foreground
			if (configureGraphics(canvas, state, false))
			{
				canvas.getGraphics().drawRect(rect.x, rect.y, rect.width,
						rect.height);
			}
		}
	}

	/**
	 * Computes the arc size for the given dimension.
	 * 
	 * @param w Width of the rectangle.
	 * @param h Height of the rectangle.
	 * @return Returns the arc size for the given dimension.
	 */
	public int getArcSize(int w, int h)
	{
		int arcSize;

		if (w <= h)
		{
			arcSize = (int) Math.round(h
					* mxConstants.RECTANGLE_ROUNDING_FACTOR);

			if (arcSize > (w / 2))
			{
				arcSize = w / 2;
			}
		}
		else
		{
			arcSize = (int) Math.round(w
					* mxConstants.RECTANGLE_ROUNDING_FACTOR);

			if (arcSize > (h / 2))
			{
				arcSize = h / 2;
			}
		}
		return arcSize;
	}

}
