package listeners;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannelBasic;
import swingClasses.ProgressSkip;

public class SkipFramesandTrackendsListener implements ActionListener {
	
	
	
      final Interactive_MTDoubleChannel parent;
      final Interactive_MTDoubleChannelBasic child;
	
	public SkipFramesandTrackendsListener(final Interactive_MTDoubleChannel parent, final Interactive_MTDoubleChannelBasic child){
	
		this.parent = parent;
		this.child = child;
	}
public SkipFramesandTrackendsListener(final Interactive_MTDoubleChannel parent){
		
		this.parent = parent;
		this.child = null;
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
		if (child == null)
		parent.frame.setLocationRelativeTo(parent.panelCont);
		else
		parent.frame.setLocationRelativeTo(child.panelCont);	
		parent.frame.setVisible(true);

		
		
		
		ProgressSkip trackMT = new ProgressSkip(parent);
		trackMT.execute();

	}
}
