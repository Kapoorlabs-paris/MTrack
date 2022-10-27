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
package MTObjects;

public class ResultsMT {

	
	    public final int framenumber;
	    public final double totallengthpixel;
	    public final double totallengthreal;
	    public final int seedid;
	    public final double[] currentpointpixel;
	  
	    public final double[] currentpointreal;
	    public final double lengthpixelperframe;
	    public final double lengthrealperframe;
	   
	   
	   
	    public ResultsMT(final int framenumber, final double totallengthpixel, final double totallengthreal, final int seedid, final double[] currentpointpixel,
	    		final double[] currentpointreal, final double lengthpixelperframe, final double lengthrealperframe){
	    	    
	    	    this.framenumber = framenumber;
	    	    this.totallengthpixel = totallengthpixel;
	    	    this.totallengthreal = totallengthreal;
	    	    this.seedid = seedid;
	    	    this.currentpointpixel = currentpointpixel;
	    	    this.currentpointreal = currentpointreal;
	    	    this.lengthpixelperframe = lengthpixelperframe;
	    	    this.lengthrealperframe = lengthrealperframe;
	    	
	    	
	    }
	    
	    
	   
	    
	
	
	
}
