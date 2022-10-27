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

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.xml.sax.InputSource;

/**
 * Contains various XML helper methods for use with mxGraph.
 */
public class mxXmlUtils
{
	/**
	 * Returns a new document for the given XML string.
	 * 
	 * @param xml
	 *            String that represents the XML data.
	 * @return Returns a new XML document.
	 */
	public static Document parseXml(String xml)
	{
		try
		{
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

			return docBuilder.parse(new InputSource(new StringReader(xml)));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}
	
	/**
	 * Returns a string that represents the given node.
	 * 
	 * @param node
	 *            Node to return the XML for.
	 * @return Returns an XML string.
	 */
	public static String getXml(Node node)
	{
		try
		{
			Transformer tf = TransformerFactory.newInstance().newTransformer();

			tf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			StreamResult dest = new StreamResult(new StringWriter());
			tf.transform(new DOMSource(node), dest);

			return dest.getWriter().toString();
		}
		catch (Exception e)
		{
			// ignore
		}

		return "";
	}
}
