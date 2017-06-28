package singleListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import interactiveMT.Interactive_MTSingleChannel;
import interactiveMT.Interactive_MTSingleChannel.ValueChange;
import swingClasses.ProgressSeeds;
import swingClasses.SingleProgressSeeds;
import updateListeners.SingleMarkends;

public class SingleFindLinesListener implements ActionListener {

	
final Interactive_MTSingleChannel parent;
	
	public SingleFindLinesListener (final Interactive_MTSingleChannel parent ){
		
		this.parent = parent;
	}
	
	@Override
	public void actionPerformed(final ActionEvent arg0) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				goSeeds();
			
			}

		});

	}
	
	
	public void goSeeds() {

		parent.jpb.setIndeterminate(false);

		parent.jpb.setMaximum(parent.max);
		parent.panel.add(parent.label);
		parent.panel.add(parent.jpb);
		parent.frame.add(parent.panel);
		parent.frame.pack();
		parent.frame.setSize(200, 100);
		parent.frame.setLocationRelativeTo(parent.panelCont);
		parent.frame.setVisible(true);

		SingleProgressSeeds trackMT = new SingleProgressSeeds(parent);
		trackMT.execute();
		
		parent.displayBitimg = false;
		parent.displayWatershedimg = false;

	}
}

