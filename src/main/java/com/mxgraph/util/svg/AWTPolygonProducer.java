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

import java.awt.Shape;

/**
 * This class produces a polygon shape from a reader.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 */
public class AWTPolygonProducer extends AWTPolylineProducer
{
	/**
	 * Utility method for creating an ExtendedGeneralPath.
	 * @param text The text representation of the path specification.
	 * @param wr The winding rule to use for creating the path.
	 */
	public static Shape createShape(String text, int wr) throws ParseException
	{
		AWTPolygonProducer ph = new AWTPolygonProducer();

		ph.setWindingRule(wr);
		PointsParser p = new PointsParser(ph);
		p.parse(text);

		return ph.getShape();
	}

	/**
	 * Implements {@link PointsHandler#endPoints()}.
	 */
	public void endPoints() throws ParseException
	{
		path.closePath();
	}
}
