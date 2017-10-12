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
package trackerType;

import java.util.Comparator;

public class AlphanumComparator implements Comparator<String>
	{
		public static final AlphanumComparator instance = new AlphanumComparator();
		
	    private final boolean isDigit(final char ch) {
	        return ch >= 48 && ch <= 57;
	    }
	    
	    // Singleton
	    private AlphanumComparator() {}

	    /** Length of string is passed in for improved efficiency (only need to calculate it once) **/
	    private final String getChunk(final String s, final int slength, int marker) {
	        final StringBuilder chunk = new StringBuilder();
	        char c = s.charAt(marker);
	        chunk.append(c);
	        marker++;
	        if (isDigit(c)) {
	            while (marker < slength) {
	                c = s.charAt(marker);
	                if (!isDigit(c))
	                    break;
	                chunk.append(c);
	                marker++;
	            }
	        } else {
	            while (marker < slength) {
	                c = s.charAt(marker);
	                if (isDigit(c))
	                    break;
	                chunk.append(c);
	                marker++;
	            }
	        }
	        return chunk.toString();
	    }

	    @Override
	    public int compare(final String s1, final String s2) {

	        int thisMarker = 0;
	        int thatMarker = 0;
	        final int s1Length = s1.length();
	        final int s2Length = s2.length();

	        while (thisMarker < s1Length && thatMarker < s2Length) {
	            final String thisChunk = getChunk(s1, s1Length, thisMarker);
	            thisMarker += thisChunk.length();

	            final String thatChunk = getChunk(s2, s2Length, thatMarker);
	            thatMarker += thatChunk.length();

	            // If both chunks contain numeric characters, sort them numerically
	            int result = 0;
	            if (isDigit(thisChunk.charAt(0)) && isDigit(thatChunk.charAt(0))) {
	                // Simple chunk comparison by length.
	                final int thisChunkLength = thisChunk.length();
	                result = thisChunkLength - thatChunk.length();
	                // If equal, the first different number counts
	                if (result == 0) {
	                    for (int i = 0; i < thisChunkLength; i++) {
	                        result = thisChunk.charAt(i) - thatChunk.charAt(i);
	                        if (result != 0) {
	                            return result;
	                        }
	                    }
	                }
	            } else {
	                result = thisChunk.compareTo(thatChunk);
	            }

	            if (result != 0)
	                return result;
	        }

	        return s1Length - s2Length;
	    }
	}

