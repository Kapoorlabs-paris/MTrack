package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannelBasic;
import interactiveMT.MainFileChooser;

public class LoadModuletrigger implements ActionListener {

	final MainFileChooser parent;
	
	
	public LoadModuletrigger(MainFileChooser parent){
		
		this.parent = parent;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		parent.frame.dispose();
		if (parent.Simplemode)
			new Interactive_MTDoubleChannelBasic(new Interactive_MTDoubleChannel(parent.originalimg, parent.originalPreprocessedimg,
					parent.psf, parent.calibration, parent.chooserB.getSelectedFile())).run(null);
		else
			new Interactive_MTDoubleChannel(parent.originalimg, parent.originalPreprocessedimg, parent.psf, parent.calibration,
					parent.chooserB.getSelectedFile()).run(null);
	}
	
	
}
