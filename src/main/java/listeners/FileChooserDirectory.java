package listeners;

import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;

import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.MainFileChooser;


	public class FileChooserDirectory implements ActionListener {
		
        MainFileChooser parent;
      
        
		public FileChooserDirectory(MainFileChooser parent) {

			this.parent = parent;
		

		}
		
		
		

		@Override
		public void actionPerformed(final ActionEvent arg0) {

			JFileChooser chooserA = new JFileChooser();
			chooserA.setCurrentDirectory(parent.chooserB.getCurrentDirectory());
			chooserA.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooserA.showOpenDialog(parent.panelIntro);
			
			
			parent.inputField.setText(parent.chooserB.getSelectedFile().getName().replaceFirst("[.][^.]+$", ""));

			parent.addToName = parent.inputField.getText();

			
		}

	
}
