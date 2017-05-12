package beadListener;

import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;

import interactiveMT.Interactive_PSFAnalyze;



	public class ChooseDirectoryListener implements ActionListener {
		final TextField filename;
        Interactive_PSFAnalyze parent;
       
        
		public ChooseDirectoryListener(Interactive_PSFAnalyze parent, TextField filename) {

			this.parent = parent;
			this.filename = filename;

		}
		
		
		

		@Override
		public void actionPerformed(final ActionEvent arg0) {

			JFileChooser chooserA = new JFileChooser();
			chooserA.setCurrentDirectory(new java.io.File("."));
			chooserA.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooserA.showOpenDialog(parent.panelFirst);
			parent.usefolder = chooserA.getSelectedFile().getAbsolutePath();

			parent.addToName = filename.getText();

			parent.SaveTxt = true;
		}

	
}
