package listeners;

import java.awt.Button;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;

import interactiveMT.Interactive_MTDoubleChannel;


public class StatsAnalyzeListener implements ActionListener {

	
	 final Interactive_MTDoubleChannel parent;
		
		
		public StatsAnalyzeListener(final Interactive_MTDoubleChannel parent){
		
			this.parent = parent;
		}
		
	
	@Override
	public void actionPerformed(final ActionEvent arg0) {

		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints c = new GridBagConstraints();

		parent.panelNinth.removeAll();
		final Label Step9 = new Label("Step 9", Label.CENTER);
		parent.panelNinth.setLayout(layout);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 4;
		c.weighty = 1.5;

		parent.panelNinth.add(Step9, c);
		final Label NumberMT = new Label("Get average MT length", Label.CENTER);
		final Button Nlength = new Button("Time averaged MT lengths");

		final Label NumberMTMax = new Label("MT length distribution", Label.CENTER);
		final Button NlengthMax = new Button("Get length distribution");

		parent.inputMaxdpixel = new JLabel("Enter maxLength of MT (pixel units): ");
		parent.Maxdpixel = new TextField();
		parent.Maxdpixel.setColumns(10);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 50);
		parent.panelNinth.add(NumberMT, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 50);
		parent.panelNinth.add(Nlength, c);
		++c.gridy;
		c.insets = new Insets(10, 10, 0, 50);
		parent.panelNinth.add(parent.inputMaxdpixel, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 50);
		parent.panelNinth.add(parent.Maxdpixel, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 50);
		parent.panelNinth.add(NumberMTMax, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 50);
		parent.panelNinth.add(NlengthMax, c);
		++c.gridy;

		Nlength.addActionListener(new NlengthListener(parent));

		NlengthMax.addActionListener(new NlengthMaxListener(parent));

	}
}

