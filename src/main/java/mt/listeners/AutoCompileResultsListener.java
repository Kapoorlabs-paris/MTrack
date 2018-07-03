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

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import fit.AbstractFunction2D;
import fit.PointFunctionMatch;
import fit.polynomial.LinearFunction;
import fit.polynomial.Polynomial;
import ij.measure.ResultsTable;
import mpicbg.models.Point;
import mt.Averagerate;
import mt.Rateobject;
import mt.Tracking;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;

public class AutoCompileResultsListener implements ActionListener {

	final InteractiveRANSAC parent;
	final int index;
	

	public AutoCompileResultsListener(final InteractiveRANSAC parent, final int index) {
		this.parent = parent;
		this.index = index;
	}

	@Override
	public void actionPerformed(final ActionEvent arg0) {
	
		
		
		int nThreads = 1;
		// set up executor service
		final ExecutorService taskexecutor = Executors.newFixedThreadPool(nThreads);
		 List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
		for (int trackindex = index; trackindex < parent.inputfiles.length; ++trackindex){
			
			tasks.add(Executors.callable(new Split(parent, trackindex)));
			
		}
		try {
			taskexecutor.invokeAll(tasks);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	
		parent.table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
		    @Override
		    public Component getTableCellRendererComponent(JTable table,
		            Object value, boolean isSelected, boolean hasFocus, int row, int col) {

		        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

		        String status = (String)table.getModel().getValueAt(row, 7);
		        if ("true".equals(status)) {
		            setBackground(Color.red);
		            
		        } else {
		            setBackground(Color.GRAY);
		        } 
		        return this;
		    }   
		});
				
			
		
           parent.table.validate();
           parent.scrollPane.validate();

	}

	

}
