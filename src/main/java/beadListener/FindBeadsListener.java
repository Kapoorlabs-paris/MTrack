package beadListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import beadFinder.ProgressBeads;
import interactiveMT.Interactive_PSFAnalyze;
import swingClasses.ProgressSeeds;

public class FindBeadsListener implements ActionListener {
	
	
	final Interactive_PSFAnalyze parent;
	
	public FindBeadsListener(final Interactive_PSFAnalyze parent){
		
		this.parent = parent;
		
	}
	
	@Override
	public void actionPerformed(final ActionEvent arg0) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				goBeads();

			}

		});

	}
	
	
	
	public void goBeads() {
		
		parent.jpb.setIndeterminate(false);

		parent.jpb.setMaximum(parent.max);
		parent.panel.add(parent.label);
		parent.panel.add(parent.jpb);
		parent.frame.add(parent.panel);
		parent.frame.pack();
		parent.frame.setSize(200, 100);
		parent.frame.setLocationRelativeTo(parent.panelCont);
		parent.frame.setVisible(true);

		ProgressBeads fitbeads = new ProgressBeads(parent);
		fitbeads.execute();
		
		
		
	}
	
	

}
