package interactiveMT;

import javax.swing.JFrame;

import ij.ImageJ;
import ij.plugin.PlugIn;


public class Microtubule_TrackerSingle implements PlugIn {
	@Override
	public void run(String arg) {
		
			new ImageJ();
			

			    JFrame frame = new JFrame("");
			    SingleFileChooser panel = new SingleFileChooser();
			  
			    frame.getContentPane().add(panel,"Center");
			    frame.setSize(panel.getPreferredSize());
			    
		}
}
