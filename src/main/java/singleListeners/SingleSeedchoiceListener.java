package singleListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTSingleChannel;

public class SingleSeedchoiceListener implements ActionListener {

	
	 final Interactive_MTSingleChannel parent;
		
		
		
	
	final JComboBox<String> cb;

	public SingleSeedchoiceListener(JComboBox<String> cb,  final Interactive_MTSingleChannel parent) {

		this.cb = cb;
		this.parent = parent;

	}

	@Override
	public void actionPerformed(final ActionEvent arg0) {

		parent.selectedSeed = cb.getSelectedIndex();

	}

}
