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
import java.io.Reader;

/**
 * This class represents a reader which normalizes the line break: \n,
 * \r, \r\n are replaced by \n.  The methods of this reader are not
 * synchronized.  The input is buffered.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 */
public abstract class NormalizingReader extends Reader
{

	/**
	 * Read characters into a portion of an array.
	 * @param cbuf  Destination buffer
	 * @param off   Offset at which to start writing characters
	 * @param len   Maximum number of characters to read
	 * @return The number of characters read, or -1 if the end of the
	 * stream has been reached
	 */
	public int read(char[] cbuf, int off, int len) throws IOException
	{
		if (len == 0)
		{
			return 0;
		}

		int c = read();
		if (c == -1)
		{
			return -1;
		}
		int result = 0;
		do
		{
			cbuf[result + off] = (char) c;
			result++;
			c = read();
		}
		while (c != -1 && result < len);
		return result;
	}

	/**
	 * Returns the current line in the stream.
	 */
	public abstract int getLine();

	/**
	 * Returns the current column in the stream.
	 */
	public abstract int getColumn();

}
