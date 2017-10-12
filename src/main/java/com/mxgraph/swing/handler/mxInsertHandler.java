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
package com.mxgraph.swing.handler;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxMouseAdapter;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxGraph;

public class mxInsertHandler extends mxMouseAdapter
{

	/**
	 * Reference to the enclosing graph component.
	 */
	protected mxGraphComponent graphComponent;

	/**
	 * Specifies if this handler is enabled. Default is true.
	 */
	protected boolean enabled = true;

	/**
	 * 
	 */
	protected String style;

	/**
	 * 
	 */
	protected Point first;

	/**
	 * 
	 */
	protected float lineWidth = 1;

	/**
	 * 
	 */
	protected Color lineColor = Color.black;

	/**
	 * 
	 */
	protected boolean rounded = false;

	/**
	 * 
	 */
	protected mxRectangle current;

	/**
	 * 
	 */
	protected mxEventSource eventSource = new mxEventSource(this);

	/**
	 * 
	 */
	public mxInsertHandler(mxGraphComponent graphComponent, String style)
	{
		this.graphComponent = graphComponent;
		this.style = style;

		// Installs the paint handler
		graphComponent.addListener(mxEvent.AFTER_PAINT, new mxIEventListener()
		{
			public void invoke(Object sender, mxEventObject evt)
			{
				Graphics g = (Graphics) evt.getProperty("g");
				paint(g);
			}
		});

		// Listens to all mouse events on the rendering control
		graphComponent.getGraphControl().addMouseListener(this);
		graphComponent.getGraphControl().addMouseMotionListener(this);
	}

	/**
	 * 
	 */
	public mxGraphComponent getGraphComponent()
	{
		return graphComponent;
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
	public boolean isStartEvent(MouseEvent e)
	{
		return true;
	}

	/**
	 * 
	 */
	public void start(MouseEvent e)
	{
		first = e.getPoint();
	}

	/**
	 * 
	 */
	public void mousePressed(MouseEvent e)
	{
		if (graphComponent.isEnabled() && isEnabled() && !e.isConsumed()
				&& isStartEvent(e))
		{
			start(e);
			e.consume();
		}
	}

	/**
	 * 
	 */
	public void mouseDragged(MouseEvent e)
	{
		if (graphComponent.isEnabled() && isEnabled() && !e.isConsumed()
				&& first != null)
		{
			mxRectangle dirty = current;

			current = new mxRectangle(first.x, first.y, 0, 0);
			current.add(new mxRectangle(e.getX(), e.getY(), 0, 0));

			if (dirty != null)
			{
				dirty.add(current);
			}
			else
			{
				dirty = current;
			}

			Rectangle tmp = dirty.getRectangle();
			int b = (int) Math.ceil(lineWidth);
			graphComponent.getGraphControl().repaint(tmp.x - b, tmp.y - b,
					tmp.width + 2 * b, tmp.height + 2 * b);

			e.consume();
		}
	}

	/**
	 * 
	 */
	public void mouseReleased(MouseEvent e)
	{
		if (graphComponent.isEnabled() && isEnabled() && !e.isConsumed()
				&& current != null)
		{
			mxGraph graph = graphComponent.getGraph();
			double scale = graph.getView().getScale();
			mxPoint tr = graph.getView().getTranslate();
			current.setX(current.getX() / scale - tr.getX());
			current.setY(current.getY() / scale - tr.getY());
			current.setWidth(current.getWidth() / scale);
			current.setHeight(current.getHeight() / scale);

			Object cell = insertCell(current);
			eventSource.fireEvent(new mxEventObject(mxEvent.INSERT, "cell",
					cell));
			e.consume();
		}

		reset();
	}

	/**
	 * 
	 */
	public Object insertCell(mxRectangle bounds)
	{
		// FIXME: Clone prototype cell for insert
		return graphComponent.getGraph().insertVertex(null, null, "",
				bounds.getX(), bounds.getY(), bounds.getWidth(),
				bounds.getHeight(), style);
	}

	/**
	 * 
	 */
	public void reset()
	{
		Rectangle dirty = null;

		if (current != null)
		{
			dirty = current.getRectangle();
		}

		current = null;
		first = null;

		if (dirty != null)
		{
			int b = (int) Math.ceil(lineWidth);
			graphComponent.getGraphControl().repaint(dirty.x - b, dirty.y - b,
					dirty.width + 2 * b, dirty.height + 2 * b);
		}
	}

	/**
	 * 
	 */
	public void paint(Graphics g)
	{
		if (first != null && current != null)
		{
			((Graphics2D) g).setStroke(new BasicStroke(lineWidth));
			g.setColor(lineColor);
			Rectangle rect = current.getRectangle();

			if (rounded)
			{
				g.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 8, 8);
			}
			else
			{
				g.drawRect(rect.x, rect.y, rect.width, rect.height);
			}
		}
	}

	/**
	 *
	 */
	public void addListener(String eventName, mxIEventListener listener)
	{
		eventSource.addListener(eventName, listener);
	}

	/**
	 *
	 */
	public void removeListener(mxIEventListener listener)
	{
		removeListener(listener, null);
	}

	/**
	 *
	 */
	public void removeListener(mxIEventListener listener, String eventName)
	{
		eventSource.removeListener(listener, eventName);
	}

}
