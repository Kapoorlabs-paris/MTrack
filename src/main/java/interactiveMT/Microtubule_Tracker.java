package interactiveMT;

import javax.swing.JFrame;

import ij.ImageJ;
import ij.plugin.PlugIn;


public class Microtubule_Tracker implements PlugIn {
	@Override
	public void run(String arg) {
		
			

			    JFrame frame = new JFrame("");
			    MainFileChooser panel = new MainFileChooser();
			  
			    frame.getContentPane().add(panel,"Center");
			    frame.setSize(panel.getPreferredSize());
			    
		}
}
