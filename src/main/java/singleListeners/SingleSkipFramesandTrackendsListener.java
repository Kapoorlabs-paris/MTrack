package singleListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTSingleChannel;
import swingClasses.ProgressSkip;
import swingClasses.SingleProgressSkip;

public class SingleSkipFramesandTrackendsListener implements ActionListener {
	
	
	
      final Interactive_MTSingleChannel parent;
      final int starttime;
      final int endtime;
	
	public SingleSkipFramesandTrackendsListener(final Interactive_MTSingleChannel parent, final int starttime, final int endtime){
	
		this.parent = parent;
		this.starttime = starttime;
		this.endtime = endtime;
	}
	
	@Override
	public void actionPerformed(final ActionEvent arg0) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				goSkip();

			}

		});
	}
	
	public void goSkip() {

		parent.jpb.setIndeterminate(false);

		parent.jpb.setMaximum(parent.max);
		parent.panel.add(parent.label);
		parent.panel.add(parent.jpb);
		parent.frame.add(parent.panel);
		parent.frame.pack();
		parent.frame.setSize(200, 100);
		parent.frame.setLocationRelativeTo(parent.panelCont);
		parent.frame.setVisible(true);

		SingleProgressSkip trackMT = new SingleProgressSkip(parent, starttime, endtime);
		trackMT.execute();

	}
}
