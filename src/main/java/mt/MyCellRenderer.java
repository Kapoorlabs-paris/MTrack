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