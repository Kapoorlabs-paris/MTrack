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
 * Copyright (c) 2010, Gaudenz Alder
 */
package com.mxgraph.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class mxResources
{

	/**
	 * Ordered list of the inserted resource bundles.
	 */
	protected static LinkedList<ResourceBundle> bundles = new LinkedList<ResourceBundle>();

	/**
	 * Returns the bundles.
	 * 
	 * @return Returns the bundles.
	 */
	public static LinkedList<ResourceBundle> getBundles()
	{
		return bundles;
	}

	/**
	 * Sets the bundles.
	 * 
	 * @param value
	 *            The bundles to set.
	 */
	public static void setBundles(LinkedList<ResourceBundle> value)
	{
		bundles = value;
	}

	/**
	 * Adds a resource bundle. This may throw a MissingResourceException that
	 * should be handled in the calling code.
	 * 
	 * @param basename
	 *            The basename of the resource bundle to add.
	 */
	public static void add(String basename)
	{
		bundles.addFirst(PropertyResourceBundle.getBundle(basename));
	}
	
	/**
	 * Adds a resource bundle. This may throw a MissingResourceException that
	 * should be handled in the calling code.
	 * 
	 * @param basename
	 *            The basename of the resource bundle to add.
	 */
	public static void add(String basename, Locale locale)
	{
		bundles.addFirst(PropertyResourceBundle.getBundle(basename, locale));
	}

	/**
	 * 
	 */
	public static String get(String key)
	{
		return get(key, null, null);
	}

	/**
	 * 
	 */
	public static String get(String key, String defaultValue)
	{
		return get(key, null, defaultValue);
	}

	/**
	 * Returns the value for the specified resource key.
	 */
	public static String get(String key, String[] params)
	{
		return get(key, params, null);
	}

	/**
	 * Returns the value for the specified resource key.
	 */
	public static String get(String key, String[] params, String defaultValue)
	{
		String value = getResource(key);

		// Applies default value if required
		if (value == null)
		{
			value = defaultValue;
		}

		// Replaces the placeholders with the values in the array
		if (value != null && params != null)
		{
			StringBuffer result = new StringBuffer();
			String index = null;

			for (int i = 0; i < value.length(); i++)
			{
				char c = value.charAt(i);

				if (c == '{')
				{
					index = "";
				}
				else if (index != null && c == '}')
				{
					int tmp = Integer.parseInt(index) - 1;

					if (tmp >= 0 && tmp < params.length)
					{
						result.append(params[tmp]);
					}

					index = null;
				}
				else if (index != null)
				{
					index += c;
				}
				else
				{
					result.append(c);
				}
			}

			value = result.toString();
		}

		return value;
	}

	/**
	 * Returns the value for <code>key</code> by searching the resource
	 * bundles in inverse order or <code>null</code> if no value can be found
	 * for <code>key</code>.
	 */
	protected static String getResource(String key)
	{
		Iterator<ResourceBundle> it = bundles.iterator();

		while (it.hasNext())
		{
			try
			{
				return it.next().getString(key);
			}
			catch (MissingResourceException mrex)
			{
				// continue
			}
		}

		return null;
	}

}
