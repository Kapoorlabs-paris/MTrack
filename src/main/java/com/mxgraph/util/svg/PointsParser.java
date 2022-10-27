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
/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package com.mxgraph.util.svg;

import java.io.IOException;

/**
 * This class implements an event-based parser for the SVG points
 * attribute values (used with polyline and polygon elements).
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 */
public class PointsParser extends NumberParser
{

	/**
	 * The points handler used to report parse events.
	 */
	protected PointsHandler pointsHandler;

	/**
	 * Whether the last character was a 'e' or 'E'.
	 */
	protected boolean eRead;

	/**
	 * Creates a new PointsParser.
	 */
	public PointsParser(PointsHandler handler)
	{
		pointsHandler = handler;
	}

	/**
	 * Allows an application to register a points handler.
	 *
	 * <p>If the application does not register a handler, all
	 * events reported by the parser will be silently ignored.
	 *
	 * <p>Applications may register a new or different handler in the
	 * middle of a parse, and the parser must begin using the new
	 * handler immediately.</p>
	 * @param handler The transform list handler.
	 */
	public void setPointsHandler(PointsHandler handler)
	{
		pointsHandler = handler;
	}

	/**
	 * Returns the points handler in use.
	 */
	public PointsHandler getPointsHandler()
	{
		return pointsHandler;
	}

	/**
	 * Parses the current stream.
	 */
	protected void doParse() throws ParseException, IOException
	{
		pointsHandler.startPoints();

		current = reader.read();
		skipSpaces();

		loop: for (;;)
		{
			if (current == -1)
			{
				break loop;
			}
			float x = parseFloat();
			skipCommaSpaces();
			float y = parseFloat();

			pointsHandler.point(x, y);
			skipCommaSpaces();
		}

		pointsHandler.endPoints();
	}
}
