package singleListeners;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
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

        parent.Cardframe.remove(parent.controlnext);
		
		parent.controlnext.removeAll();
		parent.controlprevious.removeAll();
		JPanel review = new JPanel();
		
		review.add(new JButton(new AbstractAction("\u22b2Review Panels") {

			
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout) parent.panelCont.getLayout();

				cl.previous(parent.panelCont);
			}
		}));
		

	
		
		parent.Cardframe.add(review,  BorderLayout.PAGE_END);
		parent.Cardframe.validate();
		parent.Cardframe.pack();
		
		System.out.println(endtime);
		SingleProgressSkip trackMT = new SingleProgressSkip(parent, starttime, endtime);
		trackMT.execute();

	}
}
