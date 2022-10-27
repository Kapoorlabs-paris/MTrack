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
package com.mxgraph.util;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Contains various DOM API helper methods for use with mxGraph.
 */
public class mxDomUtils
{
	/**
	 * Returns a new, empty DOM document.
	 * 
	 * @return Returns a new DOM document.
	 */
	public static Document createDocument()
	{
		Document result = null;

		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder parser = factory.newDocumentBuilder();

			result = parser.newDocument();
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}

		return result;
	}

	/**
	 * Creates a new SVG document for the given width and height.
	 */
	public static Document createSvgDocument(int width, int height)
	{
		Document document = createDocument();
		Element root = document.createElement("svg");

		String w = String.valueOf(width);
		String h = String.valueOf(height);

		root.setAttribute("width", w);
		root.setAttribute("height", h);
		root.setAttribute("viewBox", "0 0 " + w + " " + h);
		root.setAttribute("version", "1.1");
		root.setAttribute("xmlns", mxConstants.NS_SVG);
		root.setAttribute("xmlns:xlink", mxConstants.NS_XLINK);

		document.appendChild(root);

		return document;
	}

	/**
	 * 
	 */
	public static Document createVmlDocument()
	{
		Document document = createDocument();

		Element root = document.createElement("html");
		root.setAttribute("xmlns:v", "urn:schemas-microsoft-com:vml");
		root.setAttribute("xmlns:o", "urn:schemas-microsoft-com:office:office");

		document.appendChild(root);

		Element head = document.createElement("head");

		Element style = document.createElement("style");
		style.setAttribute("type", "text/css");
		style.appendChild(document
				.createTextNode("<!-- v\\:* {behavior: url(#default#VML);} -->"));

		head.appendChild(style);
		root.appendChild(head);

		Element body = document.createElement("body");
		root.appendChild(body);

		return document;
	}

	/**
	 * Returns a document with a HTML node containing a HEAD and BODY node.
	 */
	public static Document createHtmlDocument()
	{
		Document document = createDocument();

		Element root = document.createElement("html");

		document.appendChild(root);

		Element head = document.createElement("head");
		root.appendChild(head);

		Element body = document.createElement("body");
		root.appendChild(body);

		return document;
	}
}
