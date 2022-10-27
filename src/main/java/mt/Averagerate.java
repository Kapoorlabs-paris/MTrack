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
package mt;

import java.io.File;

public class Averagerate {

	
	public final double averagegrowth;
	public final double averageshrink;
	public final double catfrequ;
	public final double resfrequ;
	public final int growthevent;
	public final int shrinkevent;
	public final int catevent;
	public final int resevent;
	public final File file;

	
	
	public Averagerate(final double averagegrowth,  final double averageshrink,final double catfrequ, final double resfrequ,
			final int growthevent, final int shrinkevent, final int catevent, final int resevent, final File file){
		
		this.averagegrowth = averagegrowth;
		this.averageshrink = averageshrink;
		this.catfrequ = catfrequ;
		this.resfrequ = resfrequ;
	
		this.growthevent = growthevent;
		this.shrinkevent = shrinkevent;
		this.catevent = catevent;
		this.resevent = resevent;
		this.file = file;
		
	}
	
}
