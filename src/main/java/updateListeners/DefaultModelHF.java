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
package updateListeners;

import LineModels.UseLineModel.UserChoiceModel;
import interactiveMT.Interactive_MTDoubleChannel;

public class DefaultModelHF {

final Interactive_MTDoubleChannel parent;
	
	public DefaultModelHF(final Interactive_MTDoubleChannel parent ){
		
		this.parent = parent;
	}
	
	
	
	public  void LoadDefault(){
		
		
		parent.userChoiceModel = UserChoiceModel.Splineorderthird;
		parent.Intensityratio = 0.5;
		parent.Inispacing = 0.5 * Math.min(parent.psf[0], parent.psf[1]);
		parent.displayoverlay = true;
		parent.Domask = true;
		
	}
	
	
}
