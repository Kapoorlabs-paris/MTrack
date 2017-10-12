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

import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * @author Administrator
 * 
 */
public class mxLightweightLabel extends JLabel
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6771477489533614010L;

	/**
	 * 
	 */
	protected static mxLightweightLabel sharedInstance;

	/**
	 * Initializes the shared instance.
	 */
	static
	{
		try
		{
			sharedInstance = new mxLightweightLabel();
		}
		catch (Exception e)
		{
			// ignore
		}
	}

	/**
	 * 
	 */
	public static mxLightweightLabel getSharedInstance()
	{
		return sharedInstance;
	}

	/**
	 * 
	 * 
	 */
	public mxLightweightLabel()
	{
		setFont(new Font(mxConstants.DEFAULT_FONTFAMILY, 0,
				mxConstants.DEFAULT_FONTSIZE));
		setVerticalAlignment(SwingConstants.TOP);
	}

	/**
	 * Overridden for performance reasons.
	 * 
	 */
	public void validate()
	{
	}

	/**
	 * Overridden for performance reasons.
	 * 
	 */
	public void revalidate()
	{
	}

	/**
	 * Overridden for performance reasons.
	 * 
	 */
	public void repaint(long tm, int x, int y, int width, int height)
	{
	}

	/**
	 * Overridden for performance reasons.
	 * 
	 */
	public void repaint(Rectangle r)
	{
	}

	/**
	 * Overridden for performance reasons.
	 * 
	 */
	protected void firePropertyChange(String propertyName, Object oldValue,
			Object newValue)
	{
		// Strings get interned...
		if (propertyName == "text" || propertyName == "font")
		{
			super.firePropertyChange(propertyName, oldValue, newValue);
		}
	}

	/**
	 * Overridden for performance reasons.
	 * 
	 */
	public void firePropertyChange(String propertyName, byte oldValue,
			byte newValue)
	{
	}

	/**
	 * Overridden for performance reasons.
	 * 
	 */
	public void firePropertyChange(String propertyName, char oldValue,
			char newValue)
	{
	}

	/**
	 * Overridden for performance reasons.
	 * 
	 */
	public void firePropertyChange(String propertyName, short oldValue,
			short newValue)
	{
	}

	/**
	 * Overridden for performance reasons.
	 * 
	 */
	public void firePropertyChange(String propertyName, int oldValue,
			int newValue)
	{
	}

	/**
	 * Overridden for performance reasons.
	 * 
	 */
	public void firePropertyChange(String propertyName, long oldValue,
			long newValue)
	{
	}

	/**
	 * Overridden for performance reasons.
	 * 
	 */
	public void firePropertyChange(String propertyName, float oldValue,
			float newValue)
	{
	}

	/**
	 * Overridden for performance reasons.
	 * 
	 */
	public void firePropertyChange(String propertyName, double oldValue,
			double newValue)
	{
	}

	/**
	 * Overridden for performance reasons.
	 * 
	 */
	public void firePropertyChange(String propertyName, boolean oldValue,
			boolean newValue)
	{
	}

}
