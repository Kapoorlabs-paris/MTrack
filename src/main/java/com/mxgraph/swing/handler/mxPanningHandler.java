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
 * Copyright (c) 2008, Gaudenz Alder
 */
package com.mxgraph.swing.handler;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxMouseAdapter;

/**
 * 
 */
public class mxPanningHandler extends mxMouseAdapter
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7969814728058376339L;

	/**
	 * 
	 */
	protected mxGraphComponent graphComponent;
	
	/**
	 * 
	 */
	protected boolean enabled = true;

	/**
	 * 
	 */
	protected transient Point start;

	/**
	 * 
	 * @param graphComponent
	 */
	public mxPanningHandler(mxGraphComponent graphComponent)
	{
		this.graphComponent = graphComponent;

		graphComponent.getGraphControl().addMouseListener(this);
		graphComponent.getGraphControl().addMouseMotionListener(this);
	}

	/**
	 * 
	 */
	public boolean isEnabled()
	{
		return enabled;
	}

	/**
	 * 
	 */
	public void setEnabled(boolean value)
	{
		enabled = value;
	}

	/**
	 * 
	 */
	public void mousePressed(MouseEvent e)
	{
		if (isEnabled() && !e.isConsumed() && graphComponent.isPanningEvent(e)
				&& !e.isPopupTrigger())
		{
			start = e.getPoint();
		}
	}

	/**
	 * 
	 */
	public void mouseDragged(MouseEvent e)
	{
		if (!e.isConsumed() && start != null)
		{
			int dx = e.getX() - start.x;
			int dy = e.getY() - start.y;

			Rectangle r = graphComponent.getViewport().getViewRect();

			int right = r.x + ((dx > 0) ? 0 : r.width) - dx;
			int bottom = r.y + ((dy > 0) ? 0 : r.height) - dy;

			graphComponent.getGraphControl().scrollRectToVisible(
					new Rectangle(right, bottom, 0, 0));

			e.consume();
		}
	}

	/**
	 * 
	 */
	public void mouseReleased(MouseEvent e)
	{
		if (!e.isConsumed() && start != null)
		{
			int dx = Math.abs(start.x - e.getX());
			int dy = Math.abs(start.y - e.getY());

			if (graphComponent.isSignificant(dx, dy))
			{
				e.consume();
			}
		}

		start = null;
	}

	/**
	 * Whether or not panning is currently active
	 * @return Whether or not panning is currently active
	 */
	public boolean isActive()
	{
		return (start != null);
	}
}
