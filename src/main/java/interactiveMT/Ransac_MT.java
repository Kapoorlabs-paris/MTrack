package interactiveMT;

import javax.swing.JFrame;

import ij.ImageJ;
import ij.plugin.PlugIn;
import mt.RansacFileChooser;

public class Ransac_MT implements PlugIn {

	
		@Override
		public void run(String arg) {
			
				

				    JFrame frame = new JFrame("");
				    RansacFileChooser panel = new RansacFileChooser();
				 
				    frame.getContentPane().add(panel,"Center");
				    frame.setSize(panel.getPreferredSize());
				    
			
	}

}
