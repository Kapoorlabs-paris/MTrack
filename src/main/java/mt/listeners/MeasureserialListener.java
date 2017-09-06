package mt.listeners;
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
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

public class MeasureserialListener implements ActionListener {

		final InteractiveRANSAC parent;

		public MeasureserialListener(InteractiveRANSAC parent) {

			this.parent = parent;

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
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Rate Files", "txt");

			parent.chooserA.setFileFilter(filter);
			parent.chooserA.showOpenDialog(parent.Cardframe);

			parent.inputfiles = parent.chooserA.getSelectedFile().listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File pathname, String filename) {

					return (filename.endsWith(".txt") && !filename.contains("Rates") && !filename.contains("Average")
							&& !filename.contains("All"));
				}
			});
			parent.userTableModel = new DefaultTableModel(new Object[]{"Track File"}, 0) {
			    @Override
			    public boolean isCellEditable(int row, int column) {
			        return false;
			    }
			};
			
			
					
			
			for (int i = 0; i < parent.inputfiles.length; ++i) {
				
				String[] currenttrack = {(parent.inputfiles[i].getName())};
				parent.userTableModel.addRow(currenttrack);
			}
			
			
			parent.table = new JTable(parent.userTableModel);
			parent.previousrow = parent.row;
			System.out.println(parent.row + " Last row");
			parent.scrollPane = new JScrollPane(parent.table);
			parent.scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			parent.scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			parent.scrollPane.setPreferredSize(new Dimension(300, 200));
            parent.PanelSelectFile.removeAll();
			parent.Compilepositiverates.clear();
			parent.Compilenegativerates.clear();
			parent.PanelSelectFile.add(parent.scrollPane,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
			parent.PanelSelectFile.setBorder(parent.selectfile);
			parent.panelFirst.add(parent.PanelSelectFile, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.RELATIVE, new Insets(10, 10, 0, 10), 0, 0));
			
			parent.table.addMouseListener(new MouseAdapter() {
				  public void mouseClicked(MouseEvent e) {
				    if (e.getClickCount() == 1) {
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
			
			parent.PanelSelectFile.validate();
			parent.panelFirst.validate();
			parent.Cardframe.validate();
			
		}
		
		
	}
	