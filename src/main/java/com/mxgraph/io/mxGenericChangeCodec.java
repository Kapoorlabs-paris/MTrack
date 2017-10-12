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
 * Copyright (c) 2006, Gaudenz Alder
 */
package com.mxgraph.io;

import java.util.Map;

import org.w3c.dom.Node;

/**
 * Codec for mxChildChanges. This class is created and registered
 * dynamically at load time and used implicitely via mxCodec
 * and the mxCodecRegistry.
 */
public class mxGenericChangeCodec extends mxObjectCodec
{
	/**
	 * 
	 */
	protected String fieldname;

	/**
	 * Constructs a new model codec.
	 */
	public mxGenericChangeCodec(Object template, String fieldname)
	{
		this(template, new String[] { "model", "previous" },
				new String[] { "cell" }, null, fieldname);
	}

	/**
	 * Constructs a new model codec for the given arguments.
	 */
	public mxGenericChangeCodec(Object template, String[] exclude,
			String[] idrefs, Map<String, String> mapping, String fieldname)
	{
		super(template, exclude, idrefs, mapping);

		this.fieldname = fieldname;
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.io.mxObjectCodec#afterDecode(com.mxgraph.io.mxCodec, org.w3c.dom.Node, java.lang.Object)
	 */
	@Override
	public Object afterDecode(mxCodec dec, Node node, Object obj)
	{
		Object cell = getFieldValue(obj, "cell");

		if (cell instanceof Node)
		{
			setFieldValue(obj, "cell", dec.decodeCell((Node) cell, false));
		}

		setFieldValue(obj, "previous", getFieldValue(obj, fieldname));

		return obj;
	}

}
