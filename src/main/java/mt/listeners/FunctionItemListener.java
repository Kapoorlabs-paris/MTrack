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
package mt.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;

import org.apache.commons.io.filefilter.CanReadFileFilter;

public class FunctionItemListener implements ActionListener
{
	final InteractiveRANSAC parent;
	final JComboBox<String> choice;

	public FunctionItemListener( final InteractiveRANSAC parent, final JComboBox<String> choice )
	{
		this.parent = parent;
		this.choice = choice;
	}

	
	
	
	
	






	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		int selectedindex = choice.getSelectedIndex();
		
		if (selectedindex == 0)
			parent.functionChoice = 0;
		if (selectedindex == 1)
			parent.functionChoice = 1;
		if (selectedindex == 2)
			parent.functionChoice = 2;
		parent.setFunction();
		parent.updateRANSAC();
	}
}
