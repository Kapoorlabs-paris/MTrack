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
package comboListeners;

import java.awt.GridBagConstraints;
import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.TextField;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;


public class SliderBoxGUI {

	
	final String string;
	final float valueDimension;
	final float valueMax;
	final int scrollbarSize;
	final JScrollBar valueslider;
	final TextField inputFieldvalue;
	final Label valueText;
	
	public SliderBoxGUI(final String string, JScrollBar valueslider, TextField inputFieldvalue, Label valueText,  final int scrollbarSize, final float valueDimension, final float valueMax) {
		
		this.string  = string;
		this.scrollbarSize = scrollbarSize;
		this.valueDimension = valueDimension;
		this.valueMax = valueMax;
		this.valueslider = valueslider;
		this.inputFieldvalue = inputFieldvalue;
		this.valueText = valueText;
	}
	
	
	
	public JPanel BuildDisplay() {
	

	JPanel combosliderbox = new JPanel();

	layoutManager.Setlayout.LayoutSetter(combosliderbox);
	
	

	combosliderbox.add(valueText, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
			GridBagConstraints.HORIZONTAL, layoutManager.Setlayout.insets, 0, 0));

	combosliderbox.add(valueslider, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
			GridBagConstraints.HORIZONTAL, layoutManager.Setlayout.insets, 0, 0));

	combosliderbox.add(inputFieldvalue, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
			GridBagConstraints.HORIZONTAL, layoutManager.Setlayout.insets, 0, 0));

	
	
	return combosliderbox;

	
	
	}
	
}
