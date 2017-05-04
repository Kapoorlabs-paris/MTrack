package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import interactiveMT.Interactive_MTDoubleChannel;

public class SeedchoiceListener implements ActionListener {

	
	 final Interactive_MTDoubleChannel parent;
		
		
		
	
	final JComboBox<String> cb;

	public SeedchoiceListener(JComboBox<String> cb,  final Interactive_MTDoubleChannel parent) {

		this.cb = cb;
		this.parent = parent;

	}

	@Override
	public void actionPerformed(final ActionEvent arg0) {

		parent.selectedSeed = cb.getSelectedIndex();

	}

}
