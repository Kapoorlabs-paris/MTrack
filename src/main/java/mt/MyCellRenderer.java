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

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import net.imglib2.util.Pair;

public class MyCellRenderer extends DefaultTableCellRenderer {
	ArrayList<Pair<Boolean, Integer >> wrongfileindexlist;
	
	public MyCellRenderer(ArrayList<Pair<Boolean, Integer >> wrongfileindexlist){
		
		this.wrongfileindexlist = wrongfileindexlist;
	}
	
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

        //Cells are by default rendered as a JLabel.
        JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

        //Get the status for the current row.
        for (int index = 0; index < wrongfileindexlist.size(); ++index){
        	if (wrongfileindexlist.get(index).getA()){
		        if (wrongfileindexlist.get(index).getB() == row ) {
        l.setBackground(Color.red);

		        }
        	}
        }
        //Return the JLabel which renders the cell.
        return l;
    }
}
