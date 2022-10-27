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
package mt.listeners;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.JFileChooser;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import mt.Tracking;

public class MeasureserialListener implements ActionListener {

		final InteractiveRANSAC parent;

		public MeasureserialListener(InteractiveRANSAC parent) {

			this.parent = parent;

		}
		public static class WordWrapCellRenderer extends JTextArea implements TableCellRenderer {
		    WordWrapCellRenderer() {
		        setLineWrap(true);
		        setWrapStyleWord(true);
		    }

		    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		        setText(value.toString());
		        setSize(table.getColumnModel().getColumn(column).getWidth(), getPreferredSize().height);
		        if (table.getRowHeight(row) != getPreferredSize().height) {
		            table.setRowHeight(row, getPreferredSize().height);
		        }
		        return this;
		    }
		}
		@Override
		public void actionPerformed(final ActionEvent arg0) {
			parent.chooserA = new JFileChooser();

			parent.chooserA.setCurrentDirectory(new java.io.File("."));
			parent.chooserA.setDialogTitle(parent.choosertitleA);
			parent.chooserA.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			//
			// disable the "All files" option.
			//
			parent.chooserA.setAcceptAllFileFilterUsed(false);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Track Files", "txt");

			parent.chooserA.setFileFilter(filter);
			parent.chooserA.showOpenDialog(parent.Cardframe);

			parent.inputfiles = parent.chooserA.getSelectedFile().listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File pathname, String filename) {

					return (filename.endsWith(".txt") && !filename.contains("Rates") && !filename.contains("Average")
							&& !filename.contains("All"));
				}
			});
			
		
			Object[] colnames = new Object[] { "Track File", "Growth velocity", "Shrink velocity", "Growth events", "Shrink events",
					"fcat", "fres", "Error" };
			
			
			Object[][] rowvalues = new Object[parent.inputfiles.length][colnames.length];
			int size = 100;
			parent.table.getColumnModel().getColumn(0).setPreferredWidth(size);
			parent.table.getColumnModel().getColumn(1).setPreferredWidth(size);
			parent.table.getColumnModel().getColumn(2).setPreferredWidth(size);
			parent.table.getColumnModel().getColumn(3).setPreferredWidth(size);
			parent.table.getColumnModel().getColumn(4).setPreferredWidth(size);
			parent.table.getColumnModel().getColumn(5).setPreferredWidth(size);
			parent.table.getColumnModel().getColumn(6).setPreferredWidth(size);
			parent.table.getColumnModel().getColumn(7).setPreferredWidth(size);
			 parent.PanelDirectory.remove(parent.scrollPane);
			 parent.PanelDirectory.validate();
			 parent.PanelDirectory.repaint();
			for (int i = 0; i < parent.inputfiles.length; ++i) {
				
				rowvalues[i][0] = parent.inputfiles[i].getName();
			}
			
			
			parent.table = new JTable(rowvalues, colnames);
			parent.table.setFillsViewportHeight(true);
			parent.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			parent.table.getColumnModel().getColumn(0).setPreferredWidth(100);
			parent.table.getColumnModel().getColumn(0).setResizable(true);
			parent.table.setCellSelectionEnabled(true);
			parent.previousrow = parent.row;
			parent.scrollPane = new JScrollPane(parent.table);
			parent.scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			parent.scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			parent.table.setFillsViewportHeight(true);
			parent.table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
						boolean hasFocus, int rowA, int col) {

					super.getTableCellRendererComponent(table, value, isSelected, hasFocus, rowA, col);

					
						setBackground(Color.GRAY);
					
					return this;
				}
			});
			
			//parent.table.getColumnModel().getColumn(0).setCellRenderer(new WordWrapCellRenderer());
			parent.scrollPane.setMinimumSize(new Dimension(parent.SizeX, parent.SizeY));
			parent.scrollPane.setPreferredSize(new Dimension(parent.SizeX, parent.SizeY));
			parent.table.setFillsViewportHeight(true);
			parent.scrollPane.getViewport().add(parent.table);
			parent.scrollPane.setAutoscrolls(true);
			
           
			parent.Compilepositiverates.clear();
			parent.Compilenegativerates.clear();
		
			parent.PanelDirectory.add(parent.scrollPane,  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
					
					
			parent.PanelDirectory.setBorder(parent.selectdirectory);
			parent.panelFirst.add(parent.PanelDirectory, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.RELATIVE, new Insets(10, 10, 0, 10), 0, 0));
			if(parent.inputfiles!=null){ 
			parent.table.addMouseListener(new MouseAdapter() {
				  public void mouseClicked(MouseEvent e) {
				    if (e.getClickCount() == 1) {
				    	if (!parent.jFreeChartFrame.isVisible())
				    		parent.jFreeChartFrame = Tracking.display(parent.chart, new Dimension(500, 500));
				      JTable target = (JTable)e.getSource();
				      parent.row = target.getSelectedRow();
				      // do some action if appropriate column
				      if (parent.row > 0)
				      parent.displayclicked(parent.row);
				      else
				    	  parent.displayclicked(0);	  
				    }
				  }
				});
			}
			parent.PanelDirectory.validate();
			parent.panelFirst.validate();
			parent.Cardframe.validate();
			
		}
		
	}
	
