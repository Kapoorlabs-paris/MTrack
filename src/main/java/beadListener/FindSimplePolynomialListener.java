package beadListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import beadFinder.ProgressBeads;
import interactiveMT.Interactive_PSFAnalyze;
import polynomialBead.ProgressPolyline;

public class FindSimplePolynomialListener implements ActionListener {

	
     final Interactive_PSFAnalyze parent;
	
	public FindSimplePolynomialListener(final Interactive_PSFAnalyze parent){
		
		this.parent = parent;
		
	}
	
	
	
	@Override
	public void actionPerformed(final ActionEvent arg0) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				parent.initialpsf[0] = Float.parseFloat(parent.inputFieldX.getText());
				parent.initialpsf[1] = Float.parseFloat(parent.inputFieldY.getText());
				goPolynomialSimpleLine();

			}

		});

	}
	
public void goPolynomialSimpleLine() {
		
		parent.jpb.setIndeterminate(false);

		parent.jpb.setMaximum(parent.max);
		parent.panel.add(parent.label);
		parent.panel.add(parent.jpb);
		parent.frame.add(parent.panel);
		parent.frame.pack();
		parent.frame.setSize(200, 100);
		parent.frame.setLocationRelativeTo(parent.panelCont);
		parent.frame.setVisible(true);

		ProgressPolyline fitpoly = new ProgressPolyline(parent);
		fitpoly.execute();
		
		
		
	}
	
}
