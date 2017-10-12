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
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.view.mxCellState;

public class mxCylinderShape extends mxBasicShape
{

	/**
	 * Draws a cylinder for the given parameters.
	 */
	public void paintShape(mxGraphics2DCanvas canvas, mxCellState state)
	{
		Rectangle rect = state.getRectangle();
		int x = rect.x;
		int y = rect.y;
		int w = rect.width;
		int h = rect.height;
		int h4 = h / 4;
		int h2 = h4 / 2;
		int r = w;

		// Paints the background
		if (configureGraphics(canvas, state, true))
		{
			Area area = new Area(new Rectangle(x, y + h4 / 2, r, h - h4));
			area.add(new Area(new Rectangle(x, y + h4 / 2, r, h - h4)));
			area.add(new Area(new Ellipse2D.Float(x, y, r, h4)));
			area.add(new Area(new Ellipse2D.Float(x, y + h - h4, r, h4)));

			canvas.fillShape(area, hasShadow(canvas, state));
		}

		// Paints the foreground
		if (configureGraphics(canvas, state, false))
		{
			canvas.getGraphics().drawOval(x, y, r, h4);
			canvas.getGraphics().drawLine(x, y + h2, x, y + h - h2);
			canvas.getGraphics().drawLine(x + w, y + h2, x + w, y + h - h2);
			// TODO: Use QuadCurve2D.Float() for painting the arc
			canvas.getGraphics().drawArc(x, y + h - h4, r, h4, 0, -180);
		}
	}

}
