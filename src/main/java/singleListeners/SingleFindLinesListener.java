package singleListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import interactiveMT.Interactive_MTSingleChannel;
import interactiveMT.Interactive_MTSingleChannel.ValueChange;
import interactiveMT.Interactive_MTSingleChannelBasic;
import swingClasses.ProgressSeeds;
import swingClasses.SingleProgressSeeds;
import updateListeners.SingleMarkends;

public class SingleFindLinesListener implements ActionListener {

	
final Interactive_MTSingleChannel parent;
final Interactive_MTSingleChannelBasic child;	

	public SingleFindLinesListener (final Interactive_MTSingleChannel parent ){
		
		this.parent = parent;
		this.child = null;
	}
	
public SingleFindLinesListener (final Interactive_MTSingleChannel parent, final Interactive_MTSingleChannelBasic child ){
		
		this.parent = parent;
		this.child = child;
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
		if (child==null)
			parent.frame.setLocationRelativeTo(parent.panelCont);
			else
				parent.frame.setLocationRelativeTo(child.panelCont);
		parent.frame.setVisible(true);

		SingleProgressSeeds trackMT = new SingleProgressSeeds(parent, child);
		trackMT.execute();
		
		
		
		parent.displayBitimg = false;
		parent.displayWatershedimg = false;

	}
}

