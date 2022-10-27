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
package updateListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ij.IJ;
import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;

public class MoveToFrameListener implements ActionListener {
	
final Interactive_MTDoubleChannel parent;
	
	
	public MoveToFrameListener(final Interactive_MTDoubleChannel parent){
	
		this.parent = parent;
	}
	
	
	@Override
	public void actionPerformed(final ActionEvent arg0) {

		parent.moveDialogue();

		if (parent.thirdDimension > parent.thirdDimensionSize) {
			IJ.log("Max frame number exceeded, moving to last frame instead");
			parent.thirdDimension = parent.thirdDimensionSize;
			parent.CurrentView = util.CopyUtils.getCurrentView(parent.originalimg, parent.thirdDimension, parent.thirdDimensionSize);
			parent.CurrentPreprocessedView = util.CopyUtils.getCurrentPreView(parent.originalPreprocessedimg, parent.thirdDimension,
					parent.thirdDimensionSize);
		} else {

			parent.CurrentView = util.CopyUtils.getCurrentView(parent.originalimg, parent.thirdDimension, parent.thirdDimensionSize);
			parent.CurrentPreprocessedView = util.CopyUtils.getCurrentPreView(parent.originalPreprocessedimg, parent.thirdDimension,
					parent.thirdDimensionSize);
		}

		parent.updatePreview(ValueChange.THIRDDIM);
		Markends newends = new Markends(parent);
		newends.markend();

		

	}

}
