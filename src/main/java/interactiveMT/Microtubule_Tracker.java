package interactiveMT;

import javax.swing.JFrame;

import ij.ImageJ;
import ij.plugin.PlugIn;


public class Microtubule_Tracker implements PlugIn {
	@Override
	public void run(String arg) {
		
			new ImageJ();
			

			    JFrame frame = new JFrame("");
			    FileChooser panel = new FileChooser();
			  
			    frame.getContentPane().add(panel,"Center");
			    frame.setSize(panel.getPreferredSize());
			    
		}
}
