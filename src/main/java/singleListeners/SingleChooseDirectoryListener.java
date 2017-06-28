package singleListeners;

import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;

import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTSingleChannel;


	public class SingleChooseDirectoryListener implements ActionListener {
		final TextField filename;
        Interactive_MTSingleChannel parent;
        final File userfile;
        
		public SingleChooseDirectoryListener(Interactive_MTSingleChannel parent, TextField filename, final File userfile) {

			this.parent = parent;
			this.filename = filename;
			this.userfile = userfile;

		}
		
		
		

		@Override
		public void actionPerformed(final ActionEvent arg0) {

			JFileChooser chooserA = new JFileChooser();
			chooserA.setCurrentDirectory(userfile.getParentFile());
			chooserA.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooserA.showOpenDialog(parent.panelFirst);
			parent.usefolder = chooserA.getSelectedFile().getAbsolutePath();

			parent.addToName = filename.getText();

			parent.SaveTxt = true;
		}

	
}
