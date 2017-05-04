package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import interactiveMT.Interactive_MTDoubleChannel;

public class FindLinesListener implements ActionListener {

	
final Interactive_MTDoubleChannel parent;
	
	public FindLinesListener (final Interactive_MTDoubleChannel parent ){
		
		this.parent = parent;
	}
	
	@Override
	public void actionPerformed(final ActionEvent arg0) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				parent.goSeeds();

			}

		});

	}
}

